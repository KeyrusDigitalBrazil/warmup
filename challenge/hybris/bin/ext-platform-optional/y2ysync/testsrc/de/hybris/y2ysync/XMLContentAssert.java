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
package de.hybris.y2ysync;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.fest.assertions.GenericAssert;
import org.xml.sax.SAXException;


/**
 *
 */
public class XMLContentAssert extends GenericAssert<XMLContentAssert, String>
{

	private XMLContentAssert(final String actual) throws ParserConfigurationException, IOException, SAXException
	{
		super(XMLContentAssert.class, actual);
	}

	public static XMLContentAssert assertThat(final String actual) throws IOException, SAXException, ParserConfigurationException
	{
		return new XMLContentAssert(actual);
	}

	public XMLContentAssert hasTheSameContentAs(final String expectedXml) throws SAXException, IOException, XpathException
	{
		XMLAssert.assertXpathEvaluatesTo("1", "count(/extension)", actual);
		XMLAssert.assertXpathEvaluatesTo("1", "count(//rawItems)", actual);
		XMLAssert.assertXpathEvaluatesTo("1", "count(//canonicalItems)", actual);
		XMLAssert.assertXpathEvaluatesTo("1", "count(//targetItems)", actual);
		XMLAssert.assertXpathEvaluatesTo("2", "count(//rawItems/item)", actual);
		XMLAssert.assertXpathEvaluatesTo("1", "count(//rawItems/item[type=\"testContainer_Product\"])", actual);
		XMLAssert.assertXpathEvaluatesTo("1", "count(//rawItems/item[type=\"testContainer_Title\"])", actual);
		XMLAssert.assertXpathEvaluatesTo("2", "count(//canonicalItems/item)", actual);
		XMLAssert.assertXpathEvaluatesTo("1", "count(//canonicalItems/item[type=\"testContainer_ProductCanonical\"])", actual);
		XMLAssert.assertXpathEvaluatesTo("1", "count(//canonicalItems/item[type=\"testContainer_TitleCanonical\"])", actual);
		XMLAssert.assertXpathEvaluatesTo("2", "count(//targetItems/item)", actual);
		XMLAssert.assertXpathEvaluatesTo("1", "count(//targetItems/item[type=\"testContainer_ProductTarget\"])", actual);
		XMLAssert.assertXpathEvaluatesTo("1", "count(//targetItems/item[type=\"testContainer_TitleTarget\"])", actual);
		XMLAssert.assertXpathsEqual("/extension/rawItems/item[type=\"testContainer_Product\"]", actual,
				"/extension/rawItems/item[type=\"testContainer_Product\"]", expectedXml);
		XMLAssert.assertXpathsEqual("/extension/rawItems/item[type=\"testContainer_Title\"]", actual,
				"/extension/rawItems/item[type=\"testContainer_Title\"]", expectedXml);
		XMLAssert.assertXpathsEqual("/extension/canonicalItems/item[type=\"testContainer_ProductCanonical\"]", actual,
				"/extension/canonicalItems/item[type=\"testContainer_ProductCanonical\"]", expectedXml);
		XMLAssert.assertXpathsEqual("/extension/canonicalItems/item[type=\"testContainer_TitleCanonical\"]", actual,
				"/extension/canonicalItems/item[type=\"testContainer_TitleCanonical\"]", expectedXml);
		XMLAssert.assertXpathsEqual("/extension/targetItems/item[type=\"testContainer_ProductTarget\"]", actual,
				"/extension/targetItems/item[type=\"testContainer_ProductTarget\"]", expectedXml);
		XMLAssert.assertXpathsEqual("/extension/targetItems/item[type=\"testContainer_TitleTarget\"]", actual,
				"/extension/targetItems/item[type=\"testContainer_TitleTarget\"]", expectedXml);
		return this;
	}
}
