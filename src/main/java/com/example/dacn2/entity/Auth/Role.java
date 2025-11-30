package com.example.dacn2.entity.Auth;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Table(name = "roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // VD: ADMIN

    private String description;

    // Quan hệ Role - Permission (Nhiều - Nhiều)
    @ManyToMany(fetch = FetchType.EAGER) // EAGER để khi load Role là lấy luôn quyền
    @JoinTable(
            name = "roles_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions;
}