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
package de.hybris.platform.sap.productconfig.facades.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.PriceDataPair;
import de.hybris.platform.sap.productconfig.facades.PriceValueUpdateData;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Required;


/**
 * Populates a price summary
 *
 */
public class DeltaPricePopulator implements Populator<PriceValueUpdateModel, PriceValueUpdateData>
{
	private UniqueUIKeyGenerator uiKeyGenerator;
	private ConfigPricing configPricing;


	@Override
	public void populate(final PriceValueUpdateModel source, final PriceValueUpdateData target)
	{

		target.setCsticUiKey(buildCsticKey(source.getCsticQualifier()));
		target.setPrices(fillPrices(source.getValuePrices()));
		target.setShowDeltaPrices(source.isShowDeltaPrices());
		target.setSelectedValues(source.getSelectedValues());

	}

	protected Map<String, PriceDataPair> fillPrices(final Map<String, PriceModel> priceModels)
	{
		final Map<String, PriceDataPair> priceValuesData = new HashMap<>();
		if (priceModels != null && !priceModels.isEmpty())
		{
			for (final Entry<String, PriceModel> entry : priceModels.entrySet())
			{
				final PriceDataPair priceDataPair = new PriceDataPair();

				final PriceModel priceModel = entry.getValue();
				priceDataPair.setPriceValue(getConfigPricing().getPriceData(priceModel));
				priceDataPair.setObsoletePriceValue(getConfigPricing().getObsoletePriceData(priceModel));

				priceValuesData.put(entry.getKey(), priceDataPair);
			}
		}
		return priceValuesData;
	}

	/**
	 * @param csticQualifier
	 * @return cstic key as string in format instanceId-instanceName.groupName.csticName
	 */
	protected String buildCsticKey(final CsticQualifier csticQualifier)
	{
		return getUiKeyGenerator().generateId(csticQualifier);

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

	protected ConfigPricing getConfigPricing()
	{
		return configPricing;
	}

	/**
	 * @param configPricing
	 *           the configPricing to set
	 */
	@Required
	public void setConfigPricing(final ConfigPricing configPricing)
	{
		this.configPricing = configPricing;
	}


}
