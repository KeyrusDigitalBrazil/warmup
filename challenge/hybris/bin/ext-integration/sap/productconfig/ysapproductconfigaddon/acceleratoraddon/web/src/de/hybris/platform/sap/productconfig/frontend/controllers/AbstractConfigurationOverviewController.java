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
package de.hybris.platform.sap.productconfig.frontend.controllers;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.QuoteFacade;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartParameterData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.quote.data.QuoteData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;
import de.hybris.platform.sap.productconfig.frontend.FilterData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.util.ConfigErrorHandler;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;


/**
 * Abstract base class for all controllers of the Product Configuration Overview Page.<br>
 * The Overview page can be shown in different contexts, for each of them an own controller implementation exists. For
 * example:
 * <ul>
 * <li>Cart - {@link ConfigurationOverviewController}</li>
 * <li>Order - {@link OrderEntryOverviewController}</li>
 * <li>Variants - {@link VariantOverviewController}</li>
 * </ul>
 */
public class AbstractConfigurationOverviewController extends AbstractProductConfigController
{

	protected static final String CMS_OV_PAGE_TYPE = "productConfigOverviewPage";
	protected static final String CMS_OV_PAGE_ID = "productConfigOverview";
	protected static final String NO_ERROR_COUNT = " ";
	protected static final String[] ALLOWED_FIELDS_OVERVIEWUIDATA =
	{ "csticFilterList*", "groupFilterList*", "configId", "cpqAction", "overviewMode", "sourceDocumentId", "abstractOrderCode",
			"abstractOrderEntryNumber", "productCode" };
	@Resource(name = "sapProductConfigOverviewFacade")
	private ConfigurationOverviewFacade configurationOverviewFacade;
	@Resource(name = "quoteFacade")
	private QuoteFacade quoteFacade;
	@Resource(name = "saveCartFacade")
	private SaveCartFacade saveCartFacade;
	@Resource(name = "orderFacade")
	private OrderFacade orderFacade;
	@Resource(name = "sapProductConfigErrorHandler")
	private ConfigErrorHandler configurationErrorHandler;

	/**
	 * A HTTP-Client is an untrusted source, hence we provide a white list of form-fields, which we accept from the
	 * HTTP-Client.
	 *
	 * @param binder
	 */
	@InitBinder(SapproductconfigfrontendWebConstants.OVERVIEWUIDATA_ATTRIBUTE)
	public void initBinderConfigOverviewUiData(final WebDataBinder binder)
	{
		binder.setAllowedFields(ALLOWED_FIELDS_OVERVIEWUIDATA);
	}

	protected void prepareOverviewUiData(final UiStatus uiStatus, final OverviewUiData overviewUiData,
			final ConfigurationOverviewData configOverviewData, final KBKeyData kbKey) throws BusinessException
	{
		final String configId = configOverviewData.getId();
		overviewUiData.setConfigId(configId);
		overviewUiData.setProductCode(kbKey.getProductCode());
		overviewUiData.setPricing(configOverviewData.getPricing());
		overviewUiData.setCartEntryNumber(getCartEntryNumber(getCartFacade().getSessionCart(), configId));
		overviewUiData.setCsticFilterList(generateCsticFilterDataList(configOverviewData));
		overviewUiData.setGroupFilterList(computeUiGroupFilterList(uiStatus.getMaxGroupFilterList()));
		overviewUiData.setGroups(configOverviewData.getGroups());
	}

	protected void prepareOverviewUiData(final OverviewUiData overviewUiData, final ConfigurationOverviewData configOverviewData,
			final ProductData productData)
	{
		overviewUiData.setProductCode(productData.getCode());
		overviewUiData.setQuantity(getQuantity(productData.getBaseProduct()));
		overviewUiData.setGroups(configOverviewData.getGroups());
		overviewUiData.setPricing(configOverviewData.getPricing());
	}

	protected long getQuantity(final String baseProduct)
	{
		final UiStatus uiStatus = getSessionAccessFacade().getUiStatusForProduct(baseProduct);
		if (uiStatus != null)
		{
			return uiStatus.getQuantity();
		}

		return 1;
	}

	protected List<FilterData> computeUiGroupFilterList(final List<FilterData> maxUiGroups)
	{
		return new ArrayList<>(maxUiGroups);
	}

	protected List<FilterData> generateCsticFilterDataList(final ConfigurationOverviewData configOverviewData)
	{
		final List filterDataList = new ArrayList<>();
		if (configOverviewData != null)
		{
			final List<FilterEnum> allFilters = new ArrayList<>(Arrays.asList(FilterEnum.values()));

			for (final FilterEnum filter : allFilters)
			{
				if (FilterEnum.VISIBLE.equals(filter))
				{
					continue;
				}

				final FilterData filterData = new FilterData();
				filterData.setKey(filter.toString());
				final List appliedFilters = configOverviewData.getAppliedCsticFilters();
				if (appliedFilters != null)
				{
					filterData.setSelected(appliedFilters.contains(filter));
				}
				else
				{
					filterData.setSelected(false);
				}
				filterDataList.add(filterData);
			}
		}
		return filterDataList;
	}

