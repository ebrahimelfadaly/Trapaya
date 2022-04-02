package com.example.tripplanner.TripData;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

//not sure until now

/*convert data here from data in code to type in database*/
public class DataConverter {
    //from note as list to database as string json
    @TypeConverter
    public String fromNotesList(ArrayList<String> notes){
        if(notes==null){
            return (null);
        }
        Gson gson=new Gson();
        Type type= new TypeToken<ArrayList<String>>(){}.getType();
        //convert data to json
        return gson.toJson(notes,type);
    }

    //from data as json to Note as list
    @TypeConverter
    public ArrayList<String> toNotesList(String notesString){
        if(notesString==null)
            return (null);
        Gson gson=new Gson();
        Type type=new TypeToken<ArrayList<String>>(){}.getType();
        return gson.fromJson(notesString,type);
    }
}
