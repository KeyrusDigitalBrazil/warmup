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
package de.hybris.platform.b2b.process.approval.event;

import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;


/**
 * An event that is fired when a B2B order approval process starts
 */
public class ApprovalProcessStartEvent extends AbstractEvent
{
	private B2BApprovalProcessModel b2BApprovalProcessModel;

	public ApprovalProcessStartEvent(final B2BApprovalProcessModel b2BApprovalProcessModel)
	{
		super();
		this.b2BApprovalProcessModel = b2BApprovalProcessModel;
	}

	public B2BApprovalProcessModel getB2BApprovalProcessModel()
	{
		return b2BApprovalProcessModel;
	}
}
