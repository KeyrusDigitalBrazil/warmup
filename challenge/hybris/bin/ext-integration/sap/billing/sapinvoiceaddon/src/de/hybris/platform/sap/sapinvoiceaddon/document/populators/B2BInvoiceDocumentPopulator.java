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

import de.hybris.platform.accountsummaryaddon.document.data.B2BDocumentData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.log4j.Logger;

import de.hybris.platform.sap.sapinvoiceaddon.constants.SapinvoiceaddonConstants;
import de.hybris.platform.sap.sapinvoiceaddon.document.data.InvoiceItemData;
import de.hybris.platform.sap.sapinvoiceaddon.document.data.InvoiceItemsData;
import de.hybris.platform.sap.sapinvoiceaddon.document.data.PartnerAddressData;
import de.hybris.platform.sap.sapinvoiceaddon.jalo.PartnerAddress;
import de.hybris.platform.sap.sapinvoiceaddon.model.MaterialModel;
import de.hybris.platform.sap.sapinvoiceaddon.model.PartnerAddressModel;
import de.hybris.platform.sap.sapinvoiceaddon.model.SapB2BDocumentModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;



/**
 * Populates a hybris invoice 
 *
 * @param <SOURCE>
 *           Model representation of an invoice
 * @param <TARGET>
 *           DAO representation of an invoice
 */
public class B2BInvoiceDocumentPopulator implements Populator<SapB2BDocumentModel, B2BDocumentData>
{
	public static final Logger LOG = Logger.getLogger(B2BInvoiceDocumentPopulator.class);
	private Converter<CurrencyModel, CurrencyData> currencyConverter;
	private CommonI18NService commonI18NService;
	private CommerceCommonI18NService commerceCommonI18NService;
	private I18NService i18NService;
	private PriceDataFactory priceFactory;
	private ProductService productService;
	private Converter<ProductModel, ProductData> productConverter;
	private ConfigurationService configurationService;


	@Override
	public void populate(final SapB2BDocumentModel source, final B2BDocumentData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setDeliveryNumber(source.getDeliveryNumber());
		if (null != source.getDeliveryDate())
		{
			target.setDeliveryDate(getFormattedDate(source.getDeliveryDate()));
		}

		target.setDocumentNumber(source.getDocumentNumber());
		target.setAmount(source.getAmount());
		if (null != source.getCurrency() && null != source.getAmount())
		{
			target.setInvoiceAmount(priceFactory.create(PriceDataType.BUY, source.getAmount(),
					source.getCurrency()));
			
		}
		if (source.getDate() != null)
		{
			target.setInvoiceDate(getFormattedDate(source.getDate().toString()));
		}
		if (null != source.getCurrency())
		{
			target.setCurrency(currencyConverter.convert(source.getCurrency()));
		}
		target.setB2bUnit((source.getUnit() != null) ? source.getUnit().getUid() : null);
		target.setTaxNumber(source.getTaxNumber());
		target.setOurTaxNumber(source.getOurTaxNumber());
		if (null != source.getOrderDate())
		{
			target.setOrderDate(getFormattedDate(source.getOrderDate()));
		}
		setPartnerAddresses(source,target);
		target.setOrderNumber(source.getOrderNumber());
		target.setCustomerNumber(source.getCustomerNumber());
		target.setTermsOfPayment(source.getTermsOfPayment());
		target.setShipToAddress(source.getPayerAddress());
		target.setBillToAddress(source.getBillAddress());
		target.setInvoiceItemsData(getItemDetails(source));
		target.setNetWeight(source.getNetWeight().concat(source.getUnits()));
		target.setGrossWeight(source.getGrossWeight().concat(source.getUnits()));
		target.setTermsOfDelivery(source.getTermsOfDelivery());
		
	}

