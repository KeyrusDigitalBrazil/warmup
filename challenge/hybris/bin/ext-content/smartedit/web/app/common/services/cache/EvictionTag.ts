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

/**
 * @ngdoc object
 * @name smarteditServicesModule.object:EvictionTag
 * @description
 * A {@link smarteditServicesModule.object:@Cached @Cached} annotation is tagged with 0 to n EvictionTag, each EvictionTag possibly referencing other evictionTags.
 * <br/>An EvictionTag enables a method cache to be evicted 2 different ways:
 * <ul>
 * <li> An event with the same name as the tag is raised.</li>
 * <li> {@link smarteditServicesModule.service:CacheService#methods_evict evict} method of {@link smarteditServicesModule.service:CacheService cacheService} is invoked with the tag.</li>
 * </ul>
 */
export class EvictionTag {

    /**
     * @ngdoc property
     * @name name
     * @propertyOf smarteditServicesModule.object:EvictionTag
     * @description
     * event upon which the cache should be cleared.
     */
	public event: string;

    /**
     * @ngdoc property
     * @name relatedTags
     * @propertyOf smarteditServicesModule.object:EvictionTag
     * @description
     * other EvictionTag instances grouped under this evictionTag.
     */
	public relatedTags?: EvictionTag[];

	constructor(args: {event: string, relatedTags?: EvictionTag[]}) {
		this.event = args.event;
		this.relatedTags = args.relatedTags;
	}
}