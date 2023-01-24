package com.example.gpstracking;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        t1 = findViewById( R.id.txt );

       //  database = FirebaseDatabase.getInstance();
       //  myRef = database.getReference().child( "users" ).child( "user1" );

        HashMap<String,Object> map = new HashMap<>();
        map.put( "id1","value1" );
        map.put( "id2","value2" );
        map.put( "id3","value3" );
        map.put( "id4","value4" );
        map.put( "id5","value5" );



        FirebaseDatabase.getInstance().getReference().child( "users" ).child( "trackerdetails" ).updateChildren( map );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child( "users" );

        reference.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                t1.setText(null);
                String str = snapshot.getValue().toString();
                t1.setText( str );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );



    }

    private void trackLocation(double lat,double lon){



    }

    private void writeMessage(){

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
    }
}