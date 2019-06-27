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
package de.hybris.platform.sap.saprevenuecloudorder.populators;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.sap.hybris.saprevenuecloudproduct.model.SAPMarketToCatalogMappingModel;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants;
import de.hybris.platform.sap.saprevenuecloudorder.data.AspectData;
import de.hybris.platform.sap.saprevenuecloudorder.data.AspectsData;
import de.hybris.platform.sap.saprevenuecloudorder.data.Customer;
import de.hybris.platform.sap.saprevenuecloudorder.data.Market;
import de.hybris.platform.sap.saprevenuecloudorder.data.OrderItem;
import de.hybris.platform.sap.saprevenuecloudorder.data.PaymentData;
import de.hybris.platform.sap.saprevenuecloudorder.data.Price;
import de.hybris.platform.sap.saprevenuecloudorder.data.Product;
import de.hybris.platform.sap.saprevenuecloudorder.data.Quantity;
import de.hybris.platform.sap.saprevenuecloudorder.data.SubscriptionItem;
import de.hybris.platform.sap.saprevenuecloudorder.data.SubscriptionItemPrice;
import de.hybris.platform.sap.saprevenuecloudorder.data.SubscriptionOrder;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;

/**
 * Populate DTO {@link SubscriptionOrder} with data from {@link AbstractOrderModel}.
 *
 * @param <SOURCE> source class
 * @param <TARGET> target class
 */

