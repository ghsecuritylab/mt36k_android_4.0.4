/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: P:\\VoldEXX\\DTV\\GOLDEN_BR\\DTV_X_IDTV0801_002137_5_001\\vm_linux\\android\\dtv-android\\tv_addon\\framework\\dm\\java\\com\\mediatek\\dm\\IDMRemoteService.aidl
 */
package com.mediatek.dm;
public interface IDMRemoteService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.mediatek.dm.IDMRemoteService
{
private static final java.lang.String DESCRIPTOR = "com.mediatek.dm.IDMRemoteService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.mediatek.dm.IDMRemoteService interface,
 * generating a proxy if needed.
 */
public static com.mediatek.dm.IDMRemoteService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.mediatek.dm.IDMRemoteService))) {
return ((com.mediatek.dm.IDMRemoteService)iin);
}
return new com.mediatek.dm.IDMRemoteService.Stub.Proxy(obj);
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
case TRANSACTION_registerDMCallback:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.dm.IDMCallback _arg0;
_arg0 = com.mediatek.dm.IDMCallback.Stub.asInterface(data.readStrongBinder());
this.registerDMCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterDMCallback:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.dm.IDMCallback _arg0;
_arg0 = com.mediatek.dm.IDMCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterDMCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getDeviceCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getDeviceCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getDeviceContent:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.dm.Device _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.dm.Device.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.util.List<com.mediatek.dm.MountPoint> _result = this.getDeviceContent(_arg0);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getDeviceList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.mediatek.dm.Device> _result = this.getDeviceList();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getMountPointCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getMountPointCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getMountPointList:
{
data.enforceInterface(DESCRIPTOR);
java.util.List<com.mediatek.dm.MountPoint> _result = this.getMountPointList();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_getMountPoint:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
com.mediatek.dm.MountPoint _result = this.getMountPoint(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getParentDevice:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.dm.MountPoint _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.dm.MountPoint.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
com.mediatek.dm.Device _result = this.getParentDevice(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_umountDevice:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.umountDevice(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_mountISO:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.mountISO(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_mountISOex:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.mountISOex(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_umountISO:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.umountISO(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_isVirtualDevice:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.isVirtualDevice(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.mediatek.dm.IDMRemoteService
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
/**
     * Registers an IDMRemoteServiceListener for receiving async
     * notifications.
     */
public void registerDMCallback(com.mediatek.dm.IDMCallback listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerDMCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Unregisters an IMountServiceListener
     */
public void unregisterDMCallback(com.mediatek.dm.IDMCallback listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterDMCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	*Get device count
	*/
public int getDeviceCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDeviceCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	*Get ArrayList of mount point specified by dev
	*/
public java.util.List<com.mediatek.dm.MountPoint> getDeviceContent(com.mediatek.dm.Device dev) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<com.mediatek.dm.MountPoint> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((dev!=null)) {
_data.writeInt(1);
dev.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getDeviceContent, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(com.mediatek.dm.MountPoint.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	*Get ArrayList for device
	*/
public java.util.List<com.mediatek.dm.Device> getDeviceList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<com.mediatek.dm.Device> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDeviceList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(com.mediatek.dm.Device.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	*Get mount point count
	*/
public int getMountPointCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMountPointCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	*Get Arraylist of mount point
	*/
public java.util.List<com.mediatek.dm.MountPoint> getMountPointList() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<com.mediatek.dm.MountPoint> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getMountPointList, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(com.mediatek.dm.MountPoint.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	*Get MntPoint Instance specified by path
	*/
public com.mediatek.dm.MountPoint getMountPoint(java.lang.String path) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.mediatek.dm.MountPoint _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(path);
mRemote.transact(Stub.TRANSACTION_getMountPoint, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.mediatek.dm.MountPoint.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	*Get the Device Instance which the mntpoint belong to
	*/
public com.mediatek.dm.Device getParentDevice(com.mediatek.dm.MountPoint mntpoint) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.mediatek.dm.Device _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((mntpoint!=null)) {
_data.writeInt(1);
mntpoint.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getParentDevice, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = com.mediatek.dm.Device.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
	*unmount device by name, it will umount all partition which is belonged to 
    *the device
    */
public void umountDevice(java.lang.String devName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(devName);
mRemote.transact(Stub.TRANSACTION_umountDevice, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	*mount ISO file
	*/
public void mountISO(java.lang.String isoFilePath) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(isoFilePath);
mRemote.transact(Stub.TRANSACTION_mountISO, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/*mount iso file at current directory with a specified a label*/
public void mountISOex(java.lang.String isoFilePath, java.lang.String isoLabel) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(isoFilePath);
_data.writeString(isoLabel);
mRemote.transact(Stub.TRANSACTION_mountISOex, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	*umount ISO file
	*/
public void umountISO(java.lang.String isoMountPath) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(isoMountPath);
mRemote.transact(Stub.TRANSACTION_umountISO, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
	*Judge the mountpath is or not the isomountpath
	*/
public boolean isVirtualDevice(java.lang.String isoMountPath) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(isoMountPath);
mRemote.transact(Stub.TRANSACTION_isVirtualDevice, _data, _reply, 0);
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
static final int TRANSACTION_registerDMCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterDMCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getDeviceCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getDeviceContent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getDeviceList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getMountPointCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_getMountPointList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_getMountPoint = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_getParentDevice = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_umountDevice = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
static final int TRANSACTION_mountISO = (android.os.IBinder.FIRST_CALL_TRANSACTION + 10);
static final int TRANSACTION_mountISOex = (android.os.IBinder.FIRST_CALL_TRANSACTION + 11);
static final int TRANSACTION_umountISO = (android.os.IBinder.FIRST_CALL_TRANSACTION + 12);
static final int TRANSACTION_isVirtualDevice = (android.os.IBinder.FIRST_CALL_TRANSACTION + 13);
}
/**
     * Registers an IDMRemoteServiceListener for receiving async
     * notifications.
     */
public void registerDMCallback(com.mediatek.dm.IDMCallback listener) throws android.os.RemoteException;
/**
     * Unregisters an IMountServiceListener
     */
public void unregisterDMCallback(com.mediatek.dm.IDMCallback listener) throws android.os.RemoteException;
/**
	*Get device count
	*/
public int getDeviceCount() throws android.os.RemoteException;
/**
	*Get ArrayList of mount point specified by dev
	*/
public java.util.List<com.mediatek.dm.MountPoint> getDeviceContent(com.mediatek.dm.Device dev) throws android.os.RemoteException;
/**
	*Get ArrayList for device
	*/
public java.util.List<com.mediatek.dm.Device> getDeviceList() throws android.os.RemoteException;
/**
	*Get mount point count
	*/
public int getMountPointCount() throws android.os.RemoteException;
/**
	*Get Arraylist of mount point
	*/
public java.util.List<com.mediatek.dm.MountPoint> getMountPointList() throws android.os.RemoteException;
/**
	*Get MntPoint Instance specified by path
	*/
public com.mediatek.dm.MountPoint getMountPoint(java.lang.String path) throws android.os.RemoteException;
/**
	*Get the Device Instance which the mntpoint belong to
	*/
public com.mediatek.dm.Device getParentDevice(com.mediatek.dm.MountPoint mntpoint) throws android.os.RemoteException;
/**
	*unmount device by name, it will umount all partition which is belonged to 
    *the device
    */
public void umountDevice(java.lang.String devName) throws android.os.RemoteException;
/**
	*mount ISO file
	*/
public void mountISO(java.lang.String isoFilePath) throws android.os.RemoteException;
/*mount iso file at current directory with a specified a label*/
public void mountISOex(java.lang.String isoFilePath, java.lang.String isoLabel) throws android.os.RemoteException;
/**
	*umount ISO file
	*/
public void umountISO(java.lang.String isoMountPath) throws android.os.RemoteException;
/**
	*Judge the mountpath is or not the isomountpath
	*/
public boolean isVirtualDevice(java.lang.String isoMountPath) throws android.os.RemoteException;
}
