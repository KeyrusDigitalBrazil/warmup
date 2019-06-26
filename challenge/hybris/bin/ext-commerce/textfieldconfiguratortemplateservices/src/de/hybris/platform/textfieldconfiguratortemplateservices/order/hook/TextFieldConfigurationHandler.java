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
package de.hybris.platform.textfieldconfiguratortemplateservices.order.hook;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.order.ProductConfigurationHandler;
import de.hybris.platform.commerceservices.service.data.ProductConfigurationItem;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;
import de.hybris.platform.textfieldconfiguratortemplateservices.model.TextFieldConfiguratorSettingModel;
import de.hybris.platform.textfieldconfiguratortemplateservices.model.TextFieldConfiguredProductInfoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


public class TextFieldConfigurationHandler implements ProductConfigurationHandler
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(TextFieldConfigurationHandler.class);

	@Override
	public List<AbstractOrderEntryProductInfoModel> createProductInfo(AbstractConfiguratorSettingModel productSettings)
	{
		if (productSettings instanceof TextFieldConfiguratorSettingModel)
		{
			final TextFieldConfiguratorSettingModel textSetting = (TextFieldConfiguratorSettingModel) productSettings;

			final TextFieldConfiguredProductInfoModel result = new TextFieldConfiguredProductInfoModel();
			result.setConfiguratorType(ConfiguratorType.TEXTFIELD);
			result.setConfigurationLabel(textSetting.getTextFieldLabel());
			result.setConfigurationValue(textSetting.getTextFieldDefaultValue());
			validate(result);
			return Collections.singletonList(result);
		}
		else
		{
			throw new IllegalArgumentException("Argument must be a type of TextFieldConfiguratorSettingsModel");
		}
	}

	@Override
	public List<AbstractOrderEntryProductInfoModel> convert(final Collection<ProductConfigurationItem> items,
			final AbstractOrderEntryModel entry)
	{
		validateParameterNotNullStandardMessage("items", items);
		return items.stream()
				.peek(item -> validateParameterNotNull(item, "Items of the input collection must not be null"))
				.map(item -> {
					final TextFieldConfiguredProductInfoModel result = new TextFieldConfiguredProductInfoModel();
					result.setConfigurationLabel(item.getKey());
					if (item.getValue() != null)
					{
						result.setConfigurationValue(item.getValue().toString());
					}
					result.setConfiguratorType(ConfiguratorType.TEXTFIELD);
					return result;
				})
				.peek(this::validate)
				.collect(Collectors.toList());
	}

	protected void validate(final TextFieldConfiguredProductInfoModel item)
	{
		final String value = item.getConfigurationValue();
		if (value == null || value.isEmpty())
		{
			item.setProductInfoStatus(ProductInfoStatus.ERROR);
		}
		else
		{
			item.setProductInfoStatus(ProductInfoStatus.SUCCESS);
		}
	}
}
