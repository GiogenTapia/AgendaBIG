package com.giogen.agendabig;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.giogen.agendabig.ObjetosYDaos.DaoArchivo;
import com.giogen.agendabig.ObjetosYDaos.DaoRecordatorio;
import com.giogen.agendabig.ObjetosYDaos.Recordatorio;
import com.giogen.agendabig.R;
import android.widget.Toast;

import com.giogen.agendabig.ObjetosYDaos.DaoFicha;
import com.giogen.agendabig.ObjetosYDaos.Ficha;
import com.giogen.agendabig.ObjetosYDaos.FichaAdapter;

import java.util.ArrayList;
import java.util.List;

public class Principal extends AppCompatActivity {

    private ArrayList<Ficha> lista=new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText txtBuscar;
    private GridLayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Principal.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Principal.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }

        setContentView(R.layout.activity_principal);

        recyclerView=findViewById(R.id.rcclrFicha);
        txtBuscar=findViewById(R.id.txtBuscar);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        ActualizarRecycler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_principal,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.itmAgregar:
                Intent inte=new Intent(getApplicationContext(),Agregar.class);
                startActivity(inte);
                return true;
            case R.id.itmAcercaDe:
                Toast.makeText(getApplicationContext(),"Juaquinz, Giogen, Monty",Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ActualizarRecycler(){
        DaoFicha dao=new DaoFicha(this);
        lista=dao.SeleccionarTodos();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        FichaAdapter adapter=new FichaAdapter(this,lista);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View n=v;
                AlertDialog.Builder menu=new AlertDialog.Builder(v.getContext());
                Resources res=getResources();
                CharSequence[] opciones= {res.getString(R.string.ver),res.getString(R.string.modificar),res.getString(R.string.eliminar),res.getString(R.string.marcar)};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion){
                            case 0:
                                Toast.makeText(getApplicationContext(),lista.get(recyclerView.getChildAdapterPosition(n)).getTitulo(),Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(getApplicationContext(),mostrar.class);
                                intent.putExtra("titulo",lista.get(recyclerView.getChildAdapterPosition(n)).getTitulo());
                                startActivity(intent);
                                break;
                            case 1:
                                Intent intent1=new Intent(getApplicationContext(),actualizar.class);
                                intent1.putExtra("titulo",lista.get(recyclerView.getChildAdapterPosition(n)).getTitulo());
                                startActivity(intent1);
                                break;
                            case 2:
                                DaoFicha daonuevo=new DaoFicha(getApplicationContext());
                                DaoArchivo daoArchivo = new DaoArchivo(getApplicationContext());
                                DaoRecordatorio daoRecordatorio =  new DaoRecordatorio(getApplicationContext());
                                if(daonuevo.eliminar(lista.get(recyclerView.getChildAdapterPosition(n))) ){
                                    List<Recordatorio> recordatorios = new ArrayList<>();
                                    recordatorios = daoRecordatorio.seleccionar(lista.get(recyclerView.getChildAdapterPosition(n)));
                                    daoArchivo.eliminarTodos(lista.get(recyclerView.getChildAdapterPosition(n)));
                                    for (int i = 0; i< recordatorios.size();i++){

                                        deleteNotify(recordatorios.get(i).getId()+"");
                                    }
                                    daoRecordatorio.eliminar(lista.get(recyclerView.getChildAdapterPosition(n)));


                                    Toast.makeText(getApplicationContext(),"Se elimino",Toast.LENGTH_LONG).show();
                                }
                                ActualizarRecycler();
                                break;
                            case 3:
                                DaoFicha daoAc=new DaoFicha(getApplicationContext());
                                Ficha fichaA=lista.get(recyclerView.getChildAdapterPosition(n));
                                fichaA.setEstado("false");
                                daoAc.actualizar(fichaA);
                                ActualizarRecycler();
                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });
    }
    public void  buscar(View v){
        actualizarRecyclerBuscar();
    }

    public void actualizarRecyclerBuscar(){
        DaoFicha dao=new DaoFicha(this);
        lista=dao.SeleccionarTodos(txtBuscar.getText().toString());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        FichaAdapter adapter=new FichaAdapter(this,lista);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View n=v;
                AlertDialog.Builder menu=new AlertDialog.Builder(v.getContext());
                CharSequence[] opciones= {"Ver","Modificar","Eliminar","Marcar como terminado"};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion){
                            case 0:
                                //Toast.makeText(getApplicationContext(),lista.get(recyclerView.getChildAdapterPosition(n)).getTitulo(),Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(getApplicationContext(),mostrar.class);
                                intent.putExtra("titulo",lista.get(recyclerView.getChildAdapterPosition(n)).getTitulo());
                                startActivity(intent);
                                break;
                            case 1:
                                Toast.makeText(getApplicationContext(),"Modificar",Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                DaoFicha daonuevo=new DaoFicha(getApplicationContext());
                                DaoArchivo daoArchivo = new DaoArchivo(getApplicationContext());
                                DaoRecordatorio daoRecordatorio =  new DaoRecordatorio(getApplicationContext());
                                if(daonuevo.eliminar(lista.get(recyclerView.getChildAdapterPosition(n)))){
                                    daoArchivo.eliminarTodos(lista.get(recyclerView.getChildAdapterPosition(n)));
                                    List<Recordatorio> recordatorios = new ArrayList<>();
                                    recordatorios = daoRecordatorio.seleccionar(lista.get(recyclerView.getChildAdapterPosition(n)));

                                    for (int i = 0; i< recordatorios.size();i++){
                                        deleteNotify(recordatorios.get(i).getId()+"");
                                    }
                                    daoRecordatorio.eliminar(lista.get(recyclerView.getChildAdapterPosition(n)));
                                    Toast.makeText(getApplicationContext(),"Se elimino",Toast.LENGTH_LONG).show();
                                }
                                ActualizarRecycler();
                                break;
                            case 3:
                                DaoFicha daoAc=new DaoFicha(getApplicationContext());
                                Ficha fichaA=lista.get(recyclerView.getChildAdapterPosition(n));
                                fichaA.setEstado("false");
                                daoAc.actualizar(fichaA);
                                ActualizarRecycler();
                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });
    }
    private void deleteNotify(String tag){
        WorkManager.getInstance(this).cancelAllWorkByTag(tag);

    }
}