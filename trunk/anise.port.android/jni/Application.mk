APP_PROJECT_PATH := $(call my-dir)/..

APP_STL := stlport_static
APP_PLATFORM := android-8
APP_CFLAGS := -O3 -funroll-loops

include $(call my-dir)/Settings.mk

