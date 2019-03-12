package com.dnl.to_do.ui.on_boarding;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dnl.to_do.R;

import java.util.ArrayList;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OnBoardingActivity extends AppCompatActivity {

    public static final String COMPLETED_ON_BOARDING_TAG = "completed_on_boarding_tag";
    private static final String POSITION_TAG = "position_tag";

    @BindView(R.id.pager_indicator)
    LinearLayout pagerIndicator;

    @BindView(R.id.content_vp)
    ViewPager contentVp;

    @BindView(R.id.get_started_btn)
    View getStartedBtn;

    @BindView(R.id.previous_button)
    View previousBtn;

    @BindView(R.id.forward_button)
    View forwardBtn;

    @BindDrawable(R.drawable.non_selected_item_indicator)
    Drawable nonSelectedItemIndicator;

    @BindDrawable(R.drawable.selected_item_itendicator)
    Drawable selectedItemIndicator;

    private ImageView[] dots;

    private int previousPosition = 0;
    private int initialPosition = 0;

    private final ArrayList<OnBoardItem> onBoardItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        ButterKnife.bind(this);

        loadData();

        if (savedInstanceState != null && savedInstanceState.containsKey(POSITION_TAG)) {
            initialPosition = savedInstanceState.getInt(POSITION_TAG, 0);
            previousPosition = initialPosition;

            if (initialPosition == onBoardItems.size() - 1)
                showAnimation();
        }

        OnBoardingAdapter adapter = new OnBoardingAdapter(this, onBoardItems);
        contentVp.setAdapter(adapter);
        contentVp.setCurrentItem(initialPosition);
        contentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (ImageView dot : dots) {
                    dot.setImageDrawable(nonSelectedItemIndicator);
                }

                dots[position].setImageDrawable(selectedItemIndicator);

                if (position == dots.length - 1 && previousPosition == dots.length - 2)
                    showAnimation();
                else if (position == (dots.length - 2) && previousPosition == dots.length - 1)
                    hideAnimation();

                updateNavigationButtonsVisibility(position);

                previousPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        getStartedBtn.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(COMPLETED_ON_BOARDING_TAG, true);
            editor.apply();

            finish();
        });

        previousBtn.setOnClickListener(view -> processPrevious());
        forwardBtn.setOnClickListener(view -> processForward());

        initPagerIndicator();
        updateNavigationButtonsVisibility(initialPosition);
    }

    private void updateNavigationButtonsVisibility(int position) {
        if (position == 0 || position == dots.length - 1) {
            if (position == 0)
                previousBtn.setVisibility(View.INVISIBLE);

            if (position == dots.length - 1)
                forwardBtn.setVisibility(View.INVISIBLE);
        } else {
            previousBtn.setVisibility(View.VISIBLE);
            forwardBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(POSITION_TAG, previousPosition - 1);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void loadData() {
        OnBoardItem taskGroups = new OnBoardItem();
        taskGroups.imageResId = R.drawable.preview_task_groups;
        taskGroups.titleResId = R.string.on_boarding_organize_your_tasks;
        taskGroups.descriptionResId = R.string.on_boarding_organize_your_tasks_description;

        OnBoardItem tasks = new OnBoardItem();
        tasks.imageResId = R.drawable.preview_tasks;
        tasks.titleResId = R.string.on_boarding_track_your_tasks;
        tasks.descriptionResId = R.string.on_boarding_track_your_tasks_description;

        OnBoardItem widget = new OnBoardItem();
        widget.imageResId = R.drawable.preview_widget;
        widget.titleResId = R.string.on_boarding_widget;
        widget.descriptionResId = R.string.on_boarding_widget_description;

        onBoardItems.add(taskGroups);
        onBoardItems.add(tasks);
        onBoardItems.add(widget);
    }

    private void showAnimation() {
        Animation show = AnimationUtils.loadAnimation(this, R.anim.slide_up_anim);
        getStartedBtn.startAnimation(show);
        show.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                getStartedBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getStartedBtn.clearAnimation();
            }
        });


    }

    private void hideAnimation() {
        Animation hide = AnimationUtils.loadAnimation(this, R.anim.slide_down_anim);
        getStartedBtn.startAnimation(hide);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getStartedBtn.clearAnimation();
                getStartedBtn.setVisibility(View.INVISIBLE);
            }
        });


    }

    private void initPagerIndicator() {
        dots = new ImageView[onBoardItems.size()];

        for (int i = 0; i < onBoardItems.size(); i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(nonSelectedItemIndicator);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(6, 0, 6, 0);

            pagerIndicator.addView(dots[i], params);
        }

        dots[initialPosition].setImageDrawable(selectedItemIndicator);
    }

    private void processPrevious() {
        if (previousPosition == 0)
            return;

        contentVp.setCurrentItem(previousPosition - 1);
    }

    private void processForward() {
        if (previousPosition == dots.length - 1)
            return;

        contentVp.setCurrentItem(previousPosition + 1);
    }
}