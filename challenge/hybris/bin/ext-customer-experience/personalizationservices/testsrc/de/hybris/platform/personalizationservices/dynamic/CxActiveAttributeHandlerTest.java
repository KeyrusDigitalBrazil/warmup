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
package de.hybris.platform.personalizationservices.dynamic;

import static de.hybris.platform.personalizationservices.enums.CxItemStatus.DELETED;
import static de.hybris.platform.personalizationservices.enums.CxItemStatus.DISABLED;
import static de.hybris.platform.personalizationservices.enums.CxItemStatus.ENABLED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import de.hybris.platform.personalizationservices.enums.CxItemStatus;
import de.hybris.platform.personalizationservices.model.CxCustomizationModel;
import de.hybris.platform.personalizationservices.model.CxVariationModel;
import de.hybris.platform.personalizationservices.stub.CxCustomizationModelStub;
import de.hybris.platform.personalizationservices.stub.MockTimeService;
import de.hybris.platform.servicelayer.time.TimeService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


@RunWith(Parameterized.class)
public class CxActiveAttributeHandlerTest
{
	private final CxVariationActiveAttributeHandler variationHandler;
	private final CxCustomizationActiveAttributeHandler customizationHandler;

	private final CxVariationModel variation;
	private final CxCustomizationModel customization;
	private final Boolean expectedVariation;
	private final Boolean expectedCustomization;

