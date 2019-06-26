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

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationParameterB2B;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.ConfigurationProductUtil;
import de.hybris.platform.sap.productconfig.runtime.ssc.ConfigurationContextAndPricingWrapper;
import de.hybris.platform.sap.productconfig.runtime.ssc.PricingConfigurationParameterSSC;
import de.hybris.platform.sap.productconfig.runtime.ssc.constants.SapproductconfigruntimesscConstants;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import com.sap.custdev.projects.fbs.slc.cfg.IConfigSession;
import com.sap.custdev.projects.fbs.slc.cfg.client.IDocument;
import com.sap.custdev.projects.fbs.slc.cfg.client.IItemInfo;
import com.sap.custdev.projects.fbs.slc.cfg.client.IPricingAttribute;
import com.sap.custdev.projects.fbs.slc.cfg.client.ItemInfoData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.DocumentData;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingException;
import com.sap.custdev.projects.fbs.slc.cfg.ipintegration.InteractivePricingIntegration;
import com.sap.custdev.projects.fbs.slc.helper.ConfigSessionManager;
import com.sap.custdev.projects.fbs.slc.pricing.ip.api.InteractivePricingMgr;
import com.sap.custdev.projects.fbs.slc.pricing.slc.api.ISLCDocument;
import com.sap.spe.conversion.ICurrencyValue;
import com.sap.spe.pricing.customizing.IConditionPurpose;
import com.sap.spe.pricing.transactiondata.IPricingCondition;
import com.sap.spe.pricing.transactiondata.IPricingDocument;
import com.sap.spe.pricing.transactiondata.IPricingItem;
import com.sap.sxe.sys.SAPDate;
import com.sap.sxe.sys.SAPTimestamp;


/**
 * Default implementation of the {@link ConfigurationContextAndPricingWrapper}
 */
public class ConfigurationContextAndPricingWrapperImpl implements ConfigurationContextAndPricingWrapper
{
	private static final Logger LOG = Logger.getLogger(ConfigurationContextAndPricingWrapperImpl.class);

	private ConfigModelFactory configModelFactory;
	private CommonI18NService i18NService;
	private PricingConfigurationParameterSSC pricingConfigurationParameter;
	private ConfigurationParameterB2B configurationParameterB2B;
	private ConfigurationProductUtil configurationProductUtil;
	private final SSCTimer timer = new SSCTimer();

