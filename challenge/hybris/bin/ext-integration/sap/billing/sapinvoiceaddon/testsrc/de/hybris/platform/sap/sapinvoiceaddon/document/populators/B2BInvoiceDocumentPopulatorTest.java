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
package de.hybris.platform.sap.sapinvoiceaddon.document.populators;

import static junit.framework.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.accountsummaryaddon.formatters.AmountFormatter;
import de.hybris.platform.accountsummaryaddon.model.B2BDocumentTypeModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.sapinvoiceaddon.constants.SapinvoiceaddonConstants;
import de.hybris.platform.sap.sapinvoiceaddon.model.MaterialModel;
import de.hybris.platform.sap.sapinvoiceaddon.model.PartnerAddressModel;
import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;




/**
 *
 */
@UnitTest
public class B2BInvoiceDocumentPopulatorTest 
{
	private static final String CUR_ISOCODE = "currIsoCode";
	private static final String CUR_USD = "USD";
	private static final String Number = "123456";
	private static final String TestString = "TestString";
	private static final String Units = "KG";

	@Mock
	private CommerceCommonI18NService commerceCommonI18NService;
	
	@Mock
	private ConfigurationService configurationService;
	
	@Mock
	private Configuration configuration;
	
	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private Converter<CurrencyModel, CurrencyData> currencyConverter;
	
	@Mock
	private AmountFormatter amountFormatter;
	
	@Mock
	private Converter<ProductModel, ProductData> productConverter;
	
	@Mock
	private PriceDataFactory priceFactory;
	
	@Mock
	private ProductService productService;
	
	@InjectMocks
	private final B2BInvoiceDocumentPopulator b2BInvoiceDocumentPopulator = new B2BInvoiceDocumentPopulator();
	
	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldPopulateTargetObject()
	{
		final CurrencyModel currencyModel = mock(CurrencyModel.class);
		final CurrencyData curData = new CurrencyData();
		curData.setIsocode(CUR_ISOCODE);
		curData.setName(CUR_USD);
		
		ProductData productData=new ProductData();
		productData.setCode(Number);
		ProductModel productModel=new ProductModel();
		productModel.setCode(Number);
		
		PriceData priceData=new PriceData();
		priceData.setCurrencyIso(CUR_ISOCODE);
		priceData.setValue(new BigDecimal(Number));
		
		final B2BUnitModel b2BUnitModel = new B2BUnitModel();
		b2BUnitModel.setUid("Test Unit");

		final B2BDocumentTypeModel invoiceType = mock(B2BDocumentTypeModel.class);
		given(invoiceType.getCode()).willReturn("Invoice");
		given(invoiceType.getName()).willReturn("Invoice");

		final SapB2BDocumentModel source = new SapB2BDocumentModel();
		source.setDeliveryNumber(Number);
		source.setDeliveryDate(new Date().toString());
		source.setDocumentNumber(Number);
		source.setAmount(new BigDecimal("100.10"));
		source.setDate(new Date());
		source.setUnit(b2BUnitModel);
		source.setTaxNumber(Number);
		source.setOurTaxNumber(Number);
		source.setOrderDate(new Date().toString());
		source.setOrderNumber(Number);
		source.setCustomerNumber(Number);
		source.setTermsOfPayment(TestString);
		source.setShippingCost(TestString);
		source.setBillAddress(TestString);
		source.setNetWeight(Number);
		source.setGrossWeight(Number);
		source.setUnits(Units);
		source.setCurrency(currencyModel);
		source.setOverAllTax(Number);
		source.setGrandTotal(Number);
		source.setMaterial(getMaterialList());
		source.setPartnerAddress(populatePartnerAddress());

		final B2BDocumentData target = new B2BDocumentData();

		given(currencyModel.getIsocode()).willReturn(CUR_ISOCODE);
		given(currencyConverter.convert(currencyModel)).willReturn(curData);

		final LanguageModel language = new LanguageModel();
		final Locale locale = Locale.CANADA;
		given(commerceCommonI18NService.getLocaleForLanguage(language)).willReturn(locale);
		given(commonI18NService.getCurrentLanguage()).willReturn(language);
		given(amountFormatter.formatAmount(source.getAmount(), currencyModel, locale )).willReturn("100.10");
		given(amountFormatter.formatAmount(source.getOpenAmount(), currencyModel, locale )).willReturn("50.25");
		given(productConverter.convert(productModel)).willReturn(productData);
		given(productService.getProductForCode(source.getMaterial().get(0).getMatNo())).willReturn(productModel);
		given(priceFactory.create(PriceDataType.BUY, new BigDecimal(Number),
				currencyModel)).willReturn(priceData);
		given(configurationService.getConfiguration()).willReturn(configuration);
		given(configuration.getString(SapinvoiceaddonConstants.BILLING_ADDRESS_PARTNER_FUCNTION)).willReturn(
				TestString);
		given(configuration.getString(SapinvoiceaddonConstants.SHIPPING_ADDRESS_PARTNER_FUCNTION)).willReturn(
				TestString);
		b2BInvoiceDocumentPopulator.populate(source, target);
		assertEquals("The documentNumber should be equals", source.getDocumentNumber(), target.getDocumentNumber());
		assertEquals("The amount should be equals", source.getAmount(), target.getAmount());
		assertEquals("The currency.isocode should be equals", CUR_ISOCODE, target.getCurrency().getIsocode());
		assertEquals("The currency.name should be equals", CUR_USD, target.getCurrency().getName());
	}

	private List<PartnerAddressModel> populatePartnerAddress() {
		PartnerAddressModel partnerAddressModel=new PartnerAddressModel();
		partnerAddressModel.setCity(TestString);
		partnerAddressModel.setCountryKey(TestString);
		partnerAddressModel.setCountryCode(TestString);
		partnerAddressModel.setPartnerCode(Number);
		partnerAddressModel.setPartnerFunction(TestString);
		partnerAddressModel.setPartner(Number);
		partnerAddressModel.setPoBox(Number);
		partnerAddressModel.setStreetHouseNumber1(TestString);
		partnerAddressModel.setStreetHouseNumber2(TestString);
		return Arrays.asList(partnerAddressModel);
	}

	// creating list of sample material for invoice
	private List<MaterialModel> getMaterialList() {
		
		ProductData productData=new ProductData();
		productData.setCode(Number);
		ProductModel productModel=new ProductModel();
		productModel.setCode(Number);
		final List<MaterialModel> itemList = new ArrayList<MaterialModel>();
		MaterialModel materail1=new MaterialModel();
		materail1.setMatPosNo(Number);
		materail1.setGrossValue(Number);
		materail1.setNetValue(Number);
		materail1.setMatDesc(TestString);
		materail1.setQuantity(Number);
		materail1.setMatNo(Number);
		itemList.add(materail1);
		
		return itemList;
		
		
	}
}
