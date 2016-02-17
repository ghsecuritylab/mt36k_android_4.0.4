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
 * shall be final and binding upon both parties and shall be entered and      *
 * enforceable in any court of competent jurisdiction.                        *
 *---------------------------------------------------------------------------*/
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/socket.h>

#include <sys/ioctl.h>
#include <net/if.h>     /* for struct ifreq */
#include <net/if_arp.h> /* for ARPHRD_ETHER */

/* chg 416 (0x1A0) to 532 (0x214) */
#define _NET_EEPROM_NPTV_OFFSET 532

static int _net_check_mac (unsigned char *pui1_data)
{
    unsigned int *pui4_tmp = (unsigned int*) pui1_data;
    if (*pui4_tmp == 0 || *pui4_tmp == 0xFFFFFFFF)
    {
        unsigned short *pui2_tmp = (unsigned short*) &(pui1_data[4]);
        if ((*pui4_tmp == 0 && *pui2_tmp == 0) ||
            (*pui4_tmp == 0xFFFFFFFF && *pui2_tmp == 0xFFFF))
        {
            printf (">>>>> BAD MAC: 0x%08x%04x\n", *pui4_tmp, *pui2_tmp);
            return -1;
        }
    }
    return 0;
}

static int _net_enable (const char* ps_dev, int up)
{
    int sockfd = -1;
    int ret = -1;
    struct ifreq ifr;

    sockfd = socket (AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0)
    {
        printf (">>>>> socket error %d, %s\n", errno, strerror(errno));
        return -1;
    }

    memset (&ifr, 0, sizeof (ifr));
    //strcpy (ifr.ifr_name, ps_dev);
    strncpy(ifr.ifr_name, ps_dev, (sizeof(ifr.ifr_name)-1));

    ret = ioctl (sockfd, SIOCGIFFLAGS, &ifr);
    if (ret < 0)
    {
        printf (">>>>> ioctl %s error %d, %s\n", ps_dev, errno, strerror(errno));
        close (sockfd);
        return -1;
    }

    if (up)
    {
        if (ifr.ifr_flags & IFF_UP && ifr.ifr_flags & IFF_RUNNING)
        {
            /*printf ("%s already RUNNING\n", ps_dev);*/
            close (sockfd);
            return 0;
        }
    }
    else
    {
        if (!(ifr.ifr_flags & IFF_UP))
        {
            /*printf ("%s already down\n", ps_dev);*/
            close (sockfd);
            return 0;
        }
    }

    if (up)
    {
        ifr.ifr_flags |= (IFF_RUNNING | IFF_UP);
    }
    else
    {
        ifr.ifr_flags &= ~(IFF_RUNNING | IFF_UP);
    }

    ret = ioctl (sockfd, SIOCSIFFLAGS, &ifr);
    if (ret < 0)
    {
        printf (">>>>> ioctl set %s error %d, %s\n", ps_dev, errno, strerror(errno));
        close (sockfd);
        return -1;
    }

    /*printf ("%s set %s\n", ps_dev, up ? "RUNNING" : "down");*/
    close (sockfd);

    return 0;
}

int _net_get_mac(const char *psz_name, unsigned char *pui1_mac)
{
    int sockfd;
    struct ifreq ifr;

    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    bzero(&ifr, sizeof(ifr));
    //strcpy(ifr.ifr_name, psz_name);
    strncpy(ifr.ifr_name, psz_name, (sizeof(ifr.ifr_name)-1));

    if (ioctl(sockfd, SIOCGIFHWADDR, &ifr) < 0)
    {
        printf("SIOCGIFHWADDR ioctrl Connect failed, err : %s\n", strerror(errno));
        close (sockfd);
        return -1;
    }
    close (sockfd);
    memcpy(pui1_mac, ifr.ifr_hwaddr.sa_data, 6);
    return 0;
}

int _net_set_mac(const char *psz_name, unsigned char *pui1_mac)
{
    int sockfd;
    struct ifreq ifr;
    int ret;

    if (pui1_mac == NULL)
    {
        return -1;
    }

    if (_net_check_mac (pui1_mac) != 0)
    {
        return -1;
    }

#if 0
    {
        unsigned int *pui4_tmp = (unsigned int*) pui1_mac;
        unsigned short *pui2_tmp = (unsigned short*) (pui1_mac + 4);
        printf ("%s, %s 0x%08x%04x\n", __FUNCTION__, psz_name, ntohl (*pui4_tmp), ntohs (*pui2_tmp));
    }
#endif

    if (_net_enable(psz_name, 0) != 0)
    {
        printf("%s.%d ni_enable failed: %d, %s\n", __FUNCTION__, __LINE__, errno, strerror(errno));
        return -1;
    }

    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0)
    {
        printf("%s.%d socket failed: %d, %s\n", __FUNCTION__, __LINE__, errno, strerror(errno));
        return -1;
    }

    bzero(&ifr, sizeof(ifr));
    //strcpy(ifr.ifr_name, psz_name);
    strncpy(ifr.ifr_name, psz_name, (sizeof(ifr.ifr_name)-1));
    memcpy(&ifr.ifr_hwaddr.sa_data, pui1_mac, 6);
    ifr.ifr_hwaddr.sa_family = ARPHRD_ETHER;

    ret = ioctl(sockfd, SIOCSIFHWADDR, &ifr);
    if (ret != 0)
    {
        printf("%s.%d SIOCSIFHWADDR ioctrl failed: %d, %s\n", __FUNCTION__, __LINE__, errno, strerror(errno));
    }

    close (sockfd);

    if (_net_enable(psz_name, 1) < 0)
    {
        printf("%s enable failed\n", psz_name);
        return -1;
    }

