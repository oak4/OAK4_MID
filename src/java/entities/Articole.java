/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author oracle
 */
@Entity
@Table(name = "ARTICOLE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Articole.findAll", query = "SELECT a FROM Articole a"),
    @NamedQuery(name = "Articole.findByArticolId", query = "SELECT a FROM Articole a WHERE a.articolId = :articolId"),
    @NamedQuery(name = "Articole.findByDataPublicare", query = "SELECT a FROM Articole a WHERE a.dataPublicare = :dataPublicare"),
    @NamedQuery(name = "Articole.findByTitlu", query = "SELECT a FROM Articole a WHERE a.titlu = :titlu"),
    @NamedQuery(name = "Articole.findByContinut", query = "SELECT a FROM Articole a WHERE a.continut = :continut")})
public class Articole implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ARTICOL_ID")
    private Integer articolId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATA_PUBLICARE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPublicare;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 1000)
    @Column(name = "TITLU")
    private String titlu;
    @Size(max = 4000)
    @Column(name = "CONTINUT")
    private String continut;
    @JoinTable(name = "ARTICOLE_MEDIA", joinColumns = {
        @JoinColumn(name = "ARTICOL_ID", referencedColumnName = "ARTICOL_ID")}, inverseJoinColumns = {
        @JoinColumn(name = "MEDIA_ID", referencedColumnName = "MEDIA_ID")})
    @ManyToMany
    private Collection<Media> mediaCollection;
    @JoinTable(name = "SURSE_ARTICOLE", joinColumns = {
        @JoinColumn(name = "ARTICOL_ID", referencedColumnName = "ARTICOL_ID")}, inverseJoinColumns = {
        @JoinColumn(name = "SURSA_ID", referencedColumnName = "SURSA_ID")})
    @ManyToMany
    private Collection<Surse> surseCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "articolId")
    private Collection<Comentarii> comentariiCollection;
    @JoinColumn(name = "SURSA_ID", referencedColumnName = "SURSA_ID")
    @ManyToOne(optional = false)
    private Surse sursaId;
    @JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID")
    @ManyToOne(optional = false)
    private Status statusId;
    @JoinColumn(name = "CATEGORIE_ID", referencedColumnName = "CATEGORIE_ID")
    @ManyToOne(optional = false)
    private Categorii categorieId;
    @JoinColumn(name = "AUTOR_ID", referencedColumnName = "AUTOR_ID")
    @ManyToOne(optional = false)
    private Autori autorId;

    public Articole() {
    }

    public Articole(Integer articolId) {
        this.articolId = articolId;
    }

    public Articole(Integer articolId, Date dataPublicare, String titlu) {
        this.articolId = articolId;
        this.dataPublicare = dataPublicare;
        this.titlu = titlu;
    }

    public Integer getArticolId() {
        return articolId;
    }

    public void setArticolId(Integer articolId) {
        this.articolId = articolId;
    }

    public Date getDataPublicare() {
        return dataPublicare;
    }

    public void setDataPublicare(Date dataPublicare) {
        this.dataPublicare = dataPublicare;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
    }

    public String getContinut() {
        return continut;
    }

    public void setContinut(String continut) {
        this.continut = continut;
    }

    @XmlTransient
    public Collection<Media> getMediaCollection() {
        return mediaCollection;
    }

    public void setMediaCollection(Collection<Media> mediaCollection) {
        this.mediaCollection = mediaCollection;
    }

    @XmlTransient
    public Collection<Surse> getSurseCollection() {
        return surseCollection;
    }

    public void setSurseCollection(Collection<Surse> surseCollection) {
        this.surseCollection = surseCollection;
    }

    @XmlTransient
    public Collection<Comentarii> getComentariiCollection() {
        return comentariiCollection;
    }

    public void setComentariiCollection(Collection<Comentarii> comentariiCollection) {
        this.comentariiCollection = comentariiCollection;
    }

    public Surse getSursaId() {
        return sursaId;
    }

    public void setSursaId(Surse sursaId) {
        this.sursaId = sursaId;
    }

    public Status getStatusId() {
        return statusId;
    }

    public void setStatusId(Status statusId) {
        this.statusId = statusId;
    }

    public Categorii getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Categorii categorieId) {
        this.categorieId = categorieId;
    }

    public Autori getAutorId() {
        return autorId;
    }

    public void setAutorId(Autori autorId) {
        this.autorId = autorId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (articolId != null ? articolId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Articole)) {
            return false;
        }
        Articole other = (Articole) object;
        if ((this.articolId == null && other.articolId != null) || (this.articolId != null && !this.articolId.equals(other.articolId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Articole[ articolId=" + articolId + " ]";
    }
    
}
