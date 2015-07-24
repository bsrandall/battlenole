package com.android.battlenoleproject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class PlayComputerActivity extends Activity
    implements com.android.battlenoleproject.EnemyBoardFragment.OnFragmentFireInteractionListener,
        com.android.battlenoleproject.EnemyBoardFragment.OnEnemyFragmentContinueInteractionListener,
        PlayerBoardFragment.OnPlayerFragmentContinueInteractionListener{

    Ship[] player1Ships, computerShips;
    int player1, computer, playerTurn;

    Game game;

    private final static int ENEMY_PLAYER_NUMBER = 1;
    private final static int PLAYER_PLAYER_NUMBER = 0;

    private final static int FIRE_MISS = 61;
    private final static int FIRE_HIT = 62;

    private static final String BOARD_KEY = "board";

    private Handler myHandler = new Handler();

    private static final Random r = new Random();

    protected int random;

    private TextView topTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        Bundle bundle = getIntent().getExtras();

        Parcelable ps1[] = bundle.getParcelableArray("player1Ships");
        Parcelable ps2[] = bundle.getParcelableArray("player1Ships");

        player1Ships = new Ship[ps1.length];
        computerShips = new Ship[ps2.length];

        System.arraycopy(ps1, 0, player1Ships, 0, ps1.length);

        System.arraycopy(ps2, 0, computerShips, 0, ps1.length);

        getIntent().removeExtra("player1Ships");
        getIntent().removeExtra("player2Ships");


        game = new Game(player1Ships, computerShips);
        player1 = PLAYER_PLAYER_NUMBER;
        computer = ENEMY_PLAYER_NUMBER;

        playerTurn = 0;  // changed this to test, needs to be 0


        topTV = (TextView) findViewById(R.id.play_tv1);

        playGame();

    }

    @Override
    public void onFragmentFireInteraction(int position) {

        processPlayerFire(position);

    }

    @Override
    public void onPlayerFragmentContinueInteraction() {

        playGame();

    }

    @Override
    public void onEnemyFragmentContinueInteraction() {

        playGame();

    }


    protected void displayArrangeGameScreen(int playerTurn){


        if (playerTurn == ENEMY_PLAYER_NUMBER) {

            topTV.setText("Enemy is Firing!");

            Board playerBoard = new Board(game.getPlayerBoard(PLAYER_PLAYER_NUMBER));

            PlayerBoardFragment playerFragment = PlayerBoardFragment.newInstance(playerBoard);


            EnemyBoardFragment enemyBoardFragment = new EnemyBoardFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Context context = getApplicationContext();
            enemyBoardFragment =  (EnemyBoardFragment) fragmentManager.findFragmentByTag("enemyBoard");

            if (enemyBoardFragment != null)
                fragmentTransaction.remove(enemyBoardFragment);



            ViewGroup mainContainer = (ViewGroup) findViewById(R.id.grid_holder);


            fragmentTransaction.replace(mainContainer.getId(), playerFragment, "playerBoard");
            //fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();

        }

        else {

            topTV.setText("Pick a cell to fire on");

            Board enemyBoard = new Board(game.getPlayerBoard(ENEMY_PLAYER_NUMBER));

            EnemyBoardFragment enemyFragment = EnemyBoardFragment.newInstance(enemyBoard);

            PlayerBoardFragment playerBoardFragment = new PlayerBoardFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Context context = getApplicationContext();
            playerBoardFragment =  (PlayerBoardFragment) fragmentManager.findFragmentByTag("playerBoard");

            if (playerBoardFragment != null)
                fragmentTransaction.remove(playerBoardFragment);


            ViewGroup mainContainer = (ViewGroup) findViewById(R.id.grid_holder);


            fragmentTransaction.replace(mainContainer.getId(), enemyFragment, "enemyBoard");
           // fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();

        }


    }


    public void playGame() {


            if (playerTurn == 0) {                          // It is Player's turn show enemy board
                displayArrangeGameScreen(playerTurn);
                ;
            }

            else {                                        // It is enemy's turn show player's board
                displayArrangeGameScreen(playerTurn);
                while (game.getPlayerBoard(0).getElementAtBoardPosition(random) >= FIRE_MISS )  //  (don't select a cell that we have already hit
                    random = r.nextInt(99);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                            processEnemyFire(random);

                    }
                }, 2000);

              //  processEnemyFire(random);
                //myHandler.postDelayed(displayTurnResults, 10000);


            }



    }


    public void processPlayerFire(int position) {


        int result = this.game.processMove(playerTurn, position);


        EnemyBoardFragment enemyBoardFragment = new EnemyBoardFragment();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Context context = getApplicationContext();
        enemyBoardFragment =  (EnemyBoardFragment) fragmentManager.findFragmentByTag("enemyBoard");
        enemyBoardFragment.fire(position, result);



        if (result != FIRE_HIT) { // it was a miss, switch turns
            topTV.setText("Sorry, you missed. Switching to Enemy Turn.");
            playerTurn = game.getOpposite(playerTurn);
        }
        else {
            topTV.setText("You Hit the Enemy! Go Again.");
            if (game.fleetStillAlive(playerTurn))
                processWinner(playerTurn);
        }

        myHandler.postDelayed(changePlayers, 1000);

    }


    public void processEnemyFire(int position) {


        int result = this.game.processMove(playerTurn, position);


        PlayerBoardFragment playerBoardFragment = new PlayerBoardFragment();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Context context = getApplicationContext();
        playerBoardFragment =  (PlayerBoardFragment) fragmentManager.findFragmentByTag("playerBoard");
        playerBoardFragment.fire(position, result);




        if (result != FIRE_HIT) {// it was a miss, switch turns
            topTV.setText("Enemy Missed. Switching Turns...");
            playerTurn = game.getOpposite(playerTurn);
        }
        else {
            topTV.setText("Oh no, You were Hit!");
            if (game.fleetStillAlive(playerTurn))
                processWinner(playerTurn);
        }


        myHandler.postDelayed(changePlayers, 2000);

    }



    public void processWinner(int playerNumber) {

    }


    private Runnable changePlayers = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            playGame();
      /* and here comes the "trick" */
            // myHandler.postDelayed(this, 100);
        }
    };








}
