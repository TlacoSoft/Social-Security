package com.example.maps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button btnRegistrarse;

    //VARIABLES DE LOS DATOS A REGISTRAR
    private String correo="";
    private String contra="";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        email = (EditText) findViewById(R.id.TxtEmail);
        password = (EditText) findViewById(R.id.TxtPassword);
        btnRegistrarse = (Button) findViewById(R.id.btnRegistrar);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = email.getText().toString();
                contra = password.getText().toString();

                if(!correo.isEmpty() && !contra.isEmpty()){
                    if(password.length() >= 8){
                        registrarUser();
                    }else{
                        Toast.makeText(FirebaseActivity.this, "Ingresa una contraseña mayor a 8 ", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(FirebaseActivity.this, "Complete los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void registrarUser(){
        mAuth.createUserWithEmailAndPassword(correo,contra).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user !=null){
                        //Toast.makeText(FirebaseActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(FirebaseActivity.this, MainActivity.class));
                        finish();
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(FirebaseActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }


                /*Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){
                        startActivity(new Intent(RegistrarseActivity.this, InicioActivity.class));
                        finish();
                    }else{
                        Toast.makeText(RegistrarseActivity.this, "Usuario no registrado", Toast.LENGTH_SHORT).show();
                    }
                }*/
        });
    }
}