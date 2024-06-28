package de.uniks.stp24.service.game;

import de.uniks.stp24.model.Jobs.*;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;


import javax.inject.Inject;
import java.util.*;

public class JobsService {
    @Inject
    JobsApiService jobsApiService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;
    @Inject
    EventListener eventListener;

    Map<String, ObservableList<Job>> jobs = new HashMap<>();

    @Inject
    public JobsService() {
        this.jobs.put("building", FXCollections.observableArrayList());
        this.jobs.put("district", FXCollections.observableArrayList());
        this.jobs.put("upgrade", FXCollections.observableArrayList());
        this.jobs.put("technology", FXCollections.observableArrayList());
        this.jobs.put("collection", FXCollections.observableArrayList());
    }

    /**
     * Load jobs started by the player's empire upon entering the game.
     */
    public void loadEmpireJobs() {
        this.subscriber.subscribe(this.jobsApiService.getEmpireJobs(
                        this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId()),
                jobList -> jobList.forEach(job -> {
                    this.jobs.get(job.getType()).add(job);
                    this.jobs.get("collection").add(job);
                }),
                error -> System.out.println("Failed loading jobs")
        );
    }

    /**
     * Create a listener on job updates.
     */
    public void initializeJobsListener() {
        this.subscriber.subscribe(this.eventListener.listen(
                String.format("games.%s.empires.%s.jobs.*.*", this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId()),
                Job.class), result -> {
                    System.out.println("Job update came!");
                    Job job = result.data();
                    switch (result.suffix()) {
                        case "created" -> {
                            System.out.println("Created job: " + job.getType());
                            this.jobs.get(job.getType()).add(job);
                            this.jobs.get("collection").add(job);
                        }
                        case "updated" -> {
                            System.out.println("Updating: " + job.getType());

                            this.jobs.get(job.getType()).replaceAll(other -> other.equals(job) ? job : other);
                            this.jobs.get("collection").replaceAll(other -> other.equals(job) ? job : other);
                        }
                        case "deleted" -> {
                            System.out.println("Deletion: " + job.getType());
                            System.out.println("Is job empty? " + Objects.isNull(job));
                            System.out.println("Is job id empty? " + Objects.isNull(job.getJobID()));
                            this.jobs.get(job.getType()).removeIf(other -> other.getJobID().equals(job.getJobID()));
                            this.jobs.get("collection").removeIf(other -> other.getJobID().equals(job.getJobID()));
                        }
                    }}, error ->
                System.out.println(error.getMessage()));
    }

    /**
     * Begins a new job from given {@link JobDTO jobDTO}.
     * @param jobDTO DTO containing a request type (and optionally an island)
     * @return {@link Job Job} class containing the result of starting the job.
     */
    public Observable<Job> beginJob(JobDTO jobDTO) {
        return this.jobsApiService.createNewJob(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(), jobDTO);
    }

    public Observable<Job> getJobByID(String jobID) {
        return this.jobsApiService.getJobByID(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(), jobID);
    }

    /**
     * Stops the job of the given id. If the job is not completed, the resources for its initializing will be returned
     * to the empire.
     * @param jobID ID of the job that needs to be stopped
     * @return {@link Job Job} class containing the result of stopping the job.
     */
    public Observable<Job> stopJob(String jobID) {
        return this.jobsApiService.deleteJob(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(), jobID);
    }

    /**
     * Stops the given job. If the job is not completed, the resources for its initializing will be returned
     * to the empire.
     * @param job Job class that needs to be stopped
     * @return {@link Job Job} class containing the result of stopping the job.
     */
    public Observable<Job> stopJob(Job job) {
        return this.stopJob(job.getJobID());
    }

    public Observable<Job> setJobPriority(String jobID, int priority) {
        return this.jobsApiService.patchJobPriority(this.tokenStorage.getGameId(),
                this.tokenStorage.getEmpireId(), jobID, priority);
    }

    /**
     * Returns an {@link ObservableList ObservableList}<{@link Job Job}> of a specific job type
     * that will be dynamically updated upon starting, editing or deleting jobs.
     * @param jobType - type of the job
     * @return
     */
    public ObservableList<Job> getJobsObservableList(String jobType) {
        return this.jobs.get(jobType);
    }

    public ObservableList<Job> getJobsObservableList() {
        return this.getJobsObservableList("collection");
    }
}
