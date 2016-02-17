#
# Copyright (C) 2007 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# This is a generic phone product that isn't specialized for a specific device.
# It includes the base Android platform.

PRODUCT_POLICY := android.policy_phone

PRODUCT_PACKAGES := \
    DeskClock \
    AlarmProvider \
    CertInstaller \
    DrmProvider \
    QuickSearchBox \
    Sync \
    SystemUI \
    TCL_TV \
    FileExplorer \
    ImageViewer \
    InfoPush \
    USBMediaBrowser \
    ComMediaPlayer \
    SystemSettings \
    TclBrowser \
    UsageStats \
    UserCenter \
    FaceDetect \
    Updater \
    SyncProvider \
    bluetooth-health \
    hostapd \
    TVRCIService \
    PinyinIME \
    TCL_TVOS_TVManager_FastBoot \
    TCL_TVOS_TVManager_SafeProtection \
    TCL_TVOS_TVManager_NetManager \
    TCL_TVOS_TVManager_SoftwareManager \
    TCL_TVOS_TVManager_SystemInfo \
    TCL_TVOS_TVManager_TaskManager \
    TCL_TVOS_TVManager_TVExamination \
    TCL_TVOS_TVManager_TVManager \
    DeviceAuthentication \
    3D \
    TCL_Camera \
    HuanOnlineMusic \
    MessageBox \
    News \
    SmartTVHelper \
    Stock \
    SystemDemo \
    TvWidget \
    AppStore3.3 \
    AppStoreUpdate \
    PackageInstallerHIS \
    FamilyMonitor \
    EyesightService \
    gesturecontrol \
    NetworkWizard \
    QQforTV \
    TvMall \
    MultiScreenInteraction_TV \
    MobileOnDemand \
    ShareScreenshot \
    VoiceNews \
    VersionUpdate \
    FollowMe \
    TCLcloud \
    TCLnetplayer \
    TCLnetplayerservice \
    SystemSearch \
    TVOperationService \
    wpa_supplicant.conf \
    TCL_CyberUI \
    TCL_PackageInstallerRenew \
    Weather  \
    lexue3.0 \
    ATET_Game \
    ATET_MARKET \
    AdService \
    pay3.0 \
    XiriIME \
    AngleKaraoke \
    WeiXin \
    VOD_Main
 #   VoolePlay \
 #   ReconovaService \
 #   TCL_common_CloudShare \
 #   CloudSearch \
 #   HuanEduStandard \
 #   GameCenter \
 #   RiptideGP \
 #   GoogleMap \
 #   OnlineKSong_FullVersion \
 #   OnlineKSong_ServiceVersion \
 #   ChinaTvPay \
 #   ChinaTvPayPlugin \
 #   VIPRegister \
 #   VOAEnglish \
 #   WebAlbum \
 #   PCMRecorder \
 #   TVChannel \
 #   VAF \
 #   VoiceManager_MT36 \
 #   AndroidDownload \
 #   WeatherForecast \
 #   FavoriteChannel \
 #   TCL_heartRate \
 #   dianduEduEnter \       //diandu 1
 #   DotpanVideoPlay \
 #   DisneyGamePark \
 #   OIDEdubook_TPS \
 #   OIDRemote \           //diandu 5
 #   qqliveTV \            //tengxun_JJ
 
PRODUCT_PACKAGES += \
    icu.dat

PRODUCT_PACKAGES += \
    librs_jni \
    libvideoeditor_jni \
    libvideoeditorplayer \
    libvideoeditor_core

PRODUCT_PACKAGES += \
    audio.primary.default \
    audio_policy.default

