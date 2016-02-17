package com.tcl.stt.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPool {
	private static ThreadPoolExecutor threadPool ;

	public static ThreadPoolExecutor getThreadPool() {
		if(threadPool == null)
		{
			threadPool	= new ThreadPoolExecutor(1, 1,
					3, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(50),
					new ThreadPoolExecutor.CallerRunsPolicy());
		}
		return threadPool;
	}
	public static void init()
	{
		threadPool = null;
	}
}
