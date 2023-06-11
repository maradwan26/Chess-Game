package com.example.chessgameproject;

public class Location {
    protected int x;
    protected int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }
    // getters and setters:
    public int getX() {return x;}
    public void setX(int x) {this.x = x;}
    public int getY() {return y;}
    public void setY(int y) {this.y = y;}
    //

    public double distance(Location location1, Location location2) {
        double distance = Math.pow(location1.x - location2.x, 2) + Math.pow(location1.y - location2.y, 2);
        return Math.sqrt(distance);
    }

    public boolean inBounds(Location location) {
        return location.x >= 0 && location.x <= 7 && location.y >= 0 && location.y <= 7;
    }
}
