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
package de.hybris.platform.sap.productconfig.frontend.breadcrumb;

import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.Breadcrumb;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.ResourceBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.breadcrumb.impl.ProductBreadcrumbBuilder;
import de.hybris.platform.acceleratorstorefrontcommons.tags.Functions;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.services.impl.CPQConfigurableChecker;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.variants.model.VariantProductModel;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.util.Assert;


/**
 * CPQ Bread crumb builder. It contains methods to build bread crumbs for the ConfigurationPage as well as the
 * ConfigurationOverviewPage.
 */
public class ProductConfigureBreadcrumbBuilder extends ProductBreadcrumbBuilder
{

	protected static final String DEFAULT_TEXT_CART = "Cart";
	protected static final String DEFAULT_TEXT_CART_CONFIG = "Configuration for {0}";
	protected static final String DEFAULT_TEXT_CONFIG = "Configuration";
	protected static final String DEFAULT_TEXT_DOCUMENT_OVERVIEW = "Configuration Overview";
	protected static final String DEFAULT_TEXT_OVERVIEW = "Overview";
	protected static final String DEFAULT_TEXT_QUOTES = "Quotes";
	protected static final String DEFAULT_TEXT_ORDERS = "Orders";
	protected static final String DEFAULT_TEXT_SAVED_CARTS = "Saved Carts";

	protected static final String DEFAULT_TEXT_SINGLE_QUOTE = "Quote {0}";
	protected static final String DEFAULT_TEXT_SINGLE_ORDER = "Order {0}";
	protected static final String DEFAULT_TEXT_SINGLE_SAVED_CART = "Saved Cart {0}";
	protected static final String RESOURCE_KEY_OVERVIEW = "sapproductconfig.config.overview.breadcrumb";
	protected static final String RESOURCE_KEY_CONFIG = "sapproductconfig.config.breadcrumb";
	protected static final String RESOURCE_KEY_CART = "sapproductconfig.breadcrumb.cart";
	protected static final String RESOURCE_KEY_CART_CONFIG = "sapproductconfig.config.cart.breadcrumb";
	protected static final String RESOURCE_KEY_DOCUMENT_OVERVIEW = "sapproductconfig.config.document.overview.breadcrumb";
	protected static final String RESOURCE_KEY_SINGLE_QUOTE = "sapproductconfig.breadcrumb.quote.view";
	protected static final String RESOURCE_KEY_SINGLE_ORDER = "sapproductconfig.breadcrumb.order.view";
	protected static final String RESOURCE_KEY_SINGLE_SAVED_CART = "sapproductconfig.breadcrumb.savedcart.view";
	protected static final String RESOURCE_KEY_QUOTES = "sapproductconfig.text.account.manageQuotes.breadcrumb";
	protected static final String RESOURCE_KEY_ORDERS = "sapproductconfig.text.account.manageOrders.breadcrumb";
	protected static final String RESOURCE_KEY_SAVED_CARTS = "sapproductconfig.text.account.manageSavedCarts.breadcrumb";

	protected static final String URL_MY_ACCOUNT_MY_QUOTES = "/my-account/my-quotes";
	protected static final String URL_MY_ACCOUNT_ORDER = "/my-account/order";
	protected static final String URL_MY_ACCOUNT_ORDERS = "/my-account/orders";
	protected static final String URL_MY_ACCOUNT_SAVED_CARTS = "/my-account/saved-carts";
	protected static final String URL_CART = "/cart";
	private static final String LAST_LINK_CLASS = "active";


	private ResourceBreadcrumbBuilder accountBreadcrumbBuilder;
	private CPQConfigurableChecker cpqConfigurableChecker;

	/**
	 * Builds the breadcrumbs when the configuration page is accessed.
	 *
	 * @param productCode
	 * @return bread crumb list
	 */
	@Override
	public List<Breadcrumb> getBreadcrumbs(final String productCode)
	{
		final List<Breadcrumb> breadcrumbs = super.getBreadcrumbs(productCode);

		breadcrumbs.stream().filter(t -> LAST_LINK_CLASS.equalsIgnoreCase(t.getLinkClass())).forEach(t -> t.setLinkClass(null));

		final Breadcrumb last = new Breadcrumb(getUrl(productCode, SapproductconfigfrontendWebConstants.CONFIG_URL), getLinkText(),
				LAST_LINK_CLASS);
		breadcrumbs.add(last);

		return breadcrumbs;
	}

	@Override
	protected ProductModel getBaseProduct(final ProductModel product)
	{
		if ((product instanceof VariantProductModel) && !getCpqConfigurableChecker().isCPQConfiguratorApplicableProduct(product))
		{
			return getBaseProduct(((VariantProductModel) product).getBaseProduct());
		}
		return product;
	}

