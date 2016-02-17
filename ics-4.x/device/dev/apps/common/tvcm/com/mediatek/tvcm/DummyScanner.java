package com.mediatek.tvcm;

class DummyScanner extends ScanTask {
	String chs[] = null;
	
	DummyScanner(TVScanner scanner, String name, TVScanner.ScannerListener listener) {
		super(scanner, name, listener);
		this.chs = new String[] {
				name + "Fake1", 
				name + "Fake2",
				name + "Fake3",
				name + "Fake4",
				name + "Fake5"
				
		};
		// TODO Auto-generated constructor stub
	}
	int chNum;
	
	
	public boolean scan() {
		
		
		new Thread(new Runnable() {
			public void run() {

				

				for (chNum = 0; chNum < chs.length; chNum++) {
					scanner.getContent().getChannelManager()
							.dummyAdd(chs[chNum]);
					scanner.getHandler().post(new Runnable() {
						public void run() {
							listener.onProgress((chNum + 1)* 100 / chs.length,
									chNum);
						}
					});
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						scanner.getHandler().post(new Runnable() {
							public void run() {
								listener
										.onCompleted(
												TVScanner.ScannerListener.COMPLETE_CANCEL);
							}
						});
						return;
					}
				}
				scanner.getHandler().post(new Runnable() {
					public void run() {
						listener
								.onCompleted(
										TVScanner.ScannerListener.COMPLETE_OK);
					}
				});
				onComplete();
			}
		}).start();
		return true;

	}


	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}
}