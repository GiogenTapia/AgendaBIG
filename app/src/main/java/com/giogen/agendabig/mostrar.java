package com.giogen.agendabig;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import com.giogen.agendabig.Fragments.FragmentAudio;
import com.giogen.agendabig.Fragments.FragmentDetalle;
import com.giogen.agendabig.Fragments.FragmentSelector;
import com.giogen.agendabig.Fragments.FragmentVideo;
import com.giogen.agendabig.ObjetosYDaos.Archivo;
import com.giogen.agendabig.ObjetosYDaos.ArchivoAdapter;
import com.giogen.agendabig.ObjetosYDaos.DaoArchivo;
import com.giogen.agendabig.ObjetosYDaos.DaoFicha;
import com.giogen.agendabig.ObjetosYDaos.Ficha;

import java.util.ArrayList;
public class mostrar extends AppCompatActivity {
    private Ficha ficha;
    private ArrayList<Archivo> archivoArrayList;
    private TextView titulo;
    private TextView descripcion;
    private RadioButton nota;
    private RadioButton tarea;
    private TextView recordatorio;
    private String titulo1;


    //El metodo onCreate "inicializa" los valores de las cajas de texto y demas elementos de la interfas de la aplicacion
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar);

        Bundle bundle=getIntent().getExtras();
        titulo1=bundle.getString("titulo");
        //---------------------------------------------
        titulo=findViewById(R.id.txtTituloM);
        descripcion=findViewById(R.id.txtDescripcionM);
        nota=findViewById(R.id.rdNotaM);
        nota.setEnabled(false);
        tarea=findViewById(R.id.rdTareaM);
        tarea.setEnabled(false);
        recordatorio=findViewById(R.id.lblRecordatorioM);
        llenarCampos();
    }

    //Este metodo nos permite llenar los campos de las notas o tarea con toda la informacion
    // anteriormente guardada para mostrarla
    public void llenarCampos(){
        DaoArchivo daoArchivo=new DaoArchivo(getApplicationContext());
        DaoFicha daoFicha=new DaoFicha(getApplicationContext());
        Toast.makeText(this,titulo1,Toast.LENGTH_SHORT).show();
        ficha=daoFicha.seleccionarFicha(titulo1);
        archivoArrayList=daoArchivo.seleccionarArchivos(ficha);
        titulo.setText(ficha.getTitulo());
        descripcion.setText(ficha.getDescripcion());
        //Si lo que se guardo es una nota, se seleccionara el radiobuton de la nota
        if(ficha.getTipo().equals("nota")){
            nota.setChecked(true);
            //Si lo que se guardo es una tarea, se seleccionara el radiobuton de la tarea
        }else if(ficha.getTipo().equals("tarea")){
            tarea.setChecked(true);
            recordatorio.setText(ficha.getFechaRecordatorio());
        }
        ArchivoAdapter adapter=new ArchivoAdapter(this,archivoArrayList);
        //recyclerView.setAdapter(adapter);
        if(findViewById(R.id.contenedor)!=null&&(getSupportFragmentManager().findFragmentById(R.id.contenedor)==null)){
            FragmentSelector primerFragment=new FragmentSelector();
            primerFragment.setLista(archivoArrayList);
            primerFragment.setAdapter(adapter);
            getSupportFragmentManager().beginTransaction().add(R.id.contenedor,primerFragment).commit();
        }
    }

    //Metodo de click largo del elemento que se dara click largo de nuestras fichas
    //dependiendo de cual es el tipo de elemnto este hara una accion diferente.

    public void mostrarDetalle(Archivo archivo){
        //Si el elemento fue una imagen se comenzara a realizar un nuevo fragmento el cual  se encargara de visualizar una imagen
        if(archivo.getTipo().equals("imagen")){
            FragmentDetalle detalle=new FragmentDetalle();
            detalle.setArchivo(archivo);
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedor,detalle);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        //Si este es una imagen comenzara el fragmento que servira para visualizar nuestro video.
        else if(archivo.getTipo().equals("video")){
            FragmentVideo detalle1=new FragmentVideo();
            detalle1.setArchivo(archivo);
            FragmentTransaction transaction1=getSupportFragmentManager().beginTransaction();
            transaction1.replace(R.id.contenedor,detalle1);
            transaction1.addToBackStack(null);
            transaction1.commit();
        }
        //Y si este es un archivo de audio de comenzara con el fragmento de audio que comenzara a realizar
        //la reproduccion del audio guardado
        else if(archivo.getTipo().equals("audio")){
            FragmentAudio detalle2=new FragmentAudio();
            detalle2.setArchivo(archivo);
            FragmentTransaction transaction2=getSupportFragmentManager().beginTransaction();
            transaction2.replace(R.id.contenedor,detalle2);
            transaction2.addToBackStack(null);
            transaction2.commit();
        }
    }
}