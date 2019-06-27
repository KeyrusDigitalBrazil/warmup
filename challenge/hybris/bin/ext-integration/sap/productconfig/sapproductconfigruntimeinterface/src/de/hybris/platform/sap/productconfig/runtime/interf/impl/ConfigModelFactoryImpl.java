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
package de.hybris.platform.sap.productconfig.runtime.interf.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.ConfigModelFactory;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessage;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSeverity;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSource;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ProductConfigMessageSourceSubType;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.VariantConditionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ProductConfigMessageBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * Factory for ConfigModel objects.
 */
public class ConfigModelFactoryImpl implements ConfigModelFactory
{
	private String targetClassNameConfigModel;
	private String targetClassNameInstanceModel;
	private String targetClassNameCsticModel;
	private String targetClassNameCsticValueModel;
	private String targetClassNameCsticGroupModel;
	private String targetClassNamePriceModel;
	private String targetClassNamePriceSummaryModel;
	private String targetClassNameSolvableConflictModel;
	private String targetClassNameConflictingAssumptionModel;
	private String targetClassNameVariantConditionModel;

	private static final Logger LOG = Logger.getLogger(ConfigModelFactoryImpl.class);
	private static final String FALLBACK_FOR_TESTS = "Class name for requested model not provided via spring-context, using default values '%s' as fall back.";

	private static final String CONFIG_MODEL = "de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl";
	private static final String INSTANCE_MODEL = "de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl";
	private static final String CSTIC_MODEL = "de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticModelImpl";
	private static final String CSTIC_GROUP_MODEL = "de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticGroupModelImpl";
	private static final String CSTIC_VALUE_MODEL = "de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl";
	private static final String PRICE_MODEL = "de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl";
	private static final String PRICE_SUMMARY_MODEL = "de.hybris.platform.sap.productconfig.runtime.interf.model.PriceSummaryModel";
	private static final String SOLVABLE_CONFLICT_MODEL = "de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel";
	private static final String CONFLICTION_ASSUMPTION_MODEL = "de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel";
	private static final String VARIANT_CONDITION_MODEL = "de.hybris.platform.sap.productconfig.runtime.interf.model.impl.VariantConditionModelImpl";


	@Override
	public ConfigModel createInstanceOfConfigModel()
	{
		return createModelInstance(getTargetClassNameConfigModel(), CONFIG_MODEL);
	}

	@Override
	public InstanceModel createInstanceOfInstanceModel()
	{
		return createModelInstance(getTargetClassNameInstanceModel(), INSTANCE_MODEL);
	}

	@Override
	public CsticModel createInstanceOfCsticModel()
	{
		return createModelInstance(getTargetClassNameCsticModel(), CSTIC_MODEL);
	}

	@Override
	public CsticValueModel createInstanceOfCsticValueModel(final int valueType)
	{
		if (valueType == CsticModel.TYPE_FLOAT || valueType == CsticModel.TYPE_INTEGER)
		{
			final CsticValueModel modelInstance = createModelInstance(getTargetClassNameCsticValueModel(), CSTIC_VALUE_MODEL);
			modelInstance.setNumeric(true);
			return modelInstance;
		}

		return createModelInstance(getTargetClassNameCsticValueModel(), CSTIC_VALUE_MODEL);
	}

	@Override
	public CsticGroupModel createInstanceOfCsticGroupModel()
	{
		return createModelInstance(getTargetClassNameCsticGroupModel(), CSTIC_GROUP_MODEL);
	}

	@Override
	public PriceModel createInstanceOfPriceModel()
	{
		return createModelInstance(getTargetClassNamePriceModel(), PRICE_MODEL);
	}

	@Override
	public PriceSummaryModel createInstanceOfPriceSummaryModel()
	{
		return createModelInstance(getTargetClassNamePriceSummaryModel(), PRICE_SUMMARY_MODEL);
	}

	@Override
	public SolvableConflictModel createInstanceOfSolvableConflictModel()
	{
		return createModelInstance(getTargetClassNameSolvableConflictModel(), SOLVABLE_CONFLICT_MODEL);
	}

	@Override
	public ConflictingAssumptionModel createInstanceOfConflictingAssumptionModel()
	{
		return createModelInstance(getTargetClassNameConflictingAssumptionModel(), CONFLICTION_ASSUMPTION_MODEL);
	}


	@Override
	public PriceModel getZeroPriceModel()
	{
		return PriceModel.NO_PRICE;
	}


	/**
	 * @deprecated since 18.08.0 - use {@link ProductConfigMessageBuilder} instead
	 */
	@Override
	@Deprecated
	public ProductConfigMessage createInstanceOfProductConfigMessage(final String message, final String key,
			final ProductConfigMessageSeverity severity, final ProductConfigMessageSource source,
			final ProductConfigMessageSourceSubType subType)
	{
		final ProductConfigMessageBuilder builder = createProductConfigMessageBuilder();
		builder.appendBasicFields(message, key, severity);
		builder.appendSourceAndType(source, subType);
		return builder.build();
	}


