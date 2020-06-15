package com.proyecto.discator.bean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class Album extends Artista
{
    private String nombreAlbum;
    private String año;
    private String genero;
    private String imagen;
    private float notaMedia;
    private ArrayList<Map> comentarios;

    public String getNombreAlbum() {
        return nombreAlbum;
    }

    public void setNombreAlbum(String nombreAlbum) {
        this.nombreAlbum = nombreAlbum;
    }

    public String getAño() {
        return año;
    }

    public void setAño(String año) {
        this.año = año;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public ArrayList<Map> getComentarios() {
        return comentarios;
    }

    public void setComentarios(ArrayList<Map> comentarios) {
        this.comentarios = comentarios;
    }

    public float getNotaMedia() {
        return notaMedia;
    }

    public void setNotaMedia(float notaMedia) {
        this.notaMedia = notaMedia;
    }

    //Comparador de las notas de los albumes para su posterior ordenación
    public static Comparator<Album> notaComparator = new Comparator<Album>() {
        @Override
        public int compare(Album album1, Album album2) {
            return (album2.getNotaMedia() < album1.getNotaMedia() ? -1 :
                    (album2.getNotaMedia() == album1.getNotaMedia() ? 0 : 1));
        }
    };

    //Comparador de los años en los que se públicaron los albumes para su posterior ordenación
    public static Comparator<Album> añoComparator = new Comparator<Album>() {
        @Override
        public int compare(Album album1, Album album2) {
            return (Integer.parseInt(album2.getAño()) < Integer.parseInt(album1.getAño()) ? -1 :
                    (Integer.parseInt(album2.getAño()) == Integer.parseInt(album1.getAño()) ? 0 : 1));
        }
    };
}
