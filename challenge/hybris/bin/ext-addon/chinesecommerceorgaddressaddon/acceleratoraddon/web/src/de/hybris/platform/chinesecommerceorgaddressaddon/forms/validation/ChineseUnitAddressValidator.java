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
package de.hybris.platform.chinesecommerceorgaddressaddon.forms.validation;

import de.hybris.platform.acceleratorstorefrontcommons.forms.validation.AddressValidator;
import de.hybris.platform.addressfacades.address.AddressFacade;
import de.hybris.platform.chinesecommerceorgaddressaddon.forms.ChineseUnitAddressForm;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;


/**
 * Validator for Chinese address form
 */
@Component("chineseUnitAddressValidator")
public class ChineseUnitAddressValidator extends AddressValidator
{
	private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
	private static final String[] IGNORE_FIELDS = new String[]
	{ "firstName", "lastName", "townCity", "postcode" }; // ignore the validation annotations defined in AddressForm
	private static final String CN = "CN";

	@Resource(name = "chineseAddressFacade")
	private AddressFacade addressFacade;

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return ChineseUnitAddressForm.class.isAssignableFrom(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final ChineseUnitAddressForm addressForm = (ChineseUnitAddressForm) object;
		for (final ConstraintViolation<ChineseUnitAddressForm> e : VALIDATOR.validate(addressForm))
		{
			if (!ArrayUtils.contains(IGNORE_FIELDS, e.getPropertyPath().toString()))
			{
				final String msgKey = StringUtils.substringBetween(e.getMessage(), "{", "}");
				errors.rejectValue(e.getPropertyPath().toString(), msgKey);
			}
		}

		final String postcode = addressForm.getPostcode();
		if (StringUtils.isNotBlank(postcode) && !addressFacade.validatePostcode(postcode))
		{
			errors.rejectValue("postcode", "address.post.code.invalid");
		}

		final String regionIso = addressForm.getRegionIso();
		if (StringUtils.isEmpty(regionIso))
		{
			if (CN.equals(addressForm.getCountryIso()))
			{
				errors.rejectValue("regionIso", "address.province.required");
			}
			else
			{
				errors.rejectValue("regionIso", "address.region.required");
			}
		}
	}
}
