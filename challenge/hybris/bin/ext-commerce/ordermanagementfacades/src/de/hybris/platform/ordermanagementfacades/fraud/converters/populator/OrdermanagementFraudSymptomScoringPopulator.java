/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 *
 */
package de.hybris.platform.ordermanagementfacades.fraud.converters.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.fraud.model.FraudSymptomScoringModel;
import de.hybris.platform.ordermanagementfacades.fraud.data.FraudSymptomScoringsData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;


/**
 * Default order management implementation of fraud symptom scoring populator. 
 */
public class OrdermanagementFraudSymptomScoringPopulator implements Populator<FraudSymptomScoringModel, FraudSymptomScoringsData>
{
	@Override
	public void populate(final FraudSymptomScoringModel source, final FraudSymptomScoringsData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		
		target.setExplanation(source.getExplanation());
		target.setName(source.getName());
		target.setScore(source.getScore());
	}

}
