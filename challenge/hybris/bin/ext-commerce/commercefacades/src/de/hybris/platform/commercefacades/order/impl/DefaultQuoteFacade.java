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

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.quote.data.DiscountTypeData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.commercefacades.util.CommerceUtils;
import de.hybris.platform.commerceservices.comments.CommerceCommentService;
import de.hybris.platform.commerceservices.enums.DiscountType;
import de.hybris.platform.commerceservices.enums.QuoteAction;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.order.CommerceQuoteService;
import de.hybris.platform.commerceservices.order.strategies.QuoteUserIdentificationStrategy;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commerceservices.util.CommerceCommentUtils;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.QuoteService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of {@link QuoteFacade}.
 */
public class DefaultQuoteFacade implements QuoteFacade
{
	private CartService cartService;
	private CommerceCartService commerceCartService;
	private CommerceQuoteService commerceQuoteService;
	private QuoteService quoteService;
	private ModelService modelService;
	private Converter<QuoteModel, QuoteData> quoteConverter;
	private Converter<CartModel, CartData> cartConverter;
	private UserService userService;
	private BaseStoreService baseStoreService;
	private EnumerationService enumerationService;
	private TypeService typeService;
	private CommerceCommentService commerceCommentService;
	private QuoteUserIdentificationStrategy quoteUserIdentificationStrategy;

