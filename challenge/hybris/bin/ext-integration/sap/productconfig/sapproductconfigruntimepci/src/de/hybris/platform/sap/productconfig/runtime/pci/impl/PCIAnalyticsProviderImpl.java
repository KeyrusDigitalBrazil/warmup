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
package de.hybris.platform.sap.productconfig.runtime.pci.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.pci.PCICharonFacade;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.springframework.beans.factory.annotation.Required;


/**
 * Facilitates product configuration intelligence calls
 */
public class PCIAnalyticsProviderImpl implements AnalyticsProvider
{
	private Converter<ConfigModel, AnalyticsDocument> analyticsDocumentConverter;
	private PCICharonFacade pciCharonFacade;

	@Override
	public AnalyticsDocument getPopularity(final ConfigModel config)
	{
		final AnalyticsDocument input = getAnalyticsDocumentConverter().convert(config);
		return getPciCharonFacade().createAnalyticsDocument(input);
	}

	@Override
	public boolean isActive()
	{
		return true;
	}

	protected Converter<ConfigModel, AnalyticsDocument> getAnalyticsDocumentConverter()
	{
		return analyticsDocumentConverter;
	}

	/**
	 * @param analyticsDocumentConverter
	 *           Converter for translating the configuration model into an input for the analytics call (reducing the
	 *           model to assigned and possible values
	 */
	@Required
	public void setAnalyticsDocumentConverter(final Converter<ConfigModel, AnalyticsDocument> analyticsDocumentConverter)
	{
		this.analyticsDocumentConverter = analyticsDocumentConverter;
	}

	protected PCICharonFacade getPciCharonFacade()
	{
		return pciCharonFacade;
	}

	/**
	 * @param pciCharonFacade
	 *           Facade for wrapping the REST call
	 */
	@Required
	public void setPciCharonFacade(final PCICharonFacade pciCharonFacade)
	{
		this.pciCharonFacade = pciCharonFacade;
	}

}
