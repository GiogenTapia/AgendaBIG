package com.giogen.agendabig;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.WorkManager;

import com.giogen.agendabig.Notificacion.PlanificarAlarma;
import com.giogen.agendabig.Notificacion.WorkManagerNotify;
import com.giogen.agendabig.ObjetosYDaos.Archivo;
import com.giogen.agendabig.ObjetosYDaos.ArchivoAdapter;
import com.giogen.agendabig.ObjetosYDaos.DaoArchivo;
import com.giogen.agendabig.ObjetosYDaos.DaoFicha;
import com.giogen.agendabig.ObjetosYDaos.DaoRecordatorio;
import com.giogen.agendabig.ObjetosYDaos.Ficha;
import com.giogen.agendabig.ObjetosYDaos.Recordatorio;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class actualizar extends AppCompatActivity {
    private Ficha ficha;
    private ArrayList<Archivo> lista;
    private ArrayList<Recordatorio> listaRecordatorio;
    private ArrayList<Recordatorio> recordatorios;
    private TextView titulo;
    private TextView descripcion;
    private RadioButton nota;
    private RadioButton tarea;
    private TextView recordatorio;
    private ImageView imagen;
    String ruta = "";
    private String titulo1;
    private RecyclerView recyclerView;
    private Button btnTerminacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);

        Bundle bundle = getIntent().getExtras();
        titulo1 = bundle.getString("titulo");
        //---------------------------------------------
        titulo = findViewById(R.id.txtTituloA);
        descripcion = findViewById(R.id.txtDescripcionA);
        nota = findViewById(R.id.rdNotaA);
        btnTerminacion = findViewById(R.id.btnAgregarAlarmaA);
        imagen = findViewById(R.id.Imagen);
        nota.setEnabled(false);
        tarea = findViewById(R.id.rdTareaA);
        tarea.setEnabled(false);
        titulo.setEnabled(false);
        recordatorio = findViewById(R.id.lblRecordatorioA);
        recyclerView = findViewById(R.id.rcclcArchivoListaA);
         recordatorios = new ArrayList<>();

        llenarCampos();
    }

    //Metodo en el cual podemos obtener todos los datos de la ficha la cual se desea actualizar
    public void llenarCampos() {
        DaoArchivo daoArchivo = new DaoArchivo(getApplicationContext());
        DaoFicha daoFicha = new DaoFicha(getApplicationContext());
        DaoRecordatorio daoRecordatorio = new DaoRecordatorio(getApplicationContext());
        Toast.makeText(this, titulo1, Toast.LENGTH_SHORT).show();
        ficha = daoFicha.seleccionarFicha(titulo1);
        lista = daoArchivo.seleccionarArchivos(ficha);
        listaRecordatorio = daoRecordatorio.seleccionar(ficha);
        titulo.setText(ficha.getTitulo());
        descripcion.setText(ficha.getDescripcion());
        if (ficha.getTipo().equals("nota")) {
            nota.setChecked(true);
            btnTerminacion.setEnabled(false);
        } else if (ficha.getTipo().equals("tarea")) {
            tarea.setChecked(true);
            recordatorio.setText(ficha.getFechaRecordatorio());
            btnTerminacion.setEnabled(true);
        }
        actualizarArchivos();
    }

    public void actualizarArchivos() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        recyclerView.setLayoutManager(linearLayoutManager);
        final ArchivoAdapter adapter = new ArchivoAdapter(this, lista);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View n = v;
                AlertDialog.Builder menu = new AlertDialog.Builder(v.getContext());
                CharSequence[] opciones = {"Eliminar"};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0:
                                // Toast.makeText(getApplicationContext(),"Eliminar: "+lista.get(recyclerView.getChildAdapterPosition(n)),Toast.LENGTH_LONG).show();
                                DaoArchivo da = new DaoArchivo(getApplicationContext());
                                da.eliminar(lista.get(recyclerView.getChildAdapterPosition(n)));
                                lista.remove(lista.get(recyclerView.getChildAdapterPosition(n)));
                                actualizarArchivos();
                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });
    }

    public void setTerminar(View v) {
        Calendar fechaActual = Calendar.getInstance();
        Calendar fechaActual1 = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                recordatorio.setText(recordatorio.getText().toString() + "-" + hourOfDay + ":" + minute);
            }
        }, fechaActual1.get(Calendar.HOUR), fechaActual1.get(Calendar.MINUTE), false);
        timePickerDialog.show();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                recordatorio.setText(dayOfMonth + "-" + month + "-" + year);

            }
        }, fechaActual.get(Calendar.YEAR), fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void addRecordatorio(View v) {
        Calendar fechaActual = Calendar.getInstance();
        Calendar fechaActual1 = Calendar.getInstance();
        final Recordatorio recordatorio = new Recordatorio();
        recordatorio.setTitulo(ficha.getTitulo());
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //recordatorio.setText(lblAlarma.getText().toString()+"-"+hourOfDay+":"+minute);
                recordatorio.setMinuto(minute);
                recordatorio.setHora(hourOfDay);
                listaRecordatorio.add(recordatorio);
                recordatorios.add(recordatorio);
            }
        }, fechaActual1.get(Calendar.HOUR), fechaActual1.get(Calendar.MINUTE), false);
        timePickerDialog.show();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //recordatorio.setText(dayOfMonth+"-"+month+"-"+year);
                recordatorio.setMes(month+1);
                recordatorio.setAnio(year);
                recordatorio.setDia(dayOfMonth);

            }
        }, fechaActual.get(Calendar.YEAR), fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void btnVerRecordatoriosAA(View v) {
        AlertDialog.Builder menu = new AlertDialog.Builder(v.getContext());
        CharSequence[] opciones = new CharSequence[listaRecordatorio.size()];
        for (int i = 0; i < opciones.length; i++) {
            opciones[i] = listaRecordatorio.get(i).toString();
        }
        menu.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int opcion) {
                //Para eliminarlas
            }
        });
        menu.create().show();
    }

    public void btnGuardarAOnClick(View v) {

        try{
            for (int i=0;i<lista.size();i++){
                lista.get(i).setTitulo(titulo.getText().toString());
            }

            for (int i=0;i<recordatorios.size();i++){
                recordatorios.get(i).setTitulo(titulo.getText().toString());

            }

            String title = titulo.getText().toString();
            String desc = descripcion.getText().toString();
            Calendar fechaActual = Calendar.getInstance();
            String fechaRegistro = fechaActual.get(Calendar.DAY_OF_MONTH) + "-" + fechaActual.get(Calendar.MONTH) + "-" + fechaActual.get(Calendar.YEAR);

            if (nota.isChecked()) {
                Ficha ficha = new Ficha(title, desc, "nota", fechaRegistro, "", "true");
                DaoFicha dao = new DaoFicha(getApplicationContext());
                DaoArchivo daoArchivo = new DaoArchivo(getApplicationContext());
                DaoRecordatorio daoRecordatorio = new DaoRecordatorio(getApplicationContext());
                if (dao.actualizar(ficha)) {
                        Toast.makeText(this, "Se inserto los archivos", Toast.LENGTH_LONG).show();
                        for (int i = 0; i < recordatorios.size(); i++) {
                            recordatorios.get(i).setTitulo(titulo.getText().toString());
                            if (daoRecordatorio.Insert(recordatorios.get(i)) > 0) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
                                Date date = new Date();
                                date = simpleDateFormat.parse(recordatorios.get(i).toString());
                                Date justNow = new Date();
                                long alertTime = date.getTime() - justNow.getTime();
                                Data data = saveData(titulo.getText().toString(),desc,recordatorios.get(i).getId());
                                WorkManagerNotify.saveNotification(alertTime,data, recordatorios.get(i).getId()+"");
                        }

                    }
                }

            } else if (tarea.isChecked()) {
                Ficha tarea = new Ficha(title, desc, "tarea", fechaRegistro, recordatorio.getText().toString(), "true");
                DaoFicha dao = new DaoFicha(getApplicationContext());
                DaoArchivo daoArchivo = new DaoArchivo(getApplicationContext());
                DaoRecordatorio daoRecordatorio = new DaoRecordatorio(getApplicationContext());
                if (dao.actualizar(tarea)) {
                    Toast.makeText(this, "Se actualizo", Toast.LENGTH_LONG).show();
                        for (int i = 0; i < recordatorios.size(); i++) {
                            recordatorios.get(i).setTitulo(titulo.getText().toString());
                            if (daoRecordatorio.Insert(recordatorios.get(i)) > 0) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH:mm");
                                Date date = new Date();
                                date = simpleDateFormat.parse(recordatorios.get(i).toString());
                                Date justNow = new Date();
                                long alertTime = date.getTime() - justNow.getTime();
                                Data data = saveData(titulo.getText().toString(),desc,recordatorios.get(i).getId());
                                WorkManagerNotify.saveNotification(alertTime,data, recordatorios.get(i).getId()+"");

                            }


                    }
                }
            }
            Intent intent = new Intent(getApplicationContext(), Principal.class);
            startActivity(intent);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
        }

    }

    public void btnArchivosAOnClick(View v) {
        final View vista = v;
        AlertDialog.Builder menu = new AlertDialog.Builder(v.getContext());
        Resources res = getResources();
        CharSequence[] opciones = {res.getString(R.string.audio), res.getString(R.string.video), res.getString(R.string.foto), res.getString(R.string.archivo)};
        menu.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int opcion) {
                switch (opcion) {
                    case 0:
                        grabarAudio();
                        break;
                    case 1:
                        tomarVideo();
                        break;
                    case 2:
                        TomarImagen();
                        break;
                    case 3:
                        BuscarImagenes();
                        break;
                }
            }
        });
        menu.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    Uri audio = data.getData();
                    String cadena4 = audio.toString();
                    Archivo archivo4 = new Archivo(1, descripcion.getText().toString(), "audio", cadena4, titulo.getText().toString());
                    lista.add(archivo4);
                    DaoArchivo da4 = new DaoArchivo(getApplicationContext());
                    da4.insertarArchivo(archivo4);
                    actualizarArchivos();


                    break;
                case 2:
                    Uri vi = data.getData();
                    String cadena1 = vi.toString();
                    Archivo archivo1 = new Archivo(1, descripcion.getText().toString(), "video", cadena1, titulo.getText().toString());
                    lista.add(archivo1);
                    DaoArchivo da1 = new DaoArchivo(getApplicationContext());
                    da1.insertarArchivo(archivo1);
                    actualizarArchivos();
                    break;
                case 3:
                    imagen.setImageURI(uri);
                    Archivo archivo = new Archivo(1, descripcion.getText().toString(), "imagen", ruta, titulo.getText().toString());
                    lista.add(archivo);
                    DaoArchivo da = new DaoArchivo(getApplicationContext());
                    da.insertarArchivo(archivo);
                    actualizarArchivos();
                    break;
                case 4:
                    Uri ima = data.getData();
                    String cadena = ima.toString();
                    imagen.setImageURI(ima);
                    Archivo archivo2 = new Archivo(1, descripcion.getText().toString(), "imagen", cadena, titulo.getText().toString());
                    lista.add(archivo2);
                    DaoArchivo da2 = new DaoArchivo(getApplicationContext());
                    da2.insertarArchivo(archivo2);
                    actualizarArchivos();
                    break;
            }
        }
    }

    Uri uri;


    public void TomarImagen() {
        //uri=null;
        Intent camaraFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camaraFoto.resolveActivity(getPackageManager()) != null) {
            File foto = null;
            foto = createPhotoFile();
            try {

                uri = FileProvider.getUriForFile(getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".provider", foto);
                camaraFoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                startActivityForResult(camaraFoto, 3);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error en la foto", Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createPhotoFile() {
        try {
            String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String nameFoto = "imagen" + time;
            File almacen = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File fotoFile = File.createTempFile(nameFoto, ".jpg", almacen);
            ruta= fotoFile.getAbsolutePath();
            return fotoFile;
        } catch (Exception e) {
            return null;
        }

    }

    public void tomarVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, 2);
    }

    public void grabarAudio() {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        startActivityForResult(intent, 1);
    }

    private void BuscarImagenes() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, 4);
    }

    private void deleteNotify(String tag){
        WorkManager.getInstance(this).cancelAllWorkByTag(tag);
        Toast.makeText(this, "Para ver si se elimina", Toast.LENGTH_SHORT).show();
    }

    private String generateKey(){
        return UUID.randomUUID().toString();
    }

    private Data saveData (String title, String content, int idNotification){
        return new Data.Builder()
                .putString("Title",title)
                .putString("Content",content)
                .putInt("id",idNotification).build();
    }

}
