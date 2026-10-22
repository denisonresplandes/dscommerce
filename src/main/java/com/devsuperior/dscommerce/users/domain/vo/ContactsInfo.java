package com.devsuperior.dscommerce.users.domain.vo;

import com.devsuperior.dscommerce.shared.domain.DomainException;

public record ContactsInfo(
        Email email,
        Phone phone
) {
    public ContactsInfo {
        DomainException.requireNonNull(email, "user email is required");
        DomainException.requireNonNull(phone, "user phone number is required");
    }

    public ContactsInfo withEmail(Email email) {
        return new ContactsInfo(email, this.phone);
    }

    public ContactsInfo withPhone(Phone phone) {
        return new ContactsInfo(this.email, phone);
    }
}
