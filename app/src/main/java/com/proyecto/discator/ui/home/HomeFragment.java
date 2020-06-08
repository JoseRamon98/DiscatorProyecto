package com.proyecto.discator.ui.home;

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
import com.proyecto.discator.Adaptadores.ArtistaAdaptador;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Artista;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class HomeFragment extends Fragment
{
    private ArrayList<Artista> arrayArtistas;
    private ArtistaAdaptador adaptadorArtistas;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        arrayArtistas=new ArrayList<>();
        adaptadorArtistas = new ArtistaAdaptador(getContext(), arrayArtistas);

        //Crear un objeto FirebaseFirestore para obtener el listado de artistas
        FirebaseFirestore basedatos = FirebaseFirestore.getInstance();
        CollectionReference coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion
        coleccionArtistas.addSnapshotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e){
                if(e != null){
                    return;
                }
                arrayArtistas.clear();
                //Recorrer los documentos de la base de datos y añadi
                // rlos al vector de artistas
                for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                    Artista artista=new Artista();
                    artista.setNombre(document.getId()); //obtener nombre del artista
                    artista.setInformacion((String) document.get("Informacion")); //obtener infomación del artista
                    artista.setPais((String) document.get("Pais"));
                    artista.setImagenUrl((String) document.get("Foto"));
                    Log.i("logprueba","Listado, nombre del artista:"+document.getId());
                    arrayArtistas.add(artista);
                }
                adaptadorArtistas.notifyDataSetChanged();
            }
        });
        //Añadir el adatador al ListView
        ListView vistaListado = root.findViewById(R.id.listadoArtistas);
        vistaListado.setAdapter(adaptadorArtistas);
        return root;
    }
}