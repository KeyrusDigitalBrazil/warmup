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
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.ConfigurationPricingFacade;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Facade to retrieve the pricing for the product configuration
 *
 */
public class ConfigurationPricingFacadeImpl implements ConfigurationPricingFacade
{
	private PricingService pricingService;
	private ProductConfigurationService configurationService;
	private UniqueUIKeyGenerator uiKeyGenerator;

	private Converter<PriceSummaryModel, PricingData> priceSummaryConverter;
	private Converter<PriceValueUpdateModel, PriceValueUpdateData> deltaPriceConverter;

	private static final Logger LOG = Logger.getLogger(ConfigurationPricingFacadeImpl.class);

	@Override
	public PricingData getPriceSummary(final String configId)
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
			LOG.debug("get price summary by configId [CONFIG_ID='" + configId + "']");
		}
		final PriceSummaryModel priceSummary = getPricingService().getPriceSummary(configId);

		final PricingData summaryData = getPriceSummaryConverter().convert(priceSummary);

		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("GET price summary in FACADE took " + duration + " ms");
		}
		return summaryData;
	}

	@Override
	public List<PriceValueUpdateData> getValuePrices(final List<String> csticUiKeys, final String configId)
	{
		long startTime = 0;
		if (LOG.isDebugEnabled())
		{
			startTime = System.currentTimeMillis();
			LOG.debug("get value prices by configId [CONFIG_ID='" + configId + "']");
		}
		final List<PriceValueUpdateData> valuePrices = new ArrayList();

		final ConfigModel configModel = getConfigurationService().retrieveConfigurationModel(configId);

		final List<PriceValueUpdateModel> updateModels = retrieveValueUpdateModel(csticUiKeys, configModel);

		getPricingService().fillValuePrices(updateModels, configModel);

		for (final PriceValueUpdateModel updateModel : updateModels)
		{
			final PriceValueUpdateData valuePrice = getDeltaPriceConverter().convert(updateModel);
			valuePrices.add(valuePrice);
		}
		if (LOG.isDebugEnabled())
		{
			final long duration = System.currentTimeMillis() - startTime;
			LOG.debug("GET value prices in FACADE took " + duration + " ms");
		}
		return valuePrices;

	}

	protected final List<PriceValueUpdateModel> retrieveValueUpdateModel(final List<String> csticUiKeys,
			final ConfigModel configModel)
	{
		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		InstanceModel lastFound = null;
		for (final String csticKey : csticUiKeys)
		{
			final PriceValueUpdateModel updateModel = new PriceValueUpdateModel();
			final CsticQualifier qualifier = getUiKeyGenerator().splitId(csticKey);
			updateModel.setCsticQualifier(qualifier);
			final Pair<List<String>, InstanceModel> selectedValuesAndInstance = retrieveSelectedValuesAndInstance(qualifier,
					configModel.getRootInstance(), lastFound);
			updateModel.setSelectedValues(selectedValuesAndInstance.getLeft());
			lastFound = selectedValuesAndInstance.getRight();
			updateModels.add(updateModel);
		}
		return updateModels;
	}



	protected Pair<List<String>, InstanceModel> retrieveSelectedValuesAndInstance(final CsticQualifier qualifier,
			final InstanceModel rootInstance, final InstanceModel lastFound)
	{
		final List<String> selectedValues = new ArrayList<>();
		final Pair<CsticModel, InstanceModel> csticModelAndInstance = retrieveCsticAndInstance(qualifier, rootInstance, lastFound);
		for (final CsticValueModel valueModel : csticModelAndInstance.getLeft().getAssignedValues())
		{
			selectedValues.add(valueModel.getName());
		}
		return Pair.of(selectedValues, csticModelAndInstance.getRight());
	}


	protected Pair<CsticModel, InstanceModel> retrieveCsticAndInstance(final CsticQualifier qualifier,
			final InstanceModel rootInstance, final InstanceModel lastFound)
	{
		InstanceModel instance = null;
		if (isLastFoundInstanceMatching(qualifier, lastFound))
		{
			instance = lastFound;
		}
		else
		{
			instance = retrieveInstance(qualifier, rootInstance);
		}
		final CsticModel csticModel = instance.getCstics().stream()
				.filter(cstic -> cstic.getName().equals(qualifier.getCsticName())).findFirst().get();
		return Pair.of(csticModel, instance);

	}

	protected boolean isLastFoundInstanceMatching(final CsticQualifier qualifier, final InstanceModel lastFound)
	{
		return lastFound != null && lastFound.getId().equals(qualifier.getInstanceId())
				&& lastFound.getName().equals(qualifier.getInstanceName());
	}


	protected InstanceModel retrieveInstance(final CsticQualifier qualifier, final InstanceModel instanceModel)
	{
		if (instanceModel.getId().equals(qualifier.getInstanceId()) && instanceModel.getName().equals(qualifier.getInstanceName()))
		{
			return instanceModel;
		}

		for (final InstanceModel subinstance : instanceModel.getSubInstances())
		{
			final InstanceModel foundInstance = retrieveInstance(qualifier, subinstance);
			if (foundInstance != null)
			{
				return foundInstance;
			}
		}

		return null;
	}

	protected PricingService getPricingService()
	{
		return pricingService;
	}

	/**
	 * @param pricingService
	 *           the pricingService to set
	 */
	@Required
	public void setPricingService(final PricingService pricingService)
	{
		this.pricingService = pricingService;
	}


	protected Converter<PriceSummaryModel, PricingData> getPriceSummaryConverter()
	{
		return priceSummaryConverter;
	}

	/**
	 * @param priceSummaryConverter
	 *           the priceSummaryConverter to set
	 */
	@Required
	public void setPriceSummaryConverter(final Converter<PriceSummaryModel, PricingData> priceSummaryConverter)
	{
		this.priceSummaryConverter = priceSummaryConverter;
	}


	/**
	 * @return the deltaPriceConverter
	 */
	protected Converter<PriceValueUpdateModel, PriceValueUpdateData> getDeltaPriceConverter()
	{
		return deltaPriceConverter;
	}

	/**
	 * @param deltaPriceConverter
	 *           the deltaPriceConverter to set
	 */
	@Required
	public void setDeltaPriceConverter(final Converter<PriceValueUpdateModel, PriceValueUpdateData> deltaPriceConverter)
	{
		this.deltaPriceConverter = deltaPriceConverter;
	}

	@Override
	public boolean isPricingServiceActive()
	{
		return getPricingService().isActive();
	}

	/**
	 * @return the configurationService
	 */
	protected ProductConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	/**
	 * @param configurationService
	 *           the configurationService to set
	 */
	@Required
	public void setConfigurationService(final ProductConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}


	protected UniqueUIKeyGenerator getUiKeyGenerator()
	{
		return uiKeyGenerator;
	}

	/**
	 * @param uiKeyGenerator
	 *           the uiKeyGenerator to set
	 */
	@Required
	public void setUiKeyGenerator(final UniqueUIKeyGenerator uiKeyGenerator)
	{
		this.uiKeyGenerator = uiKeyGenerator;
	}

}
