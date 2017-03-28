
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
 *  =========================================================
 *
 *  This software consists of voluntary contributions made by many
 *  individuals on behalf of Project JXTA.  For more
 *  information on Project JXTA, please see
 *  <http://www.jxta.org/>.
 *
 *  This license is based on the BSD license adopted by the Apache Foundation.
 *
 *  $Id: PeerGroupManager.java,v 1.69 2004/01/13 22:57:48 hamada Exp $
 *
 */
package juniorvs.virtualdir.ext;

import java.net.MalformedURLException;
import java.net.UnknownServiceException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import juniorvs.virtualdir.EventoDescoberta;
import juniorvs.virtualdir.OuvinteDescoberta;
import juniorvs.virtualdir.OuvinteEstruturaGrupo;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.impl.membership.PasswdMembershipService;
import net.jxta.impl.peergroup.StdPeerGroupParamAdv;
import net.jxta.instantp2p.util.PreferenceReader;
import net.jxta.instantp2p.util.WorkerThread;
import net.jxta.membership.MembershipService;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupID;
import net.jxta.platform.ModuleSpecID;
import net.jxta.protocol.ModuleImplAdvertisement;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PeerGroupAdvertisement;
import net.jxta.rendezvous.RendezVousService;

/**
 * Classe responsável pelo gerenciamento de grupos
 * 
 * @author Sun Microsystems
 */
public class PeerGroupManager implements OuvinteDescoberta {
    
    protected static WorkerThread worker = null;
    private final static String JOINED = ".JoinedGroups";
    private final static String JOINEDRDV = ".JoinedRdvGroups";
    private final static String SELECTED_PEER = ".SelectedPeer";
    private final static String SELECTED_PEER_GROUP = ".SelectedPeerGroup";
    private final static String EOL = System.getProperty("line.separator");
    private Vector myGroups = new Vector(10);
    private Vector myGroupAdvs = new Vector(10);
    private Vector myGroupRdv = new Vector(10);
    private Vector listeners = new Vector();
    private Vector structureListeners = new Vector();
    private PeerGroup currentGroup = null;
    private PeerAdvertisement currentPeer = null;
    private Discover peerDiscover;
    private Discover groupDiscover;
    public PeerGroup rootPeerGroup;
    private boolean blockRendezVousWait = false;
    
    public PeerGroupManager(PeerGroup group, PeerGroup parentPeerGroup) {
            currentGroup = group;            
            rootPeerGroup = parentPeerGroup;
           
            myGroups.addElement(group);
            myGroupAdvs.addElement(group.getPeerGroupAdvertisement());
            myGroupRdv.addElement(new Boolean(false));

            peerDiscover = new InternalDiscover(group, DiscoveryService.PEER);
            groupDiscover = new InternalDiscover(group, DiscoveryService.GROUP);
            peerDiscover.addListener(this);
            groupDiscover.addListener(this);
    }
    
    public void setRootPeerGroup(PeerGroup peerGroup){
        this.rootPeerGroup = peerGroup;
    }
    
    public Enumeration getPeerList() {
            return peerDiscover.getAdvertisements();
    }
    
    public Enumeration getJoinedGroups() {
            return myGroupAdvs.elements();
    }

    public Enumeration getGroups() {
            Enumeration en = groupDiscover.getAdvertisements();
            Vector valid = new Vector(10);

            while (en != null && en.hasMoreElements()) {
                    PeerGroupAdvertisement adv = (PeerGroupAdvertisement) en.nextElement();

                    String nm = adv.getName();
                    if (nm == null)
                            nm = "";
                    if (nm.indexOf("JxtaWire") == -1)
                            valid.addElement(adv);
            }

            return valid.elements();
    }
    public Enumeration getGroupsDelta() {
            Vector delta = new Vector();
            Enumeration all = getGroups();

            while (all.hasMoreElements()) {
                    PeerGroupAdvertisement adv = (PeerGroupAdvertisement) all.nextElement();

                    if (-1 == getIndexOfAdv(myGroupAdvs, adv))
                            delta.add(adv);
            }
            return delta.elements();
    }
    public PeerAdvertisement getSelectedPeer() {
            return currentPeer;
    }
    public PeerGroup getSelectedPeerGroup() {

            if (!myGroups.contains(currentGroup))
                    throw new RuntimeException("falha na seleção");
            return currentGroup;
    }
    public String getMyPeerName() {
            return rootPeerGroup.getPeerName();
    }
    public WorkerThread getWorkerThread() {
            if (worker == null) {
                    worker = new WorkerThread();
                    worker.setPriority(worker.getPriority() - 1);
            }
            return worker;
    }
    public void eventoDescoberta(EventoDescoberta event) {
            processPeerStructureChanged(event);
    }
    public void searchGroups(String groupName) {
            try {
                    if (groupName == null || groupName.trim().equals("")) {
                            groupDiscover.searchAdvertisements(null, null);
                    } else {
                            groupDiscover.searchAdvertisements("Name", groupName);
                    }
            } catch (Exception iox) {
                    iox.printStackTrace();
            }
    }
    public void searchPeers(String peerName) {
            try {
                    if (peerName == null || peerName.trim().equals("")) {
                            peerDiscover.searchAdvertisements(null, null);
                    } else {
                            peerDiscover.searchAdvertisements("PeerName", peerName);
                    }
            } catch (Exception iox) {
                    iox.printStackTrace();
            }
    }
    public void addStructureListener(OuvinteEstruturaGrupo listener) {
            structureListeners.addElement(listener);
    }
    public void removeStructureListener(OuvinteEstruturaGrupo listener) {
            structureListeners.removeElement(listener);
    }
    public void joinGroup(PeerGroup pg, PeerGroupAdvertisement pgAdv, boolean isRendezVous) throws Exception {
            isRendezVous = true;
            try {

                    myGroups.addElement(pg);
                    myGroupAdvs.addElement(pgAdv);
                    myGroupRdv.addElement(new Boolean(isRendezVous));

                    updateJoinedGroups();

            } catch (Exception iox) {
                    iox.printStackTrace();
            }
    }

