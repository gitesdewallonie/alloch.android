package be.gitesdewallonie.allochambresdhotes;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import be.gitesdewallonie.allochambresdhotes.classes.CurrentLocation;
import be.gitesdewallonie.allochambresdhotes.classes.MyItemizedOverlay;
import be.gitesdewallonie.allochambresdhotes.classes.Place;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class SearchResultActivity extends MapActivity {
	ArrayList<JsonElement> items = new ArrayList<JsonElement>();
	private MapController controller;
	private MapView mapview;
	private ListView listview;
	Handler handler = new Handler();
	CurrentLocation location;
	Gson gson = new Gson();
	private MyLocationOverlay overlay;
	private ProgressDialog dialog;
	Boolean found = false;

	private void initMyLocation() {
		final ProgressDialog dialog = ProgressDialog.show(this,
				getString(R.string.find_your_location), "Please wait...");
		overlay = new MyLocationOverlay(this, mapview);
		overlay.enableMyLocation();
		mapview.getOverlays().add(overlay);
		overlay.runOnFirstFix(new Runnable() {
			public void run() {
				// controller.setZoom(15);
				found = true;
				dialog.dismiss();
				controller.animateTo(overlay.getMyLocation());
				StartSearch(((double) overlay.getMyLocation().getLatitudeE6())
						/ 1e6 + ","
						+ ((double) overlay.getMyLocation().getLongitudeE6())
						/ 1e6);
			}
		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (location == null)
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								dialog.dismiss();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
			}
		}).start();
	}

	void CheckInternetConnection() {
		if (!Lib.isConnected(this)) {
			new AlertDialog.Builder(SearchResultActivity.this)
					.setMessage(getString(R.string.no_connection))
					.setPositiveButton("Exit",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							})
					.setNegativeButton("Try again",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									CheckInternetConnection();
								}
							}).show();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (overlay != null)
			overlay.disableMyLocation();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		mapview = (MapView) findViewById(R.id.mapview);
		listview = (ListView) findViewById(R.id.listView1);
		controller = mapview.getController();
		mapview.setSatellite(false);
		mapview.setBuiltInZoomControls(true);
		String key = getIntent().getStringExtra("key");
		if (key == null) {
			initMyLocation();
		} else {
			StartSearch(key);
		}

	}

	private void StartSearch(final String key) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				dialog = ProgressDialog.show(SearchResultActivity.this,
						getString(R.string.loading), "Please wait...");
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String url = "http://www.allochambredhotes.be/getMobileClosestHebs?LANGUAGE="
							+ getString(R.string.url_language)
							+ "&address="
							+ URLEncoder.encode(key);
					String x = Lib.GetHtml(url);
					// x =
					// "{\"search_location\": {\"coordinates\": [50.400500999999998, 5.1335125000000001], \"title\": \"R\u00e9gion wallonne, Belgique\"}, \"results\": [[{\"two_person_bed\": 0, \"name\": \"Charmes d'H\u00f4tes Valentine\", \"classification\": [4], \"child_bed\": 0, \"price\": \"80.00\", \"capacity_max\": 2, \"room_number\": 1, \"thumb\": \"http://www.allochambredhotes.be/vignettes_heb/CHECR61028188400.jpg\", \"longitude\": 50.529046999999998, \"owner\": {\"website\": \"www.charmesdhotes.com\", \"language\": \"FR - NL - EN\", \"fax\": \"\", \"name\": \"MOLS\", \"firstname\": \"Didier\", \"title\": \"M. & Mme\", \"mobile\": \"0475 809 780\", \"email\": \"info@charmesdhotes.com\", \"phone\": \"085 23 03 33\"}, \"capacity_min\": 2, \"address\": {\"town\": \"Couthuin\", \"city\": \"Heron\", \"zip\": \"4218\", \"address\": \"Place Communale, 9\"}, \"latitude\": 5.1401389999999996, \"distribution\": \"1st floor : 1 room (2x1p, bath room, toilets). garden : lawn, terrace, outdoor games\r\n\r\n\r\n\", \"photos\": [\"http://www.allochambredhotes.be/photos_heb/CHECR61028188400.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188401.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188402.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188403.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188404.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188405.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188406.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188407.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188408.jpg\"], \"type\": \"Bed & breakfast\", \"additionnal_bed\": 0, \"one_person_bed\": 2, \"description\": \"Guestroom 4 corn ears. Guest house of character ideally located, near Huy, in an old brewery listed as part of the Walloon heritage. Three charming and very comfortable rooms provide the perfect setting to assure the success of your business or tourist trip. \r\n\"}, {\"two_person_bed\": 1, \"name\": \"Charmes d'H\u00f4tes Celestine\", \"classification\": [4], \"child_bed\": 0, \"price\": \"80.00\", \"capacity_max\": 3, \"room_number\": 1, \"thumb\": \"http://www.allochambredhotes.be/vignettes_heb/CHECR61028188600.jpg\", \"longitude\": 50.529046999999998, \"owner\": {\"website\": \"www.charmesdhotes.com\", \"language\": \"FR - NL - EN\", \"fax\": \"\", \"name\": \"MOLS\", \"firstname\": \"Didier\", \"title\": \"M. & Mme\", \"mobile\": \"0475 809 780\", \"email\": \"info@charmesdhotes.com\", \"phone\": \"085 23 03 33\"}, \"capacity_min\": 3, \"address\": {\"town\": \"Couthuin\", \"city\": \"Heron\", \"zip\": \"4218\", \"address\": \"Place Communale, 9\"}, \"latitude\": 5.1401389999999996, \"distribution\": \"1st floor: 1 room (1x2p., bath room, toilets). garden : lawn, terrace, outdoor games\r\n\", \"photos\": [\"http://www.allochambredhotes.be/photos_heb/CHECR61028188600.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188601.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188602.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188603.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188604.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188605.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188606.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188607.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188608.jpg\"], \"type\": \"Bed & breakfast\", \"additionnal_bed\": 0, \"one_person_bed\": 0, \"description\": \"Guestroom 4 corn ears. Guest house of character ideally located, near Huy, in an old brewery listed as part of the Walloon heritage. Three charming and very comfortable rooms provide the perfect setting to assure the success of your business or tourist trip. \"}, {\"two_person_bed\": 0, \"name\": \"Charmes d'H\u00f4tes Pauline\", \"classification\": [4], \"child_bed\": 0, \"price\": \"\", \"capacity_max\": 1, \"room_number\": 1, \"thumb\": \"http://www.allochambredhotes.be/vignettes_heb/CHECR61028188500.jpg\", \"longitude\": 50.529046999999998, \"owner\": {\"website\": \"www.charmesdhotes.com\", \"language\": \"FR - NL - EN\", \"fax\": \"\", \"name\": \"MOLS\", \"firstname\": \"Didier\", \"title\": \"M. & Mme\", \"mobile\": \"0475 809 780\", \"email\": \"info@charmesdhotes.com\", \"phone\": \"085 23 03 33\"}, \"capacity_min\": 1, \"address\": {\"town\": \"Couthuin\", \"city\": \"Heron\", \"zip\": \"4218\", \"address\": \"Place Communale, 9\"}, \"latitude\": 5.1401389999999996, \"distribution\": \"1st floor : 1 room (1x1p, bath room, toilets). garden : lawn, terrace, outdoor games\r\n\r\n\", \"photos\": [\"http://www.allochambredhotes.be/photos_heb/CHECR61028188500.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188501.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188502.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188503.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188504.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188505.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188506.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188507.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188508.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188509.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188510.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61028188511.jpg\"], \"type\": \"Bed & breakfast\", \"additionnal_bed\": 0, \"one_person_bed\": 1, \"description\": \"Guestroom 4 corn ears. Guest house of character ideally located, near Huy, in an old brewery listed as part of the Walloon heritage. Three charming and very comfortable rooms provide the perfect setting to assure the success of your business or tourist trip. \r\n\"}], {\"two_person_bed\": 1, \"name\": \"L'Ecluse\", \"classification\": [2], \"child_bed\": 1, \"price\": \"60.00\", \"capacity_max\": 5, \"room_number\": 1, \"thumb\": \"http://www.allochambredhotes.be/vignettes_heb/CHECR9200397900.jpg\", \"longitude\": 50.491771, \"owner\": {\"website\": \"\", \"language\": \"FR\", \"fax\": \"\", \"name\": \"HENRIETTE\", \"firstname\": \"Georges\", \"title\": \"M.\", \"mobile\": \"0473 380 927\", \"email\": \"mieke.delory@skynet.be\", \"phone\": \"081 58 82 97\"}, \"capacity_min\": 3, \"address\": {\"town\": \"Sclayn\", \"city\": \"Andenne\", \"zip\": \"5300\", \"address\": \"rue du Bord de l'Eau, 250\"}, \"latitude\": 5.0293089999999996, \"distribution\": \"3nd fl. : 1 room (1x1p., 1x2p., shower room, toilets). garden : terrace, bbq.\", \"photos\": [\"http://www.allochambredhotes.be/photos_heb/CHECR9200397900.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397901.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397902.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397903.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397904.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397905.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397906.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397907.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397908.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397909.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397910.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397911.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR9200397912.jpg\"], \"type\": \"Bed & breakfast\", \"additionnal_bed\": 0, \"one_person_bed\": 1, \"description\": \"Guestroom 2 corn ears. Guest rooms (two rooms separated by a large living room) in the Meuse valley near Andenne \u2013 fitted in the air-conditioned attics with a view over the Meuse and the Ravel cycling path.- Warm-hearted family welcome with possibility of extra beds.\"}, [{\"two_person_bed\": 1, \"name\": \"Le Bouchat-Oreille - Le 4\", \"classification\": [4], \"child_bed\": 0, \"price\": \"95.00\", \"capacity_max\": 2, \"room_number\": 1, \"thumb\": \"http://www.allochambredhotes.be/vignettes_heb/CH91141194200.jpg\", \"longitude\": 50.313955, \"owner\": {\"website\": \"www.bouchat-oreille.be\", \"language\": \"FR - EN\", \"fax\": \"083 48 05 27\", \"name\": \"MERSCH\", \"firstname\": \"Michel\", \"title\": \"M. & Mme\", \"mobile\": \"0479 967 441\", \"email\": \"info@bouchat-oreille.be\", \"phone\": \"\"}, \"capacity_min\": 2, \"address\": {\"town\": \"Spontin\", \"city\": \"Yvoir\", \"zip\": \"5530\", \"address\": \"rue du Bouchat, 34 A\"}, \"latitude\": 5.0058889999999998, \"distribution\": \"ground floor : 1 room (2x1p., shower room, toilets). garden : lawn, terrace, swimming pool, whirlpool.\", \"photos\": [\"http://www.allochambredhotes.be/photos_heb/CH91141194200.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194201.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194202.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194203.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194204.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194205.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194206.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194207.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194208.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194209.jpg\"], \"type\": \"Bed & breakfast\", \"additionnal_bed\": 0, \"one_person_bed\": 2, \"description\": \"Guestroom 4 corn ears. Converted guest house with four charming rooms in an enchanting setting that overlooks the village of Spontin. Ideal for visiting and for sports activities. Dinant, Namur, the la Molign\u00e9e valley, Maredsous, kayaking on the river Lesse.\r\n\r\n\"}, {\"two_person_bed\": 1, \"name\": \"Le Bouchat-Oreille - Bambou\", \"classification\": [4], \"child_bed\": 0, \"price\": \"105.00\", \"capacity_max\": 2, \"room_number\": 4, \"thumb\": \"http://www.allochambredhotes.be/vignettes_heb/CH91141194000.jpg\", \"longitude\": 50.313955, \"owner\": {\"website\": \"www.bouchat-oreille.be\", \"language\": \"FR - EN\", \"fax\": \"083 48 05 27\", \"name\": \"MERSCH\", \"firstname\": \"Michel\", \"title\": \"M. & Mme\", \"mobile\": \"0479 967 441\", \"email\": \"info@bouchat-oreille.be\", \"phone\": \"\"}, \"capacity_min\": 2, \"address\": {\"town\": \"Spontin\", \"city\": \"Yvoir\", \"zip\": \"5530\", \"address\": \"rue du Bouchat, 34 A\"}, \"latitude\": 5.0058889999999998, \"distribution\": \"ground floor : 1 room (1x2p., bath room, toilets). garden : lawn, terrace, swimming pool, whirlpool.\", \"photos\": [\"http://www.allochambredhotes.be/photos_heb/CH91141194000.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194001.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194002.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194003.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194004.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194005.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194006.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194007.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194008.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194009.jpg\"], \"type\": \"Bed & breakfast\", \"additionnal_bed\": 0, \"one_person_bed\": 0, \"description\": \"Guestroom 4 corn ears. Converted guest house with four charming rooms in an enchanting setting that overlooks the village of Spontin. Ideal for visiting and for sports activities. Dinant, Namur, the la Molign\u00e9e valley, Maredsous, kayaking on the river Lesse.\r\n\r\n\"}, {\"two_person_bed\": 1, \"name\": \"Le Bouchat-Oreille - L'Oliveraie\", \"classification\": [4], \"child_bed\": 0, \"price\": \"100.00\", \"capacity_max\": 2, \"room_number\": 1, \"thumb\": \"http://www.allochambredhotes.be/vignettes_heb/CH91141194100.jpg\", \"longitude\": 50.313955, \"owner\": {\"website\": \"www.bouchat-oreille.be\", \"language\": \"FR - EN\", \"fax\": \"083 48 05 27\", \"name\": \"MERSCH\", \"firstname\": \"Michel\", \"title\": \"M. & Mme\", \"mobile\": \"0479 967 441\", \"email\": \"info@bouchat-oreille.be\", \"phone\": \"\"}, \"capacity_min\": 2, \"address\": {\"town\": \"Spontin\", \"city\": \"Yvoir\", \"zip\": \"5530\", \"address\": \"rue du Bouchat, 34 A\"}, \"latitude\": 5.0058889999999998, \"distribution\": \"ground floor : 1 room (1x2p., shower room, toilets). garden : lawn, terrace, swimming pool, whirlpool.\", \"photos\": [\"http://www.allochambredhotes.be/photos_heb/CH91141194100.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194101.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194102.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194103.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194104.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194105.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194106.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194107.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194108.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH91141194109.jpg\"], \"type\": \"Bed & breakfast\", \"additionnal_bed\": 0, \"one_person_bed\": 0, \"description\": \"Guestroom 4 corn ears. Converted guest house with four charming rooms in an enchanting setting that overlooks the village of Spontin. Ideal for visiting and for sports activities. Dinant, Namur, the la Molign\u00e9e valley, Maredsous, kayaking on the river Lesse.\r\n\r\n\"}], {\"two_person_bed\": 1, \"name\": \"Chez Marie\", \"classification\": [2], \"child_bed\": 0, \"price\": \"66.00\", \"capacity_max\": 3, \"room_number\": 1, \"thumb\": \"http://www.allochambredhotes.be/vignettes_heb/CHECR61041145000.jpg\", \"longitude\": 50.509210000000003, \"owner\": {\"website\": \"www.lecoleduvillage.be\", \"language\": \"FR - NL - EN\", \"fax\": \"085 51 26 96\", \"name\": \"VERLAINE\", \"firstname\": \"Michel\", \"title\": \"M. & Mme\", \"mobile\": \"0494 596 670\", \"email\": \"verlaine.michel@skynet.be\", \"phone\": \"085 51 26 35\"}, \"capacity_min\": 2, \"address\": {\"town\": \"Rausa\", \"city\": \"Modave\", \"zip\": \"4577\", \"address\": \"rue Rausa, 13\"}, \"latitude\": 5.3263059999999998, \"distribution\": \"1st fl. : 1 room (1x2p., 1x1p., shower room, toilets). garden, lawn, orchard, outdoor games, bbq, table tennis.\", \"photos\": [\"http://www.allochambredhotes.be/photos_heb/CHECR61041145000.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61041145001.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61041145002.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61041145003.jpg\", \"http://www.allochambredhotes.be/photos_heb/CHECR61041145004.jpg\"], \"type\": \"Bed & breakfast\", \"additionnal_bed\": 0, \"one_person_bed\": 1, \"description\": \"Guestroom 2 corn ears. Guest room on the first floor of an old house built of local stone, fully renovated and set at the heart of the hamlet of Rausa. Room with every comfort, decorated in modern style, and warm. Separate entrance. Upon request: child's bed and chair as well as use of the owners' fitted kitchen. Quiet location: large garden, orchard with sheep. Waymarked walks, with maps or guided by the owner upon request, in nearby woods. \"}, {\"two_person_bed\": 1, \"name\": \"A l'Ombre du Noyer\", \"classification\": [3], \"child_bed\": 1, \"price\": \"65.00\", \"capacity_max\": 4, \"room_number\": 1, \"thumb\": \"http://www.allochambredhotes.be/vignettes_heb/CH92138193800.jpg\", \"longitude\": 50.528706, \"owner\": {\"website\": \"www.alombredunoyer.be\", \"language\": \"FR - NL - EN\", \"fax\": \"\", \"name\": \"HIERNAUX\", \"firstname\": \"Thomas\", \"title\": \"M. & Mme\", \"mobile\": \"0477 263 251\", \"email\": \"alombredunoyer@yahoo.com\", \"phone\": \"081 21 52 79\"}, \"capacity_min\": 2, \"address\": {\"town\": \"Marchovelette\", \"city\": \"Fernelmont\", \"zip\": \"5380\", \"address\": \"rue de Tillier, 8\"}, \"latitude\": 4.9450890000000003, \"distribution\": \"1st fl. : 1 room (1x2p., bath room, toilets). garden : lawn, terrace\r\n\", \"photos\": [\"http://www.allochambredhotes.be/photos_heb/CH92138193800.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH92138193801.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH92138193802.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH92138193803.jpg\", \"http://www.allochambredhotes.be/photos_heb/CH92138193804.jpg\"], \"type\": \"Bed & breakfast\", \"additionnal_bed\": 0, \"one_person_bed\": 0, \"description\": \"Guestroom 3 corn ears. We would be delighted to welcome you to our Bed and Breakfast. Jeanne\u2019s room has been designed in a cocoon style, an invitation to unwind surrounded by every possible comfort. And to ensure that you relax as well as possible, I can offer you an hour long body massage using essential oils. We look forward to meeting you soon, and hope you enjoy your travels. Laurence and R\u00e9my.\r\n\"}]}";
					Gson gson = new Gson();
					JsonObject ob = gson.fromJson(x, JsonObject.class);
					location = gson.fromJson(ob.get("search_location"),
							CurrentLocation.class);
					JsonArray results = ob.getAsJsonArray("results");
					for (int i = 0; i < results.size(); i++) {
						JsonElement item = results.get(i);
						if (item.isJsonObject()) {
							Place place = gson.fromJson(item, Place.class);
							Log.i(place.name, place.description);
						}
						if (item.isJsonArray()) {
							Place[] places = gson.fromJson(item, Place[].class);
							Log.i(places.length + "", places[0].name);
						}
						items.add(item);
					}
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							dialog.dismiss();
							listview.setAdapter(new MyAdapter(
									SearchResultActivity.this,
									R.layout.list_item, items));
							MyItemizedOverlay pinOverlay = new MyItemizedOverlay(
									getResources().getDrawable(R.drawable.pin),
									SearchResultActivity.this);
							pinOverlay.location = location;
							pinOverlay.map = mapview;
							Gson gson = new Gson();
							int latitude = 0;
							int longitude = 0;
							for (int i = 0; i < items.size(); i++) {
								JsonElement item = items.get(i);
								Place place = null;
								Place[] places = null;
								if (item.isJsonObject())
									place = gson.fromJson(item, Place.class);
								if (item.isJsonArray()) {
									places = gson.fromJson(item, Place[].class);
									place = places[0];
								}
								OverlayItem overlayitem = new OverlayItem(
										new GeoPoint(
												(int) (place.longitude * 1e6),
												(int) (place.latitude * 1e6)),
										"", "");
								latitude += (int) (place.latitude * 1e6);
								longitude += (int) (place.longitude * 1e6);
								int[] ids = { R.drawable.pin1, R.drawable.pin2,
										R.drawable.pin3, R.drawable.pin4,
										R.drawable.pin5, R.drawable.pin6,
										R.drawable.pin7 };
								if (places == null)
									pinOverlay.addOverlay(overlayitem, place);
								else
									pinOverlay.addOverlay(overlayitem, places);
								if (i < 7) {
									pinOverlay.BoundDrawableForItem(
											getResources().getDrawable(ids[i]),
											i);
								}
							}
							if (items.size() > 0) {
								mapview.getOverlays().add(pinOverlay);
								mapview.getController().animateTo(
										new GeoPoint(longitude / items.size(),
												latitude / items.size()));
								mapview.getController().zoomToSpan(
										pinOverlay.getLatSpanE6() * 2,
										pinOverlay.getLonSpanE6() * 2);
							}

							Display display = getWindowManager()
									.getDefaultDisplay();
							int width = display.getWidth();
							int height = display.getHeight();
							LinearLayout.LayoutParams p1 = (LayoutParams) mapview
									.getLayoutParams();
							int[] x = new int[2];
							mapview.getLocationOnScreen(x);
							p1.height = (int) (height - x[1] - dpToPx(52 * items
									.size()));
							mapview.setLayoutParams(p1);
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					handler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							AlertDialog alertDialog1 = new AlertDialog.Builder(
									SearchResultActivity.this).create();

							alertDialog1.setTitle(R.string.opps);
							alertDialog1.setMessage(getResources().getString(
									R.string.no_result_found));
							alertDialog1.setButton("OK",
									new DialogInterface.OnClickListener() {

										public void onClick(
												DialogInterface dialog,
												int which) {

										}
									});

							// Showing Alert Message
							alertDialog1.show();
							dialog.dismiss();
						}
					});

				}
			}
		}).start();
	}

	int dpToPx(int dp) {
		return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
	}

	int pxToDp(int px) {
		return (int) (px / getResources().getDisplayMetrics().density);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	class MyAdapter extends ArrayAdapter<JsonElement> {
		List<JsonElement> items;
		ImageLoader imageLoader = ImageLoader.getInstance();
		private ImageLoaderConfiguration config;
		private DisplayImageOptions defaultOptions;

		public MyAdapter(Context context, int textViewResourceId,
				List<JsonElement> objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			items = objects;
			defaultOptions = new DisplayImageOptions.Builder().displayer(
					new RoundedBitmapDisplayer(dpToPx(15))).build();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View row = convertView;
			if (convertView == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.list_item, parent, false);
			}
			TextView tv1 = (TextView) row.findViewById(R.id.textView1);
			TextView tv2 = (TextView) row.findViewById(R.id.textView2);
			ImageView img_thumb = (ImageView) row.findViewById(R.id.img_thumb);
			JsonElement item = items.get(position);
			if (item.isJsonObject()) {
				final Place place = gson.fromJson(item, Place.class);
				tv1.setText((position + 1) + ". " + place.name);
				tv2.setVisibility(View.GONE);
				imageLoader.displayImage(place.thumb, img_thumb);
				row.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(SearchResultActivity.this,
								PlaceDetailsActivity.class);
						intent.putExtra("place", place);
						intent.putExtra("location", location);
						startActivity(intent);
					}
				});
			}
			if (item.isJsonArray()) {
				final Place[] places = gson.fromJson(item, Place[].class);
				tv1.setText((position + 1) + ". " + places[0].name);
				tv2.setVisibility(View.VISIBLE);
				tv2.setText(places.length + "");

				imageLoader.displayImage(places[0].thumb, img_thumb);
				row.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(SearchResultActivity.this,
								GroupedActivity.class);
						intent.putExtra("places", places);
						intent.putExtra("location", location);
						startActivity(intent);
					}
				});
			}
			return row;
		}
	}
}
