package com.damon.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ecommerce.Interface.ItemClickListener;
import com.damon.ecommerce.R;


public class ItemViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtprdouctName , txtProductDescription,txtProductPrice,txtProductState,txtfecha,txtVistas;
    public ImageView imageView,colorEstado,mas_opciones;
    public ItemClickListener listener;
    public ProgressBar progressBar;

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.producto_imagen);
        txtprdouctName = itemView.findViewById(R.id.texto_producto);
        txtProductDescription = itemView.findViewById(R.id.texto_activo_indicacion);
        txtProductPrice = itemView.findViewById(R.id.texto_precio);
        txtProductState = itemView.findViewById(R.id.texto_activo);
        txtfecha =itemView.findViewById(R.id.texto_fecha);
        colorEstado = itemView.findViewById(R.id.colorEstado);
        mas_opciones = itemView.findViewById(R.id.mas_opciones);
        txtVistas  = itemView.findViewById(R.id.texto_vistas);
        progressBar = itemView.findViewById(R.id.progresBar);



    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onClick(v,getAdapterPosition(),false);
    }
}
