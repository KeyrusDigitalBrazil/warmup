/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commercefacades.converter.config;

import de.hybris.platform.commercefacades.converter.ModifiableConfigurablePopulator;


/**
 * Spring Bean used to modify {@link ModifiableConfigurablePopulator} instances. Supports adding or removing a
 * populator.
 *
 * @deprecated Since 6.0. Use {@link de.hybris.platform.converters.config.ConfigurablePopulatorModification} instead. Will be
 *             removed in version 6.2
 */
@Deprecated
public class ConfigurablePopulatorModification<SOURCE, TARGET, OPTION> extends
		de.hybris.platform.converters.config.ConfigurablePopulatorModification<SOURCE, TARGET, OPTION>
{
	//deprecated
}