PRODUCT_COPY_FILES := \
        system/bluetooth/data/audio.conf:system/etc/bluetooth/audio.conf \
        system/bluetooth/data/auto_pairing.conf:system/etc/bluetooth/auto_pairing.conf \
        system/bluetooth/data/blacklist.conf:system/etc/bluetooth/blacklist.conf \
        system/bluetooth/data/input.conf:system/etc/bluetooth/input.conf \
        system/bluetooth/data/network.conf:system/etc/bluetooth/network.conf \
        frameworks/base/media/libeffects/data/audio_effects.conf:system/etc/audio_effects.conf \
        packages/apps/application_platform/VoiceNews/libAisound.so:system/lib/libAisound.so \
        packages/apps/application_platform/VoiceNews/Resource.irf:system/etc/Resource.irf \
        packages/apps/application_platform/CommonMediaPlayer/libforcetv.so:system/lib/libforcetv.so \
        packages/apps/application_platform/TclBrowser/libsim-mouse.so:system/lib/libsim-mouse.so \
        packages/apps/application_platform/MultiScreenInteraction/libNDK.so:system/lib/libNDK.so \
        packages/apps/application_platform/FaceDetect/haarstage.face.data:system/lib/haarstage.face.data \
        packages/apps/application_platform/FaceDetect/libface_detect.so:system/lib/libface_detect.so \
        packages/apps/application_platform/FaceDetect/libfaceDetection.so:system/lib/libfaceDetection.so \
        packages/apps/application_platform/MobileOnDemand/libepgNDK.so:system/lib/libepgNDK.so \
	packages/apps/application_platform/gesturecontrol/libEyeSightCore-jni.so:system/lib/libEyeSightCore-jni.so \
	packages/apps/application_platform/gesturecontrol/libinject.so:system/lib/libinject.so \
	packages/apps/application_platform/gesturecontrol/libProcessJni.so:system/lib/libProcessJni.so \
	packages/apps/application_platform/gesturecontrol/libsystemChecker.so:system/lib/libsystemChecker.so \
	packages/apps/application_platform/ShareScreenshot/libmsc.so:system/lib/libmsc.so \
	packages/apps/application_platform/FamilyMonitor/libstlport_shared.so:system/lib/libstlport_shared.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_Contact.so:system/lib/libvsir_Contact.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_ctalk.so:system/lib/libvsir_ctalk.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_ET_TaskManage.so:system/lib/libvsir_ET_TaskManage.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_glview.so:system/lib/libvsir_glview.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_jni.so:system/lib/libvsir_jni.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_login.so:system/lib/libvsir_login.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_opencore-amr.so:system/lib/libvsir_opencore-amr.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_p2papi_jni.so:system/lib/libvsir_p2papi_jni.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_reg.so:system/lib/libvsir_reg.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_TcpManage.so:system/lib/libvsir_TcpManage.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_TcpMngUsers.so:system/lib/libvsir_TcpMngUsers.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_timer.so:system/lib/libvsir_timer.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_UdpManage.so:system/lib/libvsir_UdpManage.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_vcc.so:system/lib/libvsir_vcc.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_videotest.so:system/lib/libvsir_videotest.so \
	packages/apps/application_platform/FamilyMonitor/libvsir_watch.so:system/lib/libvsir_watch.so \
	packages/apps/application_platform/FamilyCloud/libKeyAndMouse.so:system/lib/libKeyAndMouse.so \
	packages/apps/application_platform/QQForTV/libcameradev2.2.so:system/lib/libcameradev2.2.so \
	packages/apps/application_platform/QQForTV/libcameradev2.3.so:system/lib/libcameradev2.3.so \
	packages/apps/application_platform/QQForTV/libcameradev4.0.so:system/lib/libcameradev4.0.so \
	packages/apps/application_platform/QQForTV/libFaceVerify.so:system/lib/libFaceVerify.so \
	packages/apps/application_platform/QQForTV/libqq_engine.so:system/lib/libqq_engine.so \
	packages/apps/application_platform/QQForTV/libqq_jni.so:system/lib/libqq_jni.so \
	packages/apps/application_platform/QQForTV/libVP8Dec.plugin.so:system/lib/libVP8Dec.plugin.so \
	packages/apps/application_platform/QQForTV/libVP8Enc.plugin.so:system/lib/libVP8Enc.plugin.so \
        packages/apps/application_platform/CyberUI/black_list.json:system/etc/black_list.json \
        packages/apps/application_platform/CyberUI/cyberui_config.json:system/etc/cyberui_config.json \
        packages/apps/application_platform/lexue3.0/libarm.so:system/lib/libarm.so \
        packages/apps/application_platform/lexue3.0/libvinit.so:system/lib/libvinit.so \
        packages/apps/application_platform/ATET/lib_All_ATET_Ime.so:system/lib/lib_All_ATET_Ime.so \
        packages/apps/application_platform/XiriIME/libsmartaiwrite-jni-v5.so:system/lib/libsmartaiwrite-jni-v5.so \
        packages/apps/application_platform/XiriIME/libvitvimemsc.so:system/lib/libvitvimemsc.so \
        packages/apps/application_platform/AngleKaraoke/lib/libAudiocnAudioUtil.so:system/lib/libAudiocnAudioUtil.so \
        packages/apps/application_platform/AngleKaraoke/lib/libAudiocnkaraoke.so:system/lib/libAudiocnkaraoke.so \
        packages/apps/application_platform/AngleKaraoke/lib/libAudiocnloop.so:system/lib/libAudiocnloop.so \
        packages/apps/application_platform/AngleKaraoke/lib/libAudiocnMP4codec.so:system/lib/libAudiocnMP4codec.so \
        packages/apps/application_platform/AngleKaraoke/lib/libAudiocnReverb.so:system/lib/libAudiocnReverb.so \
        packages/apps/application_platform/AngleKaraoke/lib/libAudiocnSoundtouch.so:system/lib/libAudiocnSoundtouch.so \
        packages/apps/application_platform/AngleKaraoke/lib/libAudioresample.so:system/lib/libAudioresample.so \
        packages/apps/application_platform/AngleKaraoke/lib/libKRoom.so:system/lib/libKRoom.so \
        packages/apps/application_platform/AngleKaraoke/lib/libKRoomJni.so:system/lib/libKRoomJni.so \
        packages/apps/application_platform/AngleKaraoke/lib/libResample.so:system/lib/libResample.so \
        packages/apps/application_platform/MessageBox/libBaiduMapSDK_v3_2_0_11.so:system/lib/libBaiduMapSDK_v3_2_0_11.so \
        packages/apps/application_platform/MessageBox/liblocSDK5.so:system/lib/liblocSDK5.so \
        packages/apps/application_platform/AppStore3.0/AppMarket3.3/blacklist_TCL-CN-MT36K-E5690A-3DG.xml:system/etc/appinfo/blacklist_TCL-CN-MT36K-E5690A-3DG.xml \
        packages/apps/application_platform/VOD/libtcl_tv-ticket-tool.so:system/lib/libtcl_tv-ticket-tool.so \

