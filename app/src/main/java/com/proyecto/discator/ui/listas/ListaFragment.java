package com.proyecto.discator.ui.listas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.proyecto.discator.Adaptadores.ListaAdaptador;
import com.proyecto.discator.ListaActivity;
import com.proyecto.discator.Main2Activity;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Lista;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListaFragment extends Fragment
{
    CollectionReference coleccionListas;

    private ArrayList<Lista> arrayLista;
    private ListaAdaptador adaptadorListas;

    private String usuario;
    private String textoLista;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View root = inflater.inflate(R.layout.fragment_listas, container, false);

        FirebaseFirestore basedatos = FirebaseFirestore.getInstance();
        usuario= FirebaseAuth.getInstance().getCurrentUser().getEmail();
        coleccionListas = basedatos.collection("Usuarios").document(usuario).collection("Listas"); //nombre de la coleccion
        coleccionListas.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                arrayLista=new ArrayList<>();
                adaptadorListas = new ListaAdaptador(getContext(), arrayLista);
                Button botonCrearLista=root.findViewById(R.id.botonCrearLista);
                botonCrearLista.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        EditText textoListaView=root.findViewById(R.id.textoLista);
                        textoLista = textoListaView.getText().toString();
                        if (textoLista.length()!=0)
                        {
                            Map<String, Object> nuevaLista = new HashMap<>();
                            nuevaLista.put("Tipo", "publica");
                            coleccionListas.document(textoLista).set(nuevaLista).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) { //libro guardado correctamente, voy al listado
                                    Intent intencion = new Intent(getActivity(), ListaActivity.class);
                                    intencion.putExtra("Nombre",textoLista);
                                    intencion.putExtra("Correo",usuario);
                                    startActivity(intencion);
                                }
                            }).addOnFailureListener(new OnFailureListener() { //si error al guardar, mostrar mensaje
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Error al crear la nueva lista", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                            textoListaView.setError("La lista no puede estar vacia");
                    }
                });

                Button botonBorrarLista=root.findViewById(R.id.botonBorrarLista);
                botonBorrarLista.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        EditText textoListaView=root.findViewById(R.id.textoLista);
                        textoLista = textoListaView.getText().toString();
                        if (textoLista.length()!=0)
                        {
                            coleccionListas.document(textoLista).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) { //libro guardado correctamente, voy al listado
                                    Intent intencion = new Intent(getActivity(), Main2Activity.class);
                                    startActivity(intencion);
                                }
                            }).addOnFailureListener(new OnFailureListener() { //si error al guardar, mostrar mensaje
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Error al borrar la lista", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                            textoListaView.setError("La lista no puede estar vacia");
                    }
                });

                //Recorrer los documentos de la base de datos y añadirlos al vector de listas
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                {
                    Lista lista=new Lista();
                    lista.setNombreLista(doc.getId());
                    lista.setPropietario(usuario);
                    arrayLista.add(lista);
                }
                adaptadorListas.notifyDataSetChanged();
                ListView vistaLista=root.findViewById(R.id.listadoListas);
                vistaLista.setAdapter(adaptadorListas);
            }
        });
        return root;
    }
}