package be.gitesdewallonie.allochambredhotes;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import be.gitesdewallonie.allochambredhotes.RealViewSwitcher.OnScreenSwitchListener;
import be.gitesdewallonie.allochambredhotes.classes.CurrentLocation;
import be.gitesdewallonie.allochambredhotes.classes.Place;

public class PlaceDetailsActivity extends Activity {
	private ScrollView scroll;
	private Place place;
	private CurrentLocation location;
	int max = 0;

	// List<Imageview>
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_details);
		place = (Place) getIntent().getSerializableExtra("place");
		location = (CurrentLocation) getIntent().getSerializableExtra(
				"location");
		final List<ImageView> imgs = new ArrayList<ImageView>();
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = width * 100 / 150;
		final ImageLoader imageLoader = ImageLoader.getInstance();
		RealViewSwitcher real = (RealViewSwitcher) findViewById(R.id.readview);
		real.setOnScreenSwitchListener(new OnScreenSwitchListener() {

			@Override
			public void onScreenSwitched(int screen) {
				// TODO Auto-generated method stub
				if (screen > max) {
					max = screen;
					imageLoader.displayImage(place.photos[max], imgs.get(max));
				}
			}
		});
		for (int i = 0; i < place.photos.length; i++) {
			ImageView img = new ImageView(this);
			img.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
			img.setScaleType(ScaleType.FIT_XY);
			if (i == 0)
				imageLoader.displayImage(place.photos[i], img);
			// img.setImageResource(R.drawable.logo1);
			imgs.add(img);
			real.addView(img);
		}
		scroll = (ScrollView) findViewById(R.id.detailsscroller);
		real.setOnTouchListener(new OnTouchListener() {

			private float lastx;
			private float lasty;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					lastx = event.getX();
					lasty = event.getY();
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (Math.abs(event.getY() - lasty) > Math.abs(event.getX()
							- lastx))
						scroll.requestDisallowInterceptTouchEvent(false);
					else
						scroll.requestDisallowInterceptTouchEvent(true);
				}
				return false;
			}
		});
		TextView txt_numberOfPeople = (TextView) findViewById(R.id.txt_numberOfPeople);
		TextView txt_NumberOfBed = (TextView) findViewById(R.id.txt_NumberOfBed);
		TextView txt_NumberOfSingleBed = (TextView) findViewById(R.id.txt_NumberOfSingleBed);
		TextView txtPrice = (TextView) findViewById(R.id.txtPrice);
		TextView txt_address = (TextView) findViewById(R.id.txt_address);
		TextView txt_description = (TextView) findViewById(R.id.txt_description);

		ImageView img3 = (ImageView) findViewById(R.id.img_logo2);
		ImageView img4 = (ImageView) findViewById(R.id.img_logo3);

		txt_numberOfPeople.setText(place.capacity_min + "/"
				+ place.capacity_max);
		if (place.two_person_bed > 0)
			txt_NumberOfBed.setText("" + place.two_person_bed);
		else {
			txt_NumberOfBed.setVisibility(View.GONE);
			img3.setVisibility(View.GONE);
		}
		if (place.one_person_bed > 0)
			txt_NumberOfSingleBed.setText("" + place.one_person_bed);
		else {
			txt_NumberOfSingleBed.setVisibility(View.GONE);
			img4.setVisibility(View.GONE);
		}
		txtPrice.setText("" + place.price + "Û");
		txt_address.setText(place.owner.title + " " + place.owner.name + "\n"
				+ place.address.address + "\n" + place.address.zip + ", "
				+ place.address.city);
		txt_description.setText(place.description);
		ImageView img_call = (ImageView) findViewById(R.id.img_call);
		ImageView img_navigation = (ImageView) findViewById(R.id.img_navigation);
		img_call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String phone = null;
				// if (!Lib.isNullOrEmpty(place.owner.mobile))
				// phone = place.owner.mobile;
				if (!Lib.isNullOrEmpty(place.owner.phone))
					phone = place.owner.phone;
				final String PHONE = phone;
				AlertDialog alertDialog1 = new AlertDialog.Builder(
						PlaceDetailsActivity.this).create();
				if (PHONE != null) {
					alertDialog1.setTitle(getResources().getString(R.string.call));
					alertDialog1.setMessage("" + PHONE);
					alertDialog1.setButton(getResources().getString(R.string.yes),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

									Intent callIntent = new Intent(
											Intent.ACTION_CALL);
									callIntent.setData(Uri
											.parse("tel:" + PHONE));
									startActivity(callIntent);
								}
							});
					alertDialog1.setButton2(getResources().getString(R.string.no),
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
				} else {
					alertDialog1.setTitle(R.string.error);
					alertDialog1.setMessage(getResources().getString(
							R.string.no_phone_specified));
					alertDialog1.setButton("OK",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
				}
				// Showing Alert Message
				alertDialog1.show();
			}
		});
		img_navigation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String uri = "http://maps.google.com/maps?saddr="
						+ location.coordinates[0] + ","
						+ location.coordinates[1] + "&daddr=" + place.longitude
						+ "," + place.latitude;
				startActivity(new Intent(android.content.Intent.ACTION_DEFAULT,
						Uri.parse(uri)));
				PlaceDetailsActivity.this.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
			}
		});
	}
	@Override
	public void onBackPressed() {
	    super.onBackPressed();
	    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);   
	}
}
