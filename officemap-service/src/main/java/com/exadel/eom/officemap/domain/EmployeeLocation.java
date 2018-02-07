package com.exadel.eom.officemap.domain;

import org.hibernate.validator.constraints.Length;

public class EmployeeLocation {
    private String uid;

    private int floor;

    @Length(min = 0, max = 255)
    private String room;

    @Length(min = 0, max = 255)
    private String seat;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }
}
