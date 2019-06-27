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
package de.hybris.platform.subscriptionservices.subscription;

import de.hybris.platform.subscriptionservices.model.BillingTimeModel;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * Billing Time service exposes methods to deal with billing time operations.
 * 
 * @spring.bean billingTimeService
 */
public interface BillingTimeService
{
	/**
	 * This method returns the {@link BillingTimeModel} associated with the code.
	 * 
	 * @param code
	 *           the code
	 * @return The {@link BillingTimeModel}
	 */
	@Nonnull
	BillingTimeModel getBillingTimeForCode(@Nonnull String code);

	/**
	 * This method returns all {@link BillingTimeModel}s.
	 * 
	 * @return All {@link BillingTimeModel}s.
	 */
	@Nonnull
	List<BillingTimeModel> getAllBillingTimes();
}
