package TestTask3;

import Board.Board;
import Board.JSONVisualizer;
import Board.Visualizer;
import Board.Subject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TestJSONVisualizer {
    @BeforeEach
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
        Visualizer visualizer = new JSONVisualizer(new ObjectMapper());
        Board board = new Board();
        board.attach(visualizer);
        assertDoesNotThrow(visualizer::setup, "Ensures that JSON is set up without any errors.");
        board.detach(visualizer);
    }

    @Test
    void updateJSON() {
        Visualizer visualizer = new JSONVisualizer(new ObjectMapper());
        Board board = new Board();
        board.attach(visualizer);
        assertDoesNotThrow(visualizer::update, "Ensures that JSON is set up without any errors.");
        board.detach(visualizer);
    }

    @Test
    void setSubjectFailure() {
        Visualizer visualizer = new JSONVisualizer(new ObjectMapper());
        Subject dummySubject = null;
        assertThrows(IllegalArgumentException.class, () -> visualizer.setSubject(dummySubject), "Subject must be a board.");
    }
}