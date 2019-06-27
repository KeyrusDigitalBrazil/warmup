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
package de.hybris.platform.acceleratorservices.dataimport.batch.converter;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.translators.ConvertPlaintextToEncodedUserPasswordTranslator;
import de.hybris.platform.jalo.Item;

import org.apache.commons.lang.RandomStringUtils;


/**
 * This translator adds some random text to the orginal password and passes this value to
 * ConvertPlaintestToEncodedUserPasswordTranslator.
 */
public class RandomPasswordTranslator extends ConvertPlaintextToEncodedUserPasswordTranslator
{
	private static final int RANDOM_STRING_LENGTH = 6;

	@Override
	public void performImport(final String cellValue, final Item processedItem) throws ImpExException
	{
		final String newCellValue = (cellValue == null ? "" : cellValue.trim()) +
				RandomStringUtils.randomAlphanumeric(RANDOM_STRING_LENGTH);
		super.performImport(newCellValue, processedItem);
	}
}
