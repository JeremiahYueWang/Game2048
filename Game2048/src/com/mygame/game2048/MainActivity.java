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
        animLayer=(AnimLayer) this.findViewById(R.id.animLayer);
        backButton=(Button) this.findViewById(R.id.back);
        
        int width=container.getWidth();
        banner.setMinimumHeight(width/3);
        
        backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				gameView.back();				
			}
		});

		gameView.getGameStateFromFile();
		isSaved=false;
                      
    }
    
    

	protected void onStart(){
		super.onStart();
	}
	
	protected void onRestart(){
		super.onRestart();
	}
	
	protected void onResume(){
		super.onResume();
	}
	
	protected void onPause(){
		super.onPause();
	}
	
	protected void onStop(){
		super.onStop();
		gameView.saveGameStateToFile();
		isSaved=true;
	}
	
	protected void onDestroy(){
		super.onDestroy();
		if(!isSaved){
			gameView.saveGameStateToFile();
		}
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
    
    public int getCurrentColumn(){
    	return gameView.getColNum();
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(requestCode==100 && resultCode==Activity.RESULT_OK){
    		int val=data.getExtras().getInt("column");
    		gameView.setColNum(val); //
    		gameView.endGame();
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
    
    public void setBackButtonEnabled(){
    	backButton.setEnabled(true);
    }
    
    public void setBackButtonUnabled(){
    	backButton.setEnabled(false);
    }
    
    private int score=0;
    private LinearLayout container;
    private LinearLayout banner;
    private TextView tvScore;
    private TextView highScore;
    private Button backButton;
    private GameView gameView;
    private AnimLayer animLayer;
    
    
    public static MainActivity mainActivity = null;
    
    public static MainActivity getMainActitive(){
    	return mainActivity;
    }
    public AnimLayer getAnimLayer(){
    	return animLayer;
    }
    
    private boolean isSaved=false;
}
