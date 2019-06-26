/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.integrationservices.config;

/**
 * Encapsulates the extension configuration properties
 */
public class DefaultIntegrationServicesConfiguration extends BaseIntegrationServicesConfiguration implements IntegrationServicesConfiguration
{
	private static final String SAP_PASSPORT_SYSTEM_ID_PROPERTY_KEY = "integrationservices.sap.passport.systemid";
	private static final String DEFAULT_SAP_PASSPORT_SYSTEM_ID = "SAP Commerce";

	private static final String SAP_PASSPORT_SERVICE_PROPERTY_KEY = "integrationservices.sap.passport.service";
	private static final int DEFAULT_SAP_PASSPORT_SERVICE = 39;

	private static final String SAP_PASSPORT_USER_PROPERTY_KEY = "integrationservices.sap.passport.user";
	private static final String DEFAULT_SAP_PASSPORT_USER = "";

	private static final String MEDIA_PERSISTENCE_MEDIA_NAME_PREFIX_PROPERTY_KEY = "integrationservices.media.persistence.media.name.prefix";
	private static final String DEFAULT_PREFIX_VALUE = "Payload_";

	@Override
	public String getMediaPersistenceMediaNamePrefix()
	{
		return getStringProperty(MEDIA_PERSISTENCE_MEDIA_NAME_PREFIX_PROPERTY_KEY, DEFAULT_PREFIX_VALUE);
	}

	@Override
	public String getSapPassportSystemId()
	{
		return getStringProperty(SAP_PASSPORT_SYSTEM_ID_PROPERTY_KEY, DEFAULT_SAP_PASSPORT_SYSTEM_ID);
	}

	@Override
	public int getSapPassportServiceValue()
	{
		return getIntegerProperty(SAP_PASSPORT_SERVICE_PROPERTY_KEY, DEFAULT_SAP_PASSPORT_SERVICE);
	}

	@Override
	public String getSapPassportUser()
	{
		return getStringProperty(SAP_PASSPORT_USER_PROPERTY_KEY, DEFAULT_SAP_PASSPORT_USER);
	}
}
