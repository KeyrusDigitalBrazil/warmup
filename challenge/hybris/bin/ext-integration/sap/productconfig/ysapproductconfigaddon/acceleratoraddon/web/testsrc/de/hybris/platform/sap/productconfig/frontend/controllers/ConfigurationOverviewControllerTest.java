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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.AbstractController;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationOverviewFacade;
import de.hybris.platform.sap.productconfig.facades.overview.CharacteristicGroup;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.facades.overview.FilterEnum;
import de.hybris.platform.sap.productconfig.frontend.CPQOverviewActionType;
import de.hybris.platform.sap.productconfig.frontend.FilterData;
import de.hybris.platform.sap.productconfig.frontend.OverviewMode;
import de.hybris.platform.sap.productconfig.frontend.OverviewUiData;
import de.hybris.platform.sap.productconfig.frontend.UiStatus;
import de.hybris.platform.sap.productconfig.frontend.constants.SapproductconfigfrontendWebConstants;
import de.hybris.platform.sap.productconfig.frontend.model.ProductConfigPageModel;
import de.hybris.platform.sap.productconfig.frontend.util.ConfigErrorHandler;
import de.hybris.platform.servicelayer.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@UnitTest
public class ConfigurationOverviewControllerTest extends AbstractProductConfigControllerBaseTest
{

	private static final String CART_ENTRY_KEY = "123456";

	// will inject cmsPageService and pageTitleResolver, as well. For both no setter exists
	@InjectMocks
	private ConfigurationOverviewController classUnderTest;
	private ConfigurationOverviewData configOverviewData;
	private UiStatus uiStatus;
	private OverviewUiData overviewUIData;

	@Mock
	private ConfigErrorHandler errorHandler;
	@Mock
	protected ConfigurationOverviewFacade configurationOverviewFacade;

	@Before
	public void setUp()
	{
		classUnderTest = new ConfigurationOverviewController();
		MockitoAnnotations.initMocks(this);
		injectMocks(classUnderTest);
		classUnderTest.setConfigurationErrorHandler(errorHandler);
		classUnderTest.setConfigurationOverviewFacade(configurationOverviewFacade);

		configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setId(CONFIG_ID);
		configOverviewData.setProductCode(PRODUCT_CODE);
		configOverviewData.setGroups(Collections.emptyList());

		csticList = createCsticsList();

		uiStatus = new UiStatus();

		kbKey = createKbKey();
		configData = createEmptyConfigData();
		configData.setKbKey(kbKey);
	}

	@Test
	public void testRedirectToErrorPage() throws Exception
	{
		initializeFirstCall();
		Mockito.when(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).thenReturn(null);
		final OverviewUiData uiData = new OverviewUiData();
		uiData.setProductCode(PRODUCT_CODE);
		classUnderTest.updateConfiguationOverview(uiData, model, request);
		Mockito.verify(errorHandler).handleErrorForAjaxRequest(request, model);
	}

	@Test
	public void testGenerateFilterDataListNoneSelected()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List<FilterData> filterDataList = classUnderTest.generateCsticFilterDataList(configOverviewData);

