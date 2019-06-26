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
package de.hybris.platform.addonsupport.setup.populator;

import de.hybris.platform.addonsupport.setup.impl.AddOnDataImportEventContext;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.commerceservices.setup.data.ImpexMacroParameterData;


public class BaseSiteImpexMacroParametersPopulator implements Populator<AddOnDataImportEventContext, ImpexMacroParameterData>
{

	@Override
	public void populate(final AddOnDataImportEventContext source, final ImpexMacroParameterData target)
			throws ConversionException
	{
		target.setSiteUid(source.getBaseSite().getUid());
		target.setStoreUid(source.getBaseSite().getUid());
		target.setSolrIndexedType(source.getBaseSite().getUid() + "ProductType");

		if (source.getBaseSite().getChannel() != null)
		{
			target.setChannel(StringUtils.lowerCase(source.getBaseSite().getChannel().getCode()));
		}
	}
}
