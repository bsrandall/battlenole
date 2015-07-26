package com.android.battlenoleproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndGame extends Activity {
    private Button mMainMenu;
    private TextView winText;
    boolean winner = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        Context context = getApplicationContext();
        winText = (TextView) findViewById(R.id.winText);
        Intent endIntent = getIntent();
        winner = endIntent.getBooleanExtra("my_winner", false);

        if (!winner) {
            winText.setText("You Lose");
        }

        mMainMenu = (Button) findViewById(R.id.goToMain);

        mMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EndGame.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}
