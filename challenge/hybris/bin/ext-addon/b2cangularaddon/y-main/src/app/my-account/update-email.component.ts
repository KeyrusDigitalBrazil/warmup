import { Component, OnInit, Input }     from '@angular/core';
import { AbstractComponent }            from './abstract.component';
import { UserService }          from '../shared/occ/user.service';
import { ValidationService }    from '../shared/error-handling/validation.service';

@Component({
    providers: [UserService, ValidationService],
    templateUrl: './update-email.component.html'
})
export class UpdateEmailComponent extends AbstractComponent
{
    private static UPDATE_SUCCESS_MSGS:string[] = ['Your email has been changed. To continue, please re-login using your new email'];
    private static EMAIL_CONFIRMATION_DOES_NOT_MATCH_MSG = 'Email and email confirmation do not match';

    public emailAddress:string;
    public checkEmailAddress:string;
    public password:string;
    
    constructor(protected userService:UserService, protected validationService:ValidationService) {
        super(userService, validationService);
    }

    public ngOnInit():void
    {
        this.emailAddress = this.userService.getEmailAddressFromCookie();
    }

    public onSubmit():void
    {
        this.validateForm();

        if (this._errorMsgs.length == 0)
        {
            this.userService.changeLogin(this.emailAddress, this.password)
                .then(() =>
                {
                    super.setSuccessMessage(UpdateEmailComponent.UPDATE_SUCCESS_MSGS);
                    this.clearFormFields();
                },
                    errors =>
                {
                    super.setErrorMessage(UpdateEmailComponent.VALIDATION_ERROR_MSGS, errors);
                    if (this.getValidationError('PasswordMismatchError'))
                    {
                        super.pushError('password', UpdateEmailComponent.PASSWORD_MISMATCH_MSG);
                    }
                });
        }
    }

    public validateForm():void
    {
        super.clearFormMessages();

        super.validateFieldEmpty('newLogin', this.emailAddress);
        super.validateFieldEmpty('checkEmailAddress', this.checkEmailAddress);
        super.validateFieldEmpty('password', this.password);
        super.validateFieldEqual('checkEmailAddress', this.checkEmailAddress, this.emailAddress, UpdateEmailComponent.EMAIL_CONFIRMATION_DOES_NOT_MATCH_MSG);
    }

    private clearFormFields():void
    {
        this.checkEmailAddress = null;
        this.password = null;
    }
}