package uns.saborea.networking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // Usar la IP de la VPS con un dominio con HTTPS
    // (IMPORTANTE) Cuando uses la IP, necesitas un certificado SSL válido
    private static final String BASE_URL = "https://root.saboreapp.online/";
    private static Retrofit retrofit;

    public static ApiService getApiservice() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
