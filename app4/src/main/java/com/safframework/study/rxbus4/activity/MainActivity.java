package com.safframework.study.rxbus4.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding2.view.RxView;
import com.safframework.injectview.annotations.InjectView;
import com.safframework.log.L;
import com.safframework.study.rxbus4.R;
import com.safframework.study.rxbus4.app.BaseActivity;
import com.safframework.study.rxbus4.domain.CrossActivityEvent;
import com.safframework.study.rxbus4.domain.ExceptionEvent;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.text1)
    TextView text1;

    @InjectView(R.id.text2)
    TextView text2;

    @InjectView(R.id.text3)
    TextView text3;

    @InjectView(R.id.text4)
    TextView text4;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        registerEvents();
    }

    private void initViews() {

        RxView.clicks(text1)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {

                Intent i = new Intent(MainActivity.this,TestEventBusActivity.class);
                startActivity(i);
            }
        });

        RxView.clicks(text2)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {

                        Intent i = new Intent(MainActivity.this,TestCrossActivity.class);
                        startActivity(i);
                    }
                });

        RxView.clicks(text3)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {

                        Intent i = new Intent(MainActivity.this,TestExceptionActivity.class);
                        startActivity(i);
                    }
                });

        RxView.clicks(text4)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {

                        Intent i = new Intent(MainActivity.this,TestStickyActivity.class);
                        startActivity(i);
                    }
                });
    }

    private void registerEvents() {

        compositeDisposable.add(rxBus.register(CrossActivityEvent.class, AndroidSchedulers.mainThread(), new Consumer<CrossActivityEvent>() {
            @Override
            public void accept(@NonNull CrossActivityEvent event) throws Exception {
                Toast.makeText(MainActivity.this,"来自MainActivity的Toast",Toast.LENGTH_SHORT).show();
            }
        }));

        compositeDisposable.add(rxBus.register(ExceptionEvent.class, AndroidSchedulers.mainThread(), new Consumer<ExceptionEvent>() {
            @Override
            public void accept(@NonNull ExceptionEvent event) throws Exception {
                String str = null;
                System.out.println(str.substring(0));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {

                L.i(throwable.getMessage());
            }
        }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
