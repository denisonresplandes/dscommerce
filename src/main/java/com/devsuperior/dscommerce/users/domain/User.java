package com.devsuperior.dscommerce.users.domain;

import com.devsuperior.dscommerce.shared.domain.AggregateRoot;
import com.devsuperior.dscommerce.shared.domain.DomainException;
import com.devsuperior.dscommerce.users.domain.vo.*;

public class User extends AggregateRoot {

    private final UserId id;
    private final PersonalInfo personalInfo;
    private ContactsInfo contactsInfo;
    private Credentials credentials;

    private User(UserId id, PersonalInfo personalInfo, ContactsInfo contactsInfo,
                 Credentials credentials) {
        DomainException.requireNonNull(personalInfo, "user personal info cannot be null");
        DomainException.requireNonNull(contactsInfo, "user contact info cannot be null");
        DomainException.requireNonNull(credentials, "user credentials cannot be null");

        this.id = id;
        this.personalInfo = personalInfo;
        this.contactsInfo = contactsInfo;
        this.credentials = credentials;
    }

    public static User create(PersonalInfo personalInfo, ContactsInfo contactsInfo,
                              Credentials credentials) {
        return new User(
                null,
                personalInfo,
                contactsInfo,
                credentials
        );
    }

    public static User restore(UserId id, PersonalInfo personalInfo,
                               ContactsInfo contactsInfo,
                               Credentials credentials) {
        DomainException.requireNonNull(id,"User ID cannot be null.");
        return new User(
                id,
                personalInfo,
                contactsInfo,
                credentials
        );
    }

    public UserId getId() {
        return id;
    }

    public Name getName() {
        return personalInfo.name();
    }

    public Email getEmail() {
        return contactsInfo.email();
    }

    public void changeEmail(Email email) {
        this.contactsInfo = this.contactsInfo.withEmail(email);
    }

    public Phone getPhone() {
        return contactsInfo.phone();
    }

    public void changePhone(Phone phone) {
        this.contactsInfo = this.contactsInfo.withPhone(phone);
    }

    public DateOfBirth getDateOfBirth() {
        return this.personalInfo.dateOfBirth();
    }

    public PasswordHash getPasswordHash() {
        return this.credentials.passwordHash();
    }

    public void changePassword(PasswordHash passwordHash) {
        this.credentials = this.credentials.withPasswordHash(passwordHash);
    }
    // demais metodos..

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
