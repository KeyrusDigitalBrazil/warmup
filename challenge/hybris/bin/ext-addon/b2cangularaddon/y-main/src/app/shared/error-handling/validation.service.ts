import { Injectable }   from '@angular/core';

import { Error }        from './error';

@Injectable()
export class ValidationService {
  hasValidationErrors(validationErrors: Error[]): boolean {
    return validationErrors && validationErrors.length > 0;
  }

  getValidationError(fieldName: string, validationErrors: Error[]): Error {
    let validationError: Error;

    if (validationErrors && validationErrors.length > 0) {
      for (let i = 0; i < validationErrors.length; i++) {
        let validationError = validationErrors[i];

        if (validationError.subject == fieldName || validationError.type == fieldName) {
          return validationError;
        }
      }
    }

    return null;
  }
}
