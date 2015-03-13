package be.gitesdewallonie.allochambredhotes;

import android.app.Activity;
import android.os.Bundle;

public class CreditActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credit);
	}
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);   
	}
}
