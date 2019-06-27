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
package de.hybris.platform.cms2.servicelayer.services.impl;

import static de.hybris.platform.cms2.servicelayer.services.impl.AbstractCMSService.CURRENTCATALOGVERSION;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.data.PagePreviewCriteriaData;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.exceptions.CMSVersionNotFoundException;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.CategoryPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSContentSlotDao;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;
import de.hybris.platform.cms2.servicelayer.daos.CMSVersionDao;
import de.hybris.platform.cms2.servicelayer.data.CMSDataFactory;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.data.RestrictionData;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.cms2.version.service.CMSVersionService;
import de.hybris.platform.cms2.version.service.CMSVersionSessionContextProvider;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCMSPageServiceTest
{
	@InjectMocks
	private DefaultCMSPageService cmsPageService;
	@Mock
	private CMSContentSlotDao cmsContentSlotDaoMock;
	@Mock
	private PageTemplateModel pageTemplateModelMock;
	@Mock
	private AbstractPageModel pageModelMock;
	@Mock
	private CMSPageDao cmsPageDao;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CMSRestrictionService cmsRestrictionService;
	@Mock
	private TypeService typeService;
	@Mock
	private CMSDataFactory cmsDataFactory;
	@Mock
	private ProductService productService;
	@Mock
	private CategoryService categoryService;
	@Mock
	private SessionService sessionService;
	@Mock
	private CMSVersionService cmsVersionService;
	@Mock
	private CMSVersionDao cmsVersionDao;
	@Mock
	private CMSVersionSessionContextProvider cmsVersionSessionContextProvider;
	@Mock
	private Comparator<AbstractPageModel> cmsItemCatalogLevelComparator;

	private final String PAGE_LABEL = "fakeLabel";
	private final String PAGE_ID = "fakeId";
	private final String PRODUCT_CODE = "fakeProductCode";
	private final String CATEGORY_NAME = "fakeCategoryName";
	private final String CATEGORY_CODE = "fakeCategoryCode";

	@Mock
	private CatalogVersionModel catalogVersionModel1;
	@Mock
	private CatalogVersionModel catalogVersionModel2;
	@Mock
	private ContentPageModel contentPageModel1;
	@Mock
	private ContentPageModel contentPageModel2;
	@Mock
	private ProductPageModel productPageModel1;
	@Mock
	private ProductPageModel productPageModel2;
	@Mock
	private CategoryPageModel categoryPageModel1;
	@Mock
	private CategoryPageModel categoryPageModel2;
	@Mock
	private ProductModel productModel;
	@Mock
	private CategoryModel categoryModel;
	@Mock
	private RestrictionData restrictionData;
	@Mock
	private ContentSlotData contentSlotData;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private CMSVersionModel cmsVersionModel;

	@Mock
	private ComposedTypeModel composedTypeModel;

	private final Collection<CatalogVersionModel> sessionCatalogVersions = asList(catalogVersionModel1, catalogVersionModel2);

	final PagePreviewCriteriaData pagePreviewCriteria = new PagePreviewCriteriaData();
	final ContentPageModel versionedPage = new ContentPageModel();

	public class SomePageModel extends AbstractPageModel
	{

	}

	@Before
	public void setUp() throws Exception
	{
		cmsPageService = new DefaultCMSPageService();
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSPageService#getContentSlotsForPageTemplate(de.hybris.platform.cms2.model.pages.PageTemplateModel)}
	 * .
	 */
	@Test
	public void testShouldCallCmsContentSlotDaoAndFindAllContentPagesByCatalogVersion()
	{
		// given
		when(cmsContentSlotDaoMock.findAllContentSlotRelationsByPageTemplate(pageTemplateModelMock))
				.thenReturn(Collections.EMPTY_LIST);

		// when
		cmsPageService.getContentSlotsForPageTemplate(pageTemplateModelMock);

		verify(cmsContentSlotDaoMock, times(1)).findAllContentSlotRelationsByPageTemplate(pageTemplateModelMock);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSPageService#getContentSlotsForPageTemplate(de.hybris.platform.cms2.model.pages.PageTemplateModel)}
	 * .
	 */
	@Test
	public void testShouldCallCmsContentSlotDaoAndGetOwnContentSlotsForPage()
	{
		// given
		when(cmsContentSlotDaoMock.findAllContentSlotRelationsByPage(pageModelMock)).thenReturn(Collections.EMPTY_LIST);

		// when
		cmsPageService.getOwnContentSlotsForPage(pageModelMock);

		verify(cmsContentSlotDaoMock, times(1)).findAllContentSlotRelationsByPage(pageModelMock);
	}

	@Test
	public void testShouldReturnFrontendTemplateName()
	{
		// given
		when(pageTemplateModelMock.getFrontendTemplateName()).thenReturn("FooBar");

		// when
		final String frontendTemplateName = cmsPageService.getFrontendTemplateName(pageTemplateModelMock);

		// then
		assertThat(frontendTemplateName, equalTo("FooBar"));
		verify(pageTemplateModelMock, times(2)).getFrontendTemplateName();
	}

	@Test
	public void testShouldReturnThePageByLabelWhenOnlyOnePageWithoutRestrictionsIsFound() throws Exception
	{
		// given
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE_LABEL, sessionCatalogVersions, asList(CmsPageStatus.ACTIVE)))
				.thenReturn(asList(contentPageModel1));
		when(contentPageModel1.getRestrictions()).thenReturn(Collections.EMPTY_LIST);

		// when
		final ContentPageModel resultContentPageModel = cmsPageService.getPageForLabel(PAGE_LABEL);

		// then
		assertThat(contentPageModel1, equalTo(resultContentPageModel));
	}

	@Test
	public void testShouldReturnLastPageByLabelWhenThereAreMoreThenOnePage() throws Exception
	{
		// given
		final Collection<AbstractPageModel> pages = asList(contentPageModel1, contentPageModel2);

		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE_LABEL, sessionCatalogVersions, asList(CmsPageStatus.ACTIVE)))
				.thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(pages);

		// when
		final ContentPageModel contentPageModel = cmsPageService.getPageForLabel(PAGE_LABEL);

		// then
		assertThat(contentPageModel, equalTo(contentPageModel2));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void testShouldThrowExceptionWhenNoPageWithLabelFound() throws Exception
	{
		// given
		final Collection<AbstractPageModel> pages = asList(contentPageModel1, contentPageModel2);

		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE_LABEL, sessionCatalogVersions, asList(CmsPageStatus.ACTIVE)))
				.thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(Collections.EMPTY_LIST);

		// when
		cmsPageService.getPageForLabel(PAGE_LABEL);
	}

	@Test
	public void testShouldReturnFirstPageByIdWhenPageByLabelHasNotBeenFound() throws Exception
	{
		// given
		final List<AbstractPageModel> pages = asList(contentPageModel1, contentPageModel2);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE_LABEL, sessionCatalogVersions, asList(CmsPageStatus.ACTIVE)))
				.thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(Collections.EMPTY_LIST);
		when(cmsPageDao.findPagesByIdAndPageStatuses(PAGE_ID, sessionCatalogVersions, asList(CmsPageStatus.ACTIVE)))
				.thenReturn(pages);

		// when
		final ContentPageModel contentPageModel = cmsPageService.getPageForLabelOrId(PAGE_ID);

		// then
		assertThat(contentPageModel, equalTo(contentPageModel1));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void testShouldThrowExceptionWhenNoPageWithIdFound() throws Exception
	{
		// given
		final List<AbstractPageModel> pages = asList(contentPageModel1, contentPageModel2);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE_LABEL, sessionCatalogVersions, asList(CmsPageStatus.ACTIVE)))
				.thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(Collections.EMPTY_LIST);
		when(cmsPageDao.findPagesByIdAndPageStatuses(PAGE_ID, sessionCatalogVersions, asList(CmsPageStatus.ACTIVE)))
				.thenReturn(Collections.EMPTY_LIST);

		// when
		cmsPageService.getPageForLabelOrId(PAGE_ID);
	}

	@Test
	public void testShouldReturnPageForProductWhenOnlyOnePageWithoutRestrictionsFound() throws Exception
	{
		// given
		when(typeService.getComposedTypeForCode(ProductPageModel._TYPECODE)).thenReturn(composedTypeModel);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(asList(productPageModel1));
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);

		// when
		final ProductPageModel productPageModel = cmsPageService.getPageForProduct(productModel);

		// then
		assertThat(productPageModel, equalTo(productPageModel1));
	}

	@Test
	public void testShouldReturnLastProductPageByProductWhenThereAreMoreThenOnePage() throws Exception
	{
		// given
		final List<AbstractPageModel> pages = asList(productPageModel1, productPageModel2);
		when(typeService.getComposedTypeForCode(ProductPageModel._TYPECODE)).thenReturn(composedTypeModel);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(asList(productPageModel1, productPageModel2));
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		when(cmsDataFactory.createRestrictionData(productModel)).thenReturn(restrictionData);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, restrictionData)).thenReturn(pages);

		// when
		final ProductPageModel productPageModel = cmsPageService.getPageForProduct(productModel);

		// then
		assertThat(productPageModel, equalTo(productPageModel2));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void testShouldThrowExceptionWhenNoProductPageFound() throws Exception
	{
		// given
		final List<AbstractPageModel> pages = asList(productPageModel1, productPageModel2);
		when(typeService.getComposedTypeForCode(ProductPageModel._TYPECODE)).thenReturn(composedTypeModel);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(asList(productPageModel1, productPageModel2));
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		when(cmsDataFactory.createRestrictionData(productModel)).thenReturn(restrictionData);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, restrictionData)).thenReturn(Collections.EMPTY_LIST);

		// when
		cmsPageService.getPageForProduct(productModel);
	}

	@Test
	public void shouldReturnProductPageByProductCode() throws Exception
	{
		// given
		when(productService.getProductForCode(PRODUCT_CODE)).thenReturn(productModel);

		final List<AbstractPageModel> pages = asList(productPageModel1, productPageModel2);
		when(typeService.getComposedTypeForCode(ProductPageModel._TYPECODE)).thenReturn(composedTypeModel);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(asList(productPageModel1, productPageModel2));
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		when(cmsDataFactory.createRestrictionData(productModel)).thenReturn(restrictionData);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, restrictionData)).thenReturn(pages);

		// when
		final ProductPageModel productPageModel = cmsPageService.getPageForProductCode(PRODUCT_CODE);

		// then
		assertThat(productPageModel, equalTo(productPageModel2));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldThrowExceptionWhenProductCodeDoesNotExists() throws Exception
	{
		// given
		when(productService.getProductForCode(PRODUCT_CODE)).thenThrow(new UnknownIdentifierException("fakeMessage"));

		// when
		cmsPageService.getPageForProductCode(PRODUCT_CODE);
	}

	@Test
	public void shouldReturnPageForCategoryWhenOnlyOnePageWithoutRestrictionsFound() throws Exception
	{
		// given
		when(typeService.getComposedTypeForCode(CategoryPageModel._TYPECODE)).thenReturn(composedTypeModel);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(asList(categoryPageModel1));
		when(categoryModel.getName()).thenReturn(CATEGORY_NAME);

		// when
		final CategoryPageModel categoryPageModel = cmsPageService.getPageForCategory(categoryModel);

		// then
		assertThat(categoryPageModel, equalTo(categoryPageModel1));
	}

	@Test
	public void shouldReturnLastCategoryPageByCategoryWhenThereAreMoreThenOnePage() throws Exception
	{
		// given
		final List<AbstractPageModel> pages = asList(categoryPageModel1, categoryPageModel2);
		when(typeService.getComposedTypeForCode(CategoryPageModel._TYPECODE)).thenReturn(composedTypeModel);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(categoryModel.getName()).thenReturn(CATEGORY_NAME);
		when(cmsDataFactory.createRestrictionData(categoryModel)).thenReturn(restrictionData);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, restrictionData)).thenReturn(pages);

		// when
		final CategoryPageModel categoryPageModel = cmsPageService.getPageForCategory(categoryModel);

		// then
		assertThat(categoryPageModel, equalTo(categoryPageModel2));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldThrowExceptionWhenNoCategoryPageFound() throws Exception
	{
		// given
		final List<AbstractPageModel> pages = asList(categoryPageModel1, categoryPageModel2);
		when(typeService.getComposedTypeForCode(CategoryPageModel._TYPECODE)).thenReturn(composedTypeModel);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(categoryModel.getName()).thenReturn(CATEGORY_NAME);
		when(cmsDataFactory.createRestrictionData(categoryModel)).thenReturn(restrictionData);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, restrictionData)).thenReturn(Collections.EMPTY_LIST);

		// when
		cmsPageService.getPageForCategory(categoryModel);
	}

	@Test
	public void shouldReturnCategoryPageByCategoryCode() throws Exception
	{
		// given
		when(sessionService.getAttribute(CURRENTCATALOGVERSION)).thenReturn(catalogVersionModel);
		when(categoryService.getCategoryForCode(catalogVersionModel, CATEGORY_CODE)).thenReturn(categoryModel);

		final List<AbstractPageModel> pages = asList(categoryPageModel1, categoryPageModel2);
		when(typeService.getComposedTypeForCode(CategoryPageModel._TYPECODE)).thenReturn(composedTypeModel);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(categoryModel.getName()).thenReturn(CATEGORY_NAME);
		when(cmsDataFactory.createRestrictionData(categoryModel)).thenReturn(restrictionData);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, sessionCatalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, restrictionData)).thenReturn(pages);

		// when
		final CategoryPageModel categoryPageModel = cmsPageService.getPageForCategoryCode(CATEGORY_CODE);

		// then
		assertThat(categoryPageModel, equalTo(categoryPageModel2));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldThrowExceptionWhenCategoryPageIsNotFoundByCode() throws Exception
	{
		// given
		when(sessionService.getAttribute(CURRENTCATALOGVERSION)).thenReturn(catalogVersionModel);
		when(categoryService.getCategoryForCode(catalogVersionModel, CATEGORY_CODE))
				.thenThrow(new UnknownIdentifierException("fakeMessage"));

		// when
		cmsPageService.getPageForCategoryCode(CATEGORY_CODE);
	}

	@Test
	public void shouldReturnTheVersionedPageWhenAValidVersionUidIsPassedToGetPageForId() throws Exception
	{
		// given
		final PagePreviewCriteriaData pagePreviewCriteria = new PagePreviewCriteriaData();
		pagePreviewCriteria.setVersionUid("validVersionUid");

		when(cmsVersionDao.findByUid("validVersionUid")).thenReturn(Optional.of(cmsVersionModel));
		when(cmsVersionService.createItemFromVersion(cmsVersionModel)).thenReturn(versionedPage);

		// when
		final AbstractPageModel result = cmsPageService.getPageForId("someId", pagePreviewCriteria);

		// then
		assertThat(result, equalTo(versionedPage));

	}

	@Test(expected = CMSVersionNotFoundException.class)
	public void shouldThrowExceptionWhenAnInvalidVersionUidIsPassedToGetPageForVersionUid() throws Exception
	{
		// given
		when(cmsVersionDao.findByUid("invalidVersionUid")).thenReturn(Optional.empty());

		// when
		cmsPageService.getPageForVersionUid("invalidVersionUid");
	}

	@Test
	public void shouldReturnTheActualPageWhenAnInvalidVersionUidIsPassedToGetPageForId() throws Exception
	{
		// given
		final PagePreviewCriteriaData pagePreviewCriteria = new PagePreviewCriteriaData();
		pagePreviewCriteria.setVersionUid("invalidVersionUid");

		when(cmsVersionDao.findByUid("invalidVersionUid")).thenReturn(Optional.empty());

		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);

		final List<AbstractPageModel> pages = asList(contentPageModel1);
		when(cmsPageDao.findPagesByIdAndPageStatuses("someId", sessionCatalogVersions, asList(CmsPageStatus.ACTIVE)))
				.thenReturn(pages);

		// when
		final AbstractPageModel result = cmsPageService.getPageForId("someId", pagePreviewCriteria);

		// then
		assertThat(result, equalTo(contentPageModel1));
	}

	@Test
	public void getPageForIdWithRestrictionsShouldReturnExpectedContentPage() throws Exception
	{
		// GIVEN
		when(contentPageModel1.getLabel()).thenReturn(PAGE_LABEL);
		when(contentPageModel2.getLabel()).thenReturn(PAGE_LABEL);
		final List<AbstractPageModel> pages = asList(contentPageModel1, contentPageModel2);
		final Collection<CatalogVersionModel> catalogVersions = Arrays.asList(catalogVersionModel1, catalogVersionModel2);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(catalogVersions);
		when(cmsPageDao.findPagesByIdAndPageStatuses(PAGE_LABEL, catalogVersions, asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE_LABEL, catalogVersions, asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(Arrays.asList(contentPageModel1));

		// WHEN
		final AbstractPageModel contentPageModel = cmsPageService.getPageForIdWithRestrictions(PAGE_LABEL);

		// THEN
		assertThat(contentPageModel, equalTo(contentPageModel1));
	}

	@Test
	public void getPageForIdWithRestrictionsShouldReturnExpectedCategoryPage() throws Exception
	{
		// GIVEN
		final Collection<CatalogVersionModel> catalogVersions = Arrays.asList(catalogVersionModel1, catalogVersionModel2);
		final List<AbstractPageModel> pages = asList(categoryPageModel1, categoryPageModel2);
		when(cmsPageDao.findPagesByIdAndPageStatuses(CATEGORY_NAME, catalogVersions, asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(typeService.getComposedTypeForCode(CategoryPageModel._TYPECODE)).thenReturn(composedTypeModel);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(catalogVersions);
		when(categoryModel.getName()).thenReturn(CATEGORY_NAME);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, catalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(pages);

		// WHEN
		final AbstractPageModel categoryPageModel = cmsPageService.getPageForIdWithRestrictions(CATEGORY_NAME);

		// THEN
		assertThat(categoryPageModel, equalTo(categoryPageModel2));
	}

	@Test
	public void getPageForIdWithRestrictionsShouldReturnExpectedProductPage() throws Exception
	{
		when(productService.getProductForCode(PRODUCT_CODE)).thenReturn(productModel);

		final Collection<CatalogVersionModel> catalogVersions = Arrays.asList(catalogVersionModel1, catalogVersionModel2);
		final List<AbstractPageModel> pages = asList(productPageModel1, productPageModel2);
		when(cmsPageDao.findPagesByIdAndPageStatuses(PRODUCT_CODE, catalogVersions, asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(typeService.getComposedTypeForCode(ProductPageModel._TYPECODE)).thenReturn(composedTypeModel);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(catalogVersions);
		when(productModel.getCode()).thenReturn(PRODUCT_CODE);
		when(cmsPageDao.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedTypeModel, catalogVersions,
				asList(CmsPageStatus.ACTIVE))).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(pages);

		// WHEN
		final AbstractPageModel categoryPageModel = cmsPageService.getPageForIdWithRestrictions(PRODUCT_CODE);

		// THEN
		assertThat(categoryPageModel, equalTo(productPageModel2));
	}

	@Test
	public void getPageForIdWithRestrictionsWithNotSupportedPageTypeShouldReturnExpectedPage() throws Exception
	{
		// GIVEN
		final SomePageModel somePageModel = new SomePageModel();
		somePageModel.setUid(PAGE_ID);

		final List<AbstractPageModel> pages = asList(somePageModel);
		final Collection<CatalogVersionModel> catalogVersions = Arrays.asList(catalogVersionModel1, catalogVersionModel2);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(catalogVersions);
		when(cmsPageDao.findPagesByIdAndPageStatuses(PAGE_ID, catalogVersions, asList(CmsPageStatus.ACTIVE))).thenReturn(pages);

		// WHEN
		final AbstractPageModel pageModel = cmsPageService.getPageForIdWithRestrictions(PAGE_ID);

		// THEN
		assertThat(pageModel, equalTo(somePageModel));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void shouldThrowExceptionWhenPageNotFoundById() throws Exception
	{
		// given
		final PagePreviewCriteriaData pagePreviewCriteria = new PagePreviewCriteriaData();
		pagePreviewCriteria.setVersionUid("versionUid");

		when(cmsVersionDao.findByUid("versionUid")).thenReturn(Optional.empty());

		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);

		final List<AbstractPageModel> pages = asList();
		when(cmsPageDao.findPagesByIdAndPageStatuses(PAGE_ID, sessionCatalogVersions, asList(CmsPageStatus.ACTIVE)))
				.thenReturn(pages);

		// when
		cmsPageService.getPageForId(PAGE_ID, pagePreviewCriteria);
	}

	@Test
	public void shouldReturnTheVersionedPageWhenAValidVersionUidIsPassedToGetPageForLabel() throws Exception
	{
		// given
		setupValidVersionUidMocks();

		// when
		final AbstractPageModel result = cmsPageService.getPageForLabel("someLabel", pagePreviewCriteria);

		// then
		assertThat(result, equalTo(versionedPage));

	}

	@Test
	public void shouldReturnTheVersionedPageWhenAValidVersionUidIsPassedToGetPageForLabelOrId() throws Exception
	{
		// given
		setupValidVersionUidMocks();

		// when
		final ContentPageModel result = cmsPageService.getPageForLabelOrId("someLabelOrId", pagePreviewCriteria);

		// then
		assertThat(result, equalTo(versionedPage));

	}

	@Test
	public void givenAValidVersionUidShouldReturnTheConentsSlotsForPageFromCachedSessionContextProvider()
	{
		// given
		setupValidVersionUidMocks();

		// when
		cmsPageService.getContentSlotModelsForPage(pageModelMock, pagePreviewCriteria);

		// then
		verify(cmsContentSlotDaoMock, times(0)).findAllContentSlotRelationsByPage(pageModelMock);
		verify(cmsVersionSessionContextProvider, times(1)).getAllCachedContentSlotsForPage();
	}

	@Test
	public void givenAValidVersionUidGetContentSlotForPageShouldReturnSlotFromSessionVersionContext() throws Exception
	{
		// given
		setupValidVersionUidMocks();

		final ContentSlotForPageModel slot1 = new ContentSlotForPageModel();
		slot1.setPosition("1");

		final ContentSlotForPageModel slot2 = new ContentSlotForPageModel();
		slot1.setPosition("2");

		when(cmsVersionSessionContextProvider.getAllCachedContentSlotsForPage()).thenReturn(asList(slot1, slot2));
		when(cmsDataFactory.createContentSlotData(any())).thenReturn(contentSlotData);

		// when
		final ContentSlotData result = cmsPageService.getContentSlotForPage(pageModelMock, "2", pagePreviewCriteria);

		// then
		assertThat(result, equalTo(contentSlotData));

	}

	@Test
	public void givenAValidVersionUidThenGetPageForCategoryShouldReturnPageForProvidedCategory() throws Exception
	{
		// given
		setupValidVersionUidMocks();

		when(cmsVersionService.createItemFromVersion(cmsVersionModel)).thenReturn(categoryPageModel1);

		// when
		final CategoryPageModel result = cmsPageService.getPageForCategory(categoryModel, pagePreviewCriteria);

		// then
		assertThat(result, equalTo(categoryPageModel1));
		verify(cmsVersionService, times(1)).createItemFromVersion(cmsVersionModel);
	}

	@Test
	public void givenAValidVersionUidThenGetPageForCategoryCodeShouldReturnPageForProvidedCategoryCode() throws Exception
	{
		// given
		setupValidVersionUidMocks();

		when(cmsVersionService.createItemFromVersion(cmsVersionModel)).thenReturn(categoryPageModel1);

		// when
		final CategoryPageModel result = cmsPageService.getPageForCategoryCode("someCategoryCode", pagePreviewCriteria);

		// then
		assertThat(result, equalTo(categoryPageModel1));
		verify(cmsVersionService, times(1)).createItemFromVersion(cmsVersionModel);
	}

	@Test
	public void givenAValidVersionUidThenGetPageForProductShouldReturnPageForProvidedProduct() throws Exception
	{
		// given
		setupValidVersionUidMocks();

		when(cmsVersionService.createItemFromVersion(cmsVersionModel)).thenReturn(productPageModel1);

		// when
		final ProductPageModel result = cmsPageService.getPageForProduct(productModel, pagePreviewCriteria);

		// then
		assertThat(result, equalTo(productPageModel1));
		verify(cmsVersionService, times(1)).createItemFromVersion(cmsVersionModel);
	}

	@Test
	public void givenAValidVersionUidThenGetPageForProductCodeShouldReturnPageForProvidedProductCode() throws Exception
	{
		// given
		setupValidVersionUidMocks();

		when(cmsVersionService.createItemFromVersion(cmsVersionModel)).thenReturn(productPageModel1);

		// when
		final ProductPageModel result = cmsPageService.getPageForProductCode("someProductCode", pagePreviewCriteria);

		// then
		assertThat(result, equalTo(productPageModel1));
		verify(cmsVersionService, times(1)).createItemFromVersion(cmsVersionModel);
	}

	@Test
	public void shouldFindHomepageForMultiCountry()
	{
		when(contentPageModel1.getLabel()).thenReturn(PAGE_LABEL);
		when(contentPageModel1.getCatalogVersion()).thenReturn(catalogVersionModel2);

		// in multi-country, multiple content pages can have the homepage flag set to TRUE (one from each catalog in the site)
		final Collection<CatalogVersionModel> catalogVersions = Arrays.asList(catalogVersionModel1, catalogVersionModel2);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(catalogVersions);
		final List<CmsPageStatus> pageStatuses = Arrays.asList(CmsPageStatus.ACTIVE);
		when(cmsPageDao.findHomepagesByPageStatuses(catalogVersions, pageStatuses))
				.thenReturn(Arrays.asList(contentPageModel1, contentPageModel2));

		// after finding all homepages in the catalog hierarchy, sort the homepages and select the one from the bottom catalog
		when(cmsItemCatalogLevelComparator.compare(contentPageModel1, contentPageModel2)).thenReturn(1);
		when(cmsItemCatalogLevelComparator.compare(contentPageModel2, contentPageModel1)).thenReturn(-1);

		// find all pages with the same label as the homepage and run the cms restrictions evaluation
		final List<AbstractPageModel> pages = Arrays.asList(contentPageModel1);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE_LABEL, Arrays.asList(catalogVersionModel2), pageStatuses))
				.thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(pages);

		final ContentPageModel result = cmsPageService.getHomepage();

		assertThat(result, equalTo(contentPageModel1));
	}

	@Test
	public void givenAValidVersionUidThenGetHomepageShouldReturnPageForTheProvidedVersionId() throws Exception
	{
		// given
		setupValidVersionUidMocks();

		when(cmsVersionService.createItemFromVersion(cmsVersionModel)).thenReturn(contentPageModel1);

		// when
		final ContentPageModel result = cmsPageService.getHomepage(pagePreviewCriteria);

		// then
		assertThat(result, equalTo(contentPageModel1));
		verify(cmsVersionService, times(1)).createItemFromVersion(cmsVersionModel);
	}

	protected void setupValidVersionUidMocks()
	{
		pagePreviewCriteria.setVersionUid("validVersionUid");

		when(cmsVersionDao.findByUid("validVersionUid")).thenReturn(Optional.of(cmsVersionModel));
		when(cmsVersionService.createItemFromVersion(cmsVersionModel)).thenReturn(versionedPage);
	}
}
