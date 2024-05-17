package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

// Created by following and tweaking this tutorial: https://reintech.io/blog/creating-simple-calculator-app-android
public class CalculatorActivity extends AppCompatActivity {

    EditText firstNumber, secondNumber;
    Button addBtn, subtractBtn, multiplyBtn, divideBtn, backBtn;
    TextView result;

    private int counter = 0;    // counter variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        /* initialize UI elements */
        firstNumber = findViewById(R.id.firstNumber);
        secondNumber = findViewById(R.id.secondNumber);
        addBtn = findViewById(R.id.addButton);
        subtractBtn = findViewById(R.id.subtractButton);
        multiplyBtn = findViewById(R.id.multiplyButton);
        divideBtn = findViewById(R.id.divideButton);
        result = findViewById(R.id.result);
        backBtn = findViewById(R.id.counter_back_btn);

        /*When add button is clicked, add the values from input 1 and input 2*/
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double input1 = Double.parseDouble(firstNumber.getText().toString());
                double input2 = Double.parseDouble(secondNumber.getText().toString());
                double res = input1 + input2;
                result.setText("" + res);
            }
        });
        subtractBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double input1 = Double.parseDouble(firstNumber.getText().toString());
                double input2 = Double.parseDouble(secondNumber.getText().toString());
                double res = input1 - input2;
                result.setText("" + res);
            }
        });
        multiplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double input1 = Double.parseDouble(firstNumber.getText().toString());
                double input2 = Double.parseDouble(secondNumber.getText().toString());
                double res = input1 * input2;
                result.setText("" + res);
            }
        });
        divideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double input1 = Double.parseDouble(firstNumber.getText().toString());
                double input2 = Double.parseDouble(secondNumber.getText().toString());
                double res = input1 / input2;
                result.setText("" + res);
            }
        });



        /* when back btn is pressed, switch back to MainActivity */
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalculatorActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}