package com.babyshop.ui.general;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.babyshop.R;
import com.babyshop.commom.BaseActivity;
import com.babyshop.commom.Constant;
import com.babyshop.commom.Url;
import com.babyshop.ui.bean.ResultAddrBean;
import com.babyshop.utils.MyOkHttpUtils;
import com.babyshop.utils.SharedPreferencesUtil;
import com.babyshop.widget.MyDialog;

/**
 * Created by admin on 2017/4/18.
 */

public class AddressActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_addr;
    private SharedPreferencesUtil shared = SharedPreferencesUtil.getInstance();
    private String addr = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        initTitle();
        initView();
    }

    private void initTitle() {
        initTitleBar("地址管理");
        findViewById(R.id.tv_title_right).setOnClickListener(this);
    }

    private void initView() {
        tv_addr = (TextView) findViewById(R.id.tv_address);
        tv_addr.setOnClickListener(this);
        getAddrOnNet();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_title_right:
                String newAddr = tv_addr.getText().toString();
                if (TextUtils.isEmpty(newAddr)){
                    showToast("地址不能为空！");
                    return;
                }
                if (!addr.equals(newAddr)){
                    setAddrOnNet(newAddr);
                }
                finish();
                break;
            case R.id.tv_address:
                modifyAddress();
                break;
        }
    }

    private void getAddrOnNet(){
        setAddrOnNet("");
    }


    private void setAddrOnNet(final String addr){
        String suffix;
        if (TextUtils.isEmpty(addr)){
            suffix = "?userid=" + shared.getUserId();
        } else {
            suffix = "?userid=" + shared.getUserId() + "&address=" + addr;
        }
        showProgress();
        MyOkHttpUtils.get(Url.ADDRESS + suffix, new MyOkHttpUtils.ResultCallback<ResultAddrBean>() {
            @Override
            public void onSuccess(ResultAddrBean response, int action) {
                dismissProgress();
                if (TextUtils.isEmpty(addr)){
                    shared.putString(Constant.U_ADDRESS, response.data);
                } else {
                    if (response.flag){
                        shared.putString(Constant.U_ADDRESS, addr);
                    }
                    showToast(response.message);
                }
                initAddress();
            }

            @Override
            public void onFailure(Exception e) {
                dismissProgress();
            }
        });
    }

    private void initAddress(){
        addr = shared.getString(Constant.U_ADDRESS, "");
        if (TextUtils.isEmpty(addr)){
            tv_addr.setText("点击添加地址");
        } else {
            tv_addr.setText(addr);
        }
    }

    private void modifyAddress(){
        new MyDialog(this).editTextDialog(addr, new MyDialog.onClickEditListener() {
            @Override
            public void editText(String msg) {
                tv_addr.setText(msg);
            }
        });
    }

}
