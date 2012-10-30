/*
 * Copyright (C) 2012 Pixmob (http://github.com/pixmob)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pixmob.httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Map;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

/**
 * This {@link HttpRequestHandler} implementation authenticates requests to a
 * remote GAE application.
 * <p>
 * Using this class requires the permission
 * <tt>android.permission.USE_CREDENTIALS</tt>.
 * </p>
 * @author Pixmob
 */
public class GoogleAppEngineAuthenticator extends HttpRequestHandler {
    public static final String GOOGLE_ACCOUNT_TYPE = "com.google";
    private final Context context;
    private final Account account;
    private final String gaeHost;
    private String authCookieValue;

    /**
     * Create a new instance of this authenticator.
     * @param context
     *            application context
     * @param account
     *            Google account
     * @param gaeHost
     *            Google App Engine hostname, such as <tt>myapp.appspot.com</tt>
     */
    public GoogleAppEngineAuthenticator(final Context context, final Account account, final String gaeHost) {
        if (context == null) {
            throw new IllegalArgumentException("Context is required");
        }
        if (account == null) {
            throw new IllegalArgumentException("Account is required");
        }
        if (gaeHost == null) {
            throw new IllegalArgumentException("Google App Engine host is required");
        }
        this.context = context;
        this.account = account;
        this.gaeHost = gaeHost;
    }

    private String generateAuthToken() throws HttpClientException {
        // Get an authentication token from the AccountManager:
        // this call is asynchronous, as the user may not respond immediately.
        final AccountManager am = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        final AccountManagerFuture<Bundle> authResultFuture;

        // The AccountManager API for authentication token is different before
        // Ice Cream Sandwich.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            authResultFuture = GetTokenLegacy.INSTANCE.get(am, account);
        } else {
            authResultFuture = GetTokenICS.INSTANCE.get(am, account);
        }

        final Bundle authResult;
        try {
            authResult = authResultFuture.getResult();
        } catch (OperationCanceledException e) {
            throw new HttpClientException("Authentication failed: canceled by user", e);
        } catch (AuthenticatorException e) {
            throw new HttpClientException("Authentication failed", e);
        } catch (IOException e) {
            throw new HttpClientException("Authentication failed: network error", e);
        }
        if (authResult == null) {
            throw new HttpClientException("Authentication failed");
        }

        final String authToken = authResult.getString(AccountManager.KEY_AUTHTOKEN);
        if (authToken == null) {
            // No authentication token found:
            // the user must allow this application to use his account.
            final Intent authPermIntent = (Intent) authResult.get(AccountManager.KEY_INTENT);
            int flags = authPermIntent.getFlags();
            flags &= ~Intent.FLAG_ACTIVITY_NEW_TASK;
            authPermIntent.setFlags(flags);

            // The request is aborted: the application should retry later.
            throw new UserInteractionRequiredException(authPermIntent);
        }
        return authToken;
    }

    private String fetchAuthCookie(String authToken, boolean invalidateToken) throws HttpClientException {
        final AccountManager am = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (invalidateToken) {
            // Invalidate authentication token, and generate a new one.
            am.invalidateAuthToken(GOOGLE_ACCOUNT_TYPE, authToken);
            authToken = generateAuthToken();
        }

        final String loginUrl = "https://" + gaeHost + "/_ah/login?continue=http://localhost/&auth="
                + urlEncode(authToken);
        final HttpClient hc = new HttpClient(context);
        final HttpResponse resp;
        try {
            resp = hc.get(loginUrl).expect(HttpURLConnection.HTTP_MOVED_TEMP).execute();
        } catch (HttpClientException e) {
            throw new HttpClientException("Authentication failed", e);
        }

        // The authentication was successful.
        // Now we need to get the authentication cookie from the response.

        final Map<String, String> cookies = resp.getCookies();
        String authCookie = cookies.get("SACSID");
        if (authCookie == null) {
            if (!invalidateToken) {
                // Try again with a new authentication token.
                return fetchAuthCookie(authToken, true);
            }
        }
        if (authCookie == null) {
            throw new HttpClientException("Authentication failed");
        }

        return authCookie;
    }

    private static String urlEncode(String str) {
        String encoded = str;
        try {
            encoded = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Unlikely to happen.
        }
        return encoded;
    }

    @Override
    public void onRequest(HttpURLConnection conn) throws Exception {
        if (authCookieValue == null) {
            final String authToken = generateAuthToken();
            final String authCookie = fetchAuthCookie(authToken, false);
            authCookieValue = "SACSID=" + authCookie;
        }

        // The authentication cookie is only stored in memory, in order to
        // prevent security issues.

        conn.addRequestProperty("Cookie", authCookieValue);
    }

    private static class GetTokenLegacy {
        public static final GetTokenLegacy INSTANCE = new GetTokenLegacy();

        @SuppressWarnings("deprecation")
        public AccountManagerFuture<Bundle> get(AccountManager am, Account account) {
            return am.getAuthToken(account, "ah", false, null, null);
        }
    }

    private static class GetTokenICS {
        public static final GetTokenICS INSTANCE = new GetTokenICS();

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        public AccountManagerFuture<Bundle> get(AccountManager am, Account account) {
            return am.getAuthToken(account, "ah", null, false, null, null);
        }
    }
}
