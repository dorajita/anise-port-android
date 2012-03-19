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
import java.io.*;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.lang.String;


// TODO: too much code here, split into multiple files, possibly auto-generated menus?
class Settings
{
	static void Load( final MainActivity p )
	{
		nativeInitKeymap();
		for( int i = 0; i < SDL_Keys.JAVA_KEYCODE_LAST; i++ )
		{
			int sdlKey = nativeGetKeymapKey(i);
			int idx = 0;
			for(int ii = 0; ii < SDL_Keys.values.length; ii++)
				if(SDL_Keys.values[ii] == sdlKey)
					idx = ii;
			Globals.RemapHwKeycode[i] = idx;
		}
		for( int i = 0; i < Globals.RemapScreenKbKeycode.length; i++ )
		{
			int sdlKey = nativeGetKeymapKeyScreenKb(i);
			int idx = 0;
			for(int ii = 0; ii < SDL_Keys.values.length; ii++)
				if(SDL_Keys.values[ii] == sdlKey)
					idx = ii;
			Globals.RemapScreenKbKeycode[i] = idx;
		}
		Globals.ScreenKbControlsShown[0] = Globals.AppNeedsArrowKeys;
		Globals.ScreenKbControlsShown[1] = Globals.AppNeedsTextInput;
		for( int i = 2; i < Globals.ScreenKbControlsShown.length; i++ )
			Globals.ScreenKbControlsShown[i] = ( i - 2 < Globals.AppTouchscreenKeyboardKeysAmount );
		for( int i = 0; i < Globals.RemapMultitouchGestureKeycode.length; i++ )
		{
			int sdlKey = nativeGetKeymapKeyMultitouchGesture(i);
			int idx = 0;
			for(int ii = 0; ii < SDL_Keys.values.length; ii++)
				if(SDL_Keys.values[ii] == sdlKey)
					idx = ii;
			Globals.RemapMultitouchGestureKeycode[i] = idx;
		}
		for( int i = 0; i < Globals.MultitouchGesturesUsed.length; i++ )
			Globals.MultitouchGesturesUsed[i] = true;
	}

	static void Apply(Activity p)
	{
		nativeSetVideoDepth(Globals.VideoDepthBpp, Globals.NeedGles2 ? 1 : 0);
		if(Globals.SmoothVideo)
			nativeSetSmoothVideo();
		if( Globals.CompatibilityHacksVideo )
		{
			Globals.MultiThreadedVideo = true;
			Globals.SwVideoMode = true;
			nativeSetCompatibilityHacks();
		}
		if( Globals.SwVideoMode && Globals.MultiThreadedVideo )
			nativeSetVideoMultithreaded();
		if( Globals.PhoneHasTrackball )
			nativeSetTrackballUsed();
		if( Globals.AppUsesMouse )
			nativeSetMouseUsed( Globals.RightClickMethod,
								Globals.ShowScreenUnderFinger,
								Globals.LeftClickMethod,
								Globals.MoveMouseWithJoystick ? 1 : 0,
								Globals.ClickMouseWithDpad ? 1 : 0,
								Globals.ClickScreenPressure,
								Globals.ClickScreenTouchspotSize,
								Globals.MoveMouseWithJoystickSpeed,
								Globals.MoveMouseWithJoystickAccel,
								Globals.LeftClickKey,
								Globals.RightClickKey,
								Globals.LeftClickTimeout,
								Globals.RightClickTimeout,
								Globals.RelativeMouseMovement ? 1 : 0,
								Globals.RelativeMouseMovementSpeed,
								Globals.RelativeMouseMovementAccel,
								Globals.ShowMouseCursor ? 1 : 0 );
		if( Globals.AppUsesJoystick && (Globals.UseAccelerometerAsArrowKeys || Globals.UseTouchscreenKeyboard) )
			nativeSetJoystickUsed();
		if( Globals.AppUsesMultitouch )
			nativeSetMultitouchUsed();
		nativeSetAccelerometerSettings(Globals.AccelerometerSensitivity, Globals.AccelerometerCenterPos);
		nativeSetTrackballDampening(Globals.TrackballDampening);
		if( Globals.UseTouchscreenKeyboard )
		{
			boolean screenKbReallyUsed = false;
			for( int i = 0; i < Globals.ScreenKbControlsShown.length; i++ )
				if( Globals.ScreenKbControlsShown[i] )
					screenKbReallyUsed = true;
			if( screenKbReallyUsed )
			{
				nativeSetTouchscreenKeyboardUsed();
				nativeSetupScreenKeyboard(	Globals.TouchscreenKeyboardSize,
											Globals.TouchscreenKeyboardDrawSize,
											Globals.TouchscreenKeyboardTheme,
											Globals.AppTouchscreenKeyboardKeysAmountAutoFire,
											Globals.TouchscreenKeyboardTransparency );
				SetupTouchscreenKeyboardGraphics(p);
				for( int i = 0; i < Globals.ScreenKbControlsShown.length; i++ )
					nativeSetScreenKbKeyUsed(i, Globals.ScreenKbControlsShown[i] ? 1 : 0);
				for( int i = 0; i < Globals.RemapScreenKbKeycode.length; i++ )
					nativeSetKeymapKeyScreenKb(i, SDL_Keys.values[Globals.RemapScreenKbKeycode[i]]);
				for( int i = 0; i < Globals.ScreenKbControlsLayout.length; i++ )
					if( Globals.ScreenKbControlsLayout[i][0] < Globals.ScreenKbControlsLayout[i][2] )
						nativeSetScreenKbKeyLayout( i, Globals.ScreenKbControlsLayout[i][0], Globals.ScreenKbControlsLayout[i][1],
							Globals.ScreenKbControlsLayout[i][2], Globals.ScreenKbControlsLayout[i][3]);
			}
			else
				Globals.UseTouchscreenKeyboard = false;
		}

		for( int i = 0; i < SDL_Keys.JAVA_KEYCODE_LAST; i++ )
			nativeSetKeymapKey(i, SDL_Keys.values[Globals.RemapHwKeycode[i]]);
		for( int i = 0; i < Globals.RemapMultitouchGestureKeycode.length; i++ )
			nativeSetKeymapKeyMultitouchGesture(i, Globals.MultitouchGesturesUsed[i] ? SDL_Keys.values[Globals.RemapMultitouchGestureKeycode[i]] : 0);
		nativeSetMultitouchGestureSensitivity(Globals.MultitouchGestureSensitivity);
		if( Globals.TouchscreenCalibration[2] > Globals.TouchscreenCalibration[0] )
			nativeSetTouchscreenCalibration(Globals.TouchscreenCalibration[0], Globals.TouchscreenCalibration[1],
				Globals.TouchscreenCalibration[2], Globals.TouchscreenCalibration[3]);

		String lang = new String(Locale.getDefault().getLanguage());
		if( Locale.getDefault().getCountry().length() > 0 )
			lang = lang + "_" + Locale.getDefault().getCountry();
		System.out.println( "libSDL: setting envvar LANGUAGE to '" + lang + "'");
		nativeSetEnv( "LANG", lang );
		nativeSetEnv( "LANGUAGE", lang );
		// TODO: get current user name and set envvar USER, the API is not availalbe on Android 1.6 so I don't bother with this
	}

