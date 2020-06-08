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
import com.proyecto.discator.Adaptadores.ArtistaAdaptador;
import com.proyecto.discator.bean.Artista;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class PrincipalActivity extends AppCompatActivity
{
    private ArrayList<Artista> arrayArtistas;
    private ArtistaAdaptador adaptadorArtistas;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        //Crear un objeto de tipo LibroAdaptador para representar en el ListView
        //el contenido del listado de libros (inicialmente vacío)
        arrayArtistas=new ArrayList<>();
        adaptadorArtistas = new ArtistaAdaptador(this, arrayArtistas);
        //Crear un objeto FirebaseFirestore para obtener el listado de libros
        FirebaseFirestore basedatos = FirebaseFirestore.getInstance();
        CollectionReference coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion
        coleccionArtistas.addSnapshotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e){
                if(e != null){
                    return;
                }
                arrayArtistas.clear();
                //Recorrer los documentos de la base de datos y añadirlos al vector de libros
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
        ListView vistaListado = findViewById(R.id.listadoArtistas);
        vistaListado.setAdapter(adaptadorArtistas);
    }
}