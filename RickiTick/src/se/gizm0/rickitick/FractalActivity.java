package se.gizm0.rickitick;



import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/** 
 * This is the main activity
 */
public class FractalActivity extends Activity {

	public static final String RICKITICK_LOG_TAG = "rickitick";
	private JuliaSetFractalView mJuliaView;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mJuliaView = (JuliaSetFractalView) findViewById(R.id.julia_set_fractal);
    }
    
    @Override
    protected void onStart() {
    	super.onResume();
    	
    	readyToStartButtonSetup();
    }    
    @Override
    protected void onStop() {
    	super.onStop();
    	mJuliaView.stop(); // stop activities when we don't display
    	
    	readyToStartButtonSetup();
    }
    
    public void onStartClick(View view){
    	mJuliaView.start();
    	
    	readyToStopButtonSetup();
    }

    public void onStopClick(View view){ 
    	mJuliaView.stop();
    	
    	readyToStartButtonSetup();
    }
    
    public void onStepOneClick(View view){
    	mJuliaView.stepOne();
    	
    	readyToStartButtonSetup();
    }

	private void readyToStartButtonSetup() {
		((Button) findViewById(R.id.step_button)).setEnabled(true);
    	((Button) findViewById(R.id.stop_button)).setEnabled(false);
    	((Button) findViewById(R.id.start_button)).setEnabled(true);
	}
	
	private void readyToStopButtonSetup() {
		((Button) findViewById(R.id.step_button)).setEnabled(false);
    	((Button) findViewById(R.id.stop_button)).setEnabled(true);
    	((Button) findViewById(R.id.start_button)).setEnabled(false);
	}
    
}