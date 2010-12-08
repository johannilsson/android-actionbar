# ActionBar for Android

![ActionBar for Android](http://markupartist.com/images/actionbar.png "ActionBar for Android")

## In your layout

    <com.markupartist.android.widget.actionbar.ActionBar
	    android:id="@+id/actionbar"
	    style="@style/ActionBar"
        />

## In your activities

    ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
    actionBar.setTitle("Home");
    actionBar.addAction(new IntentAction(this, new Intent(this, OtherActivity.class), R.drawable.ic_title_export_default));

