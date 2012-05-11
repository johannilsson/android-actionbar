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

import java.util.LinkedList;

import com.markupartist.android.widget.actionbar.R;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActionBar extends RelativeLayout implements OnClickListener {

    private LayoutInflater mInflater;
    private RelativeLayout mBarView;
    private ImageView mLogoView;
    private View mBackIndicator;
    //private View mHomeView;
    private TextView mTitleView;
    private LinearLayout mActionsView;
    private ImageButton mHomeBtn;
    private RelativeLayout mHomeLayout;
    private ProgressBar mProgress;

    public ActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mBarView = (RelativeLayout) mInflater.inflate(R.layout.actionbar, null);
        addView(mBarView);

        mLogoView = (ImageView) mBarView.findViewById(R.id.actionbar_home_logo);
        mHomeLayout = (RelativeLayout) mBarView.findViewById(R.id.actionbar_home_bg);
        mHomeBtn = (ImageButton) mBarView.findViewById(R.id.actionbar_home_btn);
        mBackIndicator = mBarView.findViewById(R.id.actionbar_home_is_back);

        mTitleView = (TextView) mBarView.findViewById(R.id.actionbar_title);
        mActionsView = (LinearLayout) mBarView.findViewById(R.id.actionbar_actions);
        
        mProgress = (ProgressBar) mBarView.findViewById(R.id.actionbar_progress);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ActionBar);
        CharSequence title = a.getString(R.styleable.ActionBar_actionbar_title);
        if (title != null) {
            setTitle(title);
        }
        a.recycle();
    }

    public void setHomeAction(Action action) {
        mHomeBtn.setOnClickListener(this);
        mHomeBtn.setTag(action);
        mHomeBtn.setImageResource(action.getDrawable());
        mHomeLayout.setVisibility(View.VISIBLE);
    }

    public void clearHomeAction() {
        mHomeLayout.setVisibility(View.GONE);
    }

    /**
     * Shows the provided logo to the left in the action bar.
     * 
     * This is ment to be used instead of the setHomeAction and does not draw
     * a divider to the left of the provided logo.
     * 
     * @param resId The drawable resource id
     */
    public void setHomeLogo(int resId) {
        // TODO: Add possibility to add an IntentAction as well.
        mLogoView.setImageResource(resId);
        mLogoView.setVisibility(View.VISIBLE);
        mHomeLayout.setVisibility(View.GONE);
    }

    /* Emulating Honeycomb, setdisplayHomeAsUpEnabled takes a boolean
     * and toggles whether the "home" view should have a little triangle
     * indicating "up" */
    public void setDisplayHomeAsUpEnabled(boolean show) {
        mBackIndicator.setVisibility(show? View.VISIBLE : View.GONE);
    }


    public void setTitle(CharSequence title) {
        mTitleView.setText(title);
    }

    public void setTitle(int resid) {
        mTitleView.setText(resid);
    }

    /**
     * Set the enabled state of the progress bar.
     * 
     * @param One of {@link View#VISIBLE}, {@link View#INVISIBLE},
     *   or {@link View#GONE}.
     */
    public void setProgressBarVisibility(int visibility) {
        mProgress.setVisibility(visibility);
    }

    /**
     * Returns the visibility status for the progress bar.
     * 
     * @param One of {@link View#VISIBLE}, {@link View#INVISIBLE},
     *   or {@link View#GONE}.
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
     */
    public void addActions(ActionList actionList) {
        int actions = actionList.size();
        for (int i = 0; i < actions; i++) {
            addAction(actionList.get(i));
        }
    }

    /**
     * Adds a new {@link Action}.
     * @param action the action to add
     */
    public void addAction(Action action) {
        final int index = mActionsView.getChildCount();
        addAction(action, index);
    }

    /**
     * Adds a new {@link Action} at the specified index.
     * @param action the action to add
     * @param index the position at which to add the action
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

    /**
     * Inflates a {@link View} with the given {@link Action}.
     * @param action the action to inflate
     * @return a view
     */
    private View inflateAction(Action action) {
    	return action.onInflateView(this, mInflater.inflate(action.getLayoutResId(), mActionsView, false) );
    }

    /**
     * A {@link LinkedList} that holds a list of {@link Action}s.
     */
    public static class ActionList extends LinkedList<Action> {
    }

    /**
     * Definition of an action that could be performed, along with a icon to
     * show.
     */
    public interface Action {
        public int getDrawable();
        public void performAction(View view);
        
        /** Used to perform any special action once the {@link ActionBar} inflated our view */
        public View onInflateView(ActionBar actionBar, View view);
        
        /** The layout used to inflate a new view for the action */
        public int getLayoutResId();
    }

    public static abstract class AbstractAction implements Action {
        final private int mDrawable;

        public AbstractAction(int drawable) {
            mDrawable = drawable;
        }

        @Override
        public int getDrawable() {
            return mDrawable;
        }
        
        @Override
        public View onInflateView(ActionBar actionBar, View view) {
            ImageButton labelView =
                (ImageButton) view.findViewById(R.id.actionbar_item);
            labelView.setImageResource(this.getDrawable());

            view.setTag(this);
            view.setOnClickListener(actionBar);
            
            return view;
        }
        
        @Override
        public int getLayoutResId() {
        	return R.layout.actionbar_item;
        }
    }

    public static class IntentAction extends AbstractAction {
        private Context mContext;
        private Intent mIntent;

        public IntentAction(Context context, Intent intent, int drawable) {
            super(drawable);
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
    
    /** Interface used to define the listener of an onClickAction */
    public static interface OnClickActionListener {
    	
    	/** Called when an onClickAction gets fired, flag will be the same as the actions flag */
    	public void onClickAction(int flag);
    }
    
    /**
     * Simple action that just performs an onClick when the user taps the action button
     * @author Moritz "Moss" Wundke (b.thax.dcg@gmail.com)
     *
     */
    public static class OnClickAction extends AbstractAction {
    	private OnClickActionListener mClickListener;
    	private int mFlag;
    	
    	public OnClickAction(Context context, OnClickActionListener clickListener, int flag, int drawable) {
            super(drawable);
            mClickListener = clickListener;
            mFlag = flag;
        }
    	
    	@Override
        public void performAction(View view) {
            if (mClickListener != null) {
            	mClickListener.onClickAction(mFlag);
            }
        }
    }
    
    /**
     * Animated action with start/stop methods.
     * @author Moritz "Moss" Wundke (b.thax.dcg@gmail.com)
     *
     */
    public static class AnimatedAction extends AbstractAction {
        private Animation mAnimation;
        private ImageButton mAnimatedView;
        private OnClickListener mClickListener;

        public AnimatedAction(Context context, OnClickListener clickListener, int drawable) {
            super(drawable);
            initAction(context, clickListener, R.anim.rotate);
        }
        
        public AnimatedAction(Context context, OnClickListener clickListener, int drawable, int animResId) {
            super(drawable);
            initAction(context, clickListener, animResId);
        }
        
        private void initAction(Context context, OnClickListener clickListener, int animResId) {
        	mAnimation = AnimationUtils.loadAnimation(context, animResId);
            mAnimation.setRepeatCount(Animation.INFINITE);
            mClickListener = clickListener;
        }
        
        /** Start the infinite animation for this action */
        public void startAnimation() {
        	if ( mAnimatedView != null ) {
        		mAnimatedView.startAnimation(mAnimation);
        	}
        }
        
        /** Stop the infinite animation for this action */
        public void stopAnimation() {
        	if ( mAnimatedView != null ) {
        		mAnimatedView.clearAnimation();
        	}
        }

        @Override
        public View onInflateView(ActionBar actionBar, View view) {
        	mAnimatedView = (ImageButton) view.findViewById(R.id.actionbar_item);
        	mAnimatedView.setImageResource(this.getDrawable());
        	
        	view.setTag(this);
        	mAnimatedView.setTag(this);
        	mAnimatedView.setOnClickListener(actionBar);
            
            return view;
        }
        
        @Override
        public void performAction(View view) {
            if (mClickListener != null) {
            	mClickListener.onClick(view);
            }
        }

    	@Override
    	public int getLayoutResId() {
    		return R.layout.animated_actionbar_item;
    	}
    }
}
