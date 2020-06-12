package com.proyecto.discator.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.proyecto.discator.AmigoActivity;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Comentario;

import java.util.ArrayList;

public class ComentarioAdaptador extends ArrayAdapter<Comentario>
{
    private Context contextPadre;

    //Llegan dos parametros, context (será la actividad que llame a esta clase) y datos (array de información de datos a mostrar)
    public ComentarioAdaptador(Context context, ArrayList<Comentario> datos)
    {
        super(context, R.layout.item_lista_comentarios, datos);
        contextPadre=context;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Obtener el item del layout item_lista
        View item = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_comentarios, null);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String correo = user.getEmail();

        //Guardamos la información en el item del listado
        final Comentario comentario=this.getItem(position);
        TextView etiquetaUsuario = item.findViewById(R.id.usuario);
        etiquetaUsuario.setText(getItem(position).getIdUsuario());
        if (getItem(position).getIdUsuario().equals(correo))
        {
            LinearLayout linearLayout=item.findViewById(R.id.layoutComentario);
            linearLayout.setBackgroundColor(Color.parseColor("#C1C1C1"));
        }
        TextView etiquetaValoracion = item.findViewById(R.id.valoracion);
        etiquetaValoracion.setText(getItem(position).getValoracion());
        TextView etiquetaComentario = item.findViewById(R.id.comentario);
        etiquetaComentario.setText(getItem(position).getComentario());

        //Accion al pulsar sobre el item, va a la pantalla que muestra detalles del comentario pasandole como parametro el identificador de usuario
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(contextPadre, AmigoActivity.class);
                intencion.putExtra("Correo", comentario.getIdUsuario());
                contextPadre.startActivity(intencion);
            }
        });
        return item;
    }
}