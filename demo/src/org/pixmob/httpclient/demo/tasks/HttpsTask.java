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
import org.pixmob.httpclient.demo.R;
import org.pixmob.httpclient.demo.Task;

import android.content.Context;

/**
 * {@link Task} implementation for connecting using Https.
 * @author Pixmob
 */
public class HttpsTask extends Task {
    public HttpsTask(final Context context) {
        super(context, R.string.task_https);
    }

    @Override
    protected void doRun() throws Exception {
        final String[] httpsUrls = { "https://www.google.com", "https://www.facebook.com",
                "https://twitter.com", "https://mobile.free.fr/moncompte/", };

        final HttpClient hc = createClient();
        for (final String url : httpsUrls) {
            hc.get(url)
                    .expectStatusCode(HttpURLConnection.HTTP_OK, HttpURLConnection.HTTP_MOVED_TEMP,
                            HttpURLConnection.HTTP_MOVED_PERM).execute();
        }
    }
}
