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
package de.hybris.platform.droolsruleengineservices.agendafilter;

import java.util.Map;
import java.util.Optional;

import org.drools.core.common.InternalFactHandle;
import org.junit.Assert;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.Match;



// used for CustomAgendaFilterSupportIT test
public class CustomAgendaFilter implements AgendaFilter
{
	@Override
	public boolean accept(final Match match)
	{
		final Rule rule = match.getRule();
		if (!"customAgendaFilterTest".equals(rule.getName()))
		{
			Assert.fail("expected rule to fire should be customAgendaFilterTest!");
		}

		final Optional<Map> mapFactOptional = match.getFactHandles().stream()
				.filter(fact -> fact instanceof InternalFactHandle).map(fact -> ((InternalFactHandle) fact).getObject())
				.filter(fact -> fact instanceof Map).map(Map.class::cast).findFirst();
		if (mapFactOptional.isPresent())
		{
			final Map<String, String> mapFact = mapFactOptional.get();
			mapFact.put("addedByAgendaFilter", "some other value");
		}
		else
		{
			Assert.fail("expected a Map<String,String>");
		}
		return true;
	}
}
