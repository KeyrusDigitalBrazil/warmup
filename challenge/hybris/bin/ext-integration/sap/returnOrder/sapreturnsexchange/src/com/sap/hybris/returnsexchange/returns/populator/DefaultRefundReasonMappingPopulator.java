/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.sap.hybris.returnsexchange.returns.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

import com.sap.hybris.returnsexchange.model.SapReturnOrderReasonModel;


public class DefaultRefundReasonMappingPopulator implements Populator<SapReturnOrderReasonModel, Map<String, Object>>
{

	@Override
	public void populate(final SapReturnOrderReasonModel source, final Map<String, Object> target) throws ConversionException
	{
		target.put(SapReturnOrderReasonModel.REFUNDREASON, source.getRefundReason().getCode());
	}
}
