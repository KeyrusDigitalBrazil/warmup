import { Component }            from '@angular/core';
import { Titles }               from '../shared/occ/titles';
import { User }                 from '../shared/occ/user';
import { AbstractComponent }    from './abstract.component';
import { UserService }          from '../shared/occ/user.service';
import { ValidationService }    from '../shared/error-handling/validation.service';

@Component({
    providers: [UserService, ValidationService],
    templateUrl: './update-profile.component.html'
})
export class UpdateProfileComponent extends AbstractComponent
{
    private static UPDATE_SUCCESS_MSGS:string[] = ['Your profile has been updated'];

    public _titles:Titles;
    private _user:User;
    
    constructor(protected userService:UserService, protected validationService:ValidationService) {
        super(userService, validationService);
    }

    public ngOnInit():void
    {
        this.getTitles();
        this.getUser();
    }

    public onSubmit():void
    {
        this.validateForm();
        
        if (this._errorMsgs.length == 0)
        {
            this.userService.update(this.user)
                .then((user) =>
                {
                    this._user = user;
                    super.setSuccessMessage(UpdateProfileComponent.UPDATE_SUCCESS_MSGS);
                },
                    errors =>
                {
                    super.setErrorMessage(UpdateProfileComponent.VALIDATION_ERROR_MSGS, errors);
                }
            );
        }
    }

    public validateForm():void
    {
        super.clearFormMessages();

        super.validateFieldEmpty('firstName', this.user.firstName);
        super.validateFieldEmpty('lastName', this.user.lastName);
    }

    private getTitles():void
    {
        this.userService.getTitles().then(titles => this._titles = titles);
    }

    private getUser():void
    {
        const emailAddress = this.userService.getEmailAddressFromCookie();
        this.userService.getUser(emailAddress)
            .then(
                user => this._user = user);
    }

    public get titles():Titles
    {
        return this._titles;
    }
    
    public set titles(titles:Titles)
    {
        this._titles = titles;
    }

    public get user():User
    {
        return this._user;
    }
    
    public set user(usr:User)
    {
        this._user = usr;
    }
}