package com.mediatek.tvcm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.mediatek.tv.model.CIListener;
import com.mediatek.tv.model.ChannelInfo;
import com.mediatek.tv.model.HostControlTune;
import com.mediatek.tv.model.MMI;
import com.mediatek.tv.model.MMIEnq;
import com.mediatek.tv.model.MMIMenu;
import com.mediatek.tv.service.CIService;
import com.mediatek.tvcm.util.Node;
import com.mediatek.tvcm.util.Tree;

import android.content.Context;
import android.widget.Toast;

class FakeMenu extends MMIMenu {
	private String title;
	private String subtitle;
	private String bottom;
	private String[] itemlist;

	public FakeMenu(String title, String subtitle, String bottom,
			String[] itemlist) {
		this.title = title;
		this.subtitle = subtitle;
		this.bottom = bottom;
		this.itemlist = itemlist;
	}

	public void close() {

	}

	public void closeDone() {

	}

	public void answer(char item) {

	}

	public String getTitle() {
		return title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public String getBottom() {
		return bottom;
	}

	public String[] getItemList() {
		return itemlist;
	}

	public String toString() {
		StringBuffer str = new StringBuffer("\n=== FakeMenu ===");
		str.append("\nTitle:" + getTitle());
		str.append("\nSubtitle:" + getSubtitle());
		str.append("\nBottom:" + getBottom());
		for (String item : getItemList()) {
			str.append("\nItem:" + item);
		}
		str.append("\n=== FakeMenu ===");
		return str.toString();
	}
}

class FakeEnq extends MMIEnq {
	private byte ans_txt_len;
	private byte b_blind_ans;
	private String text;
	private String pwd;

	public FakeEnq(byte len, byte blindByte, String text, String pwd) {
		this.ans_txt_len = len;
		this.b_blind_ans = blindByte;
		this.text = text;
		this.pwd = pwd;
	}

	protected boolean isPasswordOk() {
		return text.equals(pwd);
	}

	public void close() {

	}

	public void closeDone() {

	}

	public void answer(boolean answer, String answer_data) {

	}

	public String getText() {
		return this.text;
	}

	public boolean isBlindAns() {
		return this.b_blind_ans > 0;
	}

	public byte getAnsTextLen() {
		return this.ans_txt_len;
	}

	public String toString() {
		StringBuffer str = new StringBuffer("\n=== FakeEnq ===");
		str.append("\nTitle:" + getText());
		str.append("\nAnswer length:" + getAnsTextLen());
		str.append("\n=== FakeEnq ===");
		return str.toString();
	}
}

public class TVCAMManager extends TVComponent {
	protected CIService ciService = null;
	private CAMMenu camMenu = null;
	private CAMEnquiry camEnquiry = null;
	private Tree<MMI> fakeTree = null;
	private Node<MMI> curNode = null;
	private boolean dummyActive = false;
	private MMI mmi = null;
	private volatile boolean changed = true;
	private TVContent content = TVContent.getInstance(context);
	protected List<Runnable> delays = new ArrayList<Runnable>();
	// when channel or tv input source is locked, it must delay CI
	// notifications.
	private boolean isDelay = false;

	protected TVCAMManager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public synchronized void setDelayNotifications(boolean isDelay) {
		this.isDelay = isDelay;
	}

	public synchronized void clearDelayNotifications() {
		delays.clear();
	}

	public void RunDelayNotifications() {
		for (Runnable run : delays) {
			if("com.mediatek.tvcm.TVCAMManager$DelegaterMMIMenuReceived".equals(run.getClass().getName())){
				getHandler().postDelayed(run, 2000);
			}else{
					getHandler().post(run);
			}
		}
		delays.clear();
	}

	void init() {

		camMenu = new CAMMenu();
		camEnquiry = new CAMEnquiry();

		if (dummyMode) {
			createFakeTree();
			return;
		}
		// Currently, our hardware only support one CI slot.
		ciService = CIService.getInstance(0);
		ciService.addCIListener(new CIListenerDelegater());
	}

	public static class CAMMenu {
		private MMIMenu menu = null;

		protected void setMMIMenu(MMIMenu menu) {
			this.menu = menu;
		}

		protected MMIMenu getMMIMenu() {
			return menu;
		}

		public String getTitle() {
			if (menu != null) {
				return menu.getTitle();
			}
			return null;
		}