	@Override
	public QuoteData initiateQuote()
	{
		final CartModel cartModel = getCartService().getSessionCart();
		final QuoteModel quoteModel = getCommerceQuoteService().createQuoteFromCart(cartModel,
				getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
		getModelService().save(quoteModel);
		getCartService().removeSessionCart();
		return getQuoteConverter().convert(quoteModel);
	}

	@Override
	public CartData createCartFromQuote(final String quoteCode)
	{
		final QuoteModel quoteModel = getQuoteModelForCode(quoteCode);
		final CartModel cartModel = getCartService().createCartFromQuote(quoteModel);

		getModelService().saveAll(cartModel, quoteModel);

		return cartConverter.convert(cartModel);
	}

	@Override
	public void enableQuoteEdit(final String quoteCode)
	{
		final QuoteModel quoteModel = getQuoteModelForCode(quoteCode);
		final UserModel currentQuoteUser = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();

		getCommerceQuoteService().assignQuoteToUser(quoteModel, currentQuoteUser, currentQuoteUser);

		final CartModel cartModel = getCommerceQuoteService().loadQuoteAsSessionCart(quoteModel, currentQuoteUser);
		getModelService().saveAll(cartModel, quoteModel);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		cartModel.setCalculated(Boolean.FALSE);
		parameter.setEnableHooks(true);
		parameter.setCart(cartModel);
		getCommerceCartService().calculateCart(parameter);
		getModelService().refresh(cartModel);
		getCartService().setSessionCart(cartModel);
	}

	@Override
	public QuoteData newCart()
	{
		final QuoteModel syncedQuote = getCommerceQuoteService().updateQuoteFromCart(getCartService().getSessionCart(),
				getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
		return getQuoteConverter().convert(syncedQuote);
	}

	@Override
	public void submitQuote(final String quoteCode)
	{
		validateParameterNotNullStandardMessage("quoteCode", quoteCode);
		final QuoteModel quoteModel = getQuoteModelForCode(quoteCode);
		final UserModel userModel = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();
		final CartModel sessionCart = getCartService().getSessionCart();

		getCommerceQuoteService().validateQuoteThreshold(quoteModel, userModel, sessionCart);
		getCommerceQuoteService().unassignQuote(quoteModel, userModel);
		getCommerceQuoteService().submitQuote(quoteModel, userModel);
	}

	@Override
	public double getQuoteRequestThreshold(final String quoteCode)
	{
		validateParameterNotNullStandardMessage("quoteCode", quoteCode);
		final QuoteModel quote = getQuoteModelForCode(quoteCode);
		final UserModel user = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();
		final CartModel sessionCart = getCartService().getSessionCart();

		return getCommerceQuoteService().getQuoteRequestThreshold(quote, user, sessionCart);
	}

	@Override
	public void approveQuote(final String quoteCode)
	{
		validateParameterNotNullStandardMessage("quoteCode", quoteCode);
		final QuoteModel quoteModel = getQuoteModelForCode(quoteCode);
		getCommerceQuoteService().approveQuote(quoteModel, getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
	}

	@Override
	public void rejectQuote(final String quoteCode)
	{
		validateParameterNotNullStandardMessage("quoteCode", quoteCode);
		final QuoteModel quoteModel = getQuoteModelForCode(quoteCode);
		getCommerceQuoteService().rejectQuote(quoteModel, getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
	}

	@Override
	public void acceptAndPrepareCheckout(final String quoteCode)
	{
		final QuoteModel quoteModel = getQuoteModelForCode(quoteCode);
		final CartModel checkoutCart = getCommerceQuoteService().acceptAndPrepareCheckout(quoteModel,
				getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
		getModelService().saveAll(checkoutCart, quoteModel);

		final CommerceCartParameter parameter = new CommerceCartParameter();
		parameter.setEnableHooks(true);
		parameter.setCart(checkoutCart);
		getCommerceCartService().calculateCart(parameter);
		getModelService().refresh(checkoutCart);
		getCartService().setSessionCart(checkoutCart);
	}

	@Override
	public SearchPageData<QuoteData> getPagedQuotes(final PageableData pageableData)
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		final SearchPageData<QuoteModel> quoteModelSearchPageData = getCommerceQuoteService().getQuoteList(currentCustomer,
				getQuoteUserIdentificationStrategy().getCurrentQuoteUser(), currentBaseStore, pageableData);
		return CommerceUtils.convertPageData(quoteModelSearchPageData, getQuoteConverter());
	}

	protected QuoteModel getQuoteModelForCode(final String quoteCode)
	{
		final CustomerModel currentUser = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		return getCommerceQuoteService().getQuoteByCodeAndCustomerAndStore(currentUser,
				getQuoteUserIdentificationStrategy().getCurrentQuoteUser(), currentBaseStore, quoteCode);
	}

	@Override
	public QuoteData getQuoteForCode(final String quoteCode)
	{
		validateParameterNotNullStandardMessage("quoteCode", quoteCode);
		final QuoteModel quoteModel = getQuoteModelForCode(quoteCode);
		return getQuoteConverter().convert(quoteModel);
	}

	@Override
	public void addComment(final String text)
	{
		final CartModel sessionCart = getCartService().getSessionCart();

		validateQuoteCart(sessionCart);

		if (StringUtils.isBlank(text))
		{
			throw new IllegalArgumentException("Parameter text cannot be blank");
		}

		getCommerceCommentService().addComment(
				CommerceCommentUtils.buildQuoteCommentParameter(sessionCart, getQuoteUserIdentificationStrategy()
						.getCurrentQuoteUser(), text));
	}

	@Override
	public void addEntryComment(final long entryNumber, final String text)
	{
		final CartModel sessionCart = getCartService().getSessionCart();

		validateQuoteCart(sessionCart);

		if (StringUtils.isBlank(text))
		{
			throw new IllegalArgumentException("Parameter text cannot be blank");
		}

		if (StringUtils.length(text) > 255)
		{
			throw new IllegalArgumentException("Parameter text cannot exceed length of 255");
		}

		getCommerceCommentService().addComment(
				CommerceCommentUtils.buildQuoteEntryCommentParameter(getEntryForEntryNumber(sessionCart, (int) entryNumber),
						getQuoteUserIdentificationStrategy().getCurrentQuoteUser(), text));
	}

	@Override
	public Set<QuoteAction> getAllowedActions(final String quoteCode)
	{
		final QuoteModel quoteModel = getQuoteModelForCode(quoteCode);
		return getCommerceQuoteService().getAllowedActions(quoteModel, getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
	}

	@Override
	public void applyQuoteDiscount(final Double discountRate, final String discountTypeCode)
	{
		validateParameterNotNull(discountRate, "DiscountRate cannot be null");
		validateParameterNotNull(discountTypeCode, "DiscountTypeCode cannot be null");

		getCommerceQuoteService().applyQuoteDiscount(getCartService().getSessionCart(),
				getQuoteUserIdentificationStrategy().getCurrentQuoteUser(), discountRate, DiscountType.valueOf(discountTypeCode));
	}

	/**
	 * @deprecated Since 6.4.
	 */
	@Deprecated
	@Override
	public List<DiscountTypeData> getDiscountTypes()
	{
		final List<DiscountTypeData> discountTypeDataList = new ArrayList<>();
		final List<DiscountType> discountTypes = getEnumerationService().getEnumerationValues(DiscountType.class);
		for (final DiscountType discountTypeEnum : discountTypes)
		{
			final DiscountTypeData discountTypeData = new DiscountTypeData();
			discountTypeData.setCode(discountTypeEnum.getCode());
			discountTypeData.setName(getTypeService().getEnumerationValue(discountTypeEnum).getName());
			discountTypeDataList.add(discountTypeData);
		}
		return discountTypeDataList;
	}

	@Override
	public void cancelQuote(final String quoteCode)
	{
		validateParameterNotNullStandardMessage("quoteCode", quoteCode);

		final QuoteModel quoteModel = getQuoteModelForCode(quoteCode);
		final UserModel userModel = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();

		getCommerceQuoteService().unassignQuote(quoteModel, userModel);

		getCommerceQuoteService().cancelQuote(quoteModel, getQuoteUserIdentificationStrategy().getCurrentQuoteUser());
	}

	@Override
	public Integer getQuotesCountForCurrentUser()
	{
		final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
		final BaseStoreModel currentBaseStore = getBaseStoreService().getCurrentBaseStore();
		return getCommerceQuoteService().getQuotesCountForStoreAndUser(currentCustomer,
				getQuoteUserIdentificationStrategy().getCurrentQuoteUser(), currentBaseStore);

	}

	@Override
	public boolean isQuoteSessionCartValidForCheckout()
	{
		return getCommerceQuoteService().isQuoteCartValidForCheckout(getCartService().getSessionCart());
	}

	@Override
	public void removeQuoteCart(final String quoteCode)
	{
		getCommerceQuoteService().removeQuoteCart(getQuoteModelForCode(quoteCode));
	}

	@Override
	public QuoteData requote(final String quoteCode)
	{
		final QuoteModel quoteModel = getQuoteModelForCode(quoteCode);
		final UserModel userModel = getQuoteUserIdentificationStrategy().getCurrentQuoteUser();

		final QuoteModel newQuoteModel = getCommerceQuoteService().requote(quoteModel, userModel);
		return getQuoteConverter().convert(newQuoteModel);
	}

	protected void validateQuoteCart(final CartModel cartModel)
	{
		validateParameterNotNullStandardMessage("cartModel", cartModel);
		if (cartModel.getQuoteReference() == null)
		{
			throw new IllegalArgumentException("Unable to update quote since the session cart is not a quote cart");
		}
	}

	protected AbstractOrderEntryModel getEntryForEntryNumber(final AbstractOrderModel order, final int number)
	{
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		if (entries != null && !entries.isEmpty())
		{
			final Integer requestedEntryNumber = Integer.valueOf(number);
			for (final AbstractOrderEntryModel entry : entries)
			{
				if (entry != null && requestedEntryNumber.equals(entry.getEntryNumber()))
				{
					return entry;
				}
			}
		}
		return null;
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

	protected CommerceQuoteService getCommerceQuoteService()
	{
		return commerceQuoteService;
	}

	@Required
	public void setCommerceQuoteService(final CommerceQuoteService commerceQuoteService)
	{
		this.commerceQuoteService = commerceQuoteService;
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

	protected QuoteService getQuoteService()
	{
		return quoteService;
	}

	@Required
	public void setQuoteService(final QuoteService quoteService)
	{
		this.quoteService = quoteService;
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

	protected Converter<QuoteModel, QuoteData> getQuoteConverter()
	{
		return quoteConverter;
	}

	@Required
	public void setQuoteConverter(final Converter<QuoteModel, QuoteData> quoteConverter)
	{
		this.quoteConverter = quoteConverter;
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

	public EnumerationService getEnumerationService()
	{
		return enumerationService;
	}

	@Required
	public void setEnumerationService(final EnumerationService enumerationService)
	{
		this.enumerationService = enumerationService;
	}

	public TypeService getTypeService()
	{
		return typeService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	protected CommerceCommentService getCommerceCommentService()
	{
		return commerceCommentService;
	}

	@Required
	public void setCommerceCommentService(final CommerceCommentService commerceCommentService)
	{
		this.commerceCommentService = commerceCommentService;
	}

	protected QuoteUserIdentificationStrategy getQuoteUserIdentificationStrategy()
	{
		return quoteUserIdentificationStrategy;
	}

	@Required
	public void setQuoteUserIdentificationStrategy(final QuoteUserIdentificationStrategy quoteUserIdentificationStrategy)
	{
		this.quoteUserIdentificationStrategy = quoteUserIdentificationStrategy;
	}
}
