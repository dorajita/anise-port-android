LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := application
LOCAL_C_INCLUDES := $(LOCAL_PATH)/../sdl-$(SDL_VERSION)/include
LOCAL_CPP_EXTENSION := .cpp

ANISE_SRC_DIR		:= anise_src
ANISE_SRC_CPP		:= \
	main.cpp \
	engine.cpp \
	op_animate.cpp \
	op_call.cpp \
	op_callprocedure.cpp \
	op_clearscreen.cpp \
	op_defineprocedure.cpp \
	op_delay.cpp \
	op_displaynumber.cpp \
	op_if.cpp \
	op_makearray.cpp \
	op_makebytearray.cpp \
	op_null.cpp \
	op_saveconstant.cpp \
	op_saveexpression.cpp \
	op_setbasevariable.cpp \
	op_setcolor.cpp \
	op_setdialoguecolor.cpp \
	op_utility.cpp \
	op_wait.cpp \
	op4_blitdirect.cpp \
	op4_blitmasked.cpp \
	op4_blitswapped.cpp \
	op4_break.cpp \
	op4_callscript.cpp \
	op4_changeslot.cpp \
	op4_checkclick.cpp \
	op4_continue.cpp \
	op4_displayselection.cpp \
	op4_drawinversebox.cpp \
	op4_drawsolidbox.cpp \
	op4_dummy.cpp \
	op4_field.cpp \
	op4_initializeselection.cpp \
	op4_jumpscript.cpp \
	op4_loadfile.cpp \
	op4_loadimage.cpp \
	op4_manipulateflag.cpp \
	op4_mouse.cpp \
	op4_palette.cpp \
	op4_sound.cpp \
	op4_while.cpp \
	oput_dummy.cpp \
	oput_overlapscreen.cpp \
	oput_sprayscreen.cpp \
	oput_swapscreen.cpp \
	oput_unpackaniheader.cpp \
	script_opre.cpp \
	memory_object.cpp \
	memory_segment.cpp \
	memory_block.cpp \
	timer.cpp \
	input.cpp \
	sound.cpp \
	video.cpp \
	script.cpp \
	script_value.cpp \
	script_parameter.cpp \
	script_stack.cpp \
	file.cpp \
	image.cpp \
	animation.cpp \
	dialogue.cpp \
	dialogue_jis.cpp \
	dialogue_jishan.cpp \
	dialogue_gamebox.cpp \
	dialogue_sagwa.cpp \
	field.cpp \
	field_move.cpp \
	field_path.cpp \
	option.cpp \
	mfile.cpp \
	ymf262.cpp \

LOCAL_SRC_FILES := $(foreach FILE, $(ANISE_SRC_CPP), $(addprefix $(ANISE_SRC_DIR)/, $(FILE)))
LOCAL_SHARED_LIBRARIES := sdl-$(SDL_VERSION)

include $(BUILD_SHARED_LIBRARY)
