package com.xpf.rxjavaretrofit2demo.eventbus;

import com.jakewharton.rxrelay2.PublishRelay;
import com.jakewharton.rxrelay2.Relay;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by x-sir on 2019-06-08 :)
 * Function:静态内部类实现单例，保证线程安全
 */
public class RxBus {

    private Relay<Object> mBus;
    private final Map<Class<?>, Object> mStickyEventMap;

    private RxBus() {
        mBus = PublishRelay.create().toSerialized();
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }

    public static RxBus get() {
        return Holder.BUS;
    }

    public void post(Object object) {
        mBus.accept(object);
    }

    /**
     * post sticky event.
     *
     * @param event event object.
     */
    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        mBus.accept(event);
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }

    public Observable<Object> toObservable() {
        return mBus;
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    public <T> Observable<T> toObservableSticky(final Class<T> eventType) {
        synchronized (mStickyEventMap) {
            Observable<T> observable = mBus.ofType(eventType);
            final Object event = mStickyEventMap.get(eventType);

            if (event != null) {
                return observable.mergeWith(Observable.create(e -> e.onNext(eventType.cast(event))));
            } else {
                return observable;
            }
        }
    }

    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext, Consumer onError,
                                   Action onComplete, Consumer onSubscribe) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext, onError, onComplete, onSubscribe);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext, Consumer onError,
                                   Action onComplete) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext, onError, onComplete);
    }

    public <T> Disposable register(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext, Consumer onError) {
        return toObservable(eventType).observeOn(scheduler).subscribe(onNext, onError);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext) {
        return toObservable(eventType).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext, Consumer onError,
                                   Action onComplete, Consumer onSubscribe) {
        return toObservable(eventType).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError, onComplete, onSubscribe);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext, Consumer onError,
                                   Action onComplete) {
        return toObservable(eventType).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError, onComplete);
    }

    public <T> Disposable register(Class<T> eventType, Consumer<T> onNext, Consumer onError) {
        return toObservable(eventType).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError);
    }

    public <T> Disposable registerSticky(Class<T> eventType, Scheduler scheduler, Consumer<T> onNext) {
        return toObservableSticky(eventType).observeOn(scheduler).subscribe(onNext);
    }

    public <T> Disposable registerSticky(Class<T> eventType, Consumer<T> onNext) {
        return toObservableSticky(eventType).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext);
    }

    public <T> Disposable registerSticky(Class<T> eventType, Consumer<T> onNext, Consumer onError) {
        return toObservableSticky(eventType).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError);
    }

    /**
     * 移除指定 eventType 的 Sticky 事件
     */
    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.remove(eventType));
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }

    public void unregister(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}