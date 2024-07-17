package de.uniks.stp24.service.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Jobs.*;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Consumer;

@Singleton
public class JobsService {
    @Inject
    public JobsApiService jobsApiService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public Subscriber subscriber;
    @Inject
    public EventListener eventListener;

    Map<String, ObservableList<Job>> jobCollections = new HashMap<>();
    Map<String, ArrayList<Runnable>> jobCompletionFunctions = new HashMap<>();
    Map<String, ArrayList<Runnable>> jobDeletionFunctions = new HashMap<>();
    Map<String, Consumer<String[]>> jobInspectionFunctions = new HashMap<>();
    Map<String, ArrayList<Runnable>> jobTypeFunctions = new HashMap<>();
    Map<String, ArrayList<Consumer<Job>>> jobTypeConsumers = new HashMap<>();
    Map<String, ArrayList<Consumer<Job>>> loadTypeFunctions = new HashMap<>();
    ArrayList<Runnable> loadCommonFunctions = new ArrayList<>();
    ArrayList<Runnable> finishCommonFunctions = new ArrayList<>();
    ArrayList<Runnable> startCommonFunctions = new ArrayList<>();
    ArrayList<Runnable> jobCommonUpdates = new ArrayList<>();

    @Inject
    public JobsService() {}

    /**
     * Loads jobCollections started by the player's empire upon entering the game. <p>
     * Call this method inside a method annotated with {@link org.fulib.fx.annotation.event.OnInit @OnInit}
     * within the {@link de.uniks.stp24.service.InGameService InGameService} controller before the
     * {@link #initializeJobsListener() initializeJobsListener} method.
     */
    public void loadEmpireJobs() {
        this.jobCollections.put("building", FXCollections.observableArrayList());
        this.jobCollections.put("district", FXCollections.observableArrayList());
        this.jobCollections.put("upgrade", FXCollections.observableArrayList());
        this.jobCollections.put("technology", FXCollections.observableArrayList());
        this.jobCollections.put("collection", FXCollections.observableArrayList());

        this.subscriber.subscribe(this.jobsApiService.getEmpireJobs(
                        this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId()), jobList -> {
                    jobList.forEach(this::addJobToGroups);

                    this.loadCommonFunctions.forEach(Runnable::run);
                    this.jobCollections.get("collection").forEach(job -> {
                        if (this.loadTypeFunctions.containsKey(job.type()))
                            this.loadTypeFunctions.get(job.type()).forEach(func -> func.accept(job));
                    });
                }, error -> System.out.println("JobsService: Failed to load job collections \n" + error.getMessage())
        );
    }

    /**
     * Creates a listener on job updates. <p>
     * Call this method inside a method annotated with {@link org.fulib.fx.annotation.event.OnInit @OnInit}
     * within the {@link de.uniks.stp24.service.InGameService InGameService} controller after the
     * {@link #loadEmpireJobs() loadEmpireJobs} method.
     */
    public void initializeJobsListener() {
        this.subscriber.subscribe(this.eventListener.listen(String.format("games.%s.empires.%s.jobs.*.*",
                this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId()), Job.class), result -> {
            Job job = result.data();

            switch (result.suffix()) {
                case "created" -> this.addJobToGroups(job);
                case "updated" -> this.updateJobInGroups(job);
                case "deleted" -> this.deleteJobFromGroups(job);
            }
            this.jobCommonUpdates.forEach(Runnable::run);

            }, error -> System.out.print("JobsService: Failed to receive job updates. \n" + error.getMessage()));
    }

    public void addJobToGroups(@NotNull Job job) {
        this.jobCollections.get(job.type()).add(job);

        if (!job.type().equals("technology")) {
            if (!this.jobCollections.containsKey(job.system()))
                this.jobCollections.put(job.system(), FXCollections.observableArrayList(job));
            else this.jobCollections.get(job.system()).add(job);

            if (this.jobCollections.get(job.system()).size() == 1)
                this.jobCollections.get("collection").add(job);
        }

        this.startCommonFunctions.forEach(Runnable::run);
    }

    public void updateJobInGroups(@NotNull Job job) {
        this.jobCollections.get(job.type()).replaceAll(other -> other.equals(job) ? job : other);
        this.jobCollections.get("collection").replaceAll(other -> other.equals(job) ? job : other);

        if (!job.type().equals("technology")) {
            if (!this.jobCollections.containsKey(job.system()))
                this.jobCollections.put(job.system(), FXCollections.observableArrayList(job));
            else this.jobCollections.get(job.system()).replaceAll(other -> other.equals(job) ? job : other);

            if (this.jobCollections.get(job.system()).filtered(job1 -> job1.type().equals(job.type())).isEmpty())
                this.jobCollections.get("collection").add(job);
        }

        if (this.jobTypeFunctions.containsKey(job.type()))
            this.jobTypeFunctions.get(job.type()).forEach(Runnable::run);
        if (this.jobTypeConsumers.containsKey(job.type()))
            this.jobTypeConsumers.get(job.type()).forEach(func -> func.accept(job));
    }

