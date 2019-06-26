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
package de.hybris.platform.payment.bean.generation;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.payment.dto.BillingInfo;
import de.hybris.platform.payment.dto.CardInfo;

import org.assertj.core.api.Assertions;
import org.junit.Test;


import static org.assertj.core.api.Assertions.assertThat;


@UnitTest
public class DeepCopyTest
{
	public static final String WROCLAW = "Wroclaw";
	public static final String POLAND = "Poland";
	public static final String JAN_KOWALSKI = "Jan Kowalski";
	public static final String MUNICH="Munich";
	public static final String GERMANY ="Germany";
	public static final String OTTO_NORMALVERBRAUCHER = "Otto Normalverbraucher";

	@Test
	public void shouldCreateNewObjectsInsteadOfReusingReferencesOnCopy() throws Exception
	{
		//given
		final CardInfo sourceCardInfo = createCardInfo(WROCLAW, POLAND, JAN_KOWALSKI);
		final CardInfo targetCardInfo = createCardInfo(MUNICH, GERMANY, OTTO_NORMALVERBRAUCHER);
		//when
		targetCardInfo.copy( sourceCardInfo );
		//then
		assertPropertiesCopied(sourceCardInfo, targetCardInfo);
		assertDeepCopy(sourceCardInfo, targetCardInfo);
	}

	protected void assertDeepCopy(final CardInfo sourceCardInfo, final CardInfo targetCardInfo)
	{
		assertThat(sourceCardInfo.getBillingInfo() == targetCardInfo.getBillingInfo()).isFalse().as("Has different reference");
	}

	protected void assertPropertiesCopied(final CardInfo sourceCardInfo, final CardInfo targetCardInfo)
	{
		assertThat(sourceCardInfo.getBillingInfo().getCity()).isEqualTo(targetCardInfo.getBillingInfo().getCity());
		assertThat(sourceCardInfo.getBillingInfo().getCountry()).isEqualTo(targetCardInfo.getBillingInfo().getCountry());
		assertThat(sourceCardInfo.getCardHolderFullName()).isEqualTo(targetCardInfo.getCardHolderFullName());
	}

	protected CardInfo createCardInfo(final String city, final String country, final String cardholderName)
	{
		final BillingInfo sourceBillingInfo = new BillingInfo();
		sourceBillingInfo.setCity(city);
		sourceBillingInfo.setCountry(country);

		final CardInfo sourceCardInfo = new CardInfo();
		sourceCardInfo.setCardHolderFullName(cardholderName);
		sourceCardInfo.setBillingInfo(sourceBillingInfo);
		return sourceCardInfo;
	}
}
