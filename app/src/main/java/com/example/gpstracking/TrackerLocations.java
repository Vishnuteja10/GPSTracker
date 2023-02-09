package com.example.gpstracking;


import java.util.ArrayList;

public class TrackerLocations {

    String coordinates;
    String id ;


    public TrackerLocations(String id,String coordinates) {
        this.coordinates = coordinates;
        this.id = id;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static ArrayList<TrackerLocations> createTrackerLocationsList(ArrayList<String> list){
        ArrayList<TrackerLocations> trackerLocationsArrayList= new ArrayList<>();
        for(int i=0; i<list.size();i++){
            trackerLocationsArrayList.add( new TrackerLocations( String.valueOf( i ),list.get(i) ) );
        }
        return trackerLocationsArrayList;
    }
}

