/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/cuiyan/work/workspace/TclPortalService/src/com/tcl/VideoOnlineService/IVideoOnlineService.aidl
 */
package com.tcl.VideoOnlineService;
public interface IVideoOnlineService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.tcl.VideoOnlineService.IVideoOnlineService
{
private static final java.lang.String DESCRIPTOR = "com.tcl.VideoOnlineService.IVideoOnlineService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.tcl.VideoOnlineService.IVideoOnlineService interface,
 * generating a proxy if needed.
 */
public static com.tcl.VideoOnlineService.IVideoOnlineService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.tcl.VideoOnlineService.IVideoOnlineService))) {
return ((com.tcl.VideoOnlineService.IVideoOnlineService)iin);
}
return new com.tcl.VideoOnlineService.IVideoOnlineService.Stub.Proxy(obj);
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
case TRANSACTION_GetClassInfo:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
ClassInfoProduct _result = this.GetClassInfo(_arg0, _arg1);
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
case TRANSACTION_GetCategoryList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
CategoryListProduct _result = this.GetCategoryList(_arg0, _arg1, _arg2, _arg3);
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
case TRANSACTION_GetSearchByPageList:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
CategoryListProduct _result = this.GetSearchByPageList(_arg0, _arg1, _arg2);
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
case TRANSACTION_GetDetail:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
DetailProduct _result = this.GetDetail(_arg0, _arg1, _arg2, _arg3);
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
case TRANSACTION_GetSmallPic:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
SmallPicProduct _result = this.GetSmallPic(_arg0, _arg1);
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
case TRANSACTION_GetVideoType:
{
data.enforceInterface(DESCRIPTOR);
VideoTypeProduct _result = this.GetVideoType();
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
case TRANSACTION_GetMovieById:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
DetailProduct _result = this.GetMovieById(_arg0);
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
case TRANSACTION_SetPlatform:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.SetPlatform(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_SetNetworkTimeout:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
int _arg1;
_arg1 = data.readInt();
this.SetNetworkTimeout(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_CloseConnection:
{
data.enforceInterface(DESCRIPTOR);
this.CloseConnection();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.tcl.VideoOnlineService.IVideoOnlineService
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
public ClassInfoProduct GetClassInfo(java.lang.String class_id, java.lang.String class_level) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
ClassInfoProduct _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(class_id);
_data.writeString(class_level);
mRemote.transact(Stub.TRANSACTION_GetClassInfo, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = ClassInfoProduct.CREATOR.createFromParcel(_reply);
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
public CategoryListProduct GetCategoryList(java.lang.String main_id, java.lang.String sub_id, java.lang.String page, java.lang.String count) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
CategoryListProduct _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(main_id);
_data.writeString(sub_id);
_data.writeString(page);
_data.writeString(count);
mRemote.transact(Stub.TRANSACTION_GetCategoryList, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = CategoryListProduct.CREATOR.createFromParcel(_reply);
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
public CategoryListProduct GetSearchByPageList(java.lang.String keyword, java.lang.String page, java.lang.String count) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
CategoryListProduct _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(keyword);
_data.writeString(page);
_data.writeString(count);
mRemote.transact(Stub.TRANSACTION_GetSearchByPageList, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = CategoryListProduct.CREATOR.createFromParcel(_reply);
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
public DetailProduct GetDetail(java.lang.String level, java.lang.String id, java.lang.String page, java.lang.String name) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
DetailProduct _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(level);
_data.writeString(id);
_data.writeString(page);
_data.writeString(name);
mRemote.transact(Stub.TRANSACTION_GetDetail, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = DetailProduct.CREATOR.createFromParcel(_reply);
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
public SmallPicProduct GetSmallPic(java.lang.String id, java.lang.String level) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
SmallPicProduct _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(id);
_data.writeString(level);
mRemote.transact(Stub.TRANSACTION_GetSmallPic, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = SmallPicProduct.CREATOR.createFromParcel(_reply);
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
public VideoTypeProduct GetVideoType() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
VideoTypeProduct _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_GetVideoType, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = VideoTypeProduct.CREATOR.createFromParcel(_reply);
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
public DetailProduct GetMovieById(java.lang.String id) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
DetailProduct _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(id);
mRemote.transact(Stub.TRANSACTION_GetMovieById, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = DetailProduct.CREATOR.createFromParcel(_reply);
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
public void SetPlatform(boolean flag) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((flag)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_SetPlatform, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void SetNetworkTimeout(int timeout, int requeattime) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(timeout);
_data.writeInt(requeattime);
mRemote.transact(Stub.TRANSACTION_SetNetworkTimeout, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void CloseConnection() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_CloseConnection, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_GetClassInfo = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_GetCategoryList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_GetSearchByPageList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_GetDetail = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_GetSmallPic = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_GetVideoType = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_GetMovieById = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_SetPlatform = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_SetNetworkTimeout = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_CloseConnection = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
}
public ClassInfoProduct GetClassInfo(java.lang.String class_id, java.lang.String class_level) throws android.os.RemoteException;
public CategoryListProduct GetCategoryList(java.lang.String main_id, java.lang.String sub_id, java.lang.String page, java.lang.String count) throws android.os.RemoteException;
public CategoryListProduct GetSearchByPageList(java.lang.String keyword, java.lang.String page, java.lang.String count) throws android.os.RemoteException;
public DetailProduct GetDetail(java.lang.String level, java.lang.String id, java.lang.String page, java.lang.String name) throws android.os.RemoteException;
public SmallPicProduct GetSmallPic(java.lang.String id, java.lang.String level) throws android.os.RemoteException;
public VideoTypeProduct GetVideoType() throws android.os.RemoteException;
public DetailProduct GetMovieById(java.lang.String id) throws android.os.RemoteException;
public void SetPlatform(boolean flag) throws android.os.RemoteException;
public void SetNetworkTimeout(int timeout, int requeattime) throws android.os.RemoteException;
public void CloseConnection() throws android.os.RemoteException;
}
