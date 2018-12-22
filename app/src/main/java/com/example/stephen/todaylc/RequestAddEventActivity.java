package com.example.stephen.todaylc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
;
public class RequestAddEventActivity extends AppCompatActivity {
    private EditText nameEdit, emailEdit, organizationEdit, editDescription, editDate, editTime, editLocation, titleEdit;
    private LinearLayout addEventLayout;
    private final String MAIL_TO = "sbaker@lclark.edu"; // TODO: change this to Jason's email


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_add_event);
        editDate = findViewById(R.id.editTextDate);
        editDescription = findViewById(R.id.editTextDescription);
        editLocation = findViewById(R.id.editTextLocation);
        editTime = findViewById(R.id.editTextTime);
        emailEdit = findViewById(R.id.editTextEmail);
        organizationEdit = findViewById(R.id.editTextOrganization);
        nameEdit = findViewById(R.id.editTextName);
        titleEdit = findViewById(R.id.editTextEventTitle);

        addEventLayout = findViewById(R.id.addEventLayout);
        addEventLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.hideSoftKeyboard(RequestAddEventActivity.this);
            }
        });
    }

    /**
     * Called when the request button is pressed. Sends formatted user inputs to third party email
     * service of the user's choice.
     *
     * @param v the view which called this method
     */
    public void request(View v) {
        String emailBody = "Hi Jason,\n\n" +
                "I'm with " + organizationEdit.getText() +
                " and we would like to add our event, " + titleEdit.getText() +
                " to the LC events calendar." +
                " It will take place at " + editLocation.getText() +
                " on " + editDate.getText() + " at " + editTime.getText() + ". " +
                "The description is below, and the image is attached.\n\n" +
                "Thank you for your time,\n" + nameEdit.getText() + "\n\n" +
                editDescription.getText() + "\n\n" +
                "Sent with the LC Now App";
        String[] CC = {""};
        MainActivity.hideSoftKeyboard(RequestAddEventActivity.this);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{MAIL_TO});
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Event posting request");
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody);
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email"));
            finish();
        } catch (android.content.ActivityNotFoundException e) {
            Toast.makeText(RequestAddEventActivity.this, "No email client installed.", Toast.LENGTH_LONG).show();
        }

    }
}
