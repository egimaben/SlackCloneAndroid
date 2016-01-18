package io.cloudboost.slackclone;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

	private TextView chatText;
	private TextView user;
	private List<ChatMessage> chatMessageList = new ArrayList<>();
	@Override
	public void add(ChatMessage object) {
		chatMessageList.add(object);
		super.add(object);
	}

	public ChatArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.chatMessageList.size();
	}

	public ChatMessage getItem(int index) {
		return this.chatMessageList.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.slack_message_layout, parent, false);
		}
		ChatMessage chatMessageObj = getItem(position);
		chatText = (TextView) row.findViewById(R.id.message);
		user=(TextView) row.findViewById(R.id.user_name);
		user.setText(chatMessageObj.user);
		chatText.setText(chatMessageObj.message);
		return row;
	}
}