	private void setPartnerAddresses(SapB2BDocumentModel source,
			B2BDocumentData target) {
		final String billingAddressPartnerFunction=configurationService.getConfiguration()
				.getString(SapinvoiceaddonConstants.BILLING_ADDRESS_PARTNER_FUCNTION);
		final String shippingAddressPartnerFunction=configurationService.getConfiguration()
				.getString(SapinvoiceaddonConstants.SHIPPING_ADDRESS_PARTNER_FUCNTION);
		
		Assert.notNull(billingAddressPartnerFunction, "Parameter billingAddressPartnerFunction cannot be null.");
		Assert.notNull(shippingAddressPartnerFunction, "Parameter shippingAddressPartnerFunction cannot be null.");
	
		
		for(PartnerAddressModel partnerAddress:source.getPartnerAddress())
		{
			
			if(billingAddressPartnerFunction.equals(partnerAddress.getPartnerFunction()))
			{
				target.setPartnerbBillToAddress(populateAddress(partnerAddress));	
			}
			
			if(shippingAddressPartnerFunction.equals(partnerAddress.getPartnerFunction()))
			{
				target.setPartnerShipToAddress(populateAddress(partnerAddress));
			}
		}		
				
	}


	private PartnerAddressData populateAddress(
			PartnerAddressModel partnerAddress) {
		PartnerAddressData ptnrAddress=new PartnerAddressData();
		ptnrAddress.setCity(partnerAddress.getCity());
		ptnrAddress.setCountryCode(partnerAddress.getCountryCode());
		ptnrAddress.setCountryKey(partnerAddress.getCountryKey());
		ptnrAddress.setPartnerCode(partnerAddress.getPartnerCode());
		ptnrAddress.setPartnerFunction(partnerAddress.getPartnerFunction());
		ptnrAddress.setPartnerID(partnerAddress.getPartner());
		ptnrAddress.setPoBox(partnerAddress.getPoBox());
		ptnrAddress.setPoBoxPostalCode(partnerAddress.getPoBoxPostalCode());
		ptnrAddress.setPostalCode(partnerAddress.getPostalCode());
		ptnrAddress.setStreetHouseNumber1(partnerAddress.getStreetHouseNumber1());
		ptnrAddress.setStreetHouseNumber2(partnerAddress.getStreetHouseNumber2());
		
		return ptnrAddress;
		
	}

	/**
	 * 
	 * @param source SapB2BDocumentModel
	 * @return InvoiceItemsData - DAO  representation of items of invoice
	 * 
	 */
	private InvoiceItemsData getItemDetails(final SapB2BDocumentModel source)
	{
		
		final InvoiceItemsData orderData = new InvoiceItemsData();
		final List<MaterialModel> itemList = source.getMaterial();
		try
		{
			if (!StringUtils.isEmpty(source.getOverAllTax()))
			{
				orderData.setOverAllTax(priceFactory.create(PriceDataType.BUY, new BigDecimal(source.getOverAllTax()),
						source.getCurrency()));
			}

			if (!StringUtils.isEmpty(source.getGrandTotal()))
			{
				orderData.setGrandTotal(priceFactory.create(PriceDataType.BUY, new BigDecimal(source.getGrandTotal()),
						source.getCurrency()));
			}

			if (!StringUtils.isEmpty(source.getAmount()))
			{
				orderData.setNetValue(priceFactory.create(PriceDataType.BUY, source.getAmount(), source.getCurrency()));
			}
			final List<InvoiceItemData> invoiceItemsData = new ArrayList<InvoiceItemData>();
			for (final MaterialModel material : itemList)
			{

				final InvoiceItemData invoiceItemData = new InvoiceItemData();
				invoiceItemData.setPosNo(material.getMatPosNo());

				final ProductModel product = getProductForItem(material.getMatNo());
				if (product != null)
				{
					invoiceItemData.setProduct(productConverter.convert(product));
				}
				else
				{
					final ProductData productData = createProductFromItem(material);
					invoiceItemData.setProduct(productData);

				}
				invoiceItemData.setItemDesc(material.getMatDesc());
				invoiceItemData.setQuantity(Long.valueOf(fomartChecking(material.getQuantity())));
				if (!StringUtils.isEmpty(material.getGrossValue()))
				{
					invoiceItemData.setGrossPrice(priceFactory.create(PriceDataType.BUY, new BigDecimal(material.getGrossValue()),
							source.getCurrency()));
				}

				if (!StringUtils.isEmpty(material.getNetValue()))
				{
					invoiceItemData.setNetPrice(priceFactory.create(PriceDataType.BUY, new BigDecimal(material.getNetValue()),
							source.getCurrency()));
				}
				
				invoiceItemsData.add(invoiceItemData);
			}
			
			orderData.setEntries(invoiceItemsData);
			return orderData;
		}

		catch( final NumberFormatException numberFormatException){
			LOG.warn("Number format exception in item conversion for invoice details" + numberFormatException.getMessage());
		}
		
		catch (final Exception e)
		{
			LOG.warn("Unexpected Error Msg",e);
			LOG.warn("Error In Item conversion for Invoice Details" + source.getDocumentNumber());
			
		}
		
		return null;

	}

