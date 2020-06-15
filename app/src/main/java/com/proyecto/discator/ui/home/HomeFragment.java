package com.proyecto.discator.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.proyecto.discator.Adaptadores.ArtistaAdaptador;
import com.proyecto.discator.GrupoActivity;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Artista;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class HomeFragment extends Fragment
{
    private FirebaseFirestore basedatos;
    private CollectionReference coleccionArtistas;

    private ArrayList<Artista> arrayArtistas;
    private ArtistaAdaptador adaptadorArtistas;

    private EditText textoBuscarArtista;
    private String artista;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        arrayArtistas=new ArrayList<>();
        adaptadorArtistas = new ArtistaAdaptador(getContext(), arrayArtistas);

        //Crear un objeto FirebaseFirestore para obtener el listado de artistas
        basedatos = FirebaseFirestore.getInstance();
        coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion
        coleccionArtistas.addSnapshotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e){
                if(e != null){
                    return;
                }
                arrayArtistas.clear();
                //Recorrer los documentos de la base de datos y añadi
                // rlos al vector de artistas

                Button botonBuscarArtista=root.findViewById(R.id.buscarArtista);
                botonBuscarArtista.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textoBuscarArtista=root.findViewById(R.id.textoBuscarArtista);
                        artista=textoBuscarArtista.getText().toString();
                        if (artista.length()!=0) {
                            DocumentReference docIdRef = coleccionArtistas.document(artista);
                            docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Intent intencion = new Intent(getContext(), GrupoActivity.class);
                                            intencion.putExtra("Nombre", artista);
                                            startActivity(intencion);
                                        }else
                                            textoBuscarArtista.setError("Asegurate de escribir bien el nombre del artista");
                                    }
                                }
                            });
                        }else
                            textoBuscarArtista.setError("La caja de texto está vacía");
                    }
                });

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