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
import entities.Media;
import entities.Tag;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author oracle
 */
public class TagJpaController implements Serializable {

    public TagJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tag tag) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (tag.getMediaCollection() == null) {
            tag.setMediaCollection(new ArrayList<Media>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Media> attachedMediaCollection = new ArrayList<Media>();
            for (Media mediaCollectionMediaToAttach : tag.getMediaCollection()) {
                mediaCollectionMediaToAttach = em.getReference(mediaCollectionMediaToAttach.getClass(), mediaCollectionMediaToAttach.getMediaId());
                attachedMediaCollection.add(mediaCollectionMediaToAttach);
            }
            tag.setMediaCollection(attachedMediaCollection);
            em.persist(tag);
            for (Media mediaCollectionMedia : tag.getMediaCollection()) {
                mediaCollectionMedia.getTagCollection().add(tag);
                mediaCollectionMedia = em.merge(mediaCollectionMedia);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findTag(tag.getTagId()) != null) {
                throw new PreexistingEntityException("Tag " + tag + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Tag tag) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Tag persistentTag = em.find(Tag.class, tag.getTagId());
            Collection<Media> mediaCollectionOld = persistentTag.getMediaCollection();
            Collection<Media> mediaCollectionNew = tag.getMediaCollection();
            Collection<Media> attachedMediaCollectionNew = new ArrayList<Media>();
            for (Media mediaCollectionNewMediaToAttach : mediaCollectionNew) {
                mediaCollectionNewMediaToAttach = em.getReference(mediaCollectionNewMediaToAttach.getClass(), mediaCollectionNewMediaToAttach.getMediaId());
                attachedMediaCollectionNew.add(mediaCollectionNewMediaToAttach);
            }
            mediaCollectionNew = attachedMediaCollectionNew;
            tag.setMediaCollection(mediaCollectionNew);
            tag = em.merge(tag);
            for (Media mediaCollectionOldMedia : mediaCollectionOld) {
                if (!mediaCollectionNew.contains(mediaCollectionOldMedia)) {
                    mediaCollectionOldMedia.getTagCollection().remove(tag);
                    mediaCollectionOldMedia = em.merge(mediaCollectionOldMedia);
                }
            }
            for (Media mediaCollectionNewMedia : mediaCollectionNew) {
                if (!mediaCollectionOld.contains(mediaCollectionNewMedia)) {
                    mediaCollectionNewMedia.getTagCollection().add(tag);
                    mediaCollectionNewMedia = em.merge(mediaCollectionNewMedia);
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
                Integer id = tag.getTagId();
                if (findTag(id) == null) {
                    throw new NonexistentEntityException("The tag with id " + id + " no longer exists.");
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
            Tag tag;
            try {
                tag = em.getReference(Tag.class, id);
                tag.getTagId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tag with id " + id + " no longer exists.", enfe);
            }
            Collection<Media> mediaCollection = tag.getMediaCollection();
            for (Media mediaCollectionMedia : mediaCollection) {
                mediaCollectionMedia.getTagCollection().remove(tag);
                mediaCollectionMedia = em.merge(mediaCollectionMedia);
            }
            em.remove(tag);
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

    public List<Tag> findTagEntities() {
        return findTagEntities(true, -1, -1);
    }

    public List<Tag> findTagEntities(int maxResults, int firstResult) {
        return findTagEntities(false, maxResults, firstResult);
    }

    private List<Tag> findTagEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Tag as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Tag findTag(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tag.class, id);
        } finally {
            em.close();
        }
    }

    public int getTagCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Tag as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
