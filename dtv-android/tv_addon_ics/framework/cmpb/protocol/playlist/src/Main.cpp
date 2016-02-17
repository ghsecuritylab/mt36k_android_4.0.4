#include "PlaylistPlayer.h"
#include "StreamSelector.h"
#include <string.h>
#include <iostream>
#include <strings.h>
#include "Log.h"

#ifdef PRINT_TIME
#include <sys/time.h>
#include <time.h>
#endif

using namespace hls;
int log_level = 1;
class MyApp : public PlaylistPlayerListener
{
    virtual void notifyPlaylistPlayerListener(int event);
    virtual void notifyBegin();
    virtual void notifyEnd();
};

void MyApp:: notifyPlaylistPlayerListener(int event)
{
}

void MyApp::notifyBegin()
{
}

void MyApp:: notifyEnd()
{

}

char * g_main_argv;
int g_main_argc;

#define MAX_NUM_HANDLES      ((unsigned short) 4096)
#define SYS_MEM_SIZE ((unsigned int) 12 * 1024 * 1024)

typedef struct _THREAD_DESCR_T
{
    unsigned int  z_stack_size;
    unsigned char  ui1_priority;
    unsigned short  ui2_num_msgs;
}   THREAD_DESCR_T;

typedef struct _GEN_CONFIG_T
{
    unsigned short  ui2_version;
    void*  pv_config;
    unsigned int  z_config_size;
    THREAD_DESCR_T  t_mheg5_thread;
}   GEN_CONFIG_T;

extern "C" int c_rpc_init_client(void);
extern "C" int c_rpc_start_client(void);

extern "C" int os_init(const void *pv_addr, unsigned int z_size);
extern "C" int handle_init (unsigned short   ui2_num_handles,
                            void**   ppv_mem_addr,
                            unsigned int*  pz_mem_size);
extern "C" int x_rtos_init (GEN_CONFIG_T*  pt_config);

#define MAX_NUM_HANDLES      ((unsigned short) 4096)

using namespace std;

void init_rpc(void)
{
    GEN_CONFIG_T  t_rtos_config;
    bzero(&t_rtos_config, sizeof(GEN_CONFIG_T));
    void*       pv_mem_addr = 0;
    unsigned int z_mem_size = 0xc00000;
    int ret = 0;

    ret = x_rtos_init(&t_rtos_config);
    if (ret != 0)
    {
        printf("rtos init failed %d \n", ret);
    }
    ret = handle_init(MAX_NUM_HANDLES, &pv_mem_addr, &z_mem_size);
    if (ret != 0)
    {
        printf("handle init failed %d \n", ret);
    }
    ret = os_init(pv_mem_addr, z_mem_size);
    if (ret != 0)
    {
        printf("os init failed %d \n", ret);
    }
    ret = c_rpc_init_client();
    if (ret != 0)
    {
        printf("rpc init failed %d \n", ret);
    }
    ret = c_rpc_start_client();
    if (ret < 0)
    {
        printf("rpc start failed %d \n", ret);
    }
    printf("Rpc init OK\n");
}

static MyApp * g_app = NULL;

static PlaylistPlayer * g_player = NULL;

