package com.example.myapplication;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
                disabler(clockView);
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

    public void enabler() {
        GridLayout gridLayout = getGridLayout();

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View childView = gridLayout.getChildAt(i);
            if (childView instanceof ImageButton) {
                childView.setClickable(true);
            }
            else if (childView instanceof TodoInstance) {
                TodoInstance todoInstance = (TodoInstance) childView;
                for (int j = 0; j < todoInstance.getChildCount(); j++) {
                    View todoView = todoInstance.getChildAt(j);
                    if (todoView instanceof TodoCheckBox || todoView instanceof TodoRemove) {
                        todoView.setEnabled(true);
                        todoView.setClickable(true);
                    }
                    else if (todoView instanceof TodoText) {
                        todoView.setEnabled(true);
                    }
                    else if (todoView instanceof ClockInstance) {
                        ((ClockInstance) todoView).getChildAt(5).setEnabled(true);
                        ((ClockInstance) todoView).getChildAt(5).setClickable(true);
                    }
                }
            }
        }
    }

    public void disabler(View enabledView) {
        GridLayout gridLayout = getGridLayout();
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View gridView = gridLayout.getChildAt(i);
            for (View descendantView : gridView.getTouchables()) {
                if (descendantView == enabledView) {
                    continue;
                }
                else if (descendantView instanceof TodoText || descendantView instanceof ClockView) {
                    descendantView.setEnabled(false);
                }
                descendantView.setClickable(false);
            }
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
        private ClockInstance clockInstance;
        private TodoText todoText;
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
            this.clockInstance = new ClockInstance(getApplicationContext());
            todoInstance.addView(this.clockInstance);

            TodoCheckBox todoCheckBox = new TodoCheckBox(getApplicationContext());
            todoInstance.addView(todoCheckBox);
            this.todoText = new TodoText(getApplicationContext());
            todoInstance.addView(this.todoText);
            TodoRemove todoRemove = new TodoRemove(getApplicationContext());
            todoInstance.addView(todoRemove);
        }

        public TodoText getTodoText() {
            return this.todoText;
        }

        public ClockInstance getClockInstance() {
            return this.clockInstance;
        }

        @Override
        public int compareTo(TodoInstance todoComparable) {
            ClockInstance clockComparableTo = (ClockInstance) todoComparable.getChildAt(0);
            StringBuilder clockContentComparableTo = new StringBuilder();
            ClockView clockView;
            for (int i = 0; i < 5; i++) {
                if (i != 2) {
                    clockView = (ClockView) clockComparableTo.getChildAt(i);
                    clockContentComparableTo.append(Objects.requireNonNull(clockView.getText()).toString());
                }
            }
            int comparatorTo = Integer.parseInt(clockContentComparableTo.toString());

            ClockInstance clockComparableAgainst = getClockInstance();
            StringBuilder clockContentComparableAgainst = new StringBuilder();
            for (int i = 0; i < 5; i++) {
                if (i != 2) {
                    clockView = (ClockView) clockComparableAgainst.getChildAt(i);
                    clockContentComparableAgainst.append(Objects.requireNonNull(clockView.getText()).toString());
                }
            }
            int comparatorAgainst = Integer.parseInt(clockContentComparableAgainst.toString());
            return comparatorAgainst - comparatorTo;
        }
    }

    public class ClockInstance extends LinearLayout {
        private final ClockInstance clockInstance;
        private ClockView clockViewOne;
        private ClockView clockViewTwo;
        private ClockView clockViewThree;
        private ClockView clockViewFour;

        public ClockInstance(Context context) {
            super(context);
            this.clockInstance = this;
            clockInstance.setProperties();
            clockInstance.setChildren();
        }

        public void setProperties() {
            LinearLayout.LayoutParams myP = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            clockInstance.setLayoutParams(myP);
            clockInstance.setOrientation(LinearLayout.HORIZONTAL);
        }

        public void setChildren() {
            ArrayList<ClockView> clockList = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                ClockView clockView = new ClockView(getApplicationContext());
                if (!(i == 2)) {
                    clockView.setProperties();
                    clockList.add(clockView);
                }
                else {
                    clockView.setProperties(':');
                }
                clockInstance.addView(clockView);
            }
            ClockEdit clockEdit = new ClockEdit(getApplicationContext());
            clockInstance.addView(clockEdit);
            clockViewOne = clockList.get(0);
            clockViewTwo = clockList.get(1);
            clockViewThree = clockList.get(2);
            clockViewFour = clockList.get(3);
        }

        public ClockView getClockViewOne() {
            return this.clockViewOne;
        }

        public ClockView getClockViewTwo() {
            return this.clockViewTwo;
        }

        public ClockView getClockViewThree() {
            return this.clockViewThree;
        }

        public ClockView getClockViewFour() {
            return this.clockViewFour;
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
//            clockView.setOnFocusChangeListener(new OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (hasFocus) {
//                        showKeyboard(getApplicationContext(), v);
//                    }
//                }
//            });
            clockView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    ClockInstance clockInstance = (ClockInstance) clockView.getParent();
                    TodoInstance todoInstance = (TodoInstance) clockInstance.getParent();
                    if (clockView == clockInstance.getClockViewOne()) {
                        clockInstance.getClockViewTwo().setEnabled(true);
                        clockInstance.getClockViewTwo().setClickable(true);
                        clockInstance.getClockViewTwo().requestFocus();
                    }
                    else if (clockView == clockInstance.getClockViewTwo()) {
                        clockInstance.getClockViewThree().setEnabled(true);
                        clockInstance.getClockViewThree().setClickable(true);
                        clockInstance.getClockViewThree().requestFocus();
                    }
                    else if (clockView == clockInstance.getClockViewThree()) {
                        clockInstance.getClockViewFour().setEnabled(true);
                        clockInstance.getClockViewFour().setClickable(true);
                        clockInstance.getClockViewFour().requestFocus();
                    }
                    else if (clockView == clockInstance.getClockViewFour()) {
                        enabler();
                        todoInstance.getTodoText().setEnabled(true);
                        todoInstance.getTodoText().setClickable(true);
                        todoInstance.getTodoText().requestFocus();
                    }
                    /*
                     * The current clock view is specifically
                     * disabled here to prevent the soft keyboard
                     * from auto-hiding when switching focus to
                     * the next view (ClockView or TodoText).
                     */
                    clockView.setEnabled(false);
                    clockView.setClickable(false);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            clockView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    /*
                     * IME_ACTION_DONE listener is used here to
                     * determine if the user tried to press it before
                     * editing the clock view. IME_ACTION_DONE button
                     * itself is not used to complete the editing of
                     * the clock view.
                     */
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        disabler(clockView);
                        ClockInstance clockInstance = (ClockInstance) v.getParent();
                        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(),
                                R.animator.nudge);      // Load nudge animation.
                        set.setTarget(clockInstance);   // Set clock instance as the view to be nudged.
                        set.start();                    // Start the animation.
                    }
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
                    disabler(clockEdit);
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
            todoCheckBox.setVisibility(View.INVISIBLE);
//            Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.star_animation);
//            todoCheckBox.setAnimation(slide);
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
                        TodoCheckBox todoCheckBox = (TodoCheckBox) ((TodoInstance) todoText.getParent()).getChildAt(1);
                        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(),
                                R.animator.star_animation);
                        set.setTarget(todoCheckBox);
                        todoCheckBox.setVisibility(View.VISIBLE);
                        set.start();
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