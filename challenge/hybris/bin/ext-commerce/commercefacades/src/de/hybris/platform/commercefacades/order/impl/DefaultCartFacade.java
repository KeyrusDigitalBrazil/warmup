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
package de.hybris.platform.commercefacades.order.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.commercefacades.i18n.comparators.CountryComparator;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.data.AddToCartParams;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.CommerceCartMetadata;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.strategies.merge.ProductConfigurationMergeStrategy;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.enums.CountryType;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestoration;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartMetadataParameter;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.service.data.ProductConfigurationItem;
import de.hybris.platform.commerceservices.service.data.RemoveEntryGroupParameter;
import de.hybris.platform.commerceservices.util.CommerceCartMetadataParameterUtils;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.storelocator.pos.PointOfServiceService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Implementation for {@link CartFacade}. Delivers main functionality for cart.
 */
public class DefaultCartFacade implements CartFacade
{
	private CartService cartService;
	private ProductService productService;
	private CommerceCartService commerceCartService;
	private Converter<CartModel, CartData> miniCartConverter;
	private Converter<CartModel, CartData> cartConverter;
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	private Converter<CommerceCartRestoration, CartRestorationData> cartRestorationConverter;
	private BaseSiteService baseSiteService;
	private UserService userService;
	private PointOfServiceService pointOfServiceService;
	private DeliveryService deliveryService;
	private Converter<CountryModel, CountryData> countryConverter;
	private PriceDataFactory priceDataFactory;
	private Converter<AbstractOrderModel, List<CartModificationData>> groupCartModificationListConverter;
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	private Converter<AddToCartParams, CommerceCartParameter> commerceCartParameterConverter;
	private Map<ConfiguratorType, ProductConfigurationMergeStrategy> productConfigurationMergeStrategies;
	private ProductFacade productFacade;
	private ModelService modelService;

	@Override
	public CartData getSessionCart()
	{
		final CartData cartData;
		if (hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();
			cartData = getCartConverter().convert(cart);
		}
		else
		{
			cartData = createEmptyCart();
		}
		return cartData;
	}

	@Override
	public String getSessionCartGuid()
	{
		String sessionCartGuid = null;
		if (hasSessionCart())
		{
			sessionCartGuid = getCartService().getSessionCart().getGuid();
		}
		return sessionCartGuid;
	}

	@Override
	public CartData getMiniCart()
	{
		final CartData cartData;
		if (hasSessionCart())
		{
			final CartModel cart = getCartService().getSessionCart();
			cartData = getMiniCartConverter().convert(cart);
		}
		else
		{
			cartData = createEmptyCart();
		}
		return cartData;
	}

	protected CartData createEmptyCart()
	{
		return getMiniCartConverter().convert(null);
	}

	@Override
	public boolean hasSessionCart()
	{
		return getCartService().hasSessionCart();
	}

	@Override
	public boolean hasEntries()
	{
		return hasSessionCart() && !CollectionUtils.isEmpty(getCartService().getSessionCart().getEntries());
	}

	@Override
	public CartModificationData addToCart(final String code, final long quantity) throws CommerceCartModificationException
	{
		final AddToCartParams params = new AddToCartParams();
		params.setProductCode(code);
		params.setQuantity(quantity);

		return addToCart(params);
	}

	@Override
	public CartModificationData addToCart(final String code, final long quantity, final String storeId)
			throws CommerceCartModificationException
	{
		final AddToCartParams params = new AddToCartParams();
		params.setProductCode(code);
		params.setQuantity(quantity);
		params.setStoreId(storeId);

		return addToCart(params);
	}

	@Override
	public CartModificationData addToCart(final AddToCartParams addToCartParams) throws CommerceCartModificationException
	{
		final CommerceCartParameter parameter = getCommerceCartParameterConverter().convert(addToCartParams);
		final CommerceCartModification modification = getCommerceCartService().addToCart(parameter);

		return getCartModificationConverter().convert(modification);
	}

