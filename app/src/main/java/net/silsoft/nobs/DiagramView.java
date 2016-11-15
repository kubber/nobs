package net.silsoft.nobs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import net.silsoft.nobs.models.Cell;
import net.silsoft.nobs.models.Diagram;
import net.silsoft.nobs.models.NoteTranslator;

public class DiagramView extends View {

    public static final String THEME_BLACK_ON_WHITE="BlackOnWhite";
    public static final String THEME_WHITE_ON_BLACK="WhiteOnBlack";

    public static final String MARKER_MODE_NUMBERS = "Numbers";
    public static final String MARKER_MODE_NOTE_NAMES = "Notes";

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    Context context;
    private Diagram diagram;
    private String markerMode = MARKER_MODE_NUMBERS;

    private NoteTranslator noteTranslator = new NoteTranslator();

    public Boolean showDiagramTitles=true;

    private String key="C";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    Paint gridPaint = new Paint();
    Paint textOnMarkerPaint = new Paint();
    Paint markerPaint = new Paint();
    Paint textPaint = new Paint();  // title label
    Paint rootMarkerPaint = new Paint();
    Paint rootTextOnMarkerPaint = new Paint();

    private int strokeWidth = 5;

    public String getMarkerMode() {
        return markerMode;
    }

    public void setMarkerMode(String val){
        markerMode = val;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setColorSchema(String cs){

        if (cs.equals(THEME_BLACK_ON_WHITE)){
            gridPaint.setStrokeWidth(getStrokeWidth());
            gridPaint.setColor(Color.BLACK);
            markerPaint.setColor(Color.BLACK);
            markerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            textOnMarkerPaint.setColor(Color.WHITE);
            textPaint.setColor(Color.BLACK);
            rootMarkerPaint.setColor(Color.RED);
            rootMarkerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            rootTextOnMarkerPaint.setColor(Color.WHITE);
        }else{ //for now we only have 2 themes -> worry later
            gridPaint.setStrokeWidth(getStrokeWidth());
            gridPaint.setColor(Color.LTGRAY);
            markerPaint.setColor(Color.LTGRAY);
            markerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            textOnMarkerPaint.setColor(Color.BLACK);
            textPaint.setColor(Color.WHITE);
            rootMarkerPaint.setColor(Color.RED);
            rootMarkerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            rootTextOnMarkerPaint.setColor(Color.WHITE);
        }
    }

    public DiagramView(Context c) {
        super(c);
        context = c;

        markerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textOnMarkerPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float x1,y1,x2,y2;
        //displaying the grid can be either VERTICAL or HORIZONTAL  ( or it can switch on device orientation )
        //isn't it enough to switch cells matrix ?
//        diagram.setOrientation(Diagram.ORIENTATION_HORIZONTAL);

        float margin_top = height/10;
        float margin_left = width/10;

        float max_dim = width>=height?width:height;
        float max_grid_dim = diagram.getColsNumber()>=diagram.getRowsNumber()?diagram.getColsNumber():diagram.getRowsNumber();

        float cell_size = (max_dim - 2*margin_left - margin_top)  / (max_grid_dim+2) ;
        if (diagram.getOrientation().equals(Diagram.ORIENTATION_HORIZONTAL)) {
            cell_size -= cell_size*0.2;
        }

        float diagram_width = cell_size * (diagram.getColsNumber()-1);
        float diagram_height = cell_size * diagram.getRowsNumber();

        margin_left = (width - diagram_width) /2 ;
        margin_top = (height - diagram_height) / 2;   //todo : this needs some adjustment

        if (diagram.getOrientation().equals(Diagram.ORIENTATION_HORIZONTAL)) {
            margin_top += cell_size/1.5 ;
        }

        textPaint.setTextSize(margin_top / 2); //todo this needs adjustments
        if (textPaint.measureText(diagram.getTitle())>diagram_width){
            textPaint.setTextSize(margin_top/5);
        }

        //Diagram Title
        if (showDiagramTitles) {
            if (diagram.getOrientation().equals(Diagram.ORIENTATION_VERICAL)) {
                canvas.drawText(diagram.getTitle(),
                        (width / 2) - (textPaint.measureText(diagram.getTitle()) / 2)
                        , margin_top / 2 + margin_top / 10, textPaint);
            } else {
                canvas.drawText(diagram.getTitle(),
                        (width / 2) - (textPaint.measureText(diagram.getTitle()) / 2) + cell_size / 2
                        , margin_top / 2 + margin_top / 10, textPaint);
            }
        }

        // draw the grid : for horizontal diagram it's one row less and one column more
        int colfix =diagram.getOrientation().equals(diagram.ORIENTATION_VERICAL)?0:1;
        int rowfix=diagram.getOrientation().equals(diagram.ORIENTATION_VERICAL)?0:-1;

        for(int i=0;i<diagram.getColsNumber() + colfix;i++){
            x1 = x2 = margin_left + i*cell_size;
            y1 = margin_top;
            y2 = margin_top + (diagram.getRowsNumber() + rowfix) *cell_size;
            canvas.drawLine(x1,y1,x2,y2, gridPaint);
//            Log.i("@@@","drawing line : "+x1+"/"+y1+"/"+x2+"/"+y2);
        }

        for(int i=0;i<diagram.getRowsNumber()+1 + rowfix;i++){
            x1 = margin_left ;
            y1 = y2 = margin_top + i*cell_size;
            x2 = margin_left + (diagram.getColsNumber() - 1 + colfix) * cell_size;
            canvas.drawLine(x1,y1,x2,y2, gridPaint);
        }

        //draw markers
        Cell cell;
        float x,y, text_x, text_y;
        float markerRadius = cell_size/3;

//        Log.i("@@@","Rows : " + diagram.getRowsNumber());
//        Log.i("@@@","Cols : " + diagram.getColsNumber());

        noteTranslator.setKey(key);

        String markerText="";

        for(int i=0; i<diagram.getRowsNumber(); i++){
            for(int j=0; j<diagram.getColsNumber(); j++){
                cell = diagram.getCell(i,j);
                if (cell.isEmpty()) continue; // no marker at this position
                textOnMarkerPaint.setTextSize(markerRadius);
                rootTextOnMarkerPaint.setTextSize(markerRadius);

                if (diagram.getOrientation().equals(Diagram.ORIENTATION_VERICAL)) {
                    x = margin_left + j * cell_size ;
                    y = margin_top + i * cell_size + (cell_size / 2);
                    text_x = x - markerRadius / 4;  //todo this needs calibration
                    text_y = y + markerRadius / 4;
                }else{ // horizontal
                    x = margin_left + j * cell_size + (cell_size/2);
                    y = margin_top + i * cell_size;
                    text_x = x - markerRadius / 4;  //todo this needs calibration
                    text_y = y + markerRadius / 4;
                }

                if (cell.getValue().length()==2) text_x-=markerRadius/6;
                if (cell.getValue().length()==3) text_x-=markerRadius/2;


                if (markerMode.equals(MARKER_MODE_NUMBERS)){
                    markerText = cell.getValue();
                }else{
                    markerText = noteTranslator.translate(cell.getValue());
                }

                if (cell.isRoot()){
                    //decide if box or red or both - just red seems fine
//                    canvas.drawRect(x-markerRadius,y-markerRadius,x+markerRadius,y+markerRadius,rootMarkerPaint);
                    canvas.drawCircle(x, y, markerRadius, rootMarkerPaint);
                    canvas.drawText(markerText,text_x, text_y, rootTextOnMarkerPaint);
                }else{
                    canvas.drawCircle(x, y, markerRadius, markerPaint);
                    canvas.drawText(markerText,text_x, text_y, textOnMarkerPaint);
                }

            }
        }

    }

    public void setDiagram(Diagram diagram) {
        this.diagram = diagram;
    }

}
