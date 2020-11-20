package com.example.reversi;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;


public class PlayingActivity extends AppCompatActivity {

    final String PLAYER_NAME_ANONYMOUS = "Player1", FIRST_SCORE = "2";
    private String first_move;
    private int BOARD_SIZE = 8;
    private int[][] board;
    private BoardView boardView;
    private Player computer, player;
    private TextView player_name, player_scoreView, computer_scoreView;

    public static int getColorPieceComputer(int color_player) {
        if (color_player == GameBoard.black) return GameBoard.white;
        else return GameBoard.black;
    }

    public static int getColorPiecePlayer(int computer_player) {
        if (computer_player == GameBoard.black) return GameBoard.white;
        else return GameBoard.black;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        //Setting Toolbar
        settingToolbar();

        //Setting boardView
        boardView = findViewById(R.id.board);

        //Save the preferences of the game
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPreferences.getString("name", "");

        //If name is empty then name it Player1
        if (name.equals(""))
            name = PLAYER_NAME_ANONYMOUS;

        int depth = sharedPreferences.getInt("difficulty", 0);
        final int color_player = getColorPiece(sharedPreferences.getString("piece color", ""));
        first_move = sharedPreferences.getString("first move", "");
        final int color_computer = getColorPieceComputer(color_player);

        //Setting textview for player's name and scores
        player_name = findViewById(R.id.player_name);
        player_scoreView = findViewById(R.id.player_score);
        computer_scoreView = findViewById(R.id.computer_score);

        player_scoreView.setText(FIRST_SCORE);
        computer_scoreView.setText(FIRST_SCORE);
        player_name.setText(name);

        //Create the real player and thw computer
        player = new Player(name, color_player, 1);//human player
        computer = new Player(getResources().getString(R.string.computer_name), color_computer, depth);//computer player
        boardView.setPlayer(player);
        boardView.setComputer(computer);

        //If computer plays first then set last player the "real" player and call computer moving
        //else  set last player the computer and wait for touch
        if (first_move.equals("computer")) {
            boardView.setLastPlayer(player.getDiskType());
            boardView.invokeComputer();
        } else {
            boardView.setLastPlayer(computer.getDiskType());
        }


        /*HasChanged is a variable that is connected with the
            boardView , and when is setting with the string content "true"
            it updates scoresView of computer and player;
         */
        final Observer<String> nameObserver = new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("true")) {
                    //hasChanged variable is true
                    board = boardView.getBoard();
                    int player_score = 0;
                    int computer_score = 0;
                    for (int i = 0; i < BOARD_SIZE; i++) {
                        for (int j = 0; j < BOARD_SIZE; j++) {
                            if (board[i][j] == computer.getDiskType()) {
                                computer_score++;
                            } else if (board[i][j] == player.getDiskType()) {
                                player_score++;
                            }
                        }
                    }


                    player_scoreView.setText(String.valueOf(player_score));
                    computer_scoreView.setText(String.valueOf(computer_score));

                } else {
                    Log.i("score not", "updated");
                }
            }

        };
        boardView.getHasChanged().observe(this, nameObserver);

        ImageView computer_image = (ImageView) findViewById(R.id.computer_piece);
        ImageView player_image = (ImageView) findViewById(R.id.player_piece);
        if (computer.getDiskType() == GameBoard.black) {
            computer_image.setImageResource(R.drawable.black);
            player_image.setImageResource(R.drawable.white);
        } else {
            computer_image.setImageResource(R.drawable.white);
            player_image.setImageResource(R.drawable.black);
        }

    }

    public AlertDialog.Builder confirmDialogExit() {
        return new AlertDialog.Builder(PlayingActivity.this)
                .setTitle("Exit")
                .setMessage("Do you really want to exit?")
                .setIcon(android.R.drawable.ic_dialog_alert);

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder confirmDialog = confirmDialogExit();
        confirmDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                PlayingActivity.super.onBackPressed();
            }
        }).setNegativeButton(android.R.string.no, null).show();


    }

    private int getColorPiece(String color) {
        if (color.equals("black")) return GameBoard.black;
        else return GameBoard.white;
    }

    public void settingToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("Reversi");
        toolbar.setSubtitle("Playing Game...");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10.f);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

}