$(call inherit-product-if-exists, frameworks/base/data/fonts/fonts.mk)
$(call inherit-product-if-exists, external/lohit-fonts/fonts.mk)
$(call inherit-product-if-exists, frameworks/base/data/keyboards/keyboards.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/core.mk)
#packages/apps/application_platform/UI5.0/libUI5.so:system/lib/libUI5.so \
#packages/apps/application_platform/UI5.0/libUI5Data.so:system/lib/libUI5Data.so \
#packages/apps/application_platform/VoolePlay/libvooleglib.so:system/lib/libvooleglib.so \
#packages/apps/application_platform/CloudShare/libFaceProc_TCL_noNeon.so:system/lib/libFaceProc_TCL_noNeon.so \
#packages/apps/application_platform/CloudShare/libImageUtil_TCL_noNeon.so:system/lib/libImageUtil_TCL_noNeon.so \
#packages/apps/application_platform/CloudShare/libRecoMemoryFile.so:system/lib/libRecoMemoryFile.so \
#packages/apps/application_platform/FamilyGameCenter/GameCenter/gamecenterinfo.db:system/gamecenter/gamecenterinfo.db \
#packages/apps/application_platform/FamilyGameCenter/RiptideGp/libBlue.so:system/lib/libBlue.so \
#packages/apps/application_platform/FamilyGameCenter/RiptideGp/libfmodevent.so:system/lib/libfmodevent.so \
#packages/apps/application_platform/FamilyGameCenter/RiptideGp/libfmodex.so:system/lib/libfmodex.so \
#packages/apps/application_platform/GoogleMap/com.google.android.maps.xml:system/etc/permissions/com.google.android.maps.xml \
#packages/apps/application_platform/GoogleMap/com.google.android.maps.jar:system/framework/com.google.android.maps.jar \
#packages/apps/application_platform/TvPayment/ChinaTvPay/chinatvpay_config.bin:system/lib/chinatvpay_config.bin \
#packages/apps/application_platform/TvPayment/ChinaTvPay/libchinatvpay_jni.so:system/lib/libchinatvpay_jni.so \
#packages/apps/application_platform/TvPayment/ChinaTvPay/libtvpay-first.so:system/lib/libtvpay-first.so \
#packages/apps/application_platform/TvPayment/ChinaTvPayPlugin/libctvpayplugin.so:system/lib/libctvpayplugin.so \
#packages/apps/application_platform/TvPayment/ChinaTvPayPlugin/huanwang/html1:system/lib/huanwang/html1 \
#packages/apps/application_platform/TvPayment/ChinaTvPayPlugin/huanwang/html2:system/lib/huanwang/html2 \
#packages/apps/application_platform/TvPayment/ChinaTvPayPlugin/huanwang/html3:system/lib/huanwang/html3 \
#packages/apps/application_platform/TvPayment/ChinaTvPayPlugin/huanwang/html4:system/lib/huanwang/html4 \
#packages/apps/application_platform/TvPayment/ChinaTvPayPlugin/huanwang/html5:system/lib/huanwang/html5 \
#packages/apps/application_platform/TvPayment/ChinaTvPayPlugin/huanwang/html6:system/lib/huanwang/html6 \
#packages/apps/application_platform/WebAlbum/haarstage.data:system/lib/haarstage.data \
#packages/apps/application_platform/WebAlbum/libgesture.so:system/lib/libgesture.so \
#packages/apps/application_platform/VoiceAssist/libvitvAisound.so:system/lib/libvitvAisound.so \
#packages/apps/application_platform/VoiceAssist/libvitvAitalk4.so:system/lib/libvitvAitalk4.so \
#packages/apps/application_platform/VoiceAssist/libvitvmsc.so:system/lib/libvitvmsc.so \
#packages/apps/application_platform/VoiceAssist/libvitvvad.so:system/lib/libvitvvad.so \
#packages/apps/application_platform/VoiceAssist/libvmsim-key.so:system/lib/libvmsim-key.so \
#packages/apps/application_platform/AppStore3.0/libturn_rotate.so:system/lib/libturn_rotate.so \
#packages/apps/application_platform/AppStore3.0/libturn_rotate_data.so:system/lib/libturn_rotate_data.so \
#packages/apps/application_platform/TCL_heartRate/haar_alt2:system/lib/haar_alt2 \
#packages/apps/application_platform/TCL_heartRate/libheartrate.so:system/lib/libheartrate.so \
#packages/apps/application_platform/CloudOnDemandSys/apManage_rw.xml:system/etc/apManage_rw.xml \       //diandu 1
#packages/apps/application_platform/CloudOnDemandSys/bookid.xml:system/etc/bookid.xml \
#packages/apps/application_platform/CloudOnDemandSys/channelSwitchXml.xml:system/etc/channelSwitchXml.xml \
#packages/apps/application_platform/CloudOnDemandSys/libJNI_FileSys.so:system/lib/libJNI_FileSys.so \
#packages/apps/application_platform/CloudOnDemandSys/chompjimei_tv:system/bin/chompjimei_tv \           //diandu 5
#packages/apps/application_platform/qqliveTV/lib/libckeygenerator.so:system/lib/libckeygenerator.so \    //del tengxun_JJ
#packages/apps/application_platform/qqliveTV/lib/libDES.so:system/lib/libDES.so \
#packages/apps/application_platform/qqliveTV/lib/libgif.so:system/lib/libgif.so \
#packages/apps/application_platform/qqliveTV/lib/libHWDec9.so:libHWDec9.so \
#packages/apps/application_platform/qqliveTV/lib/libHWDec14.so:system/lib/libHWDec14.so \
#packages/apps/application_platform/qqliveTV/lib/libHWDec16.so:system/lib/libHWDec16.so \
#packages/apps/application_platform/qqliveTV/lib/libHWDec17.so:system/lib/libHWDec17.so \
#packages/apps/application_platform/qqliveTV/lib/libMtaNativeCrash.so:system/lib/libMtaNativeCrash.so \
#packages/apps/application_platform/qqliveTV/lib/libMultiScreenServer.so:system/lib/libMultiScreenServer.so \
#packages/apps/application_platform/qqliveTV/lib/libNativeRQD.so:system/lib/libNativeRQD.so \
#packages/apps/application_platform/qqliveTV/lib/libottlogin.so:system/lib/libottlogin.so \
#packages/apps/application_platform/qqliveTV/lib/libpilog.so:system/lib/libpilog.so \
#packages/apps/application_platform/qqliveTV/lib/libPlayerCore_neon.so:system/lib/libPlayerCore_neon.so \
#packages/apps/application_platform/qqliveTV/lib/libqqlivetv.so:system/lib/libqqlivetv.so \
#packages/apps/application_platform/qqliveTV/lib/libQQMMANDKSignature.so:system/lib/libQQMMANDKSignature.so \
#packages/apps/application_platform/qqliveTV/lib/libsta_jni.so:system/lib/libsta_jni.so \
#packages/apps/application_platform/qqliveTV/lib/libtvaccount.so:system/lib/libtvaccount.so \
#packages/apps/application_platform/qqliveTV/lib/libtvsubp.so:system/lib/libtvsubp.so \
#packages/apps/application_platform/qqliveTV/lib/libtv-ticket-tool.so:system/lib/libtv-ticket-tool.so \
#packages/apps/application_platform/qqliveTV/lib/libTxCodec_neon.so:system/lib/libTxCodec_neon.so \

# Overrides
PRODUCT_BRAND := generic
PRODUCT_DEVICE := generic
PRODUCT_NAME := generic_no_telephony
