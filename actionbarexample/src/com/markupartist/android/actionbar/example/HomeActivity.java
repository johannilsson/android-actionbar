package com.markupartist.android.actionbar.example;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class HomeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setTitle("Home");
        actionBar.setHomeAction(new IntentAction(this, createIntent(this), R.drawable.ic_title_home_demo));
        actionBar.addAction(new IntentAction(this, createShareIntent(), R.drawable.ic_title_share_default));
        actionBar.addAction(new IntentAction(this, new Intent(this, OtherActivity.class), R.drawable.ic_title_export_default));
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