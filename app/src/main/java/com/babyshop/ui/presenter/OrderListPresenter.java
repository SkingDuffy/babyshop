package com.babyshop.ui.presenter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.babyshop.commom.Url;
import com.babyshop.ui.bean.ResultBean;
import com.babyshop.ui.bean.ResultOrderBean;
import com.babyshop.ui.biz.RefreshBiz;
import com.babyshop.ui.view.IOrderlistView;
import com.babyshop.utils.MyOkHttpUtils;
import com.babyshop.utils.SharedPreferencesUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 2017/4/11.
 */

public class OrderListPresenter {

    IOrderlistView iOrderlistView;
    RefreshBiz refreshBiz;

    public OrderListPresenter(IOrderlistView iOrderlistView) {
        this.iOrderlistView = iOrderlistView;
        refreshBiz = new RefreshBiz();
    }

    public void getOrderList(){
        String url = Url.MY_ORDER + "?userid=" + SharedPreferencesUtil.getInstance().getUserId();
        iOrderlistView.showProgress();
        MyOkHttpUtils.get(url, new MyOkHttpUtils.ResultCallback<ResultOrderBean>() {
            @Override
            public void onSuccess(ResultOrderBean response, int action) {
                iOrderlistView.dismissProgress();
                iOrderlistView.stopRefresh();
                iOrderlistView.getOrderList(response.data);
            }

            @Override
            public void onFailure(Exception e) {
                iOrderlistView.dismissProgress();
                iOrderlistView.stopRefresh();
            }
        });
    }

    public void delOrderList(){
        iOrderlistView.showProgress();
        Map<String, String> params = new HashMap<>();
        params.put("userid", SharedPreferencesUtil.getInstance().getUserId());
        MyOkHttpUtils.post(Url.DEL_ORDER, params, new MyOkHttpUtils.ResultCallback<ResultBean>() {
            @Override
            public void onSuccess(ResultBean response, int action) {
                iOrderlistView.dismissProgress();
                iOrderlistView.showToast(response.message);
                if (response.flag)
                    iOrderlistView.onDelSuccess();
            }

            @Override
            public void onFailure(Exception e) {
                iOrderlistView.dismissProgress();
                iOrderlistView.stopRefresh();
            }
        });
    }

    public void setRefreshColor(SwipeRefreshLayout swipeRefresh){
        refreshBiz.setRefreshColor(swipeRefresh);
    }

    public void setOnRecyclerLoadMoreListener(RecyclerView mRecyclerView, RefreshBiz.OnRecyclerLoadMoreListener l){
        refreshBiz.setOnRecyclerLoadMoreListener(mRecyclerView, l);
    }

}
