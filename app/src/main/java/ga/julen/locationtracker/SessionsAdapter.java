package ga.julen.locationtracker;

import android.content.Context;
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

    public int getKey(int posicion){
        return keys[posicion];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) View.inflate(context, android.R.layout.simple_spinner_item, null);
        textView.setText(getItem(position));
        return textView;
    }
}
