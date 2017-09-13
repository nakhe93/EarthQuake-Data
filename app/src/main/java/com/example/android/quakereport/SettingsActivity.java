package com.example.android.quakereport;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;


/**
 * Created by adhiraj on 8/26/2017.
 */

public class SettingsActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static class EarthquakePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener,Preference.OnPreferenceClickListener{

        DateEditPreference endDate;
        DateEditPreference startDate;
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMagnitude = findPreference(getString(R.string.settings_min_magnitude_key));
            Preference maxMagnitude = findPreference(getString(R.string.settings_max_magnitude_key));
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            startDate = (DateEditPreference)findPreference("start_date");
            endDate = (DateEditPreference)findPreference("end_date");

            endDate.setOnPreferenceClickListener(this);
            startDate.setOnPreferenceClickListener(this);

            bindPreferenceSummaryToValue(startDate);
            bindPreferenceSummaryToValue(endDate);
            bindPreferenceSummaryToValue(orderBy);
            bindPreferenceSummaryToValue(minMagnitude);
            bindPreferenceSummaryToValue(maxMagnitude);


        }

        public boolean onPreferenceChange(Preference preference,Object value){
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            }
            else {
                preference.setSummary(stringValue);
            }
            return true;
        }


        public boolean onPreferenceClick(Preference preference){
            View v = getView();
            int mDay = 0;
            int mMonth = 0;
            int mYear = 0;

            DateEditPreference end = (DateEditPreference) findPreference("end_date");
            DateEditPreference start = (DateEditPreference) findPreference("start_date");
            if(preference == end) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                
                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                Log.d("cal date",year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                //end.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                endDate.setSummary(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                endDate.setDefaultValue(endDate.getSummary().toString());
                                Log.d("last",endDate.getSummary().toString());
                                bindPreferenceSummaryToValueDate(endDate);
                                //int setYear = year;
                                //int setMonth = monthOfYear + 1;
                                //int setDay = dayOfMonth;



                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }

            if(preference == start) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //end.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                startDate.setSummary(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                                startDate.setDefaultValue(startDate.getSummary().toString());

                                bindPreferenceSummaryToValueDate(startDate);
                                //int setYear = year;
                                //int setMonth = monthOfYear + 1;
                                //int setDay = dayOfMonth;



                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference){
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");

            onPreferenceChange(preference, preferenceString);

        }

        private void bindPreferenceSummaryToValueDate(Preference preference){
            preference.setOnPreferenceChangeListener(this);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            //Log.d("before",preferences.getString(preference.getKey(), ""));

            SharedPreferences.Editor editor = preferences.edit();

            //Log.d("new",preferences.getString(preference.getKey(),""));
            editor.putString(preference.getKey(),preference.getSummary().toString());
            editor.apply();
            editor.commit();

            //Log.d("after",preferences.getString(preference.getKey(), ""));

            //String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preference.getSummary().toString());

        }


    }
}

