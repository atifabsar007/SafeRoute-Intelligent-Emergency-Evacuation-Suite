package com.saferoute.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RoomGrid {
    public static final int ROWS = 10;
    public static final int COLS = 6;

    public static final int FRONT_DOOR_ROW = 0;
    public static final int FRONT_DOOR_COL = 5;

    public static final int BACK_DOOR_ROW = 9;
    public static final int BACK_DOOR_COL = 5;

    private final List<Student> students = new ArrayList<>();
    private final boolean[][] occupied = new boolean[ROWS][COLS];
    private int realDelayCycles = 0;

    public void populateStudents(int count) {
        students.clear();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                occupied[r][c] = false;
            }
        }
        realDelayCycles = 0;

        int spawned = 0;
        for (int r = 0; r < ROWS && spawned < count; r++) {
            for (int c = 0; c < COLS && spawned < count; c++) {
                Student s = new Student(spawned + 1, r, c);
                students.add(s);
                occupied[r][c] = true;
                spawned++;
            }
        }
    }

    public boolean stepSimulation() {
        boolean remaining = false;
        for (Student s : students) {
            if (!s.isEvacuated()) {
                remaining = true;
                break;
            }
        }
        if (!remaining) return false;

        students.sort(Comparator.comparingInt(s -> {
            if (s.isEvacuated()) return Integer.MAX_VALUE;
            int doorR = (s.getTargetDoor() == 0) ? FRONT_DOOR_ROW : BACK_DOOR_ROW;
            int doorC = (s.getTargetDoor() == 0) ? FRONT_DOOR_COL : BACK_DOOR_COL;
            return s.getDistanceToDoor(doorR, doorC);
        }));

        boolean[][] nextOccupied = new boolean[ROWS][COLS];
        int frontDoorExitCount = 0;
        int backDoorExitCount = 0;

        for (Student s : students) {
            if (s.isEvacuated()) continue;

            int doorRow = (s.getTargetDoor() == 0) ? FRONT_DOOR_ROW : BACK_DOOR_ROW;
            int doorCol = (s.getTargetDoor() == 0) ? FRONT_DOOR_COL : BACK_DOOR_COL;

            if (s.getRow() == doorRow && s.getCol() == doorCol) {
                if (s.getTargetDoor() == 0 && frontDoorExitCount < 2) {
                    s.setEvacuated(true);
                    occupied[s.getRow()][s.getCol()] = false;
                    frontDoorExitCount++;
                    continue;
                } else if (s.getTargetDoor() == 1 && backDoorExitCount < 2) {
                    s.setEvacuated(true);
                    occupied[s.getRow()][s.getCol()] = false;
                    backDoorExitCount++;
                    continue;
                } else {
                    realDelayCycles++;
                    nextOccupied[s.getRow()][s.getCol()] = true;
                    continue;
                }
            }

            int targetR = s.getRow();
            int targetC = s.getCol();

            if (targetC < doorCol) targetC++;
            else if (targetR < doorRow) targetR++;
            else if (targetR > doorRow) targetR--;

            if (nextOccupied[targetR][targetC] || (occupied[targetR][targetC] && (targetR != s.getRow() || targetC != s.getCol()))) {
                realDelayCycles++;
                nextOccupied[s.getRow()][s.getCol()] = true;
            } else {
                occupied[s.getRow()][s.getCol()] = false;
                s.setRow(targetR);
                s.setCol(targetC);
                s.setAnimX(targetC);
                s.setAnimY(targetR);
                nextOccupied[targetR][targetC] = true;
            }
        }

        return true;
    }

    public List<Student> getStudents() { return students; }
    public int getRealDelayCycles() { return realDelayCycles; }

    public int getEvacuatedCount() {
        int count = 0;
        for (Student s : students) {
            if (s.isEvacuated()) count++;
        }
        return count;
    }

    public int getStuckCount() {
        return students.size() - getEvacuatedCount();
    }
}