package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.gridlayout.widget.GridLayout;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = getApplicationContext();
        final GridLayout gridLayout = findViewById(R.id.gridLayout);

        final ImageButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout clockInstance = new LinearLayout(context);
                clockInstance.setOrientation(LinearLayout.HORIZONTAL);
                for (int i = 0; i < 5; i++) {
                    final EditText clockView = new EditText(context);
                    if (i == 2) {
                        modifyEditText(context, clockInstance, addButton, clockView, ':');
                    }
                    else {
                        modifyEditText(context, clockInstance, addButton, clockView);
                    }
                }
                ClockRemovalImageButton removeClockInstanceButton = new ClockRemovalImageButton(context, null);
                removeClockInstanceButton.setProperties();
                clockInstance.addView(removeClockInstanceButton);
                gridLayout.addView(clockInstance);
                addButton.requestFocus();
                showKeyboard(context, addButton);
            }
        });

        final ImageButton shutdownButton = findViewById(R.id.shutdownButton);
        shutdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        });

        final ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: This will start a new settings activity.
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * The reOrderViews method rearranges the EditText views            //todo: Change this comment.
     * of the gridLayout in a clock-like manner.
     */
    public void reOrderViews(Context context, ImageButton addButton) {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        View clockInstance;
        ArrayList<String> clockList = new ArrayList<>();
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            clockInstance = gridLayout.getChildAt(i);
            if (clockInstance instanceof LinearLayout) {
                int clockViews = ((LinearLayout) clockInstance).getChildCount();
                StringBuilder clockValue = new StringBuilder();
                for (int j = 0; j < clockViews; j++) {
                    View clockView = ((LinearLayout) clockInstance).getChildAt(j);
                    if (clockView instanceof EditText) {
                        if (((EditText) clockView).getText().toString().equals("")) {
                            ((EditText) clockView).setText("0");
                            clockValue.append("0");
                        }
                        else {
                            clockValue.append(((EditText) clockView).getText().toString());
                        }
                    }
                }
                clockList.add(clockValue.toString());
            }
        }
        Collections.sort(clockList);
        for (int i = 0; i < clockList.size(); i++) {
            clockInstance = gridLayout.getChildAt(i + 1);
            int clockViews = ((LinearLayout) clockInstance).getChildCount() - 1;
            ((LinearLayout) clockInstance).removeAllViews();
            String clockContent = clockList.get(i);
            for (int j = 0; j < clockViews; j++) {
                EditText clockView = new EditText(context);
                modifyEditText(context, (LinearLayout) clockInstance, addButton, clockView, clockContent.charAt(j));
            }
            ClockRemovalImageButton removeClockInstanceButton = new ClockRemovalImageButton(context, null);
            removeClockInstanceButton.setProperties();
            ((LinearLayout) clockInstance).addView(removeClockInstanceButton);
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    public void modifyEditText(final Context context, final LinearLayout clockInstanceLayout, final ImageButton addButton, final EditText clockView) {
        clockView.setId(View.generateViewId());
        clockView.setInputType(InputType.TYPE_CLASS_NUMBER);
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(1);
        clockView.setFilters(filterArray);
        clockView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        clockView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final LinearLayout parentLayout = ((LinearLayout) clockView.getParent());
                int childCount = parentLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (parentLayout.getChildAt(i) == clockView) {
                        if (i == 4) {
                            break;
                        }
                        else if (i == 1) {
                            parentLayout.getChildAt(i + 2).requestFocus();
                        }
                        else {
                            parentLayout.getChildAt(i + 1).requestFocus();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        clockView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    reOrderViews(context, addButton);
                    addButton.requestFocus();
                    hideKeyboard(context, addButton);
                    return true;
                }
                return false;
            }
        });
        clockInstanceLayout.addView(clockView);
    }

    public void modifyEditText(final Context context, final LinearLayout clockInstanceLayout, final ImageButton addButton, final EditText clockView, final Character clockContent) {
        if (clockContent.equals(':')) {
            clockView.setId(View.generateViewId());
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(1);
            clockView.setFilters(filterArray);
            clockView.setText(clockContent.toString());
            clockView.setEnabled(false);
            clockView.setBackgroundResource(android.R.color.transparent);
        }
        else {
            clockView.setId(View.generateViewId());
            clockView.setInputType(InputType.TYPE_CLASS_NUMBER);
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(1);
            clockView.setFilters(filterArray);
            clockView.setImeOptions(EditorInfo.IME_ACTION_DONE);
            clockView.setText(clockContent.toString());
            clockView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count != 0) {
                        final LinearLayout parentLayout = ((LinearLayout) clockView.getParent());
                        int childCount = parentLayout.getChildCount();
                        for (int i = 0; i < childCount; i++) {
                            if (parentLayout.getChildAt(i) == clockView) {
                                if (i == 4) {
                                    break;
                                } else if (i == 1) {
                                    parentLayout.getChildAt(i + 2).requestFocus();
                                } else {
                                    parentLayout.getChildAt(i + 1).requestFocus();
                                }
                            }
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            clockView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        reOrderViews(context, addButton);
                        addButton.requestFocus();
                        hideKeyboard(context, addButton);
                        return true;
                    }
                    return false;
                }
            });
        }
        clockInstanceLayout.addView(clockView);
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

    public class ClockRemovalImageButton extends androidx.appcompat.widget.AppCompatImageButton {

        public ClockRemovalImageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        /**
         * setProperties() method sets the parameters of the remove button of each clock instance.
         * It is used to remove its parent (LinearLayout) clock instance.
         */
        public void setProperties() {
            this.setImageResource(R.drawable.remove_icon);
            LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(35, 35);
            myP.topMargin = 40;
            this.setLayoutParams(myP);
            this.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout clockInstance = (LinearLayout) view.getParent();
                    removeClockInstance(clockInstance);
                }
            });
        }
    }

    public void removeClockInstance(LinearLayout clockInstance) {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        gridLayout.removeView(clockInstance);
    }

}