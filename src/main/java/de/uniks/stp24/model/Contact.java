package de.uniks.stp24.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Contact {
    String empireFlag;
    String empireName;
    String empireID;
    private BooleanProperty atWarWith = new SimpleBooleanProperty(false);

    public Contact() {
    }
    public Contact(String empireID) {
        this.empireID = empireID;
    }

    public String getEmpireID() {
        return empireID;
    }

    public void setEmpireID(String empireID) {
        this.empireID = empireID;
    }

    public String getEmpireName() {
        return empireName;
    }

    public String getEmpireFlag() {
        return empireFlag;
    }

    public void setEmpireFlag(String empireFlag) {
        this.empireFlag = empireFlag;
    }

    public void setEmpireName(String empireName) {
        this.empireName = empireName;
    }

    public boolean isAtWarWith() {
        return atWarWith.get();
    }

    public void setAtWarWith(boolean atWar) {
        this.atWarWith.set(atWar);
    }

    public BooleanProperty atWarWithProperty() {
        return atWarWith;
    }
}
