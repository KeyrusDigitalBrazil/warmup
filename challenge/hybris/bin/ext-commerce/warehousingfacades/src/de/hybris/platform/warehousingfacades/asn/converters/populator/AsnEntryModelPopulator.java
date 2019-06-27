/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.warehousingfacades.asn.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeEntryModel;
import de.hybris.platform.warehousingfacades.asn.data.AsnEntryData;


/**
 * Populator for populating {@link AdvancedShippingNoticeEntryModel} for the given {@link AsnEntryData}
 */
public class AsnEntryModelPopulator implements Populator<AsnEntryData, AdvancedShippingNoticeEntryModel>
{
	@Override
	public void populate(final AsnEntryData source, final AdvancedShippingNoticeEntryModel target) throws ConversionException
	{
		if (source != null && target != null)
		{
			target.setProductCode(source.getProductCode());
			target.setQuantity(source.getQuantity());
		}
	}
}
