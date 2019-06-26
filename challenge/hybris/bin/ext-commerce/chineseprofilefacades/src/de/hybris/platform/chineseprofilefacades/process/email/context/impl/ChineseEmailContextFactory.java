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
package de.hybris.platform.chineseprofilefacades.process.email.context.impl;

import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.acceleratorservices.process.email.context.impl.DefaultEmailContextFactory;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.components.CMSImageComponentModel;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;

import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;


public class ChineseEmailContextFactory extends DefaultEmailContextFactory
{
	private static final Logger LOG = Logger.getLogger(ChineseEmailContextFactory.class);
	private static final String MEDIA = "media";

	@Override
	protected void processProperties(final AbstractCMSComponentModel component, final Map<String, Object> componentContext)
	{
		for (final String property : getCmsComponentService().getEditorProperties(component))
		{
			try
			{
				final Object value;

				if (component instanceof CMSImageComponentModel && MEDIA.equals(property))
				{

					final AbstractEmailContext context = (AbstractEmailContext) componentContext.get("parentContext");

					final Locale locale = new Locale(context.getEmailLanguage().getIsocode());

					value = getModelService().getAttributeValue(component, property, locale);

				}
				else
				{
					value = getModelService().getAttributeValue(component, property);
				}

				componentContext.put(property, value);
			}
			catch (final AttributeNotSupportedException ignore)
			{
				LOG.error("Attribute not supported exception!");
			}
		}
	}

}
