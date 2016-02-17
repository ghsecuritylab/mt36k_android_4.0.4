
#ifndef EXPORT_SYMTAB
#define EXPORT_SYMTAB
#endif


#include "blcr_imports.h"
#include "blcr_ksyms.h"


_CR_IMPORT_KCODE(__kuser_helper_start, 0xc000ac60)
_CR_IMPORT_KCODE(__kuser_cmpxchg, 0xc000ac80)
_CR_IMPORT_KCODE(vectors_user_mapping, 0xc0030efc)
_CR_IMPORT_KCODE(arch_pick_mmap_layout, 0xc00b07c4)
_CR_IMPORT_KCODE(arch_unmap_area, 0xc00b9e68)
_CR_IMPORT_KCODE(copy_siginfo_to_user, 0xc005c9a0)
_CR_IMPORT_KCODE(group_send_sig_info, 0xc005bb7c)
_CR_IMPORT_KCODE(do_sigaltstack, 0xc005d38c)
_CR_IMPORT_KCODE(get_dumpable, 0xc00da5b4)
_CR_IMPORT_KDATA(suid_dumpable, 0xc0551714)
_CR_IMPORT_KCODE(set_dumpable, 0xc00da2dc)
_CR_IMPORT_KCODE(groups_search, 0xc006d824)
_CR_IMPORT_KCODE(detach_pid, 0xc0063f70)
_CR_IMPORT_KCODE(attach_pid, 0xc0063f2c)
_CR_IMPORT_KCODE(change_pid, 0xc0063fdc)
_CR_IMPORT_KCODE(find_task_by_pid_ns, 0xc0064100)
_CR_IMPORT_KCODE(free_pid, 0xc0063b44)
_CR_IMPORT_KDATA(pid_hash, 0xc0548f40)
_CR_IMPORT_KDATA(pidhash_shift, 0xc05093ac)
_CR_IMPORT_KDATA(pidmap_lock, 0xc04fa0a0)
_CR_IMPORT_KDATA(anon_pipe_buf_ops, 0xc03c4b60)
_CR_IMPORT_KCODE(pipe_fcntl, 0xc00dc568)
_CR_IMPORT_KDATA(tasklist_lock, 0xc04fa020)
_CR_IMPORT_KDATA(shmem_file_operations, 0xc03c4000)
_CR_IMPORT_KDATA(ramfs_file_operations, 0xc03cad04)
_CR_IMPORT_KCODE(do_pipe_flags, 0xc00dc3ac)
_CR_IMPORT_KCODE(sys_munmap, 0xc00bb488)
_CR_IMPORT_KCODE(sys_dup2, 0xc00e15bc)
_CR_IMPORT_KCODE(sys_lseek, 0xc00d3f4c)
_CR_IMPORT_KCODE(sys_ftruncate, 0xc00d3130)
_CR_IMPORT_KCODE(sys_mprotect, 0xc00bcf64)
_CR_IMPORT_KCODE(sys_setitimer, 0xc0052554)
_CR_IMPORT_KCODE(sys_prctl, 0xc005f8bc)
_CR_IMPORT_KCODE(copy_fs_struct, 0xc00f72a0)
_CR_IMPORT_KCODE(free_fs_struct, 0xc00f71d4)
_CR_IMPORT_KCODE(set_fs_pwd, 0xc00f6ffc)
_CR_IMPORT_KCODE(sys_mremap, 0xc00bda78)
_CR_IMPORT_KCODE(do_sigaction, 0xc005d1fc)
_CR_IMPORT_KCODE(expand_files, 0xc00eac6c)
_CR_IMPORT_KCODE(sys_link, 0xc00e0fc8)


const char *blcr_config_timestamp = BLCR_CONFIG_TIMESTAMP;
EXPORT_SYMBOL_GPL(blcr_config_timestamp);

