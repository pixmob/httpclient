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

/**
 * Base class for task.
 * @author Pixmob
 */
public abstract class Task {
    private final int name;
    
    public Task(final int name) {
        this.name = name;
    }
    
    public int getName() {
        return name;
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
