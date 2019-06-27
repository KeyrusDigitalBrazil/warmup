/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.core.jco.service.impl;

import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

import de.hybris.platform.sap.core.configuration.model.RFCDestinationAttributeModel;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.jco.service.SAPRFCDestinationService;

public class DestinationDataProviderImpl implements DestinationDataProvider
{
    private static final Logger LOG = Logger.getLogger(DestinationDataProviderImpl.class);
    
    private SAPRFCDestinationService sapRFCDestinationService;
    private static DestinationDataEventListener destinationDataEventListener;
    
    private String rfcDefaultLanguage;
    private String sncLibraryPath;
    
    public DestinationDataProviderImpl(SAPRFCDestinationService sapRFCDestinationService,String rfcDefaultLanguage,String sncLibraryPath) {
       super();
       this.sapRFCDestinationService = sapRFCDestinationService;
       this.rfcDefaultLanguage = rfcDefaultLanguage;
       this.sncLibraryPath = sncLibraryPath;
   }
    
    public SAPRFCDestinationService getSapRFCDestinationService() {
       return sapRFCDestinationService;
   }

   public void setSapRFCDestinationService(SAPRFCDestinationService sapRFCDestinationService) {
       this.sapRFCDestinationService = sapRFCDestinationService;
   }

   @Override
    public boolean supportsEvents()
    {
        return true;
    }

    @Override
    public void setDestinationDataEventListener(final DestinationDataEventListener eventListener)
    {
        this.destinationDataEventListener = eventListener;
    }
    
    public DestinationDataEventListener getDestinationDataEventListener(){
        return this.destinationDataEventListener;
    }
    
    public Properties getDestinationPropertiesFromRFCDestination(final String destinationName,final RFCDestination rfcDestination, final Properties destinationProperties)
    {
        // Connection Type related
        setConnectionTypeDestProps(rfcDestination, destinationProperties);

        // General destination properties
        setGeneralDestProps(rfcDestination, destinationProperties);

        if (rfcDestination.getJcoMsServ() != null)
        {
            destinationProperties.setProperty(DestinationDataProvider.JCO_MSSERV, rfcDestination.getJcoMsServ());
        }
        if (rfcDestination.getJcoSAPRouter() != null)
        {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SAPROUTER,
                    rfcDestination.getJcoSAPRouter());
        }

        // Delta Manager related
        setDeltaManagerDestProps(rfcDestination, destinationProperties);

        // Connection pooling related
        setConnectionPoolingDestProps(rfcDestination, destinationProperties);

        // RFC trace related
        setRFCTraceDestProps(rfcDestination, destinationProperties);

        if (rfcDestination.getJcoCPICTrace() != null)
        {
            destinationProperties.setProperty(DestinationDataProvider.JCO_CPIC_TRACE,
                    rfcDestination.getJcoCPICTrace());
        }

        // SNC related
        setSncDestProps(rfcDestination, destinationProperties);

