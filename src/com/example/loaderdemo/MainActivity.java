package com.example.loaderdemo;

import java.util.Random;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<User>{

	private static final String TAG = "LoaderDemo";
	
	private LoaderManager mLoaderManager;
	private MyLoader mLoader;

	private TextView mUsername;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mUsername = (TextView) findViewById(R.id.username);
		mLoaderManager = getSupportLoaderManager();
		
		mLoaderManager.initLoader(0, null, this);
	}
	
	public void reload(View v){
		if(mLoader != null){
			mLoader.onContentChanged();
		}
	}

	@Override
	public Loader<User> onCreateLoader(int loader, Bundle bundle) {
		Log.i(TAG, "onCreateLoader");
		mLoader = new MyLoader(this);
		return mLoader;
	}

	/**
	 * 在onLoadFinished中处理loadInBackground查询到的数据
	 */
	@Override
	public void onLoadFinished(Loader<User> loader, User user) {
		mUsername.setText(user.username);
		Log.i(TAG, "onLoadFinished："+user.username);
	}

	@Override
	public void onLoaderReset(Loader<User> bundle) {
		Log.i(TAG, "onLoaderReset");
	}
	
	
	public static class MyLoader extends AsyncTaskLoader<User>{

		private User mUser;
		
		public MyLoader(Context context) {
			super(context);
		}

		/**
		 * 在这个方法中进行数据的查询，并将查询出来的数据组装并返回。
		 * 此处休眠2秒模拟耗时数据查询，User对象模拟查询到的数据，真实项目中可能是一个List或者Cursor
		 */
		@Override
		public User loadInBackground() {
			Log.i(TAG, "loadInBackground");
			SystemClock.sleep(2000);
			User u = new User();
			u.username = "name "+new Random().nextInt();
			return u;
		}
		
		/**
		 * loadInBackground返回后此方法会被调用，loadInBackground的返回值将作为此函数的参数被传入
		 */
		@Override
		public void deliverResult(User data) {
			Log.i(TAG, "deliverResult");
			// 如果Loader已经被重置，则不需要分发，直接返回就可以
			// 当然如果你的数据是Cursor的话，最后在此处调用Cursor的close方法释放资源，此处是一个User对象，不需要释放
			if(isReset()){
				// release data
				data.username = "";
				return;
			}
			
			User oldData = mUser;
			mUser = data;
			
			if(isStarted()){
				// 分发数据
				super.deliverResult(data);
			}
			
			// 释放资源，此处为模拟释放，Cursor的话可调用close关闭
			if(oldData != null && oldData != data){
				oldData.username = "";
			}
		}
		
		 /**
	     * Must be called from the UI thread
	     */
	    @Override
	    protected void onStopLoading() {
	    	Log.i(TAG, "onStopLoading");
	        // 尝试尽可能的去停止当前执行的任务loadInBackground
	        cancelLoad();
	    }
	    
	    /**
	     * 此方法在Loader被重置时被调用
	     */
	    @Override
	    protected void onReset() {
	    	Log.i(TAG, "onReset");
	    	super.onReset();
	    	
	    	// 确保Loader被停止
	    	onStopLoading();
	    	
	    	// 释放资源
	    	mUser .username = "";
	    	mUser = null;
	    }
		
		@Override
		protected void onStartLoading() {
			Log.i(TAG, "onStartLoading");
			if (takeContentChanged() || mUser == null) {
				// 此处一定要调用forceLoad方法，否则loadInBackground不会被调用
	            forceLoad();
	        }
		}
	}
	
}
