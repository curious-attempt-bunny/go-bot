package com.company;

public class Player {

    private String name;
    private double points;

    public Player(String name) {
        this.name = name;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public double getPoints() {
        return this.points;
    }

    public String getName() {
        return this.name;
    }
}
