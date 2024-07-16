package com.example.mylivedata;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

public class MyLiveData<T> extends Object {
    private T _data;
    private List<Pair<LifecycleOwner, Observer<T>>> _observers;

    public MyLiveData() {
        _data = null;
        _observers = new ArrayList<>();
    }

    @MainThread
    public void setData(T data) {
        _data = data;
        for (Pair<LifecycleOwner, Observer<T>> ownerObserverPair : _observers) {
            if (ownerObserverPair.first == null) {
                ownerObserverPair.second.onChanged(_data);
                return;
            }

            Lifecycle.State state = ownerObserverPair.first.getLifecycle().getCurrentState();
            if (Lifecycle.State.STARTED == state || Lifecycle.State.RESUMED == state) {
                ownerObserverPair.second.onChanged(_data);
            }
        }
    }

    public void postData(T data) {
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

    @MainThread
    public void observe(Observer<T> observer) {
        _observers.add(new Pair<>(null, observer));
    }

    @MainThread
    public void observe(LifecycleOwner owner, Observer<T> observer) {
        _observers.add(new Pair<>(owner, observer));
        owner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onDestroy(owner);
                removeObserver(observer);

            }
        });
    }

    @MainThread
    public void removeObserver(Observer<T> observer){
        for (int i = 0; i < _observers.size(); i++) {
            if(_observers.get(i).second == observer)
            {
                if(_observers.get(i).first != null){

                }
                _observers.remove(i);
                return;
            }
        }
    }
}
