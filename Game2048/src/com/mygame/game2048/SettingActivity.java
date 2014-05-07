package com.mygame.game2048;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.os.Build;

public class SettingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		this.selectColumnButton=(Button)findViewById(R.id.selectcolumnbutton);
		this.selectColumnPicker=(NumberPicker)findViewById(R.id.columnnumberPicker);
		selectColumnPicker.setMinValue(2);
		selectColumnPicker.setMaxValue(6);
		selectColumnPicker.setValue(MainActivity.getMainActitive().getCurrentColumn());
		selectColumnButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				int col=selectColumnPicker.getValue();
				Intent data=new Intent();
				data.putExtra("column", col);
				setResult(Activity.RESULT_OK, data);
				finish();
			}
		});
		
	}


	private NumberPicker selectColumnPicker;
	private Button selectColumnButton;
	
}
