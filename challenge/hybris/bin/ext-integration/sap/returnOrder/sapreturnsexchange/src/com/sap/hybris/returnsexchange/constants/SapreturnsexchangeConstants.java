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
package com.sap.hybris.returnsexchange.constants;

/**
 * Global class for all Sapreturnsexchange constants. You can add global constants for your extension into this class.
 */
public final class SapreturnsexchangeConstants 
{
	
	public static final String EXTENSIONNAME = "sapreturnsexchange";

	public static class TC
	{
		public static final String SAPRETURNORDERREASON = "SapReturnOrderReason".intern();
		private TC()
		{
			//empty to avoid instantiating this constant class
		}
	}
	
	public static class Attributes
	{
		public static class ReturnRequest
		{
			public static final String DELIVERYDOCNUMBERS = "deliveryDocNumbers".intern();
			public static final String REASONCODECANCELLATION = "reasonCodeCancellation".intern();
			
			private ReturnRequest()
			{
				//empty to avoid instantiating this constant class
			}
		}
		public static class SAPConfiguration
		{
			public static final String RETURNORDERPROCESSTYPE = "returnOrderProcesstype".intern();
			public static final String SAPRETURNREASONS = "sapReturnReasons".intern();
			public static final String RETURNORDERREASON = "returnOrderReason".intern();
			
			private SAPConfiguration()
			{
				//empty to avoid instantiating this constant class
			}
		}
		private Attributes()
		{
			//empty to avoid instantiating this constant class
		}
	}
	
	public static final String RETURNORDER_CONFIRMATION_EVENT = "ReturnRequestCreationEvent_";
	public static final String RETURNORDER_GOOD_EVENT = "ApproveOrCancelGoodsEvent_";
	public static final String RETURNORDER_PAYMENT_REVERSAL_EVENT = "PaymentReversalEvent_";
	public static final String RETURNORDER_CANCELLATION_CONFIRMATION_EVENT = "CancelReturnRequestConfirmationEvent_";
	public static final String CODE = "code";
	public static final String SEPERATING_SYMBOL = "#";
	public static final String PLATFORM_LOGO_CODE = "sapreturnsexchangePlatformLogo";
	
	private SapreturnsexchangeConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension

}
