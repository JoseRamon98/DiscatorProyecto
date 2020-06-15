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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Main2Activity extends AppCompatActivity
{
    private FirebaseFirestore basedatos;
    private DocumentReference documento;

    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseUser user;
    private String correo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        user = FirebaseAuth.getInstance().getCurrentUser();
        correo = user.getEmail(); //Correo del usuario
        basedatos = FirebaseFirestore.getInstance();
        documento=basedatos.collection("Usuarios").document(correo); //Documento que hace referencia al nuevo usuario
        documento.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists())
                    {
                        Map<String, Object> nuevoUsuario = new HashMap<>(); //Creamos un map para añadir un nuevo usuario
                        nuevoUsuario.put("idUsuario", user.getUid());
                        nuevoUsuario.put("fotoUsuario", user.getPhotoUrl().toString());
                        basedatos.collection("Usuarios").document(correo).set(nuevoUsuario); //Si el usuaio se acaba de registrar, se crea un documento en la base de datos Usuario con sus datos
                    }
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_friend, R.id.nav_perfil,
                R.id.nav_ranking, R.id.nav_listas_globales, R.id.nav_listas)
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
        //Añadimos los items a la barra
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
        switch (item.getItemId()) //Obtenemos el identificador de el botón de arriba a la derecha
        {
            case R.id.cerrarSesion: //Si se pulsa sobre cerrar sesión entonces cerrarla
                 AuthUI.getInstance().signOut(Main2Activity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                     public void onComplete(@NonNull Task<Void> task) {
                         Intent intencion = new Intent(Main2Activity.this, LoginActivity.class); //Volvemos a la pantalla de iniciar sesión
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