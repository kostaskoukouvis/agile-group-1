package timer;

import android.os.CountDownTimer;
import android.view.MenuItem;

import se.chalmers.agile.R;

/**
 * Class that represents a countdown timer
 */
public class AppCountDownTimer extends CountDownTimer {

    private MenuItem timer;
    private static AppCountDownTimer instance = null;
    private long temp_millis;
    private long temp_interval;
    private long millis;
    private long interval;
    private MenuItem subPause;
    private MenuItem subStart;
    private MenuItem subReset;

    private AppCountDownTimer(long millisInFuture, long countDownInterval, MenuItem timer) {
        super(millisInFuture, countDownInterval);
        this.interval = countDownInterval;
        this.millis = millisInFuture;
        this.temp_interval = this.interval;
        this.temp_millis = this.millis;
        this.timer = timer;
        this.subPause = timer.getSubMenu().findItem(R.id.timerPause);
        this.subStart = timer.getSubMenu().findItem(R.id.timerStart);
        this.subReset = timer.getSubMenu().findItem(R.id.timerReset);

        subStart.setVisible(false);
        subPause.setVisible(true);
        subReset.setVisible(true);
    }

    public static AppCountDownTimer getInstance(long millisInFuture, long countDownInterval, MenuItem timer){
        if(instance == null){
           instance = new AppCountDownTimer(millisInFuture, countDownInterval, timer);
        }
        return instance;
    }

    @Override
    public void onTick(long millisUntilFinished) {
            temp_millis = millisUntilFinished;
            timer.setTitle("Navigator swap in: " + (millisUntilFinished/1000)/60 +
                           ":" + String.format("%02d", (int)millisUntilFinished/1000%60));
    }

    @Override
    public void onFinish() {
        subPause.setVisible(false);
        timer.setTitle("Time's up!");
    }

    public void pause(){
        instance.cancel();
        subPause.setVisible(false);
        subStart.setVisible(true);
    }

    public void resume(){
        instance.cancel();
        instance = new AppCountDownTimer(instance.temp_millis, instance.temp_interval, instance.timer);
        instance.start();
        subStart.setVisible(false);
        subPause.setVisible(true);
    }

    public void reset(){
        instance.cancel();
        instance = new AppCountDownTimer(millis, interval, instance.timer);
        instance.start();
        subPause.setVisible(true);
    }

}
