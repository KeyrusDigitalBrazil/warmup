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
package de.hybris.platform.sap.sapproductconfigsomservices.converters.populators;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;


/**
 *
 */
public class DefaultAbstractOrderEntryConfigurationInfoPopulator implements Populator<ConfigModel, List<ConfigurationInfoData>>
{
	private int maxNumberOfDisplayedCsticsInCart = 4;
	private ConfigurationInfoNameProvider configurationInfoNameProvider;
	public static final String VALUE_SEPARATOR = "; ";

	@Override
	public void populate(final ConfigModel source, final List<ConfigurationInfoData> target)
	{
		final InstanceModel rootInstance = source.getRootInstance();
		int numberOfDisplayedCstics = 0;
		for (final CsticGroupModel currentGroup : rootInstance.getCsticGroups())
		{
			numberOfDisplayedCstics = processGroupForCartDisplay(target, rootInstance, numberOfDisplayedCstics, currentGroup);
		}
		if (CollectionUtils.isEmpty(target))
		{
			addInitialConfigurationInfo(target);
		}
	}

	protected int processGroupForCartDisplay(final List<ConfigurationInfoData> target, final InstanceModel rootInstance,
			final int numberOfDisplayedCstics, final CsticGroupModel currentGroup)
	{
		int currentNumberOfDisplayedCstics = numberOfDisplayedCstics;
		for (final String csticName : currentGroup.getCsticNames())
		{
			if (currentNumberOfDisplayedCstics >= maxNumberOfDisplayedCsticsInCart)
			{
				// stop as soon as maximum number of cstics to be displayed has been reached
				break;
			}
			final CsticModel cstic = rootInstance.getCstic(csticName);
			if (displayCstic(cstic))
			{
				addCsticForCartDisplay(target, cstic);
				currentNumberOfDisplayedCstics++;
			}
		}
		return currentNumberOfDisplayedCstics;
	}

	public void addCsticForCartDisplay(final List<ConfigurationInfoData> configurationInfoInlineList, final CsticModel csticModel)
	{


		final String displayName = getConfigurationInfoNameProvider().getCharacteristicDisplayName(csticModel);

		final ConfigurationInfoData configInfoInline = new ConfigurationInfoData();
		configInfoInline.setConfigurationLabel(displayName);
		final List<CsticValueModel> assignedValues = csticModel.getAssignedValues();
		configInfoInline.setConfigurationValue(generateConfigInfoInline(assignedValues, csticModel));
		configInfoInline.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		if (csticModel.isConsistent())
		{
			configInfoInline.setStatus(ProductInfoStatus.SUCCESS);
		}
		else
		{
			configInfoInline.setStatus(ProductInfoStatus.ERROR);
		}
		configurationInfoInlineList.add(configInfoInline);
	}

	public String generateConfigInfoInline(final List<CsticValueModel> assignedValues, final CsticModel csticModel)
	{
		final StringBuilder builder = new StringBuilder();
		if (!CollectionUtils.isEmpty(assignedValues))
		{
			for (int i = 0; i < assignedValues.size(); i++)
			{
				if (i > 0)
				{
					builder.append(VALUE_SEPARATOR);
				}

				builder.append(getConfigurationInfoNameProvider().getValueDisplayName(csticModel, assignedValues.get(i)));
			}
		}
		return builder.toString();
	}

	protected boolean displayCstic(final CsticModel cstic)
	{
		return cstic.isVisible() && !CollectionUtils.isEmpty(cstic.getAssignedValues());
	}


	/**
	 * At least one ConfigurationInfo object has to be added to the target, otherwise no configuration link will be
	 * displayed.
	 *
	 * @param configurationInfoInlineList
	 */
	protected void addInitialConfigurationInfo(final List<ConfigurationInfoData> configurationInfoInlineList)
	{
		final ConfigurationInfoData configInfo = new ConfigurationInfoData();
		configInfo.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		configurationInfoInlineList.add(configInfo);
	}

	/**
	 * @param maxNumberOfDisplayedCsticsInCart
	 *           the maxNumberOfDisplayedCsticsInCart to set
	 */
	public void setMaxNumberOfDisplayedCsticsInCart(final int maxNumberOfDisplayedCsticsInCart)
	{
		this.maxNumberOfDisplayedCsticsInCart = maxNumberOfDisplayedCsticsInCart;
	}

	/**
	 * @return the configurationInfoNameProvider
	 */
	public ConfigurationInfoNameProvider getConfigurationInfoNameProvider()
	{
		return configurationInfoNameProvider;
	}

	/**
	 * @param configurationInfoNameProvider
	 *           the configurationInfoNameProvider to set
	 */
	public void setConfigurationInfoNameProvider(final ConfigurationInfoNameProvider configurationInfoNameProvider)
	{
		this.configurationInfoNameProvider = configurationInfoNameProvider;
	}


}
