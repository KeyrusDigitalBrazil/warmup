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
import {functionsUtils} from 'smarteditcommons/utils/FunctionsUtils';

import {SeConstructor, SeFactory, SeValueProvider} from './types';
import {annotationService} from '../annotationService';

/** @internal */
export class DINameUtils {

	buildComponentName(componentConstructor: SeConstructor): string {
		return this.buildName(componentConstructor).replace(/Component$/, '').replace(/Directive$/, '');
	}

	buildServiceName(serviceConstructor: SeConstructor | SeFactory): string {
		return this.buildName(serviceConstructor);
	}

	// builds the DI recipe name for a given construtor
	buildName(constructor: SeConstructor | SeFactory): string {
		const originalConstructor = annotationService.getOriginalConstructor(constructor);
		const originalName = functionsUtils.getConstructorName(originalConstructor);
		return this.convertNameCasing(originalName);
	}

	// converts the first character to lower case
	convertNameCasing(originalName: string): string {
		const builtName = originalName.substring(0, 1).toLowerCase() + originalName.substring(1);
		return builtName;
	}

	/*
	 * This method will generate a SeValueProvider from a shortHand map built off a variable:
	 * if a variable x (or DEFAULT_x) equals 5, then the method will return 
	 * { provide : 'x', useValue: 5} when it is passed {x}
	 */
	/* forbiddenNameSpaces useValue:false */
	makeValueProvider(variableShortHand: {[index: string]: any}): SeValueProvider {
		const fullKey = Object.keys(variableShortHand)[0];
		const key = fullKey.replace(/^DEFAULT_/, "");
		return {
			provide: key,
			useValue: variableShortHand[fullKey]
		};
	}

}

'se:smarteditcommons';
export const diNameUtils = new DINameUtils();