	@Override
	protected String getPageId()
	{
		return CMS_OV_PAGE_ID;
	}

	@Override
	protected String getPageType()
	{
		return CMS_OV_PAGE_TYPE;
	}


	protected ConfigurationOverviewFacade getConfigurationOverviewFacade()
	{
		return configurationOverviewFacade;
	}

	/**
	 * @param configurationOverviewFacade
	 *           injects the facade for the variant overview
	 */
	public void setConfigurationOverviewFacade(final ConfigurationOverviewFacade configurationOverviewFacade)
	{
		this.configurationOverviewFacade = configurationOverviewFacade;
	}

	protected void prepareUiModel(final HttpServletRequest request, final Model model, final UiStatus uiStatus,
			final OverviewUiData overviewUiData, final ConfigurationOverviewData configOverviewData) throws BusinessException
	{

		final String productCode = configOverviewData.getProductCode();

		populateProductData(productCode, model, request);
		populateCMSAttributes(model);

		final String configId = configOverviewData.getId();
		final boolean collectConfigurationDetails = needConfigurationDetails(overviewUiData);
		if (collectConfigurationDetails)
		{
			final KBKeyData kbKey = new KBKeyData();
			kbKey.setProductCode(productCode);
			final ConfigurationData configData = reloadConfigurationById(configId, kbKey, uiStatus);

			configData.setLinkedToCartItem(isConfigLinkedToCart(configId));
			model.addAttribute(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE, configData);
			logModelmetaData(configData);
			final BindingResult errors = getBindingResultForConfiguration(configData, uiStatus);
			updateUiStatusForOverviewInSession(getCartItemByConfigId(configId), uiStatus, overviewUiData);
			model.addAttribute(BindingResult.MODEL_KEY_PREFIX + SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE, errors);
			prepareOverviewUiData(uiStatus, overviewUiData, configOverviewData, kbKey);
			final int errorCount = getConfigFacade().getNumberOfErrors(configId);
			model.addAttribute("errorCount", getErrorCountForUi(errorCount));
			model.addAttribute("overviewUiData", overviewUiData);
			model.addAttribute(WebConstants.BREADCRUMBS_KEY,
					getBreadcrumbBuilder().getOverviewBreadcrumbs(configOverviewData.getProductCode(), overviewUiData));
		}
		else
		{
			prepareUiModel(request, model, overviewUiData, configOverviewData);
		}
	}

	protected boolean needConfigurationDetails(final OverviewUiData overviewUiData)
	{
		return OverviewMode.CONFIGURATION_OVERVIEW.equals(overviewUiData.getOverviewMode());
	}

	protected Object getErrorCountForUi(final int errorCount)
	{
		final Object errorCountForUi;
		if (errorCount > 0)
		{
			errorCountForUi = Integer.valueOf(errorCount);
		}
		else
		{
			errorCountForUi = NO_ERROR_COUNT;
		}
		return errorCountForUi;
	}

	protected void prepareUiModel(final HttpServletRequest request, final Model model, final OverviewUiData overviewUiData,
			final ConfigurationOverviewData configOverviewData) throws CMSItemNotFoundException
	{
		final ProductData productData = populateProductData(configOverviewData.getProductCode(), model, request);
		populateCMSAttributes(model);
		prepareOverviewUiData(overviewUiData, configOverviewData, productData);
		addBreadCrumb(model, productData, overviewUiData);
		model.addAttribute("overviewUiData", overviewUiData);
	}

