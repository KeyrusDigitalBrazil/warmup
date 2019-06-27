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

import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.sap.productconfig.facades.ClassificationSystemCPQAttributesProvider;
import de.hybris.platform.sap.productconfig.facades.ConfigPricing;
import de.hybris.platform.sap.productconfig.facades.ConfigurationMessageMapper;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticStatusType;
import de.hybris.platform.sap.productconfig.facades.CsticTypeMapper;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.IntervalInDomainHelper;
import de.hybris.platform.sap.productconfig.facades.UiType;
import de.hybris.platform.sap.productconfig.facades.UiTypeFinder;
import de.hybris.platform.sap.productconfig.facades.UiValidationType;
import de.hybris.platform.sap.productconfig.facades.UniqueUIKeyGenerator;
import de.hybris.platform.sap.productconfig.facades.ValueFormatTranslator;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.sap.productconfig.runtime.interf.ProviderFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Default implementation of the {@link CsticTypeMapper}.
 */
public class CsticTypeMapperImpl implements CsticTypeMapper
{
	private static final Logger LOG = Logger.getLogger(CsticTypeMapperImpl.class);
	private static final String EMPTY = "";
	private UiTypeFinder uiTypeFinder;
	private ValueFormatTranslator valueFormatTranslator;
	private IntervalInDomainHelper intervalHandler;
	private UniqueUIKeyGenerator uiKeyGenerator;

	private ConfigPricing pricingFactory;
	private ClassificationSystemCPQAttributesProvider nameProvider;
	private ProviderFactory providerFactory;

	private ConfigurationMessageMapper messagesMapper;

	private static final Pattern HTML_MATCHING_PATTERN = Pattern.compile(".*\\<.+?\\>.*");
	private static final String FORMAT = "Update CsticData to CsticModel [CSTIC_NAME='%s'; CSTIC_VALUE_TYPE='%d'; CSTIC_UI_KEY='%s'; CSTIC_UI_TYPE='%s'; CSTIC_VALUE='%s']";



	@Override
	public CsticData mapCsticModelToData(final CsticModel model, final String prefix,
			final Map<String, ClassificationSystemCPQAttributesContainer> nameMap)
	{
		// This method might be called very often (several thousand times) for large customer models.
		// LOG.isDebugEnabled() causes some memory allocation internally, which adds up a lot (2 MB for 90.000 calls)
		// so we read it only once per cstic
		final boolean isDebugEnabled = LOG.isDebugEnabled();
		final boolean isDebugEnabledNameProvider = getNameProvider().isDebugEnabled();
		final CsticData data = new CsticData();
		data.setKey(generateUniqueKey(model, prefix));

		final String name = model.getName();
		final ClassificationSystemCPQAttributesContainer cpqAttributes = getNameProvider().getCPQAttributes(name, nameMap);
		data.setName(name);
		data.setLangdepname(getNameProvider().getDisplayName(model, cpqAttributes, isDebugEnabledNameProvider));
		final String longText = getNameProvider().getLongText(model, cpqAttributes, isDebugEnabledNameProvider);
		data.setLongText(longText);
		data.setLongTextHTMLFormat(containsHTML(longText, isDebugEnabled));

		data.setInstanceId(model.getInstanceId());
		data.setVisible(model.isVisible() || (!model.isConsistent()));
		data.setRequired(model.isRequired());
		data.setIntervalInDomain(model.isIntervalInDomain());

		data.setMaxlength(model.getTypeLength());
		data.setEntryFieldMask(emptyIfNull(model.getEntryFieldMask()));
		fillPlaceholder(model, data);
		data.setAdditionalValue(EMPTY);
		data.setMedia(getNameProvider().getCsticMedia(cpqAttributes));

		final boolean useDeltaPrices = getPricingConfigurationParameters().showDeltaPrices();
		final List<CsticValueData> domainValues = createDomainValues(model, cpqAttributes, isDebugEnabled, useDeltaPrices);
		handlePriceData(model, data, domainValues);
		data.setDomainvalues(domainValues);

		data.setConflicts(Collections.emptyList());
		if (CsticModel.AUTHOR_USER.equals(model.getAuthor()))
		{
			data.setCsticStatus(CsticStatusType.FINISHED);
		}
		else
		{
			data.setCsticStatus(CsticStatusType.DEFAULT);
		}

		final UiType uiType = getUiTypeFinder().findUiTypeForCstic(model, data);
		data.setType(uiType);
		final UiValidationType validationType = getUiTypeFinder().findUiValidationTypeForCstic(model);
		data.setValidationType(validationType);

		final String singleValue = model.getSingleValue();
		final String formattedValue = getValueFormatTranslator().format(model, singleValue);
		data.setValue(singleValue);
		data.setFormattedValue(formattedValue);
		data.setLastValidValue(formattedValue);

		if (UiValidationType.NUMERIC == validationType)
		{
			mapNumericSpecifics(model, data);
		}
		adjustMaxLength(data);

		if (isDebugEnabled)
		{
			LOG.debug("Map CsticModel to CsticData [CSTIC_NAME='" + name + "';CSTIC_UI_KEY='" + data.getKey() + "';CSTIC_UI_TYPE='"
					+ data.getType() + "';CSTIC_VALUE='" + data.getValue() + "']");
		}
		return data;
	}


