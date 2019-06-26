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
package de.hybris.platform.b2b.process.approval.services.impl;

import de.hybris.platform.b2b.process.approval.services.B2BApprovalProcessService;
import de.hybris.platform.b2b.strategies.B2BApprovalProcessLookUpStrategy;
import de.hybris.platform.store.BaseStoreModel;

import java.util.Map;


/**
 * Default implementation of {@link B2BApprovalProcessService}
 */
public class DefaultB2BApprovalProcessService implements B2BApprovalProcessService
{
	private B2BApprovalProcessLookUpStrategy b2bApprovalProcessLookUpStrategy;

	@Override
	public Map<String, String> getProcesses(final BaseStoreModel store)
	{
		return getB2bApprovalProcessLookUpStrategy().getProcesses(store);
	}

	public B2BApprovalProcessLookUpStrategy getB2bApprovalProcessLookUpStrategy()
	{
		return b2bApprovalProcessLookUpStrategy;
	}

	public void setB2bApprovalProcessLookUpStrategy(final B2BApprovalProcessLookUpStrategy b2bApprovalProcessLookUpStrategy)
	{
		this.b2bApprovalProcessLookUpStrategy = b2bApprovalProcessLookUpStrategy;
	}
}
