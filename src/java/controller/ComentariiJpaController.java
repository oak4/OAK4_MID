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
import entities.Status;
import entities.Articole;
import entities.Comentarii;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author oracle
 */
public class ComentariiJpaController implements Serializable {

    public ComentariiJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Comentarii comentarii) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Status statusId = comentarii.getStatusId();
            if (statusId != null) {
                statusId = em.getReference(statusId.getClass(), statusId.getStatusId());
                comentarii.setStatusId(statusId);
            }
            Articole articolId = comentarii.getArticolId();
            if (articolId != null) {
                articolId = em.getReference(articolId.getClass(), articolId.getArticolId());
                comentarii.setArticolId(articolId);
            }
            em.persist(comentarii);
            if (statusId != null) {
                statusId.getComentariiCollection().add(comentarii);
                statusId = em.merge(statusId);
            }
            if (articolId != null) {
                articolId.getComentariiCollection().add(comentarii);
                articolId = em.merge(articolId);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findComentarii(comentarii.getComentariuId()) != null) {
                throw new PreexistingEntityException("Comentarii " + comentarii + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Comentarii comentarii) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Comentarii persistentComentarii = em.find(Comentarii.class, comentarii.getComentariuId());
            Status statusIdOld = persistentComentarii.getStatusId();
            Status statusIdNew = comentarii.getStatusId();
            Articole articolIdOld = persistentComentarii.getArticolId();
            Articole articolIdNew = comentarii.getArticolId();
            if (statusIdNew != null) {
                statusIdNew = em.getReference(statusIdNew.getClass(), statusIdNew.getStatusId());
                comentarii.setStatusId(statusIdNew);
            }
            if (articolIdNew != null) {
                articolIdNew = em.getReference(articolIdNew.getClass(), articolIdNew.getArticolId());
                comentarii.setArticolId(articolIdNew);
            }
            comentarii = em.merge(comentarii);
            if (statusIdOld != null && !statusIdOld.equals(statusIdNew)) {
                statusIdOld.getComentariiCollection().remove(comentarii);
                statusIdOld = em.merge(statusIdOld);
            }
            if (statusIdNew != null && !statusIdNew.equals(statusIdOld)) {
                statusIdNew.getComentariiCollection().add(comentarii);
                statusIdNew = em.merge(statusIdNew);
            }
            if (articolIdOld != null && !articolIdOld.equals(articolIdNew)) {
                articolIdOld.getComentariiCollection().remove(comentarii);
                articolIdOld = em.merge(articolIdOld);
            }
            if (articolIdNew != null && !articolIdNew.equals(articolIdOld)) {
                articolIdNew.getComentariiCollection().add(comentarii);
                articolIdNew = em.merge(articolIdNew);
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
                Integer id = comentarii.getComentariuId();
                if (findComentarii(id) == null) {
                    throw new NonexistentEntityException("The comentarii with id " + id + " no longer exists.");
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
            Comentarii comentarii;
            try {
                comentarii = em.getReference(Comentarii.class, id);
                comentarii.getComentariuId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The comentarii with id " + id + " no longer exists.", enfe);
            }
            Status statusId = comentarii.getStatusId();
            if (statusId != null) {
                statusId.getComentariiCollection().remove(comentarii);
                statusId = em.merge(statusId);
            }
            Articole articolId = comentarii.getArticolId();
            if (articolId != null) {
                articolId.getComentariiCollection().remove(comentarii);
                articolId = em.merge(articolId);
            }
            em.remove(comentarii);
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

    public List<Comentarii> findComentariiEntities() {
        return findComentariiEntities(true, -1, -1);
    }

    public List<Comentarii> findComentariiEntities(int maxResults, int firstResult) {
        return findComentariiEntities(false, maxResults, firstResult);
    }

    private List<Comentarii> findComentariiEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Comentarii as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Comentarii findComentarii(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Comentarii.class, id);
        } finally {
            em.close();
        }
    }

    public int getComentariiCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Comentarii as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
