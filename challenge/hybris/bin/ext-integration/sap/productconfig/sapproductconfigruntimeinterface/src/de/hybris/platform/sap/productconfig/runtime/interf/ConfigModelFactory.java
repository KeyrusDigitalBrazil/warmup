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
package de.hybris.platform.sap.productconfig.runtime.interf;

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


/**
 * Create instances of all configuration model elements.
 */
public interface ConfigModelFactory
{
	/**
	 * Create an instance of the <code>ConfigModel</code>
	 *
	 * @return an instance of the configuration model
	 */
	ConfigModel createInstanceOfConfigModel();

	/**
	 * Create an instance of the <code>InstanceModel</code>
	 *
	 * @return an instance of the instance model
	 */
	InstanceModel createInstanceOfInstanceModel();

	/**
	 * Create an instance of the <code>CsticModel</code>
	 *
	 * @return an instance of the characteristic model
	 */
	CsticModel createInstanceOfCsticModel();

	/**
	 * Create an instance of the <code>CsticValueModel</code>
	 *
	 * @param valueType
	 *           The containing Cstic value type
	 * @return an instance of the characteristic value model
	 */
	CsticValueModel createInstanceOfCsticValueModel(int valueType);

	/**
	 * Create an instance of the <code>CsticGroupModel</code>
	 *
	 * @return an instance of the characteristic group model
	 */
	CsticGroupModel createInstanceOfCsticGroupModel();


	/**
	 * Create an instance of the <code>PriceModel</code>
	 *
	 * @return an instance of the price model
	 */
	PriceModel createInstanceOfPriceModel();

	/**
	 * Create an instance of the <code>SolvableConflictModel</code>
	 *
	 * @return an instance of the Solvable Conflict Model
	 */
	SolvableConflictModel createInstanceOfSolvableConflictModel();

	/**
	 * Create an instance of the <code>ConflictingAssumptionModel</code>
	 *
	 * @return an instance of the Conflicting Assumption Model
	 */
	ConflictingAssumptionModel createInstanceOfConflictingAssumptionModel();

	/**
	 * Create an instance of the <code>PriceModel</code>
	 *
	 * @return an instance of the price model
	 */
	PriceModel getZeroPriceModel();

	/**
	 * Create an instance of the <code>PriceSummaryModel</code>
	 *
	 * @return an instance of the price summary model
	 */
	PriceSummaryModel createInstanceOfPriceSummaryModel();

	/**
	 * @return a builder to construct {@link ProductConfigMessage} objects
	 */
	default ProductConfigMessageBuilder createProductConfigMessageBuilder()
	{
		return new ProductConfigMessageBuilder();
	}

	/**
	 * Create an instance of the <code>ProductConfigMessage</code><br>
	 * uses the <code>DEFAULT</code> ProductConfigMessageSourceSubType
	 *
	 * @param message
	 *           localized message
	 * @param key
	 *           message key, should be unique together with message source
	 * @param severity
	 *           message severity
	 * @param source
	 *           message source, should be unique together with message key
	 * @return a message instance
	 * @deprecated since 18.08.0 - use {@link ProductConfigMessageBuilder} instead
	 */
	@Deprecated
	default ProductConfigMessage createInstanceOfProductConfigMessage(final String message, final String key,
			final ProductConfigMessageSeverity severity, final ProductConfigMessageSource source)
	{
		return createInstanceOfProductConfigMessage(message, key, severity, source, ProductConfigMessageSourceSubType.DEFAULT);
	}

	/**
	 * Create an instance of the <code>ProductConfigMessage</code><br>
	 *
	 * @param message
	 *           localized message
	 * @param key
	 *           message key, should be unique together with message source
	 * @param severity
	 *           message severity
	 * @param source
	 *           message source, should be unique together with message key
	 * @param subType
	 *           optional sub type of the message source
	 * @return a message instance
	 * @deprecated since 18.08.0 - use {@link ProductConfigMessageBuilder} instead
	 */
	@Deprecated
	ProductConfigMessage createInstanceOfProductConfigMessage(String message, String key, ProductConfigMessageSeverity severity,
			ProductConfigMessageSource source, ProductConfigMessageSourceSubType subType);


	/**
	 * Create an instance of the <code>VariantConditionModel</code>
	 *
	 * @return an instance of the variant condition model
	 */
	VariantConditionModel createInstanceOfVariantConditionModel();

	/**
	 * @return class name of the {@link PriceSummaryModel} implementation
	 */
	String getTargetClassNamePriceSummaryModel();


	/**
	 * @return class name of the {@link PriceModel} implementation
	 */
	String getTargetClassNamePriceModel();

	/**
	 * @return class name of the {@link CsticGroupModel} implementation
	 */
	String getTargetClassNameCsticGroupModel();

	/**
	 * @return class name of the {@link CsticValueModel} implementation
	 */
	String getTargetClassNameCsticValueModel();

	/**
	 * @return class name of the {@link CsticModel} implementation
	 */
	String getTargetClassNameCsticModel();

	/**
	 * @return class name of the {@link InstanceModel} implementation
	 */
	String getTargetClassNameInstanceModel();

	/**
	 * @return class name of the {@link ConfigModel} implementation
	 */
	String getTargetClassNameConfigModel();

	/**
	 * @return class name of the {@link SolvableConflictModel} implementation
	 */
	String getTargetClassNameSolvableConflictModel();

	/**
	 * @return class name of the {@link ConflictingAssumptionModel} implementation
	 */
	String getTargetClassNameConflictingAssumptionModel();

	/**
	 * @return class name of the {@link VariantConditionModel} implementation
	 */
	String getTargetClassNameVariantConditionModel();
}
