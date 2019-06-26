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
package de.hybris.platform.reportcockpit;

import static org.junit.Assert.assertTrue;

import de.hybris.platform.cockpit.reports.constants.JasperReportVJdbcConstants;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import de.hybris.platform.virtualjdbc.constants.VjdbcConstants;

import org.apache.log4j.Logger;
import org.junit.Test;


/**
 * JUnit Tests for the Reportcockpit extension
 */
public class ReportcockpitTest extends HybrisJUnit4TransactionalTest
{
	/** Edit the local|project.properties to change logging behaviour (properties log4j.*). */
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ReportcockpitTest.class.getName());


	@Test
	public void testVJDBCConstants()
	{
		assertTrue(JasperReportVJdbcConstants.VJDBC_DRIVER_CLASS.equals(VjdbcConstants.DB.VJDBC_DRIVER_CLASS));
		assertTrue(JasperReportVJdbcConstants.VJDBC_ID.equals(VjdbcConstants.DB.VJDBC_ID));
	}

}
