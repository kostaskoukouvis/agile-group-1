package se.chalmers.agile.tasks;

/**
 * This interface defines an action to be performed after any background computation (usually,
 * some remote API call).
 *
 * @param <T> Type of de data which will be received.
 */
public interface OnPostExecuteCallback<T> {
    /**
     * What to do on "post executing" an async task.
     *
     * @param data Data expected to be received by the "doInBackground" method.
     */
    public void performAction(T data);
}
