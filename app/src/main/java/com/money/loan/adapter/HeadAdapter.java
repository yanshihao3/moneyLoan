package com.money.loan.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.money.loan.R;
import com.money.loan.entity.ProductEntity;

import java.util.List;

/**
 * - @Author:  闫世豪
 * - @Time:  2018/9/6 下午4:05
 * - @Email whynightcode@gmail.com
 */
public class HeadAdapter extends BaseQuickAdapter<ProductEntity, BaseViewHolder> {
    RequestOptions options = new RequestOptions()
            .dontAnimate()
            .centerCrop()
            .transform(new GlideRoundTransform(5))
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public HeadAdapter(@Nullable List<ProductEntity> data) {
        super(R.layout.head_item_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ProductEntity item) {
        helper.setText(R.id.head_item_title, item.getP_name());
        Glide.with(mContext)
                .load(item.getP_logo())
                .apply(options).into((ImageView) helper.getView(R.id.head_item_logo));
    }
}
