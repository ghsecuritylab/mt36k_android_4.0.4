# $(1): src directory
# $(2): output file
# $(3): label (if any)
# $(4): if true, add journal
#
# $(1): root directory
# $(2): system directory
# $(3): output file
UBINIZE_FLAGS=-m 2048 -p 128KiB -s 2048

512M_FLASH_SUPPORT=false
1024M_FLASH_SUPPORT=true

ifeq "true" "$(512M_FLASH_SUPPORT)"
MKUBIFS_ROOTFS_FLAGS=-m 2048 -e 126976 -c 120
MKUBIFS_SYSTEM_FLAGS=-m 2048 -e 126976 -c 2447
MKUBIFS_USERDATA_FLAGS=-m 2048 -e 126976 -c 639
MKUBIFS_CACHE_FLAGS=-m 2048 -e 126976 -c 799

define build-rootfsimage-ubifs-target
	@echo "Making ubifs rootfs image"
	@ mkdir -p $(dir $(3))
	@ mkdir -p $(dir $(3))/ubi_rootfs/data
        @ $(ACP) -d -p -r $(TARGET_ROOT_OUT)/* $(dir $(3))/ubi_rootfs/data/
	@ $(ACP) external/mtd-utils/512m_rootfs.cfg $(dir $(3))
	@ $(ACP) -p $(UBINIZE) $(dir $(3))
	@ $(MKUBIFS) --devtable device/mtk/$(IC_SETTING)/configs/root_dev.txt -r $(dir $(3))/ubi_rootfs/data/ -o $(dir $(3))/ubifs.rootfs.img $(MKUBIFS_ROOTFS_FLAGS)
	@cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) 512m_rootfs.cfg
	@rm -rf $(dir $(3))/ubi_rootfs $(dir $(3))/ubifs.rootfs.img $(dir $(3))/ubinize $(dir $(3))/512m_rootfs.cfg
endef

define build-systemimage-ubifs-target
	@echo "Making ubifs system image"
	@ mkdir -p $(dir $(3))
	@ mkdir -p $(dir $(3))/ubi_root
	@ $(ACP) -d -p -r $(TARGET_OUT) $(dir $(3))/ubi_root/
	@ $(ACP) external/mtd-utils/512m_system.cfg $(dir $(3))
	@ $(ACP) -p $(UBINIZE) $(dir $(3))
	@ $(MKUBIFS) -r $(dir $(3))/ubi_root/system/ -o $(dir $(3))/ubifs.system.img $(MKUBIFS_SYSTEM_FLAGS)
	@cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) 512m_system.cfg
  @rm -rf $(dir $(3))/ubi_root $(dir $(3))/ubifs.system.img $(dir $(3))/ubinize $(dir $(3))/512m_system.cfg
endef

define build-userdataimage-ubifs-target
 @echo "Making ubifs userdata image"
 @ mkdir -p $(dir $(3))
 @ mkdir -p $(dir $(3))/data
 @ mkdir -p $(dir $(3))/data/data
 @ $(ACP) external/mtd-utils/512m_userdata.cfg $(dir $(3))
 @ $(ACP) -p $(UBINIZE) $(dir $(3))
 @ $(MKUBIFS) -r $(dir $(3))/data/data -o $(dir $(3))/ubifs.userdata.img $(MKUBIFS_USERDATA_FLAGS)
 @ cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) 512m_userdata.cfg
 @ rm -rf $(dir $(3))/data/data $(dir $(3))/ubifs.userdata.img $(dir $(3))/ubinize $(dir $(3))/512m_userdata.cfg
endef


define build-cacheimage-ubifs-target
@echo "Making ubifs cache image"
 @ mkdir -p $(dir $(3))
 @ mkdir -p $(dir $(3))/ubi_cache/data
 @ $(ACP) external/mtd-utils/512m_cache.cfg $(dir $(3))
 @ $(ACP) -p $(UBINIZE) $(dir $(3))
 @ $(MKUBIFS) -r $(dir $(3))/ubi_cache/data/ -o $(dir $(3))/ubifs.cache.img $(MKUBIFS_CACHE_FLAGS)
 @ cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) 512m_cache.cfg
 @ rm -rf $(dir $(3))/ubi_cache $(dir $(3))/cache $(dir $(3))/ubifs.cache.img $(dir $(3))/ubinize $(dir $(3))/512m_cache.cfg
endef

else
ifeq "true" "$(1024M_FLASH_SUPPORT)"
MKUBIFS_ROOTFS_FLAGS=-m 2048 -e 126976 -c 120
MKUBIFS_SYSTEM_FLAGS=-m 2048 -e 126976 -c 5255
MKUBIFS_USERDATA_FLAGS=-m 2048 -e 126976 -c 1999
MKUBIFS_CACHE_FLAGS=-m 2048 -e 126976 -c 279


define build-rootfsimage-ubifs-target
	@echo "Making ubifs rootfs image"
	@ mkdir -p $(dir $(3))
	@ mkdir -p $(dir $(3))/ubi_rootfs/data
        @ $(ACP) -d -p -r $(TARGET_ROOT_OUT)/* $(dir $(3))/ubi_rootfs/data/
	@ $(ACP) external/mtd-utils/512m_rootfs.cfg $(dir $(3))
	@ $(ACP) -p $(UBINIZE) $(dir $(3))
	@ $(MKUBIFS) --devtable device/mtk/$(IC_SETTING)/configs/root_dev.txt -r $(dir $(3))/ubi_rootfs/data/ -o $(dir $(3))/ubifs.rootfs.img $(MKUBIFS_ROOTFS_FLAGS)
	@cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) 512m_rootfs.cfg
	@rm -rf $(dir $(3))/ubi_rootfs $(dir $(3))/ubifs.rootfs.img $(dir $(3))/ubinize $(dir $(3))/512m_rootfs.cfg
endef

define build-systemimage-ubifs-target
	@echo "Making ubifs system image"
	@ mkdir -p $(dir $(3))
	@ mkdir -p $(dir $(3))/ubi_root
	@ $(ACP) -d -p -r $(TARGET_OUT) $(dir $(3))/ubi_root/
	@ $(ACP) external/mtd-utils/512m_system.cfg $(dir $(3))
	@ $(ACP) -p $(UBINIZE) $(dir $(3))
	@ $(MKUBIFS) -r $(dir $(3))/ubi_root/system/ -o $(dir $(3))/ubifs.system.img $(MKUBIFS_SYSTEM_FLAGS)
	@cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) 512m_system.cfg
  @rm -rf $(dir $(3))/ubi_root $(dir $(3))/ubifs.system.img $(dir $(3))/ubinize $(dir $(3))/512m_system.cfg
endef

define build-userdataimage-ubifs-target
 @echo "Making ubifs userdata image"
 @ mkdir -p $(dir $(3))
 @ mkdir -p $(dir $(3))/data
 @ mkdir -p $(dir $(3))/data/data
 @ $(ACP) external/mtd-utils/512m_userdata.cfg $(dir $(3))
 @ $(ACP) -p $(UBINIZE) $(dir $(3))
 @ $(MKUBIFS) -r $(dir $(3))/data/data -o $(dir $(3))/ubifs.userdata.img $(MKUBIFS_USERDATA_FLAGS)
 @ cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) 512m_userdata.cfg
 @ rm -rf $(dir $(3))/data/data $(dir $(3))/ubifs.userdata.img $(dir $(3))/ubinize $(dir $(3))/512m_userdata.cfg
endef


define build-cacheimage-ubifs-target
@echo "Making ubifs cache image"
 @ mkdir -p $(dir $(3))
 @ mkdir -p $(dir $(3))/ubi_cache/data
 @ $(ACP) external/mtd-utils/512m_cache.cfg $(dir $(3))
 @ $(ACP) -p $(UBINIZE) $(dir $(3))
 @ $(MKUBIFS) -r $(dir $(3))/ubi_cache/data/ -o $(dir $(3))/ubifs.cache.img $(MKUBIFS_CACHE_FLAGS)
 @ cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) 512m_cache.cfg
 @ rm -rf $(dir $(3))/ubi_cache $(dir $(3))/cache $(dir $(3))/ubifs.cache.img $(dir $(3))/ubinize $(dir $(3))/512m_cache.cfg
endef


else
MKUBIFS_ROOTFS_FLAGS=-m 2048 -e 126976 -c 120
MKUBIFS_SYSTEM_FLAGS=-m 2048 -e 126976 -c 1487
MKUBIFS_USERDATA_FLAGS=-m 2048 -e 126976 -c 799
MKUBIFS_CACHE_FLAGS=-m 2048 -e 126976 -c 159


define build-rootfsimage-ubifs-target
	@echo "Making ubifs rootfs image"
	@ mkdir -p $(dir $(3))
	@ mkdir -p $(dir $(3))/ubi_rootfs
        @ $(ACP) -d -p -r $(TARGET_ROOT_OUT)/* $(dir $(3))/ubi_rootfs/
	@ $(ACP) external/mtd-utils/rootfs.cfg $(dir $(3))
	@ $(ACP) -p $(UBINIZE) $(dir $(3))
	@ $(MKUBIFS) --devtable device/mtk/$(IC_SETTING)/configs/root_dev.txt -r $(dir $(3))/ubi_rootfs/ -o $(dir $(3))/ubifs.rootfs.img $(MKUBIFS_ROOTFS_FLAGS)
	@cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) rootfs.cfg
	@rm -rf $(dir $(3))/ubi_rootfs $(dir $(3))/ubifs.rootfs.img $(dir $(3))/ubinize $(dir $(3))/rootfs.cfg
endef

define build-systemimage-ubifs-target
	@echo "Making ubifs system image"
	@ mkdir -p $(dir $(3))
	@ mkdir -p $(dir $(3))/ubi_root
	@ $(ACP) -d -p -r $(TARGET_OUT) $(dir $(3))/ubi_root/
	@ $(ACP) external/mtd-utils/system.cfg $(dir $(3))
	@ $(ACP) -p $(UBINIZE) $(dir $(3))
	@ $(MKUBIFS) -r $(dir $(3))/ubi_root/system/ -o $(dir $(3))/ubifs.system.img $(MKUBIFS_SYSTEM_FLAGS)
	@cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) system.cfg
  @rm -rf $(dir $(3))/ubi_root $(dir $(3))/ubifs.system.img $(dir $(3))/ubinize $(dir $(3))/system.cfg
endef

define build-userdataimage-ubifs-target
 @echo "Making ubifs userdata image"
 @ mkdir -p $(dir $(3))
 @ mkdir -p $(dir $(3))/data
 @ mkdir -p $(dir $(3))/data/data
 @ $(ACP) external/mtd-utils/userdata.cfg $(dir $(3))
 @ $(ACP) -p $(UBINIZE) $(dir $(3))
 @ $(MKUBIFS) -r $(dir $(3))/data/data -o $(dir $(3))/ubifs.userdata.img $(MKUBIFS_USERDATA_FLAGS)
 @ cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) userdata.cfg
 @ rm -rf $(dir $(3))/data/data $(dir $(3))/ubifs.userdata.img $(dir $(3))/ubinize $(dir $(3))/userdata.cfg
endef


define build-cacheimage-ubifs-target
@echo "Making ubifs cache image"
 @ mkdir -p $(dir $(3))
 @ mkdir -p $(dir $(3))/cache
 @ $(ACP) external/mtd-utils/cache.cfg $(dir $(3))
 @ $(ACP) -p $(UBINIZE) $(dir $(3))
 @ $(MKUBIFS) -r $(dir $(3))/cache/ -o $(dir $(3))/ubifs.cache.img $(MKUBIFS_CACHE_FLAGS)
 @ cd $(dir $(3)); ./ubinize -o `basename $(3)` $(UBINIZE_FLAGS) cache.cfg
 @ rm -rf $(dir $(3))/cache $(dir $(3))/cache $(dir $(3))/ubifs.cache.img $(dir $(3))/ubinize $(dir $(3))/cache.cfg
endef

endif
endif
