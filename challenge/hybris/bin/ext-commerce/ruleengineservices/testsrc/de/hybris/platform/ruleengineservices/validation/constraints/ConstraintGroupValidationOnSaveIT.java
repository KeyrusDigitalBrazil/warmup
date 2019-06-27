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
package de.hybris.platform.ruleengineservices.validation.constraints;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.validation.daos.ConstraintDao;
import de.hybris.platform.validation.model.constraints.AbstractConstraintModel;
import de.hybris.platform.validation.model.constraints.ConstraintGroupModel;

import java.util.Collections;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class ConstraintGroupValidationOnSaveIT extends ServicelayerTest
{
	private static final String CONSTRAINT_ID = "testPattern";

	@Resource
	protected ModelService modelService;

	@Resource
	protected ConstraintDao constraintDao;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/ruleengineservices/test/constraint/validation-test-data.impex", "UTF-8");
	}

	@Test
	public void testConstraintGroupSave()
	{
		final ConstraintGroupModel constraintGroup = modelService.create(ConstraintGroupModel.class);
		constraintGroup.setId("defaultBackofficeValidationGroup");
		constraintGroup.setInterfaceName("de.hybris.platform.validation.groupinterfaces.DefaultBackofficeValidationGroup");
		final AbstractConstraintModel objectPatternConstraint = getObjectPatternConstraint();
		constraintGroup.setConstraints(Collections.singleton(objectPatternConstraint));
		objectPatternConstraint.setConstraintGroups(Collections.singleton(constraintGroup));
		modelService.saveAll(constraintGroup, objectPatternConstraint);
	}

	protected AbstractConstraintModel getObjectPatternConstraint()
	{
		return constraintDao.getAllConstraints().stream().filter(c -> CONSTRAINT_ID.equals(c.getId())).findFirst()
				.orElseThrow(() -> new RuntimeException("No Constraint with ID " + CONSTRAINT_ID));
	}
}
