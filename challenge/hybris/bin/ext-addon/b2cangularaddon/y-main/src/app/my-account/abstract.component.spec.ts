import { AbstractComponent } from './abstract.component';
import { ValidationService }    from '../shared/error-handling/validation.service';
import { UserService }          from '../shared/occ/user.service';

describe('AbstractComponent', () => {
    var abstractComponent: AbstractComponent;
    var userService: UserService;
    var validationService: ValidationService;

    beforeEach(() => {
        userService = new UserService();
        validationService = new ValidationService();
        abstractComponent = new AbstractComponent(userService, validationService);
    });

    it('should clear message', () => {
        abstractComponent.setErrorMessage(new Array("Error!"), null);
        expect(abstractComponent.errorMsgs.length).toBe(1);
        
        abstractComponent.setSuccessMessage(new Array("Success!"));
        expect(abstractComponent.successMsgs.length).toBe(1);
        
        abstractComponent.clearMessage('conf');
        abstractComponent.clearMessage('error');
        expect(abstractComponent.errorMsgs).toBe(null);
        expect(abstractComponent.successMsgs).toBe(null);
    });
});
