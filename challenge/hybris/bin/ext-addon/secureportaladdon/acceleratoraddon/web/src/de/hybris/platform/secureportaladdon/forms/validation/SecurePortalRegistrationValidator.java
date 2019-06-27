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
package de.hybris.platform.secureportaladdon.forms.validation;

import de.hybris.platform.secureportaladdon.forms.RegistrationForm;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * Validates the secure portal registration form.
 */
@Component("securePortalRegistrationValidator")
public class SecurePortalRegistrationValidator implements Validator
{
	public static final Pattern EMAIL_REGEX = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return RegistrationForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final RegistrationForm registrationForm = (RegistrationForm) object;
		final String addressLine1 = registrationForm.getCompanyAddressStreet();
		final String addressLine2 = registrationForm.getCompanyAddressStreetLine2();
		final String city = registrationForm.getCompanyAddressCity();
		final String companyName = registrationForm.getCompanyName();
		final String country = registrationForm.getCompanyAddressCountryIso();
		final String email = registrationForm.getEmail();
		final String firstName = registrationForm.getFirstName();
		final String lastName = registrationForm.getLastName();
		final String position = registrationForm.getPosition();
		final String postalCode = registrationForm.getcompanyAddressPostalCode();
		final String telephone = registrationForm.getTelephone();
		final String ext = registrationForm.getTelephoneExtension();
		final String titleCode = registrationForm.getTitleCode();

		validateBlankText(errors, addressLine1, "companyAddressStreet");
		validateBlankText(errors, city, "companyAddressCity");
		validateBlankText(errors, companyName, "companyName");
		validateBlankText(errors, country, "companyAddressCountryIso");
		validateBlankText(errors, firstName, "firstName");
		validateBlankText(errors, lastName, "lastName");
		validateBlankText(errors, position, "position");
		validateBlankText(errors, postalCode, "companyAddressPostalCode");
		validateBlankText(errors, telephone, "telephone");
		validateBlankText(errors, titleCode, "titleCode");
		validateTextLength(errors, addressLine2, "companyAddressStreetLine2");
		validateTextLength(errors, ext, "telephoneExtension");
		validateEmail(errors, email, "email");
	}

	protected void validateBlankText(final Errors errors, final String name, final String propertyName)
	{
		if (StringUtils.isBlank(name))
		{
			errors.rejectValue(propertyName, "text.secureportal.register.field.mandatory");
		}
		else
		{
			validateTextLength(errors, name, propertyName);
		}
	}

	protected void validateTextLength(final Errors errors, final String name, final String propertyName)
	{
		if (!StringUtils.isBlank(name) && StringUtils.length(name) > 255)
		{
			errors.rejectValue(propertyName, "text.secureportal.register.field.toolong");
		}
	}

	protected void validateEmail(final Errors errors, final String email, final String propertyName)
	{
		if (StringUtils.isBlank(email))
		{
			errors.rejectValue(propertyName, "text.secureportal.register.field.mandatory");
		}
		else
		{
			if (!validateEmailAddress(email))
			{
				errors.rejectValue(propertyName, "text.secureportal.register.email.invalid");
			}
			else
			{
				validateTextLength(errors, email, propertyName);
			}
		}
	}

	protected boolean validateEmailAddress(final String email)
	{
		final Matcher matcher = EMAIL_REGEX.matcher(email);
		return matcher.matches();
	}
}
