package com.example.gpstracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Recent extends AppCompatActivity {

    MapView mapView;
    double lat,lon;
    TextView textView;
    private static final int PERMISSION_FINE_LOCATION =1 ;
    FusedLocationProviderClient client;
    String latt,lonn,dest;
    Button btn;
    GraphicsOverlay graphicsOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_recent );
        mapView = findViewById( R.id.mapViewR );
        textView = findViewById(R.id.text2);
        btn = findViewById( R.id.btn );

        ArcGISMap map = new ArcGISMap( BasemapStyle.ARCGIS_STREETS);
        mapView.setMap( map );
        graphicsOverlay = new GraphicsOverlay();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "trackerdetails" );

       reference.limitToLast( 1 ).addChildEventListener( new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

               String str ="";
               for(DataSnapshot ds : snapshot.getChildren()) {
                   str = ds.getValue().toString();
                   textView.setText( str );
               }

              String[] list = str.split( "," );

               for(int i=0;i< list.length;i++){
                   lat = Double.parseDouble( list[0] );
                   lon = Double.parseDouble( list[1] );
               }

               dest = lat+","+lon;
               trackLocation(lat,lon);
           }

           @Override
           public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

           }

           @Override
           public void onChildRemoved(@NonNull DataSnapshot snapshot) {

           }

           @Override
           public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       } );

       btn.setOnClickListener( new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               alertBox();
           }
       } );



    }



    private void trackLocation(double lat,double lon){


        mapView.setViewpoint(new Viewpoint(lat, lon, 12441.638572));

        // mapView.setViewpoint(new Viewpoint(new Point(17.3457, 78.5522, SpatialReferences.getWgs84()), 12441));


      //  GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        // create a point geometry with a location
        // Point point = new Point(17.35028,78.55286 );

        Point graphicPoint = new Point(lon,lat,  SpatialReferences.getWgs84());



        // create an opaque orange (0xFFFF5733) point symbol with a blue (0xFF0063FF) outline symbol
        SimpleMarkerSymbol simpleMarkerSymbol =
                new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF5733, 20);
        SimpleLineSymbol blueOutlineSymbol =
                new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF0063FF, 2);

        simpleMarkerSymbol.setOutline(blueOutlineSymbol);



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
                    updateGps();
                }else{
                    Toast.makeText( this,"THIS APP NEEDS PERMISSIONS",Toast.LENGTH_SHORT ).show();
                    finish();
                }
                break;
        }
    }



    public void updateGps(){

        client = LocationServices.getFusedLocationProviderClient( Recent.this );

        if(ActivityCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED ){

            // user provided the permission
            client.getLastLocation().addOnSuccessListener( this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    // we got permission and location values such as lat and longitude
                    latt = String.valueOf(  location.getLatitude());
                    lonn = String.valueOf( location.getLongitude() );
                    // t1.setText( latt+","+lonn );
                    plotOnMap( latt,lonn );
                }
            } );


        }else{
            // Permission not yet granted
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions( new String[] {Manifest.permission.ACCESS_FINE_LOCATION} , PERMISSION_FINE_LOCATION);
            }

        }

    }

    public void plotOnMap(String lat , String lon){
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

    public void alertBox(){
        // Create the object of AlertDialog Builder class
        AlertDialog.Builder builder = new AlertDialog.Builder(Recent.this);

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
            updateGps();
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