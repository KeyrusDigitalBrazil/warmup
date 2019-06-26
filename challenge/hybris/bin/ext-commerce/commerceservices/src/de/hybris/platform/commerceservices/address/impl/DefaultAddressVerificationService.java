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
package de.hybris.platform.commerceservices.address.impl;

import de.hybris.platform.commerceservices.address.AddressErrorCode;
import de.hybris.platform.commerceservices.address.AddressFieldType;
import de.hybris.platform.commerceservices.address.AddressVerificationDecision;
import de.hybris.platform.commerceservices.address.AddressVerificationService;
import de.hybris.platform.commerceservices.address.data.AddressFieldErrorData;
import de.hybris.platform.commerceservices.address.data.AddressVerificationResultData;
import de.hybris.platform.core.model.user.AddressModel;


/**
 * Default implementation of {@link AddressVerificationService}.
 */
public class DefaultAddressVerificationService implements
		AddressVerificationService<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>>
{
	@Override
	public AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>> verifyAddress(
			final AddressModel addressModel)
	{
		return new AddressVerificationResultData<AddressVerificationDecision, AddressFieldErrorData<AddressFieldType, AddressErrorCode>>();
	}

	@Override
	public boolean isCustomerAllowedToIgnoreSuggestions()
	{
		return true;
	}
}
