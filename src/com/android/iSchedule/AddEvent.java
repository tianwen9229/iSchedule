package com.android.iSchedule;

import android.os.Bundle;
import android.app.Activity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class AddEvent extends Activity {
	
	public Button backButton;
	public Button doneButton;
	public EditText eventTitleEditText;
	public EditText eventContentEditText;
	public Button fromDatePickerButton;
	public Button toTimePickerButton;
	public Spinner frequencySpinner;
	public EditText eventPlaceEditText;
	public Spinner modifyModeSpinner;
		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_event);
		
		LayoutInflater liInflater = LayoutInflater.from(this);
		View view = liInflater.inflate(R.layout.datePicker, root)
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
