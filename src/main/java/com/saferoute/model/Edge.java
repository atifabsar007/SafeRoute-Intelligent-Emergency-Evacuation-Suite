package com.saferoute.model;

public class Edge {
    private final Node target;
    private final double baseCost;

    public Edge(Node target, double baseCost) {
        this.target = target;
        this.baseCost = baseCost;
    }

    public Node getTarget() { return target; }

    public double getCost(boolean isEarthquake) {
        if (target.isFire()) {
            return Double.POSITIVE_INFINITY;
        }
        if (isEarthquake && target.isLift()) {
            return Double.POSITIVE_INFINITY;
        }

        double cost = baseCost;
        if (target.getStudentCount() > 0) {
            cost += (target.getStudentCount() * 0.8);
        }
        return cost;
    }
}