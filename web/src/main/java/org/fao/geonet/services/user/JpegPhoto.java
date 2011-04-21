package org.fao.geonet.services.user;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.Iterator;

import jeeves.constants.Jeeves;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.jdom.Element;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPJSSESecureSocketFactory;
import com.novell.ldap.LDAPSearchResults;

public class JpegPhoto implements Service {

    private String sldapHost;
    private String sldapPort;
    private String sldapBase;
    private String sldapDn;
    private String sldapPwd;

    private boolean isDisabled = true ;

    private int ildapPort ;

    public Element exec(Element params, ServiceContext context) throws Exception {

        Element response = new Element("img").setAttribute("src", "/geonetwork/images/jpegphoto-na.jpg");
        response.setAttribute("alt", "jpegPhoto image");
        String uidParam =  params.getChildText("uid");
        
        String filePath = context.getAppPath() + "/images/jpegphoto-" + uidParam + ".jpg";
        
        File fJpegPhoto = new File(filePath);
        
        if (fJpegPhoto.exists()) {
            response.setAttribute("src", "/geonetwork/images/jpegphoto-" + uidParam + ".jpg");
            return response;   
        }
        
        
        
        if (isDisabled == false)
        {
            String slapAtt[] = {"jpegPhoto"} ;

            String userUidFilter = "(uid=" +  uidParam + ")";
            
            LDAPConnection lc = null;    
            try 
            {
                if ((ildapPort == 636) || (ildapPort == 10636)) {
                    lc = new LDAPConnection(new LDAPJSSESecureSocketFactory());         
                } else {
                    lc = new LDAPConnection();
                }

                lc.connect(sldapHost, ildapPort);

                lc.bind(LDAPConnection.LDAP_V3, sldapDn, sldapPwd.getBytes());

                LDAPSearchResults searchResults = lc.search(sldapBase,
                        lc.SCOPE_SUB, userUidFilter, slapAtt, false);

                while (searchResults.hasMore()) {
                    LDAPEntry nextEntry = searchResults.next();
                    LDAPAttributeSet attributeSet = nextEntry.getAttributeSet();

                    Iterator allAttributes = attributeSet.iterator();

                    while (allAttributes.hasNext()) {
                        LDAPAttribute attribute = (LDAPAttribute) allAttributes.next();
                        String attributeName = attribute.getName();
                        Enumeration allValues = attribute.getByteValues();

                        if (attributeName.equals("jpegPhoto")) {
                            while (allValues.hasMoreElements()) {
                                byte[]  curVal = (byte[]) allValues.nextElement();
                                FileOutputStream fos = new FileOutputStream(filePath);
                                fos.write(curVal);
                                fos.close();
                                response.setAttribute("src","/geonetwork/images/jpegphoto-" + uidParam + ".jpg");
                                break;
                            }
                        }
                        // get only the first jpegPhoto
                        break;
                    }
                    // get only the first result
                    break;
                } // end search result

            } catch (Exception e)
            {
                throw e ;
            } finally
            {
                if (lc != null && lc.isConnected()) {
                    lc.disconnect();
                }
            }
        }

        return response;
    }

    public void init(String appPath, ServiceConfig params) throws Exception {
        // TODO Auto-generated method stub
        try 
        {
            sldapHost = params.getValue("LDAPhost");
            sldapPort = params.getValue("LDAPport");
            sldapBase = params.getValue("LDAPbase");
            sldapDn   = params.getValue("LDAPbindDn");
            sldapPwd  = params.getValue("LDAPbindPassword"); 

            LDAPConnection lc ;

            try 
            {
                ildapPort = Integer.parseInt(sldapPort);
            } catch (NumberFormatException nfe)
            {
                ildapPort = LDAPConnection.DEFAULT_PORT;
            }
            if (ildapPort == 636)
            {
                lc = new LDAPConnection(new LDAPJSSESecureSocketFactory());                         
            }
            else // (ildapPort == 389) or else, assume it's not a SSL connection
            {
                lc = new LDAPConnection();
            }

            lc.connect(sldapHost, ildapPort);

            lc.bind(LDAPConnection.LDAP_V3, sldapDn, sldapPwd.getBytes());

            lc.disconnect();

            isDisabled = false;


        }
        catch (Exception e)
        {
            isDisabled = true;
        }

    }

}
