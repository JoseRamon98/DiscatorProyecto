package com.proyecto.discator.sorters;

import com.proyecto.discator.bean.Votacion;

import java.util.ArrayList;
import java.util.Collections;

public class VotacionSorter
{
    ArrayList<Votacion> votaciones=new ArrayList<>();

    public VotacionSorter(ArrayList<Votacion> votaciones)
    {
        this.votaciones=votaciones;
    }

    public ArrayList<Votacion> getSortedByVotacion()
    {
        Collections.sort(votaciones, Votacion.notaValoracionComparator);
        return votaciones;
    }
}