package com.ycce.kunal.conductorapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref ;;

    private Spinner selectRoute;
    private Spinner selectUpDown;
    private Spinner selectBusStop;
    private Button confirm;
    private TextView loctext;

    private Context mContext;

    private String busno ;
    private String routeName;
    private String towards;
    private String currentStop;
    private String[] route = {"","pardiycce","buldiButebori","buldiKoradi","buldiCRPF"};
    private String[] pardiycce={"","Pardi", "subhan nagar", "old pardi naka", "railway crossing", "sangharsh nagar","swaminarayan mandir", "wathoda" , "karbi",
            "dighori flyover","mhalgi nagar", "manevada square","omkar nagar","rameshawari", "Tukram hall", "narendra nagar",
            "chatrapati square","sawrakar square", "pratap nagar", "padole hospital square","sambhaji square", "NIT garden",
            "Trimurti nagar","mangalmurti square","balaji nagar", "mahindra company", "IC square", "electric zone", "Hingna T-point", "crpf", "Ycce","Wanadongri"};
    private String[] buldiButebori={"","Buldi", "Dhantoli", "Lokmat Square", "Rahate Colony", "Jail Gate", "Ajni","Sai Mandir", "Chatrapatti", "Sneh Nagar", "Rajeev Nagar",
            "Somalwada","Ujjwal Nagar", "Sonegaon", "Airport(Pride Hotel)", "Bara Kholi", "Shivangaon","Chinchbhavan", "Khapri Naka", "Khapri", "Khapri Fata", "Parsodi",
            "Gauvsi Manapur", "Jamtha", "Ashokvan", "Dongargaon", "Gothali", "Mohgaon","Satgaon Fata", "Butibori" };
    private String[] buldiKoradi={"","not yet  build"};
    private String[] buldiCRPF={"","not yet build"};
    private String[] upDown={"","Up","Down"};

    private ArrayAdapter<String> routeAdapter;
    private ArrayAdapter<String> upDownAdapter;
    private ArrayAdapter<String> busstopAdapter;

    public  static  final  String MyPref = "MyPref";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setTitle("Main Menu");
        setSupportActionBar(myToolbar);
        // Get user location
        mContext = MainActivity.this;
