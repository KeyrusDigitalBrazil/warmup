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
package de.hybris.platform.consignmenttrackingservices.service.impl;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.enums.ConsignmentStatus;
import de.hybris.platform.consignmenttrackingservices.adaptors.CarrierAdaptor;
import de.hybris.platform.consignmenttrackingservices.daos.ConsignmentDao;
import de.hybris.platform.consignmenttrackingservices.delivery.data.ConsignmentEventData;
import de.hybris.platform.consignmenttrackingservices.service.ConsignmentTrackingService;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * A default implementation of ConsignmentTrackingService.
 */
public class DefaultConsignmentTrackingService implements ConsignmentTrackingService
{

	private static final Logger LOG = Logger.getLogger(DefaultConsignmentTrackingService.class);

	private static final String TRACKING_URL_KEY = "default.carrier.tracking.url";

	private static final String DELIVERY_LEAD_TIME_KEY = "default.delivery.lead.time";

	private Map<String, CarrierAdaptor> carrierAdaptors;

	private ConfigurationService configurationService;

	private ConsignmentDao consignmentDao;

	private ModelService modelService;

	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	private BaseSiteService baseSiteService;


	@Override
	public URL getTrackingUrlForConsignment(final ConsignmentModel consignment)
	{
		return getCarrierAdaptor(consignment).map(adaptor -> adaptor.getTrackingUrl(consignment.getTrackingID()))
				.orElseGet(this::getDefaultTrackingUrl);
	}

	@Override
	public boolean isTrackingIdValid(final ConsignmentModel consignment)
	{
		return getCarrierAdaptor(consignment)
				.map(adaptor -> Boolean.valueOf(adaptor.isTrackingIdValid(consignment.getTrackingID()))).orElse(Boolean.TRUE)
				.booleanValue();
	}

	@Override
	public List<ConsignmentEventData> getConsignmentEvents(final ConsignmentModel consignment)
	{
		if (StringUtils.isBlank(consignment.getTrackingID()))
		{
			return Collections.emptyList();
		}
		else
		{
			return getCarrierAdaptor(consignment).map(adptor -> adptor.getConsignmentEvents(consignment.getTrackingID())).orElse(
					Collections.emptyList());
		}
	}

	@Override
	public Optional<ConsignmentModel> getConsignmentForCode(final String orderCode, final String consignmentCode)
	{
		return consignmentDao.findConsignmentByCode(orderCode, consignmentCode);
	}

	@Override
	public List<ConsignmentModel> getConsignmentsForOrder(final String orderCode)
	{
		return consignmentDao.findConsignmentsByOrder(orderCode);
	}

	@Override
	public void updateConsignmentStatusForCode(final String orderCode, final String consignmentCode, final ConsignmentStatus status)
	{
		getConsignmentDao().findConsignmentByCode(orderCode, consignmentCode).ifPresent(consignment -> {
			consignment.setStatus(status);
			getModelService().save(consignment);
		});
	}

	@Override
	public int getDeliveryLeadTime(final ConsignmentModel consignment)
	{
		return getCarrierAdaptor(consignment).map(adaptor -> Integer.valueOf(adaptor.getDeliveryLeadTime(consignment)))
				.orElseGet(this::getDefaultDeliveryLeadTime).intValue();
	}

	@Override
	public Map<String, CarrierAdaptor> getAllCarrierAdaptors()
	{
		return carrierAdaptors;
	}

	protected Optional<CarrierAdaptor> getCarrierAdaptor(final ConsignmentModel consignment)
	{
		if (consignment.getCarrierDetails() != null)
		{
			final String carrierCode = consignment.getCarrierDetails().getCode();
			return Optional.ofNullable(getCarrierAdaptors().get(carrierCode));
		}
		return Optional.empty();
	}

	/**
	 * get default tracking URL from properties
	 *
	 * @return default tracking URL
	 */
	protected URL getDefaultTrackingUrl()
	{
		final String baseUrl = siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSiteService.getCurrentBaseSite(), true,
				StringUtils.EMPTY);
		final Configuration config = getConfigurationService().getConfiguration();
		final String trackingUrl = config.getString(TRACKING_URL_KEY, StringUtils.EMPTY);
		try
		{
			return new URL(baseUrl + trackingUrl);
		}
		catch (final MalformedURLException e)
		{
			LOG.error("Invalid Tracking URL");
		}
		return null;
	}

	/**
	 * get default delivery lead time from properties
	 *
	 * @return default delivery lead time
	 */
	protected int getDefaultDeliveryLeadTime()
	{
		final Configuration config = getConfigurationService().getConfiguration();
		return config.getInt(DELIVERY_LEAD_TIME_KEY, 0);
	}

	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	@Required
	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	protected Map<String, CarrierAdaptor> getCarrierAdaptors()
	{
		return carrierAdaptors;
	}

	@Required
	public void setCarrierAdaptors(final Map<String, CarrierAdaptor> carrierAdaptors)
	{
		this.carrierAdaptors = carrierAdaptors;
	}

	protected ConsignmentDao getConsignmentDao()
	{
		return consignmentDao;
	}

	@Required
	public void setConsignmentDao(final ConsignmentDao consignmentDao)
	{
		this.consignmentDao = consignmentDao;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	@Required
	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
	}

	protected BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	@Required
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}
}
