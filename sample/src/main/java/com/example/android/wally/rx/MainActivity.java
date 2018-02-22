package com.example.android.wally.rx;

import android.nfc.Tag;
import android.support.v4.util.LogWriter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();
    private Button baseUseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        baseUseBtn = findViewById(R.id.baseUse);
        Button useConsumerBtn = findViewById(R.id.useConsumer);
        baseUseBtn.setOnClickListener(this);
        useConsumerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.baseUse:
                baseUser();
                break;
            case R.id.useConsumer:
                useConsumer();
                break;
        }
    }

    private void baseUser() {
        //创建可观察者，作为事件源，发送时间给观察者
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        })
                .subscribe(new Observer<Integer>() {
                    private Disposable mDisposable;

                    //创建观察者，接受事件
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe start");
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, String.valueOf(value));
                        if (value == 2) {
                            if (!mDisposable.isDisposed()) {
                                mDisposable.dispose();
                                Log.d(TAG, "切断了");
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });
    }

    private void useConsumer() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(11);
                e.onNext(12);
                e.onNext(13);
                e.onNext(14);
                //发送了完成事件后，观察者将不再接受事件，即使事件源继续发送onNext事件，观察者也不再接收
                //e.onComplete();
                e.onNext(10086);
            }
            //注册有多个重载方法，将Consumer对象防止对应的位置，则切换接收方法
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                //Consumer只接受onNext
                Log.d(TAG, "Consumer accept".concat(String.valueOf(integer)));
            }
        });
    }
}