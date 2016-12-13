/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algaworks.repository;

import com.algaworks.model.Produto;
import com.algaworks.repository.filter.ProdutoFilter;
import com.algaworks.service.NegocioException;
import com.algaworks.util.jpa.Transactional;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author dasilva
 */
public class Produtos implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Inject
    private EntityManager manager;
    
    public Produto guardar(Produto produto) {
        return manager.merge(produto);
    }
    
    @Transactional
    public void remover(Produto produto) {
        try {
            produto = porId(produto.getId());
            manager.remove(produto);
            manager.flush();
        } catch(PersistenceException ex) {
            throw new NegocioException("O produto não pode ser excluído.");
        }
    }
    
    public Produto porId(Long id) {
        return manager.find(Produto.class, id);
    }

    public Produto porSku(String sku) {
        try {
            return manager.createQuery("from Produto where upper(sku) = :sku", Produto.class)
                .setParameter("sku", sku.toUpperCase())
                .getSingleResult();
        } catch(NoResultException ex) {
            return null;
        }
    }
    
    public List<Produto> filtrados(ProdutoFilter filtro) {
        Session session = manager.unwrap(Session.class);
        
        Criteria criteria = session.createCriteria(Produto.class);
        
        if (StringUtils.isNotBlank(filtro.getSku()))
            criteria.add(Restrictions.eq("sku", filtro.getSku()));
        
        if (StringUtils.isNotBlank(filtro.getNome()))
            criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
        
        return criteria.addOrder(Order.asc("nome")).list();
    }
    
}
