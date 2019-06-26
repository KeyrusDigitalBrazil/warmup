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

'se:smarteditcommons';
export const pageCreationEvictionTag = new EvictionTag({event: "PAGE_CREATED_EVENT"});
'se:smarteditcommons';
export const pageDeletionEvictionTag = new EvictionTag({event: "PAGE_DELETED_EVENT"});
'se:smarteditcommons';
export const pageUpdateEvictionTag = new EvictionTag({event: "PAGE_UPDATED_EVENT"});
'se:smarteditcommons';
export const pageRestoredEvictionTag = new EvictionTag({event: "PAGE_RESTORED_EVENT"});
'se:smarteditcommons';
export const pageChangeEvictionTag = new EvictionTag({event: "PAGE_CHANGE"});
'se:smarteditcommons';
export const pageEvictionTag = new EvictionTag({event: "pageEvictionTag", relatedTags: [pageCreationEvictionTag, pageDeletionEvictionTag, pageUpdateEvictionTag]});
