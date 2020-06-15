package com.proyecto.discator.ui.votaciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.proyecto.discator.Adaptadores.VotacionAdaptador;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Votacion;
import com.proyecto.discator.sorters.VotacionSorter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

public class VotacionesFragment extends Fragment
{
    private ArrayList<Votacion> arrayVotacion;
    private VotacionAdaptador votacionAdaptador;

    private VotacionSorter votacionSorter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View root = inflater.inflate(R.layout.fragment_votaciones, container, false);
        FirebaseFirestore basedatos = FirebaseFirestore.getInstance();
        CollectionReference coleccionArtistas = basedatos.collection("Artistas"); //nombre de la coleccion
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String correo = user.getEmail();
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
                                float notaMedia=0;
                                if (doc.get("Comentarios")!=null)
                                {
                                    votacionAdaptador = new VotacionAdaptador(getActivity(), arrayVotacion);
                                    for (int i = 0; i < comentarios.size(); i++)
                                    {
                                        if (comentarios.get(i).get("correoUsuario").equals(correo))
                                        {
                                            Votacion votacion1 = new Votacion();
                                            votacion1.setComentario((String) comentarios.get(i).get("comentario"));
                                            votacion1.setVotacion((String) comentarios.get(i).get("valoracion"));
                                            votacion1.setImagen(imagenDisco);
                                            votacion1.setNombreAlbum(nombreAlbum);
                                            votacion1.setAño(añoDisco);
                                            float notaValoracion=Float.parseFloat(votacion1.getVotacion());
                                            votacion1.setNotaValoracion(notaValoracion);
                                            arrayVotacion.add(votacion1);
                                            votacionSorter=new VotacionSorter(arrayVotacion);
                                            votacionSorter.getSortedByVotacion(); //Se ordenan los albumes por nota
                                            TextView textoNumeroDeVotos=root.findViewById(R.id.numeroDeVotos);
                                            textoNumeroDeVotos.setText("Número de votos: "+arrayVotacion.size());
                                            for (Votacion votacion:arrayVotacion)
                                            {
                                                notaMedia+=Float.parseFloat(votacion.getVotacion());
                                            }
                                            TextView textoNotaMediaVotos=root.findViewById(R.id.notaMediaVotos);
                                            textoNotaMediaVotos.setText("Media de votos: "+String.format("%.2f",(notaMedia/arrayVotacion.size())));
                                        }
                                    }
                                    votacionAdaptador.notifyDataSetChanged();
                                    ListView listaVotaciones = root.findViewById(R.id.listadoVotaciones);
                                    listaVotaciones.setAdapter(votacionAdaptador);
                                }
                            }
                        }
                    });
                }
            }
        });
        TextView textoCorreo=root.findViewById(R.id.correo);
        textoCorreo.setText(correo);
        ImageView fotoCorreo=root.findViewById(R.id.fotoCorreo);
        Picasso.with(getContext()).load(user.getPhotoUrl()).into(fotoCorreo);
        return root;
    }
}