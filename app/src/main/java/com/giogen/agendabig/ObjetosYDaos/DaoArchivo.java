package com.giogen.agendabig.ObjetosYDaos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;

public class DaoArchivo {
    private SQLiteDatabase bd;

    public DaoArchivo(Context context){
        this.bd=new BaseDeDatos(context).getWritableDatabase();
    }

    //METODO INSERTAR 1
    public long insertarArchivo(Archivo archivo){
        ContentValues cnt=new ContentValues();
        cnt.put(BaseDeDatos.ARCHIVOCOLUMNS[1],archivo.getDescripcion());
        cnt.put(BaseDeDatos.ARCHIVOCOLUMNS[2],archivo.getTipo());
        cnt.put(BaseDeDatos.ARCHIVOCOLUMNS[3],archivo.getRuta());
        cnt.put(BaseDeDatos.ARCHIVOCOLUMNS[4],archivo.getTitulo());
        return  bd.insert(BaseDeDatos.ARCHIVO,null,cnt);
    }

    //METODO INSERTAR VARIOS
    public boolean insertarVariosArchivos(ArrayList<Archivo> lista){
        int cont=0;
        for(Archivo ar :lista){
            if(this.insertarArchivo(ar)>0){
                cont++;
            }
        }
        if(cont==lista.size()){
            return true;
        }else{
            return false;
        }
    }

    //METODO SELECCIONAR ARCHIVOS
    public ArrayList<Archivo> seleccionarArchivos(Ficha ficha){
        ArrayList<Archivo>lista=new ArrayList<>();
        Cursor c=bd.query(BaseDeDatos.ARCHIVO,BaseDeDatos.ARCHIVOCOLUMNS,BaseDeDatos.ARCHIVOCOLUMNS[4]+"=?",new String[]{ficha.getTitulo()},null,null,null);
        Archivo ar=null;
        while(c.moveToNext()){
            ar=new Archivo(c.getInt(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4));
            lista.add(ar);
        }
        return lista;
    }

    //METODO ELIMINAR ARCHIVO
    public boolean eliminar(Archivo ar){
        int no=bd.delete(BaseDeDatos.ARCHIVO,BaseDeDatos.ARCHIVOCOLUMNS[0]+"=?",new String[]{ar.getIdArchivo()+""});
        if(no>0){
            return true;
        }else{
            return false;
        }
    }


    //METODO ELIMINAR FICHA
    public boolean eliminar(Ficha ficha){
        int no=bd.delete(BaseDeDatos.ARCHIVO,BaseDeDatos.ARCHIVOCOLUMNS[4]+"=?",new String[]{ficha.getTitulo()});
        if(no>0){
            return true;
        }else{
            return false;
        }
    }

}
