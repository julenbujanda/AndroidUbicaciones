package ga.julen.locationtracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private SQLiteOpenHelper sqLite;

    private ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        try {
            Intent intent = getIntent();
            int idSesion = intent.getIntExtra("idSesion", 0);
            locations = ubicacionesPrevias(idSesion);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (SQLiteException e) {
            RelativeLayout constraintLayout = findViewById(R.id.mapsActivityLayout);
            constraintLayout.removeView(findViewById(R.id.map));
            Button btnVolver = findViewById(R.id.btnVolver);
            TextView txtAnuncio = findViewById(R.id.txtAnuncio);
            btnVolver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
            btnVolver.setVisibility(View.VISIBLE);
            txtAnuncio.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<Location> ubicacionesPrevias(int idSesion) throws SQLiteException {
        sqLite = new SQLiteOpenHelper(this, "ubicaciones", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            }
        };
        ArrayList<Location> ubicaciones = new ArrayList<>();
        Cursor cursor = sqLite.getWritableDatabase().rawQuery("SELECT * FROM ubicaciones WHERE ID = " + idSesion + ";", null);
        while (cursor.moveToNext()) {
            Location location = new Location("");
            location.setLatitude(cursor.getFloat(1));
            location.setLongitude(cursor.getFloat(2));
            ubicaciones.add(location);
        }
        return ubicaciones;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        PolylineOptions line =
                new PolylineOptions();
        LatLng lastLocation = new LatLng(-34, 151);
        boolean firstLocation = true;
        ArrayList<Marker> markers = new ArrayList<>();
        for (Location location : locations) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            line.add(latLng);
            if (firstLocation) {
                markers.add(googleMap.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.first_location))));
            }
            firstLocation = false;
            lastLocation = latLng;
        }
        line.width(5).color(Color.RED);
        markers.add(googleMap.addMarker(new MarkerOptions().position(lastLocation).title(getString(R.string.last_location))));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers)
            builder.include(marker.getPosition());
        LatLngBounds bounds = builder.build();
        googleMap.addPolyline(line);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
    }
}
