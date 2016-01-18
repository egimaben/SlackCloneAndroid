package io.cloudboost.slackclone;

import io.cloudboost.CloudException;
import io.cloudboost.CloudObject;
import io.cloudboost.CloudObjectCallback;
import io.cloudboost.CloudQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private static ArrayList<String> teamMembers;
	DrawerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle;
		teamMembers = new ArrayList<>(Arrays.asList(new String[] { "@SlackBot",
				"@Nawaz", "@Ken", "@Ben" }));
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		adapter = new DrawerAdapter(this, R.layout.row,
				getDrawerItems(teamMembers.toArray(new String[0])));
		mDrawerList.setAdapter(adapter);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		/*
		 * ActionBarDrawerToggle ties together the the proper interactions
		 * between the sliding drawer and the action bar app icon
		 */
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		android.R.color.transparent, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);

				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
		if (App.CURRENT_USER == null)
			openDialog();
		/*
		 * create a query on table chat, remember we use notification queries
		 * for this example, when u send a chat, we just save it as a
		 * CloudObject in the chat table which the chat widget listens to.
		 */
		CloudQuery query = new CloudQuery("slack_message");
		/*
		 * we also listen to "created" events on table chat, difference is we
		 * query records where admin column is set to false, that means we shall
		 * not be receiving echoes of our own messages The chat widget on the
		 * site has to query messages where admin column is set to true, so that
		 * it only receives notifications on messages from an agent not an echo
		 * of its own client message
		 */
		query.equalTo("to_user", "@bengi");
		try {
			CloudObject.on("slack_message", "created", query,
					new CloudObjectCallback() {

						@Override
						public void done(final CloudObject arg0,
								CloudException arg1) throws CloudException {
							if (arg0 != null)
								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										receiveMessage(arg0);

									}
								});

						}
					});
		} catch (CloudException e) {
			e.printStackTrace();
		}

	}

	public void openDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.dialog);
		dialog.setTitle("Chat name");
		Button ok = (Button) dialog.findViewById(R.id.dialogButtonOK);
		final EditText name = (EditText) dialog.findViewById(R.id.user);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String user = "@" + name.getText();
				if (!App.staticUsers.contains(user)) {
					name.setText("");
				} else {
					if (user.equals("@egima")) {
						App.CURRENT_USER = "@egima";
						adapter.add(new DrawerItem("@bengi", false));
						teamMembers.add("@bengi");

					} else {
						App.CURRENT_USER = "@bengi";
						adapter.add(new DrawerItem("@egima", false));
					}
					teamMembers.add("@egima");

					dialog.dismiss();
				}

			}
		});
		dialog.show();

	}

	public List<DrawerItem> getDrawerItems(String[] planets) {

		List<DrawerItem> items = new ArrayList<>();
		items.add(new DrawerItem("DIRECT MESSAGES", true));
		for (String s : planets) {
			DrawerItem item = new DrawerItem(s, false);
			items.add(item);
		}
		return items;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.search_action).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	public void onIconClick(View view) {
		if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
			mDrawerLayout.closeDrawer(Gravity.START);
		} else {
			mDrawerLayout.openDrawer(Gravity.START);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (item != null
				&& (item.getItemId() == android.R.id.icon || item.getItemId() == android.R.id.icon1)
				|| item.getItemId() == android.R.id.icon2) {
			if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
				mDrawerLayout.closeDrawer(Gravity.START);
			} else {
				mDrawerLayout.openDrawer(Gravity.START);
			}
		}
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listener for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			position -= 1;
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		Fragment fragment = new ChatFragment();
		Bundle args = new Bundle();
		args.putInt(ChatFragment.ARG_USER_NUMBER, position);
		args.putString(ChatFragment.OTHER_USER, teamMembers.get(position));
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(teamMembers.get(position));
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * this method takes a cloudobject and retrieves 'message' out of it and
	 * adds it to the adapter
	 * 
	 * @param object
	 * @return
	 */
	private boolean receiveMessage(CloudObject object) {
		String msg = object.getString("message");
		String sender = object.getString("from_user");
		if (!App.chats.containsKey(sender)) {
			ChatArrayAdapter adapter = new ChatArrayAdapter(this,
					R.layout.slack_message_layout);
			App.chats.put(sender, adapter);
		}
		App.chats.get(sender).add(new ChatMessage(msg, sender, 0));
		return true;
	}

	/**
	 * Fragment that appears in the "content_frame", shows a chats with a user
	 */
	public static class ChatFragment extends Fragment {
		public static final String ARG_USER_NUMBER = "user_number";
		public static final String OTHER_USER = "other_user";

		public ImageView sendButton;
		public ListView chatList;
		public ChatArrayAdapter adapter;
		public EditText editor;
		public String otherUser;

		public ChatFragment() {
			// Empty constructor required for fragment subclasses
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			otherUser = getArguments().getString(OTHER_USER);
			View rootView = inflater.inflate(R.layout.fragment_planet,
					container, false);
			sendButton = (ImageView) rootView.findViewById(R.id.buttonSend);
			chatList = (ListView) rootView.findViewById(R.id.chat_message_list);
			editor = (EditText) rootView.findViewById(R.id.chatText);
			if (App.chats.containsKey(otherUser)) {
				adapter = App.chats.get(otherUser);
			} else
				adapter = new ChatArrayAdapter(this.getActivity(),
						R.layout.slack_message_layout);
			chatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

			// to scroll the list view to bottom on data change
			adapter.registerDataSetObserver(new DataSetObserver() {
				@Override
				public void onChanged() {
					super.onChanged();
					chatList.setSelection(adapter.getCount() - 1);
				}
			});
			chatList.setAdapter(adapter);
			// action to take when user presses enter key
			editor.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if ((event.getAction() == KeyEvent.ACTION_DOWN)
							&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
						new sendMsg().execute(new String[] {});
						return true;
					}
					return false;
				}
			});
			sendButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					new sendMsg().execute(new String[] {});

				}
			});
			int i = getArguments().getInt(ARG_USER_NUMBER);
			String user = teamMembers.get(i);
			getActivity().setTitle(user);
			return rootView;
		}

		@Override
		public void onStop() {
			// persist the adapter so that when this fragment resumes, we
			// display the previous chats to the user
			App.chats.put(otherUser, adapter);
			super.onStop();
		}

		class sendMsg extends AsyncTask<String, String, String> {

			/**
			 * Before starting background, run this method
			 * */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				adapter.add(new ChatMessage(editor.getText().toString(),
						App.CURRENT_USER, 0));
			}

			/**
			 * send message on a background thread
			 * */
			@Override
			protected String doInBackground(String... args) {
				CloudObject obj = new CloudObject("slack_message");
				String user = App.CURRENT_USER;
				try {
					obj.set("from_user", user);
					obj.set("message", editor.getText().toString());
					obj.set("to_user", otherUser);
					obj.save(new CloudObjectCallback() {

						@Override
						public void done(final CloudObject arg0,
								final CloudException arg1)
								throws CloudException {
							getActivity().runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if (arg1 != null)
										Toast.makeText(getActivity(),
												"An error has occured",
												Toast.LENGTH_SHORT).show();
								}
							});

						}
					});
				} catch (CloudException e) {
					e.printStackTrace();
				}
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						editor.setText("");
					}
				});

				return null;
			}

			/**
			 * After completing background task, run this method
			 * **/
			protected void onPostExecute(String args) {
			}
		}
	}

}