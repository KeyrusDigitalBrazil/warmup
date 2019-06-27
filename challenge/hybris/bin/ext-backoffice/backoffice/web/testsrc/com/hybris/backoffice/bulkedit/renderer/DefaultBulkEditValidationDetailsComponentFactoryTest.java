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
package com.hybris.backoffice.bulkedit.renderer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.zkoss.zhtml.Li;
import org.zkoss.zul.Label;

import com.hybris.cockpitng.validation.impl.DefaultValidationInfo;
import com.hybris.cockpitng.validation.model.ValidationInfo;


@RunWith(MockitoJUnitRunner.class)
public class DefaultBulkEditValidationDetailsComponentFactoryTest
{

	@Spy
	private DefaultBulkEditValidationDetailsComponentFactory factory;

	@Test
	public void shouldLiBeCreatedWithoutHeaderLabel()
	{
		// given
		final String myErrorMessage = "My error message";
		final ValidationInfo validationMessage = new DefaultValidationInfo();
		((DefaultValidationInfo) validationMessage).setValidationMessage(myErrorMessage);

		// when
		final Li li = factory.createValidationDetails(validationMessage);

		// then
		assertThat(((Label) li.getChildren().get(0)).getValue()).isEqualTo(myErrorMessage);
	}

}
