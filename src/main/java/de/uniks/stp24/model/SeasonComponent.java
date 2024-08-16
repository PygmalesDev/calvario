package de.uniks.stp24.model;

public class SeasonComponent {
    String transActionTypeText;
    String resourceType;
    double resourceAmount;
    double moneyAmount;
    boolean isPlaying;

    public SeasonComponent(String transActionTypeText, String resourceType, double resourceAmount, double moneyAmount, boolean isPlaying) {
        this.transActionTypeText = transActionTypeText;
        this.resourceType = resourceType;
        this.resourceAmount = resourceAmount;
        this.moneyAmount = moneyAmount;
        this.isPlaying = isPlaying;
    }

    public String getTransActionTypeText() {
        return transActionTypeText;
    }

    public String getResourceType() {
        return resourceType;
    }

    public double getResourceAmount() {
        return resourceAmount;
    }

    public double getMoneyAmount() {
        return moneyAmount;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
