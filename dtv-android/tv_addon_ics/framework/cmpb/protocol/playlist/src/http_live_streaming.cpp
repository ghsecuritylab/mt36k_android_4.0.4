/*-----------------------------------------------------------------------------
 * Copyright (c) 2010, MediaTek Inc.
 * All rights reserved.
 *
 * Unauthorized use, practice, perform, copy, distribution, reproduction,
 * or disclosure of this information in whole or in part is prohibited.
 *-----------------------------------------------------------------------------
 * $RCSfile:  $
 * $Revision: #2 $
 * $Date: 2013/10/14 $
 * $Author: huolun.zhang $
 *
 * Description:
 *    This file is use for provide http live streaming play
 *---------------------------------------------------------------------------*/

/*------------------------------------------------------------------------
                    include files
 -----------------------------------------------------------------------*/
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <ctype.h>   // for tolower()
#include "curl/curl.h"
#include "http_live_streaming.h"
#include "Playlist.h"
#include <unistd.h>
#include <sys/stat.h>
#include <ctype.h>
#include "Log.h"



using namespace hls;

static int parse_media_info(struct HLS_PLAYLIST *pt_playlist,  char *data,  char *content);
static int parse_target_duration_info(struct HLS_PLAYLIST *pt_playlist,  char *data,  char *content);
static int parse_media_sequence_info(struct HLS_PLAYLIST * pt_playlist,  char *data,  char *content);
static int parse_encrypt_key(struct HLS_PLAYLIST *pt_playlist,  char *data,  char *content);
static int parse_program_date_time_info(struct HLS_PLAYLIST * pt_playlist,  char *data,  char *content);
static int parse_allow_cache_info(struct HLS_PLAYLIST *pt_playlist,  char *data,  char *content);
static int parse_endlist_info(struct HLS_PLAYLIST *pt_playlist,  char *data,  char *content);
static int parse_stream_inf_info(struct HLS_PLAYLIST *pt_playlist,  char *data,  char *content);
static int parse_discontinuity_info(struct HLS_PLAYLIST *pt_playlist,  char *data,  char *content);
static int parse_version_info(struct HLS_PLAYLIST *pt_playlist,  char *data,  char *content);
static int parse_playlist_type_info(struct HLS_PLAYLIST * pt_playlist, char * data, char * content);
static int parse_byterange_info(struct HLS_PLAYLIST * pt_playlist, char * data, char * content);
static int parse_playlist_media_info(struct HLS_PLAYLIST * pt_playlist, char * data, char * content);
static void remove_old_media_file(struct HLS_PLAYLIST * pt_playlist);

static void free_stream_info(struct STREAM_INFO * pt_stream_info);
static void free_media_info(struct MEDIA_INFO *pt_media_info);
static void free_media_char_buffer(struct MEDIA_INFO *pt_media_info);
static char * find_last_of_str(char * pData, char * pKeyWord);

/*------------------------------------------------------------------------
                    macros, defines, typedefs, enums
 -----------------------------------------------------------------------*/
const char * stream_info_tag[]=
{
    "BANDWIDTH=",
    "PROGRAM-ID=",
    "CODECS=",
    "RESOLUTION=",
};

const char * non_playlsit_sub_filename[]=
{
    ".mp4",
    ".mp3",
    ".mpg",
    ".wma",
    ".wmv",
    ".asf",
    NULL
};

const struct PLAYLIST_INFO_TYPE playlist_contentType_type[]=
{
    {"audio/x-scpls", PLAYLIST_PLS},
    {"video/x-ms-asf",PLAYLIST_ASX},
    {"audio/x-mpegurl", PLAYLIST_M3U},                 /* The US-ASCII */
    {"application/xspf+xml", PLAYLIST_XSPF},
    {"application/vnd.apple.mpegurl", PLAYLIST_M3U8},  /* The unicode version of m3u playlist file */
    {"application/x-mpegURL", PLAYLIST_M3U8}, /*for compatible v 01*/    	
    {NULL, PLAYLIST_UNKNOW}
};

struct PLAYLIST_INFO_TYPE playlist_sub_filename_type[] =
{
    {".pls", PLAYLIST_PLS},
    {".asx", PLAYLIST_ASX},
    {".m3u8", PLAYLIST_M3U8},   /* The unicode version of m3u playlist file */
    {".m3u", PLAYLIST_M3U},     /* The US-ASCII */
    {".xspf", PLAYLIST_XSPF},
    {NULL, PLAYLIST_UNKNOW}
};

struct HLS_TAG_FUNCTION http_live_streaming_tag_function[]=
{
    /* new tags */
    {"EXTM3U", NULL},
    {"EXTINF", parse_media_info },        /* media file */
    {"EXT-X-TARGETDURATION", parse_target_duration_info },     /* playlist */
    {"EXT-X-MEDIA-SEQUENCE", parse_media_sequence_info },      /* playlist */
    {"EXT-X-KEY", parse_encrypt_key },     /* media file */

    /* it should place this tag after every EXT-X-DISCONTINUITY tag in the playlist file */
    {"EXT-X-PROGRAM-DATE-TIME", parse_program_date_time_info },   /* media file */
    {"EXT-X-ALLOW-CACHE", parse_allow_cache_info },    /* media file */
    {"EXT-X-ENDLIST", parse_endlist_info},             /* playlist */
    {"EXT-X-STREAM-INF",parse_stream_inf_info},        /* indicate another playlist */
    {"EXT-X-DISCONTINUITY", parse_discontinuity_info}, /* playlist */
    {"EXT-X-VERSION", parse_version_info},   /* playlist */

   	{"EXT-X-PLAYLIST-TYPE", parse_playlist_type_info},  /* playlist */

    /* HLS version 4 tag */
    {"EXT-X-BYTERANGE", parse_byterange_info},   /* media file */    
    {"EXT-X-MEDIA", parse_playlist_media_info},  /* playlist */	

    {NULL, NULL}
};

const char *delim = "\n";

static int g_stop_download = false;
static CURL *g_media_file_curl = NULL;
/*------------------------------------------------------------------------
 * Name:  decodeFromUTF8
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  data
 *          len
 *
 * Outputs: - TRUE: playlist
 *            FALSE: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
static char utf82ascii[128]=
{
    /*  0   1   2   3   4   5   6   7    8    9    a     b   c    d   e   f   10  */
    ' ',' ',' ',' ',' ',' ',' ','\a','\b','\t','\n',' ','\f','\r',' ',' ',' ',
    ' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',
    '!','\"','#','$','%','&','\'','(',')','*','+',',','-','.', '/','0',
    '1','2','3','4','5','6','7','8','9',':',';','<','=','<','?','@',
    'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
    'Q','R','S','T','U','V','W','X','Y','Z','[','\\',']','^','_','`',
    'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p',
    'q','r','s','t','u','v','w','x','y','z','{','|','}','~',' '
};

/*------------------------------------------------------------------------
 * Name:  decodeFromUTF8
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  data
 *          len
 *
 * Outputs: - TRUE: playlist
 *            FALSE: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
int
decodeFromUTF8(char * data, size_t len)
{
    unsigned int i ;
    for (i = 0; i < len; i++)
    {
        data[i] = utf82ascii[(uint8_t)data[i]];
    }

    return (HLS_OK);
} /* end of decodeFromUTF8 */


/*------------------------------------------------------------------------
 * Name:  encodeToUTF8
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  data
 *          len
 *
 * Outputs: - TRUE: playlist
 *            FALSE: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
int
encodeToUTF8(char * data, size_t len)
{
    unsigned int i, j ;
    for (i = 0; i < len; i++)
    {
        for(j = 0; j <127; j++)
        {
            if(utf82ascii[j] == data[i])
            {
                data[i] = (unsigned char) j;
            }
        }/* end of for */
    } /* end of for */
    return (HLS_OK);
} /* end of decodeFromUTF8 */


/*************************************************************************
*   Static Function
*************************************************************************/
/*------------------------------------------------------------------------
 * Name:  mystrdup
 *
 * Description:
 *
 * Inputs:
 *
 * Outputs: -
 *
 * Returns: -
 -----------------------------------------------------------------------*/
char *
getHostUrl(const char * ps_url)
{
	char * ps_hostUrl = NULL;
	const char * end = ps_url + strlen(ps_url) - 1;

	while( (end > ps_url) && (*end != '/'))
	{
		end--;
	}
	if (*end == '/')
	{
		ps_hostUrl = (char *)malloc((end-ps_url+1) * sizeof(char));
		strncpy(ps_hostUrl, ps_url, end-ps_url);
		ps_hostUrl[end-ps_url] = '\0';
		LOG(6, "ps_hostUrl=%s\n",ps_hostUrl)
	}

	return ps_hostUrl;
}

/*------------------------------------------------------------------------
 * Name:  mystrdup
 *
 * Description:
 *
 * Inputs:
 *
 * Outputs: -
 *
 * Returns: -
 -----------------------------------------------------------------------*/
static char *
mystrdup(const char * pSource)
{
    char * pDest = NULL;
    if (pSource)
    {
        pDest = (char *)malloc( strlen(pSource) + 1);
        if (pDest)
        {
            strcpy(pDest, pSource);
        }
    }
    return pDest;
}

/*------------------------------------------------------------------------
 * Name:  mystrnicmp
 *
 * Description:
 *
 * Inputs:
 *
 * Outputs: -
 *
 * Returns: -
 -----------------------------------------------------------------------*/
static int
mystrnicmp(const char * str1, const char * str2, size_t num)
{
    char * lower_str1 = (char*)malloc(num + 1);
    char * lower_str2 = (char*)malloc(num + 1);
    memcpy(lower_str1, str1, num);
    lower_str1[num] = '\0';
    memcpy(lower_str2, str2, num);
    lower_str2[num] = '\0';
    char * pCh = lower_str1;
    while (*pCh)
    {
        *pCh = tolower(*pCh);
        pCh++;
    }
    pCh = lower_str2;
    while (*pCh)
    {
        *pCh = tolower(*pCh);
        pCh++;
    }
    int lower_result = strncmp(lower_str1, lower_str2, num);
    free(lower_str1);
    free(lower_str2);
    return lower_result;
}

void chars_to_hex(unsigned char * in, unsigned char *out, int iLen)
{
    if ((!in) || (!out))
        return;
    unsigned char cTmp;
    for (int i = 0; i < iLen; i++)
    {
        cTmp = toupper(*(in + i));
        if ((cTmp >= 'A') && (cTmp <= 'F'))
            *(out + i/2) |= cTmp - 55;
        else
            *(out + i/2) |= cTmp - 48;
        if (i%2 == 0)
            *(out + i/2)<<=4;
    } 
}
/*------------------------------------------------------------------------
 * Name:  CopyHeaderValueIfNameMatches
 *
 * Description:
 *
 * Inputs:  pHeaderName : find header field
 *          pHTTPResponseLine : header content
 *
 * Outputs: -
 *
 * Returns: -
 -----------------------------------------------------------------------*/
static char *
CopyHeaderValueIfNameMatches(const char * pHeaderName, const char * pHTTPResponseLine)
{
    const char * pData = pHTTPResponseLine;
    const int nHeaderLength = strlen(pHeaderName);

    // NOTE: case-insensitive compare
    if (mystrnicmp(pHeaderName, pData, nHeaderLength) == 0)
    {
        pData += nHeaderLength;
        if (strncmp(": ", pData, 2) == 0)
        {
            pData += 2;
            if (*pData)
            {
                // need to strip out "\r\n" at end of buffer
                const char * pEOL = strstr(pData, "\r\n");
                if (pEOL)
                {
                    char * pResult = mystrdup(pData);
                    pResult[pEOL - pData] = '\0';
                    return pResult;
                }
            }
        }
    }
    return NULL;
}


/*------------------------------------------------------------------------
 * Name:  consumeHeaderData
 *
 * Description:
 *
 * Inputs:
 *
 * Outputs: -
 *
 * Returns: -
 -----------------------------------------------------------------------*/
static size_t
consumeHeaderData(void *ptr, size_t z_size, size_t z_nmemb, void *pv_tag)
{
    if (g_stop_download)
    {
        LOG(0, "---[INFO]---Stop to consume header data!\n");
        return 0;
    }
    
    struct MEDIA_INFO *pt_media_info = (struct MEDIA_INFO *)pv_tag;
    size_t nSizeBytes = z_size * z_nmemb;
    char * pValue = NULL;
    const char * pData = (const char *) ptr;

    // NOTE: from the curl documentation:
    // "The headers are passed to the callback function one by one, and you can depend on that fact."
    if (strncmp("HTTP/", pData, 5) == 0)
    {
        while (*pData && *pData != ' ') pData++;
        pt_media_info->result_code = atoi(pData);
        return (nSizeBytes);
    }

    // if this is one of the headers that we care about, grab its value
        pValue = CopyHeaderValueIfNameMatches("Content-Length", pData);
        if (pValue)
        {
            pt_media_info->content_length = atoi(pValue);
            free(pValue);
            LOG(5, "%d, pt_media_info->content_length = %d", 
            pt_media_info->sequence_number,
            pt_media_info->content_length);        
        }

    
    return nSizeBytes;
} /* end of consumeHeaderData */


/*------------------------------------------------------------------------
 * Name:  consumeContentData
 *
 * Description:
 *
 * Inputs:
 *
 * Outputs: -
 *
 * Returns: -
 -----------------------------------------------------------------------*/
