package de.uniks.stp24.model;

public class Contact {
    String empireFlag;
    String empireName;
    String empireID;

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
}
