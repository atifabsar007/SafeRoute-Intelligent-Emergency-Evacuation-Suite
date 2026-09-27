package com.saferoute.database;

import java.sql.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:saferoute_kuet.db";

    public static void init() {
        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS student_logs (id INTEGER PRIMARY KEY AUTOINCREMENT, roll TEXT, route TEXT, time_spent INT);");
            stmt.execute("CREATE TABLE IF NOT EXISTS room_hazards (room_id TEXT PRIMARY KEY, hazard_level REAL);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logEvacuation(String roll, String route, int timeSpent) {
        String sql = "INSERT INTO student_logs(roll, route, time_spent) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roll);
            pstmt.setString(2, route);
            pstmt.setInt(3, timeSpent);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static int getEvacuationCount() {
        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM student_logs")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static void updateRoomHazard(String roomId, double level) {
        String sql = "INSERT INTO room_hazards(room_id, hazard_level) VALUES(?,?) ON CONFLICT(room_id) DO UPDATE SET hazard_level=excluded.hazard_level";
        try (Connection conn = DriverManager.getConnection(DB_URL); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomId);
            pstmt.setDouble(2, level);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static void clearLogs() {
        try (Connection conn = DriverManager.getConnection(DB_URL); Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM student_logs");
        } catch (SQLException e) { e.printStackTrace(); }
    }
}