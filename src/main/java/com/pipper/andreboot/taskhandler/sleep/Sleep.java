package com.pipper.andreboot.taskhandler.sleep;

import com.pipper.andreboot.core.TaskHandler;
import com.pipper.andreboot.core.task.JobTask;
import org.springframework.stereotype.Component;

@Component
public class Sleep implements TaskHandler<Object> {

    @Override
    public Object handle (JobTask aTask) throws InterruptedException {
        Thread.sleep(aTask.getLong("millis", 1000));
        return null;
    }

}
