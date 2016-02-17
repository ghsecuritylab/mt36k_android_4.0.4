
PWD        := $(shell pwd)
LINUX_ROOT := $(word 1, $(subst /vm_linux/,/vm_linux /, $(PWD)))


# Kernel modules
obj-m += blcr.o
blcr-objs := \
		cr_async.o	\
		cr_barrier.o	\
		cr_chkpt_req.o	\
		cr_dest_file.o	\
		cr_dump_self.o	\
		cr_fops.o	\
		cr_io.o		\
		cr_module.o	\
		cr_proc.o	\
		cr_rstrt_req.o	\
		cr_sync.o	\
		cr_task.o	\
		cr_trigger.o	\
		cr_ktrace.o	\
		cr_objects.o	\
		cr_compat.o	\
		cr_mmaps.o      \
		cr_vmadump.o    \
		cr_timers.o	\
		cr_pipes.o	\
		cr_creds.o	\
		cr_relocate.o	\
		cr_watchdog.o \
		./../vmadump4/vmadump_common.o \
		./../vmadump4/vmadump_arm.o \
		imports.o


# Specify flags for the module compilation
#EXTRA_CFLAGS += -g -O0
BPROC_VERSION	= "4.0.0pre8"
EXTRA_CFLAGS += -I$(LINUX_ROOT)/android/$(ANDROID_VERSION)/blcr-0.8.4/cr_module \
	-I$(LINUX_ROOT)/android/$(ANDROID_VERSION)/blcr-0.8.4 \
	-I$(LINUX_ROOT)/android/$(ANDROID_VERSION)/blcr-0.8.4/vmadump4 \
	-I$(LINUX_ROOT)/android/$(ANDROID_VERSION)/blcr-0.8.4/cr_module/arch/arm \
	-I$(LINUX_ROOT)/android/$(ANDROID_VERSION)/blcr-0.8.4/include \
	-I$(LINUX_ROOT)/output/$(BUILD_LIST)/$(DTV_OUTPUT)/obj/kernel/chiling/kernel/$(KERNEL_VER)/$(KERNEL_CONFIG)/include \
	-I$(LINUX_ROOT)/chiling/kernel/$(KERNEL_VER)/include \
	-D__NR_vmadump=-1 -DBPROC_VERSION='$(BPROC_VERSION)'

EXTRA_CFLAGS += -DBLCR_CONFIG_TIMESTAMP='"`date`"' $(my_local_flags)

#build for arm target
KERNEL_BUILD = $(LINUX_ROOT)/output/$(CUSTOMER)/$(MODEL_NAME)/$(BUILD_CFG)/obj/kernel/chiling/kernel/$(KERNEL_VER)/$(KERNEL_CONFIG)

build: kernel_modules

kernel_modules:
	make -C $(KERNEL_BUILD) M=$(PWD) modules

clean:
	make -C $(KERNEL_BUILD) M=$(PWD) clean
