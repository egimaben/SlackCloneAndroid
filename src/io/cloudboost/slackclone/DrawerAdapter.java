package io.cloudboost.slackclone;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerAdapter extends ArrayAdapter<DrawerItem> {
	Context context;
	List<DrawerItem> items;
	int layout;

	public DrawerAdapter(Context context, int resource, List<DrawerItem> objects) {
		super(context, resource, objects);
		layout = resource;
		this.context = context;
		items = objects;
	}

	class ViewHolder {
		String name;
		boolean header;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();

			view = inflater.inflate(layout, parent, false);
			holder = new ViewHolder();
		} else {
			holder = (ViewHolder) view.getTag();
		}
		DrawerItem item = getItem(position);
		holder.name = item.getName();
		holder.header = item.isHeader();
		view.setTag(holder);
		ImageView image = (ImageView) view.findViewById(R.id.plus_image);
		TextView text = (TextView) view.findViewById(R.id.menu_item);
		text.setTextColor(context.getResources().getColor(
				R.color.toolbar_background));
		Typeface face = Typeface.createFromAsset(context.getAssets(),
				"fonts/Roboto-Regular.ttf");
		text.setTypeface(face);
		if (!holder.header)
			image.setVisibility(View.INVISIBLE);
		text.setText(holder.name);
		return view;

	}

}
