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
package de.hybris.platform.sap.sapproductconfigsombol.transaction.util.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.external.Configuration;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import java.util.Date;


/**
 * This is mock implementation of the ConfigurationService
 */
public class MockConfigurationService implements ProductConfigurationService
{

	@Override
	public ConfigModel createDefaultConfiguration(final KBKey kbKey)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigModel createConfigurationForVariant(final String baseProductCode, final String variantProductCode)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateConfiguration(final ConfigModel model)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public ConfigModel retrieveConfigurationModel(final String configId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String retrieveExternalConfiguration(final String configId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigModel createConfigurationFromExternal(final KBKey kbKey, final String externalConfiguration)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigModel createConfigurationFromExternalSource(final Configuration extConfig)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releaseSession(final String configId)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int calculateNumberOfIncompleteCsticsAndSolvableConflicts(final String configId)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasKbForDate(final String productCode, final Date kbDate)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasKbForVersion(final KBKey kbKey, final String externalConfig)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getTotalNumberOfIssues(final ConfigModel configModel)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isKbVersionValid(final KBKey kbKey)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public KBKey extractKbKey(final String productCode, final String externalConfig)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
