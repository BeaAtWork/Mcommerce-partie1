package com.ecommerce.microcommerce.web.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.model.Product;
import com.ecommerce.microcommerce.web.exceptions.ProduitIntrouvableException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Iterator;
import java.util.List;


@Api(description = "API pour es opérations CRUD sur les produits.")

@RestController
public class ProductController {


    @Autowired
    private ProductDao productDao;


    //Récupérer la liste des produits

    @RequestMapping(value = "/Produits", method = RequestMethod.GET)
    public MappingJacksonValue listeProduits() {
        Iterable<Product> produits = productDao.findAll();
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produits);
        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }


    //Récupérer un produit par son Id

    @ApiOperation(value = "Récupère un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/Produits/{id}")
    public MappingJacksonValue afficherUnProduit(@PathVariable int id) {
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        Product produit = productDao.findById(id);

        if (produit == null)
            throw new ProduitIntrouvableException("Le produit avec l'id " + id + " est INTROUVABLE. Écran Bleu si je pouvais.");

        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsFiltres = new MappingJacksonValue(produit);
        produitsFiltres.setFilters(listDeNosFiltres);

        return produitsFiltres;
    }


    //ajouter un produit
    @PostMapping(value = "/ajouterProduit")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) {

        Product productAdded = productDao.save(product);

        if (productAdded == null)
            return ResponseEntity.noContent().build();

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(productAdded.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping(value = "/Produits/{id}")

    public void supprimerProduit(@PathVariable int id) {
        productDao.delete(id);
    }

    @PutMapping(value = "/Produits")

    public void updateProduit(@RequestBody Product product) {
        productDao.save(product);
    }


    //Pour les tests
    @GetMapping(value = "test/produits/{prix}")
    public List<Product> testeDeRequetes(@PathVariable int prix) {
        return productDao.chercherUnProduitCher(400);
    }


    //Récupérer la marge des produits
    @ApiOperation(value = "Récupère la marge d'un produit grâce à son ID à condition que celui-ci soit en stock!")
    @GetMapping(value = "/adminProduits")

    public MappingJacksonValue adminProduits() {
        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAll();
        Iterable<Product> produits = productDao.findAll();
        if (produits == null)
            throw new ProduitIntrouvableException("Pas de produits trouvés ...");

        Iterator it = produits.iterator();
        while (it.hasNext()) {
            Product produit = (Product) it.next();
            int marge = (produit.getPrix() - produit.getPrixAchat());
            produit.setMarge(marge);
        }
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsMapppes = new MappingJacksonValue(produits);
        produitsMapppes.setFilters(listDeNosFiltres);

        return produitsMapppes;
    }


    //Récupérer la liste des produits triés par ordre alphabétique
    @RequestMapping(value = "/produitsTries", method = RequestMethod.GET)

    public MappingJacksonValue trierProduitsParOrdreAlphabetique() {

        Sort.Order order = new Sort.Order("nom");
        Sort sort = new Sort(order);
        Iterable<Product> produits = productDao.findAll(sort);

        SimpleBeanPropertyFilter monFiltre = SimpleBeanPropertyFilter.serializeAllExcept("prixAchat");
        FilterProvider listDeNosFiltres = new SimpleFilterProvider().addFilter("monFiltreDynamique", monFiltre);
        MappingJacksonValue produitsMapppes = new MappingJacksonValue(produits);
        produitsMapppes.setFilters(listDeNosFiltres);
        return produitsMapppes;

    }


}
