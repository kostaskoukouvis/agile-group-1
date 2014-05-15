package se.chalmers.agile.tests;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.view.MenuItem;
import android.widget.Button;

import junit.framework.Assert;

import se.chalmers.agile.R;
import se.chalmers.agile.activities.MainActivity;
import se.chalmers.agile.timer.CountDownTimer;


public class TimerTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity mActivity;
    CountDownTimer timer;

    public TimerTest() {
        super(MainActivity.class);
    }

    //Setup method which run before the tests start
    @Override
    public void setUp() throws Exception{
        super.setUp();
        mActivity = getActivity();
        timer = mActivity.getCountdownTimer();
    }

    //Test that the timer is not finished and that the timer is running
    public void testInitial() throws  Exception{

        Assert.assertFalse(timer.isFinished());
        Assert.assertTrue(timer.isRunning());
    }


    //Test the pause functionality
    public void testPause() throws Exception {

        long temp;

        //Test that the timer is running and is not finished before the pause
        //Save the time before pause
        Assert.assertTrue(timer.isRunning());
        Assert.assertFalse(timer.isFinished());
        temp = timer.getTimeLeft();

        //Pause timer and check that the timer actually got paused
        timer.pauseTimer();
        Assert.assertFalse(timer.isRunning());
        Assert.assertFalse(timer.isFinished());

        //Sleep the thread and check if it has actually paused the countdown
        Thread.sleep(5000);
        Assert.assertEquals(temp, timer.getTimeLeft());

        //Resume the timer
        timer.resumeTimer();

        //Wait a second and check if the timer got resumed
        Thread.sleep(1000);
        Assert.assertTrue(timer.getTimeLeft() < temp);

    }

    //Test the resume functionality
    public void testResume() throws Exception {

        long temp;

        //Check that the timer is running and not finished
        Assert.assertFalse(timer.isFinished());
        Assert.assertTrue(timer.isRunning());

        //Pause the timer, check that the timer paused, and wait 2 seconds
        timer.pauseTimer();
        Assert.assertFalse(timer.isRunning());
        temp = timer.getTimeLeft();
        Thread.sleep(2000);

        //Resume the timer, check that the timer has not continued to count down during the pause
        // and that it is running again and not finished
        timer.resumeTimer();
        Assert.assertTrue(temp == timer.getTimeLeft());
        Assert.assertTrue(timer.isRunning());
        Assert.assertFalse(timer.isFinished());
    }

    //Test the reset functionality of the timer
    public void testReset() throws Exception {

        //Save the start time and the time left
        long timeLeft = timer.getTimeLeft();
        long timeStart = timer.getStartTime();

        //Check that the timer is running and that it is not in its start state
        Assert.assertTrue(timer.isRunning());
        Assert.assertTrue(timeLeft < timeStart);
        Assert.assertTrue(timeLeft > 0);

        //Try to reset the timer and check if it has reset properly
        timer.resetTimer();
        timeLeft = timer.getTimeLeft();
        timeStart = timer.getStartTime();
        Assert.assertTrue(timer.isRunning());
        Assert.assertTrue(timeLeft == timeStart);
        Assert.assertTrue(timeLeft > 0);
    }

}
