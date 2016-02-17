/*
 * This confidential and proprietary software may be used only as
 * authorised by a licensing agreement from ARM Limited
 * (C) COPYRIGHT 2006-2011 ARM Limited
 * ALL RIGHTS RESERVED
 * The entire notice above must be reproduced on all authorised
 * copies and copies may only be made to the extent permitted
 * by a licensing agreement from ARM Limited.
 */
#ifndef MALI_RSW_CACHE_H
#define MALI_RSW_CACHE_H

#include <mali_system.h>
#include <base/mali_memory.h>

/* Mali200 rsw */
#include <mali_rsw.h>
typedef m200_rsw mali_rsw;

/* structure wrapping up temp-values for rsw-packing */
typedef struct mali_rsw_cache
{
	mali_mem_handle list;                 /* list to cache insertions to */
	mali_mem_handle current_list;         /*  */
	u32             current_index;        /* current rsw-index */
	mali_rsw       *cache;                /* allocated as an array of rsw_cache_size */
	s32            *cache_idx;            /* */
	u16             cache_size;           /* use rsw_cache_size-1 as bitmask for current-pointer (uh, then rsw_cache_size must be a power of two...) */
	u16             cache_current;        /* index of current working rsw in rsw_cache */
	u16             max_rsw_index_count;  /* Number of RSWs available in the current_list array (128 or 256, depending on tile list format for Mali-400 MP */
} mali_rsw_cache;

/*mali_err_code __mali_rsw_cache_init(mali_rsw_cache *rsw_cache, mali_list *list, s32 cache_size) MALI_CHECK_RESULT;*/
MALI_IMPORT mali_err_code __mali_rsw_cache_init(mali_rsw_cache *rsw_cache, s32 cache_size) MALI_CHECK_RESULT;
MALI_IMPORT void __mali_rsw_cache_deinit(mali_rsw_cache *rsw_cache);

/* flush the cache */
MALI_IMPORT void __mali_rsw_cache_flush(mali_rsw_cache *cache);

/* reset the cache */
MALI_IMPORT void __mali_rsw_cache_reset(mali_rsw_cache *rsw_cache);

/* Commit rsw with caching. Returns -1 if out of RSWs. New rsw base needs to be set, and the cache must be flushed. */
MALI_IMPORT s32 __mali_rsw_cache_commit( mali_rsw_cache *cache, mali_rsw *rsws, s32 rsw_count);

/* Insert rsw directly without caching. Returns -1 if out of RSWs. New rsw base needs to be set, and the cache must be flushed. */
MALI_IMPORT s32 __mali_rsw_cache_insert( mali_rsw_cache *cache, mali_rsw *rsws, s32 rsw_count);

#endif /* MALI_RSW_CACHE_H */
