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
package de.hybris.platform.chineseprofile.forms;

/**
 * Form object for verify binding mobile number.
 */
public class VerificationCodeForm
{
	private String mobileNumber;

	private String verificationCode;

	private String codeType;

	public VerificationCodeForm()
	{
		super();
	}

	public VerificationCodeForm(final String mobileNumber)
	{
		this.mobileNumber = mobileNumber;
	}

	public String getMobileNumber()
	{
		return mobileNumber;
	}

	public void setMobileNumber(final String mobileNumber)
	{
		this.mobileNumber = mobileNumber;
	}

	public String getVerificationCode()
	{
		return verificationCode;
	}

	public void setVerificationCode(final String verificationCode)
	{
		this.verificationCode = verificationCode;
	}

	public String getCodeType()
	{
		return codeType;
	}

	public void setCodeType(final String codeType)
	{
		this.codeType = codeType;
	}
}
