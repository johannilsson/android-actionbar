/*
 * Copyright (C) 2010 Johan Nilsson <http://markupartist.com>
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
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
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
public class ActionBar extends RelativeLayout implements Menu {
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
    private final RelativeLayout mBarView;
    
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
    
    /** Bottom line when in tabs navigation mode. */
    private final View mTabsBottomLine;

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
                                //Execute call back, if exists
                                if (mListCallback != null) {
                                    mListCallback.onNavigationItemSelected(position, mListAdapter.getItemId(position));
                                }
                                
                                if (position != mSelectedIndex) {
                                    mSelectedIndex = position;
                                    reloadDisplay();
                                }
                            }
                        })
                        .setParent(mListView)
                        .show();
            }
        }
    };
    
    private final View.OnClickListener mActionClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Action action = (Action) view.getTag();    
            if (action.mListener != null) {
                action.mListener.onMenuItemClick(action);
            }
            if (action.getIntent() != null) {
                getContext().startActivity(action.getIntent());
            }
        }
    };
    
    private final View.OnClickListener mTabClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final Tab tab = (Tab) view.getTag();
            if (tab.mIsSelected) {
                tab.mListener.onTabReselected(tab);
            } else {
                tab.select();
            }
        }
    };

    
    
    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBarView = (RelativeLayout) mInflater.inflate(R.layout.actionbar, null);
        addView(mBarView);

        mHomeView = (FrameLayout) mBarView.findViewById(R.id.actionbar_home_view);
        mHomeLogo = (ImageView) mBarView.findViewById(R.id.actionbar_home_logo);
        mHomeUpIndicator = mBarView.findViewById(R.id.actionbar_home_is_back);

        mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);
        mSubtitleView = (TextView) mBarView.findViewById(R.id.actionbar_subtitle);
        
        mListView = (FrameLayout) mBarView.findViewById(R.id.actionbar_list);
        mListView.setOnClickListener(mListClicked);
        mListIndicator = mBarView.findViewById(R.id.actionbar_list_indicator);
        
        mCustomView = (FrameLayout) mBarView.findViewById(R.id.actionbar_custom);
        
        mActionsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);

        mTabsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_tabs);
        mTabsBottomLine = mBarView.findViewById(R.id.actionbar_tabs_bottom_line);

        mProgress = (ProgressBar) mBarView.findViewById(R.id.actionbar_progress);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ActionBar);
        CharSequence title = a.getString(R.styleable.ActionBar_title);
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
        
        a.recycle();
        
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
        }
        
        //Only show list if we are in list navigation and there are list items
        mListView.setVisibility(isList && hasList ? View.VISIBLE : View.GONE);
        mListIndicator.setVisibility(isList && hasList ? View.VISIBLE : View.GONE);

        // Show tabs if in tabs navigation mode.
        mTabsView.setVisibility(isTab ? View.VISIBLE : View.GONE);
        mTabsBottomLine.setVisibility(isTab ? View.VISIBLE : View.GONE);

        //Show title view if we are not in list navigation, not showing custom
        //view, and the show title flag is true
        mTitleView.setVisibility(!isList && !showingCustom && !isTab && showingTitle ? View.VISIBLE : View.GONE);
        //Show subtitle view if we are not in list navigation, not showing
        //custom view, show title flag is true, and a subtitle is set
        mSubtitleView.setVisibility(!isList && !showingCustom && !isTab && showingTitle && hasSubtitle ? View.VISIBLE : View.GONE);
        //Show custom view if we are not in list navigation, not in tab
        //navigation, and the showing custom flag is set
        mCustomView.setVisibility(!isList && !isList && !isTab && showingCustom ? View.VISIBLE : View.GONE);
    }

    // ------------------------------------------------------------------------
    // MENU INTERFACE METHODS
    // ------------------------------------------------------------------------
    
    @Override
    public MenuItem add(CharSequence title) {
        return new Action().setTitle(title);
    }
    
    @Override
    public MenuItem add(int titleRes) {
        return new Action().setTitle(titleRes);
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        return new Action(groupId, itemId, order).setTitle(title);
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        return new Action(groupId, itemId, order).setTitle(titleRes);
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
            final MenuItem item = add(groupId, itemId, order, ri.loadLabel(pm))
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
    public MenuItem findItem(int id) {
        if (id == R.id.actionbar_item_home) {
            View home = mHomeView.getChildAt(0);
            return (home != null) ? (MenuItem) home.getTag() : null;
        }
        
        final int count = size();
        for (int i = 0; i < count; i++) {
            MenuItem item = getItem(i);
            if (item.getItemId() == id) {
                return item;
            }
        }
        return null;
    }

    @Override
    public MenuItem getItem(int index) {
        View view = mActionsView.getChildAt(index);
        return (view != null) ? (MenuItem) view.getTag() : null;
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
            if (getItem(i).getGroupId() == groupId) {
                mActionsView.removeViewAt(i);
            }
        }
    }

    @Override
    public void removeItem(int id) {
        final int count = size();
        for (int i = 0; i < count; i++) {
            if (getItem(i).getItemId() == id) {
                mActionsView.removeViewAt(i);
                break;
            }
        }
    }

    @Override
    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
        final int count = size();
        for (int i = 0; i < count; i++) {
            MenuItem item = getItem(i);
            if (item.getGroupId() == group) {
                item.setCheckable(true);
            }
        }
    }

    @Override
    public void setGroupEnabled(int group, boolean enabled) {
        final int count = size();
        for (int i = 0; i < count; i++) {
            MenuItem item = getItem(i);
            if (item.getGroupId() == group) {
                item.setEnabled(enabled);
            }
        }
    }

    @Override
    public void setGroupVisible(int group, boolean visible) {
        final int count = size();
        for (int i = 0; i < count; i++) {
            MenuItem item = getItem(i);
            if (item.getGroupId() == group) {
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
        final int position = getTabCount();
        addTab(tab, position, setSelected);
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
        mTabsView.addView(tab.inflate(setSelected), position);
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
                if (getTabAt(i).isSelected()) {
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
            if (tab.isSelected()) {
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
        getSelectedTab().unselect();
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
    // CUSTOM METHODS
    // ------------------------------------------------------------------------

    /**
     * Add a new item to the menu.
     * 
     * @return The newly added menu item. 
     */
    public MenuItem add() {
        return add("");
    }
    
    /**
     * Add a new item to the menu with the given icon.
     * 
     * @param itemId Unique item id.
     * @param iconRes Icon resource ID.
     * @return The newly added menu item. 
     */
    public MenuItem add(int itemId, int iconRes) {
        return add(0, itemId, 0, "").setIcon(iconRes);
    }
    
    /**
     * Add a new item to the menu with the given id, icon, and intent.
     * 
     * @param itemId Unique item id.
     * @param iconRes Icon resource ID.
     * @param intent Intent to be executed when clicked.
     * @return The newly added menu item. 
     */
    public MenuItem add(int itemId, int iconRes, Intent intent) {
        return add(itemId, iconRes).setIntent(intent);
    }
    
    /**
     * Add a new item to the menu with the given id, icon, and click listener.
     * 
     * @param itemId Unique item id.
     * @param iconRes Icon resource ID.
     * @param listener Item click listener.
     * @return The newly added menu item. 
     */
    public MenuItem add(int itemId, int iconRes, OnMenuItemClickListener listener) {
        return add(itemId, iconRes).setOnMenuItemClickListener(listener);
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
    final class Action implements MenuItem {
        private final View mView;
        private final ImageView mIcon;
        private final int mItemId;
        private final int mGroupId;
        private final int mOrder;
        
        OnMenuItemClickListener mListener;
        Intent mIntent;
        CharSequence mTitle;
        boolean mIsEnabled;
        boolean mIsCheckable;
        boolean mIsChecked;
        
        
        Action() {
            this(0, 0, 0);
        }
        Action(int groupId, int itemId, int order) {
            mView = mInflater.inflate(R.layout.actionbar_item, mActionsView, false);
            mView.setTag(this);
            mView.setOnClickListener(ActionBar.this.mActionClicked);
            
            if (itemId == R.id.actionbar_item_home) {
                mHomeView.removeAllViews();
                mHomeView.addView(mView);
            } else {
                mActionsView.addView(mView);
            }
            
            mIcon = (ImageButton) mView.findViewById(R.id.actionbar_item);
            
            mGroupId = groupId;
            mItemId = itemId;
            mOrder = order;
            mIsEnabled = true;
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
        public Drawable getIcon() {
            return mIcon.getDrawable();
        }

        @Override
        public Intent getIntent() {
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
        public CharSequence getTitle() {
            return mTitle;
        }

        @Override
        public final CharSequence getTitleCondensed() {
            return getTitle();
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
        public boolean isEnabled() {
            return mIsEnabled;
        }

        @Override
        public boolean isVisible() {
            return mView.getVisibility() == View.VISIBLE;
        }

        @Override
        public final MenuItem setAlphabeticShortcut(char alphaChar) {
            //We do not support shortcut keys.
            return this;
        }

        @Override
        public MenuItem setCheckable(boolean checkable) {
            mIsCheckable = checkable;
            return this;
        }

        @Override
        public MenuItem setChecked(boolean checked) {
            if (mIsCheckable) {
                mIsChecked = checked;
            }
            return this;
        }

        @Override
        public MenuItem setEnabled(boolean enabled) {
            mIsEnabled = enabled;
            return this;
        }

        @Override
        public MenuItem setIcon(Drawable icon) {
            mIcon.setImageDrawable(icon);
            return this;
        }

        @Override
        public MenuItem setIcon(int iconRes) {
            return setIcon(getContext().getResources().getDrawable(iconRes));
        }

        @Override
        public MenuItem setIntent(Intent intent) {
            mIntent = intent;
            return this;
        }

        @Override
        public MenuItem setNumericShortcut(char numericChar) {
            //We do not support shortcut keys.
            return this;
        }

        @Override
        public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
            mListener = menuItemClickListener;
            return this;
        }

        @Override
        public MenuItem setShortcut(char numericChar, char alphaChar) {
            //We do not support shortcut keys.
            return this;
        }

        @Override
        public MenuItem setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        @Override
        public MenuItem setTitle(int title) {
            return setTitle(getContext().getResources().getText(title));
        }

        @Override
        public MenuItem setTitleCondensed(CharSequence title) {
            return setTitle(title);
        }

        @Override
        public MenuItem setVisible(boolean visible) {
            mView.setVisibility(visible ? View.VISIBLE : View.GONE);
            return this;
        }
    }

    /**
     * A tab in the action bar.
     */
    public class Tab {
        private View mView;
        private CharSequence mText;
        private ActionBar.TabListener mListener;
        private boolean mIsSelected;
        
        /**
         * Get whether or not this tab is currently selected.
         * 
         * @return Tab selection state.
         */
        boolean isSelected() {
            return mIsSelected;
        }

        /**
         * Return the text of this tab.
         * 
         * @return The tab's text.
         */
        public CharSequence getText() {
            return mText;
        }

        /**
         * Set the text displayed on this tab. Text may be truncated if there
         * is not room to display the entire string.
         * 
         * @param text The text to display.
         */
        public ActionBar.Tab setText(CharSequence text) {
            mText = text;

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
            
            View selectedView = mView.findViewById(R.id.actionbar_tab_selected);
            selectedView.setBackgroundColor(Color.WHITE);

            final int tabs = mTabsView.getChildCount();
            for (int tabIndex = 0; tabIndex < tabs; tabIndex++) {
                View tabView = mTabsView.getChildAt(tabIndex);
                if ((tabView != null) && (tabView.getTag() instanceof Tab)) {
                    ((Tab)tabView.getTag()).unselect();
                }
            }

            mIsSelected = true;
            if (mListener != null) {
                mListener.onTabSelected(this);
            }
        }

        void unselect() {
            if (mIsSelected) {
                View selectedView = mView.findViewById(R.id.actionbar_tab_selected);
                selectedView.setBackgroundColor(Color.TRANSPARENT);

                if (mListener != null) {
                    mListener.onTabUnselected(this);
                }
            }
            mIsSelected = false;
        }

        private View inflate(boolean isSelected) {
            mView = mInflater.inflate(R.layout.actionbar_tab, mTabsView, false);

            TextView textView =
                (TextView) mView.findViewById(R.id.actionbar_tab);
            textView.setText(getText());

            View selectedView = mView.findViewById(R.id.actionbar_tab_selected);
            if (!isSelected) {
                selectedView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                select();
            }

            mView.setTag(this);
            mView.setOnClickListener(ActionBar.this.mTabClicked);

            return mView;
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
