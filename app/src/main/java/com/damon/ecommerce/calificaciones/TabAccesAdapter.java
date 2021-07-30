package com.damon.ecommerce.calificaciones;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccesAdapter extends FragmentPagerAdapter {

    String pid;
    public TabAccesAdapter(@NonNull FragmentManager fm, String pid) {
        super(fm);
        this.pid = pid;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                CalificacionesPositivas calificacionesPositivas = new CalificacionesPositivas(pid);
                return  calificacionesPositivas;

            case 1:
                CalificacionNeutras calificacionNeutras = new CalificacionNeutras(pid);
                return  calificacionNeutras;

            case 2:
                CalificacionesNegativas calificacionesNegativas = new CalificacionesNegativas(pid);
                return  calificacionesNegativas;

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

        switch (position){
            case 0:
                return "Positivas";

            case 1:
                return  "Neutras";

            case 2:
                return  "Negativas";

            default:
                return null;
        }

    }
}
