/* pppoe.c - pppd plugin to implement PPPoE protocol.
 *
 * Copyright 2000 Michal Ostrowski <mostrows@styx.uwaterloo.ca>,
 *		  Jamal Hadi Salim <hadi@cyberus.ca>
 * Borrows heavily from the PPPoATM plugin by Mitchell Blank Jr.,
 * which is based in part on work from Jens Axboe and Paul Mackerras.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version
 *  2 of the License, or (at your option) any later version.
 */

#include <string.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <errno.h>
#include <sys/stat.h>
//#if _linux_
#include <linux/ppp_defs.h>
#include <linux/if_pppox.h>
#include <linux/if_ppp.h>
//#else
//#error this module meant for use with linux only at this time
//#endif
//#include <linux/if.h>
#include "pppoecd/pppoe.h"
//#include "pppoecd/pppd.h"
#include <fsm.h>
#include <lcp.h>
#include <ipcp.h>
#include <ccp.h>
#include <pathnames.h>

const char pppd_version[] = VERSION;
#define IFNAMSIZ 16
#define _PATH_ETHOPT         _ROOT_PATH "/etc/ppp/options."

#define PPPOE_MTU	1492
extern int kill_link;

bool	pppoe_server=0;
char	*pppoe_srv_name=NULL;
char	*pppoe_ac_name=NULL;
char    *hostuniq = NULL;
int     retries = 0;

#define SESSION_FILE "/data/data/ppp1_sid"

int setdevname_pppoe(const char *cp);

struct session *ses = NULL;

/**
	add by yigc@tcl.com
	call it before do connection
*/
static void pre_connect_pppoe_ses(void)
{
	//add by yigc@tcl.com
	unsigned int mac[6];
	unsigned int sID;
	FILE* sfile;
	int n;
	
	//disconnect before. added by yigc@tcl.com
	if (access (SESSION_FILE, F_OK) != 0) {
		return;
	}
	
    client_init_ses(ses,devnam);

    strlcpy(ppp_devnam, devnam, sizeof(ppp_devnam));
	
	sfile = fopen(SESSION_FILE, "r");
	n = fscanf(sfile, "%u:%2x:%2x:%2x:%2x:%2x:%2x",
	       &sID, &mac[0], &mac[1], &mac[2], &mac[3], &mac[4], &mac[5]);
	fclose(sfile);
	
	if (n < 7) {
		poe_info(ses, "Read PPPoE Session error!");
		return;
	}

    struct pppoe_packet padt;

	memset(&padt, 0, sizeof(struct pppoe_packet));
		
	ses->remote.sll_addr[0] = mac[0];
	ses->remote.sll_addr[1] = mac[1];
	ses->remote.sll_addr[2] = mac[2];
	ses->remote.sll_addr[3] = mac[3];
	ses->remote.sll_addr[4] = mac[4];
	ses->remote.sll_addr[5] = mac[5];
	memcpy(&padt.addr, &ses->remote, sizeof(struct sockaddr_ll));

	padt.hdr = (struct pppoe_hdr*) ses->curr_pkt.buf;
	padt.hdr->ver  = 1;
	padt.hdr->type = 1;
	padt.hdr->code = PADT_CODE;
	padt.hdr->sid  = htons(sID);


    send_disc(ses, &padt);
    ses->sp.sa_addr.pppoe.sid = 0;
	
    connect(ses->fd, (struct sockaddr*)&ses->sp,
	sizeof(struct sockaddr_pppox));
	usleep(2000000);
/**	
    int ret;
    warn("Doing pre-disconnect");
    session_disconnect(ses);
    ses->sp.sa_addr.pppoe.sid = 0;
    ret = connect(ses->fd, (struct sockaddr*)&ses->sp,
	    sizeof(struct sockaddr_pppox));
*/
}

static void post_connect_pppoe_ses(void)
{
	FILE* sfile;

	/**
	    Save session id here. added by yigc@tcl.com
	*/
	sfile = fopen(SESSION_FILE, "w");
	if (sfile) {
		poe_info(ses, "Save PPPoE Session ID & MAC!");
		fprintf(sfile,"%u:%02x:%02x:%02x:%02x:%02x:%02x\n",
			   ntohs(ses->sp.sa_addr.pppoe.sid),
			   ses->sp.sa_addr.pppoe.remote[0],
			   ses->sp.sa_addr.pppoe.remote[1],
			   ses->sp.sa_addr.pppoe.remote[2],
			   ses->sp.sa_addr.pppoe.remote[3],
			   ses->sp.sa_addr.pppoe.remote[4],
			   ses->sp.sa_addr.pppoe.remote[5]);
		fclose(sfile);
	}

}

