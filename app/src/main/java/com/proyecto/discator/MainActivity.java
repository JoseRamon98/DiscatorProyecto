package com.proyecto.discator;

import android.content.Intent;
import android.os.Bundle;

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
            Intent intencion = new Intent(this, Main2Activity.class);
            intencion.putExtra("miParametro", "valorParametro"); //pasar un parametro a la siguiente actividad
            startActivity(intencion);
        }
        else{
            Intent intencion = new Intent(this, LoginActivity.class);
            intencion.putExtra("miParametro", "valorParametro"); //pasar un parametro a la siguiente actividad
            startActivity(intencion);
        }
    }
}