/*
Simple DirectMedia Layer
Java source code (C) 2009-2011 Sergii Pylypenko
  
This software is provided 'as-is', without any express or implied
warranty.  In no event will the authors be held liable for any damages
arising from the use of this software.

Permission is granted to anyone to use this software for any purpose,
including commercial applications, and to alter it and redistribute it
freely, subject to the following restrictions:
  
1. The origin of this software must not be misrepresented; you must not
   claim that you wrote the original software. If you use this software
   in a product, an acknowledgment in the product documentation would be
   appreciated but is not required. 
2. Altered source versions must be plainly marked as such, and must not be
   misrepresented as being the original software.
3. This notice may not be removed or altered from any source distribution.
*/

package anise.port.android;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.content.res.Configuration;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.View.OnKeyListener;
import android.view.MenuItem;
import android.text.method.TextKeyListener;
import java.util.LinkedList;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import android.view.inputmethod.InputMethodManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instance = this;
		// fullscreen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if(Globals.InhibitSuspend)
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		_layout = new LinearLayout(this);
		_layout.setOrientation(LinearLayout.VERTICAL);
		_layout.setLayoutParams(new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		
		_videoLayout = new FrameLayout(this);
		_videoLayout.addView(_layout);
		
		setContentView(_videoLayout);
		

		if(mAudioThread == null) // Starting from background (should not happen)
		{
			System.out.println("libSDL: Loading libraries");
			LoadLibraries();
			InstallGameData();
			mAudioThread = new AudioThread(this);
			System.out.println("libSDL: Loading settings");
			Settings.Load(this);
			if(!Globals.CompatibilityHacksStaticInit)
				LoadApplicationLibrary(this);
		}

		initSDL();
	}
	
	public void initSDL()
	{
		if(sdlInited)
			return;
		System.out.println("libSDL: Initializing video and SDL application");
		sdlInited = true;
		if(Globals.UseAccelerometerAsArrowKeys)
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		_videoLayout.removeView(_layout);
		_layout = null;
		_btn = null;
		_inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		_videoLayout = new FrameLayout(this);
		setContentView(_videoLayout);
		mGLView = new DemoGLSurfaceView(this);
		_videoLayout.addView(mGLView);
		// Receive keyboard events
		mGLView.setFocusableInTouchMode(true);
		mGLView.setFocusable(true);
		mGLView.requestFocus();
		//DimSystemStatusBar.get().dim(_videoLayout);
		//DimSystemStatusBar.get().dim(mGLView);
	}

	@Override
	protected void onPause() {
		_isPaused = true;
		if( mGLView != null )
			mGLView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if( mGLView != null )
			mGLView.onResume();

		_isPaused = false;
	}
	
	public boolean isPaused()
	{
		return _isPaused;
	}

	@Override
	protected void onDestroy() 
	{
		if( mGLView != null )
			mGLView.exitApp();
		super.onDestroy();
		System.exit(0);
	}

	public void showScreenKeyboard(final String oldText, boolean sendBackspace)
	{
		if(_screenKeyboard != null)
			return;
		class myKeyListener implements OnKeyListener
		{
			MainActivity _parent;
			boolean sendBackspace;
			myKeyListener(MainActivity parent, boolean sendBackspace) { _parent = parent; this.sendBackspace = sendBackspace; };
			public boolean onKey(View v, int keyCode, KeyEvent event) 
			{
				if ((event.getAction() == KeyEvent.ACTION_UP) && ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_BACK)))
				{
					_parent.hideScreenKeyboard();
					return true;
				}
				if ((sendBackspace && event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DEL || keyCode == KeyEvent.KEYCODE_CLEAR))
				{
					synchronized(textInput) {
						DemoRenderer.nativeTextInput( 8, 0 ); // Send backspace to native code
					}
					return false; // and proceed to delete text in keyboard input field
				}
				return false;
			}
		};
		_screenKeyboard = new EditText(this);
		_videoLayout.addView(_screenKeyboard);
		_screenKeyboard.setOnKeyListener(new myKeyListener(this, sendBackspace));
		_screenKeyboard.setHint(R.string.text_edit_click_here);
		_screenKeyboard.setText(oldText);
		_screenKeyboard.setKeyListener(new TextKeyListener(TextKeyListener.Capitalize.NONE, false));
		_screenKeyboard.setFocusableInTouchMode(true);
		_screenKeyboard.setFocusable(true);
		_screenKeyboard.requestFocus();
		_inputManager.showSoftInput(_screenKeyboard, InputMethodManager.SHOW_FORCED);
	};

	public void hideScreenKeyboard()
	{
		if(_screenKeyboard == null)
			return;

		synchronized(textInput)
		{
			String text = _screenKeyboard.getText().toString();
			for(int i = 0; i < text.length(); i++)
			{
				DemoRenderer.nativeTextInput( (int)text.charAt(i), (int)text.codePointAt(i) );
			}
		}
		DemoRenderer.nativeTextInputFinished();
		_inputManager.hideSoftInputFromWindow(_screenKeyboard.getWindowToken(), 0);
		_videoLayout.removeView(_screenKeyboard);
		_screenKeyboard = null;
		mGLView.setFocusableInTouchMode(true);
		mGLView.setFocusable(true);
		mGLView.requestFocus();
	};

	@Override
	public boolean onKeyDown(int keyCode, final KeyEvent event)
	{
		if(_screenKeyboard != null)
			_screenKeyboard.onKeyDown(keyCode, event);
		else
		if( mGLView != null )
		{
			if( DemoGLSurfaceView.nativeKey( keyCode, 1 ) == 0 )
				return super.onKeyDown(keyCode, event);
		}

		return true;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, final KeyEvent event)
	{
		if(_screenKeyboard != null)
			_screenKeyboard.onKeyUp(keyCode, event);
		else
		if( mGLView != null )
		{
			if( DemoGLSurfaceView.nativeKey( keyCode, 0 ) == 0 )
				return super.onKeyUp(keyCode, event);
		}
		return true;
	}

	// Action bar support for Android 3.X, there are reports that on-screen overlay buttons do not send button events on Galaxy Nexus S, however in emulator everything works.
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		System.out.println("libSDL: onOptionsItemSelected: MenuItem ID " + item.getItemId() + " TODO: translate this ID into keypress event. It is reported that Samsung Droid X with ICS does NOT send a proper keyevent when you press Back on the action bar, it should send this event instead.");
		switch (item.getItemId())
		{
//			case android.R.id.home:
//			return true;
			default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean dispatchTouchEvent(final MotionEvent ev)
	{
		if(_screenKeyboard != null)
			_screenKeyboard.dispatchTouchEvent(ev);
		else
		if(mGLView != null)
			mGLView.onTouchEvent(ev);
		else
		if( _btn != null )
			return _btn.dispatchTouchEvent(ev);
		
		return true;
	}
	
//	@Override
//	public boolean dispatchGenericMotionEvent (MotionEvent ev)
//	{
//		// This code fails to run for Android 1.6, so there will be no generic motion event for Andorid screen keyboard
//		/*
//		if(_screenKeyboard != null)
//			_screenKeyboard.dispatchGenericMotionEvent(ev);
//		else
//		*/
//		if(mGLView != null)
//			mGLView.onGenericMotionEvent(ev);
//		return true;
//	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Do nothing here
	}
	
	public void showTaskbarNotification()
	{
		showTaskbarNotification("SDL application paused", "SDL application", "Application is paused, click to activate");
	}

	// Stolen from SDL port by Mamaich
	public void showTaskbarNotification(String text0, String text1, String text2)
	{
		NotificationManager NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		Notification n = new Notification(R.drawable.icon, text0, System.currentTimeMillis());
		n.setLatestEventInfo(this, text1, text2, pendingIntent);
		NotificationManager.notify(NOTIFY_ID, n);
	}

	public void hideTaskbarNotification()
	{
		NotificationManager NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationManager.cancel(NOTIFY_ID);
	}
	
	public void LoadLibraries()
	{
		try
		{
			if(Globals.NeedGles2)
				System.loadLibrary("GLESv2");
			System.out.println("libSDL: loaded GLESv2 lib");
		}
		catch ( UnsatisfiedLinkError e )
		{
			System.out.println("libSDL: Cannot load GLESv2 lib");
		}

		try
		{
			for(String l : Globals.AppLibraries)
			{
				try
				{
					String libname = System.mapLibraryName(l);
					File libpath = new File(getFilesDir().getAbsolutePath() + "/../lib/" + libname);
					System.out.println("libSDL: loading lib " + libpath.getAbsolutePath());
					System.load(libpath.getPath());
				}
				catch( UnsatisfiedLinkError e )
				{
					System.loadLibrary(l);
				}
			}
		}
		catch ( UnsatisfiedLinkError e )
		{
			System.out.println("libSDL: Error: " + e.toString());
		}
	};
	
	public void InstallGameData()
	{
		try {
			unzip(getApplicationContext().getAssets().open(Globals.AppZipFile), new File(Globals.DataDir));
		} catch (IOException e) {
			System.out.println("libSDL: Error: failed to install gamedata");
		}
	}
	
    public static void unzip (InputStream is, File dest) throws IOException
    {
    	if ( !dest.isDirectory()) 
    		throw new IOException("Invalid Unzip destination " + dest );
    	
    	ZipInputStream zip = new ZipInputStream(is);
    	
    	ZipEntry ze;
    	
    	while ( (ze = zip.getNextEntry()) != null ) {
    		final String path = dest.getAbsolutePath() 
    			+ File.separator + ze.getName();
    		
    		if ( ze.getName().indexOf("/") != -1) 
    		{
    			File parent = new File(path).getParentFile();
    			if ( !parent.exists() )
    				if ( !parent.mkdirs() )
    					throw new IOException("Unable to create folder " + parent);
    		}
    		
    		File currFile = new File(path);
    		if ( currFile.exists() == false )
    		{
	    		FileOutputStream fout = new FileOutputStream(path);
	    		byte[] bytes = new byte[8192];
	    		
	            for (int c = zip.read(bytes); c != -1; c = zip.read(bytes)) {
	              fout.write(bytes,0, c);
	            }
	            zip.closeEntry();
	            fout.close();
    		}
    	}
    }

	public static void LoadApplicationLibrary(final Context context)
	{
		String libs[] = { "application", "sdl_main" };
		try
		{
			for(String l : libs)
			{
				System.loadLibrary(l);
			}
		}
		catch ( UnsatisfiedLinkError e )
		{
			System.out.println("libSDL: error loading lib: " + e.toString());
			try
			{
				for(String l : libs)
				{
					String libname = System.mapLibraryName(l);
					File libpath = new File(context.getCacheDir(), libname);
					System.out.println("libSDL: loading lib " + libpath.getPath());
					System.load(libpath.getPath());
					libpath.delete();
				}
			}
			catch ( UnsatisfiedLinkError ee )
			{
				System.out.println("libSDL: error loading lib: " + ee.toString());
			}
		}
	}

	public int getApplicationVersion()
	{
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			System.out.println("libSDL: Cannot get the version of our own package: " + e);
		}
		return 0;
	}

	public FrameLayout getVideoLayout() { return _videoLayout; }

	static int NOTIFY_ID = 12367098; // Random ID

	private static DemoGLSurfaceView mGLView = null;
	private static AudioThread mAudioThread = null;

	private Button _btn = null;
	private LinearLayout _layout = null;

	private FrameLayout _videoLayout = null;
	private EditText _screenKeyboard = null;
	private boolean sdlInited = false;
	boolean _isPaused = false;
	private InputMethodManager _inputManager = null;

	public LinkedList<Integer> textInput = new LinkedList<Integer> ();
	public static MainActivity instance = null;
}

