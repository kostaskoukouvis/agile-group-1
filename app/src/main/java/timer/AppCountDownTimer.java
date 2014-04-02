package timer;

import android.os.CountDownTimer;
import android.view.MenuItem;

/**
 * Created by Daniel on 2014-04-02.
 */
public class AppCountDownTimer extends CountDownTimer {

    private MenuItem timer;
    private static AppCountDownTimer instance = null;

    private AppCountDownTimer(long millisInFuture, long countDownInterval, MenuItem timer) {
        super(millisInFuture, countDownInterval);
        this.timer = timer;
    }

    public static AppCountDownTimer getInstance(long millisInFuture, long countDownInterval, MenuItem timer){
        if(instance == null){
           instance = new AppCountDownTimer(millisInFuture, countDownInterval, timer);
        }
        return instance;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        timer.setTitle("" + millisUntilFinished / 1000);
    }

    @Override
    public void onFinish() {
        timer.setTitle("Time's up!");
    }
}
