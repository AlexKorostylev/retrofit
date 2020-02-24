package com.retrofit.webcloaca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.appsflyer.AppsFlyerLib;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    // Адресс для обращения на наш сервис
    // http://185.178.47.229/API/and/?project=com.site.getpost&country=ru&apid=111111&gaid=2222222&deep=9999999

    static  String link = "API/and/?project=com.site.getpost&country=ru&apid=111111&gaid=2222222&deep=9999999";
    String requestURL = "http://185.178.47.229/";
    String webviewURL = null;
    TextView return_link;
    CloacaInterface requestLink;
    String appsFlyerId;
    public static String PACKAGE_NAME;
    public static String COUNTRY_NAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Имя пакета
        COUNTRY_NAME = Locale.getDefault().getISO3Country();
        // Имя страны
        PACKAGE_NAME = getApplicationContext().getPackageName();
        // APID
        appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(this);

        String apid = requestURL;

        TextView request_link = findViewById(R.id.request_link);
        request_link.setText(apid);

        return_link = findViewById(R.id.return_link);



        initRetorfit();

        // Выполнение запроса и обработка ответа
    }

    // Построение запроса к серверу
    private void initRetorfit() {
        //Инициализация библиотеки retrofit
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                //Базовая часть адреса
                .baseUrl(requestURL)
                //Конвертер, необходимый для преобразования JSON в объекты
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON в объекты
                .build();
        //Создаем объект, при помощи которого будем выполнять запросы
        CloacaInterface requestLink = retrofit.create(CloacaInterface.class);
        Call<LinkModel> call = requestLink.loadLink(
                "com.site.getpost",
                "ru",
                "111111",
                "2222222",
                "9999999");
        try {
            requestRetrofit(call);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void requestRetrofit(Call<LinkModel> call) throws IOException {
            call.enqueue(new Callback<LinkModel>() {
                @Override
                public void onResponse(Call<LinkModel> call, Response<LinkModel> response) {
                    if (response.body() != null) {
                        String url = "here will be url:    ";
                        return_link.setText(response.body().getLink());
                    }
                }
                @Override
                public void onFailure(Call<LinkModel> call, Throwable t) {
                    Log.d("", "");
                }
            });
        }


}
