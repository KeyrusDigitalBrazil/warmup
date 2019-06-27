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
package de.hybris.platform.sap.productconfig.runtime.ssc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationParameterB2B;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.ConfigModelFactoryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ZeroPriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.productconfig.runtime.ssc.PricingConfigurationParameterSSC;
import de.hybris.platform.sap.productconfig.runtime.ssc.constants.SapproductconfigruntimesscConstants;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IDocument;
import com.sap.custdev.projects.fbs.slc.cfg.client.IItemInfo;
import com.sap.custdev.projects.fbs.slc.cfg.client.IPricingAttribute;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingIntegration;
import com.sap.custdev.projects.fbs.slc.helper.ConfigSessionManager;
import com.sap.custdev.projects.fbs.slc.pricing.ip.api.InteractivePricingMgr;
import com.sap.custdev.projects.fbs.slc.pricing.slc.api.ISLCDocument;
import com.sap.spe.conversion.ICurrencyValue;
import com.sap.spe.pricing.transactiondata.IPricingCondition;
import com.sap.spe.pricing.transactiondata.IPricingDocument;


@UnitTest
public class ConfigurationContextAndPricingWrapperImplTest
{

	private static final String SAP_UNIT_CODE = "sapUnitCode";
	private static final String PRODUCT_CODE = "productCode";
	private static final String CONFIG_ID = "1234";

	private final ConfigurationContextAndPricingWrapperImpl classUnderTest = new ConfigurationContextAndPricingWrapperImpl();
	@Mock
	private ConfigurationProductUtil mockConfigProductUtil;
	@Mock
	private PricingConfigurationParameterSSC mockPricingConfigParamater;
	@Mock
	private ProductModel mockProduct;
	@Mock
	private UnitModel mockUnitModel;
	@Mock
	private IConfigSession session;

	@Mock
	private CommonI18NService mockI18NService;

	@Mock
	private ConfigModel mockConfigModel;

	@Mock
	private ConfigModelFactoryImpl mockConfigModelFactory;

	@Mock
	private PriceModel mockPriceModel;

	@Mock
	private ConfigSessionManager mockConfigSessionMgr;

	@Mock
	private InteractivePricingIntegration mockInteractivePricingIntegration;

	@Mock
	private InteractivePricingMgr mockPricingMgr;

	@Mock
	private ISLCDocument mockDoucment;

	@Mock
	private IPricingDocument mockPricingDocument;

	@Mock
	private ICurrencyValue mockCurrencyValue;

	@Mock
	private ConfigurationParameterB2B mockConfigurationParameterB2B;

	@Mock
	private InstanceModel mockInstance;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest.setConfigurationProductUtil(mockConfigProductUtil);
		classUnderTest.setPricingConfigurationParameter(mockPricingConfigParamater);
		classUnderTest.setConfigModelFactory(mockConfigModelFactory);
		classUnderTest.setI18NService(mockI18NService);


		when(mockConfigProductUtil.getProductForCurrentCatalog(PRODUCT_CODE)).thenReturn(mockProduct);
		when(mockProduct.getUnit()).thenReturn(mockUnitModel);
		when(mockPricingConfigParamater.retrieveUnitSapCode(mockUnitModel)).thenReturn(SAP_UNIT_CODE);

		when(mockPricingConfigParamater.isPricingSupported()).thenReturn(true);
		when(session.getConfigSessionManager()).thenReturn(mockConfigSessionMgr);
		when(mockConfigSessionMgr.getInteractivePricingIntegration(CONFIG_ID)).thenReturn(mockInteractivePricingIntegration);
		when(mockInteractivePricingIntegration.getInteractivePricingManager()).thenReturn(mockPricingMgr);
		when(mockPricingMgr.getDocument()).thenReturn(mockDoucment);
		when(mockDoucment.getPricingDocument()).thenReturn(mockPricingDocument);
		when(mockPricingDocument.getNetValueWithoutFreight()).thenReturn(mockCurrencyValue);
		when(mockPricingDocument.getConditions()).thenReturn(new IPricingCondition[] {});
		when(mockConfigModelFactory.createInstanceOfPriceModel()).thenReturn(mockPriceModel);

