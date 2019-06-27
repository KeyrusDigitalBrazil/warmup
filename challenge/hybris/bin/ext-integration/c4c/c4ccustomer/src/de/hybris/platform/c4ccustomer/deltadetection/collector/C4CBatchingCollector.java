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
package de.hybris.platform.c4ccustomer.deltadetection.collector;

import com.google.common.collect.Lists;
import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.y2ysync.deltadetection.collector.BatchingCollector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.SerializationUtils;

public class C4CBatchingCollector implements BatchingCollector {
    private final int batchSize;
    private final MediaService mediaService;
    private final ModelService modelService;
    private final List<PK> createdMedias = Lists.newArrayList();
    private final List<ItemChangeDTO> currentBatch = Lists.newArrayList();
    private final String mediaCodePrefix;
    private String collectorId;
    private int batchCounter = 0;
    private Set<String> collectedItems = new HashSet<String>();

    public C4CBatchingCollector(String mediaCodePrefix, int batchSize, ModelService modelService,
            MediaService mediaService) {
        this.mediaCodePrefix = mediaCodePrefix;
        this.batchSize = batchSize;
        this.modelService = modelService;
        this.mediaService = mediaService;
    }

    public boolean collect(ItemChangeDTO change) {
        if(!collectedItems.contains(change.getItemPK().toString())) {
            this.currentBatch.add(change);
            if (this.currentBatch.size() == this.batchSize) {
                this.dumpBatchToMedia();
            }
            collectedItems.add(change.getItemPK().toString());
        }
        return true;
    }

    public void finish() {
        this.dumpBatchToMedia();
    }

    private void dumpBatchToMedia() {
        if (!this.currentBatch.isEmpty()) {
            CatalogUnawareMediaModel media = this.createMediaInDb();
            byte[] serialized = SerializationUtils.serialize(this.currentBatch);
            this.mediaService.setDataForMedia(media, serialized);
            this.createdMedias.add(media.getPk());
            this.currentBatch.clear();
            ++this.batchCounter;
        }
    }

    private CatalogUnawareMediaModel createMediaInDb() {
        CatalogUnawareMediaModel media = (CatalogUnawareMediaModel) this.modelService
                .create(CatalogUnawareMediaModel.class);
        String code = this.mediaCodePrefix + "-" + this.collectorId + "-" + this.batchCounter;
        media.setCode(code);
        media.setMime("application/x-java-serialized-object");
        media.setRealFileName(code);
        this.modelService.save(media);
        return media;
    }

    public List<PK> getPksOfBatches() {
        return this.createdMedias;
    }

    public void setId(String id) {
        this.collectorId = id;
    }

	public Set<String> getCollectedItems() {
		return collectedItems;
	}

	public void setCollectedItems(Set<String> collectedItems) {
		this.collectedItems = collectedItems;
	}
}
