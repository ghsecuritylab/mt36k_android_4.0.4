#include "StreamSelector.h"

using namespace hls;

class StreamSelectorBivl: public StreamSelector
{
public :
    // override selectStream for first stream select
    virtual void selectStream(StreamArray & strms, int downloadBandWith, StreamArray::iterator * selectWhich);
};


void StreamSelectorBivl::selectStream(StreamArray & strms, int downloadBandWith, StreamArray::iterator * selectWhich)
{
    if (downloadBandWith <= 0)
    {
        // bivl http live streaming specification
        // means first time to select stream (so nothing has been downloaded)
        
    }else{
        StreamSelector::selectStream(strms, downloadBandWith, selectWhich);
    }
}

    
