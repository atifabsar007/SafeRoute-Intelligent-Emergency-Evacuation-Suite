package com.saferoute.main;

import com.saferoute.algorithm.Graph;
import com.saferoute.model.Node;
import com.saferoute.net.WeatherService;
import com.saferoute.view.KUETMapView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApp extends Application {
    private Graph graph;
    private KUETMapView mapView;
    private Node activeStudentPos;
    private List<Node> activeRoute;

    private ComboBox<String> modeSelect;
    private ComboBox<String> roomSelect;
    private ComboBox<String> floorViewSelect;
    private Slider studentSlider;
    private Label studentCountLabel;
    private Label statusArea;
    private ProgressBar evacProgressBar;
    private Label statsLabel;

    private Label weatherLabel;
    private WeatherService weatherService;
    private ExecutorService threadPool;

    private boolean isEarthquake = false;
    private boolean isMicroViewActive = false;
    private Timeline microSimTimeline;
    private Timeline macroSimTimeline;

    @Override
    public void start(Stage stage) {
        weatherService = new WeatherService();
        threadPool = Executors.newSingleThreadExecutor();

        graph = Graph.buildKUETAcademicBuilding();
        activeStudentPos = graph.getNode("R_B_2");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0B132B;");

        // Top Weather & Status Bar
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(12, 20, 12, 20));
        topBar.setStyle("-fx-background-color: #1C2541; -fx-border-color: #3A506B; -fx-border-width: 0 0 1 0;");
        topBar.setSpacing(20);

        Label topTitleLabel = new Label("SafeRoute — KUET Emergency Evacuation Portal");
        topTitleLabel.setStyle("-fx-text-fill: #F8FAFC; -fx-font-size: 16px; -fx-font-weight: bold;");

        weatherLabel = new Label("Fetching KUET Live Weather...");
        weatherLabel.setStyle("-fx-text-fill: #38BDF8; -fx-font-size: 13px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBar.getChildren().addAll(topTitleLabel, spacer, weatherLabel);
        root.setTop(topBar);

        mapView = new KUETMapView(920, 600);

        // Sidebar Control Panel
        VBox sideBar = new VBox(10);
        sideBar.setPrefWidth(310);
        sideBar.setPadding(new Insets(18));
        sideBar.setStyle("-fx-background-color: #1C2541; -fx-border-color: #3A506B; -fx-border-width: 0 0 0 1;");

        Label title = new Label("KUET Academic Evacuation");
        title.setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 16px; -fx-font-weight: bold;");

        modeSelect = new ComboBox<>();
        modeSelect.getItems().addAll("🔥 Fire Hazard", "🌋 Earthquake Hazard");
        modeSelect.setValue("🔥 Fire Hazard");
        modeSelect.setMaxWidth(Double.MAX_VALUE);

        roomSelect = new ComboBox<>();
        roomSelect.getItems().addAll(
                "Room 101 (Block A)", "Room 201 (Block A)", "Room 301 (Block A)",
                "Room 102 (Block B)", "Room 202 (Block B)", "Room 302 (Block B)",
                "Room 103 (Block C)", "Room 203 (Block C)", "Room 303 (Block C)",
                "Room 104 (Block D)", "Room 204 (Block D)", "Room 304 (Block D)"
        );
        roomSelect.setValue("Room 202 (Block B)");
        roomSelect.setMaxWidth(Double.MAX_VALUE);

        studentSlider = new Slider(10, 60, 40);
        studentSlider.setMajorTickUnit(10);
        studentSlider.setMinorTickCount(0);
        studentSlider.setSnapToTicks(true);
        studentSlider.setShowTickMarks(true);
        studentSlider.setShowTickLabels(true);

        studentCountLabel = new Label("Student Count: 40 Students");
        studentCountLabel.setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold;");

        studentSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int count = newVal.intValue();
            studentCountLabel.setText("Student Count: " + count + " Students");
            if (activeStudentPos != null) activeStudentPos.setStudentCount(count);
            recalculateRoute();
            updateLiveStats(0);
        });

        floorViewSelect = new ComboBox<>();
        floorViewSelect.getItems().addAll("All Floors (Combined)", "Ground Floor", "1st Floor", "2nd Floor", "3rd Floor");
        floorViewSelect.setValue("All Floors (Combined)");
        floorViewSelect.setMaxWidth(Double.MAX_VALUE);

        Button applyHazardBtn = new Button("⚠️ Trigger Disaster");
        applyHazardBtn.setMaxWidth(Double.MAX_VALUE);
        applyHazardBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold;");

        Button runAnimBtn = new Button("▶ Macro Evacuate");
        runAnimBtn.setMaxWidth(Double.MAX_VALUE);
        runAnimBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold;");

        Button toggleMicroBtn = new Button("🔍 Zoom Classroom Grid");
        toggleMicroBtn.setMaxWidth(Double.MAX_VALUE);
        toggleMicroBtn.setStyle("-fx-background-color: #3B82F6; -fx-text-fill: white; -fx-font-weight: bold;");

        Button resetBtn = new Button("🔄 Reset Building");
        resetBtn.setMaxWidth(Double.MAX_VALUE);
        resetBtn.setStyle("-fx-background-color: #64748B; -fx-text-fill: white;");

        evacProgressBar = new ProgressBar(0.0);
        evacProgressBar.setMaxWidth(Double.MAX_VALUE);

        statsLabel = new Label("Evacuated: 0 / 40 (0%)");
        statsLabel.setStyle("-fx-text-fill: #38BDF8; -fx-font-weight: bold;");

        statusArea = new Label("System Ready.");
        statusArea.setStyle("-fx-text-fill: #A7F3D0;");
        statusArea.setWrapText(true);

        // Standardized Label Typography with CSS colors for dark mode legibility
        Label lblDisaster = new Label("Select Disaster:");
        lblDisaster.setStyle("-fx-text-fill: #E2E8F0; -fx-font-weight: bold;");

        Label lblRoom = new Label("Select Hazard/Student Room:");
        lblRoom.setStyle("-fx-text-fill: #E2E8F0; -fx-font-weight: bold;");

        Label lblFloor = new Label("Filter Floor View:");
        lblFloor.setStyle("-fx-text-fill: #E2E8F0; -fx-font-weight: bold;");

        Label lblProgress = new Label("Evacuation Progress:");
        lblProgress.setStyle("-fx-text-fill: #E2E8F0; -fx-font-weight: bold;");

        HBox buttonRow = new HBox(8, applyHazardBtn, runAnimBtn);
        HBox.setHgrow(applyHazardBtn, Priority.ALWAYS);
        HBox.setHgrow(runAnimBtn, Priority.ALWAYS);

        sideBar.getChildren().addAll(
                title,
                lblDisaster, modeSelect,
                lblRoom, roomSelect,
                studentCountLabel, studentSlider,
                lblFloor, floorViewSelect,
                buttonRow,
                toggleMicroBtn,
                resetBtn,
                new Separator(),
                lblProgress,
                evacProgressBar,
                statsLabel,
                statusArea
        );

        applyHazardBtn.setOnAction(e -> applyDisasterConfig());
        runAnimBtn.setOnAction(e -> runEvacuationAnimation());
        toggleMicroBtn.setOnAction(e -> toggleMicroView());
        resetBtn.setOnAction(e -> resetBuilding());
        floorViewSelect.setOnAction(e -> recalculateRoute());

        StackPane centerPane = new StackPane(mapView);
        centerPane.setStyle("-fx-background-color: #0B132B;");
        root.setCenter(centerPane);
        root.setRight(sideBar);

        Scene scene = new Scene(root, 1260, 680);
        stage.setTitle("SafeRoute — KUET Emergency Simulation");
        stage.setScene(scene);
        stage.show();

        resetBuilding();
        fetchWeatherDataAsync();
    }

    private void fetchWeatherDataAsync() {
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                return weatherService.fetchLiveAlerts();
            }
        };

        task.setOnSucceeded(e -> weatherLabel.setText(task.getValue()));
        threadPool.execute(task);
    }

    private Node getSelectedRoomNode() {
        String sel = roomSelect.getValue();
        if (sel.contains("101")) return graph.getNode("R_A_0");
        if (sel.contains("201")) return graph.getNode("R_A_2");
        if (sel.contains("301")) return graph.getNode("R_A_3");

        if (sel.contains("102")) return graph.getNode("R_B_0");
        if (sel.contains("202")) return graph.getNode("R_B_2");
        if (sel.contains("302")) return graph.getNode("R_B_3");

        if (sel.contains("103")) return graph.getNode("R_C_0");
        if (sel.contains("203")) return graph.getNode("R_C_2");
        if (sel.contains("303")) return graph.getNode("R_C_3");

        if (sel.contains("104")) return graph.getNode("R_D_0");
        if (sel.contains("204")) return graph.getNode("R_D_2");
        if (sel.contains("304")) return graph.getNode("R_D_3");

        return graph.getNode("R_B_2");
    }

    private void toggleMicroView() {
        isMicroViewActive = !isMicroViewActive;
        if (isMicroViewActive) {
            mapView.renderMicroRoomView(activeStudentPos);
            runMicroClassroomSimulation();
        } else {
            if (microSimTimeline != null) microSimTimeline.stop();
            recalculateRoute();
        }
    }

    private void runMicroClassroomSimulation() {
        if (microSimTimeline != null) microSimTimeline.stop();

        microSimTimeline = new Timeline(new KeyFrame(Duration.millis(350), e -> {
            boolean active = activeStudentPos.getRoomGrid().stepSimulation();
            mapView.renderMicroRoomView(activeStudentPos);
            updateLiveStats(activeStudentPos.getRoomGrid().getEvacuatedCount());
            if (!active) microSimTimeline.stop();
        }));
        microSimTimeline.setCycleCount(Timeline.INDEFINITE);
        microSimTimeline.play();
    }

    private void updateLiveStats(int safeCount) {
        if (activeStudentPos == null) return;

        int total = (int) studentSlider.getValue();
        int safe = Math.min(safeCount, total);

        double pct = (double) safe / total;
        evacProgressBar.setProgress(pct);
        statsLabel.setText("Evacuated: " + safe + " / " + total + " (" + (int) (pct * 100) + "%)");
    }

    private void applyDisasterConfig() {
        for (Node n : graph.getAllNodes()) n.setFire(false);

        Node targetRoom = getSelectedRoomNode();
        isEarthquake = modeSelect.getValue().contains("Earthquake");

        if (!isEarthquake) {
            targetRoom.setFire(true);
        }

        activeStudentPos = targetRoom;
        int totalStudents = (int) studentSlider.getValue();
        activeStudentPos.setStudentCount(totalStudents);

        StringBuilder info = new StringBuilder();
        if (isEarthquake) {
            info.append("🌋 EARTHQUAKE! Lifts DISABLED.\nAll students evacuation via stairs.");
        } else if (targetRoom.getFloor() == 0) {
            info.append("🔥 FIRE in ").append(targetRoom.getName()).append("!\nGround Floor Direct Outdoor Exit activated (No stairs).");
        } else {
            int liftShare = Math.min(totalStudents, Graph.LIFT_MAX_CAPACITY);
            int stairShare = totalStudents - liftShare;
            info.append("🔥 FIRE in ").append(targetRoom.getName()).append("!\n");
            info.append("HYBRID LIFT STRATEGY: ").append(liftShare).append(" students routed to Express Lift (Cap: 12). ");
            if (stairShare > 0) {
                info.append(stairShare).append(" overflow students taking Stairs.");
            }
        }

        statusArea.setText(info.toString());
        recalculateRoute();
        updateLiveStats(0);

        if (isMicroViewActive) runMicroClassroomSimulation();
    }

    private void recalculateRoute() {
        Node exitGround = graph.getNode("OUTSIDE_SAFE");
        activeRoute = graph.findPath(activeStudentPos, exitGround, isEarthquake);

        if (!isMicroViewActive) {
            mapView.renderMacroView(graph, activeRoute, activeStudentPos, getFloorFilterIndex());
        }
    }

    private int getFloorFilterIndex() {
        String sel = floorViewSelect.getValue();
        if (sel.contains("Ground")) return 0;
        if (sel.contains("1st")) return 1;
        if (sel.contains("2nd")) return 2;
        if (sel.contains("3rd")) return 3;
        return -1;
    }

    private void runEvacuationAnimation() {
        if (activeRoute == null || activeRoute.isEmpty() || isMicroViewActive) return;
        if (macroSimTimeline != null) macroSimTimeline.stop();

        int totalStudents = (int) studentSlider.getValue();
        macroSimTimeline = new Timeline();

        for (int i = 0; i < activeRoute.size(); i++) {
            final int index = i;
            final double progressRatio = (double) (i + 1) / activeRoute.size();

            KeyFrame kf = new KeyFrame(Duration.seconds(i * 0.8), e -> {
                activeStudentPos = activeRoute.get(index);
                mapView.renderMacroView(graph, activeRoute, activeStudentPos, getFloorFilterIndex());

                int currentEvacuated = (int) (progressRatio * totalStudents);
                updateLiveStats(currentEvacuated);
            });
            macroSimTimeline.getKeyFrames().add(kf);
        }
        macroSimTimeline.play();
    }

    private void resetBuilding() {
        isEarthquake = false;
        if (microSimTimeline != null) microSimTimeline.stop();
        if (macroSimTimeline != null) macroSimTimeline.stop();
        isMicroViewActive = false;

        for (Node n : graph.getAllNodes()) {
            n.setFire(false);
            n.setStudentCount(0);
        }
        activeStudentPos = graph.getNode("R_B_2");
        activeStudentPos.setStudentCount((int) studentSlider.getValue());

        statusArea.setText("Building Reset.\nGround floor direct exits active. Express Lift capacity configured (12 Max).");
        recalculateRoute();
        updateLiveStats(0);
    }

    @Override
    public void stop() {
        if (threadPool != null) {
            threadPool.shutdown();
        }
    }

    public static void main(String[] args) { launch(args); }
}