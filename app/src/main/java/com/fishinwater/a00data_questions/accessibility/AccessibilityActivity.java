package com.fishinwater.a00data_questions.accessibility;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fishinwater.a00data_questions.R;
import com.fishinwater.a00data_questions.accessibility.service.ControlService;
import com.fishinwater.a00data_questions.accessibility.utils.WechatUtils;

import java.util.Objects;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @author _yuanhao
 * @data 2020.3.31
 */
public class AccessibilityActivity extends AppCompatActivity {

    private Button button;
    private EditText et_name, et_content;
    private Context mContext;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, "FIND_CONTANCT_RESULT")) {
                Toast.makeText(AccessibilityActivity.this, "没有找到联系人", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility);
        mContext = this;

        registerReceiver(broadcastReceiver,new IntentFilter("FIND_CONTANCT_RESULT"));

        init();

    }

    private void init() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.accessibility_service);

        button = (Button) findViewById(R.id.button);
        et_name = (EditText) findViewById(R.id.et_name);
        et_content = (EditText) findViewById(R.id.et_content);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAccessibilitySettingsOn(mContext)) {
                    String name = et_name.getText().toString().trim();
                    String content = et_content.getText().toString().trim();
                    ControlService.isSendSuccess = false;
                    WechatUtils.NAME = name;
                    WechatUtils.CONTENT = content;
                    openWChart();
                } else {
                    Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(accessibleIntent);
                }
            }
        });
    }

    /**
     * 启动方法，规范代码
     * @param context
     */
    public static void actionStart(Activity context) {
        Intent intent = new Intent(context, AccessibilityActivity.class);
        context.startActivity(intent);
    }



    /**
     * 打开微信界面
     */
    private void openWChart() {

        Intent intent = new Intent();
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
        startActivity(intent);

    }


    /**
     * 判断微信助手是否开启
     *
     * @param context
     * @return
     */
    public boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.i("URL", "错误信息为：" + e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }
        return false;
    }

}
