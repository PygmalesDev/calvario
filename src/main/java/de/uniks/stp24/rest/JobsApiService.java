package de.uniks.stp24.rest;

import de.uniks.stp24.model.Jobs.*;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public interface JobsApiService {
    @GET("games/{game}/empires/{empire}/jobs")
    Observable<ArrayList<Job>> getEmpireJobs(@Path("game") String gameID, @Path("empire") String empireID);
    @GET("games/{game}/empires/{empire}/jobs")
    Observable<ArrayList<Job>> getEmpireJobsOfType(@Path("game") String gameID, @Path("empire") String empireID,
                                  @Query("type") String jobType);
    @GET("games/{game}/empires/{empire}/jobs")
    Observable<ArrayList<Job>> getIslandJobs(@Path("game") String gameID, @Path("empire") String empireID,
                                  @Query("system") String islandID);
    @GET("games/{game}/empires/{empire}/jobs")
    Observable<ArrayList<Job>> getIslandJobsOfType(@Path("game") String gameID, @Path("empire") String empireID,
                                  @Query("type") String jobType, @Query("system") String islandID);

    @POST("games/{game}/empires/{empire}/jobs")
    Observable<Job> createNewJob(@Path("game") String gameID, @Path("empire") String empireID,
                                 @Body JobDTO jobDTO);

    @GET("games/{game}/empires/{empire}/jobs/{id}")
    Observable<Job> getJobByID(@Path("game") String gameID, @Path("empire") String empireID,
                               @Path("id") String jobID);

    @PATCH("games/{game}/empires/{empire}/jobs/{id}")
    Observable<Job> patchJobPriority(@Path("game") String gameID, @Path("empire") String empireID,
                                     @Path("id") String jobID, @Body int priority);

    @DELETE("games/{game}/empires/{empire}/jobs/{id}")
    Observable<Job> deleteJob(@Path("game") String gameID, @Path("empire") String empireID,
                              @Path("id") String jobID);
}
