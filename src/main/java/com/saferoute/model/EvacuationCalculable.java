package com.saferoute.model;

public interface EvacuationCalculable {
    double calculatePathRisk();
    boolean isEvacuationRouteClear();
}