static size_t
consumeContentData(void *ptr, size_t z_size, size_t z_nmemb, void *pv_tag)
{
    hls::Playlist * pt_theList = (hls::Playlist*) pv_tag;
        
    if ( (g_stop_download == true) ||(pt_theList->resetDownload== true) )
    {
        LOG(0, "cancel media file %d download !!\n", pt_theList->pt_cur_dl_media->sequence_number);
        pt_theList->notifyMediaStatus(pt_theList->pt_cur_dl_media, MEDIA_FILE_DOWNLOAD_CANCEL);
        return 1;
    }
    
    size_t nSizeBytes = z_size * z_nmemb;
    struct CHAR_BUFFER * pt_buffer = ( struct CHAR_BUFFER *)&(pt_theList->pt_cur_dl_media->url_content);

    // Once this function is called, we know that the header has been completely downloaded.


    if (404 == pt_theList->pt_cur_dl_media->result_code )
    {
        LOG(5, "result_code = %d\n", pt_theList->pt_cur_dl_media->result_code);
        return nSizeBytes+1;
    }

    if (pt_buffer->pac_buffer == NULL)
    {
        LOG(0, "%d, pt_media_info->content_length = %d", 
        	pt_theList->pt_cur_dl_media->sequence_number,
        	pt_theList->pt_cur_dl_media->content_length);
        
        if (pt_theList->pt_cur_dl_media->content_length < nSizeBytes)
        {
            /* work-around for that server do not return correct content length */           
            int bufSize = 1024 * 1024;
         
            LOG(0, "Original content length = %d, new content length = %d!!!\n", 
            	      pt_theList->pt_cur_dl_media->content_length,  nSizeBytes);    
            
            pt_buffer->pac_buffer = (char*)malloc(bufSize+1);
            if (pt_buffer->pac_buffer == NULL)
           {
                LOG(0, "malloc size = %d, failed\n", nSizeBytes);
                return nSizeBytes+1;
            }
            memset(pt_buffer->pac_buffer, 0, bufSize+1);
            pt_buffer->i4_used_size = 0;
            pt_buffer->i4_total_size = bufSize +1;
            
        }
        else
        {
            pt_buffer->pac_buffer = (char*)malloc(pt_theList->pt_cur_dl_media->content_length+1);
            if (pt_buffer->pac_buffer == NULL)
           {
                LOG(0, "malloc size = %d, failed\n", nSizeBytes);
                return nSizeBytes+1;
            }
            memset(pt_buffer->pac_buffer, 0, pt_theList->pt_cur_dl_media->content_length +1);
            pt_buffer->i4_used_size = 0;
            pt_buffer->i4_total_size = pt_theList->pt_cur_dl_media->content_length +1;
        }    
    }

    if (pt_buffer->i4_used_size + nSizeBytes >= pt_buffer->i4_total_size)
    {
        // resize buffer
        pt_buffer->pac_buffer = (char *) realloc(pt_buffer->pac_buffer,
                                (pt_buffer->i4_total_size + nSizeBytes * 10));
        if ( pt_buffer->pac_buffer == NULL )
        {
            LOG(0, "realloc size = %d, failed\n", nSizeBytes);
            return nSizeBytes+1;
        }
        LOG(0, "[%d] : realloc size = %d, pt_buffer->i4_total_size = %d, pt_media_info->content_length = %d \n", 
        	  pt_theList->pt_cur_dl_media->sequence_number,
        	  nSizeBytes, 
        	  pt_buffer->i4_total_size, 
        	  pt_theList->pt_cur_dl_media->content_length);        
        pt_buffer->i4_total_size += (nSizeBytes * 10);

    }

    memcpy(pt_buffer->pac_buffer + pt_buffer->i4_used_size, ptr, nSizeBytes);
    pt_buffer->i4_used_size += nSizeBytes;
    pt_buffer->pac_buffer[pt_buffer->i4_used_size] = '\0';

    pt_theList->notifyMediaStatus(pt_theList->pt_cur_dl_media, MEDIA_FILE_DOWNLOADING);
    return(nSizeBytes);
} /* end of consumeContentData */


/*------------------------------------------------------------------------
 * Name:  _header_wr_function
 *
 * Description:
 *
 * Inputs:
 *
 * Outputs: -
 *
 * Returns: -
 -----------------------------------------------------------------------*/
static size_t
_header_wr_function(void *ptr, size_t z_size, size_t z_nmemb, void *pv_tag)
{
    struct CHAR_BUFFER *pt_buffer = (struct CHAR_BUFFER *) pv_tag;
    size_t z_len = z_size * z_nmemb;

    LOG(6, "_header_function, z_len = %d\n", z_len);

    if ( z_len )
    {
        if ( (pt_buffer->i4_total_size - pt_buffer->i4_used_size + 1) < z_len )
        {
            // resize buffer
            pt_buffer->pac_buffer = (char *) realloc(pt_buffer->pac_buffer,
                                    (pt_buffer->i4_total_size + z_len * 2));

            if ( pt_buffer->pac_buffer == NULL )
            {
                LOG(0, "realloc size = %d, failed\n", z_len);
                return z_len+1;
            }
            pt_buffer->i4_total_size += (z_len * 2);
        }

        memcpy(pt_buffer->pac_buffer + pt_buffer->i4_used_size, ptr, z_len);
        pt_buffer->i4_used_size += z_len;
        pt_buffer->pac_buffer[pt_buffer->i4_used_size] = '\0';
    }

    return(z_len);
} /* end of _header_wr_function */


/*------------------------------------------------------------------------
* Name:  _body_wr_function
*
* Description: This API trim head and tail space in string.
*
* Inputs:  url
*
* Outputs: - TRUE: playlist
*            FALSE: non-playlist
*
* Returns: -
-----------------------------------------------------------------------*/
static size_t
_body_wr_function(void *ptr, size_t z_size, size_t z_nmemb, void *pv_tag)
{
    struct CHAR_BUFFER * pt_buffer = (struct CHAR_BUFFER *)  pv_tag;
    size_t z_len = z_size * z_nmemb;

    LOG(6, "_body_wr_function, z_len = %d\n", z_len);
    LOG(6, "pt_buffer->pac_buffer = %p, pt_buffer->i4_total_size = %d, pt_buffer->i4_used_size = %d\n",
        pt_buffer->pac_buffer,
        pt_buffer->i4_total_size,
        pt_buffer->i4_used_size);

    if ( z_len )
    {
        if ( (pt_buffer->i4_total_size - pt_buffer->i4_used_size + 2) < z_len )
        {
            // resize buffer
            pt_buffer->pac_buffer = (char *) realloc(pt_buffer->pac_buffer,
                                    (pt_buffer->i4_total_size + z_len * 2));
            if ( pt_buffer->pac_buffer == NULL )
            {
                LOG(0, "realloc size = %d, failed\n", z_len);
                return z_len+1;
            }
            pt_buffer->i4_total_size += (z_len * 2);
        }

        memcpy( (void *)(pt_buffer->pac_buffer + pt_buffer->i4_used_size), ptr, z_len);
        pt_buffer->i4_used_size += z_len;
        pt_buffer->pac_buffer[pt_buffer->i4_used_size] = '\n';/*add*/
        pt_buffer->pac_buffer[pt_buffer->i4_used_size + 1] = '\0';
    }

    return(z_len);
} /* end of _body_wr_function */

/*------------------------------------------------------------------------
* Name:  _body_wr_function
*
* Description: This API try to break the body write.
*
* Returns: -
-----------------------------------------------------------------------*/

static size_t _body_wr_break_function(void *ptr, size_t z_size,  size_t z_nmemb, void *pv_tag)
{
	bool * pbSuccess = (bool *)pv_tag;
	if (pbSuccess)
		*pbSuccess = true;
	
    if ( z_size * z_nmemb )
    {
        // force broken;
        return z_size * z_nmemb -1;
    }

    return z_size * z_nmemb;
}   


/*-----------------------------------------------------------------------------
 * Name:  progress_function
 *
 * Description: callback function for progress
 *
 * Inputs:  pv_data: the data for write
 *          d_total:
 *          d_now:
 *          u_total:
 *          u_now:
 *
 * Outputs: received data bytes
 *
 * Returns: received data bytes
 ----------------------------------------------------------------------------*/
static size_t _progress_function(void *pv_data, double d_total, double d_now,
                              double u_total, double u_now)
{

    hls::Playlist * pt_theList = (hls::Playlist*) pv_data;
    
    if ( (g_stop_download == true) ||(pt_theList->resetDownload== true) )
    {
        LOG(0, "cancel media file download %d!!\n", pt_theList->pt_cur_dl_media->sequence_number);
        pt_theList->notifyMediaStatus(pt_theList->pt_cur_dl_media, MEDIA_FILE_DOWNLOAD_CANCEL);
        return 1;
    }
    	
    return 0;
}

/*------------------------------------------------------------------------
 * Name:  free_encrypt_key
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  ps_url
 *
 * Outputs: - playlist
 *
 *
 * Returns: - playlist
 -----------------------------------------------------------------------*/
int
free_encrypt_key(struct KEY_INFO * pt_encrypt_key)
{
    if(pt_encrypt_key == NULL)
    {
        return (HLS_OK);
    }

    if (pt_encrypt_key->ps_url)
    {
        free(pt_encrypt_key->ps_url);
        pt_encrypt_key->ps_url = NULL;
    }

    /* free header buffer */
    if(pt_encrypt_key->url_header.pac_buffer != NULL)
    {
        free(pt_encrypt_key->url_header.pac_buffer);
        pt_encrypt_key->url_header.pac_buffer = NULL;
    }

    /*free content buffer */
    if(pt_encrypt_key->url_content.pac_buffer != NULL)
    {
        free(pt_encrypt_key->url_content.pac_buffer);
        pt_encrypt_key->url_content.pac_buffer = NULL;
    }

    /* free media buffer */

    /* free playlist */
    free(pt_encrypt_key);
    pt_encrypt_key = NULL;

    return (HLS_OK);
}


/*------------------------------------------------------------------------
 * Name:  new_media_file
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  none
 *
 * Outputs: - pt_media_info
 *
 *
 * Returns: - pt_media_info
 -----------------------------------------------------------------------*/
struct MEDIA_INFO * new_media_info(void)
{
    struct MEDIA_INFO * pt_media_info = NULL;
    pt_media_info = (struct MEDIA_INFO*)malloc(sizeof(struct MEDIA_INFO));
    memset(pt_media_info, 0, sizeof(struct MEDIA_INFO));
    pt_media_info->status = MEDIA_FILE_NONE;
    pt_media_info->entry_info.type = TYPE_MEDIA;
    return(pt_media_info);
}

/*------------------------------------------------------------------------
 * Name:  new_encrypt_key
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  ps_url
 *
 * Outputs: - playlist
 *
 *
 * Returns: - playlist
 -----------------------------------------------------------------------*/
struct KEY_INFO *
new_encrypt_key(const char * ps_url)
{
    struct KEY_INFO * pt_encrypt_key;

    /* allocate playlist memory */
    if ( (pt_encrypt_key = (struct KEY_INFO *)malloc(sizeof(struct KEY_INFO))) == NULL)
    {
        LOG(0, "%s:%d malloc fail \r\n", __FUNCTION__, __LINE__);
        return NULL;
    }

    memset(pt_encrypt_key, 0, sizeof(struct KEY_INFO));
    pt_encrypt_key->entry_info.type = TYPE_KEY;

    /* allocate header buffer */
    pt_encrypt_key->url_header.pac_buffer = (char *) malloc(1024);
    if ( !pt_encrypt_key->url_header.pac_buffer )
    {
        free_encrypt_key (pt_encrypt_key);
        return NULL;
    }
    memset(pt_encrypt_key->url_header.pac_buffer, 0, 1024);
    pt_encrypt_key->url_header.i4_total_size = 1024;
    pt_encrypt_key->url_header.i4_used_size = 0;

    /* allocate content buffer */
    pt_encrypt_key->url_content.pac_buffer = (char *) malloc(128);
    if ( !pt_encrypt_key->url_content.pac_buffer )
    {
        free_encrypt_key (pt_encrypt_key);
        return NULL;
    }
    memset(pt_encrypt_key->url_content.pac_buffer, 0, 128);
    pt_encrypt_key->url_content.i4_total_size = 128;
    pt_encrypt_key->url_content.i4_used_size = 0;

    return (pt_encrypt_key);
} /* new_encrypt_key */


/*------------------------------------------------------------------------
 * Name:  new_playlist
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  ps_url
 *
 * Outputs: - playlist
 *
 *
 * Returns: - playlist
 -----------------------------------------------------------------------*/
struct HLS_PLAYLIST *
new_playlist(const char * ps_url)
{
    struct HLS_PLAYLIST  *pt_playlist = NULL;

    LOG(6, "%s, ps_url = %s\n", __FUNCTION__, ps_url);
    if (ps_url == NULL)
    {
        LOG(0, "%s:%d playlist url wrong \r\n", __FUNCTION__, __LINE__);
        return NULL;
    }

    /* allocate playlist memory */
    if ( (pt_playlist = (HLS_PLAYLIST*)malloc(sizeof(struct HLS_PLAYLIST)))== NULL)
    {
        LOG(0, "%s:%d malloc fail \r\n", __FUNCTION__, __LINE__);
        return NULL;
    }
    memset(pt_playlist, 0, sizeof(struct HLS_PLAYLIST));
    if (ps_url != NULL)
    {
        pt_playlist->ps_url = strdup(ps_url);
        pt_playlist->ps_hostUrl = getHostUrl(ps_url);
    }
    pt_playlist->entry_info.type = TYPE_PLAYLIST;
    pt_playlist->startToPlay = FALSE;
    pt_playlist->dynamic_update_count = 0;
    
    /* absence of EXT-X-ENDLIST tag and no presence of any EXT-X-MEDIA_SEQUENCE tag */
    pt_playlist->playlistContentType= TYPE_NONE;

    /* allocate header buffer */
    pt_playlist->url_header.pac_buffer = (char *) malloc(4096);

    if ( !pt_playlist->url_header.pac_buffer )
    {
        LOG(0, "allocate header buffer fail\n");
        free_playlist(pt_playlist);
        return NULL;
    }
    memset(pt_playlist->url_header.pac_buffer, 0, 4096);
    pt_playlist->url_header.i4_total_size = 4096;
    pt_playlist->url_header.i4_used_size = 0;
    pt_playlist->url_header.pac_buffer[0]='\0';

    /* allocate content buffer */
    pt_playlist->url_content.pac_buffer = (char *) malloc(100 * 1024);

    if ( !pt_playlist->url_content.pac_buffer )
    {
        LOG(0, "allocate content buffer fail\n");
        free_playlist (pt_playlist);
        return NULL;
    }
    memset(pt_playlist->url_content.pac_buffer, 0, 100 * 1024);
    pt_playlist->url_content.i4_total_size = 100 * 1024;
    pt_playlist->url_content.i4_used_size = 0;
    pt_playlist->url_content.pac_buffer[0] = '\0';
    pt_playlist->next_program_date_time[0]='\0';
    
    return (pt_playlist);
} /* new_playlist */


/*------------------------------------------------------------------------
 * Name:  free_playlist
 *
 * Description: This API free pt_playlist
 *
 * Inputs:  pt_playlist
 *
 * Outputs: - playlist
 *
 *
 * Returns: - HLS_OK / HLS_FAIL
 -----------------------------------------------------------------------*/
int
free_playlist(struct HLS_PLAYLIST * pt_playlist)
{
    struct ENTRY_INFO * ptr = NULL;

    if(pt_playlist == NULL)
    {
        return (HLS_OK);
    }

    if(pt_playlist->ps_url)
    {
        free(pt_playlist->ps_url);
        pt_playlist->ps_url = NULL;
    }

    /* free header buffer */
    if(pt_playlist->url_header.pac_buffer != NULL)
    {
        free(pt_playlist->url_header.pac_buffer);
        pt_playlist->url_header.pac_buffer = NULL;
    }

    /*free content buffer */
    if(pt_playlist->url_content.pac_buffer != NULL)
    {
        free(pt_playlist->url_content.pac_buffer);
        pt_playlist->url_content.pac_buffer = NULL;
    }

    /* free media buffer */
    while(pt_playlist->pv_entry_head != NULL)
    {
        ptr = (ENTRY_INFO*)pt_playlist->pv_entry_head;
        if (ptr == NULL)
        {
            break;
        }

        pt_playlist->pv_entry_head = ptr->next;
        if(ptr->type == TYPE_STREAM)
        {
            free_stream_info( (struct STREAM_INFO *) ptr);
        }
        else if(ptr->type == TYPE_MEDIA)
        {
            free_media_info((struct MEDIA_INFO *)ptr);
        }
        else if(ptr->type == TYPE_KEY)
        {
            free_encrypt_key((struct KEY_INFO *)ptr);
        }
        else
        {
        	/* TYPE_DISCONTINUITY_TAG go this way */
            free((struct ENTRY_INFO *)ptr);
        }
    } /* end of while */

    if(g_media_file_curl)
    {
        curl_easy_cleanup(g_media_file_curl);
        g_media_file_curl = NULL;
    }

    /* free playlist */
    free(pt_playlist);

    pt_playlist = NULL;
    return (HLS_OK);

}	/* free_playlist */


