package com.giogen.agendabig.ObjetosYDaos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.giogen.agendabig.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArchivoAdapter  extends  RecyclerView.Adapter<ArchivoAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Archivo> lista;
    private View.OnLongClickListener onLongClickListener;

    //private View.OnClickListener onClickListener;



    public ArchivoAdapter(Context context, ArrayList<Archivo> lista){
        this.context=context;
        this.lista=lista;
    }

    //Asignacion de el tipo de archivo que se estara realizando, en este caso se utiliza el
    //xml el cual tiene el formato que se adaptara cada archivo
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_archivo,viewGroup,false);
        itemView.setOnLongClickListener(onLongClickListener);
        //itemView.setOnClickListener(onClickListener);
        ViewHolder viewHolder=new ViewHolder(itemView);

        return viewHolder;
    }

    // Creacion de nuestro  elemento el cual se estara mostrando
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {


        if(lista.get(i).getTipo().equals("imagen")){
            viewHolder.icono.setImageResource(R.drawable.imagen);
        }else if(lista.get(i).getTipo().equals("video")){
            viewHolder.icono.setImageResource(R.drawable.video);
        }else if(lista.get(i).getTipo().equals("audio")){
            viewHolder.icono.setImageResource(R.drawable.audio);
        }else{
            viewHolder.icono.setImageResource(R.drawable.nota);
        }


    }



    @Override
    public int getItemCount() {
    return lista.size();
    }

    //Asignacion de la imagen y la descripcion
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView icono;
        public ViewHolder(View item){
            super(item);
            icono=(ImageView)item.findViewById(R.id.imgvwIconoAr);

        }
    }



    public void setOnItemLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }


}
