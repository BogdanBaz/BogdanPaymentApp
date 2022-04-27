package com.example.bogdanpaymentapp;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    EditText amountTxt, titleTxt;
    TextView responseText;
    Button send;

    final String TAG = "Tag";
    Spinner mySpinner;
    private String sleepNumber = "0";

    private final String[] arraySpinner = {
            "TR_PURCHASE (Standard sale transaction)",
            "TR_PREAUTH (Preauthorization)",
            "TR_COMPLETION (Preauthorization completion)",
            "TR_RETURN (Refund)",
            "TR_CANCEL (Cancelling of transaction)",
            "TR_RECON (Reconcilliation - end of day procedure)",
            "TR_PARAMS (Connect to TMS for update)"};


    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "=Activity.RESULT is: " + result);

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Log.d(TAG, "=Activity.RESULT_OK=");
                        if (intent != null) {

                            responseText.setText(
                                    (getActionName((int) Integer.valueOf(intent.getStringExtra("TRANSACTION_TYPE_KEY ")))) + "\n"
                                            + (intent.getStringExtra("TRANSACTION_RESULT_KEY").contentEquals("TRANSACTION_RESULT_OK")
                                            ? "TRANSACTION_ACCEPTED_TEXT" : "TRANSACTION_REFUSED_TEXT") + "\n"
                                            + (intent.getStringExtra("CARD_TOKEN_KEY") != null
                                            ? intent.getStringExtra("CARD_TOKEN_KEY") : "")
                                            + (intent.getStringExtra("ERROR_MESSAGE_KEY") != null
                                            ? (intent.getStringExtra("ERROR_MESSAGE_KEY")) : ""));

                            if (intent.getStringExtra(" result ").contentEquals("0")) {// accepted
                                responseText.setTextColor(getColor(R.color.purple_700));
                            } else {
                                responseText.setTextColor(getColor(com.google.android.material.R.color.design_default_color_error));
                            }
                        } else {
                            responseText.setText(result.toString());
                        }
                    } else {
                        Log.d(TAG, "=Activity.RESULT_CANCELED=");
                        responseText.setText(result.toString());
                    }
                }
            });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(TAG, "= onActivityResult =");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "= onPostCreate =");
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        Log.d(TAG, "= onPostResume =");
        try {
            Intent intent = getIntent();
            if (intent != null) {
                Log.d(TAG, "intent!=null + " + intent.getExtras());
            } else Log.d(TAG, "intent==null (((( ");


        } catch (Exception e) {
            e.printStackTrace();
            Log.e("getStringExtra_EX", e + "");
        }

        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "= onCreate = , savedInstanceState: " + savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Intent intent = getIntent();
            if (intent != null) {
                Log.d(TAG, "intent!=null + " + intent.getExtras());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("getStringExtra_EX", e + "");
        }

        mySpinner = (Spinner) findViewById(R.id.my_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(adapter);

        amountTxt = findViewById(R.id.txt_amount);

        titleTxt = findViewById(R.id.txt_title);
        responseText = findViewById(R.id.txt_response);

        send = findViewById(R.id.btn_send);
        send.setOnClickListener(view -> sendInitIntent());

    }

    // Sending transaction initialization intent
    private void sendInitIntent() {
        Intent sendIntent = getPackageManager().getLaunchIntentForPackage("com.evopayments.payiso");

     //  Intent sendIntent = new Intent();
     /*  sendIntent.setComponent(new ComponentName("com.evopayments.payiso",
                "com.evopayments.payiso.MainActivity.class "));*/

        sendIntent.setAction("com.evopayments.payiso.PERFORM_TRANSACTION");
        sendIntent.addCategory(Intent.CATEGORY_DEFAULT);
        System.out.println("!!!!!! "+ sendIntent.getAction() + " " + sendIntent.getCategories()+" " + sendIntent.getPackage() +
                " " + sendIntent.getScheme());

        sendIntent.putExtra("title", getTransactionTitle());
        sendIntent.putExtra("amount", getTransactionAmount());
        sendIntent.putExtra("type",
                String.valueOf(ActionType.getIdForName(mySpinner.getSelectedItem().toString().split(" ")[0])));

//Transaction number needed in cancel transaction
        /*if (getSlipNumber().length() > 0) {
            sendIntent.putExtra(" slipNumber ", getSlipNumber());
        }*/
        try {
            mStartForResult.launch(sendIntent);// initialization shown in receive example
            Log.d(TAG, "startForResult, intent get type: " + sendIntent.getStringExtra("type"));

        } catch (ActivityNotFoundException exception) {
            Log.d(TAG, " ActivityNotFoundException ");
            responseText.setText(" Requested application not found on terminal ");
            exception.printStackTrace();

            AlertDialog.Builder downloadDialog = new AlertDialog.Builder(this);
            downloadDialog.setTitle("ActivityNotFoundException");
            downloadDialog.setNegativeButton(":-(",
                    (dialog, i) -> dialog.dismiss());
            downloadDialog.show();
        }
    }

    private String getTransactionAmount() {
        String amount = this.amountTxt.getText().toString();
        if (!amount.equals("")) {
            return amount;
        } else return "null";
    }

    private String getSlipNumber() {
        return this.sleepNumber;
    }

    private String getTransactionTitle() {
        String title = this.titleTxt.getText().toString();
        if (!title.equals("")) {
            return title;
        } else return "null";
    }


    // Side functions
    private String getActionName(int type) {
        if (type > 0x80) {
            return ActionType.fromId(type - 0x80).name() + " REVERSAL ";
        } else {
            return ActionType.fromId(type).name();
        }
    }

    // Receiving the result intent

}