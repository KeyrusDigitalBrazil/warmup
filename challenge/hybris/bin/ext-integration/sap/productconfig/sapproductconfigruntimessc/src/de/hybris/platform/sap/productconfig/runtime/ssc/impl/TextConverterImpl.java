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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.impl.TextConverterBaseImpl;
import de.hybris.platform.sap.productconfig.runtime.ssc.TextConverter;
import de.hybris.platform.sap.productconfig.runtime.ssc.constants.SapproductconfigruntimesscConstants;

import com.sap.custdev.projects.fbs.slc.cfg.client.ITextDescription;


/**
 * Default implementation of the {@link TextConverter}<br>
 * <b>Note: This class will remove any markup from the ITF-Text, as this an potential attack vector. In case it is
 * desired to display formatted ITF-Texts on the UI, this class needs to be enhanced/replaced. One should specify a
 * white list of allowed markup for formatting.</b>
 */
public class TextConverterImpl extends TextConverterBaseImpl implements TextConverter
{
	@Override
	public String convertDependencyText(final ITextDescription[] textDescriptionArray)
	{
		String text = null;
		final StringBuilder textBuffer = new StringBuilder();


		if (textDescriptionArray != null && textDescriptionArray.length > 0)
		{
			for (final ITextDescription textDescriptionLine : textDescriptionArray)
			{
				final String textLine = textDescriptionLine.getTextLine();
				textBuffer.append(textLine);
			}
		}


		if (textBuffer.length() > 0)
		{
			text = getExplanationForDependency(textBuffer.toString());
			if (!text.isEmpty())
			{
				text = deEscapeString(text);
				text = removeFormatting(text);
				text = removeMarkup(text);
				text = replaceDoubleQuotes(text);
			}
			return text;

		}
		return text;
	}

	protected String getExplanationForDependency(final String text)
	{

		String explanation = null;

		if (text == null || text.isEmpty())
		{
			explanation = "";
		}
		else
		{
			int start = 0;
			if (text.indexOf(SapproductconfigruntimesscConstants.EXPLANATION) > -1)
			{
				start = text.indexOf(SapproductconfigruntimesscConstants.EXPLANATION)
						+ SapproductconfigruntimesscConstants.EXPLANATION.length();
			}
			int end = text.length();
			if (text.indexOf(SapproductconfigruntimesscConstants.DOCUMENTATION) > -1)
			{
				end = text.indexOf(SapproductconfigruntimesscConstants.DOCUMENTATION);
			}
			explanation = text.substring(start, end);
		}

		return explanation;
	}

}
