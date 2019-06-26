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
package de.hybris.platform.sap.sapcreditcheck.service.impl;

import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapcreditcheck.businessobject.SapCreditCheckBO;
import de.hybris.platform.sap.sapcreditcheck.constants.SapcreditcheckConstants;
import de.hybris.platform.sap.sapcreditcheck.service.SapCreditCheckBOFactory;

import org.springframework.beans.factory.annotation.Required;


/**
 * 
 */
public class DefaultSapCreditCheckBOFactory implements SapCreditCheckBOFactory
{

	private GenericFactory genericFactory;

	/**
	 * 
	 * @return the genericFactory
	 */
	public GenericFactory getGenericFactory()
	{
		return genericFactory;
	}

	/**
	 * 
	 * @param genericFactory
	 */
	@Required
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.sapcreditcheck.service.SapCreditCheckBOFactory#getSapCreditCheckBO()
	 */
	@Override
	public SapCreditCheckBO getSapCreditCheckBO()
	{
		return (SapCreditCheckBO) genericFactory.getBean(SapcreditcheckConstants.SAP_CREDTI_CHECK_BO);

	}


}
