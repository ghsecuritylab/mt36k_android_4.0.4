#!/sbin/sh

ec()
{
	/system/bin/busybox echo $* > /dev/console;
}

if [ -f /data/zygote.ss ]; then
	ec "*********************load saved zygote*******************"
	/system/bin/cr_restart --no-restore-pid -f /data/zygote.ss
	rm /data/zygote.ss*
	sync
else
	ec "optimised by blcr"
	ec "*********************srart a new zygote******************"
	/system/bin/app_process $*
fi
