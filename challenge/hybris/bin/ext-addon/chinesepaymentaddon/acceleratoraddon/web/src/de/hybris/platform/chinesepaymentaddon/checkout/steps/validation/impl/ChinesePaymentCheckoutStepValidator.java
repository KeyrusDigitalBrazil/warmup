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
package de.hybris.platform.chinesepaymentaddon.checkout.steps.validation.impl;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PaymentDetailsForm;
import de.hybris.platform.chinesepaymentaddon.forms.ChinesePaymentDetailsForm;

import javax.annotation.Resource;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


public class ChinesePaymentCheckoutStepValidator implements Validator
{

	@Resource(name = "checkoutFlowFacade")
	private CheckoutFlowFacade checkoutFlowFacade;

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return PaymentDetailsForm.class==aClass;
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{

		final ChinesePaymentDetailsForm form = (ChinesePaymentDetailsForm) object;
		if (form.getPaymentId() == null)
		{
			errors.rejectValue("PaymentId", "payment.PaymentId.invalid");
		}

	}

}