/*------------------------------------------------------------------------
 * Name:  update_playlist
 *
 * Description: This API replace pt_dst_playlist data with pt_src_playlist data
 *
 * Inputs:  pt_dst_playlist - the playlist need to be update
 *			pt_src_playlist - the new playlist used to update pt_dst_playlist           
 *
 * Outputs: - pt_src_playlist
 *
 * Returns: - HLS_OK / HLS_FAIL
 *
 * Note: pt_src_playlist will be freed after this function
 -----------------------------------------------------------------------*/
int
update_playlist(struct HLS_PLAYLIST * pt_dst_playlist, struct HLS_PLAYLIST * pt_src_playlist)
{
    struct ENTRY_INFO * ptr = NULL;

    if(pt_dst_playlist == NULL)
    {
        return (HLS_OK);
    }

    if(pt_dst_playlist->ps_url)
    {
        free(pt_dst_playlist->ps_url);
        pt_dst_playlist->ps_url = NULL;
    }

    /* free header buffer */
    if(pt_dst_playlist->url_header.pac_buffer != NULL)
    {
        free(pt_dst_playlist->url_header.pac_buffer);
        pt_dst_playlist->url_header.pac_buffer = NULL;
    }

    /*free content buffer */
    if(pt_dst_playlist->url_content.pac_buffer != NULL)
    {
        free(pt_dst_playlist->url_content.pac_buffer);
        pt_dst_playlist->url_content.pac_buffer = NULL;
    }

    /* free media buffer */
    while(pt_dst_playlist->pv_entry_head != NULL)
    {
        ptr = (ENTRY_INFO*)pt_dst_playlist->pv_entry_head;
        if (ptr == NULL)
        {
            break;
        }

        pt_dst_playlist->pv_entry_head = ptr->next;
        if(ptr->type == TYPE_STREAM)
        {
            free_stream_info( (struct STREAM_INFO *) ptr);
        }
        else if(ptr->type == TYPE_MEDIA)
        {
            free_media_info((struct MEDIA_INFO *)ptr);
        }
        else if(ptr->type == TYPE_KEY)
        {
            free_encrypt_key((struct KEY_INFO *)ptr);
        }
        else
        {
            free((struct ENTRY_INFO *)ptr);
        }
    } /* end of while */

    memcpy(pt_dst_playlist , pt_src_playlist, sizeof(struct HLS_PLAYLIST));
    /* free playlist */
    free(pt_src_playlist);

    pt_src_playlist = NULL;
    return (HLS_OK);

}	/* update_playlist */

/*------------------------------------------------------------------------
 * Name:  free_stream_info
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  pt_stream_info
 *
 * Outputs: -
 *
 *
 * Returns: -
 -----------------------------------------------------------------------*/
void
free_stream_info(struct STREAM_INFO * pt_stream_info)
{
    if (pt_stream_info == NULL)
    {
        return;
    }
    if(pt_stream_info->ps_url)
    {
        free(pt_stream_info->ps_url);
        pt_stream_info->ps_url = NULL;
    }

    if(pt_stream_info->codecs)
    {
        free(pt_stream_info->codecs);
        pt_stream_info->codecs = NULL;
    }

    if(pt_stream_info->pt_playlist)
    {
        free_playlist(pt_stream_info->pt_playlist);
        pt_stream_info->pt_playlist = NULL;
    }

    free(pt_stream_info);
    pt_stream_info = NULL;
} /* end of free_stream_info */


/*------------------------------------------------------------------------
 * Name:  dump_stream_info
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  pt_stream_info
 *
 * Outputs: -
 *
 *
 * Returns: -
 -----------------------------------------------------------------------*/
void
dump_stream_info(struct STREAM_INFO * pt_stream_info)
{
    LOG(6, "---BEGIN STREAM_INFO---\n");
    if( (pt_stream_info == NULL) || (pt_stream_info->entry_info.type != TYPE_STREAM))
    {
        LOG(0, "pt_stream_info = %p, pt_stream_info->type = %x\n",
            pt_stream_info,
            pt_stream_info->entry_info.type);
        return;
    }
    LOG(5, "program stream url=%s\n", pt_stream_info->ps_url);
    LOG(5, "program stream bandwidth=%d\n", pt_stream_info->bandwidth);
    LOG(5, "program stream program_id=%d\n", pt_stream_info->program_id);
    LOG(5, "program stream codecs=%s\n", pt_stream_info->codecs);
    LOG(5, "program stream resolution %dx%d\n",
        pt_stream_info->resolution.x,
        pt_stream_info->resolution.y);
    LOG(5, "---END STREAM_INFO---\n");
    LOG(5, "\n");
} /* end of dump_stream_info */

/*------------------------------------------------------------------------
 * Name:  free_char_buffer
 *
 * Description: 
 *
 * Inputs:  t_char_buffer
 *
 * Outputs: -
 *
 *
 * Returns: -
 *add by kun.chen:free char buffer
 -----------------------------------------------------------------------*/
static void free_char_buffer(CHAR_BUFFER &t_char_buffer)
{
    t_char_buffer.i4_total_size = 0;
    t_char_buffer.i4_used_size = 0;
    if (NULL != t_char_buffer.pac_buffer)
    {
        free(t_char_buffer.pac_buffer);
        t_char_buffer.pac_buffer = NULL;
    }
}

/*------------------------------------------------------------------------
 * Name:  free_media_char_buffer
 *
 * Description: 
 *
 * Inputs:  pt_media_info
 *
 * Outputs: -
 *
 *
 * Returns: -
 *add by kun.chen:free char buffer
 -----------------------------------------------------------------------*/
static void free_media_char_buffer(MEDIA_INFO *pt_media_info)
{
    if (NULL == pt_media_info)
        return ;
    free_char_buffer(pt_media_info->url_content);
    free_char_buffer(pt_media_info->url_header);
    pt_media_info->content_length = 0;
    pt_media_info->status = MEDIA_FILE_NONE;
    return;
}

/*------------------------------------------------------------------------
 * Name:  free_some_media_data
 *
 * Description: 
 *
 * Inputs:  pt_media_info
 *
 * Outputs: -
 *
 *
 * Returns: -
 *add by kun.chen:free char buffer
 -----------------------------------------------------------------------*/
int free_some_media_data(HLS_PLAYLIST * pt_hls_list, int iCount)
{
    ENTRY_INFO * ent = (ENTRY_INFO*)pt_hls_list->pv_entry_head;
    int i = 0;
    while((ent != NULL) && (i < iCount))
    {
        if (ent->type == TYPE_MEDIA)
        {
            MEDIA_INFO* minfo = ( MEDIA_INFO*)ent;
            if (minfo->status == MEDIA_FILE_CONSUMED || 
            	  minfo->status == MEDIA_FILE_CACHE_FAIL  ||
            	  minfo->status == MEDIA_FILE_DOWNLOAD_CANCEL)
            	  
            {
                i++;
                LOG(5, "free media file %s\n", minfo->ps_url);
                free_media_char_buffer(minfo);
            }
        }
        ent = ent->next;
    }

    return i;
}
/*------------------------------------------------------------------------
 * Name:  free_media_info
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  pt_media_info
 *
 * Outputs: -
 *
 *
 * Returns: -
 -----------------------------------------------------------------------*/
void
free_media_info(struct MEDIA_INFO *pt_media_info)
{
    
    if (pt_media_info == NULL)
    {
        return;
    }

    if(pt_media_info->ps_url)
    {
        free((void*)pt_media_info->ps_url);
        pt_media_info->ps_url = NULL;
    }

    if(pt_media_info->title)
    {
        free((void*)pt_media_info->title);
        pt_media_info->title = NULL;
    }

    if(pt_media_info->ext_x_program_date_time)
    {
        free((void*)pt_media_info->ext_x_program_date_time);
        pt_media_info->ext_x_program_date_time = NULL;
    }
    free_media_char_buffer(pt_media_info);

    free(pt_media_info);
    pt_media_info = NULL;
} /* end of free_media_info */


/*------------------------------------------------------------------------
 * Name:  dump_media_info
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  pt_media_info
 *
 * Outputs: -
 *
 *
 * Returns: -
 -----------------------------------------------------------------------*/
void
dump_media_info(struct MEDIA_INFO *pt_media_info)
{
    LOG(5, "---BEGIN MEDIA_INFO---\n");

    if ((pt_media_info == NULL) || (pt_media_info->entry_info.type != TYPE_MEDIA))
    {
        LOG(0, "pt_media_info = %p, pt_media_info->type = %x\n",
            pt_media_info,
            pt_media_info->entry_info.type);
        return;
    }
    LOG(5, "media file sequence_number = %d\n", pt_media_info->sequence_number);
    LOG(5, "media file url = %s\n", pt_media_info->ps_url);
    LOG(5, "media file title = %s\n", pt_media_info->title);
    LOG(5, "media file ext_x_program_date_time = %s\n", pt_media_info->ext_x_program_date_time);
    LOG(5, "media file duration = %d\n", pt_media_info->duration);
    if (pt_media_info->pt_media_key)
    {
        LOG(5, "media file with encryption key\n");
    }
    LOG(5, "media file status = %d\n", pt_media_info->status);
    
    LOG(5, "---END MEDIA_INFO---\n");
    LOG(5, "\n");
}	/* end of dump_media_info */


/*------------------------------------------------------------------------
 * Name:  read_line
 *
 * Description: read a line from soruce and duplicate it to dest
 *
 * Inputs:  src -
 *          dest -
 *
 * Outputs: - ptr -
 *
 *
 * Returns: - playlist
 -----------------------------------------------------------------------*/
char *
read_line(char * src, char * dest)
{
    char * ptr = NULL;

    if (src == NULL)
    {
        return (NULL);
    }

    if ( (ptr = strstr(src, delim)) == NULL)
    {
        LOG(6, "end of file\n");
        return (NULL);
    }

    if ( (ptr-src) > LINE_BUF_SIZE)
    {
        LOG(0, "(ptr-src = %d) > LINE_BUF_SIZE\n", (ptr-src));
    }
    if (dest == NULL)
    {
        LOG(0, "dest NULL\n");
        return NULL;
    }

    strncpy(dest, src, ptr-src);

    dest[ptr-src]='\0';
    if ((dest[ptr-src -1] == '\n') || (dest[ptr-src -1] == '\r'))
        dest[ptr-src - 1]='\0';
    if (dest[ptr-src -2] == '\r')
        dest[ptr-src - 2]='\0';

    LOG(8, "ptr= %p, src=%p, dest=%p, line=%s\n", ptr, src, dest, dest);

    return (ptr+strlen(delim));
} /* end of read_line */


/*------------------------------------------------------------------------
 * Name:  parse_media_info
 *
 * Description: #EXTINF:<duration>,<title>
 *             ex: #EXTINF:5,xxx.mp3
 *						 HTTP Live Streaming draft-pantos-http-live-streaming-04,
 *             Section 3.1
 *
 * Inputs:  pt_playlist
 *					data
 *
 * Outputs: -
 *
 *
 * Returns: - media file info
 -----------------------------------------------------------------------*/
static int
parse_media_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                 char *content)
{
    char * ps_start = NULL;
    char * ps_end = NULL;
    char * ptr;
    char * line = NULL;
    char buf[20];
    int i;
    struct MEDIA_INFO * pt_media_info = NULL;

    LOG(6, "Enter %s\n", __FUNCTION__);

    if ((pt_playlist == NULL) || (data == NULL) || (content == NULL))
    {
        LOG(0, "pt_playlist = %p, data = %p, content = %p\n", pt_playlist, data, content );
        return (HLS_FAIL);
    }

    if ((ptr = strstr(data, "#EXTINF:")) == NULL)
    {
        LOG(0, "not #EXTINF: tag: %s\n", data);
        return (HLS_FAIL);
    }

    pt_media_info = new_media_info();
    if (NULL == pt_media_info)
    {
        return (HLS_FAIL);
    }

    pt_media_info->switchStream = false;

    /* get media file duration  */
    ps_start = strstr(data, ":");
    if ((ps_end = strstr(ptr, ",")) != NULL)
    {    
        
        if ( (ps_end - ps_start) > 10)
        {
            LOG(0, "duration overflow %s\n", data);
            return (HLS_FAIL);
        }

        for(i = 0, ptr = (ps_start + 1) ; (i < 10) || (ptr < ps_end ); i++, ptr++)
        {
            buf[i] = *ptr;
        }
        buf[i] = '\0';

        pt_media_info->duration = atof(buf);        
    
        /* get media file title if exist */
        ps_start = ps_end + 1;
        if ((ptr = strtok(ps_start, delim)) != NULL)
        {
            pt_media_info->title = strdup(ps_start);
        }        
    }
    else
    {
        pt_media_info->duration = atof(ps_start+1);
    }

    /* if #EXTINF: tag prefix with #EXT-X-PROGRAM-DATE-TIME tag */
    if(strlen(pt_playlist->next_program_date_time) != 0)
    {
        pt_media_info->ext_x_program_date_time = (char *)malloc(sizeof(char) * 20+1);
        if(pt_media_info->ext_x_program_date_time)
        {
            strncpy((char*)pt_media_info->ext_x_program_date_time,
                    (char*)pt_playlist->next_program_date_time,
                    20);
            pt_playlist->next_program_date_time[0] = '\0';
        }		
    }
    /* get media file url */
    line = (char*)malloc(sizeof(char) * LINE_BUF_SIZE);
    content = read_line(content, line);

    /* for bivl compatible */
    if ( (ptr = strstr(line, http_live_streaming_tag_function[4].tag)) != NULL)
    {
    	http_live_streaming_tag_function[4].parse_function(pt_playlist, line, content);
    	memset (line, 0, LINE_BUF_SIZE);
        content = read_line(content, line);
    }
    /* err handling that there is  #EXTINF: but no url follow it - tiffany */
    if ( (ptr = strstr(line, http_live_streaming_tag_function[1].tag)) != NULL)
    {
        LOG(0, "No media file follow this #EXTINF tag. Continue parse next media file!! \n"); 
		free(line);
		free(pt_media_info);
        return (HLS_OK);
    }
    if ( (ptr = strstr(line, "https://")) != NULL)
    {
        pt_media_info->ps_url = strdup(line);

    }
    else if ( (ptr = strstr(line, "http://")) != NULL)
    {
        pt_media_info->ps_url = strdup(line);
    }
    else
    {
    	if (*line == '/')
    	{
    		int iPos;
    		/*truncate the host url to get server's host name/IP*/
			for (iPos = strlen("https://") + 1; iPos < strlen(pt_playlist->ps_hostUrl); iPos++)
				if ( *(pt_playlist->ps_hostUrl + iPos) == '/')
					break;
			pt_media_info->ps_url = (char *) malloc(strlen(line) + iPos + 1);
			memcpy(pt_media_info->ps_url, pt_playlist->ps_hostUrl, iPos);
			memcpy(pt_media_info->ps_url + iPos, line, strlen(line));
	        pt_media_info->ps_url[strlen(line) + iPos] = 0;
    	}
		else
		{
	    	pt_media_info->ps_url = (char *) malloc(strlen(line) + strlen(pt_playlist->ps_hostUrl)+2);
	        sprintf(pt_media_info->ps_url, "%s/%s", pt_playlist->ps_hostUrl, line);
	        pt_media_info->ps_url[strlen(line) + strlen(pt_playlist->ps_hostUrl)+1] = 0;
		} 	
    }

    /* link media file to playlist */
    pt_media_info->entry_info.prev = (ENTRY_INFO*)pt_playlist->pv_entry_tail;
    if (pt_playlist->pv_entry_tail)
    {
        ((struct ENTRY_INFO *)pt_playlist->pv_entry_tail)->next =
            (struct ENTRY_INFO *) pt_media_info;
    }
    pt_playlist->pv_entry_tail = (void *)pt_media_info;
    if (pt_playlist->pv_entry_head == NULL)
    {
        pt_playlist->pv_entry_head = pt_playlist->pv_entry_tail;
    }

    pt_media_info->sequence_number =
        pt_playlist->ext_x_media_sequence + pt_playlist->i4_numOfEntry;

    pt_playlist->i4_numOfEntry++;
    pt_playlist->duration += pt_media_info->duration;
    if(pt_playlist->pt_key != NULL)
    {
        pt_media_info->pt_media_key = pt_playlist->pt_key;
    }

    free(line);
    return (HLS_OK);
} /* end of parse_media_info */


