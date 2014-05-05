package com.mygame.game2048;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Card extends FrameLayout {

	public Card(Context context) {
		super(context);

		label=new TextView(getContext());
		label.setTextSize(32);
		label.setGravity(Gravity.CENTER);
		label.setBackgroundColor(0x33ffffff);//in setNum（）,for different Num, set different bgcolor, and textcolor
		
		
		LayoutParams lp=new LayoutParams(-1, -1);
		lp.setMargins(10, 10, 0, 0);
		addView(label, lp);
		
		setNum(0);
	}
	
	private int num=0;
	
	public void setNum(int n){
		this.num=n;
		if(n==0){
			label.setText("");
			label.setBackgroundColor(0x33ffffff);
		}else{
			label.setText(num+"");
			label.setTextColor(ColorSet[log2(n)][0]);
			label.setBackgroundColor(ColorSet[log2(n)][1]);
		}
	}
	
	private int log2(int n){
		int result=0, t=n;
		while(t>1){
			t/=2;
			result++;
		}
		return result-1;
	}
	
	public int getNum(){
		return num;
	}
	
	public boolean equals(Card o){
		return getNum()==o.getNum();
	}
	private TextView label;
	private int ColorSet[][]=
		{{0xff000000, 0x3354FF9F},{0xff000000, 0x332E8B57},{0xff000000, 0x332F4F4F},
		{0xff000000, 0x33FFEC8B},{0xff000000, 0x33FFD700},{0xff000000, 0x338B7500},
		{0xff000000, 0x33FF7F00},{0xff000000, 0x33ff7256},{0xff000000, 0x338B3E2F},
		{0xff000000, 0x33FFB6C1},{0xff000000, 0x33ba55d3},{0xff000000, 0x339400d3}};

}