	/**
	 * Builds the breadcrumbs when the configuration page is accessed from the cart.
	 *
	 * @param productCode
	 * @param cartEntryNumber
	 * @return bread crumb list
	 */
	public List<Breadcrumb> getBreadcrumbs(final String productCode, final Integer cartEntryNumber)
	{
		if (cartEntryNumber != null)
		{
			return getBreadcrumbsForConfigFromCart(productCode, cartEntryNumber);
		}
		else
		{
			return getBreadcrumbs(productCode);
		}
	}

	/**
	 * Builds the breadcrumbs for the OverviewPage. The exact structure depends on the context in which the overview page
	 * is displayed.
	 *
	 * @param productCode
	 * @param overviewUiData
	 *           context of the overview page
	 * @return bread crumb list
	 */
	public List<Breadcrumb> getOverviewBreadcrumbs(final String productCode, final OverviewUiData overviewUiData)
	{
		if (overviewUiData.getOverviewMode() == OverviewMode.QUOTATION_OVERVIEW
				|| overviewUiData.getOverviewMode() == OverviewMode.QUOTATION_VARIANT_OVERVIEW)
		{
			return getOverviewBreadcrumbsForQuotation(overviewUiData.getSourceDocumentId());
		}
		else if (overviewUiData.getOverviewMode() == OverviewMode.ORDER_OVERVIEW
				|| overviewUiData.getOverviewMode() == OverviewMode.ORDER_VARIANT_OVERVIEW)
		{
			return getOverviewBreadcrumbsForOrder(overviewUiData.getSourceDocumentId());
		}
		else if (overviewUiData.getOverviewMode() == OverviewMode.SAVED_CART_OVERVIEW
				|| overviewUiData.getOverviewMode() == OverviewMode.SAVED_CART_VARIANT_OVERVIEW)
		{
			return getOverviewBreadcrumbsForSavedCart(overviewUiData.getSourceDocumentId());
		}
		else
		{
			return getOverviewBreadcrumbs(productCode, overviewUiData.getCartEntryNumber());
		}
	}



	protected List<Breadcrumb> getOverviewBreadcrumbs(final String productCode, final Integer cartEntryNumber)
	{
		if (cartEntryNumber == null)
		{
			return getOverviewBreadcrumbsForProduct(productCode);
		}
		else
		{
			return getOverviewBreadcrumbsForConfigFromCart(productCode, cartEntryNumber);
		}

	}

	protected List<Breadcrumb> getOverviewBreadcrumbsForConfigFromCart(final String productCode, final Integer cartEntryNumber)
	{
		final List<Breadcrumb> breadcrumbs = getBreadcrumbsForConfigFromCart(productCode, cartEntryNumber);

		breadcrumbs.stream().filter(t -> LAST_LINK_CLASS.equalsIgnoreCase(t.getLinkClass())).forEach(t -> t.setLinkClass(null));

		final Breadcrumb last = new Breadcrumb(getConfigFromCartURL(cartEntryNumber, false), getOverviewLinkText(),
				LAST_LINK_CLASS);
		breadcrumbs.add(last);

		return breadcrumbs;
	}

