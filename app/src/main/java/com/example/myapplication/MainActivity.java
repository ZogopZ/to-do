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

                final ClockInstance clockInstance = new ClockInstance(context);
                todoInstance.addView(clockInstance);

                TodoText todoText = new TodoText(context);
                todoInstance.addView(todoText);

                TodoRemove todoRemove = new TodoRemove(context);
                todoInstance.addView(todoRemove);

                clockInstance.getChildAt(0).requestFocus();
                showKeyboard(context, clockInstance.getChildAt(0));
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
     * The reOrderViews method rearranges the to-do instances of the
     * gridLayout according to time ascending. The actual reordering
     * occurs when the user presses IME_ACTION_DONE in a to-do's
     * instance text view referenced here are todoText.
     */
    public void reOrderViews() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        ArrayList<TodoInstance> indexing = new ArrayList<>();
        for (int i = 1; i < gridLayout.getChildCount(); i++) {
            TodoInstance todoInstance = (TodoInstance) gridLayout.getChildAt(i);
            indexing.add(todoInstance);
        }
        Collections.sort(indexing);                     // Sort the to-do instances time ascending.
        for (TodoInstance todoInstance : indexing) {
            gridLayout.removeView(todoInstance);        // Remove unsorted to-do instances from layout.
            gridLayout.addView(todoInstance);           // Apply sorted to-do instances to layout.
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

    public class TodoRemove extends androidx.appcompat.widget.AppCompatImageButton {
        private final TodoRemove todoRemove;

        public TodoRemove(Context context) {
            super(context);
            this.todoRemove = this;
            todoRemove.setProperties();
        }

        /**
         * setProperties() method sets the parameters of the remove
         * button of each to-do instance.
         * onClick it removes its parent (LinearLayout to-do instance).
         */
        public void setProperties() {
            todoRemove.setImageResource(R.drawable.remove_icon);
            LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(25, 25);
            myP.leftMargin = 20;
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

    public class TodoInstance extends LinearLayout implements Comparable<TodoInstance> {
        private final TodoInstance todoInstance;

        public TodoInstance(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            this.todoInstance = this;
            todoInstance.setProperties();
        }

        public void setProperties() {
            todoInstance.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
            todoInstance.setLayoutParams(myP);
        }

        @Override
        public int compareTo(TodoInstance todoRename) {
            ClockInstance clockInstance1 = (ClockInstance) todoRename.getChildAt(0);
            StringBuilder clock1 = new StringBuilder();
            ClockInstance.ClockView clockView1;
            for (int i = 0; i < 5; i++) {
                if (i != 2) {
                    clockView1 = (ClockInstance.ClockView) clockInstance1.getChildAt(i);
                    clock1.append(Objects.requireNonNull(clockView1.getText()).toString());
                }
            }
            int comparator1 = Integer.parseInt(clock1.toString());

            ClockInstance clockInstance2 = (ClockInstance) todoInstance.getChildAt(0);
            StringBuilder clock2 = new StringBuilder();
            ClockInstance.ClockView clockView2;
            for (int i = 0; i < 5; i++) {
                if (i != 2) {
                    clockView2 = (ClockInstance.ClockView) clockInstance2.getChildAt(i);
                    clock2.append(Objects.requireNonNull(clockView2.getText()).toString());
                }
            }
            int comparator2 = Integer.parseInt(clock2.toString());
            System.out.println("@@@@ " + comparator2 + " @@@ " + comparator1);
            return comparator2 - comparator1;
        }
    }

    public class ClockInstance extends LinearLayout {
        private final ClockInstance clockInstance;

        public ClockInstance(Context context) {
            super(context);
            this.clockInstance = this;
            clockInstance.setProperties();
            clockInstance.setChildren();
        }

        public ClockInstance(Context context, String clockContent) {
            super(context);
            this.clockInstance = this;
            clockInstance.setProperties();
            clockInstance.setChildren(clockContent);
        }

        public void setProperties() {
            clockInstance.setGravity(Gravity.CENTER_HORIZONTAL);
            clockInstance.setOrientation(LinearLayout.HORIZONTAL);
        }

        public void setChildren() {
            for (int i = 0; i < 5; i++) {
                ClockView clockView = new ClockView(getContext().getApplicationContext());
                if (!(i == 2)) {
                    clockView.setProperties();
                }
                else {
                    clockView.setProperties(':');
                }
                clockInstance.addView(clockView);
            }
        }

        public void setChildren(String clockContent) {
            for (int i = 0; i < 5; i++) {
                char clockValue = clockContent.charAt(i);
                ClockView clockView = new ClockView(getContext().getApplicationContext());
                if (!(i == 2)) {
                    clockView.setProperties();
                    clockView.setText(Character.toString(clockValue));
                }
                else {
                    clockView.setProperties(':');
                }
                clockInstance.addView(clockView);
            }
        }

        public class ClockView extends androidx.appcompat.widget.AppCompatEditText{
            private final ClockView clockView;

            public ClockView(Context context) {
                super(context);
                this.clockView = this;
            }

            public void setProperties() {
                clockView.setId(View.generateViewId());
                clockView.setInputType(InputType.TYPE_CLASS_NUMBER);
                clockView.setFocusableInTouchMode(true);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(1);
                clockView.setFilters(filterArray);
                clockView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(25, 50);
                clockView.setLayoutParams(myP);
                clockView.setBackgroundResource(android.R.color.background_light);
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
                                    todoInstance.getChildAt(1).requestFocus();
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
                        return actionId == EditorInfo.IME_ACTION_DONE;
                    }
                });
            }

            public void setProperties(Character colon) {
                clockView.setId(View.generateViewId());
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(1);
                clockView.setFilters(filterArray);
                clockView.setText(colon.toString());
                clockView.setEnabled(false);
                LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(10, 50);
                clockView.setLayoutParams(myP);
                clockView.setBackgroundResource(android.R.color.transparent);
            }
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
            todoText.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            myP.leftMargin = 50;
            todoText.setLayoutParams(myP);
            todoText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            todoText.setMaxLines(1);
            todoText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            todoText.setFocusableInTouchMode(true);
            todoText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        reOrderViews();
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