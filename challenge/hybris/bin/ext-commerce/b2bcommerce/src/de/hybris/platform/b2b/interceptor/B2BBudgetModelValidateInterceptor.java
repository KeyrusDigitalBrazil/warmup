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
package de.hybris.platform.b2b.interceptor;

import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.springframework.beans.factory.annotation.Required;


/**
 * This interceptor ensures that all new B2B Customers are associated with a B2BUnit {@link B2BBudgetModel},
 * {@link B2BBudgetModel}
 */
public class B2BBudgetModelValidateInterceptor implements ValidateInterceptor
{
	private L10NService l10NService;

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof B2BBudgetModel)
		{

			final B2BBudgetModel budget = (B2BBudgetModel) model;
			final B2BUnitModel parentUnit = budget.getUnit();

			if (budget.getActive().booleanValue() && !parentUnit.getActive().booleanValue())
			{
				throw new InterceptorException(getL10NService().getLocalizedString("error.budget.parentunit.disabled"));
			}
		}
	}

	public L10NService getL10NService()
	{
		return l10NService;
	}


	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}

}
