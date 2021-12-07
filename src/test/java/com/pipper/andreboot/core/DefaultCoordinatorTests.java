package com.pipper.andreboot.core;

import com.pipper.andreboot.Coordinator;
import com.pipper.andreboot.core.context.SimpleContextRepository;
import com.pipper.andreboot.core.job.Job;
import com.pipper.andreboot.core.job.JobStatus;
import com.pipper.andreboot.core.job.SimpleJobRepository;
import com.pipper.andreboot.core.messenger.SimpleMessenger;
import com.pipper.andreboot.core.pipeline.YamlPipelineRepository;
import com.pipper.andreboot.core.task.DefaultTaskExecutor;
import com.pipper.andreboot.core.task.JobTask;
import com.pipper.andreboot.taskhandler.io.Print;
import com.pipper.andreboot.taskhandler.sleep.Sleep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
public class DefaultCoordinatorTests {
    @Test
    public void testStartJob () {

        DefaultWorker worker = new DefaultWorker();
        Coordinator coordinator = new Coordinator ();
        SimpleMessenger workerMessenger = new SimpleMessenger();
        workerMessenger.receive("completions", (o)->coordinator.complete((JobTask)o));
        worker.setMessenger(workerMessenger);
        DefaultTaskHandlerResolver taskHandlerResolver = new DefaultTaskHandlerResolver();

        Map<String,TaskHandler<?>> handlers = new HashMap<>();
        handlers.put("print", new Print());
        handlers.put("sleep", new Sleep());

        taskHandlerResolver.setTaskHandlers(handlers);

        worker.setTaskHandlerResolver(taskHandlerResolver);

        coordinator.setContextRepository(new SimpleContextRepository());
        SimpleJobRepository jobRepository = new SimpleJobRepository();
        coordinator.setJobRepository(jobRepository);
        coordinator.setPipelineRepository(new YamlPipelineRepository());

        SimpleMessenger coordinatorMessenger = new SimpleMessenger();
        coordinatorMessenger.receive("tasks", (o)->worker.handle((JobTask)o));
        DefaultTaskExecutor taskExecutor = new DefaultTaskExecutor();
        taskExecutor.setMessenger(coordinatorMessenger);
        coordinator.setTaskExecutor(taskExecutor);

        Job job = coordinator.start("demo/hello", new HashMap<String, Object> ());

        Job completedJob = jobRepository.findOne(job.getId());
        Assertions.assertEquals(JobStatus.COMPLETED, completedJob.getStatus());
    }
}