static IMTK_PB_CB_ERROR_CODE_T playlist_event_notify(IMTK_PB_CTRL_EVENT_T eEventType, void* pvTag, unsigned int u4Data)
{
    switch (eEventType)	
    {
        case IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE:
        {
            //fprintf(stdout, "IMTK_PB_CTRL_EVENT_CUR_TIME_UPDATE %d\n", u4Data / 1000);
            break;
        }
        case IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE: 
        {
            fprintf(stdout, "IMTK_PB_CTRL_EVENT_TOTAL_TIME_UPDATE \n");
            break;
        }
        case IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW:
        {
            fprintf(stdout, "IMTK_PB_CTRL_EVENT_BUFFER_UNDERFLOW \n");
            break;
        }
        case IMTK_PB_CTRL_EVENT_EOS: 
        {
            fprintf(stdout, "IMTK_PB_CTRL_EVENT_EOS \n");
            break;
        }
        case IMTK_PB_CTRL_EVENT_STEP_DONE: 
        {
            fprintf(stdout, "IMTK_PB_CTRL_EVENT_STEP_DONE \n");
            break;
        }
        case IMTK_PB_CTRL_EVENT_GET_BUF_READY:
        {
            fprintf(stdout, "IMTK_PB_CTRL_EVENT_GET_BUF_READY \n");
            break;
        }
        case IMTK_PB_CTRL_EVENT_PLAYBACK_ERROR:
        {
            fprintf(stdout, "IMTK_PB_CTRL_EVENT_ERROR \n");
            break;
        }
        case IMTK_PB_CTRL_EVENT_PLAY_DONE:
        {
#ifdef PRINT_TIME
            struct timeval timeV = {0};
            gettimeofday(&timeV, NULL);
            LOG(-1, "[1]Current time: [%d]s.[%d]ms!!!\n", timeV.tv_sec, timeV.tv_usec / 1000);
#endif
            fprintf(stdout, "IMTK_PB_CTRL_EVENT_PLAYED \n");
            fprintf(stdout, "---[INFO]---Duration is [%d]s!\n", g_player->duration());
            break;
        }
        default :
        {
            fprintf(stdout, "call back default %d \n", eEventType);
            break;
        }
    }

    fflush(stdout);
    return IMTK_PB_CB_ERROR_CODE_OK;
}

void set_log_level(int iLevel)
{
    log_level = iLevel;
}

int main(int argc, char *argv[])
{
    std::string cmd;
    if (argc < 2)
    {
        LOG(0, "please give the playlist url\n");
        return 0;
    }

    // got the uri
    std::string uri(argv[1]);
    LOG(5, "The uri %s will be played\n", uri.c_str());
    StreamSelector selector;
    Playlist* plist = NULL;
    init_rpc();

    g_app = new MyApp;
    
    while(1)
    {
        cout<<"please input the command:[play, quit, uri, pause, resume, seek, log]"<<endl;
        std::cin>>cmd;
        if (cmd == "quit")
        {
            // stop player
            Playlist::releasePlaylist(plist);
            g_player->stop();

            break;
        }
        else if (cmd == "play")
        {
            if (g_player == NULL)
            {
                plist = Playlist::createPlaylist(uri, &selector);
                if (plist == NULL)
                {
                    LOG(0, "The uri:%s is not a playlist\n", uri.c_str());
                    return 0;
                }
    
                g_player = new PlaylistPlayer(g_app);                   
                LOG(0, "Begin to start player!\n");                     
                g_player->start(*plist, playlist_event_notify, NULL, 0);
            }
        }   
        else if(cmd == "uri")
        {
            // set new uri
            std::string newuri;
            cout<<"Please give the new uri:";
            cin>>newuri;

            Playlist::releasePlaylist(plist);
            plist = Playlist::createPlaylist(newuri, &selector);
            if (plist == NULL)
            {
                LOG(0, "The uri:%s is not a playlist\n", newuri.c_str());
                continue;
            }
            LOG(5, "begin to play new uri\n");

            g_player->start(*plist, playlist_event_notify, NULL, 0);
        }
        else if (cmd == "pause")
        {
            g_player->pause();
        }
        else if (cmd == "resume")
        {
            g_player->resume();
        }
        else if (cmd == "seek")
        {
            int time = 0;
            fprintf(stdout, "---[TIPS]---Please input the time[0~%d]s to play:", g_player->duration());
            cin>>time;
            g_player->timeseek(time);
        }
        else if (cmd == "log")
        {
            std::string strLogLevel;
            fprintf(stdout, "---[TIPS]---Please input the log level[0~9]:");
            cin>>strLogLevel;
            set_log_level(atoi(strLogLevel.c_str()));
        }
    }

    return 0;
}
