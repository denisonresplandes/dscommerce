package com.devsuperior.dscommerce.users.domain.vo;

import java.util.Objects;

public record ContactsInfo(
        Email email,
        Phone phone
) {
    public ContactsInfo {
        Objects.requireNonNull(email, "user email is required");
        Objects.requireNonNull(phone, "user phone number is required");
    }

    public ContactsInfo withEmail(Email email) {
        return new ContactsInfo(email, this.phone);
    }

    public ContactsInfo withPhone(Phone phone) {
        return new ContactsInfo(this.email, phone);
    }
}
