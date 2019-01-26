package com.sanjidhalim.expensesplitter;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.app.ActionBar.LayoutParams;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

public class AddTrip extends AppCompatActivity {
    private EditText mParticipantNum;
    private List<EditText> allEds = new ArrayList<EditText>();
    private DbHelper dbHelper = DbHelper.getInstance(this);
    private LinearLayout ll;
    private Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        mParticipantNum = (EditText) findViewById(R.id.participantNum);

        mParticipantNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ll = (LinearLayout) findViewById(R.id.addTripLinearLayout);
/*                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);*/

                Iterator<EditText> iter = allEds.iterator();
                while (iter.hasNext()){
                    EditText et = iter.next();
                    ll.removeView(et);
                    iter.remove();
                }
                if (mBtn!=null) ll.removeView(mBtn);

                try {
                    int numParticipants = Integer.parseInt(mParticipantNum.getText().toString());

                    for (int i = 0; i < numParticipants; i++) {
                        EditText et = new EditText(getBaseContext());
                        allEds.add(et);
                        //et.setBackgroundColor(Color.parseColor("#7684a1"));
                        et.setHintTextColor(Color.parseColor("#7684a1"));
                        et.setTextColor(Color.parseColor("#000000"));
                        et.setHint("Enter Name");
                        ll.addView(et);
                    }
                    mBtn = new Button(getBaseContext());
                    mBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (allEds.size() < 2) {
                            } else {
                                String[] names = new String[allEds.size()];
                                for (int i = 0; i < allEds.size(); i++) {
                                    names[i] = allEds.get(i).getText().toString();
                                }
                                EditText et = (EditText) findViewById(R.id.tripName);
                                dbHelper.insertTrip(et.getText().toString(), names);
                                finish();
                            }
                        }
                    });
                    mBtn.setText("Create Trip");
                    ll.addView(mBtn);
                } catch (NumberFormatException e){}

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
