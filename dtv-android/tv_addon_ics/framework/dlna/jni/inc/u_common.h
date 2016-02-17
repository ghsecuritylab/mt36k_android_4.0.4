/*******************************************************************************
* Copyright (c) Koninklijke Philips Electronics N.V.
* All Rights Reserved.
*
* This source code and any compilation or derivative thereof is the
* proprietary information of Koninklijke Philips Electronics N.V.
* and is confidential in nature. Under no circumstances is this
* software to be combined with any Open Source Software in any way
* or placed under an Open Source License of any type without
* the express written permission of Koninklijke Philips Electronics N.V.
*
*############################################################*/

#ifndef _U_COMMON_H_
#define _U_COMMON_H_


/*-----------------------------------------------------------------------------
                    include files
 ----------------------------------------------------------------------------*/
#include <stdarg.h>
#include <stddef.h>
 
/*-----------------------------------------------------------------------------
                    macros, defines, typedefs, enums
 ----------------------------------------------------------------------------*/

#if !defined (_NO_TYPEDEF_UCHAR_) && !defined (_TYPEDEF_UCHAR_)
typedef unsigned char  UCHAR;

#define _TYPEDEF_UCHAR_
#endif

#if !defined (_NO_TYPEDEF_UINT8_) && !defined (_TYPEDEF_UINT8_)
typedef unsigned char  UINT8;

#define _TYPEDEF_UINT8_
#endif

#if !defined (_NO_TYPEDEF_UINT16_) && !defined (_TYPEDEF_UINT16_)
typedef unsigned short  UINT16;

#define _TYPEDEF_UINT16_
#endif

#if !defined (_NO_TYPEDEF_UINT32_) && !defined (_TYPEDEF_UINT32_)

#ifndef EXT_UINT32_TYPE
typedef unsigned long    UINT32;
#else
typedef EXT_UINT32_TYPE  UINT32;
#endif

#define _TYPEDEF_UINT32_
#endif

#if !defined (_NO_TYPEDEF_UINT64_) && !defined (_TYPEDEF_UINT64_)

#ifndef EXT_UINT64_TYPE
typedef unsigned long long  UINT64;
#else
typedef EXT_UINT64_TYPE     UINT64;
#endif

#define _TYPEDEF_UINT64_
#endif

#if !defined (_NO_TYPEDEF_CHAR_) && !defined (_TYPEDEF_CHAR_)
typedef char  CHAR;

#define _TYPEDEF_CHAR_
#endif

#if !defined (_NO_TYPEDEF_INT8_) && !defined (_TYPEDEF_INT8_)
typedef signed char  INT8;

#define _TYPEDEF_INT8_
#endif

#if !defined (_NO_TYPEDEF_INT16_) && !defined (_TYPEDEF_INT16_)
typedef signed short  INT16;

#define _TYPEDEF_INT16_
#endif

#if !defined (_NO_TYPEDEF_INT32_) && !defined (_TYPEDEF_INT32_)

#ifndef EXT_INT32_TYPE
typedef signed long     INT32;
#else
typedef EXT_INT32_TYPE  INT32;
#endif

#define _TYPEDEF_INT32_
#endif

#if !defined (_NO_TYPEDEF_INT64_) && !defined (_TYPEDEF_INT64_)

#ifndef EXT_INT64_TYPE
typedef signed long long  INT64;
#else
typedef EXT_INT64_TYPE    INT64;
#endif

#define _TYPEDEF_INT64_
#endif

#if !defined (_NO_TYPEDEF_SIZE_T_) && !defined (_TYPEDEF_SIZE_T_)

#ifndef EXT_SIZE_T_TYPE
typedef size_t           SIZE_T;
#else
typedef EXT_SIZE_T_TYPE  SIZE_T;
#endif

#define _TYPEDEF_SIZE_T_
#endif

#if !defined (_NO_TYPEDEF_UTF16_T_) && !defined (_TYPEDEF_UTF16_T_)
typedef unsigned short  UTF16_T;

#define _TYPEDEF_UTF16_T_
#endif

#if !defined (_NO_TYPEDEF_UTF32_T_) && !defined (_TYPEDEF_UTF32_T_)
typedef unsigned long  UTF32_T;

#define _TYPEDEF_UTF32_T_
#endif

#if !defined (_NO_TYPEDEF_FLOAT_) && !defined (_TYPEDEF_FLOAT_)
typedef float  FLOAT;

#define _TYPEDEF_FLOAT_
#endif

#if !defined (_NO_TYPEDEF_DOUBLE_)  && !defined (_TYPEDEF_DOUBLE_)
typedef double  DOUBLE;

#define _TYPEDEF_DOUBLE_
#endif

#ifndef _USE_LINUX
typedef unsigned char       u_char;
typedef unsigned short      u_short;
typedef unsigned int        u_int;
typedef unsigned long       u_long;

typedef signed char         int8_t;
typedef unsigned char       u_int8_t;
typedef unsigned char       uint8_t;
typedef short               int16_t;
typedef unsigned short      u_int16_t;
typedef unsigned short      uint16_t;
typedef int                 int32_t;
typedef unsigned int        u_int32_t;
typedef unsigned int        uint32_t;
typedef long long           int64_t;
typedef unsigned long long  uint64_t;
#else
#include <stdint.h>
#endif



#if !defined (_NO_TYPEDEF_VOID_)  && !defined (_TYPEDEF_VOID_)
#undef VOID
#define VOID  void

#define _TYPEDEF_VOID_
#endif


#if !defined (_NO_TYPEDEF_BOOL_) && !defined (_TYPEDEF_BOOL_)

#ifndef EXT_BOOL_TYPE
typedef UINT8  BOOL;
#else
typedef signed EXT_BOOL_TYPE  BOOL;
#endif

#define _TYPEDEF_BOOL_

#ifdef TRUE
#undef TRUE
#endif

#ifdef FALSE
#undef FALSE
#endif

#define TRUE ((BOOL) 1)
#define FALSE ((BOOL) 0)
#endif

/* Min and max macros. Watch for side effects! */
#define X_MIN(_x, _y)  (((_x) < (_y)) ? (_x) : (_y))
#define X_MAX(_x, _y)  (((_x) > (_y)) ? (_x) : (_y))

/* The following macros are useful to create bit masks. */
#define MAKE_BIT_MASK_8(_val)  (((UINT8)  1) << _val)
#define MAKE_BIT_MASK_16(_val) (((UINT16) 1) << _val)
#define MAKE_BIT_MASK_32(_val) (((UINT32) 1) << _val)
#define MAKE_BIT_MASK_64(_val) (((UINT64) 1) << _val)
typedef UINT32  HANDLE_T;    /**<  handle defination     */
#define NULL_HANDLE 0

#endif /* _U_COMMON_H_ */
