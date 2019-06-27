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
package de.hybris.platform.consignmenttrackingfacades.populators;

import de.hybris.platform.consignmenttrackingfacades.delivery.data.CarrierData;
import de.hybris.platform.consignmenttrackingservices.model.CarrierModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.i18n.I18NService;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;


/**
 * A default implementation of Carrier Populator
 */
public class DefaultCarrierPopulator implements Populator<CarrierModel, CarrierData>
{
	private I18NService i18NService;

	public I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setI18NService(final I18NService i18nService)
	{
		i18NService = i18nService;
	}

	@Override
	public void populate(final CarrierModel source, final CarrierData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setName(source.getName(i18NService.getCurrentLocale()));
	}

}
