#include "PppoeService.h"

#include <binder/IServiceManager.h>

#include <binder/IPCThreadState.h>

#include <pthread.h>
#include <android/log.h>
#include "os/Thread.h"

#include <unistd.h>
#include <errno.h>
#include <signal.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <dirent.h>

#include "cutils/properties.h"


#include "NetStatus.h"

using namespace android;

namespace pppoe {
    static const std::string TAG = "pppoeservice";
    static int status = PppoeService::Disconnect;


   /* static const char* PPPOE_PATH = "/system/bin/pppd";
    static const char* PPPOE_CMD = "pppd";
    static const char* PPPOE_ARGV_1 = "pty";
    static const char* PPPOE_ARGV_2 = "\"/system/bin/pppoe -p /tmp/pppoe.conf-pppoe.pid.pppoe -I eth0 -T 80 -U -m 1412\"";
    static const char* PPPOE_ARGV_3 = "noipdefault";
    static const char* PPPOE_ARGV_4 = "noauth";
    static const char* PPPOE_ARGV_5 = "default-asyncmap";
    static const char* PPPOE_ARGV_6 = "defaultroute";
    static const char* PPPOE_ARGV_7 = "hide-password";
    static const char* PPPOE_ARGV_8 = "nodetach";
    static const char* PPPOE_ARGV_9 = "mtu";
    static const char* PPPOE_ARGV_10 = "1492";
    static const char* PPPOE_ARGV_11 = "mru";
    static const char* PPPOE_ARGV_12 = "1492";
    static const char* PPPOE_ARGV_13 = "noaccomp";
    static const char* PPPOE_ARGV_14 = "nodeflate";
    static const char* PPPOE_ARGV_15 = "nopcomp";
    static const char* PPPOE_ARGV_16 = "novj";
    static const char* PPPOE_ARGV_17 = "novjccomp";
    static const char* PPPOE_ARGV_18 = "lcp-echo-interval";
    static const char* PPPOE_ARGV_19 = "20";
    static const char* PPPOE_ARGV_20 = "lcp-echo-failure";
    static const char* PPPOE_ARGV_21 = "3";
    static const char* PPPOE_ARGV_22 = "usepeerdns";
    static const char* PPPOE_ARGV_23 = "debug";*/

    static const char* PPPOE_ARGV_USER = "user";
    static const char* PPPOE_ARGV_PASSWORD = "password";

    static const char* PPPOE_COMMAND = "/system/bin/pppd pty '/system/bin/pppoe -p /tmp/pppoe.conf-pppoe.pid.pppoe -I ";
    static const char * PPPOE_COMMAND_TAIL = " -T 80 -U -m 1412' noipdefault noauth default-asyncmap defaultroute hide-password nodetach  mtu 1492 mru 1492 noaccomp nodeflate nopcomp novj novjccomp lcp-echo-interval 20 lcp-echo-failure 3 usepeerdns debug ";
    static const char* PPPOE_COMMAND_HANGUP_PREX = "/system/bin/pppd";
    static const char* PPPOE_COMMAND_HANGUP_DEVICE = "/dev/pts"; 
    static const char* PPPOE_COMMAND_HANGUP_END = "disconnect defaultroute";




    static const char* GET_ROUTE = "route -n | grep \"^0\.0\.0\.0 \" | grep "; 
    static const char* GET_ROUTE_TAIL = " | awk '{print $2}'";

    static const char* DELETE_ROUTE = "route del default dev ";
    static const char* ADD_ROUTE = "route add default gw ";
    static const char* ADD_ROUTE_TAIL_MATRIC1 = " metric 1 dev ";
    static const char* ADD_ROUTE_TAIL_MATRIC0 = " metric 0 dev ";



    static void modify(std::string & dev, const char * info){
        std::string get;
        get.append(GET_ROUTE).append(dev).append(GET_ROUTE_TAIL);
        FILE * fp = popen(get.c_str(), "r");

        char buf[16];
        memset(buf, 0, 16);
        while(fgets(buf, sizeof(buf), fp))
        {
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "%s", buf);
        }

        pclose(fp);

