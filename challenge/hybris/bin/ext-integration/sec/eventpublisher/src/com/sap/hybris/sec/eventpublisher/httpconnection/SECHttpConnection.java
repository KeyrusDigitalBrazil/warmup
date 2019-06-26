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
package com.sap.hybris.sec.eventpublisher.httpconnection;

import java.io.IOException;

import com.sap.hybris.sec.eventpublisher.data.ResponseData;


/**
 *
 */
public interface SECHttpConnection
{
	ResponseData sendPost(final String pathUrl, final String body) throws IOException;
}
