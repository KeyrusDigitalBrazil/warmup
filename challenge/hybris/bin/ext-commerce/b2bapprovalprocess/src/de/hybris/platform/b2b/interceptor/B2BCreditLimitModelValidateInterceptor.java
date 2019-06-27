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

import de.hybris.platform.b2b.model.B2BCreditLimitModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Required;


public class B2BCreditLimitModelValidateInterceptor implements ValidateInterceptor
{

	private L10NService l10NService;

	@Override
	public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException
	{
		if (model instanceof B2BCreditLimitModel)
		{
			final B2BCreditLimitModel creditLimit = (B2BCreditLimitModel) model;

			//Both Date Period and Date Range cannot be present together
			if (creditLimit.getDatePeriod() != null && creditLimit.getDateRange() != null)
			{
				throw new InterceptorException(getL10NService().getLocalizedString("error.dateperiodorrange.bothpresent"));
			}
			else if (creditLimit.getDatePeriod() == null && creditLimit.getDateRange() == null)
			{
				throw new InterceptorException(getL10NService().getLocalizedString("error.dateperiodorrange.missing"));
			}

			if (creditLimit.getAmount() != null && creditLimit.getAmount().compareTo(BigDecimal.ZERO) <= 0)
			{
				throw new InterceptorException(getL10NService().getLocalizedString("error.amount.lessoreqaulszero"));
			}
			if (creditLimit.getAlertThreshold() != null && creditLimit.getAlertThreshold().compareTo(BigDecimal.ZERO) <= 0)
			{
				throw new InterceptorException(getL10NService().getLocalizedString("error.threshold.lessoreqaulszero"));
			}
		}
	}

	@Required
	public void setL10NService(final L10NService l10NService)
	{
		this.l10NService = l10NService;
	}

	protected L10NService getL10NService()
	{
		return l10NService;
	}
}
