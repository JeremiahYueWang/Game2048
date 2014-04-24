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
		}else{
			label.setText(num+"");
//			label.setTextColor();
//			label.setBackgroundColor();
		}
	}
	
	public int getNum(){
		return num;
	}
	
	public boolean equals(Card o){
		return getNum()==o.getNum();
	}
	private TextView label;

}
