# This file is the top android makefile for all sub-modules.

LOCAL_PATH := $(call my-dir)

GLIB_TOP := $(LOCAL_PATH)

include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := eng

include $(GLIB_TOP)/glib/Android.mk
#include $(GLIB_TOP)/gmodule/Android.mk
#include $(GLIB_TOP)/gthread/Android.mk
#include $(GLIB_TOP)/gobject/Android.mk


