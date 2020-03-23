/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author oracle
 */
@Entity
@Table(name = "COMENTARII")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Comentarii.findAll", query = "SELECT c FROM Comentarii c"),
    @NamedQuery(name = "Comentarii.findByComentariuId", query = "SELECT c FROM Comentarii c WHERE c.comentariuId = :comentariuId"),
    @NamedQuery(name = "Comentarii.findByNumeUser", query = "SELECT c FROM Comentarii c WHERE c.numeUser = :numeUser"),
    @NamedQuery(name = "Comentarii.findByContinut", query = "SELECT c FROM Comentarii c WHERE c.continut = :continut")})
public class Comentarii implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "COMENTARIU_ID")
    private Integer comentariuId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 15)
    @Column(name = "NUME_USER")
    private String numeUser;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 300)
    @Column(name = "CONTINUT")
    private String continut;
    @JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID")
    @ManyToOne(optional = false)
    private Status statusId;
    @JoinColumn(name = "ARTICOL_ID", referencedColumnName = "ARTICOL_ID")
    @ManyToOne(optional = false)
    private Articole articolId;

    public Comentarii() {
    }

    public Comentarii(Integer comentariuId) {
        this.comentariuId = comentariuId;
    }

    public Comentarii(Integer comentariuId, String numeUser, String continut) {
        this.comentariuId = comentariuId;
        this.numeUser = numeUser;
        this.continut = continut;
    }

    public Integer getComentariuId() {
        return comentariuId;
    }

    public void setComentariuId(Integer comentariuId) {
        this.comentariuId = comentariuId;
    }

    public String getNumeUser() {
        return numeUser;
    }

    public void setNumeUser(String numeUser) {
        this.numeUser = numeUser;
    }

    public String getContinut() {
        return continut;
    }

    public void setContinut(String continut) {
        this.continut = continut;
    }

    public Status getStatusId() {
        return statusId;
    }

    public void setStatusId(Status statusId) {
        this.statusId = statusId;
    }

    public Articole getArticolId() {
        return articolId;
    }

    public void setArticolId(Articole articolId) {
        this.articolId = articolId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (comentariuId != null ? comentariuId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Comentarii)) {
            return false;
        }
        Comentarii other = (Comentarii) object;
        if ((this.comentariuId == null && other.comentariuId != null) || (this.comentariuId != null && !this.comentariuId.equals(other.comentariuId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Comentarii[ comentariuId=" + comentariuId + " ]";
    }
    
}