	@Override
	public List<CartModificationData> validateCartData() throws CommerceCartModificationException
	{
		if (hasSessionCart())
		{
			final CommerceCartParameter parameter = new CommerceCartParameter();
			parameter.setEnableHooks(true);
			parameter.setCart(getCartService().getSessionCart());
			return Converters.convertAll(getCommerceCartService().validateCart(parameter), getCartModificationConverter());
		}
		else
		{
			return Collections.emptyList();
		}
	}

	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final long quantity)
			throws CommerceCartModificationException
	{
		final AddToCartParams dto = new AddToCartParams();
		dto.setQuantity(quantity);
		final CommerceCartParameter parameter = getCommerceCartParameterConverter().convert(dto);
		parameter.setEnableHooks(true);
		parameter.setEnableHooks(true);
		parameter.setEntryNumber(entryNumber);

		final CommerceCartModification modification = getCommerceCartService().updateQuantityForCartEntry(parameter);

		return getCartModificationConverter().convert(modification);
	}

	@Override
	public CartModificationData updateCartEntry(final long entryNumber, final String storeId)
			throws CommerceCartModificationException
	{
		final AddToCartParams dto = new AddToCartParams();
		dto.setStoreId(storeId);
		final CommerceCartParameter parameter = getCommerceCartParameterConverter().convert(dto);
		parameter.setEnableHooks(true);
		parameter.setEntryNumber(entryNumber);
		final CommerceCartModification commerceCartModification;
		if (parameter.getPointOfService() == null)
		{
			commerceCartModification = getCommerceCartService().updateToShippingModeForCartEntry(parameter);
		}
		else
		{
			commerceCartModification = getCommerceCartService().updatePointOfServiceForCartEntry(parameter);
		}
		return getCartModificationConverter().convert(commerceCartModification);
	}

	@Override
	public CartRestorationData restoreSavedCart(final String guid) throws CommerceCartRestorationException
	{
		if (!hasEntries() && !hasEntryGroups())
		{
			getCartService().setSessionCart(null);
		}

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		final CartModel cartForGuidAndSiteAndUser = getCommerceCartService().getCartForGuidAndSiteAndUser(guid,
				getBaseSiteService().getCurrentBaseSite(), getUserService().getCurrentUser());
		parameter.setCart(cartForGuidAndSiteAndUser);

		return getCartRestorationConverter().convert(getCommerceCartService().restoreCart(parameter));
	}

	@Override
	public CartRestorationData restoreAnonymousCartAndTakeOwnership(final String guid) throws CommerceCartRestorationException
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		final CartModel cart = getCommerceCartService().getCartForGuidAndSiteAndUser(guid, currentBaseSite,
				getUserService().getAnonymousUser());
		if (cart == null)
		{
			throw new CommerceCartRestorationException(String.format("Cart not found for guid %s", guid));
		}
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cart);

		final CommerceCartRestoration commerceCartRestoration = getCommerceCartService().restoreCart(parameter);
		getCartService().changeCurrentCartUser(getUserService().getCurrentUser());
		return getCartRestorationConverter().convert(commerceCartRestoration);
	}

	@Override
	public CartRestorationData restoreAnonymousCartAndMerge(final String fromAnonymousCartGuid, final String toUserCartGuid)
			throws CommerceCartRestorationException, CommerceCartMergingException
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		final CartModel fromCart = getCommerceCartService().getCartForGuidAndSiteAndUser(fromAnonymousCartGuid, currentBaseSite,
				getUserService().getAnonymousUser());

		final CartModel toCart = getCommerceCartService().getCartForGuidAndSiteAndUser(toUserCartGuid, currentBaseSite,
				getUserService().getCurrentUser());

		if (toCart == null)
		{
			throw new CommerceCartRestorationException("Cart cannot be null");
		}

		if (fromCart == null)
		{
			return restoreSavedCart(toUserCartGuid);
		}

		/*
		 * if(fromCart != null && toCart == null) { return restoreAnonymousCartAndTakeOwnership(fromAnonymousCartGuid); }
		 *
		 * if(fromCart == null && toCart == null) { return null; }
		 */

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(toCart);

		final CommerceCartRestoration restoration = getCommerceCartService().restoreCart(parameter);
		parameter.setCart(getCartService().getSessionCart());

		commerceCartService.mergeCarts(fromCart, parameter.getCart(), restoration.getModifications());

		final CommerceCartRestoration commerceCartRestoration = getCommerceCartService().restoreCart(parameter);

		commerceCartRestoration.setModifications(restoration.getModifications());

		getCartService().changeCurrentCartUser(getUserService().getCurrentUser());
		return getCartRestorationConverter().convert(commerceCartRestoration);
	}

	@Override
	public CartRestorationData restoreCartAndMerge(final String fromUserCartGuid, final String toUserCartGuid)
			throws CommerceCartRestorationException, CommerceCartMergingException
	{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		final CartModel fromCart = getCommerceCartService().getCartForGuidAndSiteAndUser(fromUserCartGuid, currentBaseSite,
				getUserService().getCurrentUser());

		final CartModel toCart = getCommerceCartService().getCartForGuidAndSiteAndUser(toUserCartGuid, currentBaseSite,
				getUserService().getCurrentUser());

		if (fromCart == null && toCart != null)
		{
			return restoreSavedCart(toUserCartGuid);
		}

		if (fromCart != null && toCart == null)
		{
			return restoreSavedCart(fromUserCartGuid);
		}

		if (fromCart == null && toCart == null) // NOSONAR
		{
			return null;
		}

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(toCart);

		final CommerceCartRestoration restoration = getCommerceCartService().restoreCart(parameter);
		parameter.setCart(getCartService().getSessionCart());

		commerceCartService.mergeCarts(fromCart, parameter.getCart(), restoration.getModifications());

		final CommerceCartRestoration commerceCartRestoration = getCommerceCartService().restoreCart(parameter);

		commerceCartRestoration.setModifications(restoration.getModifications());

		getCartService().changeCurrentCartUser(getUserService().getCurrentUser());
		return getCartRestorationConverter().convert(commerceCartRestoration);
	}

	@Override
	public void removeStaleCarts()
	{
		final UserModel currentUser = getUserService().getCurrentUser();

		// DO NOT CLEAN ANONYMOUS USER CARTS
		if (getUserService().isAnonymousUser(currentUser))
		{
			return;
		}

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(getCartService().getSessionCart());
		parameter.setBaseSite(getBaseSiteService().getCurrentBaseSite());
		parameter.setUser(currentUser);
		getCommerceCartService().removeStaleCarts(parameter);
	}

	@Override
	public CartData estimateExternalTaxes(final String deliveryZipCode, final String countryIsoCode)
	{

		final CartModel currentCart = getCartService().getSessionCart();
		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(currentCart);
		parameter.setDeliveryZipCode(deliveryZipCode);
		parameter.setDeliveryCountryIso(countryIsoCode);

		final BigDecimal taxTotal = commerceCartService.estimateTaxes(parameter).getTax();

		final CartData sessionCart = getSessionCart();
		final PriceData taxData = priceDataFactory.create(PriceDataType.BUY, taxTotal, currentCart.getCurrency());
		final PriceData totalPriceData = priceDataFactory.create(PriceDataType.BUY,
				taxTotal.add(sessionCart.getTotalPrice().getValue()), currentCart.getCurrency());

		sessionCart.setTotalTax(taxData);
		sessionCart.setTotalPrice(totalPriceData);
		sessionCart.setNet(false);

		return sessionCart;
	}

	@Override
	public CartData getSessionCartWithEntryOrdering(final boolean recentlyAddedFirst)
	{
		if (hasSessionCart())
		{
			final CartData data = getSessionCart();

			if (recentlyAddedFirst)
			{
				final List<OrderEntryData> recentlyAddedListEntries = new ArrayList<>(data.getEntries());
				Collections.reverse(recentlyAddedListEntries);
				data.setEntries(Collections.unmodifiableList(recentlyAddedListEntries));
				final List<EntryGroupData> recentlyChangedEntryGroups = new ArrayList<>(data.getRootGroups());
				Collections.reverse(recentlyChangedEntryGroups);
				data.setRootGroups(Collections.unmodifiableList(recentlyChangedEntryGroups));
			}

			return data;
		}
		return createEmptyCart();
	}

	/**
	 * @deprecated since 1808. Please use {@link CheckoutFacade#getCountries(CountryType)} instead.
	 */
	@Deprecated
	@Override
	public List<CountryData> getDeliveryCountries()
	{
		final List<CountryData> countries = Converters.convertAll(getDeliveryService().getDeliveryCountriesForOrder(null),
				getCountryConverter());
		Collections.sort(countries, CountryComparator.INSTANCE);
		return countries;
	}

	@Override
	public void removeSessionCart()
	{
		cartService.removeSessionCart();
	}

	@Override
	public List<CartData> getCartsForCurrentUser()
	{
		return Converters.convertAll(
				commerceCartService.getCartsForSiteAndUser(baseSiteService.getCurrentBaseSite(), userService.getCurrentUser()),
				getCartConverter());
	}

	@Override
	public String getMostRecentCartGuidForUser(final Collection<String> excludedCartGuid)
	{
		final List<CartModel> cartModels = commerceCartService.getCartsForSiteAndUser(baseSiteService.getCurrentBaseSite(),
				userService.getCurrentUser());

		if (CollectionUtils.isNotEmpty(cartModels))
		{
			for (final CartModel cartModel : cartModels)
			{
				if (!excludedCartGuid.contains(cartModel.getGuid()) && cartModel.getSaveTime() == null)
				{
					return cartModel.getGuid();
				}
			}
		}
		return null;
	}

	@Override
	public void updateCartMetadata(final CommerceCartMetadata metadata)
	{
		validateParameterNotNullStandardMessage("metadata", metadata);

		final CommerceCartMetadataParameter parameter = CommerceCartMetadataParameterUtils.parameterBuilder()
				.name(metadata.getName()).description(metadata.getDescription()).expirationTime(metadata.getExpirationTime())
				.removeExpirationTime(metadata.isRemoveExpirationTime()).enableHooks(true).cart(getCartService().getSessionCart())
				.build();

		getCommerceCartService().updateCartMetadata(parameter);
	}

	@Override
	public CartModificationData updateCartEntry(final OrderEntryData orderEntry) throws CommerceCartModificationException
	{
		validateParameterNotNullStandardMessage("orderEntry", orderEntry);
		validateParameterNotNullStandardMessage("entryNumber", orderEntry.getEntryNumber());

		CartModificationData cartModificationData = null;

		if (orderEntry.getEntryNumber().intValue() == -1)
		{
			final Integer entryNumberForMultiD = getOrderEntryNumberForMultiD(orderEntry);
			if (entryNumberForMultiD == null)
			{
				cartModificationData = addToCart(orderEntry.getProduct().getCode(), orderEntry.getQuantity().longValue());
			}
			else
			{
				orderEntry.setEntryNumber(entryNumberForMultiD);

				// if deleting all variants at once (i.e. deleting parent)
				boolean isDeleteParent = false;
				if (orderEntry.getQuantity().intValue() == 0)
				{
					final List<ProductOption> extraOptions = Arrays.asList(ProductOption.BASIC);
					final ProductData productData = getProductFacade().getProductForCodeAndOptions(orderEntry.getProduct().getCode(),
							extraOptions);
					isDeleteParent = productData.getBaseProduct() == null;
				}

				if (isDeleteParent)
				{
					cartModificationData = deleteGroupedOrderEntriesMultiD(orderEntry);
				}
				else
				{
					cartModificationData = updateCartEntry(orderEntry.getEntryNumber().longValue(),
							orderEntry.getQuantity().longValue());
				}
			}
		}
		else
		{
			cartModificationData = updateCartEntry(orderEntry.getEntryNumber().longValue(), orderEntry.getQuantity().longValue());
		}

		if (CollectionUtils.isEmpty(orderEntry.getEntries()))
		{
			mergeOrderEntryWithModelConfiguration(orderEntry);
			cartModificationData = configureCartEntry(orderEntry, cartModificationData);
		}

		return cartModificationData;
	}

	@Override
	public CartModificationData removeEntryGroup(@Nonnull final Integer groupNumber) throws CommerceCartModificationException
	{
		validateParameterNotNullStandardMessage("groupNumber", groupNumber);
		final CartModel cartModel = getCartService().getSessionCart();

		final RemoveEntryGroupParameter parameter = new RemoveEntryGroupParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		parameter.setEntryGroupNumber(groupNumber);

		final CommerceCartModification modification = getCommerceCartService().removeEntryGroup(parameter);
		return getCartModificationConverter().convert(modification);
	}

	protected void mergeOrderEntryWithModelConfiguration(final OrderEntryData orderEntry)
	{
		List<ConfigurationInfoData> orderEntryConfiguration = orderEntry.getConfigurationInfos();
		if (orderEntryConfiguration == null)
		{
			orderEntryConfiguration = Collections.emptyList();
		}

		final List<ConfigurationInfoData> modelConfiguration = getProductFacade()
				.getConfiguratorSettingsForCode(orderEntry.getProduct().getCode());
		final List<ConfigurationInfoData> mergedConfiguration = new ArrayList<>(orderEntryConfiguration);

		for (final Map.Entry<ConfiguratorType, ProductConfigurationMergeStrategy> mergeStrategyEntry : getProductConfigurationMergeStrategies()
				.entrySet())
		{
			final ConfiguratorType configuratorType = mergeStrategyEntry.getKey();
			mergedConfiguration.addAll(
					mergeStrategyEntry.getValue().merge(filterConfigurationsByConfigurator(orderEntryConfiguration, configuratorType),
							filterConfigurationsByConfigurator(modelConfiguration, configuratorType)));
		}
		orderEntry.setConfigurationInfos(mergedConfiguration);
	}

	protected List<ConfigurationInfoData> filterConfigurationsByConfigurator(final List<ConfigurationInfoData> configurations,
			final ConfiguratorType configuratorEnum)
	{
		return configurations.stream().filter(item -> (configuratorEnum == null) ? (item.getConfiguratorType() == null)
				: configuratorEnum.equals(item.getConfiguratorType())).collect(Collectors.toList());
	}

	protected CartModificationData configureCartEntry(final OrderEntryData orderEntry,
			final CartModificationData cartModificationData) throws CommerceCartModificationException
	{
		if (CollectionUtils.isEmpty(orderEntry.getConfigurationInfos()))
		{
			return cartModificationData;
		}

		final List<ProductConfigurationItem> productConfigurationItemList = orderEntry.getConfigurationInfos().stream()
				.map(this::configurationInfoToProductConfiguration).collect(Collectors.toList());

		final CommerceCartParameter commerceCartParameter = createCommerceCartParameter(orderEntry, productConfigurationItemList);

		getCommerceCartService().configureCartEntry(commerceCartParameter);

		// The order entry has changed, so it should be reloaded.
		final CartModel cart = getCartService().getSessionCart();
		final AbstractOrderEntryModel entry = getCartService().getEntryForNumber(cart, orderEntry.getEntryNumber());
		getOrderEntryConverter().convert(entry, orderEntry);

		if (cartModificationData != null)
		{
			cartModificationData.setEntry(orderEntry);
		}

		return cartModificationData;
	}

	protected CartModificationData deleteGroupedOrderEntriesMultiD(final OrderEntryData orderEntry)
			throws CommerceCartModificationException
	{
		final List<CartModificationData> modificationDataList = new ArrayList<>();
		for (final OrderEntryData subEntry : orderEntry.getEntries())
		{
			subEntry.setEntryNumber(null);
			subEntry.setQuantity(new Long(0));
			subEntry.setEntryNumber(getOrderEntryNumberForMultiD(subEntry));
			final CartModificationData cartModificationData = updateCartEntry(subEntry);
			modificationDataList.add(cartModificationData);
		}
		final List<CartModificationData> listCartModifications = getGroupCartModificationListConverter().convert(null,
				modificationDataList);
		if (CollectionUtils.isNotEmpty(listCartModifications))
		{
			return listCartModifications.get(0);
		}
		return null;
	}


	protected Integer getOrderEntryNumberForMultiD(final OrderEntryData findEntry)
	{
		if (findEntry.getProduct() != null && findEntry.getProduct().getCode() != null)
		{
			for (final OrderEntryData orderEntry : getSessionCart().getEntries())
			{
				// find the entry
				if (orderEntry.getProduct().getCode().equals(findEntry.getProduct().getCode()))
				{
					if (CollectionUtils.isNotEmpty(orderEntry.getEntries()))
					{
						findEntry.setEntries(orderEntry.getEntries());
					}
					return orderEntry.getEntryNumber();
				}
				// check sub entries
				else if (CollectionUtils.isNotEmpty(orderEntry.getEntries()))
				{
					for (final OrderEntryData subEntry : orderEntry.getEntries())
					{
						// find the entry
						if (subEntry.getProduct().getCode().equals(findEntry.getProduct().getCode()))
						{
							return subEntry.getEntryNumber();
						}
					}
				}
			}
		}
		return null;
	}

	protected ProductConfigurationItem configurationInfoToProductConfiguration(final ConfigurationInfoData configurationInfoData)
	{
		final ProductConfigurationItem productConfigurationItem = new ProductConfigurationItem();
		productConfigurationItem.setKey(configurationInfoData.getConfigurationLabel());
		productConfigurationItem.setValue(configurationInfoData.getConfigurationValue());
		productConfigurationItem.setStatus(configurationInfoData.getStatus());
		productConfigurationItem.setConfiguratorType(configurationInfoData.getConfiguratorType());

		return productConfigurationItem;
	}

	protected CommerceCartParameter createCommerceCartParameter(final OrderEntryData orderEntry,
			final List<ProductConfigurationItem> productConfigurationItemList)
	{
		final AddToCartParams dto = new AddToCartParams();
		dto.setProductCode(orderEntry.getProduct().getCode());
		final CommerceCartParameter commerceCartParameter = getCommerceCartParameterConverter().convert(dto);
		commerceCartParameter.setEntryNumber(orderEntry.getEntryNumber());
		commerceCartParameter.setProductConfiguration(productConfigurationItemList);

		return commerceCartParameter;
	}

	protected boolean hasEntryGroups()
	{
		return hasSessionCart() && CollectionUtils.isNotEmpty(getCartService().getSessionCart().getEntryGroups());
	}

	protected CartService getCartService()
	{
		return cartService;
	}

	@Required
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	protected Converter<CartModel, CartData> getMiniCartConverter()
	{
		return miniCartConverter;
	}

	@Required
	public void setMiniCartConverter(final Converter<CartModel, CartData> miniCartConverter)
	{
		this.miniCartConverter = miniCartConverter;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected CommerceCartService getCommerceCartService()
	{
		return commerceCartService;
	}

	@Required
	public void setCommerceCartService(final CommerceCartService commerceCartService)
	{
		this.commerceCartService = commerceCartService;
	}

	protected Converter<CartModel, CartData> getCartConverter()
	{
		return cartConverter;
	}

	@Required
	public void setCartConverter(final Converter<CartModel, CartData> cartConverter)
	{
		this.cartConverter = cartConverter;
	}

	protected Converter<CommerceCartModification, CartModificationData> getCartModificationConverter()
	{
		return cartModificationConverter;
	}

	@Required
	public void setCartModificationConverter(
			final Converter<CommerceCartModification, CartModificationData> cartModificationConverter)
	{
		this.cartModificationConverter = cartModificationConverter;
	}

	protected Converter<CommerceCartRestoration, CartRestorationData> getCartRestorationConverter()
	{
		return cartRestorationConverter;
	}

	@Required
	public void setCartRestorationConverter(final Converter<CommerceCartRestoration, CartRestorationData> cartRestorationConverter)
	{
		this.cartRestorationConverter = cartRestorationConverter;
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

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	@Required
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}

	protected PointOfServiceService getPointOfServiceService()
	{
		return pointOfServiceService;
	}

	@Required
	public void setPointOfServiceService(final PointOfServiceService pointOfServiceService)
	{
		this.pointOfServiceService = pointOfServiceService;
	}

	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	public DeliveryService getDeliveryService()
	{
		return deliveryService;
	}

	public void setDeliveryService(final DeliveryService deliveryService)
	{
		this.deliveryService = deliveryService;
	}

	public Converter<CountryModel, CountryData> getCountryConverter()
	{
		return countryConverter;
	}

	public void setCountryConverter(final Converter<CountryModel, CountryData> countryConverter)
	{
		this.countryConverter = countryConverter;
	}

	public Converter<AbstractOrderModel, List<CartModificationData>> getGroupCartModificationListConverter()
	{
		return groupCartModificationListConverter;
	}

	protected Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return orderEntryConverter;
	}

	@Required
	public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter)
	{
		this.orderEntryConverter = orderEntryConverter;
	}

	@Required
	public void setGroupCartModificationListConverter(
			final Converter<AbstractOrderModel, List<CartModificationData>> groupCartModificationListConverter)
	{
		this.groupCartModificationListConverter = groupCartModificationListConverter;
	}

	protected Map<ConfiguratorType, ProductConfigurationMergeStrategy> getProductConfigurationMergeStrategies()
	{
		return productConfigurationMergeStrategies;
	}

	@Required
	public void setProductConfigurationMergeStrategies(
			final Map<ConfiguratorType, ProductConfigurationMergeStrategy> productConfigurationMergeStrategies)
	{
		this.productConfigurationMergeStrategies = productConfigurationMergeStrategies;
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

	protected Converter<AddToCartParams, CommerceCartParameter> getCommerceCartParameterConverter()
	{
		return commerceCartParameterConverter;
	}

	@Required
	public void setCommerceCartParameterConverter(
			final Converter<AddToCartParams, CommerceCartParameter> commerceCartParameterConverter)
	{
		this.commerceCartParameterConverter = commerceCartParameterConverter;
	}
}
