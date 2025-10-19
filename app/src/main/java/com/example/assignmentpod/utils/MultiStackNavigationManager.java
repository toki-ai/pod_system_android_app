package com.example.assignmentpod.utils;

import android.os.Bundle;
import android.util.Log;

import androidx.navigation.NavController;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to manage navigation state across multiple navigation stacks
 */
public class MultiStackNavigationManager {
    private static final String TAG = "MultiStackNavManager";
    private static final String KEY_CURRENT_TAB = "current_tab";
    private static final String KEY_NAV_STATES = "nav_states";
    
    private Map<Integer, Bundle> savedStates = new HashMap<>();
    private int currentTabId;
    
    public MultiStackNavigationManager() {
    }
    
    /**
     * Save the current navigation state for a tab
     */
    public void saveNavigationState(int tabId, NavController navController) {
        try {
            if (navController != null) {
                Bundle state = new Bundle();
                navController.saveState();
                savedStates.put(tabId, state);
                Log.d(TAG, "Saved navigation state for tab: " + tabId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving navigation state for tab: " + tabId, e);
        }
    }
    
    /**
     * Restore navigation state for a tab
     */
    public void restoreNavigationState(int tabId, NavController navController) {
        try {
            Bundle state = savedStates.get(tabId);
            if (state != null && navController != null) {
                navController.restoreState(state);
                Log.d(TAG, "Restored navigation state for tab: " + tabId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error restoring navigation state for tab: " + tabId, e);
        }
    }
    
    /**
     * Set current active tab
     */
    public void setCurrentTab(int tabId) {
        this.currentTabId = tabId;
    }
    
    /**
     * Get current active tab
     */
    public int getCurrentTab() {
        return currentTabId;
    }
    
    /**
     * Save all navigation states to bundle
     */
    public void saveToBundle(Bundle outState) {
        outState.putInt(KEY_CURRENT_TAB, currentTabId);
        outState.putSerializable(KEY_NAV_STATES, (HashMap<Integer, Bundle>) savedStates);
    }
    
    /**
     * Restore all navigation states from bundle
     */
    @SuppressWarnings("unchecked")
    public void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentTabId = savedInstanceState.getInt(KEY_CURRENT_TAB, 0);
            Object states = savedInstanceState.getSerializable(KEY_NAV_STATES);
            if (states instanceof HashMap) {
                savedStates = (HashMap<Integer, Bundle>) states;
            }
        }
    }
    
    /**
     * Clear all saved states
     */
    public void clearStates() {
        savedStates.clear();
        currentTabId = 0;
    }
}