package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private GridLayout gridLayout;
    private ImageButton addButton;
    private ImageButton settingsButton;
    private ImageButton shutdownButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setGridLayout();
        setAddButton();
        setSettingsButton();
        setShutdownButton();
    }

    private void setGridLayout() {
        this.gridLayout = findViewById(R.id.gridLayout);
    }

    public GridLayout getGridLayout() {
        return this.gridLayout;
    }

    private void setAddButton() {
        this.addButton = findViewById(R.id.addButton);
        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddButton().setClickable(false);
                final LinearLayout todoInstance = new TodoInstance(getApplicationContext(), null);
                getGridLayout().addView(todoInstance);
                ClockInstance clockInstance = (ClockInstance) todoInstance.getChildAt(0);
                ClockView clockView = (ClockView) clockInstance.getChildAt(0);
                clockView.requestFocus();
                showKeyboard(getApplicationContext(), clockView);
            }
        });
    }

    public ImageButton getAddButton() {
        return this.addButton;
    }

    private void setSettingsButton() {
        this.settingsButton = findViewById(R.id.settingsButton);
        getSettingsButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: This will start a new settings activity.
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    public ImageButton getSettingsButton() {
        return this.settingsButton;
    }

    private void setShutdownButton() {
        this.shutdownButton = findViewById(R.id.shutdownButton);
        getShutdownButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        });
    }

    public ImageButton getShutdownButton() {
        return this.shutdownButton;
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

    public void removeTodoInstance(LinearLayout todoInstance) {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        gridLayout.removeView(todoInstance);
    }

    public class TodoInstance extends LinearLayout implements Comparable<TodoInstance> {
        private final TodoInstance todoInstance;
        private int clockViewIndex;
        private int textViewIndex;
        private int removeViewIndex;

        public TodoInstance(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            this.todoInstance = this;
            todoInstance.setProperties();
            todoInstance.setChildren();
        }

        public void setProperties() {
            todoInstance.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT);
            myP.topMargin = 20;
            todoInstance.setLayoutParams(myP);
        }

        public void setChildren() {
            ClockInstance clockInstance = new ClockInstance(getApplicationContext());
            todoInstance.addView(clockInstance);
            TodoCheckBox todoCheckBox = new TodoCheckBox(getApplicationContext());
            todoInstance.addView(todoCheckBox);
            TodoText todoText = new TodoText(getApplicationContext());
            todoInstance.addView(todoText);
            TodoRemove todoRemove = new TodoRemove(getApplicationContext());
            todoInstance.addView(todoRemove);
            todoInstance.setChildrenIndexes();
        }

        public void setChildrenIndexes() {
            View childView;
            for (int i = 0; i < todoInstance.getChildCount(); i++) {
                childView = todoInstance.getChildAt(i);
                if (childView instanceof ClockInstance) {
                    clockViewIndex = i;
                }
                else if (childView instanceof TodoText) {
                    textViewIndex = i;
                }
                else if (childView instanceof TodoRemove) {
                    removeViewIndex = i;
                }
            }
        }

        @Override
        public int compareTo(TodoInstance todoRename) {
            ClockInstance clockInstance1 = (ClockInstance) todoRename.getChildAt(0);
            StringBuilder clock1 = new StringBuilder();
            ClockView clockView1;
            for (int i = 0; i < 5; i++) {
                if (i != 2) {
                    clockView1 = (ClockView) clockInstance1.getChildAt(i);
                    clock1.append(Objects.requireNonNull(clockView1.getText()).toString());
                }
            }
            int comparator1 = Integer.parseInt(clock1.toString());

            ClockInstance clockInstance2 = (ClockInstance) todoInstance.getChildAt(0);
            StringBuilder clock2 = new StringBuilder();
            ClockView clockView2;
            for (int i = 0; i < 5; i++) {
                if (i != 2) {
                    clockView2 = (ClockView) clockInstance2.getChildAt(i);
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
        private int clockViewOne;
        private int clockViewTwo;
        private int clockViewColon;
        private int clockViewThree;
        private int clockViewFour;
//        private int clockEdit;

        public ClockInstance(Context context) {
            super(context);
            this.clockInstance = this;
            clockInstance.setProperties();
            clockInstance.setChildren();
            clockInstance.setChildrenIndexes();
        }

        public void setProperties() {
            LinearLayout.LayoutParams myP = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            clockInstance.setLayoutParams(myP);
            clockInstance.setOrientation(LinearLayout.HORIZONTAL);
        }

        public void setChildren() {
            for (int i = 0; i < 5; i++) {
                ClockView clockView = new ClockView(getApplicationContext());
                if (!(i == 2)) {
                    clockView.setProperties();
                }
                else {
                    clockView.setProperties(':');
                }
                clockInstance.addView(clockView);
            }
            ClockEdit clockEdit = new ClockEdit(getApplicationContext());
            clockInstance.addView(clockEdit);
        }

        public void setChildrenIndexes() {
            clockViewOne = 0;
            clockViewTwo = 1;
            clockViewThree = 3;
            clockViewFour = 4;
            clockViewColon = 2;
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
            LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(30, 50);
            myP.topMargin = 20;
            clockView.setLayoutParams(myP);
            clockView.setGravity(Gravity.CENTER);
            clockView.setBackgroundResource(android.R.color.darker_gray);
            clockView.setHint("-");
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
                                // Resets the add to-do button to clickable. The user can now add a new to-do.
                                getAddButton().setClickable(true);
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
                    /* Once set, the clock view cannot be edited directly.
                       The user can still edit the clock instance using the edit button. */
                    clockView.setEnabled(false);
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
            myP.topMargin = 20;
            clockView.setLayoutParams(myP);
            clockView.setBackgroundResource(android.R.color.transparent);
        }
    }

    public class ClockEdit extends androidx.appcompat.widget.AppCompatButton {
        private final ClockEdit clockEdit;

        public ClockEdit(@NonNull Context context) {
            super(context);
            this.clockEdit = this;
            clockEdit.setProperties();
        }

        public void setProperties() {
            LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(25, 25);
            clockEdit.setLayoutParams(myP);
            clockEdit.setBackgroundResource(R.drawable.edit_icon);
            clockEdit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getAddButton().setClickable(false);
                    TodoInstance todoInstance = (TodoInstance) ((ClockInstance) v.getParent()).getParent();
                    ArrayList<View> todoChildren = new ArrayList<>();
                    for (int i = 0; i < todoInstance.getChildCount(); i++) {
                        todoChildren.add(todoInstance.getChildAt(i));
                    }
                    int iterator = 0;
                    for (View todoChild : todoChildren) {
                        todoInstance.removeView(todoChild);
                        if (iterator == 0) {
                            ClockInstance clockInstance = new ClockInstance(getApplicationContext());
                            todoInstance.addView(clockInstance);
                        }
                        else {
                            todoInstance.addView(todoChild);
                        }
                        iterator++;
                    }
                    ClockView clockView = (ClockView) ((ClockInstance) todoInstance.getChildAt(0)).getChildAt(0);
                    clockView.requestFocus();
                    showKeyboard(getApplicationContext(), clockView);
                }
            });
        }
    }

    public class TodoCheckBox extends androidx.appcompat.widget.AppCompatCheckBox {
        private final TodoCheckBox todoCheckBox;

        public TodoCheckBox(@NonNull Context context) {
            super(context);
            this.todoCheckBox = this;
            todoCheckBox.setProperties();
        }

        private void setProperties() {
            todoCheckBox.setGravity(Gravity.CENTER_HORIZONTAL);
            todoCheckBox.setButtonDrawable(android.R.drawable.btn_star);
            todoCheckBox.setClickable(true);
            Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.star_animation);
            todoCheckBox.setAnimation(slide);
        }
    }

    public class TodoText extends androidx.appcompat.widget.AppCompatEditText {
        private final TodoText todoText;

        public TodoText(@NonNull Context context) {
            super(context);
            this.todoText = this;
            todoText.setProperties();
        }

        /**
         * setProperties() method sets the parameters of the text view
         * of the to-do instance. On IME_ACTION_DONE the application
         * reorders the to-do instances according to time ascending.
         */
        private void setProperties() {
            todoText.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams myP = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            myP.leftMargin = 50;
            todoText.setLayoutParams(myP);
            todoText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            todoText.setMaxLines(1);
            todoText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            todoText.setFocusableInTouchMode(true);
            todoText.setHint("to-do");
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
}