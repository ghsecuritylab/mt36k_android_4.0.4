/* 
 * Berkeley Lab Checkpoint/Restart (BLCR) for Linux is Copyright (c)
 * 2007, The Regents of the University of California, through Lawrence
 * Berkeley National Laboratory (subject to receipt of any required
 * approvals from the U.S. Dept. of Energy).  All rights reserved.
 *
 * Portions may be copyrighted by others, as may be noted in specific
 * copyright notices within specific files.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * $Id: //DTV/MP_BR/DTV_X_IDTV0801_002158_10_001_158_001/android/ics-4.x/blcr-0.8.4/libcr/arch/arm/cr_atomic.h#1 $
 *
 * Experimental ARM support contributed by Anton V. Uzunov
 * <anton.uzunov@dsto.defence.gov.au> of the Australian Government
 * Department of Defence, Defence Science and Technology Organisation.
 *
 * ARM-specific questions should be directed to blcr-arm@hpcrd.lbl.gov.
 */

#ifndef _CR_ATOMIC_H
#define _CR_ATOMIC_H	1

#include "blcr_defines.h"

#ifndef _STRINGIFY
  #define _STRINGIFY_HELPER(x) #x
  #define _STRINGIFY(x) _STRINGIFY_HELPER(x)
#endif

// Define cri_atomic_t and five required operations:
//   read, write, inc, dec-and-test, compare-and-swap

typedef volatile unsigned int cri_atomic_t;

// Single-word reads are naturally atomic
CR_INLINE unsigned int
cri_atomic_read(cri_atomic_t *p)
{
    __asm__ __volatile__("": : :"memory");
    return( *p );
}

// Single-word writes are naturally atomic
CR_INLINE void
cri_atomic_write(cri_atomic_t *p, unsigned int val)
{
    *p = val;
    __asm__ __volatile__("": : :"memory");
}

// For kernel >= 2.6.12, we use __kernel_cmpxchg()
//    See linux-2.6.12/arch/arm/kernel/entry-armv.S
// For >= ARM6 we could/should be using load-exclusive directly.

// To construct constants from (8-bit immediates + shifts)
// we use a "base" that fits that constraint, and also lies a
// distance from __kuser_cmpxchg fitting that constraint.
// Specifically 0xffff0fff = ~(0xf0 << 8) = __kuser_cmpxchg + 0x3f
#define cri_kuser_cmpxchg	0xffff0fc0
#define cri_kuser_base		0xffff0fff
#define cri_kuser_offset	(cri_kuser_base - cri_kuser_cmpxchg)


CR_INLINE unsigned int
__cri_atomic_add_fetch(cri_atomic_t *p, unsigned int op)
{
    register unsigned long __sum asm("r1");
    register unsigned long __ptr asm("r2") = (unsigned long)(p);

    __asm__ __volatile__ (
	"0:	ldr	r0, [r2]	@ r0 = *p		\n"
	"	mov	r3, #" _STRINGIFY(cri_kuser_base) "	\n"
	"	adr	lr, 1f		@ lr = return address	\n"
	"	add	r1, r0, %2	@ r1 = r0 + op		\n"
	"	sub	pc, r3, #" _STRINGIFY(cri_kuser_offset) "\n"
	"1:	bcc     0b		@ retry on Carry Clear"
	: "=&r" (__sum)
	: "r" (__ptr), "rIL" (op)
	: "r0", "r3", "ip", "lr", "cc", "memory" );

    return __sum;
}

CR_INLINE void
cri_atomic_inc(cri_atomic_t *p)
{
    (void)__cri_atomic_add_fetch(p, 1);
}

// Returns non-zero if value reaches zero
CR_INLINE int
cri_atomic_dec_and_test(cri_atomic_t *p)
{
    return (__cri_atomic_add_fetch(p, -1) == 0);
}

// cri_cmp_swap()
//
// Atomic compare and exchange (swap).
// Atomic equivalent of:
//	if (*p == oldval) {
//	    *p = newval;
//	    return NONZERO;
//	} else {
//	    return 0;
//	}
//
CR_INLINE unsigned int
cri_cmp_swap(cri_atomic_t *p, unsigned int oldval, unsigned int newval)
{
    register unsigned int result asm("r0");
    register unsigned int _newval asm("r1") = newval;
    register unsigned int _p asm("r2") = (unsigned long)p;
    register unsigned int _oldval asm("r4") = oldval;

    /* Transient failure is possible if interrupted.
     * Since we can't distinguish the cause of the failure,
     * we must retry as long as the failure looks "improper"
     * which is defined as (!swapped && (*p == oldval))
     */
    __asm__ __volatile__ (
	"0:     mov     r0, r4          @ r0 = oldval           \n"
	"	mov	r3, #" _STRINGIFY(cri_kuser_base) "	\n"
	"	mov	lr, pc		@ lr = return addr	\n"
	"	sub	pc, r3, #" _STRINGIFY(cri_kuser_offset) "\n"
	"       ldrcc   ip, [r2]        @ if (!swapped) ip=*p   \n"
	"       eorcs   ip, r4, #1      @ else ip=oldval^1      \n"
	"       teq     r4, ip          @ if (ip == oldval)     \n"
	"       beq     0b              @    then retry           "
	: "=&r" (result)
	: "r" (_oldval), "r" (_p), "r" (_newval)
	: "r3", "ip", "lr", "cc", "memory" );

    return !result;
}

#endif
