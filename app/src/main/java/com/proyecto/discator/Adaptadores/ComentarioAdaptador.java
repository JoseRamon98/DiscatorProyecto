package com.proyecto.discator.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

        //Guardar información en el item del listado (solo guardamos el nombre del libro)
        final Comentario comentario=this.getItem(position);
        TextView etiquetaUsuario = item.findViewById(R.id.usuario);
        etiquetaUsuario.setText(getItem(position).getIdUsuario());
        TextView etiquetaValoracion = item.findViewById(R.id.valoracion);
        etiquetaValoracion.setText(getItem(position).getValoracion());
        TextView etiquetaComentario = item.findViewById(R.id.comentario);
        etiquetaComentario.setText(getItem(position).getComentario());

        //Accion al pulsar sobre el item, va a la pantalla que muestra detalles del libro pasandole como parametro el identificador
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