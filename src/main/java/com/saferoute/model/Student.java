package com.saferoute.model;

public class Student {
    private final int id;
    private int row;
    private int col;
    private double animX;
    private double animY;
    private boolean evacuated;
    private final int targetDoor; // 0 = Front Door (Top Right), 1 = Back Door (Bottom Right)

    public Student(int id, int row, int col) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.animX = col;
        this.animY = row;
        this.evacuated = false;
        // Top half rows (0-4) head to Front Door, bottom half (5-9) to Back Door
        this.targetDoor = (row < 5) ? 0 : 1;
    }

    public int getId() { return id; }
    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }
    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }
    public double getAnimX() { return animX; }
    public void setAnimX(double animX) { this.animX = animX; }
    public double getAnimY() { return animY; }
    public void setAnimY(double animY) { this.animY = animY; }
    public boolean isEvacuated() { return evacuated; }
    public void setEvacuated(boolean evacuated) { this.evacuated = evacuated; }
    public int getTargetDoor() { return targetDoor; }

    public int getDistanceToDoor(int doorRow, int doorCol) {
        return Math.abs(this.row - doorRow) + Math.abs(this.col - doorCol);
    }
}