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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * {@link HttpResponseHandler} implementation for writing the response to a
 * file.
 * @author Pixmob
 */
class WriteToFileHandler extends HttpResponseHandler {
    private final File file;
    
    public WriteToFileHandler(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        this.file = file;
    }
    
    @Override
    public void onResponse(HttpResponse response) throws HttpClientException {
        InputStream in = null;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            in = response.getPayload();
            
            final byte[] buf = new byte[1024];
            for (int bytesRead = 0; (bytesRead = in.read(buf)) != -1;) {
                out.write(buf, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new HttpClientException(
                    "Cannot write Http response to file: " + file.getPath(), e);
        } finally {
            IOUtils.close(out);
        }
    }
}
