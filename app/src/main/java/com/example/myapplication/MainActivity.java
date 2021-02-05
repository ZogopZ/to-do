package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import java.io.File;

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
                final EditText t2 = new EditText(context);
                t2.setInputType(InputType.TYPE_CLASS_NUMBER);
                int maxLength = 1;
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(maxLength);
                t2.setFilters(filterArray);
                t2.setImeOptions(EditorInfo.IME_ACTION_DONE);
                t2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            addButton.requestFocus();
                            reOrderViews(gridLayout);
                            hideKeyboard(context, t2);
                            return true;
                        }
                        return false;
                    }
                });
//                t2.setLayoutParams(myParams);
//                t1.setBackgroundResource(R.drawable.checkbox_icon);
//                t1.setLayoutParams(myParams);           /* applies layoutParams to view */
//                gridLayout.addView(t1);
                gridLayout.addView(t2);
//                t2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });


//                final ImageView imageView = new ImageView(context);
//                imageViews.add(imageView);
//                ImageView currentView = imageViews.get(imageViews.size() - 1);
//                currentView.setLayoutParams(new ConstraintLayout.LayoutParams(300, (imageViews.size() * 300 + 300)));
//                currentView.setImageResource(R.drawable.user_to_do);
//                constraintLayout.addView(currentView);
//                currentView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        System.out.println("Clicked on imageView " + v);
//                        EditText ed = new EditText(context);
//                        ed.setInputType(InputType.TYPE_CLASS_TEXT);
//                        ed.setLayoutParams(new ConstraintLayout.LayoutParams(600, (imageViews.size() * 300 + 300)));
//                        System.out.println("Theoretically something was added.");
//
//                    }
//                });
//                System.out.println("Adding to-do, size of list = " + imageViews.size());
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

    public void reOrderViews(GridLayout gridLayout) {
        View childView = new View(this);
//        Array
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            childView = gridLayout.getChildAt(i);
            if (childView instanceof EditText) {
//                childView.getId()
                System.out.println(((EditText) childView).getText().toString());
            }
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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