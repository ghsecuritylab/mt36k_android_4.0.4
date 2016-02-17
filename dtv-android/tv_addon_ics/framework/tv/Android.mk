# This makefile shows how to build your own shared library that can be
# shipped on the system of a phone, and included additional examples of
# including JNI code with the library and writing client applications against it.

LOCAL_PATH := $(call my-dir)

# Copy the add-on library XML files in the system image.
PRODUCT_COPY_FILES += \
    $(LOCAL_PATH)/com.mediatek.tv.xml:system/etc/permissions/com.mediatek.tv.xml

# the library
# ============================================================
include $(CLEAR_VARS)


#LOCAL_MODULE_TAGS := optional
#LOCAL_JAVA_LIBRARIES := core framework
#LOCAL_JAVA_LIBRARIES += android-common 

LOCAL_JAVA_LIBRARIES = com.mediatek.tv.custom

LOCAL_SRC_FILES := \
            ../tvmCustom/java/com/mediatek/tv/common/BaseConfigType.java \
            ../tvmCustom/java/com/mediatek/tv/common/ConfigType.java \
            $(call all-java-files-under,java) \
            $(call all-java-files-under,gen)


# This is the target being built.
LOCAL_MODULE:= com.mediatek.tv

include $(BUILD_JAVA_LIBRARY)


# the documentation
# ============================================================
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
    ../tvmCustom/java/com/mediatek/tv/common/BaseConfigType.java \
    ../tvmCustom/java/com/mediatek/tv/common/ConfigType.java \
    java/com/mediatek/tv/service/InputService.java    \
    java/com/mediatek/tv/service/BroadcastService.java    \
    java/com/mediatek/tv/service/InputServiceTest.java    \
    java/com/mediatek/tv/service/TVRemoteServiceHandler.java    \
    java/com/mediatek/tv/service/TVCallBack.java    \
    java/com/mediatek/tv/service/IEventNotify.java    \
    java/com/mediatek/tv/service/OSDService.java    \
    java/com/mediatek/tv/service/Logger.java    \
    java/com/mediatek/tv/service/ServiceFactory.java    \
    java/com/mediatek/tv/service/CIServiceTest.java    \
    java/com/mediatek/tv/service/ConfigService.java    \
    java/com/mediatek/tv/service/TVRemoteService.java    \
    java/com/mediatek/tv/service/ChannelService.java    \
    java/com/mediatek/tv/service/IChannelNotify.java    \
    java/com/mediatek/tv/service/ScanService.java    \
    java/com/mediatek/tv/service/TVClientHandler.java    \
    java/com/mediatek/tv/service/IService.java    \
    java/com/mediatek/tv/service/CIService.java    \
    java/com/mediatek/tv/service/TVNative.java    \
    java/com/mediatek/tv/service/ServiceManager.java    \
    java/com/mediatek/tv/service/EventService.java    \
    java/com/mediatek/tv/common/TVCommon.java    \
    java/com/mediatek/tv/common/ConfigValue.java    \
    java/com/mediatek/tv/common/TVMException.java    \
    java/com/mediatek/tv/common/ChannelCommon.java    \
    java/com/mediatek/tv/model/EventComponent.java    \
    java/com/mediatek/tv/model/AudioLanguageInfo.java    \
    java/com/mediatek/tv/model/DvbcFreqRange.java    \
    java/com/mediatek/tv/model/EventActiveWindow.java    \
    java/com/mediatek/tv/model/EventLinkage.java    \
    java/com/mediatek/tv/model/EventQuery.java    \
    java/com/mediatek/tv/model/InputExchangeOutputMute.java    \
    java/com/mediatek/tv/model/DvbcProgramType.java    \
    java/com/mediatek/tv/model/MMIMenu.java    \
    java/com/mediatek/tv/model/DtDTG.java    \
    java/com/mediatek/tv/model/ScanExchange.java    \
    java/com/mediatek/tv/model/DtType.java    \
    java/com/mediatek/tv/model/HostControlResource.java    \
    java/com/mediatek/tv/model/ScanListener.java    \
    java/com/mediatek/tv/model/InputRegion.java    \
    java/com/mediatek/tv/model/EventUpdateReason.java    \
    java/com/mediatek/tv/model/UARTSerialListener.java    \
    java/com/mediatek/tv/model/ScanExchangeFrenquenceRange.java    \
    java/com/mediatek/tv/model/MMI.java    \
    java/com/mediatek/tv/model/ChannelInfo.java    \
    java/com/mediatek/tv/model/VideoResolution.java    \
    java/com/mediatek/tv/model/MMIEnq.java    \
    java/com/mediatek/tv/model/CIInputDTVPath.java    \
    java/com/mediatek/tv/model/DtListener.java    \
    java/com/mediatek/tv/model/AnalogChannelInfo.java    \
    java/com/mediatek/tv/model/SignalLevelInfo.java    \
    java/com/mediatek/tv/model/HostControlTune.java    \
    java/com/mediatek/tv/model/HostControlReplace.java    \
    java/com/mediatek/tv/model/DvbChannelInfo.java    \
    java/com/mediatek/tv/model/InputListener.java    \
    java/com/mediatek/tv/model/CIListener.java    \
    java/com/mediatek/tv/model/ChannelModel.java    \
    java/com/mediatek/tv/model/EventInfo.java    \
    java/com/mediatek/tv/model/ScanParaPalSecam.java    \
    java/com/mediatek/tv/model/SubtitleInfo.java    \
    java/com/mediatek/tv/model/InputExchange.java    \
    java/com/mediatek/tv/model/InputRecord.java    \
    java/com/mediatek/tv/model/CITSPath.java    \
    java/com/mediatek/tv/model/EventCommand.java    \
    java/com/mediatek/tv/model/ScanParaDvbc.java    \
    java/com/mediatek/tv/model/CIPath.java    \
    java/com/mediatek/tv/model/RunTime.java    \
    java/com/mediatek/tv/model/UARTSerialParams.java    \
    java/com/mediatek/tv/model/MainFrequence.java    \
    java/com/mediatek/tv/model/AudioInfo.java    \
    java/com/mediatek/tv/model/ScanParams.java    \
    java/com/mediatek/tv/model/ExtraChannelInfo.java    \
    java/com/mediatek/tv/TVManager.java    \
    gen/com/mediatek/tv/service/ITVCallBack.java    \
    gen/com/mediatek/tv/service/R.java    \
    gen/com/mediatek/tv/service/ITVRemoteService.java


LOCAL_MODULE:= tv
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_DROIDDOC_USE_STANDARD_DOCLET := true

include $(BUILD_DROIDDOC)


# The JNI component
# ============================================================
# Also build all of the sub-targets under this one: the library's
# associated JNI code, and a sample client of the library.
include $(CLEAR_VARS)

include $(call all-makefiles-under,$(LOCAL_PATH))

