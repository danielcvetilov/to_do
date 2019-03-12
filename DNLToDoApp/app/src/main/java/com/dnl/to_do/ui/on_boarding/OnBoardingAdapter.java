package com.dnl.to_do.ui.on_boarding;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dnl.to_do.R;

import java.util.ArrayList;

class OnBoardingAdapter extends PagerAdapter {

    private final Context mContext;
    private final ArrayList<OnBoardItem> onBoardItems;


    public OnBoardingAdapter(Context mContext, ArrayList<OnBoardItem> items) {
        this.mContext = mContext;
        this.onBoardItems = items;
    }

    @Override
    public int getCount() {
        return onBoardItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.pager_item_on_boarding, container, false);

        OnBoardItem item = onBoardItems.get(position);

        ImageView imageIv = itemView.findViewById(R.id.image_iv);
        imageIv.setImageResource(item.imageResId);

        TextView titleTv = itemView.findViewById(R.id.title_tv);
        titleTv.setText(item.titleResId);

        TextView descriptionTv = itemView.findViewById(R.id.description_tv);
        descriptionTv.setText(item.descriptionResId);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}