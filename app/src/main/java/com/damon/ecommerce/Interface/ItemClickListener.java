package com.damon.ecommerce.Interface;

import android.view.View;
//clase de interfas en comunicarse para enventos de ccliks
public interface ItemClickListener {

    void  onClick(View view,int position,boolean isLongClick);
}
