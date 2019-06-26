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
package de.hybris.platform.b2b.process.approval.actions;

import de.hybris.platform.b2b.enums.MerchantCheckStatus;
import de.hybris.platform.b2b.model.B2BMerchantCheckResultModel;
import java.util.Collection;


public class B2BMerchantCheckResultHelperImpl implements B2BMerchantCheckResultHelper
{
	@Override
	public Collection<B2BMerchantCheckResultModel> filterResultByMerchantCheckResultStatus(
			final Collection<B2BMerchantCheckResultModel> result, final MerchantCheckStatus status)
	{
		return null;
	}

}
