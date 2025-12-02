package uns.saborea.registeracc;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;

import uns.saborea.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText editEmail, editPassword, editRepeatPassword;
    private MaterialButtonToggleGroup toggleGroupAccountType;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register); // Layout XML de registro

        // Vinculación de Vistas (Ajusta los IDs a tu XML)
        editEmail = findViewById(R.id.register_email);
        editPassword = findViewById(R.id.register_password);
        editRepeatPassword = findViewById(R.id.register_password_repeat);
        // Vinculación del ToggleGroup
        toggleGroupAccountType = findViewById(R.id.toggle_group_register);
        buttonRegister = findViewById(R.id.button_register_continue);

        // Listener para el botón principal
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia el flujo de registro multi-paso
                startMultiStepRegistration();
            }
        });
    }

    private void startMultiStepRegistration() {
        // Recoleccion y validación
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();
        String repeatPassword = editRepeatPassword.getText().toString();

        // Determina el tipo de cuenta seleccionado
        String tipoCuenta = getSelectedAccountType();

        // 1. Validaciones
        if (email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(repeatPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tipoCuenta == null) {
            Toast.makeText(this, "Selecciona un tipo de cuenta (Negocio o Cliente).", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Navegación al Siguiente Paso (Basado en la elección)
        Intent intent;
        Class<?> nextActivity;

        if (tipoCuenta.equals("negocio")) {
            nextActivity = RegisterActivityBusinessman.class;
        } else {
            nextActivity = RegisterActivityClient.class;
        }

        intent = new Intent(RegisterActivity.this, nextActivity);

        // 3. Pasar los datos básicos a la siguiente Activity
        // CRUCIAL: Pasamos la info de la tabla 'usuarios' a la siguiente pantalla
        intent.putExtra("EMAIL", email);
        intent.putExtra("PASSWORD", password);
        intent.putExtra("TIPO_CUENTA", tipoCuenta);

        startActivity(intent);
    }

    // Metodo para obtener el valor del MaterialButtonToggleGroup
    private String getSelectedAccountType() {
        int selectedId = toggleGroupAccountType.getCheckedButtonId();

        if (selectedId == View.NO_ID) {
            return null;
        }

        if (selectedId == R.id.button_negocio) {
            return "negocio";
        } else if (selectedId == R.id.button_cliente) {
            return "cliente";
        }
        return null;
    }
}