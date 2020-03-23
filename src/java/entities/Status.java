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
@Table(name = "STATUS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Status.findAll", query = "SELECT s FROM Status s"),
    @NamedQuery(name = "Status.findByStatusId", query = "SELECT s FROM Status s WHERE s.statusId = :statusId"),
    @NamedQuery(name = "Status.findByNume", query = "SELECT s FROM Status s WHERE s.nume = :nume")})
public class Status implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "STATUS_ID")
    private Integer statusId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "NUME")
    private String nume;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "statusId")
    private Collection<Surse> surseCollection;
    @OneToMany(mappedBy = "statusId")
    private Collection<Categorii> categoriiCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "statusId")
    private Collection<Comentarii> comentariiCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "statusId")
    private Collection<Articole> articoleCollection;
    @OneToMany(mappedBy = "statusId")
    private Collection<Autori> autoriCollection;

    public Status() {
    }

    public Status(Integer statusId) {
        this.statusId = statusId;
    }

    public Status(Integer statusId, String nume) {
        this.statusId = statusId;
        this.nume = nume;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    @XmlTransient
    public Collection<Surse> getSurseCollection() {
        return surseCollection;
    }

    public void setSurseCollection(Collection<Surse> surseCollection) {
        this.surseCollection = surseCollection;
    }

    @XmlTransient
    public Collection<Categorii> getCategoriiCollection() {
        return categoriiCollection;
    }

    public void setCategoriiCollection(Collection<Categorii> categoriiCollection) {
        this.categoriiCollection = categoriiCollection;
    }

    @XmlTransient
    public Collection<Comentarii> getComentariiCollection() {
        return comentariiCollection;
    }

    public void setComentariiCollection(Collection<Comentarii> comentariiCollection) {
        this.comentariiCollection = comentariiCollection;
    }

    @XmlTransient
    public Collection<Articole> getArticoleCollection() {
        return articoleCollection;
    }

    public void setArticoleCollection(Collection<Articole> articoleCollection) {
        this.articoleCollection = articoleCollection;
    }

    @XmlTransient
    public Collection<Autori> getAutoriCollection() {
        return autoriCollection;
    }

    public void setAutoriCollection(Collection<Autori> autoriCollection) {
        this.autoriCollection = autoriCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (statusId != null ? statusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Status)) {
            return false;
        }
        Status other = (Status) object;
        if ((this.statusId == null && other.statusId != null) || (this.statusId != null && !this.statusId.equals(other.statusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Status[ statusId=" + statusId + " ]";
    }
    
}
