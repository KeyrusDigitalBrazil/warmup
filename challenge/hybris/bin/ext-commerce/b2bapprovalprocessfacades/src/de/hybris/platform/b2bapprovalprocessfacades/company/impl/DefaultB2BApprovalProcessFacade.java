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
package de.hybris.platform.b2bapprovalprocessfacades.company.impl;

import de.hybris.platform.b2b.process.approval.services.B2BApprovalProcessService;
import de.hybris.platform.b2bapprovalprocessfacades.company.B2BApprovalProcessFacade;

import java.util.Map;

import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link B2BApprovalProcessFacade}
 */
public class DefaultB2BApprovalProcessFacade implements B2BApprovalProcessFacade
{
	private B2BApprovalProcessService b2bApprovalProcessService;

	@Override
	public Map<String, String> getProcesses()
	{
		return getB2bApprovalProcessService().getProcesses(null);
	}

	protected B2BApprovalProcessService getB2bApprovalProcessService()
	{
		return b2bApprovalProcessService;
	}

	@Required
	public void setB2bApprovalProcessService(final B2BApprovalProcessService b2bApprovalProcessService)
	{
		this.b2bApprovalProcessService = b2bApprovalProcessService;
	}
}
