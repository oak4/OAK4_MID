/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.IllegalOrphanException;
import controller.exceptions.NonexistentEntityException;
import controller.exceptions.PreexistingEntityException;
import controller.exceptions.RollbackFailureException;
import entities.Articole;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import entities.Surse;
import entities.Status;
import entities.Categorii;
import entities.Autori;
import entities.Media;
import java.util.ArrayList;
import java.util.Collection;
import entities.Comentarii;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author oracle
 */
public class ArticoleJpaController implements Serializable {

    public ArticoleJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Articole articole) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (articole.getMediaCollection() == null) {
            articole.setMediaCollection(new ArrayList<Media>());
        }
        if (articole.getSurseCollection() == null) {
            articole.setSurseCollection(new ArrayList<Surse>());
        }
        if (articole.getComentariiCollection() == null) {
            articole.setComentariiCollection(new ArrayList<Comentarii>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Surse sursaId = articole.getSursaId();
            if (sursaId != null) {
                sursaId = em.getReference(sursaId.getClass(), sursaId.getSursaId());
                articole.setSursaId(sursaId);
            }
            Status statusId = articole.getStatusId();
            if (statusId != null) {
                statusId = em.getReference(statusId.getClass(), statusId.getStatusId());
                articole.setStatusId(statusId);
            }
            Categorii categorieId = articole.getCategorieId();
            if (categorieId != null) {
                categorieId = em.getReference(categorieId.getClass(), categorieId.getCategorieId());
                articole.setCategorieId(categorieId);
            }
            Autori autorId = articole.getAutorId();
            if (autorId != null) {
                autorId = em.getReference(autorId.getClass(), autorId.getAutorId());
                articole.setAutorId(autorId);
            }
            Collection<Media> attachedMediaCollection = new ArrayList<Media>();
            for (Media mediaCollectionMediaToAttach : articole.getMediaCollection()) {
                mediaCollectionMediaToAttach = em.getReference(mediaCollectionMediaToAttach.getClass(), mediaCollectionMediaToAttach.getMediaId());
                attachedMediaCollection.add(mediaCollectionMediaToAttach);
            }
            articole.setMediaCollection(attachedMediaCollection);
            Collection<Surse> attachedSurseCollection = new ArrayList<Surse>();
            for (Surse surseCollectionSurseToAttach : articole.getSurseCollection()) {
                surseCollectionSurseToAttach = em.getReference(surseCollectionSurseToAttach.getClass(), surseCollectionSurseToAttach.getSursaId());
                attachedSurseCollection.add(surseCollectionSurseToAttach);
            }
            articole.setSurseCollection(attachedSurseCollection);
            Collection<Comentarii> attachedComentariiCollection = new ArrayList<Comentarii>();
            for (Comentarii comentariiCollectionComentariiToAttach : articole.getComentariiCollection()) {
                comentariiCollectionComentariiToAttach = em.getReference(comentariiCollectionComentariiToAttach.getClass(), comentariiCollectionComentariiToAttach.getComentariuId());
                attachedComentariiCollection.add(comentariiCollectionComentariiToAttach);
            }
            articole.setComentariiCollection(attachedComentariiCollection);
            em.persist(articole);
            if (sursaId != null) {
                sursaId.getArticoleCollection().add(articole);
                sursaId = em.merge(sursaId);
            }
            if (statusId != null) {
                statusId.getArticoleCollection().add(articole);
                statusId = em.merge(statusId);
            }
            if (categorieId != null) {
                categorieId.getArticoleCollection().add(articole);
                categorieId = em.merge(categorieId);
            }
            if (autorId != null) {
                autorId.getArticoleCollection().add(articole);
                autorId = em.merge(autorId);
            }
            for (Media mediaCollectionMedia : articole.getMediaCollection()) {
                mediaCollectionMedia.getArticoleCollection().add(articole);
                mediaCollectionMedia = em.merge(mediaCollectionMedia);
            }
            for (Surse surseCollectionSurse : articole.getSurseCollection()) {
                surseCollectionSurse.getArticoleCollection().add(articole);
                surseCollectionSurse = em.merge(surseCollectionSurse);
            }
            for (Comentarii comentariiCollectionComentarii : articole.getComentariiCollection()) {
                Articole oldArticolIdOfComentariiCollectionComentarii = comentariiCollectionComentarii.getArticolId();
                comentariiCollectionComentarii.setArticolId(articole);
                comentariiCollectionComentarii = em.merge(comentariiCollectionComentarii);
                if (oldArticolIdOfComentariiCollectionComentarii != null) {
                    oldArticolIdOfComentariiCollectionComentarii.getComentariiCollection().remove(comentariiCollectionComentarii);
                    oldArticolIdOfComentariiCollectionComentarii = em.merge(oldArticolIdOfComentariiCollectionComentarii);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findArticole(articole.getArticolId()) != null) {
                throw new PreexistingEntityException("Articole " + articole + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Articole articole) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Articole persistentArticole = em.find(Articole.class, articole.getArticolId());
            Surse sursaIdOld = persistentArticole.getSursaId();
            Surse sursaIdNew = articole.getSursaId();
            Status statusIdOld = persistentArticole.getStatusId();
            Status statusIdNew = articole.getStatusId();
            Categorii categorieIdOld = persistentArticole.getCategorieId();
            Categorii categorieIdNew = articole.getCategorieId();
            Autori autorIdOld = persistentArticole.getAutorId();
            Autori autorIdNew = articole.getAutorId();
            Collection<Media> mediaCollectionOld = persistentArticole.getMediaCollection();
            Collection<Media> mediaCollectionNew = articole.getMediaCollection();
            Collection<Surse> surseCollectionOld = persistentArticole.getSurseCollection();
            Collection<Surse> surseCollectionNew = articole.getSurseCollection();
            Collection<Comentarii> comentariiCollectionOld = persistentArticole.getComentariiCollection();
            Collection<Comentarii> comentariiCollectionNew = articole.getComentariiCollection();
            List<String> illegalOrphanMessages = null;
            for (Comentarii comentariiCollectionOldComentarii : comentariiCollectionOld) {
                if (!comentariiCollectionNew.contains(comentariiCollectionOldComentarii)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comentarii " + comentariiCollectionOldComentarii + " since its articolId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (sursaIdNew != null) {
                sursaIdNew = em.getReference(sursaIdNew.getClass(), sursaIdNew.getSursaId());
                articole.setSursaId(sursaIdNew);
            }
            if (statusIdNew != null) {
                statusIdNew = em.getReference(statusIdNew.getClass(), statusIdNew.getStatusId());
                articole.setStatusId(statusIdNew);
            }
            if (categorieIdNew != null) {
                categorieIdNew = em.getReference(categorieIdNew.getClass(), categorieIdNew.getCategorieId());
                articole.setCategorieId(categorieIdNew);
            }
            if (autorIdNew != null) {
                autorIdNew = em.getReference(autorIdNew.getClass(), autorIdNew.getAutorId());
                articole.setAutorId(autorIdNew);
            }
            Collection<Media> attachedMediaCollectionNew = new ArrayList<Media>();
            for (Media mediaCollectionNewMediaToAttach : mediaCollectionNew) {
                mediaCollectionNewMediaToAttach = em.getReference(mediaCollectionNewMediaToAttach.getClass(), mediaCollectionNewMediaToAttach.getMediaId());
                attachedMediaCollectionNew.add(mediaCollectionNewMediaToAttach);
            }
            mediaCollectionNew = attachedMediaCollectionNew;
            articole.setMediaCollection(mediaCollectionNew);
            Collection<Surse> attachedSurseCollectionNew = new ArrayList<Surse>();
            for (Surse surseCollectionNewSurseToAttach : surseCollectionNew) {
                surseCollectionNewSurseToAttach = em.getReference(surseCollectionNewSurseToAttach.getClass(), surseCollectionNewSurseToAttach.getSursaId());
                attachedSurseCollectionNew.add(surseCollectionNewSurseToAttach);
            }
            surseCollectionNew = attachedSurseCollectionNew;
            articole.setSurseCollection(surseCollectionNew);
            Collection<Comentarii> attachedComentariiCollectionNew = new ArrayList<Comentarii>();
            for (Comentarii comentariiCollectionNewComentariiToAttach : comentariiCollectionNew) {
                comentariiCollectionNewComentariiToAttach = em.getReference(comentariiCollectionNewComentariiToAttach.getClass(), comentariiCollectionNewComentariiToAttach.getComentariuId());
                attachedComentariiCollectionNew.add(comentariiCollectionNewComentariiToAttach);
            }
            comentariiCollectionNew = attachedComentariiCollectionNew;
            articole.setComentariiCollection(comentariiCollectionNew);
            articole = em.merge(articole);
            if (sursaIdOld != null && !sursaIdOld.equals(sursaIdNew)) {
                sursaIdOld.getArticoleCollection().remove(articole);
                sursaIdOld = em.merge(sursaIdOld);
            }
            if (sursaIdNew != null && !sursaIdNew.equals(sursaIdOld)) {
                sursaIdNew.getArticoleCollection().add(articole);
                sursaIdNew = em.merge(sursaIdNew);
            }
            if (statusIdOld != null && !statusIdOld.equals(statusIdNew)) {
                statusIdOld.getArticoleCollection().remove(articole);
                statusIdOld = em.merge(statusIdOld);
            }
            if (statusIdNew != null && !statusIdNew.equals(statusIdOld)) {
                statusIdNew.getArticoleCollection().add(articole);
                statusIdNew = em.merge(statusIdNew);
            }
            if (categorieIdOld != null && !categorieIdOld.equals(categorieIdNew)) {
                categorieIdOld.getArticoleCollection().remove(articole);
                categorieIdOld = em.merge(categorieIdOld);
            }
            if (categorieIdNew != null && !categorieIdNew.equals(categorieIdOld)) {
                categorieIdNew.getArticoleCollection().add(articole);
                categorieIdNew = em.merge(categorieIdNew);
            }
            if (autorIdOld != null && !autorIdOld.equals(autorIdNew)) {
                autorIdOld.getArticoleCollection().remove(articole);
                autorIdOld = em.merge(autorIdOld);
            }
            if (autorIdNew != null && !autorIdNew.equals(autorIdOld)) {
                autorIdNew.getArticoleCollection().add(articole);
                autorIdNew = em.merge(autorIdNew);
            }
            for (Media mediaCollectionOldMedia : mediaCollectionOld) {
                if (!mediaCollectionNew.contains(mediaCollectionOldMedia)) {
                    mediaCollectionOldMedia.getArticoleCollection().remove(articole);
                    mediaCollectionOldMedia = em.merge(mediaCollectionOldMedia);
                }
            }
            for (Media mediaCollectionNewMedia : mediaCollectionNew) {
                if (!mediaCollectionOld.contains(mediaCollectionNewMedia)) {
                    mediaCollectionNewMedia.getArticoleCollection().add(articole);
                    mediaCollectionNewMedia = em.merge(mediaCollectionNewMedia);
                }
            }
            for (Surse surseCollectionOldSurse : surseCollectionOld) {
                if (!surseCollectionNew.contains(surseCollectionOldSurse)) {
                    surseCollectionOldSurse.getArticoleCollection().remove(articole);
                    surseCollectionOldSurse = em.merge(surseCollectionOldSurse);
                }
            }
            for (Surse surseCollectionNewSurse : surseCollectionNew) {
                if (!surseCollectionOld.contains(surseCollectionNewSurse)) {
                    surseCollectionNewSurse.getArticoleCollection().add(articole);
                    surseCollectionNewSurse = em.merge(surseCollectionNewSurse);
                }
            }
            for (Comentarii comentariiCollectionNewComentarii : comentariiCollectionNew) {
                if (!comentariiCollectionOld.contains(comentariiCollectionNewComentarii)) {
                    Articole oldArticolIdOfComentariiCollectionNewComentarii = comentariiCollectionNewComentarii.getArticolId();
                    comentariiCollectionNewComentarii.setArticolId(articole);
                    comentariiCollectionNewComentarii = em.merge(comentariiCollectionNewComentarii);
                    if (oldArticolIdOfComentariiCollectionNewComentarii != null && !oldArticolIdOfComentariiCollectionNewComentarii.equals(articole)) {
                        oldArticolIdOfComentariiCollectionNewComentarii.getComentariiCollection().remove(comentariiCollectionNewComentarii);
                        oldArticolIdOfComentariiCollectionNewComentarii = em.merge(oldArticolIdOfComentariiCollectionNewComentarii);
                    }
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
                Integer id = articole.getArticolId();
                if (findArticole(id) == null) {
                    throw new NonexistentEntityException("The articole with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Articole articole;
            try {
                articole = em.getReference(Articole.class, id);
                articole.getArticolId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The articole with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Comentarii> comentariiCollectionOrphanCheck = articole.getComentariiCollection();
            for (Comentarii comentariiCollectionOrphanCheckComentarii : comentariiCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Articole (" + articole + ") cannot be destroyed since the Comentarii " + comentariiCollectionOrphanCheckComentarii + " in its comentariiCollection field has a non-nullable articolId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Surse sursaId = articole.getSursaId();
            if (sursaId != null) {
                sursaId.getArticoleCollection().remove(articole);
                sursaId = em.merge(sursaId);
            }
            Status statusId = articole.getStatusId();
            if (statusId != null) {
                statusId.getArticoleCollection().remove(articole);
                statusId = em.merge(statusId);
            }
            Categorii categorieId = articole.getCategorieId();
            if (categorieId != null) {
                categorieId.getArticoleCollection().remove(articole);
                categorieId = em.merge(categorieId);
            }
            Autori autorId = articole.getAutorId();
            if (autorId != null) {
                autorId.getArticoleCollection().remove(articole);
                autorId = em.merge(autorId);
            }
            Collection<Media> mediaCollection = articole.getMediaCollection();
            for (Media mediaCollectionMedia : mediaCollection) {
                mediaCollectionMedia.getArticoleCollection().remove(articole);
                mediaCollectionMedia = em.merge(mediaCollectionMedia);
            }
            Collection<Surse> surseCollection = articole.getSurseCollection();
            for (Surse surseCollectionSurse : surseCollection) {
                surseCollectionSurse.getArticoleCollection().remove(articole);
                surseCollectionSurse = em.merge(surseCollectionSurse);
            }
            em.remove(articole);
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

    public List<Articole> findArticoleEntities() {
        return findArticoleEntities(true, -1, -1);
    }

    public List<Articole> findArticoleEntities(int maxResults, int firstResult) {
        return findArticoleEntities(false, maxResults, firstResult);
    }

    private List<Articole> findArticoleEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Articole as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Articole findArticole(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Articole.class, id);
        } finally {
            em.close();
        }
    }

    public int getArticoleCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Articole as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
