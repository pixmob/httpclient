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
package org.pixmob.hcl.demo.tasks;

import org.pixmob.hcl.HttpClient;
import org.pixmob.hcl.HttpResponse;
import org.pixmob.hcl.HttpResponseHandler;
import org.pixmob.hcl.demo.R;
import org.pixmob.hcl.demo.Task;

import android.content.Context;

/**
 * {@link Task} implementation for reading <code>Content-Type</code> headers.
 * @author Pixmob
 */
public class ContentTypeTask extends Task {
    public ContentTypeTask(final Context context) {
        super(context, R.string.task_content_type);
    }
    
    @Override
    protected void doRun() throws Exception {
        final HttpClient hc = createClient();
        hc.get("http://www.wired.com/")
                .setHandler(new CheckContentHandler("text/html", "UTF-8"))
                .execute();
        hc.head("http://www.google.com/intl/en_com/images/srpr/logo3w.png")
                .setHandler(new CheckContentHandler("image/png", null))
                .execute();
    }
    /**
     * Handler for checking content type of an Http response.
     */
    private static class CheckContentHandler extends HttpResponseHandler {
        private final String contentType;
        private final String contentCharset;
        
        public CheckContentHandler(final String contentType,
                final String contentEncoding) {
            this.contentType = contentType;
            this.contentCharset = contentEncoding;
        }
        
        @Override
        public void onResponse(HttpResponse response) throws Exception {
            if (contentType != null) {
                assertEquals(contentType, response.getContentType());
            }
            if (contentCharset != null) {
                assertEquals(contentCharset, response.getContentCharset());
            }
        }
    }
}
