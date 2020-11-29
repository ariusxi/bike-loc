package br.com.felix.bikeloc.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import br.com.felix.bikeloc.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextCadastroUsername;
    private EditText editTextCadastroEmail;
    private EditText editTextCadastroSenha;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editTextCadastroUsername = findViewById(R.id.editTextCadastroUsername);
        editTextCadastroEmail = findViewById(R.id.editTextCadastroEmail);
        editTextCadastroSenha = findViewById(R.id.editTextCadastroSenha);
        auth = FirebaseAuth.getInstance();

        findViewById(R.id.imageBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();

            }
        });

        findViewById(R.id.textFacaLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });
    }

    public void signUp(View view) {
        //String username = editTextCadastroUsername.getEditableText().toString();
        String email = editTextCadastroEmail.getEditableText().toString();
        String senha = editTextCadastroSenha.getEditableText().toString();
        auth.createUserWithEmailAndPassword(email, senha).addOnSuccessListener((result) -> {
                Toast.makeText(this, result.getUser().getEmail(), Toast.LENGTH_SHORT).show();
                finish();

        }).addOnFailureListener((error) -> error.printStackTrace());
    }
}