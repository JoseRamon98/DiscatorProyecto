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
import com.proyecto.discator.Adaptadores.ListaRankAdaptador;
import com.proyecto.discator.Adaptadores.VotacionAdaptador;
import com.proyecto.discator.bean.Lista;
import com.proyecto.discator.bean.Votacion;
import com.proyecto.discator.sorters.ListaSorter;
import com.proyecto.discator.sorters.VotacionSorter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class AmigoActivity extends AppCompatActivity
{
    private ArrayList<Votacion> arrayVotacion;
    private VotacionAdaptador votacionAdaptador;
    private ArrayList<Lista> arrayLista;
    private ListaRankAdaptador adaptadorListas;
    private ListaSorter listaSorter;
    private VotacionSorter votacionSorter;

    private String correo;
    private String foto;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigo);
        FirebaseFirestore basedatos = FirebaseFirestore.getInstance();
        CollectionReference coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion Artistas
        foto=getIntent().getStringExtra("Foto"); //Se recibe la foto desde amigoAdaptador
        ImageView fotoAmigo=findViewById(R.id.imagenAmigo);
        Picasso.with(this).load(foto).into(fotoAmigo);
        correo = getIntent().getStringExtra("Correo");//Se recibe el correo desde amigoAdaptador
        TextView textoCorreo=findViewById(R.id.correoFriend);
        textoCorreo.setText(correo);
        coleccionArtistas.addSnapshotListener(new EventListener<QuerySnapshot>(){ //Se recorre la colección de los artistas
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
                                            votacion1.setComentario((String) comentarios.get(i).get("comentario")); //Obtenemos el comentario
                                            votacion1.setVotacion((String) comentarios.get(i).get("valoracion")); //Obtenemos la valoracion
                                            votacion1.setImagen(imagenDisco); //Obtenemos la imagen del disco
                                            votacion1.setNombreAlbum(nombreAlbum); //Obtenemos el nombre del disco
                                            votacion1.setAño(añoDisco); //Obtenemos el año del disco
                                            float notaValoracion=Float.parseFloat(votacion1.getVotacion()); //Parseamos el String a un float
                                            votacion1.setNotaValoracion(notaValoracion); //Obtenemos la valoracion que tiene el disco
                                            arrayVotacion.add(votacion1);
                                            votacionSorter=new VotacionSorter(arrayVotacion);
                                            votacionSorter.getSortedByVotacion(); //Se ordenan los albumes por nota
                                            TextView textoNumeroDeVotos=findViewById(R.id.numeroDeVotosAmigo);
                                            textoNumeroDeVotos.setText("Número de votos: "+arrayVotacion.size());
                                            for (Votacion votacion:arrayVotacion)
                                            {
                                                notaMedia+=Float.parseFloat(votacion.getVotacion());
                                            }
                                            TextView textoNotaMediaVotos=findViewById(R.id.MediDeVotosAmigo);
                                            textoNotaMediaVotos.setText("Media de votos: "+String.format("%.2f",(notaMedia/arrayVotacion.size())));
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

        arrayLista=new ArrayList<>();
        adaptadorListas = new ListaRankAdaptador(AmigoActivity.this, arrayLista);
        CollectionReference coleccionListas = basedatos.collection("Usuarios").document(correo).collection("Listas"); //nombre de la coleccion
        coleccionListas.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                {
                    String privacidad=(String)doc.get("Tipo");
                    ArrayList<String> likes=(ArrayList<String>)doc.get("Voto");
                    if (privacidad.equals("publica"))
                    {
                        int numeroDeLikes=0;
                        Lista lista = new Lista();
                        lista.setNombreLista(doc.getId()); //Obtenemos el nombre de la lista
                        lista.setPropietario(correo); //Obtenemos el correo de la persona que ha creado la lista
                        if (likes!=null)
                        {
                            for (int i=0; i<likes.size(); i++)
                            {
                                numeroDeLikes++; //Almacenamos el nñumero de likes
                            }
                        }
                        lista.setLikes(numeroDeLikes);
                        arrayLista.add(lista);
                        listaSorter=new ListaSorter(arrayLista);
                        listaSorter.getSortedByLikes(); //Ordenamos las listas por el número de likes
                    }
                }
                adaptadorListas.notifyDataSetChanged();
                ListView listaListasR = findViewById(R.id.listadoListasAmigo);
                listaListasR.setAdapter(adaptadorListas);
            }
        });
    }
}