package com.giogen.agendabig.ObjetosYDaos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DaoFicha {

    private SQLiteDatabase bd;

    public DaoFicha(Context context){
        this.bd=new BaseDeDatos(context).getWritableDatabase();
    }

    //Metodo para insertar una ficha en nuestra base de datos.
    public long Insert(Ficha ficha){
        ContentValues cnt=new ContentValues();
        cnt.put(BaseDeDatos.FICHACOLUMNS[0],ficha.getTitulo());
        cnt.put(BaseDeDatos.FICHACOLUMNS[1],ficha.getDescripcion());
        cnt.put(BaseDeDatos.FICHACOLUMNS[2],ficha.getTipo());
        cnt.put(BaseDeDatos.FICHACOLUMNS[3],ficha.getFechacreacion());
        cnt.put(BaseDeDatos.FICHACOLUMNS[4],ficha.getFechaRecordatorio());
        cnt.put(BaseDeDatos.FICHACOLUMNS[5],ficha.getEstado());

        return  bd.insert(BaseDeDatos.FICHA,null,cnt);
    }

    //Metodo para eliminar una ficha, se debe eliminar todos los archivos que este contenga, y aque tiene una llave foranea
    public boolean eliminar(Ficha ficha){
        int no=bd.delete(BaseDeDatos.ARCHIVO,BaseDeDatos.ARCHIVOCOLUMNS[4]+"=?",new String[]{ficha.getTitulo()});
        if(no>0){
            int no1=bd.delete(BaseDeDatos.FICHA,BaseDeDatos.FICHACOLUMNS[0]+"=?",new String[]{ficha.getTitulo()});
            if(no1>0){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
}
