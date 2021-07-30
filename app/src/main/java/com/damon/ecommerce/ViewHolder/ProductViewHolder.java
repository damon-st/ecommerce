package com.damon.ecommerce.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ecommerce.Interface.ItemClickListener;
import com.damon.ecommerce.R;

//clase encargada de las vistas de productos
public class ProductViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtprdouctName , txtProductDescription,txtProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.produc_image);
        txtprdouctName = itemView.findViewById(R.id.product_Name);
        txtProductDescription = itemView.findViewById(R.id.product_Description);
        txtProductPrice = itemView.findViewById(R.id.product_Price);


    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
         listener.onClick(v,getAdapterPosition(),false);
    }
}
