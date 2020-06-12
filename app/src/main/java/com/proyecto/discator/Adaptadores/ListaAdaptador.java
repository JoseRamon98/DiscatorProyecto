package com.proyecto.discator.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.proyecto.discator.ListaActivity;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Lista;

import java.util.ArrayList;

public class ListaAdaptador extends ArrayAdapter<Lista>
{
    private Context contextPadre;

    //Llegan dos parametros, context (será la actividad que llame a esta clase) y datos (array de información de datos a mostrar)
    public ListaAdaptador(Context context, ArrayList<Lista> datos) {
        super(context, R.layout.item_lista_lista, datos);
        contextPadre=context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //Obtener el item del layout item_lista
        View item = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_lista, null);

        //Guardar información en el item del listado (solo guardamos el nombre del libro)
        final Lista lista=this.getItem(position);
        TextView etiquetaNombre = item.findViewById(R.id.nombreLista);
        etiquetaNombre.setText(getItem(position).getNombreLista());

        //Accion al pulsar sobre el item, va a la pantalla que muestra detalles del libro pasandole como parametro el identificador
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(contextPadre, ListaActivity.class);
                intencion.putExtra("Nombre", lista.getNombreLista());
                intencion.putExtra("Correo", lista.getPropietario());
                contextPadre.startActivity(intencion);
            }
        });
        return item;
    }
}