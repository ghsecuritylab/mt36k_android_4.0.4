/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\p4_views\\jinke.mei\\ws_jinke.mei_425443_android\\DTV\\PROD_BR\\DTV_X_IDTV0801\\vm_linux\\android\\dtv-android\\tv_addon_ics\\framework\\tv\\java\\com\\mediatek\\tv\\service\\ITVRemoteService.aidl
 */
package com.mediatek.tv.service;
public interface ITVRemoteService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.mediatek.tv.service.ITVRemoteService
{
private static final java.lang.String DESCRIPTOR = "com.mediatek.tv.service.ITVRemoteService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.mediatek.tv.service.ITVRemoteService interface,
 * generating a proxy if needed.
 */
public static com.mediatek.tv.service.ITVRemoteService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.mediatek.tv.service.ITVRemoteService))) {
return ((com.mediatek.tv.service.ITVRemoteService)iin);
}
return new com.mediatek.tv.service.ITVRemoteService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.service.ITVCallBack _arg0;
_arg0 = com.mediatek.tv.service.ITVCallBack.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.service.ITVCallBack _arg0;
_arg0 = com.mediatek.tv.service.ITVCallBack.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterAll:
{
data.enforceInterface(DESCRIPTOR);
this.unregisterAll();
reply.writeNoException();
return true;
}
case TRANSACTION_openUARTSerial_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int[] _arg1;
_arg1 = data.createIntArray();
int[] _arg2;
_arg2 = data.createIntArray();
int _result = this.openUARTSerial_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
reply.writeIntArray(_arg2);
return true;
}
case TRANSACTION_closeUARTSerial_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.closeUARTSerial_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getUARTSerialSetting_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int[] _arg1;
_arg1 = data.createIntArray();
int _result = this.getUARTSerialSetting_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeIntArray(_arg1);
return true;
}
case TRANSACTION_getUARTSerialOperationMode_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int[] _arg1;
_arg1 = data.createIntArray();
int _result = this.getUARTSerialOperationMode_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeIntArray(_arg1);
return true;
}
case TRANSACTION_setUARTSerialSetting_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int[] _arg1;
_arg1 = data.createIntArray();
int _result = this.setUARTSerialSetting_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeIntArray(_arg1);
return true;
}
case TRANSACTION_setUARTSerialOperationMode_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _result = this.setUARTSerialOperationMode_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setUARTSerialMagicString_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte[] _arg1;
_arg1 = data.createByteArray();
int _result = this.setUARTSerialMagicString_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg1);
return true;
}
case TRANSACTION_outputUARTSerial_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte[] _arg1;
_arg1 = data.createByteArray();
int _result = this.outputUARTSerial_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg1);
return true;
}
case TRANSACTION_autoAdjust_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.autoAdjust_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_powerOff_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.powerOff_proxy();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCfg_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
com.mediatek.tv.common.ConfigValue _arg2;
if ((0!=data.readInt())) {
_arg2 = com.mediatek.tv.common.ConfigValue.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
com.mediatek.tv.common.ConfigValue _arg3;
if ((0!=data.readInt())) {
_arg3 = com.mediatek.tv.common.ConfigValue.CREATOR.createFromParcel(data);
}
else {
_arg3 = null;
}
int _result = this.getCfg_proxy(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg2!=null)) {
reply.writeInt(1);
_arg2.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
if ((_arg3!=null)) {
reply.writeInt(1);
_arg3.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_setCfg_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _arg1;
_arg1 = data.readString();
com.mediatek.tv.common.ConfigValue _arg2;
if ((0!=data.readInt())) {
_arg2 = com.mediatek.tv.common.ConfigValue.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
int _result = this.setCfg_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_updateCfg_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.updateCfg_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_resetCfgGroup_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.resetCfgGroup_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_readGPIO_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.common.ConfigValue _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.common.ConfigValue.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int _result = this.readGPIO_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_writeGPIO_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.common.ConfigValue _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.common.ConfigValue.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int _result = this.writeGPIO_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCfgMinMax_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.common.ConfigValue _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.common.ConfigValue.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.getCfgMinMax_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_dtSetConfig_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.dtSetConfig_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtSetDst_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _result = this.dtSetDst_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtSetTz_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
int _result = this.dtSetTz_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtSetUtc_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
int _arg1;
_arg1 = data.readInt();
int _result = this.dtSetUtc_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtSetDstCtrl_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _result = this.dtSetDstCtrl_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtSetDsChange_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
int _result = this.dtSetDsChange_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtSetDsOffset_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
int _result = this.dtSetDsOffset_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtSetSyncSrc_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
java.lang.String _arg2;
_arg2 = data.readString();
int _result = this.dtSetSyncSrc_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtSetTzCtrl_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _result = this.dtSetTzCtrl_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtSetSysCountCode_proxy:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
int _arg1;
_arg1 = data.readInt();
int _result = this.dtSetSysCountCode_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtGetDst_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.dtGetDst_proxy();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_dtGetGps_proxy:
{
data.enforceInterface(DESCRIPTOR);
int[] _arg0;
_arg0 = data.createIntArray();
long _result = this.dtGetGps_proxy(_arg0);
reply.writeNoException();
reply.writeLong(_result);
reply.writeIntArray(_arg0);
return true;
}
case TRANSACTION_dtGetTz_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.dtGetTz_proxy();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_dtGetUtc_proxy:
{
data.enforceInterface(DESCRIPTOR);
int[] _arg0;
_arg0 = data.createIntArray();
long _result = this.dtGetUtc_proxy(_arg0);
reply.writeNoException();
reply.writeLong(_result);
reply.writeIntArray(_arg0);
return true;
}
case TRANSACTION_dtGetBrdcstUtc_proxy:
{
data.enforceInterface(DESCRIPTOR);
int[] _arg0;
_arg0 = data.createIntArray();
long _result = this.dtGetBrdcstUtc_proxy(_arg0);
reply.writeNoException();
reply.writeLong(_result);
reply.writeIntArray(_arg0);
return true;
}
case TRANSACTION_dtGetCountCode_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte[] _arg1;
_arg1 = data.createByteArray();
long[] _arg2;
_arg2 = data.createLongArray();
int _result = this.dtGetCountCode_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg1);
reply.writeLongArray(_arg2);
return true;
}
case TRANSACTION_dtGetDstCtrl_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.dtGetDstCtrl_proxy();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_dtGetDsChange_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.dtGetDsChange_proxy();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_dtGetDsOffset_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _result = this.dtGetDsOffset_proxy();
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_dtGetTzCtrl_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.dtGetTzCtrl_proxy();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_dtGetNumCountCode_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.dtGetNumCountCode_proxy();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtGetSysCountCode_proxy:
{
data.enforceInterface(DESCRIPTOR);
byte[] _arg0;
_arg0 = data.createByteArray();
int[] _arg1;
_arg1 = data.createIntArray();
int _result = this.dtGetSysCountCode_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeByteArray(_arg0);
reply.writeIntArray(_arg1);
return true;
}
case TRANSACTION_dtGetLastSyncTblId_proxy:
{
data.enforceInterface(DESCRIPTOR);
byte _result = this.dtGetLastSyncTblId_proxy();
reply.writeNoException();
reply.writeByte(_result);
return true;
}
case TRANSACTION_dtCheckInputTime_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _result = this.dtCheckInputTime_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dtConfigCheckInputTime_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _result = this.dtConfigCheckInputTime_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dt_utc_sec_to_dtg_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
com.mediatek.tv.model.DtDTG _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.dt_utc_sec_to_dtg_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_dt_utc_sec_to_loc_dtg_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
com.mediatek.tv.model.DtDTG _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.dt_utc_sec_to_loc_dtg_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_dt_conv_utc_local_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.DtDTG _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.mediatek.tv.model.DtDTG _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.dt_conv_utc_local_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_dt_dtg_to_sec_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.DtDTG _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
long _result = this.dt_dtg_to_sec_proxy(_arg0);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_dt_gps_sec_to_utc_sec_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
long _result = this.dt_gps_sec_to_utc_sec_proxy(_arg0);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_dt_bcd_to_sec_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.dt_bcd_to_sec_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_dt_mjd_bcd_to_dtg_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.DtDTG _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.dt_mjd_bcd_to_dtg_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_dt_mjd_to_dtg_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
com.mediatek.tv.model.DtDTG _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.dt_mjd_to_dtg_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_dt_dtg_to_mjd_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.DtDTG _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
long _result = this.dt_dtg_to_mjd_proxy(_arg0);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_dt_dtg_to_mjd_bcd_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.DtDTG _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int[] _arg1;
_arg1 = data.createIntArray();
int _result = this.dt_dtg_to_mjd_bcd_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeIntArray(_arg1);
return true;
}
case TRANSACTION_dt_diff_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.DtDTG _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.mediatek.tv.model.DtDTG _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
long _result = this.dt_diff_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeLong(_result);
return true;
}
case TRANSACTION_dt_add_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.DtDTG _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
long _arg1;
_arg1 = data.readLong();
com.mediatek.tv.model.DtDTG _arg2;
if ((0!=data.readInt())) {
_arg2 = com.mediatek.tv.model.DtDTG.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
int _result = this.dt_add_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg2!=null)) {
reply.writeInt(1);
_arg2.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_dt_is_leap_year_proxy:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
boolean _result = this.dt_is_leap_year_proxy(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_dt_reg_nfy_fct_proxy:
{
data.enforceInterface(DESCRIPTOR);
long[] _arg0;
_arg0 = data.createLongArray();
int _result = this.dt_reg_nfy_fct_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
reply.writeLongArray(_arg0);
return true;
}
case TRANSACTION_channelSelect_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
com.mediatek.tv.model.ChannelInfo _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.ChannelInfo.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.channelSelect_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_channelSelectEx_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
com.mediatek.tv.model.ChannelInfo _arg3;
if ((0!=data.readInt())) {
_arg3 = com.mediatek.tv.model.ChannelInfo.CREATOR.createFromParcel(data);
}
else {
_arg3 = null;
}
com.mediatek.tv.model.ExtraChannelInfo _arg4;
if ((0!=data.readInt())) {
_arg4 = com.mediatek.tv.model.ExtraChannelInfo.CREATOR.createFromParcel(data);
}
else {
_arg4 = null;
}
int _result = this.channelSelectEx_proxy(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_syncStopService_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.syncStopService_proxy();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_startVideoStream_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.startVideoStream_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_syncStopSubtitleStream_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.syncStopSubtitleStream_proxy();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_fineTune_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.AnalogChannelInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.model.AnalogChannelInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int _arg1;
_arg1 = data.readInt();
boolean _arg2;
_arg2 = (0!=data.readInt());
int _result = this.fineTune_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_freeze_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
int _result = this.freeze_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setVideoMute_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.setVideoMute_proxy();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getVideoResolution_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.mediatek.tv.model.VideoResolution _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.VideoResolution.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.getVideoResolution_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getAudioInfo_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.mediatek.tv.model.AudioInfo _arg1;
_arg1 = new com.mediatek.tv.model.AudioInfo();
int _result = this.getAudioInfo_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getsignalLevelInfo_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.mediatek.tv.model.SignalLevelInfo _arg1;
_arg1 = new com.mediatek.tv.model.SignalLevelInfo();
int _result = this.getsignalLevelInfo_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getDtvAudioLangInfo_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.AudioLanguageInfo _arg0;
_arg0 = new com.mediatek.tv.model.AudioLanguageInfo();
int _result = this.getDtvAudioLangInfo_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_setDtvAudioLang_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.setDtvAudioLang_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setDtvAudioLangByIndex_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _result = this.setDtvAudioLangByIndex_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getSubtitleInfo_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.SubtitleInfo _arg0;
_arg0 = new com.mediatek.tv.model.SubtitleInfo();
int _result = this.getSubtitleInfo_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg0!=null)) {
reply.writeInt(1);
_arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_setSubtitleLang_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.setSubtitleLang_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getStreamMpegPid_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getStreamMpegPid_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_selectMpegStreamByPid_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
int _result = this.selectMpegStreamByPid_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isCaptureLogo_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isCaptureLogo_proxy();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setMute_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _result = this.setMute_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getMute_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getMute_proxy();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getDtvAudioDecodeType_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDtvAudioDecodeType_proxy();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setVideoBlueMute_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.setVideoBlueMute_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setVideoBlueMuteEx_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
boolean _arg2;
_arg2 = (0!=data.readInt());
int _result = this.setVideoBlueMuteEx_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isFreeze_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isFreeze_proxy(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_enableFreeze_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.enableFreeze_proxy(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setDisplayAspectRatio_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.setDisplayAspectRatio_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDisplayAspectRatio_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDisplayAspectRatio_proxy();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_updateTVWindowRegion_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
int _result = this.updateTVWindowRegion_proxy(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_stopStream_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _result = this.stopStream_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_startAudioStream_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.startAudioStream_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_syncStopVideoStream_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.syncStopVideoStream_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_showSnowAsNoSignal_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
int _result = this.showSnowAsNoSignal_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_updateFocusWindow_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.updateFocusWindow_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_updateTVMode_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.updateTVMode_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setMTS_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _result = this.setMTS_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setChannelList_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
com.mediatek.tv.model.ChannelModel _arg2;
if ((0!=data.readInt())) {
_arg2 = com.mediatek.tv.model.ChannelModel.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
int _result = this.setChannelList_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getChannelList_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.mediatek.tv.model.ChannelModel _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.ChannelModel.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.getChannelList_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_fsSyncChannelList_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.fsSyncChannelList_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_fsStoreChannelList_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.fsStoreChannelList_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_digitalDBClean_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.digitalDBClean_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_startScan_pal_secam_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.ScanParaPalSecam _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.ScanParaPalSecam.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.startScan_pal_secam_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_cancelScan_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.cancelScan_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getScanData_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
com.mediatek.tv.model.ScanExchangeFrenquenceRange _arg2;
if ((0!=data.readInt())) {
_arg2 = com.mediatek.tv.model.ScanExchangeFrenquenceRange.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
int _result = this.getScanData_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg2!=null)) {
reply.writeInt(1);
_arg2.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_scanExchangeData_proxy:
{
data.enforceInterface(DESCRIPTOR);
int[] _arg0;
_arg0 = data.createIntArray();
int _result = this.scanExchangeData_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
reply.writeIntArray(_arg0);
return true;
}
case TRANSACTION_startScan_dvbc_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.ScanParaDvbc _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.ScanParaDvbc.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.startScan_dvbc_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_cancelScan_dvbc_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.cancelScan_dvbc_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDefaultSymRate_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getDefaultSymRate_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDefaultFrequency_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getDefaultFrequency_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDefaultEMod_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getDefaultEMod_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDefaultNwID_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.getDefaultNwID_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDvbcScanTypeNum_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.DvbcProgramType _arg1;
_arg1 = new com.mediatek.tv.model.DvbcProgramType();
int _result = this.getDvbcScanTypeNum_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getDvbcFreqRange_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.DvbcFreqRange _arg1;
_arg1 = new com.mediatek.tv.model.DvbcFreqRange();
int _result = this.getDvbcFreqRange_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getDvbcMainFrequence_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.MainFrequence _arg1;
_arg1 = new com.mediatek.tv.model.MainFrequence();
int _result = this.getDvbcMainFrequence_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_startScan_dtmb_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.ScanParaDtmb _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.ScanParaDtmb.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.startScan_dtmb_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_cancelScan_dtmb_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.cancelScan_dtmb_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDtmbFreqRange_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.DtmbFreqRange _arg1;
_arg1 = new com.mediatek.tv.model.DtmbFreqRange();
int _result = this.getDtmbFreqRange_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getFirstDtmbScanRF_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.DtmbScanRF _arg1;
_arg1 = new com.mediatek.tv.model.DtmbScanRF();
int _result = this.getFirstDtmbScanRF_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getLastDtmbScanRF_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.DtmbScanRF _arg1;
_arg1 = new com.mediatek.tv.model.DtmbScanRF();
int _result = this.getLastDtmbScanRF_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getNextDtmbScanRF_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.DtmbScanRF _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.DtmbScanRF.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
com.mediatek.tv.model.DtmbScanRF _arg2;
_arg2 = new com.mediatek.tv.model.DtmbScanRF();
int _result = this.getNextDtmbScanRF_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg2!=null)) {
reply.writeInt(1);
_arg2.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getPrevDtmbScanRF_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.tv.model.DtmbScanRF _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.DtmbScanRF.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
com.mediatek.tv.model.DtmbScanRF _arg2;
_arg2 = new com.mediatek.tv.model.DtmbScanRF();
int _result = this.getPrevDtmbScanRF_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg2!=null)) {
reply.writeInt(1);
_arg2.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getCurrentDtmbScanRF_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
com.mediatek.tv.model.DtmbScanRF _arg2;
_arg2 = new com.mediatek.tv.model.DtmbScanRF();
int _result = this.getCurrentDtmbScanRF_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg2!=null)) {
reply.writeInt(1);
_arg2.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_inputServiceBind_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _result = this.inputServiceBind_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_inputServiceGetRecord_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.mediatek.tv.model.InputRecord _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.InputRecord.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.inputServiceGetRecord_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_inputServiceSetOutputMute:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
int _result = this.inputServiceSetOutputMute(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_inputServiceSwap_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _result = this.inputServiceSwap_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setScreenOutputRect_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
int _result = this.setScreenOutputRect_proxy(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setScreenOutputVideoRect_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _arg3;
_arg3 = data.readInt();
int _arg4;
_arg4 = data.readInt();
int _result = this.setScreenOutputVideoRect_proxy(_arg0, _arg1, _arg2, _arg3, _arg4);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getScreenOutputRect_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.mediatek.tv.model.InputRegion _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.InputRegion.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.getScreenOutputRect_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getScreenOutputVideoRect_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.mediatek.tv.model.InputRegion _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.InputRegion.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.getScreenOutputVideoRect_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_inputSourceExchangeData_proxy:
{
data.enforceInterface(DESCRIPTOR);
int[] _arg0;
_arg0 = data.createIntArray();
int _result = this.inputSourceExchangeData_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
reply.writeIntArray(_arg0);
return true;
}
case TRANSACTION_setOSDColorKey_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
int _arg1;
_arg1 = data.readInt();
boolean _result = this.setOSDColorKey_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setOSDOpacity_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.setOSDOpacity_proxy(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_eventServiceSetCommand_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.EventCommand _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.model.EventCommand.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int _result = this.eventServiceSetCommand_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_eventServiceGetPFEvents_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.DvbChannelInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.model.DvbChannelInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.util.List<com.mediatek.tv.model.EventInfo> _arg1;
_arg1 = new java.util.ArrayList<com.mediatek.tv.model.EventInfo>();
int _result = this.eventServiceGetPFEvents_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
reply.writeTypedList(_arg1);
return true;
}
case TRANSACTION_eventServiceGetScheduleEvents_proxy:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.tv.model.DvbChannelInfo _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.tv.model.DvbChannelInfo.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
long _arg1;
_arg1 = data.readLong();
long _arg2;
_arg2 = data.readLong();
java.util.List<com.mediatek.tv.model.EventInfo> _arg3;
_arg3 = new java.util.ArrayList<com.mediatek.tv.model.EventInfo>();
int _result = this.eventServiceGetScheduleEvents_proxy(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(_result);
reply.writeTypedList(_arg3);
return true;
}
case TRANSACTION_getSlotNum_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getSlotNum_proxy();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isSlotActive_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _result = this.isSlotActive_proxy(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_enterMMI_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.enterMMI_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCamName_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.lang.String _result = this.getCamName_proxy(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getCamSystemIDInfo_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int[] _result = this.getCamSystemIDInfo_proxy(_arg0);
reply.writeNoException();
reply.writeIntArray(_result);
return true;
}
case TRANSACTION_closeMMI_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.closeMMI_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setMMIClosed_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.setMMIClosed_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_answerMMIMenu_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
char _arg2;
_arg2 = (char)data.readInt();
int _result = this.answerMMIMenu_proxy(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_answerMMIEnq_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
boolean _arg2;
_arg2 = (0!=data.readInt());
java.lang.String _arg3;
_arg3 = data.readString();
int _result = this.answerMMIEnq_proxy(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_askRelease_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.askRelease_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setCITsPath_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
int _result = this.setCITsPath_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setCIInputDTVPath_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
int _result = this.setCIInputDTVPath_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getTunedChannel_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.mediatek.tv.model.HostControlTune _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.HostControlTune.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.getTunedChannel_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
if ((_arg1!=null)) {
reply.writeInt(1);
_arg1.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_activateComponent_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.activateComponent_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_inactivateComponent_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.inactivateComponent_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_updateSysStatus_proxy:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.updateSysStatus_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_isTTXAvail_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isTTXAvail_proxy();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_sendkeyEventtoComp_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _result = this.sendkeyEventtoComp_proxy(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_lockDigitalTuner_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.lockDigitalTuner_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_unlockDigitalTuner_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.unlockDigitalTuner_proxy(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurrentDTVAudioCodec_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurrentDTVAudioCodec_proxy();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getCurrentDTVVideoCodec_proxy:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCurrentDTVVideoCodec_proxy();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_setMuteState_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.setMuteState_proxy(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getMuteState_proxy:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.getMuteState_proxy();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.mediatek.tv.service.ITVRemoteService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public void registerCallback(com.mediatek.tv.service.ITVCallBack cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unregisterCallback(com.mediatek.tv.service.ITVCallBack cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unregisterAll() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_unregisterAll, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*Factory*/
public int openUARTSerial_proxy(int uartSerialID, int[] uartSerialSetting, int[] handle) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uartSerialID);
_data.writeIntArray(uartSerialSetting);
_data.writeIntArray(handle);
mRemote.transact(Stub.TRANSACTION_openUARTSerial_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readIntArray(handle);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int closeUARTSerial_proxy(int handle) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(handle);
mRemote.transact(Stub.TRANSACTION_closeUARTSerial_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getUARTSerialSetting_proxy(int handle, int[] uartSerialSetting) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(handle);
_data.writeIntArray(uartSerialSetting);
mRemote.transact(Stub.TRANSACTION_getUARTSerialSetting_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readIntArray(uartSerialSetting);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getUARTSerialOperationMode_proxy(int handle, int[] operationMode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(handle);
_data.writeIntArray(operationMode);
mRemote.transact(Stub.TRANSACTION_getUARTSerialOperationMode_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readIntArray(operationMode);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setUARTSerialSetting_proxy(int handle, int[] uartSerialSetting) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(handle);
_data.writeIntArray(uartSerialSetting);
mRemote.transact(Stub.TRANSACTION_setUARTSerialSetting_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readIntArray(uartSerialSetting);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setUARTSerialOperationMode_proxy(int handle, int operationMode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(handle);
_data.writeInt(operationMode);
mRemote.transact(Stub.TRANSACTION_setUARTSerialOperationMode_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setUARTSerialMagicString_proxy(int handle, byte[] uartSerialMagicSetting) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(handle);
_data.writeByteArray(uartSerialMagicSetting);
mRemote.transact(Stub.TRANSACTION_setUARTSerialMagicString_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(uartSerialMagicSetting);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int outputUARTSerial_proxy(int handle, byte[] uartSerialData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(handle);
_data.writeByteArray(uartSerialData);
mRemote.transact(Stub.TRANSACTION_outputUARTSerial_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(uartSerialData);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*Factory*/
public int autoAdjust_proxy(java.lang.String autoType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(autoType);
mRemote.transact(Stub.TRANSACTION_autoAdjust_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int powerOff_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_powerOff_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCfg_proxy(int inputSource, java.lang.String configType, com.mediatek.tv.common.ConfigValue configParamsValue, com.mediatek.tv.common.ConfigValue configValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(inputSource);
_data.writeString(configType);
if ((configParamsValue!=null)) {
_data.writeInt(1);
configParamsValue.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((configValue!=null)) {
_data.writeInt(1);
configValue.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getCfg_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())&&(configParamsValue!=null)) {
configParamsValue.readFromParcel(_reply);
}
if ((0!=_reply.readInt())&&(configValue!=null)) {
configValue.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setCfg_proxy(int inputSource, java.lang.String configType, com.mediatek.tv.common.ConfigValue configValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(inputSource);
_data.writeString(configType);
if ((configValue!=null)) {
_data.writeInt(1);
configValue.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setCfg_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int updateCfg_proxy(java.lang.String configType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(configType);
mRemote.transact(Stub.TRANSACTION_updateCfg_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int resetCfgGroup_proxy(java.lang.String resetType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(resetType);
mRemote.transact(Stub.TRANSACTION_resetCfgGroup_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int readGPIO_proxy(com.mediatek.tv.common.ConfigValue configValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((configValue!=null)) {
_data.writeInt(1);
configValue.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_readGPIO_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())&&(configValue!=null)) {
configValue.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int writeGPIO_proxy(com.mediatek.tv.common.ConfigValue configValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((configValue!=null)) {
_data.writeInt(1);
configValue.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_writeGPIO_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCfgMinMax_proxy(java.lang.String configType, com.mediatek.tv.common.ConfigValue configValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(configType);
if ((configValue!=null)) {
_data.writeInt(1);
configValue.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getCfgMinMax_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())&&(configValue!=null)) {
configValue.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtSetConfig_proxy(int configFlag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(configFlag);
mRemote.transact(Stub.TRANSACTION_dtSetConfig_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtSetDst_proxy(boolean bEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_dtSetDst_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtSetTz_proxy(long tzOffset) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(tzOffset);
mRemote.transact(Stub.TRANSACTION_dtSetTz_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtSetUtc_proxy(long sec, int milliSec) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(sec);
_data.writeInt(milliSec);
mRemote.transact(Stub.TRANSACTION_dtSetUtc_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtSetDstCtrl_proxy(boolean bEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_dtSetDstCtrl_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtSetDsChange_proxy(long changeTime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(changeTime);
mRemote.transact(Stub.TRANSACTION_dtSetDsChange_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtSetDsOffset_proxy(long OffsetTime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(OffsetTime);
mRemote.transact(Stub.TRANSACTION_dtSetDsOffset_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtSetSyncSrc_proxy(int eSyncSrcType, int eSrcDescType, java.lang.String data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(eSyncSrcType);
_data.writeInt(eSrcDescType);
_data.writeString(data);
mRemote.transact(Stub.TRANSACTION_dtSetSyncSrc_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtSetTzCtrl_proxy(boolean bEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_dtSetTzCtrl_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtSetSysCountCode_proxy(byte[] countCode, int regionId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(countCode);
_data.writeInt(regionId);
mRemote.transact(Stub.TRANSACTION_dtSetSysCountCode_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean dtGetDst_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_dtGetDst_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long dtGetGps_proxy(int[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeIntArray(data);
mRemote.transact(Stub.TRANSACTION_dtGetGps_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
_reply.readIntArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long dtGetTz_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_dtGetTz_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long dtGetUtc_proxy(int[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeIntArray(data);
mRemote.transact(Stub.TRANSACTION_dtGetUtc_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
_reply.readIntArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long dtGetBrdcstUtc_proxy(int[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeIntArray(data);
mRemote.transact(Stub.TRANSACTION_dtGetBrdcstUtc_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
_reply.readIntArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtGetCountCode_proxy(int index, byte[] countCode, long[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
_data.writeByteArray(countCode);
_data.writeLongArray(data);
mRemote.transact(Stub.TRANSACTION_dtGetCountCode_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(countCode);
_reply.readLongArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean dtGetDstCtrl_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_dtGetDstCtrl_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long dtGetDsChange_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_dtGetDsChange_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long dtGetDsOffset_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_dtGetDsOffset_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean dtGetTzCtrl_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_dtGetTzCtrl_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtGetNumCountCode_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_dtGetNumCountCode_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtGetSysCountCode_proxy(byte[] countCode, int[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeByteArray(countCode);
_data.writeIntArray(data);
mRemote.transact(Stub.TRANSACTION_dtGetSysCountCode_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readByteArray(countCode);
_reply.readIntArray(data);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public byte dtGetLastSyncTblId_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
byte _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_dtGetLastSyncTblId_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readByte();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtCheckInputTime_proxy(boolean bEnable) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((bEnable)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_dtCheckInputTime_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dtConfigCheckInputTime_proxy(int eSetType, int setValue) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(eSetType);
_data.writeInt(setValue);
mRemote.transact(Stub.TRANSACTION_dtConfigCheckInputTime_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dt_utc_sec_to_dtg_proxy(long utcTime, com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(utcTime);
if ((dtgTime!=null)) {
_data.writeInt(1);
dtgTime.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dt_utc_sec_to_dtg_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
dtgTime.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dt_utc_sec_to_loc_dtg_proxy(long utcTime, com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(utcTime);
if ((dtgTime!=null)) {
_data.writeInt(1);
dtgTime.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dt_utc_sec_to_loc_dtg_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
dtgTime.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dt_conv_utc_local_proxy(com.mediatek.tv.model.DtDTG dtgTimeIn, com.mediatek.tv.model.DtDTG dtgTimeOut) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((dtgTimeIn!=null)) {
_data.writeInt(1);
dtgTimeIn.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((dtgTimeOut!=null)) {
_data.writeInt(1);
dtgTimeOut.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dt_conv_utc_local_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
dtgTimeOut.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long dt_dtg_to_sec_proxy(com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((dtgTime!=null)) {
_data.writeInt(1);
dtgTime.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dt_dtg_to_sec_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long dt_gps_sec_to_utc_sec_proxy(long gpsSec) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(gpsSec);
mRemote.transact(Stub.TRANSACTION_dt_gps_sec_to_utc_sec_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dt_bcd_to_sec_proxy(java.lang.String bcdTime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(bcdTime);
mRemote.transact(Stub.TRANSACTION_dt_bcd_to_sec_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dt_mjd_bcd_to_dtg_proxy(java.lang.String bcdTime, com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(bcdTime);
if ((dtgTime!=null)) {
_data.writeInt(1);
dtgTime.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dt_mjd_bcd_to_dtg_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
dtgTime.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dt_mjd_to_dtg_proxy(long mjdTime, com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(mjdTime);
if ((dtgTime!=null)) {
_data.writeInt(1);
dtgTime.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dt_mjd_to_dtg_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
dtgTime.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long dt_dtg_to_mjd_proxy(com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((dtgTime!=null)) {
_data.writeInt(1);
dtgTime.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dt_dtg_to_mjd_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dt_dtg_to_mjd_bcd_proxy(com.mediatek.tv.model.DtDTG dtgTime, int[] mjdInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((dtgTime!=null)) {
_data.writeInt(1);
dtgTime.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeIntArray(mjdInfo);
mRemote.transact(Stub.TRANSACTION_dt_dtg_to_mjd_bcd_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readIntArray(mjdInfo);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public long dt_diff_proxy(com.mediatek.tv.model.DtDTG dtgTimeFrom, com.mediatek.tv.model.DtDTG dtgTimeTo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
long _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((dtgTimeFrom!=null)) {
_data.writeInt(1);
dtgTimeFrom.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((dtgTimeTo!=null)) {
_data.writeInt(1);
dtgTimeTo.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dt_diff_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readLong();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dt_add_proxy(com.mediatek.tv.model.DtDTG dtgTimeOld, long addSec, com.mediatek.tv.model.DtDTG dtgTimeNew) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((dtgTimeOld!=null)) {
_data.writeInt(1);
dtgTimeOld.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeLong(addSec);
if ((dtgTimeNew!=null)) {
_data.writeInt(1);
dtgTimeNew.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_dt_add_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
dtgTimeNew.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean dt_is_leap_year_proxy(long year) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(year);
mRemote.transact(Stub.TRANSACTION_dt_is_leap_year_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int dt_reg_nfy_fct_proxy(long[] handle) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLongArray(handle);
mRemote.transact(Stub.TRANSACTION_dt_reg_nfy_fct_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readLongArray(handle);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int channelSelect_proxy(boolean b_focus, com.mediatek.tv.model.ChannelInfo chInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((b_focus)?(1):(0)));
if ((chInfo!=null)) {
_data.writeInt(1);
chInfo.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_channelSelect_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int channelSelectEx_proxy(boolean b_focus, int audioLangIndex, int audioMts, com.mediatek.tv.model.ChannelInfo chInfo, com.mediatek.tv.model.ExtraChannelInfo exChInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((b_focus)?(1):(0)));
_data.writeInt(audioLangIndex);
_data.writeInt(audioMts);
if ((chInfo!=null)) {
_data.writeInt(1);
chInfo.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((exChInfo!=null)) {
_data.writeInt(1);
exChInfo.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_channelSelectEx_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int syncStopService_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_syncStopService_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int startVideoStream_proxy(int focusID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
mRemote.transact(Stub.TRANSACTION_startVideoStream_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int syncStopSubtitleStream_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_syncStopSubtitleStream_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int fineTune_proxy(com.mediatek.tv.model.AnalogChannelInfo chInfo, int freq, boolean b_tuning) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((chInfo!=null)) {
_data.writeInt(1);
chInfo.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(freq);
_data.writeInt(((b_tuning)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_fineTune_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//TODO

public int freeze_proxy(int focusID, boolean b_freeze) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
_data.writeInt(((b_freeze)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_freeze_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setVideoMute_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_setVideoMute_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getVideoResolution_proxy(int focusID, com.mediatek.tv.model.VideoResolution videoRes) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
if ((videoRes!=null)) {
_data.writeInt(1);
videoRes.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getVideoResolution_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
videoRes.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getAudioInfo_proxy(int focusID, com.mediatek.tv.model.AudioInfo audioInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
mRemote.transact(Stub.TRANSACTION_getAudioInfo_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
audioInfo.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getsignalLevelInfo_proxy(int focusID, com.mediatek.tv.model.SignalLevelInfo signalLevelInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
mRemote.transact(Stub.TRANSACTION_getsignalLevelInfo_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
signalLevelInfo.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDtvAudioLangInfo_proxy(com.mediatek.tv.model.AudioLanguageInfo audioLangInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDtvAudioLangInfo_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
audioLangInfo.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setDtvAudioLang_proxy(java.lang.String audioLang) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(audioLang);
mRemote.transact(Stub.TRANSACTION_setDtvAudioLang_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setDtvAudioLangByIndex_proxy(int focusID, int audioIndex) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
_data.writeInt(audioIndex);
mRemote.transact(Stub.TRANSACTION_setDtvAudioLangByIndex_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getSubtitleInfo_proxy(com.mediatek.tv.model.SubtitleInfo subtitleInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSubtitleInfo_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
subtitleInfo.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setSubtitleLang_proxy(java.lang.String audioLang) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(audioLang);
mRemote.transact(Stub.TRANSACTION_setSubtitleLang_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getStreamMpegPid_proxy(java.lang.String streamType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(streamType);
mRemote.transact(Stub.TRANSACTION_getStreamMpegPid_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int selectMpegStreamByPid_proxy(java.lang.String streamType, int pid) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(streamType);
_data.writeInt(pid);
mRemote.transact(Stub.TRANSACTION_selectMpegStreamByPid_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isCaptureLogo_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isCaptureLogo_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setMute_proxy(boolean b_mute) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((b_mute)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setMute_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean getMute_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMute_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDtvAudioDecodeType_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDtvAudioDecodeType_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setVideoBlueMute_proxy(int focusID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
mRemote.transact(Stub.TRANSACTION_setVideoBlueMute_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setVideoBlueMuteEx_proxy(int focusID, boolean bBlueMute, boolean bBlock) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
_data.writeInt(((bBlueMute)?(1):(0)));
_data.writeInt(((bBlock)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setVideoBlueMuteEx_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isFreeze_proxy(int focusID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
mRemote.transact(Stub.TRANSACTION_isFreeze_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean enableFreeze_proxy(int focusID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
mRemote.transact(Stub.TRANSACTION_enableFreeze_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setDisplayAspectRatio_proxy(int dispAspRatio) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(dispAspRatio);
mRemote.transact(Stub.TRANSACTION_setDisplayAspectRatio_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDisplayAspectRatio_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDisplayAspectRatio_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//int serviceSet_proxy(String setType,Object setValue);
//int serviceGet_proxy(String getType,Object getValue);

public int updateTVWindowRegion_proxy(int focusID, int winX, int winY, int winWidth, int winHeight) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
_data.writeInt(winX);
_data.writeInt(winY);
_data.writeInt(winWidth);
_data.writeInt(winHeight);
mRemote.transact(Stub.TRANSACTION_updateTVWindowRegion_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int stopStream_proxy(int focusID, int streamType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
_data.writeInt(streamType);
mRemote.transact(Stub.TRANSACTION_stopStream_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int startAudioStream_proxy(int focusID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
mRemote.transact(Stub.TRANSACTION_startAudioStream_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int syncStopVideoStream_proxy(int focusID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
mRemote.transact(Stub.TRANSACTION_syncStopVideoStream_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int showSnowAsNoSignal_proxy(int focusID, boolean bSnow) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
_data.writeInt(((bSnow)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_showSnowAsNoSignal_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int updateFocusWindow_proxy(int focusID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
mRemote.transact(Stub.TRANSACTION_updateFocusWindow_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int updateTVMode_proxy(int tvMode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(tvMode);
mRemote.transact(Stub.TRANSACTION_updateTVMode_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setMTS_proxy(int focusID, int audMTSType) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(focusID);
_data.writeInt(audMTSType);
mRemote.transact(Stub.TRANSACTION_setMTS_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//Channel service start

public int setChannelList_proxy(int channelOperator, int svlid, com.mediatek.tv.model.ChannelModel channelModel) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(channelOperator);
_data.writeInt(svlid);
if ((channelModel!=null)) {
_data.writeInt(1);
channelModel.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_setChannelList_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getChannelList_proxy(int svlId, com.mediatek.tv.model.ChannelModel channelModel) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(svlId);
if ((channelModel!=null)) {
_data.writeInt(1);
channelModel.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getChannelList_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
channelModel.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int fsSyncChannelList_proxy(int svlId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(svlId);
mRemote.transact(Stub.TRANSACTION_fsSyncChannelList_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int fsStoreChannelList_proxy(int svlId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(svlId);
mRemote.transact(Stub.TRANSACTION_fsStoreChannelList_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int digitalDBClean_proxy(int svlId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(svlId);
mRemote.transact(Stub.TRANSACTION_digitalDBClean_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
//Channel service end
/* scan service proxy function start */
public int startScan_pal_secam_proxy(java.lang.String scanMode, com.mediatek.tv.model.ScanParaPalSecam p) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
if ((p!=null)) {
_data.writeInt(1);
p.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_startScan_pal_secam_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int cancelScan_proxy(java.lang.String scanMode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
mRemote.transact(Stub.TRANSACTION_cancelScan_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getScanData_proxy(java.lang.String scanMode, int type, com.mediatek.tv.model.ScanExchangeFrenquenceRange scanExchangeData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
_data.writeInt(type);
if ((scanExchangeData!=null)) {
_data.writeInt(1);
scanExchangeData.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getScanData_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
scanExchangeData.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int scanExchangeData_proxy(int[] exchangeData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeIntArray(exchangeData);
mRemote.transact(Stub.TRANSACTION_scanExchangeData_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readIntArray(exchangeData);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int startScan_dvbc_proxy(java.lang.String scanMode, com.mediatek.tv.model.ScanParaDvbc p) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
if ((p!=null)) {
_data.writeInt(1);
p.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_startScan_dvbc_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int cancelScan_dvbc_proxy(java.lang.String scanMode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
mRemote.transact(Stub.TRANSACTION_cancelScan_dvbc_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDefaultSymRate_proxy(java.lang.String countryCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(countryCode);
mRemote.transact(Stub.TRANSACTION_getDefaultSymRate_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDefaultFrequency_proxy(java.lang.String countryCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(countryCode);
mRemote.transact(Stub.TRANSACTION_getDefaultFrequency_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDefaultEMod_proxy(java.lang.String countryCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(countryCode);
mRemote.transact(Stub.TRANSACTION_getDefaultEMod_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDefaultNwID_proxy(java.lang.String countryCode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(countryCode);
mRemote.transact(Stub.TRANSACTION_getDefaultNwID_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDvbcScanTypeNum_proxy(java.lang.String scanMode, com.mediatek.tv.model.DvbcProgramType dvbcScanData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
mRemote.transact(Stub.TRANSACTION_getDvbcScanTypeNum_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
dvbcScanData.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDvbcFreqRange_proxy(java.lang.String scanMode, com.mediatek.tv.model.DvbcFreqRange freqRange) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
mRemote.transact(Stub.TRANSACTION_getDvbcFreqRange_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
freqRange.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDvbcMainFrequence_proxy(java.lang.String scanMode, com.mediatek.tv.model.MainFrequence mainFrequence) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
mRemote.transact(Stub.TRANSACTION_getDvbcMainFrequence_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
mainFrequence.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int startScan_dtmb_proxy(java.lang.String scanMode, com.mediatek.tv.model.ScanParaDtmb p) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
if ((p!=null)) {
_data.writeInt(1);
p.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_startScan_dtmb_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int cancelScan_dtmb_proxy(java.lang.String scanMode) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
mRemote.transact(Stub.TRANSACTION_cancelScan_dtmb_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getDtmbFreqRange_proxy(java.lang.String scanMode, com.mediatek.tv.model.DtmbFreqRange freqRange) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
mRemote.transact(Stub.TRANSACTION_getDtmbFreqRange_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
freqRange.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getFirstDtmbScanRF_proxy(java.lang.String scanMode, com.mediatek.tv.model.DtmbScanRF firstRF) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
mRemote.transact(Stub.TRANSACTION_getFirstDtmbScanRF_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
firstRF.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getLastDtmbScanRF_proxy(java.lang.String scanMode, com.mediatek.tv.model.DtmbScanRF lastRF) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
mRemote.transact(Stub.TRANSACTION_getLastDtmbScanRF_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
lastRF.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getNextDtmbScanRF_proxy(java.lang.String scanMode, com.mediatek.tv.model.DtmbScanRF currRF, com.mediatek.tv.model.DtmbScanRF nextRF) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
if ((currRF!=null)) {
_data.writeInt(1);
currRF.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getNextDtmbScanRF_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
nextRF.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getPrevDtmbScanRF_proxy(java.lang.String scanMode, com.mediatek.tv.model.DtmbScanRF currRF, com.mediatek.tv.model.DtmbScanRF prevRF) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
if ((currRF!=null)) {
_data.writeInt(1);
currRF.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getPrevDtmbScanRF_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
prevRF.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurrentDtmbScanRF_proxy(java.lang.String scanMode, int channelId, com.mediatek.tv.model.DtmbScanRF currRF) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(scanMode);
_data.writeInt(channelId);
mRemote.transact(Stub.TRANSACTION_getCurrentDtmbScanRF_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
currRF.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* scan service proxy function end *//* input service proxy function start */
public int inputServiceBind_proxy(int output, int inputId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(output);
_data.writeInt(inputId);
mRemote.transact(Stub.TRANSACTION_inputServiceBind_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int inputServiceGetRecord_proxy(int index, com.mediatek.tv.model.InputRecord inputRecord) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(index);
if ((inputRecord!=null)) {
_data.writeInt(1);
inputRecord.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_inputServiceGetRecord_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
inputRecord.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int inputServiceSetOutputMute(int output, boolean mute) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(output);
_data.writeInt(((mute)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_inputServiceSetOutputMute, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int inputServiceSwap_proxy(int output1, int output2) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(output1);
_data.writeInt(output2);
mRemote.transact(Stub.TRANSACTION_inputServiceSwap_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setScreenOutputRect_proxy(int output, int left, int right, int top, int bottom) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(output);
_data.writeInt(left);
_data.writeInt(right);
_data.writeInt(top);
_data.writeInt(bottom);
mRemote.transact(Stub.TRANSACTION_setScreenOutputRect_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setScreenOutputVideoRect_proxy(int output, int left, int right, int top, int bottom) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(output);
_data.writeInt(left);
_data.writeInt(right);
_data.writeInt(top);
_data.writeInt(bottom);
mRemote.transact(Stub.TRANSACTION_setScreenOutputVideoRect_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getScreenOutputRect_proxy(int output, com.mediatek.tv.model.InputRegion inputRegion) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(output);
if ((inputRegion!=null)) {
_data.writeInt(1);
inputRegion.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getScreenOutputRect_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
inputRegion.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getScreenOutputVideoRect_proxy(int output, com.mediatek.tv.model.InputRegion inputRegion) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(output);
if ((inputRegion!=null)) {
_data.writeInt(1);
inputRegion.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getScreenOutputVideoRect_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
inputRegion.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int inputSourceExchangeData_proxy(int[] inputSourceData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeIntArray(inputSourceData);
mRemote.transact(Stub.TRANSACTION_inputSourceExchangeData_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readIntArray(inputSourceData);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* input service proxy function end *//* OSD service proxy function start */
public boolean setOSDColorKey_proxy(boolean enable, int colorkey) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((enable)?(1):(0)));
_data.writeInt(colorkey);
mRemote.transact(Stub.TRANSACTION_setOSDColorKey_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean setOSDOpacity_proxy(int opacity) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(opacity);
mRemote.transact(Stub.TRANSACTION_setOSDOpacity_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* OSD service proxy function end *//* Event service proxy function start */
public int eventServiceSetCommand_proxy(com.mediatek.tv.model.EventCommand eventCommand) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((eventCommand!=null)) {
_data.writeInt(1);
eventCommand.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_eventServiceSetCommand_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int eventServiceGetPFEvents_proxy(com.mediatek.tv.model.DvbChannelInfo channelInfo, java.util.List<com.mediatek.tv.model.EventInfo> events) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((channelInfo!=null)) {
_data.writeInt(1);
channelInfo.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_eventServiceGetPFEvents_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readTypedList(events, com.mediatek.tv.model.EventInfo.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int eventServiceGetScheduleEvents_proxy(com.mediatek.tv.model.DvbChannelInfo channelInfo, long startTime, long endTime, java.util.List<com.mediatek.tv.model.EventInfo> events) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((channelInfo!=null)) {
_data.writeInt(1);
channelInfo.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeLong(startTime);
_data.writeLong(endTime);
mRemote.transact(Stub.TRANSACTION_eventServiceGetScheduleEvents_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
_reply.readTypedList(events, com.mediatek.tv.model.EventInfo.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* Event service proxy function end */
public int getSlotNum_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSlotNum_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isSlotActive_proxy(int slotId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
mRemote.transact(Stub.TRANSACTION_isSlotActive_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int enterMMI_proxy(int slotId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
mRemote.transact(Stub.TRANSACTION_enterMMI_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getCamName_proxy(int slotId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
mRemote.transact(Stub.TRANSACTION_getCamName_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int[] getCamSystemIDInfo_proxy(int slotId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
mRemote.transact(Stub.TRANSACTION_getCamSystemIDInfo_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.createIntArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int closeMMI_proxy(int slotId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
mRemote.transact(Stub.TRANSACTION_closeMMI_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setMMIClosed_proxy(int slotId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
mRemote.transact(Stub.TRANSACTION_setMMIClosed_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int answerMMIMenu_proxy(int slotId, int menuId, char answerItem) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
_data.writeInt(menuId);
_data.writeInt(((int)answerItem));
mRemote.transact(Stub.TRANSACTION_answerMMIMenu_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int answerMMIEnq_proxy(int slotId, int enqId, boolean answer, java.lang.String answerData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
_data.writeInt(enqId);
_data.writeInt(((answer)?(1):(0)));
_data.writeString(answerData);
mRemote.transact(Stub.TRANSACTION_answerMMIEnq_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int askRelease_proxy(int slotId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
mRemote.transact(Stub.TRANSACTION_askRelease_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setCITsPath_proxy(int slotId, boolean b_switch) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
_data.writeInt(((b_switch)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setCITsPath_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int setCIInputDTVPath_proxy(int slotId, boolean b_switch) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
_data.writeInt(((b_switch)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setCIInputDTVPath_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getTunedChannel_proxy(int svlId, com.mediatek.tv.model.HostControlTune tune) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(svlId);
if ((tune!=null)) {
_data.writeInt(1);
tune.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getTunedChannel_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
if ((0!=_reply.readInt())) {
tune.readFromParcel(_reply);
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int activateComponent_proxy(java.lang.String comp) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(comp);
mRemote.transact(Stub.TRANSACTION_activateComponent_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int inactivateComponent_proxy(java.lang.String comp) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(comp);
mRemote.transact(Stub.TRANSACTION_inactivateComponent_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int updateSysStatus_proxy(java.lang.String statusDesc) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(statusDesc);
mRemote.transact(Stub.TRANSACTION_updateSysStatus_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean isTTXAvail_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isTTXAvail_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int sendkeyEventtoComp_proxy(int ui4_keycode, int keyevent) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(ui4_keycode);
_data.writeInt(keyevent);
mRemote.transact(Stub.TRANSACTION_sendkeyEventtoComp_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int lockDigitalTuner_proxy(int frequency) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(frequency);
mRemote.transact(Stub.TRANSACTION_lockDigitalTuner_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int unlockDigitalTuner_proxy(int magicID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(magicID);
mRemote.transact(Stub.TRANSACTION_unlockDigitalTuner_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurrentDTVAudioCodec_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentDTVAudioCodec_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCurrentDTVVideoCodec_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCurrentDTVVideoCodec_proxy, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void setMuteState_proxy(boolean b_mute) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((b_mute)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_setMuteState_proxy, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean getMuteState_proxy() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMuteState_proxy, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_unregisterAll = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_openUARTSerial_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_closeUARTSerial_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getUARTSerialSetting_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getUARTSerialOperationMode_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_setUARTSerialSetting_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_setUARTSerialOperationMode_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_setUARTSerialMagicString_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_outputUARTSerial_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_autoAdjust_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_powerOff_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_getCfg_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_setCfg_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_updateCfg_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_resetCfgGroup_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_readGPIO_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_writeGPIO_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_getCfgMinMax_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_dtSetConfig_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_dtSetDst_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_dtSetTz_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_dtSetUtc_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
static final int TRANSACTION_dtSetDstCtrl_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 24);
static final int TRANSACTION_dtSetDsChange_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 25);
static final int TRANSACTION_dtSetDsOffset_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 26);
static final int TRANSACTION_dtSetSyncSrc_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 27);
static final int TRANSACTION_dtSetTzCtrl_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 28);
static final int TRANSACTION_dtSetSysCountCode_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 29);
static final int TRANSACTION_dtGetDst_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 30);
static final int TRANSACTION_dtGetGps_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 31);
static final int TRANSACTION_dtGetTz_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 32);
static final int TRANSACTION_dtGetUtc_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 33);
static final int TRANSACTION_dtGetBrdcstUtc_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 34);
static final int TRANSACTION_dtGetCountCode_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 35);
static final int TRANSACTION_dtGetDstCtrl_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 36);
static final int TRANSACTION_dtGetDsChange_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 37);
static final int TRANSACTION_dtGetDsOffset_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 38);
static final int TRANSACTION_dtGetTzCtrl_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 39);
static final int TRANSACTION_dtGetNumCountCode_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 40);
static final int TRANSACTION_dtGetSysCountCode_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 41);
static final int TRANSACTION_dtGetLastSyncTblId_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 42);
static final int TRANSACTION_dtCheckInputTime_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 43);
static final int TRANSACTION_dtConfigCheckInputTime_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 44);
static final int TRANSACTION_dt_utc_sec_to_dtg_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 45);
static final int TRANSACTION_dt_utc_sec_to_loc_dtg_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 46);
static final int TRANSACTION_dt_conv_utc_local_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 47);
static final int TRANSACTION_dt_dtg_to_sec_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 48);
static final int TRANSACTION_dt_gps_sec_to_utc_sec_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 49);
static final int TRANSACTION_dt_bcd_to_sec_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 50);
static final int TRANSACTION_dt_mjd_bcd_to_dtg_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 51);
static final int TRANSACTION_dt_mjd_to_dtg_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 52);
static final int TRANSACTION_dt_dtg_to_mjd_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 53);
static final int TRANSACTION_dt_dtg_to_mjd_bcd_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 54);
static final int TRANSACTION_dt_diff_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 55);
static final int TRANSACTION_dt_add_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 56);
static final int TRANSACTION_dt_is_leap_year_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 57);
static final int TRANSACTION_dt_reg_nfy_fct_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 58);
static final int TRANSACTION_channelSelect_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 59);
static final int TRANSACTION_channelSelectEx_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 60);
static final int TRANSACTION_syncStopService_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 61);
static final int TRANSACTION_startVideoStream_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 62);
static final int TRANSACTION_syncStopSubtitleStream_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 63);
static final int TRANSACTION_fineTune_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 64);
static final int TRANSACTION_freeze_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 65);
static final int TRANSACTION_setVideoMute_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 66);
static final int TRANSACTION_getVideoResolution_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 67);
static final int TRANSACTION_getAudioInfo_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 68);
static final int TRANSACTION_getsignalLevelInfo_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 69);
static final int TRANSACTION_getDtvAudioLangInfo_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 70);
static final int TRANSACTION_setDtvAudioLang_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 71);
static final int TRANSACTION_setDtvAudioLangByIndex_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 72);
static final int TRANSACTION_getSubtitleInfo_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 73);
static final int TRANSACTION_setSubtitleLang_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 74);
static final int TRANSACTION_getStreamMpegPid_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 75);
static final int TRANSACTION_selectMpegStreamByPid_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 76);
static final int TRANSACTION_isCaptureLogo_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 77);
static final int TRANSACTION_setMute_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 78);
static final int TRANSACTION_getMute_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 79);
static final int TRANSACTION_setVideoBlueMute_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 80);
static final int TRANSACTION_setVideoBlueMuteEx_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 81);
static final int TRANSACTION_isFreeze_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 82);
static final int TRANSACTION_enableFreeze_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 83);
static final int TRANSACTION_setDisplayAspectRatio_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 84);
static final int TRANSACTION_getDisplayAspectRatio_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 85);
static final int TRANSACTION_updateTVWindowRegion_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 86);
static final int TRANSACTION_stopStream_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 87);
static final int TRANSACTION_startAudioStream_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 88);
static final int TRANSACTION_syncStopVideoStream_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 89);
static final int TRANSACTION_showSnowAsNoSignal_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 90);
static final int TRANSACTION_updateFocusWindow_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 91);
static final int TRANSACTION_updateTVMode_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 92);
static final int TRANSACTION_setMTS_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 93);
static final int TRANSACTION_setChannelList_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 94);
static final int TRANSACTION_getChannelList_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 95);
static final int TRANSACTION_fsSyncChannelList_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 96);
static final int TRANSACTION_fsStoreChannelList_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 97);
static final int TRANSACTION_digitalDBClean_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 98);
static final int TRANSACTION_startScan_pal_secam_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 99);
static final int TRANSACTION_cancelScan_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 100);
static final int TRANSACTION_getScanData_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 101);
static final int TRANSACTION_scanExchangeData_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 102);
static final int TRANSACTION_startScan_dvbc_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 103);
static final int TRANSACTION_cancelScan_dvbc_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 104);
static final int TRANSACTION_getDefaultSymRate_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 105);
static final int TRANSACTION_getDefaultFrequency_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 106);
static final int TRANSACTION_getDefaultEMod_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 107);
static final int TRANSACTION_getDefaultNwID_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 108);
static final int TRANSACTION_getDvbcScanTypeNum_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 109);
static final int TRANSACTION_getDvbcFreqRange_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 110);
static final int TRANSACTION_getDvbcMainFrequence_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 111);
static final int TRANSACTION_startScan_dtmb_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 112);
static final int TRANSACTION_cancelScan_dtmb_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 113);
static final int TRANSACTION_getDtmbFreqRange_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 114);
static final int TRANSACTION_getFirstDtmbScanRF_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 115);
static final int TRANSACTION_getLastDtmbScanRF_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 116);
static final int TRANSACTION_getNextDtmbScanRF_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 117);
static final int TRANSACTION_getPrevDtmbScanRF_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 118);
static final int TRANSACTION_getCurrentDtmbScanRF_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 119);
static final int TRANSACTION_inputServiceBind_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 120);
static final int TRANSACTION_inputServiceGetRecord_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 121);
static final int TRANSACTION_inputServiceSetOutputMute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 122);
static final int TRANSACTION_inputServiceSwap_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 123);
static final int TRANSACTION_setScreenOutputRect_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 124);
static final int TRANSACTION_setScreenOutputVideoRect_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 125);
static final int TRANSACTION_getScreenOutputRect_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 126);
static final int TRANSACTION_getScreenOutputVideoRect_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 127);
static final int TRANSACTION_inputSourceExchangeData_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 128);
static final int TRANSACTION_setOSDColorKey_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 129);
static final int TRANSACTION_setOSDOpacity_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 130);
static final int TRANSACTION_eventServiceSetCommand_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 131);
static final int TRANSACTION_eventServiceGetPFEvents_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 132);
static final int TRANSACTION_eventServiceGetScheduleEvents_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 133);
static final int TRANSACTION_getSlotNum_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 134);
static final int TRANSACTION_isSlotActive_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 135);
static final int TRANSACTION_enterMMI_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 136);
static final int TRANSACTION_getCamName_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 137);
static final int TRANSACTION_getCamSystemIDInfo_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 138);
static final int TRANSACTION_closeMMI_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 139);
static final int TRANSACTION_setMMIClosed_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 140);
static final int TRANSACTION_answerMMIMenu_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 141);
static final int TRANSACTION_answerMMIEnq_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 142);
static final int TRANSACTION_askRelease_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 143);
static final int TRANSACTION_setCITsPath_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 144);
static final int TRANSACTION_setCIInputDTVPath_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 145);
static final int TRANSACTION_getTunedChannel_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 146);
static final int TRANSACTION_activateComponent_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 147);
static final int TRANSACTION_inactivateComponent_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 148);
static final int TRANSACTION_updateSysStatus_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 149);
static final int TRANSACTION_isTTXAvail_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 150);
static final int TRANSACTION_sendkeyEventtoComp_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 151);
static final int TRANSACTION_lockDigitalTuner_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 152);
static final int TRANSACTION_unlockDigitalTuner_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 153);
static final int TRANSACTION_getCurrentDTVAudioCodec_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 154);
static final int TRANSACTION_getCurrentDTVVideoCodec_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 155);
static final int TRANSACTION_getDtvAudioDecodeType_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 156);
static final int TRANSACTION_setMuteState_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 158);
static final int TRANSACTION_getMuteState_proxy = (android.os.IBinder.FIRST_CALL_TRANSACTION + 159);
}
public void registerCallback(com.mediatek.tv.service.ITVCallBack cb) throws android.os.RemoteException;
public void unregisterCallback(com.mediatek.tv.service.ITVCallBack cb) throws android.os.RemoteException;
public void unregisterAll() throws android.os.RemoteException;
/*Factory*/
public int openUARTSerial_proxy(int uartSerialID, int[] uartSerialSetting, int[] handle) throws android.os.RemoteException;
public int closeUARTSerial_proxy(int handle) throws android.os.RemoteException;
public int getUARTSerialSetting_proxy(int handle, int[] uartSerialSetting) throws android.os.RemoteException;
public int getUARTSerialOperationMode_proxy(int handle, int[] operationMode) throws android.os.RemoteException;
public int setUARTSerialSetting_proxy(int handle, int[] uartSerialSetting) throws android.os.RemoteException;
public int setUARTSerialOperationMode_proxy(int handle, int operationMode) throws android.os.RemoteException;
public int setUARTSerialMagicString_proxy(int handle, byte[] uartSerialMagicSetting) throws android.os.RemoteException;
public int outputUARTSerial_proxy(int handle, byte[] uartSerialData) throws android.os.RemoteException;
/*Factory*/
public int autoAdjust_proxy(java.lang.String autoType) throws android.os.RemoteException;
public int powerOff_proxy() throws android.os.RemoteException;
public int getCfg_proxy(int inputSource, java.lang.String configType, com.mediatek.tv.common.ConfigValue configParamsValue, com.mediatek.tv.common.ConfigValue configValue) throws android.os.RemoteException;
public int setCfg_proxy(int inputSource, java.lang.String configType, com.mediatek.tv.common.ConfigValue configValue) throws android.os.RemoteException;
public int updateCfg_proxy(java.lang.String configType) throws android.os.RemoteException;
public int resetCfgGroup_proxy(java.lang.String resetType) throws android.os.RemoteException;
public int readGPIO_proxy(com.mediatek.tv.common.ConfigValue configValue) throws android.os.RemoteException;
public int writeGPIO_proxy(com.mediatek.tv.common.ConfigValue configValue) throws android.os.RemoteException;
public int getCfgMinMax_proxy(java.lang.String configType, com.mediatek.tv.common.ConfigValue configValue) throws android.os.RemoteException;
public int dtSetConfig_proxy(int configFlag) throws android.os.RemoteException;
public int dtSetDst_proxy(boolean bEnable) throws android.os.RemoteException;
public int dtSetTz_proxy(long tzOffset) throws android.os.RemoteException;
public int dtSetUtc_proxy(long sec, int milliSec) throws android.os.RemoteException;
public int dtSetDstCtrl_proxy(boolean bEnable) throws android.os.RemoteException;
public int dtSetDsChange_proxy(long changeTime) throws android.os.RemoteException;
public int dtSetDsOffset_proxy(long OffsetTime) throws android.os.RemoteException;
public int dtSetSyncSrc_proxy(int eSyncSrcType, int eSrcDescType, java.lang.String data) throws android.os.RemoteException;
public int dtSetTzCtrl_proxy(boolean bEnable) throws android.os.RemoteException;
public int dtSetSysCountCode_proxy(byte[] countCode, int regionId) throws android.os.RemoteException;
public boolean dtGetDst_proxy() throws android.os.RemoteException;
public long dtGetGps_proxy(int[] data) throws android.os.RemoteException;
public long dtGetTz_proxy() throws android.os.RemoteException;
public long dtGetUtc_proxy(int[] data) throws android.os.RemoteException;
public long dtGetBrdcstUtc_proxy(int[] data) throws android.os.RemoteException;
public int dtGetCountCode_proxy(int index, byte[] countCode, long[] data) throws android.os.RemoteException;
public boolean dtGetDstCtrl_proxy() throws android.os.RemoteException;
public long dtGetDsChange_proxy() throws android.os.RemoteException;
public long dtGetDsOffset_proxy() throws android.os.RemoteException;
public boolean dtGetTzCtrl_proxy() throws android.os.RemoteException;
public int dtGetNumCountCode_proxy() throws android.os.RemoteException;
public int dtGetSysCountCode_proxy(byte[] countCode, int[] data) throws android.os.RemoteException;
public byte dtGetLastSyncTblId_proxy() throws android.os.RemoteException;
public int dtCheckInputTime_proxy(boolean bEnable) throws android.os.RemoteException;
public int dtConfigCheckInputTime_proxy(int eSetType, int setValue) throws android.os.RemoteException;
public int dt_utc_sec_to_dtg_proxy(long utcTime, com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException;
public int dt_utc_sec_to_loc_dtg_proxy(long utcTime, com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException;
public int dt_conv_utc_local_proxy(com.mediatek.tv.model.DtDTG dtgTimeIn, com.mediatek.tv.model.DtDTG dtgTimeOut) throws android.os.RemoteException;
public long dt_dtg_to_sec_proxy(com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException;
public long dt_gps_sec_to_utc_sec_proxy(long gpsSec) throws android.os.RemoteException;
public int dt_bcd_to_sec_proxy(java.lang.String bcdTime) throws android.os.RemoteException;
public int dt_mjd_bcd_to_dtg_proxy(java.lang.String bcdTime, com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException;
public int dt_mjd_to_dtg_proxy(long mjdTime, com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException;
public long dt_dtg_to_mjd_proxy(com.mediatek.tv.model.DtDTG dtgTime) throws android.os.RemoteException;
public int dt_dtg_to_mjd_bcd_proxy(com.mediatek.tv.model.DtDTG dtgTime, int[] mjdInfo) throws android.os.RemoteException;
public long dt_diff_proxy(com.mediatek.tv.model.DtDTG dtgTimeFrom, com.mediatek.tv.model.DtDTG dtgTimeTo) throws android.os.RemoteException;
public int dt_add_proxy(com.mediatek.tv.model.DtDTG dtgTimeOld, long addSec, com.mediatek.tv.model.DtDTG dtgTimeNew) throws android.os.RemoteException;
public boolean dt_is_leap_year_proxy(long year) throws android.os.RemoteException;
public int dt_reg_nfy_fct_proxy(long[] handle) throws android.os.RemoteException;
public int channelSelect_proxy(boolean b_focus, com.mediatek.tv.model.ChannelInfo chInfo) throws android.os.RemoteException;
public int channelSelectEx_proxy(boolean b_focus, int audioLangIndex, int audioMts, com.mediatek.tv.model.ChannelInfo chInfo, com.mediatek.tv.model.ExtraChannelInfo exChInfo) throws android.os.RemoteException;
public int syncStopService_proxy() throws android.os.RemoteException;
public int startVideoStream_proxy(int focusID) throws android.os.RemoteException;
public int syncStopSubtitleStream_proxy() throws android.os.RemoteException;
public int fineTune_proxy(com.mediatek.tv.model.AnalogChannelInfo chInfo, int freq, boolean b_tuning) throws android.os.RemoteException;
//TODO

public int freeze_proxy(int focusID, boolean b_freeze) throws android.os.RemoteException;
public int setVideoMute_proxy() throws android.os.RemoteException;
public int getVideoResolution_proxy(int focusID, com.mediatek.tv.model.VideoResolution videoRes) throws android.os.RemoteException;
public int getAudioInfo_proxy(int focusID, com.mediatek.tv.model.AudioInfo audioInfo) throws android.os.RemoteException;
public int getsignalLevelInfo_proxy(int focusID, com.mediatek.tv.model.SignalLevelInfo signalLevelInfo) throws android.os.RemoteException;
public int getDtvAudioLangInfo_proxy(com.mediatek.tv.model.AudioLanguageInfo audioLangInfo) throws android.os.RemoteException;
public int setDtvAudioLang_proxy(java.lang.String audioLang) throws android.os.RemoteException;
public int setDtvAudioLangByIndex_proxy(int focusID, int audioIndex) throws android.os.RemoteException;
public int getSubtitleInfo_proxy(com.mediatek.tv.model.SubtitleInfo subtitleInfo) throws android.os.RemoteException;
public int setSubtitleLang_proxy(java.lang.String audioLang) throws android.os.RemoteException;
public int getStreamMpegPid_proxy(java.lang.String streamType) throws android.os.RemoteException;
public int selectMpegStreamByPid_proxy(java.lang.String streamType, int pid) throws android.os.RemoteException;
public boolean isCaptureLogo_proxy() throws android.os.RemoteException;
public int setMute_proxy(boolean b_mute) throws android.os.RemoteException;
public boolean getMute_proxy() throws android.os.RemoteException;
public int getDtvAudioDecodeType_proxy() throws android.os.RemoteException;
public int setVideoBlueMute_proxy(int focusID) throws android.os.RemoteException;
public int setVideoBlueMuteEx_proxy(int focusID, boolean bBlueMute, boolean bBlock) throws android.os.RemoteException;
public boolean isFreeze_proxy(int focusID) throws android.os.RemoteException;
public boolean enableFreeze_proxy(int focusID) throws android.os.RemoteException;
public int setDisplayAspectRatio_proxy(int dispAspRatio) throws android.os.RemoteException;
public int getDisplayAspectRatio_proxy() throws android.os.RemoteException;
//int serviceSet_proxy(String setType,Object setValue);
//int serviceGet_proxy(String getType,Object getValue);

public int updateTVWindowRegion_proxy(int focusID, int winX, int winY, int winWidth, int winHeight) throws android.os.RemoteException;
public int stopStream_proxy(int focusID, int streamType) throws android.os.RemoteException;
public int startAudioStream_proxy(int focusID) throws android.os.RemoteException;
public int syncStopVideoStream_proxy(int focusID) throws android.os.RemoteException;
public int showSnowAsNoSignal_proxy(int focusID, boolean bSnow) throws android.os.RemoteException;
public int updateFocusWindow_proxy(int focusID) throws android.os.RemoteException;
public int updateTVMode_proxy(int tvMode) throws android.os.RemoteException;
public int setMTS_proxy(int focusID, int audMTSType) throws android.os.RemoteException;
//Channel service start

public int setChannelList_proxy(int channelOperator, int svlid, com.mediatek.tv.model.ChannelModel channelModel) throws android.os.RemoteException;
public int getChannelList_proxy(int svlId, com.mediatek.tv.model.ChannelModel channelModel) throws android.os.RemoteException;
public int fsSyncChannelList_proxy(int svlId) throws android.os.RemoteException;
public int fsStoreChannelList_proxy(int svlId) throws android.os.RemoteException;
public int digitalDBClean_proxy(int svlId) throws android.os.RemoteException;
//Channel service end
/* scan service proxy function start */
public int startScan_pal_secam_proxy(java.lang.String scanMode, com.mediatek.tv.model.ScanParaPalSecam p) throws android.os.RemoteException;
public int cancelScan_proxy(java.lang.String scanMode) throws android.os.RemoteException;
public int getScanData_proxy(java.lang.String scanMode, int type, com.mediatek.tv.model.ScanExchangeFrenquenceRange scanExchangeData) throws android.os.RemoteException;
public int scanExchangeData_proxy(int[] exchangeData) throws android.os.RemoteException;
public int startScan_dvbc_proxy(java.lang.String scanMode, com.mediatek.tv.model.ScanParaDvbc p) throws android.os.RemoteException;
public int cancelScan_dvbc_proxy(java.lang.String scanMode) throws android.os.RemoteException;
public int getDefaultSymRate_proxy(java.lang.String countryCode) throws android.os.RemoteException;
public int getDefaultFrequency_proxy(java.lang.String countryCode) throws android.os.RemoteException;
public int getDefaultEMod_proxy(java.lang.String countryCode) throws android.os.RemoteException;
public int getDefaultNwID_proxy(java.lang.String countryCode) throws android.os.RemoteException;
public int getDvbcScanTypeNum_proxy(java.lang.String scanMode, com.mediatek.tv.model.DvbcProgramType dvbcScanData) throws android.os.RemoteException;
public int getDvbcFreqRange_proxy(java.lang.String scanMode, com.mediatek.tv.model.DvbcFreqRange freqRange) throws android.os.RemoteException;
public int getDvbcMainFrequence_proxy(java.lang.String scanMode, com.mediatek.tv.model.MainFrequence mainFrequence) throws android.os.RemoteException;
public int startScan_dtmb_proxy(java.lang.String scanMode, com.mediatek.tv.model.ScanParaDtmb p) throws android.os.RemoteException;
public int cancelScan_dtmb_proxy(java.lang.String scanMode) throws android.os.RemoteException;
public int getDtmbFreqRange_proxy(java.lang.String scanMode, com.mediatek.tv.model.DtmbFreqRange freqRange) throws android.os.RemoteException;
public int getFirstDtmbScanRF_proxy(java.lang.String scanMode, com.mediatek.tv.model.DtmbScanRF firstRF) throws android.os.RemoteException;
public int getLastDtmbScanRF_proxy(java.lang.String scanMode, com.mediatek.tv.model.DtmbScanRF lastRF) throws android.os.RemoteException;
public int getNextDtmbScanRF_proxy(java.lang.String scanMode, com.mediatek.tv.model.DtmbScanRF currRF, com.mediatek.tv.model.DtmbScanRF nextRF) throws android.os.RemoteException;
public int getPrevDtmbScanRF_proxy(java.lang.String scanMode, com.mediatek.tv.model.DtmbScanRF currRF, com.mediatek.tv.model.DtmbScanRF prevRF) throws android.os.RemoteException;
public int getCurrentDtmbScanRF_proxy(java.lang.String scanMode, int channelId, com.mediatek.tv.model.DtmbScanRF currRF) throws android.os.RemoteException;
/* scan service proxy function end *//* input service proxy function start */
public int inputServiceBind_proxy(int output, int inputId) throws android.os.RemoteException;
public int inputServiceGetRecord_proxy(int index, com.mediatek.tv.model.InputRecord inputRecord) throws android.os.RemoteException;
public int inputServiceSetOutputMute(int output, boolean mute) throws android.os.RemoteException;
public int inputServiceSwap_proxy(int output1, int output2) throws android.os.RemoteException;
public int setScreenOutputRect_proxy(int output, int left, int right, int top, int bottom) throws android.os.RemoteException;
public int setScreenOutputVideoRect_proxy(int output, int left, int right, int top, int bottom) throws android.os.RemoteException;
public int getScreenOutputRect_proxy(int output, com.mediatek.tv.model.InputRegion inputRegion) throws android.os.RemoteException;
public int getScreenOutputVideoRect_proxy(int output, com.mediatek.tv.model.InputRegion inputRegion) throws android.os.RemoteException;
public int inputSourceExchangeData_proxy(int[] inputSourceData) throws android.os.RemoteException;
/* input service proxy function end *//* OSD service proxy function start */
public boolean setOSDColorKey_proxy(boolean enable, int colorkey) throws android.os.RemoteException;
public boolean setOSDOpacity_proxy(int opacity) throws android.os.RemoteException;
/* OSD service proxy function end *//* Event service proxy function start */
public int eventServiceSetCommand_proxy(com.mediatek.tv.model.EventCommand eventCommand) throws android.os.RemoteException;
public int eventServiceGetPFEvents_proxy(com.mediatek.tv.model.DvbChannelInfo channelInfo, java.util.List<com.mediatek.tv.model.EventInfo> events) throws android.os.RemoteException;
public int eventServiceGetScheduleEvents_proxy(com.mediatek.tv.model.DvbChannelInfo channelInfo, long startTime, long endTime, java.util.List<com.mediatek.tv.model.EventInfo> events) throws android.os.RemoteException;
/* Event service proxy function end */
public int getSlotNum_proxy() throws android.os.RemoteException;
public boolean isSlotActive_proxy(int slotId) throws android.os.RemoteException;
public int enterMMI_proxy(int slotId) throws android.os.RemoteException;
public java.lang.String getCamName_proxy(int slotId) throws android.os.RemoteException;
public int[] getCamSystemIDInfo_proxy(int slotId) throws android.os.RemoteException;
public int closeMMI_proxy(int slotId) throws android.os.RemoteException;
public int setMMIClosed_proxy(int slotId) throws android.os.RemoteException;
public int answerMMIMenu_proxy(int slotId, int menuId, char answerItem) throws android.os.RemoteException;
public int answerMMIEnq_proxy(int slotId, int enqId, boolean answer, java.lang.String answerData) throws android.os.RemoteException;
public int askRelease_proxy(int slotId) throws android.os.RemoteException;
public int setCITsPath_proxy(int slotId, boolean b_switch) throws android.os.RemoteException;
public int setCIInputDTVPath_proxy(int slotId, boolean b_switch) throws android.os.RemoteException;
public int getTunedChannel_proxy(int svlId, com.mediatek.tv.model.HostControlTune tune) throws android.os.RemoteException;
public int activateComponent_proxy(java.lang.String comp) throws android.os.RemoteException;
public int inactivateComponent_proxy(java.lang.String comp) throws android.os.RemoteException;
public int updateSysStatus_proxy(java.lang.String statusDesc) throws android.os.RemoteException;
public boolean isTTXAvail_proxy() throws android.os.RemoteException;
public int sendkeyEventtoComp_proxy(int ui4_keycode, int keyevent) throws android.os.RemoteException;
public int lockDigitalTuner_proxy(int frequency) throws android.os.RemoteException;
public int unlockDigitalTuner_proxy(int magicID) throws android.os.RemoteException;
public int getCurrentDTVAudioCodec_proxy() throws android.os.RemoteException;
public int getCurrentDTVVideoCodec_proxy() throws android.os.RemoteException;
public void setMuteState_proxy(boolean b_mute) throws android.os.RemoteException;
public boolean getMuteState_proxy() throws android.os.RemoteException;
}
