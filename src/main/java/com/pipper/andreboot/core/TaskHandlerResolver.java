package com.pipper.andreboot.core;

import com.pipper.andreboot.core.task.JobTask;

/**
 * a strategy interface used for resolving a
 * {@link TaskHandler} implementation which can handle
 * the given {@link JobTask} instance. Implementations
 * are expected to return <code>null</code> if unable
 * to resolve an appropriate {@link TaskHandler} implementation
 * to allow for chaining multiple {@link TaskHandlerResolver}
 * implementations.
 */
public interface TaskHandlerResolver {
    TaskHandler<?> resolve (JobTask aJobTask);
}
