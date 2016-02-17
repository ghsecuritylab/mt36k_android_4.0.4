#include "StreamSelector.h"
#include <algorithm>

using namespace hls;
using namespace std;

void StreamSelector::selectStream(StreamArray & strms, int downloadBandWith, StreamArray::iterator * selectWhich)
{

    StreamArray::iterator it;
    StreamArray::iterator best = strms.end();
    // the strms must have been sorted by sortStreams.
    for (it = strms.begin(); it != strms.end(); ++it)
    {
        PlaylistStream * stream = *it;
        if (stream->getBandwith() <= downloadBandWith)
        {
            // compare next more better one
            best = it;  // before next to be compared, this one is the best.
            continue;
        }else{
            //  case of ">="
            // because the bandwith of this stream is great than download bandwith, so the one just in front of this stream(if have) is the best stream.
            if (best == strms.end())
            {
                // this one is just the first one.
                best = it;
            }
            break;
        }
    }
    *selectWhich = best;
   
    return;
}

static bool bandWithAscending(PlaylistStream * strm1, PlaylistStream * strm2)
{
    return strm1->getBandwith() < strm1->getBandwith();
}

void StreamSelector::sortStreams(StreamArray & strms)
{
    // sort streams before select stream.
    // So that the strms variable passed to selectStream method has been sorted by this method.
    std::sort(strms.begin(), strms.end(), bandWithAscending);
}
