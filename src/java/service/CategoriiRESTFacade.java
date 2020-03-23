/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import controller.CategoriiJpaController;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;
import javax.naming.InitialContext;
import entities.Categorii;
import java.net.URI;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author oracle
 */
@Path("entities.categorii")
public class CategoriiRESTFacade {

    private EntityManagerFactory getEntityManagerFactory() throws NamingException {
        return (EntityManagerFactory) new InitialContext().lookup("java:comp/env/persistence-factory");
    }

    private CategoriiJpaController getJpaController() {
        try {
            UserTransaction utx = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
            return new CategoriiJpaController(utx, getEntityManagerFactory());
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public CategoriiRESTFacade() {
    }

    @POST
    @Consumes({"application/xml", "application/json"})
    public Response create(Categorii entity) {
        try {
            getJpaController().create(entity);
            return Response.created(URI.create(entity.getCategorieId().toString())).build();
        } catch (Exception ex) {
            return Response.notModified(ex.getMessage()).build();
        }
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    public Response edit(Categorii entity) {
        try {
            getJpaController().edit(entity);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.notModified(ex.getMessage()).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response remove(@PathParam("id") Integer id) {
        try {
            getJpaController().destroy(id);
            return Response.ok().build();
        } catch (Exception ex) {
            return Response.notModified(ex.getMessage()).build();
        }
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Categorii find(@PathParam("id") Integer id) {
        return getJpaController().findCategorii(id);
    }

    @GET
    @Produces({"application/xml", "application/json"})
    public List<Categorii> findAll() {
        return getJpaController().findCategoriiEntities();
    }

    @GET
    @Path("{max}/{first}")
    @Produces({"application/xml", "application/json"})
    public List<Categorii> findRange(@PathParam("max") Integer max, @PathParam("first") Integer first) {
        return getJpaController().findCategoriiEntities(max, first);
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String count() {
        return String.valueOf(getJpaController().getCategoriiCount());
    }
    
}
