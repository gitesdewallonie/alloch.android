package be.gitesdewallonie.allochambresdhotes;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Intent;
import android.view.MotionEvent;
 
public class SplashActivity extends Activity {
	protected boolean _active = true;
	protected int _splashTime = 2000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (_active && (waited < _splashTime)) {
						sleep(100);
						if (_active) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
					// do nothing
				} finally {
					try {
						Intent intent = new Intent(SplashActivity.this,
								MainActivity.class);
						startActivity(intent);
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								finish();
							}
						}, 2000);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		splashTread.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			_active = false;
		}
		return true;
	}
}