		assertFalse(filterDataList.isEmpty());
		for (final FilterData filterData : filterDataList)
		{
			assertFalse(filterData.isSelected());
			assertFalse(filterData.getKey().equals(FilterEnum.VISIBLE.toString()));
		}
	}

	@Test
	public void testGenerateFilterDataListNull()
	{
		final ConfigurationOverviewData configOverviewData = null;
		final List<FilterData> filterDataList = classUnderTest.generateCsticFilterDataList(configOverviewData);

		assertTrue(filterDataList.isEmpty());
	}

	@Test
	public void testGenerateFilterDataListSelected()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List appliedFilters = new ArrayList<FilterEnum>();
		appliedFilters.add(FilterEnum.USER_INPUT);
		configOverviewData.setAppliedCsticFilters(appliedFilters);
		final List<FilterData> filterDataList = classUnderTest.generateCsticFilterDataList(configOverviewData);

		assertFalse(filterDataList.isEmpty());
		for (final FilterData filterData : filterDataList)
		{
			if (FilterEnum.USER_INPUT.toString().equals(filterData.getKey()))
			{
				assertTrue(filterData.isSelected());
			}
			else
			{
				assertFalse(filterData.isSelected());
			}
			assertFalse(filterData.getKey().equals(FilterEnum.VISIBLE.toString()));
		}
	}

	@Test
	public void testGenerateFilterDataListIgnoreVisible()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List appliedFilters = new ArrayList<FilterEnum>();
		appliedFilters.add(FilterEnum.VISIBLE);
		configOverviewData.setAppliedCsticFilters(appliedFilters);
		final List<FilterData> filterDataList = classUnderTest.generateCsticFilterDataList(configOverviewData);

		assertFalse(filterDataList.isEmpty());
		for (final FilterData filterData : filterDataList)
		{
			assertFalse(filterData.isSelected());
			assertFalse(filterData.getKey().equals(FilterEnum.VISIBLE.toString()));
		}
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	@Test(expected = NullPointerException.class)
	public void testInitializeGroupFilterDataListNull()
	{
		classUnderTest.initializeGroupFilterDataList(null);
	}

	@Test
	public void testInitializeGroupFilterDataListNotNull()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setGroups(new ArrayList<>());
		final CharacteristicGroup group = new CharacteristicGroup();
		group.setId("CPQ");
		group.setIsSelectedTopLevelGroup(true);
		configOverviewData.getGroups().add(group);
		final List<FilterData> groupFilterDataList = classUnderTest.initializeGroupFilterDataList(configOverviewData);
		assertNotNull("We expect groupFilterdataList does not equal null: ", groupFilterDataList);
		assertFalse("We expect empty groupFilterdataList: ", groupFilterDataList.isEmpty());
		assertTrue(groupFilterDataList.size() == 1);
		assertEquals(group.getId(), groupFilterDataList.get(0).getKey());
		assertFalse(groupFilterDataList.get(0).isSelected());
	}

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
	@Test(expected = NullPointerException.class)
	public void testComputeUiGroupFilterListNull()
	{
		classUnderTest.computeUiGroupFilterList(null);
	}

	@Test
	public void testComputeUiGroupFilterListNotNull()
	{
		final List<FilterData> maxGroupFilterList = new ArrayList<>();
		final FilterData uiFilter1 = createFilterData("WCEM_MULTI", "SAP Hardware Centre", true);
		maxGroupFilterList.add(uiFilter1);

		final List<FilterData> filterDataList = classUnderTest.computeUiGroupFilterList(maxGroupFilterList);
		assertNotNull("We expect one filterDataList: ", filterDataList);
		assertFalse("We expect not an empty list: ", filterDataList.isEmpty());
		assertTrue("We expect a list with one element in it: ", filterDataList.size() == 1);
		assertTrue("We expect a groupId 'WCEM_SIMPLE_GROUP': ",
				filterDataList.get(0).getKey().equals(maxGroupFilterList.get(0).getKey()));
		assertTrue("We expect a group to be selected: ",
				filterDataList.get(0).isSelected() == maxGroupFilterList.get(0).isSelected());
	}

	@Test
	public void testUpdateGroupFilterList()
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		final List<FilterData> groupFilterList = new ArrayList<>();
		final FilterData groupFilter = createFilterData("WCEM_SIMPLE", "SAP Software Centre", true);
		groupFilterList.add(groupFilter);
		overviewUIData.setGroupFilterList(groupFilterList);

		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> maxGroupFilterList = new ArrayList<>();
		final FilterData uiFilter1 = createFilterData("WCEM_MULTI", "SAP Hardware Centre", false);
		maxGroupFilterList.add(uiFilter1);

		final FilterData uiFilter2 = createFilterData("WCEM_SIMPLE", "SAP Software Centre", false);
		maxGroupFilterList.add(uiFilter2);
		uiStatus.setMaxGroupFilterList(maxGroupFilterList);

		classUnderTest.updateGroupFilterList(overviewUIData, uiStatus);

		final List<FilterData> uiFilterDataList = overviewUIData.getGroupFilterList();
		final List<FilterData> maxFilterDataList = uiStatus.getMaxGroupFilterList();

		assertNotNull(uiFilterDataList);
		assertNotNull(maxFilterDataList);

		assertTrue(uiFilterDataList.size() == 1);
		assertTrue(maxFilterDataList.size() == 2);

		assertEquals(uiFilterDataList.get(0).getKey(), maxFilterDataList.get(1).getKey());
		assertTrue(uiFilterDataList.get(0).isSelected() == maxFilterDataList.get(1).isSelected());
	}

	private FilterData createFilterData(final String key, final String description, final boolean selected)
	{
		final FilterData groupFilter = new FilterData();
		groupFilter.setKey(key);
		groupFilter.setDescription(description);
		groupFilter.setSelected(selected);
		return groupFilter;
	}

	@Test
	public void testUpdateGroupFilterListGroupFilterNull()
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		final List<FilterData> groupFilterList = null;
		overviewUIData.setGroupFilterList(groupFilterList);

		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> maxGroupFilterList = new ArrayList<>();
		final FilterData uiFilter1 = createFilterData("WCEM_MULTI", "SAP Hardware Centre", true);
		maxGroupFilterList.add(uiFilter1);

		final FilterData uiFilter2 = createFilterData("WCEM_SIMPLE", "SAP Software Centre", false);
		maxGroupFilterList.add(uiFilter2);
		uiStatus.setMaxGroupFilterList(maxGroupFilterList);

		classUnderTest.updateGroupFilterList(overviewUIData, uiStatus);

		final List<FilterData> uiFilterDataList = overviewUIData.getGroupFilterList();
		final List<FilterData> maxFilterDataList = uiStatus.getMaxGroupFilterList();

		assertNull(uiFilterDataList);
		assertNotNull(maxFilterDataList);

		assertTrue(maxFilterDataList.size() == 2);

		assertTrue(maxFilterDataList.get(0).getKey().equals("WCEM_MULTI"));
		assertTrue(maxFilterDataList.get(0).getDescription().equals("SAP Hardware Centre"));
		assertTrue(maxFilterDataList.get(0).isSelected());

		assertTrue(maxFilterDataList.get(1).getKey().equals("WCEM_SIMPLE"));
		assertTrue(maxFilterDataList.get(1).getDescription().equals("SAP Software Centre"));
		assertFalse(maxFilterDataList.get(1).isSelected());
	}

	@Test
	public void testUpdateAppliedFiltersNoneSelected()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List<FilterData> filterDataList = new ArrayList<FilterData>();

		final FilterData userInput = new FilterData();
		userInput.setKey(FilterEnum.USER_INPUT.toString());
		userInput.setSelected(false);
		filterDataList.add(userInput);

		final FilterData priceRelevant = new FilterData();
		priceRelevant.setKey(FilterEnum.PRICE_RELEVANT.toString());
		priceRelevant.setSelected(false);
		filterDataList.add(priceRelevant);

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setCsticFilterList(filterDataList);

		classUnderTest.updateAppliedFilters(uiStatus, configOverviewData);

		final List appliedFilters = configOverviewData.getAppliedCsticFilters();

		assertTrue(appliedFilters.size() == 1);
		assertTrue(FilterEnum.VISIBLE.equals(appliedFilters.get(0)));
	}

	@Test
	public void testUpdateAppliedFiltersOneSelected()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List<FilterData> filterDataList = new ArrayList<FilterData>();

		final FilterData userInput = new FilterData();
		userInput.setKey(FilterEnum.USER_INPUT.toString());
		userInput.setSelected(true);
		filterDataList.add(userInput);

		final FilterData priceRelevant = new FilterData();
		priceRelevant.setKey(FilterEnum.PRICE_RELEVANT.toString());
		priceRelevant.setSelected(false);
		filterDataList.add(priceRelevant);

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setCsticFilterList(filterDataList);

		classUnderTest.updateAppliedFilters(uiStatus, configOverviewData);

		final List appliedFilters = configOverviewData.getAppliedCsticFilters();

		assertTrue(appliedFilters.size() == 2);
		assertTrue(FilterEnum.VISIBLE.equals(appliedFilters.get(0)));
		assertTrue(FilterEnum.USER_INPUT.equals(appliedFilters.get(1)));
	}

	@Test
	public void testUpdateAppliedFiltersTwoSelected()
	{
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		final List<FilterData> filterDataList = new ArrayList<FilterData>();

		final FilterData userInput = new FilterData();
		userInput.setKey(FilterEnum.USER_INPUT.toString());
		userInput.setSelected(true);
		filterDataList.add(userInput);

		final FilterData priceRelevant = new FilterData();
		priceRelevant.setKey(FilterEnum.PRICE_RELEVANT.toString());
		priceRelevant.setSelected(true);
		filterDataList.add(priceRelevant);

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setCsticFilterList(filterDataList);

		classUnderTest.updateAppliedFilters(uiStatus, configOverviewData);

		final List appliedFilters = configOverviewData.getAppliedCsticFilters();

		assertTrue(appliedFilters.size() == 3);
		assertTrue(FilterEnum.VISIBLE.equals(appliedFilters.get(0)));
		assertTrue(FilterEnum.USER_INPUT.equals(appliedFilters.get(1)));
		assertTrue(FilterEnum.PRICE_RELEVANT.equals(appliedFilters.get(2)));
	}

	@Test
	public void testUpdateAppliedFiltersNull()
	{
		final ConfigurationOverviewData configOverviewData = null;
		final List<FilterData> filterDataList = new ArrayList<FilterData>();

		final FilterData userInput = new FilterData();
		userInput.setKey(FilterEnum.USER_INPUT.toString());
		userInput.setSelected(true);
		filterDataList.add(userInput);

		final FilterData priceRelevant = new FilterData();
		priceRelevant.setKey(FilterEnum.PRICE_RELEVANT.toString());
		priceRelevant.setSelected(false);
		filterDataList.add(priceRelevant);

		final UiStatus uiStatus = new UiStatus();
		uiStatus.setCsticFilterList(filterDataList);

		classUnderTest.updateAppliedFilters(uiStatus, configOverviewData);
		assertNull(configOverviewData);
	}

	@Test
	public void testUpdateGroups()
	{
		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> maxGroupFilterList = new ArrayList<>();
		final FilterData uiFilter = createFilterData("WCEM_MULTI", "SAP Hardware Centre", true);
		maxGroupFilterList.add(uiFilter);
		uiStatus.setMaxGroupFilterList(maxGroupFilterList);

		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		classUnderTest.updateGroups(uiStatus, configOverviewData);
		assertNotNull(configOverviewData);
		assertNotNull(configOverviewData.getAppliedGroupFilters());
		assertFalse(configOverviewData.getAppliedGroupFilters().isEmpty());
	}

	@Test
	public void testUpdateGroupsEmpty()
	{
		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> maxGroupFilterList = new ArrayList<>();
		final FilterData uiFilter = createFilterData("WCEM_MULTI", "SAP Hardware Centre", false);
		maxGroupFilterList.add(uiFilter);
		uiStatus.setMaxGroupFilterList(maxGroupFilterList);

		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		classUnderTest.updateGroups(uiStatus, configOverviewData);

		assertNotNull(configOverviewData);
		assertNotNull(configOverviewData.getAppliedGroupFilters());
		assertTrue(configOverviewData.getAppliedGroupFilters().isEmpty());
	}

	@Test
	public void updateCsticFilterListOverviewDataIsNull()
	{
		final UiStatus uiStatus = new UiStatus();
		classUnderTest.updateCsticFilterList(null, uiStatus);
		assertNull(uiStatus.getCsticFilterList());
	}

	@Test
	public void updateCsticFilterListOverviewDataEmpty()
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		final List<FilterData> csticFilterList = new ArrayList<>();
		overviewUIData.setCsticFilterList(csticFilterList);
		final UiStatus uiStatus = new UiStatus();

		classUnderTest.updateCsticFilterList(overviewUIData, uiStatus);
		assertNotNull(uiStatus.getCsticFilterList());
		assertTrue(uiStatus.getCsticFilterList().isEmpty());
	}

	@Test
	public void updateCsticFilterListOverviewDataNewFilter()
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		final List<FilterData> csticFilterList = new ArrayList<>();
		final FilterData uiFilter = createFilterData("WCEM_MULTI", "SAP Hardware Centre", true);
		csticFilterList.add(uiFilter);
		overviewUIData.setCsticFilterList(csticFilterList);
		final UiStatus uiStatus = new UiStatus();

		classUnderTest.updateCsticFilterList(overviewUIData, uiStatus);
		assertNotNull(uiStatus.getCsticFilterList());
		assertFalse(uiStatus.getCsticFilterList().isEmpty());
	}

	@Test
	public void updateCsticFilterListOverviewDataChangeFilter()
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		final List<FilterData> csticFilterList = new ArrayList<>();
		final FilterData uiFilter = createFilterData(FilterEnum.USER_INPUT.toString(), "New Filter", true);
		csticFilterList.add(uiFilter);
		overviewUIData.setCsticFilterList(csticFilterList);

		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> csticFilterListNew = new ArrayList<>();
		csticFilterListNew.add(createFilterData(FilterEnum.PRICE_RELEVANT.toString(), "Old Filter", true));
		uiStatus.setCsticFilterList(csticFilterListNew);

		classUnderTest.updateCsticFilterList(overviewUIData, uiStatus);


		final List<FilterData> uiFilterDataList = overviewUIData.getCsticFilterList();
		final List<FilterData> statusFilterList = uiStatus.getCsticFilterList();

		assertNotNull(uiFilterDataList);
		assertNotNull(statusFilterList);
		assertTrue(uiFilterDataList.size() == 1);
		assertTrue(statusFilterList.size() == 1);
		assertTrue(statusFilterList.get(0).getKey().equals(FilterEnum.USER_INPUT.toString()));
	}

	@Test
	public void testAddBreadCrumb()
	{
		given(productData.getCode()).willReturn(PRODUCT_CODE);
		final OverviewUiData overviewPageModel = Mockito.mock(OverviewUiData.class);
		classUnderTest.addBreadCrumb(model, productData, overviewPageModel);

		verify(productConfigurationBreadcrumbBuilder, times(1)).getOverviewBreadcrumbs(PRODUCT_CODE, overviewPageModel);
	}

	@Test
	public void testUpdateConfigurationOverview() throws Exception
	{
		prepareUpdateCall();

		classUnderTest.updateConfiguationOverview(overviewUIData, model, request);
		verify(configFacade).getConfiguration(any(ConfigurationData.class));
	}

	@Test
	public void getOrCreateUiStatusForCartEntry()
	{
		Mockito.when(sessionAccessFacade.getUiStatusForCartEntry(CART_ENTRY_KEY)).thenReturn(uiStatus);
		final UiStatus returnedUiStatus = classUnderTest.getOrCreateUiStatusForCartEntry(CART_ENTRY_KEY);
		assertSame(returnedUiStatus, uiStatus);
	}

	@Test
	public void getOrCreateUiStatusForCartEntryDefaults()
	{
		Mockito.when(sessionAccessFacade.getUiStatusForCartEntry(CART_ENTRY_KEY)).thenReturn(null);
		final UiStatus returnedUiStatus = classUnderTest.getOrCreateUiStatusForCartEntry(CART_ENTRY_KEY);
		assertNotNull(returnedUiStatus);
		assertTrue(returnedUiStatus.isHideImageGallery());
	}

	public void prepareUpdateCall() throws Exception
	{
		prepareGetCall();
		//configLinkRemoved
		given(configurationProductLinkStrategy.getConfigIdForProduct(PRODUCT_CODE)).willReturn(null);

		overviewUIData = new OverviewUiData();
		overviewUIData.setConfigId(CONFIG_ID);
		overviewUIData.setProductCode(PRODUCT_CODE);
		overviewUIData.setOverviewMode(OverviewMode.CONFIGURATION_OVERVIEW);
		classUnderTest.initializeFilterListsInUiStatus(configOverviewData, uiStatus);
		given(configFacade.getConfiguration(argThat(new ArgumentMatcher<ConfigurationData>()
		{
			@Override
			public boolean matches(final Object configData)
			{
				return CONFIG_ID.equals(((ConfigurationData) configData).getConfigId());
			}
		}))).willReturn(configData);
	}

	protected void prepareGetCall() throws Exception
	{
		initializeFirstCall();
		given(configurationProductLinkStrategy.getConfigIdForProduct(PRODUCT_CODE)).willReturn(CONFIG_ID);
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn(CART_ENTRY_KEY);
		given(configurationOverviewFacade.getOverviewForConfiguration(eq(CONFIG_ID), any())).willReturn(configOverviewData);
		given(sessionAccessFacade.getUiStatusForProduct(PRODUCT_CODE)).willReturn(uiStatus);
		given(sessionAccessFacade.getUiStatusForCartEntry(CART_ENTRY_KEY)).willReturn(uiStatus);
		final CartData cartData = new CartData();
		final OrderEntryData entry = new OrderEntryData();
		entry.setItemPK(CART_ENTRY_KEY);
		final ProductData productData = new ProductData();
		productData.setCode(PRODUCT_CODE);
		entry.setProduct(productData);
		cartData.setEntries(Collections.singletonList(entry));
		given(cartFacadeMock.getSessionCart()).willReturn(cartData);
	}

	@Override
	protected void initializeFirstCall() throws Exception
	{
		super.initializeFirstCall();
		given(cmsPageService.getPageForId("productConfigOverview")).willReturn(new ProductConfigPageModel());
	}

	@Test
	public void testGetConfigurationOverview() throws Exception
	{
		prepareGetCall();
		assertEquals(SapproductconfigfrontendWebConstants.OVERVIEW_PAGE_VIEW_NAME,
				classUnderTest.getConfigurationOverview(CONFIG_ID, model, request));
		verify(configFacade).getConfiguration(any(ConfigurationData.class));
	}

	@Test
	public void testGetConfigurationOverviewNoCartEntry() throws Exception
	{
		prepareGetCall();
		given(abstractOrderEntryLinkStrategy.getCartEntryForConfigId(CONFIG_ID)).willReturn("");

		assertEquals(AbstractController.REDIRECT_PREFIX + AbstractController.ROOT,
				classUnderTest.getConfigurationOverview(CONFIG_ID, model, request));
		verify(configFacade, times(0)).getConfiguration(any(ConfigurationData.class));
	}

	@Test
	public void testHandleCPQActionNull() throws BusinessException
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		overviewUIData.setCpqAction(CPQOverviewActionType.TOGGLE_IMAGE_GALLERY);
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setProductCode(PRODUCT_CODE);
		final UiStatus uiStatus = new UiStatus();

		classUnderTest.handleCPQAction(overviewUIData, configOverviewData, uiStatus);
		verify(sessionAccessFacade).setUiStatusForCartEntry(anyString(), eq(uiStatus));
	}

	@Test
	public void testHandleCPQActionToggleImageGallery() throws BusinessException
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		overviewUIData.setCpqAction(CPQOverviewActionType.TOGGLE_IMAGE_GALLERY);
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setProductCode(PRODUCT_CODE);
		final UiStatus uiStatus = new UiStatus();
		uiStatus.setHideImageGallery(false);

		classUnderTest.handleCPQAction(overviewUIData, configOverviewData, uiStatus);

		assertTrue(uiStatus.isHideImageGallery());
		verify(sessionAccessFacade).setUiStatusForCartEntry(anyString(), eq(uiStatus));
	}

	@Test
	public void testHandleCPQActionAppliedFilter() throws BusinessException
	{
		final OverviewUiData overviewUIData = new OverviewUiData();
		overviewUIData.setCpqAction(CPQOverviewActionType.APPLY_FILTER);
		final ConfigurationOverviewData configOverviewData = new ConfigurationOverviewData();
		configOverviewData.setProductCode(PRODUCT_CODE);
		final UiStatus uiStatus = new UiStatus();
		final List<FilterData> maxFilterDataList = new ArrayList<FilterData>();
		uiStatus.setMaxGroupFilterList(maxFilterDataList);

		final List<FilterData> csticFilterList = new ArrayList<FilterData>();
		csticFilterList.add(createFilterData(FilterEnum.PRICE_RELEVANT.toString(), "Price relevant", true));
		overviewUIData.setCsticFilterList(csticFilterList);

		classUnderTest.handleCPQAction(overviewUIData, configOverviewData, uiStatus);

		verify(sessionAccessFacade).setUiStatusForCartEntry(anyString(), eq(uiStatus));
		final List appliedFilters = configOverviewData.getAppliedCsticFilters();
		assertTrue(appliedFilters.size() == 2);
		assertTrue(FilterEnum.PRICE_RELEVANT.equals(appliedFilters.get(1)));
	}
	
	@Test
	public void testPopulateConfigurationModel( ) {
		given(configurationOverviewFacade.getOverviewForConfiguration(eq(CONFIG_ID), any())).willReturn(configOverviewData);
		ConfigurationOverviewData result = classUnderTest.populateConfigurationModel(null, CONFIG_ID, configOverviewData );
		assertNotNull(result);
		assertEquals(configOverviewData, result);
	}
}

