package com.giogen.agendabig.ObjetosYDaos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

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

    //Metodo para seleccionar todos los archivos
    public ArrayList<Ficha> SeleccionarTodos(){
        ArrayList<Ficha>lista=new ArrayList<Ficha>();
        Cursor c=bd.query(BaseDeDatos.FICHA,BaseDeDatos.FICHACOLUMNS,null,null,null,null,null);
        while(c.moveToNext()){
            lista.add(new Ficha(c.getString(0),c.getString(1),c.getString(2),c.getString(3)
                    ,c.getString(4),c.getString(5)));
        }
        return lista;
    }


    //Metodo para seleccionar los archivos por medio del titulo
    public ArrayList<Ficha>  SeleccionarTodos(String titulo){
        ArrayList<Ficha>lista=new ArrayList<Ficha>();
        Cursor c=bd.query(BaseDeDatos.FICHA,BaseDeDatos.FICHACOLUMNS,"titulo like '%"+titulo+"%'",null,null,null,null);
        while(c.moveToNext()){
            lista.add(new Ficha(c.getString(0),c.getString(1),c.getString(2),c.getString(3)
                    ,c.getString(4),c.getString(5)));
        }
        return lista;
    }

    //Metodo para seleccionar una ficha por medio del titulo
    public Ficha seleccionarFicha(String titulo){
        Cursor c=bd.query(BaseDeDatos.FICHA,BaseDeDatos.FICHACOLUMNS,BaseDeDatos.FICHACOLUMNS[0]+"=?",new String[]{titulo},null,null,null);
        Ficha fi=null;
        while(c.moveToNext()){
            fi=new Ficha(c.getString(0),c.getString(1),c.getString(2),c.getString(3)
                    ,c.getString(4),c.getString(5));
        }
        return fi;
    }




}
