package edu.marufhassan.picturepuzzle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import edu.marufhassan.picturepuzzle.data.GamesDatasource;
import edu.marufhassan.picturepuzzle.data.Score;
import edu.marufhassan.slidepuzzle.R;

public class HighScoreActivity extends Activity {
	private GamesDatasource datasource;

	private ExpandableListView exListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_high_score);
		exListView = (ExpandableListView) findViewById(R.id.expandableListView);

		datasource = new GamesDatasource(this);
		datasource.openDatabase();

		List<String> header = new ArrayList<>();
		header.add("Easy Level");
		header.add("Medium Level");
		header.add("Hard Level");

		HashMap<String, List<Score>> scores = new HashMap<>();

		List<Score> list = datasource.getScores(3, " ASC");
		scores.put(header.get(0), list);

		list = datasource.getScores(4, " ASC");
		scores.put(header.get(1), list);

		list = datasource.getScores(5, " ASC");
		scores.put(header.get(2), list);

		ExpandableListAdapter adapter = new ExpandableListAdapter(this, header,
				scores);
		exListView.setAdapter(adapter);

		datasource.closeDatabase();
	}

	private class ExpandableListAdapter extends BaseExpandableListAdapter {
		private Context context;
		private List<String> header;
		private HashMap<String, List<Score>> scores;

		public ExpandableListAdapter(Context context, List<String> header,
				HashMap<String, List<Score>> scores) {
			this.context = context;
			this.header = header;
			this.scores = scores;
		}

		@Override
		public int getGroupCount() {
			return scores.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			if (getChild(groupPosition, 0) == null) {
				return 0;
			}
			return scores.get(header.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return header.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			if (scores.get(header.get(groupPosition)) == null) {
				return null;
			}
			return scores.get(header.get(groupPosition)).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			String headerTitle = (String) getGroup(groupPosition);
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.list_group, null);
			}

			TextView lblListHeader = (TextView) convertView
					.findViewById(R.id.lblListHeader);
			lblListHeader.setTypeface(null, Typeface.BOLD);
			lblListHeader.setText(headerTitle);

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater infalInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = infalInflater.inflate(R.layout.score_item, null);
			}
			
			TextView time = (TextView) convertView.findViewById(R.id.timeScore);
			TextView move = (TextView) convertView.findViewById(R.id.moveScore);
			
			if (getChild(groupPosition, childPosition) == null) {
				time.setText("No scores to show");
				move.setText("");
				return convertView;
			}
			Score score = (Score) getChild(groupPosition, childPosition);
			time.setText(StopWatch.updateTimer(score.getTime()));
			move.setText(String.valueOf(score.getMoves()));
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return false;
		}

	}

}
