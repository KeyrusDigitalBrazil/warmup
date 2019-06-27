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
package de.hybris.platform.sap.model.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.sapmodel.model.SAPDeliveryModeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

/**
 * Populator for additional fields specific for the delivery mode mapping
 */
public class DefaultDeliveryModeMappingPopulator implements Populator<SAPDeliveryModeModel, Map<String, Object>>
{

	public void populate(final SAPDeliveryModeModel source, final Map<String, Object> target) throws ConversionException
	{
		target.put("deliveryMode", source.getDeliveryMode().getCode());
	}

}
