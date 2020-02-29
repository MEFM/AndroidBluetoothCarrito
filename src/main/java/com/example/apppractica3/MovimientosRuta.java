package com.example.apppractica3;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;
import java.util.*;

public class MovimientosRuta {
    char movimiento;
    char tiempo;

    public MovimientosRuta(char movimiento, char tiempo){
        this.movimiento = movimiento;
        this.tiempo = tiempo;
    }

    char getMovimiento(){
        return this.movimiento;
    }
    char getTiempo(){
        return this.tiempo;
    }
}


class Ruta{
    ArrayList<MovimientosRuta> ruta;
    String nombre;
    public Ruta(String nombre, ArrayList<MovimientosRuta> ruta){
        this.ruta = ruta;
        this.nombre = nombre;
    }
}