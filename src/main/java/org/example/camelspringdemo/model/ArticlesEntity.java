package org.example.camelspringdemo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@JacksonXmlRootElement(localName = "articles")
@Entity
@Table(name = "articles")
public class ArticlesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition="TEXT")
    private String headers;

    @JacksonXmlProperty(localName = "article")
    @JacksonXmlElementWrapper(useWrapping = false)
    @OneToMany(cascade = CascadeType.ALL)
    private List<ArticleEntity> articleEntityList;

}
