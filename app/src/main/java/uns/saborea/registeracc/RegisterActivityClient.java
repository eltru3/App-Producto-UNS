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

public class RegisterActivityClient extends AppCompatActivity {

    // Componentes de la Interfaz
    private EditText editNombreCuenta; // Solo necesita el nombre de usuario
    private Button buttonFinalRegister;

    // Variables para almacenar datos básicos del paso 1
    private String basicEmail, basicPassword, basicAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asegúrate de que tienes un layout XML para esta pantalla (ej: activity_register_client.xml)
        setContentView(R.layout.activity_register_client);

        // 1. OBTENER DATOS DEL INTENT (Paso 1)
        Intent intent = getIntent();
        if (intent != null) {
            basicEmail = intent.getStringExtra("EMAIL");
            basicPassword = intent.getStringExtra("PASSWORD");
            basicAccountType = intent.getStringExtra("TIPO_CUENTA");
        }

        // Validación crítica: si faltan datos o no es tipo 'cliente', navega de vuelta
        if (basicEmail == null || basicPassword == null || !basicAccountType.equals("cliente")) {
            Toast.makeText(this, "Error de flujo: Faltan datos iniciales.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 2. Vinculación de Vistas del Cliente (Ajusta el ID del campo de nombre)
        editNombreCuenta = findViewById(R.id.cli_name);
        buttonFinalRegister = findViewById(R.id.buttonRegisterClient);

        // 3. Listener Final: Enviar t0do a la API
        buttonFinalRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptFinalRegistration();
            }
        });
    }

    private void attemptFinalRegistration() {
        // Recolección y limpieza del nombre de cuenta
        String nombreCuenta = editNombreCuenta.getText().toString().trim();

        // Validación de campo
        if (nombreCuenta.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un nombre de cuenta (usuario).", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llama a la función de envío con TODOS los datos
        sendRegistrationRequest(nombreCuenta);
    }

    private void sendRegistrationRequest(String nombreCuenta) {

        // 1. Crear el mapa de datos final (Contiene la información de ambas Activities)
        Map<String, String> userData = new HashMap<>();

        // Datos del paso 1 (Tabla 'usuarios')
        userData.put("email", basicEmail);
        userData.put("password", basicPassword);
        userData.put("tipo_cuenta", basicAccountType); // Debe ser "cliente"

        // Datos específicos del cliente (Tabla 'clientes')
        userData.put("nombre_cuenta", nombreCuenta); // Clave que espera register.php

        // 2. Llamar al servicio de Retrofit
        ApiService apiService = RetrofitClient.getApiservice();
        Call<ModelResponse> call = apiService.registerUser(userData);

        // 3. Ejecutar la petición
        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(RegisterActivityClient.this, "✅ " + response.body().getMessage(), Toast.LENGTH_LONG).show();

                    // Registro exitoso: Navega al Login
                    startActivity(new Intent(RegisterActivityClient.this, LoginActivityMain.class));
                    finish();
                } else if (response.code() == 409) {
                    // CÓDIGO 409 (Conflict) - Email o nombre ya registrado (lógica de tu PHP)
                    Toast.makeText(RegisterActivityClient.this, "El email o nombre de cuenta ya existe.", Toast.LENGTH_LONG).show();
                } else {
                    // Otros errores del servidor (400, 500)
                    Toast.makeText(RegisterActivityClient.this, "Error al registrar: CODIGO " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ModelResponse> call, Throwable t) {
                // Fallo de red (Revisar SSL/Conexión)
                Toast.makeText(RegisterActivityClient.this, "Fallo de RED: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getSelectedGender() {
        int selectedId = editGenero.getCheckedRadioButtonId();

        if (selectedId == View.NO_ID) {
            return null;
        }

        if (selectedId == R.id.Male) {
            return "M";
        } else if (selectedId == R.id.Female) {
            return "F";
        } else if (selectedId == R.id.nosay) {
            return "O";
        }
        return null;
    }
}