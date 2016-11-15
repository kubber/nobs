package net.silsoft.nobs.models;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Menu {
    MenuElement[] elements;
    String title="";
    Info info;

    public String getTitle(){
        return title;
    }

    public Info getInfo() {
        return info;
    }

    public MenuElement getElement(int index){
        return elements[index]; //todo - check boundaries
    }

    public MenuElement[] getElements() {
        return elements;
    }

    public static Menu CreateFromJSONFile(String filename, Context context){
        Menu menu = new Menu();
        filename="menus/"+filename+".json";

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
            if (obj.has("title")) {
                menu.title = obj.getString("title");
            }

            if (obj.has("info")){
                menu.info = Info.CreateFromJSONFile(obj.getString("info"), context);
            }

            JSONArray j_elements = obj.getJSONArray("elements");
            menu.elements = new MenuElement[j_elements.length()];

            for (int i = 0; i < j_elements.length(); i++) {
               JSONObject entry = j_elements.getJSONObject(i);

               menu.elements[i] = new MenuElement(
                       entry.getString("title"),
                       entry.getString("type"),
                       entry.getString("link")
               );

            }

        }catch (JSONException e){
            throw new RuntimeException(e.getMessage());
        }


        return menu;
    }
}
