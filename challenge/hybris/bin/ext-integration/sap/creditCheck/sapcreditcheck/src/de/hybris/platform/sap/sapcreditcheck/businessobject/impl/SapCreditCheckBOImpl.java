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
package de.hybris.platform.sap.sapcreditcheck.businessobject.impl;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapcreditcheck.backend.SapCreditCheckBackend;
import de.hybris.platform.sap.sapcreditcheck.businessobject.SapCreditCheckBO;
import de.hybris.platform.sap.sapcreditcheck.exceptions.SapCreditCheckException;


/**
 *
 */
@BackendInterface(SapCreditCheckBackend.class)
public class SapCreditCheckBOImpl extends BusinessObjectBase implements SapCreditCheckBO
{
	
	/**
	 * @return the sapCreditCheckBackend
	 * @throws BackendException
	 */
	public SapCreditCheckBackend getSapCreditCheckBackend() throws BackendException
	{
	  return  (SapCreditCheckBackend) getBackendBusinessObject();
	}

	@Override
	public boolean checkOrderCreditBlocked(final String orderCode)
	{
		try
		{
			return getSapCreditCheckBackend().checkOrderCreditBlocked(orderCode);
		}
		catch (final BackendException e)
		{
			throw new SapCreditCheckException(e);
		}
	}
	
	@Override
	public boolean checkCreditLimitExceeded(final AbstractOrderData orderData, final String soldTo)
	{
		try
		{
			return getSapCreditCheckBackend().checkCreditLimitExceeded(orderData, soldTo);
		}
		catch (final BackendException e)
		{
			throw new SapCreditCheckException(e);
		}
	}

}