/*------------------------------------------------------------------------
 * Name:  parse_target_duration_info
 *
 * Description: #EXT-X-TARGETDURATION:<s>
 *             ex: #EXT-X-TARGETDURATION:15
 *						 HTTP Live Streaming draft-pantos-http-live-streaming-04,
 *             Section 3.2.1
 *
 * Inputs:  pt_playlist
 *					data
 *
 * Outputs: -
 *
 *
 * Returns: - media file info
 -----------------------------------------------------------------------*/
static int
parse_target_duration_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                           char *content)
{
    char * ptr = NULL;

    LOG(6, "Enter %s\n", __FUNCTION__);

    if ((pt_playlist == NULL) || (data == NULL) || (content == NULL))
    {
        LOG(0, "pt_playlist = %p, data = %p, content = %p\n", pt_playlist, data, content );
        return (HLS_FAIL);
    }

    if ((ptr = strstr(data, "#EXT-X-TARGETDURATION:")) == NULL)
    {
        LOG(0, "not #EXT-X-TARGETDURATION: tag: %s", data);
        return (HLS_FAIL);
    }

    ptr = strstr(data, ":");
    if ((pt_playlist->ext_x_targetduration) != 0)
    {
        LOG(5, "#EXT-X-TARGETDURATION appear more than once\n");
    }

    pt_playlist->ext_x_targetduration = atoi((ptr+1));
    return (HLS_OK);
} /* end of parse_target_duration_info */


/*------------------------------------------------------------------------
 * Name:  parse_media_sequence_info
 *
 * Description: #EXT-X-MEDIA-SEQUENCE:<number>
 *             ex: #EXT-X-MEDIA-SEQUENCE:2680
 *						 HTTP Live Streaming draft-pantos-http-live-streaming-04,
 *             Section 3.2.2, Section 6,3,2, Section 6.3.5
 *
 * Inputs:  pt_playlist
 *					data
 *
 * Outputs: -
 *
 *
 * Returns: - media file info
 -----------------------------------------------------------------------*/
static int
parse_media_sequence_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                          char *content)
{
    char * ptr = NULL;

    LOG(6, "Enter %s\n", __FUNCTION__);

    if ((pt_playlist == NULL) || (data == NULL) || (content == NULL) )
    {
        LOG(0, "pt_playlist = %p, data = %p, content = %p\n", pt_playlist, data, content );
        return (HLS_FAIL);
    }

    if ((ptr = strstr(data, "#EXT-X-MEDIA-SEQUENCE:")) == NULL)
    {
        LOG(0, "not #EXT-X-MEDIA-SEQUENCE: tag: %s", data);
        return (HLS_FAIL);
    }

    ptr = strstr(data, ":");
    if ((pt_playlist->ext_x_media_sequence) != 0)
    {
        LOG(6, "#EXT-X-MEDIA-SEQUENCE: appear more than once\n");
    }

    pt_playlist->ext_x_media_sequence = atoi((ptr+1));
    if (pt_playlist->playlistContentType == TYPE_NONE)
    {
        pt_playlist->playlistContentType = TYPE_EVENT_SLIDING;
    }
    return (HLS_OK);
} /* end of parse_media_sequence_info */


/*------------------------------------------------------------------------
 * Name:  parse_encrypt_key
 *
 * Description: #EXT-X-KEY:METHOD=<method>[,URI="<URI>"][,IV=<IV>]
 *             ex: #EXT-X-KEY:METHOD=AES-128,URI="https://priv.example.com/key.php?r=52"
 *						 HTTP Live Streaming draft-pantos-http-live-streaming-04,
 *             Section 3.2.3, Section 5, Section 5.2, Section 6.2.3, Section 6.3.6
 *
 * Inputs:  pt_playlist
 *					data
 *
 * Outputs: -
 *
 *
 * Returns: - media file info
 -----------------------------------------------------------------------*/
static int
parse_encrypt_key(struct HLS_PLAYLIST * pt_playlist, char * data,
                  char *content)
{
    char * ptr = NULL;
    char * ps_start, *ps_end;
    struct KEY_INFO * pt_key = NULL;

    LOG(6, "Enter %s\n", __FUNCTION__);

    if ((pt_playlist == NULL) || (data == NULL) || (content == NULL) )
    {
        LOG(0, "pt_playlist = %p, data = %p, content = %p\n", pt_playlist, data, content );
        return (HLS_FAIL);
    }

    if ((ptr = strstr(data, "#EXT-X-KEY:")) == NULL)
    {
        LOG(0, "not #EXT-X-KEY: tag: %s", data);
        return (HLS_FAIL);
    }

    if ((ptr = strstr(data, "METHOD=")) == NULL)
    {
        LOG(0, "no METHOD= tag\n");
        return (HLS_FAIL);
    }

    if ((pt_key = new_encrypt_key(NULL)) == NULL)
    {
        LOG(0, "%s, new_encrypt_key fail",__FUNCTION__);
        return (HLS_FAIL);
    }

    /* get encrypt method */
    if ((ptr = strstr(data, "AES-128")) == NULL)
    {
        if ((ptr = strstr(data, "NONE")) == NULL)
        {
            LOG(0, "incorrect encryption method %s\n", data);
            return (HLS_FAIL);
        }
        else
        {
            pt_key->method = KEY_INFO_METHOD_NONE;
        }
    }
    else
    {
        pt_key->method = KEY_INFO_METHOD_AES_128;
    }

    /* key URI attribute MUST be present */
    if ((ptr = strstr(data, "URI=")) == NULL)
    {
        free_encrypt_key(pt_key);
        LOG(0, "no key uri info %s", data);
        return (HLS_FAIL);
    }

    ps_start = ptr + strlen("URI=\"");
    if ((ps_end = strstr(ps_start, "\"")) == NULL)
    {
        free_encrypt_key(pt_key);
        LOG(0, "incurrect key uri info %s", ptr);
        return (HLS_FAIL);
    }

    pt_key->ps_url = (char*)malloc( sizeof(char) * (ps_end-ps_start+1));
    strncpy(pt_key->ps_url, ps_start, ps_end-ps_start);
    pt_key->ps_url[ps_end-ps_start]='\0';

    /* IV */
    memset(pt_key->iv, 0, sizeof(pt_key->iv));
    if ((ptr = strstr(data, "IV=")) == NULL)
    {
        LOG(6, "no IV info %s, use sequence number "
            "of the media file as the IV when encryptiong"
            " or decrypting that media file\n", data);
        pt_key->bHaveIV = false;
    }
    else
    {
        pt_key->bHaveIV = true;
        ps_start = ptr+strlen("IV=");
        chars_to_hex((unsigned char *)(ps_start + 2), (unsigned char *)pt_key->iv, KEY_INFO_METHOD_AES_128 * 2);
    }

    /* try to get encrypt key */
    if( request_encrypt_key(pt_key) == HLS_FAIL)
    {
        LOG(0, "%s, request_encrypt_key %s fail\n", __FUNCTION__, pt_key->ps_url);
        pt_key->bDownError = true;
    }
    else
    {
        pt_key->bDownError = false;/*download success*/

        /*copy the key*/
        memcpy(pt_key->key, pt_key->url_content.pac_buffer, KEY_INFO_METHOD_AES_128);
    }

    /* link key file to playlist */
    pt_key->entry_info.prev = (ENTRY_INFO*)pt_playlist->pv_entry_tail;
    if (pt_playlist->pv_entry_tail)
    {
        ((struct ENTRY_INFO *)pt_playlist->pv_entry_tail)->next =
            (struct ENTRY_INFO *)pt_key;
    }
    pt_playlist->pv_entry_tail = (void *)pt_key;
    if (pt_playlist->pv_entry_head == NULL)
    {
        pt_playlist->pv_entry_head = pt_playlist->pv_entry_tail;
    }
    pt_playlist->pt_key =  pt_key;

    if (pt_key->bDownError == true)
        return  HLS_FAIL;   
    
    return (HLS_OK);
} /* end of parse_encrypt_key */


/*------------------------------------------------------------------------
 * Name:  dump_encrypt_key
 *
 * Description: dump encrypt key info
 *
 *
 * Inputs:  pt_playlist
 *					data
 *
 * Outputs: -
 *
 *
 * Returns: - media file info
 -----------------------------------------------------------------------*/
void
dump_encrypt_key(struct KEY_INFO * pt_key)
{
    int i;

    LOG(5, "---BEGIN KEY_INFO---\n");
    if (pt_key == NULL || pt_key->entry_info.type != TYPE_KEY)
    {
        LOG(0, "pt_key = %p \n", pt_key);
        return;
    }
    LOG(5, "METHOD=%x\n", pt_key->method);
    LOG(5, "URI=%s\n", pt_key->ps_url);
    LOG(5, "KEY=");
    for (i = 0; i < KEY_INFO_METHOD_AES_128; i++)
    {
        LOG(9, "%x ", pt_key->key[i]);
    }
    LOG(5, "\n");

    LOG(5, "IV=");
    for (i = 0; i < KEY_INFO_METHOD_AES_128; i++)
    {
        LOG(9, "%02x ", pt_key->iv[i]);
    }
    LOG(5, "\n");
#if 0/*may be null*/
    LOG(6, "http response header data = %s \n",
        pt_key->url_header.pac_buffer);
    LOG(6, "http response content = ");
    for (i = 0; i < KEY_INFO_METHOD_AES_128; i++)
    {
        LOG(9, "%02x ", (unsigned char)pt_key->url_content.pac_buffer[i]);
    }
#endif
    LOG(5, "\n---END KEY_INFO---\n");
    LOG(5, "\n");
} /* end of dump_encrypt_key */


/*------------------------------------------------------------------------
 * Name:  parse_program_date_time_info
 *
 * Description: #EXT-X-PROGRAM-DATE-TIME:<YYY-MM-DDThh:mm:ssZ>
 *              ex:
 *						 HTTP Live Streaming draft-pantos-http-live-streaming-04,
 *             Section 3.2.4, Section 6.2.1 and Section 6.3.3
 *
 * Inputs:  pt_playlist
 *					data
 *
 * Outputs: -
 *
 * Returns: -
 *
 * Note: data will be free by caller function, do not directly use
 *       pointer point to data for info store
 -----------------------------------------------------------------------*/
static int
parse_program_date_time_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                             char * content)
{
    char * ptr;

    LOG(6, "Enter %s\n", __FUNCTION__);

    if ((pt_playlist == NULL) || (data == NULL) || (content == NULL))
    {
        LOG(0, "pt_playlist = %p, data = %p, content = %p\n", pt_playlist, data, content );
        return (HLS_FAIL);
    }

    if ((ptr = strstr(data, "#EXT-X-PROGRAM-DATE-TIME:")) == NULL)
    {
        LOG(0, "not #EXT-X-PROGRAM-DATE-TIME: tag: %s ", data);
        return (HLS_FAIL);
    }

    ptr += strlen("#EXT-X-PROGRAM-DATE-TIME:");
    strncpy(pt_playlist->next_program_date_time, ptr, 20);

    return (HLS_OK);
}/* end of parse_program_date_time_info */


/*------------------------------------------------------------------------
 * Name:  parse_allow_cache_info
 *
 * Description: #EXT-X-ALLOW-CACHE:<YES|NO>
 *              ex: #EXT-X-ALLOW-CACHE:YES
 *						 HTTP Live Streaming draft-pantos-http-live-streaming-04,
 *             Section 3.2.5, Section 6.3.3
 *
 * Inputs:  pt_playlist
 *					data
 *
 * Outputs: -
 *
 *
 * Returns: -
 *
 * Note: data will be free by caller function, do not directly use
 *       pointer point to data for info store
 -----------------------------------------------------------------------*/
static int
parse_allow_cache_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                       char * content)
{
    char * ptr = NULL;

    LOG(6, "Enter %s\n", __FUNCTION__);

    if ((pt_playlist == NULL) || (data == NULL) || (content == NULL))
    {
        LOG(0, "pt_playlist = %p, data = %p, content = %p\n", pt_playlist, data, content );
        return (HLS_FAIL);
    }

    if ((ptr = strstr(data, "#EXT-X-ALLOW-CACHE:")) == NULL)
    {
        LOG(0, "not #EXT-X-ALLOW-CACHE: tag: %s ", data);
        return (HLS_FAIL);
    }

    if ( (ptr = strstr(data,"YES")))
    {
        pt_playlist->ext_x_allow_cache = TRUE;
    }

    if ( (ptr = strstr(data,"NO")))
    {
        pt_playlist->ext_x_allow_cache = FALSE;
    }

    return (HLS_OK);
} /* end of parse_allow_cache_info */


/*------------------------------------------------------------------------
 * Name:  parse_endlist_info
 *
 * Description: #EXT-X-ENDLIST
 *              ex: #EXT-X-ENDLIST
 *						 HTTP Live Streaming draft-pantos-http-live-streaming-04,
 *             Section 3.2.6
 *
 * Inputs:  pt_playlist
 *					data
 *
 * Outputs: -
 *
 *
 * Returns: -
 *
 * Note: data will be free by caller function, do not directly use
 *       pointer point to data for info store
 -----------------------------------------------------------------------*/
