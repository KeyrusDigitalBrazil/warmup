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
package de.hybris.platform.assistedserviceservices.impl;

import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.assistedserviceservices.exception.AssistedServiceCartBindException;
import de.hybris.platform.assistedserviceservices.exception.AssistedServiceException;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.model.user.StoreEmployeeGroupModel;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.search.flexiblesearch.PagedFlexibleSearchService;
import de.hybris.platform.commerceservices.search.flexiblesearch.data.SortQueryData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.util.localization.Localization;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants.ASM_SESSION_PARAMETER;
import static de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants.SORT_BY_NAME_ASC;
import static de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants.SORT_BY_NAME_DESC;
import static de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants.SORT_BY_UID_ASC;
import static de.hybris.platform.assistedserviceservices.constants.AssistedserviceservicesConstants.SORT_BY_UID_DESC;


/**
 * Default implementation of {@link AssistedServiceService}
 */
public class DefaultAssistedServiceService implements AssistedServiceService
{
	private static final Logger LOG = Logger.getLogger(DefaultAssistedServiceService.class);

	private static final String USERNAME = "username";
	private static final String CURRENTDATE = "currentDate";
	private static final String LOGINDISABLED_PARAMETER = "loginDisabled";

	private CartService cartService;
	private SessionService sessionService;
	private UserService userService;
	private BaseSiteService baseSiteService;
	private ModelService modelService;
	private FlexibleSearchService flexibleSearchService;
	private PagedFlexibleSearchService pagedFlexibleSearchService;
	private CommerceCartService commerceCartService;
	private CustomerAccountService customerAccountService;
	private CommonI18NService commonI18NService;
	private TimeService timeService;


	@Override
	public SearchPageData<CustomerModel> getCustomers(final String searchCriteria, final PageableData pageableData)
	{
		final StringBuilder builder = getCustomerSearchQuery(searchCriteria);

		final Map<String, Object> params = new HashMap<>();
		params.put(CURRENTDATE, getTimeService().getCurrentTime());
		params.put(LOGINDISABLED_PARAMETER, Boolean.FALSE);

		if (StringUtils.isNotBlank(searchCriteria))
		{
			params.put(USERNAME, searchCriteria.toLowerCase());
		}

		final List<SortQueryData> sortQueries = Arrays.asList(
				createSortQueryData(SORT_BY_UID_ASC, builder.toString() + " ORDER BY {p." + CustomerModel.UID + "} ASC"),
				createSortQueryData(SORT_BY_UID_DESC, builder.toString() + " ORDER BY {p." + CustomerModel.UID + "} DESC"),
				createSortQueryData(SORT_BY_NAME_ASC, builder.toString() + " ORDER BY {p." + CustomerModel.NAME + "} ASC"),
				createSortQueryData(SORT_BY_NAME_DESC, builder.toString() + " ORDER BY {p." + CustomerModel.NAME + "} DESC"));

		return getPagedFlexibleSearchService().search(sortQueries, SORT_BY_UID_ASC, params, pageableData);
	}

	protected StringBuilder getCustomerSearchQuery(final String searchCriteria)
	{
		final StringBuilder builder = new StringBuilder();

		builder.append("SELECT ");
		builder.append("{p:" + CustomerModel.PK + "} ");
		builder.append("FROM {" + CustomerModel._TYPECODE + " AS p} ");
		builder.append("WHERE NOT {" + CustomerModel.UID + "}='" + UserConstants.ANONYMOUS_CUSTOMER_UID + "' ");
		builder.append("AND {p:" + CustomerModel.LOGINDISABLED + "} = ?" + LOGINDISABLED_PARAMETER + " ");
		builder.append("AND ({p:" + CustomerModel.DEACTIVATIONDATE + "} IS NULL OR {p:" + CustomerModel.DEACTIVATIONDATE + "} > ?"
				+ CURRENTDATE + ") ");

		if (!StringUtils.isBlank(searchCriteria))
		{
			builder.append("AND (LOWER({p:" + CustomerModel.UID + "}) LIKE CONCAT(?username, '%') ");
			builder.append("OR LOWER({p:name}) LIKE CONCAT('%', CONCAT(?username, '%'))) ");
		}
		return builder;
	}