	@Override
	public void preparePricingContext(final IConfigSession session, final String configId, final KBKey kbKey)
			throws InteractivePricingException
	{
		logPricingConfigurationParameters();
		if (pricingConfigurationParameter != null && pricingConfigurationParameter.isPricingSupported())
		{
			final IDocument docPricingCtx = getDocumentPricingContext();
			final IItemInfo itemPricingCtx = getItemPricingContext(kbKey);
			timer.start("preparePricing");
			final ConfigSessionManager configSessionManager = session.getConfigSessionManager();
			configSessionManager.setPricingContext(configId, docPricingCtx, itemPricingCtx, kbKey.getKbLogsys());
			configSessionManager.setInteractivePricingEnabled(configId, true);
			timer.stop();
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Pricing is active for config [CONFIG_ID='" + configId + "']");
			}
		}
		else
		{
			if (LOG.isDebugEnabled())
			{
				LOG.debug("Pricing is disabled/not supported [CONFIG_ID='" + configId + "']");
			}
		}
	}

	protected IDocument getDocumentPricingContext()
	{

		final IDocument documentPricingContext = new DocumentData();

		addAttributeToDocumentPricingContext(documentPricingContext, SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_VKORG,
				pricingConfigurationParameter.getSalesOrganization());
		addAttributeToDocumentPricingContext(documentPricingContext, SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_VTWEG,
				pricingConfigurationParameter.getDistributionChannelForConditions());
		addAttributeToDocumentPricingContext(documentPricingContext, SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_SPART,
				pricingConfigurationParameter.getDivisionForConditions());
		addAttributeToDocumentPricingContext(documentPricingContext,
				SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_HEADER_SPART,
				pricingConfigurationParameter.getDivisionForConditions());

		if (configurationParameterB2B != null && configurationParameterB2B.isSupported())
		{
			addAttributeToDocumentPricingContext(documentPricingContext, SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KUNNR,
					configurationParameterB2B.getCustomerNumber());
			addAttributeToDocumentPricingContext(documentPricingContext, SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_LAND1,
					configurationParameterB2B.getCountrySapCode());
			addAttributeToDocumentPricingContext(documentPricingContext, SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KONDA,
					configurationParameterB2B.getCustomerPriceGroup());
		}

		documentPricingContext.setPricingProcedure(pricingConfigurationParameter.getPricingProcedure());

		final CurrencyModel currencyModel = i18NService.getCurrentCurrency();
		final String currency = pricingConfigurationParameter.retrieveCurrencySapCode(currencyModel);

		addAttributeToDocumentPricingContext(documentPricingContext, SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_KONWA,
				currency);
		documentPricingContext.setDocumentCurrencyUnit(currency);
		documentPricingContext.setLocalCurrencyUnit(currency);

		documentPricingContext.setApplication(SapproductconfigruntimesscConstants.APPLICATION_V);
		documentPricingContext.setUsage(SapproductconfigruntimesscConstants.USAGE_A);

		if (LOG.isDebugEnabled())
		{
			documentPricingContext.setPerformPricingTrace(true);
		}

		logDocumentPricingContext(documentPricingContext);

		return documentPricingContext;
	}

	protected void logDocumentPricingContext(final IDocument documentPricingContext)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Document pricing context IDocument:");
			LOG.debug(" - getPricingProcedure() = " + documentPricingContext.getPricingProcedure());
			LOG.debug(" - getDocumentCurrencyUnit() = " + documentPricingContext.getDocumentCurrencyUnit());
			LOG.debug(" - getLocalCurrencyUnit() = " + documentPricingContext.getLocalCurrencyUnit());
			LOG.debug(" - getApplication() = " + documentPricingContext.getApplication());
			LOG.debug(" - getUsage() = " + documentPricingContext.getUsage());
			LOG.debug(" - getPerformTrace() = " + documentPricingContext.getPerformTrace());

			final Map<String, IPricingAttribute> attributes = documentPricingContext.getAttributes();
			if (attributes != null)
			{
				LOG.debug(" - getAttributes() :");
				for (final Map.Entry<String, IPricingAttribute> attribute : attributes.entrySet())
				{
					LOG.debug("  -- Document Pricing Attribute " + attribute.getKey() + " -> " + attribute.getValue().getValues());
				}
			}
		}
	}

	protected IItemInfo getItemPricingContext(final KBKey kbKey)
	{
		final IItemInfo itemPricingContext = new ItemInfoData();

		itemPricingContext.addAttribute(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_PMATN, kbKey.getProductCode());
		itemPricingContext.setProductId(kbKey.getProductCode());
		itemPricingContext.addAttribute(SapproductconfigruntimesscConstants.PRICING_ATTRIBUTE_PRSFD, "X");

		final SAPTimestamp timeStamp = new SAPTimestamp(new SAPDate(new Date()));
		itemPricingContext.addTimestamp(SapproductconfigruntimesscConstants.DET_DEFAULT_TIMESTAMP,
				timeStamp.formatyyyyMMddHHmmss());

		itemPricingContext.setQuantity(BigDecimal.ONE);
		final ProductModel product = getConfigurationProductUtil().getProductForCurrentCatalog(kbKey.getProductCode());
		final UnitModel unitModel = product.getUnit();
		final String sapUOM = pricingConfigurationParameter.retrieveUnitSapCode(unitModel);
		itemPricingContext.setQuantityUnit(sapUOM);

		itemPricingContext.setPricingRelevant(true);

		logItemPricingContext(itemPricingContext);

		return itemPricingContext;
	}

	protected void logItemPricingContext(final IItemInfo itemPricingContext)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Item pricing context IITemInfo:");
			LOG.debug(" - getProductId() = " + itemPricingContext.getProductId());
			LOG.debug(" - getTimeStamps() = " + itemPricingContext.getTimestamps());
			LOG.debug(" - getQuantity() = " + itemPricingContext.getQuantity());
			LOG.debug(" - getQuantityUnit() = " + itemPricingContext.getQuantityUnit());
			LOG.debug(" - getPricingRelevantFlag() = " + itemPricingContext.getPricingRelevantFlag());

			final Map<String, IPricingAttribute> attributes = itemPricingContext.getAttributes();
			if (attributes != null)
			{
				LOG.debug(" - getAttributes() :");
				for (final Map.Entry<String, IPricingAttribute> attribute : attributes.entrySet())
				{
					LOG.debug("  -- Item Pricing Attribute " + attribute.getKey() + " -> " + attribute.getValue().getValues());
				}
			}
		}
	}

	@Override
	public void processPrice(final IConfigSession session, final String configId, final ConfigModel configModel)
			throws InteractivePricingException
	{
		if (pricingConfigurationParameter != null && pricingConfigurationParameter.isPricingSupported())
		{
			timer.start("processPrice");
			final ConfigSessionManager configSessionManager = session.getConfigSessionManager();

			final InteractivePricingIntegration interactivePricing = configSessionManager.getInteractivePricingIntegration(configId);
			final InteractivePricingMgr pricingManager = interactivePricing.getInteractivePricingManager();

			final ISLCDocument document = pricingManager.getDocument();
			final IPricingDocument pricingDocument = document.getPricingDocument();
			final ICurrencyValue documentNetValue = pricingDocument.getNetValueWithoutFreight();
			timer.stop();
			provideCurrentTotalPriceModel(configModel, documentNetValue);

			final String targetForBasePrice = pricingConfigurationParameter.getTargetForBasePrice();
			final String targetForSelectedOptionsPrice = pricingConfigurationParameter.getTargetForSelectedOptions();
			Map<String, ICurrencyValue> condFuncValuesMap = null;

			if (StringUtils.isNotEmpty(targetForBasePrice) || StringUtils.isNotEmpty(targetForSelectedOptionsPrice))
			{
				timer.start("getConditonFunctions");
				condFuncValuesMap = pricingDocument.getAccumulatedValuesForConditionsWithPurpose();
				timer.stop();
			}

			configModel.setBasePrice(retrievePrice(targetForBasePrice, condFuncValuesMap, "Base Price"));
			configModel.setSelectedOptionsPrice(retrievePrice(targetForSelectedOptionsPrice, condFuncValuesMap, "Selected Options Price"));

			logPrices(documentNetValue, condFuncValuesMap);
			logDocumentPricingConditions(pricingDocument);
		}
	}

	protected void provideCurrentTotalPriceModel(final ConfigModel configModel, final ICurrencyValue netValue)
	{
		final PriceModel currentTotalPriceModel = configModelFactory.createInstanceOfPriceModel();
		currentTotalPriceModel.setPriceValue(netValue.getValue());
		currentTotalPriceModel.setCurrency(netValue.getUnitName());
		configModel.setCurrentTotalPrice(currentTotalPriceModel);
	}


	protected PriceModel retrievePrice(final String targetForPrice, final Map<String, ICurrencyValue> condFuncValuesMap, String logPriceType)
	{
		PriceModel priceModel = configModelFactory.getZeroPriceModel();

		if (StringUtils.isEmpty(targetForPrice))
		{
			LOG.debug("Target for " + logPriceType + " is not maintained in the SAP Base Store Configuration");
		}
		else if (condFuncValuesMap != null)
		{
			ICurrencyValue basePrice = condFuncValuesMap.get(targetForPrice);
			if (basePrice == null)
			{
				LOG.debug("No " + logPriceType + " retrieved for config");
			}
			else
			{
				priceModel = configModelFactory.createInstanceOfPriceModel();
				priceModel.setPriceValue(basePrice.getValue());
				priceModel.setCurrency(basePrice.getUnitName());
			}
		}

		return priceModel;
	}

	protected void logPrices(final ICurrencyValue netValue, final Map<String, ICurrencyValue> condFuncValuesMap)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Net Price: " + netValue);

			if (condFuncValuesMap != null)
			{
				for (final Map.Entry<String, ICurrencyValue> condFuncEntry : condFuncValuesMap.entrySet())
				{
					LOG.debug("Destination (Condition Function): " + condFuncEntry.getKey() + " -> " + condFuncEntry.getValue());
				}
			}
		}

	}

	protected void logPricingConditions(final IPricingItem pricingItem)
	{
		if (LOG.isDebugEnabled())
		{
			final IPricingCondition[] pricingConditions = pricingItem.getPricingConditions();
			logConditions(pricingConditions);
		}
	}

	protected void logDocumentPricingConditions(final IPricingDocument pricingDocument)
	{
		if (LOG.isDebugEnabled())
		{
			final IPricingCondition[] pricingConditions = pricingDocument.getConditions();
			logConditions(pricingConditions);
		}
	}

	protected void logConditions(final IPricingCondition[] pricingConditions)
	{
		LOG.debug("PricingConditions  (Condition Type; Condition Rate; Condition Value; Condition Base; Purpose):");
		for (final IPricingCondition pricingCondition : pricingConditions)
		{
			String conditionType = "";
			if (pricingCondition.getConditionTypeName() != null)
			{
				conditionType = pricingCondition.getConditionTypeName();
			}
			if (pricingCondition.getDescription() != null)
			{
				conditionType = conditionType.concat(pricingCondition.getDescription());
			}

			final IConditionPurpose conditionPurpose = pricingCondition.getPurpose();
			String purposeName = "";
			if (conditionPurpose != null)
			{
				purposeName = conditionPurpose.getName();
			}
			LOG.debug(conditionType + "; " + pricingCondition.getConditionRate() + "; " + pricingCondition.getConditionValue() + "; "
					+ pricingCondition.getConditionBase() + "; " + purposeName);
		}
	}

	protected void logPricingConfigurationParameters()
	{
		if (LOG.isDebugEnabled())
		{
			if (pricingConfigurationParameter != null)
			{
				LOG.debug("Pricing Configuration Parameters:");
				LOG.debug(" - isPricingSupported: " + pricingConfigurationParameter.isPricingSupported());
				LOG.debug(" - SalesOrganization: " + pricingConfigurationParameter.getSalesOrganization());
				LOG.debug(
						" - DistributionChannelForConditions: " + pricingConfigurationParameter.getDistributionChannelForConditions());
				LOG.debug(" - DivisionForConditions: " + pricingConfigurationParameter.getDivisionForConditions());
				LOG.debug(" - PricingProcedure: " + pricingConfigurationParameter.getPricingProcedure());
				LOG.debug(" - TargetForBasePrice: " + pricingConfigurationParameter.getTargetForBasePrice());
				LOG.debug(" - TargetForSelectedOptions: " + pricingConfigurationParameter.getTargetForSelectedOptions());
			}
			if (configurationParameterB2B != null)
			{
				LOG.debug("B2B Configuration Parameters:");
				LOG.debug(" - isSupported: " + configurationParameterB2B.isSupported());
				LOG.debug(" - CustomerNumber: " + configurationParameterB2B.getCustomerNumber());
				LOG.debug(" - CustomerPriceGroup: " + configurationParameterB2B.getCustomerPriceGroup());
				LOG.debug(" - CountrySapCode: " + configurationParameterB2B.getCountrySapCode());
			}
		}
	}

	@Override
	public Hashtable<String, String> retrieveConfigurationContext(final KBKey kbKey)
	{
		//Reason for Hashtable instead of Map: SSC needs context map this way
		final Hashtable<String, String> configContext = new Hashtable<>();

		if (pricingConfigurationParameter != null)
		{
			addCustomerNumberToContext(configContext);
			addCountrySapCodeToContext(configContext);
			addSalesOrganisationToContext(configContext);
			addDistributionChannelToContext(configContext);
			addDivisionsForConditionsToContext(configContext);
		}

		final String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_ERDAT, date);
		configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAP_KWMENG, "1");
		configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAP_MATNR, kbKey.getProductCode());

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Retrieved config context: " + configContext);
		}
		return configContext;
	}

	protected void addDivisionsForConditionsToContext(final Map<String, String> configContext)
	{
		final String division = pricingConfigurationParameter.getDivisionForConditions();
		if (division != null && !division.isEmpty())
		{
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_SPART, division);
		}
	}

	protected void addDistributionChannelToContext(final Map<String, String> configContext)
	{
		final String distributionChannel = pricingConfigurationParameter.getDistributionChannelForConditions();
		if (distributionChannel != null && !distributionChannel.isEmpty())
		{
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_VTWEG, distributionChannel);
		}
	}

	protected void addSalesOrganisationToContext(final Map<String, String> configContext)
	{
		final String salesOrganization = pricingConfigurationParameter.getSalesOrganization();
		if (salesOrganization != null && !salesOrganization.isEmpty())
		{
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_VKORG, salesOrganization);
		}
	}

	protected void addCountrySapCodeToContext(final Map<String, String> configContext)
	{
		String country = null;
		if (configurationParameterB2B != null && configurationParameterB2B.isSupported())
		{
			country = configurationParameterB2B.getCountrySapCode();
		}
		if (country != null && !country.isEmpty())
		{
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_AG_LAND1, country);
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_RG_LAND1, country);
		}
	}

	protected void addCustomerNumberToContext(final Map<String, String> configContext)
	{
		String customerNumber = null;
		if (configurationParameterB2B != null && configurationParameterB2B.isSupported())
		{
			customerNumber = configurationParameterB2B.getCustomerNumber();
		}
		if (customerNumber != null && !customerNumber.isEmpty())
		{
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBAK_KUNNR, customerNumber);
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_AG_KUNNR, customerNumber);
			configContext.put(SapproductconfigruntimesscConstants.CONTEXT_ATTRIBUTE_VBPA_RG_KUNNR, customerNumber);
		}
	}

	protected void addAttributeToDocumentPricingContext(final IDocument documentPricingContext, final String attributeName,
			final String attributeValue)
	{
		if (attributeValue != null && !attributeValue.isEmpty())
		{
			documentPricingContext.addAttribute(attributeName, attributeValue);
		}
	}

	/**
	 * @param i18nService
	 */
	public void setI18NService(final CommonI18NService i18nService)
	{
		i18NService = i18nService;
	}

	/**
	 * @param pricingConfigurationParameter
	 */
	public void setPricingConfigurationParameter(final PricingConfigurationParameterSSC pricingConfigurationParameter)
	{
		this.pricingConfigurationParameter = pricingConfigurationParameter;
	}

	/**
	 * @param configurationParameterB2B
	 */
	public void setConfigurationParameterB2B(final ConfigurationParameterB2B configurationParameterB2B)
	{
		this.configurationParameterB2B = configurationParameterB2B;
	}

	protected CommonI18NService getI18NService()
	{
		return i18NService;
	}

	protected PricingConfigurationParameterSSC getPricingConfigurationParameter()
	{
		return pricingConfigurationParameter;
	}

	protected ConfigurationParameterB2B getConfigurationParameterB2B()
	{
		return configurationParameterB2B;
	}

	/**
	 * @param configModelFactory
	 */
	@Required
	public void setConfigModelFactory(final ConfigModelFactory configModelFactory)
	{
		this.configModelFactory = configModelFactory;
	}

	protected ConfigModelFactory getConfigModelFactory()
	{
		return configModelFactory;
	}

	protected ConfigurationProductUtil getConfigurationProductUtil()
	{
		return configurationProductUtil;
	}

	@Required
	public void setConfigurationProductUtil(final ConfigurationProductUtil configurationProductUtil)
	{
		this.configurationProductUtil = configurationProductUtil;
	}
}
