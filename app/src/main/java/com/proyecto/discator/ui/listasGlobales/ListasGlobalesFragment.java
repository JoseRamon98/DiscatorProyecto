package com.proyecto.discator.ui.listasGlobales;

import android.os.Bundle;
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
import com.proyecto.discator.Adaptadores.ListaRankAdaptador;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Lista;
import com.proyecto.discator.sorters.ListaSorter;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ListasGlobalesFragment extends Fragment
{
    private CollectionReference coleccionUsuarios;
    private FirebaseFirestore basedatos;

    private ArrayList<Lista> arrayLista;
    private ListaRankAdaptador adaptadorListasR;
    private ListaSorter listaSorter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View root = inflater.inflate(R.layout.fragment_perfil, container, false);
        basedatos = FirebaseFirestore.getInstance();
        coleccionUsuarios = basedatos.collection("Usuarios");
        coleccionUsuarios.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                if (e != null) {
                    return;
                }
                arrayLista = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    adaptadorListasR = new ListaRankAdaptador(getActivity(), arrayLista);
                    final QueryDocumentSnapshot document1=document;
                    CollectionReference coleccionListas= coleccionUsuarios.document(document.getId()).collection("Listas");
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
                                    lista.setNombreLista(doc.getId());
                                    lista.setPropietario(document1.getId());
                                    if (likes!=null)
                                    {
                                        for (int i=0; i<likes.size(); i++)
                                        {
                                            numeroDeLikes++;
                                        }
                                    }
                                    lista.setLikes(numeroDeLikes);
                                    arrayLista.add(lista);
                                    listaSorter=new ListaSorter(arrayLista);
                                    listaSorter.getSortedByLikes();
                                }
                            }
                            adaptadorListasR.notifyDataSetChanged();
                            ListView listaListasR = root.findViewById(R.id.listaMejoresListas);
                            listaListasR.setAdapter(adaptadorListasR);
                        }
                    });
                }
            }
        });
        return root;
    }
}