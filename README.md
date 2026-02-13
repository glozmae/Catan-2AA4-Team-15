# 🏰 Settlers of Catan Simulator
### SFWRENG 2AA4 - Software Development II | McMaster University

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/quality_gate?project=glozmae_Catan-2AA4-Team-15)](https://sonarcloud.io/summary?id=glozmae_Catan-2AA4-Team-15) [![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://www.oracle.com/java/) [![License](https://img.shields.io/badge/License-Academic-blue)](#)

## 📖 Project Overview
This repository contains a robust Java implementation of the **Settlers of Catan** board game. Designed as a capstone for **SFWRENG 2AA4**, this project demonstrates advanced Object-Oriented Programming (OOP) principles, graph theory application for board topology, and algorithmic decision-making for AI opponents.

The simulator models the complete game lifecycle: from the randomized board setup and resource distribution to the turn-based economy and victory point calculation.

---

## ✨ Key Features

### 🧠 Intelligent Computer Opponents
* **Autonomous Decision Making:** The `ComputerPlayer` class utilizes a priority-based decision tree to analyze the board state.
* **Valid Move Scanning:** Instead of random guessing, the AI scans the graph for all legally valid moves (Roads, Settlements, Cities) before executing strategies.
* **Resource Management:** Dynamic assessment of hand resources to determine affordability of structures.

### 🗺️ Graph-Based Board Architecture
* **Node & Edge Topology:** The game board is modeled not just as a grid, but as a mathematical graph where `Node` objects (intersections) are linked by `Road` edges.
* **Distance Rule Enforcement:** Logic strictly adheres to Catan rules, preventing settlements from being placed within one edge of another node.
* **Longest Road Calculation:** Traversal algorithms capable of calculating connected road lengths for victory points.

### ⚖️ Game Economy & Mechanics
* **Resource Distribution:** A probability-based dice system (`MultiDice`) that allocates resources (Brick, Lumber, Ore, Grain, Wool) to players based on tile values.
* **Structure Polymorphism:** Extensible design for `Structure` types, allowing seamless upgrades from `Settlement` to `City`.
* **Bank System:** Finite resource tracking via the `GameBank` and `Bank` interfaces.

---

## 📂 Architecture & Package Structure

The codebase is organized into domain-specific packages to ensure low coupling and high cohesion:

| Package | Description |
| :--- | :--- |
| **`Board`** | Handles the physical state of the map. Includes `Node` (vertices), `Tile` (hexes), and `SetupManager` for board generation. |
| **`Player`** | Contains the `Player` abstract class and concrete `ComputerPlayer`. Manages inventory via `PlayerHand` and score via `VictoryPointCalculator`. |
| **`Game`** | The central controller. `Game.java` manages the turn loop, state transitions, and win conditions. `Main.java` serves as the entry point. |
| **`GameResources`** | The model layer. Defines the immutable logic for `ResourceType`, `Road`, `Settlement`, `City`, and the `Bank`. |

---

## 👥 The Team (Team 15)

| Name | Role / Contribution | Email |
| :--- | :--- | :--- |
| **Parnia Yazdinia** | Core Logic & Design | `yazdinp@mcmaster.ca` |
| **Elizabeth Glozman** | Project Lead & Architecture | `glozmae@mcmaster.ca` |
| **Yojith Sai Biradavolu** | AI & Player Logic | `biradavy@mcmaster.ca` |
| **Tai Mobasshir** | Board Topology & Graph Logic | `mobasst@mcmaster.ca` |

---

## 🚀 Getting Started

### Prerequisites
* **Java Development Kit (JDK) 17** or higher.
* An IDE (IntelliJ IDEA, Eclipse) or a terminal.

### Compilation & Execution

**Option 1: Using the Command Line**
Navigate to the root directory of the project and run the following commands to compile the source code and start the simulation:

```bash
# 1. Compile all source files into the 'out' directory
javac -d out src/**/*.java

# 2. Run the Main class (Entry Point)
java -cp out Game.Main
