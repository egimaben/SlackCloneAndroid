package io.cloudboost.slackclone;

import io.cloudboost.CloudApp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.Application;

public class App extends Application {
	public static String CURRENT_USER=null;
	public static String TO_USER="egima";
	public static HashMap<String,ChatArrayAdapter> chats=new HashMap<>();
	public static List<String> staticUsers=Arrays.asList(new String[]{"@egima","@bengi"});
	
	@Override
	public void onCreate() {
		super.onCreate();
		initClient();
	}
	public void initClient(){
		CloudApp.init("bengi", "ailFnQf+q102UpB86ZZBKg==");
	}
	public void initMaster(){
		CloudApp.init("bengi", "df6gNFKRDMXUXt+5EIWyIjaMPtQIZSEqiZxi8eAwYls=");
	}

}
