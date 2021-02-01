package com.example.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = getApplicationContext();

        final ConstraintLayout constraintLayout = findViewById(R.id.rootContainer);
        final Resolution resolution = new Resolution();
//        final ImageView imageView = new ImageView(this);
//        imageView.setLayoutParams(new ConstraintLayout.LayoutParams(resolution.width, resolution.height/2));
        final List<ImageView> imageViews = new ArrayList<ImageView>();
        final ImageButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImageView imageView = new ImageView(context);
                imageViews.add(imageView);
                ImageView currentView = imageViews.get(imageViews.size() - 1);
                currentView.setLayoutParams(new ConstraintLayout.LayoutParams(300, (imageViews.size() * 300 + 300)));
                currentView.setImageResource(R.drawable.user_to_do);
                constraintLayout.addView(currentView);
                currentView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("Clicked on imageView " + v);
                        EditText ed = new EditText(context);
                        ed.setInputType(InputType.TYPE_CLASS_TEXT);
                        ed.setLayoutParams(new ConstraintLayout.LayoutParams(600, (imageViews.size() * 300 + 300)));
                        System.out.println("Theoretically something was added.");

                    }
                });
                System.out.println("Adding to-do, size of list = " + imageViews.size());
            }
        });
        final Button rmButton = findViewById(R.id.rmButton);
        rmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageViews.size() > 0) {
                    int indexOfLastView = imageViews.size() - 1;
                    imageViews.get(indexOfLastView).setVisibility(View.GONE);
                    imageViews.remove(indexOfLastView);
                }
            }
        });


//        ConstraintLayout constraintLayout = findViewById(R.id.rootContainer);
//        if (constraintLayout != null) {
//            for (ImageView ivItem : imageViews) {
//                constraintLayout.addView(ivItem);
//            }
//            constraintLayout.addView(imageViews.get(0));
//        }


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