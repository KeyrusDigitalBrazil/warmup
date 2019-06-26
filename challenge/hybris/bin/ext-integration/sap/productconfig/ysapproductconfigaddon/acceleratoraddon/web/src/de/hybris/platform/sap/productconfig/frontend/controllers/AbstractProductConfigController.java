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

import de.hybris.platform.acceleratorservices.data.RequestContextData;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.acceleratorstorefrontcommons.forms.ReviewForm;
import de.hybris.platform.acceleratorstorefrontcommons.util.MetaSanitizerUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ImageDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.ConfigurationCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.SessionAccessFacade;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.tracking.UiTrackingRecorder;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.breadcrumb.ProductConfigureBreadcrumbBuilder;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.util.ConfigErrorHandler;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiDataStats;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStateHandler;
import de.hybris.platform.sap.productconfig.frontend.util.impl.UiStatusSync;
import de.hybris.platform.sap.productconfig.frontend.validator.ConflictChecker;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;


/**
 * Abstract base class for all CPQ UI controllers.
 */
public abstract class AbstractProductConfigController extends AbstractPageController
{
	protected static final String CMS_PC_PAGE_TYPE = "productConfigPage";
	protected static final String CMS_PC_PAGE_ID = "productConfig";
	protected static final String CMS_PAGE_TYPE = "pageType";
	private static final Logger LOGGER = Logger.getLogger(AbstractProductConfigController.class.getName());
	private static final String LOG_CONFIG_DATA = "configuration data with [CONFIG_ID: '";
	private static final String[] ALLOWED_FIELDS_CONFIG_DATA = new String[]
	{ "kbKey.productCode", "configId", "selectedGroup", "cartItemPK", "autoExpand", "focusId", "groupIdToDisplay", "quantity",
			"groupIdToDisplay", "groupToDisplay.groupIdPath", "groupToDisplay.path", "groupIdToToggle", "groupIdToToggleInSpecTree",
			"forceExpand", "hideImageGallery", "cpqAction", "groupToDisplay.path", "groups*" };

	@Resource(name = "sapProductConfigFacade")
	private ConfigurationFacade configFacade;
	@Resource(name = "sapProductConfigCartIntegrationFacade")
	private ConfigurationCartIntegrationFacade configCartFacade;
	@Resource(name = "sapProductConfigSessionAccessFacade")
	private SessionAccessFacade sessionAccessFacade;
	@Resource(name = "sapProductConfigAbstractOrderEntryLinkStrategy")
	private ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy;
	@Resource(name = "sapProductConfigProductLinkStrategy")
	private ConfigurationProductLinkStrategy configurationProductLinkStrategy;
	@Resource(name = "sapProductConfigValidator")
	private Validator productConfigurationValidator;
	@Resource(name = "sapProductConfigConflictChecker")
	private ConflictChecker productConfigurationConflictChecker;
	@Resource(name = "productVariantFacade")
	private ProductFacade productFacade;
	@Resource(name = "productService")
	private ProductService productService;
	@Resource(name = "sapProductConfigBreadcrumbBuilder")
	private ProductConfigureBreadcrumbBuilder breadcrumbBuilder;
	@Resource(name = "sapProductConfigUiTrackingRecorder")
	private UiTrackingRecorder uiRecorder;
	@Resource(name = "sapProductConfigErrorHandler")
	private ConfigErrorHandler configurationErrorHandler;
	@Resource(name = "sapProductConfigUiStateHandler")
	private UiStateHandler uiStateHandler;
	@Resource(name = "sapProductConfigUiStatusSync")
	private UiStatusSync uiStatusSync;
	@Resource(name = "cartFacade")
	private CartFacade cartFacade;