static int
parse_endlist_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                   char * content )
{
    char * ptr = NULL;

    LOG(6, "Enter %s\n", __FUNCTION__);

    if ((pt_playlist == NULL) || (data == NULL) || (content == NULL))
    {
        LOG(0, "pt_playlist = %p, data = %p, content = %p\n", pt_playlist, data, content );
        return (HLS_FAIL);
    }

    if ((ptr = strstr(data, "#EXT-X-ENDLIST")) == NULL)
    {
        LOG(0, "not #EXT-X-ENDLIST tag: %s \n", data);
        return (HLS_FAIL);
    }
    if (pt_playlist->ext_x_endlist)
    {
        LOG(0, "duplicate #EXT-X-ENDLIST tag : %s\n", data);
        return (HLS_FAIL);
    }

    pt_playlist->ext_x_endlist = 1;

    pt_playlist->playlistContentType= TYPE_VOD;
    
    return (HLS_OK);

} /* end of parse_endlist_info */


/*------------------------------------------------------------------------
 * Name:  parse_stream_inf_info
 *
 * Description: #EXT-X-STREAM-INF
 *              ex: #EXT-X-STREAM-INF:[attribute=value][,attribute=value]*
 *						 HTTP Live Streaming draft-pantos-http-live-streaming-04,
 *             Section 3.2.7
 *
 * Inputs:  pt_playlist
 *					data
 *
 * Outputs: -
 *
 *
 * Returns: - media file info
 *
 * Note: data will be free by caller function, do not directly use
 *       pointer point to data for info store
 -----------------------------------------------------------------------*/
static int
parse_stream_inf_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                      char * content)
{
    char * ptr = NULL;
    char * ps_start = NULL;
    char * ps_end = NULL;
    char * line = NULL;
    struct STREAM_INFO * pt_stream_info = NULL;

    LOG(6, "Enter %s\n", __FUNCTION__);

    if ((pt_playlist == NULL) || (data == NULL) || (content == NULL) )
    {
        LOG(0, "pt_playlist = %p, data = %p, content = %p\n", pt_playlist, data, content );
        return (HLS_FAIL);
    }

    if ((ptr = strstr(data, "#EXT-X-STREAM-INF:")) == NULL)
    {
        LOG(0, "not #EXT-X-STREAM-INF: tag: %s ", data);
        return (HLS_FAIL);
    }

    if ( (pt_stream_info = (STREAM_INFO*)malloc(sizeof(struct STREAM_INFO))) == NULL)
    {
        LOG(0, "malloc fail\n");
        return (HLS_FAIL);
    }
    memset(pt_stream_info, 0, sizeof(struct STREAM_INFO));
    pt_stream_info->entry_info.type = TYPE_STREAM;
    if ( (line = (char*)malloc( sizeof(char ) * LINE_BUF_SIZE)) == NULL)
    {
        goto fail_handle;
    }
    /***********************/
    /* check BANDWIDTH tag */
    /***********************/
    LOG(6, "check BANDWIDTH tag\n");

    ptr = data;
    if ( (ps_start = strstr(ptr, "BANDWIDTH=")) != NULL)
    {
        ps_start = ps_start + strlen("BANDWIDTH=");
        if ( (ps_end = strstr(ps_start, ",")) == NULL)
        {
            ps_end = strstr(ps_start, delim);
        }
        if (ps_end != NULL)
        {
            strncpy(line, ps_start, ps_end-ps_start);
            line[ps_end-ps_start]='\0';
        }
        else
        {
            strncpy(line, ps_start, strlen(ps_start));
            line[strlen(ps_start)]='\0';
        }
        pt_stream_info->bandwidth = atoi(line);
        pt_stream_info->attr_flag |= STREAM_ATTR_BANDWIDTH;
    } /* end of if */

    /************************/
    /* check PROGRAN-ID tag */
    /************************/
    LOG(6, "check PROGRAN-ID tag\n");

    if ( (ps_start = strstr(ptr, "PROGRAM-ID=")) != NULL)
    {
        if ( (ps_end = strstr(ps_start, ",")) == NULL)
        {
            if (( ps_end = strstr(ps_start, delim)) == NULL)
            {
                goto fail_handle;
            }
        }

        ps_start = ps_start + strlen("PROGRAM-ID=");
        strncpy(line, ps_start, ps_end-ps_start);
        line[ps_end-ps_start]='\0';
        pt_stream_info->program_id = atoi(line);

        pt_stream_info->attr_flag |= STREAM_ATTR_PROGRAM_ID;
    } /* end of if */

    /********************/
    /* check CODECS tag */
    /********************/
    LOG(6, "check CODECS tag\n");

    if ( (ps_start = strstr(ptr, "CODECS=\"")) != NULL)
    {
        ps_start = ps_start + strlen("CODECS=\"");
        if ( (ps_end = strstr(ps_start, "\"")) == NULL)
        {
            goto fail_handle;
        }
        pt_stream_info->codecs = (char*)malloc(sizeof(char) * (ps_end-ps_start+1));
        if (pt_stream_info->codecs == NULL)
        {
            goto fail_handle;
        }
        strncpy(pt_stream_info->codecs, ps_start, ps_end-ps_start);
        pt_stream_info->codecs[ps_end-ps_start]='\0';

        pt_stream_info->attr_flag |= STREAM_ATTR_CODEC;
    } /* end of if */

    /************************/
    /* check RESOLUTION tag */
    /************************/
    LOG(6, "check RESOLUTION tag\n");

    if ( (ps_start = strstr(ptr, "RESOLUTION=")) != NULL)
    {
        /* get x */
        if ( (ps_end = strstr(ps_start, "x")) == NULL)
        {
            goto fail_handle;
        }
        ps_start = ps_start + strlen("RESOLUTION=");
        strncpy(line, ps_start, ps_end-ps_start);
        line[ps_end-ps_start]='\0';
        pt_stream_info->resolution.x = atoi(line);

        /* get y */
        ps_start = ps_end;
        if ( (ps_end = strstr(ps_start, ",")) == NULL)
        {
            ps_end = strstr(ps_start, delim);
        }
        if (ps_end == NULL)
        {
            ps_end = data + strlen(data);
        }
        strncpy(line, ps_start, ps_end-ps_start);
        line[ps_end-ps_start]='\0';
        pt_stream_info->resolution.y = atoi(line);

        pt_stream_info->attr_flag |= STREAM_ATTR_RESOLUTION;
    } /* end of if */

    /**************/
    /* get ps url */
    /**************/
//    line = malloc(sizeof(char) * LINE_BUF_SIZE);
    read_line(content, line);
    if ( (ptr = strstr(line, "https://")) != NULL)
    {
        pt_stream_info->ps_url = strdup(line);
    }
    else if ( (ptr = strstr(line, "http://")) != NULL)
    {
        pt_stream_info->ps_url = strdup(line);
    }
    else
    {    	
    	pt_stream_info->ps_url = (char *) malloc(strlen(line) + strlen(pt_playlist->ps_hostUrl) + 2);
        sprintf(pt_stream_info->ps_url, "%s/%s", pt_playlist->ps_hostUrl, line);
        pt_stream_info->ps_url[strlen(line) + strlen(pt_playlist->ps_hostUrl) + 1] = 0;
        LOG(8, "%s: %s\n", __FUNCTION__, pt_stream_info->ps_url);
    }

fail_handle:

    /* if at least one attribute correct */
    if (pt_stream_info->attr_flag)
    {
        /* link stream info to playlist */
        if (pt_playlist->pv_entry_tail)
        {
            ((struct ENTRY_INFO *)pt_playlist->pv_entry_tail)->next =
                (struct ENTRY_INFO *)pt_stream_info;
        }
        pt_playlist->pv_entry_tail = (void *)pt_stream_info;
        if (pt_playlist->pv_entry_head == NULL)
        {
            pt_playlist->pv_entry_head = pt_playlist->pv_entry_tail;
        }
        pt_playlist->i4_numOfEntry++;
    }
    else
    {
        free(pt_stream_info);
        free(line);
        return (HLS_FAIL);
    }
    free(line);

    pt_playlist->playlistContentType= TYPE_PROGRAM;
    return (HLS_OK);

} /* end of parse_stream_inf_info */


/*------------------------------------------------------------------------
 * Name:  parse_discontinuity_info
 *
 * Description: #EXT-X-DISCONTINUITY
 *             The client must be prepared to reset its parser(s) and
 *             decoder(s) before playing a medai file that is preceded
 *             by EXT-X-DISCONTINUITY tag
 *						 HTTP Live Streaming draft-pantos-http-live-streaming-04,
 *             Section 3.2.8, Section 4, Section 6.2.1, and Section 6.3.3
 *
 * Inputs:  pt_playlist
 *					data
 *
 * Outputs: -
 *
 *
 * Returns: -
 *
 * Note: data will be free by caller function, do not directly use
 *       pointer point to data for info store
 -----------------------------------------------------------------------*/
static int
parse_discontinuity_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                         char * content)
{
    struct ENTRY_INFO * pt_entry_info = NULL;

    LOG(6, "Enter %s\n", __FUNCTION__);

    if ((pt_playlist == NULL) || (data == NULL) || (content == NULL) )
    {
        LOG(0, "pt_playlist = %p, data = %p, content = %p\n", pt_playlist, data, content );
        return (HLS_FAIL);
    }

    pt_entry_info = (ENTRY_INFO*)malloc(sizeof(struct ENTRY_INFO));
    memset(pt_entry_info, 0, sizeof(struct ENTRY_INFO));
    pt_entry_info->type = TYPE_DISCONTINUITY_TAG;

    /* link discontinuity tag to playlist */
    pt_entry_info->prev = (ENTRY_INFO*)pt_playlist->pv_entry_tail;
    if (pt_playlist->pv_entry_tail)
    {
        ((struct ENTRY_INFO *)pt_playlist->pv_entry_tail)->next = pt_entry_info;
    }
    pt_playlist->pv_entry_tail = (void *)pt_entry_info;
    if (pt_playlist->pv_entry_head == NULL)
    {
        pt_playlist->pv_entry_head = pt_playlist->pv_entry_tail;
    }

    return (HLS_OK);
} /* end of parse_discontinuity_info */


/*------------------------------------------------------------------------
 * Name:  parse_version_info
 *
 * Description: #EXT-X-VERSION
 *              ex: #EXT-X-VERSION:4
 *						 HTTP Live Streaming draft-pantos-http-live-streaming-04,
 *             Section 3.2.9
 * Inputs:  pt_playlist
 *					data
 *
 * Outputs: -
 *
 *
 * Returns: - media file info
 *
 * Note: data will be free by caller function, do not directly use
 *       pointer point to data for info store
 -----------------------------------------------------------------------*/
static int
parse_version_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                   char * content)
{
    char * ptr;

    LOG(6, "Enter %s\n", __FUNCTION__);

    if ((pt_playlist == NULL) || (data == NULL) || (content == NULL) )
    {
        LOG(0, "pt_playlist = %p, data = %p, content = %p\n", pt_playlist, data, content );
        return (HLS_FAIL);
    }

    if ((ptr = strstr(data, "#EXT-X-VERSION:")) == NULL)
    {
        LOG(0, "not #EXT-X-VERSION: tag: %s ", data);
        return (HLS_FAIL);
    }

    ptr += strlen("#EXT-X-VERSION:");
    pt_playlist->ext_x_version = atoi(ptr+1);

    return (HLS_OK);
} /* end of parse_version_info */


/*------------------------------------------------------------------------
 * Name:  parse_playlist_type_info
 *
 * Description: #EXT-X-PLAYLIST-TYPE
 *              ex: #EXT-X-PLAYLIST-TYPE:EVENT
 *						 HTTP Live Streaming draft-pantos-http-live-streaming-06,
 *             Section 3.2.9
 * Inputs:  pt_playlist
 *		    data
 *			content 
 *
 * Outputs: -
 *
 *
 * Returns: - media file info
 *
 * Note: data will be free by caller function, do not directly use
 *       pointer point to data for info store
 -----------------------------------------------------------------------*/
static int
parse_playlist_type_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                   char * content)
{

    char * ptr, *vPtr;

    if ((pt_playlist == NULL) || (data == NULL) || (content == NULL) )
    {
        LOG(0, "pt_playlist = %p, data = %p, content = %p\n", pt_playlist, data, content );
        return (HLS_FAIL);
    }

    if ((ptr = strstr(data, "#EXT-X-PLAYLIST-TYPE")) == NULL)
    {
        LOG(0, "not #EXT-X-VERSION: tag: %s ", data);
        return (HLS_FAIL);
    }

    ptr += strlen("#EXT-X-PLAYLIST-TYPE:");
    if ( (vPtr = strstr(ptr, "EVENT")) != NULL)
		pt_playlist->playlistContentType= TYPE_EVENT_APPENDING;
    else if ( (vPtr = strstr(ptr, "VOD")) != NULL)
    {
		pt_playlist->playlistContentType= TYPE_VOD;
		pt_playlist->ext_x_endlist = 1;
    }
    else
    	LOG(0, "parse #EXT-X-PLAYLIST-TYPE, unknow playlist type!!\n");

	LOG(0, "Playlist Type %d\n", pt_playlist->playlistContentType);    
	
    return (HLS_OK);	
} /* end of parse_playlist_type_info */


/************************************/
/* new tag defined in HLS version 4 */
/************************************/
/*------------------------------------------------------------------------
 * Name:  parse_byterange_info
 *
 * Description: #EXT-X-BYTERANGE:<n>[@0]
 *              ex:
 *
 * Inputs:  pt_playlist
 *		    data
 *			content 
 *
 * Outputs: -
 *
 * Returns: - media file info
 *
 * Note: data will be free by caller function, do not directly use
 *       pointer point to data for info store
 -----------------------------------------------------------------------*/
static int
parse_byterange_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                   char * content)
{
    return (HLS_OK);	
} /* end of parse_byterange_info */


/*------------------------------------------------------------------------
 * Name:  parse_playlist_media_info
 *
 * Description: #EXT-X-MEDIA
 *              
 *             
 * Inputs:  pt_playlist
 *		    data
 *			content 
 *
 * Outputs: -
 *
 *
 * Returns: - media file info
 *
 * Note: data will be free by caller function, do not directly use
 *       pointer point to data for info store
 -----------------------------------------------------------------------*/
static int
parse_playlist_media_info(struct HLS_PLAYLIST * pt_playlist, char * data,
                   char * content)
{
    return (HLS_OK);
} /* end of parse_playlist_media_info */

/*------------------------------------------------------------------------
 * Name:  get_playlist_type
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  playlist
 *
 * Outputs: - type
 *
 *
 * Returns: - playlist type
 -----------------------------------------------------------------------*/
