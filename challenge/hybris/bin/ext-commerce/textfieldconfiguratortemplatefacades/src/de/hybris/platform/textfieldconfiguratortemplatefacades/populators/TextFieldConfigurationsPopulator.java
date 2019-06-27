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
package de.hybris.platform.textfieldconfiguratortemplatefacades.populators;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.textfieldconfiguratortemplateservices.model.TextFieldConfiguredProductInfoModel;

import java.util.List;


/**
 * Populates confgiurations of type {@code TEXTFIELD}.
 */
public class TextFieldConfigurationsPopulator<T extends AbstractOrderEntryProductInfoModel>
		implements Populator<T, List<ConfigurationInfoData>>
{
	@Override
	public void populate(final T source, final List<ConfigurationInfoData> target)
			throws ConversionException
	{
		if (source.getConfiguratorType() == ConfiguratorType.TEXTFIELD)
		{
			if (!(source instanceof TextFieldConfiguredProductInfoModel))
			{
				throw new ConversionException("Instance with type " + source.getConfiguratorType() + " is of class "
						+ source.getClass().getName() + " which is not convertible to "
						+ TextFieldConfiguredProductInfoModel.class.getName());
			}
			final ConfigurationInfoData item = new ConfigurationInfoData();
			final TextFieldConfiguredProductInfoModel model = (TextFieldConfiguredProductInfoModel) source;
			item.setConfiguratorType(model.getConfiguratorType());
			item.setConfigurationLabel(model.getConfigurationLabel());
			item.setConfigurationValue(model.getConfigurationValue());
			item.setStatus(model.getProductInfoStatus());
			target.add(item);
		}
	}
}
