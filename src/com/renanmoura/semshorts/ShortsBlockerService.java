package com.renanmoura.semshorts;

import android.accessibilityservice.AccessibilityService;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

/** Sai do player de Shorts do YouTube assim que ele aparece na tela. */
public class ShortsBlockerService extends AccessibilityService {
    static final String PREFS = "semshorts";
    static final String KEY_ENABLED = "enabled";
    static final String KEY_COUNT = "count";
    static final String YOUTUBE = "com.google.android.youtube";

    /** IDs de view que só existem quando o player de Shorts está aberto. */
    private static final String[] SHORTS_VIEW_IDS = {
            "reel_recycler",
            "reel_player_page_container",
            "reel_watch_fragment_root",
    };

    private static final long COOLDOWN_MS = 700;
    private static final long TOAST_INTERVAL_MS = 3000;

    private long lastBack;
    private long lastToast;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null) return;
        if (!YOUTUBE.contentEquals(event.getPackageName())) return;

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (!prefs.getBoolean(KEY_ENABLED, true)) return;

        long now = SystemClock.uptimeMillis();
        if (now - lastBack < COOLDOWN_MS) return;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;
        if (root.getPackageName() == null || !YOUTUBE.contentEquals(root.getPackageName())) return;

        if (isShortsOpen(root)) {
            lastBack = now;
            performGlobalAction(GLOBAL_ACTION_BACK);
            prefs.edit().putInt(KEY_COUNT, prefs.getInt(KEY_COUNT, 0) + 1).apply();
            if (now - lastToast > TOAST_INTERVAL_MS) {
                lastToast = now;
                Toast.makeText(this, "Shorts bloqueado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static boolean isShortsOpen(AccessibilityNodeInfo root) {
        for (String id : SHORTS_VIEW_IDS) {
            List<AccessibilityNodeInfo> nodes =
                    root.findAccessibilityNodeInfosByViewId(YOUTUBE + ":id/" + id);
            if (nodes == null) continue;
            for (AccessibilityNodeInfo node : nodes) {
                if (node != null && node.isVisibleToUser()) return true;
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {
    }
}
