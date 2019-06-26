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

import java.util.HashMap;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.conn.jco.JCo;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

import de.hybris.platform.sap.core.configuration.rfc.RFCDestination;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestinationConstants;
import de.hybris.platform.sap.core.configuration.rfc.RFCDestinationService;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationEvent;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationJCoTraceEvent;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationPingEvent;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationRemoveEvent;
import de.hybris.platform.sap.core.configuration.rfc.event.SAPRFCDestinationUpdateEvent;
import de.hybris.platform.sap.core.constants.SapcoreConstants;
import de.hybris.platform.sap.core.jco.service.SAPRFCDestinationService;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import de.hybris.platform.servicelayer.tenant.TenantService;
import de.hybris.platform.util.localization.Localization;


/**
 * Default implementation for the {@link SAPRFCDestinationService}.
 */
public class DefaultSAPRFCDestinationService extends AbstractEventListener<SAPRFCDestinationEvent>
		implements SAPRFCDestinationService
{

	private static final Logger LOG = Logger.getLogger(DefaultSAPRFCDestinationService.class);
	private static final String SNC_LIB = "SNC_LIB";

	private RFCDestinationService rfcDestinationService;
	private TenantService tenantService;

	private static DestinationDataProvider destinationDataProvider = null;
	private static AtomicInteger providerUsageCount = new AtomicInteger(0);

	private static String sncLibraryPath = null;
	private static String rfcDefaultLanguage = "en";

	/**
	 * Returns the RFC default language.
	 * 
	 * @return RFC default language
	 */
	public String getRfcDefaultLanguage()
	{
		return rfcDefaultLanguage;
	}

	/**
	 * Sets the RFC default language.
	 * 
	 * @param rfcDefaultLanguage
	 *           RFC default language
	 */
	@Required
	public void setRfcDefaultLanguage(final String rfcDefaultLanguage)
	{
		if (rfcDefaultLanguage != null && !rfcDefaultLanguage.isEmpty() && !rfcDefaultLanguage.startsWith("$"))
		{
			LOG.debug("The default RFC language is: " + rfcDefaultLanguage);
			DefaultSAPRFCDestinationService.rfcDefaultLanguage = rfcDefaultLanguage;
		}
	}

	/**
	 * Returns the SNC library path.
	 * 
	 * @return SNC library path
	 */
	@SuppressWarnings("static-access")
	public String getSncLibraryPath()
	{
		if (this.sncLibraryPath == null || this.sncLibraryPath.isEmpty())
		{
			// try environment variable
			this.sncLibraryPath = System.getenv(SNC_LIB);
		}

		return sncLibraryPath;
	}

	/**
	 * Sets the SNC library path.
	 * 
	 * @param sncLibraryPath
	 *           SNC library path
	 */
	@SuppressWarnings("static-access")
	public void setSncLibraryPath(final String sncLibraryPath)
	{
		if (sncLibraryPath != null && !sncLibraryPath.isEmpty() && !sncLibraryPath.startsWith("$"))
		{
			LOG.debug("The path to SNC library is: " + sncLibraryPath);
			this.sncLibraryPath = sncLibraryPath;
		}

	}

	/**
	 * Injection setter for {@link TenantService}.
	 * 
	 * @param tenantService
	 *           {@link TenantService} to set
	 */
	@Required
	@Override
	public void setTenantService(final TenantService tenantService)
	{
		super.setTenantService(tenantService);
		this.tenantService = tenantService;
	}

	/**
	 * Injection setter for {@link RFCDestinationService}.
	 * 
	 * @param rfcDestinationService
	 *           {@link RFCDestinationService}
	 * 
	 */
	@Required
	public void setRfcDestinationService(final RFCDestinationService rfcDestinationService)
	{
		this.rfcDestinationService = rfcDestinationService;
	}

	/**
	 * Initialization method called by the Spring framework.
	 */
	public void init()
	{
		if (!"junit".equals(tenantService.getCurrentTenantId()))
		{
			LOG.debug("DestinationDataProvider gets registered.");
			registerDestinationDataProvider(this);
		}
	}

	/**
	 * Destroy method called by the Spring framework.
	 */
	public void destroy()
	{
		if (!"junit".equals(tenantService.getCurrentTenantId()))
		{
			LOG.debug("DestinationDataProvider gets destroyed.");
			unregisterDestinationDataProvider();
		}
	}

	@Override
	public RFCDestination getRFCDestination(final String jcoDestinationName)
	{
		LOG.debug("RFC Destination " + jcoDestinationName + " will be read.");
		return rfcDestinationService.getRFCDestination(jcoDestinationName);
	}

	/**
	 * Unregisters the destination data provider.
	 */
	private static synchronized void unregisterDestinationDataProvider()
	{
		if (destinationDataProvider != null && providerUsageCount.decrementAndGet() <= 0)
		{
			try
			{
				Environment.unregisterDestinationDataProvider(destinationDataProvider);
				destinationDataProvider = null;
			}
			catch (final IllegalStateException e)
			{
				LOG.error("SAP JCoDestinationData exists in service, but is not registered", e);
			}
		}
	}

	/**
	 * Registers the destination data provider.
	 * 
	 * @param sapRFCDestinationService
	 *           {@link SAPRFCDestinationService}
	 */
	private static synchronized void registerDestinationDataProvider(final SAPRFCDestinationService sapRFCDestinationService) // NOPMD
	{
		if (destinationDataProvider != null)
		{
			LOG.debug("Provider exists in DefaultSAPRFCDestinationService");
			providerUsageCount.incrementAndGet();
		}
		else
		{
			if (Environment.isDestinationDataProviderRegistered())
			{
				LOG.warn("SAP JCoDestinationData provider already registered");
			}
			else
			{
			    destinationDataProvider = new DestinationDataProviderImpl(sapRFCDestinationService,rfcDefaultLanguage, (StringUtils.isNotEmpty(sncLibraryPath) ? sncLibraryPath : System.getenv(SNC_LIB)));
				Environment.registerDestinationDataProvider(destinationDataProvider);
				providerUsageCount.incrementAndGet();
			}
		}
	}

	@Override
	protected void onEvent(final SAPRFCDestinationEvent event)
	{
		if (event instanceof SAPRFCDestinationRemoveEvent)
		{
			LOG.debug("Delete destination data event received.");
			DestinationDataEventListener destinationDataEventListener = ((DestinationDataProviderImpl)destinationDataProvider).getDestinationDataEventListener();
			destinationDataEventListener.deleted(event.getSource().toString());
		}
		else if (event instanceof SAPRFCDestinationUpdateEvent)
		{
			LOG.debug("Update destination data event received.");
			DestinationDataEventListener destinationDataEventListener = ((DestinationDataProviderImpl)destinationDataProvider).getDestinationDataEventListener();
			destinationDataEventListener.updated(event.getSource().toString());
		}
		else if (event instanceof SAPRFCDestinationPingEvent)
		{
			LOG.debug("Ping destination data event received.");
			final Map<String, Object> actionResultMap = pingCurrentDestination(event.getSource().toString());
			((SAPRFCDestinationPingEvent) event).setMessage((String) actionResultMap.get("message"));
			((SAPRFCDestinationPingEvent) event).setResultIndicator((int) actionResultMap.get("result"));
			((SAPRFCDestinationPingEvent) event).setNeedRefresh((boolean) actionResultMap.get("needRefresh"));
			return;
		}
		else if (event instanceof SAPRFCDestinationJCoTraceEvent)
		{
			LOG.debug("JCo trace enable / disable event received.");
			// Turn on / off global JCo Trace
			final int jcoTraceLevel = convertJCoTraceLevel(event.getSource().toString());
			if (jcoTraceLevel >= 0 && jcoTraceLevel < 11)
			{
				String path = null;
				if (((SAPRFCDestinationJCoTraceEvent) event).getJCoTracePath() != null
						&& !((SAPRFCDestinationJCoTraceEvent) event).getJCoTracePath().isEmpty())
				{
					path = ((SAPRFCDestinationJCoTraceEvent) event).getJCoTracePath();
				}
				JCo.setTrace(jcoTraceLevel, path);
			}
		}
	}

	/**
	 * Convert the DDLB value to a "integer".
	 * 
	 * 
	 * @param traceLevel
	 *           the tracelevel as a Constant
	 * @return the Tracelevel as a Integer
	 */
	private int convertJCoTraceLevel(final String traceLevel)
	{
	    int traceLevelAsInt=0;
		switch (traceLevel)
		{
			case RFCDestinationConstants.JCO_TRACE_LEVEL_NO_TRACE:
				traceLevelAsInt=0;
				break;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_ERRORS:
				traceLevelAsInt=1;
				break;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_ERRORS_WARNINGS:
			    traceLevelAsInt=2;
			    break;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_INFOS_ERRORS_WARNINGS:
			    traceLevelAsInt=3;
			    break;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_EXPATH_INFOS_ERRORS_WARNINGS:
			    traceLevelAsInt=4;
			    break;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_VERBEXPATH_INFOS_ERRORS_WARNINGS:
			    traceLevelAsInt=5;
			    break;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_VERBEXPATH_LIMDATADUMPS_INFOS_ERRORS_WARNINGS:
			    traceLevelAsInt=6;
			    break;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_FULLEXPATH_DATADUMPS_VERBINFOS_ERRORS_WARNINGS:
			    traceLevelAsInt=7;
			    break;
			case RFCDestinationConstants.JCO_TRACE_LEVEL_FULLEXPATH_FULLDATADUMPS_VERBINFOS_ERRORS_WARNINGS:
			    traceLevelAsInt=8;
			    break;
			default:
			    traceLevelAsInt=0;
			    break;
		}
		return traceLevelAsInt;
	}

	/**
	 * Pings the requested RFC destination name.
	 * 
	 * @param rfcDestinationName
	 *           RFC destination name
	 * @return action result map
	 */
	private Map<String, Object> pingCurrentDestination(final String rfcDestinationName)
	{
		JCoDestination jcoDestination;
		final Map<String, Object> actionResultMap = new HashMap<String, Object>();
		final String needRefreshString = "needRefresh";
		final String resultString="result";
		

		final StringBuilder buffer = new StringBuilder();
		Properties properties = null;
		try
		{
			// Create and set a SAPHybrisSession --> the SAPJCoSessionReferenceProvider requires a SAPHybrisSession
			buffer.append(Localization.getLocalizedString("ping.RFCDestination.GetDestination", new Object[]
			{ rfcDestinationName })).append(SapcoreConstants.CRLF);

			jcoDestination = JCoDestinationManager.getDestination(rfcDestinationName);
			// save properties for the catch clause
			properties = jcoDestination.getProperties();

			addConnectionData(buffer, jcoDestination);
			buffer.append(Localization.getLocalizedString("ping.RFCDestination.Execute")).append(SapcoreConstants.CRLF);

			jcoDestination.ping();
			buffer.append(Localization.getLocalizedString("ping.RFCDestination.SuccessMessage", new Object[]
			{ rfcDestinationName }));
			actionResultMap.put(resultString, 0);
			actionResultMap.put(needRefreshString, true);
		}
		catch (final JCoException e)
		{
			actionResultMap.put(resultString, 1);
			actionResultMap.put(needRefreshString, false);

			buffer.append(Localization.getLocalizedString("ping.RFCDestination.FailureMessage", new Object[]
			{ rfcDestinationName }));
			buffer.append(SapcoreConstants.CRLF).append(SapcoreConstants.CRLF);
			appendConnectionProperties(buffer, properties);
			buffer.append(Localization.getLocalizedString("ping.RFCDestination.Exception")).append(SapcoreConstants.CRLF);
			buffer.append(e.getLocalizedMessage());
			LOG.error(e);
		}
		catch (Exception ex)
		{
			LOG.error("During destination ping the following Exception occurred: ", ex);
		}
		actionResultMap.put("message", buffer.toString());
		return actionResultMap;
	}

	/**
	 * Adds the core data of the destination to the given StringBuffer.
	 * 
	 * @param buffer
	 *           the StringBuffer that contains the core data afterwards.
	 * @param jcoDestination
	 *           the core data is taken from this destination.
	 */
	private void addConnectionData(final StringBuilder buffer, final JCoDestination jcoDestination)
	{
		buffer.append(Localization.getLocalizedString("ping.RFCDestination.UseData")).append(SapcoreConstants.CRLF);
		final String host = jcoDestination.getApplicationServerHost();
		if (host != null)
		{
			buffer.append("\t").append("Host\t\t\t\t\t\t\t\t").append(host).append(SapcoreConstants.CRLF);
			buffer.append("\t").append("Instance Nr\t\t\t\t").append(jcoDestination.getSystemNumber()).append(SapcoreConstants.CRLF);
		}
		else
		{
			buffer.append("\t").append("SystemID\t\t\t\t\t").append(jcoDestination.getR3Name()).append(SapcoreConstants.CRLF);
			buffer.append("\t").append("Message Server\t").append(jcoDestination.getMessageServerHost())
					.append(SapcoreConstants.CRLF);
			buffer.append("\t").append("Logon Group\t\t\t").append(jcoDestination.getLogonGroup()).append(SapcoreConstants.CRLF);
		}
		buffer.append("\t").append("Client \t\t\t\t\t\t\t").append(jcoDestination.getClient()).append(SapcoreConstants.CRLF);
		buffer.append("\t").append("User\t\t\t\t\t\t\t\t").append(jcoDestination.getUser());
		buffer.append(SapcoreConstants.CRLF).append(SapcoreConstants.CRLF);
	}

	/**
	 * Adds the properties to the given StringBuffer. The jco-client-password is excluded if the properties contain a
	 * such a key.
	 * 
	 * @param buffer
	 *           the StringBuffer that contains the properties afterwards.
	 * @param properties
	 *           the properties that should be appended to the given StringBuffer.
	 */
	private void appendConnectionProperties(final StringBuilder buffer, final Properties properties)
	{
	    if (properties == null)
        {
	        return;//Nothing to append...return immediately
        }
	    buffer.append(Localization.getLocalizedString("ping.RFCDestination.Properties")).append(SapcoreConstants.CRLF);
	    for (final Entry<Object, Object> entry : properties.entrySet())
        {
            if (!"jco.client.passwd".equals(entry.getKey()))
            {
                final String[] key = ((String) entry.getKey()).split("\\.");
                if (key.length > 0)
                {
                    buffer.append(key[key.length - 1]).append("\t\t\t\t").append(entry.getValue()).append(SapcoreConstants.CRLF);
                }
                else
                {
                    buffer.append(entry.getKey()).append("\t\t").append(entry.getValue()).append(SapcoreConstants.CRLF);
                }
            }
        }
	    buffer.append(SapcoreConstants.CRLF);
	}
}
