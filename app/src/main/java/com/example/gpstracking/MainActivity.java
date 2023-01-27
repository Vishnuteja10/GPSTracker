package com.example.gpstracking;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.PointCollection;
import com.esri.arcgisruntime.geometry.Polygon;
import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.MapView;


import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView t1 ;
    MapView mapView;
    double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        String yourApiKey = "AAPKe51cbf3519894c988b8a151ef27fa2d1k6WJFSWYJGXW-5CNE7cs6TzTJvM1GJG_zbc6ZBHDVvwxEKktnuR4YtFUB0rUB0Bj";
        ArcGISRuntimeEnvironment.setApiKey(yourApiKey);

       // t1 = findViewById( R.id.t1 );

        mapView = findViewById( R.id.mapView );


       //  database = FirebaseDatabase.getInstance();
       //  myRef = database.getReference().child( "users" ).child( "user1" );

        HashMap<String,Object> map = new HashMap<>();
        map.put( "loc1","17.3616,78.4747" );
       // map.put( "loc2","17.3457,78.5522" );
      //  map.put( "loc3","17.3984,78.5583" );
     //   map.put( "loc4","17.3833,78.4011" );





        FirebaseDatabase.getInstance().getReference().child( "trackerdetails" ).updateChildren( map );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "trackerdetails" );

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {



                String str = dataSnapshot.getValue().toString();
                String[] list = str.split( "," );

                for(int i=0;i< list.length;i++){
                    lat = Double.parseDouble( list[0] );
                    lon = Double.parseDouble( list[1] );
                }

                trackLocation(lat,lon);

               /* Post newPost = dataSnapshot.getValue(Post.class);
                System.out.println("Author: " + newPost.author);
                System.out.println("Title: " + newPost.title);
                System.out.println("Previous Post ID: " + prevChildKey); */
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

        });

      /*  reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               /* String str = snapshot.getValue().toString();

                String list[] = str.split(",");

                t1.setText( list[0]+"@"+list[1]+"@"+list[2] ); /

               String str = snapshot.child( "loc3" ).getValue().toString();
                String[] list = str.split( "," );

               // t1.setText(list[0]+"  "+list[1]);
                for(int i=0;i< list.length;i++){
                     lat = Double.parseDouble( list[0] );
                     lon = Double.parseDouble( list[1] );
                }

                trackLocation(lat,lon);

              /*  for (DataSnapshot locSnapshot: snapshot.getChildren()) {
                    String str = locSnapshot.child("loc").getValue().toString();
                    String[] list = str.split( "," );
                    for(int i=0;i< list.length;i++){
                        lat = Double.parseDouble( list[0] );
                        lon = Double.parseDouble( list[1] );
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );  */



    }

   private void trackLocation(double lat,double lon){

        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);
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

   /* private void writeMessage(){

        // Write a message to the database
         database = FirebaseDatabase.getInstance();


        myRef.setValue("Hello, World!");
    }

    private String readFromDatabase(){
        String str="";

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
               String  value = dataSnapshot.getValue(String.class);

                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return str;
    }  */
}