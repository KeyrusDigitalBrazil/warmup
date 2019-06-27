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
package com.sap.hybris.saprevenuecloudproduct.inbound;

import static java.lang.String.format;

import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.subscriptionservices.model.BillingEventModel;
import de.hybris.platform.subscriptionservices.model.BillingTimeModel;
import de.hybris.platform.subscriptionservices.model.OneTimeChargeEntryModel;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.subscription.BillingTimeService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.saprevenuecloudproduct.constants.SaprevenuecloudproductConstants;
import com.sap.hybris.saprevenuecloudproduct.model.SAPMarketToCatalogMappingModel;
import com.sap.hybris.saprevenuecloudproduct.service.SapRevenueCloudProductService;



/**
 * Helper class for all Product import translators
 *
 * @since 6.7
 * 
 * @deprecated This class is deprecated since 1811 
 */
@Deprecated
public class SapRevenueCloudProductInboudHelper
{

	private GenericDao<SAPMarketToCatalogMappingModel> sapMarketToCatalogMappingModelGenericDao;
	private String defaultCatalogId;
	private String defaultCatalogVersion;
	private CatalogService catalogService;
	private CatalogVersionService catalogVersionService;
	private ProductService productService;
	private SapRevenueCloudProductService sapRevenueCloudProductService;
	private ModelService modelService;
	private BillingTimeService billingTimeService;

	private static final Logger LOG = LoggerFactory.getLogger(SapRevenueCloudProductInboudHelper.class);

	/**
	 * Iterates the {@link SAPMarketToCatalogMappingModel} to provide {@link CatalogModel} for a corresponding market id.
	 * If no market to {@link CatalogModel} found, default {@link CatalogModel} and version are selected.
	 *
	 * @param marketId
	 *           - market ID maintained at SAP Revenue Cloud system
	 * @return {@link CatalogModel} - catalogModel
	 */
	public CatalogModel mapMarketToCatalog(final String marketId)
	{
		final Optional<CatalogModel> cmOpt = sapMarketToCatalogMappingModelGenericDao.find().stream()
				.filter(e -> e.getMarketId().equals(marketId)).map(c -> c.getCatalog()).findFirst();
		return cmOpt.orElse(getCatalogService().getCatalogForId(getDefaultCatalogId()));

	}

	/**
	 * Returns the {@link CatalogVersion} for a market ID
	 *
	 * @param marketId
	 *           - market ID maintained at SAP Revenue Cloud system
	 * @return {@link CatalogVersion} - catalog version
	 *
	 */
	@SuppressWarnings("deprecation")
	public CatalogVersion processCatalogVersionForProduct(final String marketId) 
	{

		final CatalogModel cm = this.mapMarketToCatalog(marketId);
		final CatalogVersionModel cvm = catalogVersionService.getCatalogVersion(cm.getId(), getDefaultCatalogVersion());

		return CatalogManager.getInstance().getAllCatalogVersions().stream()
				.filter(cv -> cvm.getCatalog().getId().equals(cv.getCatalog().getId()))
				.filter(cv -> cvm.getVersion().equals(cv.getVersion())).findFirst().get();

	}

	/**
	 * Retruns {@link Product} for a specific product code and market ID. Splits the product code and the market ID from
	 * the cell value and process it.
	 *
	 * @param prodCodeMarkrtId
	 *           - product code with market ID combined with ':'
	 *
	 * @return {@link Product} - product
	 *
	 */
	public Product processProductForCodeAndMarketId(final String prodCodeMarkrtId)
	{
		final String productCode = StringUtils.substringBefore(prodCodeMarkrtId, ":");
		final String marketId = StringUtils.substringAfter(prodCodeMarkrtId, ":");
		if (StringUtils.isAnyEmpty(productCode, marketId))
		{
			throw new IllegalArgumentException("Parameters cannot be null");
		}
		final ProductModel product = getProductForCodeAndMarket(productCode, marketId);
		return ProductManager.getInstance().getProductByPK(product.getPk());
	}



	/**
	 * Returns the {@link ProductModel} for a specific product code and market ID
	 *
	 * @param productCode
	 *           - product code
	 * @param marketId
	 *           - market ID
	 *
	 * @return {@link ProductModel}
	 *
	 */
	private ProductModel getProductForCodeAndMarket(final String productCode, final String marketId)
	{
		final CatalogModel cm = this.mapMarketToCatalog(marketId);
		final CatalogVersionModel cvm = catalogVersionService.getCatalogVersion(cm.getId(), getDefaultCatalogVersion());
		return getProductService().getProductForCode(cvm, productCode);
	}

	/**
	 * Find the product associated to the subscription price plan and iterate through all its Subscription price plans.
	 * For the Subscription price plans whose price plan id is not the current one and end date is greater than current
	 * date, set it as current date as the end date
	 *
	 * @param pricePlanId
	 *           - subscription pricePlan ID
	 * @param cv
	 *           - {@link CatalogVersion}
	 *
	 */

