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
 * @description
 * Interface to describe a CMS Item field. 
 */
export interface CMSItemStructureField {
	cmsStructureType: string;
	i18nKey: string;
	qualifier: string;
	editable: boolean;
	collection?: boolean;
	localized?: boolean;
	paged?: boolean;
	required?: boolean;
}

/**
 * @description
 * Interface to describe the structure of a CMS Item. 
 */
export interface CMSItemStructure {
	attributes: CMSItemStructureField[];
	category: string;
	code: string;
	i18nKey: string;
	name: string;
	type: string;
}
