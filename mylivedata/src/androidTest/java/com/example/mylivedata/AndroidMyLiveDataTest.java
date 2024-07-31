package com.example.mylivedata;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import org.mockito.Mockito;
import org.mockito.Mockito.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AndroidMyLiveDataTest {
//    @Test
//    private void useAppContext() {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//        assertEquals("com.example.mylivedata.test", appContext.getPackageName());
//    }

    boolean isReleased;

    @Test(timeout = 1000)
    public void ValuePosted() {
        final int checkValue = 13;
        isReleased = false;
        MyLiveData<Integer> data = new MyLiveData<>();

        final Lock lock = new ReentrantLock();
        final Condition release = lock.newCondition();

        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                lock.lock();
                isReleased = true;
                release.signalAll();
                lock.unlock();
            }
        };
        data.observe(observer);
        data.postData(checkValue);

        lock.lock();
        while (!isReleased)
        {
            try {
                release.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        lock.unlock();
        assertEquals(checkValue, data.getData().intValue());
    }

    @Test
    public void AddContextObserver() {
        MyLiveData<Integer> data = new MyLiveData<>();
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

            }
        };

        LifecycleOwner lifecycleOwner = Mockito.mock(LifecycleOwner.class);
        Lifecycle lifecycle = new LifecycleRegistry(lifecycleOwner);

        data.observe(lifecycleOwner,observer);
    }
}