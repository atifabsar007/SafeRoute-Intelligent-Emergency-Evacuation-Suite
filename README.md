# 🛡️ SafeRoute — KUET Emergency Evacuation Portal

> **KUET Academic Building Disaster Simulation & Dynamic Evacuation Pathfinder**

SafeRoute is a high-performance JavaFX application designed to simulate micro- and macro-level emergency evacuations across the KUET Academic Building. Integrating Dijkstra's hazard-aware shortest-path algorithm, cellular-automata classroom grid physics, multithreaded network polling, and SQLite database auditing, SafeRoute provides real-time optimal routing for multi-hazard scenarios.

---

## 🛠️ Feature Mapping against Course Evaluation Rubric

### 1. Advanced Object-Oriented Programming (OOP)
* **Encapsulation & Domain Models:** `Node`, `Edge`, `Student`, and `RoomGrid` maintain strict private states with robust getters/setters.
* **Abstraction & Polymorphism:** Decoupled map rendering logic (`KUETMapView`) from core graph data structures (`Graph`).
* **Algorithmic Edge-Weighting:** Dynamic edge cost calculations based on hazard types (Fire vs. Earthquake) and elevator capacity limits ($12\text{ max capacity}$).

### 2. JavaFX UI Design & Layout Responsiveness
* **Panes Used:** `BorderPane`, `StackPane`, `VBox`, `HBox`, and dynamic `Canvas`.
* **UI Controls:** `ComboBox`, `Slider`, `Button`, `ProgressBar`, `Label`, `Separator`.
* **Dynamic Canvas Centering:** Canvas coordinates use window scale math `(canvasWidth - totalContentWidth) / 2` to remain centered regardless of monitor resolution.

### 3. Concurrency & Multithreading
* **ExecutorService Thread Pool:** Offloads network IO calls to prevent freezing the JavaFX Application Thread.
* **Task & Timeline Schedulers:** Asynchronous weather fetching paired with smooth 60 FPS animation frame loops (`Timeline` and `KeyFrame`).

### 4. Database Integration & Data Manipulation (CRUD)
* **SQLite Persistence:** Embedded SQLite integration tracking evacuation logs, user configurations, and hazard history.
* **Full CRUD Operations:**
  * **Create:** Log new evacuation simulation runs.
  * **Read:** Retrieve past hazard historical stats.
  * **Update:** Update classroom occupancy numbers dynamically.
  * **Delete:** Clear past session simulation logs.

### 5. Networking & Data Parsing
* **HTTP Live Requests:** `WeatherService` communicates with external REST APIs to fetch real-time weather/alert updates for the KUET campus coordinates.
* **JSON Parsing:** Asynchronously parses incoming JSON payloads to alert occupants of active regional hazards.

---
