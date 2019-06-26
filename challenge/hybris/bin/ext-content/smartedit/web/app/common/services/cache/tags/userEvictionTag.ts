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
// TODO : merge the EVENT strings and the tag ones
export const userEvictionTag = new EvictionTag({event: "USER_HAS_CHANGED"});