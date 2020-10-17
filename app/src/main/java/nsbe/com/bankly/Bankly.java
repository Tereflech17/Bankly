package nsbe.com.bankly;


import android.util.Log;

import nsbe.com.bankly.service.CustomerService;
import nsbe.com.bankly.service.UPCService;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Charlton on 11/10/17.
 */

public class Bankly {

    private UPCService upc;
    private CustomerService service;

    public static UPCService getUpc() {
        return getInstance().upc;
    }

    public static CustomerService getService() {
        return getInstance().service;
    }

    private static final Bankly ourInstance = new Bankly();

    public static Bankly getInstance() {
        return ourInstance;
    }

    private Bankly() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> Log.e(HttpLoggingInterceptor.class.getName(), String.format("%s: %s", HttpLoggingInterceptor.class.getName(), message)));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder request = original.newBuilder();
                    HttpUrl url = original.url().newBuilder()
                            .addQueryParameter("key", BuildConfig.API_KEY)
                            .build();
                    request.method(original.method(), original.body());
                    request.url(url);
                    return chain.proceed(request.build());
                })
                .addInterceptor(interceptor)
                .build();
        service = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://api.reimaginebanking.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(CustomerService.class);
        upc = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://mysql.yoprice.co:8000")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(UPCService.class);

    }
}
