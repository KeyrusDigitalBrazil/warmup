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
package de.hybris.platform.sap.productconfig.runtime.pci.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsContextEntry;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsItem;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates an analytics document with the information from configuration model
 */
public class AnalyticsDocumentPopulator implements Populator<ConfigModel, AnalyticsDocument>
{
	static final String DIVISION = "DIVISION";
	static final String DISTRIBUTION_CHANNEL = "DISTR_CHAN";
	static final String SALES_ORG = "SALES_ORG";
	private PricingConfigurationParameter pricingConfigurationParameter;
	private Converter<InstanceModel, AnalyticsItem> analyticsItemConverter;


	@Override
	public void populate(final ConfigModel source, final AnalyticsDocument target)
	{
		target.setRootProduct(source.getName());
		fillContext(target);
		populateRootItem(source, target);
	}


	protected void populateRootItem(final ConfigModel source, final AnalyticsDocument target)
	{
		final AnalyticsItem rootItem = getAnalyticsItemConverter().convert(source.getRootInstance());
		target.setRootItem(rootItem);

	}


	protected void fillContext(final AnalyticsDocument target)
	{
		target.setContextAttributes(new ArrayList<>());
		// CUSTOMER is not supported as a context entry
		target.getContextAttributes().add(createContextEntry(SALES_ORG, getPricingConfigurationParameter().getSalesOrganization()));
		target.getContextAttributes().add(
				createContextEntry(DISTRIBUTION_CHANNEL, getPricingConfigurationParameter().getDistributionChannelForConditions()));
		target.getContextAttributes().add(
				createContextEntry(DIVISION, getPricingConfigurationParameter().getDivisionForConditions()));

	}

	protected AnalyticsContextEntry createContextEntry(final String name, final String value)
	{
		final AnalyticsContextEntry entry = new AnalyticsContextEntry();
		entry.setName(name);
		entry.setValue(value);
		return entry;
	}


	protected PricingConfigurationParameter getPricingConfigurationParameter()
	{
		return pricingConfigurationParameter;
	}

	/**
	 * @param pricingConfigurationParameter
	 *           Configuration settings for pricing which we re-use for analytics like e.g. sales area information
	 */
	@Required
	public void setPricingConfigurationParameter(final PricingConfigurationParameter pricingConfigurationParameter)
	{
		this.pricingConfigurationParameter = pricingConfigurationParameter;
	}




	protected Converter<InstanceModel, AnalyticsItem> getAnalyticsItemConverter()
	{
		return analyticsItemConverter;
	}

	/**
	 * @param analyticsItemConverter
	 *           Converter for constructing the REST input on item level
	 */
	@Required
	public void setAnalyticsItemConverter(final Converter<InstanceModel, AnalyticsItem> analyticsItemConverter)
	{
		this.analyticsItemConverter = analyticsItemConverter;
	}

}
