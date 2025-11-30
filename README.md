# SunLab

![SunDowner Logo](https://github.com/NeuralCortex/Sun/blob/main/images/sun.png)

## Overview

SunLab is a JavaFX application that calculates and visually displays the positions and phases of the sun and moon. It utilizes nearly all features of the [commons-suncalc](https://shredzone.org/maven/commons-suncalc/index.html) library, available on GitHub and Maven.

### Features

SunLab provides the following features, accessible via tabs in the application:

- Daily Information
- Calendar
- Moon Phases
- Moon Parameters
- Solar Parameters
- Sun and Moon Times
- Big Data
- Sunrise and Sunset
- Altitude and Local Time
- Altitude and Azimuth
- Earth-Moon Distance
- Earth-Sun Distance
- Sun Position Diagram (Cartesian)
- Sun Position Diagram (Polar)

## Important Note

The accuracy of the calculated data depends on geographic information, requiring an internet connection to set the location using OpenStreetMap. To select a location, right-click on the map within the application.

## Prerequisites

To run SunDowner, ensure the following are installed:

- **Java SDK**: [Java 24](https://www.oracle.com/java/technologies/downloads/#jdk24-windows)
- **JavaFX SDK**: [JavaFX](https://gluonhq.com/products/javafx/)
- **SceneBuilder**: [Gluon SceneBuilder](https://gluonhq.com/products/scene-builder/) (for GUI development, optional for running)
- **Apache NetBeans 27 IDE**: [NetBeans 27](https://netbeans.apache.org/) (optional, used for development)

## Installation

1. **Download the JavaFX SDK**: Extract it to a known location (e.g., `C:\javafx-sdk-24`).
2. **Ensure Java 24 is installed**: Verify the `java` command is accessible in your system's PATH.
3. **Obtain the SunDowner JAR**: Place the `Sun-1.0.0.jar` file in a directory of your choice.

## Running the Application

### Windows
Use the provided batch file to launch SunDowner:

1. Save the following as `run_sundowner.bat` in the same directory as `Sun-1.0.0.jar`:

```batch
@echo off
REM Batch file to launch the SunDowner JavaFX application

REM Set the path to the SunDowner JAR file
SET JAR_FILE=Sun-1.0.0.jar

REM Set the path to the JavaFX SDK lib folder (update to match your JavaFX SDK location)
SET JAVAFX_LIB=C:\path\to\javafx-sdk-24\lib

REM Specify the main class (update if different or remove if specified in the JAR's manifest)
SET MAIN_CLASS=your.main.Main

REM Run the SunDowner application with required JavaFX modules
java --module-path "%JAVAFX_LIB%" --add-modules javafx.controls,javafx.fxml,javafx.swing -jar %JAR_FILE% %MAIN_CLASS%

REM Pause to keep the window open for debugging (optional, remove if not needed)
pause
```

2. Update the `JAVAFX_LIB` variable to point to your JavaFX SDK `lib` folder (e.g., `C:\javafx-sdk-24\lib`).
3. If the main class is not specified in the JAR's manifest, update `MAIN_CLASS` to the correct fully qualified class name (e.g., `your.main.Main`). Otherwise, remove `%MAIN_CLASS%` from the `java` command.
4. Double-click `run_sundowner.bat` or run it from the command prompt.
5. Ensure an internet connection is available for OpenStreetMap functionality.

### Linux/macOS
For non-Windows systems, create a shell script (e.g., `run_sundowner.sh`):

```bash
#!/bin/bash
# Script to launch the SunDowner JavaFX application

# Set the path to the SunDowner JAR file
JAR_FILE=Sun-1.0.0.jar

# Set the path to the JavaFX SDK lib folder (update to match your JavaFX SDK location)
JAVAFX_LIB=/path/to/javafx-sdk-24/lib

# Specify the main class (update if different or remove if specified in the JAR's manifest)
MAIN_CLASS=your.main.Main

# Run the SunDowner application with required JavaFX modules
java --module-path "$JAVAFX_LIB" --add-modules javafx.controls,javafx.fxml,javafx.swing -jar $JAR_FILE $MAIN_CLASS
```

1. Update the `JAVAFX_LIB` and `MAIN_CLASS` as needed.
2. Make the script executable: `chmod +x run_sundowner.sh`.
3. Run the script: `./run_sundowner.sh`.

## Troubleshooting

- **JavaFX Path Error**: Ensure the `JAVAFX_LIB` path points to the correct JavaFX SDK `lib` folder.
- **Missing Modules**: If the application fails to start, add required JavaFX modules (e.g., `javafx.graphics`) to the `--add-modules` flag.
- **Internet Connection**: Verify an active internet connection for OpenStreetMap functionality.
- **Main Class Not Found**: Confirm the `MAIN_CLASS` variable matches the application's main class or is specified in the JAR's manifest.
