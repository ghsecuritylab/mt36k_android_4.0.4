/*-----------------------------------------------------------------------------
 * Copyright (c) 2010, MediaTek Inc.
 * All rights reserved.
 *
 * Unauthorized use, practice, perform, copy, distribution, reproduction,
 * or disclosure of this information in whole or in part is prohibited.
 *-----------------------------------------------------------------------------
 * $RCSfile:  $
 * $Revision: #1 $
 * $Date: 2013/03/06 $
 * $Author: dtvbm11 $
 *
 * Description:
 *    This file is use for provide http live streaming play
 *---------------------------------------------------------------------------*/
#ifndef HTTP_LIVE_STREAMING
#define HTTP_LIVE_STREAMING

#include "curl/curl.h"
#include "Semaphore.h"

#ifdef __cplusplus
extern "C" {
#endif


    /*************************************************/
    /*   HTTP LIVE STREAMING PLAYLIST                */
    /*************************************************/

    /* This structure contain info necessary to decrypt media file */
#define KEY_INFO_METHOD_UNKNOWN   0
#define KEY_INFO_METHOD_NONE		  1
#define KEY_INFO_METHOD_AES_128		0x10

#define STREAM_ATTR_NUM           4
#define STREAM_ATTR_BANDWIDTH     1
#define STREAM_ATTR_PROGRAM_ID    2
#define STREAM_ATTR_RESOLUTION    4
#define STREAM_ATTR_CODEC         8

#define TYPE_PLAYLIST             1
#define TYPE_MEDIA                2
#define TYPE_STREAM               3
#define TYPE_KEY	              4
#define TYPE_DISCONTINUITY_TAG    5

#define HLS_OK    0
#define HLS_FAIL  1

#define TRUE      1
#define FALSE     0

#define LINE_BUF_SIZE				512

#define TYPE_EVENT_THREAD           20


    typedef enum PLAYLIST_CONTENT_TYPE_S
	{
		TYPE_NONE,
		TYPE_EVENT_SLIDING,			
		TYPE_EVENT_APPENDING,
		TYPE_VOD,
		TYPE_PROGRAM
	}
	PLAYLIST_CONTENT_TYPE;
	
    typedef enum PLAYLIST_TYPE_S
    {
        PLAYLIST_UNKNOW = 0,
        PLAYLIST_PLS,
        PLAYLIST_ASX,
        PLAYLIST_M3U,
        PLAYLIST_XSPF,
        PLAYLIST_M3U8,
    }
    PLAYLIST_TYPE;

    typedef enum  MEDIA_FILE_STATUS_S
    {
        MEDIA_FILE_NONE = 0,
        MEDIA_FILE_DOWNLOADING,
        MEDIA_FILE_CACHED,                /* download complete and cached in system */
        MEDIA_FILE_CONSUMED,              /* consume by playback */
        MEDIA_FILE_CACHE_FAIL,
        MEDIA_FILE_DOWNLOAD_CANCEL
    } MEDIA_FILE_STATUS;

    typedef enum URL_TYPE_S
    {
        URL_ONLINE = 0,
        URL_LOCAL,
        URL_UNKNOW
    }URL_TYPE;

    typedef enum DUMP_CONDITION_S
    {
        DUMP_CONDITION_ALL,
        DUMP_CONDITION_MEDIA,
        DUMP_CONDITION_KEY,
        DUMP_CONDITION_CACHED,
        DUMP_CONDITION_CONSUMED,
        DUMP_CONDITION_CACHE_FAILED
    }DUMP_CONDITION;

    typedef enum DUMP_TYPE_S
    {
        DUMP_TO_FILE,
        DUMP_TO_CONSOLE
    }DUMP_TYPE;
    
    struct RESOLUTION_INFO
    {
        int x;
        int y;
    };

    struct CHAR_BUFFER
    {
        size_t i4_total_size;
        size_t i4_used_size;
        char *pac_buffer;
    };

    struct ENTRY_INFO
    {
        int type;
        struct ENTRY_INFO *prev;
        struct ENTRY_INFO *next;

    };


    struct KEY_INFO
    {
        struct ENTRY_INFO entry_info;
        int	 method;
        char *ps_url;                     /* url specifies how to obtain the key */
        char key[KEY_INFO_METHOD_AES_128];
        char iv[KEY_INFO_METHOD_AES_128];  /* sequence number / IV attribute */
        struct CHAR_BUFFER  url_header;
        struct CHAR_BUFFER  url_content;
		bool bHaveIV;
		bool bDownError;
        /* curl */
        CURL *pt_curl;
        struct curl_slist   *palias_hdr_slist;

        time_t downloadTime;
    };

    struct MEDIA_INFO
    {
        volatile struct ENTRY_INFO entry_info;
        volatile int sequence_number;
        char *ps_url;
        volatile char *title;        /* human-readable informative title of the media segment */
        volatile char *ext_x_program_date_time;   /* absolute date and time */
        volatile int  duration;      /* specifies the duration of the media file in seconds */
        struct KEY_INFO *pt_media_key;
        struct CHAR_BUFFER  url_header;
        struct CHAR_BUFFER  url_content;
        volatile size_t content_length;
        volatile int result_code;
        volatile MEDIA_FILE_STATUS status;
        volatile int download_result;
        volatile bool switchStream;
		
        /* curl */
        CURL *pt_curl;
        struct curl_slist   *palias_hdr_slist;
		double downloadSpeed;
		
    };

#define MINFO_NEXT(m)                           \
    ((MEDIA_INFO*)((m)->entry_info.next))
#define MINFO_PREV(m)                           \
    ((MEDIA_INFO*)((m)->entry_info.prev))

    struct PLAYLIST_INFO_TYPE
    {
        const char * name;
        PLAYLIST_TYPE type;
    };
    struct STREAM_INFO;
    
    struct HLS_PLAYLIST
    {
        struct ENTRY_INFO entry_info;
        char *ps_url;
        char *ps_hostUrl;
        int url_type;
        int  i4_numOfEntry;
        time_t downloadTime;
        int  duration;
        int  ext_x_targetduration;     /* maximum EXTINF value of any media file ex: 10 seconds */
        int  ext_x_media_sequence;     /* default 0 */
        int  ext_x_version;
        volatile int  ext_x_endlist;
        volatile int startToPlay;
        int  ext_x_allow_cache;       /* applies to all segments in the playlist */
        PLAYLIST_TYPE       playlistType;
        void *pv_entry_head;
        void *pv_entry_tail;
        volatile PLAYLIST_CONTENT_TYPE  playlistContentType;
        struct KEY_INFO * pt_key;
        struct CHAR_BUFFER  url_content;
        struct CHAR_BUFFER  url_header;
        struct fifo_t* fifo;

        /* curl- these two must be free in request function */
        CURL                *pt_curl;
        struct curl_slist   *palias_hdr_slist;

        /* temp */
        char next_program_date_time[20];   /* absolute date and time */
        // added by jinlong for more advanced control
        void * controller;
        int downloadBandWith;   // should be calculated by Tiffany todo ....
        volatile STREAM_INFO * currentStream;
        
        int timeBuffered;
        /* Total content_length of cached media file */
        char byteLoaded;
        double downloadSpeed;

        /* debug info */
        int dynamic_update_count;

    };

    struct STREAM_INFO
    {
        struct ENTRY_INFO entry_info;
        char *ps_url;
        int bandwidth;
        int program_id;
        char *codecs;
        struct RESOLUTION_INFO resolution;
        int attr_flag;
        struct HLS_PLAYLIST * pt_playlist;
        // added be jinlong for stream select
        MEDIA_INFO * currentMedia;
    };

    typedef int (*tag_function)(struct HLS_PLAYLIST * pt_playlist,  char * data,  char * content);

    struct HLS_TAG_FUNCTION
    {
        const char * tag;
        tag_function  parse_function;
    };

    /**********************/
    /* function prototype */
    /**********************/
    int free_playlist(struct HLS_PLAYLIST * pt_playlist);
    int update_playlist(struct HLS_PLAYLIST * pt_dst_playlist, struct HLS_PLAYLIST * pt_src_playlist);
    void dump_encrypt_key(struct KEY_INFO * pt_key);
    int dump_playlist(struct HLS_PLAYLIST * pt_playlist, DUMP_CONDITION cond, DUMP_TYPE type);
    struct HLS_PLAYLIST * new_hls_playlist(const char * ps_url);
    int parse_extended_m3u_playlist_info(struct HLS_PLAYLIST * pt_playlist);
    int parse_extended_m3u8_playlist_info(struct HLS_PLAYLIST * pt_playlist);
    char * read_line(const char * src, char * dest);
    struct HLS_PLAYLIST * new_playlist(const char * ps_url);
    PLAYLIST_TYPE get_playlist_type(struct HLS_PLAYLIST * pt_playlist);
    int download_playlist(struct HLS_PLAYLIST * pt_playlist);
    int read_playlist(struct HLS_PLAYLIST * pt_playlist);
    int request_playlist(struct HLS_PLAYLIST * pt_playlist, int min_entry_num);
    int optimize_playlist(struct HLS_PLAYLIST * pt_playlist, int max_entry_num);   
    int request_encrypt_key(struct KEY_INFO * pt_media_key);
    int parse_ext_m3u8_playlist_info(struct HLS_PLAYLIST * pt_playlist);
    int parse_ext_m3u_playlist_info(struct HLS_PLAYLIST * pt_playlist);
    struct MEDIA_INFO * get_next_media_file(struct HLS_PLAYLIST * pt_playlist,
                                            struct MEDIA_INFO * pt_cur_media_info);
    int download_url_file(char * ps_url, char * filename);
    int download_media_file(struct MEDIA_INFO * pt_media_info);
    void stop_download();
    void start_download();
    void * http_live_streaming_thread(void  * pv_data);


    /*********************************/
    /*   PLS PLAYLIST                */
    /*********************************/

    struct PLS_ENTRY
    {
        struct ENTRY_INFO entry_info;
        int  sequence_number;
        char *ps_url;
        char *ps_title;
        int  length;
    };

    struct PLS_PLAYLIST
    {
        char *ps_url;
        int  i4_numOfEntry;
        int   version;
        PLAYLIST_TYPE       playlistType;
        struct PLS_ENTRY    *media_info;
        struct CHAR_BUFFER  url_content;
        struct CHAR_BUFFER  url_header;

        /* curl- these two must be free in request function */
        CURL                *pt_curl;
        struct curl_slist   *palias_hdr_slist;

    };

    typedef int (*pls_tag_function)(struct PLS_PLAYLIST * pt_playlist,  char * data);

    struct PLS_PLAYLIST_TAG_FUNCTION
    {
        char * tag;
        pls_tag_function  parse_function;
    };

    /* function prototype */
    int parse_pls_playlist_info(struct PLS_PLAYLIST * pt_playlist);
    int dump_pls_playlist(struct PLS_PLAYLIST * pt_playlist);

	void moveCharBuffer(struct CHAR_BUFFER & t_charbuf_dest, struct CHAR_BUFFER & t_charbuf_src);
	MEDIA_INFO *getFirstMedia(HLS_PLAYLIST * pt_playlist);
	MEDIA_INFO * getMediaLocation(HLS_PLAYLIST * pt_playlist, char * p_uri);
	int get_http_header(struct HLS_PLAYLIST * pt_playlist);
	int free_some_media_data(HLS_PLAYLIST * pt_hls_list, int iCount);
#ifdef __cplusplus
}
#endif


#endif
