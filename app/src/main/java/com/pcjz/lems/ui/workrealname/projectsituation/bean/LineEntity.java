package com.pcjz.lems.ui.workrealname.projectsituation.bean;

/**
 * created by yezhengyu on 2017/9/25 17:24
 */

public class LineEntity {
    private String id;
    private String time;
    private int towerCranes;
    private int elevators;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTowerCranes() {
        return towerCranes;
    }

    public void setTowerCranes(int towerCranes) {
        this.towerCranes = towerCranes;
    }

    public int getElevators() {
        return elevators;
    }

    public void setElevators(int elevators) {
        this.elevators = elevators;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