		public String getSubtitle() {
			if (menu != null) {
				return menu.getSubtitle();
			}
			return null;
		}

		public String getBottom() {
			if (menu != null) {
				return menu.getBottom();
			}
			return null;
		}

		public String[] getItemList() {
			if (menu != null) {
				return menu.getItemList();
			}
			return null;
		}

		public void selectMenuItem(int num) {
			if (menu != null) {
				if (num >= 0 || num <= menu.getItemList().length - 1) {
					num += 1;
					menu.answer((char) num);
				}
			}
		}

		public void cancelCurrMenu() {
			if (menu != null) {
				menu.answer((char) 0);
			}
		}

		public String toString() {
			StringBuffer str = new StringBuffer("\n=== CAMMenu ===");
			str.append("\nTitle:" + getTitle());
			str.append("\nSubtitle:" + getSubtitle());
			str.append("\nBottom:" + getBottom());
			for (String item : getItemList()) {
				str.append("\nItem:" + item);
			}
			str.append("\n=== CAMMenu ===");
			return str.toString();
		}
	}

	public static class CAMEnquiry {
		private MMIEnq enq = null;

		protected void setMMIEnq(MMIEnq enq) {
			this.enq = enq;
		}

		protected MMIEnq getMMIEnq() {
			return enq;
		}

		public String getText() {
			return enq.getText();
		}

		public byte getAnsTextLen() {
			return enq.getAnsTextLen();
		}

		public boolean isBlindAns() {
			return enq.isBlindAns();
		}

		public void answerEnquiry(boolean isAns, String pwd) {
			enq.answer(isAns, pwd);
		}

		public String toString() {
			StringBuffer str = new StringBuffer("\n=== CAMEnquiry ===");
			str.append("\nTitle:" + getText());
			str.append("\nAnswer length:" + getAnsTextLen());
			str.append("\n=== CAMEnquiry ===");
			return str.toString();
		}
	}

	class CIListenerDelegater implements CIListener {

		public int camHostControlClearReplace() {
			if (isDelay) {
				delays.add(new DelegaterHostControlClearReplace());
			} else {
				notifyHostControlClearReplace();
			}
			return 0;
		}

		public int camHostControlReplace() {
			if (isDelay) {
				delays.add(new DelegaterHostControlReplace());
			} else {
				notifyHostControlReplace();
			}
			return 0;
		}

		public int camHostControlTune() {
			HostControlTune tune = ciService.getHostControlTune();
			ChannelInfo info = tune.getTunedChannel(content
					.getChannelSelector().getCurrentChannel().getRawInfo());
			List<TVChannel> list = content.getChannelManager().getChannels();
			Iterator<TVChannel> it = list.iterator();
			while (it.hasNext()) {
				TVChannel ch = it.next();
				if (ch.getRawInfo().equals(info)) {
					if (isDelay) {
						delays.add(new DelegaterHostControlTune(ch));
					} else {
						notifyHostControlTune(ch);
					}
				}
			}

			return 0;
		}

		public int camMMIClosed(byte arg0) {
			if (isDelay) {
				delays.add(new DelegaterMMIClosed(arg0));
			} else {
				notifyMMIClosed(arg0);
			}
			return 0;
		}

		public int camMMIEnqReceived() {
			if (isDelay) {
				delays.add(new DelegaterMMIEnqReceived(ciService.getMMIEnq()));
			} else {
				notifyMMIEnqReceived(ciService.getMMIEnq());
			}

			return 0;
		}

		public int camMMIMenuReceived() {
			if (isDelay) {
				delays
						.add(new DelegaterMMIMenuReceived(ciService
								.getMMIMenu()));
			} else {
				notifyMMIMenuReceived(ciService.getMMIMenu());
			}
			return 0;
		}

		public int camStatusUpdated(byte arg0) {
			switch (arg0) {
			case CIListener.CI_CAM_STATUS_INSERT:
				if (isDelay) {
					delays.add(new DelegaterCamInserted());
				} else {
					notifyCamInserted();
				}
				break;

			case CIListener.CI_CAM_STATUS_NAME:
				if (isDelay) {
					delays.add(new DelegaterCamNamed(ciService.getCamName()));
				} else {
					notifyCamNamed(ciService.getCamName());
				}
				break;

			case CIListener.CI_CAM_STATUS_REMOVE:
				if (isDelay) {
					delays.add(new DelegaterCamRemoved());
				} else {
					notifyCamRemoved();
				}
				break;

			default:
				break;
			}
			return 0;
		}

