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
 * We are doing forced imports in order to generate the types (d.ts) of below interfaces or classes correctly.
 * If we don't include the below imports, as a part of webpack tree shaking, the types will not be generated.
 * There is an open issue in typescript github regarding forced imports
 * https://github.com/Microsoft/TypeScript/issues/9191
 * https://github.com/Microsoft/TypeScript/wiki/FAQ#why-are-imports-being-elided-in-my-emit
 * 
 * If an interface X extends an interface Y, make sure X has all types it needs from Y by checking index.d.ts, if not, do force import of X and Y.
 */
import './Cloneable';
import './ICatalog';
import './IHomepage';
import './IPermissionsDto';
import './Page';
import './Pageable';
import './Pagination';
import './Payload';
import './Primitive';
import './TypedMap';
import './ValidationError';
