package com.photo.editor.snapstudio.Templates.frame;

import static com.photo.editor.snapstudio.Templates.MainActivity.imgid;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.photo.editor.snapstudio.PhEditor.Model.Templates;
import com.photo.editor.snapstudio.R;
import com.photo.editor.snapstudio.Templates.MainActivity;

import java.util.ArrayList;

public class Adapter_grid extends BaseAdapter {

//	Integer[] Frame_id;
	Context context;
	ArrayList<Templates> itemList;
	String queryString;

	LayoutInflater inflater = null;

//	public Adapter_grid(Context context, Integer[] Frame_id) {
//		// TODO Auto-generated constructor stub
//		this.Frame_id = Frame_id;
//		this.context = context;
//		inflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	}

	public Adapter_grid(Context context, ArrayList<Templates> itemList, String queryString) {
		this.context = context;
		this.itemList = itemList;
		this.queryString = queryString;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public class Holder {

		ImageView img;

	}

	Holder holder;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View rowView = convertView;

		if (rowView == null) {
			holder = new Holder();
			rowView = inflater.inflate(R.layout.activity_grid, null);
			holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
			rowView.setTag(holder);
		} else {
			holder = (Holder) rowView.getTag();
		}

		
		new Async_Image(holder.img, itemList.get(position).getImageUrl()).execute();

//		Log.i("ok...", "" + Frame_id[position]);
//		holder.img.setImageResource(Frame_id[position]);
		Glide.with(context).load(itemList.get(position).getImageUrl()).into(holder.img);

		holder.img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				imgid = 1;
				Intent intent = new Intent(context, MainActivity.class);
				intent.putExtra("imageURI", itemList.get(position).getImageUrl());
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		});

		return rowView;
	}

	class Async_Image extends AsyncTask<Object, String, String> {
		ImageView img;
		String iId;

		public Async_Image(ImageView img, String image_id) {
			// TODO Auto-generated constructor stub
			this.img = img;
			iId = image_id;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

//			img.setImageResource(iId);
			Glide.with(context).load(iId).into(img);
		}

		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