	protected void adjustMaxLength(final CsticData data)
	{
		int maxLength = data.getMaxlength();

		for (final CsticValueData value : data.getDomainvalues())
		{
			final int valueLength = value.getName().length();
			if (maxLength < valueLength)
			{
				maxLength = valueLength;
			}
		}

		data.setMaxlength(maxLength);

	}

	protected void fillPlaceholder(final CsticModel model, final CsticData data)
	{
		if (CsticModel.TYPE_INTEGER == model.getValueType() || CsticModel.TYPE_FLOAT == model.getValueType())
		{
			data.setPlaceholder(emptyIfNull(getIntervalHandler().retrieveIntervalMask(model)));
		}
		else
		{
			data.setPlaceholder(EMPTY);
		}
	}

	protected String emptyIfNull(final String value)
	{
		return (value == null) ? EMPTY : value;
	}

	protected void mapNumericSpecifics(final CsticModel model, final CsticData data)
	{
		final int numFractionDigits = model.getNumberScale();
		final int typeLength = model.getTypeLength();
		data.setNumberScale(numFractionDigits);
		data.setTypeLength(typeLength);

		int maxlength = typeLength;
		if (numFractionDigits > 0)
		{
			maxlength++;
		}
		final int numDigits = typeLength - numFractionDigits;
		final int maxGroupimgSeperators = (numDigits - 1) / 3;
		maxlength += maxGroupimgSeperators;
		data.setMaxlength(maxlength);
	}

	protected List<CsticValueData> createDomainValues(final CsticModel model,
			final ClassificationSystemCPQAttributesContainer hybrisNames, final boolean isDebugEnabled, final boolean useDeltaPrices)
	{
		final boolean isDebugEnabledNameProvider = getNameProvider().isDebugEnabled();
		int capa = model.getAssignableValues().size();
		if (model.isConstrained() || model.isMultivalued())
		{
			capa += model.getAssignedValues().size();
		}
		final List<CsticValueData> domainValues;
		if (capa == 0)
		{
			domainValues = Collections.emptyList();
		}
		else
		{
			domainValues = new ArrayList<>(capa);
		}

		for (final CsticValueModel csticValue : model.getAssignableValues())
		{
			final CsticValueData domainValue = createDomainValue(model, csticValue, hybrisNames, isDebugEnabled,
					isDebugEnabledNameProvider, useDeltaPrices);
			domainValues.add(domainValue);
		}
		if (model.isConstrained() || model.isMultivalued())
		{
			for (final CsticValueModel assignedValue : model.getAssignedValues())
			{
				if (!model.getAssignableValues().contains(assignedValue))
				{
					final CsticValueData domainValue = createDomainValue(model, assignedValue, hybrisNames, isDebugEnabled,
							isDebugEnabledNameProvider, useDeltaPrices);
					domainValues.add(domainValue);
				}
			}
		}
		return domainValues;
	}

	/**
	 * Adds zero prices with currency to the domain-values if the cstic is price relevant.
	 *
	 * @param model
	 * @param data
	 * @param domainValues
	 */
	protected void handlePriceData(final CsticModel model, final CsticData data, final List<CsticValueData> domainValues)
	{
		final String currencyIso = identifyPriceRelevanceAndCurrency(model, data);
		if (data.isPriceRelevant())
		{
			for (final CsticValueData domainValue : domainValues)
			{
				if (domainValue.getDeltaPrice().equals(ConfigPricing.NO_PRICE))
				{
					final PriceModel priceModel = new PriceModelImpl();
					priceModel.setCurrency(currencyIso);
					priceModel.setPriceValue(BigDecimal.ZERO);
					final PriceData priceData = getPricingFactory().getPriceData(priceModel);
					domainValue.setDeltaPrice(priceData);
				}
				if (domainValue.getPrice().equals(ConfigPricing.NO_PRICE))
				{
					final PriceModel priceModel = new PriceModelImpl();
					priceModel.setCurrency(currencyIso);
					priceModel.setPriceValue(BigDecimal.ZERO);
					final PriceData priceData = getPricingFactory().getPriceData(priceModel);
					domainValue.setPrice(priceData);
				}
			}
		}
	}

