package com.proyecto.discator.bean;

import java.util.Comparator;

public class Votacion extends Album
{
    private String votacion;
    private String comentario;
    private float notaValoracion;

    public String getVotacion() {
        return votacion;
    }

    public void setVotacion(String votacion) {
        this.votacion = votacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public void setNotaValoracion(float notaValoracion) {
        this.notaValoracion=notaValoracion;
    }

    public float getNotaValoracion()
    {
        return notaValoracion;
    }

    //Comparador de las notas de los albumes para su posterior ordenación
    public static Comparator<Votacion> notaValoracionComparator = new Comparator<Votacion>() {
        @Override
        public int compare(Votacion votacion1, Votacion votacion2) {
            return (votacion2.getNotaValoracion() < votacion1.getNotaValoracion() ? -1 :
                    (votacion2.getNotaValoracion() == votacion1.getNotaValoracion() ? 0 : 1));
        }
    };
}
