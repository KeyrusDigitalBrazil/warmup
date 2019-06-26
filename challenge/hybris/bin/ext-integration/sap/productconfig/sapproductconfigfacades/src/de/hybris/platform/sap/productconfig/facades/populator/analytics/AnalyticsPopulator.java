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
package de.hybris.platform.sap.productconfig.facades.populator.analytics;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticCsticData;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticCsticValueData;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticsPopulatorInput;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticQualifier;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsItem;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsPopularityIndicator;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsPossibleValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Populator, which creates and fills a List of{@link AnalyticCsticData} Data DTO based on the {@link AnalyticsDocument}
 */
public class AnalyticsPopulator implements Populator<AnalyticsPopulatorInput, List<AnalyticCsticData>>
{


	static final String POPULARITY_INDICATOR_TYPE_PERCENTAGE = "%";
	private UniqueUIKeyGenerator uiKeyGenerator;

	@Override
	public void populate(final AnalyticsPopulatorInput source, final List<AnalyticCsticData> target)
	{
		AnalyticsItem analyticInstance = null;
		for (final String csticUiKey : source.getCsticUiKeys())
		{
			AnalyticsCharacteristic analyticCstic = null;
			final CsticQualifier qualifier = getUiKeyGenerator().splitId(csticUiKey);
			analyticInstance = findAnalyticInstance(analyticInstance, qualifier.getInstanceName(), source.getDocument());
			if (null != analyticInstance)
			{
				analyticCstic = findAnalyticCstic(qualifier.getCsticName(), analyticInstance);
			}

			if (null != analyticCstic)
			{
				final AnalyticCsticData analyticCsticData = new AnalyticCsticData();
				analyticCsticData.setCsticUiKey(csticUiKey);
				populate(analyticCstic, analyticCsticData);
				if (isValid(analyticCsticData))
				{
					target.add(analyticCsticData);
				}
			}
		}
	}


	protected boolean isValid(final AnalyticCsticData analyticCsticData)
	{
		for (final AnalyticCsticValueData value : analyticCsticData.getAnalyticValues().values())
		{
			if (value.getPopularityPercentage() > 0.0)
			{
				return true;
			}
		}
		return false;
	}

	protected void populate(final AnalyticsCharacteristic source, final AnalyticCsticData target)
	{
		final int mapCapa = (int) (source.getPossibleValues().size() / 0.75 + 1);
		final Map<String, AnalyticCsticValueData> analyticValues = new HashMap<>(mapCapa);
		target.setAnalyticValues(analyticValues);
		for (final AnalyticsPossibleValue analyticPossibleValue : source.getPossibleValues())
		{
			final AnalyticCsticValueData analyticValueData = new AnalyticCsticValueData();
			populate(analyticPossibleValue, analyticValueData);
			analyticValues.put(analyticPossibleValue.getValue(), analyticValueData);
		}
	}


	protected void populate(final AnalyticsPossibleValue source, final AnalyticCsticValueData target)
	{
		if (null != source.getPopularityIndicators())
		{
			for (final AnalyticsPopularityIndicator indicator : source.getPopularityIndicators())
			{
				if (POPULARITY_INDICATOR_TYPE_PERCENTAGE.equals(indicator.getType()))
				{
					target.setPopularityPercentage(indicator.getValue().doubleValue());
					return;
				}
			}
		}
	}

	protected AnalyticsItem findAnalyticInstance(final AnalyticsItem lastAnalyticInstance, final String instanceName,
			final AnalyticsDocument analyticsDocument)
	{

		if (lastAnalyticInstance != null && lastAnalyticInstance.getProductId().equals(instanceName))
		{
			return lastAnalyticInstance;
		}

		final AnalyticsItem analyticInstance = analyticsDocument.getRootItem();

		if (analyticInstance != null && instanceName.equals(analyticInstance.getProductId()))
		{
			return analyticInstance;
		}


		return null;
	}

	protected AnalyticsCharacteristic findAnalyticCstic(final String csticName, final AnalyticsItem analyticInstance)
	{
		for (final AnalyticsCharacteristic analyticCstic : analyticInstance.getCharacteristics())
		{
			if (analyticCstic.getId().equals(csticName))
			{
				return analyticCstic;
			}
		}

		return null;
	}

	protected UniqueUIKeyGenerator getUiKeyGenerator()
	{
		return uiKeyGenerator;
	}


	/**
	 * @param uiKeyGenerator
	 *           UI-Key generator used to split/create UI-Keys
	 */
	public void setUiKeyGenerator(final UniqueUIKeyGenerator uiKeyGenerator)
	{
		this.uiKeyGenerator = uiKeyGenerator;
	}
}