    public void createPeerGroup(String name, boolean isRendezVous, String password) {
            try {

                    ModuleImplAdvertisement newGroupImpl = rootPeerGroup.getAllPurposePeerGroupImplAdvertisement();

                    if (null != password) {
                            StdPeerGroupParamAdv params = new StdPeerGroupParamAdv(newGroupImpl.getParam());

                            boolean replacedMembership = false;
                            Hashtable services = params.getServices();

                            for (Enumeration eachModule = services.keys(); eachModule.hasMoreElements();) {

                                    Object aModuleClassID = eachModule.nextElement();

                                    if (aModuleClassID.equals(PeerGroup.membershipClassID)) {
                                            ModuleImplAdvertisement aModuleAdv = (ModuleImplAdvertisement) services.get(aModuleClassID);
                                            services.remove(aModuleClassID);

                                            ModuleImplAdvertisement implAdv =
                                                    (ModuleImplAdvertisement) AdvertisementFactory.newAdvertisement(
                                                            ModuleImplAdvertisement.getAdvertisementType());

                                            implAdv.setModuleSpecID(PasswdMembershipService.passwordMembershipSpecID);
                                            implAdv.setCompat(aModuleAdv.getCompat());
                                            implAdv.setCode(PasswdMembershipService.class.getName());
                                            implAdv.setUri(aModuleAdv.getUri());
                                            implAdv.setProvider(aModuleAdv.getProvider());
                                            implAdv.setDescription("Password Membership Service");

                                            services.put(PeerGroup.membershipClassID, implAdv);

                                            newGroupImpl.setParam((Element) params.getDocument(new MimeMediaType("text/xml")));

                                            replacedMembership = true;
                                            break;
                                    }
                            }

                            if (replacedMembership) {
                                    newGroupImpl.setParam((Element) params.getDocument(new MimeMediaType("text/xml")));

                                    if (!newGroupImpl.getModuleSpecID().equals(PeerGroup.allPurposePeerGroupSpecID)) {
                                            newGroupImpl.setModuleSpecID(
                                                    IDFactory.newModuleSpecID(newGroupImpl.getModuleSpecID().getBaseClass()));
                                    } else {
                                            ID passwdGrpModSpecID = ID.nullID;

                                            try {
                                                    passwdGrpModSpecID =
                                                            IDFactory.fromURL(
                                                                    IDFactory.jxtaURL(
                                                                            "urn",
                                                                            "",
                                                                            "jxta:uuid-" + "DeadBeefDeafBabaFeedBabe00000001" + "04" + "06"));
                                            } catch (MalformedURLException absurd) {
                                                    // Fall through.
                                            } catch (UnknownServiceException absurd2) {
                                                    // Fall through.
                                            }

                                            newGroupImpl.setModuleSpecID((ModuleSpecID) passwdGrpModSpecID);
                                    }
                            }
                    }

                    rootPeerGroup.getDiscoveryService().publish(
                            newGroupImpl,
                            PeerGroup.DEFAULT_LIFETIME,
                            PeerGroup.DEFAULT_EXPIRATION);

                    PeerGroupAdvertisement newPGAdv =
                            (PeerGroupAdvertisement) AdvertisementFactory.newAdvertisement(
                                    PeerGroupAdvertisement.getAdvertisementType());

                    newPGAdv.setPeerGroupID(IDFactory.newPeerGroupID());
                    newPGAdv.setModuleSpecID(newGroupImpl.getModuleSpecID());
                    newPGAdv.setName(name);
                    newPGAdv.setDescription("Created by myJXTA");
                    if (null != password) {
                            StructuredTextDocument params =
                                    (StructuredTextDocument) StructuredDocumentFactory.newStructuredDocument(
                                            new MimeMediaType("text/xml"),
                                            "Parm");

                            TextElement param = params.createElement("Parm");
                            params.appendChild(param);

                            String passwrdlist = "myjxtauser:" + PasswdMembershipService.makePsswd(password) + ":";

                            TextElement login = params.createElement("login", passwrdlist);
                            param.appendChild(login);

                            newPGAdv.putServiceParam(PeerGroup.membershipClassID, param);
                    }

                    rootPeerGroup.getDiscoveryService().publish(
                            newPGAdv,
                            PeerGroup.DEFAULT_LIFETIME,
                            PeerGroup.DEFAULT_EXPIRATION);

            } catch (Throwable pge) {
                    pge.printStackTrace();
            }
    }
    public synchronized void leaveGroup(PeerGroupAdvertisement group) throws Exception {
            if (null == group)
                    return;
            if (PeerGroupID.defaultNetPeerGroupID.equals(group.getPeerGroupID())) {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(EOL);
                    throw new RuntimeException(buffer.toString());
            }

            int index = getIndexOfAdv(myGroupAdvs, group);
            if (index == -1) {
                    return;
            }

            PeerGroup theGroup = (PeerGroup) myGroups.elementAt(index);
            myGroups.removeElementAt(index);
            myGroupAdvs.removeElementAt(index);
            myGroupRdv.removeElementAt(index);

            MembershipService membership = theGroup.getMembershipService();
            membership.resign();
            theGroup.stopApp();

            updateJoinedGroups();

    }
    public void savePreferences() {
            PreferenceReader.getInstance().save();
    }
    public boolean isConnectedToRendezvous() throws Exception {
            RendezVousService rdv = currentGroup.getRendezVousService();
            if (rdv.isConnectedToRendezVous() || currentGroup.isRendezvous()) {
                    return true;
            }
            return rdv.isConnectedToRendezVous();
    }
    public void unblockWaitForRendezVous() {
            blockRendezVousWait = false;
    }
    private int getIndexOfAdv(Vector vector, PeerGroupAdvertisement pgAdv) {
            ID mustMatch = pgAdv.getPeerGroupID();

            for (int i = 0; i < vector.size(); i++) {
                    PeerGroupAdvertisement targetAdv = (PeerGroupAdvertisement) vector.elementAt(i);

                    if (mustMatch.equals(targetAdv.getPeerGroupID())) {
                            return i;
                    }
            }
            return -1;
    }
    private void processPeerStructureChanged(EventoDescoberta e) {
            for (int i = 0; i < structureListeners.size(); i++) {
                    OuvinteEstruturaGrupo l = (OuvinteEstruturaGrupo) structureListeners.elementAt(i);
            }
    }
    private void updateJoinedGroups() {
            Enumeration en = myGroupAdvs.elements();
            StringBuffer buffer = new StringBuffer();
            StringBuffer bufferRdv = new StringBuffer();
            String id;
            PreferenceReader prefs = PreferenceReader.getInstance();

            if (en.hasMoreElements()) {
                    en.nextElement();
            }
            int index = 1;
            while (en.hasMoreElements()) {
                    id = ((PeerGroupAdvertisement) en.nextElement()).getPeerGroupID().toString();
                    if (((Boolean) myGroupRdv.elementAt(index)).booleanValue())
                            bufferRdv.append(id);
                    else
                            buffer.append(id);

                    if (en.hasMoreElements()) {
                            buffer.append(",");
                            bufferRdv.append(",");
                    }
            }
            prefs.setProperty(getClass().getName() + JOINED, buffer.toString());
            prefs.setProperty(getClass().getName() + JOINEDRDV, bufferRdv.toString());
    }

