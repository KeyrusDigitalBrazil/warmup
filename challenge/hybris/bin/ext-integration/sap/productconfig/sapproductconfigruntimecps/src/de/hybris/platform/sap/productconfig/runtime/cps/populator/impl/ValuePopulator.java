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
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;

import org.apache.commons.lang.StringUtils;




/**
 * Responsible to populate characteristics
 */
public class ValuePopulator implements Populator<CPSValue, CsticValueModel>
{

	@Override
	public void populate(final CPSValue source, final CsticValueModel target)
	{
		populateCoreAttributes(source, target);

	}


	protected void populateCoreAttributes(final CPSValue source, final CsticValueModel target)
	{
		final String value = source.getValue();
		if (StringUtils.isEmpty(value))
		{
			throw new IllegalStateException("Value must be neither null nor empty");
		}
		else
		{
			target.setName(value);
			populateAuthor(source, target);
		}
	}


	protected void populateAuthor(final CPSValue source, final CsticValueModel target)
	{

		final String author = source.getAuthor();
		if (!StringUtils.isEmpty(author))
		{
			// we use first char of Author, User -> 'U', System -> 'S', Default -> 'D'
			final char authorFlag = author.charAt(0);
			switch (authorFlag)
			{
				case 'U':
					target.setAuthor(CsticValueModel.AUTHOR_USER);
					target.setAuthorExternal(CsticValueModel.AUTHOR_EXTERNAL_USER);
					break;
				case 'D':
					target.setAuthor(CsticValueModel.AUTHOR_USER);
					target.setAuthorExternal(CsticValueModel.AUTHOR_EXTERNAL_DEFAULT);
					break;
				case 'S':
					target.setAuthor(CsticValueModel.AUTHOR_SYSTEM);
					target.setAuthorExternal(CsticValueModel.AUTHOR_SYSTEM);
					break;
				default:
					final String authorString = String.valueOf(authorFlag);
					target.setAuthor(authorString);
					target.setAuthorExternal(authorString);
			}
		}
	}
}
