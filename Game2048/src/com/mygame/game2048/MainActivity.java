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

public class MainActivity extends Activity {
	
	public MainActivity(){
		mainActivity=this; 
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container=(LinearLayout) this.findViewById(R.id.container);
        banner=(LinearLayout) this.findViewById(R.id.banner);
        tvScore=(TextView) this.findViewById(R.id.tvScore);
        highScore=(TextView) this.findViewById(R.id.highScore);
        gameView=(GameView) this.findViewById(R.id.gameView);
        backButton=(Button) this.findViewById(R.id.back);
        
        int width=container.getWidth();
        banner.setMinimumHeight(width/3);
        System.out.println("start game");
        
        backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				gameView.back();				
			}
		});
        
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();	//打开设置页面
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openSettings(){
    	
    	//设置棋盘大小
    	Intent intent=new Intent(this, SettingActivity.class); 
    	this.startActivityForResult(intent, 100);
    	
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(requestCode==100 && resultCode==Activity.RESULT_OK){
    		int val=data.getExtras().getInt("column");
    		System.out.println("new column is "+val);
    		gameView.setColNum(val); //
    		gameView.startGame();
    	}
    }
    
    
    public void clearScore(){
    	score=0;
    	showScore();
    }
    
    public void showScore(){
    	tvScore.setText(score+"");
    }
    
    public void setScore(int s){
    	score=s;
    	showScore();
    }
    
    public void showHighScore(int h){
    	highScore.setText(h+"");
    }
    
    
    private int score=0;
    private LinearLayout container;
    private LinearLayout banner;
    private TextView tvScore;
    private TextView highScore;
    private Button backButton;
    private GameView gameView;
    
    
    public static MainActivity mainActivity = null;
    
    public static MainActivity getMainActitive(){
    	return mainActivity;
    }

}
