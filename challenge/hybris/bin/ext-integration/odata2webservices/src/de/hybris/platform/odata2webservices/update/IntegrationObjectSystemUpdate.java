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
package de.hybris.platform.odata2webservices.update;


import static de.hybris.platform.core.initialization.SystemSetup.Process.UPDATE;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.integrationservices.constants.IntegrationservicesConstants;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.odata2webservices.enums.IntegrationType;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SystemSetup(extension = IntegrationservicesConstants.EXTENSIONNAME, process = UPDATE)
public class IntegrationObjectSystemUpdate
{
	private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationObjectSystemUpdate.class);

	private FlexibleSearchService flexibleSearchService;
	private ModelService modelService;

	@SystemSetup
	public void updateIntegrationType()
	{
		LOGGER.debug("Updating integration objects...");
		final List<IntegrationObjectModel> allIntegrationObjects = findAllIntegrationObjects();
		allIntegrationObjects.forEach(io -> {
			if (io.getIntegrationType() == null)
			{
				io.setIntegrationType(IntegrationType.INBOUND);
			}
		});

		modelService.saveAll(allIntegrationObjects);
	}

	private List<IntegrationObjectModel> findAllIntegrationObjects()
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery("SELECT {" + IntegrationObjectModel.PK + "} FROM {" + IntegrationObjectModel._TYPECODE + "}");
		return getFlexibleSearchService().<IntegrationObjectModel>search(fQuery).getResult();
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
