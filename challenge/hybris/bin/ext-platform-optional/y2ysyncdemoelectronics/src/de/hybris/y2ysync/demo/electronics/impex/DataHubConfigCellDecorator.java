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
package de.hybris.y2ysync.demo.electronics.impex;

import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataHubConfigCellDecorator extends AbstractImpExCSVCellDecorator
{
	private static final Logger LOG = LoggerFactory.getLogger(DataHubConfigCellDecorator.class);
	private static final String DH_CONFIG_TEMPLATE_FILE = "/y2ysync-demo-dh-model.xml";

	@Override
	public String decorate(final int position, final Map<Integer, String> srcLine)
	{
		final String value = srcLine.get(Integer.valueOf(position));
		return StringUtils.isEmpty(value) ? getValueFromTemplate() : value;
	}

	private String getValueFromTemplate()
	{
		try (final InputStream stream = DataHubConfigCellDecorator.class.getResourceAsStream(DH_CONFIG_TEMPLATE_FILE))
		{
			if (stream == null)
			{
				LOG.error("Template '{}' not found on a classpath!", DH_CONFIG_TEMPLATE_FILE);
				throw new SystemException("DataHub config template file not found on a classpath!");
			}
			return IOUtils.toString(stream);
		}
		catch (final IOException e)
		{
			throw new SystemException(e);
		}
	}
}
