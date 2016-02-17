#!/sbin/sh

source /etc/boot.conf
/sbin/sh /etc/script/mknode.sh

mkdir -p /tmp
# Factory mode via ttyMT3
stty -F /dev/ttyMT3 115200

if $ENABLE_MW_CLI; then
    /system/bin/dtv_svc -input_fifo &
else
    /system/bin/dtv_svc -input_fifo > /dev/null &
fi

/sbin/sh /etc/script/setup_zram.sh
echo 20480 > /proc/sys/vm/min_free_kbytes
