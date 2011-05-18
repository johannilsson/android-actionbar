package com.markupartist.android.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

/**
 * Try to load the logo from the manifest. This class can only be used on API
 * level 9 or higher.
 */
final class LogoLoader {
    /**
     * Try to load the logo from the manifest.
     * 
     * @param context Activity's context for which to try loading the logo.
     * @return Logo if found, otherwise {@code null}.
     */
    public static Drawable loadLogo(Context context) {
        Drawable logo = null;
        
        if (context instanceof Activity) {
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
        }
        
        return logo;
    }
}
