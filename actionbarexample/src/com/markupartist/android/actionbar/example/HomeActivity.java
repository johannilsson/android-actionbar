package com.markupartist.android.actionbar.example;

import com.markupartist.android.widget.actionbar.ActionBar;
import com.markupartist.android.widget.actionbar.ActionBar.Action;
import com.markupartist.android.widget.actionbar.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class HomeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setTitle("Home");
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

    private class ExampleAction implements Action {

        @Override
        public int getDrawable() {
            return R.drawable.ic_title_export_default;
        }

        @Override
        public void performAction() {
            Toast.makeText(HomeActivity.this,
                    "Example action", Toast.LENGTH_SHORT).show();
        }
        
    }
}