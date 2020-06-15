package com.proyecto.discator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ListaAmigoActivity extends AppCompatActivity
{
    private FirebaseFirestore basedatos;
    private CollectionReference coleccionListas;
    private CollectionReference coleccionArtistas;
    private DocumentReference documento;
    private DocumentReference documentoLista;
    private QueryDocumentSnapshot document1;

    private ArrayList<Album> arrayAlbumes;
    private AlbumAdaptador adaptadorAlbumes;
    private ArrayList<String> albumes;
    private ArrayList<String> votos;
    private ArrayList<String> votosVacia;

    private String usuario;
    private String correo;
    private String nombreLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_amigo);
        Button botonVotar=findViewById(R.id.botonLike);
        basedatos = FirebaseFirestore.getInstance();
        usuario = getIntent().getStringExtra("Correo");
        nombreLista = getIntent().getStringExtra("Nombre");
        correo = FirebaseAuth.getInstance().getCurrentUser().getEmail(); //Correo del usuario actual
        TextView textoCreadorLista=findViewById(R.id.correoCreadorLista);
        textoCreadorLista.setText("Lista de "+usuario); //Señalamos de quien es la lista
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
                            adaptadorAlbumes = new AlbumAdaptador(ListaAmigoActivity.this, arrayAlbumes);
                            document1 = document; //Guardamos el documento
                            CollectionReference coleccionAlbumes = coleccionArtistas.document(document.getId()).collection("Albumes"); //Colección de albumes
                            if (albumes != null) {
                                for (int i = 0; i < albumes.size(); i++) {
                                    DocumentReference docIdRef = coleccionAlbumes.document(albumes.get(i));
                                    docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            adaptadorAlbumes = new AlbumAdaptador(ListaAmigoActivity.this, arrayAlbumes);
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Album album = new Album();
                                                    album.setNombreAlbum(document.getId()); //Obtenemos el nombre del album
                                                    album.setGenero((String) document.get("Genero")); //Obtenemos el genero de album
                                                    album.setAño((String) document.get("Año")); //Obtenemos el año del album
                                                    album.setImagen((String) document.get("Imagen")); //Obtenemos la imagen del album
                                                    album.setNombre(document1.getId()); //Obtenemos el nombre del artista
                                                    arrayAlbumes.add(album); //Añadimos el album a un array
                                                    adaptadorAlbumes.notifyDataSetChanged();
                                                    ListView listadoAlbumes = findViewById(R.id.listaAlbumes);
                                                    listadoAlbumes.setAdapter(adaptadorAlbumes);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
            }
        });

        coleccionListas= FirebaseFirestore.getInstance().collection("Usuarios").document(usuario).collection("Listas");
        botonVotar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                documentoLista= coleccionListas.document(nombreLista);
                documentoLista.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();
                            votos = (ArrayList<String>) document.get("Voto"); //Obtenemos los votos si los tiene
                            votosVacia = new ArrayList<>();
                            if (document.exists())
                            {
                                if (!correo.equals(usuario))
                                {
                                    boolean encontrado=false;
                                    if (votos == null) {
                                        votosVacia.add(correo); //Lista vacia para las listas que no tengan votos
                                        documentoLista.update("Voto", votosVacia);
                                        finish();
                                    }
                                    if (document.get("Voto")!=null) {
                                        for (int i = 0; i < votos.size(); i++) {
                                            if (votos.get(i).equals(correo)) {
                                                encontrado = true; //Si se encuentra al usuario que pretende votar
                                            }
                                        }
                                    }
                                    if (!encontrado && votos != null) {
                                        votos.add(correo); //Añadimos el me gusta si la persona no le ha dado me gusta anteriormente
                                        documentoLista.update("Voto", votos);
                                        finish();
                                    } else {
                                        Toast.makeText(ListaAmigoActivity.this, "Ya has votado esta lista", Toast.LENGTH_LONG).show();
                                    }
                                }else
                                    Toast.makeText(ListaAmigoActivity.this, "No puedes votar tu propia lista", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }
}
