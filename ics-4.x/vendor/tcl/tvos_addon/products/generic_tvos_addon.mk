

#Samba
PRODUCT_PACKAGES += \
	libSndCmd \
	libsndcmd_jni \
	libBpSndCmd \
	sndcmdservice_tcl

# snapshot
PRODUCT_PACKAGES += \
	libcapture_jni

#WiFi
#PRODUCT_PACKAGES += \
#	libwifimultsupport_jni


# TCLWidget
PRODUCT_PACKAGES += \
	android.tclwidget \
	android.tclwidget.xml \

# TVOS All java Library	
PRODUCT_PACKAGES += \
	com.tcl.tvos.addon \
	com.tcl.tvos.addon.xml \
	com.tcl.adsystem


#add by tank@tcl.com
PRODUCT_PACKAGES += \
	tcl_sim_kb.ko \
	libinputproc.so \
	sinput \
	libinputproc_jni.so \
	libsim-mouse.so 


#PRODUCT_COPY_FILES
TVOS_BLD_DEVICE_DIR := vendor/tcl/tvos_addon/device/tcl
TVOS_BLD_EXTERNAL_DIR := vendor/tcl/tvos_addon/external


PRODUCT_COPY_FILES += \
	$(TVOS_BLD_EXTERNAL_DIR)/tclsimdr/input/Vendor_0019_Product_0001.kl:system/usr/keylayout/Vendor_0019_Product_0001.kl
#	$(TVOS_BLD_DEVICE_DIR)/prebuilt/bootanimation.zip:system/media/bootanimation.zip


