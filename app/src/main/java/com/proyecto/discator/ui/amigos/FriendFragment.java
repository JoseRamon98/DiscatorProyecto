package com.proyecto.discator.ui.amigos;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.proyecto.discator.Adaptadores.AmigoAdaptador;
import com.proyecto.discator.AmigoActivity;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Amigo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendFragment extends Fragment
{
    private FirebaseFirestore basedatos;
    private CollectionReference coleccionUsuarios;
    private DocumentReference documento;

    private ArrayList<Amigo> amigos;
    private AmigoAdaptador amigoAdaptador;

    private EditText textoBuscarCorreo;
    private String correoAmigo;
    private String correo;
    private boolean encontrado;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View root = inflater.inflate(R.layout.fragment_friend, container, false);
        basedatos = FirebaseFirestore.getInstance();
        coleccionUsuarios = basedatos.collection("Usuarios"); //nombre de la coleccion
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        correo = user.getEmail(); //Se recoge el correo del usuario
        documento=coleccionUsuarios.document(correo);
        documento.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
        {
            @Override
            public void onSuccess(DocumentSnapshot doc)
            {
                final ArrayList<Map> arrayAmigos=(ArrayList<Map>)doc.get("Amistad");
                if (doc.get("Amistad")!=null)
                {
                    amigos=new ArrayList<>();
                    amigoAdaptador = new AmigoAdaptador(getActivity(), amigos);
                    for(int i=0; i<arrayAmigos.size(); i++)
                    {
                        Amigo amigo=new Amigo();
                        amigo.setCorreo((String)arrayAmigos.get(i).get("correo"));
                        amigo.setFoto((String)arrayAmigos.get(i).get("fotoUsuario"));
                        amigos.add(amigo);
                    }
                    amigoAdaptador.notifyDataSetChanged();
                    ListView listaAmigos = root.findViewById(R.id.listaAmigos);
                    listaAmigos.setAdapter(amigoAdaptador);
                }
                textoBuscarCorreo=root.findViewById(R.id.textoBuscarAmigos);

                Button botonBuscarAmigo=root.findViewById(R.id.botonBuscarAmigos);
                botonBuscarAmigo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        encontrado=false;
                        if (textoBuscarCorreo.getText().toString().length()>0)
                            correoAmigo=textoBuscarCorreo.getText().toString();
                        else
                            correoAmigo="a";
                        if(arrayAmigos==null && !correoAmigo.equals(correo))
                        {
                            ArrayList<Map> arrayAmigo=new ArrayList<>();
                            buscarAmigo(correoAmigo, arrayAmigo);
                            return;
                        }
                        if (arrayAmigos!=null) {
                            for (int i = 0; i < arrayAmigos.size(); i++) {
                                if (arrayAmigos.get(i).get("correo").equals(correoAmigo)) {
                                    Toast.makeText(getContext(), "Ya has agregado a " + correoAmigo, Toast.LENGTH_SHORT).show();
                                    Intent intencion = new Intent(getContext(), AmigoActivity.class);
                                    intencion.putExtra("Correo", correoAmigo);
                                    intencion.putExtra("Foto", (String) arrayAmigos.get(i).get("fotoUsuario"));
                                    startActivity(intencion);
                                    encontrado = true;
                                }
                            }
                        }
                        if(correoAmigo.equals(correo))
                        {
                            textoBuscarCorreo.setError("No te puedes agregar a ti mismo");
                        }
                        else if(!encontrado)
                            buscarAmigo(correoAmigo, arrayAmigos);
                    }
                });
            }
        });

        return root;
    }

    private void buscarAmigo(String id, final ArrayList<Map> arrayAmigos)
    {
        basedatos = FirebaseFirestore.getInstance();
        coleccionUsuarios = basedatos.collection("Usuarios");
        DocumentReference docIdRef=coleccionUsuarios.document(id);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String correo=user.getEmail();
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot document = task.getResult();
                    ArrayList<Map> arrayBuscarAmigo = arrayAmigos;
                    if (document.exists()) {
                        Map<String, Object> mapa = new HashMap<>();
                        mapa.put("correo", document.getId());
                        mapa.put("fotoUsuario", document.get("fotoUsuario"));
                        mapa.put("idUsuario", document.get("idUsuario"));
                        arrayBuscarAmigo.add(mapa);
                        coleccionUsuarios.document(correo).update("Amistad", arrayBuscarAmigo);
                        Intent intencion = new Intent(getContext(), AmigoActivity.class);
                        intencion.putExtra("Correo", correoAmigo);
                        intencion.putExtra("Foto", (String)document.get("fotoUsuario"));
                        startActivity(intencion);
                    } else {
                        textoBuscarCorreo.setError("Este correo no existe");
                    }
                } else {
                    textoBuscarCorreo.setError("Este correo no existe");
                }
            }
        });
    }
}