package uns.saborea.registeracc;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import uns.saborea.R;
import uns.saborea.LoginActivityMain;
import uns.saborea.networking.ApiService;
import uns.saborea.networking.RetrofitClient;
import uns.saborea.networking.ModelResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivityBusinessman extends AppCompatActivity {

    private EditText editNombreNegocio, editDireccion;
    private Button buttonFinalRegister;
    private String basicEmail, basicPassword, basicAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_businessman);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // [1] OBTENCION DE DATOS DEL INTENT
        Intent intent = getIntent();
        if (intent != null) {
            basicEmail = intent.getStringExtra("EMAIL");
            basicPassword = intent.getStringExtra("PASSWORD");
            basicAccountType = intent.getStringExtra("TIPO_CUENTA");
        }

        // VALIDACION: Solo por si faltan datos previamente no llenados
        if (basicEmail == null || basicPassword == null || !basicAccountType.equals("negocio")) {
            Toast.makeText(this, "Error: Faltan datos iniciales.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // [2] Vinculación de Vistas del Negocio (Ajusta los IDs)
        editNombreNegocio = findViewById(R.id.bus_name);
        editDireccion = findViewById(R.id.bus_address);
        buttonFinalRegister = findViewById(R.id.buttonRegisterBusinessman);

        // [3] Listener Final: Se envia a la API
        buttonFinalRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptFinalRegistration();
            }
        });

        // BOTON PARA ATRAS
        ImageButton buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Metodo para verificar llenado y mandarlos a la API
    private void attemptFinalRegistration() {

        String nombreNegocio = editNombreNegocio.getText().toString().trim();
        String direccion = editDireccion.getText().toString().trim();

        // Validacion de campos
        if (nombreNegocio.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Completa el nombre y la dirección del negocio.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llama a la funcion de envio con TODOS los datos
        sendRegistrationRequest(nombreNegocio, direccion);
    }

    // Metodo de comunicacion con la API
    private void sendRegistrationRequest(String nombreNegocio, String direccion) {

        // Mapeo de toda la informacion recolectada para ser enviadas
        Map<String, String> userData = new HashMap<>();

        // // Datos (Tabla 'usuarios')
        userData.put("email", basicEmail);
        userData.put("password", basicPassword);
        userData.put("tipo_cuenta", basicAccountType);

        // Datos (Tabla 'negocio')
        userData.put("nombre_negocio", nombreNegocio);
        userData.put("direccion", direccion);
        // maps_place_id se puede dejar vacío o manejarlo en un paso futuro

        // Llama al servicio de Retrofit
        ApiService apiService = RetrofitClient.getApiservice();
        Call<ModelResponse> call = apiService.registerUser(userData);

        // Ejecutar la petición
        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    // Registro exitoso: Manda a loguearse
                    Toast.makeText(RegisterActivityBusinessman.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivityBusinessman.this, LoginActivityMain.class));
                    finish(); // Cierra esta Activity
                } else if (response.code() == 409) {
                    // CÓDIGO 409 (Conflict)
                    Toast.makeText(RegisterActivityBusinessman.this, "El email o nombre de negocio ya existe", Toast.LENGTH_LONG).show();
                } else {
                    // Otros errores del servidor (400, 500)
                    Toast.makeText(RegisterActivityBusinessman.this, "Error al registrarse: CODIGO " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelResponse> call, Throwable t) {
                // Fallo de red (Revisar SSL/Conexión)
                Toast.makeText(RegisterActivityBusinessman.this, "Fallo de Red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}