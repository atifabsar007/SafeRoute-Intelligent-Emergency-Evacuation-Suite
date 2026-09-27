package com.saferoute.view;

import com.saferoute.algorithm.Graph;
import com.saferoute.model.Edge;
import com.saferoute.model.Node;
import com.saferoute.model.RoomGrid;
import com.saferoute.model.Student;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class KUETMapView extends Canvas {
    private final GraphicsContext gc = getGraphicsContext2D();

    public KUETMapView(double w, double h) {
        super(w, h);
    }

    public void renderMacroView(Graph graph, List<Node> safeRoute, Node activeStudentPos, int selectedFloorFilter) {
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Background Slate Navy Frame
        gc.setFill(Color.web("#0B132B"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        // 1. Draw Graph Structural Connections (Edges)
        gc.setStroke(Color.web("#3A506B"));
        gc.setLineWidth(2);
        for (Node n : graph.getAllNodes()) {
            if (selectedFloorFilter != -1 && n.getFloor() != selectedFloorFilter && !n.isSafeZone()) continue;
            for (Edge edge : n.getNeighbors()) {
                Node target = edge.getTarget();
                if (selectedFloorFilter == -1 || target.getFloor() == selectedFloorFilter || target.isSafeZone()) {
                    gc.strokeLine(n.getX(), n.getY(), target.getX(), target.getY());
                }
            }
        }

        // 2. Draw Active Dijkstra Safe Route Highlight (Green Polyline)
        if (safeRoute != null && safeRoute.size() > 1) {
            gc.setStroke(Color.web("#10B981"));
            gc.setLineWidth(5);
            for (int i = 0; i < safeRoute.size() - 1; i++) {
                Node u = safeRoute.get(i);
                Node v = safeRoute.get(i + 1);
                if (selectedFloorFilter == -1 || u.getFloor() == selectedFloorFilter || v.getFloor() == selectedFloorFilter || u.isSafeZone() || v.isSafeZone()) {
                    gc.strokeLine(u.getX(), u.getY(), v.getX(), v.getY());
                }
            }
        }

        // 3. Draw Building Nodes & Text Labels
        for (Node n : graph.getAllNodes()) {
            if (selectedFloorFilter != -1 && n.getFloor() != selectedFloorFilter && !n.isSafeZone()) continue;

            double w = n.isSafeZone() ? 220 : 62;
            double h = n.isSafeZone() ? 40 : 28;

            if (n.isSafeZone()) gc.setFill(Color.web("#059669"));
            else if (n.isFire()) gc.setFill(Color.web("#EF4444"));
            else if (n.isStairs()) gc.setFill(Color.web("#D97706"));
            else if (n.isLift()) gc.setFill(Color.web("#2563EB"));
            else gc.setFill(Color.web("#475569"));

            gc.fillRoundRect(n.getX() - (w / 2), n.getY() - (h / 2), w, h, 8, 8);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("System", FontWeight.BOLD, 10));
            String display = n.isSafeZone() ? n.getName() : n.getName().replace("Block ", "").replace(" (", "_").replace(")", "");
            gc.fillText(display, n.getX() - (w / 2) + 4, n.getY() + 3);

            // Student Count Badge
            if (n.getStudentCount() > 0) {
                gc.setFill(Color.web("#F59E0B"));
                gc.fillOval(n.getX() + 15, n.getY() - 18, 18, 18);
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("System", FontWeight.BOLD, 9));
                gc.fillText(String.valueOf(n.getStudentCount()), n.getX() + 19, n.getY() - 5);
            }
        }

        // 4. Draw Active Traversal Position Tracker Indicator
        if (activeStudentPos != null) {
            if (selectedFloorFilter == -1 || activeStudentPos.getFloor() == selectedFloorFilter || activeStudentPos.isSafeZone()) {
                gc.setFill(Color.web("#EC4899"));
                gc.fillOval(activeStudentPos.getX() - 12, activeStudentPos.getY() - 12, 24, 24);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(2);
                gc.strokeOval(activeStudentPos.getX() - 12, activeStudentPos.getY() - 12, 24, 24);
            }
        }
    }

    public void renderMicroRoomView(Node selectedNode) {
        gc.clearRect(0, 0, getWidth(), getHeight());

        gc.setFill(Color.web("#1C2541"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        RoomGrid grid = selectedNode.getRoomGrid();
        int safeCount = grid.getEvacuatedCount();
        int stuckCount = grid.getStuckCount();

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 18));
        gc.fillText("🔍 Microscopic View: " + selectedNode.getName(), 30, 35);

        // Real-time Evacuation HUD
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        gc.setFill(Color.web("#10B981"));
        gc.fillText("✅ Evacuated Outside: " + safeCount, 520, 30);
        gc.setFill(Color.web("#EF4444"));
        gc.fillText("⚠️ Still Stuck inside: " + stuckCount, 520, 50);

        // Grid Render Area
        double startX = 180;
        double startY = 80;
        double cellSize = 42;

        for (int r = 0; r < RoomGrid.ROWS; r++) {
            for (int c = 0; c < RoomGrid.COLS; c++) {
                double x = startX + (c * cellSize);
                double y = startY + (r * cellSize);

                gc.setStroke(Color.web("#3A506B"));
                gc.setFill(Color.web("#0B132B"));
                gc.fillRect(x, y, cellSize - 4, cellSize - 4);
                gc.strokeRect(x, y, cellSize - 4, cellSize - 4);
            }
        }

        // Exit Gates (GATE 1 & GATE 2)
        double frontX = startX + (RoomGrid.FRONT_DOOR_COL * cellSize) + cellSize;
        double frontY = startY + (RoomGrid.FRONT_DOOR_ROW * cellSize);
        gc.setFill(Color.web("#10B981"));
        gc.fillRect(frontX, frontY, 20, cellSize - 4);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 10));
        gc.fillText("GATE 1", frontX + 25, frontY + 22);

        double backX = startX + (RoomGrid.BACK_DOOR_COL * cellSize) + cellSize;
        double backY = startY + (RoomGrid.BACK_DOOR_ROW * cellSize);
        gc.setFill(Color.web("#10B981"));
        gc.fillRect(backX, backY, 20, cellSize - 4);
        gc.setFill(Color.WHITE);
        gc.fillText("GATE 2", backX + 25, backY + 22);

        // Active Students Inside Classroom Grid
        for (Student s : grid.getStudents()) {
            if (s.isEvacuated()) continue;

            double sx = startX + (s.getAnimX() * cellSize) + 7;
            double sy = startY + (s.getAnimY() * cellSize) + 7;

            gc.setFill(s.getTargetDoor() == 0 ? Color.web("#F59E0B") : Color.web("#3B82F6"));
            gc.fillOval(sx, sy, 24, 24);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("System", FontWeight.BOLD, 9));
            gc.fillText("S" + s.getId(), sx + 4, sy + 15);
        }

        // Delay Counter Real Congestion HUD Banner
        gc.setFill(grid.getRealDelayCycles() > 0 ? Color.web("#EF4444") : Color.web("#10B981"));
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.fillText("⏱ REAL CONGESTION DELAY CYCLES: " + grid.getRealDelayCycles() + " ticks", 30, 540);
    }
}