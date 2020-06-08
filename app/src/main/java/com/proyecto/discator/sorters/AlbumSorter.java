package com.proyecto.discator.sorters;

import com.proyecto.discator.bean.Album;

import java.util.ArrayList;
import java.util.Collections;

public class AlbumSorter
{
    ArrayList<Album> albums=new ArrayList<>();

    public AlbumSorter(ArrayList<Album> albums)
    {
        this.albums=albums;
    }

    public ArrayList<Album> getSortedByNota()
    {
        Collections.sort(albums, Album.notaComparator);
        return albums;
    }

    public ArrayList<Album> getSortedByAño()
    {
        Collections.sort(albums, Album.añoComparator);
        return albums;
    }
}