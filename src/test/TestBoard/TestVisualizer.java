package TestBoard;

import Board.Board;
import Board.Visualizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TestVisualizer {
    //    @BeforeEach
    void resetJSON() {
        // Delete existing JSON files
        String mapFilePath = "visualize/base_map.json";
        String stateFilePath = "visualize/state.json";
        File mapFile = new File(mapFilePath);
        File stateFile = new File(stateFilePath);
        mapFile.delete();
        stateFile.delete();
    }

    @Test
    void setupJSON() throws IOException {
        assertDoesNotThrow(() -> Visualizer.setupJSON(new Board()), "Ensures that JSON is set up without any errors.");
    }

    @Test
    void updateJSON() {
    }
}