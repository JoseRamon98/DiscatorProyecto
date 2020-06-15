package com.proyecto.discator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout layoutDiscator=findViewById(R.id.discatorLayout);
        layoutDiscator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(MainActivity.this, Main2Activity.class); //Cuando pulsamos en la pantalla entramos en la aplicación
                startActivity(intencion);
            }
        });

        goTo();
    }


    public synchronized FirebaseAuth getFirebaseAuth() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

    private void goTo()
    {
        if (getFirebaseAuth().getCurrentUser()!=null)
        {
            Intent intencion = new Intent(this, Main2Activity.class); //Vamos a la pantala inicial de la aplicación
            startActivity(intencion);
        }
        else{
            Intent intencion = new Intent(this, LoginActivity.class); //Si no estamos registrados vamos a la ventana de registro/iicio de sesión
            startActivity(intencion);
        }
    }
}