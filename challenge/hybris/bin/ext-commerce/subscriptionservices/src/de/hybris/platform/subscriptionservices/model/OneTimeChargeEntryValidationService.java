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
 * Validation service for one time charge entries.
 */
public interface OneTimeChargeEntryValidationService
{
	/**
	 * Validates the given collection of {@link OneTimeChargeEntryModel} instances.
	 *
	 * @param oneTimeChargeEntries
	 *           the one time charge entries to validate
	 * @return a collection of validation messages
	 */
	@Nonnull
	Collection<String> validate(@Nullable Collection<OneTimeChargeEntryModel> oneTimeChargeEntries);
}
