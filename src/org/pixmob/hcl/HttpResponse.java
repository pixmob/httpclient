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
package org.pixmob.hcl;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * When the Http server receives a request from a client, the Http response is
 * sent back.
 * @author Pixmob
 */
public final class HttpResponse {
    private final int statusCode;
    private final InputStream payload;
    private final Map<String, String> cookies;
    private final Map<String, List<String>> headers;
    
    HttpResponse(final int statusCode, final InputStream payload,
            final Map<String, List<String>> headers,
            final Map<String, String> cookies) {
        this.statusCode = statusCode;
        this.payload = payload;
        this.headers = Collections.unmodifiableMap(headers);
        this.cookies = Collections.unmodifiableMap(cookies);
    }
    
    /**
     * Get the content type for this response, or <code>null</code> if unknown.
     */
    public String getContentType() {
        final List<String> contentTypes = headers.get("Content-Type");
        if (contentTypes == null || contentTypes.isEmpty()) {
            return null;
        }
        return contentTypes.get(0);
    }
    
    /**
     * Get the response payload.
     */
    public InputStream getPayload() {
        return payload;
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
     * Get the response cookies.
     */
    public Map<String, String> getCookies() {
        return cookies;
    }
}
