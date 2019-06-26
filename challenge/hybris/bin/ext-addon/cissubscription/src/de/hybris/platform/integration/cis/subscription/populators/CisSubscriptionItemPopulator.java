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
package de.hybris.platform.integration.cis.subscription.populators;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.subscriptionfacades.data.SubscriptionPricePlanData;

import org.springframework.beans.factory.annotation.Required;

import com.hybris.cis.api.subscription.model.CisSubscriptionItem;
import com.hybris.cis.api.subscription.model.CisSubscriptionPlan;
import com.hybris.cis.api.subscription.model.CisTermsOfService;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;


/**
 * Populate the CisSubscriptionItem with the OrderEntryData information
 */
public class CisSubscriptionItemPopulator implements Populator<OrderEntryData, CisSubscriptionItem>
{
	private Converter<ProductData, CisTermsOfService> cisTermsOfServiceConverter;
	private Converter<ProductData, CisSubscriptionPlan> cisSubscriptionPlanConverter;

	@Override
	public void populate(final OrderEntryData source, final CisSubscriptionItem target) throws ConversionException
	{
		validateParameterNotNullStandardMessage("target", target);

		if (source == null)
		{
			return;
		}

		final ProductData product = source.getProduct();

		target.setId(source.getEntryNumber().toString());
		target.setCode(product.getCode());
		target.setName(product.getName());

		target.setSubscriptionTerm(getCisTermsOfServiceConverter().convert(product));

		final CisSubscriptionPlan cisSubscriptionPlan = getCisSubscriptionPlanConverter().convert(product);

		if (source.getProduct().getPrice() instanceof SubscriptionPricePlanData)
		{
			target.setSubscriptionPlan(cisSubscriptionPlan);
		}

	}

	protected Converter<ProductData, CisTermsOfService> getCisTermsOfServiceConverter()
	{
		return cisTermsOfServiceConverter;
	}

	@Required
	public void setCisTermsOfServiceConverter(final Converter<ProductData, CisTermsOfService> cisTermsOfServiceConverter)
	{
		this.cisTermsOfServiceConverter = cisTermsOfServiceConverter;
	}

	protected Converter<ProductData, CisSubscriptionPlan> getCisSubscriptionPlanConverter()
	{
		return cisSubscriptionPlanConverter;
	}

	@Required
	public void setCisSubscriptionPlanConverter(final Converter<ProductData, CisSubscriptionPlan> cisSubscriptionPlanConverter)
	{
		this.cisSubscriptionPlanConverter = cisSubscriptionPlanConverter;
	}
}
