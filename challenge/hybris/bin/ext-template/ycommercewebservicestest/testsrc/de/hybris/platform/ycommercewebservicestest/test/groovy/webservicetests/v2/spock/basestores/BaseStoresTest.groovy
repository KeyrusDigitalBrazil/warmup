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
/**
 *
 */
package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.basestores

import de.hybris.bootstrap.annotations.ManualTest
import de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests.v2.spock.AbstractSpockFlowTest
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Unroll

import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.XML
import static org.apache.http.HttpStatus.SC_OK

@ManualTest
@Unroll
/**
 * @author jerome.journot
 *
 */
class BaseStoresTest extends AbstractSpockFlowTest {


    def "Client retrieves a base store by its uid: #format"() {

        when:
        HttpResponseDecorator response = restClient.get(path: getBasePathWithSite() + '/basestores/wsIntegrationTest', contentType: format)

        then:
        with(response) {
            if(isNotEmpty(data)&&isNotEmpty(data.errors))println(data)
            status == SC_OK
            isNotEmpty(data.currencies)
            data.currencies.size() > 0
        }

        where:
        format << [JSON, XML]
    }
}
