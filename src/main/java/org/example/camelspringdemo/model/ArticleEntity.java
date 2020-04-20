package org.example.camelspringdemo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "article")
public class ArticleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "articles_id")
    private ArticlesEntity articles;

    @JacksonXmlProperty(localName = "id_art")
    private Long idArt;
    @JacksonXmlProperty(localName = "name")
    private String name;
    @JacksonXmlProperty(localName = "code")
    private String code;
    @JacksonXmlProperty(localName = "username")
    private String username;
    @JacksonXmlProperty(localName = "guid")
    private String guid;

}
