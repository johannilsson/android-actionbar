package com.markupartist.android.actionbar.example;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Tab;
import com.markupartist.android.widget.ActionBar.TabListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;

public class OtherActivity extends Activity implements TabListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other);

        ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        
        getMenuInflater().inflate(R.menu.other_actionbar, actionBar.asMenu());
        actionBar.findAction(R.id.actionbar_item_home).setIntent(HomeActivity.createIntent(this));
        actionBar.findAction(R.id.item_share).setIntent(createShareIntent());
        actionBar.findAction(R.id.item_export).setOnMenuItemClickListener(exampleListener);

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText("Tab 1").setTabListener(this));
        actionBar.addTab(actionBar.newTab().setText("Tab 2").setTabListener(this));
        
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private Intent createShareIntent() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Shared from the ActionBar widget.");
        return Intent.createChooser(intent, "Share");
    }

    private final OnMenuItemClickListener exampleListener = new OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Toast.makeText(OtherActivity.this,
                    "Example action", Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    @Override
    public void onTabReselected(Tab tab) {
        Toast.makeText(this,
                String.format("Tab '%s' got reselected", tab.getText()),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTabSelected(Tab tab) {
        Toast.makeText(this,
                String.format("Tab '%s' selected", tab.getText()),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTabUnselected(Tab tab) {
        Toast.makeText(this,
                String.format("Tab '%s' got unselected", tab.getText()),
                Toast.LENGTH_SHORT).show();
    }

}