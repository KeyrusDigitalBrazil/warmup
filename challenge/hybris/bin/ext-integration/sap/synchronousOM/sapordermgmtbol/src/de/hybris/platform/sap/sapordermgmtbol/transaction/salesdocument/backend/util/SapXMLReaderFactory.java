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
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.util;

import de.hybris.platform.sap.core.common.DocumentBuilderFactoryUtil;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;


public class SapXMLReaderFactory
{

	private static volatile XMLReader xmlReader = null;

	private SapXMLReaderFactory()
	{
	}

	public static XMLReader createXMLReader() throws SAXException
	{

		if (xmlReader != null)
		{
			return xmlReader;
		}
		else
		{
			synchronized (SapXMLReaderFactory.class)
			{
				if (xmlReader != null)
				{
					return xmlReader;
				}
				else
				{
					xmlReader = XMLReaderFactory.createXMLReader();
					DocumentBuilderFactoryUtil.setSecurityFeatures(xmlReader);
					return xmlReader;
				}
			}
		}

	}

}
