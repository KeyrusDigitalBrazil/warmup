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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSPageTypeModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CatalogPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cmsfacades.common.validator.CompositeValidator;
import de.hybris.platform.cmsfacades.common.validator.FacadeValidationService;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.data.PageTypeData;
import de.hybris.platform.cmsfacades.exception.ValidationError;
import de.hybris.platform.cmsfacades.exception.ValidationException;
import de.hybris.platform.cmsfacades.pages.service.PageInitializer;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolver;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverType;
import de.hybris.platform.cmsfacades.pages.service.PageVariationResolverTypeRegistry;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.keygenerator.KeyGenerator;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.transaction.PlatformTransactionManager;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPageFacadeTest
{
	private static final String PAGE_UID = "page-uid";
	private static final String UNIQUE_KEY = "uniqueKey";
	private static final String DEFAULT_PAGE_UID = "default-page-uid";
	private static final String INVALID = "invalid";

	@InjectMocks
	private DefaultPageFacade defaultPageFacade;
	@Mock
	private CMSAdminPageService adminPageService;
	@Mock
	private PageInitializer pageInitializer;
	@Mock
	private ModelService modelService;
	@Mock
	private FacadeValidationService facadeValidationService;
	@Mock
	private KeyGenerator keyGenerator;
	@Mock
	private Map<Class<?>, CompositeValidator> cmsCreatePageValidatorFactory;
	@Mock
	private Map<Class<?>, CompositeValidator> cmsUpdatePageValidatorFactory;
	@Mock
	private Map<Class<?>, AbstractPopulatingConverter<AbstractPageData, AbstractPageModel>> pageDataPopulatorFactory;
	@Mock
	private Map<Class<?>, AbstractPopulatingConverter<AbstractPageModel, AbstractPageData>> pageModelConverterFactory;
	@Mock
	private Converter<CMSPageTypeModel, PageTypeData> pageTypeModelConverter;
	@Mock
	private ContentPageModel contentPageModel;
	@Mock
	private ProductPageModel productPageModel;
	@Mock
	private PageVariationResolverTypeRegistry registry;
	@Mock
	private PageVariationResolverType resolverType;
	@Mock
	private PageVariationResolver<AbstractPageModel> pageVariationResolver;
	@Mock
	private Comparator<AbstractPageData> cmsPageComparator;
	@Mock
	private AbstractPageData pageData1;
	@Mock
	private AbstractPageData pageData2;
	@Mock
	private AbstractPopulatingConverter<AbstractPageData, AbstractPageModel> dataConverter;
	@Mock
	private AbstractPopulatingConverter<AbstractPageModel, AbstractPageData> modelConverter;
	@Mock
	private CompositeValidator validator;
	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;
	@Mock
	private PlatformTransactionManager transactionManager;


	@Before
	public void setup()
	{
		final Map<String, List<String>> cmsItemSearchTypeBlacklistMap = new HashMap<>();
		cmsItemSearchTypeBlacklistMap.put(AbstractPageModel._TYPECODE, Arrays.asList(CatalogPageModel._TYPECODE));
		defaultPageFacade.setCmsItemSearchTypeBlacklistMap(cmsItemSearchTypeBlacklistMap);

		doReturn(UNIQUE_KEY).when(keyGenerator).generate();

		doReturn(dataConverter).when(pageDataPopulatorFactory).computeIfAbsent(any(), any());
		doReturn(contentPageModel).when(dataConverter).convert(pageData1);
		doReturn(productPageModel).when(dataConverter).convert(pageData2);

		doReturn(modelConverter).when(pageModelConverterFactory).computeIfAbsent(any(), any());
		doReturn(pageData1).when(modelConverter).convert(contentPageModel);
		doReturn(pageData2).when(modelConverter).convert(productPageModel);

		doReturn(modelConverter).when(pageModelConverterFactory).get(contentPageModel.getClass());
		doReturn(modelConverter).when(pageModelConverterFactory).get(productPageModel.getClass());

		doReturn(validator).when(cmsCreatePageValidatorFactory).get(any());
		doReturn(validator).when(cmsUpdatePageValidatorFactory).get(any());

		doNothing().when(facadeValidationService).validate(validator, pageData1);
	}

	@Test
	public void findAllPagesReturnTransformedCollectionOfAllSupportedPagesOrderByNameAscending()
	{
		// Setup
		when(pageData1.getName()).thenReturn("Bname");
		when(pageData2.getName()).thenReturn("Aname");

		final Collection<AbstractPageModel> pages = asList(contentPageModel, productPageModel);
		when(adminPageService.getAllPages()).thenReturn(pages);

		when(cmsPageComparator.compare(pageData2, pageData1)).thenReturn(-20);

		when(pageModelConverterFactory.containsKey(any())).thenReturn(Boolean.TRUE).thenReturn(Boolean.TRUE);

		// Act
		final List<AbstractPageData> results = defaultPageFacade.findAllPages();

		// Assert
		assertThat(results.size(), is(equalTo(2)));
		assertThat(results, contains(pageData2, pageData1));
	}

	@Test
	public void findPagesReturnsEmptyList()
	{
		// Setup
		when(adminPageService.getAllPages()).thenReturn(Collections.<AbstractPageModel> emptyList());

		// Act
		final List<AbstractPageData> results = defaultPageFacade.findAllPages();

		// Assert
		assertThat(results.size(), is(0));
		verify(modelConverter, never()).convert(any(AbstractPageModel.class));
	}

	@Test
	public void shouldGetPageByUid() throws CMSItemNotFoundException
	{
		// Setup
		when(adminPageService.getPageForIdFromActiveCatalogVersion(PAGE_UID)).thenReturn(contentPageModel);

		// Act
		final AbstractPageData pageByUid = defaultPageFacade.getPageByUid(PAGE_UID);

		// Assert
		verify(adminPageService).getPageForIdFromActiveCatalogVersion(PAGE_UID);
		verify(modelConverter).convert(contentPageModel);
		assertNotNull(pageByUid);
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldFailGetPageByUid_UnknownIdentifier() throws CMSItemNotFoundException
	{
		// Setup
		doThrow(UnknownIdentifierException.class).when(adminPageService).getPageForIdFromActiveCatalogVersion(PAGE_UID);

		// Act
		defaultPageFacade.getPageByUid(PAGE_UID);
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldFailGetPageByUid_AmbiguousIdentifier() throws CMSItemNotFoundException
	{
		// Setup
		doThrow(AmbiguousIdentifierException.class).when(adminPageService).getPageForIdFromActiveCatalogVersion(PAGE_UID);

		// Act
		defaultPageFacade.getPageByUid(PAGE_UID);
	}

	@Test
	public void shouldFindAllSupportedPageTypes()
	{
		// Setup
		final CMSPageTypeModel catalogPageType = new CMSPageTypeModel();
		final CMSPageTypeModel contentPageType = new CMSPageTypeModel();
		final CMSPageTypeModel productPageType = new CMSPageTypeModel();
		catalogPageType.setCode(CatalogPageModel._TYPECODE);
		contentPageType.setCode(ContentPageModel._TYPECODE);
		productPageType.setCode(ProductPageModel._TYPECODE);
		when(adminPageService.getAllPageTypes()).thenReturn(Arrays.asList(catalogPageType, contentPageType, productPageType));

		final PageTypeData pageType = new PageTypeData();
		doReturn(pageType).when(pageTypeModelConverter).convert(contentPageType);
		doReturn(pageType).when(pageTypeModelConverter).convert(productPageType);

		// Act
		final List<PageTypeData> pageTypes = defaultPageFacade.findAllPageTypes();

		// Assert
		assertThat(pageTypes.size(), is(2));
		verify(pageTypeModelConverter, times(2)).convert(any());
	}

	@Test
	public void shouldNotFindAnySupportedPageTypes()
	{
		// Setup
		final CMSPageTypeModel catalogPageType = new CMSPageTypeModel();
		catalogPageType.setCode(CatalogPageModel._TYPECODE);
		when(adminPageService.getAllPageTypes()).thenReturn(Arrays.asList(catalogPageType));

		// Act
		final List<PageTypeData> pageTypes = defaultPageFacade.findAllPageTypes();

		// Assert
		assertThat(pageTypes, empty());
		verifyZeroInteractions(pageTypeModelConverter);
	}

	@Test
	public void shouldFindFallbackPages() throws CMSItemNotFoundException
	{
		// Setup
		final ContentPageModel defaultPage = new ContentPageModel();
		defaultPage.setUid(DEFAULT_PAGE_UID);

		when(contentPageModel.getItemtype()).thenReturn(ContentPageModel._TYPECODE);

		when(adminPageService.getPageForIdFromActiveCatalogVersion(PAGE_UID)).thenReturn(contentPageModel);
		when(registry.getPageVariationResolverType(ContentPageModel._TYPECODE)).thenReturn(Optional.of(resolverType));
		when(resolverType.getResolver()).thenReturn(pageVariationResolver);
		when(pageVariationResolver.findDefaultPages(contentPageModel)).thenReturn(Arrays.asList(defaultPage));

		// Act
		final List<String> fallbacks = defaultPageFacade.findFallbackPages(PAGE_UID);

		// Assert
		assertThat(fallbacks.size(), is(1));
		assertThat(fallbacks, hasItem(DEFAULT_PAGE_UID));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldFailFindAllFallbackPages_InvalidPageId() throws CMSItemNotFoundException
	{
		// Setup
		doThrow(new UnknownIdentifierException("invalid uid")).when(adminPageService).getPageForIdFromActiveCatalogVersion(INVALID);

		// Act
		defaultPageFacade.findFallbackPages(INVALID);
	}

	@Test
	public void shouldFindVariationPages() throws CMSItemNotFoundException
	{
		// Setup
		final ContentPageModel defaultPage = new ContentPageModel();
		defaultPage.setUid(DEFAULT_PAGE_UID);
		when(contentPageModel.getUid()).thenReturn(PAGE_UID);

		when(adminPageService.getPageForIdFromActiveCatalogVersion(DEFAULT_PAGE_UID)).thenReturn(defaultPage);
		when(registry.getPageVariationResolverType(ContentPageModel._TYPECODE)).thenReturn(Optional.of(resolverType));
		when(resolverType.getResolver()).thenReturn(pageVariationResolver);
		when(pageVariationResolver.findVariationPages(defaultPage)).thenReturn(Arrays.asList(contentPageModel));

		// Act
		final List<String> variations = defaultPageFacade.findVariationPages(DEFAULT_PAGE_UID);

		// Assert
		assertThat(variations.size(), is(1));
		assertThat(variations, hasItem(PAGE_UID));
	}

	@Test
	public void shouldFindAllFallbackPagesForContentPageType()
	{
		// Setup
		final ContentPageModel defaultPage = mock(ContentPageModel.class);
		when(defaultPage.getUid()).thenReturn(PAGE_UID);
		when(contentPageModel.getUid()).thenReturn(PAGE_UID);

		when(pageData1.getName()).thenReturn("Bname");
		when(pageData2.getName()).thenReturn("Aname");

		when(registry.getPageVariationResolverType(ContentPageModel._TYPECODE)).thenReturn(Optional.of(resolverType));
		when(resolverType.getResolver()).thenReturn(pageVariationResolver);
		when(pageVariationResolver.findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE))
				.thenReturn(Arrays.asList(contentPageModel, defaultPage));

		when(modelConverter.convert(defaultPage)).thenReturn(pageData2);
		when(cmsPageComparator.compare(pageData2, pageData1)).thenReturn(-20);


		final ItemData itemData = new ItemData();
		itemData.setItemId(PAGE_UID);
		when(uniqueItemIdentifierService.getItemData(any(AbstractPageModel.class))).thenReturn(Optional.of(itemData));

		// Act
		final List<AbstractPageData> contentPages = defaultPageFacade.findPagesByType(ContentPageModel._TYPECODE, Boolean.TRUE);

		// Assert
		assertThat(contentPages.size(), is(2));
		assertThat(contentPages, contains(pageData2, pageData1));
	}

	@Test(expected = ValidationException.class)
	public void shouldFailFindAllFallbackPagesForContentPageType_InvalidTypeCode()
	{
		// Setup
		doThrow(new ValidationException(new ValidationError("invalid typecode"))).when(facadeValidationService).validate(any(),
				any());

		// Act
		defaultPageFacade.findPagesByType(CMSParagraphComponentModel._TYPECODE, Boolean.TRUE);
	}

}
