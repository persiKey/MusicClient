package com.example.mylivedata;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.List;

public class MyLiveData<T> extends Object {
    private T _data;

    class ObserverInfo {
        Observer<T> dataObserver = null;
        LifecycleOwner lifecycleOwner = null;
        LifecycleObserver lifecycleObserver = null;
    }

    private List<ObserverInfo> _observersInfoList;

    public MyLiveData() {
        _data = null;
        _observersInfoList = new ArrayList<>();
    }

    @MainThread
    public void setData(T data) {
        _data = data;
        for (ObserverInfo observerInfo : _observersInfoList) {
            if (observerInfo.lifecycleOwner == null) {
                observerInfo.dataObserver.onChanged(_data);
                continue;
            }

            Lifecycle.State state = observerInfo.lifecycleOwner.getLifecycle().getCurrentState();
            if (Lifecycle.State.STARTED == state || Lifecycle.State.RESUMED == state) {
                observerInfo.dataObserver.onChanged(_data);
                continue;
            } else if (Lifecycle.State.DESTROYED == state) {
                continue;
            }

            observerInfo.lifecycleOwner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
                @Override
                public void onResume(@NonNull LifecycleOwner owner) {
                    DefaultLifecycleObserver.super.onStart(owner);

                    Looper mainLooper = Looper.getMainLooper();
                    Handler mainHandler = new Handler(mainLooper);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            observerInfo.dataObserver.onChanged(_data);
                        }
                    });
                }
            });
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
        for (ObserverInfo observeInfo : _observersInfoList) {
            if (observeInfo.dataObserver == observer) {
                throw new IllegalArgumentException();
            }
        }
        ObserverInfo observerInfo = new ObserverInfo();
        observerInfo.dataObserver = observer;
        observerInfo.lifecycleOwner = null;
        observerInfo.lifecycleObserver = null;
        _observersInfoList.add(observerInfo);
    }

    @MainThread
    public void observe(LifecycleOwner owner, Observer<T> observer) {
        for (ObserverInfo observeInfo : _observersInfoList) {
            if (observeInfo.dataObserver == observer) {
                if (observeInfo.lifecycleOwner != owner) {
                    throw new IllegalArgumentException();
                }
            }
        }

        LifecycleObserver lifecycleObserver = new DefaultLifecycleObserver() {
            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                DefaultLifecycleObserver.super.onDestroy(owner);
                removeObserver(observer);
            }
        };

        ObserverInfo observerInfo = new ObserverInfo();
        observerInfo.dataObserver = observer;
        observerInfo.lifecycleOwner = owner;
        observerInfo.lifecycleObserver = lifecycleObserver;

        owner.getLifecycle().addObserver(lifecycleObserver);

        _observersInfoList.add(observerInfo);
    }

    @MainThread
    public void removeObserver(Observer<T> observer) {
        for (int i = 0; i < _observersInfoList.size(); i++) {
            if (_observersInfoList.get(i).dataObserver == observer) {
                ObserverInfo observerInfo = _observersInfoList.get(i);
                if (observerInfo.lifecycleOwner != null) {
                    observerInfo.lifecycleOwner.getLifecycle().removeObserver(observerInfo.lifecycleObserver);
                }
                _observersInfoList.remove(i);
                return;
            }
        }
    }
}
