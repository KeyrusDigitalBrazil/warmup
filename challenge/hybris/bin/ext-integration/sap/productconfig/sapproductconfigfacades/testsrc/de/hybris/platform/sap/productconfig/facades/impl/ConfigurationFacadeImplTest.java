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
package de.hybris.platform.sap.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.daos.ProductDao;
import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationTestData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.FirstOrLastGroupType;
import de.hybris.platform.sap.productconfig.facades.GroupType;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.PricingData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.facades.populator.SolvableConflictPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationNotFoundException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConflictingAssumptionModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.SolvableConflictModelImpl;
import de.hybris.platform.sap.productconfig.services.analytics.intf.AnalyticsService;
import de.hybris.platform.sap.productconfig.services.impl.ConfigurationVariantUtilImpl;
import de.hybris.platform.sap.productconfig.services.intf.PricingService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationAbstractOrderEntryLinkStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationClassificationCacheStrategy;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationProductLinkStrategy;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.Session;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
@SuppressWarnings("javadoc")
public class ConfigurationFacadeImplTest
{
	private static final String PRODUCT_CODE = "SAP_SIMPLE_POC";

	private static final String instanceId = "3";
	private static final String instanceId2 = "4";
	private static final String csticName = "CsticName";
	private static final String CONFIG_ID = "1";
	private static final String CONFIG_ID2 = "2";

	@Mock
	private CommerceCartService commerceCartServiceMock;
	@Mock
	private ProductConfigurationService configServiceMock;
	@Mock
	private CartService cartServiceMock;
	@Mock
	private ModelService modelServiceMock;
	@Mock
	private CartEntryModel otherCartItemMock;
	@Mock
	private ProductDao productDaoMock;
	@Mock
	private ConfigPricing configPricingMock;
	@Mock
	private ProductService productServiceMock;
	@Mock
	private ConfigurationVariantUtilImpl configurationVariantUtilMock;
	@Mock
	private AnalyticsService analyticsServiceMock;
	@Mock
	private ProviderFactory providerFactoryMock;
	@Mock
	private PricingConfigurationParameter pricingConfigurationParametersMock;
	@Mock
	private BaseStoreService baseStoreServiceMock;
	@Mock
	public ConfigurationClassificationCacheStrategy configurationClassificationCacheStrategyMock;
	@Mock
	public PricingService pricingServiceMock;
	@Mock
	private Session sessionMock;
	@Mock
	private ConfigurationProductLinkStrategy configurationProductLinkStrategyMock;
	@Mock
	private ConfigurationAbstractOrderEntryLinkStrategy orderEntryLinkStrategyMock;
	@Mock
	private ERPVariantProductModel variantProductModel;

	private ConfigurationFacadeImpl configFacade;
	private KBKeyData kbKey;
	private CartModel shoppingCart;
	private ProductModel product;
	private UnitModel unit;
	private ConfigurationData configData;
	private final List<AbstractOrderEntryModel> itemsInCart = new ArrayList<>();
	private CommerceCartModification modification;

	private final List<InstanceModel> subInstances = new ArrayList<>();
	private final InstanceModel subInstance1 = new InstanceModelImpl();
	private final InstanceModel subInstance2 = new InstanceModelImpl();
	private final List<InstanceModel> grandChildInstances1 = new ArrayList<>();
	private final InstanceModel grandChildInstance1 = new InstanceModelImpl();
	private final InstanceModel grandChildInstance2 = new InstanceModelImpl();
	private final List<InstanceModel> grandChildInstances2 = new ArrayList<>();
	private final InstanceModel grandChildInstance3 = new InstanceModelImpl();
	private InstanceModel instance;

	private final UiGroupData uiGroup = new UiGroupData();
	private final List<CsticData> cstics = new ArrayList<>();
	private final CsticData cstic = new CsticData();
	private final CsticModel csticModel = new CsticModelImpl();

	private final ConfigModel configModelConflicts = new ConfigModelImpl();
	private final List<SolvableConflictModel> solvableConflicts = new ArrayList<>();
	private final SolvableConflictModel solvableConflict = new SolvableConflictModelImpl();
	private final SolvableConflictModel solvableConflict2 = new SolvableConflictModelImpl();
	private final List<ConflictingAssumptionModel> assumptions = new ArrayList<>();
	private final ConflictingAssumptionModel conflictingAssumption = new ConflictingAssumptionModelImpl();
	private final UniqueUIKeyGeneratorImpl uiKeyGenerator = new UniqueUIKeyGeneratorImpl();

	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		final CsticTypeMapperImpl typeMapper = new CsticTypeMapperImpl();
		typeMapper.setUiTypeFinder(new UiTypeFinderImpl());
		final ValueFormatTranslator valueFormatTranslator = new ValueFormatTranslatorImpl();
		typeMapper.setValueFormatTranslator(valueFormatTranslator);
		typeMapper.setUiKeyGenerator(uiKeyGenerator);
		final IntervalInDomainHelperImpl intervalHandler = new IntervalInDomainHelperImpl();
		intervalHandler.setValueFormatTranslator(valueFormatTranslator);
		typeMapper.setMessagesMapper(new ConfigurationMessageMapperImpl());
		typeMapper.setIntervalHandler(intervalHandler);
		typeMapper.setProviderFactory(providerFactoryMock);
		typeMapper.setPricingFactory(configPricingMock);
		final ConfigConsistenceChecker configConsistenceChecker = new ConfigConsistenceCheckerImpl();

		configFacade = new ConfigurationFacadeImpl();
		configFacade.setConfigurationService(configServiceMock);
		configFacade.setCsticTypeMapper(typeMapper);
		configFacade.setConfigConsistenceChecker(configConsistenceChecker);
		configFacade.setProductDao(productDaoMock);
		configFacade.setConfigPricing(configPricingMock);
		configFacade.setConflictPopulator(new SolvableConflictPopulator());
		configFacade.setConfigurationVariantUtil(configurationVariantUtilMock);
		configFacade.setUiKeyGenerator(uiKeyGenerator);
		configFacade.setAnalyticsService(analyticsServiceMock);
		configFacade.setProductLinkStrategy(configurationProductLinkStrategyMock);
		configFacade.setMessagesMapper(new ConfigurationMessageMapperImpl());
		configFacade.setConfigurationAbstractOrderEntryLinkStrategy(orderEntryLinkStrategyMock);
		configFacade.setPricingService(pricingServiceMock);
		configFacade.setClassificationCacheStrategy(configurationClassificationCacheStrategyMock);

