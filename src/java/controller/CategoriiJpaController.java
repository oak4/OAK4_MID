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
import entities.Categorii;
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
public class CategoriiJpaController implements Serializable {

    public CategoriiJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categorii categorii) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (categorii.getArticoleCollection() == null) {
            categorii.setArticoleCollection(new ArrayList<Articole>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Status statusId = categorii.getStatusId();
            if (statusId != null) {
                statusId = em.getReference(statusId.getClass(), statusId.getStatusId());
                categorii.setStatusId(statusId);
            }
            Collection<Articole> attachedArticoleCollection = new ArrayList<Articole>();
            for (Articole articoleCollectionArticoleToAttach : categorii.getArticoleCollection()) {
                articoleCollectionArticoleToAttach = em.getReference(articoleCollectionArticoleToAttach.getClass(), articoleCollectionArticoleToAttach.getArticolId());
                attachedArticoleCollection.add(articoleCollectionArticoleToAttach);
            }
            categorii.setArticoleCollection(attachedArticoleCollection);
            em.persist(categorii);
            if (statusId != null) {
                statusId.getCategoriiCollection().add(categorii);
                statusId = em.merge(statusId);
            }
            for (Articole articoleCollectionArticole : categorii.getArticoleCollection()) {
                Categorii oldCategorieIdOfArticoleCollectionArticole = articoleCollectionArticole.getCategorieId();
                articoleCollectionArticole.setCategorieId(categorii);
                articoleCollectionArticole = em.merge(articoleCollectionArticole);
                if (oldCategorieIdOfArticoleCollectionArticole != null) {
                    oldCategorieIdOfArticoleCollectionArticole.getArticoleCollection().remove(articoleCollectionArticole);
                    oldCategorieIdOfArticoleCollectionArticole = em.merge(oldCategorieIdOfArticoleCollectionArticole);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findCategorii(categorii.getCategorieId()) != null) {
                throw new PreexistingEntityException("Categorii " + categorii + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Categorii categorii) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Categorii persistentCategorii = em.find(Categorii.class, categorii.getCategorieId());
            Status statusIdOld = persistentCategorii.getStatusId();
            Status statusIdNew = categorii.getStatusId();
            Collection<Articole> articoleCollectionOld = persistentCategorii.getArticoleCollection();
            Collection<Articole> articoleCollectionNew = categorii.getArticoleCollection();
            List<String> illegalOrphanMessages = null;
            for (Articole articoleCollectionOldArticole : articoleCollectionOld) {
                if (!articoleCollectionNew.contains(articoleCollectionOldArticole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Articole " + articoleCollectionOldArticole + " since its categorieId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (statusIdNew != null) {
                statusIdNew = em.getReference(statusIdNew.getClass(), statusIdNew.getStatusId());
                categorii.setStatusId(statusIdNew);
            }
            Collection<Articole> attachedArticoleCollectionNew = new ArrayList<Articole>();
            for (Articole articoleCollectionNewArticoleToAttach : articoleCollectionNew) {
                articoleCollectionNewArticoleToAttach = em.getReference(articoleCollectionNewArticoleToAttach.getClass(), articoleCollectionNewArticoleToAttach.getArticolId());
                attachedArticoleCollectionNew.add(articoleCollectionNewArticoleToAttach);
            }
            articoleCollectionNew = attachedArticoleCollectionNew;
            categorii.setArticoleCollection(articoleCollectionNew);
            categorii = em.merge(categorii);
            if (statusIdOld != null && !statusIdOld.equals(statusIdNew)) {
                statusIdOld.getCategoriiCollection().remove(categorii);
                statusIdOld = em.merge(statusIdOld);
            }
            if (statusIdNew != null && !statusIdNew.equals(statusIdOld)) {
                statusIdNew.getCategoriiCollection().add(categorii);
                statusIdNew = em.merge(statusIdNew);
            }
            for (Articole articoleCollectionNewArticole : articoleCollectionNew) {
                if (!articoleCollectionOld.contains(articoleCollectionNewArticole)) {
                    Categorii oldCategorieIdOfArticoleCollectionNewArticole = articoleCollectionNewArticole.getCategorieId();
                    articoleCollectionNewArticole.setCategorieId(categorii);
                    articoleCollectionNewArticole = em.merge(articoleCollectionNewArticole);
                    if (oldCategorieIdOfArticoleCollectionNewArticole != null && !oldCategorieIdOfArticoleCollectionNewArticole.equals(categorii)) {
                        oldCategorieIdOfArticoleCollectionNewArticole.getArticoleCollection().remove(articoleCollectionNewArticole);
                        oldCategorieIdOfArticoleCollectionNewArticole = em.merge(oldCategorieIdOfArticoleCollectionNewArticole);
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
                Integer id = categorii.getCategorieId();
                if (findCategorii(id) == null) {
                    throw new NonexistentEntityException("The categorii with id " + id + " no longer exists.");
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
            Categorii categorii;
            try {
                categorii = em.getReference(Categorii.class, id);
                categorii.getCategorieId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categorii with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Articole> articoleCollectionOrphanCheck = categorii.getArticoleCollection();
            for (Articole articoleCollectionOrphanCheckArticole : articoleCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Categorii (" + categorii + ") cannot be destroyed since the Articole " + articoleCollectionOrphanCheckArticole + " in its articoleCollection field has a non-nullable categorieId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Status statusId = categorii.getStatusId();
            if (statusId != null) {
                statusId.getCategoriiCollection().remove(categorii);
                statusId = em.merge(statusId);
            }
            em.remove(categorii);
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

    public List<Categorii> findCategoriiEntities() {
        return findCategoriiEntities(true, -1, -1);
    }

    public List<Categorii> findCategoriiEntities(int maxResults, int firstResult) {
        return findCategoriiEntities(false, maxResults, firstResult);
    }

    private List<Categorii> findCategoriiEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Categorii as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Categorii findCategorii(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categorii.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoriiCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Categorii as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
