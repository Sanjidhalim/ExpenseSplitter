package com.sanjidhalim.expensesplitter;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

public class TripExpensesActivity extends AppCompatActivity {
    private TableLayout mTableLayout;
    private DbHelper dbHelper = DbHelper.getInstance(this);
    private String tripName;
    private TextView mTextView;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        tripName = intent.getStringExtra("TRIP_NAME");
        mTableLayout = (TableLayout) findViewById(R.id.expenseTable);
        mTextView = (TextView) findViewById(R.id.paymentTextView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), CreateExpenseActivity.class);
                intent.putExtra("TRIP_NAME", tripName);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setExpenseList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mTableLayout.removeAllViews();
        setExpenseList();
    }

    private void setPayment(final List<String> participants){

        double totalExpense = 0;
        String noMoney = "No ones owes money! :) ";
        final List<Double> expenses = new ArrayList<Double>();
        List<String> participantCopy = new ArrayList<String>();

        for (String participant : participants){
            double expense = dbHelper.totalSpentBy(participant, tripName);
            expenses.add(expense);
            participantCopy.add(participant);
            totalExpense += expense;
        }

        if (totalExpense == 0) {
            mTextView.setText(noMoney);
            return;
        }

        Collections.sort(participantCopy, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return ((Double)(expenses.get(participants.indexOf(lhs)) - expenses.get(participants.indexOf(rhs)))).intValue();
            }
        });

        Collections.sort(expenses);
        double averageExpense = totalExpense/ participants.size();

        StringBuilder builder = new StringBuilder();


        int start = 0; int end = participants.size() - 1;
        while (start <= end){
            double lessThanMean = averageExpense - expenses.get(start);
            double greaterThanMean = expenses.get(end) - averageExpense;

            if (lessThanMean == 0){
                break;
            }

            if (lessThanMean < greaterThanMean){
                builder.append(getString(participantCopy.get(start), participantCopy.get(end), lessThanMean));
                start++;
                expenses.set(end, expenses.get(end) - lessThanMean);
            }
            else if (lessThanMean > greaterThanMean){
                builder.append(getString(participantCopy.get(start), participantCopy.get(end), greaterThanMean));
                end--;
                expenses.set(start, expenses.get(start) + greaterThanMean);
            }
            else{
                builder.append(getString(participantCopy.get(start), participantCopy.get(end), greaterThanMean));
                start++;
                end--;
            }
        }

        String message = builder.toString();
        mTextView.setText(message.isEmpty() ? noMoney : message);
//        double one = dbHelper.totalSpentBy(participants[0],tripName);
//        double two = dbHelper.totalSpentBy(participants[1],tripName);
//
//        String firstName = one < two ? participants[0] : participants[1];
//        String secondName = one > two ? participants[0] : participants[1];
//        double total = Math.abs(one - (one + two) / 2.0);
//
//        String text = firstName + " owes $" + total + " to " + secondName;
//        Log.d("MASTRING",text);
//        mTextView.setText(text);
    }

    private String getString(String firstName, String secondName, double total){
        return firstName + " owes $" + total + " to " + secondName + "\n";
    }

    public void setExpenseList(){
        Cursor expenseData = dbHelper.getTripExpenses(tripName);
        String[] participants = dbHelper.getTripParticipants(tripName);
        String[] expenses = dbHelper.getUniqueExpenses(tripName);

        Toast.makeText(getBaseContext(), Arrays.toString(expenses), Toast.LENGTH_SHORT).show();

        createHeaderTableRow(participants);
        createExpenseRows(expenses, participants, expenseData);
        setPayment(Arrays.asList(participants));

    }

    private void createHeaderTableRow(String[] participants){
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,1);
        row.setLayoutParams(lp);

        row.setClickable(true);

        TextView tv = new TextView(this);
        tv.setPadding(10,0,10,10);
        tv.setText("Expenses");
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(null, Typeface.BOLD_ITALIC);

        row.addView(tv);

        for (String participant:participants ){
            tv = new TextView(this);
            tv.setPadding(10,0,10,10);
            tv.setText(participant);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setGravity(Gravity.CENTER);
            row.addView(tv);
        }
        mTableLayout.addView(row);
    }

    private void createExpenseRows(String[] expenses,String[] participants,Cursor expenseData){
        Log.d("EXPENSE",Arrays.toString(expenses));
        Log.d("Participants",Arrays.toString(participants));
        Log.d("Cursor",expenseData.toString());
        for (String expense:expenses){
            expenseData.moveToFirst();
            double[] tempExpense = new double[participants.length];
            while(! expenseData.isAfterLast()){
                if (expenseData.getString(expenseData.getColumnIndex("expenseName")).equals(expense)){
                    for (int i=0;i<participants.length;i++){
                        String personName = expenseData.getString(expenseData.getColumnIndex("personName"));
                        if (participants[i].equals(personName)){
                            tempExpense[i]=Double.parseDouble(expenseData.getString(expenseData.getColumnIndex("expenseAmount")));
                        }
                    }
                }
                expenseData.moveToNext();
            }
            createRow(expense, tempExpense);
        }
    }

    private void createRow(final String expense, double[] tempExpense){
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT,1);
        row.setLayoutParams(lp);

        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mActionMode = startActionMode(mActionModeCallback);
                mActionMode.setTag(expense);
                mActionModeCallback.setClickedView(v);
                return true;
            }
        });

        TextView tv = new TextView(this);
        tv.setText(expense);
        tv.setGravity(Gravity.CENTER);

        row.addView(tv);

        for (double expenses:tempExpense ){
            tv = new TextView(this);
            tv.setText(Double.toString(expenses));
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setGravity(Gravity.CENTER);
            row.addView(tv);
        }
        mTableLayout.addView(row);
    }


    private interface ActionCallback extends ActionMode.Callback {
        void setClickedView(View view);
    }

    private ActionCallback mActionModeCallback = new ActionCallback() {
        public View mClickedView;

        public void setClickedView (View view){
            mClickedView = view;
            mClickedView.setBackgroundColor(Color.parseColor("#FDAFDA"));
        }

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    //Toast.makeText(getBaseContext(),mode.getTag().toString(),Toast.LENGTH_LONG).show();
                    dbHelper.deleteExpense(tripName,mode.getTag().toString());
                    mClickedView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    onResume();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mClickedView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            mActionMode = null;
        }
    };

}



