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
package de.hybris.platform.secureportaladdon.forms;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * Secure portal registration form
 */
public class RegistrationForm
{
	private String titleCode;
	/**
	 * @deprecated use firstName & and lastName to replace
	 */
	@Deprecated
	private String name;
	private String firstName;
	private String lastName;
	private String email;
	private String accountNumber;
	private String position;
	private String telephone;
	private String telephoneExtension;
	private String companyName;
	private String companyAddressStreet;
	private String companyAddressStreetLine2;
	private String companyAddressCity;
	private String companyAddressPostalCode;
	private String companyAddressCountryIso;
	private String message;

	/**
	 * @return the titleCode
	 */
	@NotEmpty(message = "{text.secureportal.register.field.mandatory}")
	public String getTitleCode()
	{
		return titleCode;
	}

	/**
	 * @param titleCode
	 *           the titleCode to set
	 */
	public void setTitleCode(final String titleCode)
	{
		this.titleCode = titleCode;
	}

	/**
	 * @deprecated deprecated form 1811
	 * @return the firstLastName
	 */
	@Deprecated
	public String getName()
	{
		return name;
	}

	/**
	 * @deprecated deprecated form 1811
	 * @param firstLastName
	 *           the firstLastName to set
	 */
	@Deprecated
	public void setName(final String firstAndLastName)
	{
		this.name = firstAndLastName;
	}


	/**
	 * @return the email
	 */
	//LOCALISATION REQUIRED
	@NotEmpty(message = "{register.email.invalid}")
	@Size(min = 1, max = 255, message = "{register.email.invalid}")
	@Email(message = "{register.email.invalid}")
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email
	 *           the email to set
	 */
	public void setEmail(final String email)
	{
		this.email = email;
	}


	// Remove the account number as requested
	/**
	 * @return the accountNumber
	 */
	public String getAccountNumber()
	{
		return accountNumber;
	}

	/**
	 * @param accountNumber
	 *           the accountNumber to set
	 */
	public void setAccountNumber(final String accountNumber)
	{
		this.accountNumber = accountNumber;
	}

	/**
	 * @return the position
	 */
	@NotEmpty(message = "{text.secureportal.register.field.mandatory}")
	public String getPosition()
	{
		return position;
	}

	/**
	 * @param position
	 *           the position to set
	 */
	public void setPosition(final String position)
	{
		this.position = position;
	}

	/**
	 * @return the telephone
	 */
	@NotEmpty(message = "{text.secureportal.register.field.mandatory}")
	public String getTelephone()
	{
		return telephone;
	}

	/**
	 * @param telephone
	 *           the telephone to set
	 */
	public void setTelephone(final String telephone)
	{
		this.telephone = telephone;
	}

	/**
	 * @return the telephoneExtension
	 */
	public String getTelephoneExtension()
	{
		return telephoneExtension;
	}

	/**
	 * @param telephoneExtension
	 *           the telephoneExtension to set
	 */
	public void setTelephoneExtension(final String telephoneExtension)
	{
		this.telephoneExtension = telephoneExtension;
	}

	/**
	 * @return the companyName
	 */
	@NotEmpty(message = "{text.secureportal.register.field.mandatory}")
	public String getCompanyName()
	{
		return companyName;
	}

	/**
	 * @param companyName
	 *           the companyName to set
	 */
	public void setCompanyName(final String companyName)
	{
		this.companyName = companyName;
	}

	/**
	 * @return the companyStreetAddress
	 */
	@NotEmpty(message = "{text.secureportal.register.field.mandatory}")
	public String getCompanyAddressStreet()
	{
		return companyAddressStreet;
	}

	/**
	 * @param companyStreetAddress
	 *           the companyStreetAddress to set
	 */
	public void setCompanyAddressStreet(final String companyStreetAddress)
	{
		this.companyAddressStreet = companyStreetAddress;
	}

	/**
	 * @return the addressLine2
	 */
	public String getCompanyAddressStreetLine2()
	{
		return companyAddressStreetLine2;
	}

	/**
	 * @param companyAddressStreetLine2
	 *           the addressLine2 to set
	 */
	public void setCompanyAddressStreetLine2(final String companyAddressStreetLine2)
	{
		this.companyAddressStreetLine2 = companyAddressStreetLine2;
	}

	/**
	 * @return the companyAddressCity
	 */
	@NotEmpty(message = "{text.secureportal.register.field.mandatory}")
	public String getCompanyAddressCity()
	{
		return companyAddressCity;
	}

	/**
	 * @param companyAddressCity
	 *           the companyAddressCity to set
	 */
	public void setCompanyAddressCity(final String companyAddressCity)
	{
		this.companyAddressCity = companyAddressCity;
	}

	/**
	 * @return the companyAddressPostalCode
	 */
	@NotEmpty(message = "{text.secureportal.register.field.mandatory}")
	public String getcompanyAddressPostalCode()
	{
		return companyAddressPostalCode;
	}

	/**
	 * @param companyAddressPostalCode
	 *           the companyAddressPostalCode to set
	 */
	public void setCompanyAddressPostalCode(final String companyAddressPostalCode)
	{
		this.companyAddressPostalCode = companyAddressPostalCode;
	}

	/**
	 * @return the companyAddressCountryIso
	 */
	@NotEmpty(message = "{text.secureportal.register.field.mandatory}")
	public String getCompanyAddressCountryIso()
	{
		return companyAddressCountryIso;
	}

	/**
	 * @param companyAddressCountryIso
	 *           the companyAddressCountryIso to set
	 */
	public void setCompanyAddressCountryIso(final String companyAddressCountryIso)
	{
		this.companyAddressCountryIso = companyAddressCountryIso;
	}

	/**
	 * @return the message
	 */
	@Length(max = 2000, message = "{text.secureportal.register.field.toolong}")
	public String getMessage()
	{
		return message;
	}

	/**
	 * @param message
	 *           the message to set
	 */
	public void setMessage(final String message)
	{
		this.message = message;
	}


	/**
	 * @return the firstName
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * @param firstName
	 *           the firstName to set
	 */
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * @param lastName
	 *           the lastName to set
	 */
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

}
