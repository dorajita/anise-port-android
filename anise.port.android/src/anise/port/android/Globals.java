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

import android.view.KeyEvent;

class Globals {
	public static String AppLibraries[] = { "sdl-1.2", };
	public static String AppZipFile = ""; 	//TODO: game archive in assests ( ex- "nanpa.zip"; )
	public static String DataDir = "/data/data/anise.port.android/files";
	public static String CommandLine = "";	//TODO: commandline for run anise ( ex- "anise nono -ls -p=" + DataDir; )  
	public static int VideoDepthBpp = 16;
	public static boolean SwVideoMode = false;
	public static boolean NeedDepthBuffer = false;
	public static boolean NeedStencilBuffer = false;
	public static boolean NeedGles2 = false;
	public static boolean CompatibilityHacksVideo = false;
	public static boolean CompatibilityHacksStaticInit = false;
	public static boolean HorizontalOrientation = true;
	public static boolean InhibitSuspend = false;
	public static boolean AppUsesMouse = true;
	public static boolean AppNeedsTwoButtonMouse = false;
	public static boolean ForceRelativeMouseMode = false; // If both on-screen keyboard and mouse are needed, this will only set the default setting, user may override it later
	public static boolean ShowMouseCursor = false;
	public static boolean AppNeedsArrowKeys = false;                                                                                                                                                                                                                                                            
	public static boolean AppNeedsTextInput = false;
	public static boolean AppUsesJoystick = false;
	public static boolean AppHandlesJoystickSensitivity = false;
	public static boolean AppUsesMultitouch = false;
	public static boolean NonBlockingSwapBuffers = false;
	public static int AppTouchscreenKeyboardKeysAmount = 0;
	public static int AppTouchscreenKeyboardKeysAmountAutoFire = 1;

	// Phone-specific config, modified by user in "Change phone config" startup dialog, TODO: move this to settings
	public static boolean PhoneHasTrackball = false;
	public static boolean PhoneHasArrowKeys = false;
	public static boolean UseAccelerometerAsArrowKeys = false;
	public static boolean UseTouchscreenKeyboard = true;
	public static int TouchscreenKeyboardSize = 0;
	public static int TouchscreenKeyboardDrawSize = 1;
	public static int TouchscreenKeyboardTheme = 2;
	public static int TouchscreenKeyboardTransparency = 2;
	public static int AccelerometerSensitivity = 2;
	public static int AccelerometerCenterPos = 2;
	public static int TrackballDampening = 0;
	public static int AudioBufferConfig = 0;
	public static int LeftClickMethod = AppNeedsTwoButtonMouse ? Mouse.LEFT_CLICK_WITH_TAP_OR_TIMEOUT : Mouse.LEFT_CLICK_NORMAL;
	public static int LeftClickKey = KeyEvent.KEYCODE_DPAD_CENTER;
	public static int LeftClickTimeout = 3;
	public static int RightClickTimeout = 4;
	public static int RightClickMethod = AppNeedsTwoButtonMouse ? Mouse.RIGHT_CLICK_WITH_MULTITOUCH : Mouse.RIGHT_CLICK_NONE;
	public static int RightClickKey = KeyEvent.KEYCODE_MENU;
	public static boolean MoveMouseWithJoystick = false;
	public static int MoveMouseWithJoystickSpeed = 0;
	public static int MoveMouseWithJoystickAccel = 0;
	public static boolean ClickMouseWithDpad = false;
	public static boolean RelativeMouseMovement = ForceRelativeMouseMode; // Laptop touchpad mode
	public static int RelativeMouseMovementSpeed = 2;
	public static int RelativeMouseMovementAccel = 0;
	public static int ShowScreenUnderFinger = Mouse.ZOOM_NONE;
	public static boolean KeepAspectRatio = false;
	public static int ClickScreenPressure = 0;
	public static int ClickScreenTouchspotSize = 0;
	public static int RemapHwKeycode[] = new int[SDL_Keys.JAVA_KEYCODE_LAST];
	public static int RemapScreenKbKeycode[] = new int[6];
	public static boolean ScreenKbControlsShown[] = new boolean[8]; /* Also joystick and text input button added */
	public static int ScreenKbControlsLayout[][] = new int[8][4];
	public static int RemapMultitouchGestureKeycode[] = new int[4];
	public static boolean MultitouchGesturesUsed[] = new boolean[4];
	public static int MultitouchGestureSensitivity = 1;
	public static int TouchscreenCalibration[] = new int[4];
	public static boolean SmoothVideo = true;
	public static boolean MultiThreadedVideo = false;
}
