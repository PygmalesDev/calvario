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
 * generate a job of a specific type. A new job has to be started using the...
 */
public class Jobs {

    /**
     * A model for a started job.
     */
    public static class Job {
        private String createdAt;
        private String updatedAt;
        private String _id;
        private int progress;
        private int total;
        private String game;
        private String empire;
        private String system;
        private int priority;
        private String type;
        private String building;
        private String district;
        private String technology;
        private Map<String, Integer> cost;
        private JobResult result;

        @Inject
        public Job() {
            this.setPriority(0);
        }

        public String getCreatedAt() {
            return this.createdAt;
        }

        public String getUpdatedAt() {
            return this.updatedAt;
        }

        public String getJobID() {
            return this._id;
        }

        public Job setBuilding(String building) {
            this.building = building;
            return this;
        }

        public String getBuilding() {
            return building;
        }

        public int getProgress() {
            return this.progress;
        }

        public int getTotal() {
            return this.total;
        }

        public String getGame() {
            return this.game;
        }

        public Job setGame(String game) {
            this.game = game;
            return this;
        }

        public String getEmpire() {
            return this.empire;
        }

        public Job setEmpire(String empire) {
            this.empire = empire;
            return this;
        }

        public String getSystem() {
            return system;
        }

        public Job setSystem(String system) {
            this.system = system;
            return this;
        }

        public int getPriority() {
            return priority;
        }

        public Job setPriority(int priority) {
            this.priority = priority;
            return this;
        }

        public String getType() {
            return type;
        }

        public Job setType(String type) {
            this.type = type;
            return this;
        }

        public String getDistrict() {
            return district;
        }

        public Job setDistrict(String district) {
            this.district = district;
            return this;
        }

        public String getTechnology() {
            return technology;
        }

        public Job setTechnology(String technology) {
            this.technology = technology;
            return this;
        }

        public Map<String, Integer> getCost() {
            return cost;
        }


        public JobResult getResult() {
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (Objects.isNull(this._id))
                return false;
            if (obj instanceof Job)
                return this._id.equals(((Job) obj).getJobID());
            return false;
        }
    }

    /**
     * Generates a basic job for creating a building on a certain island. The job has to be initialized using the...
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
     * The job has to be initialized using the...
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
     * The job has to be initialized using the...
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
     * The job has to be initialized using the...
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
     * Creates a new job from the given one. This job has to be initialized using the...
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
    private static class JobResult {
        int statusCode;
        String error;
        String message;
    }
}
