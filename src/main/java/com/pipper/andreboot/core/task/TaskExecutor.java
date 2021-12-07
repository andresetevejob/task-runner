package com.pipper.andreboot.core.task;

/**
 * A strategy interface used by implementations who are
 * responsible for executing {@link JobTask} instances.
 *
 * */
 public interface TaskExecutor {
    /**
     * Executes a {@link JobTask} instance.
     *
     * @param aTask
     *          The task to execute
     */
    void execute (JobTask aTask);
}
