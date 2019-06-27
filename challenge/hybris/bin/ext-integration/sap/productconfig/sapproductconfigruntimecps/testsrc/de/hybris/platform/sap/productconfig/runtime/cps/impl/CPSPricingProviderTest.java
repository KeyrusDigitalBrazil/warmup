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
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.same;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataKnowledgeBaseContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.cps.pricing.PricingHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigurationRetrievalOptions;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceValueUpdateModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


@UnitTest
public class CPSPricingProviderTest
{
	private static final String KB_ID = "kbId";
	private static final String CONFIG_ID = "configId";
	private static final ConfigurationRetrievalOptions OPTIONS = new ConfigurationRetrievalOptions();
	private static final String PRODUCT_ID = "PRODUCT_ID";
	private CPSPricingProvider classUnderTest;
	@Mock
	private PricingHandler pricingHandler;
	@Mock
	private ConfigurationMasterDataService masterDataService;

	private MasterDataContext ctxt;
	private CPSMasterDataKnowledgeBaseContainer kbContainer;
	private ConfigurationRetrievalOptions options;

	@Before
	public void setup() throws PricingEngineException, ConfigurationEngineException
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new CPSPricingProvider();
		classUnderTest.setPricingHandler(pricingHandler);
		Mockito.when(pricingHandler.getPriceSummary(CONFIG_ID, OPTIONS)).thenReturn(new PriceSummaryModel());

		ctxt = new MasterDataContext();
		classUnderTest.setMasterDataService(masterDataService);
		kbContainer = new CPSMasterDataKnowledgeBaseContainer();
		given(masterDataService.getMasterData(KB_ID)).willReturn(kbContainer);
		ctxt.setKbCacheContainer(kbContainer);
		options = new ConfigurationRetrievalOptions();
		options.setDiscountList(new ArrayList<>());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testGetPriceSummaryDeprecated() throws PricingEngineException
	{
		classUnderTest.getPriceSummary(CONFIG_ID);
	}

	@Test
	public void testGetPriceSummaryNotNull() throws PricingEngineException, ConfigurationEngineException
	{
		final PriceSummaryModel result = classUnderTest.getPriceSummary(CONFIG_ID, OPTIONS);
		assertNotNull(result);
	}

	@Test
	public void testFillValuePrices_updateModels_kbid() throws PricingEngineException
	{
		final List<PriceValueUpdateModel> updateModels = new ArrayList<>();
		for (int i = 0; i < 10; i++)
		{
			updateModels.add(new PriceValueUpdateModel());
		}

		classUnderTest.fillValuePrices(updateModels, KB_ID);
		Mockito.verify(pricingHandler, Mockito.times(updateModels.size()))
				.fillValuePrices(argThat(matchContextContainingSameKbContainer()), any(PriceValueUpdateModel.class));
	}

	@Test
	public void testProviderIsActive()
	{
		assertTrue(classUnderTest.isActive());
	}

	@Test
	public void testFillValuePrices() throws PricingEngineException
	{
		final ConfigModel configModel = createAndFillConfigModelSingleLevel();
		classUnderTest.fillValuePrices(configModel);
		for (final CsticModel csticModel : configModel.getRootInstance().getCstics())
		{
			checkValuePrices(csticModel);
		}
	}

	@Test
	public void testPrepareMasterDataContext()
	{

		final MasterDataContext preparedContext = classUnderTest.prepareMasterDataContext(KB_ID, options);
		assertSame(kbContainer, preparedContext.getKbCacheContainer());
		assertSame(options.getDiscountList(), preparedContext.getDiscountList());
	}

	@Test
	public void testPrepareMasterDataContextWithPricingProduct()
	{
		options.setPricingProduct(PRODUCT_ID);
		final MasterDataContext preparedContext = classUnderTest.prepareMasterDataContext(KB_ID, options);
		assertEquals(PRODUCT_ID, preparedContext.getPricingProduct());
	}

	@Test
	public void testPrepareMasterDataContextNullOptions()
	{
		final MasterDataContext preparedContext = classUnderTest.prepareMasterDataContext(KB_ID, null);
		assertSame(kbContainer, preparedContext.getKbCacheContainer());
		assertTrue(preparedContext.getDiscountList().isEmpty());
		assertNull(preparedContext.getPricingProduct());
	}

	@Test
	public void testPrepareMasterDataContextNullList()
	{
		options.setDiscountList(null);
		final MasterDataContext preparedContext = classUnderTest.prepareMasterDataContext(KB_ID, options);
		assertSame(kbContainer, preparedContext.getKbCacheContainer());
		assertTrue(preparedContext.getDiscountList().isEmpty());
	}




