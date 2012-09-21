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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Build;

/**
 * When the Http server receives a request from a client, the Http response is
 * sent back.
 * @author Pixmob
 */
public final class HttpResponse {
    private final int statusCode;
    private final Map<String, String> cookies;
    private final Map<String, List<String>> headers;
    private InputStream payload;

    HttpResponse(final int statusCode, final InputStream payload, final Map<String, List<String>> rawHeaders,
            final Map<String, String> cookies) {
        this.statusCode = statusCode;
        this.payload = payload;
        this.cookies = Collections.unmodifiableMap(cookies);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            // Before Gingerbread, Android has a bug where all headers are
            // stored in lower-case:
            // http://code.google.com/p/android/issues/detail?id=6684
            final Map<String, List<String>> newHeaders = new HashMap<String, List<String>>(rawHeaders.size());
            for (final Map.Entry<String, List<String>> e : rawHeaders.entrySet()) {
                final String key = e.getKey();
                final int keyLen = key.length();
                final StringBuilder newKey = new StringBuilder(keyLen);
                for (int i = 0; i < keyLen; ++i) {
                    final char c = key.charAt(i);
                    final char c2;
                    if (i == 0 || key.charAt(i - 1) == '-') {
                        c2 = Character.toUpperCase(c);
                    } else {
                        c2 = c;
                    }
                    newKey.append(c2);
                }
                newHeaders.put(newKey.toString(), e.getValue());
            }
            this.headers = Collections.unmodifiableMap(newHeaders);
        } else {
            this.headers = Collections.unmodifiableMap(rawHeaders);
        }
    }

    /**
     * Get the content type for this response, or <code>null</code> if unknown.
     */
    public String getContentType() {
        final String contentType = getFirstHeaderValue("Content-Type");
        if (contentType == null) {
            return null;
        }

        final int i = contentType.indexOf(';');
        return i == -1 ? contentType : contentType.substring(0, i).trim();
    }

    /**
     * Get the charset for this response, or <code>null</code> if unknown.
     */
    public String getContentCharset() {
        final String contentType = getFirstHeaderValue("Content-Type");
        if (contentType == null) {
            return null;
        }

        final int i = contentType.indexOf('=');
        return i == -1 ? null : contentType.substring(i + 1).trim();
    }

    /**
     * Get the response payload.
     */
    public InputStream getPayload() {
        return payload;
    }

    void preload() throws IOException {
        final ByteArrayOutputStream outBuf = new ByteArrayOutputStream(2048);
        final byte[] inBuf = new byte[1024];
        final InputStream input = getPayload();
        for (int bytesRead = 0; (bytesRead = input.read(inBuf)) != -1;) {
            outBuf.write(inBuf, 0, bytesRead);
        }

        payload = new ByteArrayInputStream(outBuf.toByteArray());
    }

    public void read(StringBuilder buffer) throws IOException {
        String enc = getContentCharset();
        if (enc == null) {
            enc = "UTF-8";
        }

        final InputStream input = getPayload();
        final InputStreamReader reader = new InputStreamReader(input, enc);
        final char[] inBuf = new char[64];
        for (int charsRead; (charsRead = reader.read(inBuf)) != -1;) {
            buffer.append(inBuf, 0, charsRead);
        }
    }

    /**
     * Get the response status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the response headers.
     */
    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Get the first header value, or <code>null</code> if unset.
     */
    public String getFirstHeaderValue(String name) {
        final List<String> values = headers.get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }

    /**
     * Get the response cookies.
     */
    public Map<String, String> getCookies() {
        return cookies;
    }
}
