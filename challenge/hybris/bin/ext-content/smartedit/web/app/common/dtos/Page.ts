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
import {Pagination} from 'smarteditcommons/dtos';

/**
 * @ngdoc object
 * @name Page.object:Page
 * @description
 * An object representing the backend response to a paged query
 */
export interface Page<T> {
    /**
     * @ngdoc object
     * @name Page.object:Pagination
     * @description
     * An object representing the returned pagination information from backend
     */
	pagination: Pagination;
    /**
     * @ngdoc property
     * @name results
     * @propertyOf Page.object:Page
     * @description
     * The array containing the elements pertaining to the requested page, its size will not exceed the requested page size.
     */
	results: T[];
	[index: string]: T[] | Pagination;
}