	static byte [] loadRaw(Activity p,int res)
	{
		byte [] buf = new byte[65536 * 2];
		byte [] a = new byte[0];
		try{
			InputStream is = new GZIPInputStream(p.getResources().openRawResource(res));
			int readed = 0;
			while( (readed = is.read(buf)) >= 0 )
			{
				byte [] b = new byte [a.length + readed];
				System.arraycopy(a, 0, b, 0, a.length);
				System.arraycopy(buf, 0, b, a.length, readed);
				a = b;
			}
		} catch(Exception e) {};
		return a;
	}
	
	static void SetupTouchscreenKeyboardGraphics(Activity p)
	{
		if( Globals.UseTouchscreenKeyboard )
		{
			nativeSetupScreenKeyboardButtons(loadRaw(p, R.raw.simpletheme));
		}
	}
	
	private static native void nativeSetTrackballUsed();
	private static native void nativeSetTrackballDampening(int value);
	private static native void nativeSetAccelerometerSettings(int sensitivity, int centerPos);
	private static native void nativeSetMouseUsed(int RightClickMethod, int ShowScreenUnderFinger, int LeftClickMethod, 
													int MoveMouseWithJoystick, int ClickMouseWithDpad, int MaxForce, int MaxRadius,
													int MoveMouseWithJoystickSpeed, int MoveMouseWithJoystickAccel,
													int leftClickKeycode, int rightClickKeycode,
													int leftClickTimeout, int rightClickTimeout,
													int relativeMovement, int relativeMovementSpeed,
													int relativeMovementAccel, int showMouseCursor);
	public static native void nativeSetExternalMouseDetected();
	private static native void nativeSetJoystickUsed();
	private static native void nativeSetMultitouchUsed();
	private static native void nativeSetTouchscreenKeyboardUsed();
	private static native void nativeSetSmoothVideo();
	private static native void nativeSetVideoDepth(int bpp, int gles2);
	private static native void nativeSetCompatibilityHacks();
	private static native void nativeSetVideoMultithreaded();
	private static native void nativeSetupScreenKeyboard(int size, int drawsize, int theme, int nbuttonsAutoFire, int transparency);
	private static native void nativeSetupScreenKeyboardButtons(byte[] img);
	private static native void nativeInitKeymap();
	private static native int  nativeGetKeymapKey(int key);
	private static native void nativeSetKeymapKey(int javakey, int key);
	private static native int  nativeGetKeymapKeyScreenKb(int keynum);
	private static native void nativeSetKeymapKeyScreenKb(int keynum, int key);
	private static native void nativeSetScreenKbKeyUsed(int keynum, int used);
	private static native void nativeSetScreenKbKeyLayout(int keynum, int x1, int y1, int x2, int y2);
	private static native int  nativeGetKeymapKeyMultitouchGesture(int keynum);
	private static native void nativeSetKeymapKeyMultitouchGesture(int keynum, int key);
	private static native void nativeSetMultitouchGestureSensitivity(int sensitivity);
	private static native void nativeSetTouchscreenCalibration(int x1, int y1, int x2, int y2);
	public static native void  nativeSetEnv(final String name, final String value);
	public static native int   nativeChmod(final String name, int mode);
}