PLAYLIST_TYPE
get_playlist_type(struct HLS_PLAYLIST * pt_playlist)
{
    char *contentType = NULL;
    int i;

    LOG(5, "%s\n", __FUNCTION__);

    if (pt_playlist == NULL)
    {
        LOG(0, "NULL Playlist\n");
        return (PLAYLIST_UNKNOW);
    }

    /**********************/
    /* check sub filename */
    /**********************/
    for (i = 0; ; i++)
    {
        if( (pt_playlist->ps_url == NULL ) ||
                (playlist_sub_filename_type[i].name == NULL))
        {
            break;
        }
        if (strcasestr(pt_playlist->ps_url, playlist_sub_filename_type[i].name))
        {
            pt_playlist->playlistType = playlist_sub_filename_type[i].type;
            return (playlist_sub_filename_type[i].type);
        }
    } /* end of for */

    /**********************/
    /* check content type */
    /**********************/
    if (pt_playlist->url_header.pac_buffer != NULL)
    {
    	/* for URI use m3u8.php, we need download fist, then check playlist type */
    	if(0 != get_http_header(pt_playlist))
    	{
            return (PLAYLIST_UNKNOW);
    	}
    }

    pt_playlist->url_header.i4_used_size = 0;/*reset the header buffer*/
    
    contentType = strstr(pt_playlist->url_header.pac_buffer, "Content-Type:");
    for (i = 0; ; i++)
    {
        if ( (contentType == NULL) || (playlist_contentType_type[i].name == NULL) )
        {
            break;
        }
        if (strstr(contentType, playlist_contentType_type[i].name))
        {
            pt_playlist->playlistType = playlist_contentType_type[i].type;
            return (playlist_contentType_type[i].type);
        }
    } /* end of for */

    return (PLAYLIST_UNKNOW);
} /* end of get_playlist_type */


/*------------------------------------------------------------------------
 * Name:  parse_m3u8_playlist_info
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  playlist
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: - playlist type
 -----------------------------------------------------------------------*/
int
parse_ext_m3u8_playlist_info(struct HLS_PLAYLIST * pt_playlist)
{
    LOG(6, "Enter %s\n", __FUNCTION__);

    /* transfer to ascii code */
    decodeFromUTF8(pt_playlist->url_content.pac_buffer,
                   pt_playlist->url_content.i4_used_size);

    LOG(6, "url_content = %s\n", pt_playlist->url_content.pac_buffer);

    pt_playlist->playlistType = PLAYLIST_M3U;
    parse_ext_m3u_playlist_info(pt_playlist);

    return (HLS_OK);
} /* end of parse_m3u8_playlist_info */


/*------------------------------------------------------------------------
 * Name:  parse_ext_m3u_playlist_info
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  playlist
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: - playlist type
 -----------------------------------------------------------------------*/
int
parse_ext_m3u_playlist_info(struct HLS_PLAYLIST * pt_playlist)
{
    char *ps_str = NULL;
    char * line = NULL;
    int i;
    char * content = pt_playlist->url_content.pac_buffer;

    if (pt_playlist->playlistType != PLAYLIST_M3U )
    {
        LOG(0, "pt_playlist->type != PLAYLIST_M3U \n");
        return (HLS_FAIL);
    }

    /* check whether Extended M3U file */
    line = (char*)malloc(sizeof(char) * LINE_BUF_SIZE);
    content = read_line(content, line);
    if ( (content == NULL) || (line == NULL))
    {
        LOG(0, "content = %p, line = %p\n", content, line);
        return (HLS_FAIL);
    }
    if ((ps_str = strstr(line, "#EXTM3U"))== NULL)
    {
        LOG(0, "%s:%d, not extended m3u file format\n",
            __FUNCTION__, __LINE__);
        return (HLS_FAIL);
    }
    content = read_line(content, line);
    while (content != NULL)
    {
        for (i = 0; ; i++)
        {
            if (http_live_streaming_tag_function[i].tag == NULL)
            {
                break;
            }
            ps_str = strstr(line, http_live_streaming_tag_function[i].tag);
            if (ps_str != NULL)
            {
                LOG(8, "%s, found tag: %s\n", __FUNCTION__, http_live_streaming_tag_function[i].tag );
                http_live_streaming_tag_function[i].parse_function(pt_playlist, line, content);
                break;
            }
        } /* end of for */

        if (pt_playlist->ext_x_endlist == 1)
        {
            LOG(7, "%s,#EXT-X-ENDLIST: detected!\n", __FUNCTION__);
            //break;
        }
        content = read_line(content, line);

    } /* end of while */

    free(line);

    return (HLS_OK);
} /* parse_ext_m3u_playlist_info */


/*------------------------------------------------------------------------
 * Name:  playlist_check
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  ps_url
 *
 * Outputs: - playlist
 *            NULL: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
struct HLS_PLAYLIST *
playlist_check(const char * ps_url)
{
    int i;
    struct HLS_PLAYLIST * pt_playlist;
    for(i = 0;; i++)
    {
        if (strstr(ps_url, non_playlsit_sub_filename[i]))
        {
            LOG(0, "not playlist file %s", ps_url);
            return NULL;
        }
    } /* end of for */

    pt_playlist = new_playlist(ps_url);
    if (download_playlist(pt_playlist) == HLS_FAIL)
    {
        LOG(0, "do_playlist_request fail \r\n");
        return (NULL);
    }

    return (pt_playlist);
} /* end of playlist_check */


/*------------------------------------------------------------------------
 * Name:  request_encrypt_key
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  pt_playlist
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
int
request_encrypt_key(struct KEY_INFO * pt_media_key)
{
    int ret;

    if ( pt_media_key == NULL )
    {
        return (HLS_FAIL);
    }

    if (pt_media_key->ps_url)
    {
        pt_media_key->pt_curl = curl_easy_init();
        //curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_VERBOSE, 1L);
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_FOLLOWLOCATION, 1L);
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_SSL_VERIFYPEER, 0L);
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_MAXREDIRS, 10L);
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_HEADERFUNCTION, _header_wr_function);
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_WRITEHEADER, &(pt_media_key->url_header));
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_NOPROGRESS, 1L);
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_WRITEFUNCTION, _body_wr_function);
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_WRITEDATA, &(pt_media_key->url_content));
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_PRIVATE, pt_media_key);
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_CONNECTTIMEOUT, 30);
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_URL, pt_media_key->ps_url);
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_FRESH_CONNECT, 1L);
        std::string ua_str = Playlist::GetUAString();
        if (ua_str.length() != 0)
        {
            curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_USERAGENT, ua_str.c_str());
        }
        /* create alias header, for file which format is .nsv (ShoutCast) */
        pt_media_key->palias_hdr_slist = curl_slist_append(pt_media_key->palias_hdr_slist, "ICY 200 OK");
        curl_easy_setopt(pt_media_key->pt_curl, CURLOPT_HTTP200ALIASES, pt_media_key->palias_hdr_slist);

        /* perform easy operation */
        if ((ret = curl_easy_perform(pt_media_key->pt_curl)))
        {
            LOG(0, "curl_easy_perform fail %d\n", ret);
        }
        curl_easy_cleanup(pt_media_key->pt_curl);
        pt_media_key->pt_curl = NULL;

        curl_slist_free_all(pt_media_key->palias_hdr_slist);
        pt_media_key->palias_hdr_slist = NULL;

        /* get playlist download time */
        pt_media_key->downloadTime = time(NULL);
    }
    return (HLS_OK);
} /* request_encrypt_key */

int get_http_header(struct HLS_PLAYLIST * pt_playlist)
{
    int ret = HLS_FAIL;
    CURL *pt_curl;

    if (pt_playlist == NULL)
    {
        return ret;
    }

    do
    {
        pt_curl = curl_easy_init();
        if (pt_curl == NULL)
        {
            LOG(0, "curl_easy_init fail\n");
            return ret;
        }
		bool bHeadReceiveSuccess = false;
		
        //curl_easy_setopt(pt_curl, CURLOPT_VERBOSE, 1L);
        curl_easy_setopt(pt_curl, CURLOPT_FOLLOWLOCATION, 1L);
		curl_easy_setopt(pt_curl, CURLOPT_SSL_VERIFYPEER, 0L);
        curl_easy_setopt(pt_curl, CURLOPT_MAXREDIRS, 10L);
        curl_easy_setopt(pt_curl, CURLOPT_HEADERFUNCTION, _header_wr_function);
        curl_easy_setopt(pt_curl, CURLOPT_WRITEHEADER, &(pt_playlist->url_header));
        curl_easy_setopt(pt_curl, CURLOPT_CONNECTTIMEOUT, 30);
        curl_easy_setopt(pt_curl, CURLOPT_TIMEOUT, 30);
        curl_easy_setopt(pt_curl, CURLOPT_URL, pt_playlist->ps_url);
		curl_easy_setopt(pt_curl, CURLOPT_FRESH_CONNECT, 1L);
        curl_easy_setopt(pt_curl, CURLOPT_WRITEFUNCTION, _body_wr_break_function);
        curl_easy_setopt(pt_curl, CURLOPT_WRITEDATA, &bHeadReceiveSuccess);
		std::string ua_str = Playlist::GetUAString();
        if (ua_str.length() != 0)
        {
            curl_easy_setopt(pt_curl, CURLOPT_USERAGENT, ua_str.c_str());
        }
  
        /* perform easy operation */
		curl_easy_perform(pt_curl);
		
        if(!bHeadReceiveSuccess)
        {
            LOG(0, "curl_easy_perform fail %d\n", ret);
            ret = HLS_FAIL;
            break;
        }
        ret = HLS_OK;
        LOG(9, "---[INFO]---url_header = %s!\n", pt_playlist->url_header.pac_buffer);
    }while(0);

    curl_easy_cleanup(pt_curl);
    pt_curl = NULL;
    
    return ret;
}
/*------------------------------------------------------------------------
 * Name:  download_playlist
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  pt_playlist
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
int
download_playlist(struct HLS_PLAYLIST * pt_playlist)
{
    int ret = HLS_FAIL;

    if (pt_playlist == NULL)
    {
        return ret;
    }

    do
    {
        if (pt_playlist->ps_url == NULL)
        {
            break;
        }
        LOG(5, "Enter %s, ps_url = %s\n", __FUNCTION__, pt_playlist->ps_url);
        
        pt_playlist->pt_curl = curl_easy_init();
        //curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_VERBOSE, 1L);
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_FOLLOWLOCATION, 1L);
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_SSL_VERIFYPEER, 0L);
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_MAXREDIRS, 10L);
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_HEADERFUNCTION, _header_wr_function);
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_WRITEHEADER, &(pt_playlist->url_header));
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_NOPROGRESS, 1L);     
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_WRITEFUNCTION, _body_wr_function);
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_WRITEDATA, &(pt_playlist->url_content));
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_PRIVATE, pt_playlist);
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_CONNECTTIMEOUT, 30);
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_TIMEOUT, 60);
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_URL, pt_playlist->ps_url);
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_FRESH_CONNECT, 1L);
        std::string ua_str = Playlist::GetUAString();
        if (ua_str.length() != 0)
        {
            curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_USERAGENT, ua_str.c_str());
        }
        /* create alias header, for file which format is .nsv (ShoutCast) */
        pt_playlist->palias_hdr_slist = curl_slist_append(pt_playlist->palias_hdr_slist, "ICY 200 OK");
        curl_easy_setopt(pt_playlist->pt_curl, CURLOPT_HTTP200ALIASES, pt_playlist->palias_hdr_slist);

        /* perform easy operation */
        if( (ret = curl_easy_perform(pt_playlist->pt_curl)))
        {
            LOG(0, "curl_easy_perform fail %d\n", ret);
            ret = HLS_FAIL;
            break;
        }
        ret = HLS_OK;
        LOG(9, "---[INFO]---url_header = %s!\n", pt_playlist->url_header.pac_buffer);
        LOG(9, "---[INFO]---url_content = %s!\n", pt_playlist->url_content.pac_buffer);
    }while(0);

    curl_easy_cleanup(pt_playlist->pt_curl);
    pt_playlist->pt_curl = NULL;
    
    curl_slist_free_all(pt_playlist->palias_hdr_slist);
    pt_playlist->palias_hdr_slist = NULL;
    
    /* get playlist download time */
    pt_playlist->downloadTime = time(NULL);

    LOG(0, "---[INFO]---%s downloaded \n", pt_playlist->ps_url);
    return ret;
} /* download_playlist */

/*------------------------------------------------------------------------
 * Name:  read_playlist
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  pt_playlist
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
int read_playlist(struct HLS_PLAYLIST * pt_playlist)
{
    int ret = HLS_FAIL;
    FILE * fp = NULL;
    
    if (pt_playlist == NULL)
    {
        return ret;
    }

    do
    {
        if (pt_playlist->ps_url == NULL)
        {
            break;
        }
        LOG(6, "Enter %s, ps_url = %s\n", __FUNCTION__, pt_playlist->ps_url);

        struct stat stats;

        if (stat(pt_playlist->ps_url, &stats) != 0)
        {
            break;
        }

        fp = fopen(pt_playlist->ps_url, "r");
        if (fp == NULL)
        {
            break;
        }

        if (pt_playlist->url_content.pac_buffer != NULL)
        {
            free(pt_playlist->url_content.pac_buffer);
            pt_playlist->url_content.pac_buffer = NULL;
        }
        pt_playlist->url_content.pac_buffer = (char*)malloc(stats.st_size + 1);

        if (fread(pt_playlist->url_content.pac_buffer, 1, stats.st_size, fp) != (size_t)stats.st_size)
        {
            free(pt_playlist->url_content.pac_buffer);
            pt_playlist->url_content.pac_buffer = NULL;
            break;
        }

        pt_playlist->url_content.i4_total_size = stats.st_size + 1;
        pt_playlist->url_content.i4_used_size = stats.st_size;

        ret = HLS_OK;
    }while(0);

    if (fp)
    {
        fclose(fp);
        fp = NULL;
    }
    return ret;
}

/*------------------------------------------------------------------------
 * Name:  optimize_playlist
 *
 * Description: Request min_entry_num or stream info from server
 *
 * Inputs:  pt_playlist
 *          min_entry_num - min number of mediainfo download
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: -
 *
 * Note:
 -----------------------------------------------------------------------*/
int
optimize_playlist(struct HLS_PLAYLIST * pt_playlist, int max_entry_num)
{
	int max_entry;
    if (pt_playlist->playlistContentType == TYPE_EVENT_APPENDING)
    {
	    /* The start time of playback should be as close to live as possible. 
	     * In this test case, the appending m3u8 file is very long and playback 
	     * should start close to live playback.
	     */    	
	    max_entry = max_entry_num > TYPE_EVENT_THREAD?  max_entry_num : TYPE_EVENT_THREAD;
        if(pt_playlist->i4_numOfEntry > max_entry)	
        {
			/* remove out of date media data */
			remove_old_media_file(pt_playlist);

#ifdef DEBUG      
            /* dump new playlist */
        	dump_playlist(pt_playlist, DUMP_CONDITION_ALL, DUMP_TO_CONSOLE);

#endif
        }
    }
return true;
}

/*------------------------------------------------------------------------
 * Name:  request_playlist
 *
 * Description: Request min_entry_num or stream info from server
 *
 * Inputs:  pt_playlist
 *          min_entry_num - min number of mediainfo download
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: -
 *
 * Note:
 -----------------------------------------------------------------------*/