	@InitBinder(SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE)
	protected void initBinderConfigData(final WebDataBinder binder)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("INIT Binder at: " + System.currentTimeMillis());
		}
		binder.setValidator(getProductConfigurationValidator());
		binder.setAllowedFields(ALLOWED_FIELDS_CONFIG_DATA);
	}

	protected BindingResult getBindingResultForConfigAndSaveUiStatus(final ConfigurationData configData, final UiStatus uiStatus)
	{
		final BindingResult errors = getBindingResultForConfiguration(configData, uiStatus);

		if (uiStatus.getUserInputToRemember() != null)
		{
			setUiStatusForConfig(configData, uiStatus);
		}

		return errors;
	}

	protected BindingResult getBindingResultForConfiguration(final ConfigurationData configData, final UiStatus uiStatus)
	{
		getUiStateHandler().resetGroupStatus(configData);
		BindingResult errors = new BeanPropertyBindingResult(configData, SapproductconfigfrontendWebConstants.CONFIG_ATTRIBUTE);
		// UI-Errors
		Map<String, FieldError> userInputToRestore = null;
		if (uiStatus != null)
		{
			userInputToRestore = uiStatus.getUserInputToRestore();
			final Map<String, FieldError> userInputToRemember = uiStatus.getUserInputToRemember();
			userInputToRestore = getUiStateHandler().mergeUiErrors(userInputToRestore, userInputToRemember);
			errors = getUiStateHandler().restoreValidationErrorsOnGetConfig(userInputToRestore, configData, errors);
		}

		productConfigurationConflictChecker.checkConflicts(configData, errors);
		if (isConfigLinkedToCart(configData.getConfigId()))
		{
			productConfigurationConflictChecker.checkMandatoryFields(configData, errors);
			logConfigurationCheckDeviation(errors, configData);
		}
		getProductConfigurationConflictChecker().checkCompletness(configData);
		getUiStateHandler().countNumberOfUiErrorsPerGroup(configData.getGroups());

		if (userInputToRestore != null)
		{
			final Map<String, FieldError> userInputToRemeber = getUiStateHandler().findCollapsedErrorCstics(userInputToRestore,
					configData);
			uiStatus.setUserInputToRemember(userInputToRemeber);
		}

		return errors;
	}

	/**
	 * The ConflictChecker checks only visible characteristics for consistency and completeness as only those
	 * characteristics might be changed by the user.<br>
	 * <br>
	 * If the model is modeled in a way that a conflict appears for an invisible characteristic or an invisible
	 * characteristic is mandatory but not filled this would not be identified by those checks but the overall
	 * configuration status is not consistent/complete. This leads to a situation where the configuration cannot be sent
	 * to the backend system.<br>
	 * <br>
	 * In this case the modeler needs to improve the model to avoid such situations. The user cannot do anything about
	 * this so this is only logged as an error as a hint for the modeler.
	 */
	protected void logConfigurationCheckDeviation(final BindingResult errors, final ConfigurationData configData)
	{
		if (!(configData.isComplete() && configData.isConsistent()) && !errors.hasErrors())
		{
			// Configuration is incomplete/inconsistent: check whether this is reflected in the BindingResult
			// BindingResult does not contain errors -> log deviation
			LOGGER.error("HINT: Configuration model of product " + configData.getKbKey().getProductCode()
					+ " needs to be improved! Configuration status is [complete=" + configData.isComplete() + "; consistent="
					+ configData.isConsistent()
					+ "] but the ConflictChecker signals no errors, i.e. the inconsistency/incompleteness exists at characteristics invisible for the user. Thus the user has no information what went wrong.");
		}
	}

	protected void setCartEntryLinks(final ConfigurationData configData) throws BusinessException
	{
		final String configId = configData.getConfigId();
		if (isConfigLinkedToCart(configId))
		{
			configData.setLinkedToCartItem(true);
		}
	}

	protected String getCartItemByProductCode(final String productCode)
	{
		String cartItemKey = null;
		final String configId = getProductLinkStrategy().getConfigIdForProduct(productCode);
		if (null != configId)
		{
			cartItemKey = getCartItemByConfigId(configId);
		}
		return cartItemKey;
	}

	protected String getCartItemByConfigId(final String configId)
	{
		String cartItemKey = getAbstractOrderEntryLinkStrategy().getCartEntryForConfigId(configId);
		if (null == cartItemKey)
		{
			cartItemKey = getAbstractOrderEntryLinkStrategy().getCartEntryForDraftConfigId(configId);
		}
		return cartItemKey;
	}

	protected boolean isConfigLinkedToCart(final String configId)
	{
		return !StringUtils.isEmpty(getCartItemByConfigId(configId));
	}

	protected Integer getCartEntryNumber(final String productCode) throws BusinessException
	{
		final String configId = getProductLinkStrategy().getConfigIdForProduct(productCode);
		if (configId != null)
		{
			return getCartEntryNumber(getCartFacade().getSessionCart(), configId);
		}
		return null;
	}

	protected UiStatus getUiStatusForConfig(final ConfigurationData configData)
	{
		final UiStatus uiStatus;
		if (isConfigLinkedToCart(configData.getConfigId()))
		{
			uiStatus = getSessionAccessFacade().getUiStatusForCartEntry(getCartItemByConfigId(configData.getConfigId()));
		}
		else
		{
			uiStatus = getSessionAccessFacade().getUiStatusForProduct(configData.getKbKey().getProductCode());
		}
		return uiStatus;
	}

	protected void setUiStatusForConfig(final ConfigurationData configData, final UiStatus uiStatus)
	{
		if (isConfigLinkedToCart(configData.getConfigId()))
		{
			getSessionAccessFacade().setUiStatusForCartEntry(getCartItemByConfigId(configData.getConfigId()), uiStatus);
		}
		else
		{
			getSessionAccessFacade().setUiStatusForProduct(configData.getKbKey().getProductCode(), uiStatus);
		}
	}

	protected UiStatus getUiStatusForConfigId(final String configId)
	{
		final String productCode = getProductLinkStrategy().retrieveProductCode(configId);
		UiStatus uiStatus = null;
		if (productCode != null)
		{
			uiStatus = getSessionAccessFacade().getUiStatusForProduct(productCode);
		}
		else
		{
			uiStatus = getSessionAccessFacade().getUiStatusForCartEntry(getCartItemByConfigId(configId));
		}
		return uiStatus;
	}

	protected Integer getCartEntryNumber(final AbstractOrderData orderData, final String configId) throws BusinessException
	{
		String cartItemKey = getAbstractOrderEntryLinkStrategy().getCartEntryForConfigId(configId);
		if (cartItemKey == null)
		{
			cartItemKey = getAbstractOrderEntryLinkStrategy().getCartEntryForDraftConfigId(configId);
		}
		if (cartItemKey != null)
		{
			final Integer[] entryNumber =
			{ null };
			getOrderEntry(cartItemKey, orderData).ifPresent(entry -> entryNumber[0] = entry.getEntryNumber());
			return entryNumber[0];
		}

		return null;
	}

	protected ConfigurationData loadNewConfiguration(final KBKeyData kbKey)
	{
		final ConfigurationData configData = configFacade.getConfiguration(kbKey);
		logLoadConfig(configData, "Load new ");

		initializeUiStatus(configData);

		return configData;
	}

	protected UiStatus initializeUiStatus(final ConfigurationData configData)
	{
		getUiStatusSync().setInitialStatus(configData);
		final UiStatus uiStatus = getUiStatusSync().extractUiStatusFromConfiguration(configData);
		setUiStatusForConfig(configData, uiStatus);

		return uiStatus;
	}

	protected ConfigurationData reloadConfigurationByKBKey(final KBKeyData kbKey, final UiStatus uiStatus)
	{
		//assumes that the product->cfg link exists
		final ConfigurationData configData = configFacade.getConfiguration(kbKey);
		return afterReloadConfiguration(configData, uiStatus);
	}

	protected ConfigurationData reloadConfigurationById(final String configId, final KBKeyData kbKey, final UiStatus uiStatus)
	{
		final ConfigurationData configData = new ConfigurationData();
		configData.setConfigId(configId);
		configData.setKbKey(kbKey);
		final ConfigurationData filledConfigData = configFacade.getConfiguration(configData);
		return afterReloadConfiguration(filledConfigData, uiStatus);
	}


	protected ConfigurationData afterReloadConfiguration(final ConfigurationData configData, UiStatus uiStatus)
	{
		this.logLoadConfig(configData, "Reload ");
		if (uiStatus == null)
		{
			uiStatus = initializeUiStatus(configData);
		}
		getUiStatusSync().applyUiStatusToConfiguration(configData, uiStatus);
		getUiStateHandler().compileGroupForDisplay(configData, uiStatus);
		return configData;
	}

	protected void logLoadConfig(final ConfigurationData configData, final String action)
	{
		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug(action + LOG_CONFIG_DATA + configData.getConfigId() + "']");
		}
	}

	protected ProductData populateProductData(final String productCode, final Model model, final HttpServletRequest request)
	{
		handleRequestContext(request, productCode);
		updatePageTitle(productCode, model);
		final ProductData productData = populateProductDetailForDisplay(productCode, model);

		model.addAttribute(new ReviewForm());
		final String metaKeywords = MetaSanitizerUtil.sanitizeKeywords(productData.getKeywords());
		final String metaDescription = MetaSanitizerUtil.sanitizeDescription(productData.getDescription());
		setUpMetaData(model, metaKeywords, metaDescription);

		return productData;
	}

	protected void populateCMSAttributes(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute(CMS_PAGE_TYPE, getPageType());
		final AbstractPageModel configPage = getCmsPageService().getPageForId(getPageId());
		if (LOGGER.isDebugEnabled() && configPage != null)
		{
			LOGGER.debug("Using CMS page: '" + configPage.getName() + "' [ '" + configPage.getUid() + "'] with PageType '"
					+ getPageType() + "'");
		}
		storeCmsPageInModel(model, configPage);
	}

	protected String getPageId()
	{
		return CMS_PC_PAGE_ID;
	}

	protected String getPageType()
	{
		return CMS_PC_PAGE_TYPE;
	}

	protected ProductData populateProductDetailForDisplay(final String productCode, final Model model)
	{
		final ProductData productData = getProductDataForProductCode(productCode);
		populateProductData(productData, model);
		return productData;
	}

	protected ProductData getProductDataForProductCode(final String productCode)
	{
		return getProductFacade().getProductForCodeAndOptions(productCode, Arrays.asList(ProductOption.BASIC, ProductOption.PRICE,
				ProductOption.SUMMARY, ProductOption.DESCRIPTION, ProductOption.GALLERY, ProductOption.STOCK));
	}

	protected void populateProductData(final ProductData productData, final Model model)
	{
		model.addAttribute("galleryImages", getGalleryImages(productData));
		model.addAttribute(SapproductconfigfrontendWebConstants.PRODUCT_ATTRIBUTE, productData);
	}

	protected List<Map<String, ImageData>> getGalleryImages(final ProductData productData)
	{
		if (CollectionUtils.isEmpty(productData.getImages()))
		{
			return Collections.emptyList();
		}

		final List<Map<String, ImageData>> galleryImages = new ArrayList<>();
		final List<ImageData> images = new ArrayList<>();
		for (final ImageData image : productData.getImages())
		{
			if (ImageDataType.GALLERY.equals(image.getImageType()))
			{
				images.add(image);
			}
		}

		if (CollectionUtils.isNotEmpty(images))
		{
			Collections.sort(images, (image1, image2) -> image1.getGalleryIndex().compareTo(image2.getGalleryIndex()));
			int currentIndex = images.get(0).getGalleryIndex().intValue();
			Map<String, ImageData> formats = new HashMap<>();
			for (final ImageData image : images)
			{
				if (currentIndex != image.getGalleryIndex().intValue())
				{
					galleryImages.add(formats);
					formats = new HashMap<>();
					currentIndex = image.getGalleryIndex().intValue();
				}
				formats.put(image.getFormat(), image);
			}
			if (!formats.isEmpty())
			{
				galleryImages.add(formats);
			}
		}
		return galleryImages;
	}


	protected void cleanUpSessionAttribute(final String productCode)
	{
		getProductLinkStrategy().removeConfigIdForProduct(productCode);
	}

	protected void removeNullCsticsFromGroup(final List<CsticData> dirtyList)
	{
		if (dirtyList == null)
		{
			return;
		}

		final List<CsticData> cleanList = new ArrayList<>(dirtyList.size());

		for (final CsticData data : dirtyList)
		{
			if (data.getName() != null && (data.getType() != UiType.READ_ONLY || data.isRetractTriggered()))
			{
				cleanList.add(data);
			}
		}

		dirtyList.clear();
		dirtyList.addAll(cleanList);
	}

	protected void removeNullCstics(final List<UiGroupData> groups)
	{
		if (groups == null)
		{
			return;
		}

		for (final UiGroupData group : groups)
		{
			removeNullCsticsFromGroup(group.getCstics());

			final List<UiGroupData> subGroups = group.getSubGroups();
			removeNullCstics(subGroups);
		}
	}

	protected void handleRequestContext(final HttpServletRequest request, final String productCode)
	{
		final RequestContextData requestContext = getRequestContextData(request);
		if (requestContext != null)
		{
			requestContext.setProduct(getProductService().getProductForCode(productCode));
		}
	}

	protected void logModelmetaData(final ConfigurationData configData)
	{
		if (LOGGER.isDebugEnabled())
		{
			final UiDataStats numbers = new UiDataStats();
			numbers.countCstics(configData.getGroups());

			LOGGER.debug("Modelstats of product '" + configData.getKbKey().getProductCode() + "' after Update: '" + numbers + "'");
		}
	}

	protected void logRequestMetaData(final ConfigurationData configData, final HttpServletRequest request)
	{
		if (LOGGER.isDebugEnabled())
		{
			final NumberFormat decFormat = DecimalFormat.getInstance(Locale.ENGLISH);
			LOGGER.debug("Update Configuration of product '" + configData.getKbKey().getProductCode() + "'");
			final StringBuilder sb = new StringBuilder().append("ContentLength=")
					.append(decFormat.format(request.getContentLength())).append("Bytes");
			if (request.getParameterMap() != null)
			{
				sb.append("; numberParams=");
				sb.append(decFormat.format(request.getParameterMap().size()));
			}
			else
			{
				sb.append("; parameterMap=null");
			}
			LOGGER.debug(sb.toString());
			final UiDataStats numbers = new UiDataStats();
			numbers.countCstics(configData.getGroups());

			LOGGER.debug(numbers);
		}
	}

	protected void updatePageTitle(final String productCode, final Model model)
	{
		storeContentPageTitleInModel(model, getPageTitleResolver().resolveProductPageTitle(productCode));
	}

	protected ConfigurationFacade getConfigFacade()
	{
		return configFacade;
	}

	protected ConfigurationCartIntegrationFacade getConfigCartFacade()
	{
		return configCartFacade;
	}

	protected SessionAccessFacade getSessionAccessFacade()
	{
		return sessionAccessFacade;
	}

	protected Validator getProductConfigurationValidator()
	{
		return productConfigurationValidator;
	}

	protected ConflictChecker getProductConfigurationConflictChecker()
	{
		return productConfigurationConflictChecker;
	}

	protected ProductFacade getProductFacade()
	{
		return productFacade;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	protected OrderEntryData getOrderEntry(final int entryNumber, final AbstractOrderData abstractOrder) throws BusinessException
	{
		final List<OrderEntryData> entries = abstractOrder.getEntries();
		if (entries == null)
		{
			throw new BusinessException("AbstractOrder is empty");
		}
		try
		{
			return entries.stream().filter(e -> e != null).filter(e -> e.getEntryNumber().intValue() == entryNumber).findAny().get();
		}
		catch (final NoSuchElementException e)
		{
			throw new BusinessException("AbstractOrder entry #" + entryNumber + " does not exist", e);
		}
	}

	protected Optional<OrderEntryData> getOrderEntry(final String cartItemKey, final AbstractOrderData abstractOrder)
			throws BusinessException
	{
		final List<OrderEntryData> entries = abstractOrder.getEntries();
		if (CollectionUtils.isEmpty(entries))
		{
			throw new BusinessException("AbstractOrder is empty");
		}

		return entries.stream().filter(e -> e != null && e.getItemPK() != null).filter(e -> e.getItemPK().equals(cartItemKey))
				.findAny();
	}

	protected String getProductCodeForCartItem(final String cartItemKey) throws BusinessException
	{
		final String[] productCode =
		{ null };
		getOrderEntry(cartItemKey, getCartFacade().getSessionCart())
				.ifPresent(entry -> productCode[0] = entry.getProduct().getCode());
		return productCode[0];
	}

	protected ProductConfigureBreadcrumbBuilder getBreadcrumbBuilder()
	{
		return breadcrumbBuilder;
	}

	/**
	 * @param configFacade
	 *           CPQ facade
	 */
	public void setConfigFacade(final ConfigurationFacade configFacade)
	{
		this.configFacade = configFacade;
	}

	/**
	 * @param configCartFacade
	 *           CPQ cart integration facade
	 */
	public void setConfigCartFacade(final ConfigurationCartIntegrationFacade configCartFacade)
	{
		this.configCartFacade = configCartFacade;
	}

	/**
	 * @param sessionAccessFacade
	 *           CPQ session cache access
	 */
	public void setSessionAccessFacade(final SessionAccessFacade sessionAccessFacade)
	{
		this.sessionAccessFacade = sessionAccessFacade;
	}

	protected ConfigurationAbstractOrderEntryLinkStrategy getAbstractOrderEntryLinkStrategy()
	{
		return configurationAbstractOrderEntryLinkStrategy;
	}

	public void setAbstractOrderEntryLinkStrategy(
			final ConfigurationAbstractOrderEntryLinkStrategy configurationAbstractOrderEntryLinkStrategy)
	{
		this.configurationAbstractOrderEntryLinkStrategy = configurationAbstractOrderEntryLinkStrategy;
	}

	/**
	 * @param productConfigurationValidator
	 *           CPQ validator
	 */
	public void setProductConfigurationValidator(final Validator productConfigurationValidator)
	{
		this.productConfigurationValidator = productConfigurationValidator;
	}

	/**
	 * @param productConfigurationConflictChecker
	 *           status and UI error handling&checking
	 */
	public void setProductConfigurationConflictChecker(final ConflictChecker productConfigurationConflictChecker)
	{
		this.productConfigurationConflictChecker = productConfigurationConflictChecker;
	}

	/**
	 * @param productFacade
	 *           for accessing product master data
	 */
	public void setProductFacade(final ProductFacade productFacade)
	{
		this.productFacade = productFacade;
	}

	/**
	 * @param productService
	 *           for accessing product related service
	 */
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	/**
	 * @param productConfigurationBreadcrumbBuilder
	 *           for building UI breadcrumbs
	 */
	public void setBreadcrumbBuilder(final ProductConfigureBreadcrumbBuilder productConfigurationBreadcrumbBuilder)
	{
		this.breadcrumbBuilder = productConfigurationBreadcrumbBuilder;
	}


	protected UiTrackingRecorder getUiRecorder()
	{
		return uiRecorder;
	}

	/**
	 * @param uiRecorder
	 *           triggering CPQ tracking
	 */
	public void setUiRecorder(final UiTrackingRecorder uiRecorder)
	{
		this.uiRecorder = uiRecorder;
	}

	protected boolean isConfigRemoved(final String productCode)
	{
		final boolean isConfigRemoved = getProductLinkStrategy().getConfigIdForProduct(productCode) == null;

		if (LOGGER.isDebugEnabled())
		{
			LOGGER.debug("Is configuration removed: '" + isConfigRemoved + "'");
		}

		return isConfigRemoved;
	}

	/**
	 * @return configuration error handler
	 */
	public ConfigErrorHandler getConfigurationErrorHandler()
	{
		return configurationErrorHandler;
	}

	/**
	 * @param configurationErrorHandler
	 *           for handling configuration errors
	 *
	 */
	public void setConfigurationErrorHandler(final ConfigErrorHandler configurationErrorHandler)
	{
		this.configurationErrorHandler = configurationErrorHandler;
	}

	protected UiStateHandler getUiStateHandler()
	{
		return uiStateHandler;
	}

	/**
	 * @param uiStateHandler
	 *           UI state handler
	 */
	public void setUiStateHandler(final UiStateHandler uiStateHandler)
	{
		this.uiStateHandler = uiStateHandler;
	}

	protected UiStatusSync getUiStatusSync()
	{
		return uiStatusSync;
	}

	/**
	 * @param uiStatusSync
	 *           UI status sync
	 */
	public void setUiStatusSync(final UiStatusSync uiStatusSync)
	{
		this.uiStatusSync = uiStatusSync;
	}

	protected ConfigurationProductLinkStrategy getProductLinkStrategy()
	{
		return configurationProductLinkStrategy;
	}

	public void setProductLinkStrategy(final ConfigurationProductLinkStrategy configurationProductLinkStrategy)
	{
		this.configurationProductLinkStrategy = configurationProductLinkStrategy;
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

}
