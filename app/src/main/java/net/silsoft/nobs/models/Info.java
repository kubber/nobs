package net.silsoft.nobs.models;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class Info {

    String formula_text;
    String[] formula;

    public String getFormulaText() {
        return TextUtils.join(" - ",formula);
    }

    public static Info CreateFromJSONFile(String filename, Context context){
        Info info = new Info();
        filename="info/"+filename+".json";

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
            if (obj.has("formula")) {
                JSONArray j_elements = obj.getJSONArray("formula");
                info.formula = new String[j_elements.length()];
                for(int i=0; i<j_elements.length();i++){
                    info.formula[i]=j_elements.getString(i);
                }

            }
        }catch (JSONException e){
            throw new RuntimeException(e.getMessage());
        }


        return info;
    }


}
