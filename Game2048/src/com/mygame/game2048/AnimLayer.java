package com.mygame.game2048;

import java.util.*;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.*;
import android.widget.FrameLayout;

public class AnimLayer extends FrameLayout {

	public AnimLayer(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		initLayer();
	}

	public AnimLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		initLayer();
	}

	public AnimLayer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		
		initLayer();
	}
	
	private void initLayer(){
		
	}
	
	private List<Card> cards=new ArrayList<Card>();
	
	public void createMoveAnim(final Card from,final Card to,int fromX,int fromY,int toX,int toY){
		final Card card=getCard(from.getNum());
		LayoutParams lp = new LayoutParams(from.getHeight(), from.getWidth());
		lp.leftMargin=fromX*from.getWidth();
		lp.topMargin=fromY*from.getHeight();
		card.setLayoutParams(lp);
		
		if (to.getNum()<=0) {
			to.getLabel().setVisibility(View.INVISIBLE);
		}
		TranslateAnimation ta = new TranslateAnimation(0, from.getWidth()*(toX-fromX), 0, from.getHeight()*(toY-fromY));
		ta.setDuration(100);
		ta.setAnimationListener(new Animation.AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {}

			@Override
			public void onAnimationRepeat(Animation animation) {}

			@Override
			public void onAnimationEnd(Animation animation) {
				to.getLabel().setVisibility(View.VISIBLE);
				recycleCard(card);
			}
		});
		card.startAnimation(ta);
	}
	
	private Card getCard(int num){
		Card card;
		if(cards.size()==0){
			card=new Card(getContext());
			addView(card);
		}else{
			card=cards.remove(0);
			card.setNum(num);
		}
		card.setNum(num);
		card.setVisibility(View.VISIBLE);
		return card;
	}
	
	private void recycleCard(Card card){
		card.setVisibility(View.INVISIBLE);
		card.setAnimation(null);
		cards.add(card);
	}

}
