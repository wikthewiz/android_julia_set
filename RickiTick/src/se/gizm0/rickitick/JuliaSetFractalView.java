package se.gizm0.rickitick;

import se.gizm0.rickitick.JuliaSetIterator.Direction;
import se.gizm0.rickitick.util.ShowProgress;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class JuliaSetFractalView extends View{
	
	private static final int BORDER_SIZE = 4;
	
	
	/**
	 * Used to manage the animation by sending messages to itself when calling sleep.
	 */
	private RefreshHandler mUpdater = new RefreshHandler();
	private Paint mBorder;
	private Paint mFractalPainter;
	
	/**
	 * This is the handler to call this views update
	 */
	private class RefreshHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			JuliaSetFractalView.this.update();
			JuliaSetFractalView.this.invalidate();
		}
		
		public void sleep(long delayMillis){
        	this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
		}
	}
	
	private enum Mode{ Running, Paused }	
	private Mode mMode;
	private JuliaSetIterator mJuluaSetIterator;
	private long mLastIterate;
	private long mIterateDelay = 500;

	public JuliaSetFractalView(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs,defStyle);
		initView();
	}

	public JuliaSetFractalView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

    private void initView() {
		// Initiate with default width and height. We will know
		// what size this view will have on onSizeChanged
		mJuluaSetIterator = new JuliaSetIterator();  
		
		mMode = Mode.Paused;
		mFractalPainter = new Paint();
		mFractalPainter.setAlpha(255);
		
		mBorder = new Paint();
		mBorder.setStyle(Style.STROKE);
		mBorder.setColor(0x2A8E16);
		mBorder.setStrokeWidth(BORDER_SIZE);
		mBorder.setAlpha(255);
	}
    
	public void update(){
		if (mMode == Mode.Running){
			long now = System.currentTimeMillis();
			long dif = now - mLastIterate;
			long nextUpdate = dif;
			if ( dif > mIterateDelay){
				if (!mJuluaSetIterator.hasNext()){
					mJuluaSetIterator.toogleDirection();
				}
				nextUpdate = mIterateDelay;
			}
			mUpdater.sleep(nextUpdate);
		}
	}
	
	public void stop(){
		if (mMode != Mode.Paused){
			mMode = Mode.Paused;
		}
	}
	
	public void stepOne(){
		forceFwd();
		
		if (!mJuluaSetIterator.hasNext()){
			new ShowProgress(
					getBufferRunner(1,1),	// The work to be done during progress bar			
					getContext(), 			// The context to run in		
					new Handler(new Handler.Callback() {
						public boolean handleMessage(Message msg) {
							invalidate();
							return true;
						}
					}), 					// The callback. This will start animation
					"Rickitick",			// Title
					"Buffering ... "		// Description
					);
		}else{
			invalidate();
		}
	}

	private void forceFwd() {
		if (mJuluaSetIterator.getDirection() == Direction.Backward) {
			mJuluaSetIterator.toogleDirection();
		}
	}
	
		
	public void start(){
		if (mMode != Mode.Running){
			if (needBuffering()){
				new ShowProgress(
						getBufferRunner(5,4),	// The work to be done during progress bar			
						getContext(), 			// The context to run in		
						mUpdater, 				// The callback. This will start animation
						"Rickitick",			// Title
						"Buffering ... "		// Description
						);
			}
			else{
				mUpdater.sendEmptyMessage(0);
			}

			mMode = Mode.Running;
		}
	}

	private boolean needBuffering() {
		return mJuluaSetIterator.getBufferSize() < 5;
	}

	private Runnable getBufferRunner(final int nrOfBuffers, final int nrOfStepsPerBuffer) {
		return new Runnable() { 
			public void run() {
				mJuluaSetIterator.buffer(nrOfBuffers,nrOfStepsPerBuffer);
			}
		};
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mJuluaSetIterator.setWidth(w - BORDER_SIZE * 2);
		mJuluaSetIterator.setHeigth(h - BORDER_SIZE * 2);
		mJuluaSetIterator.reset();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.d(FractalActivity.RICKITICK_LOG_TAG, "" +  getWidth() +"," + getHeight());
		if (mJuluaSetIterator.getBufferSize() == 0){
			Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.rickitick_img);
			canvas.drawBitmap(b, BORDER_SIZE, BORDER_SIZE, mFractalPainter );
		}else{
			canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), mBorder);
			canvas.drawBitmap(mJuluaSetIterator.next(), BORDER_SIZE, BORDER_SIZE, mFractalPainter);
		}
	}
}