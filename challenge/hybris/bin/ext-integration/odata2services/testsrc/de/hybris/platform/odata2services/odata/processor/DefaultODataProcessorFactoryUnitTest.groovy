/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.odata.processor

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.config.ODataServicesConfiguration
import de.hybris.platform.odata2services.odata.persistence.ItemLookupRequestFactory
import de.hybris.platform.odata2services.odata.persistence.PersistenceService
import de.hybris.platform.odata2services.odata.persistence.StorageRequestFactory
import de.hybris.platform.odata2services.odata.processor.reader.EntityReaderRegistry
import de.hybris.platform.servicelayer.model.ModelService
import org.apache.olingo.odata2.api.processor.ODataContext
import org.junit.Test
import spock.lang.Specification

@UnitTest
class DefaultODataProcessorFactoryUnitTest extends Specification {
    private final def context = Stub(ODataContext)
    def factory = new DefaultODataProcessorFactory()

    @Test
    def "creates new processor instance for every invocation"() {
        when:
        def processor1 = factory.createProcessor context
        def processor2 = factory.createProcessor context

        then:
        ! processor1.is(processor2)
    }

    @Test
    def "injects PersistenceService into the processor"() {
        setup:
        def service = Stub(PersistenceService)
        factory.persistenceService = service

        when:
        def processor = factory.createProcessor()

        then:
        processor.persistenceService == service
    }

    @Test
    def "injects ModelService into the processor"() {
        setup:
        def service = Stub(ModelService)
        factory.modelService = service

        when:
        def processor = factory.createProcessor()

        then:
        processor.modelService == service

    }

    @Test
    def "injects ODataServicesConfiguration into the processor"() {
        setup:
        def configuration = Stub(ODataServicesConfiguration)
        factory.ODataServicesConfiguration = configuration

        when:
        def processor = factory.createProcessor()

        then:
        processor.oDataServicesConfiguration == configuration
    }

    @Test
    def "injects EntityReaderRegistry into the processor"() {
        setup:
        def registry = Stub(EntityReaderRegistry)
        factory.entityReaderRegistry = registry

        when:
        def processor = factory.createProcessor()

        then:
        processor.entityReaderRegistry == registry
    }

    @Test
    def "injects ItemLookupRequestFactory into the processor"() {
        setup:
        def requestFactory = Stub(ItemLookupRequestFactory)
        factory.itemLookupRequestFactory = requestFactory

        when:
        def processor = this.factory.createProcessor()

        then:
        processor.itemLookupRequestFactory == requestFactory
    }

    @Test
    def "injects StorageRequestFactory into the processor"() {
        setup:
        def requestFactory = Stub(StorageRequestFactory)
        factory.storageRequestFactory = requestFactory

        when:
        def processor = this.factory.createProcessor()

        then:
        processor.storageRequestFactory == requestFactory
    }

    @Test
    def "passes ODataContext into the processor"() {
        when:
        def processor = factory.createProcessor(context)

        then:
        processor.context == context
    }
}
