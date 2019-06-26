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
package de.hybris.platform.ordermanagementwebservices.mapping.converters.returns;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.MappingContext;


/**
 * Bidirectional converter between a {@link de.hybris.platform.basecommerce.enums.CancelReason} and a String.
 */
@WsDTOMapping
public class CancelReasonConverter extends BidirectionalConverter<CancelReason, String>
{
	@Override
	public String convertTo(final CancelReason source, final Type<String> destinationType, final MappingContext mappingContext)
	{
		return source.toString();
	}

	@Override
	public CancelReason convertFrom(final String source, final Type<CancelReason> destinationType, final MappingContext mappingContext)
	{
		return CancelReason.valueOf(source);
	}
}
