package com.proyecto.discator.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.proyecto.discator.GrupoActivity;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Artista;

import java.util.ArrayList;

public class ArtistaAdaptador extends ArrayAdapter<Artista>
{
    private Context contextPadre;

    //Llegan dos parametros, context (será la actividad que llame a esta clase) y datos (array de información de datos a mostrar)
    public ArtistaAdaptador(Context context, ArrayList<Artista> datos) {
        super(context, R.layout.item_lista, datos);
        contextPadre=context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //Obtener el item del layout item_lista
        View item = LayoutInflater.from(getContext()).inflate(R.layout.item_lista, null);

        //Guardar información en el item del listado (solo guardamos el nombre del libro)
        final Artista artista=this.getItem(position);
        TextView etiquetaNombre = item.findViewById(R.id.nombre);
        etiquetaNombre.setText(getItem(position).getNombre());

        //Accion al pulsar sobre el item, va a la pantalla que muestra detalles del libro pasandole como parametro el identificador
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(contextPadre, GrupoActivity.class);
                intencion.putExtra("Nombre", artista.getNombre());
                contextPadre.startActivity(intencion);
            }
        });
        return item;
    }
}