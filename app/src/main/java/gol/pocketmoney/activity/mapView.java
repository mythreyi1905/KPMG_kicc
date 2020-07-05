package gol.pocketmoney.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import gol.pocketmoney.classes.JsonParser;
import gol.pocketmoney.R;

public class mapView extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ddlogesh1";
    private static int backButtonCount=0;
    private static int tab_active=1;
    /*
        1-Cycle (Default)
        2-Walk
    */

    private static int status=0;
    /*
        0-Initialise
        1-Got location
        2-After Start clicked
        3-After Stop clicked
    */

    private SharedPreferences Status;
    private SharedPreferences.Editor EStatus;
    private DatabaseReference fri;

    private MapView mapView;
    private GoogleMap mMap;
    private ArrayList<LatLng> markerPoints;

    private TextView tv_dist,tv_cal;
    private Button tab_cycle,tab_walk;
    private LinearLayout layout_data,layout_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_view);

        /********************************************************************************/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        BitmapDrawable background = new BitmapDrawable (BitmapFactory.decodeResource(getResources(), R.drawable.back_1));
        background.setTileModeX(android.graphics.Shader.TileMode.REPEAT);
        toolbar.setBackground(background);

        Status=getSharedPreferences("status", Context.MODE_PRIVATE);
        EStatus=Status.edit();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        fri = ref.child("pocket").child("aadhaar").child(Status.getString("aadhaar",null));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        TextView tv_nav_name=(TextView)header.findViewById(R.id.tv_nav_name);
        tv_nav_name.setText("Hello, " + Status.getString("username",null));

        /********************************************************************************/

        final FloatingActionButton fb=findViewById(R.id.getlocation);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,Integer.toString(status));
                if(status<2)
                    checkGPS();
            }
        });

        final Button b=findViewById(R.id.start);
        b.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                if(status==0)
                    checkGPS();
                else if(status==1){
                    fb.setVisibility(View.GONE);
                    layout_data.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 0.15f);
                    layout_map.setLayoutParams(param);

                    b.setText("FINISH");
                    status=2;
                }
                else{
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            String dist=tv_dist.getText().toString();
                            dist=dist.substring(0,dist.length()-3);
                            String cal=tv_cal.getText().toString();
                            cal=cal.substring(0,cal.length()-4);

                            int count=Status.getInt("count",0);
                            EStatus.putInt("count",count+1);    EStatus.apply();

                            EStatus.putFloat("distance"+Integer.toString(count+1),Float.parseFloat(dist)); EStatus.apply();
                            EStatus.putInt("calorie"+Integer.toString(count+1),Integer.parseInt(cal)); EStatus.apply();
                            EStatus.putInt("credit"+Integer.toString(count+1),Integer.parseInt(cal) * 2); EStatus.apply();
                            int credits = Status.getInt("credits",0);
                            EStatus.putInt( "credits",credits + Integer.parseInt(cal) * 2); EStatus.apply();
                            fri.child("credits").setValue(Integer.toString(credits + Integer.parseInt(cal) * 2));

                            if(tab_active==1) {
                                EStatus.putFloat("speed"+Integer.toString(count+1), 16.3f);    EStatus.apply();
                            }
                            else{
                                EStatus.putFloat("speed"+Integer.toString(count+1), 4.2f);    EStatus.apply();
                            }
                            status=0;
                            Intent i1=new Intent(mapView.this,viewResults.class);
                            startActivity(i1);
                        }
                    }, 2000);

                    status=3;
                    drawUserPositionMarker(null);
                    findRoutes();
                }
            }
        });

        /********************************************************************************/

        tv_dist=findViewById(R.id.tv_dist);
        tv_cal=findViewById(R.id.tv_cal);
        layout_data=findViewById(R.id.layout_data);
        layout_map=findViewById(R.id.layout_map);

        tab_cycle=findViewById(R.id.tab_cycle);
        tab_walk=findViewById(R.id.tab_walk);

        tab_cycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status!=2) {
                    tab_active=1;
                    tab_cycle.setTextColor(Color.parseColor("#000000"));
                    tab_cycle.setBackgroundColor(Color.parseColor("#00000000"));
                    tab_walk.setTextColor(Color.parseColor("#ffffff"));
                    tab_walk.setBackgroundColor(Color.parseColor("#95A9AA"));
                }
            }
        });
        tab_walk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status!=2) {
                    tab_active=2;
                    tab_walk.setTextColor(Color.parseColor("#000000"));
                    tab_walk.setBackgroundColor(Color.parseColor("#00000000"));
                    tab_cycle.setTextColor(Color.parseColor("#ffffff"));
                    tab_cycle.setBackgroundColor(Color.parseColor("#95A9AA"));
                }
            }
        });

        /********************************************************************************/

        markerPoints = new ArrayList<>();
        markerPoints.clear();

        mapView = (MapView) this.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.getUiSettings().setZoomControlsEnabled(false);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                checkGPS();

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        if(status==2) {
                            drawUserPositionMarker(latLng);
                            findRoutes();
                        }
                    }
                });
            }
        });
    }

    /********************************************************************************/

    private void drawUserPositionMarker(LatLng latLng){
        if (status == 1) {
            mMap.clear();
            markerPoints.clear();
            markerPoints.add(latLng);
            mMap.addMarker(new MarkerOptions().position(latLng).title(getAddress(latLng)).flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.user_position_point)));
            zoomMapTo(latLng);
        }
        else if (status == 2) {
            mMap.clear();
            markerPoints.add(latLng);
            LatLng orig = (LatLng)markerPoints.get(0);
            LatLng dest = (LatLng)markerPoints.get(markerPoints.size()-1);
            mMap.addMarker(new MarkerOptions().position(orig).title(getAddress(orig)).flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.addMarker(new MarkerOptions().position(dest).title(getAddress(dest)).flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.user_position_point)));
        }
        else{
            mMap.clear();

            LatLng orig = (LatLng)markerPoints.get(0);
            LatLng dest = (LatLng)markerPoints.get(markerPoints.size()-1);
            mMap.addMarker(new MarkerOptions().position(orig).title(getAddress(orig)).flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            mMap.addMarker(new MarkerOptions().position(dest).title(getAddress(dest)).flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
    }

    private void drawLocationAccuracyCircle(Location location){
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addCircle(new CircleOptions().center(latLng)
                .fillColor(Color.parseColor("#BBE3FA"))
                .strokeColor(Color.parseColor("#00A2FF"))
                .strokeWidth(2.0f).radius(5));
    }

    private void zoomMapTo(LatLng latLng) {
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f));
        }
        catch (Exception e) {
            Log.d(TAG,e.getMessage());
        }
    }

    private void findRoutes(){

        double f=0.0;

        LatLng orig = (LatLng)markerPoints.get(0);
        for(int i=1;i<markerPoints.size();i++){
            LatLng dest = (LatLng)markerPoints.get(i);
            f+=findDistance(orig,dest);
            String url = getDirectionsUrl(orig, dest);
            Log.d(TAG,url);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);

            orig=dest;
        }
        tv_dist.setText((new DecimalFormat("0.00").format(f)) + " KM");
        tv_cal.setText(Integer.toString(findCalorie(f)) + " CAL");
    }

    private float findDistance(LatLng orig,LatLng dest){
        Location loc1=new Location("Orig");
        Location loc2=new Location("Dest");

        loc1.setLatitude(orig.latitude);
        loc1.setLongitude(orig.longitude);
        loc2.setLatitude(dest.latitude);
        loc2.setLongitude(dest.longitude);

        return ((loc1.distanceTo(loc2))/1000);
    }

    private int findCalorie(double distance){
        if(tab_active==1)
            return (int)(24.75*distance);
        else
            return (int)(57.75*distance);
    }

    /*******************************************************************************/

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            }
            catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                JsonParser parser = new JsonParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                Log.d(TAG,e.getMessage());
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();

            for (int i = 0; i < result.size(); i++) {
                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
            }
            if(points.size()!=0)
                mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "&destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "&sensor=true";
        String mode = "&mode=walking";
        String key = "&key=AIzaSyDd50Up6FsrwE_obbLdlza6HYc3KH2wLqs";
        String parameters = str_origin + str_dest + sensor + mode + key;
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }
        catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /*******************************************************************************/
    //Checks for GPS Connection

    public boolean checkGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        Boolean isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPS) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mapView.this);
            alertDialog.setTitle("GPS isn't Enabled!").setMessage("Do you want to turn on GPS?").setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    }).show();
        }
        else{
            status = status<2 ? 1 : status;
            if(status==1) {
                drawUserPositionMarker(new LatLng(12.979970161799208, 77.57799133658409));

                Location src=new Location("src");
                src.setLatitude(12.979970161799208);
                src.setLongitude(77.57799133658409);
                drawLocationAccuracyCircle(src);
            }
        }
        return isGPS;
    }

    private String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(mapView.this, Locale.getDefault());
        String address="";
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address obj = addresses.get(0);
            address = obj.getAddressLine(0);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    /********************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            Intent i1=new Intent(mapView.this,myAccount.class);
            startActivity(i1);
        }
        else if (id == R.id.nav_history) {
            Intent i1=new Intent(mapView.this,history.class);
            startActivity(i1);
        }
        else if (id == R.id.nav_credits) {
            Intent i1=new Intent(mapView.this,checkCredits.class);
            startActivity(i1);
        }
        else if (id == R.id.nav_stores) {
            Intent i1=new Intent(mapView.this,checkStores.class);
            startActivity(i1);
        }
        else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
        else if (id == R.id.nav_change) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

            builder1.setTitle("Alert!").setMessage("Do you want to Change User?").setCancelable(true)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EStatus.clear();    EStatus.apply();
                        Intent i1=new Intent(mapView.this,register.class);
                        startActivity(i1);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        else if (id == R.id.nav_signout) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

            builder1.setTitle("Alert!").setMessage("Do you want to Signout?").setCancelable(true)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EStatus.clear();    EStatus.apply();
                        Intent i1=new Intent(mapView.this,register.class);
                        startActivity(i1);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
            });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        else if (id == R.id.nav_about) {
            Intent i1 = new Intent(getApplicationContext(), aboutUs.class);
            startActivity(i1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /********************************************************************************/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (this.mapView != null) {
            this.mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (this.mapView != null) {
            this.mapView.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (this.mapView != null) {
            this.mapView.onResume();
        }

    }

    @Override
    public void onDestroy() {
        if (this.mapView != null) {
            this.mapView.onDestroy();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (backButtonCount >= 1) {
                backButtonCount = 0;
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "Press Once Again to Exit", Toast.LENGTH_SHORT).show();
                backButtonCount++;
            }
            super.onBackPressed();
        }
    }
}