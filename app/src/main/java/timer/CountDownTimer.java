package timer;

import android.os.Handler;
import android.view.MenuItem;
import se.chalmers.agile.R;

/**
 * A countdown timer that counts down from a time given when created.
 */
public class CountDownTimer extends Thread {

    //Singleton instance
    private static CountDownTimer instance = null;

    //Representation of the time state of the timer
    private long millis;
    private long millisUntilFinished;
    private long interval;

    //Menu items which are to be manipulated
    private MenuItem subPause;
    private MenuItem subStart;
    private MenuItem subReset;
    private static MenuItem timer;

    //States of the timer
    private boolean running;
    private boolean finished;

    //Handler to pass information from the timer thread to the UI thread
    private Handler handler;


    /**
     *Private constructor to create a countdown timer.
     */
    private CountDownTimer(long millisInFuture, long countDownInterval, Handler handler) {
        super();
        millis = millisInFuture;
        interval = countDownInterval;
        millisUntilFinished = millisInFuture;
        running = false;
        finished = false;
        this.handler = handler;

    }

    /**
     *Method to get singleton object. Create a new object if instance is null, else return instance.
     */

    public static CountDownTimer getInstance(long millisInFuture, long countDownInterval, Handler handler){
        if(instance == null){
            instance = new CountDownTimer(millisInFuture, countDownInterval, handler);
        }
        return instance;
    }


    /**
     *Overridden run method from Thread. Defines what should happen when the timer run.
     */
    @Override
    public void run(){
        while(true){
            if(running && !finished){
                millisUntilFinished -= interval;
                if (millisUntilFinished > 0) {
                    onTick(millisUntilFinished);
                }
                else{
                    onFinish();
                }
            }
            else{
                onTick(millisUntilFinished);
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *Start the timer for the first time. If the thread is already alive, only update the timer menu item.
     */
    public void startTimer(MenuItem timer){
        this.timer = timer;
        subPause = timer.getSubMenu().findItem(R.id.timerPause);
        subStart = timer.getSubMenu().findItem(R.id.timerStart);
        subReset = timer.getSubMenu().findItem(R.id.timerReset);

        if(!this.isAlive()) {
            running = true;
            start();
        }

        if(running){
            subStart.setVisible(false);
            subPause.setVisible(true);
        }
        else{
            subStart.setVisible(true);
            subPause.setVisible(false);
        }
    }

    /**
     *Pauses the timer
     */
    public void pauseTimer(){
        subPause.setVisible(false);
        subStart.setVisible(true);
        running = false;
    }

    /**
     *Resumes the timer
     */
    public void resumeTimer(){
        subStart.setVisible(false);
        subPause.setVisible(true);
        running = true;
    }


    /**
     *Resets the timer
     */
    public void resetTimer(){
        subPause.setVisible(true);
        subStart.setVisible(false);
        millisUntilFinished = millis;
        finished = false;
        running = true;
    }


    /**
     *Check if the timer is running
     */
    public boolean isRunning(){
        return running;
    }

    /**
     *Updates the timer text. Sends information to the handler to forward information to the UI thread.
     */
    public void onTick(final long millisUntilFinished) {
        final Runnable myRunnable = new Runnable() {
            public void run() {
                timer.setTitle("Navigator swap in: " + (millisUntilFinished / 1000) / 60 +
                        ":" + String.format("%02d", (int) millisUntilFinished / 1000 % 60));
            }
        };
        handler.post(myRunnable);
    }


    /**
     * Runs when the time is up
     */
    public void onFinish(){
        subPause.setVisible(false);
        running = false;
        finished = true;
        final Runnable myRunnable = new Runnable() {
            public void run() {
                timer.setTitle("Time's up!");
            }
        };
        handler.post(myRunnable);
    }
}