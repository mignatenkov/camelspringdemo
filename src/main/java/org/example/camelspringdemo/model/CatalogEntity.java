package org.example.camelspringdemo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@JacksonXmlRootElement(localName = "CATALOG")
@Entity
@Table(name = "demo_catalog")
public class CatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JacksonXmlProperty(localName = "CD")
    @JacksonXmlElementWrapper(useWrapping = false)
    @OneToMany(cascade = CascadeType.ALL)
    private List<CdEntity> cdEntityList;

}
