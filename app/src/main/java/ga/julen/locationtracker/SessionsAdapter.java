package ga.julen.locationtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SessionsAdapter extends BaseAdapter {

    private LinkedHashMap<Integer, String> sesiones;
    private Integer[] keys;
    private Context context;

    public SessionsAdapter(LinkedHashMap<Integer, String> sesiones, Context context) {
        this.sesiones = sesiones;
        keys = sesiones.keySet().toArray(new Integer[sesiones.size()]);
        this.context = context;
    }

    @Override
    public int getCount() {
        return sesiones.size();
    }

    @Override
    public String getItem(int position) {
        return sesiones.get(keys[position]);
    }

    public int getKey(int posicion) {
        return keys[posicion];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.sesion, parent, false);
        TextView listaNombre = view.findViewById(R.id.lista_nombre);
        listaNombre.setText(getItem(position));
        return view;
    }
}
