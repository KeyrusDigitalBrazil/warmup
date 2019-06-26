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
package de.hybris.platform.cmsoccaddon.jaxb;

import de.hybris.platform.cmsoccaddon.data.CMSPageWsDTO;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentAdapterUtil.ComponentAdaptedData;
import de.hybris.platform.cmsoccaddon.jaxb.adapters.ComponentListWsDTOAdapter.ListAdaptedComponents;
import de.hybris.platform.webservicescommons.jaxb.Jaxb2HttpMessageConverter;

import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


/**
 * An XmlHttpMessageConverter that reads and writes messages.
 *
 */
public class CmsJaxb2HttpMessageConverter extends Jaxb2HttpMessageConverter
{

	@Override
	protected void marshal(final HttpHeaders headers, final Result result, final Object input, final Class clazz,
			final Marshaller marshaller)
	{
		if (!MediaType.APPLICATION_XML.isCompatibleWith(headers.getContentType()) && isCmsOutput(input))
		{
			// by default, we don't want reduce any array
			((JAXBMarshaller) marshaller).getXMLMarshaller().setReduceAnyArrays(false);
		}
		super.marshal(headers, result, input, clazz, marshaller);
	}

	/**
	 * Check whether the output object is from cmsoccaddon
	 */
	protected boolean isCmsOutput(final Object obj)
	{
		if (obj instanceof ListAdaptedComponents || obj instanceof CMSPageWsDTO || obj instanceof ComponentAdaptedData)
		{
			return true;
		}

		return false;
	}
}
