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

    Map<String, ObservableList<Job>> jobCollections = new HashMap<>();
    Map<String, ArrayList<Runnable>> jobCompletionFunctions = new HashMap<>();
    Map<String, ArrayList<Runnable>> jobDeletionFunctions = new HashMap<>();
    Map<String, ArrayList<Runnable>> jobProgressFunctions = new HashMap<>();
    Map<String, ArrayList<Runnable>> jobTypeFunctions = new HashMap<>();

    @Inject
    public JobsService() {
    }

    /**
     * Load jobCollections started by the player's empire upon entering the game.
     */
    public void loadEmpireJobs() {
        this.jobCollections.put("building", FXCollections.observableArrayList());
        this.jobCollections.put("district", FXCollections.observableArrayList());
        this.jobCollections.put("upgrade", FXCollections.observableArrayList());
        this.jobCollections.put("technology", FXCollections.observableArrayList());
        this.jobCollections.put("collection", FXCollections.observableArrayList());

        this.subscriber.subscribe(this.jobsApiService.getEmpireJobs(
                        this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId()),
                jobList -> jobList.forEach(this::addJobToGroups),
                error -> System.out.println("Failed loading jobCollections")
        );
    }

    /**
     * Create a listener on job updates.
     */
    public void initializeJobsListener() {
        this.subscriber.subscribe(this.eventListener.listen(String.format("games.%s.empires.%s.jobs.*.*",
                this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId()), Job.class), result -> {
            Job job = result.data();
            switch (result.suffix()) {
                case "created" -> this.addJobToGroups(job);
                case "updated" -> this.updateJobInGroups(job);
                case "deleted" -> this.deleteJobFromGroups(job);
            }}, error -> System.out.println(error.getMessage()));
    }

    public void addJobToGroups(Job job) {
        this.jobCollections.get(job.type()).add(job);
        this.jobCollections.get("collection").add(job);

        if (!job.system().isEmpty()) {
            if (!this.jobCollections.containsKey(job.system())) {
                this.jobCollections.put(job.system(), FXCollections.observableArrayList(job));
            }
            else {
                this.jobCollections.get(job.system()).add(job);
            }
        }
    }

    public void updateJobInGroups(Job job) {
        this.jobCollections.get(job.type()).replaceAll(other -> other.equals(job) ? job : other);
        this.jobCollections.get("collection").replaceAll(other -> other.equals(job) ? job : other);

        if (!job.system().isEmpty()) {
            if (!this.jobCollections.containsKey(job.system()))
                this.jobCollections.put(job.system(), FXCollections.observableArrayList(job));
            else
                this.jobCollections.get(job.system()).replaceAll(other -> other.equals(job) ? job : other);
        }

        if (this.jobProgressFunctions.containsKey(job._id())) {
            this.jobProgressFunctions.get(job._id()).forEach(Runnable::run);
        }
    }

    public void deleteJobFromGroups(Job job) {
        this.jobCollections.get(job.type()).removeIf(other -> other._id().equals(job._id()));
        this.jobCollections.get("collection").removeIf(other -> other._id().equals(job._id()));

        if (!job.system().isEmpty()) {
            if (this.jobCollections.containsKey(job.system())) {
                this.jobCollections.get(job.system()).removeIf(other -> other._id().equals(job._id()));
            }
        }

        if (this.jobCompletionFunctions.containsKey(job._id())) {
            this.jobCompletionFunctions.get(job._id()).forEach(Runnable::run);
        }
    }

    public void deleteJobFromGroups(String jobID) {
        this.jobCollections.forEach((key, list) -> list.removeIf(job -> job._id().equals(jobID)));
        this.jobCompletionFunctions.remove(jobID);
    }

    /**
     * A method used to define the further execution result of a job.It's useful if you need to execute
     * some methods that lay within other classes. The execution function will be deleted after the job is completed.
     * It is possible to add more than one function on the job completion.
     * @param jobID ID of the job after completion of which the function will be executed
     * @param func the execution function
     */
    public void onJobCompletion(String jobID, Runnable func) {
        if (!this.jobCompletionFunctions.containsKey(jobID))
            this.jobCompletionFunctions.put(jobID, new ArrayList<>());
        this.jobCompletionFunctions.get(jobID).add(func);
    }

    public void onJobProgress(String jobID, Runnable func) {
        if (!this.jobProgressFunctions.containsKey(jobID))
            this.jobProgressFunctions.put(jobID, new ArrayList<>());
        this.jobProgressFunctions.get(jobID).add(func);
    }

    public boolean hasOnProgress(String jobID) {
        if (this.jobProgressFunctions.containsKey(jobID))
            return this.jobProgressFunctions.get(jobID).size() > 0;
        return false;
    }

    public void onJobTypeProgress(String jobType, Runnable func) {
        if (!this.jobProgressFunctions.containsKey(jobType))
            this.jobProgressFunctions.put(jobType, new ArrayList<>());
        this.jobProgressFunctions.get(jobType).add(func);
    }

    public void onJobDeletion(String jobID, Runnable func) {
        if (!this.jobDeletionFunctions.containsKey(jobID))
            this.jobDeletionFunctions.put(jobID, new ArrayList<>());
        this.jobDeletionFunctions.get(jobID).add(func);
    }

    /**
     * Begins a new job from given {@link JobDTO jobDTO}.
     * @param jobDTO DTO containing a request type (and optionally an island)
     * @return {@link Job Job} class containing the result of starting the job.
     */
    public Observable<Job> beginJob(JobDTO jobDTO) {
        return this.jobsApiService.createNewJob(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(), jobDTO);
    }

    /**
     * Stops the job of the given id. If the job is not completed, the resources for its initializing will be returned
     * to the empire.
     * @param jobID ID of the job that needs to be stopped
     * @return {@link Job Job} class containing the result of stopping the job.
     */
    public Observable<Job> stopJob(String jobID) {
        this.deleteJobFromGroups(jobID);
        if (this.jobDeletionFunctions.containsKey(jobID))
            this.jobDeletionFunctions.get(jobID).forEach(Runnable::run);

        return this.jobsApiService.deleteJob(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(), jobID);
    }

    /**
     * Stops the given job. If the job is not completed, the resources for its initializing will be returned
     * to the empire.
     * @param job Job class that needs to be stopped
     * @return {@link Job Job} class containing the result of stopping the job.
     */
    public Observable<Job> stopJob(Job job) {
        return this.stopJob(job._id());
    }

    public Observable<Job> setJobPriority(String jobID, int priority) {
        return this.jobsApiService.patchJobPriority(this.tokenStorage.getGameId(),
                this.tokenStorage.getEmpireId(), jobID, priority);
    }

    /**
     * Returns an {@link ObservableList ObservableList}<{@link Job Job}> of a specific job type
     * that will be dynamically updated upon starting, editing or deleting jobCollections.
     * @param jobType - type of the job
     * @return
     */
    public ObservableList<Job> getJobObservableListOfType(String jobType) {
        return this.jobCollections.get(jobType);
    }

    public ObservableList<Job> getObservableListForSystem(String systemID) {
        if (!this.jobCollections.containsKey(systemID)) {
            this.jobCollections.put(systemID, FXCollections.observableArrayList());
        }
        return this.jobCollections.get(systemID);
    }

    public ObservableList<Job> getObservableJobCollection() {
        return this.getJobObservableListOfType("collection");
    }
}
