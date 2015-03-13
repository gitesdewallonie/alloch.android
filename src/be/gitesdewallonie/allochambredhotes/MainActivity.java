package be.gitesdewallonie.allochambredhotes;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.InputType;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import be.gitesdewallonie.allochambredhotes.KeyboardDetectorRelativeLayout.IKeyboardChanged;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MainActivity extends Activity {

	private KeyboardDetectorRelativeLayout rootLayout;
	private ScrollView scroller;
	Handler handler = new Handler();
	Boolean gpschoicemade = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		scroller = (ScrollView) findViewById(R.id.scrollView1);
		rootLayout = (KeyboardDetectorRelativeLayout) findViewById(R.id.rootlayout);
		rootLayout.addKeyboardStateChangedListener(new IKeyboardChanged() {

			@Override
			public void onKeyboardShown() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onKeyboardHidden() {
				// TODO Auto-generated method stub
				scroller.pageScroll(View.FOCUS_UP);
			}
		});
		ImageView logo1 = (ImageView) findViewById(R.id.img_logo1);
		ImageView logo2 = (ImageView) findViewById(R.id.img_logo2);
		ImageView logo3 = (ImageView) findViewById(R.id.img_logo3);
		logo1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "http://www.tourismewallonie.be";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				MainActivity.this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}
		});
		logo2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "http://www.wallonietourisme.be";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				MainActivity.this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}
		});
		logo3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "http://ec.europa.eu/agriculture/rurdev/index_fr.htm";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				MainActivity.this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Display display = getWindowManager()
								.getDefaultDisplay();
						int width = display.getWidth();
						int height = display.getHeight();
						View v1 = findViewById(R.id.layout1);
						View v2 = findViewById(R.id.layout2);
						LinearLayout.LayoutParams p1 = (LayoutParams) v1.getLayoutParams();
						int[] x = new int[2];
                        View cred = findViewById(R.id.creditbar);
                        LinearLayout.LayoutParams creditbar = (LayoutParams) cred.getLayoutParams();
						v1.getLocationOnScreen(x);
                        p1.height = (int) (height - x[1] - scroller.getScrollY() - cred.getHeight());
						v1.setLayoutParams(p1);
                        LinearLayout.LayoutParams p2 = (LayoutParams) v2.getLayoutParams();
                        v2.setLayoutParams(p2);
					}
				});
			}
		}).start();
		CheckInternetConnection();
	}

	void CheckInternetConnection() {
		if (!Lib.isConnected(this)) {
			new AlertDialog.Builder(MainActivity.this)
					.setMessage(getString(R.string.no_connection))
					.setPositiveButton(getResources().getString(R.string.exit),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							})
					.setNegativeButton(getResources().getString(R.string.try_again),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									CheckInternetConnection();
								}
							}).show();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		rootLayout.requestFocus();
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();
		View v1 = findViewById(R.id.layout1);
		View v2 = findViewById(R.id.layout2);
		LinearLayout.LayoutParams p1 = (LayoutParams) v1.getLayoutParams();
		int[] x = new int[2];
		v1.getLocationOnScreen(x);
        View cred = findViewById(R.id.creditbar);
        LinearLayout.LayoutParams creditbar = (LayoutParams) cred.getLayoutParams();
        p1.height = (int) (height - x[1] - scroller.getScrollY() - cred.getHeight());
		v1.setLayoutParams(p1);
        LinearLayout.LayoutParams p2 = (LayoutParams) v2.getLayoutParams();
        v2.setLayoutParams(p2);
		Button btn1 = (Button) findViewById(R.id.button1);
		final Button btn2 = (Button) findViewById(R.id.button2);
		Button btn3 = (Button) findViewById(R.id.button3);
		final EditText edit = (EditText) findViewById(R.id.editText1);
		edit.clearFocus();
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String provider = Settings.Secure.getString(
						getContentResolver(),
						Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

				if (!Lib.isConnected(MainActivity.this)) {
					CheckInternetConnection();
					return;
				}

				if (!gpschoicemade && provider != null && !provider.contains("gps")) {
						// Notify users and show settings if they want to enable
						// GPS
						new AlertDialog.Builder(MainActivity.this)
								.setMessage(getResources().getString(R.string.gps_switched_off))
								.setPositiveButton(getResources().getString(R.string.enable_gps),
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												gpschoicemade = true;
												Intent intent = new Intent(
														Settings.ACTION_LOCATION_SOURCE_SETTINGS);
												startActivityForResult(intent,
														5);
											}
										})
								.setNegativeButton(getResources().getString(R.string.dont),
										new DialogInterface.OnClickListener() {

											public void onClick(
													DialogInterface dialog,
													int which) {
												gpschoicemade = true;
                                                Intent intent = new Intent(MainActivity.this,
                                                        SearchResultActivity.class);
                                                startActivity(intent);
                                                MainActivity.this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
											}
										}).show();
				    return;
				}
				Intent intent = new Intent(MainActivity.this,
						SearchResultActivity.class);
				startActivity(intent);
				MainActivity.this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}
		});
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!Lib.isConnected(MainActivity.this)) {
					CheckInternetConnection();
					return;
				}
				String key = edit.getText().toString();
				if (key == null || key.trim().length() == 0) {
					AlertDialog alertDialog1 = new AlertDialog.Builder(
							MainActivity.this).create();

					alertDialog1.setTitle(R.string.opps);
					alertDialog1.setMessage(getResources().getString(
							R.string.you_much_enter_address));
					alertDialog1.setButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

								}
							});

					// Showing Alert Message
					alertDialog1.show();
					return;
				}
				Intent intent = new Intent(MainActivity.this,
						SearchResultActivity.class);
				intent.putExtra("key", key);
				startActivity(intent);
				MainActivity.this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}
		}); 
		edit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (event != null) {
					btn2.performClick();
				}
				return false;
			}
		});
		//edit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
		ImageView img = (ImageView) findViewById(R.id.imageView1);
		img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						CreditActivity.class);
				startActivity(intent);
				MainActivity.this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}
		});

		File cacheDir = StorageUtils.getCacheDirectory(this);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).memoryCacheExtraOptions(480, 800)
				.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null)
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 1)
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.discCache(new UnlimitedDiscCache(cacheDir))
				.imageDownloader(new BaseImageDownloader(this)) // default
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
		btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainActivity.this,
						CreditActivity.class);
				startActivity(intent);
				MainActivity.this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(MainActivity.this, CreditActivity.class);
			startActivity(intent);
			MainActivity.this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks whether a hardware keyboard is available
		if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
			Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
		} else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
			Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
		}
	}
}
