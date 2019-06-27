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
package de.hybris.platform.chinesepspalipayservices.constants;

import de.hybris.platform.payment.dto.TransactionStatus;

import java.util.HashMap;
import java.util.Map;


/**
 * Payment constants
 */
public interface PaymentConstants
{
	/**
	 * Basic request constants
	 */

	interface Basic
	{
		String EXTENSIONNAME = "chinesepspalipayservices";

		String BANK_PAY_METHOD = "bankPay";

		String INSTANT_PAY_METHOD = "directPay";

		String EXPRESS_PAY_METHOD = "expressGateway";

		String SEC_ID = "0001";

		String INPUT_CHARSET = "utf-8";

		String SIGN_TYPE = "MD5";

		String DEFAULT_LOGIN = "Y";

		String PAYMENT_PROVIDER = "Alipay";

		String MOBILE_FORMAT = "xml";

		String MOBILE_REQUEST_VERSION = "2.0";

		String MOBILE_REQUEST_TYPE = "POST";

		String RESPONSE_ROOT = "alipay";

		String RESPONSE_ATTR_PARAM = "param";

		String REFUND_BATCH_NUM = "1";

		interface PaymentType
		{
			String BUY_PRODUCT = "1";
			String DONATE = "4";
		}
	}

	interface ErrorHandler
	{
		String OUT_TRADE_NO = "out_trade_no";
		String ERROR_CODE = "error_code";
	}

	/**
	 * Controller constants
	 */
	interface Controller
	{
		String _Prefix = "checkout/multi/summary" + "/alipay/";
		String _Suffix = "Controller";
		String PSPSYNCRESPONSE = "pspsyncresponse/";
		String PSPASYNRESPONSE = "pspasynresponse/";

		String DIRECT_AND_EXPRESS_RETURN_URL = _Prefix + PSPSYNCRESPONSE + "return" + _Suffix;
		String DIRECT_AND_EXPRESS_NOTIFY_URL = _Prefix + PSPASYNRESPONSE + "notify" + _Suffix;
		String ERROR_NOTIFY_URL = _Prefix + PSPASYNRESPONSE + "error" + _Suffix;
		String WAP_RETURN_URL = _Prefix + "mobile/return" + _Suffix;
		String WAP_NOTIFY_URL = _Prefix + "mobile/notify" + _Suffix;
		String GET_REFUND_URL = _Prefix + "refund" + _Suffix;
		String REFUND_NOTIFY_URL = _Prefix + PSPASYNRESPONSE + "refundnotify" + _Suffix;
	}


	interface HTTP
	{

		String METHOD_POST = "POST";
		String METHOD_GET = "GET";

	}

	class TransactionStatusMap
	{
		
		static final Map<String, TransactionStatus> AlipayToHybris = new HashMap<String, TransactionStatus>();
		
		private TransactionStatusMap()
		{
			throw new IllegalAccessError("TransactionStatusMap class");
		}

		public static Map<String, TransactionStatus> getAlipaytohybris()
		{
			return AlipayToHybris;
		}

		static
		{
			AlipayToHybris.put("TRADE_SUCCESS", TransactionStatus.ACCEPTED);
			AlipayToHybris.put("TRADE_FINISHED", TransactionStatus.FINISHED);
			AlipayToHybris.put("TRADE_PENDING", TransactionStatus.REVIEW);
			AlipayToHybris.put("TRADE_CLOSED", TransactionStatus.CLOSED);
			AlipayToHybris.put("WAIT_BUYER_PAY", null);
		}

	}


}