	/**
	 * Builds the breadcrumbs when the configuration page is accessed from the cart.
	 *
	 * @param productCode
	 * @param cartEntryNumber
	 * @return bread crumb list
	 */
	public List<Breadcrumb> getBreadcrumbsForConfigFromCart(final String productCode, final Integer cartEntryNumber)
	{
		// There is no bean that would generate us the first part of the cart breadcrumbs,
		// the accelerator does this in controllers-> we need to partially replicate this behavior from accelerator
		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb(URL_CART, getCartText(), null));
		final Breadcrumb last = new Breadcrumb(getConfigFromCartURL(cartEntryNumber, false),
				getCurrentCartEntryConfigText(productCode), LAST_LINK_CLASS);
		breadcrumbs.add(last);
		return breadcrumbs;
	}

	protected List<Breadcrumb> getBreadcrumbsForConfigFromCartOnExistingDraft(final String productCode,
			final Integer cartEntryNumber)
	{

		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb(URL_CART, getCartText(), null));
		final Breadcrumb last = new Breadcrumb(getConfigFromCartURL(cartEntryNumber, true),
				getCurrentCartEntryConfigText(productCode), LAST_LINK_CLASS);
		breadcrumbs.add(last);
		return breadcrumbs;
	}

	protected String getConfigFromCartURL(final Integer cartEntryNumber, final boolean isFromExistingDraft)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append(URL_CART);
		builder.append("/");
		builder.append(cartEntryNumber);
		if (isFromExistingDraft)
		{
			builder.append(SapproductconfigfrontendWebConstants.CART_CONFIG_EXISTING_DRAFT_URL);
		}
		else
		{
			builder.append(SapproductconfigfrontendWebConstants.CART_CONFIG_URL);
		}
		return builder.toString();
	}

	protected List<Breadcrumb> getOverviewBreadcrumbsForQuotation(final String sourceDocumentId)
	{
		//There is no bean that would generate us the first part of the quotation breadcrumbs,
		//the accelerator does this in controllers-> we need to partially replicate this behavior from accelerator
		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb(URL_MY_ACCOUNT_MY_QUOTES, getQuotesText(), null));
		breadcrumbs.add(new Breadcrumb(URL_MY_ACCOUNT_MY_QUOTES + "/" + urlEncode(sourceDocumentId),
				getCurrentQuoteText(sourceDocumentId), null));
		final Breadcrumb last = new Breadcrumb("", getOverviewAccountLinkText(), LAST_LINK_CLASS);
		breadcrumbs.add(last);
		return breadcrumbs;
	}

	protected String getLocalizedTextOrDefault(final String key, final String defaultText)
	{
		return getLocalizedTextOrDefault(key, defaultText, null);
	}

	protected String getLocalizedTextOrDefault(final String key, final String defaultText, final Object[] args)
	{
		String localizedString = callLocalization(key, args);
		if (key.equals(localizedString))
		{
			localizedString = formatDefaultText(defaultText, args);
		}
		return localizedString;
	}

	protected String formatDefaultText(final String defaultText, final Object[] args)
	{
		String formattedText = defaultText;
		if (args != null && args.length > 0)
		{
			final MessageFormat messageFormat = new MessageFormat(defaultText);
			formattedText = messageFormat.format(args, new StringBuffer(), null).toString();
		}
		return formattedText;
	}


	protected String callLocalization(final String key, final Object[] args)
	{
		return Localization.getLocalizedString(key, args);
	}


	protected String getCurrentQuoteText(final String sourceDocumentId)
	{
		return getLocalizedTextOrDefault(RESOURCE_KEY_SINGLE_QUOTE, DEFAULT_TEXT_SINGLE_QUOTE, new Object[]
		{ sourceDocumentId });
	}

	protected String getCurrentOrderText(final String sourceDocumentId)
	{
		return getLocalizedTextOrDefault(RESOURCE_KEY_SINGLE_ORDER, DEFAULT_TEXT_SINGLE_ORDER, new Object[]
		{ sourceDocumentId });
	}

	protected String getCurrentSavedCartText(final String sourceDocumentId)
	{
		return getLocalizedTextOrDefault(RESOURCE_KEY_SINGLE_SAVED_CART, DEFAULT_TEXT_SINGLE_SAVED_CART, new Object[]
		{ sourceDocumentId });
	}

	protected String getCurrentCartEntryConfigText(final String productCode)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		final ProductModel baseProductModel = getBaseProduct(productModel);
		return getLocalizedTextOrDefault(RESOURCE_KEY_CART_CONFIG, DEFAULT_TEXT_CART_CONFIG, new Object[]
		{ baseProductModel.getName() });
	}

	protected String urlEncode(final String url)
	{
		Assert.notNull(url, "Parameter [url] cannot be null");
		return Functions.encodeUrl(url);
	}

	protected List<Breadcrumb> getOverviewBreadcrumbsForProduct(final String productCode)
	{
		final List<Breadcrumb> breadcrumbs = getBreadcrumbs(productCode);

		breadcrumbs.stream().filter(t -> LAST_LINK_CLASS.equalsIgnoreCase(t.getLinkClass())).forEach(t -> t.setLinkClass(null));

		final Breadcrumb last = new Breadcrumb(getUrl(productCode, SapproductconfigfrontendWebConstants.CONFIG_OVERVIEW_URL),
				getOverviewLinkText(), LAST_LINK_CLASS);
		breadcrumbs.add(last);

		return breadcrumbs;
	}

	/**
	 * Builds the breadcrumbs when the configuration overview page is accessed in context of a product variant.
	 *
	 * @param baseProduct
	 *           code of the base product of the variant
	 * @param productCode
	 * @return bread crumb list
	 */
	public List<Breadcrumb> getVariantOverviewBreadcrumbs(final String baseProduct, final String productCode)
	{
		final List<Breadcrumb> breadcrumbs = getBreadcrumbs(baseProduct);

		breadcrumbs.stream().filter(t -> LAST_LINK_CLASS.equalsIgnoreCase(t.getLinkClass())).forEach(t -> t.setLinkClass(null));

		final Breadcrumb last = new Breadcrumb(getUrl(productCode, SapproductconfigfrontendWebConstants.VARIANT_OVERVIEW_URL),
				getOverviewLinkText(), LAST_LINK_CLASS);
		breadcrumbs.add(last);

		return breadcrumbs;
	}

	/**
	 * Builds the breadcrumbs when the variant overview page is accessed from the cart.
	 * 
	 * @param baseProduct
	 * 	code of the base product of the variant
	 * @param code
	 * 	product code
	 * @param cartEntryNumber
	 * 	cart entry number
	 * @return
	 */
	public List<Breadcrumb> getVariantOverviewCartContextBreadcrumbs(final String baseProduct, final String code,
			final Integer cartEntryNumber)
	{
		final List<Breadcrumb> overviewBreadcrumbsForConfigFromCart = getBreadcrumbsForConfigFromCartOnExistingDraft(baseProduct,
				cartEntryNumber);

		overviewBreadcrumbsForConfigFromCart.stream().filter(t -> LAST_LINK_CLASS.equalsIgnoreCase(t.getLinkClass()))
				.forEach(t -> t.setLinkClass(null));
		final Breadcrumb last = new Breadcrumb(null, getOverviewLinkText(), LAST_LINK_CLASS);
		overviewBreadcrumbsForConfigFromCart.add(last);
		return overviewBreadcrumbsForConfigFromCart;
	}

	protected String getLinkText()
	{
		return getLocalizedTextOrDefault(RESOURCE_KEY_CONFIG, DEFAULT_TEXT_CONFIG);
	}

	protected String getCartText()
	{
		return getLocalizedTextOrDefault(RESOURCE_KEY_CART, DEFAULT_TEXT_CART);
	}

	protected String getOverviewLinkText()
	{
		return getLocalizedTextOrDefault(RESOURCE_KEY_OVERVIEW, DEFAULT_TEXT_OVERVIEW);
	}

	protected String getOverviewAccountLinkText()
	{
		return getLocalizedTextOrDefault(RESOURCE_KEY_DOCUMENT_OVERVIEW, DEFAULT_TEXT_DOCUMENT_OVERVIEW);
	}



	protected String getQuotesText()
	{
		return getLocalizedTextOrDefault(RESOURCE_KEY_QUOTES, DEFAULT_TEXT_QUOTES);
	}

	protected String getOrderText()
	{
		return getLocalizedTextOrDefault(RESOURCE_KEY_ORDERS, DEFAULT_TEXT_ORDERS);
	}

	protected String getSavedCartText()
	{
		return getLocalizedTextOrDefault(RESOURCE_KEY_SAVED_CARTS, DEFAULT_TEXT_SAVED_CARTS);
	}

	protected String getUrl(final String productCode, final String appendUrl)
	{
		final ProductModel productModel = getProductService().getProductForCode(productCode);
		final String productUrl = super.getProductModelUrlResolver().resolve(productModel);
		return productUrl + appendUrl;
	}

	protected ResourceBreadcrumbBuilder getAccountBreadcrumbBuilder()
	{
		return this.accountBreadcrumbBuilder;
	}

	/**
	 * @param accountBreadcrumbBuilder
	 *           the accountBreadcrumbBuilder to set
	 */
	public void setAccountBreadcrumbBuilder(final ResourceBreadcrumbBuilder accountBreadcrumbBuilder)
	{
		this.accountBreadcrumbBuilder = accountBreadcrumbBuilder;
	}

	protected List<Breadcrumb> getOverviewBreadcrumbsForOrder(final String orderId)
	{
		//There is no bean that would generate us the first part of the order breadcrumbs,
		//the accelerator does this in controllers-> we need to partially replicate this behavior from accelerator
		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb(URL_MY_ACCOUNT_ORDERS, getOrderText(), null));
		breadcrumbs.add(new Breadcrumb(URL_MY_ACCOUNT_ORDER + "/" + urlEncode(orderId), getCurrentOrderText(orderId), null));
		final Breadcrumb last = new Breadcrumb("", getOverviewAccountLinkText(), LAST_LINK_CLASS);
		breadcrumbs.add(last);
		return breadcrumbs;
	}

	protected List<Breadcrumb> getOverviewBreadcrumbsForSavedCart(final String sourceDocumentId)
	{
		final List<Breadcrumb> breadcrumbs = accountBreadcrumbBuilder.getBreadcrumbs(null);
		breadcrumbs.add(new Breadcrumb(URL_MY_ACCOUNT_SAVED_CARTS, getSavedCartText(), null));
		breadcrumbs.add(new Breadcrumb(URL_MY_ACCOUNT_SAVED_CARTS + "/" + urlEncode(sourceDocumentId),
				getCurrentSavedCartText(sourceDocumentId), null));
		final Breadcrumb last = new Breadcrumb("", getOverviewAccountLinkText(), LAST_LINK_CLASS);
		breadcrumbs.add(last);
		return breadcrumbs;
	}

	protected CPQConfigurableChecker getCpqConfigurableChecker()
	{
		return cpqConfigurableChecker;
	}

	public void setCpqConfigurableChecker(final CPQConfigurableChecker cpqConfigurableChecker)
	{
		this.cpqConfigurableChecker = cpqConfigurableChecker;
	}
}
