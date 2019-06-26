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

import de.hybris.platform.sap.productconfig.runtime.interf.AnalyticsProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProductCsticAndValueParameterProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.ssc.SSCSessionAccessService;
import de.hybris.platform.servicelayer.internal.service.ServicelayerUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;


/**
 * SSC implementation of the {@link ProviderFactory}.
 */
public class SSCProviderFactoryImpl implements ProviderFactory
{
	private static final Logger LOG = Logger.getLogger(SSCProviderFactoryImpl.class);

	private SSCSessionAccessService sessionAccessService;
	private ApplicationContext applicationContext;

	private String configurationProviderBeanName;

	private PricingProvider pricingProvider;
	private AnalyticsProvider analyticsProvider;
	private ProductCsticAndValueParameterProvider productCsticAndValueParameterProvider;
	private PricingConfigurationParameter pricingParameter;


	@Override
	public ConfigurationProvider getConfigurationProvider()
	{
		ConfigurationProvider provider = sessionAccessService.getConfigurationProvider();
		if (provider == null)
		{
			provider = (ConfigurationProvider) createProviderInstance(getConfigurationProviderBeanName());
			sessionAccessService.setConfigurationProvider(provider);
		}
		return provider;
	}


	protected Object createProviderInstance(final String providerBean)
	{
		final Object provider;

		ApplicationContext applCtxt = getApplicationContext();

		if (applCtxt == null)
		{
			applCtxt = ServicelayerUtils.getApplicationContext();
			setApplicationContext(applCtxt);
		}

		if (applCtxt == null)
		{
			throw new IllegalStateException("Application Context not available");
		}


		provider = applicationContext.getBean(providerBean);

		if (LOG.isDebugEnabled())
		{
			LOG.debug("created a new provider instance of " + providerBean);
		}

		return provider;
	}

	protected ApplicationContext getApplicationContext()
	{
		return applicationContext;
	}

	/**
	 * used for tests
	 */
	public void setApplicationContext(final ApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	protected SSCSessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	public String getConfigurationProviderBeanName()
	{
		return configurationProviderBeanName;
	}

	@Required
	public void setConfigurationProviderBeanName(final String configurationProviderBeanName)
	{
		this.configurationProviderBeanName = configurationProviderBeanName;
	}

	@Required
	public void setSessionAccessService(final SSCSessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

	@Override
	public ProductCsticAndValueParameterProvider getProductCsticAndValueParameterProvider()
	{
		return productCsticAndValueParameterProvider;
	}

	@Required
	public void setProductCsticAndValueParameterProvider(
			final ProductCsticAndValueParameterProvider productCsticAndValueParameterProvider)
	{
		this.productCsticAndValueParameterProvider = productCsticAndValueParameterProvider;
	}

	@Override
	public PricingConfigurationParameter getPricingParameter()
	{
		return pricingParameter;
	}

	@Required
	public void setPricingParameter(final PricingConfigurationParameter pricingParameter)
	{
		this.pricingParameter = pricingParameter;
	}

	@Override
	public PricingProvider getPricingProvider()
	{
		return pricingProvider;
	}

	@Required
	public void setPricingProvider(final PricingProvider pricingProvider)
	{
		this.pricingProvider = pricingProvider;
	}

	@Override
	public AnalyticsProvider getAnalyticsProvider()
	{
		return analyticsProvider;
	}

	@Required
	public void setAnalyticsProvider(final AnalyticsProvider analyticsProvider)
	{
		this.analyticsProvider = analyticsProvider;
	}


}