        // Additional Attributes
        setAdditionalAttributesDestProps(destinationName, rfcDestination, destinationProperties);
        return destinationProperties;

    }
    
    @Override
    public Properties getDestinationProperties(final String destinationName) // NOPMD
    {
        final RFCDestination rfcDestination = sapRFCDestinationService.getRFCDestination(destinationName);
        final Properties destinationProperties = new Properties();
        if (rfcDestination != null)
        {
            getDestinationPropertiesFromRFCDestination(destinationName,rfcDestination,destinationProperties);
        }
        return destinationProperties;
    }

    /**
     * Sets the general destination properties.
     * 
     * @param rfcDestination
     *           RFC destination
     * @param destinationProperties
     *           destination properties
     */
    private void setGeneralDestProps(final RFCDestination rfcDestination, final Properties destinationProperties)
    {
        destinationProperties.setProperty(DestinationDataProvider.JCO_CLIENT, rfcDestination.getClient());
        destinationProperties.setProperty(DestinationDataProvider.JCO_USER, rfcDestination.getUserid());
        destinationProperties.setProperty(DestinationDataProvider.JCO_PASSWD, rfcDestination.getPassword());
        destinationProperties.setProperty(DestinationDataProvider.JCO_LANG, this.rfcDefaultLanguage);
        // !!! For RFC Destinations only the configured service user is used. !!!
        // !!! Thus the authentication type is by default 'Configured_user'   !!!
        destinationProperties.setProperty(DestinationDataProvider.JCO_AUTH_TYPE,
                DestinationDataProvider.JCO_AUTH_TYPE_CONFIGURED_USER);
    }

    /**
     * Sets the RFC trace related destination properties.
     * 
     * @param rfcDestination
     *           RFC destination
     * @param destinationProperties
     *           destination properties
     */
    private void setRFCTraceDestProps(final RFCDestination rfcDestination, final Properties destinationProperties)
    {
        if (rfcDestination.isJcoRFCTraceEnabled())
        {
            destinationProperties.setProperty(DestinationDataProvider.JCO_TRACE, "1");
        }
        else
        {
            destinationProperties.setProperty(DestinationDataProvider.JCO_TRACE, "0");
        }
    }

    /**
     * Sets the delta manager related destination properties.
     * 
     * @param rfcDestination
     *           RFC destination
     * @param destinationProperties
     *           destination properties
     */
    private void setDeltaManagerDestProps(final RFCDestination rfcDestination, final Properties destinationProperties)
    {
        if (rfcDestination.getJcoClientDelta() != null)
        {
            if (rfcDestination.getJcoClientDelta().booleanValue())
            {
                destinationProperties.setProperty(DestinationDataProvider.JCO_DELTA, "1");
            }
            else
            {
                destinationProperties.setProperty(DestinationDataProvider.JCO_DELTA, "0");
            }
        }
    }

    /**
     * Sets the given additional attributes as destination properties.
     * 
     * @param destinationName
     *           destination name
     * @param rfcDestination
     *           RFC destination
     * @param destinationProperties
     *           destination properties
     */
    private void setAdditionalAttributesDestProps(final String destinationName, final RFCDestination rfcDestination,
            final Properties destinationProperties)
    {
        if (rfcDestination.getRFCDestinationAttributes() != null
                && !rfcDestination.getRFCDestinationAttributes().isEmpty())
        {
            Iterator<RFCDestinationAttributeModel> iter = rfcDestination.getRFCDestinationAttributes().iterator();
            while (iter.hasNext())
            {
                RFCDestinationAttributeModel rfcDestinationAttributesModel = iter.next();
                String attrName = rfcDestinationAttributesModel.getJcoattr_name();
                String attrValue = rfcDestinationAttributesModel.getJcoattr_value();
                if (attrName != null && attrValue != null)
                {
                    LOG.debug("In RFC Destination " + destinationName
                            + " the following attribute name and value has been set: attribute name = " + attrName
                            + " and attribute value = " + attrValue);
                    destinationProperties.setProperty(attrName, attrValue);
                }
            }
        }
    }

    /**
     * Sets the SNC related destination properties.
     * 
     * @param rfcDestination
     *           RFC destination
     * @param destinationProperties
     *           destination properties
     */
    private void setSncDestProps(final RFCDestination rfcDestination, final Properties destinationProperties)
    {
        if (rfcDestination.isSncEnabled())
        {
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_MODE, "1");
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_PARTNERNAME,
                    rfcDestination.getSncPartnerName());
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_QOP, rfcDestination.getSncQOP());

            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_LIBRARY, sncLibraryPath);
            destinationProperties.setProperty(DestinationDataProvider.JCO_SNC_SSO, "0");

        }
    }

    /**
     * Sets the connection pooling related destination properties.
     * 
     * @param rfcDestination
     *           RFC destination
     * @param destinationProperties
     *           destination properties
     */
    private void setConnectionPoolingDestProps(final RFCDestination rfcDestination,
            final Properties destinationProperties)
    {
        if (rfcDestination.getPooledConnectionMode() != null && rfcDestination.getPooledConnectionMode().booleanValue())
        {
            destinationProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY,
                    String.valueOf(rfcDestination.getPoolSize()));
            destinationProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT,
                    String.valueOf(rfcDestination.getMaxConnections()));
            destinationProperties.setProperty(DestinationDataProvider.JCO_MAX_GET_TIME,
                    String.valueOf(rfcDestination.getMaxWaitTime()));
        }
        else
        {
            // A value of 0 has the effect that there is no connection pooling. !!
            destinationProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, String.valueOf(0));
        }
    }

    /**
     * Sets the connection type related destination properties.
     * 
     * @param rfcDestination
     *           RFC destination
     * @param destinationProperties
     *           destination properties
     */
    private void setConnectionTypeDestProps(final RFCDestination rfcDestination,
            final Properties destinationProperties)
    {
        if (rfcDestination.getConnectionType() != null && rfcDestination.getConnectionType().booleanValue())
        {
            destinationProperties.setProperty(DestinationDataProvider.JCO_ASHOST, rfcDestination.getTargetHost());
            destinationProperties.setProperty(DestinationDataProvider.JCO_SYSNR, rfcDestination.getInstance());
        }
        else
        {
            destinationProperties.setProperty(DestinationDataProvider.JCO_MSHOST, rfcDestination.getMessageServer());
            destinationProperties.setProperty(DestinationDataProvider.JCO_GROUP, rfcDestination.getGroup());
            destinationProperties.setProperty(DestinationDataProvider.JCO_R3NAME, rfcDestination.getSid());
        }
    }
}
