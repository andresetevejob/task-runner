package com.pipper.andreboot.core;

import com.pipper.andreboot.core.job.MutableJobTask;
import com.pipper.andreboot.core.messenger.Messenger;
import com.pipper.andreboot.core.task.JobTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DefaultWorker {

    private TaskHandlerResolver taskHandlerResolver;
    private Messenger messenger;

    /**
     * Handle the execution of a {@link JobTask}. Implementors
     * are expected to execute the task asynchronously.
     *
     * @param aTask
     *          The task to execute.
     */
    public void handle (JobTask aTask) {
        TaskHandler<?> taskHandler = taskHandlerResolver.resolve(aTask);
        try {
            Object output = taskHandler.handle(aTask);
            MutableJobTask completion = new MutableJobTask(aTask);
            if(output!=null) {
                completion.setOutput(output);
            }
            completion.setCompletionDate(new Date());
            messenger.send("completions", completion);
        }
        catch (Exception e) {
            MutableJobTask jobTask = new MutableJobTask(aTask);
            jobTask.setException(e);
            messenger.send("errors", jobTask);
        }
    }

    /**
     * Cancel the execution of a running task.
     *
     * @param aTaskId
     *          The ID of the task to cancel.
     */
    public void cancel (String aTaskId) {
        throw new UnsupportedOperationException();
    }

    @Autowired
    public void setTaskHandlerResolver(TaskHandlerResolver aTaskHandlerResolver) {
        taskHandlerResolver = aTaskHandlerResolver;
    }

    @Lazy
    @Autowired
    public void setMessenger(Messenger aMessenger) {
        messenger = aMessenger;
    }

}
