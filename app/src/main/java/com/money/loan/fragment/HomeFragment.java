package com.money.loan.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.money.loan.activity.DetailsActivity;
import com.money.loan.activity.MainActivity;
import com.money.loan.adapter.HeadAdapter;
import com.money.loan.adapter.LoanAdapter;
import com.money.loan.entity.BannerEntity;
import com.money.loan.entity.ItemType;
import com.money.loan.entity.MultiItemBean;
import com.money.loan.utils.SPUtil;
import com.fondesa.recyclerviewdivider.RecyclerViewDivider;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.money.loan.R;
import com.money.loan.activity.HtmlActivity;
import com.money.loan.activity.LoginActivity;
import com.money.loan.adapter.ProductAdapter;
import com.money.loan.entity.ProductEntity;
import com.money.loan.net.Api;
import com.money.loan.net.ApiService;
import com.money.loan.net.Contacts;
import com.money.loan.net.OnRequestDataListener;
import com.money.loan.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bingoogolapple.bgabanner.BGABanner;


/**
 * @author yanshihao
 */
public class HomeFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.fragment_home_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;
    Unbinder unbinder;


    private BGABanner mBanner;
    private RecyclerView mHeadRV;
    private LoanAdapter mLoanAdapter;

    private HeadAdapter mHeadAdapter;

    private List<MultiItemBean> mBeanList;

    private MainActivity mMainActivity;

    private ProductEntity mProductEntity;

    private KProgressHUD mKProgressHUD;
    private boolean isFirst = true;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mKProgressHUD = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setDimAmount(0.5f);
        initView();
        initData();

    }


    private void start(ProductEntity product) {
        mProductEntity = product;
        String token = SPUtil.getString(getActivity(), Contacts.TOKEN);
        ApiService.apply(product.getId(), token);
        if (TextUtils.isEmpty(token)) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(getContext(), HtmlActivity.class);
            intent.putExtra("html", product.getUrl());
            intent.putExtra("title", product.getP_name());
            startActivity(intent);
        }
    }


    private void initView() {
        mBeanList = new ArrayList<>();
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                initData();
            }
        });

        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(gridLayoutManager);

        mLoanAdapter = new LoanAdapter(null);
        mLoanAdapter.addHeaderView(setHeadView());
        mRecyclerView.setAdapter(mLoanAdapter);
        RecyclerViewDivider.with(getContext()).color(ContextCompat.getColor(getContext(), R.color.divider_background)).size(2).build().addTo(mRecyclerView);

        mLoanAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mProductEntity = mLoanAdapter.getData().get(position);
                start(mProductEntity);
            }
        });

        mLoanAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

        //初始化headview
        mBanner.setAdapter(new BGABanner.Adapter<ImageView, BannerEntity>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, BannerEntity model, int position) {

                RequestOptions options = new RequestOptions()
                        .dontAnimate()
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(getContext())
                        .load(model.getPictrue())
                        .apply(options)
                        .into(itemView);
            }
        });
        mBanner.setDelegate(new BGABanner.Delegate<ImageView, BannerEntity>() {
            @Override
            public void onBannerItemClick(BGABanner banner, ImageView itemView, BannerEntity model, int position) {
                Intent intent = new Intent(getActivity(), HtmlActivity.class);
                intent.putExtra("link", model.getApp());
                intent.putExtra("title", model.getAdvername());
                startActivity(intent);
            }
        });
        mHeadRV.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mHeadAdapter = new HeadAdapter(null);
        mHeadRV.setAdapter(mHeadAdapter);
        mHeadAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mProductEntity = mLoanAdapter.getData().get(position);
                start(mProductEntity);
            }
        });
    }

    private View setHeadView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.head, null);
        mBanner = view.findViewById(R.id.banner);
        mHeadRV = view.findViewById(R.id.head_rv);
        view.findViewById(R.id.head_item_flash).setOnClickListener(this);
        view.findViewById(R.id.head_item_low).setOnClickListener(this);
        view.findViewById(R.id.head_item_news).setOnClickListener(this);
        view.findViewById(R.id.head_item_more_rv).setOnClickListener(this);
        view.findViewById(R.id.head_item_more).setOnClickListener(this);
        return view;
    }

    private void initData() {
        if (isFirst) {
            mKProgressHUD.show();
            isFirst = false;
        }
        ApiService.GET_SERVICE(Api.BANNER, null, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject json) {
                JSONArray jsonArray = json.getJSONArray("data");
                List<BannerEntity> entities = JSON.parseArray(JSON.toJSONString(jsonArray), BannerEntity.class);
                mBanner.setData(entities, null);
            }

            @Override
            public void requestFailure(int code, String msg) {

            }
        });
        ApiService.GET_SERVICE(Api.HOT, null, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject json) {
                JSONArray jsonArray = json.getJSONArray("data");
                List<ProductEntity> entities = JSON.parseArray(JSON.toJSONString(jsonArray), ProductEntity.class);

                mHeadAdapter.setNewData(entities);
            }

            @Override
            public void requestFailure(int code, String msg) {
                ToastUtils.showToast(msg);

            }
        });

        ApiService.GET_SERVICE(Api.LIKE, null, new OnRequestDataListener() {
            @Override
            public void requestSuccess(int code, JSONObject json) {
                JSONArray jsonArray = json.getJSONArray("data");
                List<ProductEntity> entities = JSON.parseArray(JSON.toJSONString(jsonArray), ProductEntity.class);

                mLoanAdapter.setNewData(entities);
                mRefreshLayout.finishRefresh();
                if (mKProgressHUD.isShowing()) {
                    mKProgressHUD.dismiss();
                }
            }

            @Override
            public void requestFailure(int code, String msg) {
                mRefreshLayout.finishRefresh();
                ToastUtils.showToast(msg);
                if (mKProgressHUD.isShowing()) {
                    mKProgressHUD.dismiss();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100 && requestCode == 1) {
            Intent intent = new Intent(getContext(), HtmlActivity.class);
            intent.putExtra("html", mProductEntity.getUrl());
            intent.putExtra("title", mProductEntity.getP_name());
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMainActivity = null;
    }

    public void launcher() {
        ApiService.GET_SERVICE(Api.ANWEI, null, new OnRequestDataListener() {

            @Override
            public void requestSuccess(int code, JSONObject json) {
                JSONObject object = json.getJSONObject("data");
                String url = object.getString("url");
                String name = object.getString("name");
                startActivity(new Intent(getContext(), HtmlActivity.class).putExtra("html", url)
                        .putExtra("title", "帮你借"));
            }

            @Override
            public void requestFailure(int code, String msg) {
                ToastUtils.showToast(msg);
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head_item_flash:
                startActivity(new Intent(getContext(), DetailsActivity.class)
                        .putExtra("title", "闪电放款").putExtra("type", 1));
                break;
            case R.id.head_item_low:
                startActivity(new Intent(getContext(), DetailsActivity.class)
                        .putExtra("title", "超低利率").putExtra("type", 2));
                break;
            case R.id.head_item_news:
                startActivity(new Intent(getContext(), DetailsActivity.class)
                        .putExtra("title", "新品上市" +
                                "").putExtra("type", 3));
                break;
            case R.id.head_item_more:
                startActivity(new Intent(getContext(), DetailsActivity.class)
                        .putExtra("title", "贷款大全").putExtra("type", 4));
                break;
            case R.id.head_item_more_rv:
                mMainActivity.setSelect(1);
                break;
            default:
                break;
        }
    }
}