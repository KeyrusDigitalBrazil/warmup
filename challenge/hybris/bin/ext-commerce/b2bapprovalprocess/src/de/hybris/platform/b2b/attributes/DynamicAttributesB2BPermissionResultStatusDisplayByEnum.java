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
package de.hybris.platform.b2b.attributes;

import de.hybris.platform.b2b.model.B2BPermissionResultModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.orderhandler.DynamicAttributesOrderStatusDisplayByEnum;
import de.hybris.platform.servicelayer.model.attribute.DynamicAttributeHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;



public class DynamicAttributesB2BPermissionResultStatusDisplayByEnum implements
		DynamicAttributeHandler<String, B2BPermissionResultModel>
{
	private EnumerationService enumerationService;
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DynamicAttributesOrderStatusDisplayByEnum.class);

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	@Override
	public String get(final B2BPermissionResultModel permissionResult)
	{
		final String ret = StringUtils.EMPTY;
		if (permissionResult == null)
		{
			throw new IllegalArgumentException("Item model is required");
		}
		if (permissionResult.getStatus() == null)
		{
			return ret;
		}
		return enumerationService.getEnumerationName(permissionResult.getStatus());
	}

	@Override
	public void set(final B2BPermissionResultModel model, final String value)
	{
		throw new UnsupportedOperationException();
	}

}
