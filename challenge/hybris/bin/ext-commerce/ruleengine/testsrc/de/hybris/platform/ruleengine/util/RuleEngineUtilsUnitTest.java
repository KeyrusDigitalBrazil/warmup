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
package de.hybris.platform.ruleengine.util;

import static org.fest.assertions.Assertions.assertThat;

import de.hybris.bootstrap.annotations.UnitTest;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;


@UnitTest
public class RuleEngineUtilsUnitTest
{

	@Test
	public void testGetCleanedContent() throws IOException
	{
		final String rule1Content = readFromResource("ruleengine/test/versioning/rule1.drl");
		final String rule2Content = readFromResource("ruleengine/test/versioning/rule2.drl");

		final String rule1CleanedContent = RuleEngineUtils.getCleanedContent(rule1Content, "1d1c86c4-05c0-4fa1-a3b0-35dfaee8129a");
		final String rule2CleanedContent = RuleEngineUtils.getCleanedContent(rule2Content, "1d1c86c4-05c0-4fa1-a3b0-35dfaee8129a");

		assertThat(rule1CleanedContent).isNotEmpty();
		assertThat(rule2CleanedContent).isNotEmpty();
		assertThat(rule1CleanedContent).isEqualTo(rule2CleanedContent);
	}

	private String readFromResource(final String resourceName) throws IOException
	{
		final URL url = Resources.getResource(resourceName);
		return Resources.toString(url, Charsets.UTF_8);
	}

}
