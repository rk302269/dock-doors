package com.homedepot.bootcamp.dockdoors.model;

import com.google.cloud.Timestamp;

public class Door {



    private String doorId;

    private String doorName;


    public Door(String doorId, String doorName) {
        this.doorId = doorId;
        this.doorName = doorName;
    }

    public Door() {
    }

    public String getDoorId() {
        return doorId;
    }

    public void setDoorId(String doorId) {
        this.doorId = doorId;
    }

    public String getDoorName() {
        return doorName;
    }

    public void setDoorName(String doorName) {
        this.doorName = doorName;
    }

    @Override
    public String toString() {
        return "Door{" +
                "doorId=" + doorId +
                ", doorName='" + doorName + '\'' +

                '}';
    }
}
