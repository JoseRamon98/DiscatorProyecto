package com.proyecto.discator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main2Activity extends AppCompatActivity
{
    private FirebaseFirestore basedatos;
    private CollectionReference coleccionUsuarios;
    private DocumentReference documento;

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String correo = user.getEmail();
        basedatos = FirebaseFirestore.getInstance();
        if (basedatos.collection("Usuarios").document(correo)==null)
        {
            Map<String, Object> nuevoUsuario = new HashMap<>();
            nuevoUsuario.put("idUsuario", user.getUid());
            nuevoUsuario.put("fotoUsuario", user.getPhotoUrl().toString());
            basedatos.collection("Usuarios").document(correo).set(nuevoUsuario);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.cuentaGoogle);
        navUsername.setText(correo);
        String nombre = user.getDisplayName();
        TextView navUser = (TextView) headerView.findViewById(R.id.nombreGoogle);
        navUser.setText(nombre);
        String photoUrl = Objects.requireNonNull(user.getPhotoUrl()).toString();
        ImageView userImage = headerView.findViewById(R.id.imagenGoogle);
        Picasso.with(this).load(photoUrl).into(userImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.cerrarSesion:
                AuthUI.getInstance()
                        .signOut(Main2Activity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intencion = new Intent(Main2Activity.this, LoginActivity.class);
                                intencion.putExtra("miParametro", "valorParametro"); //pasar un parametro a la siguiente actividad
                                startActivity(intencion);
                                finish();
                            }
                        });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}