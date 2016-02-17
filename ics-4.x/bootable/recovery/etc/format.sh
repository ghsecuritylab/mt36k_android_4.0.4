#!/sbin/sh
echo $* 
ubidetach /dev/ubi_ctrl -m $1
echo "now ubiformat..."
ubiformat /dev/mtd/mtd$1
echo "now ubiattach..."
ubiattach /dev/ubi_ctrl -m $1 -d $2
echo "now, ubimkvol..."
ubimkvol /dev/ubi$2 -N $3 -m
echo "format over"
sync
