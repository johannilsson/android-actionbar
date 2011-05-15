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

import com.markupartist.android.widget.actionbar.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Implementation of the action bar design pattern for use on Android 1.5+.
 * 
 * @author Johan Nilsson <http://markupartist.com>
 */
public class ActionBar extends RelativeLayout implements View.OnClickListener {
    
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
    
    ///**
    // * Show the custom view if one has been set.
    // * 
    // * @see #setDisplayShowCustomEnabled(boolean)
    // * @see #setDisplayOptions(int)
    // * @see #setDisplayOptions(int, int)
    // */
    //TODO public static final int DISPLAY_SHOW_CUSTOM = 0x10;// = android.app.ActionBar.DISPLAY_SHOW_CUSTOM;
    
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
     * Show the activity title.
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
     * List navigation mode. Instead of static title text this mode presents a
     * list menu for navigation within the activity. e.g. this might be
     * presented to the user as a dropdown list.
     */
    public static final int NAVIGATION_MODE_STANDARD = 0x0;// = android.app.ActionBar.NAVIGATION_MODE_STANDARD;
    
    /**
     * Standard navigation mode. Consists of either a logo or icon and title
     * text with an optional subtitle. Clicking any of these elements will
     * dispatch onOptionsItemSelected to the host Activity with a MenuItem with
     * item ID android.R.id.home.
     */
    public static final int NAVIGATION_MODE_LIST = 0x1;// = android.app.ActionBar.NAVIGATION_MODE_LIST;
    
    ///**
    // * Tab navigation mode. Instead of static title text this mode presents a
    // * series of tabs for navigation within the activity.
    // */
    //public static final int NAVIGATION_MODE_TABS = 0x2;// = android.app.ActionBar.NAVIGATION_MODE_TABS;

    
    private final LayoutInflater mInflater;
    
    private final RelativeLayout mBarView;
    
    private final ImageView mHomeLogo;
    
    /** Home button up indicator. */
    private final View mHomeUpIndicator;
    
    /** Title view. */
    private final TextView mTitleView;
    
    /** List view. */
    private final TextView mListView;
    
    /** List dropdown indicator. */
    private final View mListIndicator;
    
    ///** Custom view. */
    //TODO private View mCustomView;
    
    private final LinearLayout mActionsView;
    
    private final ProgressBar mProgress;
    
    /** Home logo. */
    private final ImageButton mHomeIconImage;
    
    /** Home button. */
    private final RelativeLayout mHomeIcon;
    
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
     * Current selected index of either the list or tab navigation.
     */
    private int mSelectedIndex;
    
