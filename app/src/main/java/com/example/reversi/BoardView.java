package com.example.reversi;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class BoardView extends View {
    private final Paint paint = new Paint();
    //    Notify activity when score has changed
    private MutableLiveData<String> hasChanged = new MutableLiveData<String>();
    //    Available moves
    private ArrayList<Move> pos_moves;
    private Move move;

    private int cols = 8, rows = 8;
    private int sizeOfTile, pieceRadius, cellPadding;
    private int[][] board;

    private Context context;
    private Canvas canvas;
    private Bitmap bitmap;
    private GameBoard start;
    private Player player, computer;

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initBoard();

//        Handling touching the board
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    Getting the coordinations of touching
                    float downX = motionEvent.getX();
                    float downY = motionEvent.getY();
                    Log.e("TOUCHED X:", String.valueOf(motionEvent.getX()));
                    Log.e("TOUCHED Y:", String.valueOf(motionEvent.getY()));
                    if (isInCanvas(downX, downY)) {
                        int x = (int) downX / sizeOfTile;
                        int y = (int) downY / sizeOfTile;
                        Log.e("Touching in", "Canvas!");
                        move = new Move(x, y, player.getDiskType());
                        handleUserTouch(move);
                        return true;
                    } else {
                        hasChanged.setValue("false");
                        return false;
                    }

                }
                return false;
            }
        });
    }

    /* Check if the touching coordinations is in canvas */
    public boolean isInCanvas(float downX, float downY) {
        int x = (int) downX / sizeOfTile;
        int y = (int) downY / sizeOfTile;
        if (y < 8) {
            Log.e("TOUCHED TILE X=", String.valueOf(x));
            Log.e("and Y= ", String.valueOf(y));
            return true;
        } else {
            Log.e("Sorry not in the ", "Canvas");
            return false;
        }
    }

    public void initBoard() {

        //Find Size of Tile and set it
        setSizeOfTile(findSizeOfTile());

        //Initialize board with zeros
        initBoardTable();

        //Initialize Canvas
        initCanvas();
    }

    /*Initializing Game Board*/
    private void initBoardTable() {
        start = new GameBoard();
        board = start.getBoard();
    }

    private void initCanvas() {
        Bitmap bitmap = Bitmap.createBitmap(sizeOfTile, sizeOfTile, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
    }


    //    Drawing methods
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.colorBoard));
        drawTiles(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        drawTiles(canvas);

        paint.setStyle(Paint.Style.FILL);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (start.getBoard()[i][j] == GameBoard.white) {
                    paint.setColor(getResources().getColor(R.color.white));
                    canvas.drawCircle(i * sizeOfTile + cellPadding, j * sizeOfTile + cellPadding, pieceRadius, paint);
                } else if (start.getBoard()[i][j] == GameBoard.black) {
                    paint.setColor(getResources().getColor(R.color.black));
                    canvas.drawCircle(i * sizeOfTile + cellPadding, j * sizeOfTile + cellPadding, pieceRadius, paint);
                }
            }
        }

        if (player != null) {
            if (start.getLastLetterPlayed() == computer.getDiskType()) {
                ArrayList<Move> p = start.PossibleMoves(player.getDiskType());
                if (player.getDiskType() == GameBoard.black)
                    paint.setColor(getResources().getColor(R.color.possibleMovesBlack));
                else paint.setColor(getResources().getColor(R.color.possibleMovesWhite));
                for (Move m : p) {
                    canvas.drawCircle(m.getRow() * sizeOfTile + cellPadding, m.getCol() * sizeOfTile + cellPadding, pieceRadius, paint);
                }
            }
        }

    }

    protected void drawTiles(Canvas canvas) {
        int top = 0;
        int bottom = sizeOfTile;
        for (int j = 0; j < rows; j++) {
            int left = 0;
            int right = sizeOfTile;
            for (int i = 0; i < cols; i++) {
                Rect r = new Rect(left, top, right, bottom);
                canvas.drawRect(r, paint);
                left += sizeOfTile;
                right += sizeOfTile;
            }

            top += sizeOfTile;
            bottom += sizeOfTile;
        }
        invalidate();
    }

    private int findSizeOfTile() {
        //Find Height and width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        sizeOfTile = width / rows;
        int temp = height / cols;
        if (sizeOfTile > temp) {
            sizeOfTile = temp;
        }
        Log.e("Tile", String.valueOf(sizeOfTile));
        setPieceRadius(4 * sizeOfTile / 10);
        setCellPadding(sizeOfTile / 2);
        return sizeOfTile;
    }

    //    Getters and Setters
    public void setCellPadding(int s) {
        cellPadding = s;
    }

    public void setPieceRadius(int s) {
        pieceRadius = s;
    }

    public int getSizeOfTile() {
        return sizeOfTile;
    }

    public void setSizeOfTile(int s) {
        sizeOfTile = s;
    }

    public int[][] getBoard() {
        return start.getBoard();
    }

    public GameBoard getGameBoard() {
        return start;
    }

    public MutableLiveData<String> getHasChanged() {
        return hasChanged;
    }

    //    Refresh View
    public void refreshCanvas() {
        this.invalidate();
    }

    //    Handling Touch in the Canvas
    public void handleUserTouch(Move move) {
        Log.i("I am handling touch", " ");
//        Check if it 's turn's player
        if (start.IsTerminal()) {
            start.displayWinner();
            return;
        }
        if (start.getLastLetterPlayed() == computer.getDiskType()) {
            Log.i("Last player is ", "compueter");
            pos_moves = start.PossibleMoves(player.getDiskType());
            if (!pos_moves.isEmpty()) {
                Log.i("Possible Moves:", "");
                for (int i = 0; i < pos_moves.size(); i++) {
                    Log.i(pos_moves.get(i).toString(), " ");
                }
//                Check if it is a valid move in canvas
                if (start.isValidMove(move, player.getDiskType())) {
                    start.MakeMove(move);
                    hasChanged.setValue("true");
                    start.setLastLetterPlayed(player.getDiskType());
                    refreshCanvas();
                    if (start.IsTerminal()) start.displayWinner();
                    else invokeComputer();
                } else {
                    Toast toast = Toast.makeText(this.getContext(), "This is not a possible move.", Toast.LENGTH_LONG);
                    toast.show();
                }
            } else {
                Toast toast = Toast.makeText(this.getContext(), "No possible moves for " + player.getName(), Toast.LENGTH_LONG);
                toast.show();
                start.setLastLetterPlayed(player.getDiskType());
                refreshCanvas();
                if (start.IsTerminal()) start.displayWinner();
                else invokeComputer();
            }
        } else {
            Toast toast = Toast.makeText(this.getContext(), "It's not your turn", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setComputer(Player computer) {
        this.computer = computer;
    }

    public void setHasChanged(String s) {
        hasChanged.setValue(s);
    }

    public void invokeComputer() {
        start.nextTurn(player, computer, context, this, hasChanged);
        hasChanged.setValue("true");
        refreshCanvas();
    }

    public void setLastPlayer(int l) {
        start.setLastLetterPlayed(l);
    }
}

