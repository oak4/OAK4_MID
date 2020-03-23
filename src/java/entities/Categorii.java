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
@Table(name = "CATEGORII")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Categorii.findAll", query = "SELECT c FROM Categorii c"),
    @NamedQuery(name = "Categorii.findByCategorieId", query = "SELECT c FROM Categorii c WHERE c.categorieId = :categorieId"),
    @NamedQuery(name = "Categorii.findByNume", query = "SELECT c FROM Categorii c WHERE c.nume = :nume")})
public class Categorii implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "CATEGORIE_ID")
    private Integer categorieId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "NUME")
    private String nume;
    @JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID")
    @ManyToOne
    private Status statusId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categorieId")
    private Collection<Articole> articoleCollection;

    public Categorii() {
    }

    public Categorii(Integer categorieId) {
        this.categorieId = categorieId;
    }

    public Categorii(Integer categorieId, String nume) {
        this.categorieId = categorieId;
        this.nume = nume;
    }

    public Integer getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Integer categorieId) {
        this.categorieId = categorieId;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public Status getStatusId() {
        return statusId;
    }

    public void setStatusId(Status statusId) {
        this.statusId = statusId;
    }

    @XmlTransient
    public Collection<Articole> getArticoleCollection() {
        return articoleCollection;
    }

    public void setArticoleCollection(Collection<Articole> articoleCollection) {
        this.articoleCollection = articoleCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (categorieId != null ? categorieId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Categorii)) {
            return false;
        }
        Categorii other = (Categorii) object;
        if ((this.categorieId == null && other.categorieId != null) || (this.categorieId != null && !this.categorieId.equals(other.categorieId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Categorii[ categorieId=" + categorieId + " ]";
    }
    
}
