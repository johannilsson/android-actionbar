package com.markupartist.android.actionbar.example;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        //actionBar.setHomeAction(new IntentAction(this, createIntent(this), R.drawable.ic_title_home_demo));
        actionBar.setTitle("Home");
        actionBar.addAction(new IntentAction(this, createShareIntent(), R.drawable.ic_title_share_default));
        actionBar.addAction(new IntentAction(this, new Intent(this, OtherActivity.class), R.drawable.ic_title_export_default));
        
        Button startProgress = (Button) findViewById(R.id.start_progress);
        startProgress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setProgressBarVisibility(View.VISIBLE);
            }
        });
        
        Button stopProgress = (Button) findViewById(R.id.stop_progress);
        stopProgress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setProgressBarVisibility(View.GONE);
            }
        });

        Button removeActions = (Button) findViewById(R.id.remove_actions);
        removeActions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBar.removeAllActions();
            }
        });


    }

    public static Intent createIntent(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }

    private Intent createShareIntent() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Shared from the ActionBar widget.");
        return Intent.createChooser(intent, "Share");
    }
}