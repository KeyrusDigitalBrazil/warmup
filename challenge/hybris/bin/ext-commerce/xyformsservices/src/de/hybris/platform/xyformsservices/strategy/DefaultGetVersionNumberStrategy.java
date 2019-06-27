/*
* [y] hybris Platform
*
* Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
*
* This software is the confidential and proprietary information of SAP
* ("Confidential Information"). You shall not disclose such Confidential
* Information and shall use it only in accordance with the terms of the
* license agreement you entered into with SAP.
*/
package de.hybris.platform.xyformsservices.strategy;

import de.hybris.platform.xyformsservices.daos.YFormDao;
import de.hybris.platform.xyformsservices.model.YFormDefinitionModel;

import javax.annotation.Resource;


/**
 * Simple implementation that is going to fail in case of race condition. This shouldn't be a problem since form
 * publishing won't happen that frequent and simultaneously.
 */
public class DefaultGetVersionNumberStrategy implements GetVersionNumberStrategy
{
	@Resource
	private YFormDao yformDao;

	@Override
	public int execute(final String applicationId, final String formId)
	{
		int version = 0;
		try
		{
			final YFormDefinitionModel yfd = yformDao.findYFormDefinition(applicationId, formId);
			version = yfd.getVersion() + 1;
		}
		catch (final Exception e) // NOSONAR (exception is expected if yform definition has not been found)
		{
			version = 1;
		}
		return version;
	}
}
