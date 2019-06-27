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
package de.hybris.platform.stocknotificationservices.cronjob;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.stock.strategies.WarehouseSelectionStrategy;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.notificationservices.enums.SiteMessageType;
import de.hybris.platform.notificationservices.model.SiteMessageModel;
import de.hybris.platform.notificationservices.service.NotificationService;
import de.hybris.platform.notificationservices.service.SiteMessageService;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.stock.StockService;
import de.hybris.platform.stocknotificationservices.constants.StocknotificationservicesConstants;
import de.hybris.platform.stocknotificationservices.dao.BackInStockProductInterestDao;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.localization.Localization;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.task.TaskExecutor;


/**
 * Abstract base class for sending BACK_IN_STOCK notification to customer
 */
public abstract class AbstractStockLevelStatusJob extends AbstractJobPerformable<CronJobModel>
{
	private StockService stockService;
	private BaseStoreService baseStoreService;
	private WarehouseSelectionStrategy warehouseSelectionStrategy;
	private BackInStockProductInterestDao backInStockProductInterestDao;
	private List<ProductInterestModel> inStockProductInterests;
	private TaskExecutor taskExecutor;
	private NotificationService notificationService;
	private SiteMessageService siteMessageService;
	private CommerceCommonI18NService commerceCommonI18NService;

	@Override
	public PerformResult perform(final CronJobModel job)
	{
		final Date now = Calendar.getInstance().getTime();
		final List<ProductInterestModel> productIntersts = getBackInStockProductInterestDao().findBackInStorkProductInterests();
		inStockProductInterests = productIntersts.stream().filter(pi -> isProductInStock(pi, now)).collect(Collectors.toList());
		final Map<ProductModel, SiteMessageModel> messages = new ConcurrentHashMap<>();

		inStockProductInterests.forEach(productInterest -> {
			// update the expire date to now, it will be cleaned up by another cron-job
			productInterest.setExpiryDate(now);
			modelService.save(productInterest);

			final ProductModel product = productInterest.getProduct();
			if (!messages.containsKey(product))
			{
				messages.put(product, createSiteMessage(productInterest));
			}

			final Map<String, ItemModel> data = new HashMap<>();
			data.put(StocknotificationservicesConstants.LANGUAGE, productInterest.getLanguage());
			data.put(StocknotificationservicesConstants.PRODUCT_INTEREST, productInterest);
			data.put(StocknotificationservicesConstants.SITE_MESSAGE, messages.get(product));
			getTaskExecutor().execute(createTask(data));
		});

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

	protected SiteMessageModel createSiteMessage(final ProductInterestModel interest)
	{
		final ProductModel product = interest.getProduct();
		final Locale locale = getCommerceCommonI18NService().getLocaleForLanguage(interest.getLanguage());
		final String title = Localization.getLocalizedString("back_in_stock.notification.sitemessage.title");
		final String content = Localization.getLocalizedString("back_in_stock.notification.sitemessage.content", new Object[]
		{ product.getName(locale) });

		return getSiteMessageService().createMessage(title, content, SiteMessageType.SYSTEM, product,
				NotificationType.BACK_IN_STOCK, locale);
	}

	protected abstract StockNotificationTask createTask(final Map<String, ItemModel> data);

	protected boolean isProductInStock(final ProductInterestModel productInterest, final Date now)
	{
		final ProductModel product = productInterest.getProduct();
		if (product == null)
		{
			modelService.remove(productInterest);
			return false;
		}
		if (!isProductOnSale(product, now))
		{
			return false;
		}

		final BaseStoreModel currentBaseStore = productInterest.getBaseStore();
		final List<WarehouseModel> wareHoustList = getWarehouseSelectionStrategy().getWarehousesForBaseStore(currentBaseStore);
		final StockLevelStatus stockLevelStatus = getStockService().getProductStatus(productInterest.getProduct(), wareHoustList);
		return StockLevelStatus.INSTOCK.equals(stockLevelStatus);
	}

	protected boolean isProductOnSale(final ProductModel product, final Date now)
	{
		final Date onlineDate = product.getOnlineDate();
		final Date offlineDate = product.getOfflineDate();
		return (onlineDate == null || onlineDate.before(now)) && (offlineDate == null || offlineDate.after(now));
	}

	protected StockService getStockService()
	{
		return stockService;
	}

	@Required
	public void setStockService(final StockService stockService)
	{
		this.stockService = stockService;
	}

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}

	protected WarehouseSelectionStrategy getWarehouseSelectionStrategy()
	{
		return warehouseSelectionStrategy;
	}

	@Required
	public void setWarehouseSelectionStrategy(final WarehouseSelectionStrategy warehouseSelectionStrategy)
	{
		this.warehouseSelectionStrategy = warehouseSelectionStrategy;
	}

	protected BackInStockProductInterestDao getBackInStockProductInterestDao()
	{
		return backInStockProductInterestDao;
	}

	@Required
	public void setBackInStockProductInterestDao(final BackInStockProductInterestDao backInStockProductInterestDao)
	{
		this.backInStockProductInterestDao = backInStockProductInterestDao;
	}

	public List<ProductInterestModel> getInStockProductInterests()
	{
		return inStockProductInterests;
	}

	@Required
	public void setTaskExecutor(final TaskExecutor taskExecutor)
	{
		this.taskExecutor = taskExecutor;
	}

	protected TaskExecutor getTaskExecutor()
	{
		return taskExecutor;
	}

	protected NotificationService getNotificationService()
	{
		return notificationService;
	}

	@Required
	public void setNotificationService(final NotificationService notificationService)
	{
		this.notificationService = notificationService;
	}

	protected SiteMessageService getSiteMessageService()
	{
		return siteMessageService;
	}

	@Required
	public void setSiteMessageService(final SiteMessageService siteMessageService)
	{
		this.siteMessageService = siteMessageService;
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

}
