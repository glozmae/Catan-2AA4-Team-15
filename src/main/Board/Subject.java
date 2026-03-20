package Board;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for all subjects that are being observed
 *
 * @author Yojith Sai Biradavolu, McMaster University
 */
public abstract class Subject {
    protected List<Observer> observers = new ArrayList<>();

    /**
     * Attach an observer to the subject
     *
     * @param observer The observer to attach
     */
    public void attach(Observer observer) {
        observers.add(observer);
        observer.setSubject(this);
    }

    /**
     * Detach an observer from the subject
     *
     * @param observer The observer to detach
     */
    public void detach(Observer observer) {
        observers.remove(observer);
        observer.removeSubject();
    }

    /**
     * Notify all observers of any changes
     */
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
