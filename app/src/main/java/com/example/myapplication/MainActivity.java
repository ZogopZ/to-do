package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
                final LinearLayout todoInstance = new TodoInstance(context, null);
                gridLayout.addView(todoInstance);
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
                todoInstance.addView(clockInstance);

                TodoRemove todoRemove = new TodoRemove(context);
                todoInstance.addView(todoRemove);

                TodoText todoText = new TodoText(context);
                todoInstance.addView(todoText);

                EditText firstClockView = (EditText) clockInstance.getChildAt(0);
                firstClockView.requestFocus();
                showKeyboard(context, firstClockView);
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
        ArrayList<String> clockList = new ArrayList<>();
        for (int i = 1; i < gridLayout.getChildCount(); i++) {
            LinearLayout todoInstance = (LinearLayout) gridLayout.getChildAt(i);
            LinearLayout clockInstance = (LinearLayout) todoInstance.getChildAt(0);
            StringBuilder clockValue = new StringBuilder();
            for (int j = 0; j < 5; j++) {
                EditText clockView = (EditText) clockInstance.getChildAt(j);
                if (clockView.getText().toString().equals("")) {
                    clockView.setText("0");
                    clockValue.append("0");
                } else {
                    clockValue.append(clockView.getText().toString());
                }
            }
            clockValue.append(((EditText) todoInstance.getChildAt(2)).getText().toString());
            clockList.add(clockValue.toString());
        }
        System.out.println("TSVIS PRINT " + clockList);
        Collections.sort(clockList);
        System.out.println(clockList);
        for (int i = 0; i < clockList.size(); i++) {
            LinearLayout todoInstance = (LinearLayout) gridLayout.getChildAt(i + 1);
            LinearLayout clockInstance = (LinearLayout) todoInstance.getChildAt(0);
            clockInstance.removeAllViews();
            String clockContent = clockList.get(i);
            for (int j = 0; j < 5; j++) {
                EditText clockView = new EditText(context);
                modifyEditText(context, clockInstance, addButton, clockView, clockContent.charAt(j));
            }
            TodoRemove todoRemove = new TodoRemove(context);
            todoInstance.addView(todoRemove);

            String todoText = clockContent.substring(5);
            ((EditText) todoInstance.getChildAt(2)).setText(todoText);
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
                final LinearLayout clockInstance = ((LinearLayout) clockView.getParent());
                final LinearLayout todoInstance = ((LinearLayout) clockInstance.getParent());
                int childCount = clockInstance.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (clockInstance.getChildAt(i) == clockView) {
                        if (i == 4) {
                            todoInstance.getChildAt(2).requestFocus();
                        }
                        else if (i == 1) {
                            clockInstance.getChildAt(i + 2).requestFocus();
                        }
                        else {
                            clockInstance.getChildAt(i + 1).requestFocus();
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
                    clockView.clearFocus();
                    hideKeyboard(context, clockView);
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
                        clockView.clearFocus();
                        hideKeyboard(context, clockView);
                        return true;
                    }
                    return false;
                }
            });
        }
        clockInstanceLayout.addView(clockView);
    }

    public class TodoRemove extends androidx.appcompat.widget.AppCompatImageButton {
        private final TodoRemove todoRemove;

        public TodoRemove(Context context) {
            super(context);
            this.todoRemove = this;
            todoRemove.setProperties();
        }

        /**
         * setProperties() method sets the parameters of the remove button of each to-do instance.
         * onClick it removes its parent (LinearLayout to-do instance).
         */
        public void setProperties() {
            todoRemove.setImageResource(R.drawable.remove_icon);
            LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(35, 35);
            myP.topMargin = 40;
            todoRemove.setLayoutParams(myP);
            todoRemove.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout todoInstance = (LinearLayout) view.getParent();
                    removeTodoInstance(todoInstance);
                }
            });
        }
    }

    public void removeTodoInstance(LinearLayout todoInstance) {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        gridLayout.removeView(todoInstance);
    }

    public static class TodoInstance extends LinearLayout {

        public TodoInstance(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            this.setProperties();
        }

        public void setProperties() {
            this.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
            this.setLayoutParams(myP);
        }
    }

    public class TodoText extends androidx.appcompat.widget.AppCompatEditText {
        private final TodoText todoText;

        public TodoText(@NonNull Context context) {
            super(context);
            this.todoText = this;
            todoText.setProperties();
        }

        private void setProperties() {
            LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            myP.leftMargin = 50;
            todoText.setLayoutParams(myP);
            todoText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            todoText.setMaxLines(1);
            todoText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            todoText.setFocusableInTouchMode(true);
            todoText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        todoText.setAlpha(0.5f);
                        ImageButton addButton = findViewById(R.id.addButton);
                        reOrderViews(getApplicationContext(), addButton);
                        todoText.clearFocus();
                        hideKeyboard(getApplicationContext(), todoText);
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}

//static final class Resolution {
//    private final int width;
//    private final int height;
//
//    public Resolution() {
//        this.width = Resources.getSystem().getDisplayMetrics().widthPixels;
//        this.height = Resources.getSystem().getDisplayMetrics().heightPixels;
//    }
//
//    public int getWidth() {
//        return width;
//    }
//
//    public int getHeight() {
//        return height;
//    }
//}