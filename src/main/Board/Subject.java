package Board;

/**
 * Interface for all subjects that are being observed
 *
 * @author Yojith Sai Biradavolu, McMaster University
 */
public interface Subject {
    /**
     * Attach an observer to the subject
     *
     * @param observer The observer to attach
     */
    void attach(Observer observer);

    /**
     * Detach an observer from the subject
     *
     * @param observer The observer to detach
     */
    void detach(Observer observer);

    /**
     * Notify all observers of any changes
     */
    void notifyObservers();
}
