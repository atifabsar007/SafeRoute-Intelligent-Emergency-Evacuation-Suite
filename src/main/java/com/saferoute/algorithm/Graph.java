package com.saferoute.algorithm;

import com.saferoute.model.Edge;
import com.saferoute.model.Node;

import java.util.*;

public class Graph {
    public static final int LIFT_MAX_CAPACITY = 12; // Realistic lift limit

    private final Map<String, Node> nodes = new HashMap<>();

    public void addNode(Node node) { nodes.put(node.getId(), node); }

    public void addEdge(String srcId, String destId, double cost) {
        Node src = nodes.get(srcId);
        Node dest = nodes.get(destId);
        if (src != null && dest != null) {
            src.addNeighbor(new Edge(dest, cost));
            dest.addNeighbor(new Edge(src, cost));
        }
    }

    public Node getNode(String id) { return nodes.get(id); }
    public Collection<Node> getAllNodes() { return nodes.values(); }

    public List<Node> findPath(Node start, Node target, boolean isEarthquake) {
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> parentMap = new HashMap<>();
        PriorityQueue<NodeCost> pq = new PriorityQueue<>(Comparator.comparingDouble(nc -> nc.cost));

        for (Node n : nodes.values()) distances.put(n, Double.POSITIVE_INFINITY);

        distances.put(start, 0.0);
        pq.add(new NodeCost(start, 0.0));

        while (!pq.isEmpty()) {
            NodeCost current = pq.poll();
            if (current.node.equals(target)) break;

            for (Edge edge : current.node.getNeighbors()) {
                Node neighbor = edge.getTarget();
                double edgeCost = edge.getCost(isEarthquake);

                if (Double.isInfinite(edgeCost)) continue;

                double newDist = distances.get(current.node) + edgeCost;
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    parentMap.put(neighbor, current.node);
                    pq.add(new NodeCost(neighbor, newDist));
                }
            }
        }

        List<Node> path = new LinkedList<>();
        Node curr = target;
        while (curr != null) {
            path.add(0, curr);
            curr = parentMap.get(curr);
        }
        return (path.size() > 1 && path.get(0).equals(start)) ? path : Collections.emptyList();
    }

    private static class NodeCost {
        Node node;
        double cost;
        NodeCost(Node node, double cost) { this.node = node; this.cost = cost; }
    }

    public static Graph buildKUETAcademicBuilding() {
        Graph g = new Graph();
        String[] blocks = {"A", "B", "C", "D"};
        int[] roomNumbers = {101, 102, 103, 104};

        for (int f = 0; f <= 3; f++) {
            for (int b = 0; b < blocks.length; b++) {
                String blk = blocks[b];
                double baseX = 120 + (b * 220);
                double baseY = 380 - (f * 90);

                int rNo = (f == 0) ? roomNumbers[b] : (f * 100 + (b + 1));
                String roomCode = "R_" + blk + "_" + f;
                String roomName = "Room " + rNo + " (" + blk + ")";

                g.addNode(new Node(roomCode, roomName, blk, f, false, false, false, baseX, baseY));

                String stairCode = "STAIR_" + blk + "_" + f;
                g.addNode(new Node(stairCode, blk + " Stairs (F" + f + ")", blk, f, true, false, false, baseX + 70, baseY));

                String liftCode = "LIFT_" + blk + "_" + f;
                g.addNode(new Node(liftCode, blk + " Lift (F" + f + ")", blk, f, false, true, false, baseX + 130, baseY));

                // On upper floors, rooms connect to stairs & lifts
                if (f > 0) {
                    g.addEdge(roomCode, stairCode, 5.0);
                    g.addEdge(stairCode, liftCode, 3.0);
                    g.addEdge("STAIR_" + blk + "_" + f, "STAIR_" + blk + "_" + (f - 1), 10.0);
                    g.addEdge("LIFT_" + blk + "_" + f, "LIFT_" + blk + "_" + (f - 1), 5.0);
                } else {
                    // GROUND FLOOR DIRECT ROUTING (No stairs required for Ground Rooms & Lifts)
                    g.addEdge(roomCode, liftCode, 4.0);
                }
            }

            // Horizontal corridors
            g.addEdge("STAIR_A_" + f, "STAIR_B_" + f, 12.0);
            g.addEdge("STAIR_B_" + f, "STAIR_C_" + f, 12.0);
            g.addEdge("STAIR_C_" + f, "STAIR_D_" + f, 12.0);
        }

        g.addNode(new Node("OUTSIDE_SAFE", "Central Assembly Field", "OUTDOOR", 0, false, false, true, 450, 480));

        // Ground Floor Direct Outlets
        for (String blk : blocks) {
            g.addEdge("R_" + blk + "_0", "OUTSIDE_SAFE", 6.0);       // Ground Floor rooms exit directly
            g.addEdge("STAIR_" + blk + "_0", "OUTSIDE_SAFE", 8.0);   // Ground Stairs exit directly
            g.addEdge("LIFT_" + blk + "_0", "OUTSIDE_SAFE", 5.0);    // Ground Lifts exit DIRECTLY to safety
        }

        return g;
    }
}