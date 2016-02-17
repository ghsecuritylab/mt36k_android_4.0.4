#!/sbin/sh
#
# Running at boot begining
# Prepare for booting

source /etc/boot.conf

echo ""
echo "==> pre-init start"
cat /proc/uptime
echo ""

if $ENABLE_KERNEL_LOG; then
    echo 7 > /proc/sys/kernel/printk
else
    echo 0 > /proc/sys/kernel/printk
fi

if $ENABLE_LOGCAT; then
    logcat -v time &
fi

# clean memory
rm /*.ko

# Change Mac address
if $ENABLE_DYNAMIC_MAC; then
/sbin/sh /system/etc/script/set_eth0_mac.sh
fi

# samba data
mkdir -p /data/misc/smb

