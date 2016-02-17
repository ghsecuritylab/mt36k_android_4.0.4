LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

DEFINES += 


LOCAL_SRC_FILES := \
UsageEnvironment/UsageEnvironment.cpp \
UsageEnvironment/strDup.cpp \
UsageEnvironment/HashTable.cpp \
groupsock/inet.c \
groupsock/IOHandlers.cpp \
groupsock/Groupsock.cpp \
groupsock/GroupsockHelper.cpp \
groupsock/NetInterface.cpp \
groupsock/GroupEId.cpp \
groupsock/NetAddress.cpp \
BasicUsageEnvironment/BasicTaskScheduler0.cpp \
BasicUsageEnvironment/BasicUsageEnvironment0.cpp \
BasicUsageEnvironment/BasicUsageEnvironment.cpp \
BasicUsageEnvironment/BasicTaskScheduler.cpp \
BasicUsageEnvironment/BasicHashTable.cpp \
BasicUsageEnvironment/DelayQueue.cpp \
liveMedia/our_md5.c \
liveMedia/our_md5hl.c \
liveMedia/rtcp_from_spec.c \
liveMedia/OutputFile.cpp \
liveMedia/MPEG2TransportStreamFromESSource.cpp \
liveMedia/MPEG2TransportFileServerMediaSubsession.cpp \
liveMedia/FileSink.cpp \
liveMedia/AC3AudioRTPSource.cpp \
liveMedia/H264VideoRTPSink.cpp \
liveMedia/MPEG1or2AudioRTPSource.cpp \
liveMedia/Media.cpp \
liveMedia/uLawAudioFilter.cpp \
liveMedia/AMRAudioRTPSink.cpp \
liveMedia/ADTSAudioFileSource.cpp \
liveMedia/MediaSession.cpp \
liveMedia/SIPClient.cpp \
liveMedia/RTPSink.cpp \
liveMedia/WAVAudioFileServerMediaSubsession.cpp \
liveMedia/MediaSink.cpp \
liveMedia/MPEG4ESVideoRTPSink.cpp \
liveMedia/AVIFileSink.cpp \
liveMedia/H261VideoRTPSource.cpp \
liveMedia/MPEG4VideoFileServerMediaSubsession.cpp \
liveMedia/VideoRTPSink.cpp \
liveMedia/DVVideoRTPSource.cpp \
liveMedia/GSMAudioRTPSink.cpp \
liveMedia/QCELPAudioRTPSource.cpp \
liveMedia/MPEG1or2FileServerDemux.cpp \
liveMedia/RTSPServer.cpp \
liveMedia/MP3StreamState.cpp \
liveMedia/FileServerMediaSubsession.cpp \
liveMedia/RTPInterface.cpp \
liveMedia/ByteStreamMultiFileSource.cpp \
liveMedia/MPEG4LATMAudioRTPSource.cpp \
liveMedia/BitVector.cpp \
liveMedia/MPEG1or2DemuxedElementaryStream.cpp \
liveMedia/DeviceSource.cpp \
liveMedia/MP3Transcoder.cpp \
liveMedia/H264VideoFileServerMediaSubsession.cpp \
liveMedia/MP3ADURTPSource.cpp \
liveMedia/H263plusVideoRTPSink.cpp \
liveMedia/MP3ADU.cpp \
liveMedia/MP3ADUdescriptor.cpp \
liveMedia/MP3InternalsHuffmanTable.cpp \
liveMedia/MPEG1or2AudioStreamFramer.cpp \
liveMedia/StreamParser.cpp \
liveMedia/MPEG1or2AudioRTPSink.cpp \
liveMedia/MPEG2TransportStreamFromPESSource.cpp \
liveMedia/MPEGVideoStreamFramer.cpp \
liveMedia/H264VideoStreamFramer.cpp \
liveMedia/OnDemandServerMediaSubsession.cpp \
liveMedia/MP3ADUinterleaving.cpp \
liveMedia/BasicUDPSink.cpp \
liveMedia/MPEG1or2VideoStreamDiscreteFramer.cpp \
liveMedia/SimpleRTPSink.cpp \
liveMedia/RTSPClient.cpp \
liveMedia/MPEGVideoStreamParser.cpp \
liveMedia/AMRAudioRTPSource.cpp \
liveMedia/ADTSAudioFileServerMediaSubsession.cpp \
liveMedia/DigestAuthentication.cpp \
liveMedia/MPEG1or2VideoFileServerMediaSubsession.cpp \
liveMedia/AMRAudioFileSink.cpp \
liveMedia/Locale.cpp \
liveMedia/MPEG4VideoStreamDiscreteFramer.cpp \
liveMedia/H263plusVideoFileServerMediaSubsession.cpp \
liveMedia/MultiFramedRTPSource.cpp \
liveMedia/WAVAudioFileSource.cpp \
liveMedia/AC3AudioStreamFramer.cpp \
liveMedia/AudioInputDevice.cpp \
liveMedia/JPEGVideoRTPSource.cpp \
liveMedia/MPEG4GenericRTPSource.cpp \
liveMedia/SimpleRTPSource.cpp \
liveMedia/DVVideoFileServerMediaSubsession.cpp \
liveMedia/HTTPSink.cpp \
liveMedia/Base64.cpp \
liveMedia/MPEG2TransportStreamFramer.cpp \
liveMedia/BasicUDPSource.cpp \
liveMedia/MP3HTTPSource.cpp \
liveMedia/JPEGVideoSource.cpp \
liveMedia/AMRAudioFileServerMediaSubsession.cpp \
liveMedia/H264VideoRTPSource.cpp \
liveMedia/MP3InternalsHuffman.cpp \
liveMedia/ByteStreamFileSource.cpp \
liveMedia/AudioRTPSink.cpp \
liveMedia/InputFile.cpp \
liveMedia/MPEG2IndexFromTransportStream.cpp \
liveMedia/MPEG1or2VideoRTPSink.cpp \
liveMedia/FramedFilter.cpp \
liveMedia/AMRAudioSource.cpp \
liveMedia/MPEG4VideoStreamFramer.cpp \
liveMedia/MediaSource.cpp \
liveMedia/DVVideoRTPSink.cpp \
liveMedia/RTSPCommon.cpp \
liveMedia/RTCP.cpp \
liveMedia/MPEG4LATMAudioRTPSink.cpp \
liveMedia/MP3FileSource.cpp \
liveMedia/H264VideoFileSink.cpp \
liveMedia/H264VideoStreamDiscreteFramer.cpp \
liveMedia/MP3ADUTranscoder.cpp \
liveMedia/MPEG1or2VideoStreamFramer.cpp \
liveMedia/MPEG2TransportStreamIndexFile.cpp \
liveMedia/DVVideoStreamFramer.cpp \
liveMedia/MP3ADURTPSink.cpp \
liveMedia/DarwinInjector.cpp \
liveMedia/FramedFileSource.cpp \
liveMedia/H263plusVideoRTPSource.cpp \
liveMedia/MPEG1or2Demux.cpp \
liveMedia/MPEG1or2DemuxedServerMediaSubsession.cpp \
liveMedia/MP3Internals.cpp \
liveMedia/H263plusVideoStreamParser.cpp \
liveMedia/MP3AudioFileServerMediaSubsession.cpp \
liveMedia/MPEG4ESVideoRTPSource.cpp \
liveMedia/MPEG1or2VideoRTPSource.cpp \
liveMedia/QuickTimeFileSink.cpp \
liveMedia/ServerMediaSession.cpp \
liveMedia/FramedSource.cpp \
liveMedia/H263plusVideoStreamFramer.cpp \
liveMedia/PassiveServerMediaSubsession.cpp \
liveMedia/AC3AudioRTPSink.cpp \
liveMedia/JPEGVideoRTPSink.cpp \
liveMedia/MultiFramedRTPSink.cpp \
liveMedia/MPEG2TransportStreamTrickModeFilter.cpp \
liveMedia/MPEG4GenericRTPSink.cpp \
liveMedia/AMRAudioFileSource.cpp \
liveMedia/RTPSource.cpp \
liveMedia/MPEG1or2VideoHTTPSink.cpp \
liveMedia/MPEG2TransportStreamMultiplexor.cpp \
liveMedia/QuickTimeGenericRTPSource.cpp \

