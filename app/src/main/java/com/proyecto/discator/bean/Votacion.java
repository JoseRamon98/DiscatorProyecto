package com.proyecto.discator.bean;

public class Votacion extends Album
{
    private String votacion;
    private String comentario;

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
}
