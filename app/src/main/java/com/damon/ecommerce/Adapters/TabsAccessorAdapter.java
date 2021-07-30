package com.damon.ecommerce.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.damon.ecommerce.FragmentsChat.MensajesComprasFragment;
import com.damon.ecommerce.FragmentsChat.MensajesTodoFragment;
import com.damon.ecommerce.FragmentsChat.MensajesVentasCompras;

public class TabsAccessorAdapter extends FragmentPagerAdapter {
///esta clase es la encargada de crear las tablas en la parte superior la que ara de retornar
    //los fragmentos

    public TabsAccessorAdapter( FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
        //creamos un condicional swicht para valorar cada caso asiendo instanciazs entre los fragments
        //ya que evaluara y nos ara navegar en las diferentes fragmentos
        switch (i){
            case 0:
                MensajesTodoFragment chatsFragment  =new MensajesTodoFragment();
                return chatsFragment;
//            case 1:
//             //   GroupsFragment groupsFragment  =new GroupsFragment();
//             //   return groupsFragment;
//                UsersFragment usersFragment = new UsersFragment();
//                return  usersFragment;
            case 1:
                MensajesComprasFragment contactsFragment  =new MensajesComprasFragment();
                return contactsFragment;
            case 2:
                MensajesVentasCompras requestsFragment  =new MensajesVentasCompras();
                return requestsFragment;
            default:
                return null;
        }


    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //cramos un switch para que recorra todas la posicionbes cambiando el titulo de cas fragment
        //aqui cambianos los nombres que tendran cada tabla
        switch (position){
            case 0:
                return  "TODO";
//            case 1:
//                return  "Usuarios";
            case 1:
                return  "COMPRAS";
            case 2:
                return  "VENTAS";
            default:
                return null;
        }
    }
}