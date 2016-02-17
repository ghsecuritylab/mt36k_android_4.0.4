ifeq "$(ANDROID_ICS_BROWSER_CACHE_POLICY)" "true"
PRODUCT_PROPERTY_OVERRIDES += ro.mtk.browser.cachePolicy=1
endif

ifeq "$(ANDROID_ICS_SWITCH_FBM)" "true"
PRODUCT_PROPERTY_OVERRIDES += ro.mtk.system.switchfbm=1
endif

ifeq "$(ANDROID_ICS_PNG_SW_DECODE)" "true"
# for PNG HW decode, default threshold is 1024k, set to 9999999 for SW decode
PRODUCT_PROPERTY_OVERRIDES += png.hw.threshold=9999999
endif

ifeq "$(ANDROID_ICS_JPG_SW_DECODE)" "true"
# for JPG HW decode, default threshold is ??k, set to 9999999 for SW decode
PRODUCT_PROPERTY_OVERRIDES += jpg.hw.threshold=9999999
endif


PRODUCT_PROPERTY_OVERRIDES += mtk.browser.useCmpbPlayer=1
PRODUCT_PROPERTY_OVERRIDES += mtk.browser.alwaysFullscreen=1
PRODUCT_PROPERTY_OVERRIDES += mtk.browser.openLinkInSameTab=1
PRODUCT_PROPERTY_OVERRIDES += hwui.render_dirty_regions=false
PRODUCT_PROPERTY_OVERRIDES += mtk.browser.supportAutoplay=1