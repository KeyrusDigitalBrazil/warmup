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

import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;


/**
 * Default implementation of a ShipTo
 * 
 */
public class ShipToImpl extends PartnerBaseImpl implements ShipTo
{

	private static final long serialVersionUID = 1L;

	@Override
	public ShipToImpl clone()
	{
		final ShipToImpl partnerToClone = (ShipToImpl) super.clone();
		return partnerToClone;
	}
}