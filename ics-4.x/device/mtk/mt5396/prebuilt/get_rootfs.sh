#!/sbin/sh

if [ $# -ne 1 ]
then
	PART=boot
else
	PART=$1
fi

OUTPATH=/data/out
IMGFILE=boot.img
IMG_KERNEL=$IMGFILE-kernel
IMG_RAMDISK=$IMGFILE-ramdisk.gz

rm -rf $OUTPATH
mkdir -p $OUTPATH
echo "grep -w $PART /proc/mtd | awk -F: '{print $1}'"
MTD_NO=`grep -w $PART /proc/mtd | awk -F: '{print $1}'`

if [ -z "$MTD_NO" ]
then
    echo "Partition $PART is not found in /proc/mtd"
    exit 1
fi


cd $OUTPATH
echo "cat /dev/mtd/$MTD_NO > $OUTPATH/$IMGFILE"
cat /dev/mtd/$MTD_NO > $OUTPATH/$IMGFILE
echo "split_bootimg $OUTPATH/$IMGFILE"
split_bootimg $OUTPATH/$IMGFILE
mkdir rootfs
cd rootfs
echo "lzop -dc $OUTPATH/$IMG_RAMDISK | cpio -i"
lzop -dc $OUTPATH/$IMG_RAMDISK | cpio -i
cd ..
echo "Extracting rootfs into "
echo "  ===>  $OUTPATH/rootfs"

echo "Update rootfs by "
echo "  ===> update_rootfs.sh"
