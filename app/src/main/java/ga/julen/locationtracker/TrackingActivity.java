package ga.julen.locationtracker;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Button btnMostrarMapa;
    private PolylineOptions line;
    private View overlay;

    private SQLiteOpenHelper sqLite;

    private boolean mapReady;
    private boolean followUser;

    private boolean firstLocation = true;

    private ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        locations = new ArrayList<>();
        crearDB();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        btnMostrarMapa = findViewById(R.id.btn_parar);
        btnMostrarMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        followUser = true;
        overlay = findViewById(R.id.overlay);
        overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                followUser = false;
                return false;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates("gps", 500, 0, mLocationListener);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            locations.add(location);
            if (mapReady) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                line.add(latLng);
                if (firstLocation) {
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Primera ubicación."));
                }
                firstLocation = false;
                if (followUser)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
                mMap.addPolyline(line);
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapReady = true;
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                followUser = true;
                return false;
            }
        });
        line = new PolylineOptions();
        line.width(5).color(Color.RED);
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(lastLocation));
        googleMap.addPolyline(line);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private void crearDB() {
        sqLite = new SQLiteOpenHelper(this, "ubicaciones", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase sqLiteDatabase) {
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS ubicaciones (" +
                        "ID INTEGER," +
                        "LATITUD DECIMAL(9,6)," +
                        "LONGITUD DECIMAL(9,6));");
                sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS sesiones (" +
                        "ID INTEGER," +
                        "FECHA_HORA VARCHAR(50))");
            }

            @Override
            public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            }
        };
    }

    private void guardar() {
        SQLiteDatabase db = sqLite.getWritableDatabase();
        Cursor cursor = sqLite.getWritableDatabase().rawQuery("SELECT MAX(ID) FROM ubicaciones;", null);
        int id = 0;
        if (cursor.moveToNext()) {
            id = cursor.getInt(0) + 1;
        }
        ContentValues contentValuesSesion = new ContentValues();
        contentValuesSesion.put("ID", id);
        contentValuesSesion.put("FECHA_HORA", DateFormat.getDateTimeInstance().format(new Date()));
        db.insert("sesiones", null, contentValuesSesion);
        for (Location location : locations) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", id);
            contentValues.put("LATITUD", location.getLatitude());
            contentValues.put("LONGITUD", location.getLongitude());
            db.insert("ubicaciones", null, contentValues);
        }
    }

}
