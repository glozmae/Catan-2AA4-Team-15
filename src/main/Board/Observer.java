package Board;

public interface Observer {
    /**
     * Updates state of the observer based on the subjects
     *
     */
    public void update();

    /**
     * Sets the subject that the observer is observing
     * @param subject the subject to observe
     */
    public void setSubject(Subject subject);
}
