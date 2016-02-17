#include "tv_jni_util.h"
#include "com_mediatek_tv_service_TVNative.h"
#include "config/a_cfg_custom.h"
#include "u_cfg.h"
#include "config/a_cfg.h"
#include "config/acfg_base_type.h"

EXTERN_C_START

#ifdef LOG_TAG
#undef LOG_TAG
#endif
#define LOG_TAG "[CFG_SVC-JNI]"

#if 0
#ifdef __cplusplus
#define JNI_NEW_INT_ARRAY(ENV,SIZE)                       ((ENV)->NewIntArray(SIZE))
#define JNI_SET_INT_ARRAY_REGION(ENV,ARRAY,SIZE,args)     ((ENV)->SetIntArrayRegion(ARRAY,0,SIZE,args))
#define JNI_GET_ARRAY_LEN(ENV,ARRAY)                      ((ENV)->GetArrayLength(ARRAY))
#define JNI_GET_INT_ARRAY_ELEMENTS(ENV,ARRAY)             ((ENV)->GetIntArrayElements(ARRAY,0))
#define JNI_RELEASE_INT_ARRAY(ENV,ARRAY,BODY)             ((ENV)->ReleaseIntArrayElements(ARRAY,BODY,0))

#define JNI_NEW_BYTE_ARRAY(ENV,SIZE)                      ((ENV)->NewByteArray(SIZE))
#define JNI_SET_BYTE_ARRAY_REGION(ENV,ARRAY,SIZE,args)    ((ENV)->SetByteArrayRegion(ARRAY,0,SIZE,args))
#define JNI_GET_BYTE_ARRAY_ELEMENTS(ENV,ARRAY             ((ENV)->GetByteArrayElements(ARRAY,0))
#define JNI_RELEASE_BYTE_ARRAY(ENV,ARRAY,BODY)            ((ENV)->ReleaseByteArrayElements(ARRAY,BODY,0))

#else

#define JNI_NEW_INT_ARRAY(ENV,SIZE)                       ((*ENV)->NewIntArray(ENV,SIZE))
#define JNI_SET_INT_ARRAY_REGION(ENV,ARRAY,SIZE,args)     ((*ENV)->SetIntArrayRegion(ENV,ARRAY,0,SIZE,args))
#define JNI_GET_ARRAY_LEN(ENV,ARRAY)                      ((*ENV)->GetArrayLength(ENV,ARRAY))
#define JNI_GET_INT_ARRAY_ELEMENTS(ENV,ARRAY)             ((*ENV)->GetIntArrayElements(ENV,ARRAY,0))
#define JNI_RELEASE_INT_ARRAY(ENV,ARRAY,BODY)             ((*ENV)->ReleaseIntArrayElements(ENV,ARRAY,BODY,0))

#define JNI_NEW_BYTE_ARRAY(ENV,SIZE)                      ((*ENV)->NewByteArray(ENV,SIZE))
#define JNI_SET_BYTE_ARRAY_REGION(ENV,ARRAY,SIZE,args)    ((*ENV)->SetByteArrayRegion(ENV,ARRAY,0,SIZE,args))
#define JNI_GET_BYTE_ARRAY_ELEMENTS(ENV,ARRAY)            ((*ENV)->GetByteArrayElements(ENV,ARRAY,0))
#define JNI_RELEASE_BYTE_ARRAY(ENV,ARRAY,BODY)            ((*ENV)->ReleaseByteArrayElements(ENV,ARRAY,BODY,0))
#endif

#endif

#define STR_CFG_CUSTOM_PART_SIZE   "grp_misc_custom_part_size"
#define STR_CFG_CUSTOM_CFG         "custom_cfg"



#define D_INTER_GET_RESULT_MAX_SIZE     512
static INT32 _g_ai4_d_interface_params[D_INTER_GET_RESULT_MAX_SIZE];


JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getCfg_1native(JNIEnv * env, jclass ckass, 
        jint input_src, jstring tvc_type, jobject params_value, jobject output_value)
{
    CHAR*   s_tvc_type      	= NULL;
    UINT16  ui2_cfg_id      	= 0;
    UINT16  ui2_cfg_value_type 	= 0;
    INT32   i4_ret          	= 0;
    SIZE_T  z_size          	= 0;
    INT32   i 					= 0;
    jclass  jclass_ConfigValue = NULL;

    JNI_LOGD(("{%s %d} Enter "LOG_TAIL, __FUNCTION__, __LINE__));
    
    /*check parameters*/
    if (env == NULL || input_src == NULL || tvc_type == NULL)
    {
        JNI_LOGE(("{%s %d}Invalid arguments"LOG_TAIL, __FUNCTION__, __LINE__));
        return -1;
    }

    /*get tv config type string*/
    s_tvc_type = JNI_GET_STRING_UTF_CHARS(env, tvc_type);
    if (s_tvc_type == NULL) 
    {
    	JNI_LOGE(("{%s %d}, s_tvc_type is NULL"LOG_TAIL, __FUNCTION__, __LINE__));
        return -1;
    }

    jclass_ConfigValue = JNI_GET_CLASS_BY_NAME(env, "com/mediatek/tv/common/ConfigValue");
    if (NULL == jclass_ConfigValue)
    {
        JNI_LOGE(("{%s %d}, jcalss_ConfigValue is NULL."LOG_TAIL, __FUNCTION__, __LINE__));
		JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);
        return -1;
    }

    if(strcmp(s_tvc_type, STR_CFG_CUSTOM_PART_SIZE) == 0)
    {
        JNI_LOGD(("{%s %d}come here to get custom part size"LOG_TAIL, __FUNCTION__, __LINE__));
		JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);
        if(JNI_IS_INSTANCE_OF(env, output_value, jclass_ConfigValue))
        {   
            JCOMV(env, output_value, Void, CMI(cfg_val_def, setIntValue), (jint)CFG_CUSTOM_PART_SZIE);
        }
        JNI_LOGD(("{%s %d}come here finish get custom part size"LOG_TAIL, __FUNCTION__, __LINE__));
    }
    else if(strcmp(s_tvc_type, STR_CFG_CUSTOM_CFG) == 0) //custom cfg part
    {
        JNI_LOGD(("{%s %d}come here to get custom cfg"LOG_TAIL, __FUNCTION__, __LINE__));
        if(JNI_IS_INSTANCE_OF(env, output_value, jclass_ConfigValue))
        {   
            INT32   i4_offset = 0;
            UINT32  ui4_read = 0;
            UINT8   aui1_cfg_val[CFG_CUSTOM_PART_SZIE] = {0};            
            INT32   i4_len = 0;
            
            jbyte   	ajbyte_val[CFG_CUSTOM_PART_SZIE];            
            jbyteArray  jbyteArray_args; 
            jintArray 	jintArray_val;
            jint 		ajint_elems[2];
        
			JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);

            jintArray_val = (jintArray) JCOM(env, output_value, Object, CMI(cfg_val_def, getIntArrayValue));
            if (NULL == jintArray_val)
            {
                JNI_LOGE(("{%s %d}, the input parameter is NULL."LOG_TAIL, __FUNCTION__, __LINE__));
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
                return -1;
            }
			
            i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jintArray_val);
            if (i4_len != 2)
            {
                JNI_LOGE(("{%s %d}, the input parameter size should be 2."LOG_TAIL, __FUNCTION__, __LINE__));
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
				JNI_DEL_LOCAL_REF(env, jintArray_val);
                return -1;
            }
			
            JNI_GET_INT_ARRAY_REGION(env, jintArray_val, 0, 2, &(ajint_elems[0]));
            
            JNI_LOGD(("{%s %d}the input parameter is (offset = %d, size = %d) = %d"LOG_TAIL, __FUNCTION__, __LINE__, ajint_elems[0], ajint_elems[1]));

            i4_offset = ajint_elems[0];
            z_size    = ajint_elems[1];

            if ( i4_offset + z_size > CFG_CUSTOM_PART_SZIE)
            {
                JNI_LOGE(("{%s %d}, offset + size is large than the customize size(0x%x)."LOG_TAIL, __FUNCTION__, __LINE__, CFG_CUSTOM_PART_SZIE));
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
				JNI_DEL_LOCAL_REF(env, jintArray_val);
                return -1;
            }

            JNI_LOGD(("{%s %d}EEP_OFFSET_CUSTOM_START = 0x%x"LOG_TAIL, __FUNCTION__, __LINE__, (EEP_OFFSET_CUSTOM_START + i4_offset)));
            i4_ret = a_cfg_eep_raw_read((UINT32)(EEP_OFFSET_CUSTOM_START + i4_offset), aui1_cfg_val, z_size, &ui4_read);

            JNI_LOGD(("{%s %d}i4_ret = %d, ui4_read = %d"LOG_TAIL, __FUNCTION__, __LINE__, i4_ret, ui4_read));
            
            for(i = 0; i < ui4_read; i++)
            {
#if 0
                JNI_LOGD(("{%s %d}get custom value = %d"LOG_TAIL, __FUNCTION__, __LINE__, aui1_cfg_val[i]));
#endif
                ajbyte_val[i] = aui1_cfg_val[i];                
            }
            
            jbyteArray_args = JNI_NEW_BYTE_ARRAY(env, ui4_read);
            
            JNI_SET_BYTE_ARRAY_REGION(env, jbyteArray_args, 0, ui4_read, ajbyte_val);
            
            JCOMV(env, output_value, Void, CMI(cfg_val_def, setByteArrayValue), jbyteArray_args);

            JNI_DEL_LOCAL_REF(env, jintArray_val);
            JNI_DEL_LOCAL_REF(env, jbyteArray_args);
            
        }
        JNI_LOGD(("{%s %d}come here finish get custom cfg"LOG_TAIL, __FUNCTION__, __LINE__));
    }
    else
    {
        UINT8 	ui1_cfg_ipt_src = (UINT8)input_src;
        UINT16 	ui2_cfg_grp = 0;
		
        i4_ret = a_cfg_map_string_2_id(s_tvc_type, &ui2_cfg_id, &ui2_cfg_value_type);
        if(i4_ret != APP_CFGR_OK)
        {
            JNI_LOGE(("{%s %d}No match config type!!!"LOG_TAIL, __FUNCTION__, __LINE__));
			if (NULL != jclass_ConfigValue)
		    {
		        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
		    }
			JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);
            return -1;
        }

        JNI_LOGD(("{%s %d}get s_tvc_type = %s, ui2_input_src = %d, ui2_cfg_id is 0x%x, ui2_cfg_value_type is %d"LOG_TAIL, 
                    __FUNCTION__, 
                    __LINE__, 
                    s_tvc_type,
                    input_src,
                    ui2_cfg_id,
                    ui2_cfg_value_type));
    
        JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);
        
        JNI_LOGD(("JNI_RELEASE_STRING_UTF_CHARS {%s %d}"LOG_TAIL, __FUNCTION__, __LINE__));

        ui2_cfg_grp = CFG_GET_GROUP(ui2_cfg_id);
		
        if (ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_AUDIO ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_TUNER ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_VIDEO ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_VIDEO_DECODER ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_EXTMJC ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_MISC ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_CUST_SPEC )
        {   /*it is d-interface*/
            jintArray 	jintArray_val;
            jint 		*pjint_elems;
            INT32 		i4_len = 0;
            INT32		*pi4_val = NULL; 
            BOOL   		b_malloc_param = FALSE;
            SIZE_T 		z_d_params_size = 0;
            SIZE_T 		z_d_params_tmp_size = 0;
            
            JNI_LOGD(("SEPERATE GROUP D_INTERFACE {%s %d}"LOG_TAIL, __FUNCTION__, __LINE__));
            
            if (ui2_cfg_value_type != APP_CFG_VALUE_TYPE_INT_ARRAY)
            {
                JNI_LOGE(("{%s %d}c_d_interface_cust_get!!!"LOG_TAIL, __FUNCTION__, __LINE__));
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
                return -1;
            } 
            
            jintArray_val = (jintArray) JCOM(env, params_value, Object, CMI(cfg_val_def,getIntArrayValue));       
            i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jintArray_val);
            z_d_params_size = i4_len;

            JNI_LOGD(("{%s %d}get value len= %d"LOG_TAIL, __FUNCTION__, __LINE__, i4_len));
            
            pjint_elems = (jint*)x_mem_alloc(z_d_params_size * sizeof(jint));
            if (NULL == pjint_elems)
            {
                JNI_LOGE(("{%s %d}not enough memory!!!"LOG_TAIL, __FUNCTION__, __LINE__));
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
				JNI_DEL_LOCAL_REF(env, jintArray_val);
                return -1;
            }
            
            if (z_d_params_size > D_INTER_GET_RESULT_MAX_SIZE)
            { 
                JNI_LOGD(("   {%s %d} z_d_params_size > D_INTER_GET_RESULT_MAX_SIZE"LOG_TAIL, __FUNCTION__, __LINE__));
                pi4_val = (INT32*)x_mem_alloc(z_d_params_size * sizeof(INT32));
                if (NULL == pi4_val)
                {
                    JNI_LOGE(("{%s %d}not enough memory!!!"LOG_TAIL, __FUNCTION__, __LINE__));
                    x_mem_free(pjint_elems);
					if (NULL != jclass_ConfigValue)
				    {
				        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
				    }
					JNI_DEL_LOCAL_REF(env, jintArray_val);
                    return -1;
                }
                z_d_params_tmp_size = z_d_params_size;
                b_malloc_param = TRUE;
            }
            else
            {       
                JNI_LOGD(("{%s %d} z_d_params_size <= D_INTER_GET_RESULT_MAX_SIZE"LOG_TAIL, __FUNCTION__, __LINE__));
                z_d_params_tmp_size = D_INTER_GET_RESULT_MAX_SIZE;
                pi4_val = _g_ai4_d_interface_params;
                b_malloc_param = FALSE;
            }        
            
            JNI_LOGD(("{%s %d}"LOG_TAIL, __FUNCTION__, __LINE__));
            JNI_GET_INT_ARRAY_REGION(env, jintArray_val, 0, i4_len, pjint_elems);
            JNI_LOGD(("{%s %d}"LOG_TAIL, __FUNCTION__, __LINE__));
            
            for(i = 0; i < z_d_params_size; i++)
            {
                JNI_LOGD(("{%s %d} PI4_VAL[%d] == %d"LOG_TAIL, __FUNCTION__, __LINE__ , i, pi4_val[i]));
                pi4_val[i] = (INT32)pjint_elems[i];
            }
            x_mem_free(pjint_elems);
            
            JNI_LOGD(("{%s %d}"LOG_TAIL, __FUNCTION__, __LINE__));

            pjint_elems = NULL;
                
            JNI_DEL_LOCAL_REF(env, jintArray_val);        
            
            JNI_LOGD(("{%s %d}"LOG_TAIL, __FUNCTION__, __LINE__));
            
            i4_ret = c_d_interface_cust_get(ui2_cfg_id, pi4_val, z_d_params_size, &z_d_params_tmp_size);
            if (i4_ret != APP_CFGR_OK)
            {
                JNI_LOGE(("{%s %d}c_d_interface_cust_get return %d!!!"LOG_TAIL, __FUNCTION__, __LINE__, i4_ret));
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
                return -1;
            }
            
            if(JNI_IS_INSTANCE_OF(env, output_value, jclass_ConfigValue))
            {
                jintArray   jintArray_args;
                jint* 		pjint_val;
               
                pjint_val = (jint*)x_mem_alloc(z_d_params_tmp_size * sizeof(jint));
                if (NULL == pjint_val)
                {
                    JNI_LOGE(("{%s %d}not enough memory!!!"LOG_TAIL, __FUNCTION__, __LINE__));
                    if (TRUE == b_malloc_param)
                    {
                        x_mem_free(pi4_val);
                    }
					if (NULL != jclass_ConfigValue)
				    {
				        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
				    }
                    return -1;
                }
                               
                for (i = 0; i < z_d_params_tmp_size; ++i)
                {
                    pjint_val[i] = (jint)pi4_val[i];
                }

                if (TRUE == b_malloc_param)
                {
                    x_mem_free(pi4_val);
                }
                pi4_val = NULL;
               
                jintArray_args = JNI_NEW_INT_ARRAY(env, z_d_params_tmp_size);
                   
                JNI_LOGD(("   {%s %d}"LOG_TAIL, __FUNCTION__, __LINE__));
                
                JNI_SET_INT_ARRAY_REGION(env, jintArray_args, 0, z_d_params_tmp_size, pjint_val);
               
                JCOMV(env, output_value, Void, CMI(cfg_val_def, setIntArrayValue), jintArray_args);

                JNI_DEL_LOCAL_REF(env, jintArray_args);
               
                JNI_LOGD(("   {%s %d}"LOG_TAIL, __FUNCTION__, __LINE__));
                x_mem_free(pjint_val);
                
                JNI_LOGD(("   {%s %d}"LOG_TAIL, __FUNCTION__, __LINE__));
            }
        }
        else
        {
            if (ui2_cfg_value_type == APP_CFG_VALUE_TYPE_INT_ARRAY || ui2_cfg_value_type == APP_CFG_VALUE_TYPE_BYTE_ARRAY)
            {
                INT16 	ai2_val[APP_CFG_VALUE_TYPE_ARRAY_MAX_LENGTH]; 
				
				JNI_LOGD(("{%s %d} intput z_size = %d ."LOG_TAIL, __FUNCTION__, __LINE__, z_size));
				z_size = APP_CFG_VALUE_TYPE_ARRAY_MAX_LENGTH;
                i4_ret = a_cfg_av_get(ui1_cfg_ipt_src, ui2_cfg_id, ai2_val, &z_size);
                JNI_LOGD(("{%s %d} a_cfg_av_get return %d!!!"LOG_TAIL, __FUNCTION__, __LINE__, i4_ret));
                if (z_size > APP_CFG_VALUE_TYPE_ARRAY_MAX_LENGTH)
                {
                    JNI_LOGE(("{%s %d}please enlarge your APP_CFG_VALUE_TYPE_ARRAY_MAX_LENGTH!!!"LOG_TAIL, __FUNCTION__, __LINE__));
					if (NULL != jclass_ConfigValue)
				    {
				        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
				    }
                    return -1;
                }
				
                JNI_LOGD(("{%s %d}!!!"LOG_TAIL, __FUNCTION__, __LINE__));
                if(JNI_IS_INSTANCE_OF(env, output_value, jclass_ConfigValue))
                {
                    if (ui2_cfg_value_type == APP_CFG_VALUE_TYPE_INT_ARRAY)
                    {
                        jintArray   jintArray_args;
                        jint ajint_val[APP_CFG_VALUE_TYPE_ARRAY_MAX_LENGTH] = {0};

                        for (i = 0; i < z_size; ++i)
                        {
                            ajint_val[i] = (jint)ai2_val[i];
                        }
                        
                        jintArray_args = JNI_NEW_INT_ARRAY(env, z_size);
                        
                        JNI_SET_INT_ARRAY_REGION(env, jintArray_args, 0, z_size, ajint_val);
                    
                        JCOMV(env, output_value, Void, CMI(cfg_val_def, setIntArrayValue), jintArray_args);
                
                        JNI_DEL_LOCAL_REF(env, jintArray_args);     
                    }
                    else /*APP_CFG_VALUE_TYPE_BYTE_ARRAY*/
                    {   
                        jbyteArray   jbyteArray_args;  
                        jbyte ajbyte_val[APP_CFG_VALUE_TYPE_ARRAY_MAX_LENGTH] = {0};
                        JNI_LOGD(("{%s %d}!!!"LOG_TAIL, __FUNCTION__, __LINE__));
                        for (i = 0; i < z_size; ++i)
                        {
                            JNI_LOGD(("{%s %d} %d char is %d!!!"LOG_TAIL, __FUNCTION__, __LINE__, i, (UINT8)ai2_val[i]));
                            ajbyte_val[i] = (UINT8)ai2_val[i];
                        }
                        JNI_LOGD(("{%s %d}!!!"LOG_TAIL, __FUNCTION__, __LINE__));
                        jbyteArray_args = JNI_NEW_BYTE_ARRAY(env, z_size);
                        
                        JNI_SET_BYTE_ARRAY_REGION(env, jbyteArray_args, 0, z_size, ajbyte_val);
                    
                        JCOMV(env, output_value, Void, CMI(cfg_val_def, setByteArrayValue), jbyteArray_args);
                
                        JNI_DEL_LOCAL_REF(env, jbyteArray_args);  
                    }
                }        
            }
            else if (ui2_cfg_value_type == APP_CFG_VALUE_TYPE_INT || ui2_cfg_value_type == APP_CFG_VALUE_TYPE_BOOLEAN)
            {
                INT16 i2_val = 0 ;
				
				z_size = sizeof(i2_val);
                a_cfg_av_get(ui1_cfg_ipt_src, ui2_cfg_id, &i2_val, &z_size);

                JNI_LOGD(("{%s %d}JNI get cfg val = %d"LOG_TAIL, __FUNCTION__, __LINE__, i2_val));      
                
                if(JNI_IS_INSTANCE_OF(env, output_value, jclass_ConfigValue))
                {
                    if (ui2_cfg_value_type == APP_CFG_VALUE_TYPE_INT)
                    {
                        JCOMV(env, output_value, Void, CMI(cfg_val_def, setIntValue), (jint)i2_val);
                    }
                    else /*APP_CFG_VALUE_TYPE_BOOLEAN*/
                    {
                        JCOMV(env, output_value, Void, CMI(cfg_val_def, setBoolValue), (jboolean)i2_val);
                    }
                }          
            }
            else
            {
            	JNI_LOGE(("{%s %d} invalid cfg_value_type %d."LOG_TAIL, __FUNCTION__, __LINE__, ui2_cfg_value_type));
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
                return -1;
            }
        }
    }

    JNI_LOGD(("   {%s %d}"LOG_TAIL, __FUNCTION__, __LINE__));

    if (NULL != jclass_ConfigValue)
    {
        JNI_LOGD(("   {%s %d}"LOG_TAIL, __FUNCTION__, __LINE__));
        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
    }

	JNI_LOGD(("{%s %d}get config ok."LOG_TAIL,__FUNCTION__, __LINE__));
    return 0;
}

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_setCfg_1native(JNIEnv * env, jclass ckass, 
        jint input_src, jstring tvc_type, jobject input_value)
{
    CHAR*   s_tvc_type      	= NULL;
    UINT16  ui2_cfg_id      	= 0;
    INT32   i4_ret          	= 0;
    INT32   i 					= 0;            
    UINT16  ui2_cfg_value_type 	= 0;
    SIZE_T  z_size 				= 0;
    jclass 	jclass_ConfigValue 	= NULL;

	JNI_LOGD(("{%s %d} Enter"LOG_TAIL, __FUNCTION__, __LINE__));
	
    /*check parameters*/
    if (env == NULL || input_src == NULL || tvc_type == NULL)
    {
        JNI_LOGE(("{%s %d}Invalid arguments"LOG_TAIL, __FUNCTION__, __LINE__));
        return -1;
    }

    /*get tv config type string*/
    s_tvc_type = JNI_GET_STRING_UTF_CHARS(env, tvc_type);
    if (s_tvc_type == NULL) 
    {
    	JNI_LOGE(("{%s %d} s_tvc_type is NULL"LOG_TAIL,  __FUNCTION__, __LINE__));
        return -1;
    }

    jclass_ConfigValue = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/common/ConfigValue");    

    if(strcmp(s_tvc_type, STR_CFG_CUSTOM_CFG) == 0) //custom cfg part
    {
    	JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);
        if(JNI_IS_INSTANCE_OF(env,input_value,jclass_ConfigValue))
        {
            
            INT32   i4_offset = 0;
            UINT8   aui1_cfg_val[CFG_CUSTOM_PART_SZIE];
            UINT32  ui4_read_cnt;
            jbyte   ajbyte_elems[CFG_CUSTOM_PART_SZIE];
            jbyteArray jbyteArrary_val;
            INT32   i4_len;
            
            jbyteArrary_val = (jbyteArray)JCOM(env, input_value, Object, CMI(cfg_val_def, getByteArrayValue));           
            i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jbyteArrary_val);
            i4_offset = (INT32)JCOM(env, input_value, Int, CMI(cfg_val_def, getIntValue));
        
            JNI_LOGD(("{%s %d}set value len= %d"LOG_TAIL, __FUNCTION__, __LINE__, i4_len));
        
            JNI_GET_BYTE_ARRAY_REGION(env, jbyteArrary_val, 0, i4_len, ajbyte_elems);
        
            z_size = i4_len;
        
            for(i = 0; i < i4_len; i++)
            {
#if 0            
                JNI_LOGD(("{%s %d}cfg value = %d"LOG_TAIL, __FUNCTION__, __LINE__, ajbyte_elems[i]));
#endif
                aui1_cfg_val[i] = (UINT8)ajbyte_elems[i];
            }

            JNI_LOGD(("{%s %d}i4_offset= %d"LOG_TAIL, __FUNCTION__, __LINE__, i4_offset));
            
            i4_ret = a_cfg_eep_raw_write((UINT32)(EEP_OFFSET_CUSTOM_START + i4_offset), aui1_cfg_val, z_size, &ui4_read_cnt);
            JNI_LOGD(("{%s %d}i4_ret= %d"LOG_TAIL, __FUNCTION__, __LINE__, i4_ret));
        
            JNI_DEL_LOCAL_REF(env, jbyteArrary_val);                
        }
    }
    else
    {  
        UINT16 	ui2_cfg_grp;
        UINT8 	ui1_cfg_ipt_src = (UINT8)input_src;
		
        i4_ret = a_cfg_map_string_2_id(s_tvc_type, &ui2_cfg_id, &ui2_cfg_value_type);
        if(i4_ret != APP_CFGR_OK)
        {
            JNI_LOGE(("{%s %d}No match config type!!!"LOG_TAIL, __FUNCTION__, __LINE__));
			if (NULL != jclass_ConfigValue)
		    {
		        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
		    }
			JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);
            return -1;
        }

        JNI_LOGD(("{%s %d}set s_tvc_type = %s, ui2_input_src = %d, ui2_cfg_id is 0x%x, ui2_cfg_value_type is %d"LOG_TAIL, 
                    __FUNCTION__, 
                    __LINE__, 
                    s_tvc_type,
                    input_src,
                    ui2_cfg_id,
                    ui2_cfg_value_type));

        JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);
        
        ui2_cfg_grp = CFG_GET_GROUP(ui2_cfg_id);

        if (ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_AUDIO ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_TUNER ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_VIDEO ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_VIDEO_DECODER ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_EXTMJC ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_MISC ||
            ui2_cfg_grp == APP_CFG_GRPID_D_INTERFACE_CUST_SPEC)
        {   /*it is d-interface*/
            jintArray 	jintArray_val;
            jint* 		pjint_elems;
            INT32 		i4_len;
            INT32* 		pi4_val; 
            SIZE_T 		z_d_params_size;
            
            if (ui2_cfg_value_type != APP_CFG_VALUE_TYPE_INT_ARRAY)
            {
                JNI_LOGE(("{%s %d}c_d_interface_cust_set!!!"LOG_TAIL, __FUNCTION__, __LINE__));
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
                return -1;
            } 

            jintArray_val = (jintArray) JCOM(env, input_value, Object, CMI(cfg_val_def,getIntArrayValue));   
            i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jintArray_val);
            z_d_params_size = i4_len;

            JNI_LOGD(("{%s %d}set value len= %d"LOG_TAIL, __FUNCTION__, __LINE__, i4_len));

            pjint_elems = (jint*)x_mem_alloc(z_d_params_size * sizeof(jint));
            if (NULL == pjint_elems)
            {
                JNI_LOGE(("{%s %d}not enough memory!!!"LOG_TAIL, __FUNCTION__, __LINE__));
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
				JNI_DEL_LOCAL_REF(env, jintArray_val);
                return -1;
            }
            
            pi4_val = (INT32*)x_mem_alloc(z_d_params_size * sizeof(INT32));
            if (NULL == pi4_val)
            {
                JNI_LOGE(("{%s %d}not enough memory!!!"LOG_TAIL, __FUNCTION__, __LINE__));
                x_mem_free(pjint_elems);
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
				JNI_DEL_LOCAL_REF(env, jintArray_val);
                return -1;
            }
            
            JNI_GET_INT_ARRAY_REGION(env, jintArray_val, 0, i4_len, pjint_elems);
            
            for(i = 0; i < i4_len; i++)
            {
                pi4_val[i] = (INT32)pjint_elems[i];
            }
            
            x_mem_free(pjint_elems);
            pjint_elems = NULL;
                
            JNI_DEL_LOCAL_REF(env, jintArray_val);      
            
            i4_ret = c_d_interface_cust_set(ui2_cfg_id, pi4_val, z_d_params_size);
            if (i4_ret != APP_CFGR_OK)
            {
                JNI_LOGE(("{%s %d}c_d_interface_cust_set return %d!!!"LOG_TAIL, __FUNCTION__, __LINE__, i4_ret));
                x_mem_free(pi4_val);
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
                return -1;
            }
            x_mem_free(pi4_val);     
        }
        else
        {
            if (ui2_cfg_value_type == APP_CFG_VALUE_TYPE_INT_ARRAY || ui2_cfg_value_type == APP_CFG_VALUE_TYPE_BYTE_ARRAY)
            {
                INT16 ai2_val[APP_CFG_VALUE_TYPE_ARRAY_MAX_LENGTH] = {0}; 
                int i;
                
                if(JNI_IS_INSTANCE_OF(env,input_value,jclass_ConfigValue))
                {
                    if (ui2_cfg_value_type == APP_CFG_VALUE_TYPE_INT_ARRAY)
			        {  
			            jintArray 	jintArray_val;
			            jint* 		pjint_elems;
			            INT32 		i4_len;
			            INT16* 		pi2_val; 
			            SIZE_T 		z_d_params_size;
			            
			            jintArray_val = (jintArray) JCOM(env, input_value, Object, CMI(cfg_val_def,getIntArrayValue));   
			            i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jintArray_val);
			            z_d_params_size = i4_len;

			            JNI_LOGD(("{%s %d}set value len= %d"LOG_TAIL, __FUNCTION__, __LINE__, i4_len));

			            pjint_elems = (jint*)x_mem_alloc(z_d_params_size * sizeof(jint));
			            if (NULL == pjint_elems)
			            {
			                JNI_LOGE(("{%s %d}not enough memory!!!"LOG_TAIL, __FUNCTION__, __LINE__));
							if (NULL != jclass_ConfigValue)
						    {
						        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
						    }
							JNI_DEL_LOCAL_REF(env, jintArray_val);
			                return -1;
			            }
			            
			            pi2_val = (INT16*)x_mem_alloc(z_d_params_size * sizeof(INT16));
			            if (NULL == pi2_val)
			            {
			                JNI_LOGE(("{%s %d}not enough memory!!!"LOG_TAIL, __FUNCTION__, __LINE__));
			                x_mem_free(pjint_elems);
							if (NULL != jclass_ConfigValue)
						    {
						        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
						    }
							JNI_DEL_LOCAL_REF(env, jintArray_val);
			                return -1;
			            }
			            
			            JNI_GET_INT_ARRAY_REGION(env, jintArray_val, 0, i4_len, pjint_elems);
			            
			            for(i = 0; i < i4_len; i++)
			            {
			                pi2_val[i] = (INT16)pjint_elems[i];							
			            }
			            
			            x_mem_free(pjint_elems);
			            pjint_elems = NULL;
			                
			            JNI_DEL_LOCAL_REF(env, jintArray_val);      
			            
			            i4_ret = a_cfg_av_set(ui1_cfg_ipt_src, ui2_cfg_id, pi2_val, z_d_params_size);
						
			            if (i4_ret != APP_CFGR_OK)
			            {
			                JNI_LOGE(("{%s %d}a_cfg_av_set32 return %d!!!"LOG_TAIL, __FUNCTION__, __LINE__, i4_ret));
			                x_mem_free(pi2_val);
							if (NULL != jclass_ConfigValue)
						    {
						        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
						    }
			                return -1;
			            }
			            x_mem_free(pi2_val);     
			        }						
                    else /*APP_CFG_VALUE_TYPE_BYTE_ARRAY*/
                    {
                        jbyteArray jbytArray_val;            
                        INT32 i4_len;
                        jbyte ajbyte_elems[APP_CFG_VALUE_TYPE_ARRAY_MAX_LENGTH];

                        jbytArray_val = (jbyteArray) JCOM(env, input_value, Object,CMI(cfg_val_def, getByteArrayValue));   
                        i4_len = (INT32)JNI_GET_ARRAY_LEN(env, jbytArray_val);
                            
                        JNI_LOGD(("{%s %d}set value len = %d"LOG_TAIL, __FUNCTION__, __LINE__, i4_len));
                
                        JNI_GET_BYTE_ARRAY_REGION(env, jbytArray_val, 0, 0, ajbyte_elems);
                
                        z_size = i4_len;
                
                        for(i = 0; i < i4_len; i++)
                        {
                            ai2_val[i] = (INT16)ajbyte_elems[i];
                        }
                
                        JNI_DEL_LOCAL_REF(env, jbytArray_val);  
						
						JNI_LOGD(("{%s %d}pre call a_cfg_av_set, ui2_cfg_id is 0x%x,  size is %d "LOG_TAIL, __FUNCTION__, __LINE__, ui2_cfg_id, z_size));
						a_cfg_av_set(ui1_cfg_ipt_src, ui2_cfg_id, ai2_val, z_size);
                    }
                }
            }
            else if (ui2_cfg_value_type == APP_CFG_VALUE_TYPE_INT || ui2_cfg_value_type == APP_CFG_VALUE_TYPE_BOOLEAN)
            {
                INT16 i2_cfg_val = 0;
                INT32 i4_cfg_val = 0;
                UINT16 ui2_cfg_grpid = 0;
                UINT16 ui2_cfg_recid = 0;
                
                ui2_cfg_recid = CFG_GET_SETTING(ui2_cfg_id);
                ui2_cfg_grpid = CFG_GET_GROUP(ui2_cfg_id);
                
                if(JNI_IS_INSTANCE_OF(env,input_value,jclass_ConfigValue))
                {
                    if (ui2_cfg_value_type == APP_CFG_VALUE_TYPE_INT)
                    {
                    
                        if((ui2_cfg_recid == APP_CFG_RECID_PWR_ON_TIMER )&&(ui2_cfg_grpid == APP_CFG_GRPID_MISC))
                        {
                           i4_cfg_val = (INT32)JCOM(env, input_value, Int, CMI(cfg_val_def, getIntValue));
                        }
                        else
                        {
                            i2_cfg_val = (INT16)JCOM(env, input_value, Int, CMI(cfg_val_def, getIntValue));
                        }
                    }
                    else /*APP_CFG_VALUE_TYPE_BOOLEAN*/
                    {
                        i2_cfg_val = (INT16)JCOM(env, input_value, Int, CMI(cfg_val_def, isBoolVal));      
                    }
                    JNI_LOGD(("{%s %d}pre call a_cfg_av_set, ui2_cfg_id is 0x%x,  value is %d "LOG_TAIL, __FUNCTION__, __LINE__, ui2_cfg_id, i2_cfg_val));

                    if((ui2_cfg_recid == APP_CFG_RECID_PWR_ON_TIMER )&&(ui2_cfg_grpid == APP_CFG_GRPID_MISC))
                    {
                    
                        a_cfg_av_set32(ui1_cfg_ipt_src, ui2_cfg_id, &i4_cfg_val, 1);
                    }
                    else
                    {
                        a_cfg_av_set(ui1_cfg_ipt_src, ui2_cfg_id, &i2_cfg_val, 1);
                    }
                } 
            }
            else
            {
            	JNI_LOGE(("{%s %d} invalid cfg_value_type %d"LOG_TAIL,  __FUNCTION__, __LINE__, ui2_cfg_value_type));
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
                return -1;
            }
        }
    }  

    if (NULL != jclass_ConfigValue)
    {
        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
    }

	JNI_LOGD(("{%s %d}set config ok." LOG_TAIL,__FUNCTION__, __LINE__));
    return 0;
}


JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_updateCfg_1native(JNIEnv *env, jclass clazz,
        jstring tvc_type)
{
    CHAR* 	s_tvc_type 			= NULL;   
    UINT16 	ui2_cfg_id 			= 0;
    UINT16 	ui2_cfg_value_type	= 0;
    INT32 	i4_ret 				= 0;

	JNI_LOGD(("{%s %d} Enter"LOG_TAIL, __FUNCTION__, __LINE__));
    /*check parameters*/
    if (env == NULL ||tvc_type == NULL)
    {
        JNI_LOGE(("{%s %d}Invalid arguments"LOG_TAIL, __FUNCTION__, __LINE__));
        return -1;
    }
    
    /*get tv config type string*/
    s_tvc_type = JNI_GET_STRING_UTF_CHARS(env,tvc_type);
    if (s_tvc_type == NULL) 
    {
    	JNI_LOGE(("{%s %d} s_svc_type is NULL"LOG_TAIL,  __FUNCTION__, __LINE__));
        return -1;
    }

    JNI_LOGD(("{%s %d}update s_tvc_type = %s"LOG_TAIL, __FUNCTION__, __LINE__, s_tvc_type));

    a_cfg_map_string_2_id(s_tvc_type, &ui2_cfg_id, &ui2_cfg_value_type);
    if(i4_ret != APP_CFGR_OK)
    {
        JNI_LOGE(("{%s %d}No match config type!!!"LOG_TAIL, __FUNCTION__, __LINE__));
        return -1;
    }

    JNI_LOGD(("{%s %d}update s_tvc_type = %s, ui2_cfg_id is 0x%x, ui2_cfg_value_type is %d"LOG_TAIL, 
        __FUNCTION__, 
        __LINE__, 
        s_tvc_type,
        ui2_cfg_id,
        ui2_cfg_value_type));
    
    JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);

    JNI_LOGD(("{%s %d}pre call a_cfg_av_update"LOG_TAIL, __FUNCTION__, __LINE__));
    a_cfg_av_update(ui2_cfg_id);
    
    JNI_LOGD(("{%s %d}update cfg ok"LOG_TAIL, __FUNCTION__, __LINE__));
    return 0;
}


JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_resetCfgGroup_1native(JNIEnv *env, jclass clazz,
        jstring reset_type)
{
    CHAR* 	s_reset_type 	= NULL;   
    UINT32 	ui4_reset_type 	= 0;
	INT32 	i4_ret 			= 0;

	JNI_LOGD(("{%s %d} Enter"LOG_TAIL, __FUNCTION__, __LINE__));
	
    /*check parameters*/
    if (env == NULL || reset_type == NULL)
    {
        JNI_LOGE(("{%s %d}Invalid arguments"LOG_TAIL, __FUNCTION__, __LINE__));
        return -1;
    }
    
    /*get tv config type string*/
    s_reset_type = JNI_GET_STRING_UTF_CHARS(env, reset_type);
    if (s_reset_type == NULL) 
    {
    	JNI_LOGE(("{%s %d} s_reset_type is NULL"LOG_TAIL, __FUNCTION__, __LINE__));
        return -1;
    }

    JNI_LOGD(("{%s %d}reset_type = %s"LOG_TAIL, __FUNCTION__, __LINE__, s_reset_type));

    if(strcmp(s_reset_type, RESET_USER) == 0)
    {
        JNI_LOGD(("{%s %d}_______________________________reset_type = %s"LOG_TAIL, __FUNCTION__, __LINE__, s_reset_type));
        ui4_reset_type = APP_CFG_RESET_TYPE_USER;
    }
    else if(strcmp(s_reset_type, RESET_FACTORY) == 0)
    {
        JNI_LOGD(("{%s %d}===============================reset_type = %s"LOG_TAIL, __FUNCTION__, __LINE__, s_reset_type));
        ui4_reset_type = APP_CFG_RESET_TYPE_FACTORY;
    }
    
    a_cfg_reset(ui4_reset_type);
        
    JNI_RELEASE_STRING_UTF_CHARS (env, reset_type, s_reset_type);   

	JNI_LOGD(("{%s %d} reset cfg ok"LOG_TAIL, __FUNCTION__, __LINE__));
    return 0;
}



