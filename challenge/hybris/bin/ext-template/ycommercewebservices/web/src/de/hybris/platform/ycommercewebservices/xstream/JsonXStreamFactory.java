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
package de.hybris.platform.ycommercewebservices.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;


/**
 * JSon specific {@link XStream} instances factory.
 */
public class JsonXStreamFactory extends XmlXStreamFactory
{
	private XStream jsonInstance;

	@Override
	public void afterPropertiesSet() throws Exception
	{
		jsonInstance = getObjectInternal();
	}

	@Override
	public Object getObject() throws Exception
	{
		return jsonInstance;
	}

	@Override
	protected XStream getObjectInternal()
	{
		final XStream stream = new XStream(new com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver())
		{
			@Override
			protected MapperWrapper wrapMapper(final MapperWrapper next)
			{
				return createMapperWrapper(next);
			}
		};

		stream.setMode(com.thoughtworks.xstream.XStream.NO_REFERENCES);
		return stream;
	}
}
