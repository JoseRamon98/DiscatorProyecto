package com.proyecto.discator.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.proyecto.discator.Adaptadores.AlbumAdaptador;
import com.proyecto.discator.GrupoActivity;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Album;
import com.proyecto.discator.sorters.AlbumSorter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";

    private PageViewModel pageViewModel;

    private ArrayList<Album> arrayAlbumes;
    private AlbumAdaptador adaptadorAlbumes;
    private AlbumSorter albumSorter;

    private FirebaseFirestore basedatos;
    private CollectionReference coleccionArtistas;
    private DocumentReference documento;

    private String nombre="";
    private String pais="";
    private String informacion="";
    private String imagenUrl="";

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
        this.nombre=getActivity().getIntent().getStringExtra("Nombre");
        basedatos = FirebaseFirestore.getInstance();
        coleccionArtistas = basedatos.collection("Artistas");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_grupo, container, false);
        final LinearLayout linearPantalla1 = root.findViewById(R.id.pantalla1);
        final LinearLayout linearPantalla2 = root.findViewById(R.id.pantalla2);

        documento=coleccionArtistas.document(this.nombre);
        documento.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot doc)
            {
                //Log.i("logprueba","Consulta por identificador:"+doc.get("Descripcion"));
                //Obtener textos de la base de datos
                pais=(String)doc.get("Pais");
                informacion = (String)doc.get("Informacion");
                imagenUrl=(String)doc.get("Foto");
                //Guardar textos en las cajas de texto
                TextView nombreView = ((GrupoActivity)getActivity()).findViewById(R.id.nombreArtista);
                nombreView.setText(nombre);
                TextView descripcionView = ((GrupoActivity)getActivity()).findViewById(R.id.informacionArtista);
                descripcionView.setText(informacion);
                TextView paisView = ((GrupoActivity)getActivity()).findViewById(R.id.paisArtista);
                paisView.setText(pais);
                ImageView imagen=((GrupoActivity)getActivity()).findViewById(R.id.imagen);
                Picasso.with(getActivity()).load(imagenUrl).into(imagen);
            }
        });

        //Crear un objeto FirebaseFirestore para obtener el listado de artistas
        //FirebaseFirestore basedatos = FirebaseFirestore.getInstance();
        //CollectionReference coleccionAlbumes = basedatos.collection("Albumes").document(nombre).collection("Albumes"); //nombre de la coleccion
        CollectionReference coleccionAlbumes=documento.collection("Albumes");
        arrayAlbumes=new ArrayList<>();
        adaptadorAlbumes = new AlbumAdaptador(getContext(), arrayAlbumes);
        coleccionAlbumes.addSnapshotListener(new EventListener<QuerySnapshot>()
        {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e){
                if(e != null){
                    return;
                }
                arrayAlbumes.clear();
                //Recorrer los documentos de la base de datos y añadirlos al vector de libros
                for (QueryDocumentSnapshot document : queryDocumentSnapshots)
                {
                    float notaMedia=0;
                    Album album=new Album();
                    album.setNombreAlbum(document.getId()); //obtener nombre del artista
                    album.setAño((String) document.get("Año")); //obtener infomación del artista
                    album.setGenero((String) document.get("Genero"));
                    album.setImagen((String) document.get("Imagen"));
                    album.setComentarios((ArrayList<Map>) document.get("Comentarios"));
                    if (document.get("Comentarios")!=null) {
                        for (int i = 0; i < album.getComentarios().size(); i++) {
                            notaMedia += Float.parseFloat((String) album.getComentarios().get(i).get("valoracion"));
                        }
                    }
                    if (album.getComentarios()!=null)
                        notaMedia = notaMedia / album.getComentarios().size();
                    album.setNotaMedia(notaMedia);
                    album.setNombre(nombre);
                    arrayAlbumes.add(album);
                    albumSorter=new AlbumSorter(arrayAlbumes);
                    albumSorter.getSortedByAño();
                }
                adaptadorAlbumes.notifyDataSetChanged();
            }
        });
        //Añadir el adatador al ListView
        ListView vistaListado = root.findViewById(R.id.listadoAlbumes);
        vistaListado.setAdapter(adaptadorAlbumes);


        pageViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //Ocultar todas las pestañas
                linearPantalla1.setVisibility(View.INVISIBLE);
                linearPantalla2.setVisibility(View.INVISIBLE);
                //Mostrar la pestaña pulsada
                if (s.equals("1")){
                    linearPantalla1.setVisibility(View.VISIBLE);
                }
                if (s.equals("2")){
                    linearPantalla2.setVisibility(View.VISIBLE);
                }
            }
        });
        return root;
    }
}