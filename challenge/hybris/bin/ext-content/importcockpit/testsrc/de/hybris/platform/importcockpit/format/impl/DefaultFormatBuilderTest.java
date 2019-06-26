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
package de.hybris.platform.importcockpit.format.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;


public class DefaultFormatBuilderTest
{

	public static final String QA_SECURITY_XEE_FORMATS_XML = "/qa/security/XEE_formats.xml";

	@Test
	public void xxeAttackShouldReturnEmptyResults()
	{
		final PathAwareDefaultFormatBuilder pathAwareDefaultFormatBuilder = new PathAwareDefaultFormatBuilder(
				QA_SECURITY_XEE_FORMATS_XML);
		assertThat(Files.exists(Paths.get(pathAwareDefaultFormatBuilder.getFormatsLocation().getFile()))).isTrue();
		assertThat(pathAwareDefaultFormatBuilder.getDateFormats()).isEmpty();
		assertThat(pathAwareDefaultFormatBuilder.getNumberFormats()).isEmpty();
	}

	public class PathAwareDefaultFormatBuilder extends DefaultFormatBuilder
	{
		private final String url;

		public PathAwareDefaultFormatBuilder(final String url)
		{
			this.url = url;
		}

		@Override
		protected URL getFormatsLocation()
		{
			final URL resource = PathAwareDefaultFormatBuilder.class.getResource(url);
			assertThat(resource).isNotNull();
			return resource;
		}
	}

}