		public int camSystemIDStatusUpdated(byte arg0) {
			notifySystemIDStatusUpdated(arg0);
			return 0;
		}

	}

	public interface CamStatusUpdateListener {
		void camInsertUpdated();

		void camNameUpdated(String camName);

		void camRemoveUpdated();

		void camSystemIDStatusUpdated(byte sys_id_status);
	}

	public interface MenuEnqUpdateListener {
		void enqReceived(CAMEnquiry enquiry);

		void menuReceived(CAMMenu menu);

		void menuEnqClosed(byte mmiCloseDelay);
	}

	public interface CamHostControlListener {
		void camHostControlClearReplace();

		void camHostControlReplace();

		void camHostControlTune(TVChannel ch);
	}

	private List<CamStatusUpdateListener> camStatusListeners = new ArrayList<CamStatusUpdateListener>();
	private List<MenuEnqUpdateListener> menuEnqUpdateListeners = new ArrayList<MenuEnqUpdateListener>();
	private List<CamHostControlListener> camHostControlListeners = new ArrayList<CamHostControlListener>();

	public void registerCamStatusListener(CamStatusUpdateListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		synchronized (this) {
			if (!camStatusListeners.contains(listener)) {
				camStatusListeners.add(listener);
			}
		}
	}

	public void removeCamStatusListener(CamStatusUpdateListener listener) {
		camStatusListeners.remove(listener);
	}

	public void clearCamStatusListeners() {
		camStatusListeners.clear();
	}

	public void registerMenuEnqUpdateListener(MenuEnqUpdateListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		synchronized (this) {
			if (!menuEnqUpdateListeners.contains(listener)) {
				menuEnqUpdateListeners.add(listener);
			}
		}
	}

	public void removeMenuEnqUpdateListener(MenuEnqUpdateListener listener) {
		menuEnqUpdateListeners.remove(listener);
	}

	public void clearMenuEnqUpdateListener() {
		menuEnqUpdateListeners.clear();
	}

	public void registerCamHostControlListener(CamHostControlListener listener) {
		if (listener == null) {
			throw new IllegalArgumentException();
		}
		synchronized (this) {
			if (!camHostControlListeners.contains(listener)) {
				camHostControlListeners.add(listener);
			}
		}
	}

	public void removeCamHostControlListener(CamHostControlListener listener) {
		camHostControlListeners.remove(listener);
	}

	public void clearCamHostControlListener() {
		camHostControlListeners.clear();
	}

	class DelegaterMMIClosed implements Runnable {
		private byte mmiCloseDelay;

		public DelegaterMMIClosed(byte mmiCloseDelay) {
			this.mmiCloseDelay = mmiCloseDelay;
		}

		public void run() {
			if (!changed) {
				return;
			}
			Iterator<MenuEnqUpdateListener> it = menuEnqUpdateListeners
					.iterator();
			while (it.hasNext()) {
				it.next().menuEnqClosed(mmiCloseDelay);
			}
		}

	}

	private void notifyMMIClosed(final byte mmiCloseDelay) {
		getHandler().post(new DelegaterMMIClosed(mmiCloseDelay));
	}

	class DelegaterMMIEnqReceived implements Runnable {
		private MMIEnq enq;

		public DelegaterMMIEnqReceived(MMIEnq enq) {
			this.enq = enq;
		}

		public void run() {
			if (!changed) {
				return;
			}
			Iterator<MenuEnqUpdateListener> it = menuEnqUpdateListeners
					.iterator();
			mmi = enq;
			camEnquiry.setMMIEnq(enq);
			while (it.hasNext()) {
				it.next().enqReceived(camEnquiry);
			}
		}
	}

	private void notifyMMIEnqReceived(final MMIEnq enq) {
		getHandler().post(new DelegaterMMIEnqReceived(enq));
	}

	class DelegaterMMIMenuReceived implements Runnable {
		private MMIMenu menu;

		public DelegaterMMIMenuReceived(MMIMenu menu) {
			this.menu = menu;
		}

		public void run() {
			if (!changed) {
				return;
			}
			Iterator<MenuEnqUpdateListener> it = menuEnqUpdateListeners
					.iterator();
			mmi = menu;
			camMenu.setMMIMenu(menu);
			while (it.hasNext()) {
				it.next().menuReceived(camMenu);
			}
		}
	}

