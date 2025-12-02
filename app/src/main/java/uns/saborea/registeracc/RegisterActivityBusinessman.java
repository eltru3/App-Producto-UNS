package uns.saborea.registeracc;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    // Componentes de la Interfaz
    private EditText editNombreNegocio, editTipoNegocio, editDireccion;
    private Button buttonFinalRegister;

    // Variables para almacenar datos básicos del paso 1
    private String basicEmail, basicPassword, basicAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_businessman);

        // 1. OBTENER DATOS DEL INTENT (Paso 1)
        Intent intent = getIntent();
        if (intent != null) {
            basicEmail = intent.getStringExtra("EMAIL");
            basicPassword = intent.getStringExtra("PASSWORD");
            basicAccountType = intent.getStringExtra("TIPO_CUENTA");
        }

        // Validación crítica: si faltan datos, navega de vuelta
        if (basicEmail == null || basicPassword == null || !basicAccountType.equals("negocio")) {
            Toast.makeText(this, "Error de flujo: Faltan datos iniciales.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Vinculación de Vistas del Negocio (Ajusta los IDs)
        editNombreNegocio = findViewById(R.id.bus_name);
        editTipoNegocio = findViewById(R.id.bus_type);
        editDireccion = findViewById(R.id.bus_address);
        buttonFinalRegister = findViewById(R.id.buttonRegisterBusinessman);

        // 3. Listener Final: Enviar a la API
        buttonFinalRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptFinalRegistration();
            }
        });
    }

    private void attemptFinalRegistration() {
        // Recolección y limpieza de datos especificos
        String nombreNegocio = editNombreNegocio.getText().toString().trim();
        String tipoNegocio = editTipoNegocio.getText().toString().trim();
        String direccion = editDireccion.getText().toString().trim();

        // Validacion de campos del negocio
        if (nombreNegocio.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(this, "Completa el nombre y la dirección del negocio.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llama a la funcion de envio con TODOS los datos
        sendRegistrationRequest(nombreNegocio, tipoNegocio, direccion);
    }

    private void sendRegistrationRequest(String nombreNegocio, String tipoNegocio, String direccion) {

        // 1. Crear el mapa de datos final (Contiene la información de ambas Activities)
        Map<String, String> userData = new HashMap<>();

        // Datos del paso 1 (Tabla 'usuarios')
        userData.put("email", basicEmail);
        userData.put("password", basicPassword);
        userData.put("tipo_cuenta", basicAccountType);

        // Datos especificos del negocio
        userData.put("nombre_negocio", nombreNegocio);
        userData.put("tipo_negocio", tipoNegocio);
        userData.put("direccion", direccion);
        // maps_place_id se puede dejar vacío o manejarlo en un paso futuro

        // 2. Llamar al servicio de Retrofit
        ApiService apiService = RetrofitClient.getApiservice();
        Call<ModelResponse> call = apiService.registerUser(userData);

        // 3. Ejecutar la petición
        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivityBusinessman.this, "✅ " + response.body().getMessage(), Toast.LENGTH_LONG).show();

                    // Registro exitoso: Navega al Login
                    startActivity(new Intent(RegisterActivityBusinessman.this, LoginActivityMain.class));
                    finish(); // Cierra esta Activity
                } else if (response.code() == 409) {
                    // CÓDIGO 409 (Conflict) - Email o nombre ya registrado
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