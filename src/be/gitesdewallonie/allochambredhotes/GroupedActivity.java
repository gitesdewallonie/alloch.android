package be.gitesdewallonie.allochambredhotes;

import java.util.List;

import com.google.gson.JsonElement;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import be.gitesdewallonie.allochambredhotes.classes.CurrentLocation;
import be.gitesdewallonie.allochambredhotes.classes.Place;

public class GroupedActivity extends Activity {
	private CurrentLocation location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle(R.string.grouped_rooms);
		setContentView(R.layout.activity_grouped);
		Object[] places = (Object[]) getIntent().getSerializableExtra("places");
		location = (CurrentLocation) getIntent().getSerializableExtra(
				"location");
		int x = places.length;
		ListView lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(new MyAdapter(GroupedActivity.this, R.layout.list_item,
				places));
		TextView tv1 = (TextView) findViewById(R.id.textView1);
		tv1.setText(((Place) places[0]).name);
	}

	class MyAdapter extends ArrayAdapter<Object> {
		Object[] items;
		ImageLoader imageLoader = ImageLoader.getInstance();

		public MyAdapter(Context context, int textViewResourceId,
				Object[] objects) {
			super(context, textViewResourceId, objects);
			// TODO Auto-generated constructor stub
			items = objects;
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
			final Place item = (Place) items[position];
			tv2.setVisibility(View.GONE);
			tv1.setText((position + 1) + ". " + item.name);
			imageLoader.displayImage(item.thumb, img_thumb);
			row.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(GroupedActivity.this,
							PlaceDetailsActivity.class);
					intent.putExtra("place", item);
					intent.putExtra("location", location);
					startActivity(intent);
				}
			});
			return row;
		}
	}
}