	protected CsticValueData createDomainValue(final CsticModel csticModel, final CsticValueModel csticValueModel,
			final ClassificationSystemCPQAttributesContainer hybrisNames, final boolean isDebugEnabled,
			final boolean isDebugEnabledNameProvider, final boolean useDeltaPrices)
	{
		final CsticValueData domainValue = new CsticValueData();
		final String name = csticValueModel.getName();
		domainValue.setKey(name);
		final String langDepName;
		if (CsticModel.TYPE_STRING == csticModel.getValueType())
		{
			langDepName = getNameProvider().getDisplayValueName(csticValueModel, csticModel, hybrisNames,
					isDebugEnabledNameProvider);
		}
		else
		{
			langDepName = getValueFormatTranslator().format(csticModel, name);
		}
		domainValue.setLangdepname(langDepName);
		domainValue.setName(name);
		final String longText = getNameProvider().getValueLongText(csticValueModel, csticModel, hybrisNames,
				isDebugEnabledNameProvider);
		domainValue.setLongText(longText);
		domainValue.setLongTextHTMLFormat(containsHTML(longText, isDebugEnabled));
		final boolean isAssigned = csticModel.getAssignedValues().contains(csticValueModel);
		domainValue.setSelected(isAssigned);

		final StringBuilder csticValueKey = new StringBuilder();
		csticValueKey.append(csticModel.getName());
		csticValueKey.append('_');
		csticValueKey.append(name);
		domainValue.setMedia(getNameProvider().getCsticValueMedia(csticValueKey.toString(), hybrisNames));

		final boolean isReadOnly = checkReadonly(csticValueModel, isDebugEnabled);
		domainValue.setReadonly(isReadOnly);

		final PriceData price = getPricingFactory().getPriceData(csticValueModel.getDeltaPrice());
		domainValue.setDeltaPrice(price);

		setPrice(csticValueModel, domainValue, useDeltaPrices);
		messagesMapper.mapMessagesFromModelToData(domainValue, csticValueModel);

		return domainValue;
	}


	protected void setPrice(final CsticValueModel csticValueModel, final CsticValueData domainValue, final boolean useDeltaPrices)
	{
		final PriceData valuePrice;
		final PriceData obsoleteValuePrice;
		if (useDeltaPrices)
		{
			valuePrice = getPricingFactory().getPriceData(csticValueModel.getDeltaPrice());
			obsoleteValuePrice = getPricingFactory().getObsoletePriceData(csticValueModel.getDeltaPrice());
		}
		else
		{
			valuePrice = getPricingFactory().getPriceData(csticValueModel.getValuePrice());
			obsoleteValuePrice = getPricingFactory().getObsoletePriceData(csticValueModel.getValuePrice());
		}
		domainValue.setPrice(valuePrice);
		domainValue.setObsoletePrice(obsoleteValuePrice);
		domainValue.setShowDeltaPrice(useDeltaPrices);
	}



	protected boolean checkReadonly(final CsticValueModel csticValue, final boolean isDebugEnabled)
	{
		final boolean isSystemValue = csticValue.getAuthor() != null && csticValue.getAuthor().equalsIgnoreCase(READ_ONLY_AUTHOR);

		final boolean isSelectable = csticValue.isSelectable();

		if (isDebugEnabled)
		{
			final String msg = String.format(
					"CsticValueModel [CSTIC_NAME='%s'; CSTIC_IS_SYSTEM_VALUE='%b'; CSTIC_IS_SELECTABLE='%b']", csticValue.getName(),
					Boolean.valueOf(isSystemValue), Boolean.valueOf(isSelectable));
			LOG.debug(msg);
		}

		return isSystemValue || !isSelectable;
	}


