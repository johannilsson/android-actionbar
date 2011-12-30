/*
 * Copyright (C) 2010 Johan Nilsson <http://markupartist.com>
 * Copyright (C) 2011 Jake Wharton <jakewharton@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.markupartist.android.widget;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ActionProvider;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.markupartist.android.widget.actionbar.R;

/**
 * Implementation of the action bar design pattern for use on Android 1.6+.
 * 
 * @author Johan Nilsson <http://markupartist.com>
 */
public class ActionBar extends RelativeLayout {
    /**
     * Display the 'home' element such that it appears as an 'up' affordance.
     * e.g. show an arrow to the left indicating the action that will be
     * taken. Set this flag if selecting the 'home' button in the action bar
     * to return up by a single level in your UI rather than back to the top
     * level or front page.
     * 
     * @see #setDisplayHomeAsUpEnabled(boolean)
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     */
    public static final int DISPLAY_HOME_AS_UP = 0x04;// = android.app.ActionBar.DISPLAY_HOME_AS_UP;
    
    /**
     * <p>Show the custom view if one has been set.</p>
     * 
     * <p>Due to the nature of screen real estate on a phone, this will only be
     * displayed when in {@link #NAVIGATION_MODE_STANDARD} and will hide the
     * flag {@link #DISPLAY_SHOW_TITLE}, if enabled.</p>
     * 
     * @see #setDisplayShowCustomEnabled(boolean)
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     */
    public static final int DISPLAY_SHOW_CUSTOM = 0x10;// = android.app.ActionBar.DISPLAY_SHOW_CUSTOM;
    
    /**
     * Show 'home' elements in this action bar, leaving more space for other
     * navigation elements. This includes logo and icon.
     * 
     * @see #setDisplayShowHomeEnabled(boolean)
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     */
    public static final int DISPLAY_SHOW_HOME = 0x02;// = android.app.ActionBar.DISPLAY_SHOW_HOME;
    
    /**
     * <p>Show the activity title.</p>
     * 
     * <p>Due to the nature of screen real estate on a phone, this will only be
     * displayed when in {@link #NAVIGATION_MODE_STANDARD} and will require
     * {@link #DISPLAY_SHOW_CUSTOM} to be disabled.</p>
     * 
     * @see #setDisplayShowTitleEnabled(boolean)
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     */
    public static final int DISPLAY_SHOW_TITLE = 0x08;// = android.app.ActionBar.DISPLAY_SHOW_TITLE;
    
    /**
     * Use logo instead of icon if available. This flag will cause appropriate
     * navigation modes to use a wider logo in place of the standard icon.
     * 
     * @see #setDisplayUseLogoEnabled(boolean)
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     */
    public static final int DISPLAY_USE_LOGO = 0x01;// = android.app.ActionBar.DISPLAY_USE_LOGO;

    /**
     * Standard navigation mode. Consists of either a logo or icon and title
     * text with an optional subtitle. Clicking any of these elements will
     * dispatch onOptionsItemSelected to the host Activity with a MenuItem with
     * item ID android.R.id.home.
     */
    public static final int NAVIGATION_MODE_STANDARD = 0x0;// = android.app.ActionBar.NAVIGATION_MODE_STANDARD;
    
    /**
     * List navigation mode. Instead of static title text this mode presents a
     * list menu for navigation within the activity. e.g. this might be
     * presented to the user as a dropdown list.
     */
    public static final int NAVIGATION_MODE_LIST = 0x1;// = android.app.ActionBar.NAVIGATION_MODE_LIST;
    
    /**
     * Tab navigation mode. Instead of static title text this mode presents a
     * series of tabs for navigation within the activity.
     */
    public static final int NAVIGATION_MODE_TABS = 0x2;// = android.app.ActionBar.NAVIGATION_MODE_TABS;

    
    
    /** Layout inflation service. */
    private final LayoutInflater mInflater;
    
    /** Parent view of the action bar. */
    private final LinearLayout mBarView;
    
    /** Home item view. */
    private final FrameLayout mHomeView;
    
    /** Home logo. */
    private final ImageView mHomeLogo;
    
    /** Home button up indicator. */
    private final View mHomeUpIndicator;
    
    /** Title view. */
    private final TextView mTitleView;
    
    /** Subtitle view. */
    private final TextView mSubtitleView;
    
    /** List view. */
    private final FrameLayout mListView;
    
    /** List dropdown indicator. */
    private final View mListIndicator;
    
    /** Custom view parent. */
    private final FrameLayout mCustomView;
    
    /** Container for all action items. */
    private final LinearLayout mActionsView;

    /** Container for all tab items. */
    private final LinearLayout mTabsView;

    /** Indeterminate progress bar. */
    private final ProgressBar mProgress;
    
    /**
     * Display state flags.
     * 
     * @see #getDisplayOptions()
     * @see #getDisplayOptionValue(int)
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     * @see #setDisplayOption(int, boolean)
     * @see #reloadDisplay()
     */
    private int mFlags;
    
    /**
     * Current navigation mode
     * 
     * @see #getNavigationMode()
     * @see #setNavigationMode(int)
     */
    private int mNavigationMode;
    
    /**
     * Current selected index of list navigation.
     */
    private int mSelectedIndex;
    
    /**
     * Adapter for the list navigation contents.
     */
    private SpinnerAdapter mListAdapter;
    
    /**
     * Callback for the list navigation event.
     */
    private OnNavigationListener mListCallback;

