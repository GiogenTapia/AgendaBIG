package com.giogen.agendabig.ObjetosYDaos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DaoRecordatorio {
    private SQLiteDatabase bd;

    public DaoRecordatorio(Context context){
        this.bd=new BaseDeDatos(context).getWritableDatabase();
    }

    //Insertamos un objeto completo de Recordatorio a nuestra base de datos

    public long Insert(Recordatorio recordatorio){
        ContentValues cnt=new ContentValues();
        cnt.put(BaseDeDatos.RECORDATORIOCOLUMNS[1],recordatorio.getDia());
        cnt.put(BaseDeDatos.RECORDATORIOCOLUMNS[2],recordatorio.getMes());
        cnt.put(BaseDeDatos.RECORDATORIOCOLUMNS[3],recordatorio.getAnio());
        cnt.put(BaseDeDatos.RECORDATORIOCOLUMNS[4],recordatorio.getHora());
        cnt.put(BaseDeDatos.RECORDATORIOCOLUMNS[5],recordatorio.getMinuto());
        cnt.put(BaseDeDatos.RECORDATORIOCOLUMNS[6],recordatorio.getTitulo());

        return  bd.insert(BaseDeDatos.RECORDATORIO,null,cnt);
    }

    //Obtenemos los recordatorios que se guardaron en la ficha
    public ArrayList<Recordatorio> seleccionar(Ficha ficha){
        ArrayList<Recordatorio>lista=new ArrayList<>();
        Cursor c=bd.query(BaseDeDatos.RECORDATORIO,BaseDeDatos.RECORDATORIOCOLUMNS,BaseDeDatos.RECORDATORIOCOLUMNS[6]+"=?",new String[]{ficha.getTitulo()},null,null,null);
        while(c.moveToNext()){
            lista.add(new Recordatorio(c.getInt(0),c.getInt(1),c.getInt(2),c.getInt(3),c.getInt(4),c.getInt(5),c.getString(6)));
        }
        c.close();
        return lista;
    }

    public boolean eliminar(Ficha ficha){
        int no=bd.delete(BaseDeDatos.RECORDATORIO,BaseDeDatos.RECORDATORIOCOLUMNS[6]+"=?",new String[]{ficha.getTitulo()+""});
        if(no>0){
            return true;
        }else{
            return false;
        }
    }

}
