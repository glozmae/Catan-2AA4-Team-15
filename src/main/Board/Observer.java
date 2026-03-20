package Board;

/**
 * Interface for all observers
 *
 * @author Yojith Sai Biradavolu, McMaster University
 */
public interface Observer {
    /**
     * Updates state of the observer based on the subjects
     *
     */
    void update();

    /**
     * Sets the subject that the observer is observing
     *
     * @param subject The subject to observe
     */
    void setSubject(Subject subject);

    /**
     * Removes the subject that the observer is observing
     */
    void removeSubject();
}
