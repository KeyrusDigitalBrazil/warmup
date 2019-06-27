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
package de.hybris.platform.warehousing.util.builder;

import com.google.common.collect.Sets;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.model.SourcingConfigModel;


public class SourcingConfigModelBuilder
{
	private final SourcingConfigModel model;

	private SourcingConfigModelBuilder()
	{
		model = new SourcingConfigModel();
	}

	private SourcingConfigModel getModel()
	{
		return this.model;
	}

	public static SourcingConfigModelBuilder aModel()
	{
		return new SourcingConfigModelBuilder();
	}

	public SourcingConfigModel build()
	{
		return getModel();
	}

	public SourcingConfigModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public SourcingConfigModelBuilder withBaseStore(final BaseStoreModel baseStore)
	{
		getModel().setBaseStores(Sets.newHashSet(baseStore));
		return this;
	}

	public SourcingConfigModelBuilder withSourcingFactorsWeight(final int distanceWeightFactor, final int allocationWeightFactor, final int priorityWeightFactor, final int scoreWeightFactor)
	{
		final SourcingConfigModel sourcingConfig = getModel();
		sourcingConfig.setDistanceWeightFactor(distanceWeightFactor);
		sourcingConfig.setAllocationWeightFactor(allocationWeightFactor);
		sourcingConfig.setPriorityWeightFactor(priorityWeightFactor);
		sourcingConfig.setScoreWeightFactor(scoreWeightFactor);
		return this;
	}
	
}
