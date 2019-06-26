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
package de.hybris.platform.sap.productconfig.services.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.sap.productconfig.services.enums.ProductConfigurationPersistenceCleanUpMode;
import de.hybris.platform.sap.productconfig.services.impl.ProductConfigurationPagingUtil;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationPersistenceService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationPersistenceCleanUpCronJobModel;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class PersistenceCleanUpJobTest
{

	private static final Integer THRESHOLD = 100;
	private static final String CONFIG_ID = "72635";
	private final PersistenceCleanUpJob classUnderTest = new PersistenceCleanUpJob();
	private ProductConfigurationModel productConfiguration;
	private SearchPageData<ProductConfigurationModel> searchPageData;

	@Mock
	private ProductConfigurationPersistenceService productConfigurationPersistenceService;
	@Mock
	private ProductConfigurationPersistenceCleanUpCronJobModel productConfigurationPersistenceCleanUpCronJobModel;

	private final List<ProductConfigurationModel> configurationModelList = new ArrayList<>();


	@Mock
	private ModelService modelService;

	@Mock
	private ProductConfigurationService productConfigurationService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private BaseSiteModel baseSiteModel;


	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setProductConfigurationPersistenceService(productConfigurationPersistenceService);
		classUnderTest.setProductConfigurationService(productConfigurationService);
		classUnderTest.setModelService(modelService);
		classUnderTest.setProductConfigurationPagingUtil(new ProductConfigurationPagingUtil());
		classUnderTest.setBaseSiteService(baseSiteService);

		productConfiguration = new ProductConfigurationModel();
		productConfiguration.setConfigurationId(CONFIG_ID);
		configurationModelList.add(productConfiguration);
		when(productConfigurationPersistenceCleanUpCronJobModel.getCleanUpMode())
				.thenReturn(ProductConfigurationPersistenceCleanUpMode.ALL);
		when(productConfigurationPersistenceCleanUpCronJobModel.getThresholdDays()).thenReturn(THRESHOLD);
		when(productConfigurationPersistenceCleanUpCronJobModel.getBaseSite()).thenReturn(baseSiteModel);


		searchPageData = new SearchPageData<>();
		searchPageData.setResults(configurationModelList);
		final PaginationData pagination = new PaginationData();
		pagination.setTotalNumberOfResults(configurationModelList.size());
		pagination.setNumberOfPages(1);
		searchPageData.setPagination(pagination);

		when(productConfigurationPersistenceService.getOrphaned(ProductConfigurationPagingUtil.PAGE_SIZE, 0))
				.thenReturn(searchPageData);
		when(productConfigurationPersistenceService.getProductRelatedByThreshold(THRESHOLD,
				ProductConfigurationPagingUtil.PAGE_SIZE, 0)).thenReturn(searchPageData);
	}

	@Test
	public void testBaseSiteService()
	{
		assertEquals(baseSiteService, classUnderTest.getBaseSiteService());
	}


	@Test
	public void testProductConfigurationPersistenceService()
	{
		assertEquals(productConfigurationPersistenceService, classUnderTest.getProductConfigurationPersistenceService());
	}

	@Test
	public void testProductConfigurationService()
	{
		assertEquals(productConfigurationService, classUnderTest.getProductConfigurationService());
	}

	@Test
	public void testRequestedCleanUpProductRelated()
	{
		assertTrue(classUnderTest.isRequestedCleanUpProductRelated(productConfigurationPersistenceCleanUpCronJobModel));
	}

	@Test
	public void testRequestedCleanUpOrphanedWhenAll()
	{
		assertTrue(classUnderTest.isRequestedCleanUpOrphaned(productConfigurationPersistenceCleanUpCronJobModel));
	}

	@Test
	public void testRequestedCleanUpOrphanedWhenProductRealated()
	{
		given(productConfigurationPersistenceCleanUpCronJobModel.getCleanUpMode())
				.willReturn(ProductConfigurationPersistenceCleanUpMode.ONLYPRODUCTRELATED);
		assertFalse(classUnderTest.isRequestedCleanUpOrphaned(productConfigurationPersistenceCleanUpCronJobModel));
	}

	@Test
	public void testRequestedCleanUpOrphanedWhenOrphaned()
	{
		given(productConfigurationPersistenceCleanUpCronJobModel.getCleanUpMode())
				.willReturn(ProductConfigurationPersistenceCleanUpMode.ONLYORPHANED);
		assertTrue(classUnderTest.isRequestedCleanUpOrphaned(productConfigurationPersistenceCleanUpCronJobModel));
	}


	@Test
	public void testPerformAll()
	{
		final PerformResult performResult = classUnderTest.perform(productConfigurationPersistenceCleanUpCronJobModel);
		assertNotNull(performResult);
		assertEquals(CronJobResult.SUCCESS, performResult.getResult());
		assertEquals(CronJobStatus.FINISHED, performResult.getStatus());
	}

	@Test
	public void testPerformOrpahned()
	{
		given(productConfigurationPersistenceCleanUpCronJobModel.getCleanUpMode())
				.willReturn(ProductConfigurationPersistenceCleanUpMode.ONLYORPHANED);
		final PerformResult performResult = classUnderTest.perform(productConfigurationPersistenceCleanUpCronJobModel);
		assertNotNull(performResult);
		assertEquals(CronJobResult.SUCCESS, performResult.getResult());
		assertEquals(CronJobStatus.FINISHED, performResult.getStatus());
	}

	@Test
	public void testPerformProductRelated()
	{
		given(productConfigurationPersistenceCleanUpCronJobModel.getCleanUpMode())
				.willReturn(ProductConfigurationPersistenceCleanUpMode.ONLYPRODUCTRELATED);
		final PerformResult performResult = classUnderTest.perform(productConfigurationPersistenceCleanUpCronJobModel);
		assertNotNull(performResult);
		assertEquals(CronJobResult.SUCCESS, performResult.getResult());
		assertEquals(CronJobStatus.FINISHED, performResult.getStatus());
	}


	@Test
	public void testCleanUpProductRelated()
	{
		classUnderTest.cleanUpConfigs(currentPage -> classUnderTest.searchProductRelated(THRESHOLD, currentPage));
		verify(productConfigurationService).releaseSession(CONFIG_ID);
	}

	@Test
	public void testCleanUpOrphaned()
	{
		final SearchPageData<ProductConfigurationModel> fullSearchPageData = mockFullSearchPage();
		when(productConfigurationPersistenceService.getOrphaned(ProductConfigurationPagingUtil.PAGE_SIZE, 0))
				.thenReturn(fullSearchPageData);
		when(productConfigurationPersistenceService.getOrphaned(ProductConfigurationPagingUtil.PAGE_SIZE, 1))
				.thenReturn(searchPageData);
		classUnderTest.cleanUpConfigs(currentPage -> classUnderTest.searchOrphaned(currentPage));
		verify(productConfigurationService, times(101)).releaseSession(anyString());
	}

	@Test
	public void testCleanUpProductConfigurations()
	{
		classUnderTest.cleanUpProductConfigurations(configurationModelList);
	}

	@Test
	public void testCleanUpProductConfiguration()
	{
		classUnderTest.cleanUpProductConfiguration(productConfiguration);
		verify(productConfigurationService).releaseSession(CONFIG_ID);
	}

	@Test
	public void testCleanUpProductConfigurationEngineExceptionMustBeCatched()
	{
		Mockito.doThrow(IllegalStateException.class).when(productConfigurationService).releaseSession(CONFIG_ID);
		classUnderTest.cleanUpProductConfiguration(productConfiguration);
		verify(productConfigurationService).releaseSession(CONFIG_ID);
	}

	@Test
	public void testEnsureBaseSiteAvailable()
	{
		classUnderTest.ensureBaseSiteAvailable(productConfigurationPersistenceCleanUpCronJobModel);
		verify(baseSiteService).setCurrentBaseSite(baseSiteModel, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEnsureBaseSiteAvailableNoBaseSite()
	{
		when(productConfigurationPersistenceCleanUpCronJobModel.getBaseSite()).thenReturn(null);
		classUnderTest.ensureBaseSiteAvailable(productConfigurationPersistenceCleanUpCronJobModel);
	}


	protected SearchPageData<ProductConfigurationModel> mockFullSearchPage()
	{
		final SearchPageData<ProductConfigurationModel> fullSearchPageData = new SearchPageData<>();
		List<ProductConfigurationModel> fullList;
		{
			ProductConfigurationModel model = Mockito.mock(ProductConfigurationModel.class);
			fullList = new ArrayList();
			for (int ii = 0; ii < ProductConfigurationPagingUtil.PAGE_SIZE; ii++)
			{
				model = new ProductConfigurationModel();
				model.setConfigurationId(String.valueOf(ii));
				fullList.add(model);
			}
		}
		fullSearchPageData.setResults(fullList);
		fullSearchPageData.setPagination(new PaginationData());
		return fullSearchPageData;
	}


}
