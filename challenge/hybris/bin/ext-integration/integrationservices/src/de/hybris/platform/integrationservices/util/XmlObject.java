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

package de.hybris.platform.integrationservices.util;

import java.io.InputStream;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

/**
 * A helper object for evaluating Json content by utilizing XPath expressions.
 */
public class XmlObject
{
	private static final XPathFactory X_PATH_FACTORY = XPathFactory.newInstance();

	private final Document document;

	private XmlObject(final Document ctx)
	{
		document = ctx;
	}

	/**
	 * Parses XML content from the input stream and creates new XML object
	 * @param in an input stream containing XML
	 * @return an object for evaluating structure of the parsed content
	 * @throws IllegalArgumentException if the input stream fails to read or does not contain a well formed XML
	 */
	public static XmlObject createFrom(final InputStream in)
	{
		final Document context = XmlUtils.getXmlDocument(in);
		return new XmlObject(context);
	}

	/**
	 * Parses XML content and creates new XML object
	 * @param xml an XML content to parse
	 * @return an object for evaluating structure of the parsed content
	 * @throws IllegalArgumentException if the content  does not contain a well formed XML
	 */
	public static XmlObject createFrom(final String xml)
	{
		final Document context = XmlUtils.getXmlDocument(xml);
		return new XmlObject(context);
	}

	/**
	 * Looks up a value in the parsed JSON
	 * @param path a XML path, e.g. {@code //product/code}, pointing to the element whose value needs to be retrieved.
	 * @return value of the element matching the {@code path} location.
	 */
	public String get(final String path)
	{
		try
		{
			return (String) xpath(path).evaluate(document, XPathConstants.STRING);
		}
		catch (final XPathExpressionException e)
		{
			throw new IllegalArgumentException(path + " is not a valid XPath expression", e);
		}
	}

	private static XPathExpression xpath(final String path) throws XPathExpressionException
	{
		return X_PATH_FACTORY.newXPath().compile(path);
	}
}
