package com.example.brain_trainer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brain_trainer.ScoreActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textViewTimer;
    private TextView textViewExample;
    private TextView textViewScore;
    private TextView textViewOpinion1;
    private TextView textViewOpinion2;
    private TextView textViewOpinion3;
    private TextView textViewOpinion4;

    private ArrayList<TextView> textViews;

    private String question;
    private int rightAnswer;
    private int rightAnswerPosition;
    private boolean isPositive;
    private int min = 5;
    private int max = 30;
    private int countOfQuestion = 0;
    private int countOfRightAnswers;
    private boolean gameOver = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewExample = findViewById(R.id.textViewExample);
        textViewOpinion1 = findViewById(R.id.textViewOpinion1);
        textViewOpinion2 = findViewById(R.id.textViewOpinion2);
        textViewOpinion3 = findViewById(R.id.textViewOpinion3);
        textViewOpinion4 = findViewById(R.id.textViewOpinion4);
        textViewScore = findViewById(R.id.textViewScore);
        textViews = new ArrayList<>();
        textViews.add(textViewOpinion1);
        textViews.add(textViewOpinion2);
        textViews.add(textViewOpinion3);
        textViews.add(textViewOpinion4);
        PlayNext();
        CountDownTimer timer = new CountDownTimer(35000, 1000) {
            @Override
            public void onTick(long l) {
                textViewTimer.setText(getTime(l));
                if (l < 10000) {
                    textViewTimer.setTextColor(getColor(R.color.red));
                }
                // Добавляем бонусы за достижение определенного количества правильных ответов
                if (countOfRightAnswers >= 5) {
                    textViewTimer.setText(getTime(l + 15000));
                }
            }

            @Override
            public void onFinish() {
                textViewTimer.setText(getTime(0));
                gameOver = true;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                if (countOfRightAnswers >= max) {
                    preferences.edit().putInt("max", countOfRightAnswers).apply();
                }
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("result", countOfRightAnswers);
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void PlayNext() {
        generateQuestion();
        for (int i = 0; i < textViews.size(); i++) {
            if (i == rightAnswerPosition) {
                textViews.get(i).setText(Integer.toString(rightAnswer));
            } else {
                textViews.get(i).setText(Integer.toString(generateWrongAnswer()));
            }
            String score = String.format("%s / %s", countOfRightAnswers, countOfQuestion);
            textViewScore.setText(score);
        }
    }

    // В этом методе мы будем получать наш пример, правильный ответ и его позицию.
    private void generateQuestion() {
        int a = (int) (Math.random() * (max - min + 1) + min);
        int b = (int) (Math.random() * (max - min + 1) + min);
        int mark = (int) (Math.random() * 2);
        isPositive = mark == 1;
        if (isPositive) {
            rightAnswer = a + b;
            question = String.format("%s + %s", a, b);
        } else {
            rightAnswer = a - b;
            question = String.format("%s - %s", a, b);
        }
        textViewExample.setText(question);
        rightAnswerPosition = (int) (Math.random() * 4); // Получаем позицию правильного ответа
    }


    private int generateWrongAnswer() {
        int result;
        do {
            result = (int) (Math.random() * max * 2 + 1) - (max - min);
        } while (result == rightAnswer);
        return result;
    }

    private String getTime(long millis) {
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    public void onClickGetAnswer(View view) {
        if (!gameOver) {
            TextView textView = (TextView) view;
            String answer = textView.getText().toString();
            int chosenAnswer = Integer.parseInt(answer);
            if (chosenAnswer == rightAnswer) {
                Toast.makeText(this, "Верно", Toast.LENGTH_SHORT).show();
                countOfRightAnswers++;
            } else {
                Toast.makeText(this, "Неверно", Toast.LENGTH_SHORT).show();
            }
            countOfQuestion++;
            PlayNext();
        }
    }
}