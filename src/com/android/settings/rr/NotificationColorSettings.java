/* 
 * Copyright (C) 2014 RR
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

package com.android.settings.rr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.settings.rr.SeekBarPreference;
import com.android.settings.rr.SeekBarPreferenceCham;

import android.provider.SearchIndexableResource;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.search.Indexable;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.MetricsLogger;

import java.util.ArrayList;
import java.util.List;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class NotificationColorSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener ,Indexable {

    private static final String PREF_CAT_COLORS =
            "notification_cat_colors";
    private static final String PREF_QS_PANEL_LOGO = "qs_panel_logo";
    private static final String PREF_QS_PANEL_LOGO_COLOR = "qs_panel_logo_color";
    private static final String PREF_QS_PANEL_LOGO_ALPHA = "qs_panel_logo_alpha";

    static final int DEFAULT_QS_PANEL_LOGO_COLOR = 0xFF80CBC4;
    private static final int BACKGROUND_ORIENTATION_T_B = 270;

    private static final int RR_BLUE_GREY = 0xff1b1f23;
    private static final int SYSTEMUI_SECONDARY = 0xff384248;
    private static final int WHITE = 0xffffffff;
    private static final int BLACK = 0xff000000;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;
    private static final int TRANSLUCENT_HOLO_BLUE_LIGHT = 0x4d33b5e5;
    private static final int TRANSLUCENT_WHITE = 0x4dffffff;
    private static final int MATERIAL_GREEN = 0xff009688;
    private static final int MATERIAL_BLUE_GREY = 0xff37474f;
    
    static final int DEFAULT_VOLUME_DIALOG_STROKE_COLOR = 0xFF80CBC4;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private static final int DISABLED  = 0;
    private static final int ACCENT    = 1;
    private static final int CUSTOM = 2;

    private ListPreference mQSPanelLogo;
    private ColorPickerPreference mQSPanelLogoColor;
    private SeekBarPreferenceCham mQSPanelLogoAlpha;
    private ContentResolver mResolver;

    @Override
    protected int getMetricsCategory() {
        return MetricsLogger.APPLICATION;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.notification_colors);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

            // QS panel RR logo
             mQSPanelLogo =
                     (ListPreference) findPreference(PREF_QS_PANEL_LOGO);
             int qSPanelLogo = Settings.System.getIntForUser(mResolver,
                             Settings.System.QS_PANEL_LOGO, 0,
                             UserHandle.USER_CURRENT);
             mQSPanelLogo.setValue(String.valueOf(qSPanelLogo));
             mQSPanelLogo.setSummary(mQSPanelLogo.getEntry());
             mQSPanelLogo.setOnPreferenceChangeListener(this);
 
             // QS panel RR logo color
             mQSPanelLogoColor =
                     (ColorPickerPreference) findPreference(PREF_QS_PANEL_LOGO_COLOR);
             mQSPanelLogoColor.setOnPreferenceChangeListener(this);
             int qSPanelLogoColor = Settings.System.getInt(mResolver,
                     Settings.System.QS_PANEL_LOGO_COLOR, DEFAULT_QS_PANEL_LOGO_COLOR);
             String qSHexLogoColor = String.format("#%08x", (0xFF80CBC4 & qSPanelLogoColor));
             mQSPanelLogoColor.setSummary(qSHexLogoColor);
             mQSPanelLogoColor.setNewPreviewColor(qSPanelLogoColor);
 
             // QS panel RR logo alpha
             mQSPanelLogoAlpha =
                     (SeekBarPreferenceCham) findPreference(PREF_QS_PANEL_LOGO_ALPHA);
             int qSPanelLogoAlpha = Settings.System.getInt(mResolver,
                     Settings.System.QS_PANEL_LOGO_ALPHA, 51);
             mQSPanelLogoAlpha.setValue(qSPanelLogoAlpha / 1);
             mQSPanelLogoAlpha.setOnPreferenceChangeListener(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_action_reset) // use the KitKat backup icon
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;
        String hex;
        int intHex;


         if (preference == mQSPanelLogo) {
                 int qSPanelLogo = Integer.parseInt((String) newValue);
                 int index = mQSPanelLogo.findIndexOfValue((String) newValue);
                 Settings.System.putIntForUser(mResolver, Settings.System.
                         QS_PANEL_LOGO, qSPanelLogo, UserHandle.USER_CURRENT);
                 mQSPanelLogo.setSummary(mQSPanelLogo.getEntries()[index]);
                 QSPanelLogoSettingsDisabler(qSPanelLogo);
                 return true;
        } else if (preference == mQSPanelLogoColor) {
                 hex = ColorPickerPreference.convertToARGB(
                         Integer.valueOf(String.valueOf(newValue)));
                 preference.setSummary(hex);
                 intHex = ColorPickerPreference.convertToColorInt(hex);
                 Settings.System.putInt(mResolver,
                         Settings.System.QS_PANEL_LOGO_COLOR, intHex);
                 return true;
        } else if (preference == mQSPanelLogoAlpha) {
                 int val = (Integer) newValue;
                 Settings.System.putInt(mResolver,
                        Settings.System.QS_PANEL_LOGO_ALPHA, val * 1);
                return true;
	  } 
        return false;
    }
    
       
       private void QSPanelLogoSettingsDisabler(int qSPanelLogo) {
             if (qSPanelLogo == 0) {
                 mQSPanelLogoColor.setEnabled(false);
                 mQSPanelLogoAlpha.setEnabled(false);
             } else if (qSPanelLogo == 1) {
                 mQSPanelLogoColor.setEnabled(false);
                 mQSPanelLogoAlpha.setEnabled(true);
             } else {
                 mQSPanelLogoColor.setEnabled(true);
                 mQSPanelLogoAlpha.setEnabled(true);
             }
         }

       private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        NotificationColorSettings getOwner() {
            return (NotificationColorSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_values_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_rr,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }
    
        public static final Indexable.SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(Context context,
                                                                             boolean enabled) {
                     ArrayList<SearchIndexableResource> result =
                             new ArrayList<SearchIndexableResource>();
 
                     SearchIndexableResource sir = new SearchIndexableResource(context);
                    sir.xmlResId = R.xml.notification_colors;
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
