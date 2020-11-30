package br.com.felix.bikeloc.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.auth.Session;

public class LoginFormActivity extends AppCompatActivity {

    private Session session;

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

        session = new Session(this);

        findViewById(R.id.textCreateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
            }
        });
    }

    public void login(View view) {
        String email = editTextLoginEmail.getEditableText().toString();
        String password = editTextLoginSenha.getEditableText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Você deve informar login e senha", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password).
                addOnSuccessListener((result) -> {
                    // Salvando os dados do usuário na sessão
                    FirebaseUser user = result.getUser();
                    session.set("user_id", user.getUid());

                    startActivity(new Intent (this, MainActivity.class));
                }).
                addOnFailureListener((error) -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Ocorreu um erro ao autenticar o usuário", Toast.LENGTH_SHORT).show();
                });
    }
}
