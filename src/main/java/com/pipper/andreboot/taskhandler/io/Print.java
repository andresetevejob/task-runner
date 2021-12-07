package com.pipper.andreboot.taskhandler.io;

import com.pipper.andreboot.core.TaskHandler;
import com.pipper.andreboot.core.task.JobTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Print implements TaskHandler<Object> {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Object handle (JobTask aTask) {
        log.info(aTask.getRequiredString("text"));
        return null;
    }

}