/*
 * Class:     com_mediatek_tv_service_TVNative
 * Method:    getCfgMinMax_native
 * Signature: (Ljava/lang/String;Lcom/mediatek/tv/common/ConfigValue;)I
 */
JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_getCfgMinMax_1native(JNIEnv * env, jclass ckass,
        jstring tvc_type, jobject output_value)
{
    CHAR*   s_tvc_type      	= NULL;
    UINT16  ui2_cfg_id      	= 0;
    INT32   i4_ret          	= 0;       
    UINT16  ui2_cfg_value_type 	= 0;
    SIZE_T  z_size				= 0;
    jclass 	jclass_ConfigValue 	= NULL;
    
    JNI_LOGD(("{%s %d} Enter"LOG_TAIL, __FUNCTION__, __LINE__));

    /*check parameters*/
    if (env == NULL || tvc_type == NULL)
    {
        JNI_LOGE(("{%s %d}Invalid arguments"LOG_TAIL, __FUNCTION__, __LINE__));
        return -1;
    }

    /*get tv config type string*/
    s_tvc_type = JNI_GET_STRING_UTF_CHARS(env, tvc_type);
    if (s_tvc_type == NULL) 
    {
    	JNI_LOGE(("{%s %d} s_tvc_type is NULL"LOG_TAIL, __FUNCTION__, __LINE__));
        return -1;
    }

    //get jclass 
    jclass_ConfigValue = JNI_GET_CLASS_BY_NAME(env,"com/mediatek/tv/common/ConfigValue");    

    if (NULL == jclass_ConfigValue)
    {
        JNI_LOGE(("{%s %d}, jcalss_ConfigValue is NULL."LOG_TAIL, __FUNCTION__, __LINE__));
		JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);
        return -1;
    }

    //map tvc_type string to cfg_id
    i4_ret = a_cfg_map_string_2_id(s_tvc_type, &ui2_cfg_id, &ui2_cfg_value_type);
    
    if(APP_CFGR_OK != i4_ret)
    {
        JNI_LOGE(("{%s %d}No match config type!!!"LOG_TAIL, __FUNCTION__, __LINE__));
		if (NULL != jclass_ConfigValue)
	    {
	        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
	    }
		JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);
        return -1;
    }

    JNI_LOGD(("{%s %d}PRE__get Min&Max value s_tvc_type = %s, ui2_cfg_id is 0x%x, ui2_cfg_value_type is %d"LOG_TAIL, 
                __FUNCTION__, 
                __LINE__, 
                s_tvc_type,
                ui2_cfg_id,
                ui2_cfg_value_type));
    
    JNI_RELEASE_STRING_UTF_CHARS (env, tvc_type, s_tvc_type);
    {
        INT32 i4_min_value = 0;
        INT32 i4_max_value = 0;
        //get min max value
        INT16 i2_min_value = 0;
        INT16 i2_max_value = 0;
		
        i4_ret = a_cfg_av_get_min_max(ui2_cfg_id, &i2_min_value , &i2_max_value);
        i4_min_value = i2_min_value;
        i4_max_value = i2_max_value;
        JNI_LOGD(("{%s %d} call a_cfg_av_get_min_max, ui2_cfg_id is 0x%x,  i2_min_value is %d ,i2_max_value is %d"LOG_TAIL,
                __FUNCTION__, 
                __LINE__, 
                ui2_cfg_id, 
                i2_min_value, 
                i2_max_value));
        if(APP_CFGR_OK != i4_ret)
        {
            JNI_LOGD(("{%s %d}get_min_max Failed(return %d)!!!"LOG_TAIL, __FUNCTION__, __LINE__, i4_ret));
			if (NULL != jclass_ConfigValue)
		    {
		        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
		    }
            return -1; 
        }
        else
        {
            if( JNI_IS_INSTANCE_OF(env, output_value, jclass_ConfigValue) )
            {
                JCOMV(env, output_value, Void, CMI(cfg_val_def, setMinValue), (jint)i4_min_value);
                JCOMV(env, output_value, Void, CMI(cfg_val_def, setMaxValue), (jint)i4_max_value);
            }
            else
            {
            	JNI_LOGE(("{%s %d} JNI_IS_INSTANCE_OF error."LOG_TAIL, __FUNCTION__, __LINE__));
				if (NULL != jclass_ConfigValue)
			    {
			        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
			    }
                return -1;
            }
        }
    }
    if (NULL != jclass_ConfigValue)
    {
        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
    }

	JNI_LOGD(("{%s %d} get cfg min/max ok."LOG_TAIL, __FUNCTION__, __LINE__));
    return 0;
    
}

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_powerOff_1native(JNIEnv *env, jclass clazz)
{	
	INT32 i4_ret;
    JNI_LOGD(("{%s %d} Enter"LOG_TAIL, __FUNCTION__, __LINE__));
    i4_ret = a_cfg_power_off();
	if (i4_ret != APP_CFGR_OK)
	{
		JNI_LOGE(("{%s %d} a_cfg_power_off error. %d"LOG_TAIL, __FUNCTION__, __LINE__, i4_ret));
		return -1;
	}
	
	JNI_LOGD(("{%s %d} a_cfg_power_off ok."LOG_TAIL, __FUNCTION__, __LINE__));
    return 0;
}


JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_autoAdjust_1native(JNIEnv *env, jclass clazz, jstring auto_type)
{
    JNI_LOGD(("{%s %d}", __FUNCTION__, __LINE__));

    return -1;
}

JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_readGPIO_1native(JNIEnv *env, jclass clazz,
        jobject output_value)
{    
    jclass 	jclass_ConfigValue = NULL;
    UINT32 	ui4_val = 0;
    INT32 	i4_mask = 0;
    INT32 	i4_gpio = 0;

	JNI_LOGD(("{%s %d} Enter."LOG_TAIL,  __FUNCTION__, __LINE__));

    jclass_ConfigValue = JNI_GET_CLASS_BY_NAME(env, "com/mediatek/tv/common/ConfigValue");    
    
	if (NULL == jclass_ConfigValue)
	{
		JNI_LOGE(("{%s %d}, jcalss_ConfigValue is NULL."LOG_TAIL, __FUNCTION__, __LINE__));
		return -1;
	}

    if(JNI_IS_INSTANCE_OF(env, output_value, jclass_ConfigValue))
    {
        i4_mask = (INT32)JCOM(env,output_value, Int, CMI(cfg_val_def, getGpioMask));

        i4_gpio = (INT32)JCOM(env,output_value, Int, CMI(cfg_val_def, getGpioID));

		JNI_LOGD(("{%s %d} Read GPIO: i4_mask=%d, i4_gpio=%d."LOG_TAIL, i4_mask, i4_gpio));
        a_cfg_read_gpio((UINT32)i4_gpio, (UINT32)i4_mask, &ui4_val);
		
		JNI_LOGD(("{%s %d} Read GPIO ui4_val=%d."LOG_TAIL, __FUNCTION__, __LINE__, ui4_val));
            
        JCOMV(env,output_value, Void, CMI(cfg_val_def, setGpioValue), (jint)ui4_val);
    }      

	if (NULL != jclass_ConfigValue)
    {
        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
    }
	
	JNI_LOGD(("{%s %d} Read GPIO ok."LOG_TAIL, __FUNCTION__, __LINE__));
	
    return 0;
}


