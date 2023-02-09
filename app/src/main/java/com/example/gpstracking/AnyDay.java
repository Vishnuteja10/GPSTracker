package com.example.gpstracking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnyDay extends AppCompatActivity {

    MapView mapView;
    double lat,lon;
    TextView t1,t2,t3;
    ArrayList<String> list;
    String[] arr;
    private static final int PERMISSION_FINE_LOCATION =1 ;
    FusedLocationProviderClient client;
    String latt,lonn,str,date,reqDate;
    RecyclerView rv;
    ArrayList<TrackerLocations> trackerLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_anyday );

        mapView = findViewById( R.id.mapViewY );
        t1 = findViewById( R.id.t1);
        t2 =findViewById( R.id.t2 );

        rv = findViewById( R.id.rv );

        ArcGISMap map = new ArcGISMap( BasemapStyle.ARCGIS_STREETS);
        mapView.setMap( map );

       // mapView.setViewpoint(new Viewpoint(lat, lon, 12441.638572));

        date = getIntent().getStringExtra( "date" );

        reqDate = dateToLong( date );

        list=new ArrayList<String>();

      //  HashMap<String,Object> map1 = new HashMap<>();
      //  map.put( "1675057692","17.3616,78.4747" );   // charminar
      //  map.put( "1675057867","17.3457,78.5522" );   // Lb nagar
      //  map.put( "1675057965","17.3984,78.5583" );   // uppal
      //  map.put( "1675057994","17.3833,78.4011" );   // Golconda fort
      //  map.put("1675058994","27.1751,78.0421");  // Taj mahal
        //17.4239,78.4738-Tankbund , 17.4375,78.4482-ameerpet, 17.4531, 78.6851-ghatkesat,  17.4486,78.3908- madhapur
        //17.4401,78.3489-gachibowli , 17.2403,78.4294-shamshabad, 17.4012, 78.5492-uppalStadium

      //  FirebaseDatabase.getInstance().getReference().child( "trackerdetails" ).child( "" ).updateChildren( map1 );

        getAnyDayCoord( reqDate );




    }
    private String dateToLong(String str){

        long milliseconds= 12345678910L;
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = f.parse(str);
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // t1.setText(String.valueOf( milliseconds));

        return String.valueOf( milliseconds );
    }




  /*  private void yesterDayLoc(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("trackerdetails");

        myRef.limitToLast( 2 ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String str="";
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    str = dataSnapshot.getKey();
                    break;
                }
                getYesterdayCoord( str );
                t2.setText( str );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

    }  */

    private void getAnyDayCoord(String child){
       // t3.setText( child );
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("trackerdetails");

        myRef.child( child ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list.add(   dataSnapshot.getValue().toString());
                }
                plotPoints( list );
                t1.setText( list.toString() );


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } ) ;

       // t1.setText( list.toString() );



    }

    private void plotPoints(ArrayList<String> list){
       // t1.setText( list.toString() );

        arr = list.toArray(new String[list.size()]);



        trackerLocations = TrackerLocations.createTrackerLocationsList( list );

        LocationsAdapter adapter = new LocationsAdapter( trackerLocations, new LocationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(TrackerLocations trackerLocations) {
               // Toast.makeText( AnyDay.this,trackerLocations.getCoordinates(),Toast.LENGTH_SHORT ).show();
                str = trackerLocations.getCoordinates();
                alertBox( str );
            }
        } );
        rv.setAdapter( adapter );
        rv.setLayoutManager( new LinearLayoutManager( this ) );


       /* listView.setAdapter(adapter);

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                str = (String) listView.getItemAtPosition( position );
                alertBox( str );
            }
        } );  */


        String points = "";
        for (int i=0;i<list.size();i++){

            String str = list.get( i );
            String[] list2 = str.split( "," );

            for(int j=0;j< list2.length;j++){
                lat = Double.parseDouble( list2[0] );
                lon = Double.parseDouble( list2[1] );
              //  points = points + ""+lat+" "+lon ;
            }
           // t1.setText( points );
             trackLocation(lat,lon,i);
        }
    }

    private void trackLocation( double lat , double lon,int i){



        // mapView.setViewpoint(new Viewpoint(new Point(17.3457, 78.5522, SpatialReferences.getWgs84()), 12441));

        mapView.setViewpoint(new Viewpoint(lat, lon, 12441.638572));

        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        // create a point geometry with a location
        // Point point = new Point(17.35028,78.55286 );

        Point graphicPoint = new Point(lon,lat,  SpatialReferences.getWgs84());

        SimpleMarkerSymbol simpleMarkerSymbol;
        SimpleLineSymbol blueOutlineSymbol;

        if(i == 0 ){
            // create an opaque orange (0xFFFF5733) point symbol with a blue (0xFF0063FF) outline symbol
             simpleMarkerSymbol =
                    new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.DIAMOND, 0xffffffff, 20);
            blueOutlineSymbol =
                    new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0063FF, 2);

        }else if(i== list.size()-1){

            simpleMarkerSymbol =
                    new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xff000000, 20);
            blueOutlineSymbol =
                    new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0063FF, 2);

        }else {


            // create an opaque orange (0xFFFF5733) point symbol with a blue (0xFF0063FF) outline symbol
             simpleMarkerSymbol =
                    new SimpleMarkerSymbol( SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF5733, 20 );
            blueOutlineSymbol =
                    new SimpleLineSymbol( SimpleLineSymbol.Style.SOLID, 0xFF0063FF, 2 );



        }

        simpleMarkerSymbol.setOutline( blueOutlineSymbol );

        Graphic pointGraphic = new Graphic(graphicPoint, simpleMarkerSymbol);

        // add the point graphic to the graphics overlay
        graphicsOverlay.getGraphics().add(pointGraphic);




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        switch (requestCode){
            case PERMISSION_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGps(str);
                }else{
                    Toast.makeText( this,"THIS APP NEEDS PERMISSIONS",Toast.LENGTH_SHORT ).show();
                    finish();
                }
                break;
        }
    }

    public void updateGps(String str){

        client = LocationServices.getFusedLocationProviderClient( AnyDay.this );

        if(ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED ){

            // user provided the permission
            client.getLastLocation().addOnSuccessListener( this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    // we got permission and location values such as lat and longitude
                    latt = String.valueOf(  location.getLatitude());
                    lonn = String.valueOf( location.getLongitude() );
                    // t1.setText( latt+","+lonn );
                    plotOnMap( latt,lonn ,str);
                }
            } );


        }else{
            // Permission not yet granted
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions( new String[] {Manifest.permission.ACCESS_FINE_LOCATION} , PERMISSION_FINE_LOCATION);
            }

        }

    }

    public void plotOnMap(String lat , String lon,String dest){
        String str = lat+","+lon;


        Uri baseuri = Uri.parse( "https://www.google.com/maps/dir/?api=1" );
        Uri.Builder uribuilder = baseuri.buildUpon();
        uribuilder.appendQueryParameter( "origin",str );
        uribuilder.appendQueryParameter( "destination",dest );

        Uri gmmIntentUri = Uri.parse( uribuilder.toString() );
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }

    }

    public void alertBox(String str){
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder( AnyDay.this);

        // Set the message show for the Alert time
        builder.setMessage("Do you want to exit ?");

        // Set Alert Title
        builder.setTitle("Want to know the directions on google maps");

        // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            // When the user click yes button then app will close
            //   Intent intent = new Intent(MainActivity.this,Random.class);
            //   startActivity( intent );
            updateGps(str);
        });

        // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // If user click no then dialog box is canceled.
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Show the Alert Dialog box
        alertDialog.show();

    }




}