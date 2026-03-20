package Board;

/**
 * Interface for all observers
 *
 * @author Yojith Sai Biradavolu, McMaster University
 */
public abstract class Observer {
    /**
     * Subject that the observer is observing
     */
    protected Subject subject;

    /**
     * Updates state of the observer based on the subjects
     *
     */
    public abstract void update();

    /**
     * Sets the subject that the observer is observing
     *
     * @param subject The subject to observe
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /**
     * Removes the subject that the observer is observing
     */
    public void removeSubject() {
        this.subject = null;
    }
}
