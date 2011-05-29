# Action Bar for Android

![ActionBar for Android](http://markupartist.com/images/actionbar.png "ActionBar for Android")

This projects aims to provide a reusable action bar component for Android 1.6 and up to 3.0. The action bar implements most of the functionality from the native action bar that was introduced in 3.0.

Need icons to your action bar? Olof Brickarp has ported some of [Androids native icons to vector format](http://www.yay.se/2011/02/native-android-icons-in-vector-format/).

## Usage

The action bar is implemented as a [Library Project](http://developer.android.com/guide/developing/eclipse-adt.html#libraryProject) which basically means that there's no need to copy-paste resources into your own project. Simply add the action bar as a reference to any project.

For a full demo of the action bar APIs look at the example app that's included in the repository.

This demonstrates how to use the default navigation mode and how to add actions.

### Adding the Action Bar to Layouts

The action bar is just as any other widget and is added to a layout with the following xml.

``` xml
<com.markupartist.android.widget.ActionBar
    android:id="@+id/actionbar"
    app:title="@string/some_title"
    style="@style/ActionBar"
    />
```

In the above example the title is set using `app:title` which requires that the main application namespace is added first. The title can also be set through code for example:

``` java
ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
actionBar.setTitle(R.string.my_title)
```

If the title is not set the title will be pulled from the application manifest automatically.

### Adding Action Items

Create a menu xml within `res/menu`. The item with id `actionbar_item_home` will automatically be used as the home action.

``` xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@id/actionbar_item_home"
        android:icon="@drawable/ic_actionbar_home"
        android:title="@string/home_actionbar_item_label"
        />
    <item
        android:id="@+id/item_share"
        android:icon="@drawable/ic_actionbar_share"
        android:title="@string/share_actionbar_item_label"
        />
    <item
        android:id="@+id/item_export"
        android:icon="@drawable/ic_actionbar_export"
        android:title="@string/export_actionbar_item_label"
        />
</menu>
```

Find the action bar from the layout and add action items from the menu.

``` java
ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
getMenuInflater().inflate(R.menu.other_actionbar, actionBar.asMenu());
```

Handle actions by implementing `onOptionsItemSelected` in the activity.

``` java
@Override
public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getId()) {
    case R.id.actionbar_item_home:
        // Home action was clicked.
        return true;
    case R.id.item_share:
        // Share action was clicked.
        return true;
    case R.id.item_export:
        // Export action was clicked.
        return true;       
    }
    return super.onOptionsItemSelected(item);
}
```

Actions can be handled and added in several ways, for full example please refer to the example app.

### Customization

The ActionBar is a Libary Project this means that all resources will be merged to the project that referencing the ActionBar project. The values in the main project will always be used before the default values of the ActionBar. This applies to layouts, colors, drawables and and all the other resources that's located in the ActionBar project.

To change the colors and create a blue action bar, create a `colors.xml` within `res/values` of the main project.

``` xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="actionbar_separator">#3A5FCD</color>
    <color name="actionbar_background_start">#3A5FCD</color>
    <color name="actionbar_background_end">#27408B</color>
</resources> 
```

In this example we don't override the values for `actionbar_background_item_pressed_start` and `actionbar_background_item_pressed_end` since we decided to stick with the default values for those.

The same can be done with the drawables, layouts and everything else that is located in the ActionBar project.

## Is it stable?

Yes it is, but there's no guarantees. The api however is still not stable so please check all commits since the last pull. It might also be an good idea to depend on your own fork instead of this component directly. Eventually there will be more controlled releases but until then.

## Are you using this widget?

If you're using the action bar in your app and want to tell me about it just add it to [Apps](https://github.com/johannilsson/android-actionbar/wiki/Apps) wiki page.

## Contributions

Main authors

* Johan Nilsson, <http://markupartist.com>
* Jake Wharton, <https://github.com/JakeWharton>

This widget wouldn't be the same without the excellent contributions by;

* ohhorob, <https://github.com/ohhorob>
* denravonska, <https://github.com/denravonska>
* rpdillon, <https://github.com/rpdillon>
* RickardPettersson, <https://github.com/RickardPettersson>
* Jesse Vincent, <http://blog.fsck.com>
* Gyuri Grell, <http://gyurigrell.com>

### Want to contribute?

GitHub has some great articles on [how to get started with Git and GitHub](http://help.github.com/) and how to [fork a project](http://help.github.com/forking/).

Contributers are recommended to fork the app on GitHub (but don't have too). Create a feature branch, push the branch to git hub, press Pull Request and write a simple explanation.

* One fix per commit
* All code that is contributed must be compliant with [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

## Code Style Guidelines

Contributers are recommended to follow the Android [Code Style Guidelines](http://source.android.com/source/code-style.html) with exception for line length that I try to hold to 80 columns where possible.

In short that is;

* Indentation: 4 spaces, no tabs.
* Line length: 80 columns
* Field names: Non-public, non-static fields start with m.
* Braces: Opening braces don't go on their own line.
* Acronyms are words: Treat acronyms as words in names, yielding XmlHttpRequest, getUrl(), etc.
* Consistency: Look at what's around you!

Have fun and remember we do this in our spare time so don't be too serious :)

## License
Copyright (c) 2010 [Johan Nilsson](http://markupartist.com)

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)


