package timer;

import android.os.CountDownTimer;
import android.view.MenuItem;

import se.chalmers.agile.R;

/**
 * Created by Daniel on 2014-04-02.
 */
public class AppCountDownTimer extends CountDownTimer {

    private MenuItem timer;
    private static AppCountDownTimer instance = null;
    private long temp_millis;
    private long temp_interval;
    private long millis;
    private long interval;

    private AppCountDownTimer(long millisInFuture, long countDownInterval, MenuItem timer) {
        super(millisInFuture, countDownInterval);
        this.interval = countDownInterval;
        this.millis = millisInFuture;
        this.temp_interval = this.interval;
        this.temp_millis = this.millis;
        this.timer = timer;
        timer.getSubMenu().getItem(0).setVisible(false);
        timer.getSubMenu().getItem(1).setVisible(true);
        timer.getSubMenu().getItem(2).setVisible(true);
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
        timer.getSubMenu().getItem(1).setVisible(false);
        timer.setTitle("Time's up!");
    }

    public void pause(){
        instance.cancel();
        timer.getSubMenu().getItem(1).setVisible(false);
        timer.getSubMenu().getItem(0).setVisible(true);
    }

    public void resume(){
        instance.cancel();
        instance = new AppCountDownTimer(instance.temp_millis, instance.temp_interval, instance.timer);
        instance.start();
        timer.getSubMenu().getItem(0).setVisible(false);
        timer.getSubMenu().getItem(1).setVisible(true);
    }

    public void reset(){
        instance.cancel();
        instance = new AppCountDownTimer(millis, interval, instance.timer);
        instance.start();
        timer.getSubMenu().getItem(1).setVisible(true);
    }

}
