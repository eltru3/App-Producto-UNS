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
import android.widget.RadioGroup;
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

    private EditText editNombreCuenta;
    private RadioGroup editGenero;
    private Button buttonFinalRegister;
    private String basicEmail, basicPassword, basicAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_client);

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
        if (basicEmail == null || basicPassword == null || !basicAccountType.equals("cliente")) {
            Toast.makeText(this, "Error de flujo: Faltan datos iniciales.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // [2] Vinculación de Vistas del Cliente (Ajusta los IDs)
        editNombreCuenta = findViewById(R.id.cli_name);
        buttonFinalRegister = findViewById(R.id.buttonRegisterClient);
        editGenero = findViewById(R.id.radioGroupGender);

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
        String nombreCuenta = editNombreCuenta.getText().toString().trim();
        String basicGender = getSelectedGender();

        // Validación de campo
        if (nombreCuenta.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa un nombre de cuenta (usuario).", Toast.LENGTH_SHORT).show();
            return;
        }

        if (basicGender == null) {
            Toast.makeText(this, "Por favor, escoja un género", Toast.LENGTH_SHORT).show();
            return;
        }

        // Llama a la función de envío con TODOS los datos
        sendRegistrationRequest(nombreCuenta, basicGender);
    }

    // Metodo de comunicacion con la API
    private void sendRegistrationRequest(String nombreCuenta, String basicGender) {

        // Mapeo de toda la informacion recolectada para ser enviadas
        Map<String, String> userData = new HashMap<>();

        // Datos (Tabla 'usuarios')
        userData.put("email", basicEmail);
        userData.put("password", basicPassword);
        userData.put("tipo_cuenta", basicAccountType);

        // Datos (Tabla 'cliente')
        userData.put("genero", basicGender);
        userData.put("nombre_cuenta", nombreCuenta);

        // Llama al servicio de Retrofit
        ApiService apiService = RetrofitClient.getApiservice();
        Call<ModelResponse> call = apiService.registerUser(userData);

        // Ejecutar la petición
        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    // Registro exitoso: Manda a loguearse
                    Toast.makeText(RegisterActivityClient.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivityClient.this, LoginActivityMain.class));
                    finish();
                } else if (response.code() == 409) {
                    // CÓDIGO 409 (Conflict)
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