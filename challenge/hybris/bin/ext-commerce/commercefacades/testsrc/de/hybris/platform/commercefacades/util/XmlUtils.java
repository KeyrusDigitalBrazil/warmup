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

package de.hybris.platform.commercefacades.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.XpathEngine;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Helper class for xml transformation
 */
public class XmlUtils
{
	private static final Logger LOG = Logger.getLogger(XmlUtils.class);

	private XmlUtils()
	{
		// Private constructor of utility class
	}

	/**
	 * Removes all ignored nodes from the given xml
	 *
	 * @param xml
	 *           the xml to process
	 * @param transformationResource
	 *           the transformation to apply (classpath resource)
	 * @return the processed xml (in case of errors the original xml is returned)
	 */
	public static String applyTransformationResource(final String xml, final String transformationResource)
	{
		final StreamSource source = new StreamSource(
				Thread.currentThread().getContextClassLoader().getResourceAsStream(transformationResource));

		return transform(xml, source);
	}

	/**
	 *
	 * @param targetXml
	 *           the XML to apply the XPath expression to
	 * @param xPath
	 *           the XPath expression to apply
	 * @return the result of XPath evaluation
	 */
	public static final String evaluateXPath(final String targetXml, final String xPath)
	{
		final XpathEngine xpathEngine = XMLUnit.newXpathEngine();
		try
		{
			final NodeList nodes = xpathEngine.getMatchingNodes(xPath, XMLUnit.buildControlDocument(targetXml));

			final StringWriter sw = new StringWriter();
			final Transformer serializer = TransformerFactory.newInstance().newTransformer();
			serializer.transform(new DOMSource(nodes.item(0)), new StreamResult(sw));
			return sw.toString();
		}
		catch (final XpathException e)
		{
			LOG.error("XpathException while evaluating XPath", e);
		}
		catch (final SAXException e)
		{
			LOG.error("SAXException while evaluating XPath", e);
		}
		catch (final IOException e)
		{
			LOG.error("IOException while evaluating XPath", e);
		}
		catch (final TransformerConfigurationException e)
		{
			LOG.error("TransformerConfigurationException while evaluating XPath", e);
		}
		catch (final TransformerFactoryConfigurationError e)
		{
			LOG.error("TransformerFactoryConfigurationError while evaluating XPath", e);
		}
		catch (final TransformerException e)
		{
			LOG.error("TransformerException while evaluating XPath", e);
		}

		return targetXml;
	}

	protected static String transform(final String targetXml, final StreamSource source)
			throws TransformerFactoryConfigurationError
	{
		try
		{

			final StringReader reader = new StringReader(targetXml);
			final StringWriter writer = new StringWriter();
			final TransformerFactory tFactory = TransformerFactory.newInstance();
			final Transformer transformer = tFactory.newTransformer(source);

			transformer.transform(new StreamSource(reader), new StreamResult(writer));

			return writer.toString();
		}
		catch (final TransformerException e)
		{
			LOG.error("Error while transforming XML", e);
			return targetXml;
		}
	}
}
