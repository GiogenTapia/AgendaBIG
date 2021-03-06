package com.giogen.agendabig.Modelos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.giogen.agendabig.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Agenda> lista;
    private View.OnLongClickListener onLongClickListener;

    public AgendaAdapter(Context context, ArrayList<Agenda> lista){
        this.context=context;
        this.lista=lista;
    }


    //Este es un adaptador de fichas el cual nos sirve para crear nuestras vistas de las fichas en e
    //menu principal y para ello necesitamos nuestro item ficha la cual consta de un check box,
    // una imagen y el titulo de la ficha
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_agenda,viewGroup,false);
        itemView.setOnLongClickListener(onLongClickListener);
        ViewHolder viewHolder=new ViewHolder(itemView);
        return viewHolder;
    }

    //aqui se comprueba como es que se creara esta ficha, la cual si esta es una nota se le asignara
    //una imagen en concreto al igual que si es una tarea,  y tambien se checa el estado de esta nota.
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.titulo.setText(lista.get(i).getTitulo());
        if(lista.get(i).getTipo().equals("nota")){
            viewHolder.icono.setImageResource(R.drawable.nota);
        }else if(lista.get(i).getTipo().equals("tarea")){
            viewHolder.icono.setImageResource(R.drawable.tarea);
        }
        if(lista.get(i).getEstado().equals("true")){
            viewHolder.linearLayout.setBackgroundColor(Color.WHITE);
        }else if(lista.get(i).getEstado().equals("false")){

            viewHolder.linearLayout.setBackgroundColor(Color.YELLOW);
        }

        if (!lista.get(i).getFechaRecordatorio().equalsIgnoreCase("")){
            viewHolder.fecha.setText("Termina: "+lista.get(i).getFechaRecordatorio());
        }else {

            viewHolder.fecha.setText("");
        }

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }


    //obtenemos los diferentes elementos que tiene nuestro item ficha.
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titulo;
        ImageView icono;
        LinearLayout linearLayout;
        TextView fecha;

        public ViewHolder(View item){
            super(item);
            titulo=(TextView)item.findViewById(R.id.lblTituloIcono);
            icono=(ImageView)item.findViewById(R.id.imgvwIcono);
            linearLayout = item.findViewById(R.id.contenido);
            fecha= item.findViewById(R.id.lblFecha);

        }
    }

    public void setOnItemLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

}