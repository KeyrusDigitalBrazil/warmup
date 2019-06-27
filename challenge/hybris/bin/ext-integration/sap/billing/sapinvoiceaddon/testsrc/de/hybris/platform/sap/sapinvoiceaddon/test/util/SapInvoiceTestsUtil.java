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
package de.hybris.platform.sap.sapinvoiceaddon.test.util;

import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;


public class SapInvoiceTestsUtil
{
	/*
	 * returns a String containing an impex file content
	 */

	public static String impexFileToString(final String file) throws Exception
	{
		String impexContent = null;
		InputStream inputStream = null;

		try
		{
			inputStream = ServicelayerTest.class.getResourceAsStream(file);
			impexContent = IOUtils.toString(inputStream);
		}
		finally
		{
			inputStream.close();
		}

		return impexContent;
	}



	/*
	 * Return a list of userGroups in an impex file
	 */
	public static List<String> getInvoiceNumbersFromImpex(final String impexContent, final int uidIndex)
	{

		final List<String> list = new ArrayList<String>();

		final String[] lines = impexContent.split("\n");

		int index = 0;

		while (!lines[index].trim().startsWith("INSERT_UPDATE SapB2BDocument"))
		{
			index++;
		}

		while (++index < lines.length && lines[index].trim().startsWith(";"))
		{
			final String[] lineTockens = lines[index].split(";");
			list.add(lineTockens[uidIndex]);

		}

		return list;
	}

	/*
	 * will return the invoice document Number from SAPB2BDocument
	 */
	public static String sapB2BDocumentToInvoiceNum(final SapB2BDocumentModel sapB2BDocumentModel)
	{

		return sapB2BDocumentModel.getInvoiceNumber();
	}

	public static boolean compareIds(final String stringA, final String stringB)
	{
		return stringA.equals(stringB);
	}

}
