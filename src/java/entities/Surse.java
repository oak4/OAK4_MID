/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author oracle
 */
@Entity
@Table(name = "SURSE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Surse.findAll", query = "SELECT s FROM Surse s"),
    @NamedQuery(name = "Surse.findBySursaId", query = "SELECT s FROM Surse s WHERE s.sursaId = :sursaId"),
    @NamedQuery(name = "Surse.findByNume", query = "SELECT s FROM Surse s WHERE s.nume = :nume"),
    @NamedQuery(name = "Surse.findByUrl", query = "SELECT s FROM Surse s WHERE s.url = :url")})
public class Surse implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SURSA_ID")
    private Integer sursaId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "NUME")
    private String nume;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2000)
    @Column(name = "URL")
    private String url;
    @ManyToMany(mappedBy = "surseCollection")
    private Collection<Articole> articoleCollection;
    @JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID")
    @ManyToOne(optional = false)
    private Status statusId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sursaId")
    private Collection<Articole> articoleCollection1;

    public Surse() {
    }

    public Surse(Integer sursaId) {
        this.sursaId = sursaId;
    }

    public Surse(Integer sursaId, String nume, String url) {
        this.sursaId = sursaId;
        this.nume = nume;
        this.url = url;
    }

    public Integer getSursaId() {
        return sursaId;
    }

    public void setSursaId(Integer sursaId) {
        this.sursaId = sursaId;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlTransient
    public Collection<Articole> getArticoleCollection() {
        return articoleCollection;
    }

    public void setArticoleCollection(Collection<Articole> articoleCollection) {
        this.articoleCollection = articoleCollection;
    }

    public Status getStatusId() {
        return statusId;
    }

    public void setStatusId(Status statusId) {
        this.statusId = statusId;
    }

    @XmlTransient
    public Collection<Articole> getArticoleCollection1() {
        return articoleCollection1;
    }

    public void setArticoleCollection1(Collection<Articole> articoleCollection1) {
        this.articoleCollection1 = articoleCollection1;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sursaId != null ? sursaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Surse)) {
            return false;
        }
        Surse other = (Surse) object;
        if ((this.sursaId == null && other.sursaId != null) || (this.sursaId != null && !this.sursaId.equals(other.sursaId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Surse[ sursaId=" + sursaId + " ]";
    }
    
}
