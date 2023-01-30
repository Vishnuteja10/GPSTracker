package com.example.gpstracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Recent extends AppCompatActivity {

    MapView mapView;
    double lat,lon;
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_recent );
        mapView = findViewById( R.id.mapViewR );
        //textView = findViewById(R.id.text2);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "trackerdetails" );

       reference.limitToLast( 1 ).addChildEventListener( new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
               String str = snapshot.getValue().toString();
               String[] list = str.split( "," );

               for(int i=0;i< list.length;i++){
                   lat = Double.parseDouble( list[0] );
                   lon = Double.parseDouble( list[1] );
               }

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




    }

    private void trackLocation(double lat,double lon){

        ArcGISMap map = new ArcGISMap( BasemapStyle.ARCGIS_STREETS);
        mapView.setMap( map );
        mapView.setViewpoint(new Viewpoint(lat, lon, 12441.638572));

        // mapView.setViewpoint(new Viewpoint(new Point(17.3457, 78.5522, SpatialReferences.getWgs84()), 12441));


        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
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

}