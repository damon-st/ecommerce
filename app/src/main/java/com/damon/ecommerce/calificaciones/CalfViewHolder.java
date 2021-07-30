package com.damon.ecommerce.calificaciones;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ecommerce.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class CalfViewHolder extends RecyclerView.ViewHolder {

    public ImageView producto_imagen,mas_opciones;
    public ImageView colorEstado;
    public TextView nombre_producto,nombre_vendedor,nombre_quien_reporta,comentario_quien_reporta,texto_activo,texto_descripcion_producto,texto_precio,texto_titulo,texto_reporte;
    public CircleImageView foto_profile_del_vendedor,foto_quien_reporta_usuario;
    public RelativeLayout relativeLayout;
    public RatingBar ratingBar;
    public ProgressBar progressProducto,progressUser;

    public View linea;

    public CalfViewHolder(@NonNull View itemView) {
        super(itemView);

        producto_imagen = itemView.findViewById(R.id.producto_imagen);
        nombre_producto = itemView.findViewById(R.id.texto_producto);
        foto_profile_del_vendedor = itemView.findViewById(R.id.profile_del_vendedor);
        nombre_vendedor = itemView.findViewById(R.id.texto_nombre);
        foto_quien_reporta_usuario = itemView.findViewById(R.id.foto_quien_reporta);
        nombre_quien_reporta = itemView.findViewById(R.id.nombre_quien_reporta);
        comentario_quien_reporta = itemView.findViewById(R.id.comentario_quien_reporta);
        texto_activo = itemView.findViewById(R.id.texto_activo);
        texto_descripcion_producto = itemView.findViewById(R.id.texto_descripcion_producto);
        relativeLayout = itemView.findViewById(R.id.relatc);
        mas_opciones = itemView.findViewById(R.id.mas_opciones);
        ratingBar = itemView.findViewById(R.id.ratingCalf);
        progressProducto = itemView.findViewById(R.id.progessProducto);
        colorEstado = itemView.findViewById(R.id.colorEstado);
        progressUser = itemView.findViewById(R.id.progressUser);
        texto_precio = itemView.findViewById(R.id.texto_precio);
        texto_titulo = itemView.findViewById(R.id.texto_fecha);
        texto_reporte = itemView.findViewById(R.id.texto_reporte);
        linea = itemView.findViewById(R.id.vista);
    }
}
