/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@Table(name = "MEDIA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Media.findAll", query = "SELECT m FROM Media m"),
    @NamedQuery(name = "Media.findByMediaId", query = "SELECT m FROM Media m WHERE m.mediaId = :mediaId"),
    @NamedQuery(name = "Media.findByUrl", query = "SELECT m FROM Media m WHERE m.url = :url")})
public class Media implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEDIA_ID")
    private Integer mediaId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2000)
    @Column(name = "URL")
    private String url;
    @JoinTable(name = "TAG_MEDIA", joinColumns = {
        @JoinColumn(name = "MEDIA_ID", referencedColumnName = "MEDIA_ID")}, inverseJoinColumns = {
        @JoinColumn(name = "TAG_ID", referencedColumnName = "TAG_ID")})
    @ManyToMany
    private Collection<Tag> tagCollection;
    @ManyToMany(mappedBy = "mediaCollection")
    private Collection<Articole> articoleCollection;

    public Media() {
    }

    public Media(Integer mediaId) {
        this.mediaId = mediaId;
    }

    public Media(Integer mediaId, String url) {
        this.mediaId = mediaId;
        this.url = url;
    }

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlTransient
    public Collection<Tag> getTagCollection() {
        return tagCollection;
    }

    public void setTagCollection(Collection<Tag> tagCollection) {
        this.tagCollection = tagCollection;
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
        hash += (mediaId != null ? mediaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Media)) {
            return false;
        }
        Media other = (Media) object;
        if ((this.mediaId == null && other.mediaId != null) || (this.mediaId != null && !this.mediaId.equals(other.mediaId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entities.Media[ mediaId=" + mediaId + " ]";
    }
    
}
