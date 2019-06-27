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

import de.hybris.platform.converters.Populator;
import de.hybris.platform.entitlementfacades.data.EntitlementData;
import de.hybris.platform.entitlementservices.model.ProductEntitlementModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.util.ServicesUtil;

/**
 * Populate DTO {@link EntitlementData} with data from {@link ProductEntitlementModel}
 */
public class ProductEntitlementPopulator<SOURCE extends ProductEntitlementModel, TARGET extends EntitlementData>
		implements Populator<SOURCE, TARGET>
{
	@Override
	public void populate(final SOURCE source, final TARGET target) throws ConversionException
	{
		ServicesUtil.validateParameterNotNullStandardMessage("source", source);
		ServicesUtil.validateParameterNotNullStandardMessage("target", target);

		target.setId(source.getId());
		if (source.getEntitlement() != null) {
			target.setName(source.getEntitlement().getName());
		}
		target.setDescription(source.getDescription());
		target.setTimeUnit(source.getTimeUnit());
		target.setConditionString(source.getConditionString());
		target.setConditionPath(source.getConditionPath());
		target.setQuantity(source.getQuantity() == null ? 0 : source.getQuantity());
		if (source.getConditionGeo() != null && source.getConditionGeo().size() > 0) {
			target.setConditionGeo(source.getConditionGeo());
		}

		if (source.getTimeUnitStart() == null)
		{
			target.setTimeUnitStart(null);
		}
		else
		{
			target.setTimeUnitStart(source.getTimeUnitStart());
		}

		if (source.getTimeUnitDuration() == null)
		{
			target.setTimeUnitDuration(null);
		}
		else
		{
			target.setTimeUnitDuration(source.getTimeUnitDuration());
		}
	}
}
