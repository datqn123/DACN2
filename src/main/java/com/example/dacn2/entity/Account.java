package com.example.dacn2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore; // Import cái này
import jakarta.persistence.*;
import lombok.*; // Dùng Getter/Setter thay vì Data để an toàn hơn cho Set
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 20, columnDefinition = "varchar(20) default 'ACTIVE'")
    private String status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Quan hệ 1-1 với UserProfile
    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore // Chặn vòng lặp vô tận khi in JSON
    private UserProfile userProfile;

    // Quan hệ N-N với Role (Hệ thống mới)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "accounts_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}