    private static class InternalDiscover extends Discover {
            private int type;
            public InternalDiscover(PeerGroup group, int type) {
                    super(group, type);
            }
            protected boolean validAdvertisement(Advertisement adv) {

                    boolean fl = (adv instanceof PeerGroupAdvertisement);
                    return true;
            }
            protected boolean compareAds(Advertisement ad1, Advertisement ad2) {
                    boolean result = false;

                    if (type == DiscoveryService.PEER) {
                            if (ad1 != null && ad2 != null) {
                                    PeerAdvertisement p1;
                                    PeerAdvertisement p2;

                                    try {
                                            p1 = (PeerAdvertisement) ad1;
                                            p2 = (PeerAdvertisement) ad2;
                                            result = p1.getPeerID().equals(p2.getPeerID());
                                    } catch (ClassCastException iox) {
                                            result = false;
                                    }
                            }
                    } else {
                            if (ad1 != null && ad2 != null) {
                                    PeerGroupAdvertisement p1;
                                    PeerGroupAdvertisement p2;

                                    try {
                                            p1 = (PeerGroupAdvertisement) ad1;
                                            p2 = (PeerGroupAdvertisement) ad2;
                                            result = p1.getPeerGroupID().equals(p2.getPeerGroupID());
                                    } catch (ClassCastException iox) {
                                            result = false;
                                    }
                            }
                    }
                    return result;
            }
    }
}
