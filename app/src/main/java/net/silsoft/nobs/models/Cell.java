package net.silsoft.nobs.models;

public class Cell {

    private String value;

    public String getValue(){
        return value;
    }

    public Cell(String v){
        value = v;
    }

    public boolean isEmpty(){
        if (value.equals("null")||value.equals("")) return true;
        return false;
    }

    public boolean isRoot(){
        if (value.equals("1")) return true;
        return false;
    }
}
