#!/system/bin/sh

echo "start install diandu......"

if [ ! -e /data/OIDAppTable/ ] ; then
  mkdir -p /data/OIDAppTable/
  sync
fi

if [ ! -e /data/OIDAppTable/apManage_rw.xml ] ; then
  cp -a /system/etc/apManage_rw.xml /data/OIDAppTable/apManage_rw.xml
  sync
fi

if [ ! -e /data/OIDAppTable/bookid.xml ] ; then
  cp -a /system/etc/bookid.xml /data/OIDAppTable/bookid.xml
  sync
fi

if [ ! -e /data/OIDAppTable/channelSwitchXml.xml ] ; then
  cp -a /system/etc/channelSwitchXml.xml /data/OIDAppTable/channelSwitchXml.xml
  sync
fi

chmod 777 /data/OIDAppTable/bookid.xml
chmod 777 /data/OIDAppTable/apManage_rw.xml
chmod 777 /system/bin/chompjimei_tv
chmod 777 /data/OIDAppTable/apManage_update.xml
sync

echo "end install diandu......"