    public void deleteJobFromGroups(@NotNull Job job) {
        this.jobCollections.get(job.type()).removeIf(other -> other._id().equals(job._id()));
        this.jobCollections.get("collection").removeIf(other -> other._id().equals(job._id()));

        if (!job.type().equals("technology")) {
            this.jobCollections.get(job.system()).removeIf(other -> other._id().equals(job._id()));

            ObservableList<Job> systemJobs = this.jobCollections.get(job.system());
            if (!systemJobs.isEmpty() && !this.jobCollections.get("collection").contains(systemJobs.getFirst()))
                this.jobCollections.get("collection").add(systemJobs.getFirst());
        }

        if (this.jobCompletionFunctions.containsKey(job._id()))
            this.jobCompletionFunctions.get(job._id()).forEach(Runnable::run);

        this.finishCommonFunctions.forEach(Runnable::run);
    }

    private void deleteJobFromGroups(String jobID) {
        this.jobCollections.forEach((key, list) -> list.removeIf(job -> job._id().equals(jobID)));
        this.jobCompletionFunctions.remove(jobID);
    }

    /**
     * A method to define further common functions that should be executed when a job of any type
     * is started. It is possible to add more than one function on the job start.
     * @param func {@link Runnable runnable } lambda function that will be executed after any job starts
     */
    public void onJobCommonStart(Runnable func) {
        this.startCommonFunctions.add(func);
    }

    public void onJobCommonUpdates(Runnable func) {
        this.jobCommonUpdates.add(func);
    }

    /**
     * A method used to define the {@link Runnable Runnable} lambda functions that should be executed when
     * any job of certain type progresses. It is useful if you need to execute some methods that lay within
     * other classes. It is possible to add more than one function on the job type progress.
     * @param jobType type of the job on which update the function should be executed
     * @param func the execution function
     */
    public void onJobTypeProgress(String jobType, Runnable func) {
        if (!this.jobTypeFunctions.containsKey(jobType))
            this.jobTypeFunctions.put(jobType, new ArrayList<>());
        this.jobTypeFunctions.get(jobType).add(func);
    }

    public void onJobTypeProgress(String jobType, Consumer<Job> func) {
        if (!this.jobTypeConsumers.containsKey(jobType))
            this.jobTypeConsumers.put(jobType, new ArrayList<>());
        this.jobTypeConsumers.get(jobType).add(func);
    }

    /**
     * Use this method to check whether a certain job type has some functions that run as any job of this
     * type progresses.
     * @param jobType type of the job that has to be checked
     * @return true, if the job has a function set on its progress, false otherwise
     */
    public boolean hasNoJobTypeProgress(String jobType) {
        if (this.jobTypeFunctions.containsKey(jobType))
            return this.jobTypeFunctions.get(jobType).isEmpty();
        return true;
    }

    /**
     * Stops the execution of the functions that run on any job progress of the given type.
     * @param jobType type of the job for which functions execution should be canceled
     */
    public void stopOnJobTypeProgress(String jobType) {
        this.jobTypeFunctions.remove(jobType);
    }

    /**
     * A method used to define the further execution result of a job. It's useful if you need to execute
     * some methods that lay within other classes.
     * It is possible to add more than one function on the job completion.
     * @param jobID ID of the job after completion of which the function will be executed
     * @param func the execution function
     */
    public void onJobDeletion(String jobID, Runnable func) {
        if (!this.jobDeletionFunctions.containsKey(jobID))
            this.jobDeletionFunctions.put(jobID, new ArrayList<>());
        this.jobDeletionFunctions.get(jobID).add(func);
    }

    /**
     * A method used to define the {@link Runnable Runnable} lambda functions that should be called after the
     * job is completed. It's useful if you need to execute some methods that lay within other classes.
     * The execution function will be deleted after the job is completed.
     * It is possible to add more than one function on the job completion.
     * @param jobID ID of the job after completion of which the function will be executed
     * @param func the execution function
     */
    public void onJobCompletion(String jobID, Runnable func) {
        if (!this.jobCompletionFunctions.containsKey(jobID))
            this.jobCompletionFunctions.put(jobID, new ArrayList<>());
        this.jobCompletionFunctions.get(jobID).add(func);
    }


    /**
     * A method that is used to define functions that should be executed when any on
     */
    public void onJobCommonFinish(Runnable func) {
        this.finishCommonFunctions.add(func);
    }

