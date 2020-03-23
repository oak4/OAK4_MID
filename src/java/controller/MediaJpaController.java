/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import entities.Tag;
import java.util.ArrayList;
import java.util.Collection;
import entities.Articole;
import entities.Media;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author oracle
 */
public class MediaJpaController implements Serializable {

    public MediaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Media media) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (media.getTagCollection() == null) {
            media.setTagCollection(new ArrayList<Tag>());
        }
        if (media.getArticoleCollection() == null) {
            media.setArticoleCollection(new ArrayList<Articole>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Tag> attachedTagCollection = new ArrayList<Tag>();
            for (Tag tagCollectionTagToAttach : media.getTagCollection()) {
                tagCollectionTagToAttach = em.getReference(tagCollectionTagToAttach.getClass(), tagCollectionTagToAttach.getTagId());
                attachedTagCollection.add(tagCollectionTagToAttach);
            }
            media.setTagCollection(attachedTagCollection);
            Collection<Articole> attachedArticoleCollection = new ArrayList<Articole>();
            for (Articole articoleCollectionArticoleToAttach : media.getArticoleCollection()) {
                articoleCollectionArticoleToAttach = em.getReference(articoleCollectionArticoleToAttach.getClass(), articoleCollectionArticoleToAttach.getArticolId());
                attachedArticoleCollection.add(articoleCollectionArticoleToAttach);
            }
            media.setArticoleCollection(attachedArticoleCollection);
            em.persist(media);
            for (Tag tagCollectionTag : media.getTagCollection()) {
                tagCollectionTag.getMediaCollection().add(media);
                tagCollectionTag = em.merge(tagCollectionTag);
            }
            for (Articole articoleCollectionArticole : media.getArticoleCollection()) {
                articoleCollectionArticole.getMediaCollection().add(media);
                articoleCollectionArticole = em.merge(articoleCollectionArticole);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findMedia(media.getMediaId()) != null) {
                throw new PreexistingEntityException("Media " + media + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Media media) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Media persistentMedia = em.find(Media.class, media.getMediaId());
            Collection<Tag> tagCollectionOld = persistentMedia.getTagCollection();
            Collection<Tag> tagCollectionNew = media.getTagCollection();
            Collection<Articole> articoleCollectionOld = persistentMedia.getArticoleCollection();
            Collection<Articole> articoleCollectionNew = media.getArticoleCollection();
            Collection<Tag> attachedTagCollectionNew = new ArrayList<Tag>();
            for (Tag tagCollectionNewTagToAttach : tagCollectionNew) {
                tagCollectionNewTagToAttach = em.getReference(tagCollectionNewTagToAttach.getClass(), tagCollectionNewTagToAttach.getTagId());
                attachedTagCollectionNew.add(tagCollectionNewTagToAttach);
            }
            tagCollectionNew = attachedTagCollectionNew;
            media.setTagCollection(tagCollectionNew);
            Collection<Articole> attachedArticoleCollectionNew = new ArrayList<Articole>();
            for (Articole articoleCollectionNewArticoleToAttach : articoleCollectionNew) {
                articoleCollectionNewArticoleToAttach = em.getReference(articoleCollectionNewArticoleToAttach.getClass(), articoleCollectionNewArticoleToAttach.getArticolId());
                attachedArticoleCollectionNew.add(articoleCollectionNewArticoleToAttach);
            }
            articoleCollectionNew = attachedArticoleCollectionNew;
            media.setArticoleCollection(articoleCollectionNew);
            media = em.merge(media);
            for (Tag tagCollectionOldTag : tagCollectionOld) {
                if (!tagCollectionNew.contains(tagCollectionOldTag)) {
                    tagCollectionOldTag.getMediaCollection().remove(media);
                    tagCollectionOldTag = em.merge(tagCollectionOldTag);
                }
            }
            for (Tag tagCollectionNewTag : tagCollectionNew) {
                if (!tagCollectionOld.contains(tagCollectionNewTag)) {
                    tagCollectionNewTag.getMediaCollection().add(media);
                    tagCollectionNewTag = em.merge(tagCollectionNewTag);
                }
            }
            for (Articole articoleCollectionOldArticole : articoleCollectionOld) {
                if (!articoleCollectionNew.contains(articoleCollectionOldArticole)) {
                    articoleCollectionOldArticole.getMediaCollection().remove(media);
                    articoleCollectionOldArticole = em.merge(articoleCollectionOldArticole);
                }
            }
            for (Articole articoleCollectionNewArticole : articoleCollectionNew) {
                if (!articoleCollectionOld.contains(articoleCollectionNewArticole)) {
                    articoleCollectionNewArticole.getMediaCollection().add(media);
                    articoleCollectionNewArticole = em.merge(articoleCollectionNewArticole);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = media.getMediaId();
                if (findMedia(id) == null) {
                    throw new NonexistentEntityException("The media with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Media media;
            try {
                media = em.getReference(Media.class, id);
                media.getMediaId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The media with id " + id + " no longer exists.", enfe);
            }
            Collection<Tag> tagCollection = media.getTagCollection();
            for (Tag tagCollectionTag : tagCollection) {
                tagCollectionTag.getMediaCollection().remove(media);
                tagCollectionTag = em.merge(tagCollectionTag);
            }
            Collection<Articole> articoleCollection = media.getArticoleCollection();
            for (Articole articoleCollectionArticole : articoleCollection) {
                articoleCollectionArticole.getMediaCollection().remove(media);
                articoleCollectionArticole = em.merge(articoleCollectionArticole);
            }
            em.remove(media);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Media> findMediaEntities() {
        return findMediaEntities(true, -1, -1);
    }

    public List<Media> findMediaEntities(int maxResults, int firstResult) {
        return findMediaEntities(false, maxResults, firstResult);
    }

    private List<Media> findMediaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Media as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Media findMedia(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Media.class, id);
        } finally {
            em.close();
        }
    }

    public int getMediaCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Media as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
