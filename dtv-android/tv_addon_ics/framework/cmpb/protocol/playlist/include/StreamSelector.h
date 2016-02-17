#ifndef _STREAMSELECTOR_H_
#define _STREAMSELECTOR_H_

#include "Playlist.h"

namespace hls
{

    class StreamSelector: public StreamSelectorAbstract
    {
    public:
        virtual void selectStream(StreamArray & strms, int downloadBandWith, StreamArray::iterator * selectWhich);
        virtual void sortStreams(StreamArray & strm);
    };

}

    

#endif /* _STREAMSELECTOR_H_ */
