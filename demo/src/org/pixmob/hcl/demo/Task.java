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
package org.pixmob.hcl.demo;

import org.pixmob.hcl.HttpClient;

import android.content.Context;

/**
 * Base class for task.
 * @author Pixmob
 */
public abstract class Task {
    private final Context context;
    private final String name;
    private final String sourceCodeUrl;
    
    public Task(final Context context, final int name) {
        this.context = context;
        this.name = context.getString(name);
        this.sourceCodeUrl = "https://raw.github.com/pixmob/hcl/master/demo/src/"
                + getClass().getName().replace('.', '/') + ".java";
    }
    
    protected HttpClient createClient() {
        final HttpClient hc = new HttpClient(context);
        hc.setConnectTimeout(4000);
        hc.setReadTimeout(8000);
        return hc;
    }
    
    public Context getContext() {
        return context;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSourceCodeUrl() {
        return sourceCodeUrl;
    }
    
    public static void assertEquals(String expected, String tested)
            throws TaskExecutionFailedException {
        if (expected == null && tested == null) {
            return;
        }
        if (expected.equals(tested)) {
            return;
        }
        throw new TaskExecutionFailedException("Expected: " + expected
                + "; got " + tested);
    }
    
    public final void run() throws TaskExecutionFailedException {
        try {
            doRun();
        } catch (TaskExecutionFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new TaskExecutionFailedException("Task execution failed", e);
        }
    }
    
    protected abstract void doRun() throws Exception;
}
