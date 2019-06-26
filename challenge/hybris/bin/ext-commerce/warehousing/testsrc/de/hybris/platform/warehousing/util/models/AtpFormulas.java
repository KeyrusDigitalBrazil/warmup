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
package de.hybris.platform.warehousing.util.models;

import de.hybris.platform.warehousing.model.AtpFormulaModel;
import de.hybris.platform.warehousing.util.builder.ATPFormulaModelBuilder;
import de.hybris.platform.warehousing.util.dao.WarehousingDao;
import org.springframework.beans.factory.annotation.Required;


public class AtpFormulas extends AbstractItems<AtpFormulaModel>
{
	public static final String ATPFORMULA_NAME = "hybris";
	public static final String ATPFORMULA_CUSTOM_NAME = "custom";

	private WarehousingDao<AtpFormulaModel> atpFormulaDao;

	public AtpFormulaModel Hybris()
	{
		final Boolean AVAILABILITY = Boolean.TRUE;
		final Boolean ALLOCATION = Boolean.TRUE;
		final Boolean CANCELLATION = Boolean.TRUE;
		final Boolean INCREASE = Boolean.TRUE;
		final Boolean RESERVED = Boolean.TRUE;
		final Boolean SHRINKAGE = Boolean.TRUE;
		final Boolean WASTAGE = Boolean.TRUE;
		final Boolean RETURNED = Boolean.FALSE;
		final Boolean EXTERNAL = Boolean.TRUE;

		return getOrSaveAndReturn(() -> getAtpFormulaDao().getByCode(ATPFORMULA_NAME),
				() -> ATPFormulaModelBuilder.aModel()
				.withCode(ATPFORMULA_NAME).withFormula(AVAILABILITY,ALLOCATION,CANCELLATION,INCREASE,RESERVED,SHRINKAGE,WASTAGE, RETURNED, EXTERNAL)
				.build());
	}

	public AtpFormulaModel customFormula(final Boolean includeAvailability, final Boolean includeAllocation, final Boolean includeCancellation, final Boolean includeIncrease, final Boolean includeReserved, final Boolean includeShrinkage, final Boolean includeWastage, final Boolean includeReturned, final Boolean includeExternal)
	{
		final AtpFormulaModel atpFormulaModel = getOrSaveAndReturn(() -> getAtpFormulaDao().getByCode(ATPFORMULA_CUSTOM_NAME),
				() -> ATPFormulaModelBuilder.aModel()
						.withCode(ATPFORMULA_CUSTOM_NAME)
						.withFormula(includeAvailability,includeAllocation,includeCancellation,includeIncrease,includeReserved,includeShrinkage,includeWastage, includeReturned, includeExternal)
						.build());

		getModelService().save(atpFormulaModel);
		return atpFormulaModel;
	}

	protected WarehousingDao<AtpFormulaModel> getAtpFormulaDao()
	{
		return atpFormulaDao;
	}

	@Required
	public void setAtpFormulaDao(final WarehousingDao<AtpFormulaModel> atpFormulaDao)
	{
		this.atpFormulaDao = atpFormulaDao;
	}
}
