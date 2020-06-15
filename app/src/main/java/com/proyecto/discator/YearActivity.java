package com.proyecto.discator;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.proyecto.discator.Adaptadores.AlbumAdaptador;
import com.proyecto.discator.bean.Album;
import com.proyecto.discator.bean.Comentario;
import com.proyecto.discator.sorters.AlbumSorter;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class YearActivity extends AppCompatActivity
{
    private QueryDocumentSnapshot document1;

    private ArrayList<Album> arrayAlbumes;
    private AlbumAdaptador adaptadorAlbumes;
    private ArrayList<Comentario> arrayComentario;
    private AlbumSorter albumSorter;

    private String año;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_year);

        FirebaseFirestore basedatos = FirebaseFirestore.getInstance();
        CollectionReference coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion
        año=getIntent().getStringExtra("Año");
        coleccionArtistas.addSnapshotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                if(e != null){
                    return;
                }
                for (QueryDocumentSnapshot document : queryDocumentSnapshots)
                {
                    document1=document; //Guardamos el documento para extraer el nombre del artista
                    FirebaseFirestore bd = FirebaseFirestore.getInstance();
                    CollectionReference coleccionAlbumes = bd.collection("Artistas").document(document.getId()).collection("Albumes");
                    arrayAlbumes=new ArrayList<>();
                    adaptadorAlbumes = new AlbumAdaptador(YearActivity.this, arrayAlbumes);
                    coleccionAlbumes.addSnapshotListener(new EventListener<QuerySnapshot>()
                    {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e){
                            if(e != null){
                                return;
                            }
                            //Recorrer los documentos de la base de datos y añadirlos al vector de albumes
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                            {
                                if (doc.get("Año").equals(año)) {
                                    Album album = new Album();
                                    album.setNombreAlbum(doc.getId()); //obtener nombre del album
                                    album.setAño((String) doc.get("Año")); //obtener año del album
                                    album.setGenero((String) doc.get("Genero")); //obtener genero del album
                                    album.setImagen((String) doc.get("Imagen")); //obtener la imagen del album
                                    album.setNombre(document1.getId()); //obtener el nombre del artista
                                    final ArrayList<Map> comentarios = (ArrayList<Map>) doc.get("Comentarios"); //obtener los comentarios
                                    if (doc.get("Comentarios") != null) {
                                        float notaMedia = 0;
                                        arrayComentario = new ArrayList<>();
                                        for (int i = 0; i < comentarios.size(); i++) {
                                            notaMedia += Float.parseFloat((String) comentarios.get(i).get("valoracion")); //Almacenamos las notas de los usuarios
                                            Comentario comentario1 = new Comentario();
                                            comentario1.setComentario((String) comentarios.get(i).get("comentario")); //Obtenemos el comentario
                                            comentario1.setIdUsuario((String) comentarios.get(i).get("correoUsuario")); //Obtenemos el correo
                                            comentario1.setValoracion((String) comentarios.get(i).get("valoracion")); //Obtenemos la valoración
                                            arrayComentario.add(comentario1);
                                        }
                                        notaMedia = notaMedia / comentarios.size(); //hacemos la media de las votaciones con el número de votos
                                        album.setNotaMedia(notaMedia);
                                        Log.i("NotaMedia", "" + album.getNotaMedia());
                                    }
                                    arrayAlbumes.add(album); //Añadimos el album al array
                                    albumSorter = new AlbumSorter(arrayAlbumes);
                                    albumSorter.getSortedByNota(); //Ordenamos los albumes por nota
                                }
                            }
                            adaptadorAlbumes.notifyDataSetChanged();
                            //Añadir el adatador al ListView
                            ListView vistaListado = findViewById(R.id.listaAlbumesFiltradosAño);
                            vistaListado.setAdapter(adaptadorAlbumes); //Almacenamos los albumes ordenados en el listado
                        }
                    });
                }
            }
        });
    }
}
