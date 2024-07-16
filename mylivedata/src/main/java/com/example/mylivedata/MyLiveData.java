package com.example.mylivedata;

import androidx.annotation.MainThread;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

public class MyLiveData<T> extends Object {
    private T data;
    private List<Observer<T>> observers;


    public MyLiveData(T data) {
        this.data = data;
        observers = new ArrayList<>();
    }

    public void setData(T data) {
        this.data = data;
        for (Observer<T> observer : observers) {
            observer.onChanged(this.data);
        }
    }

    public T getData() {
        return data;
    }

    void observe(Observer<T> observer) {
        observers.add(observer);
    }
}