	public void processSubscriptionPricePlanEndDate(final String pricePlanId, final CatalogVersion cv)
	{

		try
		{
			final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(cv.getCatalog().getId(),
					cv.getVersion());
			final SubscriptionPricePlanModel pricePlan = getSapRevenueCloudProductService()
					.getSubscriptionPricePlanForId(pricePlanId, catalogVersion);
			if (null != pricePlan.getProduct() && CollectionUtils.isNotEmpty(pricePlan.getProduct().getEurope1Prices()))
			{
				pricePlan.getProduct().getEurope1Prices().stream().filter(SubscriptionPricePlanModel.class::isInstance)
						.map(SubscriptionPricePlanModel.class::cast).filter(s -> !pricePlanId.equals(s.getPricePlanId()))
						.filter(s -> (s.getEndTime() != null && s.getEndTime().after(new Date()))).forEach(s -> {
							s.setEndTime(new Date());
							getModelService().save(s);
						});
			}

		}
		catch (final Exception e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(format("Error while setting the end date for the price plan with ID [%s]. Error details [%s]", pricePlanId, e));
			}
			LOG.error(format("Error while setting the end date for the price plan with ID %s.", pricePlanId));

		}

	}


	/**
	 * Create or update the {@link OneTimeChargeEntryModel} for a specific price plan ID and {@link CatalogVersion}
	 *
	 * First get the {@link SubscriptionPricePlanModel} from the price pan ID and the {@link CatalogVersionModel}. Then
	 * check if any {@link OneTimeChargeEntryModel} with {@link BillingEventModel} with code 'paynow' is present in the
	 * {@link SubscriptionPricePlanModel}. If yes, update the {@link OneTimeChargeEntryModel}'s price with all other
	 * {@link OneTimeChargeEntryModel}'s price value. If no {@link OneTimeChargeEntryModel} with
	 * {@link BillingEventModel} with code 'paynow' present, create a new one.
	 *
	 * @param pricePlanId
	 *           - subscription price plan ID
	 * @param cv
	 *           - {@link CatalogVersion}
	 *
	 *
	 */
	public void createUpdatePayNowChargeEntry(final String pricePlanId, final CatalogVersion cv)
	{
		try
		{

			final CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(cv.getCatalog().getId(),
					cv.getVersion());

			final SubscriptionPricePlanModel pricePlanModel = getSapRevenueCloudProductService()
					.getSubscriptionPricePlanForId(pricePlanId, catalogVersion);

			Optional.ofNullable(pricePlanModel).ifPresent(pricePlan -> {
				final OneTimeChargeEntryModel payNowCharge = checkForPayNowChargeEntry(pricePlan);
				modelService.save(pricePlanModel);
				modelService.refresh(pricePlanModel);
				updatePayNowChargeEntry(pricePlan, payNowCharge);
			});

		}
		catch (final Exception e)
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug(
						format("Error while creating and updating  One Time charge  for the price plan with ID [%s]. Error details [%s]", pricePlanId , e));
			}
			LOG.error(format("Error creating and updating One Time charge  for the price plan with ID %s.", pricePlanId));
		}


	}

	/**
	 * Checks if any {@link OneTimeChargeEntryModel} with {@link BillingEventModel} with code 'paynow' present. if yes,
	 * return it. Otherwise create a new one and return it.
	 *
	 * @param pricePlanModel
	 *           - {@link SubscriptionPricePlanModel}
	 * @return {@link OneTimeChargeEntryModel} - One Time charge entry with billing event paynow
	 */
	public OneTimeChargeEntryModel checkForPayNowChargeEntry(final SubscriptionPricePlanModel pricePlanModel)
	{

		if (CollectionUtils.isEmpty(pricePlanModel.getOneTimeChargeEntries()))
		{
			final OneTimeChargeEntryModel payNowChargeEntry = createNewPayNowChargeEntry(pricePlanModel);
			pricePlanModel.setOneTimeChargeEntries(Arrays.asList(payNowChargeEntry));
			getModelService().save(pricePlanModel);
			return payNowChargeEntry;
		}
		else
		{
			return pricePlanModel.getOneTimeChargeEntries().stream()
					.filter(otc -> SaprevenuecloudproductConstants.PAYNOW_BILLING_EVENT_CODE.equals(otc.getBillingEvent().getCode()))
					.findFirst().orElseGet(() -> {
						final OneTimeChargeEntryModel payNowChargeEntry = createNewPayNowChargeEntry(pricePlanModel);
						final List<OneTimeChargeEntryModel> otcEntries = new ArrayList(pricePlanModel.getOneTimeChargeEntries());
						otcEntries.add(payNowChargeEntry);
						pricePlanModel.setOneTimeChargeEntries(otcEntries);
						getModelService().save(pricePlanModel);
						return payNowChargeEntry;
					});

		}

	}

	/**
	 * Update the {@link OneTimeChargeEntryModel}'s price value with the sum of all other {@link OneTimeChargeEntryModel}
	 * price value associated to the specific {@link SubscriptionPricePlanModel}
	 *
	 * @param pricePlanModel
	 *           - {@link SubscriptionPricePlanModel}
	 * @param payNowCharge
	 *           - {@link OneTimeChargeEntryModel}
	 */
	public void updatePayNowChargeEntry(final SubscriptionPricePlanModel pricePlanModel,
			final OneTimeChargeEntryModel payNowCharge)
	{
		final Double totalOneTimePrices = pricePlanModel.getOneTimeChargeEntries().stream().filter(otc -> !otc.equals(payNowCharge))
				.mapToDouble(otc -> otc.getPrice()).sum();
		payNowCharge.setPrice(totalOneTimePrices);
		getModelService().save(payNowCharge);

	}

	/**
	 * Creates a new {@link OneTimeChargeEntryModel} and returns it.
	 *
	 * @param pricePlan
	 *           - {@link SubscriptionPricePlanModel}
	 * @return {@link OneTimeChargeEntryModel} - one Time charge entry
	 */
	private OneTimeChargeEntryModel createNewPayNowChargeEntry(final SubscriptionPricePlanModel pricePlan)
	{
		final OneTimeChargeEntryModel otcEntry = getModelService().create(OneTimeChargeEntryModel.class);

		final BillingTimeModel billTimeModel = getBillingTimeService()
				.getBillingTimeForCode(SaprevenuecloudproductConstants.PAYNOW_BILLING_EVENT_CODE);
		if (billTimeModel instanceof BillingEventModel)
		{
			otcEntry.setBillingEvent((BillingEventModel) billTimeModel);
		}
		otcEntry.setCatalogVersion(pricePlan.getCatalogVersion());
		otcEntry.setSubscriptionPricePlanOneTime(pricePlan);
		otcEntry.setPrice(0.0D);
		getModelService().save(otcEntry);
		return otcEntry;

	}
	



	/**
	 * Returns the attribute value for the attribute name from the {@link Item} being currently processed
	 *
	 * @param processedItem
	 *           - current item being processed.
	 * @param attributeName
	 *           - name of the attribute
	 * @return {@link Object} - Generic item returned
	 */
	public Object getAttributeValue(final Item processedItem, final String attributeName) throws ImpExException
	{
		try
		{
			return processedItem.getAttribute(attributeName);
		}
		catch (final JaloSecurityException e)
		{
			throw new ImpExException(e);
		}
	}

	/**
	 * @return the sapMarketToCatalogMappingModelGenericDao
	 */
	public GenericDao<SAPMarketToCatalogMappingModel> getSapMarketToCatalogMappingModelGenericDao()
	{
		return sapMarketToCatalogMappingModelGenericDao;
	}

	/**
	 * @param sapMarketToCatalogMappingModelGenericDao
	 *           the sapMarketToCatalogMappingModelGenericDao to set
	 */
	public void setSapMarketToCatalogMappingModelGenericDao(
			final GenericDao<SAPMarketToCatalogMappingModel> sapMarketToCatalogMappingModelGenericDao)
	{
		this.sapMarketToCatalogMappingModelGenericDao = sapMarketToCatalogMappingModelGenericDao;
	}



	/**
	 * @return the defaultCatalogId
	 */
	public String getDefaultCatalogId()
	{
		return defaultCatalogId;
	}

	/**
	 * @param defaultCatalogId
	 *           the defaultCatalogId to set
	 */
	public void setDefaultCatalogId(final String defaultCatalogId)
	{
		this.defaultCatalogId = defaultCatalogId;
	}

	/**
	 * @return the defaultCatalogVersion
	 */
	public String getDefaultCatalogVersion()
	{
		return defaultCatalogVersion;
	}

	/**
	 * @param defaultCatalogVersion
	 *           the defaultCatalogVersion to set
	 */
	@Required
	public void setDefaultCatalogVersion(final String defaultCatalogVersion)
	{
		this.defaultCatalogVersion = defaultCatalogVersion;
	}

	/**
	 * @return the catalogService
	 */
	public CatalogService getCatalogService()
	{
		return catalogService;
	}

	/**
	 * @param catalogService
	 *           the catalogService to set
	 */
	public void setCatalogService(final CatalogService catalogService)
	{
		this.catalogService = catalogService;
	}

	/**
	 * @return the catalogVersionService
	 */
	public CatalogVersionService getCatalogVersionService()
	{
		return catalogVersionService;
	}

	/**
	 * @param catalogVersionService
	 *           the catalogVersionService to set
	 */
	public void setCatalogVersionService(final CatalogVersionService catalogVersionService)
	{
		this.catalogVersionService = catalogVersionService;
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
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
	 *           the sapRevenueCloudProductService to set
	 */
	public void setSapRevenueCloudProductService(final SapRevenueCloudProductService sapRevenueCloudProductService)
	{
		this.sapRevenueCloudProductService = sapRevenueCloudProductService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the billingTimeService
	 */
	public BillingTimeService getBillingTimeService()
	{
		return billingTimeService;
	}

	/**
	 * @param billingTimeService
	 *           the billingTimeService to set
	 */
	public void setBillingTimeService(final BillingTimeService billingTimeService)
	{
		this.billingTimeService = billingTimeService;
	}

}