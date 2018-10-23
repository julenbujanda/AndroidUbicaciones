package ga.julen.locationtracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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
        Intent intent = getIntent();
        locations = ubicacionesPrevias();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private ArrayList<Location> ubicacionesPrevias() {
        ArrayList<Location> ubicaciones = new ArrayList<>();
        sqLite = new SQLiteOpenHelper(this, "ubicaciones", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {

            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            }
        };
        Cursor cursor = sqLite.getWritableDatabase().rawQuery("SELECT * FROM ubicaciones WHERE ID = (SELECT MAX(ID) FROM ubicaciones);", null);
        while (cursor.moveToNext()) {
            Location location = new Location("");
            location.setLatitude(cursor.getFloat(1));
            location.setLongitude(cursor.getFloat(2));
            ubicaciones.add(location);
        }
        Cursor cursor2 = sqLite.getWritableDatabase().rawQuery("SELECT * FROM ubicaciones;", null);
        while (cursor2.moveToNext()) {
            Log.d("ubicacion", cursor2.getInt(0) + " " + cursor2.getFloat(1) + cursor2.getFloat(2));
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
        for (Location location : locations) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            line.add(latLng);
            if (firstLocation) {
                googleMap.addMarker(new MarkerOptions().position(latLng).title("Primera ubicación."));
            }
            firstLocation = false;
            lastLocation = latLng;
        }
        line.width(5).color(Color.RED);
        googleMap.addMarker(new MarkerOptions().position(lastLocation).title("Última ubicación."));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 17.0f));
        googleMap.addPolyline(line);

    }
}
