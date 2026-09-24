# Concurrent Airport Simulation

## Overview

Concurrent Airport Simulation models aircraft moving through a small airport with one runway, three gates, and a single refuelling truck. Six aircraft arrive at variable intervals and coordinate landing, gate services, and take-off through multiple Java threads.

## Project scope

The simulation focuses on coordinating shared airport resources safely enough for a complete run while demonstrating Java concurrency concepts. It was developed as an individual concurrent programming project.

## Features

- Simulates six independently running aircraft.
- Restricts airport-ground capacity to three aircraft with a semaphore.
- Coordinates exclusive runway access for landing and take-off.
- Assigns and releases three airport gates.
- Runs passenger handling, cleaning, supply refilling, and aircraft refuelling as concurrent activities.
- Uses one shared refuelling truck across all gates.
- Gives Plane 5 priority as an emergency aircraft.
- Reports gate status, aircraft waiting times, planes served, and passengers boarded.

## How it works

1. Aircraft arrive at random intervals of zero to two seconds and request landing permission.
2. Air traffic control queues normal and emergency requests while enforcing airport capacity.
3. An approved aircraft uses the runway and moves to an assigned gate.
4. Passenger, cleaning, supply, and refuelling activities run at the gate.
5. The aircraft waits for its gate services to finish and requests take-off permission.
6. After every aircraft leaves, the simulation checks that all gates are empty and prints operational statistics.

## Technologies

- Java 23
- Java threads and `Runnable`
- `Semaphore`
- `synchronized`, `wait()`, and `notifyAll()`
- Thread coordination with `join()`
- In-memory queues and statistics

## Project structure

- `AirportCCPAssignment.java` - application entry point and simulation setup
- `AirTrafficControl.java` - landing, take-off, emergency, and capacity coordination
- `Airplane.java` - aircraft workflow from arrival through departure
- `Runway.java` - synchronized runway state
- `GateManager.java` and `Gate.java` - gate allocation and gate-service coordination
- `Passengers.java` - passenger embarkation and disembarkation
- `RefuelTruck.java` - shared refuelling-truck workflow
- `SupplyRefillCleaningCrew.java` - cleaning and supply-refill activity

## Compile and run

The project has no external dependencies. JDK 23 is the verified environment.

### NetBeans

The simulation was developed with NetBeans, but this repository preserves the final submitted source layout and does not include NetBeans project metadata such as `nbproject` or `build.xml`. It therefore cannot be opened directly with **File > Open Project**.

To use the NetBeans **Run Project** workflow:

1. Start Apache NetBeans with JDK 23 configured.
2. Select **File > New Project > Java with Ant > Java Application**.
3. Create the IDE project in a separate local directory and clear **Create Main Class**.
4. Create the package `airportccpassignment` under **Source Packages** and add the nine Java source files from this repository to it.
5. Set `airportccpassignment.AirportCCPAssignment` as the main class.
6. Select **Run > Run Project**, or press **F6**.

Creating the NetBeans project separately keeps generated IDE files out of this repository.

### Visual Studio Code

1. Install JDK 23 and the **Extension Pack for Java**.
2. Select **File > Open Folder** and choose this repository directory.
3. Select **Terminal > New Terminal**.
4. Run the PowerShell commands below in the integrated terminal.

Using the terminal is intentional: the submitted source files are stored at the repository root rather than in a conventional package directory, and the repository does not include Maven, Gradle, or other IDE build configuration.

### PowerShell

From the repository root:

```powershell
New-Item -ItemType Directory -Force build | Out-Null
javac -d build (Get-ChildItem -Filter *.java)
java -cp build airportccpassignment.AirportCCPAssignment
```

The console output changes between runs because arrival intervals and passenger counts are random. A normal run should complete within 60 seconds and end with the gate sanity check and statistics.

## Verification

The unchanged source was compiled with JDK 23.0.2 and exercised in three complete runs. Each run:

- completed in approximately 41 to 45 seconds;
- served all six aircraft;
- prioritized Plane 5's emergency request;
- used the shared refuelling truck sequentially;
- finished with all gates empty;
- printed passenger and waiting-time statistics; and
- exited normally.

There are no automated tests in the project. Representative successful runs demonstrate the expected workflows but do not establish formal race-freedom or deadlock-freedom.

## Current limitations

- Random arrival timing means the exact event order varies between runs.
- Landing requests announced as rejected remain queued and may land when capacity becomes available.
- Coordination relies partly on polling and shared mutable state.
- The project does not include automated tests or persistent storage.
