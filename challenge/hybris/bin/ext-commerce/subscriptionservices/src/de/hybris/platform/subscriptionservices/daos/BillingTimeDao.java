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
package de.hybris.platform.subscriptionservices.daos;

import de.hybris.platform.subscriptionservices.model.BillingTimeModel;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * Data Access Object for looking up items related to {@link BillingTimeModel}.
 * 
 * @spring.bean billingTimeDao
 */
public interface BillingTimeDao
{
	/**
	 * Find all {@link BillingTimeModel}s.
	 * 
	 * @return {@link List} of {@link BillingTimeModel}s or empty {@link List}.
	 */
	@Nonnull
	List<BillingTimeModel> findAllBillingTimes();

	/**
	 * Finds the {@link BillingTimeModel} for the given code.
	 * 
	 * @param code
	 *           the code of the {@link BillingTimeModel}.
	 * @return {@link BillingTimeModel} if the given <code>code</code> was found
	 * @throws de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
	 *            if nothing was found
	 * @throws de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException
	 *            if by the given search parameters too many models where found
	 */
	@Nonnull
	BillingTimeModel findBillingTimeByCode(@Nonnull String code);

}