    /**
     * Provides a {@link Consumer<String> Consumer}<{@link String}> lambda functions that should only be executed after
     * the initial job loading is completed. <p>
     * Provide this method with {@link #getJobObservableListOfType(String) getJobObservableListOfType},
     * {@link #getObservableListForSystem(String) getObservableListForSystem} or {@link #getObservableJobCollection()
     * getObservableJobCollection} inside a method annotated with {@link org.fulib.fx.annotation.event.OnInit @OnInit}
     * inside your {@link org.fulib.fx.annotation.controller.Controller Controller} to receive an {@link ObservableList}
     *  with loaded jobs. <p>
     *  Name the parameter inside the consumer function as a <i>jobID</i>: {@code (jobID) -> yourFunction(jobID)}.
     * @param func the function that has to be executed after the job loading process is finished
     */
    public void onJobsLoadingFinished(String jobType, Consumer<Job> func) {
        if (!this.loadTypeFunctions.containsKey(jobType))
            this.loadTypeFunctions.put(jobType, new ArrayList<>());
        this.loadTypeFunctions.get(jobType).add(func);
    }

    /**
     * Provides {@link Runnable Runnable} lambda functions that should only be executed after
     * the initial job loading is completed.
     * @param func the function that has to be executed after the job loading process is finished
     */
    public void onJobsLoadingFinished(Runnable func) {
        this.loadCommonFunctions.add(func);
    }

    /**
     * Begins a new job from given {@link JobDTO JobDTO}. Use the static methods of the
     * {@link de.uniks.stp24.model.Jobs Jobs} class to create a new JobDTO of a specific type.
     * @param jobDTO DTO containing a request type
     * @return {@link Job Job} class containing the {@link de.uniks.stp24.model.Jobs JobResult} of starting the job
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
     * @param job Job as a class that needs to be stopped
     * @return {@link Job Job} class containing the result of stopping the job.
     */
    public Observable<Job> stopJob(Job job) {
        return this.stopJob(job._id());
    }

    /**
     * Returns an {@link ObservableList ObservableList}<{@link Job Job}> of a specific job type
     * that will be dynamically updated upon starting, editing or deleting jobCollections. <p>
     * To receive a list prefilled with jobs right after the loading of the
     * {@link org.fulib.fx.annotation.controller.Controller Controller} is done, provide this function within the
     * {@link #onJobsLoadingFinished(Runnable) onJobsLoadingFinished}.
     * @param jobType type of the jobs
     * @return {@link ObservableList ObservableList}<{@link Job Job}> filtered for a specific job type
     */
    public ObservableList<Job> getJobObservableListOfType(String jobType) {
        return this.jobCollections.get(jobType);
    }

    /**
     * Returns an {@link ObservableList ObservableList}<{@link Job Job}> for a specific island
     * that will be dynamically updated upon starting, editing or deleting jobCollections. <p>
     * To receive a list prefilled with jobs right after the loading of the
     * {@link org.fulib.fx.annotation.controller.Controller Controller} is done, provide this function within the
     * {@link #onJobsLoadingFinished(Runnable) onJobsLoadingFinished}.
     * @param systemID ID of the island
     * @return {@link ObservableList ObservableList}<{@link Job Job}> with jobs of type <i>building</i>,
     * <i>district</i> and <i>upgrade</i> filtered for a specific island
     */
    public ObservableList<Job> getObservableListForSystem(String systemID) {
        if (!this.jobCollections.containsKey(systemID))
            this.jobCollections.put(systemID, FXCollections.observableArrayList());
        return this.jobCollections.get(systemID);
    }

    /**
     * Returns an {@link ObservableList ObservableList}<{@link Job Job}> containing all jobs that run in the player's
     * empire that will be dynamically updated upon starting, editing or deleting jobCollections. <p>
     * To receive a list prefilled with jobs right after the loading of the
     * {@link org.fulib.fx.annotation.controller.Controller Controller} is done, provide this function within the
     * {@link #onJobsLoadingFinished(Runnable) onJobsLoadingFinished}.
     * @return {@link ObservableList ObservableList}<{@link Job Job}> with all jobs that are currently running within
     * the empire
     */
    public ObservableList<Job> getObservableJobCollection() {
        return this.getJobObservableListOfType("collection");
    }

    public void setJobInspector(String inspectorID, Consumer<String[]> func) {
        this.jobInspectionFunctions.put(inspectorID, func);
    }

    public Consumer<String[]> getJobInspector(String inspectorID) {
        if (this.jobInspectionFunctions.containsKey(inspectorID))
            return this.jobInspectionFunctions.get(inspectorID);
        else System.out.printf("Job Service: the inspection function is not found for a given inspector ID: %s!\n", inspectorID);
        return null;
    }

    /**
     * A clearing method that should be called inside the {@link InGameController#destroy() InGameController.destroy()}
     * to properly dispose of the jobs and functions that were loaded in the service during the game.
     */
    public void dispose() {
        this.jobCollections.clear();
        this.jobTypeFunctions.clear();
        this.jobCompletionFunctions.clear();
        this.jobDeletionFunctions.clear();
        this.loadTypeFunctions.clear();
        this.loadCommonFunctions.clear();
        this.finishCommonFunctions.clear();
        this.subscriber.dispose();
    }
}