static void post_disconnect_pppoe_ses(void)
{
	if (access (SESSION_FILE, F_OK) == 0) {
		poe_info(ses, "Remove PPPoE Session file!");
		remove(SESSION_FILE);
	}
}


static int connect_pppoe_ses(void)
{
    int err=-1;

	pre_connect_pppoe_ses();
	
    client_init_ses(ses,devnam);

    strlcpy(ppp_devnam, devnam, sizeof(ppp_devnam));

    err= session_connect ( ses );

    if(err < 0){
	poe_fatal(ses,"Failed to negotiate PPPoE connection: %d %m",errno,errno);
    }


    poe_info(ses,"Connecting PPPoE socket: %E %04x %s %p",
	     ses->sp.sa_addr.pppoe.remote,
	     ses->sp.sa_addr.pppoe.sid,
	     ses->sp.sa_addr.pppoe.dev,ses);

    err = connect(ses->fd, (struct sockaddr*)&ses->sp,
		  sizeof(struct sockaddr_pppox));


    if( err < 0 ){
	poe_fatal(ses,"Failed to connect PPPoE socket: %d %m",errno,errno);
	return err;
    }
    /* Once the logging is fixed, print a message here indicating
       connection parameters */

	post_connect_pppoe_ses();

    return ses->fd;
}

static void disconnect_pppoe_ses(void)
{
    int ret;
    warn("Doing disconnect");
    session_disconnect(ses);
    ses->sp.sa_addr.pppoe.sid = 0;
    ret = connect(ses->fd, (struct sockaddr*)&ses->sp,
	    sizeof(struct sockaddr_pppox));

	post_disconnect_pppoe_ses();
}

static void init_device_pppoe(void)
{
    //info("init_device_pppoe start /cd===");//add by HQS
    struct filter *filt;
    ses=(void *)malloc(sizeof(struct session));
    if(!ses){
	fatal("No memory for new PPPoE session");
    }
    memset(ses,0,sizeof(struct session));

    if ((ses->filt=malloc(sizeof(struct filter))) == NULL) {
	poe_error (ses,"failed to malloc for Filter ");
	poe_die (-1);
    }

    filt=ses->filt;  /* makes the code more readable */
    memset(filt,0,sizeof(struct filter));

    if (pppoe_ac_name !=NULL) {
	if (strlen (pppoe_ac_name) > 255) {
	    poe_error (ses," AC name too long (maximum allowed 256 chars)");
	    poe_die(-1);
	}
	ses->filt->ntag = make_filter_tag(PTT_AC_NAME,
					  strlen(pppoe_ac_name),
					  pppoe_ac_name);

	if ( ses->filt->ntag== NULL) {
	    poe_error (ses,"failed to malloc for AC name");
	    poe_die(-1);
	}

    }


    if (pppoe_srv_name !=NULL) {
	if (strlen (pppoe_srv_name) > 255) {
	    poe_error (ses," Service name too long\n  (maximum allowed 256 chars)");
	    poe_die(-1);
	}
	ses->filt->stag = make_filter_tag(PTT_SRV_NAME,
					  strlen(pppoe_srv_name),
					  pppoe_srv_name);
	if ( ses->filt->stag == NULL) {
	    poe_error (ses,"failed to malloc for service name");
	    poe_die(-1);
	}
    }

    if (hostuniq) {
	ses->filt->htag = make_filter_tag(PTT_HOST_UNIQ,
					  strlen(hostuniq),
					  hostuniq);
	if ( ses->filt->htag == NULL) {
	    poe_error (ses,"failed to malloc for Uniq Host Id ");
	    poe_die(-1);
	}
    }

    if (retries) {
	ses->retries=retries;
    }

    memcpy( ses->name, devnam, IFNAMSIZ);
    ses->opt_debug=1;
    ses->fd = -1;
    //info("init_device_pppoe over /cd===");//add by HQS
}

