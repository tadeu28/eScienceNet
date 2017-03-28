/*
 *  Copyright (c) 2001 Sun Microsystems, Inc.  All rights
 *  reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in
 *  the documentation and/or other materials provided with the
 *  distribution.
 *
 *  3. The end-user documentation included with the redistribution,
 *  if any, must include the following acknowledgment:
 *  "This product includes software developed by the
 *  Sun Microsystems, Inc. for Project JXTA."
 *  Alternately, this acknowledgment may appear in the software itself,
 *  if and wherever such third-party acknowledgments normally appear.
 *
 *  4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and "Project JXTA" must
 *  not be used to endorse or promote products derived from this
 *  software without prior written permission. For written
 *  permission, please contact Project JXTA at http://www.jxta.org.
 *
 *  5. Products derived from this software may not be called "JXTA",
 *  nor may "JXTA" appear in their name, without prior written
 *  permission of Sun.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 *  ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 *  USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *  OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 *  SUCH DAMAGE.
 *
 *  ==========================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of Project JXTA.  For more
 *  information on Project JXTA, please see
 *  <http://www.jxta.org/>.
 *
 *  This license is based on the BSD license adopted by the Apache Foundation.
 *
 *  $Id: PipePresence.java,v 1.20 2003/02/08 19:36:51 hamada Exp $
 *
 */
package juniorvs.virtualdir.ext;
import java.io.IOException;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import net.jxta.discovery.DiscoveryService;
import net.jxta.endpoint.Message;
import net.jxta.instantp2p.util.PreferenceReader;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.OutputPipeEvent;
import net.jxta.pipe.OutputPipeListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

/**
 * Classe responsável pelas presenças dos canais
 * 
 * @author Sun Microsystems
 */