        if (strlen(buf) == 0)
        {
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "no default route");
            return ;
        }
        

        std::string del;
        del.append(DELETE_ROUTE).append(dev);
        fp = popen(del.c_str(), "r");

        
        memset(buf, 0, 16);
        while(fgets(buf, sizeof(buf), fp))
        {
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "%s", buf);
        }

        pclose(fp);

        std::string add;
        add.append(ADD_ROUTE).append(dev).append(info);
        fp = popen(add.c_str(), "r");

        memset(buf, 0, 16);
        while(fgets(buf, sizeof(buf), fp))
        {
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "%s", buf);
        }

        pclose(fp);

    }


    class PppoeThread : os::Thread {
    public:
        static const int RETRY_COUNT = 30;
    public:
        PppoeThread(std::string & dev, std::string & user, std::string & password);
        ~PppoeThread();

    public:
        void Run();
        pid_t getpid(){return pid;};
    private:
        pid_t pid;
        bool running;   
        std::list<std::string> list;

        /*struct sigaction oldPipeaction;*/
    };

    void PppoeThread::Run()
    {
        int count = 0;
        //std::list<net::DeviceInfo*> list;
        while(running) {
            /*list = net::NetStatus::getActiveDevices();
            if (list.size() > 0)
            {
            bool success = false;
            std::list<net::DeviceInfo*>::iterator it ;
            for (it = list.begin(); it != list.end(); it ++)
            {
            net::DeviceInfo* dev = (*it);
            if (dev->type == net::POINTTOPOINT)
            {
            success = true;
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PPPOE DIAL OK");
            }
            delete dev;
            }
            list.clear();
            if (success)
            {
            status = PppoeService::Connect;
            break;
            }
            } */
            if (net::NetStatus::isPPPReady())
            {
                status = PppoeService::Connect;
                break;                
            }

            {
                count ++;
                os::Thread::sleep(1000);
                __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PPPOE detect %d", count);
                if (count >= RETRY_COUNT)
                {
                    status = PppoeService::Disconnect;
                    break;
                }                
            }
        }

        property_set("net.pppoe.error", "TIMEOUT");
    }

    PppoeThread::PppoeThread(std::string & dev, std::string & user, std::string & password)
    {
        running = false;
        pid = 0;
        /*fd = popen(command.c_str(), "r");
        if (fd == NULL)
        {
        return ;
        }*/

        /*struct sigaction pipeaction;
        pipeaction.sa_handler = SIGTERM;
        ::sigemptyset(&pipeaction.sa_mask);
        pipeaction.sa_flags = 0;        
        ::sigaction(SIGPIPE, &pipeaction, &oldPipeaction);*/

        //signal(SIGCHLD, SIG_IGN);

        pid = fork(); 
        switch (pid) {
    case -1:{  
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread fork fail");
        return ;
            }
    case 0:  { 
        setsid();                
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread start execl ");
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread u and p %s %s", user.c_str(), password.c_str());
        std::string u = "\"";
        u.append(user);
        u.append("\"");

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread user %s", u.c_str());

        std::string p = "\"";
        p.append(password);
        p.append("\"");

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread password %s", p.c_str());


        std::string argv = PPPOE_COMMAND;
        argv.append(dev.c_str());
        argv.append(PPPOE_COMMAND_TAIL);
        argv.append(PPPOE_ARGV_USER).append(" ").append(u).append(" ").append(PPPOE_ARGV_PASSWORD).append(" ").append(p);

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread open %s ", argv.c_str());


        FILE * fp = popen(argv.c_str(), "r");
        /*int ret = execl (PPPOE_PATH, PPPOE_PATH, //argv.c_str(),
        PPPOE_ARGV_1, PPPOE_ARGV_2, PPPOE_ARGV_3, PPPOE_ARGV_4, PPPOE_ARGV_5, PPPOE_ARGV_6, PPPOE_ARGV_7, PPPOE_ARGV_8,  
        PPPOE_ARGV_9, PPPOE_ARGV_10, PPPOE_ARGV_11, PPPOE_ARGV_12, PPPOE_ARGV_13, PPPOE_ARGV_14, PPPOE_ARGV_15, PPPOE_ARGV_16, 
        PPPOE_ARGV_17, PPPOE_ARGV_18, PPPOE_ARGV_19, PPPOE_ARGV_20, PPPOE_ARGV_21, PPPOE_ARGV_22, PPPOE_ARGV_23, 
        PPPOE_ARGV_USER, u.c_str(), PPPOE_ARGV_PASSWORD, p.c_str(), (char*)0);*/

        char buf[256];
        while(fgets(buf, sizeof(buf), fp))
        {
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "%s", buf);
        }
        status = PppoeService::Disconnect;

        //__android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread end execl %d errno = %d", ret, errno);

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread end execl %d errno = %d", (int)fp, errno);

        pclose(fp);
        //exit (ret);
        exit (0);
             }
    default:        
        break;
        }

        running = true;
        Start();
    }

    bool isNumberString(char * str)
    {
        if (str == NULL)
        {
            return false;
        }

        for (int i = 0; i < strlen(str); i++)
        {
            if ((str[i] < '0')||(str[i] > '9'))
            {
                return false;
            }            
        }
        return true;        
    }

    PppoeThread::~PppoeThread()
    {
        running = false;
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread start to destroy PppoeThread");
        /*if (fd != NULL)
        {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread start to close");
        pclose(fd);
        fd = NULL;
        }*/

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread start to kill %d ", pid);

        if (pid > 0)
        {            

            std::string device_name = PPPOE_COMMAND_HANGUP_DEVICE;
            DIR* mDir;
            mDir = opendir(device_name.c_str());
            if (mDir != NULL)
            {
                while (true)
                {
                    dirent* result = NULL;
                    result = readdir(mDir);

                    if ((result == NULL) || (result->d_name == NULL))
                    {
                        break;
                    }
                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread add path address %x ", result->d_name);
                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread add path %s ", result->d_name);

                    if (strcmp(result->d_name, ".") != 0 && strcmp(result->d_name, "..") != 0)
                    {
                        list.push_back(result->d_name);  
                    }

                } 
                closedir(mDir);
            }

            std::list<std::string>::iterator it;

            for (it=list.begin(); it!=list.end();it++)
            {
                __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread hang up device %s/%s ", device_name.c_str(), (*it).c_str());

                std::string argv = PPPOE_COMMAND_HANGUP_PREX;
                argv.append(" ").append(device_name);  
                argv.append("/").append((*it).c_str());  
                argv.append(" ").append(PPPOE_COMMAND_HANGUP_END);

                FILE * fp = popen(argv.c_str(), "r");
                char buf[256];
                while(fgets(buf, sizeof(buf), fp))
                {
                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "%s", buf);
                }

                pclose(fp);
            }  
            list.clear();

            status = PppoeService::Disconnect;            

            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread kill %d errno = %d", pid, errno);

            int ret = kill(pid, SIGKILL);
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread kill %d errno = %d", ret, errno);



            int group = getpgid(pid);
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread start to kill group %d ", group);


            ret = killpg(group, SIGKILL);
            __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread kill group %d errno = %d", ret, errno);
        }        

        status = PppoeService::Disconnect;

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread end to kill ");

        Wait(); 
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeThread end to destroy");

        /*::sigaction(SIGPIPE, &oldPipeaction, NULL);*/

    }

    static PppoeThread * pppoe = NULL;

    int PppoeService::instantiate() {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeService instantiate");

        int r = defaultServiceManager()->addService(String16(SERVICE_NAME.c_str()), new PppoeService());

        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeService r = %d\n", r);

        return r;
    }

    PppoeService::PppoeService()
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeService created");
    }

    PppoeService::~PppoeService()
    {
        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeService destroyed");
    }

    status_t PppoeService::onTransact(
        uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags){
            switch(code) {
            case DIAL_UP:
                {
                    property_set("net.pppoe.error", "UNKNOWN");

                    int ret = 0;

                    if (pppoe != NULL)
                    {
                        reply->writeInt32(1);

                        return NO_ERROR;
                    }

                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeService start to dial up");

                    const char * device = data.readCString();
                    const char * username = data.readCString();
                    const char * password = data.readCString();

                    std::string d(device);
                    std::string u(username);
                    std::string p(password);

                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "username %s ", u.c_str());
                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "password %s ", p.c_str());


                    pppoe = new PppoeThread(d, u, p);

                    status = PppoeService::Connecting;

                    reply->writeInt32(ret);

                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeService end to dial up");

                    return NO_ERROR;
                }
                break;

            case HANG_UP:
                {
                    int ret = 0;
                    pid_t pid = 0;

                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeService start to hang up");

                    if (pppoe != NULL)
                    {
                        pid = pppoe->getpid();
                        delete pppoe;
                        if (pid != 0)
                        {
                            int status;
                            waitpid(pid, &status, 0);
                        }

                        pppoe = NULL;
                    }

                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "PppoeService end to hang up");

                    status = PppoeService::Disconnect;
                    reply->writeInt32(ret);

                    return NO_ERROR;
                }
                break; 
            case GET_INFO:
                {
                    reply->writeInt32(status);

                    return NO_ERROR;
                }
                break;

            case MONITOR_START:
                {
                    const char * device = data.readCString();

                    std::string dev(device);
                    modify(dev, ADD_ROUTE_TAIL_MATRIC1);
                    return NO_ERROR;
                }
                break;

            case MONITOR_STOP:
                {
                    const char * device = data.readCString();

                    std::string dev(device);
                    modify(dev, ADD_ROUTE_TAIL_MATRIC0);
                    return NO_ERROR;
                }
                break;

            case GET_DEVICES:
                {
                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "Get Devices");

                    std::list<std::string> list = net::NetStatus::getDevices();

                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "Has device %d", list.size());

                    reply->writeInt32(list.size());

                    for (std::list<std::string>::iterator it = list.begin();
                        it != list.end();
                        it++)
                    {
                        __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "Add device %s ", (*it).c_str());
                        reply->writeCString((*it).c_str());
                    }
                    __android_log_print(ANDROID_LOG_DEBUG, TAG.c_str(), "Get Devices");
                    return NO_ERROR;
                }
                break;

            default:

                return BBinder::onTransact(code, data, reply, flags);
            }
    }

    const std::string PppoeService::SERVICE_NAME = "pppoe.dial";
};


