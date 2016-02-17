

#ifndef _CR_DEFINES_H_
#define _CR_DEFINES_H_

#include "blcr_ioctl.h"
#include "blcr_common.h"
#include <linux/signal.h>
#include <linux/unistd.h>

#define PACKAGE_BUGREPORT "http://ftg.lbl.gov/checkpoint"
#define PACKAGE_STRING    "blcr 0.8.4"
#define PACKAGE_VERSION   "0.8.4"

enum __do_not_use_this_1
{
#define CACHE(x) \
    __do_not_use_cache_##x = x - 1,
#include <linux/kmalloc_sizes.h>
#undef CACHE
    __do_not_use_cache_me
};
#define CR_KMALLOC_MAX __do_not_use_cache_me

#define CR_INLINE static __inline__ __attribute__ ((__unused__))

// Note: CR_ASM_SI_PID_OFFSET = offsetof(struct siginfo, si_pid)
//#ifndef offsetof
//#define offsetof(TYPE, MEMBER) ((unsigned long) &((TYPE *)0)->MEMBER)
//#endif
//enum __do_not_use_this_2
//{
//    __do_not_use_op_hand_chkpt   = CR_OP_HAND_CHKPT,
//    __do_not_use_checkpoint_stub = _CR_CHECKPOINT_STUB,
//    __do_not_use_op_hand_abort   = CR_OP_HAND_ABORT,
//    __do_not_use_checkpoint_omit = CR_CHECKPOINT_OMIT,
//    __do_not_use_si_pid_offset   = offsetof(struct siginfo, si_pid),
//    __do_not_use_nr_ioctl        = __NR_ioctl,
//    __do_not_use_nr_rt_sigreturn = __NR_rt_sigreturn,
//};
//#define CR_ASM_OP_HAND_CHKPT     __do_not_use_op_hand_chkpt
//#define CR_ASM_CHECKPOINT_STUB   __do_not_use_checkpoint_stub
//#define CR_ASM_OP_HAND_ABORT     __do_not_use_op_hand_abort
//#define CR_ASM_CHECKPOINT_OMIT   __do_not_use_checkpoint_omit
//#define CR_ASM_SI_PID_OFFSET     __do_not_use_si_pid_offset
//#define CR_ASM_NR_ioctl          __do_not_use_nr_ioctl
//#define CR_ASM_NR_rt_sigreturn   __do_not_use_nr_rt_sigreturn

//CR_IOCTL_BASE=0xA1
//see asm-generic/ioctl.h for defination of _IOW
//CR_ASM_OP_HAND_CHKPT = _IOW (CR_IOCTL_BASE, 0x01, unsigned long) = 1 << 30 | A1 << 8 | 1 | 4 << 16 = 0x4004A101
//CR_ASM_CHECKPOINT_STUB = _CR_CHECKPOINT_STUB = 0x4000, see blcr_common.h
//CR_ASM_OP_HAND_ABORT = _IOW (CR_IOCTL_BASE, 0x02, unsigned long) = 1 << 30 | A1 << 8 | 2 | 4 << 16 = 0x4004A102
//CR_ASM_CHECKPOINT_OMIT = CR_CHECKPOINT_OMIT = 4, see blcr_common.h
//CR_ASM_SI_PID_OFFSET = offsetof(struct siginfo, si_pid) = 12
//CR_ASM_NR_ioctl = __NR_ioctl = 54
//CR_ASM_NR_rt_sigreturn = __NR_rt_sigreturn = 173
#define CR_ASM_OP_HAND_CHKPT     0x4004A101
#define CR_ASM_CHECKPOINT_STUB   0x4000
#define CR_ASM_OP_HAND_ABORT     0x4004A102
#define CR_ASM_CHECKPOINT_OMIT   4
#define CR_ASM_SI_PID_OFFSET     12
#define CR_ASM_NR_ioctl          54
#define CR_ASM_NR_rt_sigreturn   173


#endif

