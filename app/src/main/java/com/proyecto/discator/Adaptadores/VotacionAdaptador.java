package com.proyecto.discator.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyecto.discator.R;
import com.proyecto.discator.bean.Votacion;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VotacionAdaptador extends ArrayAdapter<Votacion>
{
    private Context contextPadre;

    //Llegan dos parametros, context (será la actividad que llame a esta clase) y datos (array de información de datos a mostrar)
    public VotacionAdaptador(Context context, ArrayList<Votacion> datos)
    {
        super(context, R.layout.item_lista_votaciones, datos);
        contextPadre=context;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Obtener el item del layout item_lista
        View item = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_votaciones, null);

        //Guardar información en el item del listado (solo guardamos el nombre del libro)
        final Votacion votacion=this.getItem(position);
        ImageView etiquetaImagen = item.findViewById(R.id.imagenU);
        Picasso.with(getContext()).load(getItem(position).getImagen()).into(etiquetaImagen);
        TextView etiquetaNombreAlbum = item.findViewById(R.id.nombreAlbumU);
        etiquetaNombreAlbum.setText(getItem(position).getNombreAlbum()+" ("+getItem(position).getAño()+")");
        TextView etiquetaValoracion = item.findViewById(R.id.valoracionU);
        etiquetaValoracion.setText(getItem(position).getVotacion());
        TextView etiquetaComentario = item.findViewById(R.id.comentarioU);
        etiquetaComentario.setText(getItem(position).getComentario());

        //Accion al pulsar sobre el item, va a la pantalla que muestra detalles del libro pasandole como parametro el identificador
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intencion = new Intent(contextPadre, VotacionActivity.class);
//                intencion.putExtra("Nombre", votacion.getIdUsuario());
//                contextPadre.startActivity(intencion);
            }
        });
        return item;
    }
}