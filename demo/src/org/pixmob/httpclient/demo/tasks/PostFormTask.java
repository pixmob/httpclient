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

import org.json.JSONObject;
import org.pixmob.httpclient.HttpClient;
import org.pixmob.httpclient.HttpResponse;
import org.pixmob.httpclient.HttpResponseHandler;
import org.pixmob.httpclient.demo.R;
import org.pixmob.httpclient.demo.Task;

import android.content.Context;

/**
 * {@link Task} implementation posting a form.
 * @author Pixmob
 */
public class PostFormTask extends Task {
    public PostFormTask(final Context context) {
        super(context, R.string.task_post_form);
    }

    @Override
    protected void doRun() throws Exception {
        final HttpClient hc = createClient();
        hc.post("http://groovyconsole.appspot.com/executor.groovy")
                .setParameter("script", "printf 'Hello Android!'").setHandler(new HttpResponseHandler() {
                    @Override
                    public void onResponse(HttpResponse response) throws Exception {
                        final StringBuilder rawJson = new StringBuilder(64);
                        response.read(rawJson);

                        final JSONObject json = new JSONObject(rawJson.toString());
                        assertEquals("Hello Android!", json.getString("outputText"));
                    }
                }).execute();
    }
}
