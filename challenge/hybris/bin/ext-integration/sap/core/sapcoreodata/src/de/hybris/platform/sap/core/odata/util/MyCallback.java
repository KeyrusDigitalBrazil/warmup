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
package de.hybris.platform.sap.core.odata.util;

import java.net.URI;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.callback.OnWriteFeedContent;
import org.apache.olingo.odata2.api.ep.callback.WriteCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackResult;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;


/**
 * @deprecated Since 6.4, replace with extension sapymktcommon
 */
@Deprecated
public class MyCallback implements OnWriteFeedContent
{
	protected DataStore dataStore;
	protected URI serviceRoot;

	@Override
	public WriteFeedCallbackResult retrieveFeedResult(final WriteFeedCallbackContext context) throws ODataApplicationException
	{
		return null;
	}

	public boolean isNavigationFromTo(final WriteCallbackContext context, final String entitySetName,
			final String navigationPropertyName) throws EdmException
	{
		return true;
	}

	public DataStore getDataStore()
	{
		return dataStore;
	}

	public URI getServiceRoot()
	{
		return serviceRoot;
	}

	public void setDataStore(DataStore dataStore)
	{
		this.dataStore = dataStore;
	}

	public void setServiceRoot(URI serviceRoot)
	{
		this.serviceRoot = serviceRoot;
	}
}
