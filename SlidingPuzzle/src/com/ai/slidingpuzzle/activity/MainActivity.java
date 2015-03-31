package com.ai.slidingpuzzle.activity;

import java.util.ArrayList;

import com.ai.slidingpuzzle.Block;
import com.ai.slidingpuzzle.BlockIndex;
import com.ai.slidingpuzzle.Board;
import com.ai.slidingpuzzle.BoardImage;
import com.ai.slidingpuzzle.BoardMove;
import com.ai.slidingpuzzle.BoardShuffle;
import com.ai.slidingpuzzle.BoardSize;
import com.ai.slidingpuzzle.R;
import com.ai.slidingpuzzle.basic.DIRECT;
import com.ai.slidingpuzzle.basic.Scale;
import com.ai.slidingpuzzle.layout.ImageLayout;
import com.ai.slidingpuzzle.layout.InfoLayout;
import com.ai.slidingpuzzle.layout.UserLayout;
import com.ai.slidingpuzzle.thread.AStarThread;
import com.ai.slidingpuzzle.thread.SolveMovingThread;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	final int HINT_COUNT =3 ;
	// Solve Handler Case
	final int START =0, STOP =1, IS_SHOW =2 ;
	// Moving Handler Case
	final int MOVE =0 ;
	
	String SOLVE_RECV, HINT_RECV, FAIL_RECV, SIZE_RECV ;
	AStarThread astarThread ;
	SolveMovingThread movingThread ;
	
	// BroadCast Receiver
	SolveAStarReceiver solveRecv ;
	FailAStarReceiver failRecv ;
	BoardSizeReceiver sizeRecv ;
	
	private Context context ;
	private Handler solveHandler, movingHandler ;
	private ProgressDialog dialog; 

	// Phone Display Scale
	private Scale scale ;
	// Board
	private Board board ;
	// path
	private ArrayList<BlockIndex> pathList ;
	
	private RelativeLayout imgRelativeLayout, infoRelativeLayout, userRelativeLayout ;
	private ImageButton hintBtn, solveBtn, sizeBtn, shuffleBtn ;
	private ImageView hintImgView ;
	private ImageView[][] imgView ;
	private TextView cntText ;
	private EditText cntEdit ;
	
	// Basic Size
	private int ROW =3, COL =3 ;
	private boolean isInitialLayout ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		// Context
		context =getApplicationContext() ;
		
		SOLVE_RECV =getResources().getString(R.string.solve_recv) ;
		HINT_RECV =getResources().getString(R.string.hint_recv) ;
		FAIL_RECV =getResources().getString(R.string.fail_recv) ;
		SIZE_RECV =getResources().getString(R.string.size_recv) ;
		
		// Handler Allocation
		solveHandler =new Handler (new SolveAStarHandlerCallback()) ;
		movingHandler =new Handler (new SolveMovingHandlerCallback()) ;
		
		// BroadCast Receiver		
		// Register Receive
		if (solveRecv == null) {
			final IntentFilter solveFilter =new IntentFilter(SOLVE_RECV) ;
			solveFilter.addAction(HINT_RECV);
			solveRecv =new SolveAStarReceiver() ;
			registerReceiver(solveRecv, solveFilter) ;
		}
		if (failRecv == null) {
			final IntentFilter failFilter =new IntentFilter(FAIL_RECV) ;
			failRecv =new FailAStarReceiver() ;
			registerReceiver(failRecv, failFilter) ;
		}
		if (sizeRecv == null) {
			final IntentFilter sizeFilter =new IntentFilter(SIZE_RECV) ;
			sizeRecv =new BoardSizeReceiver () ;
			registerReceiver(sizeRecv, sizeFilter) ;
		}

		// get Scale
		DisplayMetrics disM =new DisplayMetrics () ;
		getWindowManager ().getDefaultDisplay().getMetrics(disM) ;
		scale =new Scale (disM) ;
		
		// ImageLayout
		imgRelativeLayout =(RelativeLayout) findViewById (R.id.img_layout) ;
		
		// InfoLayout
		infoRelativeLayout =(RelativeLayout) findViewById (R.id.info_layout) ;
		cntEdit =(EditText) findViewById (R.id.cnt_edit) ;
		cntText =(TextView) findViewById (R.id.cnt_text) ;
		
		// UserLayout
		userRelativeLayout =(RelativeLayout) findViewById (R.id.user_layout) ;
		hintBtn =(ImageButton) findViewById (R.id.hint_btn) ;
		solveBtn =(ImageButton) findViewById (R.id.solve_btn) ;
		sizeBtn =(ImageButton) findViewById (R.id.size_btn) ;
		shuffleBtn =(ImageButton) findViewById (R.id.shuffle_btn) ;
		hintImgView =(ImageView) findViewById (R.id.hint_img) ;
	}
	
	private void initBoard (int row, int col) {
		
		// Init Move Counting
		InfoLayout.setMoveCntView(cntEdit, 0);
		
		// BoardSize Update시 재 할당
		if (imgView == null
				|| imgView.length != row
				|| imgView[0].length != col)
			imgView =new ImageView[row][col] ;
		
		RelativeLayout.LayoutParams params ;
		for (int i =0, viewId =1 ; i < row ; i++) {
			for (int j =0 ; j < col ; j++, viewId++) {
				
				// Block Image
				// Width base, height base
				params =new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT, 
						RelativeLayout.LayoutParams.WRAP_CONTENT) ;
				
				// 첫 열이 아닌 것들은 오른쪽으로
				if (j > 0) {
					params.addRule(RelativeLayout.RIGHT_OF, imgView[i][j-1].getId()) ;
					if (i> 0) params.addRule(RelativeLayout.BELOW, imgView[i -1][j].getId()) ;
				}
				// 첫 열인 경우엔 밑으로
				else if (i > 0) params.addRule(RelativeLayout.BELOW, imgView[i-1][j].getId()) ;
				
				if (imgView[i][j] == null) {
					// 할당
					imgView[i][j] =new ImageView (context) ;
					// 클릭 했을 경우
					imgView[i][j].setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
					
							int col =board.getSize().getCol(),
									index =v.getId() -1,
									rowIndex =index /col,
									colIndex =index %col ;
						
							int emptyRow =rowIndex, emptyCol =colIndex ;
							
							// 누른 블록에서 공백 블록을 찾기
							for (int i =0, r, c ; i < DIRECT.DIRECT_SIZE ; i++) {
								// TOP =0 BOTTM =1 LEFT =2 RIGHT =3
								r =rowIndex +DIRECT.direct[i].getRow() ;
								c =colIndex +DIRECT.direct[i].getCol() ;
								
								// 공백 블록 일때
								if (board.getEmptyBlockIndex().equalIndex(r,  c)) {
									emptyRow =r ;
									emptyCol =c ;
									
									// Move Counting
									InfoLayout.setMoveCntView(cntEdit, Integer.parseInt(cntEdit.getText().toString()) +1);
									
									break ;
								}
							}
							
							Block block =board.getBlock(new BlockIndex (rowIndex, colIndex)), 
									emptyBlock =board.getBlock(new BlockIndex (emptyRow, emptyCol)) ;
							//BlockSwap
							BoardMove.blockSwap(block, emptyBlock);
							
							// setImageScale
							// Clicked invalidate
							((ImageView) v).setImageBitmap(block.getImage()) ;
							((ImageView) findViewById (
									(emptyRow *col) +(emptyCol % col) +1))
									.setImageBitmap(emptyBlock.getImage()) ;
						
							imgRelativeLayout.invalidate() ;
						}
					}) ;
					imgView[i][j].setId(viewId) ;
					imgView[i][j].setLayoutParams(params) ;
					imgView[i][j].setAdjustViewBounds(true) ;
					imgView[i][j].setBackgroundResource(R.drawable.frame) ;
				}
			}
		}
		
		// Board set
		board =new Board (new BoardSize (row, col)) ;
		board.setImage(new BoardImage (
				board.getSize(), 
				BitmapFactory.decodeResource(getResources(), R.drawable.raba)).getBoardImage()) ;
		board.setBoardInitial() ;
		new BoardShuffle(board, board.getSize()).setBoardShuffle() ;
		ImageLayout.initImageView(scale, board, imgRelativeLayout, imgView);
	}
	
	// Hint Clicked
	public void hintClick (View v) {
		
		// AStarThread
		this.astarThread =new AStarThread (context, this.board, HINT_RECV, FAIL_RECV, solveHandler) ;
		this.startSolveThread();
	}
	
	// Solve Clicked
	public void solveClick (View v) {
		
		// AStarThread
		this.astarThread =new AStarThread (context, this.board, SOLVE_RECV, FAIL_RECV, solveHandler) ;
		this.startSolveThread();
	}

	// Size Clicked
	public void sizeClick (View v) {
		
		Intent intent =new Intent (MainActivity.this, BoardSizeActivity.class) ;
		intent.putExtra("row", this.board.getSize().getRow()) ;
		intent.putExtra("col", this.board.getSize().getCol()) ;
		startActivity(intent) ;
	}
	
	// Shuffle Clicked
	public void shuffleClick (View v) {
		
		if (this.board != null) {
			new BoardShuffle(this.board, this.board.getSize()).setBoardShuffle() ;
			ImageLayout.setImageView (board, imgRelativeLayout, imgView) ;
			// Init Move Counting
			InfoLayout.setMoveCntView(cntEdit, 0);
			
		}
	}
    
	// Thread Start
	private void startSolveThread () {
		
		this.astarThread.setDaemon(true);
		this.astarThread.setRun(true);
		this.astarThread.start();
	}
	private void startMovingThread () {
		
		this.movingThread.setDaemon (true) ;
		this.movingThread.setRun(true);
		this.movingThread.start() ;
	}
	
	// Thread Stop
	private void stopSolveThread () {
		
		try {
			this.astarThread.setRun(false);
			this.astarThread.interrupt();
			this.astarThread =null ;
		} catch (Exception e) {
			Log.e ("SolveAStarThread", "Release Error") ;
			this.astarThread =null ;
		}
	}
	private void stopMovingThread () {
		
		try {
			this.movingThread.setRun(false);
			this.movingThread.interrupt();
			this.movingThread =null ;
		} catch (Exception e) {
			Log.e ("SolveMovingThread", "Release Error") ;
			this.movingThread =null ;
		}
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if (hasFocus && !isInitialLayout) {
			isInitialLayout =true ;
			
			initBoard (ROW, COL) ;
			InfoLayout.initInfoView(scale, infoRelativeLayout, cntText, cntEdit);
			UserLayout.initBoardUserView(scale, userRelativeLayout, 
					hintBtn, solveBtn, sizeBtn, shuffleBtn, hintImgView);
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
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		return super.onOptionsItemSelected(item);
	}
	
	// Solve 완료 Action
	class SolveAStarReceiver extends BroadcastReceiver {
		public void onReceive (Context context, Intent intent) {
			
			try {	
				// 경로 찾았을 때
				if (pathList == null) pathList =new ArrayList<BlockIndex> () ;
				else if (pathList.size() > 0) pathList.removeAll(pathList) ;
				
				// 풀이 클릭
				if (intent.getAction().equals(SOLVE_RECV)) {
					// Deep Copy All
					pathList.addAll (astarThread.getPathList()) ;
					pathList.remove(0) ;
				}
				// 힌트 클릭
				else if (intent.getAction().equals(HINT_RECV)) 
					// Deep Copy moved 3
					for (int i =1 ; i < astarThread.getPathList().size() ; i++) {
						
						pathList.add(astarThread.getPathList().get(i)) ;
						if (i == 3) break ;
					}
 
				stopSolveThread();
				// Moving
				movingThread =new SolveMovingThread(context, pathList, movingHandler) ;
				startMovingThread() ;
				
			} catch (Exception e) {}
		}
	}
	
	// Solve 실패 Action
	class FailAStarReceiver extends BroadcastReceiver {
		public void onReceive (Context context, Intent intent) {
			
			try {
				// 경로 못찾았을 때
				if (intent.getAction().equals(FAIL_RECV)) {
				
					Toast.makeText(context, "Fail Solve!!\nTry Again an Shuffle Board", Toast.LENGTH_LONG).show() ;
					stopSolveThread();
				}
			} catch (Exception e) {}
		}
	}
	
	// Board Size Action
	class BoardSizeReceiver extends BroadcastReceiver {
		public void onReceive (Context context, Intent intent) {
			
			try {
				
				if (intent.getAction().equals(SIZE_RECV)) {
					
					int row =intent.getExtras().getInt("row"),
							col =intent.getExtras().getInt("col") ;
					
					// Not Update Board Size
					if (board.getSize().equalSize(row, col)) {
						// Board Shuffle
						new BoardShuffle (board, board.getSize()).setBoardShuffle() ;
						ImageLayout.setImageView(board, imgRelativeLayout, imgView);
						// Init Move Counting
						InfoLayout.setMoveCntView(cntEdit, 0);
					}
					// Update Board Size and Update Board
					else initBoard (row, col) ;
				}
			} catch (Exception e) {}
		}
	}
	
	private class SolveAStarHandlerCallback implements Handler.Callback {

		public boolean handleMessage(Message msg) {
			
			switch (msg.what) {
			
			case START :
				
				try {
					
					if (dialog == null) dialog = new ProgressDialog(MainActivity.this);
					dialog.setMessage("풀이 중 입니다.");
					dialog.setCancelable(true);
					dialog.show();
				} catch (Exception e) {}
				
				break ;
				
			case STOP :
				
				try {
					dialog.cancel();
					dialog.dismiss();
					dialog =null ;
				} catch (Exception e) {}
				
				break ;
				
			
			case IS_SHOW :
				
				try {
					// 찾지 못했는데 사용자가 취소 할 때
					if (!dialog.isShowing()) {
						dialog.cancel();
						dialog.dismiss();
						dialog =null ;
						
						// Send FailBoardCast
						sendBroadcast(new Intent (FAIL_RECV)); 
					}
				} catch (Exception e) {}
				
				break ;
			}
			return true ;
		}
	}
	
	private class SolveMovingHandlerCallback implements Handler.Callback {

		public boolean handleMessage(Message msg) {
			
			switch (msg.what) {
			
			case MOVE :
				
				try {
					
					// 공백 블록과 이동 방향의 블록 스왑
					BoardMove.blockSwap(board.getBlock((BlockIndex)msg.obj),
							board.getBlock(board.getEmptyBlockIndex()));
					ImageLayout.setImageView(board, imgRelativeLayout, imgView);
					// Move Counting
					InfoLayout.setMoveCntView(cntEdit, Integer.parseInt(cntEdit.getText().toString()) +1);
					
				} catch (Exception e) {}
				
				break ;
				
			case STOP :
				
				try {
					stopMovingThread() ;
				} catch (Exception e) {}
				
				break ;
			}
			
			return true ;
		}
	}
	
	public void onDestroy () {
		super.onDestroy(); 
		
		try {
			if (astarThread != null) stopSolveThread();
		} catch (Exception e) {}
		
		try {
			if (movingThread != null) stopMovingThread();
		} catch (Exception e) {}
		
		try {
			if (solveRecv != null) {
				unregisterReceiver(solveRecv);
				solveRecv =null ;
			}
		} catch (IllegalArgumentException e) {
			Log.e ("SuccessReceiver", "Unregister Error") ;
			solveRecv =null ;
		}
		
		try {
			if (failRecv != null) {
				unregisterReceiver(failRecv);
				failRecv =null ;
			}
		} catch (IllegalArgumentException e) {
			Log.e ("FailReceiver", "Unregister Error") ;
			failRecv =null ;
		}
		
		try {
			if (sizeRecv != null) {
				unregisterReceiver(sizeRecv);
				sizeRecv =null ;
			}
		} catch (IllegalArgumentException e) {
			Log.e ("BoardSizeReceiver", "Unregister Error") ;
			sizeRecv =null ;
		}
	}
}
