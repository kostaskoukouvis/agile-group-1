package timer;

import android.os.Handler;
import android.view.MenuItem;

import se.chalmers.agile.R;

/**
 * Created by Daniel on 2014-04-06.
 */
public class CountDownTimer extends Thread {

    private static CountDownTimer instance = null;
    private long millis;
    private long millisUntilFinished;
    private long interval;
    private MenuItem subPause;
    private MenuItem subStart;
    private MenuItem subReset;
    private static MenuItem timer;
    private boolean running;
    private boolean finished;
    private Handler handler;


    private CountDownTimer(long millisInFuture, long countDownInterval, Handler handler) {
        super();
        this.millis = millisInFuture;
        this.interval = countDownInterval;
        this.millisUntilFinished = millisInFuture;
        this.running = false;
        this.finished = false;
        this.handler = handler;

    }

    public static CountDownTimer getInstance(long millisInFuture, long countDownInterval, Handler handler){
        if(instance == null){
            instance = new CountDownTimer(millisInFuture, countDownInterval, handler);
        }
        return instance;
    }

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

    public void startTimer(MenuItem timer){
        this.timer = timer;
        this.subPause = timer.getSubMenu().findItem(R.id.timerPause);
        this.subStart = timer.getSubMenu().findItem(R.id.timerStart);
        this.subReset = timer.getSubMenu().findItem(R.id.timerReset);

        if(!this.isAlive()) {
            running = true;
            this.start();
        }

        if(running){
            this.subStart.setVisible(false);
            this.subPause.setVisible(true);
        }
        else{
            this.subStart.setVisible(true);
            this.subPause.setVisible(false);
        }
    }

    public void pauseTimer(){
        subPause.setVisible(false);
        subStart.setVisible(true);
        running = false;
    }

    public void resumeTimer(){
        subStart.setVisible(false);
        subPause.setVisible(true);
        running = true;
    }

    public void resetTimer(){
        subPause.setVisible(true);
        subStart.setVisible(false);
        this.millisUntilFinished = millis;
        this.finished = false;
        running = true;
    }

    public boolean isRunning(){
        return running;
    }

    public void onTick(final long millisUntilFinished) {

        final Runnable myRunnable = new Runnable() {
            public void run() {
                timer.setTitle("Navigator swap in: " + (millisUntilFinished / 1000) / 60 +
                        ":" + String.format("%02d", (int) millisUntilFinished / 1000 % 60));
            }
        };
        handler.post(myRunnable);
    }


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