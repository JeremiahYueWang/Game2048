package com.mygame.game2048;


import java.io.*;
import java.util.*;

import org.apache.http.util.EncodingUtils;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

public class GameView extends GridLayout {

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initGameView();
		self=this;
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initGameView();
		self=this;
	}

	public GameView(Context context) {
		super(context);

		initGameView();
		self=this;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);	
		
		startGame();
	}

	
	private int colNum = 4;
	
	public int getColNum(){
		return colNum;
	}
	
	public void setColNum(int n){
		colNum=n;
	}
	
	private Card[][] cardsMap = new Card[getColNum()][getColNum()];	
	private int[] HighScore=new int[7];	//save the high score of chest plate size from 2-6
	
	private void initGameView(){
		
		this.setBackgroundColor(0xffbbada0);	
		this.getHistoricalHighScoreFromFile();
		
		setOnTouchListener(new View.OnTouchListener() {
			private float startX, startY, offsetX, offsetY, startTime, pushingTime;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					startX=event.getX();
					startY=event.getY();
					startTime=event.getEventTime();
				break;
				case MotionEvent.ACTION_UP:

					pushingTime=event.getEventTime()-startTime;
					if(pushingTime>1000){
						back();
					}
					offsetX=event.getX()-startX;
					offsetY=event.getY()-startY;
					if(Math.abs(offsetX)>Math.abs(offsetY)){
						if(offsetX>5){
							swipeRight();
						}
						else if(offsetX<-5){
							swipeLeft();
						}
					}
					else{
						if(offsetY>5){
							swipeDown();
						}
						else if(offsetY<-5){
							swipeUp();
						}
					}
				break;
				}
				
				return true;
			}
		});
	}
	
	public void getHistoricalHighScoreFromFile(){
		
		try{
			
			FileInputStream fis=this.getContext().openFileInput("2048HighScore.txt");
			int length=fis.available();
			byte[] buffer=new byte[length];
			fis.read(buffer);
			String queryResult=EncodingUtils.getString(buffer, "UTF-8");
			String s[]=queryResult.split(" ");
			for(int i=0; i<7; i++){
				HighScore[i]=Integer.parseInt(s[i]);
			}
			fis.close();
			
		}catch(Exception e){
			for(int i=0; i<7; i++){
				HighScore[i]=0;
			}
			e.printStackTrace();
		}
		
	}
		
	public void startGame(){		

		cardsMap=new Card[getColNum()][getColNum()];
		clearCards();
		int cardWidth=(Math.min(this.getWidth(), this.getHeight())-10)/getColNum();
		addCards(cardWidth, cardWidth);
		this.setColumnCount(getColNum());
		MainActivity.getMainActitive().setBackButtonUnabled();
		MainActivity.getMainActitive().showHighScore(getHighScore(getColNum()));
				
		MainActivity.getMainActitive().clearScore();
		setScore(0);
		this.snaps.clear();
		
		for(int y=0; y<getColNum(); y++){
			for(int x=0; x<getColNum(); x++){
				cardsMap[y][x].setNum(0);
			}
		}
		
		for(int i=0; i<2; i++){
			addRandomNum();
		}
		
		saveSnap();

	}	

	private void clearCards(){
		this.removeAllViews();
	}
	
	private void addCards(int cardWidth, int cardHeight){
		
		Card c;
		
		for(int y=0; y<getColNum(); y++){
			for(int x=0; x<getColNum(); x++){
				c=new Card(getContext());
				c.setNum(0);
				addView(c, cardWidth, cardHeight);
				cardsMap[y][x]=c;
			}
		}
	}
	
	

	//private int highScore=0;
	
	public int getHighScore(int chestSize){
		return HighScore[chestSize];		
	}
	
	public void setHighScore(int chestSize, int h){
		HighScore[chestSize]=h;
	}
	
	private void updateHighScore(){
		if(score>HighScore[getColNum()]){
			setHighScore(getColNum(), score);
		}
	}
		
	private GameView self;
	
	private boolean checkFinished(){
		
		boolean isFinished=true;
		all:
		for(int y=0; y<getColNum(); y++){
			for(int x=0; x<getColNum(); x++){
				if(cardsMap[y][x].getNum()==0){
					isFinished=false;
					break all;
				}else if(x==0||y==0||x==getColNum()-1||y==getColNum()-1){
					if(y==0&&cardsMap[y][x].equals(cardsMap[y+1][x]) ||				
						(y==getColNum()-1)&&cardsMap[y][x].equals(cardsMap[y-1][x]) || 
						x==0&&cardsMap[y][x].equals(cardsMap[y][x+1]) || 
						(x==getColNum()-1)&&cardsMap[y][x].equals(cardsMap[y][x-1]) ){
						isFinished=false;
						break all;
					}
				}else if(cardsMap[y][x].equals(cardsMap[y-1][x]) || 
						cardsMap[y][x].equals(cardsMap[y+1][x]) || 
						cardsMap[y][x].equals(cardsMap[y][x-1]) || 
						cardsMap[y][x].equals(cardsMap[y][x+1])){
					isFinished=false;
					break all;
				}
			}
		}
		
		return isFinished;
	}
	
	private void saveHighScoreToFile(){
		
		try{
			
			String highScore="";
			for(int i=0; i<7; i++){
				highScore+=(HighScore[i]+" ");
			}
			byte[] buffer=highScore.getBytes();
			FileOutputStream fos=this.getContext().openFileOutput("2048HighScore.txt", Context.MODE_PRIVATE);
			fos.write(buffer);
			fos.close();
			
		}catch(Exception e){
		}
		
	}
	
	private void addRandomNum(){
		
		emptyPoints.clear();
		
		for(int y=0; y<getColNum(); y++){
			for(int x=0; x<getColNum(); x++){
				if(cardsMap[y][x].getNum()==0){
					emptyPoints.add(new Point(y, x));
				}
				
			}
		}
		
		
		Point p=emptyPoints.remove((int)(Math.random()*emptyPoints.size()));
		cardsMap[p.x][p.y].setNum(Math.random()<0.1?4:2);
		
		
		if(checkFinished()){
			updateHighScore();
			saveHighScoreToFile();
			MainActivity.getMainActitive().showHighScore(getHighScore(getColNum()));
			//显示是否重新开始
			Dialog dialog=new AlertDialog.Builder(this.getContext()).setIcon(R.drawable.ic_launcher).setTitle("游戏结束！").setMessage("重新开始么").setPositiveButton("重新开始", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					self.startGame();
				}
			}).create();
			dialog.show();
		}
	}
	
	private void saveSnap(){
		Snap snap=new Snap();
		snap.saveScore(getScore());
		for(int y=0; y<getColNum(); y++){
			for(int x=0; x<getColNum(); x++){
				if(cardsMap[y][x].getNum()>0){
					snap.addCard(y, x, cardsMap[y][x].getNum());
				}
			}
		}
		snaps.add(snap);
		
		if(isBackable()){
			MainActivity.getMainActitive().setBackButtonEnabled();
		}
	}
	
	public boolean isBackable(){
		return snaps.size()>1;
	}
	
	public void back(){
		Snap snap=null;
		if(snaps.size()>1){
			snap=snaps.remove(snaps.size()-1);
			snap=snaps.get(snaps.size()-1);
			for(int y=0; y<getColNum(); y++){
				for(int x=0; x<getColNum(); x++){
					cardsMap[y][x].setNum(0);
				}
			}
			for(int i=0; i<snap.size(); i++){
				int t[]=snap.get(i);
				cardsMap[t[0]][t[1]].setNum(t[2]);
			}
			setScore(snap.showScore());

			MainActivity.getMainActitive().setScore(getScore());
		}
		if(!isBackable()){
			MainActivity.getMainActitive().setBackButtonUnabled();
		}
	}
	
	private void swipeLeft(){
		
		boolean isChanged=false;
		
		for(int y=0; y<getColNum(); y++){
			for(int x=0; x<getColNum(); x++){
				for(int i=x+1; i<getColNum(); i++){
					if(cardsMap[y][i].getNum()>0){
						if(cardsMap[y][x].getNum()==0){
							MainActivity.getMainActitive().getAnimLayer().createMoveAnim(cardsMap[y][i],cardsMap[y][x], i, y, x, y);
							cardsMap[y][x].setNum(cardsMap[y][i].getNum());
							cardsMap[y][i].setNum(0);
							isChanged=true;
							x--;
						}else if(cardsMap[y][i].equals(cardsMap[y][x])){
							MainActivity.getMainActitive().getAnimLayer().createMoveAnim(cardsMap[y][i],cardsMap[y][x], i, y, x, y);
							
							cardsMap[y][x].setNum(cardsMap[y][x].getNum()*2);
							cardsMap[y][i].setNum(0);							
							
							setScore(getScore()+cardsMap[y][x].getNum());
							MainActivity.getMainActitive().setScore(getScore());
							isChanged=true;
						}
						break;
					}
				}
			}
		}
		
		if(isChanged){
			this.addRandomNum();
			saveSnap();
		}
		
	}
	
	private void swipeRight(){
		
		boolean isChanged=false;
		
		for(int y=0; y<getColNum(); y++){
			for(int x=getColNum()-1; x>=0; x--){
				for(int i=x-1; i>=0; i--){
					if(cardsMap[y][i].getNum()>0){
						if(cardsMap[y][x].getNum()==0){
							MainActivity.getMainActitive().getAnimLayer().createMoveAnim(cardsMap[y][i],cardsMap[y][x], i, y, x, y);
							
							cardsMap[y][x].setNum(cardsMap[y][i].getNum());
							cardsMap[y][i].setNum(0);
							x++;
							
							isChanged=true;
						}else if(cardsMap[y][i].equals(cardsMap[y][x])){
							MainActivity.getMainActitive().getAnimLayer().createMoveAnim(cardsMap[y][i],cardsMap[y][x], i, y, x, y);
							
							cardsMap[y][x].setNum(cardsMap[y][x].getNum()*2);
							cardsMap[y][i].setNum(0);
							
							setScore(getScore()+cardsMap[y][x].getNum());
							MainActivity.getMainActitive().setScore(getScore());
							
							isChanged=true;
						}
						break;
					}
				}
			}
		}
		
		if(isChanged){
			this.addRandomNum();
			saveSnap();
		}
		
	}
	
	private void swipeUp(){
		
		boolean isChanged=false;
		
		for(int x=0; x<getColNum(); x++){
			for(int y=0; y<getColNum(); y++){
				for(int i=y+1; i<getColNum(); i++){
					if(cardsMap[i][x].getNum()>0){
						if(cardsMap[y][x].getNum()==0){
							MainActivity.getMainActitive().getAnimLayer().createMoveAnim(cardsMap[i][x],cardsMap[y][x], x, i, x, y);
							
							cardsMap[y][x].setNum(cardsMap[i][x].getNum());
							cardsMap[i][x].setNum(0);
							y--;
							
							isChanged=true;
						}else if(cardsMap[i][x].equals(cardsMap[y][x])){
							MainActivity.getMainActitive().getAnimLayer().createMoveAnim(cardsMap[i][x],cardsMap[y][x], x, i, x, y);
							
							cardsMap[y][x].setNum(cardsMap[y][x].getNum()*2);
							cardsMap[i][x].setNum(0);
							
							setScore(getScore()+cardsMap[y][x].getNum());
							MainActivity.getMainActitive().setScore(getScore());
							
							isChanged=true;
						}
						break;
					}
				}
			}
		}
		
		if(isChanged){
			this.addRandomNum();
			saveSnap();
		}
		
	}
	
	private void swipeDown(){
		
		boolean isChanged=false;
		
		for(int x=0; x<getColNum(); x++){
			for(int y=getColNum()-1; y>=0; y--){
				for(int i=y-1; i>=0; i--){
					if(cardsMap[i][x].getNum()>0){
						if(cardsMap[y][x].getNum()==0){
							MainActivity.getMainActitive().getAnimLayer().createMoveAnim(cardsMap[i][x],cardsMap[y][x], x, i, x, y);
							
							cardsMap[y][x].setNum(cardsMap[i][x].getNum());
							cardsMap[i][x].setNum(0);
							y++;
							
							isChanged=true;
						}else if(cardsMap[i][x].equals(cardsMap[y][x])){
							MainActivity.getMainActitive().getAnimLayer().createMoveAnim(cardsMap[i][x],cardsMap[y][x], x, i, x, y);
							
							cardsMap[y][x].setNum(cardsMap[y][x].getNum()*2);
							cardsMap[i][x].setNum(0);
							
							setScore(getScore()+cardsMap[y][x].getNum());
							MainActivity.getMainActitive().setScore(getScore());
							
							isChanged=true;
						}
						break;
					}
				}
			}
		}
		
		if(isChanged){
			this.addRandomNum();
			saveSnap();
		}
		
	}
	
	
	private List<Point> emptyPoints = new ArrayList<Point>();
	private int score=0;
	
	public void setScore(int i){
		score=i;
	}
	
	public int getScore(){
		return score;
	}

	class Snap{
		private List<int[]> cards = new ArrayList<int[]>();
		private int score;
		
		public void addCard(int i, int j, int num){
			int [] t={i, j, num};
			cards.add(t);
		}
		
		public void saveScore(int n){
			score=n;
		}
		
		public int showScore(){
			return score;
		}
		
		public int size(){
			return cards.size();
		}
		
		public int[] get(int i){
			return cards.get(i);
		}
	}
	
	private List<Snap> snaps = new ArrayList<Snap>();
}
