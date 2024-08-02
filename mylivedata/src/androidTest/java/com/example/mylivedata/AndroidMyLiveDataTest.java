package com.example.mylivedata;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.Observer;
import androidx.lifecycle.testing.TestLifecycleOwner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.concurrent.atomic.AtomicBoolean;
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
        while (!isReleased) {
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
    public void AddContextObservers() {
        MyLiveData<Integer> data = new MyLiveData<>();
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

            }
        };

        Observer<Integer> secondObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

            }
        };

        TestLifecycleOwner lifecycleOwner = new TestLifecycleOwner();
        lifecycleOwner.setCurrentState(Lifecycle.State.CREATED);

        data.observe(lifecycleOwner, observer);
        assertEquals(1, lifecycleOwner.getObserverCount());

        data.observe(lifecycleOwner, secondObserver);
        assertEquals(2, lifecycleOwner.getObserverCount());

        assertThrows(IllegalArgumentException.class, () -> {
            data.observe(lifecycleOwner, observer);
        });
    }

    AtomicBoolean flag = new AtomicBoolean();
    @Test
    public void ObserverIsNotNotified() {
        flag.set(false);
        final int dummyValue = 13;

        MyLiveData<Integer> data = new MyLiveData<>();
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                flag.set(true);
                assertEquals(dummyValue, integer.intValue());
            }
        };
        TestLifecycleOwner lifecycleOwner = new TestLifecycleOwner();
        lifecycleOwner.setCurrentState(Lifecycle.State.INITIALIZED);
        data.observe(lifecycleOwner, observer);
        data.postData(dummyValue);

        for (int i = 0; i < 10; ++i) {
            assertEquals(false, flag.get());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                fail("Thread exception");
            }
        }

        data.setData(dummyValue);
        assertEquals(false, flag.get());

        lifecycleOwner.setCurrentState(Lifecycle.State.RESUMED);
        data.setData(dummyValue);
        assertEquals(true, flag.get());
    }


}