		given(providerFactoryMock.getPricingParameter()).willReturn(pricingConfigurationParametersMock);
		given(pricingConfigurationParametersMock.showDeltaPrices()).willReturn(true);
		given(Boolean.valueOf(pricingServiceMock.isActive())).willReturn(Boolean.FALSE);

		kbKey = new KBKeyData();
		kbKey.setProductCode(PRODUCT_CODE);
		kbKey.setKbName("YSAP_SIMPLE_POC");
		kbKey.setKbLogsys("ABC");
		kbKey.setKbVersion("123");

		shoppingCart = new CartModel();
		shoppingCart.setEntries(itemsInCart);
		product = new ProductModel();
		unit = new UnitModel();

		product.setCode(PRODUCT_CODE);
		product.setUnit(unit);

		configData = new ConfigurationData();
		configData.setKbKey(kbKey);

		final PricingData pricingData = new PricingData();
		pricingData.setBasePrice(ConfigPricing.NO_PRICE);
		pricingData.setSelectedOptions(ConfigPricing.NO_PRICE);
		pricingData.setCurrentTotal(ConfigPricing.NO_PRICE);

		given(cartServiceMock.getSessionCart()).willReturn(shoppingCart);
		given(otherCartItemMock.getPk()).willReturn(PK.parse("1234567890"));
		given(configPricingMock.getPricingData(any(ConfigModel.class))).willReturn(pricingData);

		final AbstractOrderEntryModel cartItem = new CartEntryModel();
		cartItem.setProduct(product);
		modification = new CommerceCartModification();
		modification.setEntry(cartItem);

		given(productDaoMock.findProductsByCode(any(String.class))).willReturn(null);

		/*
		 * we use mock instead of real class, because calling baseStore.getName() would lead to illegalArgument Exception,
		 * as no LocalProvider is injected into the item Context. baseStore.getName() is used in some debug statetments,
		 * so when running tests with log level debug this would lead to an exception
		 */
		final BaseStoreModel baseStore = Mockito.mock(BaseStoreModel.class);
		given(baseStore.getCatalogs()).willReturn(Collections.EMPTY_LIST);
		given(baseStoreServiceMock.getCurrentBaseStore()).willReturn(baseStore);

		final ClassificationSystemCPQAttributesProviderImpl nameProvider = new ClassificationSystemCPQAttributesProviderImpl();
		nameProvider.setBaseStoreService(baseStoreServiceMock);
		typeMapper.setNameProvider(nameProvider);

		given(productServiceMock.getProductForCode(PRODUCT_CODE)).willReturn(product);
		configFacade.setProductService(productServiceMock);

