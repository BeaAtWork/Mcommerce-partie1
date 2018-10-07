package com.ecommerce.microcommerce.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@JsonFilter("monFiltreDynamique")
public class Marge {

    Product product;
    int marge;

    public Marge() {
    }


    //constructeur par défaut
    public Marge(Product product, int marge) {
        this.product = product;
        this.marge = marge;
    }

    public Product getProduct() {
        return product;
    }

    public int getMarge() {
        return marge;
    }

    @Override
    public String toString() {
        return "Marge{" +
                "product=" + product +
                ", marge=" + marge +
                '}';
    }

    public void setMarge(int marge) {
        this.marge = marge;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
