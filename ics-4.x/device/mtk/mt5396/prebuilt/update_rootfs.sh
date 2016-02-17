#!/sbin/sh
if [ $# -ne 1 ]
then
    PART=boot
else
    PART=$1
fi

OUTPATH=/data/out
ROOTFS=$OUTPATH/rootfs
IMG_FILE=$PART.img
NEWIMG_FILE=new-$PART.img
IMG_KERNEL=$IMG_FILE-kernel
NEWIMG_RAMDISK=$NEWIMG_FILE-ramdisk.gz

MTD_NO=`grep $PART /proc/mtd | awk -F: '{print $1}'`

if [ -z "$MTD_NO" ]
then
	echo "Partition $PART is not found in /proc/mtd"
	exit 1
fi

cd $OUTPATH

echo "mkbootfs $ROOTFS | lzop -9 > $OUTPATH/$NEWIMG_RAMDISK"
mkbootfs $ROOTFS | lzop -9 > $OUTPATH/$NEWIMG_RAMDISK
echo "mkbootimg  --kernel $OUTPATH/$IMG_KERNEL  --ramdisk $OUTPATH/$NEWIMG_RAMDISK --output $OUTPATH/$NEWIMG_FILE"
mkbootimg  --kernel $IMG_KERNEL  --ramdisk $OUTPATH/$NEWIMG_RAMDISK --output $OUTPATH/$NEWIMG_FILE

echo "flash_image $PART $OUTPATH/$NEWIMG_FILE"
flash_image $PART $OUTPATH/$NEWIMG_FILE
