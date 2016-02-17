package com.mediatek.tv.model;

/* need application/user implement the function body ... */
public interface CIListener {
	public static byte CI_CAM_STATUS_INSERT = 0;/*Indicates that cam is now inserted into Host*/
	public static byte CI_CAM_STATUS_NAME = 1;/*Indicates that cam initialized finished and CIService#isSlotActive() will return TRUE,CIService#getCamName will return CAM's name string*/
	public static byte CI_CAM_STATUS_REMOVE = 2;/*Indicates that cam is removed from Host*/
	
	public static byte CI_CAM_SYSTEM_ID_STATUS_INVALID = 0;/*Indicate no cam is inserted into host*/
	public static byte CI_CAM_SYSTEM_ID_STATUS_WAIT = 1;/*Indicate cam is in initializing state*/
	public static byte CI_CAM_SYSTEM_ID_STATUS_READY = 2;/*Indicate cam is in initialized succeed*/
	public static byte CI_CAM_SYSTEM_ID_STATUS_MATCH = 3;/*Indicate cam's ca id match with CA Descriptor in program's PMT table*/
    public static byte CI_CAM_SYSTEM_ID_STATUS_NOT_MATCH = 4;/*Indicate cam's ca id dismatch with CA Descriptor in program's PMT table*/
	/**
		 * CAM status update notification.
		 * 
		 * 
		 * @param name
		 *			  cam status (CI_CAM_STATUS_INSERT, CI_CAM_STATUS_NAME, CI_CAM_STATUS_REMOVE)
		 * @see CIService#addCIListener
		 * @see CIService#removeCIListener
	 */
    public int camStatusUpdated(byte cam_status);
	/**
		 *  MMI menu received from CAM.
		 *  After received this notification,CIService#getMMIMenu() will return MMIMenu object
		 * 
		 * @see CIService#getMMIMenu
		 * @see MMIMenu
	 */
    public int camMMIMenuReceived();
	/**
		 *  MMI enq received from CAM.
		 *  After received this notification,CIService#getMMIEnq() will return MMIEnq object
		 * 
		 * @see CIService#getMMIEng
		 * @see MMIEnq
	 */
    public int camMMIEnqReceived();
	/**
		 *  MMI(MMI menu or MMI enq) is closed by CAM.
		 * 
		 * @param name
		 *			  mmi close delay time, the delay in seconds before CI mmi is to be closed 
		 * @see CIService#getMMIEng
		 * @see MMIEnq
	 */
    public int camMMIClosed(byte mmi_close_delay);
	/**
		 *  Host Control resource Tune apdu received from CAM.
		 *  After received this notification,CIService#getHostControlTune() will return HostControlTune object
		 * 
		 * @see CIService#getHostControlTune
		 * @see HostControlTune
	 */
    public int camHostControlTune();
	/**
		 *  Host Control resource Replace apdu received from CAM.
		 *  After received this notification,CIService#getHostControlReplace() will return HostControlReplace object
		 * 
		 * @see CIService#getHostControlReplace
		 * @see HostControlReplace
	 */
    public int camHostControlReplace();
	/**
		 *  Host Control resource Clear Replace apdu received from CAM.
		 * 
		 * @see CIService#getHostControlReplace
		 * @see HostControlReplace
	 */
    public int camHostControlClearReplace();
	/**
		 *  CAM's system ID status updated, this notification is used when one program is selected and APP maybe display UI hint.
		 * 
		 * @see CI_CAM_SYSTEM_ID_STATUS_INVALID
		 * @see CI_CAM_SYSTEM_ID_STATUS_WAIT
	        * @see CI_CAM_SYSTEM_ID_STATUS_READY
	        * @see CI_CAM_SYSTEM_ID_STATUS_MATCH
	        * @see CI_CAM_SYSTEM_ID_STATUS_NOT_MATCH
	 */
    public int camSystemIDStatusUpdated(byte sys_id_status);
}