	@Override
	public void updateCsticModelValuesFromData(final CsticData data, final CsticModel model)
	{
		// This method might be called very often (several thousand times) for large customer models.
		// LOG.isDebugEnabled() causes some memory allocation internally, which adds up a lot (2 MB for 90.000 calls)
		// so we read it only once per cstic
		final boolean isDebugEnabled = LOG.isDebugEnabled();
		handleRetraction(data, model, isDebugEnabled);
		final UiType uiType = data.getType();

		if (isUiTypeReadOnly(uiType))
		{
			return;
		}

		if (isUiTypeMultiselectionValue(uiType) && data.getDomainvalues() != null)
		{
			for (final CsticValueData valueData : data.getDomainvalues())
			{
				final String value = valueData.getName();
				final String parsedValue = getValueFormatTranslator().parse(uiType, value);
				if (valueData.isSelected())
				{
					model.addValue(parsedValue);
				}
				else
				{
					model.removeValue(parsedValue);
				}
			}
		}
		else
		{
			String value = getValueFromCstcData(data, isDebugEnabled);
			if (isUiTypeWithAdditionalValue(uiType))
			{
				value = getValueForUiTypeWithAdditionalValue(data, model, value);
			}
			else
			{
				value = getValueFormatTranslator().parse(uiType, value);
			}

			if (isUiTypeDrownDownAndNullValue(uiType, value))
			{
				model.setSingleValue(null);
			}
			else
			{
				model.setSingleValue(value);
			}
		}

		if (isDebugEnabled)
		{
			final Object[] args = new Object[5];
			args[0] = model.getName();
			args[1] = Integer.valueOf(model.getValueType());
			args[2] = data.getKey();
			args[3] = data.getType();
			args[4] = data.getValue();
			final String msg = String.format(FORMAT, args);
			LOG.debug(msg);
		}
	}

	protected String getValueForUiTypeWithAdditionalValue(final CsticData data, final CsticModel model, final String value)
	{
		final String additionalValue = data.getAdditionalValue();
		if (StringUtils.isEmpty(additionalValue))
		{
			return value;
		}

		if (!getValueFormatTranslator().isNumericCsticType(model))
		{
			return additionalValue;
		}

		return getValueFormatTranslator().parse(UiType.NUMERIC, additionalValue);
	}

	protected boolean isUiTypeDrownDownAndNullValue(final UiType uiType, final String value)
	{
		return (UiType.DROPDOWN_ADDITIONAL_INPUT == uiType || UiType.DROPDOWN == uiType) && "NULL_VALUE".equals(value);
	}

	protected boolean isUiTypeWithAdditionalValue(final UiType uiType)
	{
		return UiType.DROPDOWN_ADDITIONAL_INPUT == uiType || UiType.RADIO_BUTTON_ADDITIONAL_INPUT == uiType;
	}

	protected boolean isUiTypeMultiselectionValue(final UiType uiType)
	{
		return UiType.CHECK_BOX_LIST == uiType || UiType.CHECK_BOX == uiType || UiType.MULTI_SELECTION_IMAGE == uiType;
	}

	/**
	 * Handles the retraction of a cstic which means that all user inputs to this cstic are discarded. This is needed for
	 * conflict solving
	 *
	 * @param data
	 * @param model
	 */
	protected void handleRetraction(final CsticData data, final CsticModel model, final boolean isDebugEnabled)
	{
		if (data.isRetractTriggered())
		{
			model.setRetractTriggered(true);
			if (isDebugEnabled)
			{
				LOG.debug("Cstic: " + data.getName() + " is marked as retracted");
			}
		}
	}


	protected String getValueFromCstcData(final CsticData data, final boolean isDebugEnabled)
	{
		String value = data.getValue();
		final UiType uiType = data.getType();
		if (UiType.NUMERIC == uiType)
		{
			value = data.getFormattedValue();
		}

		if (isDebugEnabled)
		{
			final String msg = String.format("CsticData [CSTIC_NAME='%s'; CSTIC_VALUE='%s']", data.getName(), value);
			LOG.debug(msg);
		}

		return value;
	}

	@Override
	public String generateUniqueKey(final CsticModel model, final String prefix)
	{
		final String key = getUiKeyGenerator().generateCsticId(model, null, prefix);

		if (LOG.isDebugEnabled())
		{
			final String msg = String.format("CsticModel [CSTIC_NAME='%s'; CSTIC_UI_KEY='%s']", model.getName(), key);
			LOG.debug(msg);
		}

		return key;
	}

	protected boolean containsHTML(final String longText, final boolean isDebugEnabled)
	{
		boolean containsHTML = false;
		if (longText != null)
		{
			containsHTML = HTML_MATCHING_PATTERN.matcher(longText).matches();
		}

		if (isDebugEnabled)
		{
			LOG.debug("Long text contains HTML: '" + containsHTML + "'");
		}

		return containsHTML;
	}

