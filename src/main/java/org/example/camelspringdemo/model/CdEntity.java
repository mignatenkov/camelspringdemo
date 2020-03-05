package org.example.camelspringdemo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "demo_cd")
public class CdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "demo_catalog_id")
    private CatalogEntity catalog;

    @JacksonXmlProperty(localName = "TITLE")
    private String title;
    @JacksonXmlProperty(localName = "ARTIST")
    private String artist;
    @JacksonXmlProperty(localName = "COUNTRY")
    private String country;
    @JacksonXmlProperty(localName = "COMPANY")
    private String company;
    @JacksonXmlProperty(localName = "PRICE")
    private String price;
    @JacksonXmlProperty(localName = "YEAR")
    private String year;

}
