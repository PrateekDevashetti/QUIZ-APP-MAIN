package com.example.qapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Question extends AppCompatActivity {

    private TextView question,noindicator;
    private LinearLayout optionsContainer;
    private Button submitButton,nextButton;
    private int count = 0;
    private  List <QuestionModel> list;
    private int position=0;
    private int score = 0;
    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("qapp-73e2b");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        question = findViewById(R.id.question);
        noindicator = findViewById(R.id.noindicator);
        optionsContainer = findViewById(R.id.optionsContainer);
        submitButton = findViewById(R.id.submitButton);
        nextButton = findViewById(R.id.nextButton);


        list = new ArrayList<>();
       myRef.child("question").addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                   list.add(snapshot.getValue(QuestionModel.class));
               }
               if (list.size() > 0) {

                   for (int i=0;i<4;i++){
                       optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               checkAnswer((Button)v);
                           }
                       });
                   }
                   playAnim(question,0,list.get(position).getQuestion() );
                   nextButton.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           nextButton.setEnabled(false);
                           nextButton.setAlpha(0.7f);
                           position++;
                           enableOption(true);
                           if (position == list.size()){
                               //scoreActivity
                               return;
                           }
                           count =0;
                           playAnim(question,0,list.get(position).getQuestion());
                       }
                   });
               }else{
                   finish();
                   Toast.makeText(Question.this,"no question",Toast.LENGTH_LONG);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               Toast.makeText(Question.this,databaseError.getMessage(), Toast.LENGTH_LONG).show();
           }
       });

    }
    private void playAnim(final View view, final int value, final String data){
      view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(600).setStartDelay(100).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
        @Override
     public void onAnimationStart(Animator animation) {
        if(value == 0 && count < 4){
            String option="";
            if (count ==0){
                option = list.get(position).getOptionA();
            }else if (count == 1){
                option = list.get(position).getOptionB();
            }else if (count ==2){
                option = list.get(position).getOptionC();
            }else if (count ==3) {
                option = list.get(position).getOptionD();
            }
          playAnim(optionsContainer.getChildAt(count),0,option);
        count++;
     }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

               if(value ==0){
                   try {
                       ((TextView)view).setText(data);
                       noindicator.setText(position+1+"/"+list.size());
                   }catch (ClassCastException ex){
                       ((Button)view).setText(data);
                   }
                   view.setTag(data);
                     playAnim(view,1,data);}
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
    private void checkAnswer(Button selectedOption){
        enableOption(false);
        nextButton.setEnabled(true);
        nextButton.setAlpha(1);
        if (selectedOption.getText().toString().equals(list.get(position).getCorrectANS())){
            //correct
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E6812F")));
            score++;
        }else{

            //incorrect
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            Button correctoption = (Button)optionsContainer.findViewWithTag(list.get(position).getCorrectANS());
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E6812F")));
        }
    }

    private void enableOption(boolean enable){
        for (int i=0;i<4;i++) {

        optionsContainer.getChildAt(i).setEnabled(enable);
        if (enable){
            optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CBDBE6")));
        }
        }

    }
}
