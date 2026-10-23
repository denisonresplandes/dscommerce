package com.devsuperior.dscommerce.users.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * Modelo de persistência do usuário (tabela {@code tb_user}).
 *
 * <p><b>Restrição única em {@code email}:</b> é a garantia FINAL de que não existem dois usuários com o mesmo
 * e-mail. A verificação feita antes de salvar ({@code UserRepository.existsByEmail}) é só uma checagem rápida e
 * amigável; ela pode ser "furada" por duas requisições simultâneas que passam juntas por ela. Só o banco consegue
 * decidir essa disputa com segurança — a segunda gravação é recusada e vira HTTP 409 no
 * {@code GlobalExceptionHandler}. Como efeito colateral, o banco cria um índice, o que acelera a busca por e-mail.
 */
@Entity
@Table(
        name = "tb_user",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email")
)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "password", nullable = false)
    private String passwordHash;

    UserEntity() {}

    public UserEntity(Long id, String name, String email,
                      String phone, LocalDate dateOfBirth, String passwordHash) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserEntity other)) {
            return false;
        }
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
