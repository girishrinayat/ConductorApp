package com.ycce.kunal.conductorapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference locationRef ;;

    Button signOut;
    boolean isGPSEnabled=false;

    Context mContext;
    TextView locationtxt, locationText;

    String busno ;

    public  static  final  String MyPref = "MyPref";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle =   getIntent().getExtras();

        busno = bundle.getString("BusNo");
        setContentView(R.layout.activity_main);


        locationRef= database.getReference("Locations").child("Bus No: "+busno);
        locationtxt = (TextView)findViewById(R.id.locationview);

        signOut = (Button) findViewById(R.id.logout);

        sharedPreferences = getSharedPreferences(MyPref,MODE_PRIVATE);
        editor = sharedPreferences.edit();


        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.commit();
//                myRef= database.getReference();
                locationRef.removeValue();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });
        // Get user location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Keep track of user location.
        // Use callback/listener since requesting immediately may return null location.
        // IMPORTANT: TO GET GPS TO WORK, MAKE SURE THE LOCATION SERVICES ON YOUR PHONE ARE ON.
        // FOR ME, THIS WAS LOCATED IN SETTINGS > LOCATION.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, new Listener());

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, new Listener());
        // Have another for GPS provider just in case.

        // Try to request the location immediately
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (location == null){
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location != null){
            handleLatLng(location.getLatitude(), location.getLongitude());
        }

        Toast.makeText(getApplicationContext(),
                "Trying to obtain GPS coordinates. Make sure you have location services on.",
                Toast.LENGTH_LONG).show();

    }



    /**
     * Handle lat lng.
     */
    private void handleLatLng(double latitude, double longitude){
        Log.v("TAG", "(" + latitude + "," + longitude + ")");
        //busno =busno+"Location";

        locationRef.setValue(latitude+","+longitude);

        locationtxt.setText(latitude+","+longitude);

        // Toast.makeText(getApplicationContext(),""+latitude +" "+longitude,Toast.LENGTH_LONG).show();
    }

    /**
     * Listener for changing gps coords.
     */
    private class Listener implements LocationListener {

        //LocationListener interface

//        Location manager class
        public void onLocationChanged(Location location) {

            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            handleLatLng(latitude, longitude);

        }

        public void onProviderDisabled(String provider){}
        public void onProviderEnabled(String provider){}
        public void onStatusChanged(String provider, int status, Bundle extras){

        }


    }
}




