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
@Table(name = "AUTORI")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Autori.findAll", query = "SELECT a FROM Autori a"),
    @NamedQuery(name = "Autori.findByAutorId", query = "SELECT a FROM Autori a WHERE a.autorId = :autorId"),
    @NamedQuery(name = "Autori.findByNume", query = "SELECT a FROM Autori a WHERE a.nume = :nume")})
public class Autori implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "AUTOR_ID")
    private Integer autorId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 160)
    @Column(name = "NUME")
    private String nume;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "autorId")
    private Collection<Articole> articoleCollection;
    @JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID")
    @ManyToOne
    private Status statusId;

    public Autori() {
    }

    public Autori(Integer autorId) {
        this.autorId = autorId;
    }

    public Autori(Integer autorId, String nume) {
        this.autorId = autorId;
        this.nume = nume;
    }

    public Integer getAutorId() {
        return autorId;
    }

    public void setAutorId(Integer autorId) {
        this.autorId = autorId;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (autorId != null ? autorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Autori)) {
            return false;
        }
        Autori other = (Autori) object;
        if ((this.autorId == null && other.autorId != null) || (this.autorId != null && !this.autorId.equals(other.autorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Autori[ autorId=" + autorId + " ]";
    }
    
}
