# 🛡️ SafeRoute-Intelligent Emergency Evacuation Suite

## Academic Building Disaster Simulation and Evacuation Pathfinding System

SafeRoute is a JavaFX-based emergency evacuation simulation system developed to find safe evacuation routes inside the KUET Academic Building.

The system represents the building environment using graph-based models and applies pathfinding algorithms to calculate suitable evacuation routes during emergency situations such as fire and earthquake scenarios.

The project demonstrates practical implementation of Object-Oriented Programming, Graph Algorithms, JavaFX GUI Development, Database Management, and Simulation Techniques.

---

# 📌 Project Overview

In emergency situations, selecting a safe evacuation route quickly is a critical challenge.

SafeRoute solves this problem by modeling the building structure as a graph:

- Rooms and important locations are represented as nodes.
- Connections between locations are represented as edges.
- Route costs are calculated based on emergency conditions.
- Dijkstra's Algorithm is used to find optimized evacuation paths.

---

# ✨ Features

## 🚨 Emergency Route Finding

- Finds shortest evacuation paths using Dijkstra's Algorithm.
- Calculates routes based on building connectivity.
- Supports emergency-based route selection.
- Provides visual route representation.

---

## 🏢 Building Simulation

The system simulates:

- Classrooms
- Rooms
- Corridors
- Emergency exits
- Student movement

It supports both classroom-level and building-level evacuation simulation.

---

## 🔥 Hazard Simulation

Supported emergency conditions:

- Fire scenario
- Earthquake scenario
- Blocked path conditions
- Dynamic route changes

The system updates route availability according to selected hazards.

---

## 🖥️ JavaFX User Interface

The application provides an interactive graphical interface.

Implemented JavaFX components:

- BorderPane
- StackPane
- VBox
- HBox
- Button
- ComboBox
- Slider
- Label
- Canvas visualization

Users can select emergency scenarios and observe evacuation results.

---

## 🗄️ Database Management

SQLite database is integrated for storing simulation information.

Implemented CRUD operations:

### Create
Stores new evacuation simulation records.

### Read
Retrieves previous simulation data.

### Update
Updates stored simulation information.

### Delete
Removes unwanted records.

---

## ⚡ Multithreading

The project uses Java concurrency features for efficient execution.

Implemented concepts:

- ExecutorService
- Background tasks
- JavaFX Timeline animation

These features help perform operations without freezing the user interface.

---

# 🏗️ System Architecture

```
+------------------------------------------------+
|              SafeRoute Application              |
+------------------------------------------------+

                    |
                    v

+------------------------------------------------+
|              JavaFX User Interface              |
|                                                |
|  Controls | Visualization | User Interaction   |
+------------------------------------------------+

                    |
                    v

+----------------------+-------------------------+
|                      |                         |
v                      v                         v

Pathfinding        Simulation Engine       Database Layer

Dijkstra            Room Movement          SQLite
Algorithm           Simulation             CRUD Operations


                    |
                    v

+------------------------------------------------+
|                 Model Classes                  |
|                                                |
| Node | Edge | Student | Room | Hazard          |
+------------------------------------------------+
```

---

# 🛠️ Technologies Used

| Technology | Purpose |
|------------|---------|
| Java | Main Programming Language |
| JavaFX | User Interface Development |
| Maven | Project Build Management |
| SQLite | Database Storage |
| JDBC | Database Connection |
| Dijkstra Algorithm | Route Optimization |
| Git & GitHub | Version Control |

---

# 📂 Project Structure

```
SafeRoute
│
├── src
│   │
│   └── main
│       │
│       ├── java
│       │   │
│       │   ├── controller
│       │   ├── model
│       │   ├── service
│       │   └── simulation
│       │
│       └── resources
│           └── database
│
├── pom.xml
│
├── README.md
│
└── .gitignore
```

---

# 🚀 Installation & Running

## Requirements

Before running the project, install:

- JDK 17 or higher
- Maven
- JavaFX SDK

---

## Clone Repository

```bash
git clone https://github.com/atifabsar007/SafeRoute-Intelligent-Emergency-Evacuation-Suite.git
```

---

## Navigate to Project Folder

```bash
cd SafeRoute-Intelligent-Emergency-Evacuation-Suite
```

---

## Run Application

```bash
mvn clean javafx:run
```

---

# 📚 Concepts Implemented

This project demonstrates:

- Object-Oriented Programming
- Encapsulation and Abstraction
- Graph Data Structure
- Dijkstra Shortest Path Algorithm
- JavaFX GUI Development
- SQLite Database Integration
- CRUD Operations
- Multithreading
- Software Project Organization

---

# 🎓 Academic Purpose

SafeRoute was developed as an academic software project to demonstrate practical application of programming concepts and software development techniques.

---

# 👨‍💻 Developer

**Md. Atif Absar**

**Project Name:** SafeRoute - Intelligent-Emergency-Evacuation-Suite


---

# 📄 License

This project is developed for educational purposes.
