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
package de.hybris.platform.sap.sapmodel.daos;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.sapmodel.model.SAPPricingSalesAreaToCatalogModel;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.store.BaseStoreModel;


public class NetAttributeHandler implements DynamicAttributeHandler<Boolean, SAPPricingSalesAreaToCatalogModel>
{

	private static final Logger LOGGER = LogManager.getLogger(NetAttributeHandler.class);

	@Autowired
	protected FlexibleSearchService flexibleSearchService; // NOPMD

	/*
	 * Returns Net (true) or Gross (false) of Base Store based on PricingSalesArea mapping
	 * 
	 * Base Store is linked to SAP Base Store Configuration 
	 * SAP Base Store Configuration defines a Sales Organization and Distribution Channel 
	 * Pricing Replication (SAP Global Configuration) maps Sales Organization and Distribution Channel to Catalog 
	 * Using Sales Organization and Distribution Channel, we can get if a Price is Net or Gross 
	 * If there is an issue with configuration, we default to Gross (false)
	 */
	@Override
	public Boolean get(SAPPricingSalesAreaToCatalogModel model)
	{
		// Set example model for search
		final SAPConfigurationModel sapConfigurationExampleModel = new SAPConfigurationModel();
		sapConfigurationExampleModel.setSapcommon_distributionChannel(model.getDistributionChannel());
		sapConfigurationExampleModel.setSapcommon_salesOrganization(model.getSalesOrganization());

		// Search for existing configurations using the example model
		final List<SAPConfigurationModel> sapConfigurationModels = flexibleSearchService.getModelsByExample(sapConfigurationExampleModel);

		if (sapConfigurationModels.isEmpty())
		{
			LOGGER.error("No SAP Configuration found for Sales Organization {} and distribution channel {} - Returning default value FALSE (gross)", model.getSalesOrganization(), model.getDistributionChannel());
			return Boolean.FALSE; // default to gross
		}

		// Find Net value from first basestore in first configuration model that has a basestore
		for (SAPConfigurationModel sapConfiguration : sapConfigurationModels)
		{
			LOGGER.debug("SAP Configuration found: {}", sapConfiguration.getCore_name());

			for (BaseStoreModel baseStoreModel : Optional.ofNullable(sapConfiguration.getBaseStores()).orElse(Collections.emptyList()))
			{
				return baseStoreModel.isNet();
			}
		}

		LOGGER.error("No Base Store assigned to SAP Configuration {} was found - Returning default value FALSE (gross)", sapConfigurationModels.stream().map(SAPConfigurationModel::getCore_name).collect(Collectors.joining(",")));
		return Boolean.FALSE; // default to gross
	}

	@Override
	public void set(SAPPricingSalesAreaToCatalogModel model, Boolean value)
	{
		throw new UnsupportedOperationException();

	}

}
