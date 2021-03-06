package com.pipper.andreboot;

import com.pipper.andreboot.core.context.Context;
import com.pipper.andreboot.core.context.ContextRepository;
import com.pipper.andreboot.core.context.SimpleContext;
import com.pipper.andreboot.core.job.*;
import com.pipper.andreboot.core.pipeline.Pipeline;
import com.pipper.andreboot.core.pipeline.PipelineRepository;
import com.pipper.andreboot.core.task.JobTask;
import com.pipper.andreboot.core.task.TaskExecutor;
import com.pipper.andreboot.core.task.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;

@Component
public class Coordinator {

    private PipelineRepository pipelineRepository;
    private JobRepository jobRepository;
    private ApplicationEventPublisher eventPublisher;
    private ContextRepository contextRepository;
    private TaskExecutor taskExecutor;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Starts a job instance.
     *
     * @param aPipelineId
     *          The ID of the pipeline that will execute the job.
     * @param aInput
     *          A Key-Value map representing the Job's input.
     * @return Job
     *           The instance of the Job
     */
    public Job start (String aPipelineId, Map<String, Object> aInput) {
        Assert.notNull(aPipelineId,"pipelineId must not be null");
        Pipeline pipeline = pipelineRepository.findOne(aPipelineId);
        Assert.notNull(pipeline,String.format("Unkown pipeline: %s", aPipelineId));

        MutableJob job = new MutableJob(pipeline);
        job.setStatus(JobStatus.STARTED);
        log.debug("Job {} started",job.getId());
        jobRepository.save(job);

        Context context = new SimpleContext(job.getId(), aInput!=null?aInput: Collections.emptyMap());
        contextRepository.save(context);

        execute (job);

        return job;
    }

    private void execute (MutableJob aJob) {
        if(aJob.hasMoreTasks()) {
            JobTask nextTask = aJob.nextTask();
            jobRepository.save(aJob);
            taskExecutor.execute(nextTask);
        }
        else {
            MutableJob job = new MutableJob(aJob);
            job.setStatus(JobStatus.COMPLETED);
            jobRepository.save(job);
            log.debug("Job {} completed successfully",aJob.getId());
        }
    }

    /**
     * Stop a running job.
     *
     * @param aJobId
     *          The id of the job to stop
     *
     * @return The stopped {@link Job}
     */
    public Job stop (String aJobId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Resume a stopped or failed job.
     *
     * @param aJobId
     *          The id of the job to resume.
     * @return The resumed job
     */
    public Job resume (String aJobId) {
        throw new UnsupportedOperationException();
    }

    /**
     * Complete a task of a given job.
     *
     * @param aTask
     *          The task to complete.
     */
    public void complete (JobTask aTask) {
        log.debug("Completing task {}", aTask.getId());
        MutableJobTask task = new MutableJobTask(aTask);
        task.setStatus(TaskStatus.COMPLETED);
        MutableJob job = new MutableJob (jobRepository.findJobByTaskId (aTask.getId()));
        Assert.notNull(job,String.format("No job found for task %s ",aTask.getId()));
        job.updateTask(task);
        jobRepository.save(job);
        execute (job);
    }

    /**
     * Handle an erroring task.
     *
     * @param aTask
     *          The task to handle.
     */
    public void error (JobTask aTask) {
        throw new UnsupportedOperationException();
    }

    /**
     * Handles application events.
     *
     * @param aEvent
     *          The event to handle
     */
    public void on (Object aEvent) {
        eventPublisher.publishEvent (aEvent);
    }

    @Autowired
    public void setContextRepository(ContextRepository aContextRepository) {
        contextRepository = aContextRepository;
    }

    @Autowired
    public void setEventPublisher(ApplicationEventPublisher aEventPublisher) {
        eventPublisher = aEventPublisher;
    }

    @Autowired
    public void setJobRepository(JobRepository aJobRepository) {
        jobRepository = aJobRepository;
    }

    @Autowired
    public void setTaskExecutor(TaskExecutor aTaskExecutor) {
        taskExecutor = aTaskExecutor;
    }

    @Autowired
    public void setPipelineRepository(PipelineRepository aPipelineRepository) {
        pipelineRepository = aPipelineRepository;
    }

}
