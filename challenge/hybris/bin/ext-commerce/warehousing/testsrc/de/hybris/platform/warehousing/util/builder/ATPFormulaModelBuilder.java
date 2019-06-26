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
package de.hybris.platform.warehousing.util.builder;

import com.google.common.collect.Sets;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.warehousing.model.AtpFormulaModel;


public class ATPFormulaModelBuilder
{
	private final AtpFormulaModel model;

	private ATPFormulaModelBuilder()
	{
		model = new AtpFormulaModel();
	}

	private AtpFormulaModel getModel()
	{
		return this.model;
	}

	public static ATPFormulaModelBuilder aModel()
	{
		return new ATPFormulaModelBuilder();
	}

	public AtpFormulaModel build()
	{
		return getModel();
	}

	public ATPFormulaModelBuilder withCode(final String code)
	{
		getModel().setCode(code);
		return this;
	}

	public ATPFormulaModelBuilder withBaseStore(final BaseStoreModel baseStore)
	{
		getModel().setBaseStores(Sets.newHashSet(baseStore));
		return this;
	}

	public ATPFormulaModelBuilder withFormula(final Boolean availability,final Boolean allocation,final Boolean cancellation,final Boolean increase,final Boolean reserved,final Boolean shrinkage,final Boolean wastage, final Boolean returned, final Boolean external)
	{
		getModel().setAvailability(availability);
		getModel().setAllocation(allocation);
		getModel().setCancellation(cancellation);
		getModel().setIncrease(increase);
		getModel().setReserved(reserved);
		getModel().setShrinkage(shrinkage);
		getModel().setWastage(wastage);
		getModel().setReturned(returned);
		getModel().setExternal(external);
		return this;
	}
}