	protected SortQueryData createSortQueryData(final String sortCode, final String query)
	{
		final SortQueryData result = new SortQueryData();
		result.setSortCode(sortCode);
		result.setQuery(query);
		return result;
	}

	@Override
	public void bindCustomerToCart(final String customerId, final String cartId) throws AssistedServiceException
	{
		final UserModel customer = customerId == null ? getUserService().getCurrentUser()
				: getUserService().getUserForUID(customerId);
		final CartModel cart = cartId == null ? getCartService().getSessionCart()
				: getCartByCode(cartId, getUserService().getAnonymousUser());
		if (cart == null || !getUserService().isAnonymousUser(cart.getUser()))
		{
			throw new AssistedServiceCartBindException(Localization.getLocalizedString("asm.bindCart.error.not_anonymous_cart"));
		}
		getUserService().setCurrentUser(customer);
		getCartService().setSessionCart(cart);
		getCartService().changeCurrentCartUser(customer);
		getAsmSession().setEmulatedCustomer(customer);
	}

	@Override
	public AssistedServiceSession getAsmSession()
	{
		return getSessionService().getAttribute(ASM_SESSION_PARAMETER);
	}

	@Override
	public void restoreCartToUser(final CartModel cart, final UserModel user)
	{
		if (user != null && CollectionUtils.isNotEmpty(cart.getEntries()))
		{
			getCartService().changeCurrentCartUser(user);
			// refresh persistence context after cart user manipulations
			// without this step - customer will not have modified cart
			getModelService().refresh(user);
		}
	}

	@Override
	public CustomerModel createNewCustomer(final String customerId, final String customerName) throws DuplicateUidException
	{
		final CustomerModel customerModel = getModelService().create(CustomerModel.class);

		customerModel.setName(customerName.trim());
		customerModel.setUid(customerId);
		customerModel.setLoginDisabled(false);
		customerModel.setSessionCurrency(getCommonI18NService().getCurrentCurrency());
		customerModel.setSessionLanguage(getCommonI18NService().getCurrentLanguage());
		getCustomerAccountService().register(customerModel, null);
		LOG.info(String.format("New customer has been created via ASM: uid [%s]", customerId));

		return customerModel;
	}

	@Override
	public Collection<CartModel> getCartsForCustomer(final CustomerModel customer)
	{
		final BaseSiteModel paramBaseSiteModel = getBaseSiteService().getCurrentBaseSite();
		return getCommerceCartService().getCartsForSiteAndUser(paramBaseSiteModel, customer);
	}

	@Override
	public UserModel getCustomer(final String customerId)
	{
		if (StringUtils.isBlank(customerId))
		{
			return getUserService().getAnonymousUser();
		}
		else
		{
			final StringBuilder buf = new StringBuilder();

			// select the chosen customer using his UID or CustomerId
			buf.append("SELECT DISTINCT {p:" + CustomerModel.PK + "} ");
			buf.append("FROM {" + CustomerModel._TYPECODE + " as p } ");
			buf.append("WHERE {p:" + CustomerModel.UID + "} = ?customerId ");
			buf.append("OR {p:" + CustomerModel.CUSTOMERID + "} = ?customerId ");

			final FlexibleSearchQuery query = new FlexibleSearchQuery(buf.toString());
			query.addQueryParameter("customerId", customerId);
			final List<CustomerModel> matchCustomers = getFlexibleSearchService().<CustomerModel> search(query).getResult();
			if (CollectionUtils.isEmpty(matchCustomers))
			{
				throw new UnknownIdentifierException(
						(new StringBuilder("Cannot find user with id '")).append(customerId).append("'").toString());
			}
			if (matchCustomers.size() > 1)
			{
				LOG.warn("More than two customers were found with id=[" + customerId + "]");
			}
			return matchCustomers.iterator().next();
		}
	}

	@Override
	public CartModel getLatestModifiedCart(final UserModel customer)
	{
		return getCommerceCartService().getCartForGuidAndSiteAndUser(null, getBaseSiteService().getCurrentBaseSite(), customer);
	}

