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
package de.hybris.platform.sapdigitalpaymentaddon.controllers.pages.checkout.validation;


import de.hybris.platform.sapdigitalpaymentaddon.forms.BillingAddressDetailsForm;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 *
 * Billing address validator. Cheks the billing address fields on submitting the BillingAddressDetailsForm form
 *
 */
@Component("billingAddressValidator")
public class BillingAddressValidator implements Validator
{

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.validation.Validator#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(final Class<?> aClass)
	{
		return BillingAddressDetailsForm.class.equals(aClass);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
	 */
	@Override
	public void validate(final Object object, final Errors errors)
	{
		final BillingAddressDetailsForm form = (BillingAddressDetailsForm) object;
		if (!form.isUseDeliveryAddress())
		{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.titleCode", "address.title.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.firstName", "address.firstName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.lastName", "address.lastName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.line1", "address.line1.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.townCity", "address.townCity.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.postcode", "address.postcode.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.countryIso", "address.country.invalid");
			// ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billingAddress.line2", "address.line2.invalid"); // for some addresses this field is required by cybersource
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj)
	{
		return super.equals(obj);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		// YTODO Auto-generated method stub
		return super.hashCode();
	}



}
