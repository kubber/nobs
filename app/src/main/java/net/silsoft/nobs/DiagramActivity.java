package net.silsoft.nobs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.silsoft.nobs.models.Diagram;

public class DiagramActivity extends Activity{
    public static final String DIAGRAM_FILE="diagram_file";

    private String diagram_file;
    private View v;

    public String getDiagramFile() {
        return diagram_file;
    }

    public void setDiagramFile(String file){
        this.diagram_file = file;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle b = getIntent().getExtras();
        String mf = null;
        try {
            mf = b.getString(DIAGRAM_FILE);
        }catch(Exception ex){
            //todo : this is dirty there must be better way to test if element exists in bundle
        }
        if (mf!=null){
            setDiagramFile(b.getString(DIAGRAM_FILE));
        };

        //SET FULL SCREEN MODE
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //HIDE NAVIGATION BAR
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        final DiagramView diagramView;
        diagramView = new DiagramView(getApplicationContext());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final Diagram diagram = Diagram.CreateFromJson(getDiagramFile(), this);

//        Log.i("@@@","Rows " + diagram.getRowsNumber());
//        Log.i("@@@","Cols " + diagram.getColsNumber());

        String displayMode = preferences.getString("diagram_display_mode",Diagram.ORIENTATION_VERICAL);
        if (displayMode.equals(Diagram.ORIENTATION_VERICAL)){
            diagram.setOrientation(Diagram.ORIENTATION_VERICAL);
        }

        if (displayMode.equals(Diagram.ORIENTATION_HORIZONTAL)){
            diagram.setOrientation(Diagram.ORIENTATION_HORIZONTAL);
        }

        if (displayMode.equals(Diagram.ORIENTATION_ORIENTATION)){
            if (getResources().getConfiguration().orientation==2){
                //Landscape
                diagram.setOrientation(Diagram.ORIENTATION_HORIZONTAL);
            }else{
                //portrait
                diagram.setOrientation(Diagram.ORIENTATION_VERICAL);
            };

        }


        diagramView.showDiagramTitles = preferences.getBoolean("diagram_titles", true);
        diagramView.setDiagram(diagram);

        String theme = preferences.getString("theme", DiagramView.THEME_BLACK_ON_WHITE);

        if (theme.equals(DiagramView.THEME_BLACK_ON_WHITE)){
            diagramView.setColorSchema(DiagramView.THEME_BLACK_ON_WHITE);
            diagramView.setBackgroundColor(Color.WHITE);
        }else{
            diagramView.setColorSchema(DiagramView.THEME_WHITE_ON_BLACK);
            diagramView.setBackgroundColor(Color.BLACK);
        }

        //todo : add to json if diagram is available in free version or not
        //todo : more than single diagram on page ? , scrollable zoomaable ? , click on diagram brings single diagram view or fuck it ? - it would be cool to be able to see at least 1,2,3 chord, chord,scale, chord,scale,arpeggio ...

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.diagram_view, null);
        final LinearLayout diagramContainer = (LinearLayout)v.findViewById(R.id.diagram_container);
        diagramContainer.addView(diagramView);

        final Spinner spinner = (Spinner) v.findViewById(R.id.diagram_toolbar_keys_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.keys_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            //todo : add flag -> remember that it will be executed twice - first time on initialization second time on change !

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String option = spinner.getSelectedItem().toString();

                //todo : this should stay after orientation change and maybe even after diagram change - but for sure after orientation chagne
                if (option.equals("123")) { //todo : hardcoded value ! what if translations are enabled !
                    diagramView.setMarkerMode(DiagramView.MARKER_MODE_NUMBERS);
                } else {
                    diagramView.setKey(option);
                    diagramView.setMarkerMode(DiagramView.MARKER_MODE_NOTE_NAMES);
                }

                //refresh diagram
                setContentView(v);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setContentView(v);

        diagramContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //this will trigger setOnSystemUiVisibilityChange as well
                    //todo probably better to use some flag/semaphore as well here .
                    View decorView = getWindow().getDecorView();
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                }
                return true;
            }
        });

        //show action bar when full screen is shown
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                Toolbar toolbar = (Toolbar)findViewById(R.id.diagram_toolbar);
                if (visibility == 0) {
                    toolbar.setVisibility(View.VISIBLE);
                }else{
                    toolbar.setVisibility(View.GONE);
                }
            }
        });


    }


}
