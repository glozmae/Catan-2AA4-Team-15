package Player;

/**
 * A legal move that can be shown to an optional strategy advisor.
 *
 * @param id unique number used to select the move
 * @param score score calculated by the existing rule-based AI
 * @param description human-readable description of the move
 */
public record MoveOption(int id, double score, String description) {
}
