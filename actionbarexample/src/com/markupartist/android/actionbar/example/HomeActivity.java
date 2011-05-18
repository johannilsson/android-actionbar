package com.markupartist.android.actionbar.example;

import java.util.Random;

import com.markupartist.android.widget.ActionBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SpinnerAdapter;
import android.widget.Toast;


public class HomeActivity extends Activity {
    private static final Random RANDOM = new Random();
    
    private ActionBar mActionBar;
    
    private Button mActionBarShow;
    private Button mActionBarHide;
    
    private Button mNavigationStandard;
    private Button mNavigationList;
    private Button mNavigationTabs;
    
    private Button mTabAdd;
    private Button mTabSelect;
    private Button mTabRemove;
    private Button mTabRemoveAll;
    
    private Button mHomeShow;
    private Button mHomeHide;
    
    private Button mHomeLogoUse;
    private Button mHomeLogoDoNotUse;
    
    private Button mHomeAsUpShow;
    private Button mHomeAsUpHide;
    
    private Button mTitleShow;
    private Button mTitleHide;
    
    private Button mSubtitleShow;
    private Button mSubtitleHide;
    
    private Button mActionAdd;
    private Button mActionRemoveOne;
    private Button mActionRemoveShare;
    private Button mActionRemoveAll;
    
    private Button mCustomViewShow;
    private Button mCustomViewHide;
    
    private Button mProgressStart;
    private Button mProgressStop;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mActionBar = (ActionBar) findViewById(R.id.actionbar);

        // The title will automatically be pulled from the AndroidManifest.xml.
        // You can also assign the title programmatically by passing a
        // CharSequence or resource id.
        //actionBar.setTitle(R.string.some_title);
        
        mActionBar.setHomeLogo(R.drawable.logo);
        mActionBar.setCustomView(R.layout.custom_view);
        mActionBar.setDisplayShowCustomEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.add(0, R.id.actionbar_item_home, 0, "")
            .setIcon(R.drawable.ic_title_home_default)
            .setIntent(createIntent(this));
        
