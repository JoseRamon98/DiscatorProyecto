package com.proyecto.discator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.proyecto.discator.Adaptadores.AlbumAdaptador;
import com.proyecto.discator.bean.Album;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ListaActivity extends AppCompatActivity {
    private FirebaseFirestore basedatos;
    private CollectionReference coleccionListas;
    private CollectionReference coleccionArtistas;
    private DocumentReference documento;

    private ArrayList<Album> arrayAlbumes;
    private AlbumAdaptador adaptadorAlbumes;
    private ArrayList<String> albumes;

    private ArrayList<String> discos;
    private ArrayList<String> discosVacia;

    private EditText textoAlbumView;
    private String nombreAlbum;
    private String usuario;
    private String textoAlbum;
    private String nombreLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        basedatos = FirebaseFirestore.getInstance();
        usuario = getIntent().getStringExtra("Correo");
        nombreLista = getIntent().getStringExtra("Nombre");
        coleccionListas = basedatos.collection("Usuarios").document(usuario).collection("Listas"); //nombre de la coleccion
        documento = coleccionListas.document(nombreLista);
        documento.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot doc) {

                albumes = (ArrayList<String>) doc.get("Album");
                coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion
                coleccionArtistas.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        arrayAlbumes = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            adaptadorAlbumes = new AlbumAdaptador(ListaActivity.this, arrayAlbumes);
                            final QueryDocumentSnapshot document1 = document;
                            CollectionReference coleccionAlbumes = coleccionArtistas.document(document.getId()).collection("Albumes");
                            if (albumes != null) {
                                for (int i = 0; i < albumes.size(); i++) {
                                    DocumentReference docIdRef = coleccionAlbumes.document(albumes.get(i));
                                    docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            adaptadorAlbumes = new AlbumAdaptador(ListaActivity.this, arrayAlbumes);
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Album album = new Album();
                                                    album.setNombreAlbum(document.getId());
                                                    album.setGenero((String) document.get("Genero"));
                                                    album.setAño((String) document.get("Año"));
                                                    album.setImagen((String) document.get("Imagen"));
                                                    album.setNombre(document1.getId());
                                                    arrayAlbumes.add(album);
                                                    adaptadorAlbumes.notifyDataSetChanged();
                                                    ListView listadoAlbumes = findViewById(R.id.listaAlbumes);
                                                    listadoAlbumes.setAdapter(adaptadorAlbumes);
                                                }
                                            }
                                            else
                                                textoAlbumView.setError("El album no existe");
                                        }
                                    });
                                }
                            }
                        }
                    }
                });

                Button botonPrivacidad=findViewById(R.id.botonPrivacidad);
                Button botonAñadirALista = findViewById(R.id.botonAñadirALista);
                textoAlbumView = findViewById(R.id.textoAlbum);
                final Switch switchPrivada = findViewById(R.id.switch1);
                if (!usuario.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    botonAñadirALista.setVisibility(View.GONE);
                    textoAlbumView.setVisibility(View.GONE);
                    switchPrivada.setVisibility(View.GONE);
                    botonPrivacidad.setVisibility(View.GONE);
                }
                botonAñadirALista.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textoAlbum = textoAlbumView.getText().toString();
                        if (textoAlbum.length() != 0) {
                            DocumentReference docIdRef = coleccionListas.document(nombreLista);
                            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        discos = (ArrayList) document.get("Album");
                                        discosVacia = new ArrayList<>();
                                        if (document.exists()) {
                                            if (document.get("Album") != null) {
                                                coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion
                                                coleccionArtistas.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                        if (e != null) {
                                                            return;
                                                        }
                                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                            CollectionReference coleccionAlbumes = coleccionArtistas.document(document.getId()).collection("Albumes");
                                                            DocumentReference docIdRef = coleccionAlbumes.document(textoAlbum);
                                                            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot document = task.getResult();
                                                                        if (document.exists()) {
                                                                            discos.add(textoAlbum);
                                                                            coleccionListas.document(nombreLista).update("Album", discos);
                                                                            finish();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            } else {
                                                coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion
                                                coleccionArtistas.addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                                        if (e != null) {
                                                            return;
                                                        }
                                                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                                            CollectionReference coleccionAlbumes = coleccionArtistas.document(document.getId()).collection("Albumes");
                                                            DocumentReference docIdRef = coleccionAlbumes.document(textoAlbum);
                                                            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        DocumentSnapshot document = task.getResult();
                                                                        if (document.exists()) {
                                                                            discosVacia.add(textoAlbum);
                                                                            coleccionListas.document(nombreLista).update("Album", discosVacia);
                                                                            finish();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            textoAlbumView.setError("Error al añadir el album");
                                        }
                                    } else {
                                        textoAlbumView.setError("Error al añadir el album");
                                    }
                                }
                            });
                        } else
                            textoAlbumView.setError("La lista no puede estar vacia");
                    }
                });
                String tipo = (String) doc.get("Tipo");
                if (tipo.equals("publica"))
                    switchPrivada.setChecked(true);
                else
                    switchPrivada.setChecked(false);

                botonPrivacidad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (switchPrivada.isChecked())
                            documento.update("Tipo", "publica");
                        else
                            documento.update("Tipo", "privada");
                        finish();
                    }
                });
            }
        });
    }
}