#if 0
    /* write config (eeprom) */
    if (x_strcmp (psz_name, NI_ETHERNET_0) == 0)
    {
        if ((ret = _write_mac_config ((unsigned char*)pui1_mac)) != 0)
        {
            printf ("write mac config err %d\n", ret);
            return -1;
        }
    }
#endif

    {
        int i;
        printf ("set phy MAC: ");
        for (i = 0; i < 6; i++)
        {
            printf ("%02x",  pui1_mac[i]);
        }
        printf ("\n");
    }

    return ret;
}

int main (int argc, char** argv)
{
    /*
     * /dev/eeprom_3
     */
    FILE* file = NULL;
    unsigned char data[7] = "";
    int ret = 0;
    int i;
    unsigned char sum = 0;

    if (argc <= 2)
    {
        printf ("Usage: %s dev_name mac_address\n", argv[0]);
        printf ("example: %s eth0 00:12:34:56:78:9A\n", argv[0]);
    }

    file = fopen ("/dev/eeprom_3", "r+");
    if (file == NULL)
    {
        printf ("open eeprom err, %d, %s\n", errno, strerror (errno));
        printf ("dtv driver may be not inited\n");
        return -1;
    }

    if (fseek (file, _NET_EEPROM_NPTV_OFFSET, SEEK_SET) != 0)
    {
        fclose (file);
        printf ("seek eeprom err, %d, %s\n", errno, strerror (errno));
        return -1;
    }

    /* read cur */
    ret = fread(data, 1, sizeof (data), file);
    if (ret != sizeof(data))
    {
        printf (">>>>> read %d\n", ret);
    }

    /* checksum */
    sum = 0;
    for(i = 0; i < 7; i++)
    {
        sum += data[i];
    }

    printf ("current eth0 MAC in eeprom (0x%x): ", _NET_EEPROM_NPTV_OFFSET);
    for (i = 0; i < 6; i++)
    {
        printf ("%02x",  data[i]);
        if (i != 5) putchar (':');
    }
    if (sum != 0)
    {
        printf ("  - 0x%02x checksum error 0x%02x", data[6], sum);
    }
    putchar ('\n');

    if (argc <= 2)
    {
        fclose (file);
        if (_net_check_mac (data) == 0)
        {
            unsigned char rdata[7] = {0};
            _net_get_mac ("eth0", rdata);
            if (memcmp (data, rdata, 6) != 0)
            {
                printf ("current phy MAC: ");
                for (i = 0; i < 6; i++)
                {
                    printf ("%02x",  rdata[i]);
                }
                printf ("\n");
                _net_set_mac ("eth0", data);
            }
        }
        return 0;
    }

    /* set mac */
    /* check mac */
    {
        sum = 0;
        if (strcmp (argv[1], "eth0") != 0)
        {
            printf ("not support device %s\n", argv[1]);
            fclose (file);
            return -1;
        }

        memset (data, 0, sizeof (data));
        sscanf(argv[2], "%hhx:%hhx:%hhx:%hhx:%hhx:%hhx",
               &(data[0]),
               &(data[1]),
               &(data[2]),
               &(data[3]),
               &(data[4]),
               &(data[5]));
        printf ("set %s MAC ", argv[1]);
        for (i = 0; i < 6; i++)
        {
            sum += data[i];
            printf ("%02x",  data[i]);
            if (i != 5) putchar (':');
        }
        /* checksum */
        data[6] = ~sum + 1;
        printf ("\n");
    }

    if (_net_check_mac (data) != 0)
    {
        fclose (file);
        return 0;
    }

    if (fseek (file, _NET_EEPROM_NPTV_OFFSET, SEEK_SET) != 0)
    {
        fclose (file);
        printf ("\nseek eeprom err, %d, %s\n", errno, strerror (errno));
        return -1;
    }

#if 1
    ret = fwrite (data, 1, sizeof (data), file);
    if (ret == sizeof(data))
    {
        printf ("Success\n");
    }
    else
    {
        printf ("Error\n");
    }
#else
    printf ("No write\n");
#endif

    fclose (file);

    _net_set_mac (argv[1], data);

    return 0;
}

