package com.proyecto.discator.ui.albumesFiltro;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.proyecto.discator.Adaptadores.AlbumAdaptador;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Album;
import com.proyecto.discator.bean.Comentario;
import com.proyecto.discator.sorters.AlbumSorter;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class AlbumesFiltroFragment extends Fragment
{
    private ArrayList<Album> arrayAlbumes;
    private AlbumAdaptador adaptadorAlbumes;
    private ArrayList<Comentario> arrayComentario;
    private AlbumSorter albumSorter;

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState)
    {
        final View root = inflater.inflate(R.layout.fragment_albumes_filtro, container, false);
        FirebaseFirestore basedatos = FirebaseFirestore.getInstance();
        CollectionReference coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion
        //Se recorre la colección de artistas
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
                    arrayAlbumes=new ArrayList<>();//Array vacio con los albumes de la colección
                    adaptadorAlbumes = new AlbumAdaptador(getContext(), arrayAlbumes);
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
                                Album album=new Album();
                                album.setNombreAlbum(doc.getId()); //obtener nombre del album
                                album.setAño((String) doc.get("Año")); //obtener año del album
                                album.setGenero((String) doc.get("Genero")); //obtener genero del album
                                album.setImagen((String) doc.get("Imagen")); //obtener la imagen del album
                                album.setNombre(document1.getId());
                                final ArrayList<Map> comentarios=(ArrayList<Map>)doc.get("Comentarios"); //obtener los comentarios del album
                                if (doc.get("Comentarios")!=null)
                                {
                                    float notaMedia = 0;
                                    arrayComentario = new ArrayList<>(); //Array de comentarios
                                    for (int i = 0; i < comentarios.size(); i++)
                                    {
                                        notaMedia += Float.parseFloat((String) comentarios.get(i).get("valoracion")); //Se almacena la nota que le ha otorgado cada usuario
                                        Comentario comentario1 = new Comentario();
                                        comentario1.setComentario((String) comentarios.get(i).get("comentario")); //obtener el comentario
                                        comentario1.setIdUsuario((String) comentarios.get(i).get("correoUsuario")); //obtener correo del usuario que ha comentado
                                        comentario1.setValoracion((String) comentarios.get(i).get("valoracion")); //obtener la valoración dque el usuaro le ha otorgado al album
                                        arrayComentario.add(comentario1); //Se añaden al Array la inforación
                                    }
                                    notaMedia = notaMedia / comentarios.size(); //calculo de la nota media
                                    album.setNotaMedia(notaMedia);
                                    Log.i("NotaMedia",""+album.getNotaMedia());
                                }
                                arrayAlbumes.add(album);
                                albumSorter=new AlbumSorter(arrayAlbumes);
                                albumSorter.getSortedByNota(); //Se ordenan los albumes por nota
                            }
                            adaptadorAlbumes.notifyDataSetChanged();
                            //Añadir el adatador al ListView
                            ListView vistaListado = root.findViewById(R.id.listaAlbumesFiltrados);
                            vistaListado.setAdapter(adaptadorAlbumes);
                        }
                    });
                }
            }
        });
        return root;
    }
}