#define NUM_OF_MIN_MEDIA_INFO  30
int
request_playlist(struct HLS_PLAYLIST * pt_playlist, int min_entry_num)
{

    LOG(5, "Enter %s, ps_url = %s\n", __FUNCTION__, pt_playlist->ps_url);

    if (pt_playlist == NULL)
    {
        return (HLS_FAIL);
    }

    /* for better performance, before playback, we should get
       at least min_entry_num media files
     */
#if 0
    while(pt_playlist->i4_numOfEntry  < min_entry_num)

#endif
    {
        if (access(pt_playlist->ps_url, R_OK))
        {
            pt_playlist->url_type = (int)URL_ONLINE;
            if (HLS_FAIL == download_playlist(pt_playlist))
            {
                return (HLS_FAIL);
            }
        }
        else
        {
            pt_playlist->url_type = (int)URL_LOCAL;
            if (HLS_FAIL == read_playlist(pt_playlist))
            {
                return (HLS_FAIL);
            }
        }

        LOG(6, "download playlist ok\n");

		/*update host url?*/
        char tmpLine[LINE_BUF_SIZE];
        memset(tmpLine, 0, LINE_BUF_SIZE);
        char * pLocation = find_last_of_str(pt_playlist->url_header.pac_buffer, "Location:");
        if (pLocation != NULL)
        {
            read_line(pLocation, tmpLine);
            char *pLocationUrl = strstr(tmpLine, "http");
            if (pLocationUrl != NULL)
            {
                char *pTmp = pt_playlist->ps_hostUrl;
                pt_playlist->ps_hostUrl = getHostUrl(pLocationUrl);
                if (pt_playlist->ps_hostUrl != NULL)
                {
                    if (pTmp != NULL)
                        free(pTmp);
                }
                else
                    pt_playlist->ps_hostUrl = pTmp;
            }
        }
        LOG(0, "replace host url to%s\n", (pt_playlist->ps_hostUrl==NULL)?"NULL":pt_playlist->ps_hostUrl);
        
        if ( pt_playlist->playlistType == PLAYLIST_UNKNOW )
        {
            /* get playlist type */
            if (get_playlist_type(pt_playlist) == PLAYLIST_UNKNOW)
            {
            	LOG(0, "PLAYLIST_UNKNOW!!\n");
            	return (HLS_FAIL);
            }
        }

        /*  */
        if(pt_playlist->playlistType == PLAYLIST_M3U8)
        {
            LOG(6, "m3u8 parse\n");
            if (HLS_FAIL == parse_ext_m3u8_playlist_info(pt_playlist))
            {
            	return (HLS_FAIL);
            }
        }
        else
        {
            LOG(6, "m3u parse\n");
            if ( HLS_FAIL == parse_ext_m3u_playlist_info(pt_playlist))
            {
            	return (HLS_FAIL);
            }
        }
        LOG(6, "playlist parse end\n");
        pt_playlist->url_content.i4_used_size = 0;

        /* if there is only stream info in this url
           then we do not need to request playlist
           again
         */
        if ( (pt_playlist->playlistContentType != TYPE_PROGRAM) && (pt_playlist->duration == 0) )
        {
            LOG(0, "There is no media info in this playlist!! \n");
            return (HLS_FAIL);
            // break;
        }
        
#ifdef DEBUG
    dump_playlist(pt_playlist, DUMP_CONDITION_ALL, DUMP_TO_CONSOLE);
#endif

    } 

    LOG(0, "playlist playback duration is %d!!\n", pt_playlist->duration);

    if (pt_playlist->controller)
    {
    	LOG(0, "playlist playback duration is %d!!\n", pt_playlist->duration);
    	
        Playlist *pl = (Playlist*)(pt_playlist->controller);
        pl->realizeStream(pt_playlist);
    }
    return (HLS_OK);

} /* reques_parse_playlist */

static void 
remove_old_media_file(struct HLS_PLAYLIST * pt_playlist)
{
    struct ENTRY_INFO * ptr = NULL;
    struct ENTRY_INFO * rPtr = NULL;
    int i4_numOfEntry = 0;
    bool start_to_remove = false;
    
    ptr = (struct ENTRY_INFO *)pt_playlist->pv_entry_tail;
    while(ptr)
    {
        if(ptr->type == TYPE_MEDIA)
        {        
            i4_numOfEntry++;
            if (i4_numOfEntry > TYPE_EVENT_THREAD)
            {
            	/* remove entries before this media file */
            	start_to_remove = true;
            	break;
            }
        }
        else if(ptr->type == TYPE_KEY)
        {
        	LOG(0, "start_to_remove == true\n");
        	/* remove entries before this key file */
            start_to_remove = true;
            break;
        }
        else if(ptr->type == TYPE_DISCONTINUITY_TAG)
        {
            LOG(0, "start_to_remove == true\n");
            /* remove entries before this discontinuity file */
            if (i4_numOfEntry != 0)
	            start_to_remove = true;
            break;
        }
        ptr = ptr->prev;       
    } /* end of while */

    
    while(ptr->prev)
    {
    	
    	rPtr = ptr->prev;    	   		
    	ptr->prev = rPtr->prev;
    	rPtr->next = ptr;
    	if (rPtr->type == TYPE_MEDIA)
    	{
    	    free_media_info((struct MEDIA_INFO *)rPtr);
    	}
    	else if (rPtr->type == TYPE_KEY)
    	{
    	    free_encrypt_key((struct KEY_INFO *)rPtr);	
    	}  	
    	pt_playlist->i4_numOfEntry--;
    } /* end of while */
    
    pt_playlist->pv_entry_head = ptr;
    
} /* remove_old_media_file */

/*------------------------------------------------------------------------
 * Name:  dump_playlist
 *
 * Description: This API trim head and tail space in string.
 *
 * Inputs:  pt_playlist
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
int
dump_playlist(struct HLS_PLAYLIST * pt_playlist, DUMP_CONDITION cond, DUMP_TYPE type)
{
    struct ENTRY_INFO * ptr = NULL;
    struct MEDIA_INFO * ptrMedia = NULL;
    if (type == DUMP_TO_FILE)
    {
    	START_LOG_TO_FILE();
    }
    LOG(5, "---BEGIN HTTP LIVE STREAMING PLAYLIST---\n");
    LOG(5, "playlist url = %s\n", pt_playlist->ps_url);
    LOG(5, "playlist i4_numOfEntry = %d\n", pt_playlist->i4_numOfEntry);
    LOG(5, "playlist duration = %d\n", pt_playlist->duration);
    LOG(5, "playlist ext_x_targetduration = %d\n", pt_playlist->ext_x_targetduration);
    LOG(5, "playlist ext_x_media_sequence = %d\n", pt_playlist->ext_x_media_sequence);
    LOG(5, "playlist ext_x_version = %d\n", pt_playlist->ext_x_version);
    LOG(5, "playlist ext_x_endlist = %d\n", pt_playlist->ext_x_endlist);
    LOG(5, "playlist ext_x_allow_cache = %d\n", pt_playlist->ext_x_allow_cache);
    LOG(5, "playlist playlistType = %d\n", pt_playlist->playlistType);
    LOG(5, "playlist playlistContentType = %d\n", pt_playlist->playlistContentType);

    ptr = (struct ENTRY_INFO *)pt_playlist->pv_entry_head;
    while(ptr)
    {
        if(ptr->type == TYPE_PLAYLIST)
        {
            dump_playlist((struct HLS_PLAYLIST *)ptr, cond, DUMP_TO_CONSOLE);
        }
        else if(ptr->type == TYPE_MEDIA)
        {
        	if (cond == DUMP_CONDITION_CACHED)
        	{
        	    ptrMedia = (struct MEDIA_INFO *)ptr;
        	    if (ptrMedia->status == MEDIA_FILE_CACHED)
        	    {
                    dump_media_info((struct MEDIA_INFO *)ptr);
        	    }
        	}
        	else if(cond == DUMP_CONDITION_CONSUMED)
        	{
        	    ptrMedia = (struct MEDIA_INFO *)ptr;
        	    if (ptrMedia->status == MEDIA_FILE_CONSUMED)
        	    {
                    dump_media_info((struct MEDIA_INFO *)ptr);
        	    }        		
        	}
        	else
        	{
        		dump_media_info((struct MEDIA_INFO *)ptr);
        	}
        }
        else if(ptr->type == TYPE_STREAM)
        {
            dump_stream_info((struct STREAM_INFO *)ptr);
        }
        else if(ptr->type == TYPE_KEY)
        {
            if (cond == DUMP_CONDITION_ALL)
                dump_encrypt_key((struct KEY_INFO *)ptr);
        }
        else if(ptr->type == TYPE_DISCONTINUITY_TAG)
        {
            LOG(0, "TYPE_DISCONTINUITY_TAG\n");
            break;
        }
        ptr = ptr->next;
    } /* end of while */
    LOG(5, "playlist downloadTime = %s\n", asctime(localtime(&(pt_playlist->downloadTime)) ) );
    LOG(5, "---END HTTP LIVE STREAMING PLAYLIST---\n");

    if (type == DUMP_TO_FILE)
    {
    	STOP_LOG_TO_FILE();
    }
    
    return (HLS_OK);
} /* end of dump_playlist */


/*------------------------------------------------------------------------
 * Name:  get_next_media_file
 *
 * Description: Get next media file
 *
 * Inputs:  pt_playlist
 *          pt_cur_media_info
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
struct MEDIA_INFO *
get_next_media_file(struct HLS_PLAYLIST * pt_playlist,
                    struct MEDIA_INFO * pt_cur_media_info)
{
    struct STREAM_INFO * pt_stream_info = NULL;
    struct MEDIA_INFO * pt_media_info = NULL;
    struct ENTRY_INFO * ptr = NULL;

    if ( (pt_playlist == NULL) )
    {
        LOG(0, "pt_playlist[NULL] = %p \n", pt_playlist );
        return (NULL);
    } 

    if (pt_cur_media_info == NULL)
    {
        LOG(0, "pt_cur_media_info[NULL] %lu\n", pthread_self());
        /* skip key entry type */

        ptr = (struct ENTRY_INFO *)pt_playlist->pv_entry_head;
        if ( NULL ==  ptr)
        {
            LOG(0, "No entry in playlist %p!!\n", pt_playlist);
            return (NULL);
        }

        while ((TYPE_STREAM != ptr->type) && (TYPE_MEDIA != ptr->type))
        {
            ptr = ptr->next;
            if (ptr == NULL)
            {
                LOG(0, "pt_media_info = NULL \n");
                return (NULL);
            }

        } /* end of while */

        if (ptr->type == TYPE_MEDIA)
        {
            pt_media_info = (struct MEDIA_INFO *) ptr;
            LOG(6, "pt_media_info->ps_url = %s\n", pt_media_info->ps_url);
            return (pt_media_info);
        }

        /* stream */
        if (ptr->type == TYPE_STREAM)
        {
            pt_stream_info = (struct STREAM_INFO *) ptr;

            /* get playlist */
            if (pt_stream_info->pt_playlist == NULL)
            {

                pt_stream_info->pt_playlist = new_playlist(pt_stream_info->ps_url);

                if (pt_stream_info->pt_playlist == NULL)
                {
                    LOG(0, "new playlist fail\n");
                    return (NULL);
                }
                /* TBD: define min entry number */

                request_playlist(pt_stream_info->pt_playlist, 30);
#if 0 //DEBUG                

                dump_playlist(pt_stream_info->pt_playlist, 0, DUMP_TO_CONSOLE);
#endif

            } /* get next entry from playlist */

            pt_media_info = get_next_media_file(pt_stream_info->pt_playlist, NULL);

            return (pt_media_info);
        } /* end of if */
    }
    else
    {
        /* get next media file info */

        if (NULL == pt_cur_media_info->entry_info.next)
        {
            LOG(0, "end of playlist!! \n");
            return (NULL);
        }

        /* skip key entry */
        ptr = (struct ENTRY_INFO *)pt_cur_media_info->entry_info.next;

        while ((TYPE_STREAM != ptr->type) && (TYPE_MEDIA != ptr->type))
        {
            ptr = ptr->next;
            if (ptr == NULL)
            {
                LOG(0, "pt_media_info = NULL \n");
                return (NULL);
            }
        } /* end of while */

        if (ptr->type == TYPE_MEDIA)
        {
            pt_media_info = (struct MEDIA_INFO *)ptr;
            return (pt_media_info);
        }

        /* stream */
        if (ptr->type == TYPE_STREAM)
        {
            pt_stream_info = (struct STREAM_INFO *)ptr;
            /* get playlist */
            if (pt_stream_info->pt_playlist == NULL)
            {

                pt_stream_info->pt_playlist = new_playlist(pt_stream_info->ps_url);

                if (pt_stream_info->pt_playlist == NULL)
                {
                    LOG(0, "new playlist fail\n");
                    return (NULL);
                }

                request_playlist(pt_stream_info->pt_playlist, 30);
            } /* get next entry from playlist */

            pt_media_info = get_next_media_file(pt_stream_info->pt_playlist, NULL);

            return (pt_media_info);
        } /* end of if */
    }
    return (NULL);
} /* end of get_next_media_file */

