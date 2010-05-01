/*
 * Copyright (C) 2010 Peter Dornbach.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dornbachs.zebra;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class StartNewActivity extends Activity implements View.OnClickListener {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Apparently this cannot be set from the style.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		setContentView(R.layout.start_new);

		GridView gridview = (GridView) findViewById(R.id.start_new_grid);
		gridview.setAdapter(new ImageAdapter(this));
	}

	public void onClick(View view) {
		setResult(view.getId());
		finish();
	}

	private class ImageAdapter extends BaseAdapter {
		ImageAdapter(Context c) {
			_context = c;
			loadResourceIds();
		}

		public int getCount() {
			return _thumbIds.length;
		}

		public Object getItem(int i) {
			return null;
		}

		public long getItemId(int i) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			if (convertView == null) { // if it's not recycled, initialize some attributes
				imageView = new ImageView(_context);
				imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);
				imageView.setOnClickListener(StartNewActivity.this);
			} else {
				imageView = (ImageView) convertView;
			}

			imageView.setImageResource(_thumbIds[position]);
			imageView.setId(_outlineIds[position]);
			return imageView;
		}

		public int getOutlineId(int position) {
			return _outlineIds[position];
		}

		private void loadResourceIds() {
			// Use reflection to list resource ids of thumbnails and outline
			// images.First, we list all the drawables starting with the proper
			// prefixes into 2 maps.
			Map<String, Integer> outlineMap = new TreeMap<String, Integer>();
			Map<String, Integer> thumbMap = new TreeMap<String, Integer>();
			Field[] drawables = R.drawable.class.getDeclaredFields();
			for (int i = 0; i < drawables.length; i++) {
				String name = drawables[i].getName();
				try {
					if (name.startsWith(PREFIX_OUTLINE))
						outlineMap.put(name.substring(PREFIX_OUTLINE.length()),
								drawables[i].getInt(null));
					if (name.startsWith(PREFIX_THUMB))
						thumbMap.put(name.substring(PREFIX_THUMB.length()),
								drawables[i].getInt(null));
				} catch (IllegalAccessException e) {
				}
			}
			Set<String> keys = outlineMap.keySet();
			keys.retainAll(thumbMap.keySet());
			_outlineIds = new Integer[keys.size()];
			_thumbIds = new Integer[keys.size()];
			int j = 0;
			Iterator<String> i = keys.iterator();
			while (i.hasNext()) {
				String key = i.next();
				_outlineIds[j] = outlineMap.get(key);
				_thumbIds[j] = thumbMap.get(key);
				j++;
			}
		}

		private static final String PREFIX_OUTLINE = "outline";
		private static final String PREFIX_THUMB = "thumb";

		private Context _context;
		private Integer[] _thumbIds;
		private Integer[] _outlineIds;
	}
}
