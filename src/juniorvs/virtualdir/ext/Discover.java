
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
 *  $Id: Discover.java,v 1.4 2003/02/08 19:36:50 hamada Exp $
 *
 */

package juniorvs.virtualdir.ext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import juniorvs.virtualdir.EventoDescoberta;
import juniorvs.virtualdir.OuvinteDescoberta;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.Advertisement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PipeAdvertisement;

/**
 * Classe responsável pelas descobertas na rede
 * 
 * @author Sun Microsystems
 */
public abstract class Discover implements DiscoveryListener {
    
    protected final static int THRESHOLD = 50;
    private int adType = -1;
    private String attribute = null;
    protected DiscoveryService discover = null;
    private Vector listeners = new Vector();
    private String value = null;
    public Discover(PeerGroup group, int adType) {
            this(group, adType, null, null);
    }
    public Discover(PeerGroup group, int adType, String attr, String value) {

            this.adType = adType;
            this.attribute = attr;
            this.value = value;
            groupChanged(group);
    }

    protected synchronized void addAdToList(Advertisement adv, List list) {
            int index = -1;
            boolean flag = false;

            if (index != -1) {
                    list.remove(index);
            }
            list.add(adv);
    }
    public void addListener(OuvinteDescoberta listener) {
            listeners.addElement(listener);
    }
    public void discoveryEvent(DiscoveryEvent event) {
            DiscoveryResponseMsg res = event.getResponse();
            Enumeration en;
            Advertisement adv = null;
            boolean exception = false;
            ArrayList result = new ArrayList();

            if (res.getDiscoveryType() == adType) {
                    en = res.getAdvertisements();
                    if (en == null || !en.hasMoreElements()) {
                            return;
                    }
                    while (en.hasMoreElements()) {
                            try {
                                    adv = (Advertisement) en.nextElement();
                                    if (isInvalidPipeAdv(adv)) {
                                            continue;
                                    }
                                    // adv is good add it
                                    addAdToList(adv, result);
                            } catch (Exception ex) {
                                    ex.printStackTrace();
                                    exception = true;
                            }
                    }
                    if (result != null) {
                            processAdDiscovered(new EventoDescoberta(this, EventoDescoberta.ANUNCIO_ADICIONADO, result));
                    }
            }
    }
    public Enumeration getAdvertisements() {
            try {
                    return discover.getLocalAdvertisements(adType, attribute, value);
            } catch (IOException e) {
                    return null;
            }
    }
    public void groupChanged(PeerGroup group) {
            if (group != null) {
                    if (discover != null) {
                            discover.removeDiscoveryListener(this);
                    }
                    discover = group.getDiscoveryService();
                    discover.addDiscoveryListener(this);
            }
            try {

                    processAdDiscovered(new EventoDescoberta(this, EventoDescoberta.TODOS_ANUNCIOS_DELETADOS, (List) null));
                    searchAdvertisements(attribute, value);
            } catch (Exception iox) {
                    System.out.println(iox.getMessage());
            }
    }

    private boolean isInvalidPipeAdv(Advertisement adv) {

            if (adType == DiscoveryService.ADV) {
                    if (adv instanceof PipeAdvertisement) {
                            PipeAdvertisement pipeAdv = (PipeAdvertisement) adv;
                            return pipeAdv.getName().startsWith(value);
                    }
            }
            return false;
    }
    protected void processAdDiscovered(EventoDescoberta event) {
            for (int i = 0; i < listeners.size(); i++) {
                    ((OuvinteDescoberta) listeners.elementAt(i)).eventoDescoberta(event);
            }
    }
    public void publishAdvertisement(Advertisement adv) throws IOException {
            discover.publish(adv);
    }
    public void removeListener(OuvinteDescoberta listener) {
            listeners.removeElement(listener);
    }
    public void searchAdvertisements() throws IOException {
            searchAdvertisements(attribute, value);
    }
    public void searchAdvertisements(String attr, String value) throws IOException {
            discover.getRemoteAdvertisements(null, adType, attr, value, THRESHOLD, null);

            Enumeration en = discover.getLocalAdvertisements(adType, attr, value);

            ArrayList result = new ArrayList();

            while (en != null && en.hasMoreElements()) {
                    Advertisement adv = (Advertisement) en.nextElement();
                    addAdToList(adv, result);
            }
            processAdDiscovered(new EventoDescoberta(this, EventoDescoberta.ANUNCIO_ADICIONADO, result));
    }
    protected abstract boolean validAdvertisement(Advertisement adv);

}
