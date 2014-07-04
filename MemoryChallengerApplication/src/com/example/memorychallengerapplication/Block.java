package com.example.memorychallengerapplication;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class Block extends AutoResizeButton{

	private int enable = 0;
	private boolean opened = false;
	private int blockNum;
	private int blockColor;

	
	public Block(Context context) {
		super(context);
		//setBackgroundColor(Color.CYAN);

		//this.setBackgroundResource(R.layout.button_layout);
		//setBackground(getResources().getDrawable(R.drawable.lock));
		setBackgroundResource(R.drawable.button_enabled);
		setText("  ");				 
		// TODO Auto-generated constructor stub
	}
	
	public void setDefaultBackGround()
	{
		//setBackground(getResources().getDrawable(R.drawable.lock));
		setBackgroundResource(R.drawable.button_enabled);
		setText("  ");		
	}
	public int getEnabledStatus()
	{
		return enable;
	}
	
	public void setTickMark()
	{
		//setBackground(getResources().getDrawable(R.drawable.grey));
		setBackgroundResource(R.drawable.button_shape);
		setText("$$");
	}
	
	public void setNumberAndBackground()
	{
		setText(Integer.toString(blockNum).trim());
		if(blockColor==0)
		{
			//setBackground(getResources().getDrawable(R.drawable.blue));
			setBackgroundResource(R.drawable.button_blue);
		}
		else
		{
			//setBackground(getResources().getDrawable(R.drawable.brown));
			setBackgroundResource(R.drawable.button_yellow);
		}
		
	}
	
	public void setNumber(int num)
	{
		blockNum = num;
	}
	
	public void setColor(int b)
	{
		blockColor = b;
	}
	
	public int getNumber()
	{
		return blockNum;
	}
	
	public int getColor()
	{
		return blockColor;
	}
	
	public boolean doesMatch(Block b)
	{
		if((b.getNumber() == this.getNumber()) && (b.getColor() == this.getColor()))
			return true;
		return false;
	}
	
	public void setButtonStatus(int b)
	{
		this.enable = b;
	}
	
	public void setOpened(boolean b)
	{
		this.opened = b;
	}
	
	public  boolean getOpened()
	{
		return opened;
	}
}
