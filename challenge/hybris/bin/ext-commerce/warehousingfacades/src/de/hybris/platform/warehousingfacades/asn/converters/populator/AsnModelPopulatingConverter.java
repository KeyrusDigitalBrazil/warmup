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

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.model.AdvancedShippingNoticeModel;
import de.hybris.platform.warehousingfacades.asn.data.AsnData;

import org.springframework.beans.factory.annotation.Required;


/**
 * Custom converter for converting {@link AsnData} into {@link AdvancedShippingNoticeModel}
 */
public class AsnModelPopulatingConverter extends AbstractPopulatingConverter<AsnData, AdvancedShippingNoticeModel>
{
	private ModelService modelService;

	@Override
	protected AdvancedShippingNoticeModel createTarget()
	{
		return getModelService().create(AdvancedShippingNoticeModel.class);
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}
}
