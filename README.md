# ActionBar for Android

![ActionBar for Android](http://markupartist.com/images/actionbar.png "ActionBar for Android")

## In your layout

    <com.markupartist.android.widget.ActionBar
	    android:id="@+id/actionbar"
	    style="@style/ActionBar"
        />

## In your activities

    ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
    actionBar.setTitle("Other");
    actionBar.setHomeAction(new IntentAction(this, HomeActivity.createIntent(this), R.drawable.ic_title_home_default));
    actionBar.addAction(new IntentAction(this, createShareIntent(), R.drawable.ic_title_share_default));
    actionBar.addAction(new ExampleAction());

### Custom actions

ActionBar comes with a convenient IntentAction that makes it easy to create action out of IntentS. To create custom ActionS simply implement the Action interface and build your own like the Toast example below.

    private class ExampleAction implements Action {

        @Override
        public int getDrawable() {
            return R.drawable.ic_title_export_default;
        }

        @Override
        public void performAction() {
            Toast.makeText(OtherActivity.this,
                    "Example action", Toast.LENGTH_SHORT).show();
        }

    }

