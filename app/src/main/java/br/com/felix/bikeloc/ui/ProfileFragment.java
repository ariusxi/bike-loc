package br.com.felix.bikeloc.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.felix.bikeloc.R;
import br.com.felix.bikeloc.auth.Session;

public class ProfileFragment extends Fragment {

    private Session session;

    private TextView profileEditText;
    private TextView logoutEditText;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new Session(getActivity());

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializando os text views da tela
        profileEditText = (TextView) getActivity().findViewById(R.id.profileEditText);
        logoutEditText = (TextView) getActivity().findViewById(R.id.logoutEditText);

        // Definindo o texto do usuário na aba de perfil
        profileEditText.setText(session.get("user_name"));

        logoutEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginFormIntent = new Intent(getActivity(), LoginFormActivity.class);
                startActivity(loginFormIntent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}