LOCAL_C_INCLUDES += \
	$(JNI_H_INCLUDE) \
	$(LOCAL_PATH) \
    $(LOCAL_PATH)/BasicUsageEnvironment \
	$(LOCAL_PATH)/groupsock \
	$(LOCAL_PATH)/liveMedia \
	$(LOCAL_PATH)/UsageEnvironment \
	$(LOCAL_PATH)/BasicUsageEnvironment/include \
	$(LOCAL_PATH)/groupsock/include \
	$(LOCAL_PATH)/liveMedia/include \
	$(LOCAL_PATH)/UsageEnvironment/include \
	$(LOCAL_PATH)/../../froyo-2.2/external/stlport/stlport \
	$(LOCAL_PATH)/../../froyo-2.2/bionic \
	$(LOCAL_PATH)/../../froyo-2.2/bionic/libstdc++/include \




LOCAL_SHARED_LIBRARIES += libnativehelper libutils libstlport
include external/stlport/libstlport.mk

LOCAL_CFLAGS := -DSOCKLEN_T=socklen_t -DNO_SSTREAM=1 -D_LARGEFILE_SOURCE=1 -D_FILE_OFFSET_BITS=64 -DANDROID -D__arm
LOCAL_CXXFLAGS := -DSOCKLEN_T=socklen_t -DNO_SSTREAM=1 -D_LARGEFILE_SOURCE=1 -D_FILE_OFFSET_BITS=64 -DANDROID -D__arm

LOCAL_MODULE:= libliveMedia

LOCAL_MODULE_TAGS := optional

LOCAL_LDLIBS := -llog -ldl
#LOCAL_LDLIBS += -L$(LOCAL_PATH) -lstlport

#APP_STL := stlport_static
#LOCAL_LDLIBS += -lstlport

include $(BUILD_SHARED_LIBRARY)

