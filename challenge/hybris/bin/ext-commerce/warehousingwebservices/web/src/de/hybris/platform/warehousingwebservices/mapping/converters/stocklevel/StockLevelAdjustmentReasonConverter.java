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
package de.hybris.platform.warehousingwebservices.mapping.converters.stocklevel;

import de.hybris.platform.warehousing.enums.StockLevelAdjustmentReason;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.MappingContext;


/**
 * Bidirectional converter between {@link StockLevelAdjustmentReason} and String
 */
@WsDTOMapping
public class StockLevelAdjustmentReasonConverter extends BidirectionalConverter<StockLevelAdjustmentReason, String>
{
	@Override
	public StockLevelAdjustmentReason convertFrom(final String source, final Type<StockLevelAdjustmentReason> destinationType, final MappingContext mappingContext)
	{
		return StockLevelAdjustmentReason.valueOf(source);
	}

	@Override
	public String convertTo(final StockLevelAdjustmentReason source, final Type<String> destinationType, final MappingContext mappingContext)
	{
		return source.toString();
	}
}
