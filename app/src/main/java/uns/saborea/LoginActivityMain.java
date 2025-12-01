package uns.saborea;

import android.content.Intent;
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
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import uns.saborea.registeracc.RegisterActivity;

public class LoginActivityMain extends AppCompatActivity {

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
    }
}
