package com.proyecto.discator.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.proyecto.discator.AlbumActivity;
import com.proyecto.discator.R;
import com.proyecto.discator.bean.Album;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AlbumAdaptador extends ArrayAdapter<Album>
{
    private Context contextPadre;

    //Llegan dos parametros, context (será la actividad que llame a esta clase) y datos (array de información de datos a mostrar)
    public AlbumAdaptador(Context context, ArrayList<Album> datos) {
        super(context, R.layout.item_lista_albumes, datos);
        contextPadre=context;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Obtener el item del layout item_lista
        View item = LayoutInflater.from(getContext()).inflate(R.layout.item_lista_albumes, null);

        //Guardar información en el item del listado (solo guardamos el nombre del libro)
        final Album album=this.getItem(position);
        ImageView etiquetaImagen=item.findViewById((R.id.imagenAlbum));
        Picasso.with(getContext()).load(album.getImagen()).into(etiquetaImagen);
        TextView etiquetaNombreAlbum = item.findViewById(R.id.nombreAlbum);
        etiquetaNombreAlbum.setText(getItem(position).getNombreAlbum()+" ("+getItem(position).getAño()+")");
        TextView etiquetaNota=item.findViewById(R.id.notaAlbum);
        if (album.getNotaMedia()!=0)
            etiquetaNota.setText(String.valueOf(album.getNotaMedia()));

        //Accion al pulsar sobre el item, va a la pantalla que muestra detalles del libro pasandole como parametro el identificador
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intencion = new Intent(contextPadre, AlbumActivity.class);
                intencion.putExtra("Nombre", album.getNombreAlbum());
                intencion.putExtra("Nombre Artista", album.getNombre());
                contextPadre.startActivity(intencion);
            }
        });
        return item;
    }
}