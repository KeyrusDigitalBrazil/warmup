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
package de.hybris.platform.acceleratorservices.web.payment.validation;


import de.hybris.platform.acceleratorservices.util.CalendarHelper;
import de.hybris.platform.acceleratorservices.web.payment.forms.PaymentDetailsForm;
import de.hybris.platform.acceleratorservices.web.payment.forms.SopPaymentDetailsForm;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component("sopPaymentDetailsValidator")
public class SopPaymentDetailsValidator implements Validator
{
	private static final String PAYMENT_START_DATE_INVALID = "payment.startDate.invalid";

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return PaymentDetailsForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final SopPaymentDetailsForm form = (SopPaymentDetailsForm) object;

		final Calendar startOfCurrentMonth = CalendarHelper.getCalendarResetTime();
		startOfCurrentMonth.set(Calendar.DAY_OF_MONTH, 1);

		final Calendar startOfNextMonth =CalendarHelper. getCalendarResetTime();
		startOfNextMonth.set(Calendar.DAY_OF_MONTH, 1);
		startOfNextMonth.add(Calendar.MONTH, 1);

		final Calendar start = CalendarHelper.parseDate(form.getCard_startMonth(), form.getCard_startYear());
		final Calendar expiration = CalendarHelper.parseDate(form.getCard_expirationMonth(), form.getCard_expirationYear());

		if (start != null && !start.before(startOfNextMonth))
		{
			errors.rejectValue("card_startMonth", PAYMENT_START_DATE_INVALID);
		}
		if (expiration != null && expiration.before(startOfCurrentMonth))
		{
			errors.rejectValue("card_expirationMonth", PAYMENT_START_DATE_INVALID);
		}
		if (start != null && expiration != null && start.after(expiration))
		{
			errors.rejectValue("card_startMonth", PAYMENT_START_DATE_INVALID);
		}

		if (StringUtils.isBlank(form.getBillTo_country()))
		{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_country", "address.country.invalid");
		}
		else
		{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_firstName", "address.firstName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_lastName", "address.lastName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_street1", "address.line1.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_city", "address.city.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_postalCode", "address.postcode.invalid");
		}
	}
}
