#include <jni.h>

#include "Dial.h"
#include "cutils/properties.h"


#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_com_mediatek_pppoe_PppoeService_nativeDialUp(JNIEnv * env, jobject thiz, jstring device, jstring username, jstring password)
{
	pppoe::Dial dial;
	const char * dev = env->GetStringUTFChars(device, NULL);
	const char * user = env->GetStringUTFChars(username, NULL);
	const char * pass = env->GetStringUTFChars(password, NULL);
	std::string d(dev);
	std::string u(user);
	std::string p(pass);
    int ret = dial.dialUp(d, u, p);
    env->ReleaseStringUTFChars(device, dev);
    env->ReleaseStringUTFChars(username, user);
    env->ReleaseStringUTFChars(password, pass);
    return ret;
}
JNIEXPORT jint JNICALL Java_com_mediatek_pppoe_PppoeService_nativeHangUp(JNIEnv * env, jobject thiz)
{
	pppoe::Dial dial;
    int ret = dial.hangUp();
    return ret;
}

JNIEXPORT jint JNICALL Java_com_mediatek_pppoe_PppoeService_nativeGetStatus(JNIEnv * env, jobject thiz)
{
	pppoe::Dial dial;
    int ret = dial.getStatus();
    return ret;
}

JNIEXPORT jstring JNICALL Java_com_mediatek_pppoe_PppoeService_nativeGetErrorCode(JNIEnv * env, jobject thiz)
{
    std::string info;
    char prop_value[PROPERTY_VALUE_MAX];
    memset(prop_value, 0, PROPERTY_VALUE_MAX);
    property_get("net.pppoe.error", prop_value, NULL);
    info.append(prop_value);
    return env->NewStringUTF(info.c_str());
}


JNIEXPORT void JNICALL Java_com_mediatek_pppoe_PppoeService_nativeMonitorStart(JNIEnv * env, jobject thiz, jstring device)
{
    const char * dev = env->GetStringUTFChars(device, NULL);
  
    std::string d(dev);
   
    env->ReleaseStringUTFChars(device, dev);
    pppoe::Dial dial;
        dial.start(d);

}

JNIEXPORT void JNICALL Java_com_mediatek_pppoe_PppoeService_nativeMonitorStop(JNIEnv * env, jobject thiz, jstring device)
{
    const char * dev = env->GetStringUTFChars(device, NULL);

    std::string d(dev);

    env->ReleaseStringUTFChars(device, dev);
     pppoe::Dial dial;
             dial.stop(d);

}

JNIEXPORT jstring JNICALL Java_com_mediatek_pppoe_PppoeService_nativeGetInfo(JNIEnv * env, jobject thiz)
 {

    std::string info;
    char prop_value[PROPERTY_VALUE_MAX];
    memset(prop_value, 0, PROPERTY_VALUE_MAX);
    property_get("net.pppoe.ip", prop_value, NULL);
    info.append(prop_value).append("#");
    memset(prop_value, 0, PROPERTY_VALUE_MAX);
    property_get("net.pppoe.mask", prop_value, NULL);
    info.append(prop_value).append("#");
    memset(prop_value, 0, PROPERTY_VALUE_MAX);
    property_get("net.pppoe.gw", prop_value, NULL);
    info.append(prop_value).append("#");
    memset(prop_value, 0, PROPERTY_VALUE_MAX);
    property_get("net.dns1", prop_value, NULL);
    info.append(prop_value).append("#");
    memset(prop_value, 0, PROPERTY_VALUE_MAX);
    property_get("net.dns2", prop_value, NULL);
    info.append(prop_value);
    return env->NewStringUTF(info.c_str());
}

JNIEXPORT jstring JNICALL Java_com_mediatek_pppoe_PppoeService_nativeGetDevices(JNIEnv * env, jobject thiz)
{

    std::string info;

    pppoe::Dial dial;

    std::list<std::string> list = dial.getDevices();

    for (std::list<std::string>::iterator it = list.begin();
        it != list.end();
        it++)
    {
        info.append(*it).append("#");
    }
   
    return env->NewStringUTF(info.c_str());
}

#ifdef __cplusplus
}
#endif
