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

import com.giogen.agendabig.datos.DaoArchivo;
import com.giogen.agendabig.datos.DaoRecordatorio;
import com.giogen.agendabig.Modelos.Recordatorio;

import android.widget.Toast;

import com.giogen.agendabig.datos.DaoAgenda;
import com.giogen.agendabig.Modelos.Agenda;
import com.giogen.agendabig.Modelos.AgendaAdapter;

import java.util.ArrayList;
import java.util.List;

public class Principal extends AppCompatActivity {

    private ArrayList<Agenda> listaAgendas = new ArrayList<>();
    private RecyclerView recyclerView;
    private EditText txtBusqueda;
    private GridLayoutManager layoutManager;

    /**
     * Metodo que se ejecuta cuando se crea la pantalla, en ella pedimos los permisos a realizar que son los perimos
     * para escribir en la memoria y obtenemos los diferentes elementos que tenemos en nuestra activity principal
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Principal.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Principal.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }

        setContentView(R.layout.activity_principal);

        recyclerView = findViewById(R.id.rcclrFicha);
        txtBusqueda = findViewById(R.id.txtBuscar);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        ActualizarRecycler();
    }


    /**
     * Metodo en el cual comienza el inflater de nuestro menu principal, el cual
     * son los tres puntos que estan en la parte superior derecha
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return true;
    }

    /**
     * Las diferentes opciones sobre el menu de tres puntos,
     * esta comienza las diferentes cosas que tenemos en ella.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itmAgregar:
                Intent inte = new Intent(getApplicationContext(), Agregar.class);
                startActivity(inte);
                return true;
            case R.id.itmAcercaDe:
                Toast.makeText(getApplicationContext(), "Juaquinz, Giogen, Monty", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Metodo del recycler el cual este comenzara a cargar todas nuestras opciones de las notas
     * y a cargar las agendas creadas con sus respectivos on long click. en esta parte se comenzara a crear
     * el adaptador.
     */
    public void ActualizarRecycler() {
        DaoAgenda dao = new DaoAgenda(this);
        listaAgendas = dao.SeleccionarTodos();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        AgendaAdapter adapter = new AgendaAdapter(this, listaAgendas);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View n = v;
                AlertDialog.Builder menu = new AlertDialog.Builder(v.getContext());
                Resources res = getResources();
                String estado = "";
                if (listaAgendas.get(recyclerView.getChildAdapterPosition(n)).getEstado().equalsIgnoreCase("false")) {
                    estado = res.getString(R.string.marcar2);
                    ;
                } else {
                    estado = res.getString(R.string.marcar);
                    ;
                }


                CharSequence[] opciones = {res.getString(R.string.ver), res.getString(R.string.modificar), res.getString(R.string.eliminar), estado};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0:
                                //Caso para ver las notas
                                Toast.makeText(getApplicationContext(), listaAgendas.get(recyclerView.getChildAdapterPosition(n)).getTitulo(), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Mostrar.class);
                                intent.putExtra("titulo", listaAgendas.get(recyclerView.getChildAdapterPosition(n)).getTitulo());
                                startActivity(intent);
                                break;
                            case 1:
                                //Caso para actualizar las notas
                                Intent intent1 = new Intent(getApplicationContext(), Actualizar.class);
                                intent1.putExtra("titulo", listaAgendas.get(recyclerView.getChildAdapterPosition(n)).getTitulo());
                                startActivity(intent1);
                                break;
                            case 2:
                                //Caso para eliminar
                                DaoAgenda daonuevo = new DaoAgenda(getApplicationContext());
                                DaoArchivo daoArchivo = new DaoArchivo(getApplicationContext());
                                DaoRecordatorio daoRecordatorio = new DaoRecordatorio(getApplicationContext());
                                if (daonuevo.eliminar(listaAgendas.get(recyclerView.getChildAdapterPosition(n)))) {
                                    List<Recordatorio> recordatorios = new ArrayList<>();
                                    recordatorios = daoRecordatorio.seleccionar(listaAgendas.get(recyclerView.getChildAdapterPosition(n)));
                                    daoArchivo.eliminarTodos(listaAgendas.get(recyclerView.getChildAdapterPosition(n)));
                                    for (int i = 0; i < recordatorios.size(); i++) {

                                        deleteNotify(recordatorios.get(i).getId() + "");
                                    }
                                    daoRecordatorio.eliminar(listaAgendas.get(recyclerView.getChildAdapterPosition(n)));


                                    Toast.makeText(getApplicationContext(), "Se elimino", Toast.LENGTH_LONG).show();
                                }
                                ActualizarRecycler();
                                break;
                            case 3:
                                //Caso para poder cambiar el estado de la nota
                                DaoAgenda daoAc = new DaoAgenda(getApplicationContext());
                                Agenda agendaA = listaAgendas.get(recyclerView.getChildAdapterPosition(n));
                                if (agendaA.getEstado().equalsIgnoreCase("false") && agendaA.getTipo().equalsIgnoreCase("tarea")) {

                                    agendaA.setEstado("true");
                                    daoAc.actualizar(agendaA);
                                    ActualizarRecycler();
                                } else {
                                    if (agendaA.getEstado().equalsIgnoreCase("true") && agendaA.getTipo().equalsIgnoreCase("tarea")) {

                                        agendaA.setEstado("false");
                                        daoAc.actualizar(agendaA);
                                        ActualizarRecycler();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "solo aplica para tareas", Toast.LENGTH_LONG).show();
                                    }
                                }


                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });
    }

    /**
     * Metodo de click donde se buscaran las diferentes agendas
     * @param v
     */
    public void buscar(View v) {
        Busqueda();
    }

    /**
     * Metodo que sirve para buscar una nota, el cual se obtiene del dao
     * y en el comenzamos a buscar esa nota y comenzamos a asignar el adapter al
     * linear layout que tenemos y  le asigna los diferentes argumentos
     */
    public void Busqueda() {
        DaoAgenda dao = new DaoAgenda(this);
        listaAgendas = dao.SeleccionarTodos(txtBusqueda.getText().toString());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        AgendaAdapter adapter = new AgendaAdapter(this, listaAgendas);

        recyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View n = v;
                Resources res = getResources();
                AlertDialog.Builder menu = new AlertDialog.Builder(v.getContext());
                String estado = "";
                if (listaAgendas.get(recyclerView.getChildAdapterPosition(n)).getEstado().equalsIgnoreCase("false")) {
                    estado = res.getString(R.string.marcar);
                } else {
                    estado = res.getString(R.string.marcar2);
                }
                CharSequence[] opciones = {res.getString(R.string.ver), res.getString(R.string.modificar), res.getString(R.string.eliminar), estado};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0:
                                //Toast.makeText(getApplicationContext(),lista.get(recyclerView.getChildAdapterPosition(n)).getTitulo(),Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Mostrar.class);
                                intent.putExtra("titulo", listaAgendas.get(recyclerView.getChildAdapterPosition(n)).getTitulo());
                                startActivity(intent);
                                break;
                            case 1:
                                Toast.makeText(getApplicationContext(), "Modificar", Toast.LENGTH_LONG).show();
                                break;
                            case 2:
                                DaoAgenda daonuevo = new DaoAgenda(getApplicationContext());
                                DaoArchivo daoArchivo = new DaoArchivo(getApplicationContext());
                                DaoRecordatorio daoRecordatorio = new DaoRecordatorio(getApplicationContext());
                                if (daonuevo.eliminar(listaAgendas.get(recyclerView.getChildAdapterPosition(n)))) {
                                    daoArchivo.eliminarTodos(listaAgendas.get(recyclerView.getChildAdapterPosition(n)));
                                    List<Recordatorio> recordatorios = new ArrayList<>();
                                    recordatorios = daoRecordatorio.seleccionar(listaAgendas.get(recyclerView.getChildAdapterPosition(n)));

                                    for (int i = 0; i < recordatorios.size(); i++) {
                                        deleteNotify(recordatorios.get(i).getId() + "");
                                    }
                                    daoRecordatorio.eliminar(listaAgendas.get(recyclerView.getChildAdapterPosition(n)));
                                    Toast.makeText(getApplicationContext(), "Se elimino", Toast.LENGTH_LONG).show();
                                }
                                ActualizarRecycler();
                                break;
                            case 3:
                                DaoAgenda daoAc = new DaoAgenda(getApplicationContext());
                                Agenda agendaA = listaAgendas.get(recyclerView.getChildAdapterPosition(n));
                                if (agendaA.getEstado().equalsIgnoreCase("false") && agendaA.getTipo().equalsIgnoreCase("tarea")) {

                                    agendaA.setEstado("true");
                                    daoAc.actualizar(agendaA);
                                    ActualizarRecycler();
                                } else {
                                    if (agendaA.getEstado().equalsIgnoreCase("true") && agendaA.getTipo().equalsIgnoreCase("tarea")) {

                                        agendaA.setEstado("false");
                                        daoAc.actualizar(agendaA);
                                        ActualizarRecycler();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "solo aplica para tareas", Toast.LENGTH_LONG).show();
                                    }
                                }

                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });
    }

    /**
     * Metodo para eliminar las diferentes notificaciones creadas.
     * @param tag
     */
    private void deleteNotify(String tag) {
        WorkManager.getInstance(this).cancelAllWorkByTag(tag);

    }
}