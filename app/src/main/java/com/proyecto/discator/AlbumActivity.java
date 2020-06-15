package com.proyecto.discator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.proyecto.discator.Adaptadores.ComentarioAdaptador;
import com.proyecto.discator.bean.Comentario;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlbumActivity extends AppCompatActivity
{
    private ArrayList<Comentario> arrayComentario;

    private FirebaseFirestore basedatos;
    private CollectionReference coleccionAlbumes;
    private DocumentReference documento;

    private ComentarioAdaptador comentarioAdaptador;

    private EditText textoComentario;
    RatingBar votacionBar;
    ArrayList<Map> comentariosEscritos;

    private String nombreArtista="";
    private String nombre="";
    private String año="";
    private String genero="";
    private String imagenurl="";
    private String comentario="";
    private String votacion="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        this.nombre=getIntent().getStringExtra("Nombre");
        this.nombreArtista=getIntent().getStringExtra("Nombre Artista");
        basedatos = FirebaseFirestore.getInstance();
        coleccionAlbumes = basedatos.collection("Artistas").document(nombreArtista).collection("Albumes");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //Consultar documento de la collecion Albumes por identificador
        documento=coleccionAlbumes.document(this.nombre);
        documento.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot doc)
            {
                //Obtener textos de la base de datos
                genero=(String)doc.get("Genero");
                año = (String)doc.get("Año");
                imagenurl=(String)doc.get("Imagen");
                final ArrayList<Map> comentarios=(ArrayList<Map>)doc.get("Comentarios");
                //Guardar textos en las cajas de texto
                TextView nombreView = findViewById(R.id.nombreAlbum);
                nombreView.setText(nombre+" ("+año+")");
                TextView generoView = findViewById(R.id.generoAlbum);
                generoView.setText(genero);
                ImageView imagen=findViewById(R.id.imagenAlbum);
                Picasso.with(AlbumActivity.this).load(imagenurl).into(imagen);

                if (doc.get("Comentarios")!=null)
                {
                    float notaMedia = 0;
                    arrayComentario = new ArrayList<>();
                    comentarioAdaptador = new ComentarioAdaptador(AlbumActivity.this, arrayComentario);
                    for (int i = 0; i < comentarios.size(); i++) {
                        notaMedia += Float.parseFloat((String) comentarios.get(i).get("valoracion"));
                        Comentario comentario1 = new Comentario();
                        comentario1.setComentario((String) comentarios.get(i).get("comentario"));
                        comentario1.setIdUsuario((String) comentarios.get(i).get("correoUsuario"));
                        comentario1.setValoracion((String) comentarios.get(i).get("valoracion"));
                        arrayComentario.add(comentario1);
                    }
                    comentarioAdaptador.notifyDataSetChanged();
                    notaMedia = notaMedia / comentarios.size();
                    TextView nota_media = findViewById(R.id.nota_media);
                    nota_media.setText(String.format("%.2f",(notaMedia)));
                    ListView listaComentarios = findViewById(R.id.listadoComentarios);
                    listaComentarios.setAdapter(comentarioAdaptador);
                }
                else {
                    TextView nota_media = findViewById(R.id.nota_media);
                    nota_media.setText("-");
                }

                //
                comentariosEscritos=new ArrayList<>();

                //Acciones del boton que guarda las modificaciones
                Button objetoBotonEnviar = findViewById(R.id.enviarComentario);
                objetoBotonEnviar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        votacionBar=findViewById(R.id.ratingBar);
                        votacion=String.valueOf(votacionBar.getRating()*2);
                        textoComentario=findViewById(R.id.textoComentario);
                        comentario=textoComentario.getText().toString();
                        if (comentario.length() > 600)
                            textoComentario.setError("Error, no puede haber mas de 600 caracteres");
                        else if(votacionBar.getRating()==0)
                            textoComentario.setError("Error, falta la votación");
                        else
                        {
                            if (comentarios!=null)
                            {
                                boolean encontrado=false;
                                int pos=0;
                                for (int i=0; i<comentarios.size(); i++)
                                {
                                    if (comentarios.get(i).get("correoUsuario").equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                                    {
                                        encontrado=true;
                                        pos=i;
                                    }
                                }
                                if (!encontrado)
                                {
                                    Map<String, Object> mapa = new HashMap<>(); //Creamos un mapa con los comentarios del disco para añadirlos
                                    mapa.put("comentario", comentario);
                                    mapa.put("correoUsuario", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    mapa.put("valoracion", votacion);
                                    comentarios.add(mapa);
                                    coleccionAlbumes.document(nombre).update("Comentarios", comentarios); //Añadimos a los comentarios un nuevo comentario
                                    finish();
                                }
                                else
                                {
                                    Map<String, Object> mapa = new HashMap<>(); //Creamos un mapa con los comentarios del disco para añadirlos
                                    mapa.put("comentario", comentario);
                                    mapa.put("correoUsuario", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                    mapa.put("valoracion", votacion);
                                    comentarios.set(pos, mapa); //Si ya ha hecho un comentario ylo vuelve a hacer, este se cambiará por el anterior
                                    coleccionAlbumes.document(nombre).update("Comentarios", comentarios); //Añadimos a los comentarios un nuevo comentario
                                    finish();
                                }
                            }
                            else
                            {
                                Map<String, Object> mapa = new HashMap<>();
                                mapa.put("comentario", comentario);
                                mapa.put("correoUsuario", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                mapa.put("valoracion", votacion);
                                comentariosEscritos.add(mapa);
                                coleccionAlbumes.document(nombre).update("Comentarios", comentariosEscritos); //Creamos un nuevo campo para loc comentarios e introducimos uno
                                finish();
                            }
                        }
                    }
                });

                generoView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view)
                    { //Al pulsar sobre el género, se filtran los albumes por este
                        Intent intencion = new Intent(view.getContext(), GenerosActivity.class);
                        intencion.putExtra("Genero", genero);
                        startActivity(intencion);
                    }
                });

                nombreView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view)
                    { //Al pulsar sobre el año, se filtran los albumes por este
                        Intent intencion = new Intent(view.getContext(), YearActivity.class);
                        intencion.putExtra("Año", año);
                        startActivity(intencion);
                    }
                });
            }
        });
    }
}