/*------------------------------------------------------------------------
 * Name:  download_media_file
 *
 * Description: Get next media file
 *
 * Inputs:  pt_playlist
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
#ifndef COMMON_CURL_HANDLE
#define COMMON_CURL_HANDLE
#endif

int
download_media_file(Playlist * pt_playlist, struct MEDIA_INFO * pt_media_info)
{
    int ret;
    double ui8_speed = 0;

    if ( pt_media_info->ps_url == NULL)
    {
        LOG(0, "download_media_file is NULL!\n");
        return (HLS_FAIL);
    }
    
    if(pt_media_info->pt_media_key != NULL)
    {
        if ( pt_media_info->pt_media_key->bDownError == true)
        {
            LOG(0, "doesn't need to down the media!\n");
            return (HLS_FAIL);
        }
    }    
    
    LOG(6, "download_media_file %s\n", pt_media_info->ps_url);

    pt_playlist->pt_cur_dl_media = pt_media_info;
    pt_media_info->status = MEDIA_FILE_NONE;
    memset(&pt_media_info->url_content, 0, sizeof(struct CHAR_BUFFER));
    memset(&pt_media_info->url_header, 0, sizeof(struct CHAR_BUFFER));
    
    if (pt_media_info->ps_url)
    {
#ifdef COMMON_CURL_HANDLE
        if(NULL == g_media_file_curl)
        {
            g_media_file_curl = curl_easy_init();
#ifdef DEBUG_HLS
            curl_easy_setopt(g_media_file_curl, CURLOPT_VERBOSE, 1L);
#endif
            curl_easy_setopt(g_media_file_curl, CURLOPT_FOLLOWLOCATION, 1L);
            curl_easy_setopt(g_media_file_curl, CURLOPT_SSL_VERIFYPEER, 0L);
            curl_easy_setopt(g_media_file_curl, CURLOPT_HEADERFUNCTION, consumeHeaderData);
            curl_easy_setopt(g_media_file_curl, CURLOPT_MAXREDIRS, 10L);
            curl_easy_setopt(g_media_file_curl, CURLOPT_NOPROGRESS, 0L);
            curl_easy_setopt(g_media_file_curl, CURLOPT_PROGRESSFUNCTION, _progress_function);  
            curl_easy_setopt(g_media_file_curl, CURLOPT_PROGRESSDATA, pt_playlist);        
            curl_easy_setopt(g_media_file_curl, CURLOPT_WRITEFUNCTION, consumeContentData);
            curl_easy_setopt(g_media_file_curl, CURLOPT_CONNECTTIMEOUT, 30);
            curl_easy_setopt(g_media_file_curl, CURLOPT_TIMEOUT, 60);
            curl_easy_setopt(g_media_file_curl, CURLOPT_FRESH_CONNECT, 0L);
            curl_easy_setopt(g_media_file_curl, CURLOPT_POSTREDIR, CURL_REDIR_POST_ALL);            
        }
        curl_easy_setopt(g_media_file_curl, CURLOPT_WRITEDATA, pt_playlist);
        curl_easy_setopt(g_media_file_curl, CURLOPT_WRITEHEADER, pt_media_info);
        curl_easy_setopt(g_media_file_curl, CURLOPT_URL, pt_media_info->ps_url);
        std::string ua_str = Playlist::GetUAString();
        if (ua_str.length() != 0)
        {
            curl_easy_setopt(g_media_file_curl, CURLOPT_USERAGENT, ua_str.c_str() );
        }
        pt_media_info->palias_hdr_slist = curl_slist_append(pt_media_info->palias_hdr_slist, "ICY 200 OK");
        curl_easy_setopt(g_media_file_curl, CURLOPT_HTTP200ALIASES, pt_media_info->palias_hdr_slist);

        /* perform easy operation */
        LOG(8, "---[INFO]---Start to curl_easy_perform [%d]!\n", pt_media_info->sequence_number);
        ret = curl_easy_perform(g_media_file_curl);
        if (ret)
        {
             LOG(0, "download fail,  %d/%d downloaded (%d) !  %s  \n", 
            	pt_media_info->url_content.i4_used_size, 
            	pt_media_info->content_length, ret, pt_media_info->ps_url);
        }

        LOG(5, " %d/%d downloaded (%d) !\n", 
        	pt_media_info->url_content.i4_used_size, 
        	pt_media_info->content_length, ret);
                    
    	if (CURLE_OK != curl_easy_getinfo(g_media_file_curl, CURLINFO_SPEED_DOWNLOAD , &ui8_speed))
	{
    	    LOG(0, "get %s speed fail! res = %d\n", pt_media_info->ps_url, ret);
    	}
        pt_media_info->downloadSpeed = ui8_speed;        
        
        curl_slist_free_all(pt_media_info->palias_hdr_slist);
        pt_media_info->palias_hdr_slist = NULL;
#else
        pt_media_info->pt_curl = curl_easy_init();
#ifdef DEBUG_HLS
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_VERBOSE, 1L);
#endif
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_FOLLOWLOCATION, 1L);
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_SSL_VERIFYPEER, 0L);
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_HEADERFUNCTION, consumeHeaderData);
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_WRITEHEADER, pt_media_info);
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_MAXREDIRS, 10L);
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_NOPROGRESS, 0L);
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_PROGRESSFUNCTION, _progress_function);  
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_PROGRESSDATA, pt_playlist);        
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_WRITEFUNCTION, consumeContentData);
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_WRITEDATA, pt_playlist);
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_CONNECTTIMEOUT, 30);
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_TIMEOUT, 60);
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_URL, pt_media_info->ps_url);
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_FRESH_CONNECT, 0L);
        std::string ua_str = Playlist::GetUAString();
        if (ua_str.length() != 0)
        {
            curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_USERAGENT, ua_str.c_str() );
        }
        /* create alias header, for file which format is .nsv (ShoutCast) */
        pt_media_info->palias_hdr_slist = curl_slist_append(pt_media_info->palias_hdr_slist, "ICY 200 OK");
        curl_easy_setopt(pt_media_info->pt_curl, CURLOPT_HTTP200ALIASES, pt_media_info->palias_hdr_slist);

        /* perform easy operation */
        LOG(8, "---[INFO]---Start to curl_easy_perform [%d]!\n", pt_media_info->sequence_number);
        ret = curl_easy_perform(pt_media_info->pt_curl);
        if (ret)
        {
            LOG(0, "%s download fail,  %d/%d downloaded (%d) !\n", 
            	pt_media_info->ps_url, pt_media_info->url_content.i4_used_size, 
            	pt_media_info->content_length, ret);
        }

        LOG(5, " %d/%d downloaded (%d) !\n", 
        	pt_media_info->url_content.i4_used_size, 
        	pt_media_info->content_length, ret);
            
        if (CURLE_OK != curl_easy_getinfo(pt_media_info->pt_curl, CURLINFO_SPEED_DOWNLOAD , &ui8_speed);)
        {
    	    LOG(0, "get %s speed fail! res = %d\n", pt_media_info->ps_url, ret);
        }
        pt_media_info->downloadSpeed = ui8_speed;        
        
        curl_easy_cleanup(pt_media_info->pt_curl);
        pt_media_info->pt_curl = NULL;
        curl_slist_free_all(pt_media_info->palias_hdr_slist);
        pt_media_info->palias_hdr_slist = NULL;
#endif
    } /* end of if */


    LOG(5, "download_media_file %s\n", pt_media_info->ps_url);

    if (ret || (pt_media_info->result_code > 400))
    {
    
        LOG(0, "download_media_file %s has problem!!\n", pt_media_info->ps_url);
        /* clear but do not free buffer, we could reuse it or let memroy analysis to free it */
        pt_media_info->url_content.i4_used_size = 0;
        memset(pt_media_info->url_content.pac_buffer, 0, pt_media_info->url_content.i4_total_size);
        return HLS_FAIL;
    }


    return HLS_OK;
} /* end of download_media_file */


/*------------------------------------------------------------------------
 * Name:  download_url_file
 *
 * Description: Get next media file
 *
 * Inputs:  pt_playlist
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
int
download_url_file(char * ps_url, char * filename)
{

    CURL *pt_curl = NULL;
    struct curl_slist   *palias_hdr_slist = NULL;
    FILE *fp = NULL;

    LOG(6, "download_url_file %s\n", ps_url);

    if ( ps_url == NULL)
    {
        return (HLS_FAIL);
    }

    if (filename == NULL)
    {
        filename = (char *)"/tmp/mnt/temp_media_file.ts";
    }

    fp = fopen(filename, "wb");
    if (ps_url)
    {
        pt_curl = curl_easy_init();
        curl_easy_setopt(pt_curl, CURLOPT_VERBOSE, 1L);
        curl_easy_setopt(pt_curl, CURLOPT_FOLLOWLOCATION, 1L);
        curl_easy_setopt(pt_curl, CURLOPT_SSL_VERIFYPEER, 0L);
        curl_easy_setopt(pt_curl, CURLOPT_MAXREDIRS, 10L);
        curl_easy_setopt(pt_curl, CURLOPT_NOPROGRESS, 1L);
        curl_easy_setopt(pt_curl, CURLOPT_WRITEFUNCTION, NULL);
        curl_easy_setopt(pt_curl, CURLOPT_WRITEDATA, fp);
        curl_easy_setopt(pt_curl, CURLOPT_CONNECTTIMEOUT, 30);
        curl_easy_setopt(pt_curl, CURLOPT_URL, ps_url);
        curl_easy_setopt(pt_curl, CURLOPT_FRESH_CONNECT, 1L);
        std::string ua_str = Playlist::GetUAString();
        if (ua_str.length() != 0)
        {
            curl_easy_setopt(pt_curl, CURLOPT_USERAGENT, ua_str.c_str());
        }
        /* create alias header, for file which format is .nsv (ShoutCast) */
        palias_hdr_slist = curl_slist_append(palias_hdr_slist, "ICY 200 OK");
        curl_easy_setopt(pt_curl, CURLOPT_HTTP200ALIASES, palias_hdr_slist);

        /* perform easy operation */
        curl_easy_perform(pt_curl);
        curl_easy_cleanup(pt_curl);
        pt_curl = NULL;
        curl_slist_free_all(palias_hdr_slist);
        palias_hdr_slist = NULL;

    }
    fclose(fp);
    return (HLS_OK);
}

void stop_download()
{
    g_stop_download = true;
}

void start_download()
{
    g_stop_download = false;
}

int alloc_media_file_buf(struct MEDIA_INFO * pt_media_info, struct HLS_PLAYLIST * pt_playlist);

/*------------------------------------------------------------------------
 * Name:  http_live_streaming_thread
 *
 * Description: Get next media file
 *
 * Inputs:  pt_playlist
 *
 * Outputs: - HLS_OK: playlist
 *            HLS_FAIL: non-playlist
 *
 * Returns: -
 -----------------------------------------------------------------------*/
void *
http_live_streaming_thread(void  * pv_data)
{
    if ( NULL == pv_data)
    {
        LOG(0, "pv_data is NULL!\n");
        return NULL;
    }

    struct MEDIA_INFO * pt_media_info;
    hls::Playlist * pt_theList = (hls::Playlist*) pv_data;

    // playlist realize moved here, because it costs much time, if the outer called it and the outer will be blocked some time.
    if(! (pt_theList->realize())){
        // can't download playlist
        LOG(0, "Realize fail!\n");
        return 0;                 // terminal itself.
    }
    
    LOG(5, "****    %s created %lu!!    ****\n", __FUNCTION__, pthread_self());
    while ((pt_media_info = pt_theList->popDownloadTask()) != NULL) // only when playlist won't be need to be worked, the pt_media_info will be set to NULL, or popDownloadTask will give a non-null value even when it blocked for a while.
    {
        
        LOG(0, "---[INFO]---sequence_number = %d!\n", pt_media_info->sequence_number);
        if (HLS_OK != download_media_file(pt_theList, pt_media_info) )
        {
            pt_theList->notifyMediaStatus(pt_media_info, MEDIA_FILE_CACHE_FAIL); // have failed.
        }else{
            pt_theList->notifyMediaStatus(pt_media_info, MEDIA_FILE_CACHED); // default param means no failure between downloading. 
        }            
        
        if ( NULL == pt_media_info)
        {
            LOG(0, "pt_media_info[NULL]!\n");
            break;
        } 

        if (g_stop_download)
	 {
		LOG(0, "stop download thread!\n");
		break;
	 }

        LOG(0, "---[INFO]---%d, speed=%.3f!\n", 
        		pt_media_info->sequence_number, pt_media_info->downloadSpeed);
        
        pt_theList->getCurHLSPlaylist()->downloadSpeed = pt_media_info->downloadSpeed;

        if (true == pt_theList->resetDownload)
        {
            pt_theList->getCurHLSPlaylist()->timeBuffered = 0;
            pt_theList->getCurHLSPlaylist()->byteLoaded = 0;                
        }
        else
        {
            pt_theList->getCurHLSPlaylist()->timeBuffered += pt_media_info->duration;
            pt_theList->getCurHLSPlaylist()->byteLoaded += pt_media_info->content_length;      
            if(pt_theList->needSelectStream()){	
                pt_theList->selectStream(pt_media_info);
            }        
        }
        
    } /* end of while */

    LOG(0, "---[INFO]---Quit %s!\n", __FUNCTION__);
    return 0;
} /* end of http_live_streaming_thread */


/*------------------------------------------------------------------------
 * Name:  alloc_media_file_buf
 *
 * Description: try to reuse
 *
 * Inputs:  pt_media_info
 *          pt_playlist
 *
 * Outputs: - HLS_OK
 *            HLS_FAIL
 *
 * Returns: -
 -----------------------------------------------------------------------*/
int
alloc_media_file_buf(struct MEDIA_INFO * pt_media_info, struct HLS_PLAYLIST * pt_playlist)
{
    struct MEDIA_INFO * ptr;

    ptr = get_next_media_file(pt_playlist, NULL);
    while( NULL != ptr )
    {
        if ( ( MEDIA_FILE_CONSUMED  == ptr->status ) &&
                ( NULL != ptr->url_content.pac_buffer ))
        {
            /* reuse buffer */
            LOG(5, "%d  reuse buffer %d\n", pt_media_info->sequence_number , ptr->sequence_number);
            pt_media_info->url_content.pac_buffer = ptr->url_content.pac_buffer;
            pt_media_info->url_content.i4_total_size = ptr->url_content.i4_total_size;
            pt_media_info->url_content.i4_used_size = 0;

            // reset the ptr media info. because maybe some later this media will be selected again by user.
            ptr->url_content.pac_buffer = NULL;
            ptr->url_content.i4_total_size = 0;
            ptr->url_content.i4_used_size = 0;
            ptr->status = MEDIA_FILE_NONE;
            return (TRUE);
        } /* end of if */

        ptr = get_next_media_file(pt_playlist, ptr);
    } /* end of while */

    return (FALSE);
} /* end of alloc_media_file_buf */
MEDIA_INFO * getMediaLocation(HLS_PLAYLIST * pt_playlist, char * p_uri)
{
    if ((NULL == pt_playlist) || (NULL == p_uri))
    {
        return NULL;
    }

    struct ENTRY_INFO * ent = NULL;

    ent = (ENTRY_INFO*)pt_playlist->pv_entry_head;
    while(ent != NULL)
    {
        if (ent->type == TYPE_MEDIA)
        {
            MEDIA_INFO* minfo = ( MEDIA_INFO*)ent;
            if (strcmp(p_uri,minfo->ps_url) == 0)
            {
                return minfo;
            }
        }
        ent = ent->next;
    }
    return NULL;

}

MEDIA_INFO *getFirstMedia(HLS_PLAYLIST * pt_playlist)
{
    if (NULL == pt_playlist)
    {
        return NULL;
    }

    struct ENTRY_INFO * ent = NULL;

    ent = (ENTRY_INFO*)pt_playlist->pv_entry_head;
    while(ent != NULL)
    {
        if (ent->type == TYPE_MEDIA)
        {
            MEDIA_INFO* minfo = ( MEDIA_INFO*)ent;

            return minfo;
        }
        ent = ent->next;
    }
    return NULL;
}

void moveCharBuffer(struct CHAR_BUFFER & t_charbuf_dest, struct CHAR_BUFFER & t_charbuf_src)
{
    t_charbuf_dest.i4_total_size = t_charbuf_src.i4_total_size;
    t_charbuf_dest.i4_used_size = t_charbuf_src.i4_used_size;
    t_charbuf_dest.pac_buffer = t_charbuf_src.pac_buffer;

    t_charbuf_src.i4_total_size = 0;
    t_charbuf_src.i4_used_size = 0;
    t_charbuf_src.pac_buffer = NULL;
}

char * find_last_of_str(char * pData, char * pKeyWord)
{
    char * p_1 = NULL;
    char * p_2 = NULL;
    int iLen = 0;
    if ((NULL == pData) || (NULL == pKeyWord))
    {
        return NULL;
    }

    iLen = strlen(pKeyWord);
    
    p_1 = strstr(pData, pKeyWord);
    while (p_1 != NULL)
    {
        p_2 = strstr(p_1 + iLen, pKeyWord);
        if (p_2 != NULL)
            p_1 = p_2;
        else
            break;
    }

    return p_1;
}


