package com.markupartist.android.actionbar.example;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.AbstractAction;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class OtherActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other);

        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setTitle("Other");
        actionBar.setHomeAction(new IntentAction(this, HomeActivity.createIntent(this), R.drawable.ic_title_home_default));
        actionBar.addAction(new IntentAction(this, createShareIntent(), R.drawable.ic_title_share_default));
        actionBar.addAction(new ExampleAction());
    }

    private Intent createShareIntent() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Shared from the ActionBar widget.");
        return Intent.createChooser(intent, "Share");
    }

    private class ExampleAction extends AbstractAction {

        public ExampleAction() {
            super(R.drawable.ic_title_export_default);
        }

        @Override
        public void performAction() {
            Toast.makeText(OtherActivity.this,
                    "Example action", Toast.LENGTH_SHORT).show();
        }

    }

}