	/**
	 * @param value
	 * @return String , format checking 
	 */
	private String fomartChecking(final String value)
	{
		return value.replaceAll("(?<=^\\d+)\\.0*$", "");
	}




	/**
	 * @param matNo
	 * @return ProductModel
	 */
	private ProductModel getProductForItem(final String matNo)
	{
		try
		{
			return productService.getProductForCode(matNo);
		}
		catch (final IllegalArgumentException | UnknownIdentifierException |  AmbiguousIdentifierException | IllegalStateException ex)
		{
			LOG.error(String.format("Unable to access the product model for code: %s ", matNo),ex);	
		}
		return null;
	}

	/**
	 * @param material
	 * @return ProductData
	 */
	private ProductData createProductFromItem(final MaterialModel material)
	{

		final ProductData productData = new ProductData();
		productData.setCode(material.getMatNo());
		productData.setName(material.getMatDesc());
		return productData;

	}

	/**
	 * @param date
	 * @return String - return formated date ex. Jan 25 2016
	 */
	private String getFormattedDate(final String date)
	{
		Assert.notNull(date, "Parameter date cannot be null.");
		SimpleDateFormat sdf = null;
		if (date.contains(":"))
		{
			sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		}
		else if (date.contains("-"))
		{
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		}
		else
		{
			sdf = new SimpleDateFormat("yyyyMMdd");
		}
		Date convertedDate = null;
		try
		{
			convertedDate = sdf.parse(date);
			final DateFormat out = new SimpleDateFormat("MMM dd yyyy");
			return out.format(convertedDate);
		}
		catch (final ParseException e)
		{
			LOG.warn("Date not parsable Invoice Document Populator");
			return null;
		}

	}


	protected Locale getLocale()
	{
		final LanguageModel currentLanguage = getCommonI18NService().getCurrentLanguage();
		Locale locale = getCommerceCommonI18NService().getLocaleForLanguage(currentLanguage);
		if (locale == null)
		{
			locale = getI18NService().getCurrentLocale();
		}
		return locale;
	}


	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	@Required
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	protected CommerceCommonI18NService getCommerceCommonI18NService()
	{
		return commerceCommonI18NService;
	}

	@Required
	public void setCommerceCommonI18NService(final CommerceCommonI18NService commerceCommonI18NService)
	{
		this.commerceCommonI18NService = commerceCommonI18NService;
	}

	protected I18NService getI18NService()
	{
		return i18NService;
	}

	@Required
	public void setI18NService(final I18NService i18NService)
	{
		this.i18NService = i18NService;
	}

	
	@Required
	public void setCurrencyConverter(final Converter<CurrencyModel, CurrencyData> currencyConverter)
	{
		this.currencyConverter = currencyConverter;
	}

	/**
	 * @return the priceFactory
	 */
	public PriceDataFactory getPriceFactory()
	{
		return priceFactory;
	}

	/**
	 * @param priceFactory
	 *           the priceFactory to set
	 */
	@Required
	public void setPriceFactory(final PriceDataFactory priceFactory)
	{
		this.priceFactory = priceFactory;
	}

	/**
	 * @return the productService
	 */
	public ProductService getProductService()
	{
		return productService;
	}

	/**
	 * @param productService
	 *           the productService to set
	 */
	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	protected Converter<ProductModel, ProductData> getProductConverter()
	{
		return productConverter;
	}

	@Required
	public void setProductConverter(final Converter<ProductModel, ProductData> productConverter)
	{
		this.productConverter = productConverter;
	}
	
	protected ConfigurationService getConfigurationService() {
		return configurationService;
	}

	@Required
	public void setConfigurationService(
			final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
