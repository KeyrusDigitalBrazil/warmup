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
package de.hybris.platform.savedorderforms.services.impl;

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.savedorderforms.daos.OrderFormDao;
import de.hybris.platform.savedorderforms.model.OrderFormModel;
import de.hybris.platform.savedorderforms.services.OrderFormService;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

import static de.hybris.platform.servicelayer.util.ServicesUtil.*;

/**
 * Default implementation of the {@link de.hybris.platform.savedorderforms.services.OrderFormService}.
 */
public class DefaultOrderFormService implements OrderFormService
{
	private OrderFormDao orderFormDao;

	@Override
	public OrderFormModel getOrderFormForCode(final String code)
	{
		validateParameterNotNull(code, "Parameter code must not be null");

        final OrderFormModel orderFormModel = getOrderFormDao().findOrderFormByCode(code);

		return orderFormModel;
	}

    @Override
    public List<OrderFormModel> getOrderFormsForUser(UserModel userModel)
    {
        validateParameterNotNull(userModel, "Parameter userModel must not be null");

        final List<OrderFormModel> orderFormModels = getOrderFormDao().findOrderFormsForUser(userModel);

        return orderFormModels;
    }

    @Required
    public void setOrderFormDao(final OrderFormDao orderFormDao)
    {
        this.orderFormDao = orderFormDao;
    }

    protected OrderFormDao getOrderFormDao()
    {
        return orderFormDao;
    }

}
