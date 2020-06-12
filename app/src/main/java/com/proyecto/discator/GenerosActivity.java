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

public class GenerosActivity extends AppCompatActivity
{
    private ArrayList<Album> arrayAlbumes;
    private AlbumAdaptador adaptadorAlbumes;
    private ArrayList<Comentario> arrayComentario;
    private AlbumSorter albumSorter;

    private String genero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generos);

        FirebaseFirestore basedatos = FirebaseFirestore.getInstance();
        CollectionReference coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion
        genero=getIntent().getStringExtra("Genero");
        coleccionArtistas.addSnapshotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                if(e != null){
                    return;
                }
                for (QueryDocumentSnapshot document : queryDocumentSnapshots)
                {
                    final QueryDocumentSnapshot document1=document;
                    FirebaseFirestore bd = FirebaseFirestore.getInstance();
                    CollectionReference coleccionAlbumes = bd.collection("Artistas").document(document.getId()).collection("Albumes");
                    arrayAlbumes=new ArrayList<>();
                    adaptadorAlbumes = new AlbumAdaptador(GenerosActivity.this, arrayAlbumes);
                    coleccionAlbumes.addSnapshotListener(new EventListener<QuerySnapshot>()
                    {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e){
                            if(e != null){
                                return;
                            }
                            //Recorrer los documentos de la base de datos y añadirlos al vector de libros
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                            {
                                if (doc.get("Genero").equals(genero)) {
                                    Album album = new Album();
                                    album.setNombreAlbum(doc.getId()); //obtener nombre del album
                                    album.setAño((String) doc.get("Año")); //obtener año del album
                                    album.setGenero((String) doc.get("Genero")); //obtener genero del album
                                    album.setImagen((String) doc.get("Imagen")); //obtener la imagen del album
                                    album.setNombre(document1.getId());
                                    final ArrayList<Map> comentarios = (ArrayList<Map>) doc.get("Comentarios");
                                    if (doc.get("Comentarios") != null) {
                                        float notaMedia = 0;
                                        arrayComentario = new ArrayList<>();
                                        for (int i = 0; i < comentarios.size(); i++) {
                                            notaMedia += Float.parseFloat((String) comentarios.get(i).get("valoracion"));
                                            Comentario comentario1 = new Comentario();
                                            comentario1.setComentario((String) comentarios.get(i).get("comentario"));
                                            comentario1.setIdUsuario((String) comentarios.get(i).get("correoUsuario"));
                                            comentario1.setValoracion((String) comentarios.get(i).get("valoracion"));
                                            arrayComentario.add(comentario1);
                                        }
                                        notaMedia = notaMedia / comentarios.size();
                                        album.setNotaMedia(notaMedia);
                                        Log.i("NotaMedia", "" + album.getNotaMedia());
                                    }
                                    arrayAlbumes.add(album);
                                    albumSorter = new AlbumSorter(arrayAlbumes);
                                    albumSorter.getSortedByNota();
                                }
                            }
                            adaptadorAlbumes.notifyDataSetChanged();
                            //Añadir el adatador al ListView
                            ListView vistaListado = findViewById(R.id.listaAlbumesFiltradosGenero);
                            vistaListado.setAdapter(adaptadorAlbumes);
                        }
                    });
                }
            }
        });
    }
}