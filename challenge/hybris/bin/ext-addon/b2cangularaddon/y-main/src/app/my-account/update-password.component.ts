import { Component }            from '@angular/core';
import { AbstractComponent }    from './abstract.component';
import { UserService }          from '../shared/occ/user.service';
import { ValidationService }    from '../shared/error-handling/validation.service';

@Component({
    providers: [UserService, ValidationService],
    templateUrl: './update-password.component.html'
})
export class UpdatePasswordComponent extends AbstractComponent
{
    private static UPDATE_SUCCESS_MSGS:string[] = ['Your password has been changed'];
    private static PASSWORD_CONFIRMATION_DOES_NOT_MATCH_MSG = 'Password and password confirmation do not match';

    public currentPassword:string;
    public newPassword:string;
    public confirmedNewPassword:string;
    
    constructor(protected userService:UserService, protected validationService:ValidationService) {
        super(userService, validationService);
    }

    public onSubmit():void
    {
        this.validateForm();

        if (this._errorMsgs.length == 0)
        {
            this.userService.changePassword(this.currentPassword, this.newPassword)
                .then(() =>
                {
                    super.setSuccessMessage(UpdatePasswordComponent.UPDATE_SUCCESS_MSGS);
                    this.clearFormFields();
                },
                    errors =>
                {
                    super.setErrorMessage(UpdatePasswordComponent.VALIDATION_ERROR_MSGS, errors);
                    if (super.getValidationError('PasswordMismatchError'))
                    {
                        super.pushError('currentPassword', UpdatePasswordComponent.PASSWORD_MISMATCH_MSG);
                    }
                });
        }
    }

    public validateForm():void
    {
        super.clearFormMessages();

        super.validateFieldEmpty('currentPassword', this.currentPassword);
        super.validateFieldEmpty('password', this.newPassword);
        super.validateFieldEmpty('confirmedNewPassword', this.confirmedNewPassword);
        super.validateFieldEqual('confirmedNewPassword', this.newPassword, this.confirmedNewPassword, UpdatePasswordComponent.PASSWORD_CONFIRMATION_DOES_NOT_MATCH_MSG);
    }

    private clearFormFields():void
    {
        this.currentPassword = null;
        this.newPassword = null;
        this.confirmedNewPassword = null;
    }
}