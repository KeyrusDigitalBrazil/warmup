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
package de.hybris.platform.commercefacades.order.converters.populator;

import de.hybris.platform.comments.model.CommentModel;
import de.hybris.platform.commercefacades.comment.data.CommentData;
import de.hybris.platform.commercefacades.order.CommerceEntryGroupUtils;
import de.hybris.platform.commercefacades.order.EntryGroupData;
import de.hybris.platform.commercefacades.order.VirtualEntryGroupStrategy;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.DiscountType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.order.EntryGroup;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.order.EntryGroupService;
import de.hybris.platform.promotions.PromotionsService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.DiscountValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import com.google.common.math.DoubleMath;


/**
 * Abstract class for order converters. Conversion methods should be implemented in inheriting class.
 *
 */
public abstract class AbstractOrderPopulator<SOURCE extends AbstractOrderModel, TARGET extends AbstractOrderData> implements
		Populator<SOURCE, TARGET>
{
	private static final double EPSILON = 0.01d;

	private ModelService modelService;
	private PromotionsService promotionsService;
	private PriceDataFactory priceDataFactory;
	private CommonI18NService commonI18NService;
	private TypeService typeService;

	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	private Converter<AddressModel, AddressData> addressConverter;
	private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	private Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter;
	private Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter;
	private Converter<PromotionResultModel, PromotionResultData> promotionResultConverter;
	private Converter<PrincipalModel, PrincipalData> principalConverter;
	private Converter<CommentModel, CommentData> orderCommentConverter;
	private Converter<EntryGroup, EntryGroupData> entryGroupConverter;
	private EntryGroupService entryGroupService;
	private CommerceEntryGroupUtils commerceEntryGroupUtils;
	private VirtualEntryGroupStrategy virtualEntryGroupStrategy;

	private final Map<String, PriceData> priceData = new HashMap<String, PriceData>();

	protected Map<String, PriceData> getPriceData()
	{
		return priceData;
	}


	protected TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}


	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected Converter<AbstractOrderEntryModel, OrderEntryData> getOrderEntryConverter()
	{
		return this.orderEntryConverter;
	}

	@Required
	public void setOrderEntryConverter(final Converter<AbstractOrderEntryModel, OrderEntryData> converter)
	{
		this.orderEntryConverter = converter;
	}

	protected PromotionsService getPromotionsService()
	{
		return promotionsService;
	}

	@Required
	public void setPromotionsService(final PromotionsService promotionsService)
	{
		this.promotionsService = promotionsService;
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


	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	@Required
	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> getCreditCardPaymentInfoConverter()
	{
		return creditCardPaymentInfoConverter;
	}

	@Required
	public void setCreditCardPaymentInfoConverter(
			final Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter)
	{
		this.creditCardPaymentInfoConverter = creditCardPaymentInfoConverter;
	}

	protected Converter<DeliveryModeModel, DeliveryModeData> getDeliveryModeConverter()
	{
		return deliveryModeConverter;
	}

	@Required
	public void setDeliveryModeConverter(final Converter<DeliveryModeModel, DeliveryModeData> deliveryModeConverter)
	{
		this.deliveryModeConverter = deliveryModeConverter;
	}

	protected Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> getZoneDeliveryModeConverter()
	{
		return zoneDeliveryModeConverter;
	}

	@Required
	public void setZoneDeliveryModeConverter(final Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter)
	{
		this.zoneDeliveryModeConverter = zoneDeliveryModeConverter;
	}

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	@Required
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}

	protected Converter<PromotionResultModel, PromotionResultData> getPromotionResultConverter()
	{
		return promotionResultConverter;
	}

	@Required
	public void setPromotionResultConverter(final Converter<PromotionResultModel, PromotionResultData> promotionResultConverter)
	{
		this.promotionResultConverter = promotionResultConverter;
	}

	protected Converter<PrincipalModel, PrincipalData> getPrincipalConverter()
	{
		return principalConverter;
	}

	@Required
	public void setPrincipalConverter(final Converter<PrincipalModel, PrincipalData> principalConverter)
	{
		this.principalConverter = principalConverter;
	}

	protected Converter<CommentModel, CommentData> getOrderCommentConverter()
	{
		return orderCommentConverter;
	}

	@Required
	public void setOrderCommentConverter(final Converter<CommentModel, CommentData> orderCommentConverter)
	{
		this.orderCommentConverter = orderCommentConverter;
	}

	protected Converter<EntryGroup, EntryGroupData> getEntryGroupConverter()
	{
		return entryGroupConverter;
	}

	@Required
	public void setEntryGroupConverter(final Converter<EntryGroup, EntryGroupData> entryGroupConverter)
	{
		this.entryGroupConverter = entryGroupConverter;
	}

	protected EntryGroupService getEntryGroupService()
	{
		return entryGroupService;
	}

	@Required
	public void setEntryGroupService(final EntryGroupService entryGroupService)
	{
		this.entryGroupService = entryGroupService;
	}

	protected CommerceEntryGroupUtils getCommerceEntryGroupUtils()
	{
		return commerceEntryGroupUtils;
	}

	@Required
	public void setCommerceEntryGroupUtils(final CommerceEntryGroupUtils commerceEntryGroupUtils)
	{
		this.commerceEntryGroupUtils = commerceEntryGroupUtils;
	}

	protected VirtualEntryGroupStrategy getVirtualEntryGroupStrategy()
	{
		return virtualEntryGroupStrategy;
	}

	@Required
	public void setVirtualEntryGroupStrategy(final VirtualEntryGroupStrategy virtualEntryGroupStrategy)
	{
		this.virtualEntryGroupStrategy = virtualEntryGroupStrategy;
	}

	protected void addCommon(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		prototype.setCode(source.getCode());
		prototype.setName(source.getName());
		prototype.setDescription(source.getDescription());
		prototype.setExpirationTime(source.getExpirationTime());
		if (source.getSite() != null)
		{
			prototype.setSite(source.getSite().getUid());
		}
		if (source.getStore() != null)
		{
			prototype.setStore(source.getStore().getUid());
		}
		prototype.setTotalItems(calcTotalItems(source));
		prototype.setNet(Boolean.TRUE.equals(source.getNet()));
		prototype.setGuid(source.getGuid());
		prototype.setCalculated(Boolean.TRUE.equals(source.getCalculated()));
		prototype.setTotalUnitCount(calcTotalUnitCount(source));
	}

	protected Integer calcTotalItems(final AbstractOrderModel source)
	{
		return Integer.valueOf(source.getEntries().size());
	}

	protected void addEntries(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		prototype.setEntries(getOrderEntryConverter().convertAll(source.getEntries()));
	}

	protected void addComments(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		prototype.setComments(getOrderCommentConverter().convertAll(source.getComments()));
	}

	protected void addDeliveryAddress(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		if (source.getDeliveryAddress() != null)
		{
			prototype.setDeliveryAddress(getAddressConverter().convert(source.getDeliveryAddress()));
		}
	}

	/**
	 * Fills references to other groups of each converted {@code EntryGroupData}.
	 * <p>
	 * {@code EntryGroup} stores only child items, and {@link EntryGroupPopulator} does not have information of the whole
	 * group list, so can not assign parents.
	 * </p>
	 *
	 * @param source
	 *           order model that has entry groups
	 * @param target
	 *           entry group DTOs constructed from {@code source} (map groupNUmber to group)
	 */
	protected void updateEntryGroupReferences(@Nonnull final Collection<EntryGroup> source,
			@Nonnull final Map<Integer, EntryGroupData> target)
	{
		// assign children and parents
		source.forEach(model -> {
			final EntryGroupData dto = getGroup(model.getGroupNumber(), target);
			if (model.getChildren() == null)
			{
				dto.setChildren(Collections.emptyList());
			}
			else
			{
				dto.setChildren(model.getChildren().stream()
						.map(EntryGroup::getGroupNumber)
						.map(idx -> getGroup(idx, target))
						.peek(child -> child.setParent(dto))
						.collect(Collectors.toList()));
			}
		});
	}

	protected void assignParentGroups(@Nonnull final Collection<EntryGroupData> groups)
	{
		groups.stream()
				.filter(item -> item.getParent() == null)
				.forEach(item -> {
			item.setRootGroup(item);
			if (item.getChildren() != null)
			{
				final List<EntryGroupData> subtree = new ArrayList<>(item.getChildren());
				for (int i = 0; i < subtree.size(); i++)
				{
					final EntryGroupData child = subtree.get(i);
					child.setRootGroup(item);
					child.setParent(item);
					if (child.getChildren() != null)
					{
						subtree.addAll(child.getChildren());
					}
				}
			}
		});
	}

	protected EntryGroupData getGroup(final Integer number, final Map<Integer, EntryGroupData> map)
	{
		ServicesUtil.validateParameterNotNullStandardMessage("number", number);
		final EntryGroupData dto = map.get(number);
		if (dto == null)
		{
			throw new IllegalArgumentException("EntryGroupData #" + number + " does not exist");
		}
		return dto;
	}

	protected void addDeliveryMethod(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		final DeliveryModeModel deliveryMode = source.getDeliveryMode();
		if (deliveryMode != null)
		{
			DeliveryModeData deliveryModeData;
			if (deliveryMode instanceof ZoneDeliveryModeModel)
			{
				deliveryModeData = getZoneDeliveryModeConverter().convert((ZoneDeliveryModeModel) deliveryMode);
			}
			else
			{
				deliveryModeData = getDeliveryModeConverter().convert(deliveryMode);
			}

			if (source.getDeliveryCost() != null)
			{
				deliveryModeData.setDeliveryCost(getPriceDataFactory().create(PriceDataType.BUY,
						BigDecimal.valueOf(source.getDeliveryCost().doubleValue()), source.getCurrency().getIsocode()));
			}
			prototype.setDeliveryMode(deliveryModeData);
		}
	}

	protected void addPaymentInformation(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		final PaymentInfoModel paymentInfo = source.getPaymentInfo();
		if (paymentInfo instanceof CreditCardPaymentInfoModel)
		{
			final CCPaymentInfoData paymentInfoData = getCreditCardPaymentInfoConverter().convert(
					(CreditCardPaymentInfoModel) paymentInfo);
			prototype.setPaymentInfo(paymentInfoData);
		}
	}


	/*
	 * Adds applied and potential promotions.
	 */
	protected void addPromotions(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		addPromotions(source, getPromotionsService().getPromotionResults(source), prototype);
	}

	protected void addPromotions(final AbstractOrderModel source, final PromotionOrderResults promoOrderResults,
			final AbstractOrderData prototype)
	{
		final double quoteDiscountsAmount = getQuoteDiscountsAmount(source);
		prototype.setQuoteDiscounts(createPrice(source, Double.valueOf(quoteDiscountsAmount)));

		final Pair<DiscountType, Double> quoteDiscountsTypeAndRate = getQuoteDiscountsTypeAndRate(source);
		prototype.setQuoteDiscountsType(quoteDiscountsTypeAndRate.getKey().getCode());
		prototype.setQuoteDiscountsRate(quoteDiscountsTypeAndRate.getValue());

		if (promoOrderResults != null)
		{
			final double productsDiscountsAmount = getProductsDiscountsAmount(source);
			final double orderDiscountsAmount = getOrderDiscountsAmount(source);

			prototype.setProductDiscounts(createPrice(source, Double.valueOf(productsDiscountsAmount)));
			prototype.setOrderDiscounts(createPrice(source, Double.valueOf(orderDiscountsAmount)));
			prototype.setTotalDiscounts(createPrice(source, Double.valueOf(productsDiscountsAmount + orderDiscountsAmount)));
			prototype.setTotalDiscountsWithQuoteDiscounts(createPrice(source,
					Double.valueOf(productsDiscountsAmount + orderDiscountsAmount + quoteDiscountsAmount)));
			prototype.setAppliedOrderPromotions(getPromotions(promoOrderResults.getAppliedOrderPromotions()));
			prototype.setAppliedProductPromotions(getPromotions(promoOrderResults.getAppliedProductPromotions()));
		}
	}

	protected double getProductsDiscountsAmount(final AbstractOrderModel source)
	{
		double discounts = 0.0d;

		final List<AbstractOrderEntryModel> entries = source.getEntries();
		if (entries != null)
		{
			for (final AbstractOrderEntryModel entry : entries)
			{
				final List<DiscountValue> discountValues = entry.getDiscountValues();
				if (discountValues != null)
				{
					for (final DiscountValue dValue : discountValues)
					{
						discounts += dValue.getAppliedValue();
					}
				}
			}
		}
		return discounts;
	}

	protected double getOrderDiscountsAmount(final AbstractOrderModel source)
	{
		double discounts = 0.0d;
		final List<DiscountValue> discountList = source.getGlobalDiscountValues(); // discounts on the cart itself
		if (discountList != null && !discountList.isEmpty())
		{
			for (final DiscountValue discount : discountList)
			{
				final double value = discount.getAppliedValue();
				if (DoubleMath.fuzzyCompare(value, 0, EPSILON) > 0
						&& !CommerceServicesConstants.QUOTE_DISCOUNT_CODE.equals(discount.getCode()))
				{
					discounts += value;
				}
			}
		}
		return discounts;
	}

	protected double getQuoteDiscountsAmount(final AbstractOrderModel source)
	{
		double discounts = 0.0d;
		final List<DiscountValue> discountList = source.getGlobalDiscountValues(); // discounts on the cart itself
		if (discountList != null && !discountList.isEmpty())
		{
			for (final DiscountValue discount : discountList)
			{
				final double value = discount.getAppliedValue();
				if (DoubleMath.fuzzyCompare(value, 0, EPSILON) > 0
						&& CommerceServicesConstants.QUOTE_DISCOUNT_CODE.equals(discount.getCode()))
				{
					discounts += value;
				}
			}
		}
		return discounts;
	}

	protected Pair<DiscountType, Double> getQuoteDiscountsTypeAndRate(final AbstractOrderModel source)
	{
		double discounts = 0.0d;
		DiscountType discountType = DiscountType.PERCENT;
		final List<DiscountValue> discountList = source.getGlobalDiscountValues(); // discounts on the cart itself
		if (discountList != null && !discountList.isEmpty())
		{
			for (final DiscountValue discount : discountList)
			{
				final double value = discount.getAppliedValue();
				if (DoubleMath.fuzzyCompare(value, 0, EPSILON) > 0
						&& CommerceServicesConstants.QUOTE_DISCOUNT_CODE.equals(discount.getCode()))
				{
					// for now there is only one quote discount entry
					discounts = discount.getValue();
					if (discount.isAsTargetPrice())
					{
						discountType = DiscountType.TARGET;
					}
					else if (discount.isAbsolute())
					{
						discountType = DiscountType.ABSOLUTE;
					}
					break;
				}
			}
		}
		return Pair.of(discountType, Double.valueOf(discounts));
	}

	/*
	 * Extracts (and converts to POJOs) promotions from given results.
	 */
	protected List<PromotionResultData> getPromotions(final List<PromotionResult> promotionsResults)
	{
		final ArrayList<PromotionResultModel> promotionResultModels = getModelService().getAll(promotionsResults,
				new ArrayList<PromotionResultModel>());
		return getPromotionResultConverter().convertAll(promotionResultModels);
	}

	protected PriceData createPrice(final AbstractOrderModel source, final Double val)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("source order must not be null");
		}

		final CurrencyModel currency = source.getCurrency();
		if (currency == null)
		{
			throw new IllegalArgumentException("source order currency must not be null");
		}

		// Get double value, handle null as zero
		final double priceValue = val != null ? val.doubleValue() : 0d;

		return getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(priceValue), currency);
	}

	protected void addTotals(final AbstractOrderModel source, final AbstractOrderData prototype)
	{
		final double orderDiscountsAmount = getOrderDiscountsAmount(source);
		final double quoteDiscountsAmount = getQuoteDiscountsAmount(source);

		prototype.setTotalPrice(createPrice(source, source.getTotalPrice()));
		prototype.setTotalTax(createPrice(source, source.getTotalTax()));
		final double subTotal = source.getSubtotal().doubleValue() - orderDiscountsAmount - quoteDiscountsAmount;
		final PriceData subTotalPriceData = createPrice(source, Double.valueOf(subTotal));
		prototype.setSubTotal(subTotalPriceData);
		prototype.setSubTotalWithoutQuoteDiscounts(createPrice(source, Double.valueOf(subTotal + quoteDiscountsAmount)));
		prototype.setDeliveryCost(source.getDeliveryMode() != null ? createPrice(source, source.getDeliveryCost()) : null);
		prototype.setTotalPriceWithTax((createPrice(source, calcTotalWithTax(source))));
	}

	protected PriceData createZeroPrice()
	{
		final String key = getCommonI18NService().getCurrentCurrency().getIsocode();
		if (getPriceData().containsKey(key))
		{
			return getPriceData().get(key);
		}
		else
		{
			final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.ZERO,
					getCommonI18NService().getCurrentCurrency());
			getPriceData().put(key, priceData);
			return priceData;
		}
	}

	protected Double calcTotalWithTax(final AbstractOrderModel source)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("source order must not be null");
		}
		if (source.getTotalPrice() == null)
		{
			return 0.0d;
		}

		BigDecimal totalPrice = BigDecimal.valueOf(source.getTotalPrice().doubleValue());

		// Add the taxes to the total price if the cart is net; if the total was null taxes should be null as well
		if (Boolean.TRUE.equals(source.getNet()) && totalPrice.compareTo(BigDecimal.ZERO) != 0 && source.getTotalTax() != null)
		{
			totalPrice = totalPrice.add(BigDecimal.valueOf(source.getTotalTax().doubleValue()));
		}

		return totalPrice.doubleValue();
	}

	protected Integer calcTotalUnitCount(final AbstractOrderModel source)
	{
		int totalUnitCount = 0;
		for (final AbstractOrderEntryModel orderEntryModel : source.getEntries())
		{
			totalUnitCount += orderEntryModel.getQuantity().intValue();
		}
		return Integer.valueOf(totalUnitCount);
	}

	protected void checkForGuestCustomer(final OrderModel source, final OrderData target)
	{
		if (CustomerType.GUEST.equals(((CustomerModel) source.getUser()).getType()))
		{
			target.setGuestCustomer(true);
		}
	}

	protected void addDeliveryStatus(final OrderModel source, final OrderData target)
	{
		target.setDeliveryStatus(source.getDeliveryStatus());
		if (source.getDeliveryStatus() != null)
		{
			target.setDeliveryStatusDisplay(getTypeService().getEnumerationValue(source.getDeliveryStatus()).getName());
		}
	}

	protected void addPrincipalInformation(final AbstractOrderModel source, final AbstractOrderData target)
	{
		target.setUser(getPrincipalConverter().convert(source.getUser()));
	}

	protected void addEntryGroups(final AbstractOrderModel source, final AbstractOrderData target)
	{
		if (target.getEntries() == null)
		{
			target.setEntries(Collections.emptyList());
		}
		final List<EntryGroupData> rootGroups;
		if (source.getEntryGroups() == null)
		{
			rootGroups = Collections.emptyList();
		}
		else
		{
			final List<List<EntryGroupData>> groupTrees = source.getEntryGroups().stream()
					.map(getEntryGroupService()::getNestedGroups)
					.map(getEntryGroupConverter()::convertAll)
					.collect(Collectors.toList());
			rootGroups = groupTrees.stream()
					.map(tree -> tree.get(0))
					.collect(Collectors.toList());
			final List<EntryGroup> sourceGroups = source.getEntryGroups().stream()
					.map(getEntryGroupService()::getNestedGroups)
					.flatMap(Collection::stream)
					.collect(Collectors.toList());
			final Map<Integer, EntryGroupData> targetGroups = groupTrees.stream()
					.flatMap(Collection::stream)
					.collect(Collectors.toMap(x -> x.getGroupNumber(), x -> x));
			updateEntryGroupReferences(sourceGroups, targetGroups);
			assignParentGroups(targetGroups.values());
		}

		final MultivaluedMap<Integer, OrderEntryData> groupIdToEntryDataMap = mapGroupIdToEntryData(source, target);
		rootGroups.forEach(rootGroup -> assignEntriesToGroups(rootGroup, groupIdToEntryDataMap));
		final List<OrderEntryData> standaloneEntries = groupIdToEntryDataMap.get(null);
		if (CollectionUtils.isEmpty(standaloneEntries))
		{
			target.setRootGroups(rootGroups);
		}
		else
		{
			// Wrap root groups to make the array extensible.
			final List<EntryGroupData> groups = new ArrayList<>(rootGroups);
			standaloneEntries.forEach(entry -> getVirtualEntryGroupStrategy().createGroup(groups, entry));
			groups.forEach(group -> group.setRootGroup(group));
			target.setRootGroups(groups);
		}
		sortEntryGroups(target);
	}

	/**
	 * Sort root entry groups within order.
	 *
	 * @param order
	 */
	protected void sortEntryGroups(final AbstractOrderData order)
	{
		final List<EntryGroupData> sortedRoots = new ArrayList<>();
		final List<OrderEntryData> entries = new ArrayList<>(order.getEntries());
		Collections.reverse(entries);
		entries.stream()
				.filter(entry -> entry.getEntryGroupNumbers() != null)
				.flatMap(entry -> entry.getEntryGroupNumbers().stream())
				.filter(num -> num != null && num.intValue() > 0)
				.map(number -> getCommerceEntryGroupUtils().getGroup(order, number))
				.map(EntryGroupData::getRootGroup)
				.distinct()
				.forEach(root -> sortedRoots.add(root));
		order.getRootGroups().stream()
				.filter(root -> !sortedRoots.contains(root))
				.forEach(sortedRoots::add);
		order.setRootGroups(sortedRoots);
	}

	protected MultivaluedMap<Integer, OrderEntryData> mapGroupIdToEntryData(final AbstractOrderModel source,
			final AbstractOrderData target)
	{
		final MultivaluedMap<Integer, OrderEntryData> groupIdToEntryDataMap = new MultivaluedHashMap<>();
		if (CollectionUtils.isNotEmpty(source.getEntries()))
		{
			source.getEntries().forEach(entryModel -> {
				final OrderEntryData dto = target.getEntries().stream()
							.filter(entryData -> Objects.equals(entryData.getEntryNumber(), entryModel.getEntryNumber()))
							.findAny().orElseThrow(() -> new IllegalArgumentException(
								"Order entry model " + entryModel.getEntryNumber() + " has no corresponding entry data"));
				if (CollectionUtils.isEmpty(entryModel.getEntryGroupNumbers()))
				{
					groupIdToEntryDataMap.add(null, dto);
				}
				else
				{
					entryModel.getEntryGroupNumbers().forEach(entryGroupNumber -> groupIdToEntryDataMap.add(entryGroupNumber, dto));
				}
			});
		}
		return groupIdToEntryDataMap;
	}

	protected void assignEntriesToGroups(final EntryGroupData entryGroup,
			final MultivaluedMap<Integer, OrderEntryData> orderEntryDataMap)
	{
		entryGroup.setOrderEntries(Collections.emptyList());
		final Integer entryGroupNumber = entryGroup.getGroupNumber();
		final List<OrderEntryData> entryDataWithGroup = orderEntryDataMap.get(entryGroupNumber);
		entryGroup.setOrderEntries(entryDataWithGroup == null ? Collections.emptyList() : entryDataWithGroup);
		if (CollectionUtils.isNotEmpty(entryGroup.getChildren()))
		{
			entryGroup.getChildren().stream()
					.forEach(child -> assignEntriesToGroups(child, orderEntryDataMap));
		}
	}
}
