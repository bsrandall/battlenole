package com.android.battlenoleproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by srandall on 7/12/15.
 */
public class SetupPlayer2 extends Activity {

    protected static final int NO_FIELD_IS_AIMED = -1;

    protected ImageAdapter imageAdapter;
    protected GridView boardGrid;
    private TextView mHeader;
    private ImageButton mBack;
    private ImageButton mRedeploy;
    private ImageButton mSetShip;

    protected Button buttonRotate;
    protected Button buttonStart;

    protected Ship[] player1Ships;
    protected Ship[] player2Ships;

    private static final String TAG = SetupActivity.class.getSimpleName();

    private boolean lastClickWasShip;

    private int clickedShipNumber, lastClickedShipNumber;

    private boolean playComputer;

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();

        Parcelable ps1[] = bundle.getParcelableArray("player1Ships");
        Parcelable ps2[] = bundle.getParcelableArray("player2Ships");

        player1Ships = new Ship[ps1.length];
      //   computerShips = new Ship[ps2.length];

        System.arraycopy(ps1, 0, player1Ships, 0, ps1.length);

        // System.arraycopy(ps2, 0, computerShips, 0, ps1.length);

        getIntent().removeExtra("player1Ships");
        //  getIntent().removeExtra("player2Ships");

        lastClickWasShip = false;
        clickedShipNumber = -1;
        lastClickedShipNumber = 0;


        player2Ships = new Ship[4];
        for (int i = 0; i < 4; ++i) {
            player2Ships[i] = new Ship(i + 2);
        }

        randomizeShips(player2Ships);


