/* 
 * Berkeley Lab Checkpoint/Restart (BLCR) for Linux is Copyright (c)
 * 2007, The Regents of the University of California, through Lawrence
 * Berkeley National Laboratory (subject to receipt of any required
 * approvals from the U.S. Dept. of Energy).  All rights reserved.
 *
 * Portions may be copyrighted by others, as may be noted in specific
 * copyright notices within specific files.
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: //DTV/MP_BR/DTV_X_IDTV0801_002158_10_001_158_001/android/ics-4.x/blcr-0.8.4/include/blcr_vmadump.h#1 $
 *
 * This file provides declarations for BLCR-specific VMADump extensions.
 */
#ifndef _BLCR_VMADUMP_H
#define _BLCR_VMADUMP_H
 
#ifndef EXPORT_SYMBOL_GPL
  #define EXPORT_SYMBOL_GPL EXPORT_SYMBOL
#endif

/* Not maintaining the binfmt code */
#undef HAVE_BINFMT_VMADUMP

/* Flags for dump/undump */
#define VMAD_DUMP_NOSHANON    0x0100  /* let BLCR dump shared anonymous mappings */
#define VMAD_DUMP_NOEXEC      0x0200  /* let BLCR dump the executable */
#define VMAD_DUMP_NOPRIVATE   0x0400  /* let BLCR dump private filenamed memory */
#define VMAD_DUMP_NOSHARED    0x0800  /* let BLCR dump shared filenamed memory */

/* Additional flags */
#define VMAD_DUMP_REGSONLY 0x1000	/* Only thread-specific info */

/* Check for mis-match */
#if defined(CR_CHKPT_DUMP_EXEC) && (CR_CHKPT_DUMP_EXEC != VMAD_DUMP_NOEXEC)
  #error "Mismatch CR_CHKPT_DUMP_EXEC vs. VMAD_DUMP_EXEC"
#endif
#if defined(CR_CHKPT_DUMP_PRIVATE) && (CR_CHKPT_DUMP_PRIVATE != VMAD_DUMP_NOPRIVATE)
  #error "Mismatch CR_CHKPT_DUMP_PRIVATE vs. VMAD_DUMP_PRIVATE"
#endif
#if defined(CR_CHKPT_DUMP_SHARED) && (CR_CHKPT_DUMP_SHARED != VMAD_DUMP_NOSHARED)
  #error "Mismatch CR_CHKPT_DUMP_SHARED vs. VMAD_DUMP_SHARED"
#endif

#ifdef __KERNEL__
#include "blcr_imports.h"

/* Overload the namelen flag to store ARCH-specific mappings */
#define VMAD_NAMELEN_ARCH (PAGE_SIZE+1)

#if defined(ARCH_HAS_SETUP_ADDITIONAL_PAGES)
  #define VMAD_HAVE_ARCH_MAPS 1
#else
  #define VMAD_HAVE_ARCH_MAPS 0
#endif

#if VMAD_HAVE_ARCH_MAPS
  extern int vmad_is_arch_map(const struct vm_area_struct *map);
#else
  #define vmad_is_arch_map(map)	(0)
#endif

/* Here because vmadump and BLCR must agree */
static inline int vmad_dentry_unlinked(struct dentry *dentry) {
  return ((!IS_ROOT(dentry) && d_unhashed(dentry)) ||
	  (dentry->d_inode->i_nlink == 0) ||
	  (dentry->d_flags & DCACHE_NFSFS_RENAMED));
}

/* Here because vmadump and BLCR must agree */
static inline int vmad_is_special_mmap(struct vm_area_struct *map, int flags) {
  struct file *filp = map->vm_file;
  unsigned long vm_flags = map->vm_flags;

  BUG_ON(!filp);
				    
  if (vmad_is_arch_map(map)) return 0;

#ifdef CONFIG_HUGETLBFS
  /* Ignore unlinked status, since hugetlbfs is not persistent */
  if (is_vm_hugetlb_page(map)) return 1;
#endif

  if (vmad_dentry_unlinked(filp->f_dentry)) {
    return (vm_flags & VM_SHARED);
  }

  return (((flags & VMAD_DUMP_NOEXEC)    &&  (vm_flags & VM_EXECUTABLE)) ||
	  ((flags & VMAD_DUMP_NOPRIVATE) && !(vm_flags & VM_SHARED)) ||
	  ((flags & VMAD_DUMP_NOSHARED)  &&  (vm_flags & VM_SHARED)));
}

#endif /* defined(__KERNEL__) */

#endif
