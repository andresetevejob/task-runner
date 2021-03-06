package com.pipper.andreboot.core.task;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EachTaskExecutor implements TaskExecutor, TaskExecutorResolver {

    @Override
    public void execute(JobTask aTask) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TaskExecutor resolve (JobTask aTask) {
        if(aTask.getType().equals("each")) {
            return this;
        }
        return null;
    }

}
