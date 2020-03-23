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
import entities.Autori;
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
public class AutoriJpaController implements Serializable {

    public AutoriJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Autori autori) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (autori.getArticoleCollection() == null) {
            autori.setArticoleCollection(new ArrayList<Articole>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Status statusId = autori.getStatusId();
            if (statusId != null) {
                statusId = em.getReference(statusId.getClass(), statusId.getStatusId());
                autori.setStatusId(statusId);
            }
            Collection<Articole> attachedArticoleCollection = new ArrayList<Articole>();
            for (Articole articoleCollectionArticoleToAttach : autori.getArticoleCollection()) {
                articoleCollectionArticoleToAttach = em.getReference(articoleCollectionArticoleToAttach.getClass(), articoleCollectionArticoleToAttach.getArticolId());
                attachedArticoleCollection.add(articoleCollectionArticoleToAttach);
            }
            autori.setArticoleCollection(attachedArticoleCollection);
            em.persist(autori);
            if (statusId != null) {
                statusId.getAutoriCollection().add(autori);
                statusId = em.merge(statusId);
            }
            for (Articole articoleCollectionArticole : autori.getArticoleCollection()) {
                Autori oldAutorIdOfArticoleCollectionArticole = articoleCollectionArticole.getAutorId();
                articoleCollectionArticole.setAutorId(autori);
                articoleCollectionArticole = em.merge(articoleCollectionArticole);
                if (oldAutorIdOfArticoleCollectionArticole != null) {
                    oldAutorIdOfArticoleCollectionArticole.getArticoleCollection().remove(articoleCollectionArticole);
                    oldAutorIdOfArticoleCollectionArticole = em.merge(oldAutorIdOfArticoleCollectionArticole);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findAutori(autori.getAutorId()) != null) {
                throw new PreexistingEntityException("Autori " + autori + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Autori autori) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Autori persistentAutori = em.find(Autori.class, autori.getAutorId());
            Status statusIdOld = persistentAutori.getStatusId();
            Status statusIdNew = autori.getStatusId();
            Collection<Articole> articoleCollectionOld = persistentAutori.getArticoleCollection();
            Collection<Articole> articoleCollectionNew = autori.getArticoleCollection();
            List<String> illegalOrphanMessages = null;
            for (Articole articoleCollectionOldArticole : articoleCollectionOld) {
                if (!articoleCollectionNew.contains(articoleCollectionOldArticole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Articole " + articoleCollectionOldArticole + " since its autorId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (statusIdNew != null) {
                statusIdNew = em.getReference(statusIdNew.getClass(), statusIdNew.getStatusId());
                autori.setStatusId(statusIdNew);
            }
            Collection<Articole> attachedArticoleCollectionNew = new ArrayList<Articole>();
            for (Articole articoleCollectionNewArticoleToAttach : articoleCollectionNew) {
                articoleCollectionNewArticoleToAttach = em.getReference(articoleCollectionNewArticoleToAttach.getClass(), articoleCollectionNewArticoleToAttach.getArticolId());
                attachedArticoleCollectionNew.add(articoleCollectionNewArticoleToAttach);
            }
            articoleCollectionNew = attachedArticoleCollectionNew;
            autori.setArticoleCollection(articoleCollectionNew);
            autori = em.merge(autori);
            if (statusIdOld != null && !statusIdOld.equals(statusIdNew)) {
                statusIdOld.getAutoriCollection().remove(autori);
                statusIdOld = em.merge(statusIdOld);
            }
            if (statusIdNew != null && !statusIdNew.equals(statusIdOld)) {
                statusIdNew.getAutoriCollection().add(autori);
                statusIdNew = em.merge(statusIdNew);
            }
            for (Articole articoleCollectionNewArticole : articoleCollectionNew) {
                if (!articoleCollectionOld.contains(articoleCollectionNewArticole)) {
                    Autori oldAutorIdOfArticoleCollectionNewArticole = articoleCollectionNewArticole.getAutorId();
                    articoleCollectionNewArticole.setAutorId(autori);
                    articoleCollectionNewArticole = em.merge(articoleCollectionNewArticole);
                    if (oldAutorIdOfArticoleCollectionNewArticole != null && !oldAutorIdOfArticoleCollectionNewArticole.equals(autori)) {
                        oldAutorIdOfArticoleCollectionNewArticole.getArticoleCollection().remove(articoleCollectionNewArticole);
                        oldAutorIdOfArticoleCollectionNewArticole = em.merge(oldAutorIdOfArticoleCollectionNewArticole);
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
                Integer id = autori.getAutorId();
                if (findAutori(id) == null) {
                    throw new NonexistentEntityException("The autori with id " + id + " no longer exists.");
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
            Autori autori;
            try {
                autori = em.getReference(Autori.class, id);
                autori.getAutorId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The autori with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Articole> articoleCollectionOrphanCheck = autori.getArticoleCollection();
            for (Articole articoleCollectionOrphanCheckArticole : articoleCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Autori (" + autori + ") cannot be destroyed since the Articole " + articoleCollectionOrphanCheckArticole + " in its articoleCollection field has a non-nullable autorId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Status statusId = autori.getStatusId();
            if (statusId != null) {
                statusId.getAutoriCollection().remove(autori);
                statusId = em.merge(statusId);
            }
            em.remove(autori);
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

    public List<Autori> findAutoriEntities() {
        return findAutoriEntities(true, -1, -1);
    }

    public List<Autori> findAutoriEntities(int maxResults, int firstResult) {
        return findAutoriEntities(false, maxResults, firstResult);
    }

    private List<Autori> findAutoriEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Autori as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Autori findAutori(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Autori.class, id);
        } finally {
            em.close();
        }
    }

    public int getAutoriCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Autori as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
