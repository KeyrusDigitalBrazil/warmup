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
package de.hybris.platform.commercefacades.address.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class SingleLineAddressFormatPopulator implements Populator<AddressModel, StringBuilder>
{
	private static final Logger LOG = Logger.getLogger(SingleLineAddressFormatPopulator.class);

	private List<String> addressFormatList;

	@Override
	public void populate(final AddressModel addressModel, final StringBuilder addressLine) throws ConversionException
	{
		for (final String field : addressFormatList)
		{
			try
			{
				final String fieldValue = (String) PropertyUtils.getProperty(addressModel, field);
				if (fieldValue != null)
				{
					addressLine.append(fieldValue);
					addressLine.append(", ");
				}
			}
			catch (final NestedNullException e)
			{
				if (LOG.isDebugEnabled())
				{
					LOG.debug(e.getLocalizedMessage(), e);
				}
			}
			catch (final Exception e)
			{
				throw new ConversionException(e.getLocalizedMessage(), e);
			}
		}

		if (addressLine.length() > 2)
		{
			// Trim last ", "
			addressLine.setLength(addressLine.length() - 2);
		}
	}

	protected List<String> getAddressFormatList()
	{
		return addressFormatList;
	}

	@Required
	public void setAddressFormatList(final List<String> addressFormatList)
	{
		this.addressFormatList = addressFormatList;
	}
}
