package com.mediatekk.tvcm;

import java.util.Comparator;
import java.util.List;

/**
 * TVChannelList is the 'view' of channel operation. TVChannelManager will create channel list
 * by filter. 
 *  
 * @author mtk40063
 *
 */
public interface TVChannelList {

	public interface ChannelFilter {
		final ChannelFilter TVSkipFilter = new ChannelFilter() {
			public boolean filter(TVChannel channel) {
				return channel.isSkip();
			}
		};
		final ChannelFilter TVNotSkipFilter = new ChannelFilter() {
			public boolean filter(TVChannel channel) {
				return !channel.isSkip();
			}
		};
		final ChannelFilter TVFavoriteFilter = new ChannelFilter() {
			public boolean filter(TVChannel channel) {
				return channel.isFavorite();
			}
		};

		/**
		 * Filter function.
		 * 
		 * @param channel
		 * @return true: The channel will be in list <br>
		 *         false: The channel will not be in list.
		 */
		public boolean filter(TVChannel channel);
	}

	public interface ListChangedListener {
		/**
		 * The list has been changed
		 */
		void onChanged();
	}

	/**
	 * Get some channels after start index, with a filter.
	 * 
	 * @param startIdx
	 * @param count
	 * @param filter
	 * @return
	 */
	public abstract List<TVChannel> getChannels(ChannelFilter filter);

	/**
	 * Get some channels after start index, with a filter.
	 * 
	 * @param startIdx
	 * @param count
	 * @param filter
	 * @return
	 */
	public abstract List<TVChannel> getChannels(int startIdx, int count,
			ChannelFilter filter);

	/**
	 * @deprecated This should not be used or just for a trace.. 
	 * 
	 */
	public abstract int getChannelLength();

	/**
	 * Find channel with channel number
	 * 
	 * @param num
	 * @return
	 */
	public abstract TVChannel findChannelByNumber(short num);

	/**
	 * Get next channel. If it cannot find , arg channel self is returned
	 * 
	 * @param ch
	 * @param round
	 *            : Turn back to head?
	 * @param filter
	 *            : Can be null.
	 * @return return channel self if not found, or the channel after the ch and
	 *         in filter.
	 */
	public abstract TVChannel nextChannel(TVChannel ch, boolean round,
			ChannelFilter filter);

	/**
	 * Get prev channel. If it cannot find , arg channel self is returned
	 * 
	 * @param ch
	 * @param round
	 *            : Turn back to tail?
	 * @param filter
	 *            : Can be null.
	 * @return return channel self if not found, or the channel before the ch
	 *         and in filter.
	 */
	public abstract TVChannel prevChannel(TVChannel ch, boolean round,
			ChannelFilter filter);

	public abstract TVChannel firstChannel(boolean round, ChannelFilter filter);
	public abstract TVChannel lastChannel(boolean round, ChannelFilter filter);
	
	public abstract void insertChannelAfter(TVChannel chSrc, TVChannel chDst);

	/**
	 * Insert the channel before the channelAfter.
	 * 
	 * @param channelSrc
	 * @param channelDest
	 */
	public abstract void insertChannel(TVChannel chSrc, TVChannel chDst);

	/**
	 * Swap two channels.
	 * 
	 * @param ch1
	 * @param ch2
	 */
	public void swapChannel(TVChannel ch1, TVChannel ch2);

	/**
	 * Delete a channel. If channel dose not exit in the list, no channel will be deleted. 
	 * @param ch
	 */
	public void deleteChannel(TVChannel ch);

	/**
	 * Delete channel
	 * 
	 * @param ch
	 */
	abstract void onChannelDeleted(TVChannel ch);
	abstract void onClear();
	abstract void onChannelAdd(TVChannel ch);
	abstract void onChannelEdit(TVChannel ch);
	/**
	 * Register channel list listener.
	 * 
	 * @param listener
	 */
	public void registerListListener(ListChangedListener listener);

	/**
	 * Remove channel list listener.
	 * 
	 * @param listener
	 */
	public void removeListListener(ListChangedListener listener);
	
	public void notifyListeners() ;

}