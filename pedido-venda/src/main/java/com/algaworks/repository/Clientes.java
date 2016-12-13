/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.algaworks.repository;

import com.algaworks.model.Cliente;
import com.algaworks.repository.filter.ClienteFilter;
import com.algaworks.service.NegocioException;
import com.algaworks.util.jpa.Transactional;
import java.io.Serializable;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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
public class Clientes implements Serializable {
    @Inject
    private EntityManager manager;

    public Cliente guardar(Cliente cliente) {
        return manager.merge(cliente);
    }

    public List<Cliente> todos() {
        return manager.createQuery("from Grupo", Cliente.class).getResultList();
    }

    public Cliente porId(Long id) {
        return manager.find(Cliente.class, id);
    }

    @Transactional
    public void remover(Cliente cliente) {
        try {
            cliente = porId(cliente.getId());
            manager.remove(cliente);
            manager.flush();
        } catch(PersistenceException ex) {
            throw new NegocioException("O cliente não pode ser excluído.");
        }
    }
    
    public List<Cliente> filtrados(ClienteFilter filtro) {
        Session session = manager.unwrap(Session.class);
        
        Criteria criteria = session.createCriteria(Cliente.class);
        
        if (StringUtils.isNotBlank(filtro.getDocumentoReceitaFederal()))
            criteria.add(Restrictions.ilike("documentoReceitaFederal", filtro.getDocumentoReceitaFederal(), MatchMode.ANYWHERE));
        
        if (StringUtils.isNotBlank(filtro.getNome()))
            criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
        
        return criteria.addOrder(Order.asc("nome")).list();
    }
}
