package ga.julen.locationtracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.LinkedHashMap;

public class ListaActivity extends AppCompatActivity {

    private SessionsAdapter sessionsAdapter;
    private ListView listView;
    private SQLiteOpenHelper sqLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        listView = findViewById(R.id.listView);
        popularListView();
    }

    private void popularListView() {
        sqLite = new SQLiteOpenHelper(this, "ubicaciones", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            }
        };
        LinkedHashMap<Integer, String> spinnerArray = new LinkedHashMap<>();
        Cursor cursor2 = sqLite.getWritableDatabase().rawQuery("SELECT ID, FECHA_HORA FROM sesiones ORDER BY ID DESC;", null);
        while (cursor2.moveToNext()) {
            spinnerArray.put(cursor2.getInt(0), cursor2.getString(1));
        }
        sessionsAdapter = new SessionsAdapter(spinnerArray, this);
        listView.setAdapter(sessionsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("idSesion", sessionsAdapter.getKey(position));
                startActivity(intent);
            }
        });
    }

}
