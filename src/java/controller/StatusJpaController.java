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
import entities.Surse;
import java.util.ArrayList;
import java.util.Collection;
import entities.Categorii;
import entities.Comentarii;
import entities.Articole;
import entities.Autori;
import entities.Status;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author oracle
 */
public class StatusJpaController implements Serializable {

    public StatusJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Status status) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (status.getSurseCollection() == null) {
            status.setSurseCollection(new ArrayList<Surse>());
        }
        if (status.getCategoriiCollection() == null) {
            status.setCategoriiCollection(new ArrayList<Categorii>());
        }
        if (status.getComentariiCollection() == null) {
            status.setComentariiCollection(new ArrayList<Comentarii>());
        }
        if (status.getArticoleCollection() == null) {
            status.setArticoleCollection(new ArrayList<Articole>());
        }
        if (status.getAutoriCollection() == null) {
            status.setAutoriCollection(new ArrayList<Autori>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Collection<Surse> attachedSurseCollection = new ArrayList<Surse>();
            for (Surse surseCollectionSurseToAttach : status.getSurseCollection()) {
                surseCollectionSurseToAttach = em.getReference(surseCollectionSurseToAttach.getClass(), surseCollectionSurseToAttach.getSursaId());
                attachedSurseCollection.add(surseCollectionSurseToAttach);
            }
            status.setSurseCollection(attachedSurseCollection);
            Collection<Categorii> attachedCategoriiCollection = new ArrayList<Categorii>();
            for (Categorii categoriiCollectionCategoriiToAttach : status.getCategoriiCollection()) {
                categoriiCollectionCategoriiToAttach = em.getReference(categoriiCollectionCategoriiToAttach.getClass(), categoriiCollectionCategoriiToAttach.getCategorieId());
                attachedCategoriiCollection.add(categoriiCollectionCategoriiToAttach);
            }
            status.setCategoriiCollection(attachedCategoriiCollection);
            Collection<Comentarii> attachedComentariiCollection = new ArrayList<Comentarii>();
            for (Comentarii comentariiCollectionComentariiToAttach : status.getComentariiCollection()) {
                comentariiCollectionComentariiToAttach = em.getReference(comentariiCollectionComentariiToAttach.getClass(), comentariiCollectionComentariiToAttach.getComentariuId());
                attachedComentariiCollection.add(comentariiCollectionComentariiToAttach);
            }
            status.setComentariiCollection(attachedComentariiCollection);
            Collection<Articole> attachedArticoleCollection = new ArrayList<Articole>();
            for (Articole articoleCollectionArticoleToAttach : status.getArticoleCollection()) {
                articoleCollectionArticoleToAttach = em.getReference(articoleCollectionArticoleToAttach.getClass(), articoleCollectionArticoleToAttach.getArticolId());
                attachedArticoleCollection.add(articoleCollectionArticoleToAttach);
            }
            status.setArticoleCollection(attachedArticoleCollection);
            Collection<Autori> attachedAutoriCollection = new ArrayList<Autori>();
            for (Autori autoriCollectionAutoriToAttach : status.getAutoriCollection()) {
                autoriCollectionAutoriToAttach = em.getReference(autoriCollectionAutoriToAttach.getClass(), autoriCollectionAutoriToAttach.getAutorId());
                attachedAutoriCollection.add(autoriCollectionAutoriToAttach);
            }
            status.setAutoriCollection(attachedAutoriCollection);
            em.persist(status);
            for (Surse surseCollectionSurse : status.getSurseCollection()) {
                Status oldStatusIdOfSurseCollectionSurse = surseCollectionSurse.getStatusId();
                surseCollectionSurse.setStatusId(status);
                surseCollectionSurse = em.merge(surseCollectionSurse);
                if (oldStatusIdOfSurseCollectionSurse != null) {
                    oldStatusIdOfSurseCollectionSurse.getSurseCollection().remove(surseCollectionSurse);
                    oldStatusIdOfSurseCollectionSurse = em.merge(oldStatusIdOfSurseCollectionSurse);
                }
            }
            for (Categorii categoriiCollectionCategorii : status.getCategoriiCollection()) {
                Status oldStatusIdOfCategoriiCollectionCategorii = categoriiCollectionCategorii.getStatusId();
                categoriiCollectionCategorii.setStatusId(status);
                categoriiCollectionCategorii = em.merge(categoriiCollectionCategorii);
                if (oldStatusIdOfCategoriiCollectionCategorii != null) {
                    oldStatusIdOfCategoriiCollectionCategorii.getCategoriiCollection().remove(categoriiCollectionCategorii);
                    oldStatusIdOfCategoriiCollectionCategorii = em.merge(oldStatusIdOfCategoriiCollectionCategorii);
                }
            }
            for (Comentarii comentariiCollectionComentarii : status.getComentariiCollection()) {
                Status oldStatusIdOfComentariiCollectionComentarii = comentariiCollectionComentarii.getStatusId();
                comentariiCollectionComentarii.setStatusId(status);
                comentariiCollectionComentarii = em.merge(comentariiCollectionComentarii);
                if (oldStatusIdOfComentariiCollectionComentarii != null) {
                    oldStatusIdOfComentariiCollectionComentarii.getComentariiCollection().remove(comentariiCollectionComentarii);
                    oldStatusIdOfComentariiCollectionComentarii = em.merge(oldStatusIdOfComentariiCollectionComentarii);
                }
            }
            for (Articole articoleCollectionArticole : status.getArticoleCollection()) {
                Status oldStatusIdOfArticoleCollectionArticole = articoleCollectionArticole.getStatusId();
                articoleCollectionArticole.setStatusId(status);
                articoleCollectionArticole = em.merge(articoleCollectionArticole);
                if (oldStatusIdOfArticoleCollectionArticole != null) {
                    oldStatusIdOfArticoleCollectionArticole.getArticoleCollection().remove(articoleCollectionArticole);
                    oldStatusIdOfArticoleCollectionArticole = em.merge(oldStatusIdOfArticoleCollectionArticole);
                }
            }
            for (Autori autoriCollectionAutori : status.getAutoriCollection()) {
                Status oldStatusIdOfAutoriCollectionAutori = autoriCollectionAutori.getStatusId();
                autoriCollectionAutori.setStatusId(status);
                autoriCollectionAutori = em.merge(autoriCollectionAutori);
                if (oldStatusIdOfAutoriCollectionAutori != null) {
                    oldStatusIdOfAutoriCollectionAutori.getAutoriCollection().remove(autoriCollectionAutori);
                    oldStatusIdOfAutoriCollectionAutori = em.merge(oldStatusIdOfAutoriCollectionAutori);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findStatus(status.getStatusId()) != null) {
                throw new PreexistingEntityException("Status " + status + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Status status) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Status persistentStatus = em.find(Status.class, status.getStatusId());
            Collection<Surse> surseCollectionOld = persistentStatus.getSurseCollection();
            Collection<Surse> surseCollectionNew = status.getSurseCollection();
            Collection<Categorii> categoriiCollectionOld = persistentStatus.getCategoriiCollection();
            Collection<Categorii> categoriiCollectionNew = status.getCategoriiCollection();
            Collection<Comentarii> comentariiCollectionOld = persistentStatus.getComentariiCollection();
            Collection<Comentarii> comentariiCollectionNew = status.getComentariiCollection();
            Collection<Articole> articoleCollectionOld = persistentStatus.getArticoleCollection();
            Collection<Articole> articoleCollectionNew = status.getArticoleCollection();
            Collection<Autori> autoriCollectionOld = persistentStatus.getAutoriCollection();
            Collection<Autori> autoriCollectionNew = status.getAutoriCollection();
            List<String> illegalOrphanMessages = null;
            for (Surse surseCollectionOldSurse : surseCollectionOld) {
                if (!surseCollectionNew.contains(surseCollectionOldSurse)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Surse " + surseCollectionOldSurse + " since its statusId field is not nullable.");
                }
            }
            for (Comentarii comentariiCollectionOldComentarii : comentariiCollectionOld) {
                if (!comentariiCollectionNew.contains(comentariiCollectionOldComentarii)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Comentarii " + comentariiCollectionOldComentarii + " since its statusId field is not nullable.");
                }
            }
            for (Articole articoleCollectionOldArticole : articoleCollectionOld) {
                if (!articoleCollectionNew.contains(articoleCollectionOldArticole)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Articole " + articoleCollectionOldArticole + " since its statusId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Surse> attachedSurseCollectionNew = new ArrayList<Surse>();
            for (Surse surseCollectionNewSurseToAttach : surseCollectionNew) {
                surseCollectionNewSurseToAttach = em.getReference(surseCollectionNewSurseToAttach.getClass(), surseCollectionNewSurseToAttach.getSursaId());
                attachedSurseCollectionNew.add(surseCollectionNewSurseToAttach);
            }
            surseCollectionNew = attachedSurseCollectionNew;
            status.setSurseCollection(surseCollectionNew);
            Collection<Categorii> attachedCategoriiCollectionNew = new ArrayList<Categorii>();
            for (Categorii categoriiCollectionNewCategoriiToAttach : categoriiCollectionNew) {
                categoriiCollectionNewCategoriiToAttach = em.getReference(categoriiCollectionNewCategoriiToAttach.getClass(), categoriiCollectionNewCategoriiToAttach.getCategorieId());
                attachedCategoriiCollectionNew.add(categoriiCollectionNewCategoriiToAttach);
            }
            categoriiCollectionNew = attachedCategoriiCollectionNew;
            status.setCategoriiCollection(categoriiCollectionNew);
            Collection<Comentarii> attachedComentariiCollectionNew = new ArrayList<Comentarii>();
            for (Comentarii comentariiCollectionNewComentariiToAttach : comentariiCollectionNew) {
                comentariiCollectionNewComentariiToAttach = em.getReference(comentariiCollectionNewComentariiToAttach.getClass(), comentariiCollectionNewComentariiToAttach.getComentariuId());
                attachedComentariiCollectionNew.add(comentariiCollectionNewComentariiToAttach);
            }
            comentariiCollectionNew = attachedComentariiCollectionNew;
            status.setComentariiCollection(comentariiCollectionNew);
            Collection<Articole> attachedArticoleCollectionNew = new ArrayList<Articole>();
            for (Articole articoleCollectionNewArticoleToAttach : articoleCollectionNew) {
                articoleCollectionNewArticoleToAttach = em.getReference(articoleCollectionNewArticoleToAttach.getClass(), articoleCollectionNewArticoleToAttach.getArticolId());
                attachedArticoleCollectionNew.add(articoleCollectionNewArticoleToAttach);
            }
            articoleCollectionNew = attachedArticoleCollectionNew;
            status.setArticoleCollection(articoleCollectionNew);
            Collection<Autori> attachedAutoriCollectionNew = new ArrayList<Autori>();
            for (Autori autoriCollectionNewAutoriToAttach : autoriCollectionNew) {
                autoriCollectionNewAutoriToAttach = em.getReference(autoriCollectionNewAutoriToAttach.getClass(), autoriCollectionNewAutoriToAttach.getAutorId());
                attachedAutoriCollectionNew.add(autoriCollectionNewAutoriToAttach);
            }
            autoriCollectionNew = attachedAutoriCollectionNew;
            status.setAutoriCollection(autoriCollectionNew);
            status = em.merge(status);
            for (Surse surseCollectionNewSurse : surseCollectionNew) {
                if (!surseCollectionOld.contains(surseCollectionNewSurse)) {
                    Status oldStatusIdOfSurseCollectionNewSurse = surseCollectionNewSurse.getStatusId();
                    surseCollectionNewSurse.setStatusId(status);
                    surseCollectionNewSurse = em.merge(surseCollectionNewSurse);
                    if (oldStatusIdOfSurseCollectionNewSurse != null && !oldStatusIdOfSurseCollectionNewSurse.equals(status)) {
                        oldStatusIdOfSurseCollectionNewSurse.getSurseCollection().remove(surseCollectionNewSurse);
                        oldStatusIdOfSurseCollectionNewSurse = em.merge(oldStatusIdOfSurseCollectionNewSurse);
                    }
                }
            }
            for (Categorii categoriiCollectionOldCategorii : categoriiCollectionOld) {
                if (!categoriiCollectionNew.contains(categoriiCollectionOldCategorii)) {
                    categoriiCollectionOldCategorii.setStatusId(null);
                    categoriiCollectionOldCategorii = em.merge(categoriiCollectionOldCategorii);
                }
            }
            for (Categorii categoriiCollectionNewCategorii : categoriiCollectionNew) {
                if (!categoriiCollectionOld.contains(categoriiCollectionNewCategorii)) {
                    Status oldStatusIdOfCategoriiCollectionNewCategorii = categoriiCollectionNewCategorii.getStatusId();
                    categoriiCollectionNewCategorii.setStatusId(status);
                    categoriiCollectionNewCategorii = em.merge(categoriiCollectionNewCategorii);
                    if (oldStatusIdOfCategoriiCollectionNewCategorii != null && !oldStatusIdOfCategoriiCollectionNewCategorii.equals(status)) {
                        oldStatusIdOfCategoriiCollectionNewCategorii.getCategoriiCollection().remove(categoriiCollectionNewCategorii);
                        oldStatusIdOfCategoriiCollectionNewCategorii = em.merge(oldStatusIdOfCategoriiCollectionNewCategorii);
                    }
                }
            }
            for (Comentarii comentariiCollectionNewComentarii : comentariiCollectionNew) {
                if (!comentariiCollectionOld.contains(comentariiCollectionNewComentarii)) {
                    Status oldStatusIdOfComentariiCollectionNewComentarii = comentariiCollectionNewComentarii.getStatusId();
                    comentariiCollectionNewComentarii.setStatusId(status);
                    comentariiCollectionNewComentarii = em.merge(comentariiCollectionNewComentarii);
                    if (oldStatusIdOfComentariiCollectionNewComentarii != null && !oldStatusIdOfComentariiCollectionNewComentarii.equals(status)) {
                        oldStatusIdOfComentariiCollectionNewComentarii.getComentariiCollection().remove(comentariiCollectionNewComentarii);
                        oldStatusIdOfComentariiCollectionNewComentarii = em.merge(oldStatusIdOfComentariiCollectionNewComentarii);
                    }
                }
            }
            for (Articole articoleCollectionNewArticole : articoleCollectionNew) {
                if (!articoleCollectionOld.contains(articoleCollectionNewArticole)) {
                    Status oldStatusIdOfArticoleCollectionNewArticole = articoleCollectionNewArticole.getStatusId();
                    articoleCollectionNewArticole.setStatusId(status);
                    articoleCollectionNewArticole = em.merge(articoleCollectionNewArticole);
                    if (oldStatusIdOfArticoleCollectionNewArticole != null && !oldStatusIdOfArticoleCollectionNewArticole.equals(status)) {
                        oldStatusIdOfArticoleCollectionNewArticole.getArticoleCollection().remove(articoleCollectionNewArticole);
                        oldStatusIdOfArticoleCollectionNewArticole = em.merge(oldStatusIdOfArticoleCollectionNewArticole);
                    }
                }
            }
            for (Autori autoriCollectionOldAutori : autoriCollectionOld) {
                if (!autoriCollectionNew.contains(autoriCollectionOldAutori)) {
                    autoriCollectionOldAutori.setStatusId(null);
                    autoriCollectionOldAutori = em.merge(autoriCollectionOldAutori);
                }
            }
            for (Autori autoriCollectionNewAutori : autoriCollectionNew) {
                if (!autoriCollectionOld.contains(autoriCollectionNewAutori)) {
                    Status oldStatusIdOfAutoriCollectionNewAutori = autoriCollectionNewAutori.getStatusId();
                    autoriCollectionNewAutori.setStatusId(status);
                    autoriCollectionNewAutori = em.merge(autoriCollectionNewAutori);
                    if (oldStatusIdOfAutoriCollectionNewAutori != null && !oldStatusIdOfAutoriCollectionNewAutori.equals(status)) {
                        oldStatusIdOfAutoriCollectionNewAutori.getAutoriCollection().remove(autoriCollectionNewAutori);
                        oldStatusIdOfAutoriCollectionNewAutori = em.merge(oldStatusIdOfAutoriCollectionNewAutori);
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
                Integer id = status.getStatusId();
                if (findStatus(id) == null) {
                    throw new NonexistentEntityException("The status with id " + id + " no longer exists.");
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
            Status status;
            try {
                status = em.getReference(Status.class, id);
                status.getStatusId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The status with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Surse> surseCollectionOrphanCheck = status.getSurseCollection();
            for (Surse surseCollectionOrphanCheckSurse : surseCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Status (" + status + ") cannot be destroyed since the Surse " + surseCollectionOrphanCheckSurse + " in its surseCollection field has a non-nullable statusId field.");
            }
            Collection<Comentarii> comentariiCollectionOrphanCheck = status.getComentariiCollection();
            for (Comentarii comentariiCollectionOrphanCheckComentarii : comentariiCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Status (" + status + ") cannot be destroyed since the Comentarii " + comentariiCollectionOrphanCheckComentarii + " in its comentariiCollection field has a non-nullable statusId field.");
            }
            Collection<Articole> articoleCollectionOrphanCheck = status.getArticoleCollection();
            for (Articole articoleCollectionOrphanCheckArticole : articoleCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Status (" + status + ") cannot be destroyed since the Articole " + articoleCollectionOrphanCheckArticole + " in its articoleCollection field has a non-nullable statusId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Categorii> categoriiCollection = status.getCategoriiCollection();
            for (Categorii categoriiCollectionCategorii : categoriiCollection) {
                categoriiCollectionCategorii.setStatusId(null);
                categoriiCollectionCategorii = em.merge(categoriiCollectionCategorii);
            }
            Collection<Autori> autoriCollection = status.getAutoriCollection();
            for (Autori autoriCollectionAutori : autoriCollection) {
                autoriCollectionAutori.setStatusId(null);
                autoriCollectionAutori = em.merge(autoriCollectionAutori);
            }
            em.remove(status);
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

    public List<Status> findStatusEntities() {
        return findStatusEntities(true, -1, -1);
    }

    public List<Status> findStatusEntities(int maxResults, int firstResult) {
        return findStatusEntities(false, maxResults, firstResult);
    }

    private List<Status> findStatusEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Status as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Status findStatus(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Status.class, id);
        } finally {
            em.close();
        }
    }

    public int getStatusCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Status as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