	//{0} - start date
	//{1} - end date
	//{2} - current date
	//{3} - customization state
	//{4} - variation state
	//{5} - expected customization
	//{6} - expected variation
	@Parameters(name = "{index}: (Enabled: {3} / {4}, Start: {0}, End: {1}, Current: {2}) => Expected:{5} / {6}")
	public static Collection<Object[]> getParameters()
	{
		//@formatter:off
		return Arrays.asList(new Object[][] {

			//variation enabled
			//start < end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, ENABLED, TRUE , TRUE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), ENABLED, ENABLED, TRUE , TRUE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), ENABLED, ENABLED, FALSE, FALSE },
			//start == end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, ENABLED, FALSE, FALSE },
			//start > end
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), ENABLED, ENABLED, FALSE, FALSE },
			//no start
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(1900, 1, 1, 10, 00, 00), ENABLED, ENABLED, TRUE , TRUE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), ENABLED, ENABLED, TRUE , TRUE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), ENABLED, ENABLED, FALSE, FALSE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), ENABLED, ENABLED, FALSE, FALSE },
			//no end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, ENABLED, TRUE , TRUE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2100, 1, 1, 10, 00, 00), ENABLED, ENABLED, TRUE , TRUE },
			//no date
			{null,												null,												  LocalDateTime.of(1700, 1, 1, 10, 00, 00), ENABLED, ENABLED, TRUE , TRUE },
			{null,												null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, ENABLED, TRUE , TRUE },
			{null,												null,												  LocalDateTime.of(2500, 1, 1, 10, 00, 00), ENABLED, ENABLED, TRUE , TRUE },

			//variation disabled
			//start < end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, DISABLED, TRUE , FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), ENABLED, DISABLED, TRUE , FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), ENABLED, DISABLED, FALSE, FALSE },
			//start == end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, DISABLED, FALSE, FALSE },
			//start > end
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), ENABLED, DISABLED, FALSE, FALSE },
			//no start
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(1900, 1, 1, 10, 00, 00), ENABLED, DISABLED, TRUE , FALSE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), ENABLED, DISABLED, TRUE , FALSE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), ENABLED, DISABLED, FALSE, FALSE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), ENABLED, DISABLED, FALSE, FALSE },
			//no end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, DISABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, DISABLED, TRUE , FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2100, 1, 1, 10, 00, 00), ENABLED, DISABLED, TRUE , FALSE },
			//no date
			{null,												null,												  LocalDateTime.of(1700, 1, 1, 10, 00, 00), ENABLED, DISABLED, TRUE , FALSE },
			{null,												null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, DISABLED, TRUE , FALSE },
			{null,												null,												  LocalDateTime.of(2500, 1, 1, 10, 00, 00), ENABLED, DISABLED, TRUE , FALSE },

			//variation deleted
			//start < end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, DELETED, TRUE , FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), ENABLED, DELETED, TRUE , FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), ENABLED, DELETED, FALSE, FALSE },
			//start == end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, DELETED, FALSE, FALSE },
			//start > end
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), ENABLED, DELETED, FALSE, FALSE },
			//no start
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(1900, 1, 1, 10, 00, 00), ENABLED, DELETED, TRUE , FALSE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), ENABLED, DELETED, TRUE , FALSE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), ENABLED, DELETED, FALSE, FALSE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), ENABLED, DELETED, FALSE, FALSE },
			//no end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1,  9, 59, 59), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, DELETED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 01), ENABLED, DELETED, TRUE , FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2100, 1, 1, 10, 00, 00), ENABLED, DELETED, TRUE , FALSE },
			//no date
			{null,												null,												  LocalDateTime.of(1700, 1, 1, 10, 00, 00), ENABLED, DELETED, TRUE , FALSE },
			{null,												null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 00), ENABLED, DELETED, TRUE , FALSE },
			{null,												null,												  LocalDateTime.of(2500, 1, 1, 10, 00, 00), ENABLED, DELETED, TRUE , FALSE },

			//customization disabled
			//start < end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), DISABLED, ENABLED, FALSE, FALSE },
			//start == end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), DISABLED, ENABLED, FALSE, FALSE },
			//start > end
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1,  9, 59, 59), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 01), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2000, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), DISABLED, ENABLED, FALSE, FALSE },
			//no start
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(1900, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1,  9, 59, 59), DISABLED, ENABLED, FALSE, FALSE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
			{null,												LocalDateTime.of(2001, 1, 1, 10, 00, 00),LocalDateTime.of(2001, 1, 1, 10, 00, 01), DISABLED, ENABLED, FALSE, FALSE },
			//no end
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1,  9, 59, 59), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 01), DISABLED, ENABLED, FALSE, FALSE },
			{LocalDateTime.of(2000, 1, 1, 10, 00, 00),null,												  LocalDateTime.of(2100, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
			//no date
			{null,												null,												  LocalDateTime.of(1700, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
			{null,												null,												  LocalDateTime.of(2000, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
			{null,												null,												  LocalDateTime.of(2500, 1, 1, 10, 00, 00), DISABLED, ENABLED, FALSE, FALSE },
		});
		//@formatter:on
	}

	public CxActiveAttributeHandlerTest(final LocalDateTime start, final LocalDateTime end, final LocalDateTime current,
			final CxItemStatus customizationStatus, final CxItemStatus variationStatus, final Boolean expectedCustomization,
			final Boolean expectedVariation)
	{

		final TimeService timeService = new MockTimeService();
		timeService.setCurrentTime(getDateFromDateTime(current));


		variationHandler = new CxVariationActiveAttributeHandler();
		customizationHandler = new CxCustomizationActiveAttributeHandler();
		customizationHandler.setTimeService(timeService);

		customization = new CxCustomizationModelStub(timeService);
		customization.setEnabledStartDate(getDateFromDateTime(start));
		customization.setEnabledEndDate(getDateFromDateTime(end));
		customization.setStatus(customizationStatus);

		variation = new CxVariationModel();
		variation.setCustomization(customization);
		variation.setStatus(variationStatus);

		this.expectedCustomization = expectedCustomization;
		this.expectedVariation = expectedVariation;
	}

	@Test
	public void validateCustomization()
	{
		//when
		final Boolean active = customizationHandler.get(customization);

		//then
		Assert.assertEquals(expectedCustomization, active);
	}

	@Test
	public void validateVariation()
	{
		//when
		final Boolean active = variationHandler.get(variation);

		//then
		Assert.assertEquals(expectedVariation, active);
	}




	protected Date getDateFromDateTime(final LocalDateTime time)
	{
		if (time == null)
		{
			return null;
		}
		else
		{
			return Date.from(time.toInstant(ZoneOffset.UTC));
		}
	}
}
