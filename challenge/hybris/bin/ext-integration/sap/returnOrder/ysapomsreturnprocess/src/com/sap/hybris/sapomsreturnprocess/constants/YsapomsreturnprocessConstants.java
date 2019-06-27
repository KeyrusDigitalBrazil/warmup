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
package com.sap.hybris.sapomsreturnprocess.constants;

/**
 * Global class for all Ysapomsreturnprocess constants. You can add global constants for your extension into this class.
 */
public final class YsapomsreturnprocessConstants
{
	public static final String EXTENSIONNAME = "ysapomsreturnprocess";

	public static class Attributes
	{
		public static class ConsignmentEntry
		{
			public static final String AMOUNT = "amount".intern();
			public static final String QUANTITYRETURNEDUPTIL = "quantityReturnedUptil".intern();
			public static final String RETURNQUANTITY = "returnQuantity".intern();

			private ConsignmentEntry()
			{
				//empty to avoid instantiating this constant class
			}
		}

		public static class ReturnRequest
		{
			public static final String SAPLOGICALSYSTEM = "sapLogicalSystem".intern();
			public static final String SAPRETURNREQUESTS = "sapReturnRequests".intern();
			public static final String SAPSALESORGANIZATION = "sapSalesOrganization".intern();
			public static final String SAPSYSTEMTYPE = "sapSystemType".intern();

			private ReturnRequest()
			{
				//empty to avoid instantiating this constant class
			}
		}

		public static class SAPPlantLogSysOrg
		{
			public static final String REASONCODECANCELLATION = "reasonCodeCancellation".intern();

			private SAPPlantLogSysOrg()
			{
				//empty to avoid instantiating this constant class
			}
		}

		private Attributes()
		{
			//empty to avoid instantiating this constant class
		}
	}

	public static class TC
	{
		public static final String SAPRETURNREQUESTORDERSTATUS = "SAPReturnRequestOrderStatus".intern();
		public static final String SAPRETURNREQUESTS = "SAPReturnRequests".intern();

		private TC()
		{
			//empty to avoid instantiating this constant class
		}
	}

	public static final String PLATFORM_LOGO_CODE = "ysapomsreturnprocessPlatformLogo";
	public static final String MISSING_SALES_ORG = "MISSING_SALES_ORG";
	public static final String MISSING_LOGICAL_SYSTEM = "MISSING_LOGICAL_SYSTEM";
	public static final String UNDERSCORE = "_";
	public static final String COMMA = ",";
	public static final String PIPE = "|";

	private YsapomsreturnprocessConstants()
	{
		//empty to avoid instantiating this constant class
	}


}
