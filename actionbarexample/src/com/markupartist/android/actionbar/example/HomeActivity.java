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
import android.widget.SpinnerAdapter;
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
        actionBar.setCustomView(R.layout.custom_view);
        
        SpinnerAdapter listAdapter = ArrayAdapter.createFromResource(this, R.array.locations, R.layout.actionbar_title);
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

        
        final Button startProgress = (Button) findViewById(R.id.start_progress);
        final Button stopProgress = (Button) findViewById(R.id.stop_progress);
        
        startProgress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setProgressBarVisibility(View.VISIBLE);
                startProgress.setEnabled(false);
                stopProgress.setEnabled(true);
            }
        });
        stopProgress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setProgressBarVisibility(View.GONE);
                stopProgress.setEnabled(false);
                startProgress.setEnabled(true);
            }
        });

        
        final Button addAction = (Button) findViewById(R.id.add_action);
        final Button removeAction = (Button) findViewById(R.id.remove_action);
        final Button removeShareAction = (Button) findViewById(R.id.remove_share_action);
        final Button removeActions = (Button) findViewById(R.id.remove_actions);
        
        addAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionBar.getActionCount() == 0) {
                    actionBar.addAction(shareAction);
                    removeShareAction.setEnabled(true);
                } else {
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

                removeAction.setEnabled(true);
                removeActions.setEnabled(true);
            }
        });
        removeAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int actionCount = actionBar.getActionCount();
                actionBar.removeActionAt(actionCount - 1);
                Toast.makeText(HomeActivity.this, "Removed action." , Toast.LENGTH_SHORT).show();
                
                if (actionBar.getActionCount() == 0) {
                    removeShareAction.setEnabled(false);
                    removeAction.setEnabled(false);
                    removeActions.setEnabled(false);
                }
            }
        });
        removeShareAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBar.removeAction(shareAction);
                removeShareAction.setEnabled(false);
                
                if (actionBar.getActionCount() == 0) {
                    removeAction.setEnabled(false);
                    removeActions.setEnabled(false);
                }
            }
        });
        removeActions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBar.removeAllActions();
                removeAction.setEnabled(false);
                removeActions.setEnabled(false);
                removeShareAction.setEnabled(false);
            }
        });
        
        
        final Button showSubtitle = (Button) findViewById(R.id.display_subtitle_show);
        final Button hideSubtitle = (Button) findViewById(R.id.display_subtitle_hide);
        
        showSubtitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setSubtitle("The quick brown fox jumps over the lazy dog.");
                showSubtitle.setEnabled(false);
                hideSubtitle.setEnabled(true);
            }
        });
        hideSubtitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setSubtitle(null);
                hideSubtitle.setEnabled(false);
                showSubtitle.setEnabled(true);
            }
        });
        
        
        final Button showTitle = (Button) findViewById(R.id.display_title_show);
        final Button hideTitle = (Button) findViewById(R.id.display_title_hide);
        
        showTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayShowTitleEnabled(true);
                showTitle.setEnabled(false);
                hideTitle.setEnabled(true);
                
                boolean hasSubtitle = actionBar.getSubtitle() != null;
                showSubtitle.setEnabled(!hasSubtitle);
                hideSubtitle.setEnabled(hasSubtitle);
            }
        });
        hideTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayShowTitleEnabled(false);
                hideTitle.setEnabled(false);
                showTitle.setEnabled(true);
                
                showSubtitle.setEnabled(false);
                hideSubtitle.setEnabled(false);
            }
        });
        
        
        final Button showCustom = (Button) findViewById(R.id.display_custom_show);
        final Button hideCustom = (Button) findViewById(R.id.display_custom_hide);
        
        showCustom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayShowCustomEnabled(true);
                showCustom.setEnabled(false);
                hideCustom.setEnabled(true);
                
                showTitle.setEnabled(false);
                hideTitle.setEnabled(false);
                showSubtitle.setEnabled(false);
                hideSubtitle.setEnabled(false);
            }
        });
        hideCustom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayShowCustomEnabled(false);
                hideCustom.setEnabled(false);
                showCustom.setEnabled(true);

                boolean isShowingTitle = (actionBar.getDisplayOptions() & ActionBar.DISPLAY_SHOW_TITLE) != 0;
                showTitle.setEnabled(!isShowingTitle);
                hideTitle.setEnabled(isShowingTitle);
                
                boolean hasSubtitle = actionBar.getSubtitle() != null;
                showSubtitle.setEnabled(!hasSubtitle);
                hideSubtitle.setEnabled(hasSubtitle);
            }
        });
        
        
        final Button standardNavigation = (Button) findViewById(R.id.navigation_standard);
        final Button listNavigation = (Button) findViewById(R.id.navigation_list);
        
        standardNavigation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                standardNavigation.setEnabled(false);
                listNavigation.setEnabled(true);
                
                boolean isShowingTitle = (actionBar.getDisplayOptions() & ActionBar.DISPLAY_SHOW_TITLE) != 0;
                showTitle.setEnabled(!isShowingTitle);
                hideTitle.setEnabled(isShowingTitle);
                
                boolean hasSubtitle = actionBar.getSubtitle() != null;
                showSubtitle.setEnabled(!hasSubtitle);
                hideSubtitle.setEnabled(hasSubtitle);
                
                boolean isShowingCustom = (actionBar.getDisplayOptions() & ActionBar.DISPLAY_SHOW_CUSTOM) != 0;
                showCustom.setEnabled(!isShowingCustom);
                hideCustom.setEnabled(isShowingCustom);
            }
        });
        listNavigation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                listNavigation.setEnabled(false);
                standardNavigation.setEnabled(true);
                
                showTitle.setEnabled(false);
                hideTitle.setEnabled(false);
                showSubtitle.setEnabled(false);
                hideSubtitle.setEnabled(false);
                showCustom.setEnabled(false);
                hideCustom.setEnabled(false);
            }
        });
        
        
        final Button showHomeAsUp = (Button) findViewById(R.id.display_home_as_up_show);
        final Button hideHomeAsUp = (Button) findViewById(R.id.display_home_as_up_hide);
        
        showHomeAsUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                showHomeAsUp.setEnabled(false);
                hideHomeAsUp.setEnabled(true);
            }
        });
        hideHomeAsUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayHomeAsUpEnabled(false);
                hideHomeAsUp.setEnabled(false);
                showHomeAsUp.setEnabled(true);
            }
        });
        
        
        final Button useLogo = (Button) findViewById(R.id.display_logo_show);
        final Button doNoUseLogo = (Button) findViewById(R.id.display_logo_hide);
        
        useLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayUseLogoEnabled(true);
                useLogo.setEnabled(false);
                doNoUseLogo.setEnabled(true);
            }
        });
        doNoUseLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayUseLogoEnabled(false);
                doNoUseLogo.setEnabled(false);
                useLogo.setEnabled(true);
            }
        });
        
        
        final Button showHome = (Button) findViewById(R.id.display_home_show);
        final Button hideHome = (Button) findViewById(R.id.display_home_hide);
        
        showHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayShowHomeEnabled(true);
                showHome.setEnabled(false);
                hideHome.setEnabled(true);
                
                boolean isHomeAsUp = (actionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0;
                showHomeAsUp.setEnabled(!isHomeAsUp);
                hideHomeAsUp.setEnabled(isHomeAsUp);
                
                boolean isUsingLogo = (actionBar.getDisplayOptions() & ActionBar.DISPLAY_USE_LOGO) != 0;
                useLogo.setEnabled(!isUsingLogo);
                doNoUseLogo.setEnabled(isUsingLogo);
            }
        });
        hideHome.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBar.setDisplayShowHomeEnabled(false);
                hideHome.setEnabled(false);
                showHome.setEnabled(true);
                
                showHomeAsUp.setEnabled(false);
                hideHomeAsUp.setEnabled(false);
                useLogo.setEnabled(false);
                doNoUseLogo.setEnabled(false);
            }
        });
        
        
        //Defaults
        stopProgress.performClick();
        hideCustom.performClick();
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