    /**
     * Adapter for the list navigation contents.
     */
    private ListAdapter mListAdapter;
    
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
                new AlertDialog.Builder(getContext())
                        .setAdapter(mListAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                //Execute call back, if exists
                                if (mListCallback != null) {
                                    mListCallback.onNavigationItemSelected(position, mListAdapter.getItemId(position));
                                }
                                
                                //Update list view in action bar
                                mListView.setText(mListAdapter.getItem(position).toString());
                            }
                        })
                        .show();
            }
        }
    };

    
    
    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBarView = (RelativeLayout) mInflater.inflate(R.layout.actionbar, null);
        addView(mBarView);

        mHomeLogo = (ImageView) mBarView.findViewById(R.id.actionbar_home_logo);
        mHomeIcon = (RelativeLayout) mBarView.findViewById(R.id.actionbar_home_bg);
        mHomeIconImage = (ImageButton) mBarView.findViewById(R.id.actionbar_home_btn);
        mHomeUpIndicator = mBarView.findViewById(R.id.actionbar_home_is_back);

        mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);
        
        mListView = (TextView) mBarView.findViewById(R.id.actionbar_list);
        mListView.setOnClickListener(mListClicked);
        
        mListIndicator = mBarView.findViewById(R.id.actionbar_list_indicator);
        
        mActionsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);
        
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
                setTitle(context.getPackageManager().getActivityInfo(componentName, 0).labelRes);
            } catch (NameNotFoundException e) {
                //Can't load and/or find title. Eat exception.
            }
        }
        
        a.recycle();
        
        //Must be >= gingerbread to look for a logo
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) && (context instanceof Activity)) {
            Drawable logo = null;
            Activity activity = (Activity) context;
            
            //Try to load the logo from the Activity's manifest entry
            try {
                logo = context.getPackageManager().getActivityLogo(activity.getComponentName());
            } catch (NameNotFoundException e) {
                //Can't load and/or find logo. Eat exception.
            }
            
            if (logo == null) {
                //Try to load the logo from the Application's manifest entry
                logo = activity.getApplicationInfo().loadLogo(context.getPackageManager());
            }
            
            if (logo != null) {
                setHomeLogo(logo);
            }
        }
        
        //Show the home icon and title by default
        setDisplayOption(DISPLAY_SHOW_TITLE, true);
        reloadDisplay();
        
        //Use standard navigation by default
        setNavigationMode(NAVIGATION_MODE_STANDARD);
    }
    
    
    
    // ------------------------------------------------------------------------
    // PRIVATE HELPER METHODS
    // ------------------------------------------------------------------------
    
    /**
     * Helper to set a flag to a new value. This will also update the action
     * bar accordingly to reflect the current state of the flags.
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
        if (getDisplayOptionValue(DISPLAY_SHOW_HOME)) {
            mHomeUpIndicator.setVisibility(getDisplayOptionValue(DISPLAY_HOME_AS_UP) ? View.VISIBLE : View.GONE);
            final boolean usingLogo = getDisplayOptionValue(DISPLAY_USE_LOGO);
            mHomeLogo.setVisibility(usingLogo ? View.VISIBLE : View.GONE);
            mHomeIcon.setVisibility(usingLogo ? View.GONE : View.VISIBLE);
        } else {
            mHomeUpIndicator.setVisibility(View.GONE);
            mHomeLogo.setVisibility(View.GONE);
            mHomeIcon.setVisibility(View.GONE);
        }
        
        mTitleView.setVisibility(getDisplayOptionValue(DISPLAY_SHOW_TITLE) ? View.VISIBLE : View.GONE);
        //TODO mCustomView.setVisibility(getDisplayOptionValue(DISPLAY_SHOW_CUSTOM) ? View.VISIBLE : View.GONE);
    }
    
    /**
     * Reload the current action bar navigation state.
     */
    private void reloadNavigation() {
        boolean isList = mNavigationMode == NAVIGATION_MODE_LIST;
        
        if (isList) {
            //Set the list view to the currently selected item
            mListView.setText(mListAdapter.getItem(mSelectedIndex).toString());
        }
        
        mListView.setVisibility(isList ? View.VISIBLE : View.GONE);
        mListIndicator.setVisibility(isList ? View.VISIBLE : View.GONE);
        
        //Only hide title view if we are in list navigation or the show title flag is false
        mTitleView.setVisibility(isList || !getDisplayOptionValue(DISPLAY_SHOW_TITLE) ? View.GONE : View.VISIBLE);
    }

    /**
     * Inflates a {@link View} with the given {@link Action}.
     * 
     * @param action The action to inflate
     * @return View
     */
    private View inflateAction(Action action) {
        View view = mInflater.inflate(R.layout.actionbar_item, mActionsView, false);

        ImageButton labelView =
            (ImageButton) view.findViewById(R.id.actionbar_item);
        
        if (action.getIcon() != null) {
            labelView.setImageDrawable(action.getIcon());
        } else if (action.getDrawable() != NO_ID) {
            labelView.setImageResource(action.getDrawable());
        }

        view.setTag(action);
        view.setOnClickListener(this);
        return view;
    }
    
    // ------------------------------------------------------------------------
    // NATIVE ACTION BAR METHODS
    // ------------------------------------------------------------------------
    
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
    
    public int getNavigationItemCount() {
        if (mNavigationMode == NAVIGATION_MODE_LIST) {
            return mListAdapter.getCount();
        }
        //if (this.mNavigationMode == NAVIGATION_MODE_TABS) {
        //  //TODO
        //}
        return -1;
    }
    
    public int getNavigationMode() {
        return mNavigationMode;
    }
    
    public int getSelectedNavigationIndex() {
        if ((mNavigationMode == NAVIGATION_MODE_LIST)/* || (mNavigationMode == NAVIGATION_MODE_TABS*/) {
            return mSelectedIndex;
        }
        return -1;
    }
    
    /**
     * Returns the current ActionBar title in standard mode.
     * 
     * @return The current ActionBar title or null.
     * 
     * @see #setTitle(CharSequence)
     * @see #setTitle(int)
     * @see #setDisplayShowTitleEnabled(boolean)
     */
    public CharSequence getTitle() {
        if (getDisplayOptionValue(DISPLAY_SHOW_TITLE)) {
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
        return getVisibility() == View.GONE;
    }
    
    //Implemented by superclass:
    //public void setBackgroundDrawable(Drawable d) {}

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
    
    ///**
    // * <p>Set whether a custom view should be displayed, if set.</p>
    // * 
    // * <p>To set several display options at once, see the setDisplayOptions
    // * methods.</p>
    // * 
    // * @param showCustom {@code true} if the currently set custom view should
    // * be displayed, {@code false} otherwise.
    // * 
    // * @see #setDisplayOptions(int)
    // * @see #setDisplayOptions(int, int)
    // */
    //TODO public void setDisplayShowCustomEnabled(boolean showCustom) {
    //  setDisplayOption(DISPLAY_SHOW_CUSTOM, showCustom);
    //  reloadFromDisplayOptions();
    //}
    
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
     * Set whether an activity title should be displayed.
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
    
    public void setListNavigationCallbacks(ListAdapter adapter, ActionBar.OnNavigationListener callback) {
        //Reset selected item
        mSelectedIndex = 0;
        //Save adapter and callback
        mListAdapter = adapter;
        mListCallback = callback;
        
        reloadNavigation();
    }
    
    public void setNavigationMode(int mode) {
        if ((mode != NAVIGATION_MODE_STANDARD) && (mode != NAVIGATION_MODE_LIST)
                /*TODO && (mode != NAVIGATION_MODE_TABS)*/) {
            throw new IllegalArgumentException("Unknown navigation mode value " + Integer.toString(mode));
        }
        
        if (mode != mNavigationMode) {
            mNavigationMode = mode;
            mSelectedIndex = (mode == NAVIGATION_MODE_STANDARD) ? -1 : 0;
            reloadNavigation();
        }
    }
    
    public void setSelectedNavigationItem(int position) {
        if ((mNavigationMode != NAVIGATION_MODE_STANDARD) && (position != mSelectedIndex)) {
            mSelectedIndex = position;
            reloadNavigation();
        }
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
        mTitleView.setText(title);
    }

    /**
     * Set the action bar's title.
     * 
     * @param resid Resource ID of title string to set.
     * 
     * @see #setTitle(CharSequence)
     * @see #getTitle()
     */
    public void setTitle(int resid) {
        mTitleView.setText(resid);
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
    // LEGACY AND DEPRECATED METHODS
    // ------------------------------------------------------------------------

    /**
     * Set the special action for home icon and logo. This will automatically
     * enable {@link #DISPLAY_SHOW_HOME}.
     * 
     * @param action Action to set.
     */
    public void setHomeAction(Action action) {
        mHomeLogo.setOnClickListener(this);
        mHomeLogo.setTag(action);
        
        mHomeIconImage.setOnClickListener(this);
        mHomeIconImage.setTag(action);
        if (action.getIcon() != null) {
            mHomeIconImage.setImageDrawable(action.getIcon());
        } else if (action.getDrawable() != NO_ID) {
            mHomeIconImage.setImageResource(action.getDrawable());
        }
        
        setDisplayShowHomeEnabled(true);
    }
    
    /**
     * @deprecated See {@link #setDisplayShowHomeEnabled(boolean)}.
     */
    @Deprecated
    public void clearHomeAction() {
        setDisplayShowHomeEnabled(false);
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
     * Function to set a click listener for Title TextView
     * 
     * @param listener the onClickListener
     */
    public void setOnTitleClickListener(OnClickListener listener) {
        mTitleView.setOnClickListener(listener);
    }

    @Override
    public void onClick(View view) {
        final Object tag = view.getTag();
        if (tag instanceof Action) {
            final Action action = (Action) tag;
            action.performAction(view);
        }
    }

    /**
     * Adds a list of {@link Action}s.
     * @param actionList the actions to add
     * 
     * @see #addAction(Action)
     * @see #addAction(Action, int)
     */
    public void addActions(List<Action> actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     * 
     * @param action the action to add
     * 
     * @see #addAction(Action, int)
     * @see #addActions(List)
     */
    public void addAction(Action action) {
        final int index = mActionsView.getChildCount();
        addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     * 
     * @param action the action to add
     * @param index the position at which to add the action
     * 
     * @see #addAction(Action)
     * @see #addActions(List)
     */
    public void addAction(Action action, int index) {
        mActionsView.addView(inflateAction(action), index);
    }

    /**
     * Removes all action views from this action bar
     */
    public void removeAllActions() {
        mActionsView.removeAllViews();
    }

    /**
     * Remove a action from the action bar.
     * 
     * @param index position of action to remove
     */
    public void removeActionAt(int index) {
        mActionsView.removeViewAt(index);
    }

    /**
     * Remove a action from the action bar.
     * @param action The action to remove
     */
    public void removeAction(Action action) {
        int childCount = mActionsView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = mActionsView.getChildAt(i);
            if (view != null) {
                final Object tag = view.getTag();
                if (tag instanceof Action && tag.equals(action)) {
                    mActionsView.removeView(view);
                }
            }
        }
    }

    /**
     * Returns the number of actions currently registered with the action bar.
     * @return action count
     */
    public int getActionCount() {
        return mActionsView.getChildCount();
    }
    
    // ------------------------------------------------------------------------
    // HELPER INTERFACES AND HELPER CLASSES
    // ------------------------------------------------------------------------

    /**
     * Definition of an action that can be performed along with a icon to show.
     */
    public static abstract class Action {
        /**
         * @deprecated Use {@link #getIcon()}.
         */
        @Deprecated
        public int getDrawable() {
            return 0;
        }
        
        /**
         * Get the icon of the action.
         * 
         * @return Drawable icon.
         */
        public abstract Drawable getIcon();
        
        /**
         * Callback when this action is clicked on.
         * 
         * @param view Action view.
         */
        public abstract void performAction(View view);
    }

    public static abstract class AbstractAction extends Action {
        private final int mDrawable;
        private final Drawable mIcon;

        /**
         * @deprecated Use {@link #AbstractAction(Drawable)}.
         */
        @Deprecated
        public AbstractAction(int drawable) {
            mDrawable = drawable;
            mIcon = null;
        }
        /**
         * 
         * @param icon
         */
        public AbstractAction(Drawable icon) {
            mDrawable = NO_ID;
            mIcon = icon;
        }

        @Override
        public int getDrawable() {
            return mDrawable;
        }
        
        @Override
        public Drawable getIcon() {
            return mIcon;
        }
    }

    public static class IntentAction extends AbstractAction {
        private final Context mContext;
        private final Intent mIntent;

        public IntentAction(Context context, Intent intent, int drawable) {
            //Load drawable from context and call sibling constructor
            this(context, intent, context.getResources().getDrawable(drawable));
        }
        public IntentAction(Context context, Intent intent, Drawable icon) {
            super(icon);
            mContext = context;
            mIntent = intent;
        }

        @Override
        public void performAction(View view) {
            try {
                mContext.startActivity(mIntent); 
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext,
                        mContext.getText(R.string.actionbar_activity_not_found),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    public static abstract class SearchAction extends AbstractAction {
        public SearchAction() {
            super(R.drawable.actionbar_search);
        }
    }
    */
}
