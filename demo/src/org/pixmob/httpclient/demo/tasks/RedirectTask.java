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

import java.net.HttpURLConnection;

import org.pixmob.httpclient.HttpClient;
import org.pixmob.httpclient.HttpResponse;
import org.pixmob.httpclient.demo.R;
import org.pixmob.httpclient.demo.Task;

import android.content.Context;

/**
 * {@link Task} implementation for testing redirection (status code 301).
 * @author Pixmob
 */
public class RedirectTask extends Task {
    public RedirectTask(final Context context) {
        super(context, R.string.task_redirect);
    }

    @Override
    protected void doRun() throws Exception {
        final HttpClient hc = createClient();
        final HttpResponse response = hc.get("http://google.com").expect(HttpURLConnection.HTTP_MOVED_PERM)
                .execute();
        assertEquals("text/html", response.getContentType());
        assertEquals("UTF-8", response.getContentCharset());
    }
}
