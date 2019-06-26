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
import {SeModule} from 'smarteditcommons';
import {RestrictionsListComponent} from './restrictionsList/RestrictionsListComponent';
import {DuplicatePrimaryNonContentPageComponent} from './pageRestore/duplicatePrimaryNonContentPage/DuplicatePrimaryNonContentPageComponent';
import {DuplicatePrimaryContentPageLabelComponent} from './pageRestore/duplicatePrimaryContentPageLabel/DuplicatePrimaryContentPageLabelComponent';
import {MissingPrimaryContentPageComponent} from './pageRestore/missingPrimaryContentPage/MissingPrimaryContentPageComponent';

/**
 * @ngdoc overview
 * @name genericEditorWidgetsModule
 *
 * @description
 * Module containing all the generic editor widgets. 
 */
@SeModule({
	imports: [],
	declarations: [
		RestrictionsListComponent,
		DuplicatePrimaryNonContentPageComponent,
		DuplicatePrimaryContentPageLabelComponent,
		MissingPrimaryContentPageComponent
	]
})
export class GenericEditorWidgetsModule {}