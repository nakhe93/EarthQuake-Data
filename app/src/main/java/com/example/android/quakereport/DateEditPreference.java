package com.example.android.quakereport;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Created by adhiraj on 9/12/2017.
 */

public class DateEditPreference extends Preference {
    public DateEditPreference(Context context){
        super(context);
    }

    public DateEditPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public DateEditPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


}
