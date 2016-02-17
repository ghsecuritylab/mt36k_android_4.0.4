/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.settings.wifi;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.net.InetAddress;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class AdvancedWifiSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "AdvancedWifiSettings";
    private static final String KEY_MAC_ADDRESS = "mac_address";
    private static final String KEY_CURRENT_IP_ADDRESS = "current_ip_address";
    private static final String KEY_FREQUENCY_BAND = "frequency_band";
    private static final String KEY_NOTIFY_OPEN_NETWORKS = "notify_open_networks";
    private static final String KEY_SLEEP_POLICY = "sleep_policy";
    private static final String KEY_ENABLE_WIFI_WATCHDOG = "wifi_enable_watchdog_service";
	 private static final String KEY_NETMASK_ADDRESS = "netmask";
	 private static final String KEY_GATEWAY_ADDRESS = "gateway";
	 private static final String KEY_DNS1_ADDRESS = "dns1";
	 private static final String KEY_DNS2_ADDRESS = "dns2";
	 private static final String KEY_RSSI = "rssi";
	 private static final String KEY_CHANNELNUM = "channelnum";
	 private static final String KEY_CHANNELFREQ = "channelfreq";

	 private static final String KEY_BSSID = "bssid";
	 private static final String KEY_SSID = "ssid";
	 private static final String KEY_NETWORKID = "networkid";
	 //private static final String KEY_MODE = "mode";
	 private static final String KEY_PAIRWISE_CIPHER = "pairwise_cipher";
	 private static final String KEY_GROUP_CIPHER = "group_cipher";
	 private static final String KEY_KEY_MGMT = "key_mgmt";
	 private static final String KEY_CURRENT_RATE = "current_rate";
	 private static final String KEY_PROTOCOL_CAPS = "protocol_caps";
	 private static final String KEY_WPA_STATE = "wpa_state";

    private WifiManager mWifiManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.wifi_advanced_settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void onResume() {
        super.onResume();
        initPreferences();
        refreshWifiInfo();
    }

    private void initPreferences() {
        CheckBoxPreference notifyOpenNetworks =
            (CheckBoxPreference) findPreference(KEY_NOTIFY_OPEN_NETWORKS);
        notifyOpenNetworks.setChecked(Secure.getInt(getContentResolver(),
                Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON, 0) == 1);
        notifyOpenNetworks.setEnabled(mWifiManager.isWifiEnabled());

        CheckBoxPreference watchdogEnabled =
            (CheckBoxPreference) findPreference(KEY_ENABLE_WIFI_WATCHDOG);
        if (watchdogEnabled != null) {
            watchdogEnabled.setChecked(Secure.getInt(getContentResolver(),
                        Secure.WIFI_WATCHDOG_ON, 1) == 1);

            //TODO: Bring this back after changing watchdog behavior
            getPreferenceScreen().removePreference(watchdogEnabled);
        }

        ListPreference frequencyPref = (ListPreference) findPreference(KEY_FREQUENCY_BAND);

        if (mWifiManager.isDualBandSupported()) {
            frequencyPref.setOnPreferenceChangeListener(this);
            int value = mWifiManager.getFrequencyBand();
            if (value != -1) {
                frequencyPref.setValue(String.valueOf(value));
            } else {
                Log.e(TAG, "Failed to fetch frequency band");
            }
        } else {
            if (frequencyPref != null) {
                // null if it has already been removed before resume
                getPreferenceScreen().removePreference(frequencyPref);
            }
        }

        ListPreference sleepPolicyPref = (ListPreference) findPreference(KEY_SLEEP_POLICY);
        if (sleepPolicyPref != null) {
            if (Utils.isWifiOnly(getActivity())) {
                sleepPolicyPref.setEntries(R.array.wifi_sleep_policy_entries_wifi_only);
            }
            sleepPolicyPref.setOnPreferenceChangeListener(this);
            int value = Settings.System.getInt(getContentResolver(),
                    Settings.System.WIFI_SLEEP_POLICY,
                    Settings.System.WIFI_SLEEP_POLICY_NEVER);
            String stringValue = String.valueOf(value);
            sleepPolicyPref.setValue(stringValue);
            updateSleepPolicySummary(sleepPolicyPref, stringValue);
        }
    }

    private void updateSleepPolicySummary(Preference sleepPolicyPref, String value) {
        if (value != null) {
            String[] values = getResources().getStringArray(R.array.wifi_sleep_policy_values);
            final int summaryArrayResId = Utils.isWifiOnly(getActivity()) ?
                    R.array.wifi_sleep_policy_entries_wifi_only : R.array.wifi_sleep_policy_entries;
            String[] summaries = getResources().getStringArray(summaryArrayResId);
            for (int i = 0; i < values.length; i++) {
                if (value.equals(values[i])) {
                    if (i < summaries.length) {
                        sleepPolicyPref.setSummary(summaries[i]);
                        return;
                    }
                }
            }
        }

        sleepPolicyPref.setSummary("");
        Log.e(TAG, "Invalid sleep policy value: " + value);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
        String key = preference.getKey();

        if (KEY_NOTIFY_OPEN_NETWORKS.equals(key)) {
            Secure.putInt(getContentResolver(),
                    Secure.WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
        } else if (KEY_ENABLE_WIFI_WATCHDOG.equals(key)) {
            Secure.putInt(getContentResolver(),
                    Secure.WIFI_WATCHDOG_ON,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
        } else {
            return super.onPreferenceTreeClick(screen, preference);
        }
        return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();

        if (KEY_FREQUENCY_BAND.equals(key)) {
            try {
                mWifiManager.setFrequencyBand(Integer.parseInt((String) newValue), true);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), R.string.wifi_setting_frequency_band_error,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (KEY_SLEEP_POLICY.equals(key)) {
            try {
                String stringValue = (String) newValue;
                Settings.System.putInt(getContentResolver(), Settings.System.WIFI_SLEEP_POLICY,
                        Integer.parseInt(stringValue));
                updateSleepPolicySummary(preference, stringValue);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), R.string.wifi_setting_sleep_policy_error,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void refreshWifiInfo() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
		DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();

        Preference wifiMacAddressPref = findPreference(KEY_MAC_ADDRESS);
        String macAddress = wifiInfo == null ? null : wifiInfo.getMacAddress();
        wifiMacAddressPref.setSummary(!TextUtils.isEmpty(macAddress) ? macAddress
                : getActivity().getString(R.string.status_unavailable));

        Preference wifiIpAddressPref = findPreference(KEY_CURRENT_IP_ADDRESS);
        String ipAddress = Utils.getWifiIpAddresses(getActivity());
        wifiIpAddressPref.setSummary(ipAddress == null ?
                getActivity().getString(R.string.status_unavailable) : ipAddress);
		Preference wifiGateWayPref = findPreference(KEY_GATEWAY_ADDRESS);
        String netGateWayAddress = dhcpInfo.getGateWayValue();
        wifiGateWayPref.setSummary(netGateWayAddress == null ?
                getActivity().getString(R.string.status_unavailable) : netGateWayAddress);
		
		Preference wifiNetMaskPref = findPreference(KEY_NETMASK_ADDRESS);
        String netMaskAddress = dhcpInfo.getNetMaskValue();
        wifiNetMaskPref.setSummary(netMaskAddress == null ?
                getActivity().getString(R.string.status_unavailable) : netMaskAddress);

		Preference wifiDNS1Pref = findPreference(KEY_DNS1_ADDRESS);
        String netDNS1Address = dhcpInfo.getDNS1Value();
        wifiDNS1Pref.setSummary(netDNS1Address == null ?
                getActivity().getString(R.string.status_unavailable) : netDNS1Address);

		Preference wifiDNS2Pref = findPreference(KEY_DNS2_ADDRESS);
        String netDNS2Address = dhcpInfo.getDNS2Value();
        wifiDNS2Pref.setSummary(netDNS2Address == null ?
                getActivity().getString(R.string.status_unavailable) : netDNS2Address);

		Preference wifiRssiPref = findPreference(KEY_RSSI);
        //String netRssi = wifiInfo.getRssi()+"";
        String netRssi = mWifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4)+"";
        wifiRssiPref.setSummary(netRssi == null ?
                getActivity().getString(R.string.status_unavailable) : netRssi);

		Preference wifiChNumPref = findPreference(KEY_CHANNELNUM);		
        String chNum = wifiInfo.getChannelNumber(wifiInfo.getFrequency())+"";
        wifiChNumPref.setSummary(chNum == null ?
                getActivity().getString(R.string.status_unavailable) : chNum);

		Preference wifiChFreqPref = findPreference(KEY_CHANNELFREQ);
        String chFreq = wifiInfo.getFrequency()+"";
        wifiChFreqPref.setSummary(chFreq == null ?
                getActivity().getString(R.string.status_unavailable) : chFreq);

		/*Preference wifiBssidPref = findPreference(KEY_BSSID);
        String bssid = wifiInfo.getBSSID();
        wifiBssidPref.setSummary(bssid == null ?
                getActivity().getString(R.string.status_unavailable) : bssid);
		Preference wifiSsidPref = findPreference(KEY_SSID);
        String ssid = wifiInfo.getSSID();
        wifiSsidPref.setSummary(ssid == null ?
                getActivity().getString(R.string.status_unavailable) : ssid);
		Preference wifiNetIdPref = findPreference(KEY_NETWORKID);
        String netId = wifiInfo.getNetworkId()+"";
        wifiNetIdPref.setSummary(netId == null ?
                getActivity().getString(R.string.status_unavailable) : netId);
		
		Preference wifiPairwisePref = findPreference(KEY_PAIRWISE_CIPHER);
        String pairwise = wifiInfo.getPairwiseCipher();
        wifiPairwisePref.setSummary(pairwise == null ?
                getActivity().getString(R.string.status_unavailable) : pairwise);
		Preference wifiGroupPref = findPreference(KEY_GROUP_CIPHER);
        String group = wifiInfo.getGroupCipher();
        wifiGroupPref.setSummary(group == null ?
                getActivity().getString(R.string.status_unavailable) : group);
		Preference wifiMgmtPref = findPreference(KEY_KEY_MGMT);
        String mgmt = wifiInfo.getKeyMgmt();
        wifiMgmtPref.setSummary(mgmt == null ?
                getActivity().getString(R.string.status_unavailable) : mgmt);
		Preference wifiCurrentRatePref = findPreference(KEY_CURRENT_RATE);
        String currentRate = wifiInfo.getLinkSpeed()+"";
        wifiCurrentRatePref.setSummary(currentRate == null ?
                getActivity().getString(R.string.status_unavailable) : currentRate);
		Preference wifiCapsPref = findPreference(KEY_PROTOCOL_CAPS);
        String caps = wifiInfo.getGroupCipher();
        wifiCapsPref.setSummary(caps == null ?
                getActivity().getString(R.string.status_unavailable) : caps);
		Preference wifiWapstatePref = findPreference(KEY_WPA_STATE);
        String wapState = wifiInfo.getWpaState();
        wifiWapstatePref.setSummary(wapState == null ?
                getActivity().getString(R.string.status_unavailable) : wapState);*/
    }

}
