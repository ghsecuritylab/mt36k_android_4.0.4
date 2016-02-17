/*
 * define path names
 *
 * $Id: pathnames.h,v 1.1 2008-08-04 06:11:51 winfred Exp $
 */

#ifdef HAVE_PATHS_H
#include <paths.h>

#else /* HAVE_PATHS_H */
#ifndef _PATH_VARRUN
#define _PATH_VARRUN 	"/tmp/ppp/"
#endif
#define _PATH_DEVNULL	"/dev/null"
#endif /* HAVE_PATHS_H */

#ifndef _ROOT_PATH
#define _ROOT_PATH
#endif

#define _PATH_ADSLADDRESS  "/data/data/addr"//add by HQS--20110523
#define _PATH_ADSLERROR  "/data/data/error"//add by HQS--20110523
#define _PATH_ADSLPID  "/data/data/ppid"//add by HQS
#define _PATH_ADSLPIDFILE  "/data/data/iface"//add by HQS--20110523
#define _PATH_UPAPFILE 	 _ROOT_PATH "/data/pap-secrets"//"/tmp/ppp/pap-secrets"
#define _PATH_CHAPFILE 	 _ROOT_PATH "/data/chap-secrets"//"/tmp/ppp/chap-secrets"
#define _PATH_SYSOPTIONS _ROOT_PATH "/data/options"
#define _PATH_IPUP	 _ROOT_PATH "/etc/ppp/ip-up"
#define _PATH_IPDOWN	 _ROOT_PATH "/etc/ppp/ip-down"
#define _PATH_AUTHUP	 _ROOT_PATH "/tmp/ppp/auth-up"
#define _PATH_AUTHDOWN	 _ROOT_PATH "/tmp/ppp/auth-down"
#define _PATH_TTYOPT	 _ROOT_PATH "/tmp/ppp/options."
#define _PATH_CONNERRS	 _ROOT_PATH "/tmp/ppp/connect-errors"
#define _PATH_PEERFILES	 _ROOT_PATH "/tmp/ppp/peers/"
#define _PATH_RESOLV	 _ROOT_PATH "/data/data/resolv.conf"//"/tmp/ppp/resolv.conf"
//#define _PATH_SETPPPOEPID     _ROOT_PATH "/tmp/ppp/set-pppoepid" 
#define _PATH_SETPPPOEPID     _ROOT_PATH "/data/data/set-pppoepid" //mod by HQS--20110603

#define _PATH_USEROPT	 ".ppprc"

#ifdef INET6
#define _PATH_IPV6UP     _ROOT_PATH "/tmp/ppp/ipv6-up"
#define _PATH_IPV6DOWN   _ROOT_PATH "/tmp/ppp/ipv6-down"
#endif

#ifdef IPX_CHANGE
#define _PATH_IPXUP	 _ROOT_PATH "/tmp/ppp/ipx-up"
#define _PATH_IPXDOWN	 _ROOT_PATH "/tmp/ppp/ipx-down"
#endif /* IPX_CHANGE */

#ifdef __STDC__
#define _PATH_PPPDB	_ROOT_PATH _PATH_VARRUN "pppd.tdb"
#else /* __STDC__ */
#ifdef HAVE_PATHS_H
#define _PATH_PPPDB	"/var/run/pppd.tdb"
#else
#define _PATH_PPPDB	"/tmp/ppp/pppd.tdb"
#endif
#endif /* __STDC__ */

#ifdef PLUGIN
#define _PATH_PLUGIN	"/system/lib" VERSION
#endif /* PLUGIN */
