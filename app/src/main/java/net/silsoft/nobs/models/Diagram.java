package net.silsoft.nobs.models;

// represents single diagram - is created from local JSON file ( mainly )

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Diagram {

    public static final String ORIENTATION_HORIZONTAL = "HORIZONTAL";
    public static final String ORIENTATION_VERICAL = "VERTICAL";
    public static final String ORIENTATION_ORIENTATION = "ORIENTATION";

    private String title;
    private Cell map[][];

    private String orientation = ORIENTATION_VERICAL;

    public String getTitle(){
        return title;
    }

    public String getTextDump(){
        String txt = "";
        for (int i=0;i<getRowsNumber();i++){
            for(int j=0;j<getColsNumber();j++){
                txt += getCell(i, j);
            }
        }

        return txt;
    }

    public Cell[][] getMapHorizontal(){
        //todo : WTF map[0].lenght does not work ?
        int cols=0;
        for (int i=0; i<map[0].length;i++){
            if (map[0][i]==null) continue;
            cols++;
        }
        int rows = map.length;

        Cell hmap[][] = new Cell[cols][rows];

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
               hmap[(cols-1)-j][i]= new Cell(map[i][j].getValue());
            }
        }
        return hmap;
    }

    public Cell[][] getMapVertical(){
        return map;
    }

    public Cell[][] getMap(){
        if (orientation.equals(ORIENTATION_HORIZONTAL)){
            return getMapHorizontal();
        }else{
            return getMapVertical();
        }
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }


    public Cell getCell(int row, int col){
        if (getMap()[row]==null) return new Cell("");
        if (getMap()[row][col]==null) return new Cell("");

        if (getOrientation().equals(ORIENTATION_HORIZONTAL)){
            return getMapHorizontal()[row][col];
        }else{
            return getMap()[row][col];
        }
    }


    public int getRowsNumber(){
        return getMap().length;
//      if (orientation.equals(ORIENTATION_VERICAL)){
//          return getMap().length;
//      }  else{
//          return getMap().length - 1;  // because on vertical we display notes on lines
//      }
    }

    //todo : what the fuck is going on here ? why simple getMap()[0].length is not enough ?
    public int getColsNumber(){
        int len=0;
        for (int i=0; i<getMap()[0].length;i++){
            if (getMap()[0][i]==null) continue;
            len++;
        }

        return len;
    }


    public static Diagram CreateFromJson(String filename, Context context){
        Diagram diagram = new Diagram();
        filename="diagrams/"+filename+".json";
        //read json file and create diagram
        String json = null;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex.getMessage());
        }

        try {
            JSONObject obj = new JSONObject(json);
            //if json file structure changes - adjust this
            diagram.title = obj.getString("title");
            JSONArray diagram_map = obj.getJSONArray("map");
            diagram.map = new Cell[diagram_map.length()][diagram_map.getJSONArray(0).length()];
//            Log.i("@@@","Map size : "+diagram_map.length() +":"+diagram_map.getJSONArray(0).length() );

            for (int i = 0; i < diagram_map.length(); i++) {
                JSONArray jrow = diagram_map.getJSONArray(i);
                Cell[] row = new Cell[diagram_map.getJSONArray(0).length()];

                for(int j=0; j<diagram_map.getJSONArray(0).length();j++){
                    row[j] = new Cell(jrow.getString(j));
                }

                diagram.map[i] = row;
            }

        }catch (JSONException e){
            throw new RuntimeException(e.getMessage());
        }

        return diagram;
    }


}


