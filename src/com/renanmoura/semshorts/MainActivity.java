package com.renanmoura.semshorts;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView status;
    private TextView counter;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(ShortsBlockerService.PREFS, MODE_PRIVATE);
        int pad = (int) (24 * getResources().getDisplayMetrics().density);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);

        status = new TextView(this);
        status.setTextSize(20);
        root.addView(status);

        TextView help = new TextView(this);
        help.setTextSize(15);
        help.setPadding(0, pad / 2, 0, pad / 2);
        help.setText("Para funcionar, ative o serviço \"Sem Shorts\" em Acessibilidade > "
                + "Apps instalados.\n\nSe o Android disser que a configuração é restrita: "
                + "abra Configurações > Aplicativos > Sem Shorts, toque nos três pontinhos "
                + "no canto superior e escolha \"Permitir configurações restritas\". "
                + "Depois volte aqui.");
        root.addView(help);

        Button open = new Button(this);
        open.setText("Abrir configurações de acessibilidade");
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
        root.addView(open);

        Switch enabled = new Switch(this);
        enabled.setText("Bloqueio ativo");
        enabled.setTextSize(17);
        enabled.setPadding(0, pad, 0, pad / 2);
        enabled.setChecked(prefs.getBoolean(ShortsBlockerService.KEY_ENABLED, true));
        enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton b, boolean checked) {
                prefs.edit().putBoolean(ShortsBlockerService.KEY_ENABLED, checked).apply();
            }
        });
        root.addView(enabled);

        counter = new TextView(this);
        counter.setTextSize(15);
        root.addView(counter);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(root);
        setContentView(scroll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status.setText(isServiceOn()
                ? "✅ Serviço ativado"
                : "⚠️ Serviço desativado");
        counter.setText("Shorts bloqueados até agora: "
                + prefs.getInt(ShortsBlockerService.KEY_COUNT, 0));
    }

    private boolean isServiceOn() {
        String on = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (on == null) return false;
        ComponentName me = new ComponentName(this, ShortsBlockerService.class);
        for (String s : on.split(":")) {
            ComponentName c = ComponentName.unflattenFromString(s);
            if (me.equals(c)) return true;
        }
        return false;
    }
}
