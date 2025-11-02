# Crosswords — JavaFX Game

Crosswords is a desktop crossword-style puzzle game built with Java and JavaFX as part of the PO2 course project. The game generates the grid (board) automatically at runtime; the words to be found are read from a plain text (.txt) file. Players form words by clicking boxes on the generated grid.

## Authors
- Rafael Narciso - 24473
- Martinho Caeiro - 23919

## Status
- Educational class project
- Java 17 / JavaFX desktop application
- Developed using IntelliJ IDEA

## Table of Contents
- About
- Features
- Requirements
- Build & Run (IntelliJ / JDK 17)
- Running a packaged JAR
- Words file format and location
- Gameplay / Controls
- Adding / Editing Word Lists
- Troubleshooting
- Contributing

## About
This project implements a clickable crossword-like game using JavaFX for the UI. At startup the application generates the board layout automatically and then places the target words from a text file. The player selects letters by clicking cells to form the words from the word list.

## Features
- Automatically generated grid/board
- Target words loaded from a plain text (.txt) file
- Click cells to select letters and form words
- Visual highlighting of selected cells
- Word validation / checking (UI dependent)
- Simple word-list management via text files

## Requirements
- JDK 17 (tested)
- IntelliJ IDEA (project developed and tested in IntelliJ)
- JavaFX (if not provided by your JDK/distribution, see Build & Run)

## Build & Run (IntelliJ / JDK 17)
1. Open the project in IntelliJ IDEA.
2. Ensure the Project SDK is set to JDK 17:
   - File > Project Structure > Project > Project SDK -> select JDK 17.
3. If JavaFX libraries are required by your runtime:
   - Download the JavaFX SDK matching JDK 17 from https://openjfx.io/ (only necessary if your JDK distribution doesn't include JavaFX).
   - Add JavaFX as a library in the project or add VM options to the run configuration:
     --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml
     (Replace /path/to/javafx/lib with your real path.)
4. Run the main application class (the class extending javafx.application.Application) using the project's run configuration.

## Running a packaged JAR
- If you build a JAR without bundling JavaFX, launch with:
  ```
  java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml -jar path/to/your-app.jar
  ```
- For a self-contained distribution, consider using jlink / jpackage (requires more setup).

## Words file format and location
- The application reads the list of target words from a plain text (.txt) file.
- Common and recommended format:
  - One word per line.
  - Words can be uppercase or lowercase (the loader may be case-insensitive).
  - No extra characters or separators; just the words you want the game to place.
  - Example:
    ```
    APPLE
    RIVER
    JAVA
    CROSSWORD
    ```
- Where to place the file:
  - Check the code for the exact path or filename the loader expects (common locations: `src/main/resources/`, a `data/` folder, or the working directory).
  - If the loader uses a fixed filename (for example `words.txt`), place your file in the application's working directory or in `src/main/resources` so it ends up on the classpath.
- If you want, I can inspect the repository and update this README with the exact filename/path and any special formatting rules used by your loader.

## Gameplay / Controls
- Start the application.
- The application generates the board and places the target words (hidden within the grid).
- Click cells (letters) to select them in sequence; selected cells are highlighted.
- Use the UI button(s) to submit or check the currently selected sequence against the word list (if available).
- Found words may be marked visually (highlighted, crossed out from the word list, etc.) depending on UI implementation.

## Adding / Editing Word Lists
- Create or edit a .txt file containing the words you want players to find, following the format above.
- Save the file where the application expects to load it (see "Words file format and location").
- Restart the application or use any in-app load feature (if implemented) to load the new word list.

## Troubleshooting
- JavaFX runtime errors (NoClassDefFoundError or javafx.* not found):
  - Ensure JavaFX is available and supply the `--module-path` and `--add-modules` VM options when required.
- Word file not found or loading errors:
  - Verify the filename and location match what the application expects. Check console output for loader exceptions.
- Words not placed or overlapping incorrectly:
  - Check the formatting of the words file (no empty lines or invalid characters). Inspect console logs for placement errors.
- UI not responding / buttons disabled:
  - Check console logs for uncaught exceptions, especially during FXML loading or controller initialization.
  - Verify required resources (FXML, images, word files) are present on the classpath.

## Contributing
- This is a class project but improvements, bug fixes, and clearer word-management are welcome.
- Suggested workflow:
  - Fork the repository
  - Create a feature/fix branch
  - Submit a pull request with a clear description and testing steps
    
