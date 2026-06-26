package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "supermarkets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supermarket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "website_url")
    private String websiteUrl;

    @OneToMany(mappedBy = "supermarket", fetch = FetchType.LAZY)
    private List<Promotion> promotions;
}
