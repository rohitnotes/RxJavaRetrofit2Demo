package com.xpf.rxjavaretrofit2demo.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.orhanobut.logger.Logger;
import com.xpf.rxjavaretrofit2demo.R;
import com.xpf.rxjavaretrofit2demo.api.GithubService;
import com.xpf.rxjavaretrofit2demo.api.RequestUrl;
import com.xpf.rxjavaretrofit2demo.utils.ToastUtil;
import com.xpf.rxjavaretrofit2demo.view.XDialog;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by x-sir on 2016-12-21 :)
 * Function:SimpleRetrofit(没有经过封装的用法)
 * {@link # https://github.com/xinpengfei520/RxJavaRetrofit2Demo}
 */
public class SimpleRetrofit extends AppCompatActivity {

    private TextView textView;
    private ProgressDialog progressDialog;
    private static final String TAG = SimpleRetrofit.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_retrofit);
        textView = findViewById(R.id.textView);
        progressDialog = XDialog.create(this);

        Intent intent = getIntent();
        String account = intent.getStringExtra("account");
        if (!TextUtils.isEmpty(account)) {
            progressDialog.show();
            simpleRetrofit(account);
        }
    }

    private void simpleRetrofit(String account) {
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        Retrofit.Builder builder = new Retrofit
                        .Builder()
                        .baseUrl(RequestUrl.BASE_URL);
        Retrofit retrofit = builder.client(httpClient.build()).build();
        GithubService githubService = retrofit.create(GithubService.class);
        Call<ResponseBody> call = githubService.getUserString(account);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                try {
                    String json = response.body().string();
                    parseJson(json);
                } catch (Exception e) {
                    e.printStackTrace();
                    ToastUtil.showShort("not find such user!");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Logger.t(TAG).e(t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void parseJson(String json) {
        if (!TextUtils.isEmpty(json)) {
            Logger.t(TAG).json(json);
            textView.setText(json);
        }
    }
}
