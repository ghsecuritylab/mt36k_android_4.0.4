#!/sbin/sh
if [ "$1" == chd ]; then
/sbin/chmod -R 777 /data/misc/wifi/wpa_supplicant
echo "chmod supplicant 777  succ"

elif [ "$1" == rm ]; then
/sbin/rm -f /data/misc/wifi/wpa_supplicant/*

elif [ "$1" == iptables-w ]; then
echo "1" >> /proc/sys/net/ipv4/ip_forward
/system/bin/iptables -t nat -A POSTROUTING -s 192.168.10.0/24 -o wlan0 -j MASQUERADE
/system/bin/iptables -t nat -A PREROUTING -p udp -i p2p0 --dport 53 -j DNAT --to-destination $2:53
/system/bin/iptables -L -t nat -nv

elif [ "$1" == iptables-e ]; then
echo "1" >> /proc/sys/net/ipv4/ip_forward
/system/bin/iptables -t nat -A POSTROUTING -s 192.168.10.0/24 -o eth0 -j MASQUERADE
/system/bin/iptables -t nat -A PREROUTING -p udp -i p2p0 --dport 53 -j DNAT --to-destination $2:53
/system/bin/iptables -L -t nat -nv

elif [ "$1" == clear ]; then
mkdir -p /lib/modules/$(uname -r)
/system/bin/iptables -F -t nat

elif [ "$1" == iw ]; then
/system/bin/iw dev wlan0 interface add p2p0 type managed
echo "p2p0 gen succ"

elif [ "$1" == ifcfg ]; then
/system/bin/ifconfig wlan0 $2

elif [ "$1" == k2_pre ]; then   
/system/bin/wlanconfig wlan0 create wlandev wifi0 wlanmode sta
/system/bin/iwpriv wlan0 shortgi 1
/system/bin/iwpriv wifi0 ForBiasAuto 1
/system/bin/iwpriv wifi0 AMPDU 1
/system/bin/iwpriv wifi0 AMPDUFrames 32
/system/bin/iwpriv wifi0 AMPDULim 50000
echo "prepare wpa_supplicant"

elif [ "$1" == ra_pre ]; then   
/system/bin/ifconfig wlan0 up
fi 
