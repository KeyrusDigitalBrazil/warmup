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
package de.hybris.platform.promotionengineservices.promotionengine.impl;

import static java.nio.charset.Charset.forName;
import static java.nio.file.Files.newInputStream;
import static java.nio.file.Paths.get;

import de.hybris.platform.promotionengineservices.promotionengine.PromotionEngineService;
import de.hybris.platform.ruleengine.dao.DroolsKIEBaseDao;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;


/**
 * Base class for promotionengineservices tests
 */
public class PromotionEngineServiceBaseTest extends ServicelayerTest // NOSONAR
{

	@Resource
	private PromotionEngineService promotionEngineService;

	@Resource
	private DroolsKIEBaseDao droolsKIEBaseDao;

	protected PromotionEngineService getPromotionEngineService()
	{
		return promotionEngineService;
	}

	protected String readRuleFile(final String fileName, final String path) throws IOException
	{
		final Path rulePath = get(getApplicationContext().getResource("classpath:" + path + fileName).getURI());
		final InputStream is = newInputStream(rulePath);
		final StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, forName("UTF-8"));
		return writer.toString();
	}

	protected DroolsKIEBaseModel getKieBaseModel(final String kieBaseName)
	{
		return getDroolsKIEBaseDao().findAllKIEBases().stream().filter(b -> b.getName().equals(kieBaseName)).findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"KieBaseModel with name " + kieBaseName + " was not found. Check your test setup"));
	}

	protected DroolsKIEBaseDao getDroolsKIEBaseDao()
	{
		return droolsKIEBaseDao;
	}
}
