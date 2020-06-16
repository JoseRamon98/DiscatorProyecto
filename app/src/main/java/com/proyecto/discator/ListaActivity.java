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
        usuario = getIntent().getStringExtra("Correo"); //Obtenemos el correo del creador de la lista
        nombreLista = getIntent().getStringExtra("Nombre"); //Obtenemos el nombre de la lista
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
                        arrayAlbumes = new ArrayList<>(); //Array de albumes
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            adaptadorAlbumes = new AlbumAdaptador(ListaActivity.this, arrayAlbumes);
                            final QueryDocumentSnapshot document1 = document; //Copia del documento para extraer su id
                            CollectionReference coleccionAlbumes = coleccionArtistas.document(document.getId()).collection("Albumes");
                            if (albumes != null) {
                                for (int i = 0; i < albumes.size(); i++) { //Recorremos el array de albumes
                                    DocumentReference docIdRef = coleccionAlbumes.document(albumes.get(i));
                                    docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            adaptadorAlbumes = new AlbumAdaptador(ListaActivity.this, arrayAlbumes);
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Album album = new Album();
                                                    album.setNombreAlbum(document.getId()); //Extraemos el nombre del album
                                                    album.setGenero((String) document.get("Genero")); //Extraemos el genero del album
                                                    album.setAño((String) document.get("Año")); //Extraemos el año del album
                                                    album.setImagen((String) document.get("Imagen")); //Extraemos la imagen del album
                                                    album.setNombre(document1.getId()); //Extraemos el nombre del artista
                                                    arrayAlbumes.add(album);
                                                    adaptadorAlbumes.notifyDataSetChanged();
                                                    ListView listadoAlbumes = findViewById(R.id.listaAlbumes);
                                                    listadoAlbumes.setAdapter(adaptadorAlbumes);
                                                }else
                                                    textoAlbumView.setError("Asegurate que has introducido el nombre del album correctamente");
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

                botonAñadirALista.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //Acción de añadir un album a la lista
                        textoAlbum = textoAlbumView.getText().toString();
                        if (textoAlbum.length() != 0) {
                            DocumentReference docIdRef = coleccionListas.document(nombreLista);
                            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        discos = (ArrayList) document.get("Album"); // Array de los discos que ya están en la lista
                                        discosVacia = new ArrayList<>(); //Array de discos vacia por si la lista no tiene discos
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
                                                                        if (document.exists()) { //Si el documento esiste añadimos el album a la lista
                                                                            discos.add(textoAlbum);
                                                                            coleccionListas.document(nombreLista).update("Album", discos); //Aadimos el nombre del album a la lista
                                                                            finish();
                                                                        }else {
                                                                            textoAlbumView.setError("El album no existe");
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
                                                                        if (document.exists()) { //Si el documento existe añadimos el album a la lista
                                                                            discosVacia.add(textoAlbum); //Se añade el album a esta lista porque la lista actual carece de albumes
                                                                            coleccionListas.document(nombreLista).update("Album", discosVacia); //Añadimos el nombre del album a la lista vacia
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
