package uns.saborea.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // CONVERTIDOR A JSON
    private static final String BASE_URL = "https://root.saboreapp.online/";
    private static Retrofit retrofit;

    // Instancia de ApiService
    public static ApiService getApiservice() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } return retrofit.create(ApiService.class);
    }
}
