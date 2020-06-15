package com.proyecto.discator.sorters;

import com.proyecto.discator.bean.Lista;

import java.util.ArrayList;
import java.util.Collections;

public class ListaSorter
{
    ArrayList<Lista> listas=new ArrayList<>();

    public ListaSorter(ArrayList<Lista> listas)
    {
        this.listas=listas;
    }

    public ArrayList<Lista> getSortedByLikes()
    {
        Collections.sort(listas, Lista.listaComparator);
        return listas;
    }
}