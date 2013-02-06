package se.gizm0.rickitick;

import java.util.ArrayList;
import java.util.Iterator;

import se.gizm0.math.ComplexNumber;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

class JuliaSetIterator implements Iterator<Bitmap>{
	
	public enum Direction{ Forward, Backward}
	
	private static ComplexNumber mC = new ComplexNumber(-0.223, 0.745);
	private int mWidth;
	private int mHeigth;
	
	private double[][] mZ;
	/**
	 * Holds the last iteration of values
	 */
	private ComplexNumber[][] mZCache;
	
	private int mIterationCount;

	private final double MIN_X = -1.5;
	private final double MAX_X = 1.5;
	private final double MIN_Y = -1.5;
	private final double MAX_Y = 1.5;
	
	private static final int MAX_BUFFER_SIZE = 5;
	private final Paint mWhite = new Paint();
	private final Paint mBlack = new Paint();
	
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGTH = 100;
	
	private Paint mColor = new Paint();
	private final float[] hsv = new float[3];
	
	private ArrayList<Bitmap> mImageBuffer;
	private boolean mRevert;
	
	public JuliaSetIterator(){

		mWidth = DEFAULT_WIDTH;
		mHeigth = DEFAULT_HEIGTH;
		
		initFractal();		
		
		mWhite.setColor(0xFFFFFFF);
		mWhite.setAlpha(255);
		mBlack.setColor(0x0000000);
		mBlack.setAlpha(255);
	}
	
	private void initFractal() {

		Color.RGBToHSV(177, 62, 21, hsv);
		mColor.setAlpha(255);
		mZ = new double[mWidth][mHeigth];
		mZCache = new ComplexNumber[mWidth][mHeigth];
		mImageBuffer = new ArrayList<Bitmap>(MAX_BUFFER_SIZE);
		
		mIterationCount = 0;
		mBufferIndex = 0;
	}
	
	public int getWidth() {
		return mWidth;
	}

	public void setWidth(int width) {
		mWidth = width;
	}
	
	public int getHeigth() {
		return mHeigth;
	}

	public void setHeigth(int heigth) {
		mHeigth = heigth;
	}
	
	public boolean hasNext() {
		if (!mRevert){
			return mImageBuffer.size() - (mBufferIndex + 1) >= 0;
		}else{
			return mBufferIndex > 0;
		}
	}

	private int mBufferIndex;
	public Bitmap next() {
		Log.d(FractalActivity.RICKITICK_LOG_TAG, "index: " + mBufferIndex + " size(): " +mImageBuffer.size());
		Bitmap b = mImageBuffer.get(mBufferIndex);
		updateIterationCount();
		return b;
	}

	private void updateIterationCount() {
		if (!mRevert){
			if (mBufferIndex < MAX_BUFFER_SIZE){
				mBufferIndex++;
			}
			mIterationCount++;
		}else{
			if (mBufferIndex > 0){
				mBufferIndex--;
			}
		}
	}
	
	public void buffer(int nrOfBuffers, int nrOfStepsPerBuffer){
		for (int i = 0; i < nrOfBuffers; i++) {
			runSteps(nrOfStepsPerBuffer);
			mIterationCount += nrOfStepsPerBuffer;
			addToBuffer();
		}
	}

	private void runSteps(int nrOfSteps){
		// Go through each pixel.
		for(int x=0; x < mWidth; x++){
			for(int y=0; y < mHeigth; y++){
				
				if (mZCache[x][y] == null){
					mZCache[x][y] = getStartValue(x, y);
				}
				
				// fill the boolean array.
				mZ[x][y] = getJuliaSetFor(x, y, nrOfSteps);
			}
		}
	}
	
	private ComplexNumber getStartValue(int x, int y){
		double a = (double)x*(MAX_X-MIN_X)/(double)mWidth + MIN_X;
		double b = (double)y*(MAX_Y-MIN_Y)/(double)mHeigth + MIN_Y;
		return new ComplexNumber(a, b);
	}
	
	/**
	 * This is the basic quadratic julia set.  The formula is:
     * f(z+1) = z^2+c
	 * @param cn
	 * @return
	 */
	private double getJuliaSetFor(int x, int y, int nrOfSteps){
		for (int i = 0; i < nrOfSteps; i++){
			mZCache[x][y] = mZCache[x][y].square().add(mC);
		}
		// If the threshold^2 is larger than the magnitude, return true.
		return mZCache[x][y].magnitude();
	}

	private void addToBuffer() {
		Bitmap b = Bitmap.createBitmap(mWidth, mHeigth, Bitmap.Config.ARGB_8888);;
		Canvas canvas = new Canvas(b);

		for(int x = 0; x < mWidth; x++){
			for(int y = 0; y < mHeigth; y++){
				canvas.drawPoint(x, y, getColor( mZ[x][y])  );
			}
		}
		
		if (mImageBuffer.size() >= MAX_BUFFER_SIZE){
			mImageBuffer.remove(0);
			mBufferIndex--;
		}
		mImageBuffer.add(b);
	}

	private Paint getColor(double z){
		float xt = (mIterationCount);
		if (z > xt ){
			return mBlack;
		}
		
		double y = (double)(Math.sqrt(z))/(Math.sqrt(xt));
		
		hsv[2] = y > 1 ? 1 : (float)y;
		
		mColor.setColor(Color.HSVToColor(255, hsv));
		return mColor;
	}

	public void remove(){}
	
	/**
	 * This will force the current iteration start from beginning and 
	 * redraw it with any new size.
	 */
	public void reset(){
		initFractal();
	}

	public Direction getDirection(){
		return mRevert ? Direction.Backward : Direction.Forward;
	}
	
	public void toogleDirection() {
		mRevert = !mRevert;
		if (mRevert){
			if (mBufferIndex == mImageBuffer.size()){
				mBufferIndex--;
			} 
		}
	}

	public int getBufferSize() {
		return mImageBuffer.size();
	}
}