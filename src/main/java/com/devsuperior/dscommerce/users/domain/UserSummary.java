package com.devsuperior.dscommerce.users.domain;

import com.devsuperior.dscommerce.users.domain.vo.ContactsInfo;
import com.devsuperior.dscommerce.users.domain.vo.PersonalInfo;
import com.devsuperior.dscommerce.users.domain.vo.UserId;

public record UserSummary(
        UserId id,
        PersonalInfo personalInfo,
        ContactsInfo contactsInfo
) {
}
