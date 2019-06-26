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

package de.hybris.platform.customerinterestsservices.productinterest.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.customerinterestsservices.model.ProductInterestModel;
import de.hybris.platform.customerinterestsservices.productinterest.ProductInterestService;
import de.hybris.platform.customerinterestsservices.productinterest.daos.ProductInterestDao;
import de.hybris.platform.notificationservices.enums.NotificationType;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class DefaultProductInterestService implements ProductInterestService//NOSONAR
{
	private static final Logger LOG = Logger.getLogger(DefaultProductInterestService.class);
	private ModelService modelService;
	private ProductInterestDao productInterestDao;
	private UserService userService;
	private BaseStoreService baseStoreService;
	private BaseSiteService baseSiteService;

	@Override
	public void saveProductInterest(final ProductInterestModel productInterest)
	{
		modelService.save(productInterest);
	}


	@Override
	public void removeProductInterest(final ProductInterestModel productInterest)
	{
		modelService.remove(productInterest);
	}

	@Override
	public Optional<ProductInterestModel> getProductInterest(final ProductModel productModel, final CustomerModel customerModel,
			final NotificationType notificationType, final BaseStoreModel baseStore, final BaseSiteModel baseSite)
	{
		return productInterestDao.findProductInterest(productModel, customerModel, notificationType, baseStore, baseSite);
	}

	@Override
	public void removeAllProductInterests(final String productCode)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final BaseSiteModel baseSiteModel = getBaseSiteService().getCurrentBaseSite();
		productInterestDao.findProductInterestsByCustomer(currentCustomer, baseStoreModel, baseSiteModel).stream()
				.filter(x -> productCode.equals(x.getProduct().getCode())).forEach(x -> getModelService().remove(x));
	}

	@Override
	public Map<ProductModel, Map<NotificationType, Date>> getProductsByCustomerInterests(final PageableData pageableData)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final BaseSiteModel baseSiteModel = getBaseSiteService().getCurrentBaseSite();

		final Map<String, Map<String, String>> productMap = productInterestDao.findProductsByCustomerInterests(currentCustomer,
				baseStoreModel, baseSiteModel, pageableData);

		final Map<ProductModel, Map<NotificationType, Date>> productModelMap = new LinkedHashMap<>();
		productMap
				.forEach((productPk, interestCreationMap) -> fillProductModelMap(productModelMap, productPk, interestCreationMap));
		return productModelMap;

	}

	@Override
	public Map<ProductModel, Map<NotificationType, Date>> findProductInterestsByCustomer()
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final BaseSiteModel baseSiteModel = getBaseSiteService().getCurrentBaseSite();

		final Map<String, Map<String, String>> productMap = productInterestDao
				.findProductInterestRelationsByCustomer(currentCustomer, baseStoreModel, baseSiteModel);

		final Map<ProductModel, Map<NotificationType, Date>> productModelMap = new LinkedHashMap<>();
		productMap
				.forEach((productPk, interestCreationMap) -> fillProductModelMap(productModelMap, productPk, interestCreationMap));
		return productModelMap;
	}

	@Override
	public int getProductsCountByCustomerInterests(final PageableData pageableData)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel baseStoreModel = getBaseStoreService().getCurrentBaseStore();
		final BaseSiteModel baseSiteModel = getBaseSiteService().getCurrentBaseSite();
		return productInterestDao.findProductsCountByCustomerInterests(currentCustomer, baseStoreModel, baseSiteModel,
				pageableData);
	}

	protected void fillProductModelMap(final Map<ProductModel, Map<NotificationType, Date>> productModelMap,
			final String productPk, final Map<String, String> interestCreationMap)
	{
		try
		{
			final ProductModel productModel = modelService.get(PK.parse(productPk));
			final Map<NotificationType, Date> interestMap = buildInterestMap(interestCreationMap);

			productModelMap.put(productModel, interestMap);
		}
		catch (final ModelLoadingException e)//NOSONAR
		{
			LOG.info("No Product found for given pk " + productPk);
		}

	}

	/**
	 * build interest data as a linked map
	 *
	 * @param interestCreationMap
	 * @return
	 */
	protected Map<NotificationType, Date> buildInterestMap(final Map<String, String> interestCreationMap)
	{
		final EnumMap<NotificationType, Date> interestMap = new EnumMap<>(NotificationType.class);
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		interestCreationMap.forEach((notificationTypePK, creationTime) -> {
			try
			{
				final Date creationDate = sdf.parse(creationTime);
				interestMap.put(NotificationType.valueOf(modelService.get(PK.parse(notificationTypePK)).toString()), creationDate);
			}
			catch (final ParseException e)
			{
				LOG.error("Can't parse " + creationTime + " Date format");
			}
			catch (final ModelLoadingException e)//NOSONAR
			{
					LOG.error("No NotificationType found for given pk " + notificationTypePK);
			}
		});
		return interestMap;
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


	protected ProductInterestDao getProductInterestDao()
	{
		return productInterestDao;
	}

	@Required
	public void setProductInterestDao(final ProductInterestDao productInterestDao)
	{
		this.productInterestDao = productInterestDao;
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

	protected BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	@Required
	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
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
