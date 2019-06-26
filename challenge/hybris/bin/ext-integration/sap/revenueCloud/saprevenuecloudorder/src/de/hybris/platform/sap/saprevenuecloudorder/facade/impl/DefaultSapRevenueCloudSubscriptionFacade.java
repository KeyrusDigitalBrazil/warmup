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
package de.hybris.platform.sap.saprevenuecloudorder.facade.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.saprevenuecloudorder.constants.SaprevenuecloudorderConstants;
import de.hybris.platform.sap.saprevenuecloudorder.facade.SapRevenueCloudSubscriptionFacade;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.BillItem;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Bills;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.CancelSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.ExtendSubscription;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.MetaData;
import de.hybris.platform.sap.saprevenuecloudorder.pojo.Subscription;
import de.hybris.platform.sap.saprevenuecloudorder.service.SapRevenueCloudSubscriptionService;
import de.hybris.platform.sap.saprevenuecloudorder.util.SapRevenueCloudSubscriptionUtil;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.subscriptionfacades.data.SubscriptionBillingData;
import de.hybris.platform.subscriptionfacades.data.SubscriptionData;
import de.hybris.platform.subscriptionfacades.exceptions.SubscriptionFacadeException;
import de.hybris.platform.subscriptionfacades.impl.DefaultSubscriptionFacade;
import de.hybris.platform.subscriptionservices.model.BillingPlanModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;

/**
 * SAR RevenueCloud implementation of the {@link SapRevenueCloudSubscriptionFacade} interface and extending few methods {@link DefaultSubscriptionFacade} class.
 */
