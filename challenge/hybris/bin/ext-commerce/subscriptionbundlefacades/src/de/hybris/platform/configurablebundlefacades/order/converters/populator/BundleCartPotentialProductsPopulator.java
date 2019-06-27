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

package de.hybris.platform.configurablebundlefacades.order.converters.populator;

import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.configurablebundlefacades.data.BundleTemplateData;
import de.hybris.platform.configurablebundlefacades.order.converters.comparator.AbstractBundleOrderEntryComparator;
import de.hybris.platform.configurablebundleservices.bundle.BundleCommerceCartService;
import de.hybris.platform.configurablebundleservices.bundle.BundleTemplateService;
import de.hybris.platform.configurablebundleservices.constants.ConfigurableBundleServicesConstants;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import javax.annotation.Nonnull;

/**
 * Populates cart DTO with potential entries based on existing entries.
 *
 * @param <S>
 *    	CartModel
 * @param <T>
 *    	CartData
 * @deprecated since 6.5 - is not a part of generic bundling functionality. Should be implemented for
 * specific accelerator module if is planned to be used.
 */
@Deprecated
public class BundleCartPotentialProductsPopulator<S extends CartModel, T extends CartData> extends
		AbstractBundleOrderPopulator<S, T>
{
	protected static final String POTENTIAL_PRODUCT_COUNT_KEY = "configurablebundlefacades.bundlecartpopulator.potentialproduct.count";
	protected static final int DEFAULT_PRODUCT_COUNT = 5;

	private BundleCommerceCartService bundleCommerceCartService;
	private BundleTemplateService bundleTemplateService;
	private Collection<AbstractBundleOrderEntryComparator<OrderEntryData>> orderEntryDataComparators;
	private AbstractProductPopulator<ProductModel, ProductData> productBundlePopulator;
	private ConfigurationService configurationService;

	private CartService cartService;

	/**
	 * This template method which coordinates the data population
	 */
	@Override
	public void populate(@Nonnull final S source, @Nonnull final T target)
	{
		validateParameterNotNullStandardMessage("source", source);
		validateParameterNotNullStandardMessage("target", target);
		final List<OrderEntryData> entriesList = getPotentialProducts(source, target);

		mergeEntries(entriesList, target);

		final List<OrderEntryData> sortedEntries = sortingOrderEntries(target.getEntries());
		target.setEntries(sortedEntries);

	}

	/**
	 * Method to populate the potential products as OrderEntryData for the each Bundle component that have been added in
	 * the cart.
	 *
	 * @param source population source
	 * @param target population target
	 * @return list of existing order entry and the potential products
	 */
	protected List<OrderEntryData> getPotentialProducts(@Nonnull final S source, @Nonnull final T target)
	{
		if (CollectionUtils.isEmpty(target.getEntries()))
		{
			return Collections.emptyList();
		}

		// List which holds the potential products from all the component.
		final List<OrderEntryData> potentialEntriesList = new ArrayList<>();
		//local variable which holds the bundle component used to eliminate the hunt for the potential product
		//if it is already done.
		final List<String> visitedTemplate = new ArrayList<>();
		target.getEntries().stream()
				.filter(orderEntry -> orderEntry.getProduct() != null)
				.filter(orderEntry -> orderEntry.getProduct().getBundleTemplates() != null)
				.peek(orderEntry -> populateEntryProduct(source, orderEntry))
				.filter(orderEntry -> orderEntry.getBundleNo() > ConfigurableBundleServicesConstants.NO_BUNDLE)
				.forEach(orderEntry -> orderEntry.getProduct().getBundleTemplates().stream()
						.filter(template -> template.getId() != null)
						.filter(template -> !visitedTemplate.contains(template.getId()))
						.peek(template -> visitedTemplate.add(template.getId()))
						.forEach(template -> addPotentialProduct(source, target, orderEntry, template, potentialEntriesList))
				);

		return potentialEntriesList;
	}

	protected void addPotentialProduct(@Nonnull final S source, @Nonnull final T target,
			@Nonnull final OrderEntryData orderEntry,
			@Nonnull final BundleTemplateData component, @Nonnull final List<OrderEntryData> potentialEntries)
	{
		final List<CartEntryModel> bundleEntries = getBundleCommerceCartService().getCartEntriesForComponentInBundle(
				source, getBundleTemplateService().getBundleTemplateForCode(component.getId()),
				orderEntry.getBundleNo());

		final int maxPotentialProducts = maxPotentialProducts(component, bundleEntries.size());

		if (maxPotentialProducts > 0 && getCartMaxPotentialProductCount(target, component) < maxPotentialProducts)
		{
			// call to create new OrderEntryData for each bundle component
			potentialEntries.addAll(populatePotentialOrderEntry(target, component, maxPotentialProducts,
					orderEntry.getProduct().getBundleTemplates()));
		}
	}

	protected void populateEntryProduct(@Nonnull final CartModel source, @Nonnull final OrderEntryData orderEntry)
	{
		final CartEntryModel entryModel = getCartService().getEntryForNumber(source, orderEntry.getEntryNumber().intValue());
		getProductBundlePopulator().populate(entryModel.getProduct(), orderEntry.getProduct());
	}

	/**
	 * Get the count number for current cart DTO's potential product.
	 *
	 * @param orderData order to count potential products for
	 * @param template component
	 * @return number of potential products for given component
	 */
	 protected int getCartMaxPotentialProductCount(
	 		@Nonnull final AbstractOrderData orderData, @Nonnull final BundleTemplateData template)
	 {
		 int cnt = 0;
		 for (final OrderEntryData entryData : orderData.getEntries())
		 {
			 if (areComponentsEqual(entryData, template))
			 {
				 cnt++;
			 }
		 }

		 return cnt;
	 }

	protected boolean areComponentsEqual(@Nonnull final OrderEntryData entryData, @Nonnull final BundleTemplateData templateData)
	{
		if (entryData.getComponent() == null || entryData.getBundleNo() != ConfigurableBundleServicesConstants.NO_BUNDLE)
		{
			return false;
		}
		if (templateData.getId() == null || !templateData.getId().equals(entryData.getComponent().getId()))
		{
			return false;
		}
		return templateData.getVersion() != null && templateData.getVersion().equals(entryData.getComponent().getVersion());
	}

	/**
	 * Method to get the possible potential product count for the each Bundle component that have been added in the cart.
	 *
	 * @param bundleTemplate component
	 * @param productsInCart number of products for the component
	 * @return returns the maximum possible potential product count
	 */
	protected int maxPotentialProducts(@Nonnull final BundleTemplateData bundleTemplate, final int productsInCart)
	{
		//* When number of selected products in a bundle cart component matches upper limit of selection criteria,
		//   then the potential product should be zero for that Bundle component.
		//* When number of selected products in a bundle cart component is lower then upper limit of selection criteria,
		//	  then show available potential products up to configured maximum.
		final int configuredNumberofProducts
				= getConfigurationService().getConfiguration().getInt(POTENTIAL_PRODUCT_COUNT_KEY, DEFAULT_PRODUCT_COUNT);

		return (bundleTemplate.getMaxItemsAllowed() - productsInCart) > 0 ? configuredNumberofProducts : 0;
	}


	/**
	 * Helper method used to populate the potential products as OrderEntryData
	 *
	 * @param target DTO
	 * @param template component
	 * @param numberOfPotentialProduct number of free slots for potential components
	 * @return List of potential products as OrderEntryData
	 */
	protected List<OrderEntryData> populatePotentialOrderEntry(
			@Nonnull final AbstractOrderData target, @Nonnull final BundleTemplateData template,
			final int numberOfPotentialProduct, final List<BundleTemplateData> allBundleTemplates)
	{
		final List<OrderEntryData> potentialEntriesList = new ArrayList<>();

		if (CollectionUtils.isEmpty(template.getProducts()))
		{
			return potentialEntriesList;
		}

		for (final ProductData productData : template.getProducts())
		{
			// create the OrderEntry only for the product which is not exist in the cart.
			if (!isDuplicateProduct(target, productData, template) && potentialEntriesList.size() < numberOfPotentialProduct)
			{
				potentialEntriesList.add(createOrderEntryData(productData, template));
				productData.setBundleTemplates(allBundleTemplates);
			}
		}

		return potentialEntriesList;

	}

	/**
	 * Helper method used to create OrderEntryData for the given product.
	 *
	 * @param product a product
	 * @return OrderEntryData
	 */
	protected OrderEntryData createOrderEntryData(final ProductData product, final BundleTemplateData bundleTemplate)
	{
		final OrderEntryData orderEntry = new OrderEntryData();

		orderEntry.setAddable(true);
		orderEntry.setProduct(product);
		orderEntry.setComponent(bundleTemplate);
		return orderEntry;
	}

	/**
	 * Helper method used to find whether the given product that belongs to the Bundle component is already a part of
	 * cart entry.
	 *
	 * @param target cart DTO
	 * @param productData product
	 * @param template component
	 * @return true if the product already exists in the cart
	 */
	protected boolean isDuplicateProduct(@Nonnull final AbstractOrderData target, @Nonnull final ProductData productData,
			@Nonnull final BundleTemplateData template)
	{
		return target.getEntries().stream()
				.filter(Objects::nonNull)
				.filter(orderEntry -> orderEntry.getComponent() != null)
				.filter(orderEntry -> orderEntry.getProduct() != null)
				.filter(orderEntry -> productData.getCode().equals(orderEntry.getProduct().getCode()))
				.anyMatch(orderEntry -> template.getId().equals(orderEntry.getComponent().getId()));
	}

	/**
	 * Helper method to merge the potential product list to the existing OrderEntry.
	 *
	 * @param potentialProduct potential products
	 * @param target cart
	 */
	protected void mergeEntries(final List<OrderEntryData> potentialProduct, @Nonnull final AbstractOrderData target)
	{
		final List<OrderEntryData> consolidatedEntries = new ArrayList<>(target.getEntries());
		consolidatedEntries.addAll(potentialProduct);
		target.setEntries(consolidatedEntries);
	}

	/**
	 * Method which delegates the method call to AbstractBundleOrderEntryComparator which sorts the OrderDataEntry based
	 * on product position that have been added to the component.
	 *
	 * @param orderEntries entries to sort
	 * @return sorted list of OrderEntryData
	 */
	protected List<OrderEntryData> sortingOrderEntries(@Nonnull final List<OrderEntryData> orderEntries)
	{
		for (final AbstractBundleOrderEntryComparator<OrderEntryData> comparator : getOrderEntryDataComparators())
		{
			orderEntries.sort(comparator);
		}
		return orderEntries;
	}

	@Required
	public void setBundleCommerceCartService(final BundleCommerceCartService bundleCommerceCartService)
	{
		this.bundleCommerceCartService = bundleCommerceCartService;
	}

	protected BundleCommerceCartService getBundleCommerceCartService()
	{
		return bundleCommerceCartService;
	}

	@Override
	@Required
	public void setBundleTemplateService(final BundleTemplateService bundleTemplateService)
	{
		this.bundleTemplateService = bundleTemplateService;
	}

	@Override
	protected BundleTemplateService getBundleTemplateService()
	{
		return bundleTemplateService;
	}

	protected Collection<AbstractBundleOrderEntryComparator<OrderEntryData>> getOrderEntryDataComparators()
	{
		return orderEntryDataComparators;
	}

	@Required
	public void setOrderEntryDataComparators(
			final Collection<AbstractBundleOrderEntryComparator<OrderEntryData>> orderEntryDataComparators)
	{
		this.orderEntryDataComparators = orderEntryDataComparators;
	}

    protected AbstractProductPopulator<ProductModel, ProductData> getProductBundlePopulator()
    {
        return productBundlePopulator;
    }

    @Required
    public void setProductBundlePopulator(AbstractProductPopulator<ProductModel, ProductData> productBundlePopulator)
    {
        this.productBundlePopulator = productBundlePopulator;
    }

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(CartService cartService)
	{
		this.cartService = cartService;
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
}
