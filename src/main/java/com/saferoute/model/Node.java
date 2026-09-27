package com.saferoute.model;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private final String id;
    private final String name;
    private final String block;
    private final int floor;
    private final boolean isStairs;
    private final boolean isLift;
    private final boolean isSafeZone;
    private final double x;
    private final double y;

    private boolean isFire = false;
    private int studentCount = 0;
    private final List<Edge> neighbors = new ArrayList<>();
    private final RoomGrid roomGrid = new RoomGrid();

    public Node(String id, String name, String block, int floor, boolean isStairs, boolean isLift, boolean isSafeZone, double x, double y) {
        this.id = id;
        this.name = name;
        this.block = block;
        this.floor = floor;
        this.isStairs = isStairs;
        this.isLift = isLift;
        this.isSafeZone = isSafeZone;
        this.x = x;
        this.y = y;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getBlock() { return block; }
    public int getFloor() { return floor; }
    public boolean isStairs() { return isStairs; }
    public boolean isLift() { return isLift; }
    public boolean isSafeZone() { return isSafeZone; }
    public double getX() { return x; }
    public double getY() { return y; }

    public boolean isFire() { return isFire; }
    public void setFire(boolean fire) { isFire = fire; }

    public int getStudentCount() { return studentCount; }
    public void setStudentCount(int studentCount) {
        this.studentCount = studentCount;
        if (studentCount > 0) roomGrid.populateStudents(studentCount);
    }

    public RoomGrid getRoomGrid() { return roomGrid; }
    public List<Edge> getNeighbors() { return neighbors; }
    public void addNeighbor(Edge edge) { neighbors.add(edge); }
}