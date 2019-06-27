/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import de.hybris.platform.sap.core.common.message.Message;


/**
 * 
 */
public class OrderMgmtMessage extends Message
{
	private String processStep = "";

	/**
	 * @param type
	 */
	public OrderMgmtMessage(final int type)
	{
		super(type);
	}

	/**
	 * @return the processStep
	 */
	public String getProcessStep()
	{
		return processStep;
	}

	/**
	 * @param processStep
	 *           the processStep to set
	 */
	public void setProcessStep(final String processStep)
	{
		this.processStep = processStep;
	}

}
