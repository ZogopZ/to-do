package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = getApplicationContext();

        final ImageButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GridLayout.LayoutParams myParams = new GridLayout.LayoutParams();
                myParams.width = 50;                    /* rescales check-box's width */
                myParams.height = 50;                   /* rescales check-box's height */
                myParams.setGravity(Gravity.CENTER);    /* aligns views to gridLayout's center */
                myParams.topMargin = 10;
//                final ImageView t1 = new ImageView(context);
                final GridLayout gridLayout = findViewById(R.id.gridLayout);
                final EditText clockView = new EditText(context);
                modifyEditText(context, gridLayout, addButton, clockView);
//                final EditText t2 = new EditText(context);
//                final ClockView clockView = new ClockView(context, null);
//                final ClockView clockView = new ClockView(context, null);
//                clockView.EditClockView(context, gridLayout, addButton);
//                gridLayout.addView(clockView);
            }
        });
//        final Button rmButton = findViewById(R.id.rmButton);
//        rmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (imageViews.size() > 0) {
//                    int indexOfLastView = imageViews.size() - 1;
//                    imageViews.get(indexOfLastView).setVisibility(View.GONE);
//                    imageViews.remove(indexOfLastView);
//                }
//            }
//        });
//

//        ConstraintLayout constraintLayout = findViewById(R.id.rootContainer);
//        if (constraintLayout != null) {
//            for (ImageView ivItem : imageViews) {
//                constraintLayout.addView(ivItem);
//            }
//            constraintLayout.addView(imageViews.get(0));
//        }

        final ImageButton shutdownButton = findViewById(R.id.shutdownButton);
        shutdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        });

    }

    /**
     * The reOrderViews method rearranges the EditText views
     * of the gridLayout in a clock-like manner.
     */
    public void reOrderViews(Context context, GridLayout gridLayout, ImageButton addButton) {
        View childView;
        ArrayList<Integer> clockList = new ArrayList<>();
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            childView = gridLayout.getChildAt(i);
            if (childView instanceof EditText) {
                String clockValue = ((EditText) childView).getText().toString();
                if (!(clockValue.equals(""))) {
                    /*
                     * Check for user's empty set clock value.
                     * Only non-empty values are inserted into the list.
                     */
                    clockList.add(Integer.parseInt(clockValue));
                }
                gridLayout.removeView(childView);   /* remove the processed child View */
                i--;                                /* the index must be recalculated due to the removeView above */
            }
        }
        Collections.sort(clockList);
        for (Integer clockValue: clockList) {
            final EditText clockView = new EditText(context);
            modifyEditText(context, gridLayout, addButton, clockView, clockValue);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void modifyEditText(final Context context, final GridLayout gridLayout, final ImageButton addButton, final EditText clockView) {
        clockView.setId(View.generateViewId());
        clockView.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(1);
        clockView.setFilters(filterArray);
        clockView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        clockView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addButton.requestFocus();
                    reOrderViews(context, gridLayout, addButton);
                    hideKeyboard(context, clockView);
                    return true;
                }
                return false;
            }
        });
        gridLayout.addView(clockView);
    }

    public void modifyEditText(final Context context, final GridLayout gridLayout, final ImageButton addButton, final EditText clockView, Integer clockValue) {
        modifyEditText(context, gridLayout, addButton, clockView);
        clockView.setText(clockValue.toString());
    }

    static final class Resolution {
        private final int width;
        private final int height;

        public Resolution() {
            this.width = Resources.getSystem().getDisplayMetrics().widthPixels;
            this.height = Resources.getSystem().getDisplayMetrics().heightPixels;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }


}