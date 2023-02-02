package com.example.gpstracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class yesterday extends AppCompatActivity {

    MapView mapView;
    double lat,lon;
    TextView t1,t2,t3;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_yesterday );

        mapView = findViewById( R.id.mapViewY );
        t1 = findViewById( R.id.t1);
        t2 =findViewById( R.id.t2 );
        t3 = findViewById( R.id.t3 );
        ArcGISMap map = new ArcGISMap( BasemapStyle.ARCGIS_STREETS);
        mapView.setMap( map );

       // mapView.setViewpoint(new Viewpoint(lat, lon, 12441.638572));

        list=new ArrayList<String>();

        HashMap<String,Object> map1 = new HashMap<>();
      //  map.put( "1675057692","17.3616,78.4747" );   // charminar
      //  map.put( "1675057867","17.3457,78.5522" );   // Lb nagar
      //  map.put( "1675057965","17.3984,78.5583" );   // uppal
      //  map.put( "1675057994","17.3833,78.4011" );   // Golconda fort
      //  map.put("1675058994","27.1751,78.0421");  // Taj mahal

      //  FirebaseDatabase.getInstance().getReference().child( "trackerdetails" ).child( "" ).updateChildren( map1 );

        yesterDayLoc();


    }




    private void yesterDayLoc(){
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

    }

    private void getYesterdayCoord(String child){
        t3.setText( child );
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("trackerdetails");

        myRef.child( child ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list.add(   dataSnapshot.getValue().toString());
                }
                plotPoints( list );
               // t1.setText( list.toString() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } ) ;

       // t1.setText( list.toString() );



    }

    private void plotPoints(List<String> list){
       // t1.setText( list.toString() );
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
}