public class PipePresence implements Runnable, OutputPipeListener {
	public final static int CHAT = 0;
	public final static String BuddyListName = "IP2PBuddyList";
	private final static int MediumNap = 5 * 60 * 1000; // 5 Minutes
	private final static int LongNap = 15 * 60 * 1000; // 15 Minutes
	protected Hashtable offlineBuddies = new Hashtable();
	protected Hashtable onlineBuddies = new Hashtable();
	private PeerGroup group = null;
	private PipeService pipe = null;
	private DiscoveryService discovery = null;
	private Vector listeners = new Vector();
	private PipeAdvertisement replyPipe = null;
	private Hashtable pipes = new Hashtable();
	private Timer timer = new Timer();
	private Chat chat = null;
	public PipePresence(PeerGroup group, PipeAdvertisement replyPipe, Chat chat) {
		this.group = group;
		this.chat = chat;
		this.replyPipe = replyPipe;
		this.pipe = group.getPipeService();
		this.discovery = group.getDiscoveryService();
		Thread thread = new Thread(this, "VirtualDir:PipePresence");
		thread.start();
	}
	public void setReplyPipe(PipeAdvertisement adv) {
		replyPipe = adv;
	}
	public PipeAdvertisement getReplyPipe() {
		return replyPipe;
	}
	public void probe(String pipeID) {
		try {
			Enumeration en = discovery.getLocalAdvertisements(
					DiscoveryService.ADV, PipeAdvertisement.IdTag, pipeID);
			if (en.hasMoreElements()) {
				PipeAdvertisement pipeAdv = (PipeAdvertisement) en
						.nextElement();
				if (pipeAdv != null) {
					pipe.createOutputPipe(pipeAdv, this);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void probe(String buddy, Enumeration advertisements) {
		try {
			while (advertisements.hasMoreElements()) {
				PipeAdvertisement pipeAdv = (PipeAdvertisement) advertisements
						.nextElement();
				if (pipeAdv != null
						&& (!(pipeAdv.getType())
								.equals(PipeService.PropagateType))) {
					if (buddy != null) {
						offlineBuddies.put(buddy, pipeAdv.getPipeID()
								.toString());
					}
					pipe.createOutputPipe(pipeAdv, this);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public Enumeration getPipes() {
		return pipes.keys();
	}
	public void probe(String buddy, PipeAdvertisement pipeAdv) {
		if (buddy != null) {
			offlineBuddies.put(buddy, pipeAdv.getPipeID().toString());
		}
		try {
			pipe.createOutputPipe(pipeAdv, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run() {
		loadBuddyList();
		timer.scheduleAtFixedRate(new MediumNapTask(this), 0, // Now
				MediumNap);
		timer.scheduleAtFixedRate(new LongNapTask(this), 0, // Now
				LongNap);
	}
	public OutputPipe getOutputPipe(String id) {
		return (OutputPipe) pipes.get(id);
	}
	public void removeOutputPipe(String id) {
		pipes.remove(id);
	}
	public void outputPipeEvent(OutputPipeEvent event) {
		OutputPipe op = event.getOutputPipe();
		String pipeId = event.getPipeID();
		OutputPipe old = (OutputPipe) pipes.put(pipeId, op);
		if (old != null) {
			old.close();
		}
		boolean foundInOffline = false;
		Enumeration en = offlineBuddies.keys();
		try {
			String buddy = null;
			String pid = null;
			while (en.hasMoreElements()) {
				buddy = (String) en.nextElement();
				pid = (String) offlineBuddies.get(buddy);
				if ((pid != null) && (pid.equals(pipeId))) {
					offlineBuddies.remove(buddy);
					onlineBuddies.put(buddy, new OnlineBuddy(buddy, pipeId,
							group, this, replyPipe));
					foundInOffline = true;
					break;
				}
			}
		} catch (Exception ez1) {
		}
		if (!foundInOffline) {
			en = onlineBuddies.keys();
			try {
				String buddy = null;
				while (en.hasMoreElements()) {
					buddy = (String) en.nextElement();
					OnlineBuddy onlineBuddy = (OnlineBuddy) onlineBuddies
							.get(buddy);
					if ((onlineBuddy != null)
							&& (onlineBuddy.pipeId.equals(pipeId))) {
						onlineBuddy.lastChecked = System.currentTimeMillis();
						break;
					}
				}
			} catch (Exception ez1) {
			}
		}
		if (listeners.size() > 0) {
			PresenceEvent newevent = new PresenceEvent(this, event.getPipeID(),
					getName(event.getPipeID()), true);
			for (int i = 0; i < listeners.size(); i++) {
				Listener pl = (Listener) listeners.elementAt(i);
				pl.presenceEvent(newevent);
			}
		}
	}
	public Enumeration getNames() {
		Vector names = new Vector();
		Enumeration en = pipes.keys();
		while (en.hasMoreElements()) {
			names.addElement(getName((String) en.nextElement()));
		}
		return names.elements();
	}
	public void processAck(String buddy) {
		OnlineBuddy onlineBuddy = (OnlineBuddy) onlineBuddies.get(buddy);
		if (onlineBuddy != null) {
			// Update the last checked time
			onlineBuddy.lastChecked = System.currentTimeMillis();
		}
	}
	public synchronized void addListener(Listener listener) {
		listeners.addElement(listener);
	}
	public synchronized boolean removeListener(Listener listener) {
		return (listeners.removeElement(listener));
	}
	public void addOnlineBuddy(String buddy, PipeAdvertisement buddyAdv) {
		if (buddyAdv == null) {
			return;
		}
		PreferenceReader prefs = PreferenceReader.getInstance();
		String propPrefix = getClass().getName() + BuddyListName;
		try {
			probe(buddy, buddyAdv);
		} catch (Exception ez1) {
		}
		prefs.setProperty(propPrefix + buddy, buddy);
		prefs.save();
	}
	public OnlineBuddy getOnlineBuddy(String name) {
		return (OnlineBuddy) onlineBuddies.get(name);
	}
	public String getName(String id) {
		Enumeration en = null;
		try {
			en = discovery.getLocalAdvertisements(DiscoveryService.ADV,
					PipeAdvertisement.IdTag, id);
			if (en == null) {
				return null;
			}
		} catch (IOException e) {
		}
		if (en.hasMoreElements()) {
			PipeAdvertisement pipeAdv = (PipeAdvertisement) en.nextElement();
			String name = pipeAdv.getName();
			if (name != null) {
				if (name.startsWith(Chat.CHATNAMETAG + ".")) {
					return name.substring(name.lastIndexOf('.') + 1);
				}
			}
		}
		return null;
	}
	private void loadBuddyList() {
		PreferenceReader prefs = PreferenceReader.getInstance();
		String propPrefix = getClass().getName() + BuddyListName;
		Enumeration en = prefs.propertyNames();
		if ((en == null) || (!en.hasMoreElements())) {
			// Not properties...
			return;
		}
		String propName = null;
		while (en.hasMoreElements()) {
			try {
				propName = (String) en.nextElement();
				if (propName.startsWith(propPrefix)) {
					// This is a buddy.
					String buddy = (String) prefs.getProperty(propName);
					// Probe it
					probe(buddy, discovery.getLocalAdvertisements(
							DiscoveryService.ADV, PipeAdvertisement.NameTag,
							Chat.CHATNAMETAG + "." + buddy));
				}
			} catch (Exception ez1) {
				continue;
			}
		}
	}
	public class PresenceEvent extends EventObject {
		private String pipeID = null;
		private String name = null;
		private boolean status = false;
		public PresenceEvent(Object source, String id, String name,
				boolean status) {
			super(source);
			this.pipeID = id;
			this.name = name;
			this.status = status;
		}
		public String getPipeID() {
			return pipeID;
		}
		public String getName() {
			return name;
		}
		public boolean getStatus() {
			return status;
		}
	}
	public interface Listener extends EventListener {
		void presenceEvent(PresenceEvent event);
	}
	protected void notifyOfflineBuddy(OnlineBuddy buddy) {
		if (listeners.size() > 0) {
			PresenceEvent newevent = new PresenceEvent(this, buddy.pipeId,
					buddy.name, false);
			for (int i = 0; i < listeners.size(); i++) {
				Listener pl = (Listener) listeners.elementAt(i);
				pl.presenceEvent(newevent);
			}
		}
	}
	protected void ping(OnlineBuddy buddy) {
		chat.pingBuddy(buddy);
	}
	protected void piggyback(OnlineBuddy buddy, Message msg) {
		chat.piggyback(buddy, msg);
	}
	protected class MediumNapTask extends TimerTask {
		private PipePresence presence = null;
		public MediumNapTask(PipePresence presence) {
			this.presence = presence;
		}
		public void run() {
			Enumeration buddys = null;
			OnlineBuddy buddy = null;
			String buddyName = null;
			Hashtable onlineBuddies = presence.onlineBuddies;
			Hashtable offlineBuddies = presence.offlineBuddies;
			if (onlineBuddies.size() > 0) {
				buddys = onlineBuddies.keys();
				while (buddys.hasMoreElements()) {
					buddyName = (String) buddys.nextElement();
					buddy = (OnlineBuddy) onlineBuddies.get(buddyName);
					long currentTime = System.currentTimeMillis();
					long lastChecked = currentTime - buddy.lastChecked;
					if (lastChecked > MediumNap) {
						removeOutputPipe(buddy.pipeId);
						onlineBuddies.remove(buddyName);
						presence.notifyOfflineBuddy(buddy);
						offlineBuddies.put(buddyName, buddy.pipeId);
					} else {
						presence.ping(buddy);
					}
				}
			}
		}
	}
	protected class LongNapTask extends TimerTask {
		private PipePresence presence = null;
		public LongNapTask(PipePresence presence) {
			this.presence = presence;
		}
		public void run() {
			Enumeration buddys = null;
			Hashtable offlineBuddies = presence.offlineBuddies;
			buddys = offlineBuddies.elements();
			while (buddys.hasMoreElements()) {
				try {
					probe((String) buddys.nextElement());
				} catch (Exception ez1) {
					continue;
				}
			}
		}
	}
}
