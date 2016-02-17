
PWD        := $(shell pwd)
LINUX_ROOT := $(word 1, $(subst /vm_linux/,/vm_linux /, $(PWD)))
SYSTEMMAP  := $(LINUX_ROOT)/output/$(CUSTOMER)/$(MODEL_NAME)/$(BUILD_CFG)/obj/kernel/chiling/kernel/$(KERNEL_VER)/$(KERNEL_CONFIG)/System.map
IMPORTS    := $(LINUX_ROOT)/android/$(ANDROID_VERSION)/blcr-0.8.4/cr_module/imports.c

ifndef my_local_flags
export my_local_flags = -DHAVE_FTB=0 -DCRI_DEBUG=0 -DCR_KERNEL_TRACING=0 -DCR_RESTORE_IDS=0 -DLIBCR_TRACING=0 -DCR_STACK_GROWTH=-1
endif

all:
	@echo ""                      >  $(IMPORTS)
	@echo "#ifndef EXPORT_SYMTAB" >> $(IMPORTS)
	@echo "#define EXPORT_SYMTAB" >> $(IMPORTS)
	@echo "#endif"                >> $(IMPORTS)
	@echo ""                      >> $(IMPORTS)
	@echo ""                      >> $(IMPORTS)
	@echo "#include \"blcr_imports.h\"" >> $(IMPORTS)
	@echo "#include \"blcr_ksyms.h\""   >> $(IMPORTS)
	@echo ""                      >> $(IMPORTS)
	@echo ""                      >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(__kuser_helper_start, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w __kuser_helper_start))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(__kuser_cmpxchg, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w __kuser_cmpxchg))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(vectors_user_mapping, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w vectors_user_mapping))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(arch_pick_mmap_layout, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w arch_pick_mmap_layout))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(arch_unmap_area, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w arch_unmap_area))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(copy_siginfo_to_user, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w copy_siginfo_to_user))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(group_send_sig_info, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w group_send_sig_info))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(do_sigaltstack, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w do_sigaltstack))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(get_dumpable, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w get_dumpable))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KDATA(suid_dumpable, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w suid_dumpable))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(set_dumpable, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w set_dumpable))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(groups_search, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w groups_search))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(detach_pid, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w detach_pid))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(attach_pid, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w attach_pid))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(change_pid, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w change_pid))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(find_task_by_pid_ns, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w find_task_by_pid_ns))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(free_pid, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w free_pid))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KDATA(pid_hash, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w pid_hash))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KDATA(pidhash_shift, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w pidhash_shift))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KDATA(pidmap_lock, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w pidmap_lock))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KDATA(anon_pipe_buf_ops, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w anon_pipe_buf_ops))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(pipe_fcntl, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w pipe_fcntl))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KDATA(tasklist_lock, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w tasklist_lock))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KDATA(shmem_file_operations, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w shmem_file_operations))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KDATA(ramfs_file_operations, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w ramfs_file_operations))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(do_pipe_flags, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w do_pipe_flags))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(sys_munmap, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w sys_munmap))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(sys_dup2, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w sys_dup2))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(sys_lseek, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w sys_lseek))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(sys_ftruncate, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w sys_ftruncate))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(sys_mprotect, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w sys_mprotect))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(sys_setitimer, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w sys_setitimer))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(sys_prctl, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w sys_prctl))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(copy_fs_struct, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w copy_fs_struct))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(free_fs_struct, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w free_fs_struct))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(set_fs_pwd, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w set_fs_pwd))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(sys_mremap, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w sys_mremap))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(do_sigaction, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w do_sigaction))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(expand_files, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w expand_files))))" >> $(IMPORTS)
	@echo "_CR_IMPORT_KCODE(sys_link, 0x$(firstword $(shell (cat $(SYSTEMMAP) | grep -w sys_link))))" >> $(IMPORTS)
	@echo "" >> $(IMPORTS)
	@echo "" >> $(IMPORTS)
	@echo "const char *blcr_config_timestamp = BLCR_CONFIG_TIMESTAMP;" >> $(IMPORTS)
	@echo "EXPORT_SYMBOL_GPL(blcr_config_timestamp);" >> $(IMPORTS)
	@echo "" >> $(IMPORTS)
	@mkdir -p $(LINUX_ROOT)/android/$(ANDROID_VERSION)/blcr-0.8.4/include/linux
	@ln -sf $(LINUX_ROOT)/chiling/kernel/$(KERNEL_VER)/include/linux/kmalloc_sizes.h $(LINUX_ROOT)/android/$(ANDROID_VERSION)/blcr-0.8.4/include/linux/
	make -C cr_module

clean:
	make -C cr_module clean
	@rm $(IMPORTS)
