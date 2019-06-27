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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlUtils
{
	private XmlUtils()
	{
	}

	/**
	 * Parses XML contained in a {@code String}
	 * @param xmlBody an XML content.
	 * @return a parsed document representing {@code xmlBody}.
	 * @throws IllegalArgumentException when the {@code xmlBody} does not contain a well formed XML.
	 */
	public static Document getXmlDocument(final String xmlBody)
	{
		final InputSource is = new InputSource(new StringReader(xmlBody));
		return parseDocument(is);
	}

	/**
	 * Parses XML contained in the input stream
	 * @param in an input stream to read XML content from.
	 * @return a parsed document representing the XML in the input stream.
	 * @throws IllegalArgumentException when the input stream failed to read or does not contain a well formed XML.
	 */
	public static Document getXmlDocument(final InputStream in)
	{
		final InputSource is = new InputSource(in);
		return parseDocument(is);
	}

	private static Document parseDocument(final InputSource src)
	{
		try
		{
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(true);
			factory.setFeature("http://apache.org/xml/features/validation/dynamic", true);
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			factory.setXIncludeAware(false);
			factory.setExpandEntityReferences(false);
			final DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(src);
		}
		catch (final IOException | SAXException | ParserConfigurationException e)
		{
			throw new IllegalArgumentException("Received input stream is in invalid state or contains unparseable XML", e);
		}
	}
}
