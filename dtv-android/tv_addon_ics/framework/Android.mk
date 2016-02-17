ifeq "$(RLS_CUSTOM_BUILD)" "true"
include $(shell pwd)/../dtv-android/tv_addon_ics/framework/*/Android.mk
else
include $(call first-makefiles-under,$(LOCAL_PATH)/tvmCustom)
include $(call all-subdir-makefiles)
endif
