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
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of Project JXTA.  For more
 *  information on Project JXTA, please see
 *  <http://www.jxta.org/>.
 *
 *  This license is based on the BSD license adopted by the Apache Foundation.
 *
 *  $Id: Chat.java,v 1.114 2003/12/04 23:29:31 hamada Exp $
 */

package juniorvs.virtualdir.ext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.JOptionPane;
import juniorvs.virtualdir.EventoDescoberta;
import juniorvs.virtualdir.Mensagem;
import juniorvs.virtualdir.OuvinteEstruturaGrupo;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredTextDocument;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.MessageElement;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.id.IDFactory;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PipeAdvertisement;

/**
 * Class responsável pelo Chat
 * 
 * @author Sun Microsystems
 */
public class Chat extends Discover implements DiscoveryListener,
                                              PipeMsgListener,
                                              Runnable,
                                              PipePresence.Listener {
    
    private static String EOL = System.getProperty("line.separator");
    public final static String CHATNAMETAG = "JxtaTalkUserName";
    public final static String SENDERNAME = "JxtaTalkSenderName";
    public final static String SENDERGROUPNAME = "GrpName";
    public final static String SENDERMESSAGE = "JxtaTalkSenderMessage";
    public final static String SRCPEERADV = "JxtaTalkSrcPeerAdv";
    public final static String SRCPIPEADV = "JxtaTalkSrcPipeAdv";
    public final static String GROUPID = "JxtaTalkGroupId";
    public final static String COMMAND = "JxtaTalkCommand";
    public final static String PING = "Ping";
    public final static String ACK = "Ack";
    protected final static int PipeTimeout = 50000;
    private final static int WaitingTime = 2000;
    private final static int MAXRETRIES = 10;
    private boolean groupChanged = false;
    protected PipeAdvertisement myPipeAdvt = null;
    protected PeerAdvertisement myPeerAdvt = null;
    private String myPipeAdvString = null;
    private String myPeerAdvString = null;
    protected PeerGroupManager manager = null;
    protected Mensagem messageBoard;
    protected InputPipe inputPipe = null;
    protected OnlineBuddy currentUser = null;
    protected PipeService pipes = null;
    protected PeerGroup currentGroup = null;
    protected DiscoveryService discovery = null;
    protected boolean chatInProgress = false;
    protected String toName;
    protected String myName;
    protected Vector structureListeners = new Vector();
    protected Vector pipePListener = new Vector();
    protected Vector inputPipes = new Vector();
    private static Vector chatSession = new Vector(10);
    private static Hashtable pipePTable = new Hashtable();
    private static Hashtable groups = new Hashtable();
    private boolean initialized = false;
    public interface Listener extends EventListener {
            void userEvent(PipePresence.PresenceEvent event);
            void refreshEvent(Enumeration en);
    }
    public Chat(PeerGroupManager manager, Mensagem messageBoard) {
            this(manager, messageBoard, PipeAdvertisement.NameTag, CHATNAMETAG
                            + ".*");
    }
    protected Chat(PeerGroupManager manager, Mensagem messageBoard,
                    String attr, String value) {
            super(manager.getSelectedPeerGroup(), DiscoveryService.ADV, attr, value);
            this.manager = manager;
            this.messageBoard = messageBoard;
            Thread thread = new Thread(this, "Messaning Thread");
            thread.start();
    }
    public void run() {
            currentGroup = manager.getSelectedPeerGroup();
            pipes = currentGroup.getPipeService();
            recordGroup(currentGroup);
            discovery = currentGroup.getDiscoveryService();
            myName = manager.getMyPeerName();
            chatSession.addElement(currentGroup.getPeerGroupID());
            login(myName);
            getPipePresence(currentGroup, myPipeAdvt);
            initialized = true;
    }
    private PipePresence getPipePresence(PeerGroup g,
                    PipeAdvertisement localPipeAdv) {
            PipePresence tmpPresence = (PipePresence) pipePTable.get(g.getPeerGroupID()
                            .toString());
            if (tmpPresence == null) {
                    // No PipePresence has been created yet.
                    tmpPresence = new PipePresence(g, localPipeAdv, this);
                    pipePTable.put(manager.getSelectedPeerGroup().getPeerGroupID()
                                    .toString(), tmpPresence);
                    tmpPresence.addListener(this);
            }
            tmpPresence.setReplyPipe(localPipeAdv);
            return tmpPresence;
    }
    private String advToString(Advertisement adv) {
            StringWriter out = new StringWriter();
            MimeMediaType displayAs = new MimeMediaType("text/xml");
            try {
                    StructuredTextDocument doc = (StructuredTextDocument) adv
                                    .getDocument(displayAs);
                    doc.sendToWriter(out);
                    return out.toString();
            } catch (Exception all) {
                    return null;
            }
    }
    public String getName(PipeAdvertisement pipeAdv) {
            if (pipeAdv == null)
                    return null;
            String name = pipeAdv.getName();
            if (name != null) {
                    return name.substring(CHATNAMETAG.length() + 1 /* the dot */
                    );
            }
            return null;
    }
    private void recordGroup(PeerGroup group) {
            groups.put(group.getPeerGroupID().toString(), group);
    }
    private PeerGroup getGroup(String pgid) {
            return (PeerGroup) groups.get(pgid);
    }
    public synchronized void groupChanged(PeerGroup group) {
            super.groupChanged(group);
            currentGroup = group;
            pipes = group.getPipeService();
            discovery = group.getDiscoveryService();
            try {
                    Enumeration en = discovery.getLocalAdvertisements(
                                    DiscoveryService.ADV, PipeAdvertisement.NameTag,
                                    CHATNAMETAG + ".*");
                    java.util.Vector result = new java.util.Vector();
                    while (en != null && en.hasMoreElements()) {
                            PipeAdvertisement adv = (PipeAdvertisement) en.nextElement();
                            if (validAdvertisement(adv)) {
                                    result.add(adv);
                            }
                    }
            } catch (IOException e) {
            }
            if (!initialized)
                    return;
            if (!chatSession.contains(group.getPeerGroupID())) {
                    myName = manager.getMyPeerName();
                    login(myName);
                    chatSession.addElement(group.getPeerGroupID());
            }
            recordGroup(group);
            PipePresence tmpp = getPipePresence(group, myPipeAdvt);
            refreshNotify(tmpp.getNames());
    }
    public void startChat(PipeAdvertisement toWhom) throws Exception {
            if (toWhom != null) {
                    PeerGroup group = manager.getSelectedPeerGroup();
                    currentUser = getPipePresence(group, myPipeAdvt).getOnlineBuddy(
                                    getName(toWhom));
                    if (currentUser != null) {
                            toName = getName(toWhom);
                            messageBoard.info("Connected with " + getName(toWhom));
                            return;
                    }
                    probeUser(toWhom);
                    messageBoard.info(getName(toWhom) + " is not connected." + EOL);
            }
    }
    public void pingBuddy(OnlineBuddy buddy) {
            Message msg = new Message();
            msg.addMessageElement(null, new StringMessageElement(SRCPIPEADV,
                            advToString(buddy.getReplyPipe()), null));
            msg.addMessageElement(null, new StringMessageElement(SRCPEERADV,
                            myPeerAdvString, null));
            msg.addMessageElement(null, new StringMessageElement(GROUPID, buddy
                            .getGroup().getPeerGroupID().toString(), null));
            msg.addMessageElement(null, new StringMessageElement(COMMAND, PING,
                            null));
            msg.addMessageElement(null, new StringMessageElement(SENDERNAME,
                            myName, null));
            buddy.sendMessage(msg);
    }
    public void piggyback(OnlineBuddy buddy, Message msg) {
            msg.addMessageElement(null, new StringMessageElement(SRCPIPEADV,
                            advToString(buddy.getReplyPipe()), null));
            msg.addMessageElement(null, new StringMessageElement(SRCPEERADV,
                            myPeerAdvString, null));
            msg.addMessageElement(null, new StringMessageElement(GROUPID, buddy
                            .getGroup().getPeerGroupID().toString(), null));
            msg.addMessageElement(null, new StringMessageElement(SENDERNAME,
                            myName, null));
    }
    public void probeUser(PipeAdvertisement toWhom) {
            PeerGroup group = manager.getSelectedPeerGroup();
            getPipePresence(group, myPipeAdvt).addOnlineBuddy(getName(toWhom),
                            toWhom);
    }
    private void login(String loginName) {
            myName = loginName;
            runChatServer();
    }
    public synchronized void addListener(Listener listener) {
            if (!pipePListener.contains(listener)) {
                    pipePListener.addElement(listener);
            }
    }
    public synchronized boolean removeListener(Listener listener) {
            return (pipePListener.removeElement(listener));
    }
    public synchronized PipeAdvertisement findUser(String name,
                    DiscoveryService discovery) {
            int i = 0;
            PipeAdvertisement adv = findLocalUser(name, discovery);
            if (adv != null) {
                    return adv;
            }
            discovery.getRemoteAdvertisements(null, DiscoveryService.ADV,
                            PipeAdvertisement.NameTag, "*." + name, 1, null);
            while (true) {
                    try {
                            if (i > MAXRETRIES) {
                                    break;
                            }
                            if (i > MAXRETRIES / 2) {
                                    discovery.getRemoteAdvertisements(null,
                                                    DiscoveryService.ADV, PipeAdvertisement.NameTag,
                                                    "*." + name, 1, null);
                            }
                            Thread.sleep(WaitingTime);
                            adv = findLocalUser(name, discovery);
                            if (adv != null) {
                                    return adv;
                            }
                            i++;
                    } catch (Exception e) {
                    }
            }
            return findLocalUser(name, discovery);
    }
    private synchronized PipeAdvertisement findLocalUser(String name,
                    DiscoveryService discovery) {
            Enumeration en;
            try {
                    en = discovery.getLocalAdvertisements(DiscoveryService.ADV,
                                    PipeAdvertisement.NameTag, "*." + name);
                    if ((en != null) && (en.hasMoreElements())) {
                            PipeAdvertisement adv = null;
                            while (en.hasMoreElements()) {
                                    try {
                                            adv = (PipeAdvertisement) en.nextElement();
                                            return adv;
                                    } catch (Exception e) {
                                            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
                                    }
                            }
                    }
            } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            }
            return null;
    }
    public void sendMessageToPeers(String message) {
            messageBoard.exibirMensagem(myName + "> " + message + EOL);
            if (currentUser != null) {
                    Message msg = null;
                    InputStream ip = null;
                    try {
                            if (message == null) {
                                    return;
                            }
                            msg = new Message();
                            msg.addMessageElement(null, new StringMessageElement(
                                            SENDERMESSAGE, message, null));
                            // push my Name
                            msg.addMessageElement(null, new StringMessageElement(
                                            SENDERNAME, myName, null));
                            // push my GroupName
                            msg.addMessageElement(null, new StringMessageElement(
                                            SENDERGROUPNAME, manager.getSelectedPeerGroup()
                                                            .getPeerGroupName(), null));
                            currentUser.sendMessage(msg);
                    } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
                    }
            }
    }
    public synchronized void runChatServer() {
            // autologin
            PeerGroup group = manager.getSelectedPeerGroup();
            DiscoveryService tmpDiscovery = group.getDiscoveryService();
            PipeService tmpPipe = group.getPipeService();
            myPeerAdvt = group.getPeerAdvertisement();
            myPeerAdvString = advToString(myPeerAdvt);
            myPipeAdvt = findUser(myName, tmpDiscovery);
            if (myPipeAdvt == null) {
                    messageBoard.info("Autenticated " + myName + " in group "
                                    + group.getPeerGroupName());
                    myPipeAdvt = (PipeAdvertisement) AdvertisementFactory
                                    .newAdvertisement(PipeAdvertisement.getAdvertisementType());
                    myPipeAdvt.setPipeID(IDFactory.newPipeID((PeerGroupID) group
                                    .getPeerGroupID()));
                    myPipeAdvt.setName(CHATNAMETAG + "." + myName);
                    myPipeAdvt.setType(PipeService.UnicastSecureType);
            }
            myPipeAdvt.setType(PipeService.UnicastSecureType);
            myPipeAdvString = advToString(myPipeAdvt);
            try {
                    publishAdvertisement(myPipeAdvt, tmpDiscovery);
                    inputPipe = tmpPipe.createInputPipe(myPipeAdvt, this);
                    inputPipes.addElement(inputPipe);
                    messageBoard.info(myName + " was authenticated in group " + group.getPeerGroupName() + EOL);
                    probeUser(myPipeAdvt);
            } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            }
    }
    public synchronized void publishAdvertisement(Advertisement adv,
                    DiscoveryService discovery) throws IOException {
            discovery.publish(adv);
    }
    public void pipeMsgEvent(PipeMsgEvent event) {
            Message msg = event.getMessage();
            processMessage(msg);
    }
    public void addStructureListener(OuvinteEstruturaGrupo listener) {
            structureListeners.addElement(listener);
    }
    public void removeStructureListener(OuvinteEstruturaGrupo listener) {
            structureListeners.removeElement(listener);
    }
    public PipeAdvertisement getPipeAdvertisement(String name) {
            Enumeration en = getAdvertisements();
            if (en == null) {
                    return null;
            }
            PipeAdvertisement pipeAd = null;
            PipeAdvertisement tmp;
            while (en.hasMoreElements()) {
                    tmp = (PipeAdvertisement) en.nextElement();
                    if (tmp.getName().endsWith(name)) {
                            pipeAd = tmp;
                            break;
                    }
            }
            return pipeAd;
    }
    public void searchAdvertisements() throws IOException {
            discover.getRemoteAdvertisements(null, DiscoveryService.ADV,
                            PipeAdvertisement.NameTag, CHATNAMETAG + ".*",
                            Discover.THRESHOLD, null);
            Enumeration en = discover.getLocalAdvertisements(
                            DiscoveryService.ADV, PipeAdvertisement.NameTag, CHATNAMETAG
                                            + ".*");
            java.util.ArrayList result = new java.util.ArrayList();
            while (en != null && en.hasMoreElements()) {
                    PipeAdvertisement adv = (PipeAdvertisement) en.nextElement();
                    if (validAdvertisement(adv)) {
                            addAdToList(adv, result);
                    }
            }
            processAdDiscovered(new EventoDescoberta(this,
                            EventoDescoberta.ANUNCIO_ADICIONADO, result));
    }
    protected byte[] getTagValue(Message msg, String tag) throws Exception {
            byte[] buffer = null;
            MessageElement elem = msg.getMessageElement(null, tag);
            if (elem != null) {
                    msg.removeMessageElement(elem);
                    InputStream ip = elem.getStream();
                    if (ip != null) {
                            buffer = new byte[ip.available()];
                            ip.read(buffer);
                    }
                    return buffer;
            } else {
                    return null;
            }
    }
    protected String getTagString(Message msg, String tag, String defaultValue)
                    throws Exception {
            byte[] buffer = getTagValue(msg, tag);
            String result;
            if (buffer != null) {
                    result = new String(buffer);
            } else {
                    result = defaultValue;
            }
            return result;
    }
    public boolean validAdvertisement(Advertisement adv) {
            PipeAdvertisement pipeAdv;
            if (adv instanceof PipeAdvertisement) {
                    pipeAdv = (PipeAdvertisement) adv;
                    String str = pipeAdv.getName();
                    String type = pipeAdv.getType();
                    if (str.startsWith(CHATNAMETAG + ".")
                                    && !(type.equals(PipeService.PropagateType))) {
                            return true;
                    }
            }
            return false;
    }
    protected void processMessage(Message msg) {
            String srcPeerAdvWireFormat = popString(msg, SRCPEERADV);
            PeerAdvertisement srcPeerAdv = null;
            try {
                    if (srcPeerAdvWireFormat != null) {
                            srcPeerAdv = (PeerAdvertisement) AdvertisementFactory
                                            .newAdvertisement(new MimeMediaType("text/xml"),
                                                            new ByteArrayInputStream(srcPeerAdvWireFormat
                                                                            .getBytes()));
                            discovery.publish(srcPeerAdv);
                    }
            } catch (Exception e) {
            }
            String srcPipeAdvWireFormat = popString(msg, SRCPIPEADV);
            PipeAdvertisement srcPipeAdv = null;
            try {
                    if (srcPipeAdvWireFormat != null) {
                            srcPipeAdv = (PipeAdvertisement) AdvertisementFactory
                                            .newAdvertisement(new MimeMediaType("text/xml"),
                                                            new ByteArrayInputStream(srcPipeAdvWireFormat
                                                                            .getBytes()));
                            discovery.publish(srcPipeAdv);
                    }
            } catch (Exception e) {
            }
            String groupId = popString(msg, GROUPID);
            PeerGroup group = null;
            if (groupId != null) {
                    group = getGroup(groupId);
            }
            String sender = null;
            String groupname = null;
            String senderMessage = null;
            try {
                    sender = getTagString(msg, SENDERNAME, "anonymous");
                    groupname = getTagString(msg, SENDERGROUPNAME, "unknown");
                    senderMessage = getTagString(msg, SENDERMESSAGE, null);
                    String msgstr;
                    if (groupname.equals(manager.getSelectedPeerGroup()
                                    .getPeerGroupName())) {
                            msgstr = sender + "> " + senderMessage + EOL;
                    } else {
                            msgstr = sender + "@" + groupname + "> " + senderMessage + EOL;
                    }
                    if (senderMessage != null) {
                            messageBoard.exibirMensagem(msgstr, sender);
                    }
                    if ((srcPipeAdv != null) && (group != null)) {
                            PipePresence p = getPipePresence(group, myPipeAdvt);
                            if (p != null) {
                                    p.addOnlineBuddy(sender, srcPipeAdv);
                            }
                    }
            } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e, ".: e-ScienceNet :.", JOptionPane.ERROR_MESSAGE);
            }
            String cmd = popString(msg, COMMAND);
            if (cmd == null) {
                    return;
            }
            if (cmd.equals(PING) && (group != null)) {
                    OutputPipe op = null;
                    HashSet set = new HashSet(1);
                    set.add(srcPeerAdv.getPeerID());
                    try {
                            op = group.getPipeService().createOutputPipe(srcPipeAdv, set,
                                            PipeTimeout);
                            if (op != null) {
                                    Message rep = new Message();
                                    rep.addMessageElement(null, new StringMessageElement(
                                                    COMMAND, ACK, null));
                                    rep.addMessageElement(null, new StringMessageElement(
                                                    GROUPID, groupId, null));
                                    rep.addMessageElement(null, new StringMessageElement(
                                                    SENDERNAME, myName, null));
                                    op.send(rep);
                            }
                    } catch (Exception ez1) {

                    }
            }
            if (cmd.equals(ACK) && (group != null)) {
                    PipePresence p = getPipePresence(group, myPipeAdvt);
                    if (p != null) {
                            p.processAck(sender);
                    }
            }
    }
    public void presenceEvent(PipePresence.PresenceEvent event) {
            if (event.getName().equals(myName) && !event.getStatus()) {
                    return;
            }
            if (pipePListener != null) {
                    for (int i = 0; i < pipePListener.size(); i++) {
                            Listener l = (Listener) pipePListener.elementAt(i);
                            l.userEvent(event);
                    }
            }
    }
    public void refreshNotify(Enumeration en) {
            if (pipePListener != null) {
                    for (int i = 0; i < pipePListener.size(); i++) {
                            Listener l = (Listener) pipePListener.elementAt(i);
                            l.refreshEvent(en);
                    }
            }
    }
    private static String popString(Message message, String name) {
            MessageElement el = message.getMessageElement(null, name);
            if (el != null) {
                    message.removeMessageElement(el);
                    return el.toString();
            }
            return null;
    }
}
