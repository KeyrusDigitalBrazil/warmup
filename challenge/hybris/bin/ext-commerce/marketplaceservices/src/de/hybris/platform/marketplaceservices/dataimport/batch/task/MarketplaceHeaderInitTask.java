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
package de.hybris.platform.marketplaceservices.dataimport.batch.task;

import java.io.File;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.acceleratorservices.dataimport.batch.BatchHeader;
import de.hybris.platform.acceleratorservices.dataimport.batch.task.HeaderInitTask;
import de.hybris.platform.marketplaceservices.dataimport.batch.MarketplaceBatchHeader;
import de.hybris.platform.marketplaceservices.dataimport.batch.util.DataIntegrationUtils;
import de.hybris.platform.marketplaceservices.dataimport.batch.util.VendorProductCatalogParser;
import de.hybris.platform.servicelayer.config.ConfigurationService;


/**
 * Task to initialize the batch header with the sequence id, language and catalog
 */
public class MarketplaceHeaderInitTask extends HeaderInitTask
{
	private static final String PRODUCT_TAXGROUP_PROPERTY = "marketplaceservices.product.taxgroup";

	private VendorProductCatalogParser vendorCatalogParser;
	private ConfigurationService configurationService;

	@Override
	public BatchHeader execute(final BatchHeader header)
	{
		final MarketplaceBatchHeader marketplaceBatchHeader = new MarketplaceBatchHeader(super.execute(header));
		final String catalog = vendorCatalogParser.getVendorCatalog(header.getFile());
		marketplaceBatchHeader.setCatalog(catalog);
		final String vendorCode = resolveVendorCode(header.getFile());
		marketplaceBatchHeader.setVendorCode(vendorCode);
		marketplaceBatchHeader.setTaxGroup(getConfigurationService().getConfiguration().getString(PRODUCT_TAXGROUP_PROPERTY));
		return marketplaceBatchHeader;
	}

	@Required
	public void setVendorCatalogParser(final VendorProductCatalogParser vendorCatalogParser)
	{
		this.vendorCatalogParser = vendorCatalogParser;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	protected VendorProductCatalogParser getVendorCatalogParser()
	{
		return vendorCatalogParser;
	}
	
	protected String resolveVendorCode(File file) {
		return DataIntegrationUtils.resolveVendorCode(file);
	}

}
