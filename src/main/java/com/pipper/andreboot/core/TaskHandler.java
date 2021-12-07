package com.pipper.andreboot.core;

import com.pipper.andreboot.core.task.JobTask;

/**
 * A startegy interface used for executing a {@link JobTask}.
 */
public interface TaskHandler<T> {
    T handle (JobTask aTask) throws Exception;
}
