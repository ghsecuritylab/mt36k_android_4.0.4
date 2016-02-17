# ./lib/Makefile.inc
# Using the backslash as line continuation character might be problematic
# with some make flavours, as Watcom's wmake showed us already. If we
# ever want to change this in a portable manner then we should consider
# this idea (posted to the libcurl list by Adam Kellas):
# CSRC1 = file1.c file2.c file3.c
# CSRC2 = file4.c file5.c file6.c
# CSOURCES = $(CSRC1) $(CSRC2)

CSOURCES = mms.c mmsh.c mmsx.c uri.c

HHEADERS = asfheader.h bswap.h mms.h mms_config.h mms-common.h mms-common-funcs.h \
           mmsh.h mmsio.h mmsx.h uri.h

