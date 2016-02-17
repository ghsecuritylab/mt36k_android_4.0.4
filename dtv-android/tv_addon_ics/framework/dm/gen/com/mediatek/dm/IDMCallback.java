/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: P:\\VoldEXX\\DTV\\GOLDEN_BR\\DTV_X_IDTV0801_002137_5_001\\vm_linux\\android\\dtv-android\\tv_addon\\framework\\dm\\java\\com\\mediatek\\dm\\IDMCallback.aidl
 */
package com.mediatek.dm;
public interface IDMCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.mediatek.dm.IDMCallback
{
private static final java.lang.String DESCRIPTOR = "com.mediatek.dm.IDMCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.mediatek.dm.IDMCallback interface,
 * generating a proxy if needed.
 */
public static com.mediatek.dm.IDMCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.mediatek.dm.IDMCallback))) {
return ((com.mediatek.dm.IDMCallback)iin);
}
return new com.mediatek.dm.IDMCallback.Stub.Proxy(obj);
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
case TRANSACTION_notifyDeviceEvent:
{
data.enforceInterface(DESCRIPTOR);
com.mediatek.dm.DeviceManagerEvent _arg0;
if ((0!=data.readInt())) {
_arg0 = com.mediatek.dm.DeviceManagerEvent.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.notifyDeviceEvent(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.mediatek.dm.IDMCallback
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
public void notifyDeviceEvent(com.mediatek.dm.DeviceManagerEvent event) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((event!=null)) {
_data.writeInt(1);
event.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_notifyDeviceEvent, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_notifyDeviceEvent = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void notifyDeviceEvent(com.mediatek.dm.DeviceManagerEvent event) throws android.os.RemoteException;
}
