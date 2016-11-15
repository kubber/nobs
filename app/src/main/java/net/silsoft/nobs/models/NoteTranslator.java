package net.silsoft.nobs.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class NoteTranslator {

    final List<String> notes_sharp = Arrays.asList("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B");
    final List<String> notes_flat = Arrays.asList("C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B");

    final List<String> sharps = Arrays.asList("C#","D","D#","E","E#","F#","G","G#","A","A#","B");
    final List<String> flats = Arrays.asList("C","Db","Eb","F","Gb","Ab","Bb");

    List<String> notes = notes_sharp;

    HashMap<String, Integer> numbers = new HashMap<String, Integer>();

    public void setKey(String k){
        if (sharps.contains(k)){
            notes = rotate(notes_sharp, notes_sharp.indexOf(k));
        }else{
            notes = rotate(notes_flat, notes_flat.indexOf(k));
        }
    }

    private static <T> List<T> rotate(List<T> aL, int shift) {
        List<T> newValues = new ArrayList<T>(aL);
        Collections.rotate(newValues, -shift);
        return newValues;
    }

    public NoteTranslator(){
        numbers.put("1",0);   // number, semitones
        numbers.put("b2",1);
        numbers.put("2",2);
        numbers.put("#2",3);
        numbers.put("b3",3);
        numbers.put("3",4);
        numbers.put("b4",3);
        numbers.put("4",5);
        numbers.put("#4",6);
        numbers.put("b5",6);
        numbers.put("5",7);
        numbers.put("#5",8);
        numbers.put("b6",8);
        numbers.put("6",9);
        numbers.put("bb7",9);
        numbers.put("b7",10);
        numbers.put("7",11);
        numbers.put("b9",1);
        numbers.put("9",2);
        numbers.put("11",5);
        numbers.put("#11",6);
        numbers.put("13",9);
    }

    /* "b2","G"  will return flat2 from G major */
    public String translate(String number){
        if (numbers.containsKey(number)) {
            if (notes.get(numbers.get(number))!=null){
                return notes.get(numbers.get(number));
            }else{
                return number;
            }
        }else{
            return number;
        }
    }



}
