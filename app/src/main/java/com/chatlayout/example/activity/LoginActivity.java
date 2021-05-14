package com.chatlayout.example.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.chatlayout.example.R;
import com.chatlayout.example.utils.CheckUpdateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.tv_version)
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //版本信息
        tvVersion.setText(getStringFormat("VersionName: %s(VersionCode: %d)",
                AppUtils.getAppVersionName(), AppUtils.getAppVersionCode()));

        //检查更新
        new CheckUpdateUtils().check(this);
    }

    @OnClick(R.id.btn_go2chat)
    public void onViewClicked() {
        startActivity(new Intent(activity, MainActivity.class));
    }
}
