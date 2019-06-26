/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2webservices.converter;

import de.hybris.platform.odata2services.odata.ODataContextGenerator;

import javax.servlet.http.HttpServletRequest;

import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.convert.converter.Converter;

public class HttpServletRequestToODataContextConverter implements Converter<HttpServletRequest, ODataContext>
{
	private Converter<HttpServletRequest, ODataRequest> requestConverter;
	private ODataContextGenerator oDataContextGenerator;

	@Override
	public ODataContext convert(final HttpServletRequest request)
	{
		final ODataRequest oDataRequest = requestConverter.convert(request);

		return oDataContextGenerator.generate(oDataRequest);
	}

	@Required
	public void setRequestConverter(final Converter<HttpServletRequest, ODataRequest> requestConverter)
	{
		this.requestConverter = requestConverter;
	}

	@Required
	public void setODataContextGenerator(final ODataContextGenerator oDataContextGenerator)
	{
		this.oDataContextGenerator = oDataContextGenerator;
	}
}