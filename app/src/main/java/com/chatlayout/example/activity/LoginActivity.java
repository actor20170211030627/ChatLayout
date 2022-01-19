package com.chatlayout.example.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.chatlayout.example.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tvVersion = viewBinding.tvVersion;

        //版本信息
        tvVersion.setText(getStringFormat("VersionName: %s(VersionCode: %d)",
                AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));
    }

    @Override
    public void onViewClicked(View view) {
        startActivity(new Intent(activity, MainActivity.class));
    }
}
