package com.example.mylivedata;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

public class MyLiveData<T> extends Object {
    private T _data;
    private List<Observer<T>> _observers;


    public MyLiveData(T data) {
        _data = data;
        _observers = new ArrayList<>();
    }

    @MainThread
    public void setData(T data) {
        _data = data;
        for (Observer<T> observer : _observers) {
            observer.onChanged(_data);
        }
    }

    public void postData(T data)
    {
        Looper mainLooper = Looper.getMainLooper();
        Handler mainHandler = new Handler(mainLooper);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                setData(data);
            }
        });
    }
    public T getData() {
        return _data;
    }

    void observe(Observer<T> observer) {
        _observers.add(observer);
    }
}
