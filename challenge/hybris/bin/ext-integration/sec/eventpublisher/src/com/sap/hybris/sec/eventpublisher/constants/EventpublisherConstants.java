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
package com.sap.hybris.sec.eventpublisher.constants;

/**
 * Global class for all Eventpublisher constants. You can add global constants for your extension into this class.
 */
public final class EventpublisherConstants extends GeneratedEventpublisherConstants
{

	public static final String PLATFORM_LOGO_CODE = "EventPublisherPlatformLogo";

	public static final String QUOTES = "\"";
	public static final String ADDITIONAL_CODE = "additionalCode";

	public static final String BASE_URL = "sap.secintegration.hci.baseurl";

	public static final String USERNAME = "sap.secintegration.hci.username";
	public static final String PASSWORD = "sap.secintegration.hci.password";

	public static final String HCI_PROJECT_PATH = "sap.secintegration.hci.project.path";
	public static final String CUSTOMER_PATH = "sap.secintegration.hci.customer.path";
	public static final String ADDRESS_PATH = "sap.secintegration.hci.customer.address.path";
	public static final String ORDER_PATH = "sap.secintegration.hci.order.path";
	public static final String RETURN_REQUEST_PATH = "sap.secintegration.scpi.return.request.path";

	public static final String SHIPPING_ADDRESS = "shippingAddress";
	public static final String BILLING_ADDRESS = "billingAddress";

	public static final String BACKSLASH = "/";

	public static final String SCPI_PROXY_URL = "sap.secintegration.hci.proxy.url";

	public static final String BD_TYPE = "sap.secintegration.bdtype";
	public static final String DATE_FORMATTER_TYPE = "sap.secintegration.dateformat.type";
	public static final String CUSTOMER_ID_ATTRIBUTE = "sap.secintegration.customer.attribute.id";

	public static final int CUSTOMER_MODEL_TYPECODE = 4;
	public static final int ADDRESS_MODEL_TYPECODE = 23;
	public static final int ORDER_MODEL_TYPECODE = 45;
	public static final int RETURN_REQUEST_MODEL_TYPECODE = 2051;
	public static final String CODE_CONSTANT = "code";
	public static final String PORT_CONSTANT = "port";

	public static final String WEBSOCKET_SERVER_ENDPOINT_INTERNALCONTEXT = "internalcontext";
	public static final String WEBSOCKET_SERVER_ENDPOINT_CLIENTCONTEXT = "clientcontext";
	public static final String WEBSOCKET_SERVER_ENDPOINT_PATH = "/hybrisCustomer/hybrisAgent";
	public static final String WEBSOCKET_SERVER_ENDPOINT_BASE_URL = "ws://localhost:{" + PORT_CONSTANT
			+ "}/eventpublisher/ordersocket/";
	public static final String WEBSOCKET_SERVER_ENDPOINT_PORT = "tomcat.http.port";
	public static final String WEBSOCKET_SERVER_ENDPOINT_HOST_INTERNAL = "sap.secintegration.internal.server.host";
	public static final String ORDER_MODIFICATION_TYPE = "create";

	public static final String HCI_PUBLICATION_STATUS_OK = "200";
	public static final String HCI_PUBLICATION_STATUS_CREATED = "201";
	public static final String ORDER_ID = "id";
	public static final String ORDER_CREATED = "order-created";
	public static final String ORDER_UPDATED = "order-updated";
	public static final String ORDER_DELETED = "order-deleted";
	public static final String RETURN_CREATED = "return-created";
	public static final String RETURN_UPDATED = "return-updated";
	public static final String RETURN_DELETED = "return-deleted";
	public static final String WEBSOCKET_SERVER_ENDPOINT_ORDERS="orders";
	public static final String WEBSOCKET_MESSAGE_SOURCE="sap.secintegration.websocket.msgsource";
	public static final String WEBSOCKET_CURRENT_METHOD="sap.secintegration.websocket.current.method";
	public static final String WEBSOCKET_NEXT_METHOD="sap.secintegration.websocket.next.method";
	public static final String  WEBSOCKET_RESPONSE_IS_NEW_OBJECT="sap.secintegration.websocket.new.object";
	public static final int WEBSOCKET_RESPONSE_OK_VALUE=0;
	
	

	public static final String SSO_COOKIE_NAME = "sso.cookie.name";

	public static final String AFTER_ORDER_SAVE_EVENT_HANDLER = "afterOrderSaveEventHandler";
	public static final String REPLICATED_TYPE_CODES = "sap.cecintegration.replicated.type.codes";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String REPLICATED_TYPE_DATA = "sap.cecintegration.data";
	public static final String REPLICATED_CUSTOMER_DATA = "sap.cecintegration.data.customer";
	public static final String HCI_PUBLISH = "sap.secintegration.hci";
	public static final String PATH = "path";
	public static final String OPEN_BRACE = "{";
	public static final String CLOSE_BRACE = "}";
	public static final String EMPTY_STRING = "";
	public static final String GET = "get";
	public static final String ORDER = "Order";
	public static final String CUSTOMER = "Customer";
	public static final String ADDRESS = "Address";
	public static final String RETURN_REQUEST = "ReturnRequest";
	public static final String MINUS_ONE = "-1";
	public static final String IS = "is";
	public static final String SEC_INTEGRATION_IS_ACTIVE = "sap.secintegration.active";

	private EventpublisherConstants()
	{
		//empty to avoid instantiating this constant class
	}


}

