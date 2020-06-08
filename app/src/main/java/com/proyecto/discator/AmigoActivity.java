package com.proyecto.discator;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.proyecto.discator.Adaptadores.VotacionAdaptador;
import com.proyecto.discator.bean.Votacion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class AmigoActivity extends AppCompatActivity
{
    private ArrayList<Votacion> arrayVotacion;
    private VotacionAdaptador votacionAdaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigo);
        FirebaseFirestore basedatos = FirebaseFirestore.getInstance();
        CollectionReference coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion
        final String foto=getIntent().getStringExtra("Foto");
        ImageView fotoAmigo=findViewById(R.id.imagenAmigo);
        Picasso.with(this).load(foto).into(fotoAmigo);
        final String correo = getIntent().getStringExtra("Correo");
        TextView textoCorreo=findViewById(R.id.correoFriend);
        textoCorreo.setText(correo);
        coleccionArtistas.addSnapshotListener(new EventListener<QuerySnapshot>(){
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                if(e != null){
                    return;
                }
                //Recorrer los documentos de la base de datos y añadirlos al vector de votaciones
                arrayVotacion = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots)
                {
                    FirebaseFirestore bd = FirebaseFirestore.getInstance();
                    CollectionReference coleccionAlbumes = bd.collection("Artistas").document(document.getId()).collection("Albumes");
                    coleccionAlbumes.addSnapshotListener(new EventListener<QuerySnapshot>()
                    {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                        {
                            if(e != null){
                                return;
                            }

                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                            {
                                final ArrayList<Map> comentarios=(ArrayList<Map>)doc.get("Comentarios");
                                String imagenDisco=(String)doc.get("Imagen");
                                String nombreAlbum=doc.getId();
                                String añoDisco=(String)doc.get("Año");
                                if (doc.get("Comentarios")!=null)
                                {
                                    votacionAdaptador = new VotacionAdaptador(AmigoActivity.this, arrayVotacion);
                                    for (int i = 0; i < comentarios.size(); i++)
                                    {
                                        float notaMedia=0;
                                        if (comentarios.get(i).get("correoUsuario").equals(correo))
                                        {
                                            Votacion votacion1 = new Votacion();
                                            votacion1.setComentario((String) comentarios.get(i).get("comentario"));
                                            votacion1.setVotacion((String) comentarios.get(i).get("valoracion"));
                                            votacion1.setImagen(imagenDisco);
                                            votacion1.setNombreAlbum(nombreAlbum);
                                            votacion1.setAño(añoDisco);
                                            arrayVotacion.add(votacion1);
                                            TextView textoNumeroDeVotos=findViewById(R.id.numeroDeVotosAmigo);
                                            textoNumeroDeVotos.setText(String.valueOf(arrayVotacion.size()));
                                            for (Votacion votacion:arrayVotacion)
                                            {
                                                notaMedia+=Float.parseFloat(votacion.getVotacion());
                                            }
                                            TextView textoNotaMediaVotos=findViewById(R.id.MediDeVotosAmigo);
                                            textoNotaMediaVotos.setText(String.format("%.2f",(notaMedia/arrayVotacion.size())));
                                        }
                                    }
                                    votacionAdaptador.notifyDataSetChanged();
                                }
                            }
                            ListView listaVotaciones = findViewById(R.id.listadoAlbumesAmigo);
                            listaVotaciones.setAdapter(votacionAdaptador);
                        }
                    });
                }
            }
        });
    }
}