    /**
     * Listener for list title click. Will display a list dialog of all the
     * options provided and execute the specified {@link OnNavigationListener}.
     */
    private final View.OnClickListener mListClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListAdapter != null) {
                new ListNavigationDropdown.Builder(getContext())
                        .setAdapter(mListAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                if (position != mSelectedIndex) {
                                    mSelectedIndex = position;
                                    reloadDisplay();
                                }

                                //Execute call back, if exists
                                if (mListCallback != null) {
                                    mListCallback.onNavigationItemSelected(position, mListAdapter.getItemId(position));
                                }
                            }
                        })
                        .setParent(mListView)
                        .show();
            }
        }
    };
    
    /**
     * Listener for action item click.
     */
    private final View.OnClickListener mActionClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((Action) view.getTag()).select();
        }
    };
    
    /**
     * Listener for tab clicked.
     */
    private final View.OnClickListener mTabClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ((Tab) view.getTag()).select();
        }
    };

    /**
     * Instantiate a new action bar instance.
     * 
     * @param context Context to which the action bar will be tied.
     * @param attrs Configuration attributes.
     */
    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        String title = null;

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ActionBar);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
            case R.styleable.ActionBar_title:
                title = a.getString(attr);
                break;
            }
        }
        a.recycle();

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBarView = (LinearLayout) mInflater.inflate(R.layout.actionbar, null);
        addView(mBarView);

        mHomeView = (FrameLayout) mBarView.findViewById(R.id.actionbar_home_view);
        mHomeLogo = (ImageView) mBarView.findViewById(R.id.actionbar_home_logo);
        mHomeUpIndicator = mBarView.findViewById(R.id.actionbar_home_is_back);

        mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);
        mSubtitleView = (TextView) mBarView.findViewById(R.id.actionbar_subtitle);
        
        mListView = (FrameLayout) mBarView.findViewById(R.id.actionbar_list);
        mListIndicator = mBarView.findViewById(R.id.actionbar_list_indicator);
        
        mCustomView = (FrameLayout) mBarView.findViewById(R.id.actionbar_custom);
        
        mActionsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);

        mTabsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_tabs);

        mProgress = (ProgressBar) mBarView.findViewById(R.id.actionbar_progress);

        if (title != null) {
            setTitle(title);
        } else if (context instanceof Activity) {
            //Try to load title from the Activity's manifest entry
            try {
                ComponentName componentName = ((Activity) context).getComponentName();
                PackageManager packageManager = ((Activity) context).getPackageManager();
                setTitle(packageManager.getActivityInfo(componentName, PackageManager.GET_ACTIVITIES).loadLabel(packageManager));
            } catch (NameNotFoundException e) {
                //Can't load and/or find title. Eat exception.
            }
        }

        //Must be >= gingerbread to look for a logo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Drawable logo = LogoLoader.loadLogo(context);
            if (logo != null) {
                setHomeLogo(logo);
            }
        }
        
        //Show the title by default
        setDisplayOption(DISPLAY_SHOW_TITLE, true);
        //Use standard navigation by default (this will call reloadDisplay)
        setNavigationMode(NAVIGATION_MODE_STANDARD);
    }
    
    
    
    // ------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    // ------------------------------------------------------------------------
    
    /**
     * Helper to set a flag to a new value.
     * 
     * @param flag Flag to update.
     * @param enabled New value.
     */
    private void setDisplayOption(int flag, boolean enabled) {
        //Remove current value and OR with new value
        mFlags = (mFlags & ~flag) | (enabled ? flag : 0);
    }
    
    /**
     * Helper to get a boolean value for a specific flag.
     * 
     * @param flag Target flag.
     * @return Value.
     */
    private boolean getDisplayOptionValue(int flag) {
        return (mFlags & flag) != 0;
    }
    
    /**
     * Reload the current action bar display state.
     */
    private void reloadDisplay() {
        final boolean isList = mNavigationMode == NAVIGATION_MODE_LIST;
        final boolean isTab = mNavigationMode == NAVIGATION_MODE_TABS;
        final boolean hasList = (mListAdapter != null) && (mListAdapter.getCount() > 0);
        final boolean showingTitle = getDisplayOptionValue(DISPLAY_SHOW_TITLE);
        final boolean showingCustom = getDisplayOptionValue(DISPLAY_SHOW_CUSTOM);
        final boolean usingLogo = getDisplayOptionValue(DISPLAY_USE_LOGO);
        final boolean hasSubtitle = (mSubtitleView.getText() != null) && !mSubtitleView.getText().equals(""); 
        
        if (getDisplayOptionValue(DISPLAY_SHOW_HOME)) {
            mHomeUpIndicator.setVisibility(getDisplayOptionValue(DISPLAY_HOME_AS_UP) ? View.VISIBLE : View.GONE);
            mHomeLogo.setVisibility(usingLogo ? View.VISIBLE : View.GONE);
            mHomeView.setVisibility(usingLogo ? View.GONE : View.VISIBLE);
        } else {
            mHomeUpIndicator.setVisibility(View.GONE);
            mHomeLogo.setVisibility(View.GONE);
            mHomeView.setVisibility(View.GONE);
        }
        
        //If we are a list, set the list view to the currently selected item
        if (isList) {
            View oldView = mListView.getChildAt(0);
            mListView.removeAllViews();
            if (hasList) {
                mListView.addView(mListAdapter.getView(mSelectedIndex, oldView, mListView));
            }

            View firstListItem = mListView.getChildAt(mSelectedIndex);
            if (firstListItem != null) {
                firstListItem.setOnClickListener(mListClicked);
            }
        }
        
        //Only show list if we are in list navigation and there are list items
        mListView.setVisibility(isList && hasList ? View.VISIBLE : View.GONE);
        mListIndicator.setVisibility(isList && hasList ? View.VISIBLE : View.GONE);

        // Show tabs if in tabs navigation mode.
        mTabsView.setVisibility(isTab ? View.VISIBLE : View.GONE);

        //Show title view if we are not in list navigation, not showing custom
        //view, and the show title flag is true
        mTitleView.setVisibility(!isList && !showingCustom && showingTitle ? View.VISIBLE : View.GONE);
        //Show subtitle view if we are not in list navigation, not showing
        //custom view, show title flag is true, and a subtitle is set
        mSubtitleView.setVisibility(!isList && !showingCustom && showingTitle && hasSubtitle ? View.VISIBLE : View.GONE);
        //Show custom view if we are not in list navigation, not in tab
        //navigation, and the showing custom flag is set
        mCustomView.setVisibility(!isList && !isList && !isTab && showingCustom ? View.VISIBLE : View.GONE);
    }

    // ------------------------------------------------------------------------
    // NATIVE ACTION BAR METHODS
    // ------------------------------------------------------------------------

    /**
     * Add a tab for use in tabbed navigation mode. The tab will be added at
     * the end of the list. If this is the first tab to be added it will become
     * the selected tab.
     * 
     * @param tab A new Tab.
     */
    public void addTab(Tab tab) {
        final int position = getTabCount();
        addTab(tab, position, position == 0 ? true : false);
    }
    
    /**
     * Add a tab for use in tabbed navigation mode. The tab will be added at the
     * end of the list.
     * 
     * @param tab Tab to add
     * @param setSelected True if the added tab should become the selected tab.
     */
    public void addTab(Tab tab, boolean setSelected) {
        addTab(tab, getTabCount(), setSelected);
    }

    /**
     * Add a tab for use in tabbed navigation mode. The tab will be inserted at
     * position. If this is the first tab to be added it will become the
     * selected tab.
     * 
     * @param tab Tab to add.
     * @param position The new position of the tab.
     */
    public void addTab(Tab tab, int position) {
        addTab(tab, position, getTabCount() == 0 ? true : false);
    }

    /**
     * Add a tab for use in tabbed navigation mode. The tab will be insterted
     * at position.
     * 
     * @param tab The tab to add
     * @param position The new position of the tab.
     * @param setSelected True if the added tab should become the selected tab.
     */
    public void addTab(Tab tab, int position, boolean setSelected) {
        mTabsView.addView(tab.mView, position);
        if (setSelected) {
            tab.select();
        }
    }
    
    /**
     * @return The current custom view.
     */
    public View getCustomView() {
        return this.mCustomView.getChildAt(0);
    }
    
    /**
     * Get the current display options.
     * 
     * @return The current set of display options.
     */
    public int getDisplayOptions() {
        return mFlags;
    }
    
    //Implemented by superclass:
    //public int getHeight() {}
    
    /**
     * Get the number of navigation items present in the current navigation
     * mode.
     */
    public int getNavigationItemCount() {
        if (mNavigationMode == NAVIGATION_MODE_LIST) {
            return mListAdapter.getCount();
        }
        if (this.mNavigationMode == NAVIGATION_MODE_TABS) {
            return getTabCount();
        }
        return 0;
    }
    
    /**
     * Returns the current navigation mode. The result will be one of:
     * <ul>
     *  <li>{@link #NAVIGATION_MODE_STANDARD}</li>
     *  <li>{@link #NAVIGATION_MODE_LIST}</li>
     * </ul>
     * 
     * @return The current navigation mode.
     * 
     * @see #setNavigationMode(int)
     */
    public int getNavigationMode() {
        return mNavigationMode;
    }
    
    /**
     * Get the position of the selected navigation item in list or tabbed
     * navigation modes.
     * 
     * @return Position of the selected item.
     */
    public int getSelectedNavigationIndex() {
        if (mNavigationMode == NAVIGATION_MODE_LIST) {
            return mSelectedIndex;
        }
        if (mNavigationMode == NAVIGATION_MODE_TABS) {
            final int count = getTabCount();
            for (int i = 0; i < count; i++) {
                if (getTabAt(i).mIsSelected) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * Returns the currently selected tab if in tabbed navigation mode and there
     * is at least one tab present.
     * 
     * @return The currently selected tab or {@code null}.
     */
    public Tab getSelectedTab() {
        final int count = getTabCount();
        for (int i = 0; i < count; i++) {
            Tab tab = getTabAt(i);
            if (tab.mIsSelected) {
                return tab;
            }
        }
        return null;
    }
    
    /**
     * Returns the current ActionBar subtitle in standard mode. Returns
     * {@code null} if {@link #getNavigationMode()} would not return
     * {@link #NAVIGATION_MODE_STANDARD}.
     * 
     * @return The current ActionBar subtitle or {@code null}.
     * 
     * @see #setSubtitle(CharSequence)
     * @see #setSubtitle(int)
     * @see #setDisplayShowTitleEnabled(boolean)
     */
    public CharSequence getSubtitle() {
        if ((mNavigationMode == NAVIGATION_MODE_STANDARD) && !mSubtitleView.getText().equals("")) {
            return mSubtitleView.getText();
        } else {
            return null;
        }
    }
    
    /**
     * Returns the tab at the specified index.
     * 
     * @param index Index value.
     * @return Tab at index or {@code null} if it does not exist.
     */
    public Tab getTabAt(int index) {
        View tab = mTabsView.getChildAt(index);
        return (tab != null) ? (Tab)tab.getTag() : null;
    }
    
    /**
     * Returns the number of tabs currently registered with the action bar.
     * 
     * @return Tab count.
     */
    public int getTabCount() {
        return mTabsView.getChildCount();
    }
    
    /**
     * Returns the current ActionBar title in standard mode. Returns
     * {@code null} if {@link #getNavigationMode()} would not return
     * {@link #NAVIGATION_MODE_STANDARD}.
     * 
     * @return The current ActionBar title or {@code null}.
     * 
     * @see #setTitle(CharSequence)
     * @see #setTitle(int)
     * @see #setDisplayShowTitleEnabled(boolean)
     */
    public CharSequence getTitle() {
        if ((mNavigationMode == NAVIGATION_MODE_STANDARD) && !mTitleView.getText().equals("")) {
            return mTitleView.getText();
        } else {
            return null;
        }
    }
    
    /**
     * Hide the action bar if it is currently showing.
     * 
     * @see #show()
     * @see #isShowing()
     */
    public void hide() {
        setVisibility(View.GONE);
    }
    
    /**
     * Get current visibility of the action bar.
     * 
     * @return {@code true} if the ActionBar is showing, {@code false}
     * otherwise.
     * 
     * @see #hide()
     * @see #show()
     */
    public boolean isShowing() {
        return getVisibility() == View.VISIBLE;
    }

    /**
     * Create and return a new {@link ActionBar.Tab}. This tab will not be
     * included in the action bar until it is added.
     * 
     * @return A new Tab.
     * 
     * @see #addTab(Tab)
     */
    public Tab newTab() {
        return new Tab();
    }
    
    /**
     * Remove all tabs from the action bar and deselect the current tab.
     */
    public void removeAllTabs() {
        Tab selected = getSelectedTab();
        if (selected != null) {
            selected.unselect();
        }
        mTabsView.removeAllViews();
    }
    
    /**
     * Remove a tab from the action bar. If the removed tab was selected it will
     * be deselected and another tab will be selected if present.
     * 
     * @param tab The tab to remove.
     */
    public void removeTab(Tab tab) {
        final int count = getTabCount();
        for (int i = 0; i < count; i++) {
            if (getTabAt(i).equals(tab)) {
                removeTabAt(i);
            }
        }
    }
    
    /**
     * Remove a tab from the action bar. If the removed tab was selected it will
     * be deselected and another tab will be selected if present.
     * 
     * @param index Position of the tab to remove.
     */
    public void removeTabAt(int index) {
        Tab tab = getTabAt(index);
        if (tab != null) {
            tab.unselect();
            mTabsView.removeViewAt(index);
        
            if (index > 0) {
                //Select previous tab
                ((Tab)mTabsView.getChildAt(index - 1).getTag()).select();
            } else if (mTabsView.getChildCount() > 0) {
                //Select first tab
                ((Tab)mTabsView.getChildAt(0).getTag()).select();
            }
        }
    }
    
    /**
     * <p>Select the specified tab. If it is not a child of this action bar it
     * will be added.</p>
     * 
     * <p>Note: If you want to select by index, use
     * {@link #setSelectedNavigationItem(int)}.</p>
     * 
     * @param tab Tab to select.
     */
    public void selectTab(Tab tab) {
        final int count = getTabCount();
        for (int i = 0; i < count; i++) {
            Tab existingTab = getTabAt(i);
            if (existingTab.equals(tab)) {
                existingTab.select();
                return;
            }
        }
        addTab(tab, true);
    }
    
    //Implemented by superclass:
    //public void setBackgroundDrawable(Drawable d) {}
    
    /**
     * <p>Set the action bar into custom navigation mode, supplying a view for
     * custom navigation.</p>
     * 
     * <p>Custom navigation views appear between the application icon and any
     * action buttons and may use any space available there. Common use cases
     * for custom navigation views might include an auto-suggesting address bar
     * for a browser or other navigation mechanisms that do not translate well
     * to provided navigation modes.</p>
     * 
     * <p>The display option DISPLAY_SHOW_CUSTOM must be set for the custom
     * view to be displayed.</p>
     * 
     * @param resId Resource ID of a layout to inflate into the ActionBar.
     * 
     * @see #getCustomView()
     * @see #setCustomView(View)
     */
    public void setCustomView(int resId) {
        mCustomView.removeAllViews();
        mInflater.inflate(resId, mCustomView, true);
        setDisplayShowCustomEnabled(true);
    }
    
    /**
     * Set the action bar into custom navigation mode, supplying a view for
     * custom navigation. Custom navigation views appear between the
     * application icon and any action buttons and may use any space available
     * there. Common use cases for custom navigation views might include an
     * auto-suggesting address bar for a browser or other navigation mechanisms
     * that do not translate well to provided navigation modes.
     * 
     * @param view Custom navigation view to place in the ActionBar.
     */
    public void setCustomView(View view) {
        mCustomView.removeAllViews();
        mCustomView.addView(view);
        setDisplayShowCustomEnabled(true);
    }

    /**
     * Set whether home should be displayed as an "up" affordance. Set this to
     * true if selecting "home" returns up by a single level in your UI rather
     * than back to the top level or front page.
     * 
     * @param showHomeAsUp {@code true} to show the user that selecting home
     * will return one level up rather than to the top level of the app.
     * 
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     */
    public void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
        setDisplayOption(DISPLAY_HOME_AS_UP, showHomeAsUp);
        reloadDisplay();
    }
    
    /**
     * <p>Set selected display options. Only the options specified by mask will
     * be changed. To change all display option bits at once, see
     * {@link #setDisplayOptions(int)}.</p>
     * 
     * <p>Example: {@code setDisplayOptions(0, DISPLAY_SHOW_HOME)} will disable
     * the {@link #DISPLAY_SHOW_HOME} option.
     * {@code setDisplayOptions(DISPLAY_SHOW_HOME, DISPLAY_SHOW_HOME | DISPLAY_USE_LOGO)}
     * will enable {@link #DISPLAY_SHOW_HOME} and disable
     * {@link #DISPLAY_USE_LOGO}.</p>
     * 
     * @param options A combination of the bits defined by the DISPLAY_
     * constants defined in ActionBar.
     * @param mask A bit mask declaring which display options should be changed
     */
    public void setDisplayOptions(int options, int mask) {
        mFlags = (mFlags & ~mask) | options;
        reloadDisplay();
    }
    
    /**
     * Set display options. This changes all display option bits at once. To
     * change a limited subset of display options, see
     * {@link #setDisplayOptions(int, int)}.
     * 
     * @param options A combination of the bits defined by the DISPLAY_
     * constants defined in ActionBar.
     */
    public void setDisplayOptions(int options) {
        mFlags = options;
        reloadDisplay();
    }
    
    /**
     * <p>Set whether a custom view should be displayed, if set.</p>
     * 
     * <p>To set several display options at once, see the setDisplayOptions
     * methods.</p>
     * 
     * <p>Due to the nature of screen real estate on a phone, this will only be
     * displayed when in {@link #NAVIGATION_MODE_STANDARD} and will hide the
     * flag {@link #DISPLAY_SHOW_TITLE}, if enabled.</p>
     * 
     * @param showCustom {@code true} if the currently set custom view should
     * be displayed, {@code false} otherwise.
     * 
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     */
    public void setDisplayShowCustomEnabled(boolean showCustom) {
        setDisplayOption(DISPLAY_SHOW_CUSTOM, showCustom);
        reloadDisplay();
    }
    
    /**
     * Set whether to include the application home affordance in the action
     * bar. Home is presented as either an activity icon or logo.
     * 
     * @param showHome {@code true} to show home, {@code false} otherwise.
     * 
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     */
    public void setDisplayShowHomeEnabled(boolean showHome) {
        setDisplayOption(DISPLAY_SHOW_HOME, showHome);
        reloadDisplay();
    }
    
    /**
     * <p>Set whether an activity title should be displayed.</p>
     * 
     * <p>Due to the nature of screen real estate on a phone, this will only be
     * displayed when in {@link #NAVIGATION_MODE_STANDARD} and will require
     * {@link #DISPLAY_SHOW_CUSTOM} to be disabled.</p>
     * 
     * @param showTitle {@code true} to display a title if present.
     * 
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     */
    public void setDisplayShowTitleEnabled(boolean showTitle) {
        setDisplayOption(DISPLAY_SHOW_TITLE, showTitle);
        reloadDisplay();
    }
    
    /**
     * Set whether to display the activity logo rather than the activity icon.
     * A logo is often a wider, more detailed image.
     * 
     * @param useLogo {@code true} to use the activity logo, {@code false} to
     * use the activity icon.
     * 
     * @see #setDisplayOptions(int)
     * @see #setDisplayOptions(int, int)
     */
    public void setDisplayUseLogoEnabled(boolean useLogo) {
        setDisplayOption(DISPLAY_USE_LOGO, useLogo);
        reloadDisplay();
    }
    
    /**
     * Set the adapter and navigation callback for list navigation mode. The
     * supplied adapter will provide views for the expanded list as well as the
     * currently selected item. (These may be displayed differently.) The
     * supplied OnNavigationListener will alert the application when the user
     * changes the current list selection.
     * 
     * @param adapter An adapter that will provide a list of items to be
     * rendered to string.
     * @param callback An OnNavigationListener that will receive events when
     * the user selects a navigation item.
     */
    public void setListNavigationCallbacks(SpinnerAdapter adapter, ActionBar.OnNavigationListener callback) {
        //Reset selected item
        mSelectedIndex = 0;
        //Save adapter and callback
        mListAdapter = adapter;
        mListCallback = callback;
        
        reloadDisplay();
    }
    
    /**
     * Set the current navigation mode.
     * 
     * @param mode The new mode to set.
     * 
     * @see #getNavigationMode()
     * @see #NAVIGATION_MODE_STANDARD
     * @see #NAVIGATION_MODE_LIST
     * @see #NAVIGATION_MODE_TABS
     */
    public void setNavigationMode(int mode) {
        if ((mode != NAVIGATION_MODE_STANDARD) && (mode != NAVIGATION_MODE_LIST)
                && (mode != NAVIGATION_MODE_TABS)) {
            throw new IllegalArgumentException("Unknown navigation mode value " + Integer.toString(mode));
        }

        if (mode != mNavigationMode) {
            mNavigationMode = mode;
            mSelectedIndex = (mode != NAVIGATION_MODE_LIST) ? -1 : 0;
            reloadDisplay();
        }
    }
    
    /**
     * Set the selected navigation item in list or tabbed navigation modes.
     * 
     * @param position Position of the item to select.
     */
    public void setSelectedNavigationItem(int position) {
        if (mNavigationMode == NAVIGATION_MODE_LIST) {
            mSelectedIndex = position;
            reloadDisplay();
        } else if (mNavigationMode == NAVIGATION_MODE_TABS) {
            getTabAt(position).select();
        }
    }
    
    /**
     * Set the action bar's subtitle. This will only be displayed if
     * {@link #DISPLAY_SHOW_TITLE} is set.
     * 
     * @param resId Resource ID of subtitle string to set.
     * 
     * @see #setSubtitle(CharSequence)
     * @see #setDisplayShowTitleEnabled(boolean)
     */
    public void setSubtitle(int resId) {
        mSubtitleView.setText(resId);
        reloadDisplay();
    }
    
    /**(
     * Set the action bar's subtitle. This will only be displayed if
     * {@link #DISPLAY_SHOW_TITLE} is set. Set to {@code null} to disable the
     * subtitle entirely.
     * 
     * @param subtitle Subtitle to set
     * 
     * @see #setSubtitle(int)
     * @see #setDisplayShowTitleEnabled(boolean)
     */
    public void setSubtitle(CharSequence subtitle) {
        mSubtitleView.setText((subtitle == null) ? "" : subtitle);
        reloadDisplay();
    }

    /**
     * Set the action bar's title.
     * 
     * @param title Title to set.
     * 
     * @see #setTitle(int)
     * @see #getTitle()
     */
    public void setTitle(CharSequence title) {
        mTitleView.setText((title == null) ? "" : title);
    }

    /**
     * Set the action bar's title.
     * 
     * @param resId Resource ID of title string to set.
     * 
     * @see #setTitle(CharSequence)
     * @see #getTitle()
     */
    public void setTitle(int resId) {
        mTitleView.setText(resId);
    }
    
    /**
     * Show the action bar if it is not currently showing.
     * 
     * @see #hide()
     * @see #isShowing()
     */
    public void show() {
        setVisibility(View.VISIBLE);
    }

    // ------------------------------------------------------------------------
    // ACTION ITEM METHODS
    // ------------------------------------------------------------------------
    
    /**
     * <p>Adds a new {@link Action}.</p>
     * 
     * <p>To add a home action using this method make sure the action ID is
     * {@code R.id.actionbar_item_home}.</p>
     * 
     * @param action Action to add.
     * 
     * @see #addAction(Action, int)
     * @see #addActions(Action...)
     */
    public void addAction(Action action) {
        addAction(action, getActionCount());
    }
    
    /**
     * <p>Adds a new {@link Action} at the specified index.</p>
     * 
     * <p>To add a home action using this method make sure the action ID is
     * {@code R.id.actionbar_item_home}.</p>
     * 
     * @param action Action to add.
     * @param index The position at which to add the action.
     * 
     * @see #addAction(Action)
     * @see #addActions(Action...)
     */
    public void addAction(Action action, int index) {
        if (!action.mActionBar.equals(this)) {
            throw new IllegalStateException("Cannot add an action from a different action bar.");
        }

        if (action.mItemId == R.id.actionbar_item_home) {
            mHomeView.removeAllViews();
            mHomeView.addView(action.mView);
        } else {
            mActionsView.addView(action.mView, index);
        }
    }
    
    /**
     * Adds a list of {@link Action}s.
     * 
     * @param actions List of actions to add.
     * 
     * @see #addAction(Action)
     * @see #addAction(Action, int)
     */
    public void addActions(Action... actions) {
        for (Action action : actions) {
            addAction(action);
        }
    }
    
    /**
     * Return a proxy to the action bar that implementes the {@link Menu}
     * interface to allow for inflation of action items from an XML resource.
     * 
     * @return Menu proxy instance.
     */
    public final Menu asMenu() {
        return new MenuImpl();
    }
    
    /**
     * @deprecated Use {@link #setDisplayShowHomeEnabled(boolean)}.
     */
    @Deprecated
    public void clearHomeAction() {
        mHomeView.removeAllViews();
    }
    
    /**
     * Find an action with the specified ID.
     * 
     * @param itemId Action ID.
     * @return Action or {@code null}.
     */
    public Action findAction(int itemId) {
        if (itemId == R.id.actionbar_item_home) {
            View view = mHomeView.getChildAt(0);
            if (view != null) {
                return (Action) view.getTag();
            }
        } else {
            final int count = mActionsView.getChildCount();
            for (int i = 0; i < count; i++) {
                Action action = (Action) mActionsView.getChildAt(i).getTag();
                if (action.getItemId() == itemId) {
                    return action;
                }
            }
        }
        return null;
    }
    
    /**
     * Return an action at the specified index if it exists.
     * 
     * @param index Index of the action.
     * @return Action or {@code null} if it does not exist.
     * 
     * @see #getActionCount()
     */
    public Action getActionAt(int index) {
        View view = mActionsView.getChildAt(index);
        return (view != null) ? (Action) view.getTag() : null;
    }
    
    /**
     * Returns the number of actions currently registered with the action bar.
     * 
     * @return Action count.
     */
    public int getActionCount() {
        return mActionsView.getChildCount();
    }
    
    /**
     * Create a new action for this action bar.
     * 
     * @return Action instance.
     * 
     * @see #newAction(int)
     */
    public Action newAction() {
        return new Action(this);
    }
    
    /**
     * Create a new action for this action bar with the specified unique ID.
     * 
     * @param itemId Unique action ID.
     * @return Action instance.
     * 
     * @see #newAction()
     */
    public Action newAction(int itemId) {
        Action action = new Action(this);
        action.mItemId = itemId;
        return action;
    }
    
    /**
     * Remove an action from the action bar.
     * 
     * @param action Action to remove.
     */
    public void removeAction(Action action) {
        final int count = mActionsView.getChildCount();
        for (int i = 0; i < count; i++) {
            if (mActionsView.getChildAt(i).getTag().equals(action)) {
                mActionsView.removeViewAt(i);
                break;
            }
        }
    }
    
    /**
     * Remove an action from the action bar.
     * 
     * @param itemId ID of the action to remove.
     */
    public void removeAction(int itemId) {
        final int count = mActionsView.getChildCount();
        for (int i = 0; i < count; i++) {
            if (((Action)mActionsView.getChildAt(i).getTag()).getItemId() == itemId) {
                mActionsView.removeViewAt(i);
                break;
            }
        }
    }
    
    /**
     * Remove an action from the action bar.
     * 
     * @param index Position of the action to remove.
     */
    public void removeActionAt(int index) {
        mActionsView.removeViewAt(index);
    }
    
    /**
     * Removes all action views from this action bar.
     */
    public void removeAllActions() {
        mActionsView.removeAllViews();
    }
    
    /**
     * Remove the item at the specified index.
     * 
     * @param index The position of the item to remove.
     */
    public void removeItemAt(int index) {
        mActionsView.removeViewAt(index);
    }
    
    /**
     * Set the special action for home icon and logo. This will automatically
     * enable {@link #DISPLAY_SHOW_HOME}.
     * 
     * @param action Action to set.
     */
    public void setHomeAction(Action action) {
        action.mItemId = R.id.actionbar_item_home;
        addAction(action);
        setDisplayShowHomeEnabled(true);
    }

    /**
     * <p>Set a logo for the action bar.</p>
     * 
     * <p>You must call {@link #setDisplayUseLogoEnabled(boolean)} or
     * either {@link #setDisplayOptions(int)} or
     * {@link #setDisplayOptions(int, int)} with the {@link #DISPLAY_USE_LOGO}
     * flag.</p>
     * 
     * <p><em>Note:</em> For forward compatibility you should also specify your
     * logo in the {@code android:logo} attribute of the entry for the activity
     * and/or the application in the manifest.</p>
     * 
     * @param resId Resource ID of the logo.
     * 
     * @see #setHomeLogo(Drawable)
     */
    public void setHomeLogo(int resId) {
        mHomeLogo.setImageResource(resId);
    }

    /**
     * <p>Set a logo for the action bar.</p>
     * 
     * <p>You must call {@link #setDisplayUseLogoEnabled(boolean)} or
     * either {@link #setDisplayOptions(int)} or
     * {@link #setDisplayOptions(int, int)} with the {@link #DISPLAY_USE_LOGO}
     * flag.</p>
     * 
     * <p><em>Note:</em> For forward compatibility you should also specify your
     * logo in the {@code android:logo} attribute of the entry for the activity
     * and/or the application in the manifest.</p>
     * 
     * @param logo Drawable logo.
     * 
     * @see #setHomeLogo(int)
     */
    public void setHomeLogo(Drawable logo) {
        mHomeLogo.setImageDrawable(logo);
    }

    /**
     * Set the enabled state of the progress bar.
     * 
     * @param One of {@link View#VISIBLE}, {@link View#INVISIBLE},
     *   or {@link View#GONE}.
     * 
     * @see #getProgressBarVisibility()
     */
    public void setProgressBarVisibility(int visibility) {
        mProgress.setVisibility(visibility);
    }

    /**
     * Returns the visibility status for the progress bar.
     * 
     * @param One of {@link View#VISIBLE}, {@link View#INVISIBLE},
     *   or {@link View#GONE}.
     * 
     * @see #setProgressBarVisibility(int)
     */
    public int getProgressBarVisibility() {
        return mProgress.getVisibility();
    }

    /**
     * Function to set a click listener for title and subtitle TextView.
     * 
     * @param listener The callback listener.
     */
    public void setOnTitleClickListener(OnClickListener listener) {
        mTitleView.setOnClickListener(listener);
        mSubtitleView.setOnClickListener(listener);
    }

    // ------------------------------------------------------------------------
    // HELPER INTERFACES AND HELPER CLASSES
    // ------------------------------------------------------------------------

    /**
     * Exception which conveys a particular feature or method is not implemented.
     */
    public static class NotImplementedException extends RuntimeException {
        private static final long serialVersionUID = -7062769349049315824L;
    }

    /**
     * Definition of an action that can be performed along with a icon to show.
     */
    public static class Action implements MenuItem {
        private final ActionBar mActionBar;
        private final View mView;
        private final ImageView mIcon;
        private /*final*/ int mItemId;
        private final int mGroupId;
        private final int mOrder;
        
        private OnMenuItemClickListener mListener;
        private Intent mIntent;
        private CharSequence mTitle;
        private CharSequence mTitleCondensed;
        private boolean mIsEnabled;
        private boolean mIsCheckable;
        private boolean mIsChecked;

        /**
         * This constructor should <strong>ONLY</strong> be used in the
         * instantiation of Actions which will be later passed to
         * {@link ActionBar#addAction(Action)}.
         */
        Action(ActionBar actionBar) {
            mActionBar = actionBar;
            mGroupId = 0;
            mItemId = 0;
            mOrder = 0;
            mIsEnabled = true;

            mView = mActionBar.mInflater.inflate(R.layout.actionbar_item, mActionBar.mActionsView, false);
            mView.setTag(this);
            mView.setOnClickListener(mActionBar.mActionClicked);
            
            mIcon = (ImageButton) mView.findViewById(R.id.actionbar_item);
        }
        
        /**
         * This constructor should <strong>ONLY</strong> be used in the
         * inflation of Actions from a menu XML resource.
         */
        Action(ActionBar actionBar, int groupId, int itemId, int order, CharSequence title) {
            mActionBar = actionBar;
            mGroupId = groupId;
            mItemId = itemId;
            mOrder = order;
            mTitle = title;
            mIsEnabled = true;

            mView = mActionBar.mInflater.inflate(R.layout.actionbar_item, mActionBar.mActionsView, false);
            mView.setTag(this);
            mView.setOnClickListener(mActionBar.mActionClicked);

            mIcon = (ImageButton) mView.findViewById(R.id.actionbar_item);
            
            //Attach to view
            mActionBar.addAction(this);
        }
        

        @Override
        public final char getAlphabeticShortcut() {
            //We do not support shortcut keys.
            throw new NotImplementedException();
        }

        @Override
        public final int getGroupId() {
            return mGroupId;
        }
        
        @Override
        public final Drawable getIcon() {
            return mIcon.getDrawable();
        }

        @Override
        public final Intent getIntent() {
            return mIntent;
        }

        @Override
        public final int getItemId() {
            return mItemId;
        }

        @Override
        public final ContextMenuInfo getMenuInfo() {
            return null;
        }

        @Override
        public final char getNumericShortcut() {
            //We do not support shortcut keys.
            throw new NotImplementedException();
        }

        @Override
        public final int getOrder() {
            return mOrder;
        }

        @Override
        public final SubMenu getSubMenu() {
            return null;
        }

        @Override
        public final CharSequence getTitle() {
            return mTitle;
        }

        @Override
        public final CharSequence getTitleCondensed() {
            if (mTitleCondensed == null) {
                return getTitle();
            }
            return mTitleCondensed;
        }

        @Override
        public final boolean hasSubMenu() {
            return false;
        }

        @Override
        public final boolean isCheckable() {
            return mIsCheckable;
        }

        @Override
        public final boolean isChecked() {
            return mIsCheckable && mIsChecked;
        }

        @Override
        public final boolean isEnabled() {
            return mIsEnabled;
        }

        @Override
        public final boolean isVisible() {
            return mView.getVisibility() == View.VISIBLE;
        }

        @Override
        public final Action setAlphabeticShortcut(char alphaChar) {
            //We do not support shortcut keys.
            return this;
        }

        @Override
        public final Action setCheckable(boolean checkable) {
            mIsCheckable = checkable;
            return this;
        }

        @Override
        public final Action setChecked(boolean checked) {
            if (mIsCheckable) {
                mIsChecked = checked;
            }
            return this;
        }

        @Override
        public final Action setEnabled(boolean enabled) {
            mIsEnabled = enabled;
            return this;
        }

        @Override
        public final Action setIcon(Drawable icon) {
            mIcon.setImageDrawable(icon);
            return this;
        }

        @Override
        public final Action setIcon(int iconRes) {
            Drawable icon = mActionBar.getContext().getResources().getDrawable(iconRes);
            mIcon.setImageDrawable(icon);
            return this;
        }

        @Override
        public final Action setIntent(Intent intent) {
            mIntent = intent;
            return this;
        }

        @Override
        public final Action setNumericShortcut(char numericChar) {
            //We do not support shortcut keys.
            return this;
        }

        @Override
        public final Action setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
            mListener = menuItemClickListener;
            return this;
        }

        @Override
        public final Action setShortcut(char numericChar, char alphaChar) {
            //We do not support shortcut keys.
            return this;
        }

        @Override
        public final Action setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        @Override
        public final Action setTitle(int title) {
            if (title > 0) {
                this.setTitle(mActionBar.getContext().getResources().getText(title));
            }
            return this;
        }

        @Override
        public final Action setTitleCondensed(CharSequence title) {
            mTitleCondensed = title;
            return this;
        }

        @Override
        public final Action setVisible(boolean visible) {
            mView.setVisibility(visible ? View.VISIBLE : View.GONE);
            return this;
        }
        
        /**
         * Perform all actions associated with selection. Flip the checked
         * state (if checkable), execute the {@link OnMenuItemClickListener}
         * (if available) or the {@link Activity#onMenuItemSelected(int, MenuItem)}
         * that will call through to {@link Activity#onOptionsItemSelected(MenuItem)}.
         * The {@link Intent} will always be executed (if set).
         */
        public void select() {
            if (mIsCheckable) {
                mIsChecked = !mIsChecked;
            }

            if (mListener != null) {
                mListener.onMenuItemClick(this);
            } else if (mListener == null && mActionBar.getContext() instanceof Activity) {
                ((Activity) mActionBar.getContext()).onMenuItemSelected(
                        Window.FEATURE_OPTIONS_PANEL, this);
            }
            if (mIntent != null) {
                mActionBar.getContext().startActivity(mIntent);
            }
        }

        @Override
        public boolean collapseActionView() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean expandActionView() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public ActionProvider getActionProvider() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public View getActionView() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isActionViewExpanded() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public MenuItem setActionProvider(ActionProvider arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MenuItem setActionView(View view) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MenuItem setActionView(int resId) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public MenuItem setOnActionExpandListener(
                OnActionExpandListener listener) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setShowAsAction(int actionEnum) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public MenuItem setShowAsActionFlags(int actionEnum) {
            // TODO Auto-generated method stub
            return null;
        }
    }
    
    /**
     * Implementation of the {@link Menu} interface for inflation of action
     * items from XML.
     */
    final class MenuImpl implements Menu {
        private static final int DEFAULT_ITEM_ID = 0;
        private static final int DEFAULT_GROUP_ID = 0;
        private static final int DEFAULT_ORDER = 0;

        @Override
        public Action add(CharSequence title) {
            return this.add(DEFAULT_GROUP_ID, DEFAULT_ITEM_ID,
                    DEFAULT_ORDER, title);
        }

        @Override
        public Action add(int titleRes) {
            return this.add(DEFAULT_GROUP_ID, DEFAULT_ITEM_ID,
                    DEFAULT_ORDER, titleRes);
        }

        @Override
        public Action add(int groupId, int itemId, int order, int titleRes) {
            String title = null;
            if (titleRes > 0) {
                title = getContext().getResources().getString(titleRes);
            }
            return this.add(groupId, itemId, order, title);
        }

        @Override
        public Action add(int groupId, int itemId, int order, CharSequence title) {
            return new Action(ActionBar.this, groupId, itemId, order, title);
        }

        @Override
        public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
            //DISCLAIMER: I have no idea what this does. Taken verbatim from
            // http://android.git.kernel.org/?p=platform/frameworks/base.git;a=blob;f=core/java/com/android/internal/view/menu/MenuBuilder.java;h=228d5d09b0ab2efdd1a5bbc73853c92ef40b7cf7;hb=HEAD#l420
            PackageManager pm = getContext().getPackageManager();
            final List<ResolveInfo> lri =
                pm.queryIntentActivityOptions(caller, specifics, intent, 0);
            final int count = (lri != null) ? lri.size() : 0;
            
            if ((flags & Menu.FLAG_APPEND_TO_GROUP) == 0) {
                removeGroup(groupId);
            }
            
            for (int i = 0; i < count; i++) {
                final ResolveInfo ri = lri.get(i);
                Intent rintent = new Intent(
                    (ri.specificIndex < 0) ? intent : specifics[ri.specificIndex]);
                rintent.setComponent(new ComponentName(
                        ri.activityInfo.applicationInfo.packageName,
                        ri.activityInfo.name));
                final Action item = add(groupId, itemId, order, ri.loadLabel(pm))
                        .setIcon(ri.loadIcon(pm))
                        .setIntent(rintent);
                if ((outSpecificItems != null) && (ri.specificIndex > 0)) {
                    outSpecificItems[ri.specificIndex] = item;
                }
            }
            
            return count;
        }

        @Override
        public SubMenu addSubMenu(CharSequence title) {
            //We do not support sub-menus.
            throw new NotImplementedException();
        }

        @Override
        public SubMenu addSubMenu(int titleRes) {
            //We do not support sub-menus.
            throw new NotImplementedException();
        }

        @Override
        public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
            //We do not support sub-menus.
            throw new NotImplementedException();
        }

        @Override
        public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
            //We do not support sub-menus.
            throw new NotImplementedException();
        }

        @Override
        public void clear() {
            mActionsView.removeAllViews();
        }

        @Override
        public void close() {
            //Negative. We are always open.
        }

        @Override
        public Action findItem(int id) {
            if (id == R.id.actionbar_item_home) {
                View home = mHomeView.getChildAt(0);
                return (home != null) ? (Action) home.getTag() : null;
            }
            
            final int count = size();
            for (int i = 0; i < count; i++) {
                Action item = getItem(i);
                if (item.mItemId == id) {
                    return item;
                }
            }
            return null;
        }

        @Override
        public Action getItem(int index) {
            View view = mActionsView.getChildAt(index);
            return (view != null) ? (Action) view.getTag() : null;
        }

        @Override
        public boolean hasVisibleItems() {
            final int count = size();
            for (int i = 0; i < count; i++) {
                if (getItem(i).isVisible()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isShortcutKey(int keyCode, KeyEvent event) {
            //We do not support shortcut keys.
            throw new NotImplementedException();
        }

        @Override
        public boolean performIdentifierAction(int id, int flags) {
            //We do not support shortcut keys.
            throw new NotImplementedException();
        }

        @Override
        public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
            //We do not support shortcut keys.
            throw new NotImplementedException();
        }

        @Override
        public void removeGroup(int groupId) {
            final int count = size();
            for (int i = 0; i < count; i++) {
                if (getItem(i).mGroupId == groupId) {
                    mActionsView.removeViewAt(i);
                }
            }
        }

        @Override
        public void removeItem(int id) {
            final int count = size();
            for (int i = 0; i < count; i++) {
                if (getItem(i).mItemId == id) {
                    mActionsView.removeViewAt(i);
                    break;
                }
            }
        }

        @Override
        public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
            final int count = size();
            for (int i = 0; i < count; i++) {
                Action item = getItem(i);
                if (item.mGroupId == group) {
                    item.mIsCheckable = true;
                }
            }
        }

        @Override
        public void setGroupEnabled(int group, boolean enabled) {
            final int count = size();
            for (int i = 0; i < count; i++) {
                Action item = getItem(i);
                if (item.mGroupId == group) {
                    item.mIsEnabled = enabled;
                }
            }
        }

        @Override
        public void setGroupVisible(int group, boolean visible) {
            final int count = size();
            for (int i = 0; i < count; i++) {
                Action item = getItem(i);
                if (item.mGroupId == group) {
                    item.setVisible(visible);
                }
            }
        }

        @Override
        public void setQwertyMode(boolean isQwerty) {
            //Do nothing. We do not support shortcuts.
        }

        @Override
        public int size() {
            return mActionsView.getChildCount();
        }
    }

    /**
     * A tab in the action bar.
     */
    public final class Tab {
        /**
         * An invalid position for a tab.
         * 
         * @see #getPosition()
         */
        public static final int INVALID_POSITION = -1;
        
        final View mView;
        final ImageView mIconView;
        final TextView mTextView;
        final FrameLayout mCustomView;
        
        ActionBar.TabListener mListener;
        boolean mIsSelected;
        Object mTag;
        
        
        Tab() {
            mView = mInflater.inflate(R.layout.actionbar_tab, mTabsView, false);
            mView.setTag(this);
            mView.setOnClickListener(mTabClicked);
            
            mIconView = (ImageView)mView.findViewById(R.id.actionbar_tab_icon);
            mTextView = (TextView)mView.findViewById(R.id.actionbar_tab_text);
            mCustomView = (FrameLayout)mView.findViewById(R.id.actionbar_tab_custom);
        }

        
        /**
         * Update display to reflect current property state.
         */
        void reloadDisplay() {
            boolean hasCustom = mCustomView.getChildCount() > 0;
            this.mIconView.setVisibility(hasCustom ? View.GONE : View.VISIBLE);
            this.mTextView.setVisibility(hasCustom ? View.GONE : View.VISIBLE);
            this.mCustomView.setVisibility(hasCustom ? View.VISIBLE : View.GONE);
        }

        /**
         * Retrieve a previously set custom view for this tab.
         * 
         * @return The custom view set by {@link #setCustomView(View)}.
         */
        public View getCustomView() {
            return mCustomView.getChildAt(0);
        }

        /**
         * Return the icon associated with this tab.
         * 
         * @return The tab's icon.
         */
        public Drawable getIcon() {
            return mIconView.getDrawable();
        }
        
        /**
         * Return the current position of this tab in the action bar.
         * 
         * @return Current position, or {@link #INVALID_POSITION} if this tab
         * is not currently in the action bar.
         */
        public int getPosition() {
            final int count = mTabsView.getChildCount();
            for (int i = 0; i < count; i++) {
                if (mTabsView.getChildAt(i).getTag().equals(this)) {
                    return i;
                }
            }
            return INVALID_POSITION;
        }
        
        /**
         * @return This tab's tag object.
         */
        public Object getTag() {
            return mTag;
        }
        
        /**
         * Return the text of this tab.
         * 
         * @return The tab's text.
         */
        public CharSequence getText() {
            return mTextView.getText();
        }

        /**
         * Select this tab. Only valid if the tab has been added to the action
         * bar.
         */
        public void select() {
            if (mIsSelected) {
                if (mListener != null) {
                    mListener.onTabReselected(this);
                }
                return;
            }
            
            Tab selected = getSelectedTab();
            if (selected != null) {
                selected.unselect();
            }

            mIsSelected = true;
            mView.setSelected(true);
            if (mListener != null) {
                mListener.onTabSelected(this);
            }
        }

        /**
         * Set a custom view to be used for this tab. This overrides values set
         * by {@link #setText(CharSequence)} and {@link #setIcon(Drawable)}.
         * 
         * @param layoutResId A layout resource to inflate and use as a custom
         * tab view
         * @return The current instance for call chaining.
         */
        public ActionBar.Tab setCustomView(int layoutResId) {
            mCustomView.removeAllViews();
            mInflater.inflate(layoutResId, this.mCustomView, true);
            reloadDisplay();
            return this;
        }

        /**
         * Set a custom view to be used for this tab. This overrides values set
         * by {@link #setText(CharSequence)} and {@link #setIcon(Drawable)}.
         * 
         * @param view Custom view to be used as a tab.
         * @return The current instance for call chaining.
         */
        public ActionBar.Tab setCustomView(View view) {
            mCustomView.removeAllViews();
            if (view != null) {
                mCustomView.addView(view);
            }
            reloadDisplay();
            return this;
        }

        /**
         * Set the icon displayed on this tab.
         * 
         * @param icon The drawable to use as an icon.
         * @return The current instance for call chaining.
         */
        public ActionBar.Tab setIcon(Drawable icon) {
            mIconView.setImageDrawable(icon);
            return this;
        }

        /**
         * Set the icon displayed on this tab.
         * 
         * @param resId Resource ID referring to the drawable to use as an icon
         * @return The current instance for call chaining.
         */
        public ActionBar.Tab setIcon(int resId) {
            mIconView.setImageResource(resId);
            return this;
        }

        /**
         * Set the {@link ActionBar.TabListener} that will handle switching to
         * and from this tab. All tabs must have a TabListener set before being
         * added to the ActionBar.
         * 
         * @param listener Listener to handle tab selection events.
         */
        public ActionBar.Tab setTabListener(ActionBar.TabListener listener) {
            mListener = listener;
            return this;
        }

        /**
         * Give this Tab an arbitrary object to hold for later use.
         * 
         * @param obj Object to store.
         * @return The current instance for call chaining.
         */
        public ActionBar.Tab setTag(Object obj) {
            mTag = obj;
            return this;
        }

        /**
         * Set the text displayed on this tab. Text may be truncated if there
         * is not room to display the entire string.
         * 
         * @param text The text to display.
         * @return The current instance for call chaining.
         */
        public ActionBar.Tab setText(CharSequence text) {
            mTextView.setText(text);
            return this;
        }

        /**
         * Set the text displayed on this tab. Text may be truncated if there
         * is not room to display the entire string.
         * 
         * @param text Resource ID of text to display.
         * @return The current instance for call chaining.
         */
        public ActionBar.Tab setText(int resId) {
            mTextView.setText(resId);
            return this;
        }

        /**
         * Unselect this tab. Only valid if the tab has been added to the
         * action bar and was previously selected.
         */
        void unselect() {
            if (mIsSelected) {
                mView.setSelected(false);
                mIsSelected = false;

                if (mListener != null) {
                    mListener.onTabUnselected(this);
                }
            }
        }
    }

    /**
     * Callback interface invoked when a tab is focused, unfocused, added, or
     * removed.
     */
    public interface TabListener {
        /**
         * Called when a tab that is already selected is chosen again by the
         * user. Some applications may use this action to return to the top
         * level of a category.
         * 
         * @param tab The tab that was reselected.
         */
        public void onTabReselected(ActionBar.Tab tab);

        /**
         * Called when a tab enters the selected state.
         * 
         * @param tab The tab that was selected.
         */
        public void onTabSelected(ActionBar.Tab tab);

        /**
         * Called when a tab exits the selected state.
         * 
         * @param tab The tab that was unselected.
         */
        public void onTabUnselected(ActionBar.Tab tab);
    }
    
    /**
     * Listener interface for ActionBar navigation events.
     */
    public static interface OnNavigationListener {
        /**
         * This method is called whenever a navigation item in your action bar
         * is selected.
         * 
         * @param itemPosition Position of the item clicked.
         * @param itemId ID of the item clicked.
         * @return {@code true} if the event was handled, {@code false}
         * otherwise.
         */
        boolean onNavigationItemSelected(int itemPosition, long itemId);
    }
}
