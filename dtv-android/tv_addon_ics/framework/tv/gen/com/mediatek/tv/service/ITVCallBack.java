/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: Z:\\p4_android2\\DTV\\GOLDEN_BR\\DTV_X_IDTV0801_002137_5_001\\vm_linux\\android\\dtv-android\\tv_addon\\framework\\tv\\java\\com\\mediatek\\tv\\service\\ITVCallBack.aidl
 */
package com.mediatek.tv.service;
public interface ITVCallBack extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.mediatek.tv.service.ITVCallBack
{
private static final java.lang.String DESCRIPTOR = "com.mediatek.tv.service.ITVCallBack";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.mediatek.tv.service.ITVCallBack interface,
 * generating a proxy if needed.
 */
public static com.mediatek.tv.service.ITVCallBack asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.mediatek.tv.service.ITVCallBack))) {
return ((com.mediatek.tv.service.ITVCallBack)iin);
}
return new com.mediatek.tv.service.ITVCallBack.Stub.Proxy(obj);
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
case TRANSACTION_notifyChannelUpdated:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
this.notifyChannelUpdated(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_notifyScanProgress:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _result = this.notifyScanProgress(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_notifyScanFrequence:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.notifyScanFrequence(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_notifyScanCompleted:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.notifyScanCompleted();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_notifyScanCanceled:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.notifyScanCanceled();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_notifyScanError:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _result = this.notifyScanError(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_notifyScanUserOperation:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
int _result = this.notifyScanUserOperation(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_onUARTSerialListener:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
byte[] _arg3;
_arg3 = data.createByteArray();
this.onUARTSerialListener(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_onOperationDone:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
boolean _arg1;
_arg1 = (0!=data.readInt());
this.onOperationDone(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onSourceDetected:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onSourceDetected(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onOutputSignalStatus:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.onOutputSignalStatus(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_camStatusUpdated:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte _arg1;
_arg1 = data.readByte();
int _result = this.camStatusUpdated(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_camMMIMenuReceived:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
byte _arg2;
_arg2 = data.readByte();
java.lang.String _arg3;
_arg3 = data.readString();
java.lang.String _arg4;
_arg4 = data.readString();
java.lang.String _arg5;
_arg5 = data.readString();
java.lang.String[] _arg6;
_arg6 = data.createStringArray();
int _result = this.camMMIMenuReceived(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_camMMIEnqReceived:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.mediatek.tv.model.MMIEnq _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.MMIEnq.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.camMMIEnqReceived(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_camMMIClosed:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte _arg1;
_arg1 = data.readByte();
int _result = this.camMMIClosed(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_camHostControlTune:
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
int _result = this.camHostControlTune(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_camHostControlReplace:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
com.mediatek.tv.model.HostControlReplace _arg1;
if ((0!=data.readInt())) {
_arg1 = com.mediatek.tv.model.HostControlReplace.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
int _result = this.camHostControlReplace(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_camHostControlClearReplace:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte _arg1;
_arg1 = data.readByte();
int _result = this.camHostControlClearReplace(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_camSystemIDStatusUpdated:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
byte _arg1;
_arg1 = data.readByte();
int _result = this.camSystemIDStatusUpdated(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_camSystemIDInfoUpdated:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int[] _arg1;
_arg1 = data.createIntArray();
int _result = this.camSystemIDInfoUpdated(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_eventServiceNotifyUpdate:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
this.eventServiceNotifyUpdate(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_notifyDT:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
this.notifyDT(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_notifyDbgLevel:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.notifyDbgLevel(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_notifyCompInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.notifyCompInfo(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.mediatek.tv.service.ITVCallBack
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
/*Channel serivce callback start*/
public void notifyChannelUpdated(int condition, int reason, int data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(condition);
_data.writeInt(reason);
_data.writeInt(data);
mRemote.transact(Stub.TRANSACTION_notifyChannelUpdated, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*Channel serivce callback start*//*Scan callback start*/
public int notifyScanProgress(int progress, int channels) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(progress);
_data.writeInt(channels);
mRemote.transact(Stub.TRANSACTION_notifyScanProgress, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int notifyScanFrequence(int frequence) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(frequence);
mRemote.transact(Stub.TRANSACTION_notifyScanFrequence, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int notifyScanCompleted() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notifyScanCompleted, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int notifyScanCanceled() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_notifyScanCanceled, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int notifyScanError(int error) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(error);
mRemote.transact(Stub.TRANSACTION_notifyScanError, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int notifyScanUserOperation(int currentFreq, int foundChNum, int finishData) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(currentFreq);
_data.writeInt(foundChNum);
_data.writeInt(finishData);
mRemote.transact(Stub.TRANSACTION_notifyScanUserOperation, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*Scan callback end*//*Brdcst service start*//*void nfySvctxMsgCB(String key,int data);*//*Brdcst service end*//*configure service start*/
public void onUARTSerialListener(int uartSerialID, int ioNotifyCond, int eventCode, byte[] data) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(uartSerialID);
_data.writeInt(ioNotifyCond);
_data.writeInt(eventCode);
_data.writeByteArray(data);
mRemote.transact(Stub.TRANSACTION_onUARTSerialListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*configure service end*//*input service start*/
public void onOperationDone(int output, boolean isSignalLoss) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(output);
_data.writeInt(((isSignalLoss)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_onOperationDone, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onSourceDetected(int inputId, int signalStatus) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(inputId);
_data.writeInt(signalStatus);
mRemote.transact(Stub.TRANSACTION_onSourceDetected, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void onOutputSignalStatus(int output, int signalStatus) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(output);
_data.writeInt(signalStatus);
mRemote.transact(Stub.TRANSACTION_onOutputSignalStatus, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*input service end*//*ci service start*/
public int camStatusUpdated(int slotId, byte cam_status) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
_data.writeByte(cam_status);
mRemote.transact(Stub.TRANSACTION_camStatusUpdated, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int camMMIMenuReceived(int slotId, int menuId, byte choiceNum, java.lang.String title, java.lang.String subTitle, java.lang.String bottom, java.lang.String[] itemlist) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
_data.writeInt(menuId);
_data.writeByte(choiceNum);
_data.writeString(title);
_data.writeString(subTitle);
_data.writeString(bottom);
_data.writeStringArray(itemlist);
mRemote.transact(Stub.TRANSACTION_camMMIMenuReceived, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int camMMIEnqReceived(int slotId, com.mediatek.tv.model.MMIEnq enq) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
if ((enq!=null)) {
_data.writeInt(1);
enq.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_camMMIEnqReceived, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int camMMIClosed(int slotId, byte mmi_close_delay) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
_data.writeByte(mmi_close_delay);
mRemote.transact(Stub.TRANSACTION_camMMIClosed, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int camHostControlTune(int slotId, com.mediatek.tv.model.HostControlTune tune_request) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
if ((tune_request!=null)) {
_data.writeInt(1);
tune_request.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_camHostControlTune, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int camHostControlReplace(int slotId, com.mediatek.tv.model.HostControlReplace replace_request) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
if ((replace_request!=null)) {
_data.writeInt(1);
replace_request.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_camHostControlReplace, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int camHostControlClearReplace(int slotId, byte refId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
_data.writeByte(refId);
mRemote.transact(Stub.TRANSACTION_camHostControlClearReplace, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int camSystemIDStatusUpdated(int slotId, byte sysIdStatus) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
_data.writeByte(sysIdStatus);
mRemote.transact(Stub.TRANSACTION_camSystemIDStatusUpdated, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int camSystemIDInfoUpdated(int slotId, int[] arrInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(slotId);
_data.writeIntArray(arrInfo);
mRemote.transact(Stub.TRANSACTION_camSystemIDInfoUpdated, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/*ci service end*//*Event service notify start*/
public void eventServiceNotifyUpdate(int reason, int svlid, int channelId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(reason);
_data.writeInt(svlid);
_data.writeInt(channelId);
mRemote.transact(Stub.TRANSACTION_eventServiceNotifyUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*Event service notify end*/
public void notifyDT(int h_handle, int cond, int delta_time) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(h_handle);
_data.writeInt(cond);
_data.writeInt(delta_time);
mRemote.transact(Stub.TRANSACTION_notifyDT, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void notifyDbgLevel(int debugLevel) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(debugLevel);
mRemote.transact(Stub.TRANSACTION_notifyDbgLevel, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/* for comp service start */
public void notifyCompInfo(java.lang.String NotifyInfo) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(NotifyInfo);
mRemote.transact(Stub.TRANSACTION_notifyCompInfo, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_notifyChannelUpdated = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_notifyScanProgress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_notifyScanFrequence = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_notifyScanCompleted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_notifyScanCanceled = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_notifyScanError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_notifyScanUserOperation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_onUARTSerialListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_onOperationDone = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_onSourceDetected = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_onOutputSignalStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_camStatusUpdated = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_camMMIMenuReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_camMMIEnqReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
static final int TRANSACTION_camMMIClosed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 14);
static final int TRANSACTION_camHostControlTune = (android.os.IBinder.FIRST_CALL_TRANSACTION + 15);
static final int TRANSACTION_camHostControlReplace = (android.os.IBinder.FIRST_CALL_TRANSACTION + 16);
static final int TRANSACTION_camHostControlClearReplace = (android.os.IBinder.FIRST_CALL_TRANSACTION + 17);
static final int TRANSACTION_camSystemIDStatusUpdated = (android.os.IBinder.FIRST_CALL_TRANSACTION + 18);
static final int TRANSACTION_camSystemIDInfoUpdated = (android.os.IBinder.FIRST_CALL_TRANSACTION + 19);
static final int TRANSACTION_eventServiceNotifyUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 20);
static final int TRANSACTION_notifyDT = (android.os.IBinder.FIRST_CALL_TRANSACTION + 21);
static final int TRANSACTION_notifyDbgLevel = (android.os.IBinder.FIRST_CALL_TRANSACTION + 22);
static final int TRANSACTION_notifyCompInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 23);
}
/*Channel serivce callback start*/
public void notifyChannelUpdated(int condition, int reason, int data) throws android.os.RemoteException;
/*Channel serivce callback start*//*Scan callback start*/
public int notifyScanProgress(int progress, int channels) throws android.os.RemoteException;
public int notifyScanFrequence(int frequence) throws android.os.RemoteException;
public int notifyScanCompleted() throws android.os.RemoteException;
public int notifyScanCanceled() throws android.os.RemoteException;
public int notifyScanError(int error) throws android.os.RemoteException;
public int notifyScanUserOperation(int currentFreq, int foundChNum, int finishData) throws android.os.RemoteException;
/*Scan callback end*//*Brdcst service start*//*void nfySvctxMsgCB(String key,int data);*//*Brdcst service end*//*configure service start*/
public void onUARTSerialListener(int uartSerialID, int ioNotifyCond, int eventCode, byte[] data) throws android.os.RemoteException;
/*configure service end*//*input service start*/
public void onOperationDone(int output, boolean isSignalLoss) throws android.os.RemoteException;
public void onSourceDetected(int inputId, int signalStatus) throws android.os.RemoteException;
public void onOutputSignalStatus(int output, int signalStatus) throws android.os.RemoteException;
/*input service end*//*ci service start*/
public int camStatusUpdated(int slotId, byte cam_status) throws android.os.RemoteException;
public int camMMIMenuReceived(int slotId, int menuId, byte choiceNum, java.lang.String title, java.lang.String subTitle, java.lang.String bottom, java.lang.String[] itemlist) throws android.os.RemoteException;
public int camMMIEnqReceived(int slotId, com.mediatek.tv.model.MMIEnq enq) throws android.os.RemoteException;
public int camMMIClosed(int slotId, byte mmi_close_delay) throws android.os.RemoteException;
public int camHostControlTune(int slotId, com.mediatek.tv.model.HostControlTune tune_request) throws android.os.RemoteException;
public int camHostControlReplace(int slotId, com.mediatek.tv.model.HostControlReplace replace_request) throws android.os.RemoteException;
public int camHostControlClearReplace(int slotId, byte refId) throws android.os.RemoteException;
public int camSystemIDStatusUpdated(int slotId, byte sysIdStatus) throws android.os.RemoteException;
public int camSystemIDInfoUpdated(int slotId, int[] arrInfo) throws android.os.RemoteException;
/*ci service end*//*Event service notify start*/
public void eventServiceNotifyUpdate(int reason, int svlid, int channelId) throws android.os.RemoteException;
/*Event service notify end*/
public void notifyDT(int h_handle, int cond, int delta_time) throws android.os.RemoteException;
public void notifyDbgLevel(int debugLevel) throws android.os.RemoteException;
/* for comp service start */
public void notifyCompInfo(java.lang.String NotifyInfo) throws android.os.RemoteException;
}