static void send_config_pppoe(int mtu,
			      u_int32_t asyncmap,
			      int pcomp,
			      int accomp)
{
    int sock;
    struct ifreq ifr;

    if (mtu > PPPOE_MTU)
	warn("Couldn't increase MTU to %d", mtu);
    sock = socket(AF_INET, SOCK_DGRAM, 0);
    if (sock < 0)
	fatal("Couldn't create IP socket: %m");
    strlcpy(ifr.ifr_name, ifname, sizeof(ifr.ifr_name));
    ifr.ifr_mtu = mtu;
    if (ioctl(sock, SIOCSIFMTU, (caddr_t) &ifr) < 0)
	fatal("ioctl(SIOCSIFMTU): %m");
    (void) close (sock);
}


static void recv_config_pppoe(int mru,
			      u_int32_t asyncmap,
			      int pcomp,
			      int accomp)
{
    if (mru > PPPOE_MTU)
	error("Couldn't increase MRU to %d", mru);
}

struct channel pppoe_channel;
/* Check is cp is a valid ethernet device
 * return either 1 if "cp" is a reasonable thing to name a device
 * or die.
 * Note that we don't actually open the device at this point
 * We do need to fill in:
 *   devnam: a string representation of the device
 */

int (*old_setdevname_hook)(const char* cp) = NULL;
int setdevname_pppoe(const char *cp)
{
    int ret;
    char dev[IFNAMSIZ+1];
    int addr[ETH_ALEN];
    int sid;
    option_t *opt;
    //info("setdevname_pppoe  //cd===");//add by HQS
    ret =sscanf(cp, FMTSTRING(IFNAMSIZ),addr, addr+1, addr+2,
		addr+3, addr+4, addr+5,&sid,dev);
    if( ret != 8 ){

	ret = get_sockaddr_ll(cp,NULL);
        if (ret < 0)
	    fatal("PPPoE: Cannot create PF_PACKET socket for PPPoE discovery\n");
	if (ret == 1)
	    strncpy(devnam, cp, sizeof(devnam));
    }else{
	/* long form parsed */
	ret = get_sockaddr_ll(dev,NULL);
        if (ret < 0)
	    fatal("PPPoE: Cannot create PF_PACKET socket for PPPoE discovery\n");

	strncpy(devnam, cp, sizeof(devnam));
	ret = 1;
    }

    info("PPPoE: Use %s for PPPoE discovery\n", devnam);

    if( ret == 1 && the_channel != &pppoe_channel ){

	the_channel = &pppoe_channel;

	lcp_allowoptions[0].neg_accompression = 0;
	lcp_wantoptions[0].neg_accompression = 0;

	lcp_allowoptions[0].neg_asyncmap = 0;
	lcp_wantoptions[0].neg_asyncmap = 0;

	lcp_allowoptions[0].neg_pcompression = 0;
	lcp_wantoptions[0].neg_pcompression = 0;

	ipcp_allowoptions[0].neg_vj=0;
	ipcp_wantoptions[0].neg_vj=0;

	ipcp_allowoptions[0].default_route=1;
	
	/* remove for add -R parameter set default route. by tallest.*/
	//ipcp_wantoptions[0].default_route=0;
	
	for (opt = ipcp_protent.options; opt->name != NULL; ++opt) {
		if (!strncmp(opt->name, "usepeerdns", 10)) {
			*(bool *)(opt->addr) = 1;
			break;
		}
	}
    //info("PPPoE: Use %s for PPPoE discovery 001\n", devnam);

#ifdef CCP_SUPPORT
	ccp_allowoptions[0].deflate = 0 ;
	ccp_wantoptions[0].deflate = 0 ;

	ccp_allowoptions[0].bsd_compress = 0;
	ccp_wantoptions[0].bsd_compress = 0;
#endif
    //info("PPPoE: Use %s for PPPoE discovery 002\n", devnam);
	init_device_pppoe();
    }
    return ret;
}

struct channel pppoe_channel = {
    options: NULL,
    process_extra_options: NULL,
    check_options: NULL,
    connect: &connect_pppoe_ses,
    disconnect: &disconnect_pppoe_ses,
    establish_ppp: &generic_establish_ppp,
    disestablish_ppp: &generic_disestablish_ppp,
    send_config: &send_config_pppoe,
    recv_config: &recv_config_pppoe,
    close: NULL,
    cleanup: NULL
};