	/**
	 * Checks the CsticModel for price relevance (i.e. at least one assignable value has a value price). It sets the
	 * priceRelevant-flag at csticData and returns the relevant currency (ISO) if the cstic is price relevant.
	 *
	 * @param csticModel
	 *           model to be checked
	 * @param csticData
	 *           DTO to be modified
	 * @return currency (ISO) if cstic is price relevant otherwise null
	 */
	protected String identifyPriceRelevanceAndCurrency(final CsticModel csticModel, final CsticData csticData)
	{
		String currencyIso = null;
		final List<CsticValueModel> values = csticModel.getAssignableValues();
		if (values != null)
		{
			for (final CsticValueModel value : values)
			{
				if (!value.getValuePrice().equals(PriceModel.NO_PRICE))
				{
					csticData.setPriceRelevant(true);
					currencyIso = value.getValuePrice().getCurrency();
					break;
				}
			}
		}
		return currencyIso;
	}

	/**
	 *
	 * @param uiTypeFinder
	 */
	@Required
	public void setUiTypeFinder(final UiTypeFinder uiTypeFinder)
	{
		this.uiTypeFinder = uiTypeFinder;
	}

	protected UiTypeFinder getUiTypeFinder()
	{
		return uiTypeFinder;
	}

	/**
	 *
	 * @param pricingFactory
	 */
	@Required
	public void setPricingFactory(final ConfigPricing pricingFactory)
	{
		this.pricingFactory = pricingFactory;
	}

	/**
	 *
	 * @return pricingFactory
	 */
	protected ConfigPricing getPricingFactory()
	{
		return pricingFactory;
	}

	/**
	 * @param valueFormatTranslator
	 *           the valueFormatTranslator to set
	 */
	@Required
	public void setValueFormatTranslator(final ValueFormatTranslator valueFormatTranslator)
	{
		this.valueFormatTranslator = valueFormatTranslator;
	}

	/**
	 * @return the valueFormatTranslator
	 */
	protected ValueFormatTranslator getValueFormatTranslator()
	{
		return valueFormatTranslator;
	}

	/**
	 * @param nameProvider
	 *           hybris characteristic and value name provider
	 */
	@Required
	public void setNameProvider(final ClassificationSystemCPQAttributesProvider nameProvider)
	{
		this.nameProvider = nameProvider;
	}

	/**
	 * @return the hybris characteristic and value name provider
	 */
	protected ClassificationSystemCPQAttributesProvider getNameProvider()
	{
		return nameProvider;
	}

	/**
	 * @param uiKeyGenerator
	 *           for generating uniqueKeys
	 */
	@Required
	public void setUiKeyGenerator(final UniqueUIKeyGenerator uiKeyGenerator)
	{
		this.uiKeyGenerator = uiKeyGenerator;
	}

	protected UniqueUIKeyGenerator getUiKeyGenerator()
	{
		return uiKeyGenerator;
	}

	protected IntervalInDomainHelper getIntervalHandler()
	{
		return intervalHandler;
	}

	/**
	 * @param intervalHandler
	 */
	public void setIntervalHandler(final IntervalInDomainHelper intervalHandler)
	{
		this.intervalHandler = intervalHandler;
	}


	protected boolean isUiTypeReadOnly(final UiType uiType)
	{
		return UiType.READ_ONLY == uiType || UiType.READ_ONLY_MULTI_SELECTION_IMAGE == uiType
				|| UiType.READ_ONLY_SINGLE_SELECTION_IMAGE == uiType;
	}

	/**
	 * @return the pricingConfigurationParameter
	 */
	protected PricingConfigurationParameter getPricingConfigurationParameters()
	{
		return getProviderFactory().getPricingParameter();
	}

	protected ProviderFactory getProviderFactory()
	{
		return providerFactory;
	}

	/**
	 * @param providerFactory
	 *           the provider factory to set
	 */
	@Required
	public void setProviderFactory(final ProviderFactory providerFactory)
	{
		this.providerFactory = providerFactory;
	}

	protected ConfigurationMessageMapper getMessagesMapper()
	{
		return messagesMapper;
	}

	/**
	 *
	 * @param messagesMapper
	 *           the messages mapper to set
	 */
	@Required
	public void setMessagesMapper(final ConfigurationMessageMapper messagesMapper)
	{
		this.messagesMapper = messagesMapper;
	}

}
