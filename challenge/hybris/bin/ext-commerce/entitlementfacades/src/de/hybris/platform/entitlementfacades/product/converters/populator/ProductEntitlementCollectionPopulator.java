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
package de.hybris.platform.entitlementfacades.product.converters.populator;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.entitlementfacades.data.EntitlementData;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Populate DTO {@link ProductData} with data from {@link ProductModel}
 */
public class ProductEntitlementCollectionPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends
		AbstractProductPopulator<SOURCE, TARGET>
{
	private Converter<ProductEntitlementModel, EntitlementData> entitlementConverter;

	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("target", target);

		if (CollectionUtils.isEmpty(source.getProductEntitlements()))
		{
			target.setEntitlements(Collections.<EntitlementData>emptyList());
		}
		else
		{
			final List<EntitlementData> entitlements = source.getProductEntitlements().stream().map(entitlement -> getEntitlementConverter().convert(entitlement)).collect(Collectors.toList());
			target.setEntitlements(entitlements);
		}
	}

	protected Converter<ProductEntitlementModel, EntitlementData> getEntitlementConverter()
	{
		return entitlementConverter;
	}

	@Required
	public void setEntitlementConverter(
			final Converter<ProductEntitlementModel, EntitlementData> entitlementConverter)
	{
		this.entitlementConverter = entitlementConverter;
	}
}
