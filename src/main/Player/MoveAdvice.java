package Player;

/**
 * A move selected by a strategy advisor and its short explanation.
 *
 * @param moveId id of the selected legal move
 * @param reason short explanation for the selection
 */
public record MoveAdvice(int moveId, String reason) {
}
