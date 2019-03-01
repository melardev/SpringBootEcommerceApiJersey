package com.melardev.spring.shoppingcartweb.models;

import com.melardev.spring.shoppingcartweb.services.StringHelper;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "categories")
public class Category extends TimestampedEntity {

    @Column(nullable = false)
    protected String name;

    @Column(nullable = false, unique = true)
    protected String slug;

    private String description;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<CategoryImage> images = new ArrayList<>();

    public Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Category() {

    }

    public Category(String name) {
        this.setName(name);
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @PrePersist
    public void preCreate() {
        slugifyIfEmptySlug();
    }

    @PreUpdate
    public void preUpdateEntity() {
        slugifyIfEmptySlug();
    }

    private void slugifyIfEmptySlug() {
        if (StringHelper.isEmpty(getSlug()))
            setSlug(StringHelper.slugify(getName()));
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<CategoryImage> getImages() {
        return images;
    }

    public void setImages(List<CategoryImage> images) {
        this.images = images;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
