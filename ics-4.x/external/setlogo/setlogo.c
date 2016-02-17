/*----------------------------------------------------------------------------*
 * Copyright Statement:                                                       *
 *                                                                            *
 *   This software/firmware and related documentation ("MediaTek Software")   *
 * are protected under international and related jurisdictions'copyright laws *
 * as unpublished works. The information contained herein is confidential and *
 * proprietary to MediaTek Inc. Without the prior written permission of       *
 * MediaTek Inc., any reproduction, modification, use or disclosure of        *
 * MediaTek Software, and information contained herein, in whole or in part,  *
 * shall be strictly prohibited.                                              *
 * MediaTek Inc. Copyright (C) 2010. All rights reserved.                     *
 *                                                                            *
 *   BY OPENING THIS FILE, RECEIVER HEREBY UNEQUIVOCALLY ACKNOWLEDGES AND     *
 * AGREES TO THE FOLLOWING:                                                   *
 *                                                                            *
 *   1)Any and all intellectual property rights (including without            *
 * limitation, patent, copyright, and trade secrets) in and to this           *
 * Software/firmware and related documentation ("MediaTek Software") shall    *
 * remain the exclusive property of MediaTek Inc. Any and all intellectual    *
 * property rights (including without limitation, patent, copyright, and      *
 * trade secrets) in and to any modifications and derivatives to MediaTek     *
 * Software, whoever made, shall also remain the exclusive property of        *
 * MediaTek Inc.  Nothing herein shall be construed as any transfer of any    *
 * title to any intellectual property right in MediaTek Software to Receiver. *
 *                                                                            *
 *   2)This MediaTek Software Receiver received from MediaTek Inc. and/or its *
 * representatives is provided to Receiver on an "AS IS" basis only.          *
 * MediaTek Inc. expressly disclaims all warranties, expressed or implied,    *
 * including but not limited to any implied warranties of merchantability,    *
 * non-infringement and fitness for a particular purpose and any warranties   *
 * arising out of course of performance, course of dealing or usage of trade. *
 * MediaTek Inc. does not provide any warranty whatsoever with respect to the *
 * software of any third party which may be used by, incorporated in, or      *
 * supplied with the MediaTek Software, and Receiver agrees to look only to   *
 * such third parties for any warranty claim relating thereto.  Receiver      *
 * expressly acknowledges that it is Receiver's sole responsibility to obtain *
 * from any third party all proper licenses contained in or delivered with    *
 * MediaTek Software.  MediaTek is not responsible for any MediaTek Software  *
 * releases made to Receiver's specifications or to conform to a particular   *
 * standard or open forum.                                                    *
 *                                                                            *
 *   3)Receiver further acknowledge that Receiver may, either presently       *
 * and/or in the future, instruct MediaTek Inc. to assist it in the           *
 * development and the implementation, in accordance with Receiver's designs, *
 * of certain softwares relating to Receiver's product(s) (the "Services").   *
 * Except as may be otherwise agreed to in writing, no warranties of any      *
 * kind, whether express or implied, are given by MediaTek Inc. with respect  *
 * to the Services provided, and the Services are provided on an "AS IS"      *
 * basis. Receiver further acknowledges that the Services may contain errors  *
 * that testing is important and it is solely responsible for fully testing   *
 * the Services and/or derivatives thereof before they are used, sublicensed  *
 * or distributed. Should there be any third party action brought against     *
 * MediaTek Inc. arising out of or relating to the Services, Receiver agree   *
 * to fully indemnify and hold MediaTek Inc. harmless.  If the parties        *
 * mutually agree to enter into or continue a business relationship or other  *
 * arrangement, the terms and conditions set forth herein shall remain        *
 * effective and, unless explicitly stated otherwise, shall prevail in the    *
 * event of a conflict in the terms in any agreements entered into between    *
 * the parties.                                                               *
 *                                                                            *
 *   4)Receiver's sole and exclusive remedy and MediaTek Inc.'s entire and    *
 * cumulative liability with respect to MediaTek Software released hereunder  *
 * will be, at MediaTek Inc.'s sole discretion, to replace or revise the      *
 * MediaTek Software at issue.                                                *
 *                                                                            *
 *   5)The transaction contemplated hereunder shall be construed in           *
 * accordance with the laws of Singapore, excluding its conflict of laws      *
 * principles.  Any disputes, controversies or claims arising thereof and     *
 * related thereto shall be settled via arbitration in Singapore, under the   *
 * then current rules of the International Chamber of Commerce (ICC).  The    *
 * arbitration shall be conducted in English. The awards of the arbitration   *
 * shall be cleanup and binding upon both parties and shall be entered and      *
 * enforceable in any court of competent jurisdiction.                        *
 *---------------------------------------------------------------------------*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>

/* boot logo offset is 0x230 */
#define _LOGO_EEPROM_NPTV_OFFSET 0x230 // 560
#define EEPROM_FILENAME     "/dev/eeprom_3"
#define LOGO_PARTITION      "/dev/mtd/mtdsdm14"   

#define BUFFER_SIZE         (256*1024)      // should be "nand flash block size" * n
#define MB                  (1024*1024)

const unsigned char JPEG_HEADER[] = {
    0xff, 0xd8
};

enum {
    ERR_OK                  =  0,
    ERR_OPEN_FAIL           = -1,
    ERR_WRONG_INDEX         = -2,
    ERR_INDEX_CHECK_FAIL    = -3,
    ERR_MEMORY_ALLOC_FAIL   = -4,
    ERR_JPEG_CHECK_FAIL     = -5,
    ERR_JPEG_SIZE_FAIL      = -6
};

