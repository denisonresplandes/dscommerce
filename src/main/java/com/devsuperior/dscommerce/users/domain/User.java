package com.devsuperior.dscommerce.users.domain;

import com.devsuperior.dscommerce.shared.domain.AggregateRoot;
import com.devsuperior.dscommerce.users.domain.vo.UserId;
import com.devsuperior.dscommerce.users.domain.vo.*;

import java.util.Objects;

public class User extends AggregateRoot {

    private final UserId id;
    private final PersonalInfo personalInfo;
    private ContactsInfo contactsInfo;
    private Credentials credentials;

    private User(UserId id, PersonalInfo personalInfo, ContactsInfo contactsInfo,
                 Credentials credentials) {
        this.id = id;
        this.personalInfo = Objects.requireNonNull(personalInfo, "user personal info is required");
        this.contactsInfo = Objects.requireNonNull(contactsInfo, "user contact information is required");
        this.credentials = Objects.requireNonNull(credentials, "user credentials are required");
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
        Objects.requireNonNull(id, "user id is required");
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
