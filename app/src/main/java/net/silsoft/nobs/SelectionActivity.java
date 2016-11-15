package net.silsoft.nobs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import net.silsoft.nobs.models.Diagram;
import net.silsoft.nobs.models.Menu;
import net.silsoft.nobs.models.MenuElement;

public class SelectionActivity extends Activity {
    private final String MENU_FILE="menu_file";

    private String menu_file="home"; // .json is added in class, file need to be in menus folder

    public void setMenuFile(String menu_file) {
        this.menu_file = menu_file;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();
        String mf = null;
        try {
            mf = b.getString(MENU_FILE);
        }catch(Exception ex){
        }

        if (mf!=null){
            setMenuFile(b.getString(MENU_FILE));
        };

        //FULL SCREEN
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //IF HOME SCREEN -> hide navigation
        //todo : this sucks because first touch will just bring navigation bar not click the button -> for now disable
        if (menu_file.equals("home")){
//            View decorView = getWindow().getDecorView();
//            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
//            decorView.setSystemUiVisibility(uiOptions);
        }
        Menu menu = Menu.CreateFromJSONFile(menu_file, this);

        LayoutParams layoutParams;
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        TextView titleLabel = new TextView(this);
        titleLabel.setText(R.string.app_title);
        titleLabel.setGravity(Gravity.CENTER);
        mainLayout.addView(titleLabel);

        //add formula when possible + maybe some more scale information in future ?

        if (menu.getInfo()!=null) {
            TextView formulaLabel = new TextView(this);
            formulaLabel.setText(menu.getInfo().getFormulaText());
            formulaLabel.setGravity(Gravity.CENTER);
            mainLayout.addView(formulaLabel);
        }

        mainLayout.setBackgroundColor(Color.BLACK);
        mainLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        ScrollView scrollView = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);

        layout.setBackgroundColor(Color.BLACK); //todo : if theme
        scrollView.setBackgroundColor(Color.BLACK);
        layout.setOrientation(LinearLayout.VERTICAL);


        if (menu.getTitle().length()>0){
            titleLabel.setText(menu.getTitle());
        }

        Button btn;
        DiagramView diagramView;
        Diagram diagram;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        for (int i = 0 ; i< menu.getElements().length; i++){
            btn = new Button(this);


            final MenuElement me = menu.getElement(i);

            btn.setText(me.getTitle());
            btn.setPadding(30, 30, 30, 30); //todo : move this values somewhere
            btn.setTextSize(30); //todo : move this values somewhere
            btn.setBackgroundColor(Color.DKGRAY);
            btn.setTextColor(Color.WHITE);
            layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(30, 30, 30, 30);  //todo : move this values somewhere like styles

            btn.setLayoutParams(layoutParams);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //it can be either another Selection Activity or Diagram View

                    if (me.getType().equals(me.TYPE_MENU)) {
                        Intent intent = new Intent(getApplicationContext(), SelectionActivity.class);
                        Bundle b = new Bundle();
                        b.putString(MENU_FILE, me.getLink());
                        intent.putExtras(b);
                        startActivity(intent);
                    }

                    if (me.getType().equals(me.TYPE_DIAGRAM)) {
                        //launch diagram set view
                        Intent intent = new Intent(getApplicationContext(), DiagramActivity.class);
                        Bundle b = new Bundle();
                        b.putString(DiagramActivity.DIAGRAM_FILE, me.getLink());
                        intent.putExtras(b);
                        startActivity(intent);
                    }


//                    if (me.getType().equals(me.TYPE_ACTIVITY)) {
//                        //todo : if I need in future will need to make some mods, for now only settings
//                        Intent intent = new Intent(getApplicationContext(), UserSettingsActivity.class);
//                        startActivity(intent);
//                    }

                }
            });


            int diagramWidth = metrics.widthPixels / 2 + metrics.widthPixels / 8 ;
            // -> add diagram to list as well if the link is type diagram
            if (me.getType().equals("diagram")) {
                diagram = Diagram.CreateFromJson(me.getLink(), this);
                diagramView = new DiagramView(this);
                // + todo : recycle bitmaps somehow or do something to prevent creation of so many bitmaps I will run our of memory ...
                diagramView.setDiagram(diagram);
                diagramView.setColorSchema(diagramView.THEME_WHITE_ON_BLACK);
                diagramView.showDiagramTitles = false;

                diagramView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, diagramWidth ));

                diagramView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //launch diagram set view
                        Intent intent = new Intent(getApplicationContext(), DiagramActivity.class);
                        Bundle b = new Bundle();
                        b.putString(DiagramActivity.DIAGRAM_FILE, me.getLink());
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                });
                layout.addView(diagramView);
            }else{

                layout.addView(btn);
            }

        }


        mainLayout.addView(scrollView);
        scrollView.addView(layout);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.addView(mainLayout);
        relativeLayout.setBackgroundColor(Color.BLACK);

        if (menu_file.equals("home")) {
            LinearLayout bottomHome = new LinearLayout(this);
            bottomHome.setOrientation(LinearLayout.VERTICAL);
            RelativeLayout.LayoutParams buttonSettingsLayoutParams =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            buttonSettingsLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            bottomHome.setLayoutParams(buttonSettingsLayoutParams); //todo fix name + probably type

            Button buttonInfo = new Button(this);
            buttonInfo.setText("Info"); //todo translate
            buttonInfo.setTextSize(20);
            buttonInfo.setTextColor(Color.WHITE);
            buttonInfo.setBackgroundColor(Color.DKGRAY);
            LayoutParams buttonMoreLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonMoreLayoutParams.setMargins(30,30,30,30);  //todo in one place !
            buttonInfo.setLayoutParams(buttonMoreLayoutParams);
            buttonInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://github.com/kubber/nobs";  //todo -> put some redirector here so I can change destination !
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            bottomHome.addView(buttonInfo);


            Button buttonSettings = new Button(this);
            buttonSettings.setText("Settings"); //todo transalte  lub ikonki ?
            buttonSettings.setBackgroundColor(Color.DKGRAY);
            buttonSettings.setTextSize(20);
            buttonSettings.setTextColor(Color.WHITE);
            buttonSettings.setLayoutParams(buttonMoreLayoutParams);
            bottomHome.addView(buttonSettings);

            buttonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), UserSettingsActivity.class);
                    startActivity(intent);
                }
            });


            relativeLayout.addView(bottomHome);
        }

        setContentView(relativeLayout);
    }


//    public static class UserSettingsActivity extends PreferenceActivity{
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.preferences);
//        }
//    }
}
