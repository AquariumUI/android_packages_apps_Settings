/*
 * Copyright (C) 2026 AquariumUI
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

package com.android.settings.display;

import static androidx.lifecycle.Lifecycle.Event.ON_START;
import static androidx.lifecycle.Lifecycle.Event.ON_STOP;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.provider.Settings;

import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.core.TogglePreferenceController;

/**
 * Preference controller for showing/hiding the carrier name on the lock screen.
 */
public class CarrierTextPreferenceController extends TogglePreferenceController {

    private static final String SETTING_KEY = "show_lockscreen_carrier_text";

    private final ContentObserver mSettingsObserver;
    private final ContentResolver mContentResolver;
    private Preference mPreference;

    public CarrierTextPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
        mContentResolver = context.getContentResolver();
        mSettingsObserver = new ContentObserver(null) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                updateState(mPreference);
            }
        };
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mPreference = screen.findPreference(getPreferenceKey());
    }

    /** Called when activity starts being displayed to user. */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        mContentResolver.registerContentObserver(
                Settings.Secure.getUriFor(SETTING_KEY), false, mSettingsObserver);
    }

    /** Called when activity stops being displayed to user. */
    @OnLifecycleEvent(ON_STOP)
    public void onStop() {
        mContentResolver.unregisterContentObserver(mSettingsObserver);
    }

    @Override
    public boolean isChecked() {
        return Settings.Secure.getInt(mContentResolver, SETTING_KEY, 1) != 0;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        return Settings.Secure.putInt(mContentResolver, SETTING_KEY, isChecked ? 1 : 0);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshSummary(preference);
    }
}
