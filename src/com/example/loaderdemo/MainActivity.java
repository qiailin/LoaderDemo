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
	 * ��onLoadFinished�д���loadInBackground��ѯ��������
	 */
	@Override
	public void onLoadFinished(Loader<User> loader, User user) {
		mUsername.setText(user.username);
		Log.i(TAG, "onLoadFinished��"+user.username);
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
		 * ����������н������ݵĲ�ѯ��������ѯ������������װ�����ء�
		 * �˴�����2��ģ���ʱ���ݲ�ѯ��User����ģ���ѯ�������ݣ���ʵ��Ŀ�п�����һ��List����Cursor
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
		 * loadInBackground���غ�˷����ᱻ���ã�loadInBackground�ķ���ֵ����Ϊ�˺����Ĳ���������
		 */
		@Override
		public void deliverResult(User data) {
			Log.i(TAG, "deliverResult");
			// ���Loader�Ѿ������ã�����Ҫ�ַ���ֱ�ӷ��ؾͿ���
			// ��Ȼ������������Cursor�Ļ�������ڴ˴�����Cursor��close�����ͷ���Դ���˴���һ��User���󣬲���Ҫ�ͷ�
			if(isReset()){
				// release data
				data.username = "";
				return;
			}
			
			User oldData = mUser;
			mUser = data;
			
			if(isStarted()){
				// �ַ�����
				super.deliverResult(data);
			}
			
			// �ͷ���Դ���˴�Ϊģ���ͷţ�Cursor�Ļ��ɵ���close�ر�
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
	        // ���Ծ����ܵ�ȥֹͣ��ǰִ�е�����loadInBackground
	        cancelLoad();
	    }
	    
	    /**
	     * �˷�����Loader������ʱ������
	     */
	    @Override
	    protected void onReset() {
	    	Log.i(TAG, "onReset");
	    	super.onReset();
	    	
	    	// ȷ��Loader��ֹͣ
	    	onStopLoading();
	    	
	    	// �ͷ���Դ
	    	mUser .username = "";
	    	mUser = null;
	    }
		
		@Override
		protected void onStartLoading() {
			Log.i(TAG, "onStartLoading");
			if (takeContentChanged() || mUser == null) {
				// �˴�һ��Ҫ����forceLoad����������loadInBackground���ᱻ����
	            forceLoad();
	        }
		}
	}
	
}