static int open_file_seek(char *filename, int mode, int offset)
{
    int file;
    file = open (filename, mode);
    if (file < 0)
    {
        fprintf (stderr, "[Error] Open file '%s' error. %s\n", filename, strerror(errno));
        return 0;
    }

    if (lseek (file, offset, SEEK_SET) != offset)
    {
        close (file);
        fprintf (stderr, "[Error] Seek err at offset %d. %s\n", offset, strerror(errno));
        return 0;
    }

    return file;
}

int get_logo_index(void)
{
    int file;
    unsigned char uIndex[2];
    
    file = open_file_seek(EEPROM_FILENAME, O_RDONLY, _LOGO_EEPROM_NPTV_OFFSET);
    if (file == 0)
        return ERR_OPEN_FAIL;

    read(file, uIndex, sizeof(uIndex));
    close(file);

    if (uIndex[1] == (1<<uIndex[0]) && uIndex[0] <= 3)
    {
        return uIndex[0];
    }
    else
    {
        return ERR_INDEX_CHECK_FAIL;
    }
}

int set_logo_index(int index)
{
    int file;
    unsigned char uIndex[2];
    
    if (index >= 0 && index <= 3)
    {
        uIndex[0] = index;
        uIndex[1] = (1<<index);
    }
    else
    {
        fprintf(stderr, "[Error] wrong logo index number (%d). (0 <= index <= 3)\n", index);
        return ERR_WRONG_INDEX;
    }
    file = open_file_seek(EEPROM_FILENAME, O_RDWR, _LOGO_EEPROM_NPTV_OFFSET);
    if (file == 0)
        return ERR_OPEN_FAIL;

    write(file, uIndex, sizeof(uIndex));
    close(file);

    return 0;
}

int set_logo_data(int index, char *jpeg_filename)
{
    int file, jpeg_file;
    int jpeg_size;
    int size, total_size;
    char *buf;
    int result = ERR_OK;

    file = 0;
    jpeg_file = 0;
    buf = NULL;
    total_size = 0;

    buf = (char *)malloc(BUFFER_SIZE);
	
    if (buf == 0)
    {
        fprintf(stderr, "[Error] Cannot allocate enough buffer!\n");
        result = ERR_MEMORY_ALLOC_FAIL;
        goto cleanup;
    }

    if (!(index >= 0 && index <= 3))
    {
        fprintf(stderr, "[Error] Wrong logo index number (%d). (0 <= index <= 3)\n", index);
        result = ERR_WRONG_INDEX;
        goto cleanup;
    }

    /* open jpeg file and check */
    jpeg_file = open_file_seek(jpeg_filename, O_RDONLY, 0);
    if (jpeg_file == 0)
    {
        result = ERR_OPEN_FAIL;
        goto cleanup;
    }
    /* check jpeg header and file size */
    read(jpeg_file, buf, sizeof(JPEG_HEADER));
    if (memcmp(JPEG_HEADER, buf, sizeof(JPEG_HEADER)))
    {
        fprintf(stderr, "[Error] The file '%s' is not a valid jpeg file\n", jpeg_filename);
        result = ERR_JPEG_CHECK_FAIL;
        goto cleanup;
    }
    jpeg_size = lseek(jpeg_file, 0, SEEK_END);

    if (jpeg_size > MB)
    {
        fprintf(stderr, "[Error] The jpeg file '%s' size is larger than 1MB!\n", jpeg_filename);
        result = ERR_JPEG_SIZE_FAIL;
        goto cleanup;
    }
    lseek(jpeg_file, 0, SEEK_SET);

    file = open_file_seek(LOGO_PARTITION, O_RDWR, index*MB);
    if (file == 0)
    {
        result = ERR_OPEN_FAIL;
        goto cleanup;
    }

    /* store jpeg content to logo partition */
    while (1)
    {
        size = read(jpeg_file, buf, BUFFER_SIZE);		
        if (size == 0)
            break;
        write(file, buf, size);
        total_size += size;
    }
    printf("[Info] Store jpeg file '%s' (size: %d) to logo %d\n", jpeg_filename, total_size, index);

cleanup:
    if (buf)
        free(buf);
    if (jpeg_file)
        close(jpeg_file);
    if (file)
        close(file);

    return result;
}

#define USAGE   \
    "Usage: setlogo INDEX FILE (Store JPEG_FILE to logo partition by index INDEX\n" \
    "    or setlogo -s INDEX   (Select boot logo number)\n" \
    "    or setlogo            (Show help and current logo index)\n\n"
int main (int argc, char** argv)
{
    int index;
    char *jpeg_filename;

    if (argc != 3)
    {
        printf(USAGE);		
        index = get_logo_index();
        if (index >= 0)
            printf("[Info] Current logo index is %d\n", index);
        return 0;
    }

    if (!memcmp(argv[1], "-s", 3))
    {
        index = atol(argv[2]);
        if (!set_logo_index(index))
        {
            printf("[Info] Set logo index to %d\n", index);
            return 0;
        }
        else
            return -1;
    }

    index = atol(argv[1]);
    jpeg_filename = argv[2];

    if (!set_logo_data(index, jpeg_filename))
    {
        printf("[Info] Maybe you want to use ' setlogo -s %d ' to select this logo\n", index);
        return 0;
    }
    else
        return -1;
}

