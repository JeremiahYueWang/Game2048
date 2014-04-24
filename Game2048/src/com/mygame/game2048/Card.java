package com.mygame.game2048;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Card extends FrameLayout {

	public Card(Context context) {
		super(context);

		label=new TextView(getContext());
		label.setTextSize(32);
		
		LayoutParams lp=new LayoutParams(-1, -1);
		addView(label, lp);
		
		setNum(0);
	}
	
	private int num=0;
	
	public void setNum(int n){
		this.num=n;
		label.setText(num+"");
	}
	
	public int getNum(){
		return num;
	}
	
	public boolean equal(Card o){
		return getNum()==o.getNum();
	}
	private TextView label;

}