	private void notifyMMIMenuReceived(final MMIMenu menu) {
		getHandler().post(new DelegaterMMIMenuReceived(menu));
	}

	class DelegaterHostControlTune implements Runnable {
		private TVChannel ch;

		public DelegaterHostControlTune(TVChannel ch) {
			this.ch = ch;
		}

		public void run() {
			if (!changed) {
				return;
			}
			Iterator<CamHostControlListener> it = camHostControlListeners
					.iterator();
			while (it.hasNext()) {
				it.next().camHostControlTune(ch);
			}
		}

	}

	private void notifyHostControlTune(final TVChannel ch) {
		getHandler().post(new DelegaterHostControlTune(ch));
	}

	class DelegaterHostControlReplace implements Runnable {

		public void run() {
			if (!changed) {
				return;
			}
			Iterator<CamHostControlListener> it = camHostControlListeners
					.iterator();
			while (it.hasNext()) {
				it.next().camHostControlReplace();
			}
		}

	}

	private void notifyHostControlReplace() {
		getHandler().post(new DelegaterHostControlReplace());
	}

	class DelegaterHostControlClearReplace implements Runnable {

		public void run() {
			if (!changed) {
				return;
			}
			Iterator<CamHostControlListener> it = camHostControlListeners
					.iterator();
			while (it.hasNext()) {
				it.next().camHostControlClearReplace();
			}
		}

	}

	private void notifyHostControlClearReplace() {
		getHandler().post(new DelegaterHostControlClearReplace());
	}

	class DelegaterCamNamed implements Runnable {
		private String name;

		public DelegaterCamNamed(String name) {
			this.name = name;
		}

		public void run() {
			if (!changed) {
				return;
			}
			Iterator<CamStatusUpdateListener> it = camStatusListeners
					.iterator();
			while (it.hasNext()) {
				it.next().camNameUpdated(name);
			}
		}
	}

	private void notifyCamNamed(final String name) {
		getHandler().post(new DelegaterCamNamed(name));
	}

	class DelegaterCamInserted implements Runnable {

		public void run() {
			if (!changed) {
				return;
			}
			Iterator<CamStatusUpdateListener> it = camStatusListeners
					.iterator();
			while (it.hasNext()) {
				it.next().camInsertUpdated();
			}
		}

	}

	private void notifyCamInserted() {
		getHandler().post(new DelegaterCamInserted());
	}

	class DelegaterCamRemoved implements Runnable {

		public void run() {
			if (!changed) {
				return;
			}
			Iterator<CamStatusUpdateListener> it = camStatusListeners
					.iterator();
			while (it.hasNext()) {
				it.next().camRemoveUpdated();
			}
		}

	}

	private void notifyCamRemoved() {
		getHandler().post(new DelegaterCamRemoved());
	}

	class DelegaterSystemIDStatusUpdated implements Runnable {
		private byte sysIdStatus;

		public DelegaterSystemIDStatusUpdated(byte sysIdStatus) {
			this.sysIdStatus = sysIdStatus;
		}

		public void run() {
			if (!changed) {
				return;
			}
			Iterator<CamStatusUpdateListener> it = camStatusListeners
					.iterator();
			while (it.hasNext()) {
				it.next().camSystemIDStatusUpdated(sysIdStatus);
			}
		}

	}

	private void notifySystemIDStatusUpdated(final byte sysIdStatus) {
		getHandler().post(new DelegaterSystemIDStatusUpdated(sysIdStatus));
	}

	protected void switchInputPath(boolean on_off) {
		ciService.getCIInputDTVPath().switchPath(on_off);
	}

	protected void switchTSPath(boolean on_off) {
		ciService.getCITSPath().switchPath(on_off);
	}

	public int getCISlotNum() {
		if (dummyMode) {
			return 0;
		} else {
			return CIService.getSlotNum();
		}

	}

	public String getCamName() {
		if (dummyMode) {
			return "Dummy viaccess";
		} else {
			return ciService.getCamName();
		}

	}

	public void closeCurrentMMI() {
		if (mmi != null) {
			mmi.close();
		}
	}

	public CAMMenu getCamMenu() {
		return camMenu;
	}

	public CAMEnquiry getCamEnquiry() {
		return camEnquiry;
	}

	public boolean isSlotActive() {
		if (dummyMode) {
			return dummyActive;
		} else {
			return ciService.isSlotActive();
		}
	}

