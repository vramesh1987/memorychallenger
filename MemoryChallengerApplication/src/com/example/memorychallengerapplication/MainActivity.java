package com.example.memorychallengerapplication;

import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.widget.GridLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int NUM_ROWS = 5;
	private static final int NUM_COLS = 4;
	public static final int BUTTON_ENABLED = 0;
	public static final int BUTTON_DISABLED = 1;
	public static final int BUTTON_MATCHED = 2;
	private Block[] blockList = new Block[NUM_ROWS*NUM_COLS];
	private int[] numberPool = new int[99];
	private int[][] chosenNumbers = new int[NUM_ROWS*NUM_COLS][2];
	private int[][] tempArray = new int[NUM_ROWS*NUM_COLS][2];
	private Block prevBlock=null, currBlock=null;
	private Handler tHandler = new Handler();
	private Handler timerHandler = new Handler();
	private boolean isHandlerActive = false, userTouched = false;
	private TableLayout tl;
	private TextView time_taken, best_time;
	private int time_cnt = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		initializeBlockNumbers();
		
		Button closeButton = (Button)findViewById(R.id.Button6);
		Button hintsButton = (Button)findViewById(R.id.Button4);
		Button restartButton = (Button)findViewById(R.id.Button3);
		time_taken = (TextView)findViewById(R.id.textView1);
		best_time = (TextView)findViewById(R.id.textView2);
		
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		hintsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		restartButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				restartGame();
			}
			
		});
		
		//RelativeLayout rl = (RelativeLayout)findViewById(R.id.my_rel_layout);
		tl = (TableLayout)findViewById(R.id.block_layout);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
		        ViewGroup.LayoutParams.MATCH_PARENT,
		        ViewGroup.LayoutParams.MATCH_PARENT);
		
		tl.setLayoutParams(lp);
		tl.setShrinkAllColumns(true);//false maybe?
		tl.setStretchAllColumns(true);//false maybe?
		
		TableLayout.LayoutParams rowLp = new TableLayout.LayoutParams(
		        ViewGroup.LayoutParams.MATCH_PARENT,
		        ViewGroup.LayoutParams.MATCH_PARENT,
		        1.0f);
		TableRow.LayoutParams cellLp = new TableRow.LayoutParams(
		        ViewGroup.LayoutParams.WRAP_CONTENT,
		        ViewGroup.LayoutParams.WRAP_CONTENT,
		        1.0f);
		
		for(int i=0;i<NUM_ROWS;i++)
		{
			TableRow tr = new TableRow(this);
			
			for(int j=0;j<NUM_COLS;j++)
			{
				tr.addView(blockList[i*NUM_COLS+j] = new Block(this), cellLp);
				blockList[i*NUM_COLS+j].setNumber(chosenNumbers[i*NUM_COLS+j][0]);
				blockList[i*NUM_COLS+j].setColor(chosenNumbers[i*NUM_COLS+j][1]);
				blockList[i*NUM_COLS+j].setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						if(userTouched == false)
						{
							tHandler.postDelayed(tRunnable, 1000);
						}
						userTouched = true;
						if(((Block) v).getEnabledStatus() != BUTTON_ENABLED)
						{
							return;
						}
						if(isHandlerActive == true)
						{
							tHandler.removeCallbacks(r);
							prevBlock.setDefaultBackGround();
							currBlock.setDefaultBackGround();
							prevBlock.setButtonStatus(BUTTON_ENABLED);
							currBlock.setButtonStatus(BUTTON_ENABLED);
							isHandlerActive = false;
							prevBlock = currBlock;
							currBlock = (Block) v;
							currBlock.setNumberAndBackground();
							currBlock.setButtonStatus(BUTTON_DISABLED);
							return;
						}
						prevBlock = currBlock;
						currBlock = (Block) v;
						if(prevBlock!=null && prevBlock.getEnabledStatus() == BUTTON_DISABLED/*prevBlock.getOpened()==true*/)
						{
							if(currBlock.doesMatch(prevBlock))
							{
								currBlock.setTickMark();
								prevBlock.setTickMark();
								currBlock.setButtonStatus(BUTTON_MATCHED);
								prevBlock.setButtonStatus(BUTTON_MATCHED);
								Log.i("DEBUG", "current and previous blocks matched are " + currBlock.getNumber() + "," + currBlock.getColor() + " " + prevBlock.getNumber() + "," + prevBlock.getColor());
								checkGameEnd();
							}
							else
							{
								currBlock.setNumberAndBackground();
								prevBlock.setNumberAndBackground();
								currBlock.setButtonStatus(BUTTON_DISABLED);
								prevBlock.setButtonStatus(BUTTON_DISABLED);
								isHandlerActive = true;
								tHandler.postDelayed(r, 2000);
							}
						}
						else
						{
							currBlock.setNumberAndBackground();
							//currBlock.setOpened(true);
							currBlock.setButtonStatus(BUTTON_DISABLED);
						}
					}
				});
			}
			tl.addView(tr, rowLp);
		}
	}

	Runnable r = new Runnable(){

		@Override
		public void run() {
				prevBlock.setDefaultBackGround();
				currBlock.setDefaultBackGround();
				prevBlock.setButtonStatus(BUTTON_ENABLED);
				currBlock.setButtonStatus(BUTTON_ENABLED);
				isHandlerActive = false;
		}
	};
	
	Runnable tRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			time_taken.setText("time taken \n\n" + Integer.toString(++time_cnt));
			timerHandler.postDelayed(tRunnable, 1000);
		}
		
	};
	
	private void initializeBlockNumbers()
	{
		for(int i=0;i<99;i++)
		{
			numberPool[i]=0;
		}
		for(int i=0;i<NUM_ROWS*NUM_COLS;i++)
		{
			chosenNumbers[i][0]=0;
			chosenNumbers[i][1]=0;
		}
		int numberRange = NUM_ROWS*NUM_COLS;
		int numTries = 0, cnt = 0;
		for(int i=0;i<(NUM_ROWS*NUM_COLS/4);i++)
		{
			Random r = new Random();
			numTries = 0;
			int nextNumber = r.nextInt(99);
			while(numberPool[nextNumber]!=0 && numTries<5)
			{
				nextNumber = r.nextInt(99);
				numTries++;
			}
			if(numTries>=5)
			{
				while(nextNumber >=0 && nextNumber < 99 && numberPool[nextNumber]!=0)
				{
					nextNumber += 1;
					if(nextNumber >=0 && nextNumber < 99 && numberPool[nextNumber]!=0)
					{
						nextNumber -= 2;
					}
				}
			}
			numberPool[nextNumber] = nextNumber+1;
			for(int k=0;k<4;k++)
			{
				tempArray[cnt][0] = nextNumber+1;
				tempArray[cnt++][1] = k%2;
			}
		}
		for(int i=0;i<NUM_ROWS*NUM_COLS;i++)
		{
			//Log.i("ARRAY", Integer.toString(tempArray[i]));
		}
		Log.i("ARRAY", "in here");
		cnt = 0;
		for(int i=0;i<NUM_ROWS*NUM_COLS;i++)
		{
			Random r = new Random();
			numTries = 0; 
			int arrayIndex = r.nextInt(numberRange);
			while(chosenNumbers[arrayIndex][0]!=0 && numTries<5)
			{
				arrayIndex = r.nextInt(numberRange);
				numTries++;
			}
			if(numTries>=5)
			{
				int mul = 1;
				while(true)	
				{
					arrayIndex += mul * 1;
					if(arrayIndex < 0 || arrayIndex >= (NUM_ROWS*NUM_COLS) || chosenNumbers[arrayIndex][0]!=0)
					{
						arrayIndex -= mul  * 2;
					}
					else
					{
						break;
					}
					if(arrayIndex < 0 || arrayIndex >= (NUM_ROWS*NUM_COLS) || chosenNumbers[arrayIndex][0]!=0)
					{
						arrayIndex += mul  * 1;
					}
					else
					{
						break;
					}
					mul++;
				}
			}
			//Log.i("ARRAY", Integer.toString(arrayIndex));
			chosenNumbers[arrayIndex][0] = tempArray[cnt][0];
			chosenNumbers[arrayIndex][1] = tempArray[cnt++][1];
		}
		for(int i=0;i<NUM_ROWS*NUM_COLS;i++)
		{
			Log.i("ARRAY", Integer.toString(chosenNumbers[i][0])+" "+Integer.toString(chosenNumbers[i][1]));
		}
	}
	
	public void checkGameEnd()
	{
		int cnt = 0;
		for(int i=0;i<NUM_ROWS*NUM_COLS;i++)
		{
			if(blockList[i].getEnabledStatus() == BUTTON_MATCHED)
				cnt++;
		}
		if(cnt==(NUM_ROWS*NUM_COLS))
		{
			Log.i("GAME", "game is won!!");
			best_time.setText("best time \n \n" + Integer.toString(time_cnt));
			new GameWinDialogNew().show(getFragmentManager(), null);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void restartGame(){
		initializeBlockNumbers();
		for(int i=0;i<NUM_ROWS;i++)
		{
			TableRow curRow = (TableRow)tl.getChildAt(i);
			for(int j=0;j<NUM_COLS;j++)
			{
				Block curBlock = (Block)curRow.getChildAt(j);
				curBlock.setNumber(chosenNumbers[i*NUM_COLS+j][0]);
				curBlock.setColor(chosenNumbers[i*NUM_COLS+j][1]);
				curBlock.setDefaultBackGround();
				curBlock.setButtonStatus(BUTTON_ENABLED);
				userTouched = false;
			}
		}
	}
	
	public class GameWinDialogNew extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        Builder setNegativeButton = builder.setMessage(R.string.game_win)
	               .setPositiveButton(R.string.restart_game, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // FIRE ZE MISSILES!
	                	   restartGame();
	                   }
	               })
	               .setNegativeButton(R.string.quit_game, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                	  finish();
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}
	
	@Override
	protected void onResume()
	{
		SharedPreferences highScore = getPreferences(MODE_PRIVATE);
		String high_score = highScore.getString("highScore", "Not Set");
		Log.i("debug", high_score);
		if(high_score == "Not Set")
		{
			best_time.setText("best time \n \n -");
		}
		else
		{
			best_time.setText(high_score);
		}
		super.onResume();
	}
	
	@Override
	protected void onStop()
	{
		SharedPreferences highScore = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = highScore.edit();
		Log.i("DEBUG ON STOP", best_time.getText().toString());
		editor.putString("highScore", best_time.getText().toString());
		editor.commit();
		super.onStop();
	}

}