	/**
	 * @deprecated since 6.6, use {@link AssistedServiceService#getOrderByCode(String, UserModel)} instead
	 *
	 * @param orderCode
	 * 			  the order's code
	 * @param customer
	 * 			  customer model whose order to be returned
     */
	@Deprecated
	@Override
	public OrderModel gerOrderByCode(final String orderCode, final UserModel customer)
	{
		return getOrderByCode(orderCode, customer);
	}

	@Override
	public OrderModel getOrderByCode(final String orderCode, final UserModel customer)
	{
		final StringBuilder buf = new StringBuilder();

		// select the chosen order using his code
		buf.append("SELECT DISTINCT {p:" + OrderModel.PK + "} ");
		buf.append("FROM {" + OrderModel._TYPECODE + " as p } ");
		buf.append("WHERE {p:" + OrderModel.CODE + "} = ?orderCode ");
		buf.append("OR {p:" + OrderModel.GUID + "} = ?orderCode ");

		final FlexibleSearchQuery query = new FlexibleSearchQuery(buf.toString());
		query.addQueryParameter("orderCode", orderCode);
		final List<OrderModel> matchedOrder = getFlexibleSearchService().<OrderModel> search(query).getResult();
		if (CollectionUtils.isEmpty(matchedOrder))
		{
			return null;
		}
		if (matchedOrder.size() > 1)
		{
			LOG.warn("More than two orders were found with code=[" + orderCode + "]"); // how??
		}
		final OrderModel order = matchedOrder.iterator().next();
		if (!isAbstractOrderMatchBaseSite(order)
				|| ((!getUserService().isAnonymousUser(customer)) && !order.getUser().getUid().equals(customer.getUid())))
		{
			return null;
		}
		return order;
	}

	@Override
	public CartModel getCartByCode(final String cartCode, final UserModel customer)
	{
		final CartModel cartModel = getCommerceCartService().getCartForCodeAndUser(cartCode, customer);
		if (cartModel != null)
		{
			return isAbstractOrderMatchBaseSite(cartModel) ? cartModel : null;
		}
		return getCommerceCartService().getCartForGuidAndSiteAndUser(cartCode, getBaseSiteService().getCurrentBaseSite(), customer);
	}

	@Override
	public PointOfServiceModel getAssistedServiceAgentStore()
	{
		return getAssistedServiceAgentStore(getAsmSession().getAgent());
	}

	@Override
	public PointOfServiceModel getAssistedServiceAgentStore(final UserModel agent)
	{
		if (agent != null && CollectionUtils.isNotEmpty(agent.getAllGroups()))
		{
			final List<StoreEmployeeGroupModel> storeEmployeeGroups = getUserService()
					.getAllUserGroupsForUser(agent, StoreEmployeeGroupModel.class).stream().filter(group -> group.getStore() != null)
					.collect(Collectors.toList());
			if (!storeEmployeeGroups.isEmpty())
			{
				return storeEmployeeGroups.get(0).getStore();
			}

		}
		return null;
	}

	@Override
	public boolean isAbstractOrderMatchBaseSite(final AbstractOrderModel abstractOrderModel)
	{
		return abstractOrderModel.getSite() != null
				&& getBaseSiteService().getCurrentBaseSite().getUid().equals(abstractOrderModel.getSite().getUid());
	}

	protected FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	@Required
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
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

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
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

	protected CustomerAccountService getCustomerAccountService()
	{
		return customerAccountService;
	}

	@Required
	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	protected SessionService getSessionService()
	{
		return sessionService;
	}

	@Required
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
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

	protected UserService getUserService()
	{
		return userService;
	}

	@Required
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
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

	protected PagedFlexibleSearchService getPagedFlexibleSearchService()
	{
		return pagedFlexibleSearchService;
	}

	@Required
	public void setPagedFlexibleSearchService(final PagedFlexibleSearchService pagedFlexibleSearchService)
	{
		this.pagedFlexibleSearchService = pagedFlexibleSearchService;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}
}