//        location = null;
      LocationManager  locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if ( !locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        Bundle bundle =   getIntent().getExtras();

        if (bundle!=null){
            busno = bundle.getString("BusNo");
        }
        ref= database.getReference("Locations").child("Bus No: "+busno);

        confirm = (Button) findViewById(R.id.confirm);
        selectRoute = (Spinner) findViewById(R.id.route);
        selectUpDown = (Spinner) findViewById(R.id.upDown);
        selectBusStop = (Spinner) findViewById(R.id.currentPos);
        loctext = (TextView) findViewById(R.id.loctext);

        sharedPreferences = getSharedPreferences(MyPref,MODE_PRIVATE);
        editor = sharedPreferences.edit();


        // Get user location
         locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Keep track of user location.
        // Use callback/listener since requesting immediately may return null location.
        // IMPORTANT: TO GET GPS TO WORK, MAKE SURE THE LOCATION SERVICES ON YOUR PHONE ARE ON.
        // FOR ME, THIS WAS LOCATED IN SETTINGS > LOCATION.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           return   ;
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
/*

        Toast.makeText(getApplicationContext(),
                "Trying to obtain GPS coordinates. Make sure you have location services on.",
                Toast.LENGTH_LONG).show();
*/

       // Creating adapter for spinner
        routeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, route);
        // Drop down layout style - list view with radio button
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        selectRoute.setAdapter(routeAdapter);

        selectRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                routeName = adapterView.getItemAtPosition(i).toString();
                getDirections(routeName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(mContext, "please Select route!!!!!", Toast.LENGTH_SHORT).show();
            }
        });


        onClickEvents();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//         Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.logout){
            editor.clear();
            editor.commit();
            ref = database.getReference("Locations").child("Bus No: "+busno);
            ref.removeValue();
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            finish();
            startActivity(intent);
            return true;

        }else if (id == R.id.exit){
            finish();
            return true;
        }else if (id == R.id.camera){
            startActivity(new Intent(MainActivity.this,ScanActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void onClickEvents() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(mContext, "Please check the internet connection", Toast.LENGTH_SHORT).show();
                } else {
                    if (routeName == "" || towards == "" || currentStop == "") {
                        Toast.makeText(mContext, "Please select the valid input!!!!", Toast.LENGTH_SHORT).show();
                    } else {

                        ref.child("routeName").setValue(routeName);
                        ref.child("towards").setValue(towards);
                        ref.child("currentStop").setValue(currentStop);
                        Toast.makeText(mContext, "Sucessfull!!!!", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }

    private void getDirections(String routeNames) {
        this.routeName = routeNames;
        if (routeName!=""){
            // Creating adapter for spinner
            upDownAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, upDown);
            // Drop down layout style - list view with radio button
            upDownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
            selectUpDown.setAdapter(upDownAdapter);

            //event
            selectUpDown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    towards = adapterView.getItemAtPosition(i).toString();
                    onSelectRoute(routeName,towards);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    Toast.makeText(mContext, "please Select direction!!!!!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void onSelectRoute(String routeName , String towards) {
       if (towards != ""){
           if (routeName.equals("pardiycce")){
               // Creating adapter for spinner
               busstopAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pardiycce);
               // Drop down layout style - list view with radio button
               busstopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               // attaching data adapter to spinner
               selectBusStop.setAdapter(busstopAdapter);
               selectBusStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    currentStop= adapterView.getItemAtPosition(i).toString();
                   }

                   @Override
                   public void onNothingSelected(AdapterView<?> adapterView) {

                   }
               });

           }else if(routeName.equals("buldiButebori")){
               // Creating adapter for spinner
               busstopAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, buldiButebori);
               // Drop down layout style - list view with radio button
               busstopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               // attaching data adapter to spinner
               selectBusStop.setAdapter(busstopAdapter);
               // event
               selectBusStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                       currentStop= adapterView.getItemAtPosition(i).toString();

                   }

                   @Override
                   public void onNothingSelected(AdapterView<?> adapterView) {

                   }
               });
           }else if(routeName.equals("buldiKoradi")){
               // Creating adapter for spinner
               busstopAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, buldiKoradi);
               // Drop down layout style - list view with radio button
               busstopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               // attaching data adapter to spinner
               selectBusStop.setAdapter(busstopAdapter);
               // event
               selectBusStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                       currentStop= adapterView.getItemAtPosition(i).toString();
                   }

                   @Override
                   public void onNothingSelected(AdapterView<?> adapterView) {

                   }
               });
           }else if(routeName.equals("buldiCRPF")){
               // Creating adapter for spinner
               busstopAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, buldiCRPF);
               // Drop down layout style - list view with radio button
               busstopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
               // attaching data adapter to spinner
               selectBusStop.setAdapter(busstopAdapter);
               // event
               selectBusStop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                   @Override
                   public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                       currentStop= adapterView.getItemAtPosition(i).toString();
                   }

                   @Override
                   public void onNothingSelected(AdapterView<?> adapterView) {

                   }
               });
           }
       }
    }


    /**
     * Handle lat lng.
     */
    private void handleLatLng(double latitude, double longitude){
        Log.v("TAG", "(" + latitude + "," + longitude + ")");
        //busno =busno+"Location";
        ref.child("current position").setValue(latitude+","+longitude);
        loctext.setText(latitude+","+longitude);

        // Toast.makeText(getApplicationContext(),""+latitude +" "+longitude,Toast.LENGTH_LONG).show();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
        public void onStatusChanged(String provider, int status, Bundle extras){}


    }


    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&activeNetwork.isConnectedOrConnecting();
    }

}