JNIEXPORT jint JNICALL Java_com_mediatek_tv_service_TVNative_writeGPIO_1native(JNIEnv * env, jclass ckass,  
        jobject input_value)
{
    jclass 	jclass_ConfigValue = NULL;
    INT32 	i4_val 	= 0;
    INT32 	i4_mask = 0;
    INT32 	i4_gpio = 0;

	JNI_LOGD(("{%s %d} Enter."LOG_TAIL, __FUNCTION__, __LINE__));
    jclass_ConfigValue = JNI_GET_CLASS_BY_NAME(env, "com/mediatek/tv/common/ConfigValue");    
    if (NULL == jclass_ConfigValue)
	{
		JNI_LOGE(("{%s %d}, jcalss_ConfigValue is NULL."LOG_TAIL, __FUNCTION__, __LINE__));
		return -1;
	}
	
    if(JNI_IS_INSTANCE_OF(env, input_value, jclass_ConfigValue))
    {
        i4_mask = (INT32)JCOM(env, input_value, Int, CMI(cfg_val_def, getGpioMask));

        i4_gpio = (INT32)JCOM(env, input_value, Int, CMI(cfg_val_def, getGpioID));

        i4_val = (INT32)JCOM(env, input_value, Int, CMI(cfg_val_def, getGpioValue));

		JNI_LOGD(("{%s %d} Write GPIO: i4_mask=%d, i4_gpio=%d, i4_val=%d."LOG_TAIL, i4_mask, i4_gpio, i4_val));
        a_cfg_write_gpio(i4_gpio, i4_mask, i4_val);
    }    

	if (NULL != jclass_ConfigValue)
    {
        JNI_DEL_LOCAL_REF(env, jclass_ConfigValue);
    }
	
	JNI_LOGD(("{%s %d} Write GPIO ok."LOG_TAIL, __FUNCTION__, __LINE__));
    return 0;
}  


EXTERN_C_END