	protected void addBreadCrumb(final Model model, final ProductData productData, final OverviewUiData overviewUiData)
	{
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getBreadcrumbBuilder().getOverviewBreadcrumbs(productData.getCode(), overviewUiData));
	}

	protected void updateUiStatusForOverviewInSession(final String cartEntryKey, final UiStatus uiStatus,
			final OverviewUiData overviewUIData) throws BusinessException
	{
		if (uiStatus.getUserInputToRemember() != null)
		{
			setUiStatusForOverviewInSession(uiStatus, cartEntryKey, overviewUIData);
		}
	}

	protected List<FilterData> initializeGroupFilterDataList(final ConfigurationOverviewData configOverviewData)
	{
		final List<CharacteristicGroup> groups = configOverviewData.getGroups();
		final List<FilterData> filterDataList = new ArrayList<>();
		for (final CharacteristicGroup group : groups)
		{
			final FilterData filterData = new FilterData();
			filterData.setKey(group.getId());
			filterData.setDescription(group.getGroupDescription());
			//CPQ design convention:
			//Although all groups are shown, none of the group filters are initially displayed as selected.
			filterData.setSelected(false);
			filterDataList.add(filterData);
		}
		return filterDataList;
	}


	protected void initializeFilterListsInUiStatus(final ConfigurationOverviewData configOverviewData, final UiStatus uiStatus)
	{
		uiStatus.setCsticFilterList(generateCsticFilterDataList(null));
		uiStatus.setMaxGroupFilterList(initializeGroupFilterDataList(configOverviewData));
	}

	protected String getQuoteItemPk(final String quoteCode, final int quoteEntryNumber) throws BusinessException
	{
		final QuoteData quoteData = getQuoteFacade().getQuoteForCode(quoteCode);
		return getOrderEntry(quoteEntryNumber, quoteData).getItemPK();
	}

	protected String getOrderItemPk(final String orderCode, final int orderEntryNumber) throws BusinessException
	{
		final OrderData orderData = getOrderFacade().getOrderDetailsForCodeWithoutUser(orderCode);
		return getOrderEntry(orderEntryNumber, orderData).getItemPK();
	}

	protected void setUiStatusForOverviewInSession(final UiStatus uiStatus, final String cartEntryKey,
			final OverviewUiData overviewUiData) throws BusinessException
	{
		if (OverviewMode.QUOTATION_OVERVIEW.equals(overviewUiData.getOverviewMode()))
		{
			getSessionAccessFacade().setUiStatusForCartEntry(
					getQuoteItemPk(overviewUiData.getAbstractOrderCode(), overviewUiData.getAbstractOrderEntryNumber().intValue()),
					uiStatus);
		}
		else if (OverviewMode.ORDER_OVERVIEW.equals(overviewUiData.getOverviewMode()))
		{
			getSessionAccessFacade().setUiStatusForCartEntry(
					getOrderItemPk(overviewUiData.getAbstractOrderCode(), overviewUiData.getAbstractOrderEntryNumber().intValue()),
					uiStatus);
		}
		else if (OverviewMode.SAVED_CART_OVERVIEW.equals(overviewUiData.getOverviewMode()))
		{
			getSessionAccessFacade().setUiStatusForCartEntry(
					getCartItemPk(overviewUiData.getAbstractOrderCode(), overviewUiData.getAbstractOrderEntryNumber().intValue()),
					uiStatus);
		}
		else
		{
			getSessionAccessFacade().setUiStatusForCartEntry(cartEntryKey, uiStatus);
		}
	}

	protected UiStatus getUiStatusForOverview(final String cartEntryKey, final OverviewUiData overviewUiData)
			throws BusinessException
	{
		final OverviewMode overviewMode = overviewUiData.getOverviewMode();
		if (OverviewMode.QUOTATION_OVERVIEW.equals(overviewMode))
		{
			return getSessionAccessFacade().getUiStatusForCartEntry(
					getQuoteItemPk(overviewUiData.getAbstractOrderCode(), overviewUiData.getAbstractOrderEntryNumber().intValue()));
		}
		if (OverviewMode.ORDER_OVERVIEW.equals(overviewMode))
		{
			return getSessionAccessFacade().getUiStatusForCartEntry(
					getOrderItemPk(overviewUiData.getAbstractOrderCode(), overviewUiData.getAbstractOrderEntryNumber().intValue()));
		}
		if (OverviewMode.SAVED_CART_OVERVIEW.equals(overviewMode))
		{
			return getSessionAccessFacade().getUiStatusForCartEntry(
					getCartItemPk(overviewUiData.getAbstractOrderCode(), overviewUiData.getAbstractOrderEntryNumber().intValue()));
		}

		return getSessionAccessFacade().getUiStatusForCartEntry(cartEntryKey);
	}

	protected String getCartItemPk(final String abstractOrderCode, final int intValue) throws BusinessException
	{
		final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
		parameters.setCartId(abstractOrderCode);
		final CommerceSaveCartResultData commerceSaveCartResultData = getSaveCartFacade().getCartForCodeAndCurrentUser(parameters);
		return getOrderEntry(intValue, commerceSaveCartResultData.getSavedCartData()).getItemPK();
	}

	protected QuoteFacade getQuoteFacade()
	{
		return quoteFacade;
	}

	/**
	 * @param quoteFacade
	 *           quotation facade
	 */
	public void setQuoteFacade(final QuoteFacade quoteFacade)
	{
		this.quoteFacade = quoteFacade;
	}

	protected OrderFacade getOrderFacade()
	{
		return orderFacade;
	}

	/**
	 * @param orderFacade
	 *           order facade
	 */
	public void setOrderFacade(final OrderFacade orderFacade)
	{
		this.orderFacade = orderFacade;
	}

	/**
	 * @return configuration error handler
	 */
	@Override
	public ConfigErrorHandler getConfigurationErrorHandler()
	{
		return configurationErrorHandler;
	}

	/**
	 * @param configurationErrorHandler
	 *           for handling configuration errors
	 *
	 */
	@Override
	public void setConfigurationErrorHandler(final ConfigErrorHandler configurationErrorHandler)
	{
		this.configurationErrorHandler = configurationErrorHandler;
	}

	/**
	 * @return the saveCartFacade
	 */
	public SaveCartFacade getSaveCartFacade()
	{
		return saveCartFacade;
	}

	/**
	 * @param saveCartFacade
	 *           the saveCartFacade to set
	 */
	public void setSaveCartFacade(final SaveCartFacade saveCartFacade)
	{
		this.saveCartFacade = saveCartFacade;
	}

}
