package com.example.gpstracking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.mapping.BasemapStyle;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;
    TextView t1, t2;
    MapView mapView;
    double lat,lon;
    Button btn,DateBtn;
    EditText editText;
    String name,date;
    List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        name="";
        date="";

        list=new ArrayList<String>();

        String yourApiKey = "AAPKe51cbf3519894c988b8a151ef27fa2d1k6WJFSWYJGXW-5CNE7cs6TzTJvM1GJG_zbc6ZBHDVvwxEKktnuR4YtFUB0rUB0Bj";
        ArcGISRuntimeEnvironment.setApiKey(yourApiKey);



        t1 = findViewById( R.id.mt );
        t2 = findViewById( R.id.mt2 );
        DateBtn = findViewById( R.id.Dbtn );
        editText = findViewById( R.id.eText );

        mapView = findViewById( R.id.mapView );

        ArcGISMap map = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);
        mapView.setMap( map );

        btn = findViewById( R.id.recent );

        btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Recent.class);
                startActivity( intent );

            }
        } );



        DateBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AnyDay.class);
                startActivity( intent );
            }
        } );


       //  database = FirebaseDatabase.getInstance();
       //  myRef = database.getReference().child( "users" ).child( "user1" );

        HashMap<String,Object> map1 = new HashMap<>();
       // map.put( "1675057692","17.3616,78.4747" );   // charminar
       // map.put( "1675057867","17.3457,78.5522" );   // Lb nagar
      //  map.put( "1675057965","17.3984,78.5583" );   // uppal
     //   map.put( "1675057994","17.3833,78.4011" );   // Golconda fort
     //   map.put("1675058994","27.1751,78.0421");  // Taj Mahal   // high tech city: 17.4435,78.3772 // jubliee hills: 17.4326,78.4071



        /* String str_date="31-01-2023";
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null;
        try {
            date = (Date)formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        t1.setText( ""+date.getTime());  */



        // FirebaseDatabase.getInstance().getReference().child( "trackerdetails" ).updateChildren( map );

        /* To get the new date timestamp whenever the tracker adds a new child of todays date to tracker details node */
     /*   DatabaseReference itemsRef = FirebaseDatabase.getInstance().getReference().child("trackerdetails");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                     name = ds.getKey();
                   // t2.setText( name );
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("TAG", databaseError.getMessage()); //Don't ignore potential errors!
            }
        };
        itemsRef.addValueEventListener(eventListener);  */

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "trackerdetails" ) ;

        reference.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                name = snapshot.getKey();
                t1.setText( name );
                reference.child( name ).addChildEventListener( new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        String str = snapshot.getValue().toString();
                        t2.setText( str );
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
        } ) ;

        // The belo code is used to create a calender kind of interface which enables user to select date
        editText.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our edit text.
                                editText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        DateBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date = editText.getText().toString();
                t1.setText( date );
                Intent intent = new Intent(MainActivity.this,AnyDay.class);
                intent.putExtra( "date",date );
                startActivity( intent );
            }
        } );




    }

   private void trackLocation(double lat,double lon){


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