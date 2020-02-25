package com.retrofit.webcloaca;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.appsflyer.AppsFlyerLib;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {



    private static final String TAG = MainActivity.class.getSimpleName();
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR=1;
    WebView mWebView;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(Build.VERSION.SDK_INT >= 21){
            Uri[] results = null;
            //Check if response is positive
            if(resultCode== Activity.RESULT_OK){
                if(requestCode == FCR){
                    if(null == mUMA){
                        return;
                    }
                    if(intent == null || intent.getData() == null){
                        //Capture Photo if no image available
                        if(mCM != null){
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    }else{
                        String dataString = intent.getDataString();
                        if(dataString != null){
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        }else{
            if(requestCode == FCR){
                if(null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide(); // Убирает заголовок

        uiView();  // Webview component

        //Определение наличия подключения к Интернету
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo !=null && networkInfo.isConnected()){
            if (savedInstanceState == null)
            {
                // Выполнение запроса и обработка ответа
                // Открытие ссылки по запросу
                //("http://185.178.47.229/API/and/?project=com.site.getpost12312&country=ru&apid=1111&gaid=2222222&deep=asdad");
                initRetorfit(getPackage (), getCountry(), getApid(), getGaid(), getDeep());
            }
        }else {
            mWebView.loadUrl("");
            networkAlert();
        }
    }

    // WebView
    private void uiView() {
        // Webview component
        mWebView = findViewById(R.id.web_link);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.getSettings().setLoadsImagesAutomatically(true);
        CookieManager.getInstance().setAcceptCookie(true);

        mWebView.getSettings().setAppCachePath(""+this.getCacheDir());
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        if(Build.VERSION.SDK_INT >= 21){
            mWebView.getSettings().setMixedContentMode(0);
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT >= 19){
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        mWebView.setWebViewClient(new MyWebViewClient());

        mWebView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
                //setSupportProgressBarIndeterminateVisibility((progress == 100)?false:true);
            }
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                CharSequence notavail = "Webpage not available";
            }

        });
    }

    // Retrofit. Инициализация
    private void initRetorfit( String package_name, String country, String apid,
                               String gaid, String deep) {
        //Инициализация библиотеки retrofit
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                //Базовая часть адреса
                .baseUrl("http://185.178.47.229/")
                //Конвертер, необходимый для преобразования JSON в объекты
                .addConverterFactory(GsonConverterFactory.create()) //Конвертер, необходимый для преобразования JSON в объекты
                .build();
        //Создаем объект, при помощи которого будем выполнять запросы
        CloacaInterface requestLink = retrofit.create(CloacaInterface.class);
        Call<LinkModel> call = requestLink.loadLink(package_name, country, apid, gaid, deep);
        try {
            requestRetrofit(call);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Retrofit. Построение запроса к серверу
    private void requestRetrofit(Call<LinkModel> call) throws IOException {
        call.enqueue(new Callback<LinkModel>() {
            @Override
            public void onResponse(Call<LinkModel> call, Response<LinkModel> response) {
                if (response.body() != null) {
                    mWebView.loadUrl(response.body().getLink());
                    String status = response.body().getStatus();
                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LinkModel> call, Throwable t) {
                Log.d("", "");
            }
        });
    }

    // Webview. Сохранение состояния перед поворотом экрана
    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        mWebView.saveState(outState);
    }

    // Webview. Восстановление состояния после поворота экрана
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        mWebView.restoreState(savedInstanceState);
    }

    // Webview. Возврат на предидущую страницу
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // Webview. Открытие ссылки в приложении
    private class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if( url.startsWith("http:") || url.startsWith("https:") ) {
                return false;
            }

            // Otherwise allow the OS to handle things like tel, mailto, etc.
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity( intent );
            return true;
        }
    }

    // Всплывающее окно - Отсутствие интернета
    public void networkAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Отсутствует сетевое подключение")
                .setMessage("Подключите интернет соединение и повторите попытку")
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // Имя пакета
    public String getPackage (){
        String PACKAGE = getApplicationContext().getPackageName().toLowerCase();
        //String PACKAGE = "com.site.getpost12312";
        Toast.makeText(getApplicationContext(), PACKAGE, Toast.LENGTH_SHORT).show();
        return PACKAGE;
    }

    // Имя страны
    public String getCountry(){
        String COUNTRY = Locale.getDefault().getCountry().toLowerCase();
        Toast.makeText(getApplicationContext(), COUNTRY, Toast.LENGTH_SHORT).show();
        return COUNTRY;
    }

    // APID
    public String getApid(){
        String APID = AppsFlyerLib.getInstance().getAppsFlyerUID(this);
        //String APID = "1111";
        Toast.makeText(getApplicationContext(), APID, Toast.LENGTH_SHORT).show();
        return APID;
    }

    // GAID
    public String getGaid(){
        String GAID = "2222222" ;
        return GAID;
    }

    // DEEP
    public String getDeep(){
        String DEEP = "asdad";
        return DEEP;
    }
}
