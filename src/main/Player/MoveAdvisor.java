package Player;

import java.util.List;
import java.util.Optional;

/**
 * Selects one move from a list that has already passed the game's rule checks.
 */
@FunctionalInterface
public interface MoveAdvisor {

    /** Advisor used when the optional LLM feature is not configured. */
    MoveAdvisor DISABLED = (gameState, legalMoves) -> Optional.empty();

    /**
     * Chooses one of the supplied legal moves.
     *
     * @param gameState short summary of the current player state
     * @param legalMoves moves already validated by the game
     * @return advice, or an empty result when no recommendation is available
     */
    Optional<MoveAdvice> advise(String gameState, List<MoveOption> legalMoves);
}
