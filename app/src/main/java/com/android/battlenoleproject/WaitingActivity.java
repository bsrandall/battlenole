package com.android.battlenoleproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by stephanie on 7/23/15.
 */
public class WaitingActivity extends Activity {
    private ImageButton mBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);

        // Stop annoying default activity transition
        getWindow().setWindowAnimations(0);

        mBack = (ImageButton) findViewById(R.id.btnBack);

        // Return to previous menu
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
