package com.proyecto.discator.bean;

import java.util.Comparator;

public class Lista extends Album
{
    private String nombreLista;
    private String propietario;
    private int likes;

    public String getNombreLista() {
        return nombreLista;
    }

    public void setNombreLista(String nombreLista) {
        this.nombreLista = nombreLista;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    //Comparador del numero de "me gusta" que tiene la lista
    public static Comparator<Lista> listaComparator = new Comparator<Lista>() {
        @Override
        public int compare(Lista lista1, Lista lista2) {
            return (lista2.getLikes() < lista1.getLikes() ? -1 :
                    (lista2.getLikes() == lista1.getLikes() ? 0 : 1));
        }
    };
}