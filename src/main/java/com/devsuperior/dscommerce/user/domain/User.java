package com.devsuperior.dscommerce.user.domain;

import com.devsuperior.dscommerce.user.domain.vo.UserId;
import com.devsuperior.dscommerce.user.domain.vo.*;

import java.util.Objects;

public class User {

    private final UserId userId;
    private final Name name;
    private Email email;
    private Phone phone;
    private final DateOfBirth dateOfBirth;
    private PasswordHash passwordHash;

    private User(UserId userId, Name name, Email email,
                 Phone phone, DateOfBirth dateOfBirth, PasswordHash passwordHash) {
        this.userId = userId;
        this.name = Objects.requireNonNull(name, "name is required");
        this.email = Objects.requireNonNull(email, "email is required");
        this.phone = Objects.requireNonNull(phone, "phone number is required");
        this.dateOfBirth = Objects.requireNonNull(dateOfBirth, "date of birth is required");
        this.passwordHash = Objects.requireNonNull(passwordHash, "password hash is required");
    }

    public static User create(Name name, Email email, Phone phone,
                              DateOfBirth dateOfBirth, PasswordHash passwordHash) {
        return new User(
                null,
                name,
                email,
                phone,
                dateOfBirth,
                passwordHash
        );
    }

    public static User restore(UserId userId, Name name, Email email, Phone phone,
                               DateOfBirth dateOfBirth, PasswordHash passwordHash) {
        Objects.requireNonNull(userId, "user id is required");
        return new User(
                userId,
                name,
                email,
                phone,
                dateOfBirth,
                passwordHash
        );
    }

    public UserId getId() {
        return userId;
    }

    public Name getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public void changeEmail(Email email) {
        this.email = Objects.requireNonNull(
                email,
                "email is required"
        );
    }

    public Phone getPhone() {
        return phone;
    }

    public void changePhone(Phone phone) {
        this.phone = Objects.requireNonNull(
                phone,
                "phone number is required"
        );
    }

    public DateOfBirth getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPasswordHash() {
        return passwordHash.value();
    }

    public void changePassword(PasswordHash passwordHash) {
        this.passwordHash = Objects.requireNonNull(
                passwordHash,
                "password hash is required"
        );
    }
    // demais metodos..

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User that)) {
            return false;
        }
        return userId != null && userId.equals(that.userId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
