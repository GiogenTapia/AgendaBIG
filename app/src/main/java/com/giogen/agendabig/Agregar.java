package com.giogen.agendabig;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.giogen.agendabig.Notificacion.PlanificarAlarma;
import com.giogen.agendabig.ObjetosYDaos.Archivo;
import com.giogen.agendabig.ObjetosYDaos.ArchivoAdapter;
import com.giogen.agendabig.ObjetosYDaos.DaoArchivo;
import com.giogen.agendabig.ObjetosYDaos.DaoFicha;
import com.giogen.agendabig.ObjetosYDaos.DaoRecordatorio;
import com.giogen.agendabig.ObjetosYDaos.Ficha;
import com.giogen.agendabig.ObjetosYDaos.Recordatorio;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Agregar extends AppCompatActivity {
    private Button btnAgregarAlarma;
    private TextView lblAlarma;
    private TextView txtTitulo;
    private TextView txtDescripcion;
    private RadioGroup rdg;
    private RadioButton rdNota;
    private RadioButton rdTarea;
    private ImageView imagen;
    private RecyclerView recyclerView;
    //private LinearLayout lnrRecordatorio;
    private String tipo;
    private ArrayList<Archivo> lista = new ArrayList<>();
    private ArrayList<Recordatorio> recordatorios = new ArrayList<>();
    Uri uri;
    String ruta = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar);
        btnAgregarAlarma = findViewById(R.id.btnAgregarAlarma);
        lblAlarma = findViewById(R.id.lblRecordatorio);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        imagen = findViewById(R.id.Imagen);
        recyclerView = findViewById(R.id.rcclcArchivoLista);
        rdg = findViewById(R.id.rdgTipo);
        rdNota = findViewById(R.id.rdNota);
        rdTarea = findViewById(R.id.rdTarea);
        btnAgregarAlarma.setEnabled(false);

        //lnrRecordatorio.findViewById(R.id.lnrRecordatoria);


        rdg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rdNota.isChecked()) {
                    //lnrRecordatorio.setVisibility(View.INVISIBLE);
                    tipo = "nota";
                    btnAgregarAlarma.setEnabled(false);
                } else {
                    //lnrRecordatorio.setVisibility(View.VISIBLE);
                    btnAgregarAlarma.setEnabled(true);
                    tipo = "tarea";
                }


            }
        });
    }

    public void btnAgregarAlarmaOnClick(View v) {
        Calendar fechaActual = Calendar.getInstance();
        Calendar fechaActual1 = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                lblAlarma.setText(lblAlarma.getText().toString() + "-" + hourOfDay + ":" + minute);
            }
        }, fechaActual1.get(Calendar.HOUR), fechaActual1.get(Calendar.MINUTE), false);
        timePickerDialog.show();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                lblAlarma.setText(dayOfMonth + "-" + month + "-" + year);

            }
        }, fechaActual.get(Calendar.YEAR), fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    public void btnGuardarOnClick(View v) {
        try {

            for (int i=0;i<lista.size();i++){
                lista.get(i).setTitulo(txtTitulo.getText().toString());
            }

            for (int i=0;i<recordatorios.size();i++){
                recordatorios.get(i).setTitulo(txtTitulo.getText().toString());
            }

            String titulo = txtTitulo.getText().toString();
            String descripcion = txtDescripcion.getText().toString();
            Calendar fechaActual = Calendar.getInstance();
            String fechaRegistro = fechaActual.get(Calendar.DAY_OF_MONTH) + "-" + fechaActual.get(Calendar.MONTH) + "-" + fechaActual.get(Calendar.YEAR);
            if (tipo.equals("nota")) {
                Ficha ficha = new Ficha(titulo, descripcion, tipo, fechaRegistro, "", "true");
                DaoFicha dao = new DaoFicha(getApplicationContext());
                DaoArchivo daoArchivo = new DaoArchivo(getApplicationContext());
                DaoRecordatorio daoRecordatorio = new DaoRecordatorio(getApplicationContext());
                if (dao.Insert(ficha) > 0) {
                    Toast.makeText(this, "Se inserto", Toast.LENGTH_LONG).show();
                    if (daoArchivo.insertarVariosArchivos(lista)) {
                        Toast.makeText(this, "Se inserto los archivos", Toast.LENGTH_LONG).show();
                        for (int i = 0; i < recordatorios.size(); i++) {
                            recordatorios.get(i).setTitulo(txtTitulo.getText().toString());
                            if (daoRecordatorio.Insert(recordatorios.get(i)) > 0) {
                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                Intent intent = new Intent(getApplicationContext(), PlanificarAlarma.class);
                                intent.putExtra("titulo", txtTitulo.getText().toString());
                                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, recordatorios.get(i).milis(), pi);
                                Toast.makeText(getApplicationContext(), "s" + recordatorios.get(i).milis(), Toast.LENGTH_LONG).show();
                            }
                        }
                        Intent intent = new Intent(getApplicationContext(), Principal.class);
                        startActivity(intent);
                    }
                }

            } else if (tipo.equals("tarea")) {
                Ficha tarea = new Ficha(titulo, descripcion, tipo, fechaRegistro, lblAlarma.getText().toString(), "true");
                DaoFicha dao = new DaoFicha(getApplicationContext());
                DaoArchivo daoArchivo = new DaoArchivo(getApplicationContext());
                DaoRecordatorio daoRecordatorio = new DaoRecordatorio(getApplicationContext());
                if (dao.Insert(tarea) > 0) {
                    Toast.makeText(this, "Se inserto", Toast.LENGTH_LONG).show();
                    if (daoArchivo.insertarVariosArchivos(lista)) {
                        Toast.makeText(this, "Se inserto los archivos", Toast.LENGTH_LONG).show();
                        for (int i = 0; i < recordatorios.size(); i++) {
                            recordatorios.get(i).setTitulo(txtTitulo.getText().toString());
                            if (daoRecordatorio.Insert(recordatorios.get(i)) > 0) {
                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                Intent intent = new Intent(getApplicationContext(), PlanificarAlarma.class);
                                intent.putExtra("titulo", txtTitulo.getText().toString());
                                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, recordatorios.get(i).milis(), pi);
                                Toast.makeText(getApplicationContext(), "s" + recordatorios.get(i).milis(), Toast.LENGTH_LONG).show();
                            }
                        }
                        Intent intent = new Intent(getApplicationContext(), Principal.class);
                        startActivity(intent);
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Faltan datos",Toast.LENGTH_LONG).show();
        }
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
                Resources res = getResources();
                CharSequence[] opciones = {res.getString(R.string.eliminar)};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0:

                                // Toast.makeText(getApplicationContext(),"Eliminar: "+lista.get(recyclerView.getChildAdapterPosition(n)),Toast.LENGTH_LONG).show();
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

    public void btnArchivosOnClick(View v) {
        final View vista = v;
        AlertDialog.Builder menu = new AlertDialog.Builder(v.getContext());
        Resources res = getResources();

        CharSequence[] opciones = {res.getString(R.string.audio), res.getString(R.string.video), res.getString(R.string.foto), res.getString(R.string.archivo)};
        menu.setItems(opciones, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
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
                        try {
                            TomarImagen();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                    //Caso para los audios
                    Uri audio = data.getData();
                    String cadena4 = audio.toString();
                    Archivo archivo4 = new Archivo(1, txtDescripcion.getText().toString(), "audio", cadena4, txtTitulo.getText().toString());
                    lista.add(archivo4);
                    actualizarArchivos();

                    break;
                case 2:
                    //Caso para los videos.
                    Uri vi = data.getData();
                    String cadena1 = vi.toString();
                    Archivo archivo1 = new Archivo(1, txtDescripcion.getText().toString(), "video", cadena1, txtTitulo.getText().toString());
                    lista.add(archivo1);
                    actualizarArchivos();

                    break;
                case 3:

                    try {
                        //caso para el resultado de la camara
                        imagen.setImageURI(uri);
                        Archivo archivo = new Archivo(1, txtDescripcion.getText().toString(), "imagen", ruta, txtTitulo.getText().toString());
                        lista.add(archivo);
                        actualizarArchivos();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e + "", Toast.LENGTH_LONG).show();
                    }


                    break;
                case 4:

                    //Este caso es para el seleccionar un archivo
                    Uri ima = data.getData();
                    String cadena = ima.toString();
                    Uri r = Uri.parse(cadena);
                    imagen.setImageURI(r);
                    Archivo archivo2 = new Archivo(1, txtDescripcion.getText().toString(), "imagen", cadena, txtTitulo.getText().toString());
                    lista.add(archivo2);
                    actualizarArchivos();

                    break;
            }
        }
    }


    public void TomarImagen() throws IOException {

        Intent camaraFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File foto = createPhotoFile();
            uri = FileProvider.getUriForFile(getApplicationContext(),
                    "com.giogen.agendabig.provider", foto);
            camaraFoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            startActivityForResult(camaraFoto, 3);



    }

    private File createPhotoFile() throws IOException {

        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nameFoto = "imagen" + time;
        File almacen = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File fotoFile = File.createTempFile(nameFoto, ".jpg", almacen);
        ruta = fotoFile.getAbsolutePath();
        return fotoFile;


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

    public void MostarRecordatorios(View v) {

        AlertDialog.Builder menu = new AlertDialog.Builder(v.getContext());
        CharSequence[] opciones = new CharSequence[recordatorios.size()];
        for (int i = 0; i < opciones.length; i++) {
            opciones[i] = recordatorios.get(i).toString();
        }
        menu.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int opcion) {
                //Para eliminarlas
            }
        });
        menu.create().show();
    }

    public void AgregarRecordatorio(View v) {
        Calendar fechaActual = Calendar.getInstance();
        Calendar fechaActual1 = Calendar.getInstance();
        final Recordatorio recordatorio = new Recordatorio();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                recordatorio.setHora(hourOfDay);
                recordatorio.setMinuto(minute);
                recordatorios.add(recordatorio);
            }
        }, fechaActual1.get(Calendar.HOUR), fechaActual1.get(Calendar.MINUTE), false);
        timePickerDialog.show();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                recordatorio.setDia(dayOfMonth);
                recordatorio.setMes(month);
                recordatorio.setAnio(year);
            }
        }, fechaActual.get(Calendar.YEAR), fechaActual.get(Calendar.MONTH), fechaActual.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


}