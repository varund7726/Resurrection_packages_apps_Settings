/*
* Copyright (C) 2016 RR
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.android.settings.rr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.SwitchPreference;
import com.android.settings.rr.SeekBarPreference;
import android.provider.Settings;
import com.android.settings.util.Helpers;
import org.cyanogenmod.internal.util.CmLockPatternUtils;
import com.android.settings.Utils;
import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.util.Log;

import com.android.internal.logging.MetricsLogger;
import cyanogenmod.providers.CMSettings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.List;
import java.util.ArrayList;

public class HeaderColors extends SettingsPreferenceFragment  implements Preference.OnPreferenceChangeListener ,Indexable {

 private static final String HEADER_CLOCK_COLOR = "header_clock_color";
 private static final String HEADER_DETAIL_COLOR = "header_detail_color";
 private static final String HEADER_WEATHERONE_COLOR = "header_weatherone_color";
 private static final String HEADER_WEATHERTWO_COLOR = "header_weathertwo_color";
 private static final String QS_HEADER_TEXT_COLOR = "qs_header_text_color";
 private static final String QS_HEADER_COLOR = "qs_header_color";
 private static final String HEADER_BATTERY_COLOR = "header_battery_text_color";
 private static final String HEADER_ALARM_COLOR = "header_alarm_text_color";
 private static final String STATUS_BAR_EXPANDED_HEADER_STROKE = "status_bar_expanded_header_stroke";
 private static final String STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR = "status_bar_expanded_header_stroke_color";
 private static final String STATUS_BAR_EXPANDED_HEADER_STROKE_THICKNESS = "status_bar_expanded_header_stroke_thickness";
 private static final String STATUS_BAR_EXPANDED_HEADER_CORNER_RADIUS = "status_bar_expanded_header_corner_radius";
 private static final String STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_GAP = "status_bar_expanded_header_stroke_dash_gap";
 private static final String STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_WIDTH = "status_bar_expanded_header_stroke_dash_width";
 private static final String PREF_GRADIENT_ORIENTATION_HEADER = "header_background_gradient_orientation";
 private static final String PREF_USE_CENTER_COLOR_HEADER = "header_background_gradient_use_center_color";
 private static final String PREF_HEADER_START_COLOR = "header_background_color_start";
 private static final String PREF_CENTER_COLOR_HEADER = "header_background_color_center";
 private static final String PREF_HEADER_END_COLOR = "header_background_color_end";
private static final String STROKE_CATEGORY = "stroke_settings";

    static final int DEFAULT = 0xffffffff;
    private static final int MENU_RESET = Menu.FIRST;

    static final int DEFAULT_BG = 0xff263238;
    static final int DEFAULT_HEADER_BG = 0xff384248;
    static final int DEFAULT_SECONDARY_TEXT = 0xb3ffffff;
    static final int DEFAULT_TEXT = 0xffffffff;
    private static final int BLACK = 0xff000000;
    private static final int DEFAULT_BACKGROUND_COLOR = 0xff263238;
    private static final int WHITE = 0xffffffff;
    
    private static final int DISABLED  = 0;
    private static final int ACCENT    = 1;
    private static final int CUSTOM = 2;
    
    static final int DEFAULT_QS_STROKE_COLOR = 0xFF80CBC4;
    static final int DEFAULT_HEADER_STROKE_COLOR = 0xFF80CBC4;
    private static final int BACKGROUND_ORIENTATION_T_B = 270;
	

    private ColorPickerPreference mHeaderCLockColor;
    private ColorPickerPreference mHeaderDetailColor;
    private ColorPickerPreference mHeaderWeatheroneColor;
    private ColorPickerPreference mHeaderWeathertwoColor;	
    private ColorPickerPreference mBatteryColor;
    private ColorPickerPreference mAlarmColor;
    private ListPreference mSBEHStroke;
    private ColorPickerPreference mSBEHStrokeColor;
    private SeekBarPreference mSBEHStrokeThickness;
    private SeekBarPreference mSBEHCornerRadius;
    private SeekBarPreference mSBEHStrokeDashGap;
    private SeekBarPreference mSBEHStrokeDashWidth;
    private SwitchPreference mHeaderUseCenterColor;
    private ColorPickerPreference mHeaderStartColor;
    private ColorPickerPreference mHeaderCenterColor;
    private ColorPickerPreference mHeaderEndColor;
    private ListPreference mGradientOrientationHeader;
    private ColorPickerPreference mStartColor;
    private ColorPickerPreference mHeaderTextColor;
    private ColorPickerPreference mHeaderColor;
 

 @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        addPreferencesFromResource(R.xml.rr_header_colors);
        PreferenceScreen prefSet = getPreferenceScreen();
        final ContentResolver resolver = getActivity().getContentResolver();

   	    int intColor;
        String hexColor;

        final int strokeMode = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE, ACCENT);
        boolean notDisabled = strokeMode == ACCENT || strokeMode == CUSTOM;

        mHeaderCLockColor = (ColorPickerPreference) findPreference(HEADER_CLOCK_COLOR);
        mHeaderCLockColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.HEADER_CLOCK_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mHeaderCLockColor.setSummary(hexColor);
        mHeaderCLockColor.setNewPreviewColor(intColor);

        mHeaderDetailColor = (ColorPickerPreference) findPreference(HEADER_DETAIL_COLOR);
        mHeaderDetailColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.HEADER_DETAIL_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mHeaderDetailColor.setSummary(hexColor);
        mHeaderDetailColor.setNewPreviewColor(intColor);

        mHeaderWeatheroneColor = (ColorPickerPreference) findPreference(HEADER_WEATHERONE_COLOR);
        mHeaderWeatheroneColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.HEADER_WEATHERONE_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mHeaderWeatheroneColor.setSummary(hexColor);
        mHeaderWeatheroneColor.setNewPreviewColor(intColor);

        mHeaderWeathertwoColor = (ColorPickerPreference) findPreference(HEADER_WEATHERTWO_COLOR);
        mHeaderWeathertwoColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.HEADER_WEATHERTWO_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mHeaderWeathertwoColor.setSummary(hexColor);
        mHeaderWeathertwoColor.setNewPreviewColor(intColor);

        mHeaderTextColor = (ColorPickerPreference) findPreference(QS_HEADER_TEXT_COLOR);
        mHeaderTextColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.QS_HEADER_TEXT_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mHeaderTextColor.setSummary(hexColor);
        mHeaderTextColor.setNewPreviewColor(intColor);

        mHeaderColor = (ColorPickerPreference) findPreference(QS_HEADER_COLOR);
        mHeaderColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.QS_HEADER_COLOR,DEFAULT_HEADER_BG);
        hexColor = String.format("#%08x", (0xff384248 & intColor));
        mHeaderColor.setSummary(hexColor);
        mHeaderColor.setNewPreviewColor(intColor);

       	mBatteryColor = (ColorPickerPreference) findPreference(HEADER_BATTERY_COLOR);
        mBatteryColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.HEADER_BATTERY_TEXT_COLOR, DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryColor.setSummary(hexColor);
        mBatteryColor.setNewPreviewColor(intColor);

        mAlarmColor = (ColorPickerPreference) findPreference(HEADER_ALARM_COLOR);
        mAlarmColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.HEADER_ALARM_TEXT_COLOR , DEFAULT);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mAlarmColor.setSummary(hexColor);
        mAlarmColor.setNewPreviewColor(intColor);

        mGradientOrientationHeader =
                (ListPreference) findPreference(PREF_GRADIENT_ORIENTATION_HEADER);
        final int orientation = Settings.System.getInt(getContentResolver(),
                Settings.System.HEADER_BACKGROUND_GRADIENT_ORIENTATION,
                BACKGROUND_ORIENTATION_T_B);
         mGradientOrientationHeader.setValue(String.valueOf(orientation));
         mGradientOrientationHeader.setSummary(mGradientOrientationHeader.getEntry());
         mGradientOrientationHeader.setOnPreferenceChangeListener(this);

         mHeaderStartColor =
                (ColorPickerPreference) findPreference(PREF_HEADER_START_COLOR);
         intColor = Settings.System.getInt(getContentResolver(),
                   Settings.System.HEADER_BACKGROUND_COLOR_START, BLACK); 
         mHeaderStartColor.setNewPreviewColor(intColor);
         hexColor = String.format("#%08x", (0xffffffff & intColor));
         mHeaderStartColor.setSummary(hexColor);
         mHeaderStartColor.setOnPreferenceChangeListener(this);
   
         final boolean useheaderCenterColor = Settings.System.getInt(getContentResolver(),
                 Settings.System.HEADER_BACKGROUND_GRADIENT_USE_CENTER_COLOR, 0) == 1;;
   
         mHeaderUseCenterColor = (SwitchPreference) findPreference(PREF_USE_CENTER_COLOR_HEADER);
         mHeaderUseCenterColor.setChecked(useheaderCenterColor);
         mHeaderUseCenterColor.setOnPreferenceChangeListener(this);
   
         mStartColor.setTitle(getResources().getString(R.string.background_start_color_title));
   
         mHeaderCenterColor =
                (ColorPickerPreference) findPreference(PREF_CENTER_COLOR_HEADER);
         intColor = Settings.System.getInt(getContentResolver(),
                Settings.System.HEADER_BACKGROUND_COLOR_CENTER, BLACK); 
         mHeaderCenterColor.setNewPreviewColor(intColor);
         hexColor = String.format("#%08x", (0xffffffff & intColor));
         mHeaderCenterColor.setSummary(hexColor);
         mHeaderCenterColor.setOnPreferenceChangeListener(this);
   
         mHeaderEndColor =
               (ColorPickerPreference) findPreference(PREF_HEADER_END_COLOR);
         intColor = Settings.System.getInt(getContentResolver(),
               Settings.System.HEADER_BACKGROUND_COLOR_END, BLACK); 
         mHeaderEndColor.setNewPreviewColor(intColor);
         hexColor = String.format("#%08x", (0xffffffff & intColor));
         mHeaderEndColor.setSummary(hexColor);
         mHeaderEndColor.setOnPreferenceChangeListener(this);


         final int strokeMode = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE, ACCENT);
	   boolean notDisabled = strokeMode == ACCENT || strokeMode == CUSTOM;
            
         mSBEHStroke = (ListPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE);
         mSBEHStroke.setValue(String.valueOf(strokeMode));
         mSBEHStroke.setSummary(mSBEHStroke.getEntry());
         mSBEHStroke.setOnPreferenceChangeListener(this);

         mSBEHCornerRadius =
                (SeekBarPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_CORNER_RADIUS);
         int cornerRadius = Settings.System.getInt(getContentResolver(),
                Settings.System.STATUS_BAR_EXPANDED_HEADER_CORNER_RADIUS, 2);
         mSBEHCornerRadius.setValue(cornerRadius / 1);
         mSBEHCornerRadius.setOnPreferenceChangeListener(this);

         mSBEHStrokeDashGap =
                 (SeekBarPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_GAP);
         int strokeDashGap = Settings.System.getInt(getContentResolver(),
                 Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_GAP, 10);
         mSBEHStrokeDashGap.setValue(strokeDashGap / 1);
         mSBEHStrokeDashGap.setOnPreferenceChangeListener(this);

         mSBEHStrokeDashWidth =
                 (SeekBarPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_WIDTH);
         int strokeDashWidth = Settings.System.getInt(getContentResolver(),
                 Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_WIDTH, 0);
         mSBEHStrokeDashWidth.setValue(strokeDashWidth / 1);
         mSBEHStrokeDashWidth.setOnPreferenceChangeListener(this);

         mSBEHStrokeThickness =
                (SeekBarPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_THICKNESS);
         int strokeThickness = Settings.System.getInt(getContentResolver(),
                 Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_THICKNESS, 4);
         mSBEHStrokeThickness.setValue(strokeThickness / 1);
         mSBEHStrokeThickness.setOnPreferenceChangeListener(this);

         mSBEHStrokeColor =
                 (ColorPickerPreference) findPreference(STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR);
         intColor = Settings.System.getInt(getContentResolver(),
                 Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR, DEFAULT_HEADER_STROKE_COLOR); 
         mSBEHStrokeColor.setNewPreviewColor(intColor);
         hexColor = String.format("#%08x", (0xffffffff & intColor));
         mSBEHStrokeColor.setSummary(hexColor);
         mSBEHStrokeColor.setOnPreferenceChangeListener(this);

         int headerstroke = Settings.System.getIntForUser(getContentResolver(),
                            Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE, 0,
                            UserHandle.USER_CURRENT);
	   
}


    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.NOTIFICATION_DRAWER_SETTINGS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
	ContentResolver resolver = getActivity().getContentResolver();
	Resources res = getResources();
	  if (preference == mHeaderCLockColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HEADER_CLOCK_COLOR, intHex);
            return true;
         } else if (preference == mHeaderDetailColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HEADER_DETAIL_COLOR, intHex);
            return true;
         } else if (preference == mHeaderWeatheroneColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HEADER_WEATHERONE_COLOR, intHex);
            return true;
         } else if (preference == mHeaderWeathertwoColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HEADER_WEATHERTWO_COLOR, intHex);
            return true;
	  } else if (preference == mHeaderTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.QS_HEADER_TEXT_COLOR, intHex);
            return true;
         } else if (preference == mHeaderColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.QS_HEADER_COLOR, intHex);
            return true;
         }  else if (preference == mBatteryColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HEADER_BATTERY_TEXT_COLOR, intHex);
            return true;
         }  else if (preference == mAlarmColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HEADER_ALARM_TEXT_COLOR, intHex);
            return true;
         }  else if (preference == mHeaderUseCenterColor) {
               boolean value = (Boolean) newValue;
               Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                       Settings.System.HEADER_BACKGROUND_GRADIENT_USE_CENTER_COLOR,
                       value ? 1 : 0);
               return true;
           } else if (preference == mHeaderStartColor) {
               String hex = ColorPickerPreference.convertToARGB(
                       Integer.valueOf(String.valueOf(newValue)));
               int intHex = ColorPickerPreference.convertToColorInt(hex);
               Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                       Settings.System.HEADER_BACKGROUND_COLOR_START, intHex);
               preference.setSummary(hex);
               return true;
           } else if (preference == mHeaderCenterColor) {
               String hex = ColorPickerPreference.convertToARGB(
                       Integer.valueOf(String.valueOf(newValue)));
               int intHex  = ColorPickerPreference.convertToColorInt(hex);
               Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                       Settings.System.HEADER_BACKGROUND_COLOR_CENTER, intHex);
               preference.setSummary(hex);
               return true;
           } else if (preference == mHeaderEndColor) {
               String hex= ColorPickerPreference.convertToARGB(
                       Integer.valueOf(String.valueOf(newValue)));
               int intHex = ColorPickerPreference.convertToColorInt(hex);
               Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                       Settings.System.HEADER_BACKGROUND_COLOR_END, intHex);
               preference.setSummary(hex);
               return true;
           }  else if (preference == mGradientOrientationHeader) {
               int intValue = Integer.valueOf((String) newValue);
               int index = mGradientOrientationHeader.findIndexOfValue((String) newValue);
               Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                       Settings.System.HEADER_BACKGROUND_GRADIENT_ORIENTATION,
                       intValue);
               mGradientOrientationHeader.setSummary(mGradientOrientationHeader.getEntries()[index]);
               return true;
         }  else if (preference == mSBEHStroke) {
	    int Hstroke = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), 
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE,
                    Integer.valueOf((String) newValue));
            mSBEHStroke.setValue(String.valueOf(newValue));
            mSBEHStroke.setSummary(mSBEHStroke.getEntry());
            return true;
        } else if (preference == mSBEHStrokeColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSBEHStrokeThickness) {
            int val = (Integer) newValue;
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_THICKNESS, val * 1);
            return true;
        } else if (preference == mSBEHCornerRadius) {
            int val = (Integer) newValue;
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_CORNER_RADIUS, val * 1);
            return true;
        } else if (preference == mSBEHStrokeDashGap) {
            int val = (Integer) newValue;
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_GAP, val * 1);
            return true;
        } else if (preference == mSBEHStrokeDashWidth) {
            int val = (Integer) newValue;
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_HEADER_STROKE_DASH_WIDTH, val * 1);
            return true;
	}
	return false;
	}


  
            public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                             boolean enabled) {
                     ArrayList<SearchIndexableResource> result =
                             new ArrayList<SearchIndexableResource>();
 
                     SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.rr_header_colors;
                     result.add(sir);
 
                     return result;
                 }
 
                 @Override
                 public List<String> getNonIndexableKeys(Context context) {
                     final List<String> keys = new ArrayList<String>();
                     return keys;
                 }
         };

}