public class DefaultSAPRevenueCloudSubscriptionOrderPopulator
		implements Populator<AbstractOrderModel, SubscriptionOrder> 
{

	private static final Logger LOG = Logger.getLogger(DefaultSAPRevenueCloudSubscriptionOrderPopulator.class);

	private SubscriptionCommercePriceService commercePriceService;
	private GenericDao<SAPMarketToCatalogMappingModel> sapCatalogToMarketMappingGenericDao;
	private CMSSiteService cmsSiteService;
	private ConfigurationService configurationService;
	private SapRevenueCloudSubscriptionService sapRevenueCloudSubscriptionService;
	private CommonI18NService commonI18NService;

	@Override
	public void populate(final AbstractOrderModel order, final SubscriptionOrder subscriptionOrder)
			throws ConversionException 
	{
		final String owner = ((CustomerModel) order.getUser()).getUid();
		subscriptionOrder.setOwner(owner);
		final Market market = new Market();
		final CatalogModel catalog = order.getStore().getCatalogs().get(0);
		final String marketId = getMarketFromCatalog(catalog);
		market.setMarketId(marketId);
		subscriptionOrder.setMarket(market);
		final Customer customer = new Customer();
		final String id = ((CustomerModel) order.getUser()).getRevenueCloudCustomerId();
		customer.setCustomerNumber(id);
		subscriptionOrder.setCustomer(customer);
		// Payment Data
		//addPaymentDetalsForSubscriptionOrder(order.getPaymentInfo(), subscriptionOrder);
		PaymentData paymentData = new PaymentData();
		paymentData.setPaymentMethod(getConfigurationService().getConfiguration()
				.getString(SaprevenuecloudorderConstants.DEFAULT_PAYMENT_TYPE));

		try 
		{
			populateOrderItems(order, subscriptionOrder);
			LOG.debug(subscriptionOrder);
		}
		catch (final CMSItemNotFoundException e)
		{
			LOG.error(e);
		}
	}

	/**
	 * Add extract payment details from {@link PaymentInfoModel} and populate
	 * {@link SubscriptionOrder}
	 * 
	 * @param paymentInfoModel
	 * @param subscriptionOrder
	 */
	protected void addPaymentDetalsForSubscriptionOrder(PaymentInfoModel paymentInfoModel,
			SubscriptionOrder subscriptionOrder)
	{
		validateParameterNotNullStandardMessage("paymentInfoModel", paymentInfoModel);
		PaymentData paymentData = new PaymentData();
		if (paymentInfoModel instanceof CreditCardPaymentInfoModel) 
		{
			paymentData.setPaymentMethod(SaprevenuecloudorderConstants.CARD_PAYMENT_TYPE);
			paymentData.setPaymentCardToken(((CreditCardPaymentInfoModel) paymentInfoModel).getSubscriptionId());
		} 
		else 
		{
			paymentData.setPaymentMethod(getConfigurationService().getConfiguration()
					.getString(SaprevenuecloudorderConstants.DEFAULT_PAYMENT_TYPE));
		}
		subscriptionOrder.setPaymentData(paymentData);
	}

	protected void populateOrderItems(final AbstractOrderModel order, final SubscriptionOrder subscriptionOrder)
			throws CMSItemNotFoundException
	{
		final List<OrderItem> orderItems = new ArrayList<>();

		for (final AbstractOrderEntryModel orderEntry : order.getEntries())
		{
			if (orderEntry.getProduct().getSubscriptionCode().isEmpty()) 
			{
				continue;
			}
			final OrderItem orderItem = new OrderItem();
			orderItem.setItemType(SaprevenuecloudorderConstants.SUBSCRIPTIONITEM);

			// Product
			final Product product = new Product();
			product.setId((orderEntry.getProduct().getSubscriptionCode()));
			orderItem.setProduct(product);

			// Quantity
			final Quantity quantity = new Quantity();
			quantity.setValue(orderEntry.getProduct().getPriceQuantity().toString());
			quantity.setUnit(orderEntry.getProduct().getUnit().getCode());
			orderItem.setQuantity(quantity);

			// Price
			final Price price = new Price();
			final AspectData aspectsData = new AspectData();
			final SubscriptionItemPrice itemPrice = new SubscriptionItemPrice();
			getCmsSiteService().setCurrentSiteAndCatalogVersions((CMSSiteModel) order.getSite(), true);
			getCommonI18NService().setCurrentCurrency(order.getCurrency());
			final SubscriptionPricePlanModel pricePlanModel = getCommercePriceService()
					.getSubscriptionPricePlanForProduct(orderEntry.getProduct());

			itemPrice.setPriceObjectId(pricePlanModel.getPricePlanId());
			aspectsData.setSubscriptionItemPrice(itemPrice);
			price.setAspectData(aspectsData);
			orderItem.setPrice(price);
			final SubscriptionItem item = new SubscriptionItem();
			// set ValidFrom Date
			final TimeZone tzUTC = TimeZone.getTimeZone(ZoneId.of(SaprevenuecloudorderConstants.UTC));
			item.setValidFrom(ZonedDateTime.now().withZoneSameInstant(tzUTC.toZoneId())
					.format(SaprevenuecloudorderConstants.ISO8601_FORMATTER));
			final AspectsData dateAspectsData = new AspectsData();
			dateAspectsData.setSubscriptionItem(item);
			orderItem.setAspectsData(dateAspectsData);
			// Adding Order Item to the list
			orderItems.add(orderItem);
		}

		subscriptionOrder.setOrderItems(orderItems);
	}

	protected String getMarketFromCatalog(final CatalogModel catalog) 
	{
		try 
		{
			final Optional<String> cmOpt = getSapCatalogToMarketMappingGenericDao().find().stream()
					.filter(e -> e.getCatalog().equals(catalog)).map(SAPMarketToCatalogMappingModel::getMarketId)
					.findFirst();
			return cmOpt.orElse("");
		} 
		catch (final NoSuchElementException exception) 
		{
			LOG.error("No Mapping Market found for" + catalog + exception);
		}
		return "";
	}

	public GenericDao<SAPMarketToCatalogMappingModel> getSapCatalogToMarketMappingGenericDao() 
	{
		return sapCatalogToMarketMappingGenericDao;
	}

	public void setSapCatalogToMarketMappingGenericDao(
			final GenericDao<SAPMarketToCatalogMappingModel> sapCatalogToMarketMappingGenericDao) 
	{
		this.sapCatalogToMarketMappingGenericDao = sapCatalogToMarketMappingGenericDao;
	}

	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	public void setCmsSiteService(final CMSSiteService cmsSiteService) 
	{
		this.cmsSiteService = cmsSiteService;
	}

	public SubscriptionCommercePriceService getCommercePriceService() 
	{
		return commercePriceService;
	}

	public void setCommercePriceService(final SubscriptionCommercePriceService commercePriceService) 
	{
		this.commercePriceService = commercePriceService;
	}

	public ConfigurationService getConfigurationService() 
	{
		return configurationService;
	}

	public void setConfigurationService(final ConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}

	public SapRevenueCloudSubscriptionService getSapRevenueCloudSubscriptionService() 
	{
		return sapRevenueCloudSubscriptionService;
	}

	public void setSapRevenueCloudSubscriptionService(
			SapRevenueCloudSubscriptionService sapRevenueCloudSubscriptionService) 
	{
		this.sapRevenueCloudSubscriptionService = sapRevenueCloudSubscriptionService;
	}
	

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(CommonI18NService commonI18NService) 
	{
		this.commonI18NService = commonI18NService;
	}


}
