package de.uniks.stp24.model;

import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;

/**
 * A supermodel for creating jobs and controlling their status. <br>
 * Use {@link #createBuildingJob(String, String) createBuildingJob},
 * {@link #createDistrictJob(String, String) createDistrictJob},
 * {@link #createIslandUpgradeJob(String) createIslandUpgradeJob} or
 * {@link #createTechnologyJob(String) createTechnologyJob} to quickly
 * generate a job of a specific type. A new job has to be started using the
 * {@link de.uniks.stp24.service.game.JobsService#beginJob(JobDTO) JobService.beginJob}({@link JobDTO JobDTO}).
 */
public class Jobs {

    /**
     * A model for a started job.
     */
    public record Job(
             String createdAt,
             String updatedAt,
             String _id,
             int progress,
             int total,
             String game,
             String empire,
             String system,
             int priority,
             String type,
             String building,
             String district,
             String technology,
             Map<String, Integer> cost,
             JobResult result) {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Job) return this._id.equals(((Job) obj)._id());
            return false;
        }
    }

    /**
     * Generates a basic job for creating a building on a certain island. The job has to be initialized using the
     * {@link de.uniks.stp24.service.game.JobsService#beginJob(JobDTO) JobService.beginJob}({@link JobDTO JobDTO}).
     *
     * @param systemID ID of an island, where the building has to be placed
     * @param buildingID type of the building
     * @return New {@link JobDTO JobDTO} for the building job
     */
    public static JobDTO createBuildingJob(String systemID, String buildingID) {
        return new JobDTO(
                systemID,
                0,
                "building",
                buildingID,
                null, null
        );
    }

    /**
     * Generates a basic job for building a district cell of a district on a certain island.
     * The job has to be initialized using the
     * {@link de.uniks.stp24.service.game.JobsService#beginJob(JobDTO) JobService.beginJob}({@link JobDTO JobDTO}).
     *
     * @param systemID ID of an island, where the building has to be placed
     * @param districtID type of the district
     * @return New {@link JobDTO JobDTO} for the building of a district cell job
     */
    public static JobDTO createDistrictJob(String systemID, String districtID) {
        return new JobDTO(
                systemID,
                0,
                "district",
                null,
                districtID,
                null
        );
    }

    /**
     * Generates a basic job for upgrading an island to the next level.
     * The job has to be initialized using the
     * {@link de.uniks.stp24.service.game.JobsService#beginJob(JobDTO) JobService.beginJob}({@link JobDTO JobDTO}).
     *
     * @param systemID ID of an island that needs to be upgraded
     * @return New {@link JobDTO JobDTO} for an island upgrade
     */
    public static JobDTO createIslandUpgradeJob(String systemID) {
        return new JobDTO(
                systemID,
                0,
                "upgrade",
                null,
                null,
                null
        );
    }

    /**
     * Generates a basic job for researching a technology for the empire.
     * The job has to be initialized using the
     * {@link de.uniks.stp24.service.game.JobsService#beginJob(JobDTO) JobService.beginJob}({@link JobDTO JobDTO}).
     *
     * @param technologyID type of the technology that needs to be researched
     * @return New {@link JobDTO JobDTO} for the technology research
     */
    public static JobDTO createTechnologyJob(String technologyID) {
        return new JobDTO(
                null,
                0,
                "technology",
                null,
                null,
                technologyID
        );
    }

    /**
     * Creates a new job from the given job instance. This job has to be initialized using the
     * {@link de.uniks.stp24.service.game.JobsService#beginJob(JobDTO) JobService.beginJob}({@link JobDTO JobDTO}).
     * @return New {@link JobDTO JobDTO} that is a copy of the given job
     */
    public static JobDTO createJobFromGiven(Job job) {
        return new JobDTO(
                job.system,
                job.priority,
                job.type,
                job.building,
                job.district,
                job.technology
        );
    }

    /**
     * A DTO for starting a new job of a provided type.
     */
    public record JobDTO(
        String system,
        int priority,
        String type,
        String building,
        String district,
        String technology
    ) {}

    /**
     * A helper class that contains a result from the server request of starting a new job.
     */
    public static class JobResult {
        int statusCode;
        String error;
        String message;
    }
}
