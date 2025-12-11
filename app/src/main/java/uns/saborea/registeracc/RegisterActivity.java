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

import com.google.android.material.button.MaterialButtonToggleGroup;

import uns.saborea.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText editEmail, editPassword, editRepeatPassword;
    private MaterialButtonToggleGroup toggleGroupAccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editEmail = findViewById(R.id.register_email);
        editPassword = findViewById(R.id.register_password);
        editRepeatPassword = findViewById(R.id.register_passwordconfirm);
        toggleGroupAccountType = findViewById(R.id.toggle_group_register);

        // BOTON CONTINUAR
        Button buttonRegister = findViewById(R.id.button_register_continue);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMultiStepRegistration();
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

    /* Metodo preventivo de llenado de campos */
    private void startMultiStepRegistration() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();
        String repeatPassword = editRepeatPassword.getText().toString();

        String tipoCuenta = getSelectedAccountType();

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

        Intent intent;
        Class<?> nextActivity;

        if (tipoCuenta.equals("negocio")) {
            nextActivity = RegisterActivityBusinessman.class;
        } else {
            nextActivity = RegisterActivityClient.class;
        }

        intent = new Intent(RegisterActivity.this, nextActivity);

        intent.putExtra("EMAIL", email);
        intent.putExtra("PASSWORD", password);
        intent.putExtra("TIPO_CUENTA", tipoCuenta);

        startActivity(intent);
    }

    /* Metodo para asignar texto del tipo de cuenta */
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
