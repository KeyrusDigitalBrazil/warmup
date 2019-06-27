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
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;


/**
 * Bill To Partner information. <br>
 * 
 */
public class BillToImpl extends PartnerBaseImpl implements BillTo
{

	private static final long serialVersionUID = 1L;

	@Override
	public BillToImpl clone()
	{
		return (BillToImpl) super.clone();
	}

}