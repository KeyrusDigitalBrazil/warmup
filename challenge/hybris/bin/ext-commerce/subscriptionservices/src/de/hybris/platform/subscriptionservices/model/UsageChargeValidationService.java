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
package de.hybris.platform.subscriptionservices.model;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 * Validation service for usage charge.
 */
public interface UsageChargeValidationService
{
	/**
	 * Validates given usage charge models.
	 * 
	 * @param usageCharges
	 *           Usage charge models to be validated
	 * @return Validation messages
	 */
	@Nonnull
	Collection<String> validate(@Nullable Collection<UsageChargeModel> usageCharges);
}
