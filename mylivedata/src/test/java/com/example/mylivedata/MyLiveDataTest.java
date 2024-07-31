package com.example.mylivedata;

import org.junit.Test;

import static org.junit.Assert.*;

import androidx.lifecycle.Observer;

public class MyLiveDataTest {
    @Test
    public void CreatedAndSet() {
        final int checkValue = 13;
        MyLiveData<Integer> data = new MyLiveData<>();
        data.setData(checkValue);

        assertEquals(checkValue, data.getData().intValue());
    }

    @Test
    public void CreatedNull() {
        MyLiveData<Integer> data = new MyLiveData<>();

        assertNull(data.getData());
    }

    @Test
    public void ValueChanged() {
        final int checkValue = 13;
        final int changedValue = 50;

        MyLiveData<Integer> data = new MyLiveData<>();
        data.setData(checkValue);
        assertEquals(checkValue, data.getData().intValue());

        data.setData(changedValue);
        assertEquals(changedValue, data.getData().intValue());
    }


    @Test
    public void AddTwoTheSameObservers() {
        MyLiveData<Integer> data = new MyLiveData<>();
        Observer<Integer> observer = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

            }
        };

        data.observe(observer);
        assertThrows(IllegalArgumentException.class, () -> data.observe(observer));
    }

    @Test
    public void NullableObserver() {
        MyLiveData<Integer> data = new MyLiveData<>();

        data.observe(null);
    }

}
