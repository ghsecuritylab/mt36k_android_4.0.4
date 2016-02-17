#
# Copyright (c) 2013 TCL Corp.
# All rights reserved.
# usage :
#   include $(ANDROID_BUILD_TOP)/vendor/tcl/tvos_addon/tvos_base.mk
# lvh@tcl , 2014.1 create

define tcl-test-java-files
$(patsubst ./%,%, $(shell cd $(LOCAL_PATH) ; find $(1) -name "*.java" -and -not -name ".*") )
endef

define tcl-test-cpp-files
$(patsubst ./%,%, $(shell cd $(LOCAL_PATH) ; find $(1) -name "*.cpp" -and -not -name ".*") )
endef

define tcl-test-c-files
$(patsubst ./%,%, $(shell cd $(LOCAL_PATH) ; find $(1) -name "*.c" -and -not -name ".*") )
endef

