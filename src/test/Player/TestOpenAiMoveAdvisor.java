package Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

/** Tests parsing and validation of LLM move responses without making live API calls. */
public class TestOpenAiMoveAdvisor {

    @Test
    void parsesValidMoveAndReason() {
        Optional<MoveAdvice> advice = OpenAiMoveAdvisor.parseAdvice(
                "MOVE_ID: 1\nREASON: This move improves the road network.", 3);

        assertTrue(advice.isPresent());
        assertEquals(1, advice.get().moveId());
        assertEquals("This move improves the road network.", advice.get().reason());
    }

    @Test
    void rejectsMoveThatWasNotOffered() {
        Optional<MoveAdvice> advice = OpenAiMoveAdvisor.parseAdvice(
                "MOVE_ID: 7\nREASON: Build here.", 2);

        assertTrue(advice.isEmpty());
    }

    @Test
    void rejectsMalformedResponse() {
        assertTrue(OpenAiMoveAdvisor.parseAdvice("Build a road.", 2).isEmpty());
        assertTrue(OpenAiMoveAdvisor.parseAdvice("", 2).isEmpty());
    }
}
