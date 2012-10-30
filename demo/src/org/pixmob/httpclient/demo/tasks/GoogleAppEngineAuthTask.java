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
package org.pixmob.httpclient.demo.tasks;

import org.pixmob.httpclient.GoogleAppEngineAuthenticator;
import org.pixmob.httpclient.HttpClient;
import org.pixmob.httpclient.demo.R;
import org.pixmob.httpclient.demo.Task;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

/**
 * {@link Task} implementation for authenticating a request with a Google App
 * Engine hosted application.
 * @author Pixmob
 */
public class GoogleAppEngineAuthTask extends Task {
    public GoogleAppEngineAuthTask(final Context context) {
        super(context, R.string.task_gae_auth);
    }

    @Override
    protected void doRun() throws Exception {
        final AccountManager am = (AccountManager) getContext().getSystemService(Context.ACCOUNT_SERVICE);
        final Account[] accounts = am.getAccountsByType(GoogleAppEngineAuthenticator.GOOGLE_ACCOUNT_TYPE);
        if (accounts == null || accounts.length == 0) {
            throw new IllegalStateException("No Google account found");
        }

        final String gaeHost = "pushpushandroid.appspot.com";
        final GoogleAppEngineAuthenticator auth = new GoogleAppEngineAuthenticator(getContext(), accounts[0],
                gaeHost);
        final HttpClient hc = createClient();
        hc.head("https://" + gaeHost).with(auth).execute();
    }
}
