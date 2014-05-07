package com.mygame.game2048;


import java.io.*;
import java.util.*;

import org.apache.http.util.EncodingUtils;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
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
		//从历史中读取上次结束的状态
		
		
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

		MainActivity.getMainActitive().showHighScore(getHighScore(getColNum()));
		for(int y=0; y<getColNum(); y++){
			for(int x=0; x<getColNum(); x++){
				cardsMap[y][x].setNum(0);
			}
		}
		
		if(snaps.size()>=1){
			loadSnap(snaps.get(snaps.size()-1));
		}else{
			
			for(int i=0; i<2; i++){
				addRandomNum();
			}
			
			saveSnap();
		}

	}	
	
	public void endGame(){
		this.snaps.clear();
		this.setScore(0);
		MainActivity.getMainActitive().clearScore();
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
					self.endGame();
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
	
	private void loadSnap(Snap snap){
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
		if(!isBackable()){
			MainActivity.getMainActitive().setBackButtonUnabled();
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
			
			loadSnap(snap);
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

	public void saveGameStateToFile(){
		String gameState="";
		gameState+=(getColNum()+" ");	//保存当前棋盘大小
		if(checkFinished()){
			gameState+="0 ";	//显示只有0个状态；
		}else{
			gameState+=(snaps.size()+" ");	//保存snaps的大小
			for(int i=0; i<snaps.size(); i++){
				gameState+=snaps.get(i).toString();
			}
		}
		
		try{
			byte[] buffer=gameState.getBytes();
			FileOutputStream fos=this.getContext().openFileOutput("2048GameState.txt", Context.MODE_PRIVATE);
			fos.write(buffer);
			fos.close();
		}catch(Exception e){
			
		}
	}
	
	public void getGameStateFromFile(){
		try{
			FileInputStream fis=this.getContext().openFileInput("2048GameState.txt");
			int length=fis.available();
			byte[] buffer=new byte[length];
			fis.read(buffer);
			String queryResult=EncodingUtils.getString(buffer, "UTF-8");
			String gameState[]=queryResult.split(" ");
			
			int index=0;
			setColNum(Integer.parseInt(gameState[index++]));
			
			this.snaps.clear();
			int snapsSize=Integer.parseInt(gameState[index++]);
			
			for(int i=0; i<snapsSize; i++){
				Snap snap=new Snap();
				snap.saveScore(Integer.parseInt(gameState[index++]));
				int cardsNum=Integer.parseInt(gameState[index++]);
				for(int j=0; j<cardsNum; j++){
					snap.addCard(Integer.parseInt(gameState[index]), Integer.parseInt(gameState[index+1]), Integer.parseInt(gameState[index+2]));
					index+=3;
				}
				snaps.add(snap);
			}			

			fis.close();
			
		}catch(Exception e){
			//没有找到文件表示第一次游戏
			setColNum(4);
			MainActivity.getMainActitive().clearScore();
			this.snaps.clear();
		}
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
		
		public String toString(){
			
			String result="";
			result+=(score+" ");	//分数
			result+=(cards.size()+" ");	//当前局面的卡片个数
			for(int i=0; i<cards.size(); i++){
				result+=(cards.get(i)[0]+" "+cards.get(i)[1]+" "+cards.get(i)[2]+" ");
			}
			return result;
			
		}
	}
	
	private List<Snap> snaps = new ArrayList<Snap>();
}
