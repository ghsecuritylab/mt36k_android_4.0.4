# ./lib/Makefile.inc
# Using the backslash as line continuation character might be problematic
# with some make flavours, as Watcom's wmake showed us already. If we
# ever want to change this in a portable manner then we should consider
# this idea (posted to the libcurl list by Adam Kellas):
# CSRC1 = file1.c file2.c file3.c
# CSRC2 = file4.c file5.c file6.c
# CSOURCES = $(CSRC1) $(CSRC2)
CURLX_ONES = $(top_srcdir)/lib/strtoofft.c \
	$(top_srcdir)/lib/strdup.c \
	$(top_srcdir)/lib/rawstr.c \
	$(top_srcdir)/lib/nonblock.c
	
CSOURCES = 

HHEADERS = 

