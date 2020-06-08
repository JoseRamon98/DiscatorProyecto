package com.proyecto.discator.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyecto.discator.AmigoActivity;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Amigo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AmigoAdaptador extends ArrayAdapter<Amigo>
{
    private Context contextPadre;

    //Llegan dos parametros, context (será la actividad que llame a esta clase) y datos (array de información de datos a mostrar)
    public AmigoAdaptador(Context context, ArrayList<Amigo> datos)
    {
        super(context, R.layout.item_lista_amigos, datos);
        contextPadre=context;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Obtener el item del layout item_lista
        View item = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_amigos, null);

        //Guardar información en el item del listado (solo guardamos el nombre del libro)
        final Amigo amigo=this.getItem(position);
        ImageView fotoAmigo=item.findViewById(R.id.fotoAmigo);
        Picasso.with(contextPadre).load(getItem(position).getFoto()).into(fotoAmigo);
        TextView etiquetaCorreo = item.findViewById(R.id.correoAmigo);
        etiquetaCorreo.setText(getItem(position).getCorreo());

        //Accion al pulsar sobre el item, va a la pantalla que muestra detalles del libro pasandole como parametro el identificador
        item.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intencion = new Intent(contextPadre, AmigoActivity.class);
                intencion.putExtra("Correo",amigo.getCorreo());
                intencion.putExtra("Foto",amigo.getFoto());
                contextPadre.startActivity(intencion);
            }
        });
        return item;
    }
}