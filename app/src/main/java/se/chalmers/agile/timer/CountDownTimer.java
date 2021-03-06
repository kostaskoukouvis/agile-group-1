package se.chalmers.agile.timer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import se.chalmers.agile.constants.Constants;
import se.chalmers.agile.R;

/**
 * A countdown se.chalmers.agile.timer that counts down from a time given when created.
 */
public class CountDownTimer extends Thread {

    //Singleton instance
    private static CountDownTimer instance = null;

    //Representation of the time state of the se.chalmers.agile.timer
    private long millis;
    private long millisUntilFinished;
    private long interval;

    //Menu items which are to be manipulated
    private MenuItem subPause;
    private MenuItem subStart;
    private MenuItem timer;

    //States of the se.chalmers.agile.timer
    private boolean running;
    private boolean finished;

    //Handler to pass information from the se.chalmers.agile.timer thread to the UI thread
    private Handler handler;

    //Constant to represent when the time is out
    private static final long TIME_OUT = 0;

    //Constant to represent how many seconds there are per minute
    private static final long SECONDS_MINUTE = 60;

    //Access the settings values
    public SharedPreferences SP;


    /**
     *Private constructor to create a countdown se.chalmers.agile.timer.
     */
    private CountDownTimer(long countDownInterval, Handler handler, Context context) {
        super();
        SP = PreferenceManager.getDefaultSharedPreferences(context);
        millis = millisUntilFinished = Long.parseLong(SP.getString("countdownTime", "1800000"));
        interval = countDownInterval;
        millisUntilFinished = millis;
        running = false;
        finished = false;
        this.handler = handler;

    }

    /**
     *Method to get singleton object. Create a new object if instance is null, else return instance.
     */

    public static CountDownTimer getInstance(long countDownInterval, Handler handler, Context context){
        if(instance == null){
            instance = new CountDownTimer(countDownInterval, handler, context);
        }
        return instance;
    }


    /**
     *Overridden run method from Thread. Defines what should happen when the se.chalmers.agile.timer run.
     */
    @Override
    public void run(){
        while(true){
            if(running && !finished){
                millisUntilFinished -= interval;
                if (millisUntilFinished > TIME_OUT) {
                    onTick(millisUntilFinished);
                }
                else{
                    onFinish();
                }
            }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *Start the se.chalmers.agile.timer for the first time. If the thread is already alive, only update the se.chalmers.agile.timer menu item.
     */
    public void startTimer(MenuItem timer){
        this.timer = timer;
        subPause = timer.getSubMenu().findItem(R.id.timerPause);
        subStart = timer.getSubMenu().findItem(R.id.timerStart);

        if(!this.isAlive()) {
            running = true;
            start();
        }

        if(running){
            subStart.setVisible(false);
            subPause.setVisible(true);
        }
        else{
            if(finished){
                onFinish();
            }
            else {
                pauseTimer();
            }
        }
    }

    /**
     *Pauses the se.chalmers.agile.timer
     */
    public void pauseTimer(){
        subPause.setVisible(false);
        subStart.setVisible(true);
        onTick(millisUntilFinished);
        running = false;
    }

    /**
     *Resumes the se.chalmers.agile.timer
     */
    public void resumeTimer(){
        subStart.setVisible(false);
        subPause.setVisible(true);
        running = true;
    }


    /**
     *Resets the se.chalmers.agile.timer
     */
    public void resetTimer(){
        subPause.setVisible(true);
        subStart.setVisible(false);
        millisUntilFinished = Long.parseLong(SP.getString("countdownTime", "1800000"));
        finished = false;
        running = true;
    }

    public long getTimeLeft(){
        return millisUntilFinished;
    }


    /**
     *Check if the se.chalmers.agile.timer is running
     */
    public boolean isRunning(){
        return running;
    }

    /**
     *Updates the se.chalmers.agile.timer text. Sends information to the handler to forward information to the UI thread.
     */
    private void onTick(final long millisUntilFinished) {
        final Runnable myRunnable = new Runnable() {
            public void run() {
                timer.setTitle("Swap in: " + (millisUntilFinished / Constants.SECOND) / SECONDS_MINUTE +
                        ":" + String.format("%02d", (int) millisUntilFinished /
                        Constants.SECOND % SECONDS_MINUTE));
            }
        };
        handler.post(myRunnable);
    }


    /**
     * Runs when the time is up
     */
    private void onFinish(){
        subStart.setVisible(false);
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

    public boolean isFinished() {
        return finished;
    }

    public long getStartTime() {
        return millis;
    }
}