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
package de.hybris.platform.cmsfacades.pages.impl;

import de.hybris.platform.cms2.common.annotations.HybrisDeprecation;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.data.PageTypeData;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;
import de.hybris.platform.cmsfacades.rendering.PageRenderingService;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Validator;


/**
 * Default implementation of {@link PageFacade}.
 */
public class DefaultPageFacade implements PageFacade
{
	private CMSAdminPageService adminPageService;
	private FacadeValidationService facadeValidationService;
	private PageRenderingService pageRenderingService;

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	private Map<Class<?>, AbstractPopulatingConverter<AbstractPageData, AbstractPageModel>> pageDataPopulatorFactory;
	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	private Map<Class<?>, AbstractPopulatingConverter<AbstractPageModel, AbstractPageData>> pageModelConverterFactory;

	private PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry;
	private Converter<CMSPageTypeModel, PageTypeData> pageTypeModelConverter;
	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	private Comparator<AbstractPageData> cmsPageComparator;
	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	private Validator cmsFindVariationPageValidator;
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	private Map<String, List<String>> cmsItemSearchTypeBlacklistMap;

	@Override
	public AbstractPageData getPageData(final String pageType, final String pageLabelOrId, final String code)
			throws CMSItemNotFoundException
	{
		return getPageRenderingService().getPageRenderingData(pageType, pageLabelOrId, code);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@Override
	public List<AbstractPageData> findAllPages()
	{
		final List<String> blacklistedPageTypes = getCmsItemSearchTypeBlacklistMap().get(AbstractPageModel._TYPECODE);
		final Predicate<AbstractPageModel> isNotBlacklistedPageType = pageModel -> {
			return !blacklistedPageTypes.contains(pageModel.getItemtype());
		};

		return getAdminPageService().getAllPages().stream().filter(isNotBlacklistedPageType)
				.filter(model -> getPageModelConverterFactory().containsKey(model.getClass()))
				.map(model -> getPageModelConverter(model.getClass()).convert(model)).sorted(getCmsPageComparator())
				.collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PageTypeData> findAllPageTypes()
	{
		final List<String> blacklistedPageTypes = getCmsItemSearchTypeBlacklistMap().get(AbstractPageModel._TYPECODE);
		final Predicate<CMSPageTypeModel> isNotBlacklistedPageType = pageTypeModel -> {
			return !blacklistedPageTypes.contains(pageTypeModel.getCode());
		};

		return getAdminPageService().getAllPageTypes().stream().filter(isNotBlacklistedPageType) //
				.map(pageType -> getPageTypeModelConverter().convert(pageType)) //
				.collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@Override
	public List<AbstractPageData> findPagesByType(final String typeCode, final Boolean isDefaultPage)
	{
		final AbstractPageData pageData = new AbstractPageData();
		pageData.setTypeCode(typeCode);
		pageData.setDefaultPage(isDefaultPage);

		getFacadeValidationService().validate(getCmsFindVariationPageValidator(), pageData);

		return getPageVariationResolver(typeCode).findPagesByType(typeCode, isDefaultPage).stream() //
				.map(model -> {
					final AbstractPageData abstractPageData = getPageModelConverter(model.getClass()).convert(model);
					getUniqueItemIdentifierService().getItemData(model).ifPresent(itemData -> {
						abstractPageData.setUuid(itemData.getItemId());
					});
					return abstractPageData;
				}).sorted(getCmsPageComparator()).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> findVariationPages(final String pageId) throws CMSItemNotFoundException
	{
		final AbstractPageModel page = getPageModelById(pageId);
		return getPageVariationResolver(page.getItemtype()).findVariationPages(page).stream().map(pageData -> pageData.getUid())
				.collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> findFallbackPages(final String pageId) throws CMSItemNotFoundException
	{
		final AbstractPageModel page = getPageModelById(pageId);
		return getPageVariationResolver(page.getItemtype()).findDefaultPages(page).stream().map(pageData -> pageData.getUid())
				.collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@Override
	public AbstractPageData getPageByUid(final String uid) throws CMSItemNotFoundException
	{
		final AbstractPageModel pageModel = getPageModelById(uid);
		return getPageModelConverter(pageModel.getClass()).convert(pageModel);
	}

	protected AbstractPageModel getPageModelById(final String pageId) throws CMSItemNotFoundException
	{
		try
		{
			return getAdminPageService().getPageForIdFromActiveCatalogVersion(pageId);
		}
		catch (UnknownIdentifierException | AmbiguousIdentifierException e)
		{
			throw new CMSItemNotFoundException("Cannot find page with uid [" + pageId + "].", e);
		}
	}

	protected PageVariationResolver<AbstractPageModel> getPageVariationResolver(final String typeCode)
	{
		return getPageVariationResolverTypeRegistry().getPageVariationResolverType(typeCode).get().getResolver();
	}

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	protected AbstractPopulatingConverter<AbstractPageData, AbstractPageModel> getPageDataConverter(final Class<?> pageClass)
	{
		return getPageDataPopulatorFactory().computeIfAbsent(pageClass, k -> {
			throw new ConversionException(String.format("Converter not found for CMS Page Data [%s]", pageClass.getName()));
		});
	}

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	protected AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> getPageModelConverter(final Class<?> pageClass)
	{
		return getPageModelConverterFactory().computeIfAbsent(pageClass, k -> {
			throw new ConversionException(String.format("Converter not found for CMS Page Model [%s]", pageClass.getName()));
		});
	}

	public CMSAdminPageService getAdminPageService()
	{
		return adminPageService;
	}

	@Required
	public void setAdminPageService(final CMSAdminPageService adminPageService)
	{
		this.adminPageService = adminPageService;
	}

	protected FacadeValidationService getFacadeValidationService()
	{
		return facadeValidationService;
	}

	@Required
	public void setFacadeValidationService(final FacadeValidationService facadeValidationService)
	{
		this.facadeValidationService = facadeValidationService;
	}

	protected Map<Class<?>, AbstractPopulatingConverter<AbstractPageData, AbstractPageModel>> getPageDataPopulatorFactory()
	{
		return pageDataPopulatorFactory;
	}

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@Required
	public void setPageDataPopulatorFactory(
			final Map<Class<?>, AbstractPopulatingConverter<AbstractPageData, AbstractPageModel>> pageDataPopulatorFactory)
	{
		this.pageDataPopulatorFactory = pageDataPopulatorFactory;
	}

	protected Map<Class<?>, AbstractPopulatingConverter<AbstractPageModel, AbstractPageData>> getPageModelConverterFactory()
	{
		return pageModelConverterFactory;
	}

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@Required
	public void setPageModelConverterFactory(
			final Map<Class<?>, AbstractPopulatingConverter<AbstractPageModel, AbstractPageData>> pageModelConverterFactory)
	{
		this.pageModelConverterFactory = pageModelConverterFactory;
	}

	protected Converter<CMSPageTypeModel, PageTypeData> getPageTypeModelConverter()
	{
		return pageTypeModelConverter;
	}

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@Required
	public void setPageTypeModelConverter(final Converter<CMSPageTypeModel, PageTypeData> pageTypeModelConverter)
	{
		this.pageTypeModelConverter = pageTypeModelConverter;
	}

	protected Validator getCmsFindVariationPageValidator()
	{
		return cmsFindVariationPageValidator;
	}

	/**
	 * @deprecated since 6.6
	 */
	@Deprecated
	@HybrisDeprecation(sinceVersion = "6.6")
	@Required
	public void setCmsFindVariationPageValidator(final Validator cmsFindVariationPageValidator)
	{
		this.cmsFindVariationPageValidator = cmsFindVariationPageValidator;
	}


	protected PageVariationResolverTypeRegistry getPageVariationResolverTypeRegistry()
	{
		return pageVariationResolverTypeRegistry;
	}

	@Required
	public void setPageVariationResolverTypeRegistry(final PageVariationResolverTypeRegistry pageVariationResolverTypeRegistry)
	{
		this.pageVariationResolverTypeRegistry = pageVariationResolverTypeRegistry;
	}

	protected Comparator<AbstractPageData> getCmsPageComparator()
	{
		return cmsPageComparator;
	}

	@Required
	public void setCmsPageComparator(final Comparator<AbstractPageData> cmsPageComparator)
	{
		this.cmsPageComparator = cmsPageComparator;
	}

	protected Map<String, List<String>> getCmsItemSearchTypeBlacklistMap()
	{
		return cmsItemSearchTypeBlacklistMap;
	}

	protected UniqueItemIdentifierService getUniqueItemIdentifierService()
	{
		return uniqueItemIdentifierService;
	}

	@Required
	public void setUniqueItemIdentifierService(final UniqueItemIdentifierService uniqueItemIdentifierService)
	{
		this.uniqueItemIdentifierService = uniqueItemIdentifierService;
	}

	@Required
	public void setCmsItemSearchTypeBlacklistMap(final Map<String, List<String>> cmsItemSearchTypeBlacklistMap)
	{
		this.cmsItemSearchTypeBlacklistMap = cmsItemSearchTypeBlacklistMap;
	}

	protected PageRenderingService getPageRenderingService()
	{
		return pageRenderingService;
	}

	@Required
	public void setPageRenderingService(final PageRenderingService pageRenderingService)
	{
		this.pageRenderingService = pageRenderingService;
	}
}
