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
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import entities.Status;
import entities.Articole;
import entities.Surse;
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
public class SurseJpaController implements Serializable {

    public SurseJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Surse surse) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (surse.getArticoleCollection() == null) {
            surse.setArticoleCollection(new ArrayList<Articole>());
        }
        if (surse.getArticoleCollection1() == null) {
            surse.setArticoleCollection1(new ArrayList<Articole>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Status statusId = surse.getStatusId();
            if (statusId != null) {
                statusId = em.getReference(statusId.getClass(), statusId.getStatusId());
                surse.setStatusId(statusId);
            }
            Collection<Articole> attachedArticoleCollection = new ArrayList<Articole>();
            for (Articole articoleCollectionArticoleToAttach : surse.getArticoleCollection()) {
                articoleCollectionArticoleToAttach = em.getReference(articoleCollectionArticoleToAttach.getClass(), articoleCollectionArticoleToAttach.getArticolId());
                attachedArticoleCollection.add(articoleCollectionArticoleToAttach);
            }
            surse.setArticoleCollection(attachedArticoleCollection);
            Collection<Articole> attachedArticoleCollection1 = new ArrayList<Articole>();
            for (Articole articoleCollection1ArticoleToAttach : surse.getArticoleCollection1()) {
                articoleCollection1ArticoleToAttach = em.getReference(articoleCollection1ArticoleToAttach.getClass(), articoleCollection1ArticoleToAttach.getArticolId());
                attachedArticoleCollection1.add(articoleCollection1ArticoleToAttach);
            }
            surse.setArticoleCollection1(attachedArticoleCollection1);
            em.persist(surse);
            if (statusId != null) {
                statusId.getSurseCollection().add(surse);
                statusId = em.merge(statusId);
            }
            for (Articole articoleCollectionArticole : surse.getArticoleCollection()) {
                articoleCollectionArticole.getSurseCollection().add(surse);
                articoleCollectionArticole = em.merge(articoleCollectionArticole);
            }
            for (Articole articoleCollection1Articole : surse.getArticoleCollection1()) {
                Surse oldSursaIdOfArticoleCollection1Articole = articoleCollection1Articole.getSursaId();
                articoleCollection1Articole.setSursaId(surse);
                articoleCollection1Articole = em.merge(articoleCollection1Articole);
                if (oldSursaIdOfArticoleCollection1Articole != null) {
                    oldSursaIdOfArticoleCollection1Articole.getArticoleCollection1().remove(articoleCollection1Articole);
                    oldSursaIdOfArticoleCollection1Articole = em.merge(oldSursaIdOfArticoleCollection1Articole);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findSurse(surse.getSursaId()) != null) {
                throw new PreexistingEntityException("Surse " + surse + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Surse surse) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Surse persistentSurse = em.find(Surse.class, surse.getSursaId());
            Status statusIdOld = persistentSurse.getStatusId();
            Status statusIdNew = surse.getStatusId();
            Collection<Articole> articoleCollectionOld = persistentSurse.getArticoleCollection();
            Collection<Articole> articoleCollectionNew = surse.getArticoleCollection();
            Collection<Articole> articoleCollection1Old = persistentSurse.getArticoleCollection1();
            Collection<Articole> articoleCollection1New = surse.getArticoleCollection1();
            List<String> illegalOrphanMessages = null;
            for (Articole articoleCollection1OldArticole : articoleCollection1Old) {
                if (!articoleCollection1New.contains(articoleCollection1OldArticole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Articole " + articoleCollection1OldArticole + " since its sursaId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (statusIdNew != null) {
                statusIdNew = em.getReference(statusIdNew.getClass(), statusIdNew.getStatusId());
                surse.setStatusId(statusIdNew);
            }
            Collection<Articole> attachedArticoleCollectionNew = new ArrayList<Articole>();
            for (Articole articoleCollectionNewArticoleToAttach : articoleCollectionNew) {
                articoleCollectionNewArticoleToAttach = em.getReference(articoleCollectionNewArticoleToAttach.getClass(), articoleCollectionNewArticoleToAttach.getArticolId());
                attachedArticoleCollectionNew.add(articoleCollectionNewArticoleToAttach);
            }
            articoleCollectionNew = attachedArticoleCollectionNew;
            surse.setArticoleCollection(articoleCollectionNew);
            Collection<Articole> attachedArticoleCollection1New = new ArrayList<Articole>();
            for (Articole articoleCollection1NewArticoleToAttach : articoleCollection1New) {
                articoleCollection1NewArticoleToAttach = em.getReference(articoleCollection1NewArticoleToAttach.getClass(), articoleCollection1NewArticoleToAttach.getArticolId());
                attachedArticoleCollection1New.add(articoleCollection1NewArticoleToAttach);
            }
            articoleCollection1New = attachedArticoleCollection1New;
            surse.setArticoleCollection1(articoleCollection1New);
            surse = em.merge(surse);
            if (statusIdOld != null && !statusIdOld.equals(statusIdNew)) {
                statusIdOld.getSurseCollection().remove(surse);
                statusIdOld = em.merge(statusIdOld);
            }
            if (statusIdNew != null && !statusIdNew.equals(statusIdOld)) {
                statusIdNew.getSurseCollection().add(surse);
                statusIdNew = em.merge(statusIdNew);
            }
            for (Articole articoleCollectionOldArticole : articoleCollectionOld) {
                if (!articoleCollectionNew.contains(articoleCollectionOldArticole)) {
                    articoleCollectionOldArticole.getSurseCollection().remove(surse);
                    articoleCollectionOldArticole = em.merge(articoleCollectionOldArticole);
                }
            }
            for (Articole articoleCollectionNewArticole : articoleCollectionNew) {
                if (!articoleCollectionOld.contains(articoleCollectionNewArticole)) {
                    articoleCollectionNewArticole.getSurseCollection().add(surse);
                    articoleCollectionNewArticole = em.merge(articoleCollectionNewArticole);
                }
            }
            for (Articole articoleCollection1NewArticole : articoleCollection1New) {
                if (!articoleCollection1Old.contains(articoleCollection1NewArticole)) {
                    Surse oldSursaIdOfArticoleCollection1NewArticole = articoleCollection1NewArticole.getSursaId();
                    articoleCollection1NewArticole.setSursaId(surse);
                    articoleCollection1NewArticole = em.merge(articoleCollection1NewArticole);
                    if (oldSursaIdOfArticoleCollection1NewArticole != null && !oldSursaIdOfArticoleCollection1NewArticole.equals(surse)) {
                        oldSursaIdOfArticoleCollection1NewArticole.getArticoleCollection1().remove(articoleCollection1NewArticole);
                        oldSursaIdOfArticoleCollection1NewArticole = em.merge(oldSursaIdOfArticoleCollection1NewArticole);
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
                Integer id = surse.getSursaId();
                if (findSurse(id) == null) {
                    throw new NonexistentEntityException("The surse with id " + id + " no longer exists.");
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
            Surse surse;
            try {
                surse = em.getReference(Surse.class, id);
                surse.getSursaId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The surse with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Articole> articoleCollection1OrphanCheck = surse.getArticoleCollection1();
            for (Articole articoleCollection1OrphanCheckArticole : articoleCollection1OrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Surse (" + surse + ") cannot be destroyed since the Articole " + articoleCollection1OrphanCheckArticole + " in its articoleCollection1 field has a non-nullable sursaId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Status statusId = surse.getStatusId();
            if (statusId != null) {
                statusId.getSurseCollection().remove(surse);
                statusId = em.merge(statusId);
            }
            Collection<Articole> articoleCollection = surse.getArticoleCollection();
            for (Articole articoleCollectionArticole : articoleCollection) {
                articoleCollectionArticole.getSurseCollection().remove(surse);
                articoleCollectionArticole = em.merge(articoleCollectionArticole);
            }
            em.remove(surse);
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

    public List<Surse> findSurseEntities() {
        return findSurseEntities(true, -1, -1);
    }

    public List<Surse> findSurseEntities(int maxResults, int firstResult) {
        return findSurseEntities(false, maxResults, firstResult);
    }

    private List<Surse> findSurseEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Surse as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Surse findSurse(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Surse.class, id);
        } finally {
            em.close();
        }
    }

    public int getSurseCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Surse as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
