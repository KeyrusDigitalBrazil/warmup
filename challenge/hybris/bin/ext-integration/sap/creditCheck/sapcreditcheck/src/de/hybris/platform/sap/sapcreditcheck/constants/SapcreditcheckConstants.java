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
package de.hybris.platform.sap.sapcreditcheck.constants;

/**
 * Global class for all Sapcreditcheck constants. You can add global constants for your extension into this class.
 */
public final class SapcreditcheckConstants 
{
	public static final String EXTENSIONNAME = "sapcreditcheck";
	public static final String SAP_CREDTI_CHECK_BO = "sapCreditCheckBO";
	public static final String ERP_LORD_GET_ALL = "ERP_LORD_GET_ALL";
	public static final String BAPI_CREDITCHECK = "BAPI_CREDITCHECK";
	public static final String IV_TRTYP = "A";
	public static final String SAPCREDITCHECK_CCACTIVE = "sapcreditcheckactive";
	public static final String CREDIT_GROUP = "01";
	public static final String CREDIT_BLOCKED_STATUS = "B";
	public static final String CART_CHECKOUT_CREDITCHECK_EXCEEDED = "cart.creditcheck.exceeded";
	public static final String CART_CHECKOUT_TECHNICAL_ERROR = "cart.creditcheck.technical.error";

	private SapcreditcheckConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
	
	public static class Attributes
	{
		public static class SAPConfiguration
		{
			public static final String SAPCREDITCHECKACTIVE = "sapcreditcheckactive".intern();
		}
	}
}
