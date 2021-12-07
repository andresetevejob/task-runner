package com.pipper.andreboot.core.task;

import com.pipper.andreboot.core.messenger.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class DefaultTaskExecutor implements TaskExecutor, TaskExecutorResolver{
    private Messenger messenger;

    private static final String DEFAULT_TASK_QUEUE = "tasks";

    @Override
    public void execute (JobTask aTask) {
        String node = aTask.getNode();
        messenger.send(node!=null?node:DEFAULT_TASK_QUEUE, aTask);
    }

    @Autowired
    public void setMessenger(Messenger aMessenger) {
        messenger = aMessenger;
    }

    @Override
    public TaskExecutor resolve (JobTask aTask) {
        return this;
    }
}
