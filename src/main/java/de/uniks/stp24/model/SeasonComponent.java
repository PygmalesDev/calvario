package de.uniks.stp24.model;

public class SeasonComponent {
    String transActionTypeText;
    String resourceType;
    int resourceAmount;
    int moneyAmount;
    boolean isPlaying;

    public SeasonComponent(String transActionTypeText, String resourceType, int resourceAmount, int moneyAmount, boolean isPlaying) {
        this.transActionTypeText = transActionTypeText;
        this.resourceType = resourceType;
        this.resourceAmount = resourceAmount;
        this.moneyAmount = moneyAmount;
        this.isPlaying = isPlaying;
    }

    public String getTransActionTypeText() {
        return transActionTypeText;
    }

    public void setTransActionTypeText(String transActionTypeText) {
        this.transActionTypeText = transActionTypeText;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public int getResourceAmount() {
        return resourceAmount;
    }

    public void setResourceAmount(int resourceAmount) {
        this.resourceAmount = resourceAmount;
    }

    public int getMoneyAmount() {
        return moneyAmount;
    }

    public void setMoneyAmount(int moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
