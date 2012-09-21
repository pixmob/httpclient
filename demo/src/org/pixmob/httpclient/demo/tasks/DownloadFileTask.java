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

import java.io.File;

import org.pixmob.httpclient.HttpClient;
import org.pixmob.httpclient.demo.R;
import org.pixmob.httpclient.demo.Task;
import org.pixmob.httpclient.demo.TaskExecutionFailedException;

import android.content.Context;

/**
 * {@link Task} implementation for downloading a file.
 * @author Pixmob
 */
public class DownloadFileTask extends Task {
    public DownloadFileTask(final Context context) {
        super(context, R.string.task_download_file);
    }

    @Override
    protected void doRun() throws Exception {
        final File imgFile = new File(getContext().getCacheDir(), "google_logo.png");
        imgFile.delete();

        final HttpClient hc = createClient();
        hc.get("http://www.google.fr/images/srpr/logo3w.png").to(imgFile).execute();

        if (!imgFile.exists() || imgFile.length() == 0) {
            throw new TaskExecutionFailedException("File download failed");
        }
    }
}
