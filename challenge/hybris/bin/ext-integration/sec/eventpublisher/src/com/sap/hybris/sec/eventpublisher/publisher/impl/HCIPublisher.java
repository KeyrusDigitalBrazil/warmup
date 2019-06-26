package com.sap.hybris.sec.eventpublisher.publisher.impl;

import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.ADDRESS;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.ADDRESS_PATH;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.CUSTOMER;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.CUSTOMER_PATH;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.ORDER;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.ORDER_PATH;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.RETURN_REQUEST;
import static com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants.RETURN_REQUEST_PATH;

import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import com.sap.hybris.sec.eventpublisher.constants.EventpublisherConstants;
import com.sap.hybris.sec.eventpublisher.data.ResponseData;
import com.sap.hybris.sec.eventpublisher.httpconnection.SECHttpConnection;
import com.sap.hybris.sec.eventpublisher.publisher.Publisher;

import de.hybris.platform.servicelayer.config.ConfigurationService;


public class HCIPublisher implements Publisher
{
	
	private ConfigurationService configurationService;
	private SECHttpConnection secHttpConnection;

	
	public ResponseData publishJson(String json, String publishedItemType) throws IOException{
		
		return secHttpConnection.sendPost(getPathUrl(publishedItemType), json);
	}


	private String getPathUrl(String publishedItemName) {
		String projectPath = getConfigurationService().getConfiguration()
				.getString(EventpublisherConstants.HCI_PROJECT_PATH);
		return projectPath + getItemPath(publishedItemName);
	}
	
	protected String getItemPath(String publishedItemName) {
		Configuration config = getConfigurationService().getConfiguration();
		switch (publishedItemName) {
		case CUSTOMER:
			return config.getString(CUSTOMER_PATH);
		case ADDRESS:
			return config.getString(ADDRESS_PATH);
		case ORDER:
			return config.getString(ORDER_PATH);
		case RETURN_REQUEST:
			return config.getString(RETURN_REQUEST_PATH);
		default:
			return publishedItemName;
		}
	}


	/**
	 * @return the configurationService
	 */
	public ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	/**
	 * @return the secHttpConnection
	 */
	public SECHttpConnection getSecHttpConnection()
	{
		return secHttpConnection;
	}

	/**
	 * @param secHttpConnection
	 *           the secHttpConnection to set
	 */
	public void setSecHttpConnection(final SECHttpConnection secHttpConnection)
	{
		this.secHttpConnection = secHttpConnection;
	}



}