public class DefaultSapRevenueCloudSubscriptionFacade extends DefaultSubscriptionFacade
		implements SapRevenueCloudSubscriptionFacade 
	{

	private static final Logger LOG = Logger.getLogger(DefaultSapRevenueCloudSubscriptionFacade.class);

	private SapRevenueCloudSubscriptionService sapRevenueCloudSubscriptionService;
	private UserService userService;

	private SapRevenueCloudProductService sapRevenueCloudProductService;
	private BaseStoreService baseStoreService;
	private ConfigurationService configurationService;
	private Converter<Subscription,SubscriptionData> sapSubscriptionConverter;
	private Converter<Subscription,SubscriptionData> sapSubscriptionDetailConverter;
	private Converter<Bills,SubscriptionBillingData> sapSubscriptionBillsConverter;
	private Converter<BillItem,SubscriptionBillingData> sapSubscriptionBillDetailConverter;
	private B2BUnitService b2bUnitService;

	@Override
	public Collection<SubscriptionData> getSubscriptions() throws SubscriptionFacadeException
	{
		String customerId ;
		final CustomerModel customer = ((CustomerModel) userService.getCurrentUser());
		if (customer instanceof B2BCustomerModel) {
			B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;
			B2BUnitModel b2bUnit = (B2BUnitModel) getB2bUnitService().getParent(b2bCustomer);
			B2BUnitModel rootUnit = (B2BUnitModel) getB2bUnitService().getRootUnit(b2bUnit);
			customerId = rootUnit.getRevenueCloudCompanyId();
			
		} 
		else
		{
			customerId = ((CustomerModel) userService.getCurrentUser()).getRevenueCloudCustomerId();
		}
		final List<Subscription> subscriptions = sapRevenueCloudSubscriptionService
				.getSubscriptionsByClientId(customerId).stream()
				.sorted(Comparator.comparing(Subscription::getCreatedAt, Comparator.reverseOrder()))
				.collect(Collectors.toList());
		LOG.info(String.format("Customer [%s] subscriptions with descending creation date:", customerId));
		subscriptions.stream().forEach(entry -> LOG.info(entry.getSubscriptionId()));
		return Converters.convertAll(subscriptions, sapSubscriptionConverter);
	}

	@Override
	public SubscriptionData getSubscription(final String subscriptionId) throws SubscriptionFacadeException 
	{
		final Subscription sapSubscription = sapRevenueCloudSubscriptionService.getSubscriptionById(subscriptionId);
		if (sapSubscription == null)
		{
			return new SubscriptionData();
		}
		return sapSubscriptionDetailConverter.convert(sapSubscription);
	}

	@Override
	public boolean cancelSubscription(final SubscriptionData subscriptionData) throws SubscriptionFacadeException
	{
		final MetaData metaData = new MetaData();
		metaData.setVersion(subscriptionData.getVersion());
		final CancelSubscription cancelSubsciption = new CancelSubscription();
		cancelSubsciption.setCancellationReason(
				getConfigurationService().getConfiguration().getString(SaprevenuecloudorderConstants.CANCELLATION_REASON));
		cancelSubsciption.setMetaData(metaData);
		cancelSubsciption.setRequestedCancellationDate(subscriptionData.getValidTillDate());
		sapRevenueCloudSubscriptionService.cancelSubscription(subscriptionData.getId(), cancelSubsciption);
		return true;
	}

	@Override
	public boolean extendSubscription(final SubscriptionData subscriptionData) throws SubscriptionFacadeException
	{
		final ExtendSubscription extendSubscription = new ExtendSubscription();
		final MetaData metaData = new MetaData();
		metaData.setVersion(subscriptionData.getVersion());
		extendSubscription.setMetaData(metaData);
		if (subscriptionData.getUnlimited().booleanValue())
		{
			extendSubscription.setUnlimited("true");
		} 
		else
		{
			extendSubscription.setUnlimited("false");
			final CatalogModel currentCatalog = getBaseStoreService().getCurrentBaseStore().getCatalogs().get(0);
			final SubscriptionPricePlanModel pricePlanModel = getRatePlanId(subscriptionData.getRatePlanId(),
					currentCatalog.getActiveCatalogVersion());
			BillingPlanModel billingPlanModel = pricePlanModel.getProduct().getSubscriptionTerm().getBillingPlan();
			extendSubscription.setExtensionDate(calculateExtensionDate(billingPlanModel.getId(),
					subscriptionData.getValidTillDate(), subscriptionData.getExtendedPeriod()));
		}
		getSapSubscriptionService().extendSubscription(subscriptionData.getId(), extendSubscription);
		return true;
	}

	@Override
	public SubscriptionData computeCancellationDate(String subscriptionID) 
	{
		final String DATE_TIME_PATTERN = "yyyy-MM-dd";
		final SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_PATTERN);
		final Date currentDate = Date.from(ZonedDateTime.now().toInstant());
		final String reqCancellationDate = formatter.format(currentDate);
		String effCancellationDate = sapRevenueCloudSubscriptionService.computeCancelaltionDate(subscriptionID,
				reqCancellationDate);
		SubscriptionData subscriptionData = new SubscriptionData();
		LOG.info("calculated end date:" + SapRevenueCloudSubscriptionUtil.stringToDate(effCancellationDate));
		subscriptionData.setEndDate(SapRevenueCloudSubscriptionUtil.stringToDate(effCancellationDate));
		subscriptionData.setValidTillDate(effCancellationDate);
		return subscriptionData;
	}
	
	@Override
	public List<SubscriptionBillingData> getSubscriptionBills(String customerID,String fromDate,String toDate)
	{
		
		String customerId ;
		final CustomerModel customer = ((CustomerModel) userService.getCurrentUser());
		if (customer instanceof B2BCustomerModel) {
			B2BCustomerModel b2bCustomer = (B2BCustomerModel) customer;
			B2BUnitModel b2bUnit = (B2BUnitModel) getB2bUnitService().getParent(b2bCustomer);
			B2BUnitModel rootUnit = (B2BUnitModel) getB2bUnitService().getRootUnit(b2bUnit);
			customerId = rootUnit.getRevenueCloudCompanyId();
		} 
		else
		{
			customerId = ((CustomerModel) userService.getCurrentUser()).getRevenueCloudCustomerId();
		}
		List<Bills> bills = sapRevenueCloudSubscriptionService.getBillsBySubscriptionsId(customerId, fromDate, toDate);
		return Converters.convertAll(bills, sapSubscriptionBillsConverter);
	}
	
	@Override
	public List<SubscriptionBillingData> getSubscriptionBillsById(String billId)
	{
		Bills subscriptionBill = sapRevenueCloudSubscriptionService.getSubscriptionBillsById(billId);
		List<SubscriptionBillingData> billsList = new ArrayList<>();
		if(subscriptionBill!=null)
		{
			List<BillItem> billItems = subscriptionBill.getBillItems();
			if(billItems!=null && !billItems.isEmpty())
			{
				return Converters.convertAll(billItems, sapSubscriptionBillDetailConverter);
			}
		}
		return billsList;
	}

	protected SubscriptionPricePlanModel getRatePlanId(final String ratePlanId,
			final CatalogVersionModel currentCatalog) 
	{
		return getSapRevenueCloudProductService().getSubscriptionPricePlanForId(ratePlanId, currentCatalog);
	}

	protected String calculateExtensionDate(final String billingPlanId, final String validUntildate,
			final String extensionPeriod)
	{
		final SimpleDateFormat dateFormatter = new SimpleDateFormat(SaprevenuecloudorderConstants.YYYY_MM_DD);
		String extensionDate = "";
		Integer extensionTerm = Integer.parseInt(extensionPeriod);
		try 
		{
			final String validTilldate = validUntildate;
			final GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(dateFormatter.parse(validTilldate));
			LOG.debug("ValidDate of subscription : " + validTilldate);
			final int month = calendar.get(Calendar.MONTH);
			int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			int newValidTillMonth = 0;

			switch (billingPlanId)
			{
				case "anniversary_monthly":
				case "calendar_monthly":
					newValidTillMonth = month + extensionTerm;
					break;
				case "anniversary_quarterly":
				case "calendar_quarterly":
					newValidTillMonth = month + (extensionTerm * 3);
					break;
				case "anniversary_half_yearly":
				case "calendar_half_yearly":
					newValidTillMonth = month + (extensionTerm * 6);
					break;
				case "anniversary_yearly":
				case "calendar_yearly":
					newValidTillMonth = month + (extensionTerm * 12);
					break;
				default:
					LOG.warn(String.format("Unknown frequency code \"%s\"", billingPlanId));
					break;
			}

			calendar.set(Calendar.MONTH, newValidTillMonth);
			if (billingPlanId.contains("calendar"))
			{
				calendar.set(Calendar.DAY_OF_MONTH, 1);
			} 
			else
			{
				calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			final TimeZone defaultTZ = TimeZone.getTimeZone(
					getConfigurationService().getConfiguration().getString(SaprevenuecloudorderConstants.DEFAULT_TIMEZONE));
			calendar.setTimeZone(defaultTZ);
			extensionDate = calendar.toZonedDateTime().withZoneSameInstant(ZoneId.of(SaprevenuecloudorderConstants.UTC))
					.format(SaprevenuecloudorderConstants.ISO8601_FORMATTER);
			LOG.debug("Calculated Extension Date : " + extensionDate);
			}

		} 
		catch (final ParseException e) 
		{
			LOG.info("Exception while parsing dates :" + e);
		}

		return extensionDate;
	}


	@Override
	public List<ProductData> getUpsellingOptionsForSubscription(final String productCode) 
	{
		return Collections.emptyList();
	}


	protected SapRevenueCloudSubscriptionService getSapSubscriptionService()
	{
		return sapRevenueCloudSubscriptionService;
	}

	@Required
	public void setSapSubscriptionService(final SapRevenueCloudSubscriptionService sapRevenueCloudSubscriptionService) 
	{
		this.sapRevenueCloudSubscriptionService = sapRevenueCloudSubscriptionService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the sapRevenueCloudProductService
	 */
	public SapRevenueCloudProductService getSapRevenueCloudProductService()
	{
		return sapRevenueCloudProductService;
	}

	/**
	 * @param sapRevenueCloudProductService
	 *            the sapRevenueCloudProductService to set
	 */
	public void setSapRevenueCloudProductService(final SapRevenueCloudProductService sapRevenueCloudProductService) 
	{
		this.sapRevenueCloudProductService = sapRevenueCloudProductService;
	}


	@Override
	public BaseStoreService getBaseStoreService() 
	{
		return baseStoreService;
	}

	@Override
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}


	public Converter<Subscription, SubscriptionData> getSapSubscriptionDetailConverter() {
		return sapSubscriptionDetailConverter;
	}

	public void setSapSubscriptionDetailConverter(
			Converter<Subscription, SubscriptionData> sapSubscriptionDetailConverter) {
		this.sapSubscriptionDetailConverter = sapSubscriptionDetailConverter;
	}


	public Converter<Bills, SubscriptionBillingData> getSapSubscriptionBillsConverter() {
		return sapSubscriptionBillsConverter;
	}

	public void setSapSubscriptionBillsConverter(Converter<Bills, SubscriptionBillingData> sapSubscriptionBillsConverter) {
		this.sapSubscriptionBillsConverter = sapSubscriptionBillsConverter;
	}
	

	public Converter<BillItem, SubscriptionBillingData> getSapSubscriptionBillDetailConverter() {
		return sapSubscriptionBillDetailConverter;
	}

	public void setSapSubscriptionBillDetailConverter(
			Converter<BillItem, SubscriptionBillingData> sapSubscriptionBillDetailConverter) {
		this.sapSubscriptionBillDetailConverter = sapSubscriptionBillDetailConverter;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	public Converter<Subscription, SubscriptionData> getSapSubscriptionConverter() {
		return sapSubscriptionConverter;
	}

	public void setSapSubscriptionConverter(Converter<Subscription, SubscriptionData> sapSubscriptionConverter) {
		this.sapSubscriptionConverter = sapSubscriptionConverter;
	}


	public B2BUnitService getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}



}
