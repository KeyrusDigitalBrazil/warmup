/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.cmsfacades.synchronization.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.SyncItemJobModel;
import de.hybris.platform.cmsfacades.data.SyncJobData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SyncItemJobToSyncJobDataPopulatorTest
{

    @InjectMocks
    private SyncItemJobToSyncJobDataPopulator populator;

    @Test
    public void populatorShouldSetCatalogVersionsIfSourceIsProvided()
    {
        // Arrange
        String sourceVersion = "someSourceVersion";
        CatalogVersionModel sourceCatalogVersion = new CatalogVersionModel();
        sourceCatalogVersion.setVersion(sourceVersion);

        String targetVersion = "someTargetVersion";
        CatalogVersionModel targetCatalogVersion = new CatalogVersionModel();
        targetCatalogVersion.setVersion( targetVersion );

        SyncItemJobModel syncItemJobModel = new SyncItemJobModel();
        syncItemJobModel.setSourceVersion(sourceCatalogVersion);
        syncItemJobModel.setTargetVersion(targetCatalogVersion);

        Optional<SyncItemJobModel> source = Optional.of( syncItemJobModel );
        SyncJobData target = new SyncJobData();

        // Act
        populator.populate(source, target);

        // Assert
        assertEquals( target.getSourceCatalogVersion(), sourceVersion );
        assertEquals( target.getTargetCatalogVersion(), targetVersion );
    }

    @Test
    public void populatorShouldNotFailIfSourceIsEmpty()
    {
        // Arrange
        Optional<SyncItemJobModel> source = Optional.empty();
        SyncJobData target = new SyncJobData();

        // Act
        populator.populate( source, target );

        // Assert
        assertNull( target.getSourceCatalogVersion() );
        assertNull( target.getTargetCatalogVersion() );
    }

}
