package be.gitesdewallonie.allochambresdhotes.classes;

import java.util.ArrayList;
import java.util.List;

import org.restlet.security.User;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import be.gitesdewallonie.allochambresdhotes.GroupedActivity;
import be.gitesdewallonie.allochambresdhotes.Lib;
import be.gitesdewallonie.allochambresdhotes.PlaceDetailsActivity;
import be.gitesdewallonie.allochambresdhotes.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private List<Object> ids = new ArrayList<Object>();
	public MapView map;
	Activity mContext;
 	public CurrentLocation location;
	public MyItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public void addOverlay(OverlayItem overlay, Object id) {
		mOverlays.add(overlay);
		ids.add(id);
		populate();
	}

	public void BoundDrawableForItem(Drawable drawable, int i) {
		mOverlays.get(i).setMarker(boundCenterBottom(drawable));
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

	public MyItemizedOverlay(Drawable defaultMarker, Activity context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
	}

	@Override
	protected boolean onTap(int index) {
		Object ob = ids.get(index);
		View popUp = mContext.getLayoutInflater().inflate(
				R.layout.location_info, map, false);
		TextView t1 = (TextView) popUp.findViewById(R.id.textView1);
		TextView t2 = (TextView) popUp.findViewById(R.id.textView2);
		ImageView img2 = (ImageView) popUp.findViewById(R.id.img_logo1);
		if (ob != null && ob.getClass() == Place.class) {
			final Place p = (Place) ob;
			t1.setText(p.name);
			t2.setText(p.address.address);
			img2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext,
							PlaceDetailsActivity.class);
					intent.putExtra("place", p);
					intent.putExtra("location", location);
					mContext.startActivity(intent);
				}
			});
		}
		if (ob != null && ob.getClass() == Place[].class) {
			final Place[] p = (Place[]) ob;
			t1.setText(p[0].name);
			t2.setText(p[0].address.address);
			img2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, GroupedActivity.class);
					intent.putExtra("places", p);
					intent.putExtra("location", location);
					mContext.startActivity(intent);
				}
			});
		}
		popUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				map.removeAllViews();
			}
		});
		GeoPoint current = createItem(index).getPoint();
		MapView.LayoutParams mapParams = new MapView.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, current, -5, -50,
				MapView.LayoutParams.BOTTOM_CENTER);
		map.removeAllViews();
		map.addView(popUp, mapParams);
		map.getController().animateTo(current);
		return true;
	}
}