        displaySetupScreen();
        attachActionListeners();
        buttonStart.setEnabled(false);
        buttonRotate.setEnabled(true);


    }

    protected void displaySetupScreen() {
        setContentView(R.layout.activity_setup);

        buttonRotate = (Button) findViewById(R.id.buttonRotate);
        buttonStart = (Button) findViewById(R.id.buttonPlayGame);

        boardGrid = (GridView) findViewById(R.id.setup_gridview);

        imageAdapter = new ImageAdapter(this, this.player2Ships);
        boardGrid.setAdapter(imageAdapter);

        // Stop annoying default activity transition
        getWindow().setWindowAnimations(0);

        mHeader = (TextView) findViewById(R.id.headerTextView);

        mHeader.setText(" Player 2 Position Your Ships!");

    }

    private void attachActionListeners() {
        mBack = (ImageButton) findViewById(R.id.btnBack);
        mRedeploy = (ImageButton) findViewById(R.id.btnRedeploy);
        mSetShip = (ImageButton) findViewById(R.id.btnSetShips);

        // Return to main menu
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Redeploy ships
        mRedeploy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getApplicationContext(), "Redeploy ships", Toast.LENGTH_LONG).show();
                // Create new randomization and update view
                randomizeShips(player2Ships);
                imageAdapter.notifyDataSetChanged();

                //disable game start and re-enable rotate if necessary
                buttonStart.setEnabled(false);
                buttonRotate.setEnabled(true);
            }
        });

        // Lock in ship placement
        mSetShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code goes here
                // This enables the begin game button
                // Should we also disable some other buttons?
                buttonStart.setEnabled(true);
                buttonRotate.setEnabled(false);
                //Intent intent = new Intent(getApplicationContext(), com.android.battleshipmp.Game.class);
                //startActivity(intent);
            }
        });


        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonRotate.setEnabled(false);
                player1Ready();
            }
        });


        //boardGame GridView listener sets the aimedField property and changes aim color
        boardGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageView _iv = (ImageView) v;

                clickedShipNumber = isShip(position);
                if (clickedShipNumber != -1) { // this is a ship and we are returned ship number 0 - 4

                    lastClickWasShip = true;
                    lastClickedShipNumber = clickedShipNumber;


                } else {  // this is not a ship.

                    if (lastClickWasShip) {  // If a Ship was previous ImageView clicked
                        // we know the ship number that was clicked. We now know board position clicked
                        boolean valid = true;
                        for (int i = 0; i < player2Ships.length; ++i) {
                            if (i != lastClickedShipNumber) {
                                valid = shipNoConflicts(player2Ships[lastClickedShipNumber].getDirection(), position, player2Ships[lastClickedShipNumber].getLength(), player2Ships[i].getCoordinates());
                                if (valid == false)
                                    break;

                            }
                        }

                        if (valid == true) {
                            if (shipWillFit(player2Ships[lastClickedShipNumber].getDirection(), position,
                                    player2Ships[lastClickedShipNumber].getLength())) {

                                redrawShip(position, lastClickedShipNumber);

                            }

                        }
                    }


                }

                buttonRotate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lastClickedShipNumber != -1)
                            rotateShip(lastClickedShipNumber);
                    }
                });


            }
        });

    }

    private void executeFire() {

    }

    private void player1Ready() {


        Intent intent = new Intent(this.getApplicationContext(), OtherPlayer.class);

        Bundle bundle = new Bundle();
        bundle.putParcelableArray("player1Ships", player1Ships);
        bundle.putParcelableArray("player2Ships", player2Ships);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();

    }

    public void randomizeShips(Ship[] ships) {

        Random r = new Random();

        for (int shipCount = 0; shipCount < ships.length; ++shipCount)
            ships[shipCount].clearCoordinates();

        for (int shipCount = 0; shipCount < ships.length; ++shipCount) {
            int direction = r.nextInt(2);
            ships[shipCount].setDirection(direction);
            int randomStartPosition = r.nextInt(99);
            boolean valid = false;
            while (valid == false) {
                randomStartPosition = r.nextInt(99);
                if (shipWillFit(direction, randomStartPosition, ships[shipCount].getLength())) {
                    valid = true;
                    if (shipCount > 0) {
                        for (int i = shipCount - 1; i > 0; --i) {
                            valid = shipNoConflicts(direction, randomStartPosition, ships[shipCount].getLength(), ships[i].getCoordinates());  // 7-25 PM updates
                            if (valid == false)
                                break;
                        }
                    }

                }
            }  // valid is true so we have a valid start position and direction for this ship
            // we now need to update this ship's coordinates - still within for loop for each ship

            ArrayList<Integer> coordinates = new ArrayList<Integer>(ships[shipCount].getLength());

            if (direction == 0) {

                for (int i = 0; i < ships[shipCount].getLength(); ++i) {
                    coordinates.add(randomStartPosition + i);
                }

            } else {
                for (int i = 0; i < ships[shipCount].getLength(); ++i) {
                    coordinates.add(randomStartPosition + (i * 10));
                }

            }

            ships[shipCount].setCoordinates(coordinates);


        }  // end of for each ship

    }  // end randomizeShips()


    public boolean shipNoConflicts(int direction, int startPosition, int length, ArrayList<Integer> coordinates) {
        boolean valid = true;
// if direction is horizontal, have to check in loop for startPosition + checkedShipped length -1
        if (direction == 0) {
            for (int i = startPosition; i < startPosition + length; ++i) {
                if (coordinates.contains(i)) {  // horizontal
                    valid = false;
                    break;
                }
            }
        } else {
            for (int i = startPosition; i < startPosition + (length * 10); i += 10) {
                if (coordinates.contains(i)) {  // horizontal
                    valid = false;
                    break;
                }
            }
        }

        return valid;
    }

    public boolean shipWillFit(int direction, int startPosition, int shipLength) {
        boolean valid = false;

        if (direction == 0) {   // horizontal
            if ((startPosition + 10) / 10 * 10 - startPosition >= shipLength) {
                valid = true;
            }

        } else {
            if (((shipLength - 1) * 10) + startPosition < 100) {
                valid = true;
            }

        }
        return valid;
    }

    public int isShip(int position) {
        int shipNumber = -1;

        for (int shipCount = 0; shipCount < player2Ships.length; ++shipCount) {

            ArrayList<Integer> coordinates = new ArrayList<>(this.player2Ships[shipCount].getLength());
            coordinates = this.player2Ships[shipCount].getCoordinates();

            if (coordinates.indexOf(position) != -1) {
                shipNumber = shipCount;
                break;
            }

        }

        return shipNumber;
    }

    public void rotateShip(int shipNumber) {
        boolean valid = true;
        int oldDirection = this.player2Ships[shipNumber].getDirection();
        int newDirection;
        if (oldDirection == 0)
            newDirection = 1;
        else
            newDirection = 0;


        for (int i = 0; i < 2; ++i) {   // take current startPosition and try adding from 0 to 2 to see if it will rotate


            if (shipWillFit(newDirection, this.player2Ships[shipNumber].getStartPosition() + i,
                    this.player2Ships[shipNumber].getLength())) {

                for (int j = 0; j < player2Ships.length; ++j) {   // if it will fit at startPosition + i

                    if (j != shipNumber) {  // Do not check the ship you are trying to rotate
                        valid = shipNoConflicts(newDirection, this.player2Ships[shipNumber].getStartPosition() + i,
                                this.player2Ships[shipNumber].getLength(), this.player2Ships[j].getCoordinates());
                        if (valid == false) {

                            break;
                        }
                    }
                }

                if (valid == true) {

                    this.player2Ships[shipNumber].setDirection(newDirection);
                    redrawShip(this.player2Ships[shipNumber].getStartPosition(), shipNumber);
                    break;
                }

            }
            if (valid == true)
                break;

        }


    }


    public void redrawShip(int position, int shipNumber) {

        int imageResource = R.drawable.white;

        ArrayList<Integer> oldCoordinates = new ArrayList<>(this.player2Ships[shipNumber].getLength());

        ArrayList<Integer> newCoordinates = new ArrayList<>(this.player2Ships[shipNumber].getLength());

        oldCoordinates = this.player2Ships[shipNumber].getCoordinates();

        if (player2Ships[shipNumber].getDirection() == 0) {

            for (int i = 0; i < player2Ships[shipNumber].getLength(); ++i) {
                newCoordinates.add(position + i);
            }

        } else {
            for (int i = 0; i < player2Ships[shipNumber].getLength(); ++i) {
                newCoordinates.add(position + (i * 10));
            }

        }

        player2Ships[shipNumber].setCoordinates(newCoordinates);


        imageAdapter.notifyDataSetChanged();

    }
}
