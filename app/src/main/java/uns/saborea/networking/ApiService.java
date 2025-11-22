package uns.saborea.networking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.Map;

public interface ApiService {

    // Se define la ruta a la URL base
    @POST("api/register.php")
    Call<ModelResponse> registerUser(@Body Map<String, String> userData);

    @POST("api/register.php")
    Call<ModelResponse> loginUser(@Body Map<String, String> userData);
}
