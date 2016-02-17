#!/sbin/sh

mknod /dev/mali c 253 0
mknod /dev/ump c 254 0

mknod /dev/eeprom_4 c 226 0
mknod /dev/eeprom_3 c 227 0
mknod /dev/eeprom_0 c 228 0

mknod /dev/rmmgr  c 229 0
mknod /dev/mtal   c 241 0
mknod /dev/feeder c 247 0
mknod /dev/jpg    c 251 0
mknod /dev/fbm_png  c 251 112
mknod /dev/fbm_jpg_vdp  c 251 111
mknod /dev/cb     c 241 8
mknod /dev/cli    c 241 4
mknod /dev/dsp c 14 3
mknod /dev/mixer c 14 0
mknod /dev/kmem2  c 244 0
mknod /dev/vomx   c 238 0
mknod /dev/gpio    c 241 12
mknod /dev/aomx   c 237 0
mknod /dev/b2r    c 243 0

mknod /dev/dsp1   c 14 19  # USB audio device

chmod 0666 /dev/mali
chmod 0666 /dev/ump

chmod 666 /dev/eeprom_4
chmod 666 /dev/eeprom_3
chmod 666 /dev/eeprom_0

chmod 666 /dev/rmmgr
chmod 666 /dev/mtal
chmod 666 /dev/feeder
chmod 666 /dev/jpg
chmod 666 /dev/cb
chmod 666 /dev/cli
chmod 666 /dev/kmem2
chmod 666 /dev/vomx
chmod 666 /dev/gpio
chmod 666 /dev/aomx
chmod 666 /dev/b2r

chmod 666 /dev/dsp1