        SpinnerAdapter listAdapter = ArrayAdapter.createFromResource(this, R.array.locations, R.layout.actionbar_title);
        mActionBar.setListNavigationCallbacks(listAdapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                Toast.makeText(HomeActivity.this, "List navigation location changed.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        addShareItem();
        addOtherItem();
        
        mProgressStart = (Button) findViewById(R.id.start_progress);
        mProgressStop = (Button) findViewById(R.id.stop_progress);
        
        mProgressStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setProgressBarVisibility(View.VISIBLE);
                updateButtonStates();
            }
        });
        mProgressStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setProgressBarVisibility(View.GONE);
                updateButtonStates();
            }
        });

        
        mActionAdd = (Button) findViewById(R.id.add_action);
        mActionRemoveOne = (Button) findViewById(R.id.remove_action);
        mActionRemoveShare = (Button) findViewById(R.id.remove_share_action);
        mActionRemoveAll = (Button) findViewById(R.id.remove_actions);
        
        mActionAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActionBar.size() == 0) {
                    addShareItem();
                } else {
                    mActionBar.add(0, R.drawable.ic_title_export_default)
                        .setOnMenuItemClickListener(new OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Toast.makeText(HomeActivity.this, "Action clicked!", Toast.LENGTH_SHORT).show();
                                return true;
                            }
                        });
                }
                updateButtonStates();
            }
        });
        mActionRemoveOne.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int actionCount = mActionBar.size();
                mActionBar.removeItemAt(actionCount - 1);
                Toast.makeText(HomeActivity.this, "Removed action." , Toast.LENGTH_SHORT).show();
                updateButtonStates();
            }
        });
        mActionRemoveShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mActionBar.removeItem(R.id.item_share);
                updateButtonStates();
            }
        });
        mActionRemoveAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mActionBar.clear();
                updateButtonStates();
            }
        });
        
        
        mSubtitleShow = (Button) findViewById(R.id.display_subtitle_show);
        mSubtitleHide = (Button) findViewById(R.id.display_subtitle_hide);
        
        mSubtitleShow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setSubtitle("The quick brown fox jumps over the lazy dog.");
                updateButtonStates();
            }
        });
        mSubtitleHide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setSubtitle(null);
                updateButtonStates();
            }
        });
        
        
        mTitleShow = (Button) findViewById(R.id.display_title_show);
        mTitleHide = (Button) findViewById(R.id.display_title_hide);
        
        mTitleShow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setDisplayShowTitleEnabled(true);
                updateButtonStates();
            }
        });
        mTitleHide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setDisplayShowTitleEnabled(false);
                updateButtonStates();
            }
        });
        
        
        mCustomViewShow = (Button) findViewById(R.id.display_custom_show);
        mCustomViewHide = (Button) findViewById(R.id.display_custom_hide);
        
        mCustomViewShow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setDisplayShowCustomEnabled(true);
                updateButtonStates();
            }
        });
        mCustomViewHide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setDisplayShowCustomEnabled(false);
                updateButtonStates();
            }
        });
        
        
        mNavigationStandard = (Button) findViewById(R.id.navigation_standard);
        mNavigationList = (Button) findViewById(R.id.navigation_list);
        mNavigationTabs = (Button) findViewById(R.id.navigation_tabs);
        
        mNavigationStandard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                updateButtonStates();
            }
        });
        mNavigationList.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
                updateButtonStates();
            }
        });
        mNavigationTabs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                updateButtonStates();
            }
        });
        
        
        mHomeAsUpShow = (Button) findViewById(R.id.display_home_as_up_show);
        mHomeAsUpHide = (Button) findViewById(R.id.display_home_as_up_hide);
        
        mHomeAsUpShow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setDisplayHomeAsUpEnabled(true);
                updateButtonStates();
            }
        });
        mHomeAsUpHide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setDisplayHomeAsUpEnabled(false);
                updateButtonStates();
            }
        });
        
        
        mHomeLogoUse = (Button) findViewById(R.id.display_logo_show);
        mHomeLogoDoNotUse = (Button) findViewById(R.id.display_logo_hide);
        
        mHomeLogoUse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setDisplayUseLogoEnabled(true);
                updateButtonStates();
            }
        });
        mHomeLogoDoNotUse.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setDisplayUseLogoEnabled(false);
                updateButtonStates();
            }
        });
        
        
        mHomeShow = (Button) findViewById(R.id.display_home_show);
        mHomeHide = (Button) findViewById(R.id.display_home_hide);
        
        mHomeShow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setDisplayShowHomeEnabled(true);
                updateButtonStates();
            }
        });
        mHomeHide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.setDisplayShowHomeEnabled(false);
                updateButtonStates();
            }
        });
        
        mActionBarShow = (Button) findViewById(R.id.display_actionbar_show);
        mActionBarHide= (Button) findViewById(R.id.display_actionbar_hide);
        
        mActionBarShow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.show();
                updateButtonStates();
            }
        });
        mActionBarHide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.hide();
                updateButtonStates();
            }
        });
        
        mTabAdd = (Button) findViewById(R.id.display_tab_add);
        mTabSelect = (Button) findViewById(R.id.display_tab_select);
        mTabRemove = (Button) findViewById(R.id.display_tab_remove);
        mTabRemoveAll = (Button) findViewById(R.id.display_tab_remove_all);
        
        mTabAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionBar.Tab newTab = mActionBar.newTab();

                if (RANDOM.nextBoolean()) {
                    newTab.setCustomView(R.layout.tab_custom_view);
                } else {
                    boolean icon = RANDOM.nextBoolean();
                    if (icon) {
                        newTab.setIcon(R.drawable.ic_title_share_default);
                    }
                    if (!icon || RANDOM.nextBoolean()) {
                        newTab.setText("Tab Text!");
                    }
                }
                
                mActionBar.addTab(newTab);
                
                updateButtonStates();
            }
        });
        mTabSelect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionBar.getTabCount() > 0) {
                    final int newIndex = RANDOM.nextInt(mActionBar.getTabCount());
                    mActionBar.setSelectedNavigationItem(newIndex);
                }
            }
        });
        mTabRemove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.removeTabAt(mActionBar.getTabCount() - 1);
                updateButtonStates();
            }
        });
        mTabRemoveAll.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionBar.removeAllTabs();
                updateButtonStates();
            }
        });
        
        
        updateButtonStates();
    }
    
    private void addShareItem() {
        mActionBar.add(0, R.id.item_share, 0, "")
            .setIcon(R.drawable.ic_title_share_default)
            .setIntent(createShareIntent());
    }
    
    private void addOtherItem() {
        mActionBar.add()
        .setIcon(R.drawable.ic_title_export_default)
        .setIntent(new Intent(this, OtherActivity.class));
    }
    
    private void updateButtonStates() {
        boolean isVisible = mActionBar.isShowing();
        boolean isProgressStarted = mActionBar.getProgressBarVisibility() == View.VISIBLE;
        boolean isShowingHome = (mActionBar.getDisplayOptions() & ActionBar.DISPLAY_SHOW_HOME) != 0;
        boolean isUsingHomeLogo = (mActionBar.getDisplayOptions() & ActionBar.DISPLAY_USE_LOGO) != 0;
        boolean isHomeShownAsUp = (mActionBar.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0;
        boolean isShowingCustom = (mActionBar.getDisplayOptions() & ActionBar.DISPLAY_SHOW_CUSTOM) != 0;
        boolean isShowingTitle = (mActionBar.getDisplayOptions() & ActionBar.DISPLAY_SHOW_TITLE) != 0;
        boolean isNavigationStandard = mActionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_STANDARD;
        boolean isNavigationList = mActionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST;
        boolean isNavigationTabs = mActionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS;
        boolean hasSubtitle = mActionBar.getSubtitle() != null;
        boolean hasTabs = mActionBar.getTabCount() > 0;
        boolean hasMaxTabs = mActionBar.getTabCount() > 3;
        boolean hasActions = mActionBar.size() > 0;
        boolean hasMaxActions = mActionBar.size() > 3;
        boolean hasActionShare = mActionBar.findItem(R.id.item_share) != null;
        
        mActionBarShow.setEnabled(!isVisible);
        mActionBarHide.setEnabled(isVisible);
        
        mHomeShow.setEnabled(isVisible && !isShowingHome);
        mHomeHide.setEnabled(isVisible && isShowingHome);
        
        mHomeLogoUse.setEnabled(isVisible && isShowingHome && !isUsingHomeLogo);
        mHomeLogoDoNotUse.setEnabled(isVisible && isShowingHome && isUsingHomeLogo);
        
        mHomeAsUpShow.setEnabled(isVisible && isShowingHome && !isHomeShownAsUp);
        mHomeAsUpHide.setEnabled(isVisible && isShowingHome && isHomeShownAsUp);
        
        mNavigationList.setEnabled(isVisible && !isNavigationList);
        mNavigationStandard.setEnabled(isVisible && !isNavigationStandard);
        mNavigationTabs.setEnabled(isVisible && !isNavigationTabs);
        
        mCustomViewShow.setEnabled(isVisible && isNavigationStandard && !isShowingCustom);
        mCustomViewHide.setEnabled(isVisible && isNavigationStandard && isShowingCustom);
        
        mTitleShow.setEnabled(isVisible && isNavigationStandard && !isShowingCustom && !isShowingTitle);
        mTitleHide.setEnabled(isVisible && isNavigationStandard && !isShowingCustom && isShowingTitle);
        
        mSubtitleShow.setEnabled(isVisible && isNavigationStandard && !isShowingCustom && isShowingTitle && !hasSubtitle);
        mSubtitleHide.setEnabled(isVisible && isNavigationStandard && !isShowingCustom && isShowingTitle && hasSubtitle);
        
        mActionAdd.setEnabled(isVisible && !hasMaxActions);
        mActionRemoveOne.setEnabled(isVisible && hasActions);
        mActionRemoveAll.setEnabled(isVisible && hasActions);
        mActionRemoveShare.setEnabled(isVisible && hasActionShare);
        
        mTabAdd.setEnabled(isVisible && isNavigationTabs && !hasMaxTabs);
        mTabRemove.setEnabled(isVisible && isNavigationTabs && hasTabs);
        mTabRemoveAll.setEnabled(isVisible && isNavigationTabs && hasTabs);
        mTabSelect.setEnabled(isVisible && isNavigationTabs && hasTabs);
        
        mProgressStart.setEnabled(isVisible && !isProgressStarted);
        mProgressStop.setEnabled(isVisible && isProgressStarted);
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