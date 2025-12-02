package uns.saborea;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import uns.saborea.business_activities.BusinessDashboardActivity;
import uns.saborea.clienta_ctivities.ClientHomeActivity;
import uns.saborea.networking.ApiService;
import uns.saborea.networking.ModelResponse;
import uns.saborea.networking.RetrofitClient;
import uns.saborea.registeracc.RegisterActivity;

public class LoginActivityMain extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button buttonLogin, buttonRegister;

    // Nombres para SharedPreferences
    private static final String PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_ACCOUNT_TYPE = "accountType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // TEXTO: "¿No tienes una cuenta? Regístrate"
        TextView textRegistro = findViewById(R.id.textRegistro);
        if (textRegistro != null) {

            String part1 = "¿No tienes una cuenta? ";
            String part2 = "Regístrate";
            String full = part1 + part2;

            SpannableString ss = new SpannableString(full);

            // Color de "¿No tienes una cuenta?" (gris)
            ss.setSpan(new ForegroundColorSpan(Color.parseColor("#777777")),
                    0, part1.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // CLICK + apariencia de "Regístrate" (NEGRO)
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(LoginActivityMain.this, RegisterActivity.class);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.BLACK);    // COLOR NEGRO
                    ds.setUnderlineText(false);   // sin subrayado
                }
            };

            int start = part1.length();
            int end = start + part2.length();

            ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Aplicación de texto interactivo de ¿No tienes una cuenta? Registrate
            textRegistro.setText(ss);
            textRegistro.setMovementMethod(LinkMovementMethod.getInstance());
            textRegistro.setHighlightColor(Color.TRANSPARENT);
        }

        // 1. Vincular Vistas (Ajusta los IDs a tu XML de Login)
        editEmail = findViewById(R.id.emailInput);
        editPassword = findViewById(R.id.passwordInput);
        buttonLogin = findViewById(R.id.buttonLogin); // ID del botón "Iniciar sesión"

        // 2. Comprobar si ya hay una sesión guardada antes de mostrar la pantalla de login
        // checkExistingSession();

        // 3. Listener para el Login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    // Metodo que gestiona la recolección de datos y la llamada a la API
    private void attemptLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa tu email y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }
        sendLoginRequest(email, password);
    }

    private void sendLoginRequest(String email, String password) {

        // Crear el mapa de credenciales para enviar al login.php
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        // Obtener la instancia de la API y ejecutar la petición
        ApiService apiService = RetrofitClient.getApiservice();
        Call<ModelResponse> call = apiService.loginUser(credentials);

        call.enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    // CÓDIGO 200 (OK) - Login Exitoso
                    ModelResponse body = response.body();

                    // 1. Guardar la ID y el rol del usuario para mantener la sesión
                    saveUserSession(body.getUserId(), body.getAccountType());

                    Toast.makeText(LoginActivityMain.this, "✅ " + body.getMessage(), Toast.LENGTH_LONG).show();

                    // 2. Navegar a la pantalla principal (Home)
                    navigateToHome(body.getAccountType());

                } else if (response.code() == 401) {
                    // CÓDIGO 401 (Unauthorized) - Email o contraseña incorrectos
                    Toast.makeText(LoginActivityMain.this, "Credenciales inválidas. Intenta de nuevo.", Toast.LENGTH_LONG).show();

                } else {
                    // Otros errores (500, etc.)
                    Toast.makeText(LoginActivityMain.this, "Error al iniciar sesión: CODIGO " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override // <-- Metodo 'onFailure' ahora completo para resolver el error
            public void onFailure(Call<ModelResponse> call, Throwable t) {
                // Fallo de red (Revisar SSL/Conexión/VPS)
                Toast.makeText(LoginActivityMain.this, "Fallo de Red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    // ========================================================================
    // MÉTODOS DE GESTIÓN DE SESIÓN
    // ========================================================================

    /** Guarda la ID del usuario y el tipo de cuenta en SharedPreferences. */
    private void saveUserSession(int userId, String accountType) {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_ACCOUNT_TYPE, accountType);

        editor.apply(); // Aplica los cambios de forma asíncrona
        Toast.makeText(this, "Sesión guardada para ID: " + userId, Toast.LENGTH_SHORT).show();
    }

    /** Navega a la pantalla principal segun el tipo de cuenta.
     * Esta funcion reemplaza t0do de navegación.*/
    private void navigateToHome(String accountType) {
        Class<?> destinationActivity;

        // Activity de inicio para cada tipo de cuenta
        if (accountType.equals("negocio")) {
            destinationActivity = BusinessDashboardActivity.class;
        } else if (accountType.equals("cliente")) {
            destinationActivity = ClientHomeActivity.class;
        } else {
            Toast.makeText(this, "Tipo de cuenta no reconocido.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(LoginActivityMain.this, destinationActivity);
        startActivity(intent);
        finish(); // Cierra la Activity de Login para que el usuario no pueda volver con el boton "Atrás"
    }

    // Muestra como verificar la sesion al iniciar la App (no usado todavia)
    private void checkExistingSession() {
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String accountType = sharedPref.getString(KEY_ACCOUNT_TYPE, null);

        if (accountType != null) {
            Toast.makeText(this, "Sesión activa, navegando como " + accountType, Toast.LENGTH_SHORT).show();
            navigateToHome(accountType);
        }
    }

    /** Metodo ESTÁTICO para cerrar sesión desde cualquier Activity. */
    public static void logoutUser(Context context) {
        // Accede a SharedPreferences y limpia los datos
        SharedPreferences sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.remove(KEY_USER_ID);
        editor.remove(KEY_ACCOUNT_TYPE);
        editor.apply(); // Limpia los datos

        // Navega de vuelta a la pantalla de Login y limpia el stack de actividades
        Intent intent = new Intent(context, LoginActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
