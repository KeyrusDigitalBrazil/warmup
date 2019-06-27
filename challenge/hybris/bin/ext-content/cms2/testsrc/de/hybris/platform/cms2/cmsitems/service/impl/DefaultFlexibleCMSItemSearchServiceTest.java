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
package de.hybris.platform.cms2.cmsitems.service.impl;

import static de.hybris.platform.cms2.cmsitems.service.impl.DefaultFlexibleCMSItemSearchService.ITEM_SEARCH_PARAM_CHECK;
import static de.hybris.platform.cms2.cmsitems.service.impl.DefaultFlexibleCMSItemSearchService.ITEM_SEARCH_PARAM_CHECK_NULL;
import static de.hybris.platform.cms2.cmsitems.service.impl.DefaultFlexibleCMSItemSearchService.MASK_CHECK;
import static de.hybris.platform.cms2.cmsitems.service.impl.DefaultFlexibleCMSItemSearchService.MASK_QUERY_PARAM;
import static de.hybris.platform.cms2.cmsitems.service.impl.DefaultFlexibleCMSItemSearchService.TYPECODES_QUERY_PARAM;
import static org.fest.assertions.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.cmsitems.service.FlexibleSearchAttributeValueConverter;
import de.hybris.platform.cms2.cmsitems.service.SortStatementFormatter;
import de.hybris.platform.cms2.common.service.SearchHelper;
import de.hybris.platform.cms2.data.CMSItemSearchData;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cms2.model.CMSComponentTypeModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.namedquery.Sort;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.security.permissions.PermissionCRUDService;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultFlexibleCMSItemSearchServiceTest
{
	private static final String CATALOG_ID = "CATALOG_ID";
	private static final String CATALOG_VERSION = "CATALOG_VERSION";
	private static final String ORDER_BY_NAME = " ORDER BY LOWER({c.name}) ASC";
	private static final String ORDER_BY_NAME_AND_DESCRIPTION = " ORDER BY LOWER({c.name}) ASC, LOWER({c.description}) DESC";
	private static final String TYPECODE = "testTypeCode";

	@InjectMocks
	private DefaultFlexibleCMSItemSearchService flexibleCMSItemsSearchService;

	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private FlexibleSearchService flexibleSearchService;
	@Mock
	private FlexibleSearchAttributeValueConverter flexibleSearchAttributeValueConverter;
	@Mock
	private TypeService typeService;
	@Mock
	private PermissionCRUDService permissionCRUDService;
	@Mock
	private SortStatementFormatter itemtypeSortStatementFormatter;
	@Mock
	private SortStatementFormatter stringSortStatementFormatter;
	@Mock
	private SortStatementFormatter sortStatementFormatter;
	@Mock
	private AttributeDescriptorModel attributeDescriptor;
	@Mock
	private Map<String, List<String>> cmsItemSearchTypeBlacklistMap;
	@Mock
	private SearchHelper searchHelper;
	@Mock
	private ComposedTypeModel cmsLinkComponentTypeModel;
	@Mock
	private CMSComponentTypeModel cmsComponentTypeModel;
	@Mock
	private ComposedTypeModel itemComposedTypeModel;
	@Mock
	private ComposedTypeModel superTypeModel;
	@Mock
	private ComposedTypeModel subTypeModel;

	private List<SortStatementFormatter> sortStatementFormatters;

	@Before
	public void setup()
	{
		when(catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION)).thenReturn(catalogVersionModel);
		when(typeService.getAttributeDescriptor(anyString(), anyString())).thenReturn(attributeDescriptor);

		when(cmsLinkComponentTypeModel.getCode()).thenReturn(CMSLinkComponentModel._TYPECODE);
		when(cmsLinkComponentTypeModel.getAllSuperTypes()).thenReturn(Arrays.asList(cmsComponentTypeModel, itemComposedTypeModel));

		when(cmsComponentTypeModel.getCode()).thenReturn(CMSComponentTypeModel._TYPECODE);
		when(cmsComponentTypeModel.getAllSuperTypes()).thenReturn(Arrays.asList(itemComposedTypeModel));

		when(itemComposedTypeModel.getCode()).thenReturn(CMSItemModel._TYPECODE);

		when(typeService.getComposedTypeForCode(cmsLinkComponentTypeModel.getCode())).thenReturn(cmsLinkComponentTypeModel);
		when(typeService.getComposedTypeForCode(itemComposedTypeModel.getCode())).thenReturn(itemComposedTypeModel);

		sortStatementFormatters = Arrays.asList(stringSortStatementFormatter, itemtypeSortStatementFormatter);
		flexibleCMSItemsSearchService.setSortStatementFormatters(sortStatementFormatters);
		flexibleCMSItemsSearchService.setDefaultSortStatementFormatter(sortStatementFormatter);
		flexibleCMSItemsSearchService.setFlexibleSearchAttributeValueConverter(flexibleSearchAttributeValueConverter);
		flexibleCMSItemsSearchService.setCmsItemSearchTypeBlacklistMap(cmsItemSearchTypeBlacklistMap);

		when(permissionCRUDService.canReadType(any(ComposedTypeModel.class))).thenReturn(Boolean.TRUE);
	}

	@Test
	public void testSearchQueryContainsNoMaskAndNoType()
	{
		final String mask = null;
		final String type = null;
		final FlexibleSearchQuery capturedQuery = triggerQuery(mask, type);

		assertNoMaskQuery(capturedQuery, mask);
		assertExpectedValueInQuery(capturedQuery, CMSItemModel._TYPECODE);
	}

	@Test
	public void testSearchQueryContainsMaskAndNoType()
	{
		final String mask = "someMask";
		final String type = null;
		final FlexibleSearchQuery capturedQuery = triggerQuery(mask, type);

		assertMaskQuery(capturedQuery, mask);
		assertExpectedValueInQuery(capturedQuery, CMSItemModel._TYPECODE);
	}

	@Test
	public void testSearchQueryContainsNoMaskAndType()
	{
		final String type = CMSLinkComponentModel._TYPECODE;

		final String mask = null;
		final FlexibleSearchQuery capturedQuery = triggerQuery(mask, type);

		assertNoMaskQuery(capturedQuery, mask);
		assertExpectedValueInQuery(capturedQuery, type);
	}

	@Test
	public void testSearchQueryContainsMaskAndType()
	{
		final String type = CMSLinkComponentModel._TYPECODE;

		final String mask = "someMask";
		final FlexibleSearchQuery capturedQuery = triggerQuery(mask, type);

		assertMaskQuery(capturedQuery, mask);
		assertExpectedValueInQuery(capturedQuery, type);
	}

	@Test
	public void testSearchQueryContainsAdditionalParams()
	{
		// GIVEN
		final String type = CMSLinkComponentModel._TYPECODE;
		final String param1 = "label";
		final String param2 = "title";
		final String mask = null;
		final Map<String, String> itemSearchParams = new HashMap<>();
		itemSearchParams.put(param1, "123");
		itemSearchParams.put(param2, "456");

		final AttributeDescriptorModel model1 = new AttributeDescriptorModel();
		when(typeService.getAttributeDescriptor(cmsLinkComponentTypeModel, param1)).thenReturn(model1);
		when(flexibleSearchAttributeValueConverter.convert(model1, "123")).thenReturn("123");

		final AttributeDescriptorModel model2 = new AttributeDescriptorModel();
		when(typeService.getAttributeDescriptor(cmsLinkComponentTypeModel, param2)).thenReturn(model2);
		when(flexibleSearchAttributeValueConverter.convert(model2, "456")).thenReturn("456");

		// WHEN
		final FlexibleSearchQuery capturedQuery = triggerQuery(mask, type, itemSearchParams);

		// THEN
		assertExpectedValueInQuery(capturedQuery, type);
		itemSearchParams.keySet().forEach(field -> {
			assertThat(capturedQuery.getQueryParameters().keySet(), hasItem(field));
			assertThat(capturedQuery.getQueryParameters().get(field), equalTo(itemSearchParams.get(field)));
			assertThat(capturedQuery.getQuery(), containsString(String.format(ITEM_SEARCH_PARAM_CHECK, field, field)));
		});
	}

	@Test
	public void testSearchQueryContainsAdditionalNullParam()
	{
		// GIVEN
		final String type = CMSLinkComponentModel._TYPECODE;
		final String param1 = "label";
		final String mask = null;
		final Map<String, String> itemSearchParams = new HashMap<>();
		itemSearchParams.put(param1, "null");
		final AttributeDescriptorModel model1 = new AttributeDescriptorModel();
		when(typeService.getAttributeDescriptor(cmsLinkComponentTypeModel, param1)).thenReturn(model1);
		when(flexibleSearchAttributeValueConverter.convert(model1, "123")).thenReturn("123");

		// WHEN
		final FlexibleSearchQuery capturedQuery = triggerQuery(mask, type, itemSearchParams);

		// THEN
		assertExpectedValueInQuery(capturedQuery, type);
		itemSearchParams.keySet().forEach(field -> {
			assertThat(capturedQuery.getQueryParameters().keySet(), not(hasItem(field)));
			assertThat(capturedQuery.getQuery(), containsString(String.format(ITEM_SEARCH_PARAM_CHECK_NULL, field, field)));
		});
	}

	@Test
	public void testSearchQueryContainsSort()
	{
		final String type = CMSLinkComponentModel._TYPECODE;
		when(stringSortStatementFormatter.isApplicable(attributeDescriptor)).thenReturn(true);
		when(stringSortStatementFormatter.formatSortStatement(any())).thenReturn("LOWER({c.name})");
		when(searchHelper.convertSort("name:ASC", SortDirection.ASC))
				.thenReturn(Collections.singletonList(new Sort().withParameter("LOWER({c.name})").withDirection(SortDirection.ASC)));

		final FlexibleSearchQuery capturedQuery = triggerQuery(null, type, new HashMap<>());

		assertExpectedValueInQuery(capturedQuery, type);
		assertExpectedValueInQuery(capturedQuery, ORDER_BY_NAME);
	}

	@Test
	public void testSearchQueryNoTypeCodeContainsSort()
	{
		when(stringSortStatementFormatter.isApplicable(attributeDescriptor)).thenReturn(true);
		when(stringSortStatementFormatter.formatSortStatement(any())).thenReturn("LOWER({c.name})");
		when(searchHelper.convertSort("name:ASC", SortDirection.ASC))
				.thenReturn(Collections.singletonList(new Sort().withParameter("LOWER({c.name})").withDirection(SortDirection.ASC)));

		final FlexibleSearchQuery capturedQuery = triggerQuery(null, null, new HashMap<>());

		assertExpectedValueInQuery(capturedQuery, CMSItemModel._TYPECODE);
		assertExpectedValueInQuery(capturedQuery, ORDER_BY_NAME);
	}

	@Test(expected = IllegalArgumentException.class)
	public void exceptionIsThrownForMissingSearchParamData()
	{
		flexibleCMSItemsSearchService.findCMSItems(null, new PageableData());
	}

	@Test(expected = IllegalArgumentException.class)
	public void exceptionIsThrownForMissingPagingData()
	{
		flexibleCMSItemsSearchService.findCMSItems(new CMSItemSearchData(), null);
	}

	@Test
	public void testAppendSortNameAndLowerCaseDirection()
	{
		when(stringSortStatementFormatter.isApplicable(attributeDescriptor)).thenReturn(true);
		when(stringSortStatementFormatter.formatSortStatement(any())).thenReturn("LOWER({c.name})");
		when(searchHelper.convertSort("name:asc", SortDirection.ASC))
				.thenReturn(Collections.singletonList(new Sort().withParameter("LOWER({c.name})").withDirection(SortDirection.ASC)));

		final StringBuilder queryBuilder = new StringBuilder();
		flexibleCMSItemsSearchService.appendSort("name:asc", queryBuilder, TYPECODE);

		assertThat(queryBuilder.toString(), equalTo(ORDER_BY_NAME));
	}

	@Test
	public void testAppendSortNameAndUpperCaseDirection()
	{
		when(stringSortStatementFormatter.isApplicable(attributeDescriptor)).thenReturn(true);
		when(stringSortStatementFormatter.formatSortStatement(any())).thenReturn("LOWER({c.name})");
		when(searchHelper.convertSort("name:ASC", SortDirection.ASC))
				.thenReturn(Collections.singletonList(new Sort().withParameter("LOWER({c.name})").withDirection(SortDirection.ASC)));


		final StringBuilder queryBuilder = new StringBuilder();
		flexibleCMSItemsSearchService.appendSort("name:ASC", queryBuilder, TYPECODE);

		assertThat(queryBuilder.toString(), equalTo(ORDER_BY_NAME));
	}

	@Test
	public void testAppendSortNameAndInvalidDirection()
	{
		when(stringSortStatementFormatter.isApplicable(attributeDescriptor)).thenReturn(true);
		when(stringSortStatementFormatter.formatSortStatement(any())).thenReturn("LOWER({c.name})");
		when(searchHelper.convertSort("name:Invalid", SortDirection.ASC))
				.thenReturn(Collections.singletonList(new Sort().withParameter("name").withDirection(SortDirection.ASC)));

		final StringBuilder queryBuilder = new StringBuilder();
		flexibleCMSItemsSearchService.appendSort("name:Invalid", queryBuilder, TYPECODE);

		assertThat(queryBuilder.toString(), equalTo(ORDER_BY_NAME));
	}

	@Test
	public void testAppendSortNameWithDefaultSortDirection()
	{
		when(stringSortStatementFormatter.isApplicable(attributeDescriptor)).thenReturn(true);
		when(stringSortStatementFormatter.formatSortStatement(any())).thenReturn("LOWER({c.name})");
		when(searchHelper.convertSort("name", SortDirection.ASC))
				.thenReturn(Collections.singletonList(new Sort().withParameter("name").withDirection(SortDirection.ASC)));

		final StringBuilder queryBuilder = new StringBuilder();
		flexibleCMSItemsSearchService.appendSort("name", queryBuilder, TYPECODE);

		assertThat(queryBuilder.toString(), equalTo(ORDER_BY_NAME));
	}

	@Test
	public void testAppendSortNameAndDescription()
	{
		when(stringSortStatementFormatter.isApplicable(attributeDescriptor)).thenReturn(true).thenReturn(true);
		when(stringSortStatementFormatter.formatSortStatement(any())).thenReturn("LOWER({c.name})")
				.thenReturn("LOWER({c.description})");
		when(searchHelper.convertSort("name:ASC,description:DESC", SortDirection.ASC))
				.thenReturn(Arrays.asList(new Sort().withParameter("name").withDirection(SortDirection.ASC),
						new Sort().withParameter("description").withDirection(SortDirection.DESC)));

		final StringBuilder queryBuilder = new StringBuilder();
		flexibleCMSItemsSearchService.appendSort("name:ASC,description:DESC", queryBuilder, TYPECODE);

		assertThat(queryBuilder.toString(), equalTo(ORDER_BY_NAME_AND_DESCRIPTION));
	}

	@Test
	public void testNotAppendTypeExclusions()
	{
		when(cmsItemSearchTypeBlacklistMap.containsKey(anyString())).thenReturn(Boolean.FALSE);

		final StringBuilder queryBuilder = new StringBuilder();
		final HashMap<String, Object> queryParameters = new HashMap<>();
		flexibleCMSItemsSearchService.appendTypeExclusions(Arrays.asList(cmsLinkComponentTypeModel), queryBuilder, queryParameters);

		verify(cmsItemSearchTypeBlacklistMap, times(0)).get(AbstractPageModel._TYPECODE);
		assertThat(queryBuilder.toString(), isEmptyString());
		assertThat(queryParameters.values(), empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testValidateSearchDataWithTypeCodeAndTypeCodesShouldThrowError()
	{
		final CMSItemSearchData cmsItemSearchData = new CMSItemSearchData();
		cmsItemSearchData.setTypeCode("anyType1");
		cmsItemSearchData.setTypeCodes(Arrays.asList("anyType2"));

		flexibleCMSItemsSearchService.validateSearchData(cmsItemSearchData, new PageableData());
	}

	@Test
	public void testGetValidComposedTypesShouldFilterOutInvalidTypeCodes()
	{
		when(typeService.getComposedTypeForCode(cmsLinkComponentTypeModel.getCode())).thenReturn(cmsLinkComponentTypeModel);
		when(typeService.getComposedTypeForCode("invalidTypeCode")).thenThrow(new UnknownIdentifierException(""));

		final CMSItemSearchData cmsItemSearchData = new CMSItemSearchData();
		cmsItemSearchData.setTypeCodes(Arrays.asList(cmsLinkComponentTypeModel.getCode(), "invalidTypeCode"));
		final List<ComposedTypeModel> expectedComposedTypes = flexibleCMSItemsSearchService
				.getValidComposedTypes(cmsItemSearchData);

		assertThat(expectedComposedTypes).hasSize(1);
		assertThat(expectedComposedTypes.get(0), equalTo(cmsLinkComponentTypeModel));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetValidComposedTypesShouldThrowErrorIfThereIsNoValidTypeCode()
	{
		when(typeService.getComposedTypeForCode("invalidTypeCode")).thenThrow(new UnknownIdentifierException(""));

		final CMSItemSearchData cmsItemSearchData = new CMSItemSearchData();
		cmsItemSearchData.setTypeCode("invalidTypeCode");

		flexibleCMSItemsSearchService.getValidComposedTypes(cmsItemSearchData);
	}

	@Test
	public void testGetFirstCommonAncestorTypeCode()
	{
		final String expectedCommonAncestor = flexibleCMSItemsSearchService
				.getFirstCommonAncestorTypeCode(Arrays.asList(cmsLinkComponentTypeModel, cmsComponentTypeModel));

		assertThat(expectedCommonAncestor, equalTo(CMSComponentTypeModel._TYPECODE));
	}

	@Test
	public void testGetFirstCommonAncestorTypeCodeWithItemComposedTypeShouldReturnCMSItemTypeCode()
	{
		when(cmsLinkComponentTypeModel.getCode()).thenReturn("anyTypeCode");
		when(itemComposedTypeModel.getAllSuperTypes()).thenReturn(Arrays.asList(superTypeModel));
		when(superTypeModel.getCode()).thenReturn("anyTypeCode");

		// superTypes comparison between [anyTypeCode, CMSComponentType, CMSItem] and [CMSItem, anyTypeCode]
		final String expectedCommonAncestor = flexibleCMSItemsSearchService
				.getFirstCommonAncestorTypeCode(Arrays.asList(cmsLinkComponentTypeModel, itemComposedTypeModel));

		assertThat(expectedCommonAncestor, equalTo(CMSItemModel._TYPECODE));
	}

	@Test
	public void testPrepareQueryBuilder()
	{
		// GIVEN
		when(itemComposedTypeModel.getAbstract()).thenReturn(true);
		when(cmsLinkComponentTypeModel.getAllSubTypes()).thenReturn(Arrays.asList(subTypeModel));
		when(subTypeModel.getCode()).thenReturn("subTypeCode");

		// WHEN
		final List<ComposedTypeModel> composedTypes = Arrays.asList(cmsLinkComponentTypeModel, itemComposedTypeModel);
		final HashMap<String, Object> queryParameters = new HashMap<>();
		final StringBuilder stringBuilder = flexibleCMSItemsSearchService.prepareQueryBuilder(composedTypes,
				CMSComponentTypeModel._TYPECODE, queryParameters);

		// THEN
		assertThat(queryParameters.keySet(), hasItem(TYPECODES_QUERY_PARAM));
		assertThat(queryParameters.get(TYPECODES_QUERY_PARAM),
				equalTo(Arrays.asList(CMSLinkComponentModel._TYPECODE, "subTypeCode")));
		assertThat(stringBuilder.toString(), containsString("SELECT {c.PK} FROM { " + CMSComponentTypeModel._TYPECODE + " as c"));
	}

	@Test
	public void testGetFirstCommonElement()
	{
		final String firstCommonElement = "ANY_ELEMENT";

		final List<String> listA = Arrays.asList("string 1", "string 2", firstCommonElement);
		final List<String> listB = Arrays.asList("any value", firstCommonElement, "anyElementValue");

		final List<List<String>> collections = Arrays.asList(listA, listB);

		final String element = flexibleCMSItemsSearchService.getFirstCommonElement(collections);
		assertThat(element, equalTo(firstCommonElement));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFirstCommonElementThrowExceptionIfNull()
	{
		final List<String> listA = Arrays.asList("string 1", "string 2");
		final List<String> listB = Arrays.asList("value");

		final List<List<String>> collections = Arrays.asList(listA, listB);

		flexibleCMSItemsSearchService.getFirstCommonElement(collections);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFirstCommonElementThrowExceptionIfOneListIsEmpty()
	{
		final List<String> listA = Arrays.asList();
		final List<String> listB = Arrays.asList("value", "anyElementValue");

		final List<List<String>> collections = Arrays.asList(listA, listB);

		flexibleCMSItemsSearchService.getFirstCommonElement(collections);
	}

	protected FlexibleSearchQuery triggerQuery(final String mask, final String typeCode,
			final Map<String, String> itemSearchParams)
	{
		final CMSItemSearchData cmsItemSearchData = new CMSItemSearchData();
		cmsItemSearchData.setCatalogId(CATALOG_ID);
		cmsItemSearchData.setCatalogVersion(CATALOG_VERSION);
		cmsItemSearchData.setMask(mask);
		cmsItemSearchData.setTypeCode(typeCode);
		cmsItemSearchData.setItemSearchParams(itemSearchParams);

		final PageableData pageableData = new PageableData();
		pageableData.setCurrentPage(0);
		pageableData.setPageSize(5);
		pageableData.setSort("name:ASC");

		flexibleCMSItemsSearchService.findCMSItems(cmsItemSearchData, pageableData);

		// capture query
		final ArgumentCaptor<FlexibleSearchQuery> args = ArgumentCaptor.forClass(FlexibleSearchQuery.class);
		verify(flexibleSearchService).search(args.capture());

		return args.getValue();
	}

	protected FlexibleSearchQuery triggerQuery(final String mask, final String typeCode)
	{
		return triggerQuery(mask, typeCode, null);
	}

	protected void assertMaskQuery(final FlexibleSearchQuery flexibleSearchQuery, final String expectedMask)
	{
		assertThat(flexibleSearchQuery.getQueryParameters().keySet(), hasItem(MASK_QUERY_PARAM));
		assertThat(flexibleSearchQuery.getQueryParameters().get(MASK_QUERY_PARAM), equalTo("%" + expectedMask + "%"));
		assertThat(flexibleSearchQuery.getQuery(), containsString(MASK_CHECK));
	}

	protected void assertNoMaskQuery(final FlexibleSearchQuery flexibleSearchQuery, final String expectedMask)
	{
		assertThat(flexibleSearchQuery.getQueryParameters().keySet(), not(hasItem(MASK_QUERY_PARAM)));
		assertThat(flexibleSearchQuery.getQueryParameters().get(MASK_QUERY_PARAM), not(equalTo("%" + expectedMask + "%")));
		assertThat(flexibleSearchQuery.getQuery(), not(containsString(MASK_CHECK)));
	}

	protected void assertExpectedValueInQuery(final FlexibleSearchQuery flexibleSearchQuery, final String expectedValue)
	{
		assertThat(expectedValue, notNullValue());
		assertThat(flexibleSearchQuery.getQuery(), containsString(expectedValue));
	}
}
