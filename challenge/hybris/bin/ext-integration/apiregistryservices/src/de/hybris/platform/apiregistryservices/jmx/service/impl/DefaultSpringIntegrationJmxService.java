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
package de.hybris.platform.apiregistryservices.jmx.service.impl;

import de.hybris.platform.apiregistryservices.jmx.QueueInfoBeanImpl;
import de.hybris.platform.apiregistryservices.jmx.service.SpringIntegrationJmxService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.jmx.MBeanRegisterUtilities;
import de.hybris.platform.jmx.mbeans.impl.AbstractJMXMBean;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.channel.QueueChannel;


/**
 * Default implementation of {@link SpringIntegrationJmxService}
 */
public class DefaultSpringIntegrationJmxService implements SpringIntegrationJmxService
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultSpringIntegrationJmxService.class);
	private MBeanRegisterUtilities mbeanRegisterUtility;

	/**
	 * @param jmxPath       suffix for jxm path
	 * @param beanInterface for mbean
	 * @see SpringIntegrationJmxService#registerAllSpringQueues(String, Class)
	 */
	@Override
	public void registerAllSpringQueues(final String jmxPath, final Class beanInterface)
	{
		final HashMap<String, AbstractJMXMBean> map = new HashMap<>();

		final Map<String, QueueChannel> beansOfType = Registry.getApplicationContext().getBeansOfType(QueueChannel.class);
		beansOfType.forEach((s, queueChannel) ->
		{
			queueChannel.setStatsEnabled(true);
			final QueueInfoBeanImpl queueInfoBean = new QueueInfoBeanImpl();
			queueInfoBean.setJmxPath(jmxPath + "_" + s);
			queueInfoBean.setBeanInterface(beanInterface);
			queueInfoBean.setBeanName(s);
			queueInfoBean.setChannel(queueChannel);
			try
			{
				queueInfoBean.afterPropertiesSet();
			}
			catch (final Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
			map.put(s, queueInfoBean);
		});
		getMBeanRegisterUtility().registerMBeans(map);
	}

	protected MBeanRegisterUtilities getMBeanRegisterUtility()
	{
		return mbeanRegisterUtility;
	}

	@Required
	public void setMbeanRegisterUtility(final MBeanRegisterUtilities mbeanRegisterUtility)
	{
		this.mbeanRegisterUtility = mbeanRegisterUtility;
	}
}
