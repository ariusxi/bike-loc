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
    private EditText editTextCadastroConfirmSenha;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Fields of the Signin form
        editTextCadastroUsername = (EditText) findViewById(R.id.editTextCadastroUsername);
        editTextCadastroEmail = (EditText) findViewById(R.id.editTextCadastroEmail);
        editTextCadastroSenha = (EditText) findViewById(R.id.editTextCadastroSenha);
        editTextCadastroConfirmSenha = (EditText) findViewById(R.id.editTextCadastroConfirmSenha);

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
        String email = editTextCadastroEmail.getEditableText().toString();
        String password = editTextCadastroSenha.getEditableText().toString();
        String confirmPassword = editTextCadastroConfirmSenha.getEditableText().toString();

        // Verificando se os campos obrigatórios foram preenchidos
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Você deve preencher todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validando se as senhas estão acima de 6 dígitos
        if (password.length() < 6 || confirmPassword.length() < 6) {
            Toast.makeText(this, "As senhas devem ser maior que 6 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validando se a senha e a confirmação de senha estão iguais
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "A confirmação de senha e a senha devem estar iguais", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cadastrando usuário no firebase
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener((result) -> {
            Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener((error) -> {
            Toast.makeText(this, "Ocorreu um erro ao cadastrar usuário", Toast.LENGTH_SHORT).show();
        });
    }
}