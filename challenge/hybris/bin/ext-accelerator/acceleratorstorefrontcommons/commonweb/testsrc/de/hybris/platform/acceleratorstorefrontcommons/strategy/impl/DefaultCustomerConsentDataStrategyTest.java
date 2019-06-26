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
package de.hybris.platform.acceleratorstorefrontcommons.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.commercefacades.consent.ConsentFacade;
import de.hybris.platform.commercefacades.consent.data.ConsentData;
import de.hybris.platform.commercefacades.consent.data.ConsentTemplateData;
import de.hybris.platform.servicelayer.session.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@UnitTest
public class DefaultCustomerConsentDataStrategyTest
{
    private static final String WITHDRAWN = "WITHDRAWN";
    private static final String GIVEN = "GIVEN";
    @Mock
    private SessionService sessionService;
    @Mock
    private ConsentFacade consentFacade;
    @InjectMocks
    private DefaultCustomerConsentDataStrategy customerConsentDataStrategy ;

    @Before
    public void setUp() throws IOException
    {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldPopulateCustomerConsentDataInSessionWhenUserLogsIn() throws Exception
    {
        //given
        String template1="templateCode1",template2="templateCode2",template3="templateCode3",template4="templateCode4";
        ConsentData consentData = buildConsentData();
        List<ConsentTemplateData> consentTemplatesData = Arrays.asList(
                ConsentTemplateDataBuilder.aConsentTemplateData()
                        .withId(template1)
                        .withVersion(1)
                        .withExposed(true)
                        .build(),
                ConsentTemplateDataBuilder.aConsentTemplateData()
                        .withId(template2)
                        .withVersion(1)
                        .withExposed(true)
                        .build(),
                ConsentTemplateDataBuilder.aConsentTemplateData()
                        .withId(template3)
                        .withVersion(1)
                        .withConsentData(consentData)
                        .withExposed(true)
                        .build(),
                ConsentTemplateDataBuilder.aConsentTemplateData()
                        .withId(template4)
                        .withVersion(1)
                        .withConsentData(consentData)
                        .withExposed(false)
                        .build()
        );

        given(consentFacade.getConsentTemplatesWithConsents()).willReturn(consentTemplatesData);

        //when
        customerConsentDataStrategy.populateCustomerConsentDataInSession();

        //then
        final ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);
        verify(sessionService).setAttribute(Mockito.eq(WebConstants.USER_CONSENTS),captor.capture());
        Map consentMap  =  captor.getValue();

        assertThat(consentMap.size() == 3);
        assertEquals(WITHDRAWN,consentMap.get(template1));
        assertEquals(WITHDRAWN,consentMap.get(template2));
        assertEquals(GIVEN,consentMap.get(template3));
    }

    private ConsentData buildConsentData()
    {
        ConsentData consentData = new ConsentData();
        consentData.setConsentGivenDate(new Date());
        return consentData;
    }
}
class ConsentTemplateDataBuilder
{
    private String id;

    private String name;

    private String description;

    private int version;

    private boolean exposed;

    private ConsentData consentData;

    private ConsentTemplateDataBuilder()
    {
    }

    public static ConsentTemplateDataBuilder aConsentTemplateData()
    {
        return new ConsentTemplateDataBuilder();
    }

    public ConsentTemplateDataBuilder withId(String id)
    {
        this.id = id;
        return this;
    }

    public ConsentTemplateDataBuilder withName(String name)
    {
        this.name = name;
        return this;
    }

    public ConsentTemplateDataBuilder withDescription(String description)
    {
        this.description = description;
        return this;
    }

    public ConsentTemplateDataBuilder withVersion(int version)
    {
        this.version = version;
        return this;
    }

    public ConsentTemplateDataBuilder withExposed(boolean exposed)
    {
        this.exposed = exposed;
        return this;
    }

    public ConsentTemplateDataBuilder withConsentData(ConsentData consentData)
    {
        this.consentData = consentData;
        return this;
    }

    public ConsentTemplateData build()
    {
        ConsentTemplateData consentTemplateData = new ConsentTemplateData();
        consentTemplateData.setId(id);
        consentTemplateData.setName(name);
        consentTemplateData.setDescription(description);
        consentTemplateData.setVersion(Integer.valueOf(version));
        consentTemplateData.setExposed(exposed);
        consentTemplateData.setConsentData(consentData);
        return consentTemplateData;
    }
}