	protected <T> T createModelInstance(final String targetClassName, final String defaultModelInstance)
	{
		try
		{
			if (StringUtils.isEmpty(targetClassName))
			{
				LOG.warn(String.format(FALLBACK_FOR_TESTS, defaultModelInstance));
				return (T) Class.forName(defaultModelInstance).newInstance();
			}
			return (T) Class.forName(targetClassName).newInstance();
		}
		catch (final InstantiationException | IllegalAccessException | ClassNotFoundException ex)
		{
			throw new IllegalArgumentException("Could not create model for class " + targetClassName, ex);
		}
	}

	@Override
	public VariantConditionModel createInstanceOfVariantConditionModel()
	{
		return createModelInstance(getTargetClassNameVariantConditionModel(), VARIANT_CONDITION_MODEL);
	}

	@Override
	public String getTargetClassNameConfigModel()
	{
		return targetClassNameConfigModel;
	}

	/**
	 * @param targetClassNameConfigModel
	 *           class name of {@link ConfigModel} implementation
	 */
	@Required
	public void setTargetClassNameConfigModel(final String targetClassNameConfigModel)
	{
		this.targetClassNameConfigModel = targetClassNameConfigModel;
	}

	@Override
	public String getTargetClassNameInstanceModel()
	{
		return targetClassNameInstanceModel;
	}

	/**
	 * @param targetClassNameInstanceModel
	 *           class name of {@link InstanceModel} implementation
	 */
	@Required
	public void setTargetClassNameInstanceModel(final String targetClassNameInstanceModel)
	{
		this.targetClassNameInstanceModel = targetClassNameInstanceModel;
	}

	@Override
	public String getTargetClassNameCsticModel()
	{
		return targetClassNameCsticModel;
	}

	/**
	 * @param targetClassNameCsticModel
	 *           class name of {@link CsticModel} implementation
	 */
	@Required
	public void setTargetClassNameCsticModel(final String targetClassNameCsticModel)
	{
		this.targetClassNameCsticModel = targetClassNameCsticModel;
	}

	@Override
	public String getTargetClassNameCsticValueModel()
	{
		return targetClassNameCsticValueModel;
	}

	/**
	 * @param targetClassNameCsticValueModel
	 *           class name of {@link CsticValueModel} implementation
	 */
	@Required
	public void setTargetClassNameCsticValueModel(final String targetClassNameCsticValueModel)
	{
		this.targetClassNameCsticValueModel = targetClassNameCsticValueModel;
	}

	@Override
	public String getTargetClassNameCsticGroupModel()
	{
		return targetClassNameCsticGroupModel;
	}

	/**
	 * @param targetClassNameCsticGroupModel
	 *           class name of {@link CsticGroupModel} implementation
	 */
	@Required
	public void setTargetClassNameCsticGroupModel(final String targetClassNameCsticGroupModel)
	{
		this.targetClassNameCsticGroupModel = targetClassNameCsticGroupModel;
	}

	@Override
	public String getTargetClassNamePriceModel()
	{
		return targetClassNamePriceModel;
	}

	/**
	 * @param targetClassNamePriceModel
	 *           class name of {@link PriceModel} implementation
	 */
	@Required
	public void setTargetClassNamePriceModel(final String targetClassNamePriceModel)
	{
		this.targetClassNamePriceModel = targetClassNamePriceModel;
	}

	@Override
	public String getTargetClassNamePriceSummaryModel()
	{
		return targetClassNamePriceSummaryModel;
	}

	/**
	 * @param targetClassNamePriceSummaryModel
	 *           class name of {@link PriceModel} implementation
	 */
	@Required
	public void setTargetClassNamePriceSummaryModel(final String targetClassNamePriceSummaryModel)
	{
		this.targetClassNamePriceSummaryModel = targetClassNamePriceSummaryModel;
	}


	@Override
	public String getTargetClassNameSolvableConflictModel()
	{
		return targetClassNameSolvableConflictModel;
	}

	/**
	 * @param targetClassNameSolvableConflictModel
	 *           class name of {@link SolvableConflictModel} implementation
	 */
	public void setTargetClassNameSolvableConflictModel(final String targetClassNameSolvableConflictModel)
	{
		this.targetClassNameSolvableConflictModel = targetClassNameSolvableConflictModel;
	}

	@Override
	public String getTargetClassNameConflictingAssumptionModel()
	{
		return targetClassNameConflictingAssumptionModel;
	}

	/**
	 * @param targetClassNameConflictingAssumptionModel
	 *           class name of {@link ConflictingAssumptionModel} implementation
	 */
	public void setTargetClassNameConflictingAssumptionModel(final String targetClassNameConflictingAssumptionModel)
	{
		this.targetClassNameConflictingAssumptionModel = targetClassNameConflictingAssumptionModel;
	}

	@Override
	public String getTargetClassNameVariantConditionModel()
	{
		return targetClassNameVariantConditionModel;
	}

	/**
	 * @param targetClassNameVariantConditionModel
	 *           class name of {@link VariantConditionModel} implementation
	 */
	public void setTargetClassNameVariantConditionModel(final String targetClassNameVariantConditionModel)
	{
		this.targetClassNameVariantConditionModel = targetClassNameVariantConditionModel;
	}

}
