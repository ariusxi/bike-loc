package br.com.felix.bikeloc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import br.com.felix.bikeloc.R;

public class LoginFormActivity extends AppCompatActivity {

    private EditText editTextLoginEmail;
    private EditText editTextLoginSenha;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        editTextLoginEmail = findViewById(R.id.editTextLoginEmail);
        editTextLoginSenha = findViewById(R.id.editTextLoginSenha);
        auth = FirebaseAuth.getInstance();

        findViewById(R.id.textCreateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });
    }

    public void login(View view) {
        String email = editTextLoginEmail.getEditableText().toString();
        String senha = editTextLoginSenha.getEditableText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Você deve informar login e senha", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, senha).
                addOnSuccessListener((result) -> {
                    startActivity(new Intent (this, MainActivity.class));
                }).
                addOnFailureListener((error) -> {
                    error.printStackTrace();
                });
    }
}
