package com.sanjidhalim.expensesplitter;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateExpenseActivity extends AppCompatActivity {
    private DbHelper dbHelper= DbHelper.getInstance(this);
    private LinearLayout mLinearLayout;
    private List<EditText> mList;
    private String tripName;
    private String[] participants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_expense);

        mLinearLayout = (LinearLayout) findViewById(R.id.createExpenseLinearLayout);

        Intent intent = getIntent();
        tripName =  intent.getStringExtra("TRIP_NAME");
        Toast.makeText(getBaseContext(), tripName, Toast.LENGTH_SHORT).show();
        participants = dbHelper.getTripParticipants(tripName);

        makeLayout(participants);
    }


    private void makeLayout(final String[] participants){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);

        final EditText expenseName = new EditText(this);
        expenseName.setHint("Enter Expense Name");
        mLinearLayout.addView(expenseName);
        mList = new ArrayList<>();

        for (int i=0; i<participants.length;i++){
            TextView textView = new TextView(this);
            String string="Enter "+participants[i]+"'s contribution";
            textView.setText(string);

            EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            et.setHint("0");
            mList.add(et);

            mLinearLayout.addView(textView);
            mLinearLayout.addView(et);
        }

        Button btn = new Button(this);
        btn.setText("Create Expense");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expense = expenseName.getText().toString();
                double[] contributions = new double[mList.size()];
                int i = 0;
                for (EditText et : mList) {
                    String text = et.getText().toString();
                    double value;
                    try {
                        value = Double.parseDouble(text);
                    } catch (NumberFormatException e){
                        value = 0;
                    }
                    contributions[i++] = value;
                }
                dbHelper.saveExpenses(tripName, expense, participants, contributions);
                finish();
            }
        });
        mLinearLayout.addView(btn);
    }
}
