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

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.facades.ClassificationSystemCPQAttributesProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationClassificationCacheStrategy;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populator to create an AbstractOrderEntryProductInfoModel from a given ConfigurationModel.<br>
 * AbstractOrderEntryProductInfoModel contains a short list of some values of the related runtime configuration, so it
 * can be used to show a configuration summary within the cart or order, without the need to instantiate the whole
 * configuration, or even to be aware of the configuration at all.
 */
public class ConfigurationOrderEntryProductInfoModelPopulator
		implements Populator<ConfigModel, List<AbstractOrderEntryProductInfoModel>>
{
	private int maxNumberOfDisplayedCsticsInCart = 4;
	static final String VALUE_SEPARATOR = "; ";
	private ConfigurationClassificationCacheStrategy configurationClassificationCacheStrategy;
	private ClassificationSystemCPQAttributesProvider nameProvider;

	@Override
	public void populate(final ConfigModel source, final List<AbstractOrderEntryProductInfoModel> target)
	{
		final InstanceModel rootInstance = source.getRootInstance();
		int numberOfDisplayedCstics = 0;
		final Map<String, ClassificationSystemCPQAttributesContainer> nameMap = getClassificationCacheStrategy()
				.getCachedNameMap(source);
		for (final CsticGroupModel currentGroup : rootInstance.getCsticGroups())
		{
			numberOfDisplayedCstics = processGroupForCartDisplay(target, rootInstance, numberOfDisplayedCstics, currentGroup,
					nameMap);
		}
		if (CollectionUtils.isEmpty(target))
		{
			addInitialConfigurationInfo(target);
		}
	}

	protected int processGroupForCartDisplay(final List<AbstractOrderEntryProductInfoModel> target,
			final InstanceModel rootInstance, final int numberOfDisplayedCstics, final CsticGroupModel currentGroup,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
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
				addCsticForCartDisplay(target, cstic, nameMap);
				currentNumberOfDisplayedCstics++;
			}
		}
		return currentNumberOfDisplayedCstics;
	}

	protected void addCsticForCartDisplay(final List<AbstractOrderEntryProductInfoModel> configurationInfoModel,
			final CsticModel csticModel, final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{

		final ClassificationSystemCPQAttributesProvider cpqNameProvider = getNameProvider();
		final ClassificationSystemCPQAttributesContainer cpqAttributes = cpqNameProvider.getCPQAttributes(csticModel.getName(),
				nameMap);
		final boolean isNameProviderDebugEnabled = cpqNameProvider.isDebugEnabled();
		final String displayName = cpqNameProvider.getDisplayName(csticModel, cpqAttributes, isNameProviderDebugEnabled);

		final CPQOrderEntryProductInfoModel configInfoInline = new CPQOrderEntryProductInfoModel();
		configInfoInline.setCpqCharacteristicName(displayName);
		final List<CsticValueModel> assignedValues = csticModel.getAssignedValues();
		configInfoInline.setCpqCharacteristicAssignedValues(
				generateConfigInfoInline(assignedValues, csticModel, cpqAttributes, isNameProviderDebugEnabled));
		configInfoInline.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		if (csticModel.isConsistent())
		{
			configInfoInline.setProductInfoStatus(ProductInfoStatus.SUCCESS);
		}
		else
		{
			configInfoInline.setProductInfoStatus(ProductInfoStatus.ERROR);
		}
		configurationInfoModel.add(configInfoInline);
	}

	protected String generateConfigInfoInline(final List<CsticValueModel> assignedValues, final CsticModel csticModel,
			final ClassificationSystemCPQAttributesContainer cpqAttributes, final boolean isNameProviderDebugEnabled)
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
				builder.append(getNameProvider().getOverviewValueName(assignedValues.get(i), csticModel, cpqAttributes,
						isNameProviderDebugEnabled));
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
	 * @param configurationInfoModel
	 */
	protected void addInitialConfigurationInfo(final List<AbstractOrderEntryProductInfoModel> configurationInfoModel)
	{
		final CPQOrderEntryProductInfoModel configInfo = new CPQOrderEntryProductInfoModel();
		configInfo.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
		configurationInfoModel.add(configInfo);
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
	 * @return the nameProvider
	 */
	public ClassificationSystemCPQAttributesProvider getNameProvider()
	{
		return nameProvider;
	}

	/**
	 * @param nameProvider
	 *           the nameProvider to set
	 */
	public void setNameProvider(final ClassificationSystemCPQAttributesProvider nameProvider)
	{
		this.nameProvider = nameProvider;
	}

	protected ConfigurationClassificationCacheStrategy getClassificationCacheStrategy()
	{
		return configurationClassificationCacheStrategy;
	}

	@Required
	public void setClassificationCacheStrategy(
			final ConfigurationClassificationCacheStrategy configurationClassificationCacheStrategy)
	{
		this.configurationClassificationCacheStrategy = configurationClassificationCacheStrategy;
	}

}