	protected void checkValuePrices(final CsticModel csticModel)
	{
		assertNotNull(csticModel);
		for (final CsticValueModel possibleValue : csticModel.getAssignableValues())
		{
			assertNotNull(possibleValue.getValuePrice());
		}
	}

	protected ConfigModel createAndFillConfigModelSingleLevel()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setKbId(KB_ID);
		configModel.setRootInstance(new InstanceModelImpl());
		final CsticModel cstic1 = createCsticModel("cstic1");
		final CsticModel cstic2 = createCsticModel("cstic2");
		final CsticModel cstic3 = createCsticModel("cstic3");
		configModel.getRootInstance().addCstic(cstic1);
		configModel.getRootInstance().addCstic(cstic2);
		configModel.getRootInstance().addCstic(cstic3);
		return configModel;
	}

	protected CsticModel createCsticModel(final String cstic)
	{
		final CsticModel cstic1 = new CsticModelImpl();
		cstic1.setName(cstic);
		return cstic1;
	}

	@Test
	public void testFillValuePrices_Instance_SingleLevel() throws PricingEngineException
	{
		final ConfigModel singleLevel = createAndFillConfigModelSingleLevel();
		classUnderTest.fillValuePricesForInstance(singleLevel.getRootInstance(), ctxt);
		for (final CsticModel cstic : singleLevel.getRootInstance().getCstics())
		{
			Mockito.verify(pricingHandler).fillValuePrices(ctxt, cstic);
		}
	}

	@Test
	public void testFillValuePrices_SingleLevel() throws PricingEngineException
	{
		final ConfigModel singleLevel = createAndFillConfigModelSingleLevel();
		classUnderTest.fillValuePrices(singleLevel);
		for (final CsticModel cstic : singleLevel.getRootInstance().getCstics())
		{
			Mockito.verify(pricingHandler).fillValuePrices(argThat(matchContextContainingSameKbContainer()), same(cstic));
		}
	}

	protected ArgumentMatcher<MasterDataContext> matchContextContainingSameKbContainer()
	{
		return new ArgumentMatcher<MasterDataContext>()
		{
			@Override
			public boolean matches(final Object argument)
			{
				return ((MasterDataContext) argument).getKbCacheContainer() == kbContainer;
			}

		};
	}

	protected ConfigModel createAndFillConfigModelMultiLevel()
	{
		final ConfigModel configModel = new ConfigModelImpl();
		configModel.setKbId(KB_ID);
		configModel.setRootInstance(new InstanceModelImpl());
		final CsticModel cstic1 = createCsticModel("cstic1");
		final CsticModel cstic2 = createCsticModel("cstic2");
		final CsticModel cstic3 = createCsticModel("cstic3");
		configModel.getRootInstance().addCstic(cstic1);
		configModel.getRootInstance().addCstic(cstic2);
		configModel.getRootInstance().addCstic(cstic3);

		final List<InstanceModel> subInstances = new ArrayList<>();
		subInstances.add(new InstanceModelImpl());
		final List<InstanceModel> subSubInstances = new ArrayList<>();

		subSubInstances.add(new InstanceModelImpl());
		subSubInstances.get(0).addCstic(createCsticModel("cstic4"));
		subSubInstances.get(0).addCstic(createCsticModel("cstic5"));
		subSubInstances.add(new InstanceModelImpl());

		subInstances.get(0).setSubInstances(subSubInstances);
		configModel.getRootInstance().setSubInstances(subInstances);

		return configModel;
	}

	@Test
	public void testFillValuePrices_Instance_MultiLevel() throws PricingEngineException
	{
		final ConfigModel multiLevel = createAndFillConfigModelMultiLevel();
		classUnderTest.fillValuePricesForInstance(multiLevel.getRootInstance(), ctxt);
		for (final CsticModel cstic : multiLevel.getRootInstance().getCstics())
		{
			Mockito.verify(pricingHandler).fillValuePrices(ctxt, cstic);
		}
		for (final CsticModel cstic : multiLevel.getRootInstance().getSubInstances().get(0).getCstics())
		{
			Mockito.verify(pricingHandler).fillValuePrices(ctxt, cstic);
		}
		Mockito.verify(pricingHandler, Mockito.times(5)).fillValuePrices(argThat(matchContextContainingSameKbContainer()),
				Mockito.any(CsticModel.class));
	}
}
