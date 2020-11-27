package com.giogen.agendabig.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.giogen.agendabig.Modelos.Agenda;

import java.util.ArrayList;

public class DaoAgenda {

    private SQLiteDatabase bd;

    public DaoAgenda(Context context){
        this.bd=new BaseDeDatos(context).getWritableDatabase();
    }

    //Metodo para insertar una ficha en nuestra base de datos.
    public long Insert(Agenda agenda){
        ContentValues cnt=new ContentValues();
        cnt.put(BaseDeDatos.FICHACOLUMNS[0], agenda.getTitulo());
        cnt.put(BaseDeDatos.FICHACOLUMNS[1], agenda.getDescripcion());
        cnt.put(BaseDeDatos.FICHACOLUMNS[2], agenda.getTipo());
        cnt.put(BaseDeDatos.FICHACOLUMNS[3], agenda.getFechacreacion());
        cnt.put(BaseDeDatos.FICHACOLUMNS[4], agenda.getFechaRecordatorio());
        cnt.put(BaseDeDatos.FICHACOLUMNS[5], agenda.getEstado());

        return  bd.insert(BaseDeDatos.AGENDA,null,cnt);
    }

    //Metodo para eliminar una ficha, se debe eliminar todos los archivos que este contenga, y aque tiene una llave foranea
    public boolean eliminar(Agenda agenda){
        int no1=bd.delete(BaseDeDatos.AGENDA,BaseDeDatos.FICHACOLUMNS[0]+"=?",new String[]{agenda.getTitulo()});
        if(no1>0){
            return true;
        }else{
            return false;
        }
    }

    //Metodo para seleccionar todos los archivos
    public ArrayList<Agenda> SeleccionarTodos(){
        ArrayList<Agenda>lista=new ArrayList<Agenda>();
        Cursor c=bd.query(BaseDeDatos.AGENDA,BaseDeDatos.FICHACOLUMNS,null,null,null,null,null);
        while(c.moveToNext()){
            lista.add(new Agenda(c.getString(0),c.getString(1),c.getString(2),c.getString(3)
                    ,c.getString(4),c.getString(5)));
        }
        return lista;
    }


    //Metodo para seleccionar los archivos por medio del titulo
    public ArrayList<Agenda>  SeleccionarTodos(String titulo){
        ArrayList<Agenda>lista=new ArrayList<Agenda>();
        Cursor c=bd.query(BaseDeDatos.AGENDA,BaseDeDatos.FICHACOLUMNS,"titulo like '%"+titulo+"%'",null,null,null,null);
        while(c.moveToNext()){
            lista.add(new Agenda(c.getString(0),c.getString(1),c.getString(2),c.getString(3)
                    ,c.getString(4),c.getString(5)));
        }
        return lista;
    }

    //Metodo para seleccionar una ficha por medio del titulo
    public Agenda seleccionarFicha(String titulo){
        Cursor c=bd.query(BaseDeDatos.AGENDA,BaseDeDatos.FICHACOLUMNS,BaseDeDatos.FICHACOLUMNS[0]+"=?",new String[]{titulo},null,null,null);
        Agenda fi=null;
        while(c.moveToNext()){
            fi=new Agenda(c.getString(0),c.getString(1),c.getString(2),c.getString(3)
                    ,c.getString(4),c.getString(5));
        }
        return fi;
    }

    public boolean actualizar(Agenda agenda){
        ContentValues cnt=new ContentValues();
        cnt.put(BaseDeDatos.FICHACOLUMNS[1], agenda.getDescripcion());
        cnt.put(BaseDeDatos.FICHACOLUMNS[2], agenda.getTipo());
        cnt.put(BaseDeDatos.FICHACOLUMNS[3], agenda.getFechacreacion());
        cnt.put(BaseDeDatos.FICHACOLUMNS[4], agenda.getFechaRecordatorio());
        cnt.put(BaseDeDatos.FICHACOLUMNS[5], agenda.getEstado());
        long no=bd.update(BaseDeDatos.AGENDA,cnt,BaseDeDatos.FICHACOLUMNS[0]+"=?",new String[]{agenda.getTitulo()});
        if(no>0){
            return true;
        }else{
            return false;
        }
    }



}
