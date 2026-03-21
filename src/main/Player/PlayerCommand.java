package Player;

/**
 * Represents a player action that can be executed, undone, and redone.
 */
public interface PlayerCommand {
    void execute();
    void undo();
    String getExecuteMessage();
    String getUndoMessage();
}