		when(mockConfigModel.getRootInstance()).thenReturn(mockInstance);
		when(mockInstance.getName()).thenReturn("ROOT");
	}

	@Test
	public void testGetItemPricingContext()
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		final IItemInfo result = classUnderTest.getItemPricingContext(kbKey);
		assertNotNull(result);

		assertEquals(PRODUCT_CODE,
				result.getAttributes().get(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_PMATN).getValues().get(0));
		assertEquals(PRODUCT_CODE, result.getProductId());
		assertEquals("X",
				result.getAttributes().get(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_PRSFD).getValues().get(0));
		assertNotNull(result.getTimestamps().get(SapproductconfigruntimesscConstants.DET_DEFAULT_TIMESTAMP));
		assertEquals(0, BigDecimal.ONE.compareTo(result.getQuantity()));
		assertEquals(SAP_UNIT_CODE, result.getQuantityUnit());
		assertTrue(result.getPricingRelevantFlag());
	}

	@Test
	public void testProcessPriceWithoutParameters() throws Exception
	{
		classUnderTest.setPricingConfigurationParameter(null);
		classUnderTest.processPrice(session, CONFIG_ID, null);
		verify(session, times(0)).getConfigSessionManager();
	}

	@Test
	public void testProcessPricePricingUnsupported() throws Exception
	{
		when(mockPricingConfigParamater.isPricingSupported()).thenReturn(false);
		classUnderTest.processPrice(session, CONFIG_ID, null);
		verify(session, times(0)).getConfigSessionManager();
	}

	@Test
	public void testProcessPriceTargetBasePriceIsNullAndOptionPriceIsNull() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn(null);
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn(null);

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(0)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testProcessPriceTargetBasePriceIsNullAndOptionPriceIsEmpty() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn(null);
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn("");

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(0)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testProcessPriceTargetBasePriceIsEmptyAndOptionPriceIsNull() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn("");
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn(null);

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(0)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testProcessPriceTargetBasePriceIsEmptyAndOptionPriceIsEmpty() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn("");
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn(null);

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(0)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testProcessPriceBasePriceIsSetAndOptionPriceIsNull() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn("47");
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn(null);

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(1)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testProcessPriceBasePriceIsNullAndOptionPriceIsSet() throws Exception
	{
		when(mockPricingConfigParamater.getTargetForBasePrice()).thenReturn(null);
		when(mockPricingConfigParamater.getTargetForSelectedOptions()).thenReturn("47");

		classUnderTest.processPrice(session, CONFIG_ID, mockConfigModel);
		verify(session, times(1)).getConfigSessionManager();
		verify(mockPricingDocument, times(1)).getAccumulatedValuesForConditionsWithPurpose();
	}

	@Test
	public void testRetrivePrice()
	{
		when(mockConfigModelFactory.getZeroPriceModel()).thenCallRealMethod();
		when(mockConfigModelFactory.createInstanceOfPriceModel()).thenReturn(mockPriceModel);
		when(mockCurrencyValue.getValue()).thenReturn(new BigDecimal(12));
		when(mockCurrencyValue.getUnitName()).thenReturn("EUR");

		Map<String, ICurrencyValue> condFuncValuesMap = new HashMap<>();
		condFuncValuesMap.put("47", mockCurrencyValue);


		PriceModel priceModel = classUnderTest.retrievePrice(null, null, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice("", null, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice("47", null, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice(null, condFuncValuesMap, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice("", condFuncValuesMap, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice("NONE", condFuncValuesMap, "base price");
		assertEquals(ZeroPriceModelImpl.NO_PRICE, priceModel);

		priceModel = classUnderTest.retrievePrice("47", condFuncValuesMap, "base price");
		assertEquals(mockPriceModel, priceModel);
	}

	@Test
	public void testGetDocumentPricingContextB2BContext()
	{
		when(mockPricingConfigParamater.getSalesOrganization()).thenReturn("4711");

		classUnderTest.setConfigurationParameterB2B(null);
		IDocument documentPricingContext = classUnderTest.getDocumentPricingContext();
		Map<String, IPricingAttribute> attributes = documentPricingContext.getAttributes();
		assertFalse(attributes.containsKey(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KUNNR));

		when(mockConfigurationParameterB2B.isSupported()).thenReturn(false);
		documentPricingContext = classUnderTest.getDocumentPricingContext();
		attributes = documentPricingContext.getAttributes();
		assertFalse(attributes.containsKey(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KUNNR));

		when(mockConfigurationParameterB2B.isSupported()).thenReturn(true);
		classUnderTest.setConfigurationParameterB2B(mockConfigurationParameterB2B);
		when(mockConfigurationParameterB2B.getCustomerNumber()).thenReturn("0815");
		documentPricingContext = classUnderTest.getDocumentPricingContext();
		attributes = documentPricingContext.getAttributes();
		assertTrue(attributes.containsKey(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KUNNR));
	}
}
