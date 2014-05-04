package com.mygame.game2048;


import java.io.*;
import java.util.*;

import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;

public class GameView extends GridLayout {

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		initGameView();
		
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initGameView();
		
	}

	public GameView(Context context) {
		super(context);

		initGameView();
		
	}

	private int colNum = 2;
	
	public int getColNum(){
		return colNum;
	}
	
	public void setColNum(int n){
		colNum=n;
	}
	
	
	private void initGameView(){
		
		
		this.setColumnCount(getColNum());
		this.setBackgroundColor(0xffbbada0);
		
		
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
					System.out.println("during time "+pushingTime);
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
	
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		
		int cardWidth=(Math.min(w, h)-10)/getColNum();
		addCards(cardWidth, cardWidth);
		startGame();
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
	
	public void startGame(){
		

		setHighScore(this.getHistoricalHighScore());
		MainActivity.getMainActitive().showHighScore(getHighScore());
		
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
	
	public int getHistoricalHighScore(){
		
		try{
			
			FileInputStream fis=this.getContext().openFileInput("2048HighScore.txt");
			int length=fis.available();
			byte[] buffer=new byte[length];
			fis.read(buffer);
			String queryResult=EncodingUtils.getString(buffer, "UTF-8");
			int start=queryResult.indexOf(" ");
			int h=Integer.parseInt(queryResult.substring(start).trim());
			fis.close();
			return h;
			
		}catch(Exception e){
			System.out.println("read get highscore from file error!!!!!!!!!");
			e.printStackTrace();
			return 0;
		}
		
	}
	

	private int highScore=0;
	
	public int getHighScore(){
		return highScore;		
	}
	
	public void setHighScore(int h){
		highScore=h;
	}
	
	private void updateHighScore(){
		if(score>highScore){
			setHighScore(score);
		}
	}
		
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
		if(isFinished){
			updateHighScore();
			saveHighScoreToFile();
			MainActivity.getMainActitive().showHighScore(getHighScore());
		}
		return isFinished;
	}
	
	private void saveHighScoreToFile(){
		
		try{
			String highScore=colNum+" "+getHighScore()+" ";
			byte[] buffer=highScore.getBytes();
			FileOutputStream fos=this.getContext().openFileOutput("2048HighScore.txt", Context.MODE_PRIVATE);
			fos.write(buffer);
			fos.close();
			
		}catch(Exception e){
			System.out.println("save highcore to file error!!!!!!");
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
			System.out.println("finished");
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
	}
	
	public void back(){
		Snap snap=null;
		if(snaps.size()>1){
			System.out.println("snaps.size="+snaps.size());
			snap=snaps.remove(snaps.size()-1);
			snap=snaps.get(snaps.size()-1);
			System.out.println("number of cards="+snap.size());
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
			System.out.println("the previous score is "+getScore());

			MainActivity.getMainActitive().setScore(getScore());
		}
		System.out.println("back");
	}
	
	private void swipeLeft(){
		
		boolean isChanged=false;
		
		for(int y=0; y<getColNum(); y++){
			for(int x=0; x<getColNum(); x++){
				for(int i=x+1; i<getColNum(); i++){
					if(cardsMap[y][i].getNum()>0){
						if(cardsMap[y][x].getNum()==0){
							cardsMap[y][x].setNum(cardsMap[y][i].getNum());
							cardsMap[y][i].setNum(0);
							isChanged=true;
							x--;
						}else if(cardsMap[y][i].equals(cardsMap[y][x])){
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
			saveSnap();
			this.addRandomNum();
		}
		
	}
	
	private void swipeRight(){
		
		boolean isChanged=false;
		
		for(int y=0; y<getColNum(); y++){
			for(int x=getColNum()-1; x>=0; x--){
				for(int i=x-1; i>=0; i--){
					if(cardsMap[y][i].getNum()>0){
						if(cardsMap[y][x].getNum()==0){
							cardsMap[y][x].setNum(cardsMap[y][i].getNum());
							cardsMap[y][i].setNum(0);
							x++;
							
							isChanged=true;
						}else if(cardsMap[y][i].equals(cardsMap[y][x])){
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
			saveSnap();
			this.addRandomNum();
		}
		
	}
	
	private void swipeUp(){
		
		boolean isChanged=false;
		
		for(int x=0; x<getColNum(); x++){
			for(int y=0; y<getColNum(); y++){
				for(int i=y+1; i<getColNum(); i++){
					if(cardsMap[i][x].getNum()>0){
						if(cardsMap[y][x].getNum()==0){
							cardsMap[y][x].setNum(cardsMap[i][x].getNum());
							cardsMap[i][x].setNum(0);
							y--;
							
							isChanged=true;
						}else if(cardsMap[i][x].equals(cardsMap[y][x])){
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
							cardsMap[y][x].setNum(cardsMap[i][x].getNum());
							cardsMap[i][x].setNum(0);
							y++;
							
							isChanged=true;
						}else if(cardsMap[i][x].equals(cardsMap[y][x])){
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
	
	private Card[][] cardsMap = new Card[getColNum()][getColNum()];	
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
