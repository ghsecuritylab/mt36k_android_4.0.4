package cn.thinkit.libtmsr30;

import android.util.Log;

class MyLog {
	/**
	 * 打印Log，包含类名，行数
	 * 
	 * @param string
	 *            要打印的内容
	 */
	public static void log(String string) {
		if (Recorder.isDebug()) 
		{
			StringBuffer sb = new StringBuffer();
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			sb.append("class: ").append(stacks[1].getClassName()).append(
					"; method: ").append(stacks[1].getMethodName()).append(
					"; number: ").append(stacks[1].getLineNumber());
			Log.v("kang", "kang:" + sb.toString() + "--------->" + string);
		}
	}
}