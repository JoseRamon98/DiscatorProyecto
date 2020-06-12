package com.proyecto.discator.ui.perfil;

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

import java.util.ArrayList;

import javax.annotation.Nullable;

public class PerfilFragment extends Fragment
{
    private CollectionReference coleccionUsuarios;
    private FirebaseFirestore basedatos;

    private ArrayList<Lista> arrayLista;
    private ListaRankAdaptador adaptadorListasR;

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
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    arrayLista = new ArrayList<>();
                    final QueryDocumentSnapshot document1=document;
                    CollectionReference coleccionListas= coleccionUsuarios.document(document.getId()).collection("Listas");
                    coleccionListas.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                return;
                            }
                            adaptadorListasR = new ListaRankAdaptador(getContext(), arrayLista);
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots)
                            {
                                String privacidad=(String)doc.get("Tipo");
                                if (privacidad.equals("publica")) {
                                    Lista lista = new Lista();
                                    lista.setNombreLista(doc.getId());
                                    lista.setPropietario(document1.getId());
                                    arrayLista.add(lista);
                                    adaptadorListasR.notifyDataSetChanged();
                                    ListView listaListasR = root.findViewById(R.id.listaMejoresListas);
                                    listaListasR.setAdapter(adaptadorListasR);
                                }
                            }
                        }
                    });
                }
            }
        });
        return root;
    }
}