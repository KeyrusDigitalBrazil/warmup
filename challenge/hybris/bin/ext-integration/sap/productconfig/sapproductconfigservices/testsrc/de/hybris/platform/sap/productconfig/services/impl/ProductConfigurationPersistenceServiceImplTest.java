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
package de.hybris.platform.sap.productconfig.services.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.sap.productconfig.services.exceptions.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchParameter;
import de.hybris.platform.servicelayer.search.paginated.PaginatedFlexibleSearchService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class ProductConfigurationPersistenceServiceImplTest
{
	private static final String MATCH_ANY = "--any--";
	private static final String CONFIG_ID = "config123";
	private static final String CONFIG_ID_DRAFT = "draft123";
	private static final String CART_ENTRY_PK = "cartEntry123";
	private static final String PRODUCT_CODE = "pCode123";
	private static final String USER_SESSION_ID = "userSessionId";
	private static final String USER_ID = "userId";
	private static final String USER_PK = "123";
	private static final String PRODUCT_CODE_NOT_UNIQUE = "pCode890";
	private static final String CONFIG_ID_NOT_UNIQUE = "configNonUnique";
	private static final Integer thresholdInDays = 100;
	private static final Date SELECTION_DATE = Calendar.getInstance().getTime();
	private static final int PAGE_SIZE = 100;

	private ProductConfigurationPersistenceServiceImpl classUnderTest;
	private ProductConfigurationModel configModel;
	private AbstractOrderEntryModel entryModel;

	@Mock
	private FlexibleSearchService flexibleSearchServiceMock;
	@Mock
	private PaginatedFlexibleSearchService paginatedFlexibleSearchService;
	@Mock
	private SessionService sessionServiceMock;
	@Mock
	private UserService userServiceMock;
	@Mock
	private UserModel userModelMock;
	private ArgumentMatcher<FlexibleSearchQuery> queryMatcherRelatedeQuery;
	private ArgumentMatcher queryMatcherRelatedDraftQuery;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ProductConfigurationPersistenceServiceImpl();
		classUnderTest.setFlexibleSearchService(flexibleSearchServiceMock);
		classUnderTest.setUserService(userServiceMock);
		classUnderTest.setSessionService(sessionServiceMock);
		classUnderTest.setPaginatedFlexibleSearchService(paginatedFlexibleSearchService);


		configModel = new ProductConfigurationModel();
		configModel.setConfigurationId(CONFIG_ID);
		entryModel = new AbstractOrderEntryModel();


		willThrow(UnknownIdentifierException.class).given(flexibleSearchServiceMock).searchUnique(any());
		willReturn(new SearchResultImpl<>(Collections.emptyList(), 0, 0, 0)).given(flexibleSearchServiceMock)
				.search(any(FlexibleSearchQuery.class));

		ArgumentMatcher<FlexibleSearchQuery> queryMatcher;
		queryMatcher = matchQueryWithParams(ProductConfigurationPersistenceServiceImpl.SELECT_BY_CONFIG_ID,
				Collections.singletonMap("configId", CONFIG_ID));
		mockGivenQuery(queryMatcher, configModel);

		queryMatcher = matchQueryWithParams(ProductConfigurationPersistenceServiceImpl.SELECT_ENTRY_BY_CONFIG_ID,
				Collections.singletonMap("configId", CONFIG_ID));
		mockGivenQuery(queryMatcher, entryModel);

		queryMatcher = matchQueryWithParams(ProductConfigurationPersistenceServiceImpl.SELECT_ENTRY_BY_DRAFT_CONFIG_ID,
				Collections.singletonMap("configId", CONFIG_ID_DRAFT));
		mockGivenQuery(queryMatcher, entryModel);

		queryMatcher = matchQueryWithParams(ProductConfigurationPersistenceServiceImpl.SELECT_ENTRY_BY_PK,
				Collections.singletonMap("cartEntryKey", CART_ENTRY_PK));
		mockGivenQuery(queryMatcher, entryModel);

		queryMatcher = matchQueryWithParams(ProductConfigurationPersistenceServiceImpl.SELECT_BY_USER_SESSION_ID,
				Collections.singletonMap("userSessionId", USER_SESSION_ID));
		mockGivenQuery(queryMatcher, configModel);

		HashMap expectedParams = new HashMap();
		expectedParams.put("productCode", PRODUCT_CODE);
		expectedParams.put("userPk", USER_PK);
		queryMatcher = matchQueryWithParams(ProductConfigurationPersistenceServiceImpl.SELECT_BY_PRODUCT_CODE_AND_USER,
				expectedParams);
		mockGivenQuery(queryMatcher, configModel);

		expectedParams = new HashMap();
		expectedParams.put("productCode", PRODUCT_CODE);
		expectedParams.put("sessionId", USER_SESSION_ID);
		queryMatcher = matchQueryWithParams(ProductConfigurationPersistenceServiceImpl.SELECT_BY_PRODUCT_CODE_AND_SESSIONID,
				expectedParams);
		mockGivenQuery(queryMatcher, configModel);


		given(userModelMock.getPk()).willReturn(PK.parse(USER_PK));
		given(userServiceMock.getCurrentUser()).willReturn(userModelMock);

		final Session session = Mockito.mock(Session.class);
		given(session.getSessionId()).willReturn(USER_SESSION_ID);
		given(sessionServiceMock.getCurrentSession()).willReturn(session);

		expectedParams = new HashMap();
		expectedParams.put("productCode", PRODUCT_CODE_NOT_UNIQUE);
		expectedParams.put("userPk", USER_PK);
		queryMatcher = matchQueryWithParams(ProductConfigurationPersistenceServiceImpl.SELECT_BY_PRODUCT_CODE_AND_USER,
				expectedParams);
		mockNonUniqueResult(queryMatcher, 2);

		queryMatcher = matchQueryWithParams(ProductConfigurationPersistenceServiceImpl.SELECT_ENTRY_BY_CONFIG_ID,
				Collections.singletonMap("configId", CONFIG_ID_NOT_UNIQUE));
		mockNonUniqueResult(queryMatcher, 2);

		expectedParams = new HashMap();
		expectedParams.put("configId", CONFIG_ID);
		expectedParams.put("cartEntryKey", CART_ENTRY_PK);
		queryMatcherRelatedeQuery = matchQueryWithParams(
				ProductConfigurationPersistenceServiceImpl.SELECT_BY_CONFIG_ID_AND_CART_ENTRY_KEY, expectedParams);
		mockNonUniqueResult(queryMatcherRelatedeQuery, 0);

		expectedParams = new HashMap();
		expectedParams.put("configId", CONFIG_ID);
		expectedParams.put("cartEntryKey", CART_ENTRY_PK);
		queryMatcherRelatedDraftQuery = matchQueryWithParams(
				ProductConfigurationPersistenceServiceImpl.SELECT_BY_DRAFT_CONFIG_ID_AND_CART_ENTRY_KEY, expectedParams);
		mockNonUniqueResult(queryMatcherRelatedDraftQuery, 0);

		ArgumentMatcher<PaginatedFlexibleSearchParameter> pageableQueryMatcher = matchPageableQueryWithParams(
				ProductConfigurationPersistenceServiceImpl.SELECT_PRODUCT_RELATED,
				Collections.singletonMap("modifiedTime", MATCH_ANY), PAGE_SIZE, 0);
		mockPageableResult(pageableQueryMatcher, 3);

		pageableQueryMatcher = matchPageableQueryWithParams(ProductConfigurationPersistenceServiceImpl.SELECT_ORPHANED,
				Collections.emptyMap(), PAGE_SIZE, 0);
		mockPageableResult(pageableQueryMatcher, 2);
	}

	protected void mockPageableResult(final ArgumentMatcher<PaginatedFlexibleSearchParameter> queryMatcher, final int size)
	{
		final List<ProductConfigurationModel> list = new ArrayList<>();
		for (int ii = 0; ii < size; ii++)
		{
			list.add(configModel);
		}
		final SearchPageData<ProductConfigurationModel> pageData = new SearchPageData<>();
		pageData.setResults(list);

		willReturn(pageData).given(paginatedFlexibleSearchService).search(argThat(queryMatcher));
	}

	protected void mockNonUniqueResult(final ArgumentMatcher<FlexibleSearchQuery> queryMatcher, final int numResults)
	{
		final List<ProductConfigurationModel> list = new ArrayList<>();
		for (int ii = 0; ii < numResults; ii++)
		{
			list.add(configModel);
		}
		willReturn(new SearchResultImpl<>(list, numResults, numResults, numResults)).given(flexibleSearchServiceMock)
				.search(argThat(queryMatcher));
	}

	protected void mockGivenQuery(final ArgumentMatcher<FlexibleSearchQuery> queryMatcher, final Object model)
	{
		willReturn(model).given(flexibleSearchServiceMock).searchUnique(argThat(queryMatcher));
		willReturn(new SearchResultImpl<>(Collections.singletonList(model), 1, 1, 1)).given(flexibleSearchServiceMock)
				.search(argThat(queryMatcher));
	}

	@Test
	public void getOrderEntryByConfigId()
	{
		final AbstractOrderEntryModel orderEntry = classUnderTest.getOrderEntryByConfigId(CONFIG_ID, false);
		assertSame(entryModel, orderEntry);
	}

	@Test
	public void getOrderEntryByDraftConfigId()
	{
		final AbstractOrderEntryModel orderEntry = classUnderTest.getOrderEntryByConfigId(CONFIG_ID_DRAFT, true);
		assertSame(entryModel, orderEntry);
	}


	@Test
	public void getOrderEntryByConfigIdReturnNull()
	{
		final AbstractOrderEntryModel orderEntry = classUnderTest.getOrderEntryByConfigId("bla", false);
		assertNull(orderEntry);
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void getOrderEntryByConfigIdNonUniqueResult()
	{
		classUnderTest.getOrderEntryByConfigId(CONFIG_ID_NOT_UNIQUE, false);
	}

	@Test
	public void getByProductCode()
	{
		final ProductConfigurationModel model = classUnderTest.getByProductCode(PRODUCT_CODE);
		assertSame(configModel, model);
	}

	@Test
	public void getByProductCodeReturnNull()
	{
		final ProductConfigurationModel model = classUnderTest.getByProductCode("bla");
		assertNull(model);
	}

	@Test(expected = AmbiguousIdentifierException.class)
	public void getByProductCodeNonUniqueResult()
	{
		classUnderTest.getByProductCode(PRODUCT_CODE_NOT_UNIQUE);
	}

	@Test
	public void getByProductCodeAnonymousUser()
	{
		given(userServiceMock.isAnonymousUser(userModelMock)).willReturn(true);
		final ProductConfigurationModel model = classUnderTest.getByProductCode(PRODUCT_CODE);
		assertSame(configModel, model);
	}


	@Test(expected = IllegalArgumentException.class)
	public void getByProductCodeNull()
	{
		classUnderTest.getByProductCode(null);
	}


	@Test
	public void testGetByConfigId()
	{
		given(userServiceMock.isAnonymousUser(userModelMock)).willReturn(true);
		final ProductConfigurationModel model = classUnderTest.getByConfigId(CONFIG_ID);
		assertSame(configModel, model);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetByConfigIdNull()
	{
		classUnderTest.getByConfigId(null);
	}

	@Test(expected = ConfigurationNotFoundException.class)
	public void testGetByConfigIdConfigRemoved()
	{
		classUnderTest.getByConfigId("bla");
	}

	@Test
	public void testGetByConfigIdConfigAllowNull()
	{
		assertNull(classUnderTest.getByConfigId("bla", true));
	}

	@Test
	public void getOrderEntryByPK()
	{
		final AbstractOrderEntryModel model = classUnderTest.getOrderEntryByPK(CART_ENTRY_PK);
		assertSame(entryModel, model);
	}

	@Test(expected = IllegalArgumentException.class)
	public void getEntryByConfigIdNull()
	{
		classUnderTest.getOrderEntryByPK(null);
	}

	@Test
	public void testGetByUserSessionId()
	{
		final List<ProductConfigurationModel> models = classUnderTest.getByUserSessionId(USER_SESSION_ID);
		assertEquals(1, models.size());
		assertSame(configModel, models.get(0));
	}

	@Test
	public void testGetByUserId()
	{
		final List<ProductConfigurationModel> models = classUnderTest.getByUserSessionId(USER_SESSION_ID);
		assertEquals(1, models.size());
		assertSame(configModel, models.get(0));
	}

	@Test
	public void testGetProductRelatedByThreshold()
	{
		final SearchPageData<ProductConfigurationModel> productList = classUnderTest.getProductRelatedByThreshold(thresholdInDays,
				PAGE_SIZE, 0);
		assertNotNull(productList);
		assertEquals(3, productList.getResults().size());
	}

	@Test
	public void testGetOrphaned()
	{
		final SearchPageData<ProductConfigurationModel> orphanedList = classUnderTest.getOrphaned(PAGE_SIZE, 0);
		assertNotNull(orphanedList);
		assertEquals(2, orphanedList.getResults().size());
	}

	@Test
	public void testCalculateModificationDate()
	{
		final Date modificationDate = classUnderTest.calculateModificationDate(thresholdInDays);
		assertNotNull(modificationDate);
		final Date today = Calendar.getInstance().getTime();

		//Make test robust in the sense that the runtime between method invocation and check does not matter
		assertEquals(1,
				today.toInstant().minus(thresholdInDays, ChronoUnit.DAYS).compareTo(modificationDate.toInstant().minusSeconds(1)));
		assertEquals(-1,
				today.toInstant().minus(thresholdInDays, ChronoUnit.DAYS).compareTo(modificationDate.toInstant().plusSeconds(1)));
	}


	@Test(expected = NullPointerException.class)
	public void testGetProductRelatedByThresholdDateNull()
	{
		classUnderTest.getProductRelatedByThreshold(null, 0, 0);
	}

	@Test
	public void testPaginatedFlexibleSearchService()
	{
		assertEquals(paginatedFlexibleSearchService, classUnderTest.getPaginatedFlexibleSearchService());
	}

	@Test
	public void testCreatePageableQuery()
	{
		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery("dummy");
		final PaginatedFlexibleSearchParameter searchInput = classUnderTest.createPageableQuery(flexibleSearchQuery, PAGE_SIZE, 0);
		assertNotNull(searchInput);
		assertSame(flexibleSearchQuery, searchInput.getFlexibleSearchQuery());
		assertEquals(PAGE_SIZE, searchInput.getSearchPageData().getPagination().getPageSize());
	}

	@Test
	public void testIsOnlyRelatedToGivenEntryTrue()
	{
		assertTrue(classUnderTest.isOnlyRelatedToGivenEntry(CONFIG_ID, CART_ENTRY_PK));
	}

	@Test
	public void testIsOnlyRelatedToGivenEntryFalse()
	{
		mockNonUniqueResult(queryMatcherRelatedeQuery, 1);
		assertFalse(classUnderTest.isOnlyRelatedToGivenEntry(CONFIG_ID, CART_ENTRY_PK));
	}

	@Test
	public void testIsOnlyRelatedToGivenEntryDraftFalse()
	{
		mockNonUniqueResult(queryMatcherRelatedDraftQuery, 1);
		assertFalse(classUnderTest.isOnlyRelatedToGivenEntry(CONFIG_ID, CART_ENTRY_PK));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsOnlyRelatedToGivenEntryNullConfigId()
	{
		assertFalse(classUnderTest.isOnlyRelatedToGivenEntry(null, CART_ENTRY_PK));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIsOnlyRelatedToGivenEntryNullCartEntryKey()
	{
		assertFalse(classUnderTest.isOnlyRelatedToGivenEntry(CONFIG_ID, null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetAllOrderEntriesByConfigIdNull()
	{
		classUnderTest.getAllOrderEntriesByConfigId(null);
	}

	@Test
	public void testGetAllOrderEntriesByConfigId()
	{
		final ArgumentMatcher<FlexibleSearchQuery> queryMatcher = matchQueryWithParams(
				ProductConfigurationPersistenceServiceImpl.SELECT_ENTRY_BY_DRAFT_CONFIG_ID,
				Collections.singletonMap("configId", CONFIG_ID));
		mockGivenQuery(queryMatcher, entryModel);
		final List<AbstractOrderEntryModel> allEntries = classUnderTest.getAllOrderEntriesByConfigId(CONFIG_ID);
		assertEquals(2, allEntries.size());
	}

	protected ArgumentMatcher<FlexibleSearchQuery> matchQueryWithParams(final String queryString, final Map<String, String> params)
	{
		return new ArgumentMatcher<FlexibleSearchQuery>()
		{
			@Override
			public boolean matches(final Object argument)
			{
				final FlexibleSearchQuery query = (FlexibleSearchQuery) argument;
				boolean matches = queryString.equals(query.getQuery()) && query.getQueryParameters().size() == params.size();
				for (final Entry<String, String> expectedParam : params.entrySet())
				{
					matches = matches && expectedParam.getValue().equals(query.getQueryParameters().get(expectedParam.getKey()));
				}
				return matches;
			}
		};
	}

	protected ArgumentMatcher<PaginatedFlexibleSearchParameter> matchPageableQueryWithParams(final String queryString,
			final Map<String, String> params, final int pageSize, final int currentPage)
	{
		return new ArgumentMatcher<PaginatedFlexibleSearchParameter>()
		{
			@Override
			public boolean matches(final Object argument)
			{
				final PaginatedFlexibleSearchParameter pageableQuery = (PaginatedFlexibleSearchParameter) argument;
				final FlexibleSearchQuery query = pageableQuery.getFlexibleSearchQuery();
				boolean matches = queryString.equals(query.getQuery()) && query.getQueryParameters().size() == params.size();
				matches = matches && pageableQuery.getSearchPageData().getPagination().getCurrentPage() == currentPage;
				matches = matches && pageableQuery.getSearchPageData().getPagination().getPageSize() == pageSize;
				for (final Entry<String, String> expectedParam : params.entrySet())
				{
					if (MATCH_ANY.equals(expectedParam.getValue()))
					{
						matches = matches && query.getQueryParameters().get(expectedParam.getKey()) != null;
					}
					else
					{
						matches = matches && expectedParam.getValue().equals(query.getQueryParameters().get(expectedParam.getKey()));
					}
				}
				return matches;
			}
		};
	}
}
