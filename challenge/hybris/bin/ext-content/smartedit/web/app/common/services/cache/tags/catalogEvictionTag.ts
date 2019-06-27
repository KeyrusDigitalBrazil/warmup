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
import {EvictionTag} from '../EvictionTag';
import {userEvictionTag} from './userEvictionTag';

'se:smarteditcommons';
export const catalogSyncedEvictionTag = new EvictionTag({event: "CATALOG_SYNCHRONIZED_EVENT"});
'se:smarteditcommons';
export const catalogEvictionTag = new EvictionTag({event: "CATALOG_EVENT", relatedTags: [catalogSyncedEvictionTag, userEvictionTag]});
