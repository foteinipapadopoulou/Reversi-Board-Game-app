package com.example.reversi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class BoardView extends View {
    private Canvas canvas;
    private final Paint paint = new Paint();
    private int cols,rows;
    private int sizeOfTile;
    private int [][] board;
    private Bitmap bitmap;
    public BoardView(Context context){
       super(context);
        initBoard(8,8);
    }
    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initBoard(8,8);
    }

    public void initBoard(int numCol,int numRow){

        //Define number of columns and rows and size of each tile
        cols = numCol;
        rows = numRow;
        sizeOfTile = 100;

        //Initialize board with zeros
        board = new int[rows][cols];
        initBoardTable();
        Bitmap bitmap = Bitmap.createBitmap(sizeOfTile,sizeOfTile, Bitmap.Config.ARGB_8888);
        canvas = new Canvas (bitmap);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

    }

    private void initBoardTable() {
        for(int x = 0; x < rows; x++){
            for(int y = 0; y < cols; y++){
                board[x][y] = 0;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.colorBoard));
        drawTiles(canvas);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        drawTiles(canvas);

    }

    protected void drawTiles(Canvas canvas){
        int top = 0;
        int bottom = sizeOfTile;
        for(int j = 0;j < rows;j++){
            int left = 0;
            int right = sizeOfTile;
            for(int i = 0;i < cols; i++) {
                Rect r = new Rect(left,top,right,bottom);
                canvas.drawRect(r, paint);
                left += sizeOfTile;
                right += sizeOfTile;
            }

            top +=sizeOfTile;
            bottom += sizeOfTile;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        sizeOfTile = w / rows;
        int temp = h / cols;
        if(sizeOfTile > temp){
            sizeOfTile=temp;
        }
        Log.e("Tile", String.valueOf(sizeOfTile));
    }

}
