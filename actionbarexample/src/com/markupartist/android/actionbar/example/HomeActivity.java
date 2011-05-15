package com.markupartist.android.actionbar.example;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.markupartist.android.widget.ActionBar.IntentAction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Toast;

public class HomeActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
        actionBar.setHomeAction(new IntentAction(this, createIntent(this), R.drawable.ic_title_home_default));
        actionBar.setTitle("Android-ActionBar Example");
        actionBar.setHomeLogo(R.drawable.logo);
        
        ListAdapter listAdapter = ArrayAdapter.createFromResource(this, R.array.locations, android.R.layout.select_dialog_item);
        actionBar.setListNavigationCallbacks(listAdapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                Toast.makeText(HomeActivity.this, "List navigation location changed.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        final Action shareAction = new IntentAction(this, createShareIntent(), R.drawable.ic_title_share_default);
        actionBar.addAction(shareAction);
        final Action otherAction = new IntentAction(this, new Intent(this, OtherActivity.class), R.drawable.ic_title_export_default);
        actionBar.addAction(otherAction);

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

        Button addAction = (Button) findViewById(R.id.add_action);
        addAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBar.addAction(new Action() {
                    @Override
                    public void performAction(View view) {
                        Toast.makeText(HomeActivity.this, "Added action.", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public Drawable getIcon() {
                        return getResources().getDrawable(R.drawable.ic_title_share_default);
                    }
                });
            }
        });

        Button removeAction = (Button) findViewById(R.id.remove_action);
        removeAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int actionCount = actionBar.getActionCount();
                actionBar.removeActionAt(actionCount - 1);
                Toast.makeText(HomeActivity.this, "Removed action." , Toast.LENGTH_SHORT).show();
            }
        });

        Button removeShareAction = (Button) findViewById(R.id.remove_share_action);
        removeShareAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBar.removeAction(shareAction);
            }
        });
        
        final Button showTitle = (Button) findViewById(R.id.display_title_show);
        showTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayShowTitleEnabled(true);
            }
        });
        
        final Button hideTitle = (Button) findViewById(R.id.display_title_hide);
        hideTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayShowTitleEnabled(false);
            }
        });
        
        Button standardNavigation = (Button) findViewById(R.id.navigation_standard);
        standardNavigation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                showTitle.setEnabled(true);
                hideTitle.setEnabled(true);
            }
        });
        
        Button listNavigation = (Button) findViewById(R.id.navigation_list);
        listNavigation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                showTitle.setEnabled(false);
                hideTitle.setEnabled(false);
            }
        });
        
        final Button showHomeAsUp = (Button) findViewById(R.id.display_home_as_up_show);
        showHomeAsUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        });
        
        final Button hideHomeAsUp = (Button) findViewById(R.id.display_home_as_up_hide);
        hideHomeAsUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayHomeAsUpEnabled(false);
            }
        });
        
        final Button useLogo = (Button) findViewById(R.id.display_logo_show);
        useLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayUseLogoEnabled(true);
            }
        });
        
        final Button doNoUseLogo = (Button) findViewById(R.id.display_logo_hide);
        doNoUseLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayUseLogoEnabled(false);
            }
        });
        
        Button showHome = (Button) findViewById(R.id.display_home_show);
        showHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayShowHomeEnabled(true);
                showHomeAsUp.setEnabled(true);
                hideHomeAsUp.setEnabled(true);
                useLogo.setEnabled(true);
                doNoUseLogo.setEnabled(true);
            }
        });
        
        Button hideHome = (Button) findViewById(R.id.display_home_hide);
        hideHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayShowHomeEnabled(false);
                showHomeAsUp.setEnabled(false);
                hideHomeAsUp.setEnabled(false);
                useLogo.setEnabled(false);
                doNoUseLogo.setEnabled(false);
            }
        });
        
        //Defaults
        showTitle.performClick();
        hideHome.performClick();
        standardNavigation.performClick();
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