// *** HONEYCOMB / ICS FIX FOR FULLSCREEN MODE, by lmak ***
//abstract class DimSystemStatusBar
//{
//	public static DimSystemStatusBar get()
//	{
//		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
//			return DimSystemStatusBarHoneycomb.Holder.sInstance;
//		else
//			return DimSystemStatusBarDummy.Holder.sInstance;
//	}
//	public abstract void dim(final View view);
//
//	private static class DimSystemStatusBarHoneycomb extends DimSystemStatusBar
//	{
//		private static class Holder
//		{
//			private static final DimSystemStatusBarHoneycomb sInstance = new DimSystemStatusBarHoneycomb();
//		}
//	    public void dim(final View view)
//	    {
//	      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
//	         System.out.println("Dimming system status bar for Honeycomb");
//	         setLowProfileMode(view);
//	         // This part of code generates lot of debug messages on emulator: "V/PhoneStatusBar(  133): setLightsOn(true)"
//	         /*
//	         view.setOnSystemUiVisibilityChangeListener(
//	               new View.OnSystemUiVisibilityChangeListener() {
//	                  public void onSystemUiVisibilityChange(int visibility) {
//	                        android.os.Handler handler = new Handler() {
//	                           @Override
//	                           public void handleMessage(Message msg) {
//	                              if(msg.what == 10) {
//	                                 setLowProfileMode(view);
//	                              }
//	                           }
//	                        };
//	                        Message msg = handler.obtainMessage(10);
//	                        handler.sendMessageDelayed(msg,1000);
//	                   }
//	               }
//	         );
//	         */
//	      }
//	   }
//	   // *** HONEYCOMB / ICS FIX FOR FULLSCREEN MODE ***
//	   private void setLowProfileMode(final View view)
//	   {
//	       if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
//	         int hiddenStatusCode = android.view.View.STATUS_BAR_HIDDEN;
//	         /*
//	         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//	            // ICS has the same constant redefined with a different name.
//	            hiddenStatusCode = android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE;
//	         }
//	         */
//	         view.setSystemUiVisibility(hiddenStatusCode);
//	      }
//	   }
//	}
//	private static class DimSystemStatusBarDummy extends DimSystemStatusBar
//	{
//		private static class Holder
//		{
//			private static final DimSystemStatusBarDummy sInstance = new DimSystemStatusBarDummy();
//		}
//		public void dim(final View view)
//		{
//		}
//	}
//}