	public void enterCAMMainMenu() {
		if (dummyMode) {
			curNode = fakeTree.getRootElement();
			notifyMMIMenuReceived((FakeMenu) fakeTree.getRootElement()
					.getData());
		} else {
			ciService.enterMMI();
		}
	}

	public void selectMenuItem(int num) {
		if (dummyMode) {
			if (!curNode.hasChildren()) {
				TVComponent.TvLog("has no children");
			} else {
				if (num <= curNode.getChildren().size() - 1 && num >= 0) {
					curNode = curNode.getChildren().get(num);
				} else {
					return;
				}
			}

			if (curNode.getData() instanceof FakeMenu) {
				notifyMMIMenuReceived((FakeMenu) curNode.getData());
			} else {
				notifyMMIEnqReceived((FakeEnq) curNode.getData());
			}

		} else {
			camMenu.selectMenuItem(num);
		}
	}

	public void cancelCurrMenu() {
		if (dummyMode) {
			if (curNode.getParentNode() != null) {
				curNode = curNode.getParentNode();
				if (curNode.getData() instanceof FakeMenu) {
					notifyMMIMenuReceived((FakeMenu) curNode.getData());
				} else {
					notifyMMIEnqReceived((FakeEnq) curNode.getData());
				}
			} else {
				notifyMMIClosed((byte) 0);
			}
		} else {
			camMenu.cancelCurrMenu();
		}
	}

	public void answerEnquiry(boolean isAns, String pwd) {
		if (dummyMode) {
			if (isAns) {
				FakeEnq enq = (FakeEnq) curNode.getData();
				if (enq.isPasswordOk()) {
					Toast.makeText(context, "Password validation is OK.", 1000)
							.show();
				} else {
					Toast.makeText(context, "Password validation is Faild.",
							1000).show();
				}
			} else {
				cancelCurrMenu();
			}
		} else {
			camEnquiry.answerEnquiry(isAns, pwd);
		}

	}

	public void enableNotification(boolean changed) {
		this.changed = changed;
	}

	private void createFakeTree() {
		fakeTree = new Tree<MMI>();
		Node<MMI> main = new Node<MMI>(new FakeMenu("Fake viaccess Module", "",
				"Select the item", new String[] { "Consultations",
						"Authorizations", "Module Information" }));
		Node<MMI> item1 = new Node<MMI>(new FakeMenu("Fake viaccess Module",
				"Consultations", "Insert a smart card",
				new String[] { "Consultations Enquiry" }), main);
		Node<MMI> subenq1 = new Node<MMI>(new FakeEnq((byte) 6, (byte) 1,
				"Fake Enq one", "123456"), item1);
		item1.addChild(subenq1);

		Node<MMI> item2 = new Node<MMI>(new FakeMenu("Fake viaccess Module",
				"Authorizations", "Insert a smart card",
				new String[] { "Authorizations Enquiry" }), main);

		Node<MMI> subenq2 = new Node<MMI>(new FakeEnq((byte) 0xff, (byte) 0,
				"Fake Enq two", "1234"), item2);
		item2.addChild(subenq2);

		Node<MMI> item3 = new Node<MMI>(
				new FakeMenu("Fake viaccess Module", "Module Information",
						"OK to return to the main menu", new String[] {
								"Viaccess v1.0 V484", "V1.10.000",
								"Manufacturer: SmartDTV",
								"Serial Number:445465544545" }), main);
		main.addChild(item1);
		main.addChild(item2);
		main.addChild(item3);
		fakeTree.setRootElement(main);
		List<Node<MMI>> list = fakeTree.toList();
		Iterator<Node<MMI>> it = list.iterator();
		while (it.hasNext()) {
			TVComponent.TvLog(it.next().getData().toString());
		}
	}

	/**
	 * This public method is only for Dummy mode.
	 * 
	 * @throws IllegalAccessException
	 */
	public void dummyNotifyCamNamed() {
		if (dummyMode) {
			notifyCamNamed("Dummy viaccess");
			dummyActive = true;
		} else {
			try {
				throw new IllegalAccessException("Only for Dummy Mode.");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This public method is only for Dummy mode.
	 * 
	 * @throws IllegalAccessException
	 */
	public void dummyNotifyCamRemoved() {
		if (dummyMode) {
			notifyCamRemoved();
			dummyActive = false;
		} else {
			try {
				throw new IllegalAccessException("Only for Dummy Mode.");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