		given(configServiceMock.createDefaultConfiguration(
				new KBKeyImpl(kbKey.getProductCode(), kbKey.getKbName(), kbKey.getKbLogsys(), kbKey.getKbVersion())))
						.willReturn(configModelConflicts);
	}

	@Test
	public void testRetrieveRelatedInstanceModelForNullGroup() throws Exception
	{
		final InstanceModel instance = null;
		final UiGroupData uiSubGroup = new UiGroupData();
		final InstanceModel instModel = configFacade.retrieveRelatedInstanceModel(instance, uiSubGroup);
		assertNull(instModel);
	}

	@Test
	public void testGetConfiguration() throws Exception
	{
		initializeFirstCall();
		ConfigurationData configContent = configFacade.getConfiguration(kbKey);

		assertNotNull(configContent);
		assertEquals(PRODUCT_CODE, configContent.getKbKey().getProductCode());

		configContent = configFacade.getConfiguration(configContent);
		assertNotNull(configContent);
		assertEquals(PRODUCT_CODE, configContent.getKbKey().getProductCode());
	}

	@Test
	public void testGetConfigurationProductLinkAvailable() throws Exception
	{
		initializeFirstCall();
		when(configurationProductLinkStrategyMock.getConfigIdForProduct(PRODUCT_CODE)).thenReturn(CONFIG_ID);
		final ConfigurationData configContent = configFacade.getConfiguration(kbKey);

		assertNotNull(configContent);
		assertEquals(PRODUCT_CODE, configContent.getKbKey().getProductCode());
	}

	@Test
	public void testGetConfigurationCreatesProductLink() throws Exception
	{
		initializeFirstCall();
		final ConfigurationData configContent = configFacade.getConfiguration(kbKey);
		verify(configurationProductLinkStrategyMock).setConfigIdForProduct(PRODUCT_CODE, CONFIG_ID);
	}

	@Test
	public void testGetConfigurationProductAndCartLinkAvailable() throws Exception
	{
		initializeFirstCall();
		when(configurationProductLinkStrategyMock.getConfigIdForProduct(PRODUCT_CODE)).thenReturn(CONFIG_ID2);
		when(orderEntryLinkStrategyMock.getCartEntryForConfigId(CONFIG_ID2)).thenReturn("123");
		final ConfigurationData configContent = configFacade.getConfiguration(kbKey);

		assertNotNull(configContent);
		assertEquals(CONFIG_ID, configContent.getConfigId());
		verify(configurationProductLinkStrategyMock).setConfigIdForProduct(PRODUCT_CODE, CONFIG_ID);
	}

	@Test
	public void testUpdateConfiguration() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCall();
		final ConfigurationData configContentBeforeUpdate = configFacade.getConfiguration(kbKey);
		assertNotNull(configContentBeforeUpdate);

		CsticData csticDataToUpdate = getCsticToUpdate(configContentBeforeUpdate);
		assertNotNull("Cstic '" + ConfigurationTestData.STR_NAME + "' not found in test data", csticDataToUpdate);
		csticDataToUpdate.setValue("This value has been updated");

		configFacade.updateConfiguration(configContentBeforeUpdate);

		final ConfigurationData configContentAfterUpdate = configFacade.getConfiguration(configContentBeforeUpdate);
		Mockito.verify(configServiceMock).updateConfiguration(createdConfigModel);
		assertNotNull(configContentAfterUpdate);

		csticDataToUpdate = getCsticToUpdate(configContentAfterUpdate);
		assertNotNull("Cstic '" + ConfigurationTestData.STR_NAME + "' not found in test data", csticDataToUpdate);
		assertEquals("This value has been updated", csticDataToUpdate.getValue());
	}

	@Test
	public void testUpdateConfigurationInvalidGroupType()
	{
		initializeFirstCall();
		final ConfigurationData configContentBeforeUpdate = configFacade.getConfiguration(kbKey);
		assertNotNull(configContentBeforeUpdate);
		configContentBeforeUpdate.getGroups().get(0).setGroupType(null);
		//Test: this goes through even if group type is null
		configFacade.updateConfiguration(configContentBeforeUpdate);
	}

	@Test
	public void testShowLegendInConfiguration() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCall();
		final ConfigurationData configData = configFacade.getConfiguration(kbKey);
		assertNotNull(configData);
		final CsticModel cstic = createdConfigModel.getRootInstance().getCstic(ConfigurationTestData.STR_NAME);
		cstic.setRequired(true);
		assertTrue("Cstic '" + ConfigurationTestData.STR_NAME + "' is requiered, the legend should be shown",
				configData.isShowLegend());
	}

	@Test
	public void testShowLegendInConfigurationWithSubInstances() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithGroupsAndSubInstancies();
		List<CsticModel> cstics = createdConfigModel.getRootInstance().getCstics();
		setCsticsNotRequired(cstics);
		cstics = createdConfigModel.getRootInstance().getSubInstances().get(0).getCstics();
		cstics.get(0).setRequired(true);

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);
		assertNotNull(configData);
		assertTrue("Cstic GROUP1INST1.SAP_STRING_SIMPLE is requiered, the legend should be shown", configData.isShowLegend());
	}


	@Test
	public void testNotShowLegendInConfigurationWithSubInstances() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithGroupsAndSubInstancies();
		List<CsticModel> cstics = createdConfigModel.getRootInstance().getCstics();
		setCsticsNotRequired(cstics);
		cstics = createdConfigModel.getRootInstance().getSubInstances().get(0).getCstics();
		setCsticsNotRequired(cstics);
		cstics = createdConfigModel.getRootInstance().getSubInstances().get(0).getSubInstances().get(0).getCstics();
		setCsticsNotRequired(cstics);

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);
		assertNotNull(configData);
		assertFalse("Cstics are not requiered, the legend should not be shown", configData.isShowLegend());
	}

	protected void setCsticsNotRequired(final List<CsticModel> cstics)
	{
		for (final CsticModel cstic : cstics)
		{
			cstic.setRequired(false);
		}
	}

	@Test
	public void testNotShowLegendInConfiguration() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCall();
		final CsticModel cstic = createdConfigModel.getRootInstance().getCstic(ConfigurationTestData.STR_NAME);
		cstic.setRequired(false);

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);
		assertNotNull(configData);

		assertFalse("Cstic '" + ConfigurationTestData.STR_NAME + "' is optional, the legend should not be shown",
				configData.isShowLegend());
	}

	@Test
	public void testGroups() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithGroups();
		final ConfigurationData configData = configFacade.getConfiguration(kbKey);

		final InstanceModel rootMInstance = createdConfigModel.getRootInstance();
		final List<CsticGroupModel> csticModelGroups = rootMInstance.getCsticGroups();
		final List<UiGroupData> csticDataGroups = configData.getGroups();

		assertEquals(csticModelGroups.size(), csticDataGroups.size());

		final CsticGroupModel csticMGroup1 = csticModelGroups.get(0);
		final UiGroupData csticDGroup1 = csticDataGroups.get(0);
		final String expectedGroupId = uiKeyGenerator.generateGroupIdForGroup(rootMInstance,
				rootMInstance.retrieveCsticGroupsWithCstics().get(0));
		assertEquals(expectedGroupId, csticDGroup1.getId());
		assertEquals(csticMGroup1.getDescription(), csticDGroup1.getDescription());

		final List<String> csticNames = csticMGroup1.getCsticNames();
		final List<CsticData> cstics = csticDGroup1.getCstics();
		// one cstic is not visible - this was filtered during update
		assertEquals(csticNames.size() - 1, cstics.size());
		assertEquals(csticNames.get(0), cstics.get(0).getName());
	}

	@Test
	public void testEmptyGroups() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithGroups();

		final CsticGroupModel emptyGroup = new CsticGroupModelImpl();
		emptyGroup.setName("EMPTY");
		emptyGroup.setDescription("Empty Group");
		final List<CsticGroupModel> csticGroupModels = createdConfigModel.getRootInstance().getCsticGroups();
		csticGroupModels.add(emptyGroup);
		createdConfigModel.getRootInstance().setCsticGroups(csticGroupModels);

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);

		final List<CsticGroupModel> csticModelGroups = createdConfigModel.getRootInstance().getCsticGroups();
		final List<UiGroupData> csticDataGroups = configData.getGroups();
		assertEquals(csticModelGroups.size() - 1, csticDataGroups.size());
	}

	@Test
	public void testEmptyInstance() throws Exception
	{
		initializeFirstCallWithEmptyInstance();

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);
		final List<UiGroupData> csticDataGroups = configData.getGroups();
		assertEquals("Root missing", 1, csticDataGroups.size());
		final UiGroupData root = csticDataGroups.get(0);
		assertEquals("Root is empty", 0, root.getCstics().size());
	}

	@Test
	public void testGroupWithNotVisibleCstics() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithGroups();

		final CsticGroupModel notVisibleGroup = new CsticGroupModelImpl();
		notVisibleGroup.setName("NOTVISIBLEGROUP");
		notVisibleGroup.setDescription("This group contains only a not visible cstic");

		final List<String> notVisibleCstics = new ArrayList<>();
		notVisibleCstics.add("NOTVISIBLE");
		notVisibleGroup.setCsticNames(notVisibleCstics);

		final CsticModel notVisibleCstic = new CsticModelImpl();
		notVisibleCstic.setName("NOTVISIBLE");
		notVisibleCstic.setVisible(false);
		notVisibleCstic.setConsistent(true);
		createdConfigModel.getRootInstance().addCstic(notVisibleCstic);

		final List<CsticGroupModel> csticGroups = createdConfigModel.getRootInstance().getCsticGroups();
		csticGroups.add(notVisibleGroup);
		createdConfigModel.getRootInstance().setCsticGroups(csticGroups);

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);

		final List<CsticGroupModel> csticModelGroups = createdConfigModel.getRootInstance().getCsticGroups();
		final List<UiGroupData> csticDataGroups = configData.getGroups();
		assertEquals(csticModelGroups.size() - 1, csticDataGroups.size());
	}

	private CsticData getCsticToUpdate(final ConfigurationData configContentBeforeUpdate)
	{
		CsticData csticDataToUpdate = null;
		for (final CsticData csticData : configContentBeforeUpdate.getGroups().get(0).getCstics())
		{
			if (csticData.getName().equals(ConfigurationTestData.STR_NAME))
			{
				csticDataToUpdate = csticData;
			}
		}
		return csticDataToUpdate;
	}

	@Test
	public void testGroupAndEmptySubInstance() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithGroupAndInstance();

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);

		final List<InstanceModel> modelInstances = createdConfigModel.getRootInstance().getSubInstances();
		assertEquals("1 instance ", 1, modelInstances.size());

		final List<UiGroupData> uiGroups = configData.getGroups();
		assertEquals("2 UiGroups: 1 istances and 1 groups", 2, uiGroups.size());
		assertEquals(modelInstances.get(0).getName(), uiGroups.get(1).getName());
		assertEquals("[" + modelInstances.get(0).getName() + "]", uiGroups.get(1).getDescription());
	}

	@Test
	public void testGroupsAndSubInstanceHierarhy() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithGroupsAndSubInstancies();

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);

		final List<InstanceModel> modelInstances = createdConfigModel.getRootInstance().getSubInstances();
		assertEquals("2 instance on the fist level", 2, modelInstances.size());

		final List<UiGroupData> uiGroups = configData.getGroups();
		assertEquals("4 UiGroups: 2 istances and 2 groups", 4, uiGroups.size());

		final List<InstanceModel> subInstances = modelInstances.get(0).getSubInstances();
		final List<UiGroupData> uiSubGroups = uiGroups.get(2).getSubGroups();
		assertEquals("2 istances on level 2", 2, subInstances.size());
		assertEquals("4 UiGroups: 2 istances and 2 groups on level 2", 4, uiSubGroups.size());

		final List<InstanceModel> subSubInstances = subInstances.get(0).getSubInstances();
		final List<UiGroupData> uiSubSubGroups = uiSubGroups.get(2).getSubGroups();
		assertEquals("1 istances on level 3", 1, subSubInstances.size());
		assertEquals("1 UiGroups: 1 istances on level 3", 2, uiSubSubGroups.size());
	}

	@Test
	public void testTypeGroupOrTypeInstace() throws Exception
	{
		initializeFirstCallWithGroupsAndSubInstancies();

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);

		final List<UiGroupData> uiGroups = configData.getGroups();
		assertEquals("UIGroup has to have type instance", GroupType.INSTANCE, uiGroups.get(3).getGroupType());
		assertEquals("UIGroup has to have type instance", GroupType.INSTANCE, uiGroups.get(2).getGroupType());
		assertEquals("UIGroup has to have type group", GroupType.CSTIC_GROUP, uiGroups.get(1).getGroupType());
		assertEquals("UIGroup has to have type group", GroupType.CSTIC_GROUP, uiGroups.get(0).getGroupType());
	}

	@Test
	public void testNonConfigurableInstance() throws Exception
	{
		initializeFirstCallWithGroupsAndSubInstancies();

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);
		final List<UiGroupData> uiGroups = configData.getGroups();
		assertTrue("Ui Group should be configurable", uiGroups.get(0).isConfigurable());
		assertTrue("Ui Group should be configurable", uiGroups.get(1).isConfigurable());
		assertTrue("Ui Group should be configurable", uiGroups.get(2).isConfigurable());
		assertFalse("Ui Group should be non configurable", uiGroups.get(3).isConfigurable());
		assertTrue("Ui Group should be configurable", uiGroups.get(2).getSubGroups().get(0).isConfigurable());
		assertTrue("Ui Group should be configurable", uiGroups.get(2).getSubGroups().get(1).isConfigurable());
		assertTrue("Ui Group should be  configurable", uiGroups.get(2).getSubGroups().get(2).isConfigurable());
		assertFalse("Ui Group should be non configurable", uiGroups.get(2).getSubGroups().get(3).isConfigurable());
	}

	@Test
	public void testOneConfigurableSubGroup_noSubGroups() throws Exception
	{
		initializeFirstCallWithGroups();

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);
		final List<UiGroupData> uiGroups = configData.getGroups();
		assertNull("There is no subgroups", uiGroups.get(0).getSubGroups());
		assertFalse("There is no subgroups", uiGroups.get(0).isOneConfigurableSubGroup());
	}

	@Test
	public void testOneConfigurableSubGroup_onlyOne() throws Exception
	{
		initializeFirstCallWithGroupsAndSubInstancies();

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);
		final List<UiGroupData> uiGroups = configData.getGroups();
		final UiGroupData uiGroupData = uiGroups.get(2).getSubGroups().get(2);
		final List<UiGroupData> subGroups = uiGroups.get(2).getSubGroups().get(2).getSubGroups();
		assertEquals("There are two subgroups", 2, subGroups.size());
		assertTrue("First group is configurable", subGroups.get(0).isConfigurable());
		assertFalse("Secodn group is non configurable", subGroups.get(1).isConfigurable());
		assertTrue("There is only one non configurable subgroup", uiGroupData.isOneConfigurableSubGroup());
	}

	@Test
	public void testOneConfigurableSubGroup_MoreThenOne() throws Exception
	{
		initializeFirstCallWithGroupsAndSubInstancies();

		final ConfigurationData configData = configFacade.getConfiguration(kbKey);
		final List<UiGroupData> uiGroups = configData.getGroups();
		final List<UiGroupData> subGroups = uiGroups.get(2).getSubGroups();
		assertTrue("First group is configurable", subGroups.get(0).isConfigurable());
		assertTrue("Second group is configurable", subGroups.get(1).isConfigurable());
		assertFalse("There is two configurable subgroup", uiGroups.get(2).isOneConfigurableSubGroup());
	}

	private ConfigModel initializeFirstCall()
	{
		final ConfigModel createdConfigModel = ConfigurationTestData.createConfigModelWithCstic();
		given(configServiceMock.createDefaultConfiguration(any(KBKey.class))).willReturn(createdConfigModel);
		given(configServiceMock.retrieveConfigurationModel(createdConfigModel.getId())).willReturn(createdConfigModel);
		return createdConfigModel;
	}

	private ConfigModel initializeFirstCallWithGroups()
	{
		final ConfigModel createdConfigModel = ConfigurationTestData.createConfigModelWithGroups();
		given(configServiceMock.createDefaultConfiguration(any(KBKey.class))).willReturn(createdConfigModel);
		given(configServiceMock.retrieveConfigurationModel(createdConfigModel.getId())).willReturn(createdConfigModel);
		return createdConfigModel;
	}

	private ConfigModel initializeFirstCallWithOneGroup()
	{
		final ConfigModel createdConfigModel = ConfigurationTestData.createConfigModelWithOneGroup();
		given(configServiceMock.createDefaultConfiguration(any(KBKey.class))).willReturn(createdConfigModel);
		given(configServiceMock.retrieveConfigurationModel(createdConfigModel.getId())).willReturn(createdConfigModel);
		return createdConfigModel;
	}

	private ConfigModel initializeFirstCallWithEmptyInstance()
	{
		final ConfigModel createdConfigModel = ConfigurationTestData.createConfigModelWithSubInstanceOnly();
		given(configServiceMock.createDefaultConfiguration(any(KBKey.class))).willReturn(createdConfigModel);
		given(configServiceMock.retrieveConfigurationModel(createdConfigModel.getId())).willReturn(createdConfigModel);
		return createdConfigModel;
	}

	private ConfigModel initializeFirstCallWithGroupAndInstance()
	{
		final ConfigModel createdConfigModel = ConfigurationTestData.createConfigModelWithSubInstance();
		given(configServiceMock.createDefaultConfiguration(any(KBKey.class))).willReturn(createdConfigModel);
		given(configServiceMock.retrieveConfigurationModel(createdConfigModel.getId())).willReturn(createdConfigModel);
		return createdConfigModel;
	}

	private ConfigModel initializeFirstCallWithGroupsAndSubInstancies()
	{
		final ConfigModel createdConfigModel = ConfigurationTestData.createConfigModelWithGroupsAndSubInstances();
		given(configServiceMock.createDefaultConfiguration(any(KBKey.class))).willReturn(createdConfigModel);
		given(configServiceMock.retrieveConfigurationModel(createdConfigModel.getId())).willReturn(createdConfigModel);
		return createdConfigModel;
	}

	@Test
	public void testUpdateUiGroups2()
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithGroupsAndSubInstancies();
		final InstanceModel rootInstance = createdConfigModel.getRootInstance();
		assertNotNull("Root instance is null: ", rootInstance);
		final ConfigurationData configContentBeforeUpdate = configFacade.getConfiguration(kbKey);
		assertNotNull("CofigurationData 'configContentBeforeUpdate' is emtpy: ", configContentBeforeUpdate);

		final String instId = rootInstance.getSubInstances().get(0).getId();

		final String sourceValue = "This value has been updated from conflict";

		final CsticData csticDataConflict = new CsticData();
		csticDataConflict.setName(ConfigurationTestData.STR_NAME);
		csticDataConflict.setInstanceId(instId);
		csticDataConflict.setValue(sourceValue);
		csticDataConflict.setType(UiType.STRING);

		final UiGroupData conflictuiGroup = new UiGroupData();
		conflictuiGroup.setName(GroupType.CONFLICT.toString());
		conflictuiGroup.setGroupType(GroupType.CONFLICT);
		conflictuiGroup.setConfigurable(true);
		conflictuiGroup.setCollapsed(false);
		final List<CsticData> cstics = new ArrayList<>();
		cstics.add(csticDataConflict);
		conflictuiGroup.setCstics(cstics);
		conflictuiGroup.setId(GroupType.CONFLICT.toString() + "11");
		final List<UiGroupData> subGroups = new ArrayList<>();
		conflictuiGroup.setSubGroups(subGroups);

		final List<UiGroupData> conflicts = new ArrayList<>();
		conflicts.add(conflictuiGroup);

		final UiGroupData conflictHeader = new UiGroupData();
		conflictHeader.setGroupType(GroupType.CONFLICT_HEADER);
		conflictHeader.setConfigurable(true);
		conflictHeader.setCollapsed(false);
		conflictHeader.setId(GroupType.CONFLICT_HEADER.toString());
		conflictHeader.setName(GroupType.CONFLICT_HEADER.toString());
		conflictHeader.setFirstOrLastGroup(FirstOrLastGroupType.INTERJACENT);
		conflictHeader.setSubGroups(conflicts);
		conflictHeader.setCstics(new ArrayList<>());
		conflictHeader.setNumberErrorCstics(conflicts.size());
		configFacade.updateUiGroup(rootInstance, conflictHeader);

		final InstanceModel subinst = rootInstance.getSubInstance(instId);
		final CsticModel targetCstic = subinst.getCstic(ConfigurationTestData.STR_NAME);

		final String targetValue = targetCstic.getSingleValue();

		assertEquals(sourceValue, targetValue);
	}

	@Test
	public void testUpdateMultiLevelConfiguration() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithGroupsAndSubInstancies();
		final ConfigurationData configContentBeforeUpdate = configFacade.getConfiguration(kbKey);
		assertNotNull(configContentBeforeUpdate);

		// Get cstic STR_NAME from root instance GROUP1
		CsticData csticDataToUpdate = getCsticToUpdate(configContentBeforeUpdate);
		assertNotNull("Cstic '" + ConfigurationTestData.STR_NAME + "' not found in test data", csticDataToUpdate);
		csticDataToUpdate.setValue("This value has been updated");


		// Get cstic STR_NAME from SI1 GROUP1
		final int[] groupPath =
		{ 2, 0 };
		CsticData csticDataToUpdateSI1 = getCsticToUpdate(configContentBeforeUpdate, groupPath, ConfigurationTestData.STR_NAME);
		assertNotNull("Cstic '" + ConfigurationTestData.STR_NAME + "' not found in test data in Subinstance", csticDataToUpdateSI1);
		csticDataToUpdateSI1.setValue("This value has been updated");

		configFacade.updateConfiguration(configContentBeforeUpdate);

		final ConfigurationData configContentAfterUpdate = configFacade.getConfiguration(configContentBeforeUpdate);
		Mockito.verify(configServiceMock).updateConfiguration(createdConfigModel);
		assertNotNull(configContentAfterUpdate);

		// Check cstic STR_NAME from root instance GROUP1
		csticDataToUpdate = getCsticToUpdate(configContentAfterUpdate);
		assertNotNull("Cstic '" + ConfigurationTestData.STR_NAME + "' not found in test data", csticDataToUpdate);
		assertEquals("This value has been updated", csticDataToUpdate.getValue());

		// Check cstic STR_NAME from SI1 GROUP1
		csticDataToUpdateSI1 = getCsticToUpdate(configContentBeforeUpdate, groupPath, ConfigurationTestData.STR_NAME);
		assertNotNull("Cstic '" + ConfigurationTestData.STR_NAME + "' not found in test data", csticDataToUpdateSI1);
		assertEquals("This value has been updated", csticDataToUpdateSI1.getValue());
	}

	private CsticData getCsticToUpdate(final ConfigurationData configContentBeforeUpdate, final int groupPath[],
			final String csticName)
	{
		CsticData csticDataToUpdate = null;

		List<UiGroupData> groups = configContentBeforeUpdate.getGroups();

		//navigate to the ui group
		UiGroupData groupData = null;
		for (int i = 0; i < groupPath.length; i++)
		{
			groupData = groups.get(groupPath[i]);
			groups = groupData.getSubGroups();
		}

		// retrieve cstic
		if (groupData != null)
		{
			for (final CsticData csticData : groupData.getCstics())
			{
				if (csticData.getName().equals(csticName))
				{
					csticDataToUpdate = csticData;
				}
			}
		}

		return csticDataToUpdate;
	}

	@Test
	public void testPrevNextFlagsOnGroupsSingleLevel() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithGroups();
		final List<UiGroupData> csticGroupsFlat = new ArrayList<>();
		final List<UiGroupData> groups = configFacade.getCsticGroupsFromModel(createdConfigModel, csticGroupsFlat);
		configData.setCsticGroupsFlat(csticGroupsFlat);
		configData.setGroups(groups);
		configFacade.finalizeUiGroups(configData, createdConfigModel);
		assertEquals("There should be two groups", 2, groups.size());
		final UiGroupData firstGroup = groups.get(0);
		final UiGroupData secondGroup = groups.get(1);
		assertEquals("First group should be marked with FIRST", FirstOrLastGroupType.FIRST, firstGroup.getFirstOrLastGroup());
		assertEquals("Second group should be marked with LAST", FirstOrLastGroupType.LAST, secondGroup.getFirstOrLastGroup());
	}

	@Test
	public void testPrevNextFlagsOnGroupsMultiLevel() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithGroupsAndSubInstancies();
		final List<UiGroupData> csticGroupsFlat = new ArrayList<>();
		final List<UiGroupData> groups = configFacade.getCsticGroupsFromModel(createdConfigModel, csticGroupsFlat);
		configData.setCsticGroupsFlat(csticGroupsFlat);
		configData.setGroups(groups);
		configFacade.finalizeUiGroups(configData, createdConfigModel);
		assertEquals("There should be four groups", 4, groups.size());
		final UiGroupData firstGroup = groups.get(0);
		assertEquals("First group should be marked with FIRST", FirstOrLastGroupType.FIRST, firstGroup.getFirstOrLastGroup());
		final UiGroupData subInstance1Level1Group = groups.get(2);
		final UiGroupData subInstance1Level1FirstGroup = subInstance1Level1Group.getSubGroups().get(0);
		assertEquals("First group of SUBINSTANCE1LEVEL1 should be marked with INTERJACENT", FirstOrLastGroupType.INTERJACENT,
				subInstance1Level1FirstGroup.getFirstOrLastGroup());
		final UiGroupData subInstance1Level2Group = subInstance1Level1Group.getSubGroups().get(2);
		final UiGroupData subInstance1Level2GeneralGroup = subInstance1Level2Group.getSubGroups().get(0);

		assertEquals("General group of SUBINSTANCE1LEVEL2 should be marked with LAST", FirstOrLastGroupType.LAST,
				subInstance1Level2GeneralGroup.getFirstOrLastGroup());
	}

	@Test
	public void testPrevNextFlagsOnlyOneGroup() throws Exception
	{
		final ConfigModel createdConfigModel = initializeFirstCallWithOneGroup();
		final List<UiGroupData> csticGroupsFlat = new ArrayList<>();
		final List<UiGroupData> groups = configFacade.getCsticGroupsFromModel(createdConfigModel, csticGroupsFlat);
		configData.setCsticGroupsFlat(csticGroupsFlat);
		configData.setGroups(groups);
		configFacade.finalizeUiGroups(configData, createdConfigModel);
		assertEquals("There should be one group", 1, groups.size());
		final UiGroupData firstGroup = groups.get(0);
		assertEquals("First group should be marked with ONLYNONE as it is the only one", FirstOrLastGroupType.ONLYONE,
				firstGroup.getFirstOrLastGroup());
	}

	@Test
	public void testGetSubInstanceFindRoot()
	{
		final InstanceModel instance = new InstanceModelImpl();
		instance.setId(instanceId);
		assertNotNull("We expect to find root instance", configFacade.getSubInstance(instance, instanceId));
	}

	@Test
	public void testGetSubInstanceDontFindRoot()
	{
		final InstanceModel instance = new InstanceModelImpl();
		instance.setId(instanceId2);
		assertNull("We must not find root instance since id's do not match", configFacade.getSubInstance(instance, instanceId));
	}

	@Test
	public void testGetSubInstanceMultiLevelInstIdsNull()
	{
		prepareMultiLevelStructure();
		checkNothingFound();
	}

	@Test
	public void testGetSubInstanceMultiLevelInstIdsNoMatch()
	{
		prepareMultiLevelStructure();
		subInstance1.setId(instanceId2);
		checkNothingFound();
	}

	@Test
	public void testGetSubInstanceMultiLevelInstIdsNoMatchGrand()
	{
		prepareMultiLevelStructure();
		grandChildInstance1.setId(instanceId2);
		checkNothingFound();
	}

	@Test
	public void testGetSubInstanceMultiLevelChild()
	{
		prepareMultiLevelStructure();
		subInstance2.setId(instanceId);
		final InstanceModel instanceFound = configFacade.getSubInstance(instance, instanceId);
		assertNotNull("We expect to find a sub instance", instanceFound);
		assertEquals(subInstance2, instanceFound);
	}

	@Test
	public void testGetSubInstanceMultiLevelGrandChild1()
	{
		prepareMultiLevelStructure();
		grandChildInstance1.setId(instanceId);
		final InstanceModel instanceFound = configFacade.getSubInstance(instance, instanceId);
		assertNotNull("We expect to find a grand child instance", instanceFound);
		assertEquals(grandChildInstance1, instanceFound);
	}

	@Test
	public void testGetSubInstanceMultiLevelGrandChild3()
	{
		prepareMultiLevelStructure();
		grandChildInstance3.setId(instanceId);
		final InstanceModel instanceFound = configFacade.getSubInstance(instance, instanceId);
		assertNotNull("We expect to find a grand child instance", instanceFound);
		assertEquals(grandChildInstance3, instanceFound);
	}

	@Test(expected = IllegalStateException.class)
	public void testUpdateConflictGroupNoMatchingInstance()
	{
		prepareMultiLevelStructure();
		uiGroup.setCstics(cstics);
		cstics.add(cstic);
		cstic.setInstanceId(instanceId);
		//we expect an exception as we don't find a matching instance!!
		configFacade.updateConflictGroup(instance, uiGroup);
	}

	@Test
	public void testUpdateConflictGroupNullCsticList()
	{
		uiGroup.setCstics(null);
		//This is an allowed situation for conflicts without
		//conflicting assumptions. Test is that no exception
		//occurs
		configFacade.updateConflictGroup(null, uiGroup);
	}

	@Test(expected = IllegalStateException.class)
	public void testUpdateConflictGroupNoInstanceIdAtAll()
	{
		prepareMultiLevelStructure();
		uiGroup.setCstics(cstics);
		cstics.add(cstic);
		cstic.setInstanceId(null);
		//we expect an exception as we don't find a matching instance!!
		configFacade.updateConflictGroup(instance, uiGroup);
	}

	@Test(expected = IllegalStateException.class)
	public void testUpdateConflictGroupNoCsticAtInstance()
	{
		prepareMultiLevelStructure();
		uiGroup.setCstics(cstics);
		cstics.add(cstic);
		cstic.setInstanceId(instanceId2);
		cstic.setName(csticName);
		instance.addCstic(csticModel);
		csticModel.setName("This cstic does not exist");
		configFacade.updateConflictGroup(instance, uiGroup);
	}

	@Test
	public void testUpdateConflictGroup()
	{
		prepareMultiLevelStructure();
		uiGroup.setCstics(cstics);
		cstics.add(cstic);
		cstic.setInstanceId(instanceId2);
		cstic.setName(csticName);
		instance.addCstic(csticModel);
		csticModel.setName(csticName);
		//Here the test is: no exception happens, cstic model can be updated
		configFacade.updateConflictGroup(instance, uiGroup);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFinalizeUiGroupsNoGroups()
	{
		configModelConflicts.setSolvableConflicts(solvableConflicts);
		solvableConflicts.add(solvableConflict);
		//exception, as we don't have groups available
		configFacade.finalizeUiGroups(configData, configModelConflicts);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFinalizeUiGroupsNoGroupsFlat()
	{
		configModelConflicts.setSolvableConflicts(solvableConflicts);
		solvableConflicts.add(solvableConflict);
		configData.setGroups(new ArrayList<>());
		//exception, as we don't have flat groups available
		configFacade.finalizeUiGroups(configData, configModelConflicts);
	}

	@Test
	public void testFinalizeUiGroups()
	{
		prepareConflictingModel();
		configFacade.finalizeUiGroups(configData, configModelConflicts);
		final List<UiGroupData> groups = configData.getGroups();
		assertEquals("We expect one conflict group", 1, groups.size());
		final UiGroupData uiGroupData = groups.get(0);
		assertEquals("We expect conflict header", GroupType.CONFLICT_HEADER, uiGroupData.getGroupType());
		final UiGroupData uiGroupData2 = uiGroupData.getSubGroups().get(0);
		assertEquals("We expect one conflict", GroupType.CONFLICT, uiGroupData2.getGroupType());
		assertEquals("Must be first and last", FirstOrLastGroupType.ONLYONE, uiGroupData2.getFirstOrLastGroup());
	}

	@Test
	public void testFinalizeUiGroups2Conflicts()
	{
		prepareConflictingModel();

		solvableConflicts.add(solvableConflict2);
		configData.getCsticGroupsFlat().add(uiGroup);
		configData.getGroups().add(uiGroup);
		uiGroup.setCstics(cstics);
		cstics.add(cstic);
		cstic.setInstanceId(instanceId2);
		cstic.setName(csticName);
		configFacade.finalizeUiGroups(configData, configModelConflicts);
		final List<UiGroupData> groups = configData.getGroups();
		assertEquals("We expect 1 conflict header plus a normal one", 2, groups.size());
		final UiGroupData uiGroupData = groups.get(0);
		final UiGroupData uiGroupData2 = uiGroupData.getSubGroups().get(0);
		assertEquals("We expect conflict ", GroupType.CONFLICT, uiGroupData2.getGroupType());
		assertEquals("Must be first ", FirstOrLastGroupType.FIRST, uiGroupData2.getFirstOrLastGroup());
	}

	@Test
	public void testConflictGroupProcessing()
	{
		assertTrue(configFacade.isConflictGroupProcessing());
		configFacade.setConflictGroupProcessing(false);
		assertFalse(configFacade.isConflictGroupProcessing());
	}

	@Test
	public void testProductLinkStrategy()
	{
		assertEquals(configurationProductLinkStrategyMock, configFacade.getProductLinkStrategy());
	}

	@Test
	public void testCreateConfiguration()
	{
		initializeFirstCall();
		final ConfigurationData configuration = configFacade.createConfiguration(kbKey);
		assertNotNull(configuration);
		assertEquals(PRODUCT_CODE, configuration.getKbKey().getProductCode());
	}

	@Test
	public void testGetConfigurationWConfigId()
	{
		initializeFirstCall();
		final ConfigurationData configuration = configFacade.getConfiguration(kbKey, CONFIG_ID);
		assertNotNull(configuration);
		assertEquals(PRODUCT_CODE, configuration.getKbKey().getProductCode());
	}

	@Test
	public void testReplaceProductForNotChangeableVariantChangeble()
	{
		final String VARIANT_PRODUCT_CODE = "VARIANT";
		kbKey.setProductCode(VARIANT_PRODUCT_CODE);
		given(productServiceMock.getProductForCode(VARIANT_PRODUCT_CODE)).willReturn(variantProductModel);
		given(Boolean.valueOf(configurationVariantUtilMock.isCPQNotChangeableVariantProduct(variantProductModel)))
				.willReturn(Boolean.FALSE);
		given(variantProductModel.getBaseProduct()).willReturn(product);
		configFacade.replaceProductForNotChangeableVariant(kbKey);
		assertEquals(VARIANT_PRODUCT_CODE, kbKey.getProductCode());
	}

	@Test
	public void testReplaceProductForNotChangeableVariantNotChangeble()
	{
		final String VARIANT_PRODUCT_CODE = "VARIANT";
		kbKey.setProductCode(VARIANT_PRODUCT_CODE);
		given(productServiceMock.getProductForCode(VARIANT_PRODUCT_CODE)).willReturn(variantProductModel);
		given(Boolean.valueOf(configurationVariantUtilMock.isCPQNotChangeableVariantProduct(variantProductModel)))
				.willReturn(Boolean.TRUE);
		given(variantProductModel.getBaseProduct()).willReturn(product);
		configFacade.replaceProductForNotChangeableVariant(kbKey);
		assertEquals(PRODUCT_CODE, kbKey.getProductCode());
	}

	private void prepareConflictingModel()
	{
		solvableConflicts.add(solvableConflict);
		configModelConflicts.setSolvableConflicts(solvableConflicts);
		solvableConflict.setConflictingAssumptions(assumptions);
		solvableConflict2.setConflictingAssumptions(assumptions);
		assumptions.add(conflictingAssumption);
		conflictingAssumption.setCsticName(csticName);
		conflictingAssumption.setInstanceId(instanceId2);
		configData.setGroups(new ArrayList<>());
		configData.setCsticGroupsFlat(new ArrayList());
	}

	private void checkNothingFound()
	{
		assertNull("We must not find any instance since id's do not match", configFacade.getSubInstance(instance, instanceId));
	}

	private void prepareMultiLevelStructure()
	{
		instance = new InstanceModelImpl();
		instance.setId(instanceId2);
		subInstances.add(subInstance1);
		subInstances.add(subInstance2);
		grandChildInstances1.add(grandChildInstance1);
		grandChildInstances1.add(grandChildInstance2);
		grandChildInstances2.add(grandChildInstance3);
		subInstance1.setSubInstances(grandChildInstances1);
		subInstance2.setSubInstances(grandChildInstances2);
		instance.setSubInstances(subInstances);
	}

	@Test
	public void testgetConfigurationWithFallbackNoException()
	{
		initializeFirstCall();
		final ConfigurationData returnedConfigData = configFacade.getConfigurationWithFallback(kbKey, CONFIG_ID);
		assertEquals(CONFIG_ID, returnedConfigData.getConfigId());
	}

	@Test(expected = IllegalStateException.class)
	public void testgetConfigurationWithFallbackException()
	{
		initializeFirstCall();
		given(configServiceMock.retrieveConfigurationModel(CONFIG_ID)).willThrow(new IllegalStateException());
		final ConfigurationData returnedConfigData = configFacade.getConfigurationWithFallback(kbKey, CONFIG_ID);

	}

	@Test
	public void testgetConfigurationWithFallback()
	{
		initializeFirstCall();
		final Throwable ex = new IllegalStateException(new ConfigurationNotFoundException());
		given(configServiceMock.retrieveConfigurationModel(CONFIG_ID)).willThrow(ex);
		final ConfigurationData returnedConfigData = configFacade.getConfigurationWithFallback(kbKey, CONFIG_ID);
		assertEquals(CONFIG_ID, returnedConfigData.getConfigId());
	}
}
